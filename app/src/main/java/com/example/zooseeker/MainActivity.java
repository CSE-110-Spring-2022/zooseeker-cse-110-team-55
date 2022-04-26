package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.zooseeker.viewmodels.MainActivityViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set view binding
     //   binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Inject viewmodel instance
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // TODO: Observe changes of LiveData objects

        Intent intent = new Intent (this, HomeActivity.class);
        startActivity(intent);
    }


}