package com.example.truckapp.di.app;

import android.app.Application;

import com.example.truckapp.MainActivity;
import com.example.truckapp.di.app.BleModule.BleModule;

public class App extends Application {

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder()
                .bleModule(new BleModule(getApplicationContext()))
                .build();

    }

    public static AppComponent getComponent() {
        return component;
    }

}