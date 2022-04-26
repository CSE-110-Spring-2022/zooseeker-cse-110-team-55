package com.example.zooseeker.viewmodels;

import android.content.Context;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.AnimalItemDao;
import com.example.zooseeker.repositories.AnimalDatabase;

import java.util.ArrayList;
import java.util.*;

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
        setAnimals(new ArrayList<>());
        selectedAnimals.setValue(_selectedAnimals);
    }

    public void toggleSelectedAnimal(Animal animal) {
        Optional<Animal> potentialMatch = _selectedAnimals
                .stream()
                .filter(a -> a.name.equals(animal.name))
                .findFirst();
        Animal matchedAnimal = potentialMatch.orElse(null);

        if (matchedAnimal != null) {
            _selectedAnimals.remove(matchedAnimal);
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

    public void setAnimals(List<Animal> animals) {
        this.animals.setValue(animals);
    }

    public List<Animal> searchInDatabase(Context context, String query) {
        AnimalItemDao animalItemDao = AnimalDatabase.getSingleton(context).animalItemDao();
        return animalItemDao.get(query);
    }
}
