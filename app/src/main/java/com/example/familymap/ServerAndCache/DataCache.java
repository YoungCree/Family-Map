package com.example.familymap.ServerAndCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import FamServer.model.Event;
import FamServer.model.Person;

public class DataCache {

    private static DataCache instance;
    private String rootUserPersonID;
    private ArrayList<Event> events;
    private ArrayList<Event> filteredEvents = new ArrayList<>();
    private ArrayList<Person> people;
    private HashMap<Person, ArrayList<Event>> eventsToPeople = new HashMap<>();
    private HashMap<Event, Person> eventToPerson = new HashMap<>();
    private HashMap<String, ArrayList<Event>> eventsToType = new HashMap<>();
    private HashMap<String, Integer> eventToColor = new HashMap<>();
    private HashMap<Person, ArrayList<Person>> peopleToFam = new HashMap<>();
    private int numColors = 0;
    private int helpColor = 0;
    private boolean lifeStoryChecked = false;
    private boolean famTreeChecked = false;
    private boolean spouseLinesChecked = false;
    private boolean fatherSideChecked = false;
    private boolean motherSideChecked = false;
    private boolean maleEventChecked = false;
    private boolean femaleEventChecked = false;

    private boolean firstTimeMap = true;
    private boolean firstTimeSettings = true;
    private boolean isLogout = false;

    private Person currPerson;
    private Event currEvent;

    public static DataCache getInstance() {
        if(instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    private DataCache() {

    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void mapEventToPerson() {
        for (Person i : people) {
            for (Event j : events) {
                if (i.getPerson_id().equals(j.getPerson_id())) {
                    if (!eventsToPeople.containsKey(i)) {
                        ArrayList<Event> temp = new ArrayList<>();
                        temp.add(j);
                        eventsToPeople.put(i, temp);
                    }
                    else eventsToPeople.get(i).add(j);
                }
            }
            //Sort the events from earliest to latest
            ArrayList<Event> eventsToSort = eventsToPeople.get(i);
            Collections.sort(eventsToSort, new Comparator<Event>() {
                @Override
                public int compare(Event o1, Event o2) {
                    if (o1.getEvent_year() < o2.getEvent_year()) return -1;
                    if (o2.getEvent_year() > o1.getEvent_year()) return +1;
                    else return 0;
                }
            });
        }
    }

    public void mapPeopleToFam() {
        for (Person i : people) {
            for (Person j : people) {
                if (i.getFather_id() != null) {
                    if (i.getFather_id().equals(j.getPerson_id())) {
                        if (!peopleToFam.containsKey(i)) {
                            ArrayList<Person> temp = new ArrayList<>();
                            temp.add(j);
                            peopleToFam.put(i, temp);
                        } else peopleToFam.get(i).add(j);
                    }
                }
                if (i.getMother_id() != null) {
                    if (i.getMother_id().equals(j.getPerson_id())) {
                        if (!peopleToFam.containsKey(i)) {
                            ArrayList<Person> temp = new ArrayList<>();
                            temp.add(j);
                            peopleToFam.put(i, temp);
                        } else peopleToFam.get(i).add(j);
                    }
                }
                if (i.getSpouse_id() != null) {
                    if (i.getSpouse_id().equals(j.getPerson_id())) {
                        if (!peopleToFam.containsKey(i)) {
                            ArrayList<Person> temp = new ArrayList<>();
                            temp.add(j);
                            peopleToFam.put(i, temp);
                        } else peopleToFam.get(i).add(j);
                    }
                }
                if (j.getMother_id() != null) {
                    if (i.getPerson_id().equals(j.getFather_id()) || i.getPerson_id().equals(j.getMother_id())) {
                        if (!peopleToFam.containsKey(i)) {
                            ArrayList<Person> temp = new ArrayList<>();
                            temp.add(j);
                            peopleToFam.put(i, temp);
                        } else peopleToFam.get(i).add(j);
                    }
                }
            }
        }
    }

    public void mapEventToEventType() {
        for (Event i : events) {
            if (!eventsToType.containsKey(i.getEvent_type())) {
                ArrayList<Event> temp = new ArrayList<>();
                temp.add(i);
                eventsToType.put(i.getEvent_type(), temp);
                numColors++;
            }
            else eventsToType.get(i.getEvent_type()).add(i);
        }
    }

    public void mapEventToColor() {
        for (Event i : events) {
            if (!eventToColor.containsKey(i.getEvent_type().toLowerCase())) {
                if (helpColor >= 360) helpColor = 25;
                eventToColor.put(i.getEvent_type().toLowerCase(), helpColor);
                helpColor += 45;
            }
        }
        helpColor = 0;
    }

    public void mapPersonToEvent() {
        for (Event i : events) {
            for (Person j : people) {
                if (i.getPerson_id().equals(j.getPerson_id())) {
                    eventToPerson.put(i, j);
                }
            }
        }
    }

    public void filterEventsToMale() {
        for (Event i : events) {
            Person tempPers = eventToPerson.get(i);
            if (tempPers.getGender().equals("m")) filteredEvents.add(i);
        }
    }

    public void clearMaleFilter() {
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        for (Event i : filteredEvents) {
            Person tempPers = eventToPerson.get(i);
            if (tempPers.getGender().equals("m")) {
                eventsToRemove.add(i);
            }
        }
        filteredEvents.removeAll(eventsToRemove);
    }

    public void filterEventsToFemale() {
        for (Event i : events) {
            Person tempPers = eventToPerson.get(i);
            if (tempPers.getGender().equals("f")) filteredEvents.add(i);
        }
    }

    public void clearFemailFilter() {
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        for (Event i : filteredEvents) {
            Person tempPers = eventToPerson.get(i);
            if (tempPers.getGender().equals("f")) {
                eventsToRemove.add(i);
            }
        }
        filteredEvents.removeAll(eventsToRemove);
    }

    public void filterEventsToFather() {
        Person rootUser = findPerson(rootUserPersonID);
        ArrayList<Person> famOfPerson = peopleToFam.get(rootUser);
        ArrayList<Event> eventsOfPerson = eventsToPeople.get(rootUser);
        filteredEvents.addAll(eventsOfPerson);
        Person father = findDad(rootUser, famOfPerson);
        ArrayList<Person> famOfFather = peopleToFam.get(father);
        ArrayList<Event> fatherEvents = eventsToPeople.get(father);
        filteredEvents.addAll(fatherEvents);
        findGenerations(father, famOfFather);
        if (!isMaleEventChecked()) clearMaleFilter();
        if (!isFemaleEventChecked()) clearFemailFilter();
    }

    public void filterEventsToMother() {
        Person rootUser = findPerson(rootUserPersonID);
        ArrayList<Person> famOfPerson = peopleToFam.get(rootUser);
        ArrayList<Event> eventsOfPerson = eventsToPeople.get(rootUser);
        filteredEvents.addAll(eventsOfPerson);
        Person mother = findMom(rootUser, famOfPerson);
        ArrayList<Person> famOfMother = peopleToFam.get(mother);
        ArrayList<Event> motherEvents = eventsToPeople.get(mother);
        filteredEvents.addAll(motherEvents);
        findGenerations(mother, famOfMother);
        if (!isMaleEventChecked()) clearMaleFilter();
        if (!isFemaleEventChecked()) clearFemailFilter();
    }

    public void removeFatherEvents() {
        Person rootUser = findPerson(rootUserPersonID);
        ArrayList<Person> famOfPerson = peopleToFam.get(rootUser);
        Person father = findDad(rootUser, famOfPerson);
        ArrayList<Person> famOfFather = peopleToFam.get(father);
        ArrayList<Event> fatherEvents = eventsToPeople.get(father);
        filteredEvents.removeAll(fatherEvents);
        removeGenerations(father, famOfFather);
    }

    public void removeMotherEvents() {
        Person rootUser = findPerson(rootUserPersonID);
        ArrayList<Person> famOfPerson = peopleToFam.get(rootUser);
        Person mother = findMom(rootUser, famOfPerson);
        ArrayList<Person> famOfMother = peopleToFam.get(mother);
        ArrayList<Event> motherEvents = eventsToPeople.get(mother);
        filteredEvents.removeAll(motherEvents);
        removeGenerations(mother, famOfMother);
    }

    public void findGenerations(Person person, ArrayList<Person> famOfPerson) {
        if (findDad(person, famOfPerson) != null) {
            Person father = findDad(person, famOfPerson);
            ArrayList<Person> relativesDad = peopleToFam.get(father);
            ArrayList<Event> fatherEvents = eventsToPeople.get(father);
            filteredEvents.addAll(fatherEvents);
            findGenerations(father, relativesDad);
        }
        if (findMom(person, famOfPerson) != null) {
            Person mother = findMom(person, famOfPerson);
            ArrayList<Person> relativesMom = peopleToFam.get(mother);
            ArrayList<Event> motherEvents = eventsToPeople.get(mother);
            filteredEvents.addAll(motherEvents);
            findGenerations(mother, relativesMom);
        }
    }

    public void removeGenerations(Person person, ArrayList<Person> famOfPerson) {
        if (findDad(person, famOfPerson) != null) {
            Person father = findDad(person, famOfPerson);
            ArrayList<Person> relativesDad = peopleToFam.get(father);
            ArrayList<Event> fatherEvents = eventsToPeople.get(father);
            filteredEvents.removeAll(fatherEvents);
            removeGenerations(father, relativesDad);
        }
        if (findMom(person, famOfPerson) != null) {
            Person mother = findMom(person, famOfPerson);
            ArrayList<Person> relativesMom = peopleToFam.get(mother);
            ArrayList<Event> motherEvents = eventsToPeople.get(mother);
            filteredEvents.removeAll(motherEvents);
            removeGenerations(mother, relativesMom);
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

    public HashMap<Person, ArrayList<Event>> getEventsToPeople() {
        return eventsToPeople;
    }

    public HashMap<Person, ArrayList<Person>> getPeopleToFam() {
        return peopleToFam;
    }

    public HashMap<String, ArrayList<Event>> getEventsToType() {
        return eventsToType;
    }

    public HashMap<Event, Person> getEventToPerson() {
        return eventToPerson;
    }

    public int getNumColors() {
        return numColors;
    }

    public HashMap<String, Integer> getEventToColor() {
        return eventToColor;
    }

    public boolean isLifeStoryChecked() {
        return lifeStoryChecked;
    }

    public void setLifeStoryChecked(boolean lifeStoryChecked) {
        this.lifeStoryChecked = lifeStoryChecked;
    }

    public boolean isFamTreeChecked() {
        return famTreeChecked;
    }

    public void setFamTreeChecked(boolean famTreeChecked) {
        this.famTreeChecked = famTreeChecked;
    }

    public boolean isSpouseLinesChecked() {
        return spouseLinesChecked;
    }

    public void setSpouseLinesChecked(boolean spouseLinesChecked) {
        this.spouseLinesChecked = spouseLinesChecked;
    }

    public boolean isFatherSideChecked() {
        return fatherSideChecked;
    }

    public void setFatherSideChecked(boolean fatherSideChecked) {
        this.fatherSideChecked = fatherSideChecked;
    }

    public boolean isMotherSideChecked() {
        return motherSideChecked;
    }

    public void setMotherSideChecked(boolean motherSideChecked) {
        this.motherSideChecked = motherSideChecked;
    }

    public boolean isMaleEventChecked() {
        return maleEventChecked;
    }

    public void setMaleEventChecked(boolean maleEventChecked) {
        this.maleEventChecked = maleEventChecked;
    }

    public boolean isFemaleEventChecked() {
        return femaleEventChecked;
    }

    public void setFemaleEventChecked(boolean femaleEventChecked) {
        this.femaleEventChecked = femaleEventChecked;
    }

    public void setLogout(boolean logout) {
        isLogout = logout;
    }

    public boolean isLogout() {
        return isLogout;
    }

    public Person getCurrPerson() {
        return currPerson;
    }

    public void setCurrPerson(Person currPerson) {
        this.currPerson = currPerson;
    }

    public Event getCurrEvent() {
        return currEvent;
    }

    public void setCurrEvent(Event currEvent) {
        this.currEvent = currEvent;
    }

    public ArrayList<Event> getFilteredEvents() {
        return filteredEvents;
    }

    public void setFilteredEvents(ArrayList<Event> filteredEvents) {
        this.filteredEvents = filteredEvents;
    }

    public String getRootUserPersonID() {
        return rootUserPersonID;
    }

    public void setRootUserPersonID(String rootUserPersonID) {
        this.rootUserPersonID = rootUserPersonID;
    }

    public Person findPerson(String personID) {
        for (Person i: people) {
            if (i.getPerson_id().equals(personID)) return i;
        }
        return null;
    }

    public Event findEvent(String eventID) {
        for (Event i : events) {
            if (i.getEvent_id().equals(eventID)) return i;
        }
        return null;
    }

    public boolean isFirstTimeMap() {
        return firstTimeMap;
    }

    public void setFirstTimeMap(boolean firstTimeMap) {
        this.firstTimeMap = firstTimeMap;
    }

    public boolean isFirstTimeSettings() {
        return firstTimeSettings;
    }

    public void setFirstTimeSettings(boolean firstTimeSettings) {
        this.firstTimeSettings = firstTimeSettings;
    }
}
