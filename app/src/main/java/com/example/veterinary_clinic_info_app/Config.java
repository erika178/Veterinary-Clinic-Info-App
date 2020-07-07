package com.example.veterinary_clinic_info_app;

public class Config {

    private Settings settings;

    public Config(boolean isChatEnabled,boolean isCallEnabled,String workHours){
        settings = new Settings(isChatEnabled,isCallEnabled,workHours);
    }

    public Settings getSettings() {
        return settings;
    }
}