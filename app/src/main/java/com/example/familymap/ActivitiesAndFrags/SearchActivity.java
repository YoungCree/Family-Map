package com.example.familymap.ActivitiesAndFrags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.familymap.R;
import com.example.familymap.ServerAndCache.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import FamServer.model.Event;
import FamServer.model.Person;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView searchRecyclerView;
    private FamAdapter famAdapter;
    private static final int PERSON_TYPE = 0;
    private static final int EVENT_TYPE = 1;

    private ArrayList<Person> basicPeople = new ArrayList<>();
    private ArrayList<Event> basicEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                basicPeople = searchPeople(query);
                basicEvents = searchEvents(query);
                updateUI(basicPeople, basicEvents);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                basicPeople = searchPeople(newText);
                basicEvents = searchEvents(newText);
                if (newText.length() == 0) {
                    basicEvents.clear();
                    basicPeople.clear();
                }
                updateUI(basicPeople, basicEvents);
                return true;
            }
        });

        searchRecyclerView = findViewById(R.id.recycler_view);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private ArrayList<Person> searchPeople(String query) {
        DataCache dataCache = DataCache.getInstance();
        ArrayList<Person> people = dataCache.getPeople();
        ArrayList<Person> filterPeople = new ArrayList<>();

        for (Person i : people) {
            if (i.getFirst_name().toLowerCase().contains(query.toLowerCase()) ||
                i.getLast_name().toLowerCase().contains(query.toLowerCase())) {
                    if (!filterPeople.contains(i)) filterPeople.add(i);
            }
        }
        return filterPeople;
    }

    private ArrayList<Event> searchEvents(String query) {
        DataCache dataCache = DataCache.getInstance();
        ArrayList<Event> events = dataCache.getFilteredEvents();
        ArrayList<Event> eventsFiltered = new ArrayList<>();

        for (Event i : events) {
            String year = Integer.toString(i.getEvent_year());
            if (i.getCountry().toLowerCase().contains(query.toLowerCase()) ||
                i.getCity().toLowerCase().contains(query.toLowerCase()) ||
                i.getEvent_type().toLowerCase().contains(query.toLowerCase()) ||
                year.toLowerCase().contains(query.toLowerCase())) {
                    if (!eventsFiltered.contains(i)) eventsFiltered.add(i);
            }
        }
        return eventsFiltered;
    }

    @Override
     public void onResume() {
        super.onResume();
        updateUI(basicPeople, basicEvents);
     }

    private void updateUI(ArrayList<Person> peopleFiltered, ArrayList<Event> eventsFiltered) {
        famAdapter = new FamAdapter(peopleFiltered, eventsFiltered);
        searchRecyclerView.setAdapter(famAdapter);
    }

    private class FamHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Person person;
        private Event event;

        private final int viewType;

        private ImageView markerIcon;
        private TextView personNameTextView;
        private TextView bottomDescriptionTextView;

        FamHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            markerIcon = view.findViewById(R.id.listMarkerImageSearch);
            personNameTextView = view.findViewById(R.id.liftListItemSearch);
            bottomDescriptionTextView = view.findViewById(R.id.lifeListItemNameSearch);

            view.setOnClickListener(this);
        }

        void bind(Person personGiven) {
            person = personGiven;
            Drawable maleIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(40);
            Drawable femaleIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(40);
            if (person.getGender().equals("m")) markerIcon.setImageDrawable(maleIcon);
            else markerIcon.setImageDrawable(femaleIcon);
            String personName = person.getFirst_name() + " " + person.getLast_name();
            personNameTextView.setText(personName);
            bottomDescriptionTextView.setText("");
        }

        void bind(Event eventGiven) {
            event = eventGiven;
            DataCache dataCache = DataCache.getInstance();
            HashMap<String, Integer> eventsToColor = dataCache.getEventToColor();
            float[] helper = new float[3];
            helper[0] = eventsToColor.get(event.getEvent_type().toLowerCase());
            helper[1] = 100;
            helper[2] = 50;
            Drawable marker = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).color(Color.HSVToColor(helper)).sizeDp(40);
            markerIcon.setImageDrawable(marker);
            String eventInfo = event.getEvent_type() +
                    ": " + event.getCity() + ", " +
                    event.getCountry() + " (" +
                    event.getEvent_year() + ")";
            Person associatedPerson = dataCache.findPerson(event.getPerson_id());
            String description = associatedPerson.getFirst_name() + " " + associatedPerson.getLast_name();
            personNameTextView.setText(eventInfo);
            bottomDescriptionTextView.setText(description);
        }

        @Override
        public void onClick(View v) {
            DataCache dataCache = DataCache.getInstance();
            if (viewType == PERSON_TYPE) {
                dataCache.setCurrPerson(person);
                Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                startActivity(intent);
            } else {
                dataCache.setCurrEvent(event);
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                startActivity(intent);
            }
        }
    }

    private class FamAdapter extends RecyclerView.Adapter<FamHolder> {

        private List<Person> peeps;
        private List<Event> events;

        FamAdapter(ArrayList<Person> peepsGiven, ArrayList<Event> eventsGiven) {
            peeps = peepsGiven;
            events = eventsGiven;
        }

        @Override
        public int getItemViewType(int position) {
            return position < peeps.size() ? PERSON_TYPE : EVENT_TYPE;
        }

        @NonNull
        @Override
        public FamHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater.inflate(R.layout.search_list_item, parent, false);
            return new FamHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull FamHolder holder, int position) {
            if (position < peeps.size()) {
                Person person = peeps.get(position);
                holder.bind(person);
            } else {
                holder.bind(events.get(position - peeps.size()));
            }
        }

        @Override
        public int getItemCount() {
            return peeps.size() + events.size();
        }
    }

}
