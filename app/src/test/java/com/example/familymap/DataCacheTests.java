package com.example.familymap;

import com.example.familymap.ServerAndCache.DataCache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import FamServer.model.Event;
import FamServer.model.Person;

import static org.junit.Assert.*;

public class DataCacheTests {
    private DataCache dataCache = DataCache.getInstance();
    private Person person;
    private Person person1;
    private Person person2;
    private Event event;
    private Event event1;
    private Event event2;
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Person> people = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        person = new Person("userTest1", "Gale", "Gale", "Johnson",
                "m", "father1", "mother1", "spouse1");
        person1 = new Person("userTest1", "Erika", "Erika", "Kohler",
                "f", "father2","mother2","spouse2");
        person2 = new Person("userTest1", "Corey", "Corey", "Kohler",
                "f", "father3","mother3","spouse3");
        event = new Event("birth", "Gale", "Gale",
                10.3f, 10.3f, "Japan", "Ushiku",
                "birth", 2016);
        event1 = new Event("death", "Gale", "Erika",
                10.3f, 10.3f, "Japan", "Ushiku",
                "death", 2016);
        event2 = new Event("marriage", "Gale", "Corey",
                10.3f, 10.3f, "Japan", "Ushiku",
                "marriage", 2016);
    }

    @After
    public void tearDown() throws Exception {
        people.clear();
        events.clear();
    }

    //Tests for calculating relationships...
    @Test
    public void calculateFamRelationsFatherPass() {
        person.setFather_id("father");
        person1.setPerson_id("father");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        boolean contains = false;
        dataCache.mapPeopleToFam();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        ArrayList<Person> personResult = peopleToFam.get(person);
        if (personResult.contains(person1)) contains = true;
        assertTrue(contains);
    }

    @Test
    public void calculateFamRelationsFatherFail() {
        person.setFather_id("father");
        person1.setPerson_id("wrong");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        dataCache.mapPeopleToFam();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        assertNull(peopleToFam.get(person));
    }

    @Test
    public void calculateFamRelationsMotherPass() {
        person.setMother_id("mother");
        person1.setPerson_id("mother");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        boolean contains = false;
        dataCache.mapPeopleToFam();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        ArrayList<Person> personResult = peopleToFam.get(person);
        if (personResult.contains(person1)) contains = true;
        assertTrue(contains);
    }

    @Test
    public void calculateFamRelationsMotherFail() {
        person.setMother_id("mother");
        person1.setPerson_id("wrong");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        dataCache.mapPeopleToFam();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        assertNull(peopleToFam.get(person));
    }

    @Test
    public void calculateFamRelationsSpousePass() {
        person.setSpouse_id("spouse");
        person1.setPerson_id("spouse");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        boolean contains = false;
        dataCache.mapPeopleToFam();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        ArrayList<Person> personResult = peopleToFam.get(person);
        if (personResult.contains(person1)) contains = true;
        assertTrue(contains);
    }

    @Test
    public void calculateFamRelationsSpouseFail() {
        person.setSpouse_id("spouse");
        person1.setPerson_id("wrong");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        dataCache.mapPeopleToFam();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        assertNull(peopleToFam.get(person));
    }

    @Test
    public void calculateFamRelationsChildPass() {
        person.setPerson_id("parent");
        person1.setFather_id("parent");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        boolean contains = false;
        dataCache.mapPeopleToFam();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        ArrayList<Person> personResult = peopleToFam.get(person);
        if (personResult.contains(person1)) contains = true;
        assertTrue(contains);
    }

    @Test
    public void calculateFamRelationsChildFail() {
        person.setPerson_id("parent");
        person1.setFather_id("wrong");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        dataCache.mapPeopleToFam();
        HashMap<Person, ArrayList<Person>> peopleToFam = dataCache.getPeopleToFam();
        assertNull(peopleToFam.get(person));
    }

    //Tests for filtering events
    @Test
    public void filterForMalesPass() {
        person.setGender("m");
        person1.setGender("f");
        person.setPerson_id("first");
        person1.setPerson_id("second");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        event.setPerson_id("first");
        event1.setPerson_id("second");
        events.add(event);
        events.add(event1);
        dataCache.setEvents(events);
        dataCache.mapPersonToEvent();
        dataCache.filterEventsToMale();
        ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();
        boolean itWorked = false;
        if (filteredEvents.get(0).getPerson_id().equals("first")) itWorked = true;
        if (filteredEvents.size() > 1) itWorked = false;
        assertTrue(itWorked);
        dataCache.getFilteredEvents().clear();
        dataCache.getEventToPerson().clear();
    }

    @Test
    public void filterForMalesFail() {
        person.setGender("f");
        person1.setGender("f");
        person.setPerson_id("first");
        person1.setPerson_id("second");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        event.setPerson_id("first");
        event1.setPerson_id("second");
        events.add(event);
        events.add(event1);
        dataCache.setEvents(events);
        dataCache.mapPersonToEvent();
        dataCache.filterEventsToMale();
        ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();
        assertTrue(filteredEvents.size() == 0);
        dataCache.getFilteredEvents().clear();
        dataCache.getEventToPerson().clear();
    }

    @Test
    public void filterForFemalesPass() {
        person.setGender("m");
        person1.setGender("f");
        person.setPerson_id("first");
        person1.setPerson_id("second");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        event.setPerson_id("first");
        event1.setPerson_id("second");
        events.add(event);
        events.add(event1);
        dataCache.setEvents(events);
        dataCache.mapPersonToEvent();
        dataCache.filterEventsToFemale();
        ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();
        boolean itWorked = false;
        if (filteredEvents.get(0).getPerson_id().equals("second")) itWorked = true;
        if (filteredEvents.size() > 1) itWorked = false;
        assertTrue(itWorked);
        dataCache.getFilteredEvents().clear();
        dataCache.getEventToPerson().clear();
    }

    @Test
    public void filterForFemalesFail() {
        person.setGender("m");
        person1.setGender("m");
        person.setPerson_id("first");
        person1.setPerson_id("second");
        people.add(person);
        people.add(person1);
        dataCache.setPeople(people);
        event.setPerson_id("first");
        event1.setPerson_id("second");
        events.add(event);
        events.add(event1);
        dataCache.setEvents(events);
        dataCache.mapPersonToEvent();
        dataCache.filterEventsToFemale();
        ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();
        assertTrue(filteredEvents.size() == 0);
        dataCache.getFilteredEvents().clear();
        dataCache.getEventToPerson().clear();
    }

    @Test
    public void filterForFatherSidePass() {
        person.setPerson_id("root");
        dataCache.setRootUserPersonID("root");
        person.setFather_id("father");
        person1.setPerson_id("father");
        person1.setFather_id("gpa");
        person2.setPerson_id("gpa");
        people.add(person);
        people.add(person1);
        people.add(person2);
        person1.setGender("m");
        person2.setGender("m");
        dataCache.setPeople(people);
        event.setPerson_id("root");
        event1.setPerson_id("father");
        event2.setPerson_id("gpa");
        events.add(event);
        events.add(event1);
        events.add(event2);
        dataCache.setEvents(events);
        dataCache.setMaleEventChecked(true);
        dataCache.mapPersonToEvent();
        dataCache.mapPeopleToFam();
        dataCache.mapEventToPerson();
        dataCache.filterEventsToFather();
        ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();
        assertTrue(filteredEvents.contains(event) && filteredEvents.contains(event1) &&
                filteredEvents.contains(event2));
        dataCache.getFilteredEvents().clear();
        dataCache.getEventToPerson().clear();
        dataCache.getPeopleToFam().clear();
        dataCache.setMaleEventChecked(false);
    }

    @Test
    public void filterForFatherSideFail() {
        person.setPerson_id("root");
        dataCache.setRootUserPersonID("root");
        person.setFather_id("father");
        person1.setPerson_id("father");
        person1.setFather_id("gpa");
        person2.setPerson_id("papi");
        people.add(person);
        people.add(person1);
        people.add(person2);
        person1.setGender("m");
        person2.setGender("m");
        dataCache.setPeople(people);
        event.setPerson_id("root");
        event1.setPerson_id("father");
        event2.setPerson_id("papi");
        events.add(event);
        events.add(event1);
        events.add(event2);
        dataCache.setEvents(events);
        dataCache.setMaleEventChecked(true);
        dataCache.mapPersonToEvent();
        dataCache.mapPeopleToFam();
        dataCache.mapEventToPerson();
        dataCache.filterEventsToFather();
        ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();
        assertFalse(filteredEvents.contains(event2));
        dataCache.getFilteredEvents().clear();
        dataCache.getEventToPerson().clear();
        dataCache.getPeopleToFam().clear();
        dataCache.setMaleEventChecked(false);
    }

    @Test
    public void filterForMotherSidePass() {
        person.setPerson_id("root");
        dataCache.setRootUserPersonID("root");
        person.setMother_id("father");
        person1.setPerson_id("father");
        person1.setMother_id("gpa");
        person2.setPerson_id("gpa");
        people.add(person);
        people.add(person1);
        people.add(person2);
        person1.setGender("f");
        person2.setGender("f");
        dataCache.setPeople(people);
        event.setPerson_id("root");
        event1.setPerson_id("father");
        event2.setPerson_id("gpa");
        events.add(event);
        events.add(event1);
        events.add(event2);
        dataCache.setEvents(events);
        dataCache.setFemaleEventChecked(true);
        dataCache.setMaleEventChecked(true);
        dataCache.mapPersonToEvent();
        dataCache.mapPeopleToFam();
        dataCache.mapEventToPerson();
        dataCache.filterEventsToMother();
        ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();
        assertTrue(filteredEvents.contains(event) && filteredEvents.contains(event1) &&
                filteredEvents.contains(event2));
        dataCache.getFilteredEvents().clear();
        dataCache.getEventToPerson().clear();
        dataCache.getPeopleToFam().clear();
        dataCache.setFemaleEventChecked(false);
        dataCache.setMaleEventChecked(false);
    }

    @Test
    public void filterForMotherSideFail() {
        person.setPerson_id("root");
        dataCache.setRootUserPersonID("root");
        person.setMother_id("father");
        person1.setPerson_id("father");
        person1.setMother_id("gpa");
        person2.setPerson_id("papi");
        people.add(person);
        people.add(person1);
        people.add(person2);
        person1.setGender("f");
        person2.setGender("f");
        dataCache.setPeople(people);
        event.setPerson_id("root");
        event1.setPerson_id("father");
        event2.setPerson_id("papi");
        events.add(event);
        events.add(event1);
        events.add(event2);
        dataCache.setEvents(events);
        dataCache.setFemaleEventChecked(true);
        dataCache.setMaleEventChecked(true);
        dataCache.mapPersonToEvent();
        dataCache.mapPeopleToFam();
        dataCache.mapEventToPerson();
        dataCache.filterEventsToMother();
        ArrayList<Event> filteredEvents = dataCache.getFilteredEvents();
        assertFalse(filteredEvents.contains(event2));
        dataCache.getFilteredEvents().clear();
        dataCache.getEventToPerson().clear();
        dataCache.getPeopleToFam().clear();
        dataCache.setFemaleEventChecked(false);
        dataCache.setMaleEventChecked(false);
    }

    //Chronologically sort events
    @Test
    public void chronSortPass() {
        person.setPerson_id("this");
        event.setPerson_id("this");
        event1.setPerson_id("this");
        event2.setPerson_id("this");
        event.setEvent_year(2000);
        event1.setEvent_year(1900);
        event2.setEvent_year(2020);
        people.add(person);
        events.add(event);
        events.add(event1);
        events.add(event2);
        dataCache.setPeople(people);
        dataCache.setEvents(events);
        dataCache.mapEventToPerson();
        HashMap<Person, ArrayList<Event>> eventsToPeople = dataCache.getEventsToPeople();
        ArrayList<Event> eventsFound = eventsToPeople.get(person);
        assertTrue(eventsFound.get(0).getEvent_year() < eventsFound.get(1).getEvent_year() &&
                eventsFound.get(1).getEvent_year() < eventsFound.get(2).getEvent_year());
        dataCache.getEventsToPeople().clear();
    }

    @Test
    public void chronSortFail() {
        person.setPerson_id("this");
        event.setPerson_id("this");
        event1.setPerson_id("this");
        event2.setPerson_id("this");
        event.setEvent_year(2000);
        event1.setEvent_year(1900);
        event2.setEvent_year(2020);
        people.add(person);
        events.add(event);
        events.add(event1);
        events.add(event2);
        dataCache.setPeople(people);
        dataCache.setEvents(events);
        dataCache.mapEventToPerson();
        HashMap<Person, ArrayList<Event>> eventsToPeople = dataCache.getEventsToPeople();
        ArrayList<Event> eventsFound = eventsToPeople.get(person);
        assertFalse(eventsFound.get(1).getEvent_year() < eventsFound.get(0).getEvent_year() &&
                eventsFound.get(2).getEvent_year() < eventsFound.get(1).getEvent_year());
        dataCache.getEventsToPeople().clear();
    }

    //Finding people and events
    @Test
    public void findPersonPass() {
        person.setPerson_id("findMe");
        people.add(person);
        dataCache.setPeople(people);
        assertEquals(dataCache.findPerson("findMe"), person);
    }

    @Test
    public void findPersonFail() {
        person.setPerson_id("findMe");
        people.add(person);
        dataCache.setPeople(people);
        assertNull(dataCache.findPerson("wrong"));
    }

    @Test
    public void findEventPass() {
        event.setEvent_id("findMe");
        events.add(event);
        dataCache.setEvents(events);
        assertEquals(dataCache.findEvent("findMe"), event);
    }

    @Test
    public void findEventFail() {
        event.setEvent_id("findMe");
        events.add(event);
        dataCache.setEvents(events);
        assertNull(dataCache.findEvent("wrong"));
    }
}
