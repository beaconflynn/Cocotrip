package com.zohan.cocotrip;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

/**
 * Created by imthanapat on 30/1/2018 AD.
 */

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withSplashTimeOut(5000)
                .withTargetActivity(LoginActivity.class)
                .withLogo(R.mipmap.ic_cocotrip)
                .withBackgroundResource(R.mipmap.bklogin)
                .withAfterLogoText("' Make Your Trip Easier '");
        config.getAfterLogoTextView().setTextColor(Color.BLACK);
        View view = config.create();
        setContentView(view);
    }

}
