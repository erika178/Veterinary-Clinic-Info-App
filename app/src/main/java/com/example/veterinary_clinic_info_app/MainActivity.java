package com.example.veterinary_clinic_info_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBarConfig;
    private ProgressBar progressBarPets;
    private Button buttonChat;
    private Button buttonCall;
    private TextView textView;

    final int RESPONSE_CODE_SUCCESS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("TecnicTest", "onCreate");
        textView = findViewById(R.id.text_info);
        setWorkhours("----");

        buttonChat = findViewById(R.id.button_chat);
        buttonChat.setOnClickListener(v -> {
            //ここに押下時の処理
        });

        buttonCall = findViewById(R.id.button_call);
        buttonCall.setOnClickListener(v -> {
            //ここに押下時の処理
        });

        progressBarConfig = findViewById(R.id.progressBarHorizontalConfig);
        progressBarPets = findViewById(R.id.progressBarHorizontalPets);


        fetchConfig();
        fetchPets();

    }

    private void fetchConfig() {
        showProgress(progressBarConfig);
        OkHttpClient client = new OkHttpClient();

        Request requestConfig = new Request.Builder()
                .url("https://www.amock.io/api/erika178/config.json")
                .build();

        client.newCall(requestConfig).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.i("TecnicTest", "onResponse " + "responsecode => " + response.code() + "responsebody => " + responseBody);
                //ここにパースとか
                if (response.code() == RESPONSE_CODE_SUCCESS) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody).getJSONObject("settings");
//                        boolean isChatEnabled = jsonSetting.getBoolean("isChatEnabled");
//                        boolean isCallEnabled = jsonSetting.getBoolean("isCallEnabled");
//                        String workHours = jsonSetting.getString("workHours");
                        //変数に入れた方がいい？
                        Config config = new Config(jsonObject.getBoolean("isChatEnabled"), jsonObject.getBoolean("isCallEnabled"), jsonObject.getString("workHours"));

                        Log.i("TecnicTest", "isChatEnabled " + config.getSettings().isChatEnabled());
                        Log.i("TecnicTest", "isCallEnabled " + config.getSettings().isCallEnabled());
                        mainHandler.post(() -> {
                            setButtonVisible(buttonChat, config.getSettings().isChatEnabled());
                            setButtonVisible(buttonCall, config.getSettings().isCallEnabled());
                            setWorkhours(config.getSettings().getWorkHours());
                        });

                    } catch (JSONException je) {
                        Log.i("TecnicTest", "Json parse error!");
                    }


                } else {
//                    //エラー

//                    //これだとアプリ落ちたかも
//                    mainHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                            builder.setMessage("Data get error!!")
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            finish();
//                                        }
//                                    });
//                            builder.show();
//                        }
//                    });

                }

                mainHandler.post(() -> hideProgress(progressBarConfig));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("TecnicTest", "onFailure " + e.getMessage());
                mainHandler.post(() -> hideProgress(progressBarConfig));
            }
        });

        Log.i("TecnicTest", "after enqueue");
    }

    private void fetchPets() {
        showProgress(progressBarPets);
        OkHttpClient client = new OkHttpClient();

        Request requestPets = new Request.Builder()
                .url("https://www.amock.io/api/erika178/pets.json")
                .build();

        client.newCall(requestPets).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.i("TecnicTest", "onResponse " + "responsecode => " + response.code() + "responsebody => " + responseBody);
                //ここにパースとか
                if (response.code() == RESPONSE_CODE_SUCCESS) {
                    try {
                        JSONArray jsonArray = new JSONObject(responseBody).getJSONArray("pets");
                        List<Pet> pets = new ArrayList<>();
                        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            //変数に入れた方がいい？
                            Pet pet = new Pet(jsonObject.getString("image_url"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("content_url"),
                                    sdFormat.parse(jsonObject.getString("date_added")));
                            pets.add(pet);

                            Log.i("TecnicTest", "[[[pet : " + i+ " ]]] "+ pets.get(i).getTitle()+"///"+sdFormat.format(pets.get(i).getDate_added()));


                        }


                    } catch (JSONException je) {
                        Log.i("TecnicTest", "Json parse error!");
                    } catch (ParseException e){
                        Log.i("TecnicTest", "Json date parse error!"+e.getMessage());

                    }

                } else {


                }
                mainHandler.post(() -> hideProgress(progressBarPets));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.i("TecnicTest", "onFailure " + e.getMessage());
                mainHandler.post(() -> hideProgress(progressBarPets));
            }
        });

        Log.i("TecnicTest", "after enqueue");
    }

    private void showProgress(ProgressBar progressBar) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgress(ProgressBar progressBar) {
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void setButtonVisible(Button button, boolean visible) {
        if (visible) {
            button.setVisibility(View.VISIBLE);
            button.setEnabled(true);
        } else {
            button.setVisibility(View.GONE);
        }
    }

    private void setWorkhours(String workhours) {
        textView.setText(String.format(getString(R.string.office_hours), workhours));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TecnicTest", "onDestroy");
    }
}