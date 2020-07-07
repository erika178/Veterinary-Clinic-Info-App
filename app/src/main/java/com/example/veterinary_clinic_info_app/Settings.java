package com.example.veterinary_clinic_info_app;

public class Settings {
    private boolean isChatEnabled;
    private boolean isCallEnabled;
    private String workHours;

    public Settings(boolean isChatEnabled, boolean isCallEnabled, String workHours) {
        this.isChatEnabled = isChatEnabled;
        this.isCallEnabled = isCallEnabled;
        this.workHours = workHours;
    }

    public boolean isChatEnabled() {
        return isChatEnabled;
    }

    public boolean isCallEnabled() {
        return isCallEnabled;
    }

    public String getWorkHours() {
        return workHours;
    }
}
