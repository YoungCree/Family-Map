package com.example.familymap.ActivitiesAndFrags;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.familymap.R;
import com.example.familymap.ServerAndCache.DataCache;

public class SettingsActivity extends AppCompatActivity {

    private Switch lifeStoryLinesSwitch;
    private Switch famTreeLinesSwitch;
    private Switch spouseLinesSwitch;
    private Switch fatherSideSwitch;
    private Switch motherSideSwitch;
    private Switch maleEventSwitch;
    private Switch femaleEventSwitch;
    private LinearLayout logoutLinearLayout;

    private DataCache dataCache = DataCache.getInstance();

    private boolean lifeStoryChecked = dataCache.isLifeStoryChecked();
    private boolean famTreeChecked = dataCache.isFamTreeChecked();
    private boolean spouseLinesChecked = dataCache.isSpouseLinesChecked();
    private boolean fatherSideChecked = dataCache.isFatherSideChecked();
    private boolean motherSideChecked = dataCache.isMotherSideChecked();
    private boolean maleEventChecked = dataCache.isMaleEventChecked();
    private boolean femaleEventChecked = dataCache.isFemaleEventChecked();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dataCache.setLogout(false);

        lifeStoryLinesSwitch = findViewById(R.id.LifeStoryLinesSwitch);
        famTreeLinesSwitch = findViewById(R.id.FamTreeLinesSwitch);
        spouseLinesSwitch = findViewById(R.id.SpouseLinesSwitch);
        fatherSideSwitch = findViewById(R.id.FatherSideSwitch);
        motherSideSwitch = findViewById(R.id.MotherSideSwitch);
        maleEventSwitch = findViewById(R.id.MaleEventSwitch);
        femaleEventSwitch = findViewById(R.id.FemaleEventSwitch);
        logoutLinearLayout = findViewById(R.id.LogoutLinearLayout);

        lifeStoryLinesSwitch.setChecked(lifeStoryChecked);
        famTreeLinesSwitch.setChecked(famTreeChecked);
        spouseLinesSwitch.setChecked(spouseLinesChecked);
        fatherSideSwitch.setChecked(fatherSideChecked);
        motherSideSwitch.setChecked(motherSideChecked);
        maleEventSwitch.setChecked(maleEventChecked);
        femaleEventSwitch.setChecked(femaleEventChecked);

        if (dataCache.isFirstTimeSettings()) {
            fatherSideSwitch.setChecked(true);
            motherSideSwitch.setChecked(true);
            maleEventSwitch.setChecked(true);
            femaleEventSwitch.setChecked(true);
            dataCache.setFatherSideChecked(true);
            dataCache.setMotherSideChecked(true);
            dataCache.setMaleEventChecked(true);
            dataCache.setFemaleEventChecked(true);
            dataCache.setFirstTimeSettings(false);
        }

        lifeStoryLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setLifeStoryChecked(true);
                } else {
                    dataCache.setLifeStoryChecked(false);
                }
            }
        });

        famTreeLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setFamTreeChecked(true);
                } else {
                    dataCache.setFamTreeChecked(false);
                }
            }
        });

        spouseLinesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setSpouseLinesChecked(true);
                } else {
                    dataCache.setSpouseLinesChecked(false);
                }
            }
        });

        fatherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setFatherSideChecked(true);
                    dataCache.filterEventsToFather();
                } else {
                    dataCache.setFatherSideChecked(false);
                    dataCache.removeFatherEvents();
                }
            }
        });

        motherSideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setMotherSideChecked(true);
                    dataCache.filterEventsToMother();
                } else {
                    dataCache.setMotherSideChecked(false);
                    dataCache.removeMotherEvents();
                }
            }
        });

        maleEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setMaleEventChecked(true);
                    dataCache.filterEventsToMale();
                } else {
                    dataCache.setMaleEventChecked(false);
                    dataCache.clearMaleFilter();
                }
            }
        });

        femaleEventSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dataCache.setFemaleEventChecked(true);
                    dataCache.filterEventsToFemale();
                } else {
                    dataCache.setFemaleEventChecked(false);
                    dataCache.clearFemailFilter();
                }
            }
        });

        logoutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                dataCache.setLogout(true);
                dataCache.setLifeStoryChecked(false);
                dataCache.setFemaleEventChecked(false);
                dataCache.setFamTreeChecked(false);
                dataCache.setMaleEventChecked(false);
                dataCache.setSpouseLinesChecked(false);
                dataCache.setFatherSideChecked(false);
                dataCache.setMotherSideChecked(false);
                startActivity(intent);
                dataCache.setFirstTimeSettings(true);
                finishAffinity();
            }
        });
    }
}