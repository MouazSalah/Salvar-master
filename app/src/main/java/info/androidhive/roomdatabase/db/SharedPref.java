package info.androidhive.roomdatabase.db;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref
{
    Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SharedPref(Context con)
    {
        this.context = con;
        pref = con.getSharedPreferences("SALVAR_DATABASE", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveItemValue(long value)
    {
        editor.putLong("ITEM_VALUE", value);
        editor.commit();
    }

    public long getItemValue()
    {
        return pref.getLong("ITEM_VALUE", 0);
    }

}
