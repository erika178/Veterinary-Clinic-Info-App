package com.example.veterinary_clinic_info_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private AsyncHttpRequest asyncHttpRequest;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("TecnicTest", "onCreate");
        ((TextView)findViewById(R.id.text_info)).setText(String.format(getString(R.string.office_hours),"----"));

        Button ButtonChat = findViewById(R.id.button_chat);
        ButtonChat.setOnClickListener(v -> {
            //ここに押下時の処理
        });

        Button ButtonCall = findViewById(R.id.button_call);
        ButtonCall.setOnClickListener(v -> {
            //ここに押下時の処理
        });

        fetchConfig();
        fetchPets();

    }

    private void fetchConfig() {
        //        asyncHttpRequest = new AsyncHttpRequest(this);
//        asyncHttpRequest.execute("http://www.amock.io/api/erika178/config.json");

        showProgress(R.id.progressBarHorizontalConfig);
        OkHttpClient client = new OkHttpClient();

        Request requestConfig = new Request.Builder()
                .url("https://www.amock.io/api/erika178/config.json")
                .build();

        client.newCall(requestConfig).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i("TecnicTest", "onResponse " +"responsecode => " + response.code() +"responsebody => " + response.body().string());
                //ここにパースとか
                mainHandler.post(() -> hideProgress(R.id.progressBarHorizontalConfig));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("TecnicTest", "onFailure " + e.getMessage());
                mainHandler.post(() -> hideProgress(R.id.progressBarHorizontalConfig));
            }
        });

        Log.i("TecnicTest", "after enqueue");
    }

    private void fetchPets() {



        showProgress(R.id.progressBarHorizontalPets);
        OkHttpClient client = new OkHttpClient();

        Request requestPets = new Request.Builder()
                .url("https://www.amock.io/api/erika178/pets.json")
                .build();

        client.newCall(requestPets).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i("TecnicTest", "onResponse " +"responsecode => " + response.code() +"responsebody => " + response.body().string());
                //ここにパースとか
                mainHandler.post(() -> hideProgress(R.id.progressBarHorizontalPets));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("TecnicTest", "onFailure " + e.getMessage());
                mainHandler.post(() -> hideProgress(R.id.progressBarHorizontalPets));
            }
        });

        Log.i("TecnicTest", "after enqueue");
    }

    private void showProgress(int id) {
        progressBar = findViewById(id);
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgress(int id) {
        progressBar = findViewById(id);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        asyncHttpRequest.cancel(false);
        Log.i("TecnicTest", "onDestroy");
    }
}