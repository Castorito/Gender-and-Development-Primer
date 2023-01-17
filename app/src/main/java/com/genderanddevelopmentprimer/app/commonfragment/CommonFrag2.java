package com.genderanddevelopmentprimer.app.commonfragment;

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
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

public class CommonFrag2 extends Fragment {

    int lastPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.common_frag_2, container, false);
        PDFView pdfView = v.findViewById(R.id.commonfrag2);
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
                        Button quizBtn = v.findViewById(R.id.btn_quiz);
                        if (page == lastPage - 1) {
                            quizBtn.setVisibility(View.VISIBLE);
                        } else {
                            quizBtn.setVisibility(View.GONE);
                        }
                    }
                })
                .load();
        return v;
    }
}
