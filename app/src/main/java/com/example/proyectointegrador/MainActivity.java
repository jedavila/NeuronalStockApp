package com.example.proyectointegrador;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    private MainActivityFragment fragmentApp;
    private Modelo modelo = Modelo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentApp = (MainActivityFragment)
                getSupportFragmentManager().findFragmentById(
                        R.id.frgApp);

        if(findViewById(R.id.frgStatics)!=null){
            modelo.setLandscape(true);
        }

    }

}






