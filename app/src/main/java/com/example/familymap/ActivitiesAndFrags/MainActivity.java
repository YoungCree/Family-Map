package com.example.familymap.ActivitiesAndFrags;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.familymap.R;
import com.example.familymap.ServerAndCache.DataCache;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

public class MainActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private MapFragment mapFragment;
    private FragmentManager fm = this.getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataCache dataCache = DataCache.getInstance();

        Iconify.with(new FontAwesomeModule());

        if (dataCache.getEvents() == null || dataCache.isLogout()) {
            loginFragment = (LoginFragment) fm.findFragmentById(R.id.LoginFrameLayout);
            if (loginFragment == null) {
                loginFragment = new LoginFragment();
                fm.beginTransaction()
                        .add(R.id.LoginFrameLayout, loginFragment)
                        .commit();
            }
        }
        else {
            ShowMapFrag();
        }
    }

    public void ShowMapFrag() {
        mapFragment = new MapFragment();
        mapFragment.setIsMainMap(true);
        fm.beginTransaction()
                .replace(R.id.LoginFrameLayout, mapFragment)
                .addToBackStack(null)
                .commit();
    }
}
