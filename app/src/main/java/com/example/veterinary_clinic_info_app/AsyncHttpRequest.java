package com.example.veterinary_clinic_info_app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class AsyncHttpRequest extends AsyncTask<String,Void,String> {
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
    protected String doInBackground(String... params) {
        Log.i("TecnicTest", "doInBackground");

        //ここにjson取得処理
        try {
            Thread.sleep(5000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("TecnicTest", "onPostExecute");
        //todo必要ならなんか入れる
        Activity activity = weakActivity.get();
        if ((activity == null) || (activity.isFinishing()) || (activity.isDestroyed())) {
            // activity is no longer valid, don't do anything!
            Log.i("TecnicTest", "activity null");
            return;
        }

        //仮です
        TextView tv = activity.findViewById(R.id.text_info);
        tv.setText(String.format(activity.getApplicationContext().getString(R.string.office_hours),"json data!!"));
        //仮です

    }
}
