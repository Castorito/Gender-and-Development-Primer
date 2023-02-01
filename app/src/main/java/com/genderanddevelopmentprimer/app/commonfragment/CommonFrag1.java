package com.genderanddevelopmentprimer.app.commonfragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

public class CommonFrag1 extends Fragment {

    int lastPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.common_frag_1, container, false);
        PDFView pdfView = v.findViewById(R.id.commonfrag1);
        Button quizBtn = v.findViewById(R.id.btn_quiz);

        pdfView.fromAsset("Common Fragment1.pdf")
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        lastPage = nbPages;
                        Toast.makeText(getActivity(), "Reach the final page to take the quiz", Toast.LENGTH_SHORT).show();
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        if (page == lastPage - 1) {
                            quizBtn.setVisibility(View.VISIBLE);
                        } else {
                            quizBtn.setVisibility(View.GONE);
                        }
                    }
                }).spacing(2)
                .load();

        quizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(new Intent(getActivity(), Quiz.class));
                i.putExtra("Value", "cf1");
                startActivity(i);
            }
        });
        return v;
    }
}
