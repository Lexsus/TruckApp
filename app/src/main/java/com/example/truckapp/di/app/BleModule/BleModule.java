package com.example.truckapp.di.app.BleModule;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class BleModule {
    private final Context context;

    public BleModule (Context context) {
        this.context = context;
    }
    @Provides
    IBleUtils provideBleUtils() {
        return new BleUtils(context());
    }

    @Provides //scope is not necessary for parameters stored within the module
    public Context context() {
        return context;
    }

}
