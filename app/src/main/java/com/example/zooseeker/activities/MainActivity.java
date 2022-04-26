package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.zooseeker.HomeActivity;
import com.example.zooseeker.R;
import com.example.zooseeker.adapters.AnimalAdapter.OnAnimalClickListener;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;
import com.example.zooseeker.adapters.AnimalAdapter;
import com.example.zooseeker.databinding.ActivityAnimalListBinding;
import com.example.zooseeker.databinding.ActivityMainBinding;
import com.example.zooseeker.models.Animal;
<<<<<<< HEAD

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent (this, HomeActivity.class);
=======
import com.example.zooseeker.viewmodels.AnimalListViewModel;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, AnimalListActivity.class);
>>>>>>> dfad0fc (RecyclerView layout and logic)
        startActivity(intent);
    }
}