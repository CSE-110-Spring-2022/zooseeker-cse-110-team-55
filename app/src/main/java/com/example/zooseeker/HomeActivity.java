package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

    }

    private List<Animal> doMySearch(String query) {

        AnimalItemDao animalItemDao = AnimalDatabase.getSingleton(this).animalItemDao();
        List<Animal> animalItems = animalItemDao.get(query);
        Log.d("SearchActivity", String.valueOf(animalItems.size()));
        return animalItems;

    }

}