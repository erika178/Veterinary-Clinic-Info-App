package com.example.veterinary_clinic_info_app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TecnicTest","MainActivity : onCreate");

        //todo ここの引数いらない
        showMainFragment("hoge","huga");
    }

    private void showMainFragment(String param1, String param2) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.container, MainFragment.newInstance(param1,param2));
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TecnicTest","MainActivity : onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("TecnicTest","MainActivity : onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TecnicTest","MainActivity : onDestroy");
    }
}