package com.example.zooseeker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zooseeker.R;
import com.example.zooseeker.models.Animal;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to display data in Animal model
 */
public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalHolder> {
    List<Animal> animals = new ArrayList<>();
    OnAnimalClickListener onAnimalClickListener;

    public AnimalAdapter(OnAnimalClickListener onAnimalClickListener) {
        this.onAnimalClickListener = onAnimalClickListener;
    }

    @NonNull
    @Override
    public AnimalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_item, parent, false);
        return new AnimalHolder(itemView, onAnimalClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalHolder holder, int position) {
        Animal curAnimal = animals.get(position);
        holder.animalName.setText(curAnimal.name);
        holder.checkBox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return animals.size();
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
        // TODO: Avoid using notifyDataSetChanged
        notifyDataSetChanged();
    }

    /**
     * Internal ViewHolder to hold
     */
    class AnimalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView animalName;
        private CheckBox checkBox;

        private OnAnimalClickListener onAnimalClickListener;

        public AnimalHolder(@NonNull View itemView, OnAnimalClickListener onAnimalClickListener) {
            super(itemView);
            animalName = itemView.findViewById(R.id.animal_name);
            checkBox = itemView.findViewById(R.id.animal_checked);
            checkBox.setActivated(false);
            this.onAnimalClickListener = onAnimalClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onAnimalClickListener.onAnimalClick(getAdapterPosition());
            checkBox.setChecked(!checkBox.isChecked());
        }
    }

    public interface OnAnimalClickListener {
        void onAnimalClick(int position);
    }
}
