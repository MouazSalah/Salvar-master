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

    public void saveOldValue(double value)
    {
        editor.putLong("oldValue", (long) value);
        editor.commit();
    }

    public long getOldValue()
    {
        return pref.getLong("oldValue", 0);
    }


}
