package com.example.veterinary_clinic_info_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AsyncHttpRequest asyncHttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TecnicTest", "onCreate");
        ((TextView)findViewById(R.id.text_info)).setText(String.format(getString(R.string.office_hours),"----"));

        Button ButtonChat = findViewById(R.id.button_chat);
        ButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button ButtonCall = findViewById(R.id.button_call);
        ButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        asyncHttpRequest = new AsyncHttpRequest(this);
        asyncHttpRequest.execute("temp");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncHttpRequest.cancel(false);
        Log.i("TecnicTest", "onDestroy");
    }
}