package com.example.veterinary_clinic_info_app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//todo Webview画面で横画面にしたときに情報取得しないようにする
//todo Webview画面からもどったときに情報取得しないようにする？
public class MainFragment extends Fragment {

    private Button buttonChat;
    private Button buttonCall;
    private TextView textView;
    private OkHttpClient client;
    private ProgressBar progressBarConfig;
    private ProgressBar progressBarPets;
    private PetsAdapter petsAdapter;
    private List<Pet> pets = new ArrayList<>();
    int parsedWorkDayFrom;
    int parsedWorkDayTo;
    Calendar parsedWorkHourFrom;
    Calendar parsedWorkHourTo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.i("TecnicTest", "MainFragment : onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("TecnicTest", "MainFragment : onCreateView");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TecnicTest", "MainFragment : onViewCreated");

        textView = view.findViewById(R.id.text_view_work_hours);
        setTextViewWorkHours("----");

        buttonChat = view.findViewById(R.id.button_chat);
        buttonChat.setOnClickListener(v -> {
            checkWorkHours();
        });

        buttonCall = view.findViewById(R.id.button_call);
        buttonCall.setOnClickListener(v -> {
            checkWorkHours();
        });
        progressBarConfig = view.findViewById(R.id.progressBarHorizontalConfig);
        progressBarPets = view.findViewById(R.id.progressBarHorizontalPets);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_pets);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(layoutManager);

        petsAdapter = new PetsAdapter(pets);
        petsAdapter.setClickListener(content_url -> showPetInfo(content_url));
        petsAdapter.setImageEmptyListener((content_url, position) -> fetchBitmapData(content_url, position));
        recyclerView.setAdapter(petsAdapter);

        client = new OkHttpClient();
        fetchConfig();
        fetchPets();

    }

    private void fetchConfig() {
        showProgress(progressBarConfig);

        Request requestConfig = new Request.Builder()
                .url("https://demo3248394.mockable.io/config")
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
                    Log.d("TecnicTest",e.getMessage());
                    showDialogAndFinish(getString(R.string.title_network_error), getString(R.string.error_message_network_error));
                });
            }
        });
    }

    private void fetchPets() {
        showProgress(progressBarPets);

        Request requestPets = new Request.Builder()
                .url("https://demo3248394.mockable.io/pets")
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
                    Log.d("TecnicTest",e.getMessage());
                    showDialogAndFinish(getString(R.string.title_network_error), getString(R.string.error_message_network_error));
                });
            }
        });
    }

    private void showPetInfo(String content_url) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.container, PetContentsFragment.newInstance(content_url));
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, id) -> dialog.dismiss());
        builder.show();
    }

    private void showDialogAndFinish(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, id) -> requireActivity().finish());
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TecnicTest", "MainFragment : onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("TecnicTest", "MainFragment : onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TecnicTest", "MainFragment : onDestroy");
    }
}