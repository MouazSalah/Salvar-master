package info.androidhive.roomdatabase.utils;

import android.app.Application;
import android.content.Context;

public class AppContext extends Application
{
    private static Context mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = getApplicationContext();
        }
    }

    public static Context getInstance() {
        return mInstance;
    }
}
