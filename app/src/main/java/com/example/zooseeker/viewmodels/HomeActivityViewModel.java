package com.example.zooseeker.viewmodels;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zooseeker.models.Animal;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

public class HomeActivityViewModel extends ViewModel {
    // List of animals to be displayed
    private List<Animal> _animals = new ArrayList<>();
    private MutableLiveData<List<Animal>> animals = new MutableLiveData<>();


    // List of selected animals
    private List<Animal> _selectedAnimals = new ArrayList<>();
    private MutableLiveData<List<Animal>> selectedAnimals = new MutableLiveData<>();
    public ObservableInt numSelectedAnimals = new ObservableInt(0);

    // Constructor
    public HomeActivityViewModel() {
        // TODO: Instantiate repository instance
        initAnimals();
    }

    public void toggleSelectedAnimal(Animal animal) {
        if (_selectedAnimals.contains(animal)) {
            _selectedAnimals.remove(animal);
        } else {
            _selectedAnimals.add(animal);
        }

        numSelectedAnimals.set(_selectedAnimals.size());
        selectedAnimals.setValue(_selectedAnimals);
    }

    public LiveData<List<Animal>> getSelectedAnimals() { return selectedAnimals; }

    public LiveData<List<Animal>> getAnimals() {
        return animals;
    }

    public void setAnimals(MutableLiveData<List<Animal>> animals) {
        this.animals = animals;
    }

    private void initAnimals() {
        setAnimals(new MutableLiveData<>(new ArrayList<>(
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
        )));
    }
}
