package com.genderanddevelopmentprimer.app.teacherfragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.genderanddevelopmentprimer.app.R;
import com.genderanddevelopmentprimer.app.mainfunctions.Quiz;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TeacherFrag8 extends Fragment {
    int lastPage;
    PDFView pdfView;
    Button quizBtn;
    String pdfFile;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pdf_layout_frag, container, false);
        pdfView = v.findViewById(R.id.pdfFrag);
        quizBtn = v.findViewById(R.id.btn_quiz);

        isConnected();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("/Teacher PDF Files/Teacher Fragment8.pdf");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Handle successful download URL generation
            String downloadUrl = uri.toString();
            new RetrievePDFfromUrl().execute(downloadUrl);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e(TAG, "Error getting download URL: " + exception.getMessage());
        });

        quizBtn.setOnClickListener(view -> {
            Intent i = new Intent(new Intent(getActivity(), Quiz.class));
            //identify the questions for contents of specific fragment
            i.putExtra("identifier", "tf8");
            startActivity(i);
        });
        return v;
    }
    private void isConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            // Not connected to the internet
            Toast.makeText(getContext(), "Failed to load.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> Toast.makeText(getContext(), "No internet connection.", Toast.LENGTH_LONG).show(), 3000); // 5 second delay

        } else {
            Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
        }
    }

    class RetrievePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            pdfView.fromStream(inputStream)
                    .onLoad(nbPages -> {
                        lastPage = nbPages;
                        Toast.makeText(pdfView.getContext(), "Reach the final page to take the quiz", Toast.LENGTH_SHORT).show();
                    })
                    .onPageChange((page, pageCount) -> {
                        if (page == lastPage - 1) {
                            quizBtn.setVisibility(View.VISIBLE);
                        } else {
                            quizBtn.setVisibility(View.GONE);
                        }
                    }).spacing(2).load();
        }
    }
}
