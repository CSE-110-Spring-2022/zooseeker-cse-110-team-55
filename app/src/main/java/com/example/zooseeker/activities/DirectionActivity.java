package com.example.zooseeker.activities;

import static com.example.zooseeker.util.Constant.EXTRA_LISTEN_TO_GPS;
import static com.example.zooseeker.util.Constant.SHARED_PREF;
import static com.example.zooseeker.util.Constant.CURR_INDEX;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.DirectionAdapter;
import com.example.zooseeker.databinding.ActivityDirectionBinding;
import com.example.zooseeker.fragments.RouteSummaryFragment;
import com.example.zooseeker.util.PermissionChecker;
import com.example.zooseeker.viewmodels.PlanViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

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
        // Set view model
        vm = new ViewModelProvider(this).get(PlanViewModel.class);
        binding.setVm(vm);

        // Load exhibit groups
        var t = new TypeToken<HashMap<String, List<String>>>(){}.getType();
        HashMap<String, List<String>> groups = new Gson().fromJson(intent.getStringExtra("exhibit_groups"), t);
        vm.setExhibitGroups(groups);

        // Initialize the route
        ArrayList<String> selected = intent.getStringArrayListExtra("selected_animals");
        vm.initRoute(selected);

        // Initialize recyclerview and adapter
        RecyclerView rv = findViewById(R.id.direction_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        DirectionAdapter adapter = new DirectionAdapter(vm.getRoute().getGraph());
        rv.setAdapter(adapter);

        // Observe changes to list of current directions
        vm.getDirections().observe(this, adapter::setDirections);
        vm.detailedDirectionToggle.observe(this, vm::updateCurrentDirections);

        // Show route summary fragment
        buttonDialog = findViewById(R.id.routeSummaryButton);
        buttonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                routeSummaryFragment = new RouteSummaryFragment();
                routeSummaryFragment.show(getSupportFragmentManager(), "TAG");
            }
        });

        // Load direction index from shared preferences and configure plan view model
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        int curr_index = sharedPreferences.getInt(CURR_INDEX, 0);
        for (int i = 0; i < curr_index; i++){
            vm.nextExhibitCommand.execute(this);
        }

        // Use location
        boolean useGps = getIntent().getBooleanExtra(EXTRA_LISTEN_TO_GPS, true);
        if (useGps) {
            initLocationListener(vm.lastKnownLocation::setValue);
        }
    }

    @SuppressLint("MissingPermission")
    private void initLocationListener(Consumer<Pair<Double, Double>> handler) {
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                var coords = new Pair<>(location.getLatitude(), location.getLongitude());
                handler.accept(coords);
            }
        };
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
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

        // Increment direction index from shared preferences or reset index when reaches final destination
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int temp = sharedPreferences.getInt(CURR_INDEX, 0);
        editor.putInt(CURR_INDEX, temp + 1);
        if(vm.isLastExhibit()){
            editor.putInt(CURR_INDEX, -1);
        }
        editor.apply();
    }

    // Create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.direction_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.eraseRoutePlanButton:
                SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                vm.clearPlan();

                // TODO Change to update activity from another activity
                super.finish();
                this.finish();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                return true;
            case R.id.toggleDetailed:
                Detailed = !item.isChecked();
                item.setChecked(Detailed);
                vm.detailedDirectionToggle.setValue(Detailed);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}









