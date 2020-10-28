package com.example.truckapp.di.app;

import android.content.Context;

import com.example.truckapp.MainActivity;
import com.example.truckapp.di.app.BleModule.BleModule;
import com.example.truckapp.di.app.BleModule.BleUtils;
import com.example.truckapp.di.app.BleModule.IBleUtils;

import dagger.Component;

@Component(modules = {BleModule.class})
public interface AppComponent {
    IBleUtils getBleUtils();

    Context context();

    void inject(MainActivity mainActivity);
}