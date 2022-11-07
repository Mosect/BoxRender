package com.mosect.app.boxrender;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_opengl).setOnClickListener(v -> {
            Intent intent = new Intent(this, OpenglActivity.class);
            startActivity(intent);
        });
    }
}
