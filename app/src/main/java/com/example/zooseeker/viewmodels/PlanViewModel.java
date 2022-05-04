package com.example.zooseeker.viewmodels;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zooseeker.models.Graph.GraphData.GraphNode;

import java.util.List;

public class PlanViewModel extends ViewModel {
    private int curExhibit = -1;
    private List<List<GraphNode>> _plan;
    public MutableLiveData<List<GraphNode>> directions;
    public ObservableField<String> nextExhibit;

    public PlanViewModel() {
        directions = new MutableLiveData<>();
    }

    public void setPlan(List<List<GraphNode>> plan) {
        this._plan = plan;
    }

    public void getNextDirections() {
        if (curExhibit < _plan.size() - 1) {
            curExhibit++;
            setDirections(_plan.get(curExhibit));
        }
    }

    private void setDirections(List<GraphNode> directions) {
        this.directions.setValue(directions);
    }

    public LiveData<List<GraphNode>> getDirections() {
        return directions;
    }
}
