package info.androidhive.roomdatabase.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import info.androidhive.roomdatabase.R;
import info.androidhive.roomdatabase.ui.items.MainActivity;

public class LoginActivity extends AppCompatActivity
{
    EditText userNameEditText, passwordEditText;
    Button loginBtn;
    TextView cancelBtn;

    SharedPreferences pref ;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameEditText= (EditText)findViewById(R.id.username_edittext);
        passwordEditText= (EditText)findViewById(R.id.password_edittext);


        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        sharedPrefTask();

        loginBtn = (Button)findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (checkAllFields()== true)
                {
                    editor.putString("user_name", userNameEditText.getText().toString()); // Storing string
                    editor.putString("password", passwordEditText.getText().toString()); // Storing string
                    editor.commit(); // commit changes

                    Intent intent =new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        cancelBtn = (TextView)findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

    }

    private void sharedPrefTask()
    {
        String user = pref.getString("user_name", null); // getting String
        String password = pref.getString("password", null); // getting String

        if (user != null && password != null)
        {
            Log.d("login" , user);
            Log.d("login" , password);
            if (user.equals("admin") && password.equals("Pa$$word"))
            {
                Log.d("login" , "done");
                Intent intent =new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    private boolean checkAllFields()
    {
        boolean result = false;

        if (userNameEditText.getText().toString().isEmpty())
        {
            userNameEditText.setError("enter name");

        }
        else if(passwordEditText.getText().toString().isEmpty())
        {
            passwordEditText.setError("enter password");
        }
        else if(!userNameEditText.getText().toString().equals("admin"))
        {
            userNameEditText.setError("invalid user name");
        }
        else if (!passwordEditText.getText().toString().equals("Pa$$word"))
        {
            passwordEditText.setError("wrong password");
        }
        else
        {
            result = true;
        }

        return result;
    }
}
