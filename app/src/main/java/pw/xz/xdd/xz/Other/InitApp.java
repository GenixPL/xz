package pw.xz.xdd.xz.Other;

import android.support.multidex.MultiDexApplication;

import com.indoorway.android.common.sdk.IndoorwaySdk;

/**
 * Created by Czarek on 2018-01-13.
 */

public class InitApp extends MultiDexApplication {

    public static int MY_PERMISSIONS_ACCESS_FINE_LOCATION;

    @Override
    public void onCreate() {
        super.onCreate();
        // init application context on each Application start
        IndoorwaySdk.initContext(getApplicationContext());

        // it's up to you when to initialize IndoorwaySdk, once initialized it will work forever!
        IndoorwaySdk.configure("6513a6eb-133a-43f6-a206-6afe3e07fe96");

    }
}
