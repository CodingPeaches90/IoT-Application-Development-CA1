/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.x15515673.sensorfirebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 *
 * @author jordanmay
 * x15515673 -- IOT Application Development
 * 
 */
public class FirebaseReceiver
{ 
   /*
        We use a semaphore to control our multiprocessing
        We can exploit this data type to ensure our main 
        class does not finish executing!
    */
        
    static Semaphore semaphore;
    
    /*
        Main Method entry point
    */
    public static void main(String...args) throws FileNotFoundException, IOException, InterruptedException
    {
        /* Assign Variables*/
        
        semaphore = new Semaphore(0);
       
        /*
            Read in the Account Details and try to connect to Firebase's Database, If 
            can't connect then exit the program and take no further action.
        */
        
        FileInputStream serviceAccount = new FileInputStream("iotappdev-firebase-adminsdk-ynmhv-0a3d1e158d.json");
        
        try{
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://iotappdev.firebaseio.com/")
                .build();

            FirebaseApp.initializeApp(options);
        }catch(IOError e){
            System.out.println("IOERROR " + e);
            System.exit(0);
        }
        
        /*
            We declare two Asynchronous Firebase listeners to listen and both the Sensor information
            node and also our configuration node. Each listener has a onDataChange method that takes 
            a DataSnapshot param. These methods are evoked when there is a change in value anywhere within the 
            specified nodes.
        */
                
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sensors/");
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot ds) {
                System.out.println("Sensor Information : " + ds);
            }

            @Override
            public void onCancelled(DatabaseError de) {} 
        });
        DatabaseReference re1 = FirebaseDatabase.getInstance().getReference("Configuration/");
        re1.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot ds) {
                System.out.println("Configuration Information : " + ds);
            }

            @Override
            public void onCancelled(DatabaseError de) {}  
        });
        /*
            Wait till the semaphore is released.
        */
        
        semaphore.acquire();
    }
}
