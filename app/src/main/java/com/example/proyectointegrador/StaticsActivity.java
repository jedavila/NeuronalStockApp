package com.example.proyectointegrador;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class StaticsActivity extends AppCompatActivity {
    private StaticsFragment fragmentApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statics);
        fragmentApp = (StaticsFragment)
                getSupportFragmentManager().findFragmentById(
                        R.id.frgStatics);

    }
}