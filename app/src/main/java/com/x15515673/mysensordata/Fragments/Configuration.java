package com.x15515673.mysensordata.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.x15515673.mysensordata.R;

import java.util.Map;

/**
 * Author : Jordan May
 * Student Number : x15515673
 * IoT Application Development
 */

/*
    This class is for our configuraton fragment in order to get an
    representation of our Fragment View. we use the oncreareview
    method to load in our XML layout resource file.
 */

public class Configuration extends Fragment {

    /*
        We delcare our Firebase instances in order to get a reference to our database.
    * */
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

    /*
        Variables for our Buttons and Fields!
     */
    Switch temperatureSwitch;
    EditText temperatureRate;
    Button temperatureRateSubmit;

    Switch soundSwitch;
    EditText soundRate;
    Button soundSubmit;

    Switch ledLight;

    /*
        Declare static strings for switch states.
     */
    static final String switchStateON = "ON";
    static final String switchStateOFF = "OFF";
    static final String TAG = "class";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.configuration_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        /* Setters of variables*/
        temperatureSwitch = getActivity().findViewById(R.id.switch1);
        temperatureRate = getActivity().findViewById(R.id.tempRate);
        temperatureRateSubmit = getActivity().findViewById(R.id.temperatureSubmitRate);

        soundSwitch = getActivity().findViewById(R.id.SoundStateSwitch);
        soundRate = getActivity().findViewById(R.id.tempRate2);
        soundSubmit = getActivity().findViewById(R.id.soundSubmitRate);

        ledLight = getActivity().findViewById(R.id.soundStateSwitch);


        /*
            Button listener for our Temperature Submit button!
            When the user presses submit for our Temperature Values, check the entered
            value is an integer and not a char or invalid reference. The Sound submit button is also declared.
        * */
        temperatureRateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rate = temperatureRate.getText().toString();
                rateChecker(rate,"Configuration", "TemperatureSensor","Temperature Rate");
            }
        });

        soundSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rate = soundRate.getText().toString();
                rateChecker(rate,"Configuration", "SoundSensor", "Sound Rate");
            }
        });



        /*
            Here we have three single value listeners in order to preemptively
            trigger the switch states based on what is in the database. This
            ensures that the user knows what the state the sensor is on the minute
            they go to this Fragment!
        * */
        dbReference.child("Configuration").child("TemperatureSensor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        String tempState = String.valueOf(map.get("Temperature State"));
                        if (tempState.equals("ON")){
                            temperatureSwitch.setChecked(true);
                        }else{
                            temperatureSwitch.setChecked(false);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        dbReference.child("Configuration").child("SoundSensor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        String soundState = String.valueOf(map.get("Sound State"));
                        if (soundState.equals("ON")){
                            soundSwitch.setChecked(true);
                        }else{
                            soundSwitch.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        dbReference.child("Configuration").child("LED")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        String ledstate = String.valueOf(map.get("LED State"));
                        if (ledstate.equals("ON")){
                            ledLight.setChecked(true);
                        }else{
                            ledLight.setChecked(false);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        /*
            These listeners are attached to the switches. When the user switches the button
            the code makes a call to Firebase specified at a certain node and changes the value
            within the database.
         *  */

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    /* if the button is checked */
                    dbReference.child("Configuration").child("SoundSensor").child("Sound State")
                            .setValue(switchStateON)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                                                .setTitle("Sound Sensor!")
                                                .setMessage("Your Sound Sensor is now ON!")
                                                .sneakSuccess();
                                    } else {
                                        Toast.makeText(getActivity(), "HMM", Toast.LENGTH_LONG).show();


                                    }
                                }
                            });
                }else{
                    dbReference.child("Configuration").child("SoundSensor").child("Sound State")
                            .setValue(switchStateOFF)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        //Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                                                .setTitle("Sound Sensor")
                                                .setMessage("Sound Sensor is now OFF")
                                                .sneakError();

                                    } else {
                                        Toast.makeText(getActivity(), "HMM", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                }
            }
        });

        temperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /*
                        If the user switches to TRUE then turn the reading for Temperature sensor on!
                     */
                    dbReference.child("Configuration").child("TemperatureSensor").child("Temperature State")
                            .setValue(switchStateON)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                                                .setTitle("Temperature Sensor!")
                                                .setMessage("Your Temperature Sensor is now ON!")
                                                .sneakSuccess();
                                    } else {
                                        Toast.makeText(getActivity(), "HMM", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                } else {
                    /**
                     * If the user switches to FALSE then the turn the reading off for the Temp Sensor!
                     */
                    dbReference.child("Configuration").child("TemperatureSensor").child("Temperature State")
                            .setValue(switchStateOFF)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                                                .setTitle("Temperature Sensor")
                                                .setMessage("Temperature Sensor is now OFF")
                                                .sneakError();
                                    } else {
                                        Toast.makeText(getActivity(), "HMM", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                }
            }
        });

        ledLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    dbReference.child("Configuration").child("LED").child("LED State")
                            .setValue(switchStateON)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                                                .setTitle("LED Sensor!")
                                                .setMessage("Your LED Sensor is now ON!")
                                                .sneakSuccess();
                                    } else {
                                        Toast.makeText(getActivity(), "HMM", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                }else{
                    dbReference.child("Configuration").child("LED").child("LED State")
                            .setValue(switchStateOFF)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                                                .setTitle("LED Sensor")
                                                .setMessage("LED Sensor is now OFF")
                                                .sneakError();
                                    } else {
                                        Toast.makeText(getActivity(), "HMM", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                }
            }
        });
    }

    /*
     *   This public method checks that our rate number that the user entered is valid. i.e not a
     *   string or negative value.
     * */
    public void rateChecker(String rateNumber, String dbLevel1, final String dbLevel2, String dbLevel3)
    {
        if (rateNumber.matches(""))
        {
            /* if the piece of text is blank then Toast*/
            Sneaker.with(getActivity())
                    .setTitle("Warning!!")
                    .setMessage("Rate Must not be blank")
                    .sneakWarning();
        }else{
            /* if the user did enter something, check it is an integer*/
            try{

                int rate = Integer.parseInt(rateNumber);
                if (rate <= 0)
                {
                    Sneaker.with(getActivity())
                            .setTitle("Warning!!")
                            .setMessage("Rate Must not be just 0 or less then 0")
                            .sneakWarning();
                }else{
                    /* the number is valid, push to Firebase Rate node! */
                    dbReference.child(dbLevel1).child(dbLevel2).child(dbLevel3)
                            .setValue(rate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                                                .setTitle("Submitted")
                                                .setMessage("Your " + dbLevel2 +" is now set at this rate!")
                                                .sneakSuccess();
                                    } else {
                                        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                                                .setTitle("Warning!!")
                                                .setMessage("Oh No... Something went wrong!")
                                                .sneakWarning();
                                    }
                                }
                            });
                }

            }catch (NumberFormatException nException){
                /* if the user did not enter number java will throw a number format exception */
                Sneaker.with(getActivity())
                        .setTitle("Warning!!")
                        .setMessage("Rate must be a number!")
                        .sneakWarning();
            }

        }

    }
}
