package com.example.familymap.ActivitiesAndFrags;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familymap.R;
import com.example.familymap.ServerAndCache.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import FamServer.model.Event;
import FamServer.model.Person;

public class PersonActivity extends AppCompatActivity {

    DataCache dataCache = DataCache.getInstance();
    private Person currPerson = dataCache.getCurrPerson();

    private TextView firstNameView;
    private TextView lastNameView;
    private TextView genderView;

    private HashMap<String, Integer> eventToColor = dataCache.getEventToColor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firstNameView = findViewById(R.id.FirstNameView);
        lastNameView = findViewById(R.id.LastNameView);
        genderView = findViewById(R.id.GenderVeiw);

        firstNameView.setText(currPerson.getFirst_name());
        lastNameView.setText(currPerson.getLast_name());
        if (currPerson.getGender().equals("m")) {
            genderView.setText(R.string.male);
        } else  {
            genderView.setText(R.string.female);
        }

        ExpandableListView expandableListView = findViewById(R.id.ExpandListLifeEvents);
        HashMap<Person, ArrayList<Event>> eventsToPeople = dataCache.getEventsToPeople();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        List<Event> events = eventsToPeople.get(currPerson);
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if (o1.getEvent_year() < o2.getEvent_year()) return -1;
                if (o2.getEvent_year() > o1.getEvent_year()) return +1;
                else return 0;
            }
        });
        List<Person> people = peopleToFam.get(currPerson);
        expandableListView.setAdapter(new ExpandableListAdapter(events, people));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENTS_POS = 0;
        private static final int PEOPLE_POS = 1;

        private List<Person> people;
        private List<Event> events;

        ExpandableListAdapter(List<Event> events, List<Person> people) {
            this.events = events;
            this.people = people;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENTS_POS:
                    return events.size();
                case PEOPLE_POS:
                    return people.size();
                default:
                    throw new IllegalArgumentException("Unrecognized" + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case EVENTS_POS:
                    return getString(R.string.lifeEventList);
                case PEOPLE_POS:
                    return getString(R.string.famPeople);
                default:
                    throw new IllegalArgumentException("Unrec group pos" + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case EVENTS_POS:
                    return events.get(childPosition);
                case PEOPLE_POS:
                    return people.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrec group pos" + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.life_list_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.lifeEventListHeader);

            switch (groupPosition) {
                case EVENTS_POS:
                    titleView.setText(R.string.lifeEventList);
                    break;
                case PEOPLE_POS:
                    titleView.setText(R.string.famPeople);
                    break;
                default:
                    throw new IllegalArgumentException("Unrec group pos" + groupPosition);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;
            ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();

            switch (groupPosition) {
                case EVENTS_POS:
                    itemView = getLayoutInflater().inflate(R.layout.life_list_item, parent, false);
                    if (filteredEvents.contains(events.get(childPosition))) {
                        initializeLifeEventView(itemView, childPosition);
                    }
                    // initialize
                    break;
                case PEOPLE_POS:
                    itemView = getLayoutInflater().inflate(R.layout.family_list_item, parent, false);
                    initializePeopleFamView(itemView, childPosition);
                    break;
                    // initialize
                default:
                    throw new IllegalArgumentException("Unrec group pos" + groupPosition);
            }
            return itemView;
        }

        private void initializeLifeEventView(View lifeEventItemView, final int childPosition) {
            TextView lifeEventView = lifeEventItemView.findViewById(R.id.lifeEventListHeader);

            ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();

            TextView lifeEventName = lifeEventItemView.findViewById(R.id.liftListItem);
            String eventInfo = events.get(childPosition).getEvent_type() +
                    ": " + events.get(childPosition).getCity() + ", " +
                    events.get(childPosition).getCountry() + " (" +
                    events.get(childPosition).getEvent_year() + ")";
            lifeEventName.setText(eventInfo);

            TextView lifeEventOwner = lifeEventItemView.findViewById(R.id.lifeListItemName);
            String eventOwner = currPerson.getFirst_name() + " " + currPerson.getLast_name();
            lifeEventOwner.setText(eventOwner);
            float[] helper = new float[3];
            helper[0] = eventToColor.get(events.get(childPosition).getEvent_type().toLowerCase());
            helper[1] = 100;
            helper[2] = 50;
            ImageView markerImage = lifeEventItemView.findViewById(R.id.listMarkerImage);
            Drawable marker = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).color(Color.HSVToColor(helper)).sizeDp(40);
            markerImage.setImageDrawable(marker);

            lifeEventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // switch to Event Activity focused on event chosen
                    dataCache.setCurrEvent(events.get(childPosition));
                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                    startActivity(intent);
                }
            });
        }

        private void initializePeopleFamView(View peopleFamItemView, final int childPosition) {
            Person tempPerson = people.get(childPosition);

            TextView peopleFamName = peopleFamItemView.findViewById(R.id.famListItem);
            String nameOfPerson = tempPerson.getFirst_name() + " " + tempPerson.getLast_name();
            peopleFamName.setText(nameOfPerson);

            TextView peopleFamDesc = peopleFamItemView.findViewById(R.id.famListItemName);
            String relationToCurrPerson = getRelationToPerson(currPerson, tempPerson);
            peopleFamDesc.setText(relationToCurrPerson);

            ImageView genderImage = peopleFamItemView.findViewById(R.id.listGenderImage);
            Drawable maleIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).colorRes(R.color.male_icon).sizeDp(40);
            Drawable femaleIcon = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).colorRes(R.color.female_icon).sizeDp(40);
            if (tempPerson.getGender().equals("m")) genderImage.setImageDrawable(maleIcon);
            else genderImage.setImageDrawable(femaleIcon);

            peopleFamItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // switch to Person Activity of selected Person
                    dataCache.setCurrPerson(tempPerson);
                    Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private String getRelationToPerson(Person currentPerson, Person relatedPerson) {
            if (currentPerson.getFather_id() != null) {
                if (currentPerson.getFather_id().equals(relatedPerson.getPerson_id())) {
                    return "Father";
                }
            }
            if (currentPerson.getMother_id() != null) {
                if (currentPerson.getMother_id().equals(relatedPerson.getPerson_id())) {
                    return "Mother";
                }
            }
            if (currentPerson.getSpouse_id() != null) {
                if (currentPerson.getSpouse_id().equals(relatedPerson.getPerson_id())) {
                    return "Spouse";
                }
            }
            if (relatedPerson.getMother_id() != null && relatedPerson.getFather_id() != null) {
                if (currentPerson.getPerson_id().equals(relatedPerson.getMother_id()) || currentPerson.getPerson_id().equals(relatedPerson.getFather_id())) {
                    return "Child";
                }
            }
            return "ERROR";
        }
    }
}
