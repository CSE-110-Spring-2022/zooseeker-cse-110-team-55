package com.example.zooseeker.activities;

import static com.example.zooseeker.util.Constant.EXTRA_LISTEN_TO_GPS;
import static com.example.zooseeker.util.Constant.SHARED_PREF;
import static com.example.zooseeker.util.Constant.CURR_INDEX;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.Observable.OnPropertyChangedCallback;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.zooseeker.R;
import com.example.zooseeker.adapters.DirectionAdapter;
import com.example.zooseeker.databinding.ActivityDirectionBinding;
import com.example.zooseeker.fragments.RouteSummaryFragment;
import com.example.zooseeker.util.Alert;
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
        buttonDialog.setOnClickListener(view -> {
            routeSummaryFragment = new RouteSummaryFragment();
            routeSummaryFragment.show(getSupportFragmentManager(), "TAG");
        });

        // Load direction index from shared preferences and configure plan view model
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        int curr_index = sharedPreferences.getInt(CURR_INDEX, 0);
        for (int i = 0; i < curr_index; i++){
            vm.nextExhibitCommand.execute(this);
        }

        // Use location
        boolean useGps = getIntent().getBooleanExtra(EXTRA_LISTEN_TO_GPS, false);
        if (useGps) initLocationListener(vm::adjustToNewLocation);
        else vm.lastKnownLocation.observe(this, vm::adjustToNewLocation);

        var x = this;
        vm.closestExhibit.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Alert.alert(x, "Uh oh!", String.format("Looks like you're off track. You're now closest to: %s. Reroute?", ((ObservableField<String>) sender).get()));
            }
        });
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

    @SuppressLint("SetTextI18n")
    public void onMockButtonClicked(View view) {
        // TODO: could define this layout in an XML and inflate it, instead of defining in code...
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
                | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
                | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        // Default is orangutan!
        final EditText latInput = new EditText(this);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        latInput.setText("32.736864688333235");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        lngInput.setText("-117.16364410510093");

        final LinearLayout layout = new LinearLayout(this);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        var builder = new AlertDialog.Builder(this)
                .setTitle("Inject a Mock Location")
                .setView(layout)
                .setPositiveButton("Submit", (dialog, which) -> {
                    var lat = Double.parseDouble(latInput.getText().toString());
                    var lng = Double.parseDouble(lngInput.getText().toString());
                    vm.lastKnownLocation.setValue((Pair.create(lat, lng)));
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        builder.show();
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
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
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









