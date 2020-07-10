package com.example.veterinary_clinic_info_app;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PetContentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PetContentsFragment extends Fragment {

    private static final String CONTENT_URL = "param1";
    private String contentUrl;
    private WebView webView;
    private ProgressBar progressBar;

    public PetContentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contentUrl Parameter 1.
     * @return A new instance of fragment PetContentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PetContentsFragment newInstance(String contentUrl) {
        PetContentsFragment fragment = new PetContentsFragment();
        Bundle args = new Bundle();
        args.putString(CONTENT_URL, contentUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contentUrl = getArguments().getString(CONTENT_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_contents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = view.findViewById(R.id.web_view_pet);
        progressBar = view.findViewById(R.id.progressBar);
//        ProgressDialog progressDialog = ProgressDialog.show(view.getContext(), "Loading...", "Please Wait",true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.INVISIBLE);
//                if (progressDialog != null && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
            }
        });
        webView.loadUrl(contentUrl);
    }
}