package com.x15515673.mysensordata;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.x15515673.mysensordata.Fragments.Configuration;
import com.x15515673.mysensordata.Fragments.Sensor;

import java.util.Map;

/**
 * Author : Jordan May
 * Student Number : x15515673
 * IoT Application Development
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{
    /**
     * This application utilises fragments to switch the views. The advantage of fragments is
     * that the UI of the application appears to be more seamless. Furthermore, it saves memory within the application
     * and thus it is more performant then using Activities
     */
    static final String TAG = "class";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Declare our bottom navigation and add a listener to it!
         */
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        /**
         * Load the sensor Fragment when the view is created!
         */
        loadAFragment(new Sensor());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        /*
            Controller method to control what to do when a item in the navigation bar is selected!
         */
        Fragment f = null;

        switch (menuItem.getItemId()){
            case R.id.navigation_sensors:
                f = new Sensor();
                break;
            case R.id.navigation_configuration:
                f = new Configuration();
                break;
        }
        return loadAFragment(f);
    }

    /*
        Loading Fragments
    * */
    private boolean loadAFragment(Fragment f)
    {
        // If Fragment Object is equal to null then do this
        if (f != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
            return true;
        }
        return false;
    }
}
