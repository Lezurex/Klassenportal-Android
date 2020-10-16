package com.lezurex.ap20bklassenportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.lezurex.ap20bklassenportal.login.LoginActivity;

import java.util.concurrent.atomic.AtomicBoolean;

public class PreloadActivity extends AppCompatActivity {

    public static Resources resources;

    private ConstraintLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }

    private void init() {
        setContentView(R.layout.activity_preload);

        resources = getResources();
        parent = findViewById(R.id.parent);

        Utils.getInstance(this);

        AtomicBoolean network = new AtomicBoolean(false);

        new Thread(() -> {
            Snackbar snackbar = null;
            while (!network.get()) {
                if (Utils.getInstance(this).isNetworkAvailablable()) {
                    network.set(true);
                    Intent intent;
                    if (Utils.getInstance(PreloadActivity.this).getAccessToken() == null) {
                        intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    } else {
                        if (!Utils.getInstance(PreloadActivity.this).validateToken()) {
                            intent = new Intent(this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        } else {
                            Utils.getInstance(this).refreshTasks();
                            intent = new Intent(this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        }
                    }
                    startActivity(intent);
                    if (snackbar != null)
                        snackbar.dismiss();
                }
                if (snackbar == null) {
                    snackbar = Snackbar.make(findViewById(R.id.parent), getResources().getString(R.string.err_no_internet), Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

}