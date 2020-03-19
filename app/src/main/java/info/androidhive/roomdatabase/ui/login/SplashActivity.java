package info.androidhive.roomdatabase.ui.login;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.roomdatabase.R;

public class SplashActivity extends AppCompatActivity
{
    @BindView(R.id.name) TextView nameText;
    @BindView(R.id.mobile) TextView mobileText;

    @BindView(R.id.logo)
    ImageView logoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_transition_ttb);
        Animation b = AnimationUtils.loadAnimation(this, R.anim.fade_transition_btt);
        a.reset();
        a.setDuration(3000);
        b.reset();
        b.setDuration(3000);

        logoImage.clearAnimation();
        logoImage.startAnimation(a);
        nameText.clearAnimation();
        nameText.startAnimation(b);
        mobileText.clearAnimation();
        mobileText.startAnimation(b);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run()
            {
                Intent mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 5000);
    }
}
