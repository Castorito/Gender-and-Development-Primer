package com.genderanddevelopmentprimer.app;

import static android.content.Context.UI_MODE_SERVICE;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

public class DisplayFragment extends Fragment {

    PDFView pdfView;
    String identifier;
    Button quizBtn;
    int lastPage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        identifier = requireArguments().getString("identifier");

        View view = inflater.inflate(R.layout.pdf_layout_frag, container, false);
        pdfView = view.findViewById(R.id.pdfFrag);
        quizBtn = view.findViewById(R.id.btn_quiz);

        UiModeManager uiModeManager = (UiModeManager) requireContext().getSystemService(UI_MODE_SERVICE);

        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            // The device is currently in night mode
            quizBtn.setBackgroundColor(requireContext().getColor(R.color.blue));
        } else {
            // The device is not currently in night mode
            quizBtn.setBackgroundColor(requireContext().getColor(R.color.pink));
        }

        isConnected();

        DocumentReference documentReference = firestore.collection("links").document("paths");
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            String link = documentSnapshot.getString(identifier);
            StorageReference storageRef = storage.getReference().child(Objects.requireNonNull(link));

            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Handle successful download URL generation
                String downloadUrl = uri.toString();
                new RetrievePDFfromUrl().execute(downloadUrl);
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Toast.makeText(getContext(), "Loading Failed.", Toast.LENGTH_SHORT).show();
            });
        });

        quizBtn.setOnClickListener(view1 -> {
            Intent i = new Intent(new Intent(getActivity(), Quiz.class));
            //identify the questions for contents of specific fragment
            i.putExtra("identifier", identifier);
            startActivity(i);
        });

        return view;
    }

    private void isConnected() {

        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        boolean isNetworkAvailable = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        if (!isNetworkAvailable) {
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
            if (!Objects.equals(identifier, "tf6") || !Objects.equals(identifier, "tf7") || !Objects.equals(identifier, "tf8")) {
                Toast.makeText(pdfView.getContext(), "Reach the final page to take the quiz", Toast.LENGTH_SHORT).show();
                pdfView.fromStream(inputStream)
                        .onLoad(nbPages -> {
                            lastPage = nbPages;
                        })
                        .onPageChange((page, pageCount) -> {
                            quizBtn.setVisibility(page == lastPage - 1 ? View.VISIBLE : View.INVISIBLE);
                        }).autoSpacing(true).enableAntialiasing(true).pageSnap(true)
                        .load();
            } else {
                pdfView.fromStream(inputStream)
                        .autoSpacing(true).pageSnap(true).enableAntialiasing(true)
                        .load();
            }
        }
    }
}
