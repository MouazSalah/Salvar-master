package info.androidhive.roomdatabase.ui.items;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.androidhive.roomdatabase.R;
import info.androidhive.roomdatabase.ui.login.SplashActivity;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ItemsListFragment(), ItemsListFragment.TAG)
                    .commitNow();
        }
    }

    @OnClick(R.id.fab)
    void onFabClick() {
        ItemsListFragment fragment = (ItemsListFragment) getSupportFragmentManager().findFragmentByTag(ItemsListFragment.TAG);
        if (fragment != null) {
            fragment.showNoteDialog(false, null, -1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_delete_all)
        {
            ItemsListFragment fragment = (ItemsListFragment) getSupportFragmentManager().findFragmentByTag(ItemsListFragment.TAG);
            if (fragment != null) {
                fragment.deleteAllNotes();
            }
            return true;
        }

        if (item.getItemId() == R.id.action_log_out)
        {
            SharedPreferences preferences = getSharedPreferences("MyPref", 0);
            preferences.edit().remove("user_name").commit();
            preferences.edit().remove("password").commit();

            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
