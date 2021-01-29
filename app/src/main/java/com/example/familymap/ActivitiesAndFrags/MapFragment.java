package com.example.familymap.ActivitiesAndFrags;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymap.R;
import com.example.familymap.ServerAndCache.DataCache;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import FamServer.model.Event;
import FamServer.model.Person;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private DataCache dataCache = DataCache.getInstance();
    private ArrayList<Event> events = dataCache.getEvents();
    private ArrayList<Person> people = dataCache.getPeople();
    private TextView bottomMessage;
    private ImageView genderImage;
    private HashMap<String, ArrayList<Event>> eventToType = dataCache.getEventsToType();
    private HashMap<String, Integer> eventToColor = dataCache.getEventToColor();
    private boolean isMainMap = true;
    private Event selectedEvent;
    private Person selectedPerson;
    private Event currEvent = dataCache.getCurrEvent();
    private List<Polyline> spousePolylines = new ArrayList<>();
    private List<Polyline> familyPolylines = new ArrayList<>();
    private List<Polyline> lifePolylines = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private HashMap<Person, ArrayList<Event>> eventsToPeople = dataCache.getEventsToPeople();
    private HashMap<Person, ArrayList<Person>> peopleToPeople = dataCache.getPeopleToFam();

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomMessage = view.findViewById(R.id.mapTextView);
        genderImage = view.findViewById(R.id.genderImage);

        if (isMainMap) setHasOptionsMenu(true);
        if (!dataCache.isSpouseLinesChecked()) {
            for (Polyline i : spousePolylines) {
                i.remove();
            }
            spousePolylines.clear();
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        addMarkers();
        if (!isMainMap) {
            if (dataCache.isSpouseLinesChecked()) drawSpouseLines();
            if (dataCache.isFamTreeChecked()) {
                drawFamLines(selectedPerson, peopleToPeople.get(selectedPerson),
                        new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude()), 25);
            }
            if (dataCache.isLifeStoryChecked()) drawLifeLines();
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Erase all previous polylines
                for (Polyline i : spousePolylines) {
                    i.remove();
                }
                spousePolylines.clear();
                for (Polyline i : familyPolylines) {
                    i.remove();
                }
                familyPolylines.clear();
                for (Polyline i : lifePolylines) {
                    i.remove();
                }
                lifePolylines.clear();

                String eventID = (String) marker.getTag();
                selectedEvent = findEvent(eventID);
                selectedPerson = findPerson(selectedEvent.getPerson_id());
                String messageToSet = selectedPerson.getFirst_name()
                        + " " + selectedPerson.getLast_name()
                        + "\n"
                        + selectedEvent.getEvent_type()
                        + ": " + selectedEvent.getCity() + ", " + selectedEvent.getCountry()
                        + " (" + selectedEvent.getEvent_year() + ")";
                bottomMessage.setText(messageToSet);
                Drawable maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(40);
                Drawable femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(40);
                if (selectedPerson.getGender().equals("m")) genderImage.setImageDrawable(maleIcon);
                else genderImage.setImageDrawable(femaleIcon);

                //Drawing lines...
                ArrayList<Person> famOfSelectedPerson = peopleToPeople.get(selectedPerson);
                LatLng firstLat = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
                //Spouse Lines...
                if (dataCache.isSpouseLinesChecked()) {
                    drawSpouseLines();
                }
                //Family Tree Lines...
                if (dataCache.isFamTreeChecked()) {
                    drawFamLines(selectedPerson, famOfSelectedPerson, firstLat, 25);
                }
                //Life Event Lines...
                if (dataCache.isLifeStoryChecked()) {
                    drawLifeLines();
                }

                bottomMessage.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        dataCache.setCurrPerson(selectedPerson);
                        Intent intent = new Intent(getActivity(), PersonActivity.class);
                        startActivity(intent);
                    }
                });
                return false;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if (!dataCache.isFirstTimeMap()) {
            if (dataCache.isMaleEventChecked() || dataCache.isFemaleEventChecked() || dataCache.isFatherSideChecked() || dataCache.isMotherSideChecked()) {
                if (!dataCache.isFatherSideChecked()) dataCache.removeFatherEvents();
                if (!dataCache.isMotherSideChecked()) dataCache.removeMotherEvents();
                events = (ArrayList<Event>) dataCache.getFilteredEvents().clone();
                if (isMainMap) {
                    map.clear();
                    addMarkers();
                }
            } else if (!dataCache.isFemaleEventChecked() && !dataCache.isMaleEventChecked() && !dataCache.isMotherSideChecked() && !dataCache.isFatherSideChecked()) {
                if (markers.size() != 0) {
                    events = dataCache.getFilteredEvents();
                    map.clear();
                    addMarkers();
                }
            }
            if (!dataCache.isSpouseLinesChecked()) {
                for (Polyline i : spousePolylines) {
                    i.remove();
                }
                spousePolylines.clear();
            } else {
                if (selectedPerson != null && selectedEvent != null && isMainMap) {
                    for (Marker i : markers) {
                        if (i.getTag() == selectedEvent.getEvent_id()) drawSpouseLines();
                    }
                }
            }
            if (!dataCache.isFamTreeChecked()) {
                for (Polyline i : familyPolylines) {
                    i.remove();
                }
                familyPolylines.clear();
            } else {
                if (selectedPerson != null && selectedEvent != null && isMainMap) {
                    for (Marker i : markers) {
                        if (i.getTag() == selectedEvent.getEvent_id()) {
                            ArrayList<Person> famOfSelectedPerson = peopleToPeople.get(selectedPerson);
                            LatLng firstLat = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
                            drawFamLines(selectedPerson, famOfSelectedPerson, firstLat, 25);
                        }
                    }
                }
            }
            if (!dataCache.isLifeStoryChecked()) {
                for (Polyline i : lifePolylines) {
                    i.remove();
                }
                lifePolylines.clear();
            } else {
                if (selectedPerson != null && selectedEvent != null && isMainMap) {
                    for (Marker i : markers) {
                        if (i.getTag() == selectedEvent.getEvent_id()) drawLifeLines();
                    }
                }
            }
        } else dataCache.setFirstTimeMap(false);
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchMenuItem:
                //This is where we need to switch to search frag
                Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                startActivity(intentSearch);

                return true;
            case R.id.settingsMenuItem:
                //This is where we need to switch to settings frag
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addMarkers() {

        for (Event i: events) {
            LatLng tmpPos = new LatLng(i.getLatitude(), i.getLongitude());
            Marker tmpMark;
            tmpMark = map.addMarker(new MarkerOptions()
                    .position(tmpPos)
                    .icon(BitmapDescriptorFactory.defaultMarker(eventToColor.get(i.getEvent_type().toLowerCase()))));
            tmpMark.setTag(i.getEvent_id());
            markers.add(tmpMark);
            if (!isMainMap) {
                if (i.getEvent_id().equals(currEvent.getEvent_id())) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(tmpPos));
                    selectedEvent = currEvent;
                    selectedPerson = findPerson(selectedEvent.getPerson_id());
                    String messageToSet = selectedPerson.getFirst_name()
                            + " " + selectedPerson.getLast_name()
                            + "\n"
                            + selectedEvent.getEvent_type()
                            + ": " + selectedEvent.getCity() + ", " + selectedEvent.getCountry()
                            + " (" + selectedEvent.getEvent_year() + ")";
                    bottomMessage.setText(messageToSet);
                    Drawable maleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(40);
                    Drawable femaleIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(40);
                    if (selectedPerson.getGender().equals("m")) genderImage.setImageDrawable(maleIcon);
                    else genderImage.setImageDrawable(femaleIcon);

                    bottomMessage.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            dataCache.setCurrPerson(selectedPerson);
                            Intent intent = new Intent(getActivity(), PersonActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }

    public Event findEvent(String eventID) {
        for (Event i: events) {
            if (i.getEvent_id().equals(eventID)) return i;
        }
        return null;
    }

    public Person findPerson(String personID) {
        for (Person i: people) {
            if (i.getPerson_id().equals(personID)) return i;
        }
        return null;
    }

    public void setIsMainMap(boolean aBool) {
        isMainMap = aBool;
    }

    public void drawFamLines(Person person, ArrayList<Person> famOfPerson, LatLng firstLat, float level) {
        if (findDad(person, famOfPerson) != null) {
            Person father = findDad(person, famOfPerson);
            ArrayList<Person> relativesDad = peopleToPeople.get(father);
            Event earlyEventFather = eventsToPeople.get(father).get(0);
            LatLng secondLat = new LatLng(earlyEventFather.getLatitude(), earlyEventFather.getLongitude());
            for (Marker i : markers) {
                if (i.getTag() == earlyEventFather.getEvent_id()) {
                    familyPolylines.add(map.addPolyline(new PolylineOptions().add(firstLat, secondLat).color(Color.BLUE).width(level)));
                    drawFamLines(father, relativesDad, secondLat, level-4);
                }
            }
        }
        if (findMom(person, famOfPerson) != null) {
            Person mother = findMom(person, famOfPerson);
            ArrayList<Person> relativesMom = peopleToPeople.get(mother);
            Event earlyEventMother = eventsToPeople.get(mother).get(0);
            LatLng secondLat = new LatLng(earlyEventMother.getLatitude(), earlyEventMother.getLongitude());
            for (Marker i : markers) {
                if (i.getTag() == earlyEventMother.getEvent_id()) {
                    familyPolylines.add(map.addPolyline(new PolylineOptions().add(firstLat, secondLat).color(Color.BLUE).width(level)));
                    drawFamLines(mother, relativesMom, secondLat, level - 4);
                }
            }
        }
    }

    public void drawSpouseLines() {
        ArrayList<Person> famOfSelectedPerson = peopleToPeople.get(selectedPerson);
        LatLng firstLat = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
        for (Person i : famOfSelectedPerson) {
            if (selectedPerson.getSpouse_id() != null) {
                if (selectedPerson.getSpouse_id().equals(i.getPerson_id())) {
                    Event earlyEventSpouse = eventsToPeople.get(i).get(0);
                    LatLng secondLat = new LatLng(earlyEventSpouse.getLatitude(), earlyEventSpouse.getLongitude());
                    for (Marker j : markers) {
                        if (j.getTag() == earlyEventSpouse.getEvent_id()) {
                            spousePolylines.add(map.addPolyline(new PolylineOptions().add(firstLat, secondLat).color(Color.RED)));
                        }
                    }
                }
            }
        }
    }

    public void drawLifeLines() {
        LatLng firstLat = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
        if (eventsToPeople.get(selectedPerson) != null) {
            ArrayList<Event> lifeStoryEvents = eventsToPeople.get(selectedPerson);
            firstLat = new LatLng(lifeStoryEvents.get(0).getLatitude(), lifeStoryEvents.get(0).getLongitude());
            for (Event i : lifeStoryEvents) {
                LatLng secondLat = new LatLng(i.getLatitude(), i.getLongitude());
                for (Marker j : markers) {
                    if (j.getTag() == i.getEvent_id()) {
                        lifePolylines.add(map.addPolyline(new PolylineOptions().add(firstLat, secondLat).color(Color.YELLOW)));
                        firstLat = secondLat;
                    }
                }

            }
        }
    }

    public Person findDad(Person person, ArrayList<Person> famOfPerson) {
        if (person.getFather_id() == null) return null;
        for (Person i : famOfPerson) {
            if (person.getFather_id().equals(i.getPerson_id())) {
                return i;
            }
        }
        return null;
    }

    public Person findMom(Person person, ArrayList<Person> famOfPerson) {
        if (person.getMother_id() == null) return null;
        for (Person i : famOfPerson) {
            if (person.getMother_id().equals(i.getPerson_id())) {
                return i;
            }
        }
        return null;
    }
}