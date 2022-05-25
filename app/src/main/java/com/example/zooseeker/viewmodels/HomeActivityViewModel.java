package com.example.zooseeker.viewmodels;

import android.content.Context;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zooseeker.contracts.ICommand;
import com.example.zooseeker.models.Animal;
import com.example.zooseeker.models.Animal.AnimalDisplay;
import com.example.zooseeker.models.SearchCommandParams;
import com.example.zooseeker.models.SelectedAnimalParams;
import com.example.zooseeker.repositories.AnimalItemDao;
import com.example.zooseeker.repositories.AnimalDatabase;

import java.util.ArrayList;
import java.util.*;

public class HomeActivityViewModel extends ViewModel {
    // List of animals to be displayed
    private MutableLiveData<List<AnimalDisplay>> animals = new MutableLiveData<>();

    // List of selected animals
    private List<AnimalDisplay> _selectedAnimals = new ArrayList<>();
    public ObservableInt numSelectedAnimals = new ObservableInt(0);


    // Commands for Activity to call
    public ICommand<SearchCommandParams> searchCommand = params -> performSearch(params.context, params.query);
    public ICommand<SelectedAnimalParams> selectAnimalCommand = params -> {
        AnimalDisplay animal = getAnimals().getValue().get(params.position);
        toggleSelectedAnimal(animal);
    };

    // Constructor
    public HomeActivityViewModel() {
        // TODO: Instantiate repository instance
        setAnimals(new ArrayList<>());
    }

    private void toggleSelectedAnimal(AnimalDisplay animal) {
        Optional<AnimalDisplay> potentialMatch = _selectedAnimals
                .stream()
                .filter(a -> a.name.equals(animal.name))
                .findFirst();
        AnimalDisplay matchedAnimal = potentialMatch.orElse(null);

        if (matchedAnimal != null) {
            _selectedAnimals.remove(matchedAnimal);
        } else {
            _selectedAnimals.add(animal);
        }

        numSelectedAnimals.set(_selectedAnimals.size());
    }

    private void performSearch(Context context, String query) {
        // Show selected exhibits if empty
        var animalsToDisplay = query.equals("") ?
                new ArrayList<>(_selectedAnimals) :
                searchInDatabase(context, query);

        setAnimals(animalsToDisplay);
    }

    private List<AnimalDisplay> searchInDatabase(Context context, String query) {
        AnimalItemDao animalItemDao = AnimalDatabase.getSingleton(context).animalItemDao();
        return animalItemDao.getDisplay(query);
    }

    public AnimalDisplay searchInDatabaseById(Context context, String query) {
        AnimalItemDao animalItemDao = AnimalDatabase.getSingleton(context).animalItemDao();
        return animalItemDao.getById(query);
    }

    public List<AnimalDisplay> getSelectedAnimals() { return _selectedAnimals; }

    public LiveData<List<AnimalDisplay>> getAnimals() {
        return animals;
    }

    public void setAnimals(List<AnimalDisplay> animals) {
        this.animals.setValue(animals);
    }

    public void setSelectedAnimals(List<AnimalDisplay> animalList){
        _selectedAnimals = animalList;
        numSelectedAnimals.set(animalList.size());
    }

    public void clear() {
        _selectedAnimals.clear();
        numSelectedAnimals.set(0);
    }
}
