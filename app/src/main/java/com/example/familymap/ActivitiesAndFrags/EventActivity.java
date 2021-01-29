package com.example.familymap.ActivitiesAndFrags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.familymap.R;

public class EventActivity extends AppCompatActivity {

    private MapFragment mapFragment;
    private FragmentManager fm = this.getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mapFragment = (MapFragment) fm.findFragmentById(R.id.MapFrameLayout);
        if (mapFragment == null) {
            mapFragment = new MapFragment();
            mapFragment.setIsMainMap(false);
            fm.beginTransaction()
                    .add(R.id.MapFrameLayout, mapFragment)
                    .commit();
        }
    }
}
