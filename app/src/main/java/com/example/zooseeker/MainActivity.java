package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.zooseeker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set view binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        /* replaces this:
         *      TextView tv = findViewById(R.id.mytextview);
         *      tv.setText("hello world!");
         *  with this:
         *      binding.mytextview.setText("view binding is awesome!");
         */
    }
}