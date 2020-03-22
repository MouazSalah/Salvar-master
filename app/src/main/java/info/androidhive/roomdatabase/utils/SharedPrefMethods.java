package info.androidhive.roomdatabase.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefMethods
{
    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SharedPrefMethods(Context con)
    {
        this.context = con;
        pref = con.getSharedPreferences("SALVAR_SHAREDPREF", Context.MODE_PRIVATE);
        editor = pref.edit();
    }



}
