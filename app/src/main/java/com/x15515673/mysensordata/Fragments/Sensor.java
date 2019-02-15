package com.x15515673.mysensordata.Fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.x15515673.mysensordata.R;

import org.w3c.dom.Text;

import java.util.Map;

import static com.x15515673.mysensordata.R.*;
/**
 * Author : Jordan May
 * Student Number : x15515673
 * IoT Application Development
 */
/*
    Class to construct our Sensor main view fragment!
 */
public class Sensor extends Fragment
{
    /*
        Variable objects representing each text view
     */
    private TextView temperatureTextField;
    private TextView humidityTextField;
    private TextView tempHumState;

    private TextView soundSensorTextField;
    private TextView soundSensorState;

    private TextView lightLEDState;

    static final String TAG = "class";

    /*
       Firebase Setup!
    */
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    /*
        Paths
    */
    DatabaseReference rootReference;
    DatabaseReference configuration;
    DatabaseReference soundRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        return inflater.inflate(layout.sensors_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        /*
            Assign each object to the XML reference
         */
        temperatureTextField = getActivity().findViewById(id.tempHum);
        humidityTextField = getActivity().findViewById(id.humidityTextField);
        tempHumState = getActivity().findViewById(R.id.temphumstate);

        soundSensorTextField = getActivity().findViewById(id.soundRate);
        soundSensorState = getActivity().findViewById(id.soundState);

        lightLEDState = getActivity().findViewById(id.ledState);


        /*
            Here we are attaching several listeners to our specified firebase nodes
            and populating, in real time, the textview's from our Real time database.

            Each event listener watches for a change in data at the specified node, if there is then
            it grabs a snapshot of the data. Firebase operates a key value configuration so a simple
            map that stores string, object pairs stores this datasnapshot in which can be easily queried.

         *  */
        configuration = FirebaseDatabase.getInstance().getReference();
        /* Temperature + Humidity */
        configuration.child("Configuration").child("TemperatureSensor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        String tempState = String.valueOf(map.get("Temperature State"));
                        /*
                            So if the database says this sensor is on, then set our text to ON
                            and color this text to a GREEN color
                         */
                        if (tempState.equals("ON")){
                            tempHumState.setText("ON");
                            tempHumState.setTextColor(getResources().getColor(color.colorPrimary));

                        }else{
                            /*
                                Else, set the Text to OFF! and set the color to RED
                             */
                            tempHumState.setText("OFF");
                            tempHumState.setTextColor(getResources().getColor(color.colorAccent));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        /* Sound */
        configuration.child("Configuration").child("SoundSensor")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                String soundSensor = String.valueOf(map.get("Sound State"));
                /*
                    So if the database says this sensor is on, then set our text to ON
                    and color this text to a GREEN color
                */
                if (soundSensor.equals("ON")){
                    soundSensorState.setText("ON");
                    soundSensorState.setTextColor(getResources().getColor(color.colorPrimary));
                }else{
                    /*
                      Else, set the Text to OFF! and set the color to RED
                    */
                    soundSensorState.setText("OFF");
                    soundSensorState.setTextColor(getResources().getColor(color.colorAccent));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* LED Bulb*/
        configuration.child("Configuration").child("LED")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        String ledState = String.valueOf(map.get("LED State"));
                        /*
                            So if the database says this sensor is on, then set our text to ON
                            and color this text to a GREEN color
                        */
                        if (ledState.equals("ON")){
                            lightLEDState.setText("ON");
                            lightLEDState.setTextColor(getResources().getColor(color.colorPrimary));
                        }else{
                            /*
                                Else, set the Text to OFF! and set the color to RED
                            */
                            lightLEDState.setText("OFF");
                            lightLEDState.setTextColor(getResources().getColor(color.colorAccent));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        /**
         * These listeners sets the textviews to the current sensor data. They have a different type
         * of event listener as we need to get all current values for the specified node!
         */
        rootReference = database.getReference().child("Sensors").child("TemperatureSensor");
        rootReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                String temperatureValues = String.valueOf(map.get("Temperature"));
                String humidityValues = String.valueOf(map.get("Humidity"));
                temperatureTextField.setText(temperatureValues);
                humidityTextField.setText(humidityValues);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        soundRef = database.getReference().child("Sensors").child("SoundSensor");
        soundRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                String soundSensor = String.valueOf(map.get("Sound"));
                soundSensorTextField.setText(soundSensor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
