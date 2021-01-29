package com.example.familymap.ServerAndCache;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import FamServer.model.Event;
import FamServer.requests.AllEventsRequest;
import FamServer.requests.LoginRequest;
import FamServer.requests.PersonFamRequest;
import FamServer.requests.PersonRequest;
import FamServer.requests.RegisterRequest;
import FamServer.results.AllEventsResult;
import FamServer.results.LoginResult;
import FamServer.results.PersonFamResult;
import FamServer.results.PersonResult;
import FamServer.results.RegisterResult;

public class ServerProxy {

    public LoginResult login(String address, String port, LoginRequest loginRequest) {
        try {
            URL url = new URL("http://" + address + ":" + port + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(loginRequest);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                LoginResult loginResult = gson.fromJson(respData, LoginResult.class);
                DataCache dataCache = DataCache.getInstance();
                dataCache.setRootUserPersonID(loginResult.getPerson_id());
                return loginResult;
            }
            else {
                return new LoginResult(false, "HTTP_NOT_OK");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LoginResult(false, "Something odd happened");
    }

    public PersonResult getPerson(String address, String port, PersonRequest personRequest) {
        try {
            URL url = new URL("http://" + address + ":" + port + "/person/" + personRequest.getPerson_id());
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", personRequest.getAuthToken());
            http.connect();

            Gson gson = new Gson();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                return gson.fromJson(respData, PersonResult.class);
            }
            else {
                return new PersonResult(false, "HTTP_NOT_OK");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PersonResult(false, "Something odd happened");
    }

    public RegisterResult registerUser(String address, String port, RegisterRequest registerRequest) {
        try {
            URL url = new URL("http://" + address + ":" + port + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(registerRequest);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                RegisterResult registerResult = gson.fromJson(respData, RegisterResult.class);
                DataCache dataCache = DataCache.getInstance();
                dataCache.setRootUserPersonID(registerResult.getPerson_id());
                return registerResult;
            }
            else {
                return new RegisterResult(false, "HTTP_NOT_OK");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RegisterResult(false, "Something odd happened");
    }

    public Boolean getData(String address, String port, PersonFamRequest personFamRequest, AllEventsRequest allEventsRequest) {
        DataCache dataCache = DataCache.getInstance();
        try {
            URL url = new URL("http://" + address + ":" + port + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", allEventsRequest.getAuthToken());
            http.connect();

            Gson gson = new Gson();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                AllEventsResult allEventsResult = gson.fromJson(respData, AllEventsResult.class);
                dataCache.setEvents(allEventsResult.getEvents());
                dataCache.setFilteredEvents((ArrayList<Event>) dataCache.getEvents().clone());
            }
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            URL url = new URL("http://" + address + ":" + port + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", personFamRequest.getAuthToken());
            http.connect();

            Gson gson = new Gson();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                PersonFamResult personFamResult = gson.fromJson(respData, PersonFamResult.class);
                dataCache.setPeople(personFamResult.getPersons());
                dataCache.mapEventToPerson();
                dataCache.mapEventToEventType();
                dataCache.mapEventToColor();
                dataCache.mapPeopleToFam();
                dataCache.mapPersonToEvent();
                return true;
            }
            else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
