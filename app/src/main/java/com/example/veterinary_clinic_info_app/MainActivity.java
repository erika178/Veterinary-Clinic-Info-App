package com.example.veterinary_clinic_info_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    int parsedWorkDayFrom;
    int parsedWorkDayTo;
    Calendar parsedWorkHourFrom;
    Calendar parsedWorkHourTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view_work_hours);
        setTextViewWorkHours("----");

        buttonChat = findViewById(R.id.button_chat);
        buttonChat.setOnClickListener(v -> {
            checkWorkHours();
        });

        buttonCall = findViewById(R.id.button_call);
        buttonCall.setOnClickListener(v -> {
            checkWorkHours();
        });

        progressBarConfig = findViewById(R.id.progressBarHorizontalConfig);
        progressBarPets = findViewById(R.id.progressBarHorizontalPets);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_pets);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        petsAdapter = new PetsAdapter(pets);
        petsAdapter.setClickListener(content_url -> showPetInfo(content_url));
        petsAdapter.setImageEmptyListener(new PetsAdapter.OnImageEmptyListener() {
            @Override
            public void onImageEmpty(String content_url, int position) {
                fetchBitmapData(content_url, position);
            }
        });
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
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string()).getJSONObject("settings");
                        Config config = new Config(jsonObject.getBoolean("isChatEnabled"),
                                jsonObject.getBoolean("isCallEnabled"),
                                jsonObject.getString("workHours"));

                        String WorkHours = config.getSettings().getWorkHours();
                        parseWorkHours(WorkHours);

                        mainHandler.post(() -> {
                            setButtonVisible(buttonChat, config.getSettings().isChatEnabled());
                            setButtonVisible(buttonCall, config.getSettings().isCallEnabled());
                            setTextViewWorkHours(WorkHours);
                        });

                    } catch (JSONException je) {
                        mainHandler.post(() -> showDialogAndFinish(getString(R.string.title_data_format_invalid), getString(R.string.error_message_json_format_invalid)));
                    } catch (ParseException e) {
                        mainHandler.post(() -> showDialogAndFinish(getString(R.string.title_data_value_invalid), getString(R.string.error_message_data_value_invalid)));
                    }

                } else {
                    mainHandler.post(() -> showDialogAndFinish(getString(R.string.title_http_response_invalid), getString(R.string.error_message_http_response_invalid)));
                }

                mainHandler.post(() -> hideProgress(progressBarConfig));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mainHandler.post(() -> {
                    hideProgress(progressBarConfig);
                    showDialogAndFinish(getString(R.string.title_network_error), getString(R.string.error_message_network_error));
                });
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

                        mainHandler.post(() -> {
                            petsAdapter.updateData(pets);
                        });

                    } catch (JSONException je) {
                        mainHandler.post(() -> showDialogAndFinish(getString(R.string.title_data_format_invalid), getString(R.string.error_message_json_format_invalid)));
                    } catch (ParseException e) {
                        mainHandler.post(() -> showDialogAndFinish(getString(R.string.title_data_value_invalid), getString(R.string.error_message_data_value_invalid)));
                    }

                } else {
                    mainHandler.post(() -> showDialogAndFinish(getString(R.string.title_http_response_invalid), getString(R.string.error_message_http_response_invalid)));
                }
                mainHandler.post(() -> hideProgress(progressBarPets));
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mainHandler.post(() -> {
                    hideProgress(progressBarConfig);
                    showDialogAndFinish(getString(R.string.title_network_error), getString(R.string.error_message_network_error));
                });
            }
        });
    }

    private void showPetInfo(String content_url) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.container, PetContentsFragment.newInstance(content_url));
        fragmentTransaction.commit();
    }

    private void fetchBitmapData(String imageUrl, int position) {
        Request requestImage = new Request.Builder()
                .url(imageUrl)
                .build();

        client.newCall(requestImage).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //nothing to do
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    pets.get(position).setBitmap(bitmap);
                    mainHandler.post(() -> petsAdapter.updateItem(pets, position));
                }
            }
        });
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

    private void setTextViewWorkHours(String workHours) {
        textView.setText(String.format(getString(R.string.office_hours), workHours));
    }

    private void parseWorkHours(String workHours) throws ParseException {
        String[] workHoursSeparate = workHours.split(" ");

        //DayOfWeek
        String workDayFrom = workHoursSeparate[0].split("-")[0];
        String workDayTo = workHoursSeparate[0].split("-")[1];
        parsedWorkDayFrom = parseDayOfWeek(workDayFrom);
        parsedWorkDayTo = parseDayOfWeek(workDayTo);

        //Hours
        String workHourFrom = workHoursSeparate[1];
        String workHourTo = workHoursSeparate[3];
        parsedWorkHourFrom = parseHour(workHourFrom);
        parsedWorkHourTo = parseHour(workHourTo);
    }

    private void checkWorkHours() {
        Calendar calendarToday = Calendar.getInstance(TimeZone.getDefault());
        int todayOfWeek = calendarToday.get(Calendar.DAY_OF_WEEK);

        if (!isBetweenDaysOfWeek(todayOfWeek)) {
            showDialog(getString(R.string.title_closed), getString(R.string.message_outside_work_hours));
            return;
        }

        if (!isBetweenHours(calendarToday)) {
            showDialog(getString(R.string.title_closed), getString(R.string.message_outside_work_hours));
            return;
        }

        showDialog(getString(R.string.title_open), getString(R.string.message_within_work_hours));

    }

    private boolean isBetweenDaysOfWeek(int todayOfWeek) {
        return parsedWorkDayFrom <= todayOfWeek && todayOfWeek <= parsedWorkDayTo;
    }

    private boolean isBetweenHours(Calendar calendarToday) {
        return (calendarToday.after(parsedWorkHourFrom) && calendarToday.before(parsedWorkHourTo));
    }

    private static int parseDayOfWeek(String day) throws ParseException {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
        Date date = dayFormat.parse(day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }

    private Calendar parseHour(String workHour) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date dateFormatHours = simpleDateFormat.parse(workHour);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(dateFormatHours);
        calendar.set(Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR), Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH), Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DATE));
        return calendar;
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, id) -> dialog.dismiss());
        builder.show();
    }

    private void showDialogAndFinish(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, id) -> finishAndRemoveTask());
        builder.show();
    }

}