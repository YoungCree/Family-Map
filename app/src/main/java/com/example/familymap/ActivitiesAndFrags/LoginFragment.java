package com.example.familymap.ActivitiesAndFrags;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.familymap.R;
import com.example.familymap.ServerAndCache.ServerProxy;

import FamServer.requests.AllEventsRequest;
import FamServer.requests.LoginRequest;
import FamServer.requests.PersonFamRequest;
import FamServer.requests.PersonRequest;
import FamServer.requests.RegisterRequest;
import FamServer.results.LoginResult;
import FamServer.results.PersonResult;
import FamServer.results.RegisterResult;

public class LoginFragment extends Fragment {
    public static final String ARG_TITLE = "title";
    private String title;
    private EditText serverHostEditText;
    private EditText serverPortEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private RadioGroup genderRadioGroup;
    private Button signInButton;
    private Button registerButton;

    private String serverHost;
    private String serverPort;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;

    private Boolean signIn1 = true;
    private Boolean signIn2 = true;
    private Boolean signIn3 = false;
    private Boolean signIn4 = false;

    private Boolean reg1 = false;
    private Boolean reg2 = false;
    private Boolean reg3 = false;
    private Boolean reg4 = false;

    private String authToken;
    private String personID;

    private Boolean loginSuccess = false;

    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        serverHostEditText = view.findViewById(R.id.serverHostEditText);
        serverPortEditText = view.findViewById(R.id.serverPortEditText);
        userNameEditText = view.findViewById(R.id.userNameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        firstNameEditText = view.findViewById(R.id.firstNameEditTExt);
        lastNameEditText = view.findViewById(R.id.lastNameEditTExt);
        emailEditText = view.findViewById(R.id.emailEditTExt);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        signInButton = view.findViewById(R.id.signInButton);
        registerButton = view.findViewById(R.id.registerButton);

        serverHostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                serverHost = serverHostEditText.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                serverHost = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (serverHost.length() == 0) signIn1 = false;
                else signIn1 = true;
                serverHost = s.toString();
                enableSignIn();
                enableRegister();
            }
        });

        serverPortEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                serverPort = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                serverPort = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (serverPort.length() == 0) signIn2 = false;
                else signIn2 = true;
                enableSignIn();
                enableRegister();
            }
        });

        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (userName.length() == 0) signIn3 = false;
                else signIn3 = true;
                enableSignIn();
                enableRegister();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password.length() == 0) signIn4 = false;
                else signIn4 = true;
                enableSignIn();
                enableRegister();
            }
        });

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (firstName.length() == 0) reg1 = false;
                else reg1 = true;
                enableRegister();
            }
        });

        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (lastName.length() == 0) reg2 = false;
                else reg2 = true;
                enableRegister();
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (email.length() == 0) reg3 = false;
                else reg3 = true;
                enableRegister();
            }
        });

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioFemale:
                        gender = "f";
                        reg4 = true;
                        enableRegister();
                        break;
                    case R.id.radioMale:
                        gender = "m";
                        reg4 = true;
                        enableRegister();
                        break;
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverHost == null) serverHost = serverHostEditText.getText().toString();
                if (serverPort == null) serverPort = serverPortEditText.getText().toString();
                LoginTask loginTask = new LoginTask();
                loginTask.execute();
                GetPersonTask getPersonTask = new GetPersonTask();
                getPersonTask.execute();
                GetDataTask getDataTask = new GetDataTask();
                getDataTask.execute();
                personID = null;
                authToken = null;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverHost == null) serverHost = serverHostEditText.getText().toString();
                if (serverPort == null) serverPort = serverPortEditText.getText().toString();
                RegisterTask registerTask = new RegisterTask();
                registerTask.execute();
                GetDataTask getDataTask = new GetDataTask();
                getDataTask.execute();
                personID = null;
                authToken = null;
            }
        });

        return view;
    }

    private class LoginTask extends AsyncTask<Void, Void, LoginResult> {
        @Override
        protected LoginResult doInBackground(Void... voids) {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(userName);
            loginRequest.setPassword(password);

            ServerProxy serverProxy = new ServerProxy();
            return serverProxy.login(serverHost, serverPort, loginRequest);
        }

        @Override
        protected void onPostExecute(LoginResult loginResult) {
            if (loginResult.isSuccess()) {
                authToken = loginResult.getAuthToken();
                personID = loginResult.getPerson_id();
                loginSuccess = true;
            }
            else {
                loginSuccess = false;
                authToken = null;
                personID = null;
                Toast.makeText(getContext(), "Error while logging in user", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetPersonTask extends AsyncTask<Void, Void, PersonResult> {
        @Override
        protected PersonResult doInBackground(Void... voids) {
            PersonRequest personRequest = new PersonRequest();
            personRequest.setAuthToken(authToken);
            personRequest.setPerson_id(personID);

            ServerProxy serverProxy = new ServerProxy();
            return  serverProxy.getPerson(serverHost, serverPort, personRequest);
        }

        @Override
        protected void onPostExecute(PersonResult personResult) {
            if (personResult.isSuccess()) {
                Toast.makeText(getContext(), "Welcome: " + personResult.getFirstName() + " " + personResult.getLastName(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Error while getting user info", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            PersonFamRequest personFamRequest = new PersonFamRequest();
            personFamRequest.setAuthToken(authToken);

            AllEventsRequest allEventsRequest = new AllEventsRequest();
            allEventsRequest.setAuthToken(authToken);

            ServerProxy serverProxy = new ServerProxy();
            Boolean success = serverProxy.getData(serverHost, serverPort, personFamRequest, allEventsRequest);
            return success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(getContext(), "Data successfully pulled from server", Toast.LENGTH_SHORT).show();
                mainActivity = (MainActivity) getContext();
                mainActivity.ShowMapFrag();
            }
            else {
                Toast.makeText(getContext(), "Error while pulling data from server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RegisterTask extends AsyncTask<Void, Void, RegisterResult> {
        @Override
        protected RegisterResult doInBackground(Void... voids) {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setFirstName(firstName);
            registerRequest.setLastName(lastName);
            registerRequest.setUserName(userName);
            registerRequest.setPassword(password);
            registerRequest.setEmail(email);
            registerRequest.setGender(gender);

            ServerProxy serverProxy = new ServerProxy();
            RegisterResult registerResult = serverProxy.registerUser(serverHost, serverPort, registerRequest);
            authToken = registerResult.getAuthToken();
            return registerResult;
        }

        @Override
        protected void onPostExecute(RegisterResult registerResult) {
            if (registerResult.isSuccess()) {
                Toast.makeText(getContext(), "Welcome: " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Error while registering user", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableSignIn() {
        if (signIn1 && signIn2 && signIn3 && signIn4) {
            signInButton.setEnabled(true);
        }
        else signInButton.setEnabled(false);
    }

    private void enableRegister() {
        if (signInButton.isEnabled() && reg1 && reg2 && reg3 && reg4) {
            registerButton.setEnabled(true);
        }
        else registerButton.setEnabled(false);
    }
}
