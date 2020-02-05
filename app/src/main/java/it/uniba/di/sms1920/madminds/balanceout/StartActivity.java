package it.uniba.di.sms1920.madminds.balanceout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class StartActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                Animatoo.animateFade(StartActivity.this);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

}
