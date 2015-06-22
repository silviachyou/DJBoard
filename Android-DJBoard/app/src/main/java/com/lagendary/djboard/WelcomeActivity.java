package com.lagendary.djboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by joshua on 6/22/15.
 */
public class WelcomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void startSkateboarding(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void startMusicSettings(View view) {
        startActivity(new Intent(this, MusicSettingsActivity.class));
    }
}
