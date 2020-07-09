package com.example.veterinary_clinic_info_app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AsyncHttpRequest extends AsyncTask<String,Void,JSONObject> {
    private final WeakReference<Activity> weakActivity;

    public AsyncHttpRequest(Activity activity) {
        Log.i("TecnicTest", "AsyncHttpRequest");
        this.weakActivity = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //ローディング

    }

    @Override
    protected JSONObject doInBackground(String... params) {
        Log.i("TecnicTest", "doInBackground");


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(params[0]).build();

        //ここにjson取得処理
//        try {
//
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);
        Log.i("TecnicTest", "onPostExecute");
        //todo必要ならなんか入れる
        Activity activity = weakActivity.get();
        if ((activity == null) || (activity.isFinishing()) || (activity.isDestroyed())) {
            // activity is no longer valid, don't do anything!
            Log.i("TecnicTest", "activity null");
            return;
        }

        //仮です
        TextView tv = activity.findViewById(R.id.text_view_work_hours);
        tv.setText(String.format(activity.getApplicationContext().getString(R.string.office_hours),"json data!!"));
        //仮です

    }
}
