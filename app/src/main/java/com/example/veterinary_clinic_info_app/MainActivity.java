package com.example.veterinary_clinic_info_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Build;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client;
    private ProgressBar progressBarConfig;
    private ProgressBar progressBarPets;
    private Button buttonChat;
    private Button buttonCall;
    private TextView textView;
    private PetsAdapter petsAdapter;
    private List<Pet> pets = new ArrayList<>();
    private String WorkHours = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("TecnicTest", "onCreate");
        textView = findViewById(R.id.text_info);
        setWorkHours("----");

        buttonChat = findViewById(R.id.button_chat);
        buttonChat.setOnClickListener(v -> {
            checkWorkHours(WorkHours);
        });

        buttonCall = findViewById(R.id.button_call);
        buttonCall.setOnClickListener(v -> {
            checkWorkHours(WorkHours);
        });

        progressBarConfig = findViewById(R.id.progressBarHorizontalConfig);
        progressBarPets = findViewById(R.id.progressBarHorizontalPets);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_pets);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        petsAdapter = new PetsAdapter(pets);
        recyclerView.setAdapter(petsAdapter);

        client = new OkHttpClient();
        fetchConfig();
        fetchPets();
    }

    private void fetchConfig() {
        showProgress(progressBarConfig);

        Request requestConfig = new Request.Builder()
                .url("https://www.amock.io/api/erika178/config.json")
                .build();

        client.newCall(requestConfig).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.i("TecnicTest", "onResponse");
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string()).getJSONObject("settings");
                        Config config = new Config(jsonObject.getBoolean("isChatEnabled"),
                                jsonObject.getBoolean("isCallEnabled"),
                                jsonObject.getString("workHours"));

                        mainHandler.post(() -> {
                            setButtonVisible(buttonChat, config.getSettings().isChatEnabled());
                            setButtonVisible(buttonCall, config.getSettings().isCallEnabled());
                            WorkHours = config.getSettings().getWorkHours();
                            setWorkHours(WorkHours);
                        });

                    } catch (JSONException je) {
                        //TODO Handle this exception
                        Log.i("TecnicTest", "Json parse error!");
                        mainHandler.post(() -> showDialogAndFinish(getString(R.string.error_message_json_invalid)));
                    }


                } else {
                    //TODO Handle this exception
                }

                mainHandler.post(() -> hideProgress(progressBarConfig));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //TODO Handle this exception
                Log.i("TecnicTest", "onFailure " + e.getMessage());
                mainHandler.post(() -> hideProgress(progressBarConfig));
            }
        });
    }

    private void fetchPets() {
        showProgress(progressBarPets);

        Request requestPets = new Request.Builder()
                .url("https://www.amock.io/api/erika178/pets.json")
                .build();

        client.newCall(requestPets).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONObject(response.body().string()).getJSONArray("pets");
                        pets = new ArrayList<>();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Pet pet = new Pet(jsonObject.getString("image_url"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("content_url"),
                                    simpleDateFormat.parse(jsonObject.getString("date_added")));
                            pets.add(pet);
                        }

                        mainHandler.post(() -> petsAdapter.updateData(pets));

                    } catch (JSONException je) {
                        //TODO Handle this exception
                        Log.e("TecnicTest", "Json parse error!");
                    } catch (ParseException e) {
                        //TODO Handle this exception
                        Log.i("TecnicTest", "Json date parse error!" + e.getMessage());
                    }

                } else {
                    //TODO Handle this exception
                }
                mainHandler.post(() -> hideProgress(progressBarPets));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //TODO Handle this exception
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

    private void setWorkHours(String workHours) {
        textView.setText(String.format(getString(R.string.office_hours), workHours));
    }


    private void checkWorkHours(String workHours) {
        Calendar calendarToday = Calendar.getInstance(TimeZone.getDefault());
        int todayOfWeek = calendarToday.get(Calendar.DAY_OF_WEEK);

        try {
            String[] workHoursSeparate = workHours.split(" ");

            boolean isBetweenDayOfWeek = checkDayOfWeek(todayOfWeek, workHoursSeparate[0].split("-"));
            if (!isBetweenDayOfWeek) {
                showDialog(getString(R.string.message_outside_work_hours));
                return;
            }

            boolean isBetweenHours = checkHours(calendarToday, workHoursSeparate);
            if (!isBetweenHours) {
                showDialog(getString(R.string.message_outside_work_hours));
                return;
            }

            showDialog(getString(R.string.message_within_work_hours));

        } catch (Exception e) {
            Log.e("TecnicTest", "Json date parse error!" + e.getMessage());
        }

    }

    private boolean checkHours(Calendar calendarToday, String[] workHours) throws ParseException {

        Calendar calendarFrom = parseHour(calendarToday, workHours[1]);
        Calendar calendarTo = parseHour(calendarToday, workHours[3]);

        Log.i("TecnicTest", "Today : " + calendarToday.getTime());
        Log.i("TecnicTest", "From : " + calendarFrom.getTime());
        Log.i("TecnicTest", "To : " + calendarTo.getTime());

        if (calendarToday.after(calendarFrom) && calendarToday.before(calendarTo)) {
            return true;
        } else {
            return false;
        }
    }

    private Calendar parseHour(Calendar calendarToday, String workHour) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date dateFormatHours = simpleDateFormat.parse(workHour);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(dateFormatHours);
        calendar.set(calendarToday.get(Calendar.YEAR), calendarToday.get(Calendar.MONTH), calendarToday.get(Calendar.DATE));
        return calendar;
    }

    private boolean checkDayOfWeek(int todayOfWeek, String[] workDayFromTo) throws ParseException {
        int workDayFrom = parseDayOfWeek(workDayFromTo[0]);
        int workDayTo = parseDayOfWeek(workDayFromTo[1]);
        if (workDayFrom <= todayOfWeek && todayOfWeek <= workDayTo) {
            return true;
        } else {
            return false;
        }
    }

    private static int parseDayOfWeek(String day) throws ParseException {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        Date date = dayFormat.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }

    private void showDialog(String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());//MainActivity.thisを指定しないとクラッシュする
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void showDialogAndFinish(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAndRemoveTask();//これでいいかどうか確認
                    }
                });
        builder.show();
    }

}