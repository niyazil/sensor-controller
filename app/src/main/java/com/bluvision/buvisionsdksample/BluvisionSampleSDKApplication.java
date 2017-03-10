package com.bluvision.buvisionsdksample;

import com.bluvision.beeks.sdk.util.BeaconManager;

import android.app.Application;
import android.util.Log;

public class BluvisionSampleSDKApplication extends Application {

    private BeaconManager mBeaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mBeaconManager = new BeaconManager(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        mBeaconManager.stop();


    }

    public BeaconManager getBeaconManager() {
        return mBeaconManager;
    }

    public void setBeaconManager(BeaconManager beaconManager) {
        mBeaconManager = beaconManager;
    }
}
