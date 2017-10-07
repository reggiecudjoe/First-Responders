package com.pubnub.example.android.datastream.mapexample.pubnubandroidmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Sim1_screen(View view) {
        Intent intent = new Intent(this, Sim1.class);
        startActivity(intent);
    }

    public void Sim2_screen(View view){
        Intent intent = new Intent(this, Sim2.class);
        startActivity(intent);
    }
}
