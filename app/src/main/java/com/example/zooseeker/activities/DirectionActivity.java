package com.example.zooseeker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.DirectionAdapter;
import com.example.zooseeker.databinding.ActivityDirectionBinding;
import com.example.zooseeker.fragments.RouteSummaryFragment;
import com.example.zooseeker.models.DirectionItem;
import com.example.zooseeker.models.Graph.GraphData.GraphEdge;
import com.example.zooseeker.viewmodels.HomeActivityViewModel;
import com.example.zooseeker.viewmodels.PlanViewModel;

import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    private PlanViewModel vm;
    private ActivityDirectionBinding binding;
    private Button buttonDialog;
    private RouteSummaryFragment routeSummaryFragment;
    private boolean Detailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_direction);
        // Set viewmodel
        vm = new ViewModelProvider(this).get(PlanViewModel.class);
        binding.setVm(vm);

        // Initialize the route
        vm.initRoute(intent.getStringArrayListExtra("selected_animals"));

        // Initialize recyclerview and adapter
        RecyclerView rv = findViewById(R.id.direction_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        DirectionAdapter adapter = new DirectionAdapter(vm.getRoute().getGraph());
        rv.setAdapter(adapter);

        // Observe changes to list of current directions
        vm.getDirections().observe(this, adapter::setDirections);

        // Show route summary fragment
        buttonDialog = findViewById(R.id.routeSummaryButton);
        buttonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeSummaryFragment = new RouteSummaryFragment();
                routeSummaryFragment.show(getSupportFragmentManager(), "TAG");
            }
        });
    }

    // Hide route summary fragment
    public void onHideClicked(View view){
        routeSummaryFragment.dismiss();
    }

    /**
     * Called when the next button is clicked
     * @param view
     */
    public void onLaunchNextClicked(View view) {
        vm.nextExhibitCommand.execute(this);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.direction_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.eraseRoutePlanButton:
                vm.clearPlan();
            // If it doesn't break we'll delete this
//              this.finish();
//              this.finish();
//              // possible stack overflow
//              Intent intent = new Intent(this, HomeActivity.class);
//              startActivity(intent);
                Button button = findViewById(R.id.eraseSelectedExhibitsButton);
                button.performClick();
                return true;
            case R.id.toggleDetailed:
                Detailed = !item.isChecked();
                item.setChecked(Detailed);
                vm.detailedDirectionToggle.setValue(Detailed);
                vm.updateCurrentDirections();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}