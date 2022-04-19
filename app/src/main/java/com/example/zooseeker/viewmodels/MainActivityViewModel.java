package com.example.zooseeker.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zooseeker.models.Animal;

import java.util.ArrayList;
import java.util.*;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<List<Animal>> animals;

    // Constructor
    public MainActivityViewModel() {
        // TODO: Instantiate repository instance
        initAnimals();
    }

    public LiveData<List<Animal>> getAnimals() {
        return animals;
    }

    public void setAnimals(MutableLiveData<List<Animal>> animals) {
        this.animals = animals;
    }

    private void initAnimals() {
        animals = new MutableLiveData<>();
        animals.setValue(new ArrayList<>(
                Arrays.asList(
                        new Animal("Animal One", "Location 1"),
                        new Animal("Animal Two", "Location Two"),
                        new Animal("Animal Three", "Location Three3"),
                        new Animal("Animal One", "Location 1"),
                        new Animal("Animal Two", "Location Two"),
                        new Animal("Animal Three", "Location Three3"),
                        new Animal("Animal One", "Location 1"),
                        new Animal("Animal Two", "Location Two"),
                        new Animal("Animal Three", "Location Three3"),
                        new Animal("Animal One", "Location 1"),
                        new Animal("Animal Two", "Location Two"),
                        new Animal("Animal Three", "Location Three3")
                )
        ));
    }
}
