import time
import random
from random import randint
import sys
from threading import Thread
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

# grove pi imports
import grovepi
from grovepi import *

# variables for sensors
temperature_humidty_port = 2
led_light = 5
sound_sensor = 4
pinMode(led_light,"OUTPUT")
grovepi.pinMode(sound_sensor,"INPUT")


# Firebase Credential Set Up!
try:
    cred = credentials.Certificate('iotappdev-firebase-adminsdk-ynmhv-0a3d1e158d.json')
    firebase_admin.initialize_app(cred, {'databaseURL' : 'https://iotappdev.firebaseio.com/'})
except IOError as e:
    print("Can't read JSON, is the path correct")
    print("I/O error({0}): {1}".format(e.errno, e.strerror))

# Store the root node of the Firebase Data Structure!
root_node = db.reference()
root_node1 = db.reference()
# The Listener Callback is a Background Thread which
# Listens to up to date data from our specified node
# Exclusively our Configuratiuon Threading Task
def startFirebaseListenerThread():
    firebase_admin.db.reference('/').listen(listen_to_firebase_configuration)
# When there is a change in our root node then we check 
# our values and run the relevant methods!
def listen_to_firebase_configuration(event):
    # Declare our Firebase DB Paths to Each Sensor
    temperatureState = db.reference('Configuration/TemperatureSensor/Temperature State')
    ledState = db.reference('Configuration/LED/LED State')
    soundSensor = db.reference('Configuration/SoundSensor/Sound State')
    
    # Temperature block
    if temperatureState.get() == "ON":
        # In here we want to start a new thread  
        # To publish our Temperature Sensor Data
        # Also we should declare a global boolean!
        global turnTemperatureState
        turnTemperatureState = True
        # Now we Know our state we should try to start out Thread
        try:
            # Start a new thread for the Temperature Publishing
            start_temperature_publishing = Thread(target = publishTemperatureInformation)
            if not start_temperature_publishing.isAlive():
                start_temperature_publishing.start()
                
        except Exception as e:
            exc_info = sys.exc_info()
            print(exc_info)
            pass

        # print("Turn the Temperature ON! annd start thread")
    else:
        turnTemperatureState = False
        print("Turn the Temperature Off!")
    
    # Sound Sensor Block
    if soundSensor.get() == "ON":
        # if the user presses ON for the sounnd sensor then we start our thread!
        global turnSoundState
        turnSoundState = True
        
        try:
            # Establish our Thread!
            start_sound_publishing = Thread(target = publishSoundSensor)
            
            if not start_sound_publishing.isAlive():
                start_sound_publishing.start()
                print("Turning Sound Thread On!")
                
        except Exception as e:
            exc_info = sys.exc_info()
            print(exc_info)
            pass
    else:
        turnSoundState = False
        print("Turning the Sound off from publishing!")
    
    # LED Block
    state = ledState.get()
    lightLEDData(state)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
                          #   Sensor Publishing  #
# These methods all run on their on thread as defined by the above listener. When
# there is a data change to our Firebase DB we publish our Sensor data. We need
# to be able to keep listening to our config data, both Rate and State.
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 

# Get Temperature from our Sensor and publish to Firebase
def publishTemperatureInformation():
    # While the user wants the temperature "ON" Keep printing sensor information
    ref1 = db.reference('Configuration/TemperatureSensor/Temperature Rate')
    while turnTemperatureState:
        tempSampleRate = int(ref1.get())
        value = getTemperatureHumData(tempSampleRate);
        value1 = getTemperatureHumidity(tempSampleRate);
        
        # Calls to our Firebase instance, each node update them!
        temperatureUpdate = root_node.child("Sensors/TemperatureSensor/").update({'Temperature' : value})
        temperatureUpdate = root_node.child("Sensors/TemperatureSensor/").update({'Humidity' : value1})


    print("***********************************")
    print("PUBLISHING STOPPED FOR TEMPERATURE!")
    print("***********************************")

# Get our Sound sensor and publish to Firebase!
def publishSoundSensor():
    # So we know the user wants the sensor on, now get the specified rate!
    ref2 = db.reference('Configuration/SoundSensor/Sound Rate')
    while turnSoundState:
        soundRate = int(ref2.get())
        value1 = getSoundSensorData(soundRate)
        
        # We have our Value now store into Firebase!
        soundUpdate = root_node1.child("Sensors/SoundSensor/").update({'Sound' : value1})
    print("*****************************")
    print("PUBLISHING STOPPED FOR SOUND!")
    print("*****************************")

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
                            #   Sensor Querying  #                            
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# Getting the Temperature Data
def getTemperatureHumData(tempSampleRate):
    time.sleep(tempSampleRate)
    try:
        [temp, humidity] = dht(temperature_humidty_port,0)
        temperature = temp
        
        return temperature

    except IOError:
        
        return 0
    
# Gettinng the Humidity Data
def getTemperatureHumidity(tempSampleRate):
    time.sleep(tempSampleRate)
    try:
        [temp, hum] = dht(temperature_humidty_port,0)
        humidity = hum
        return humidity
    except IOError:
        return 0

def lightLEDData(state):
    try:
        if state == "ON":
            print("LED IS ON")
            digitalWrite(led_light,1)
        else:
            print("LED IS OFF")
            digitalWrite(led_light,0)
    except IOError:
        return 0

def getSoundSensorData(soundRate):
    time.sleep(soundRate)
    try:
        sensor_value = grovepi.analogRead(sound_sensor)
        return sensor_value
    except IOError:
        return 0
    
# We want to be able to start our listener so we multi thread
# publish_to_firebase_thread = Thread(target=publishTemperatureInformation)
listen_for_config_details = Thread(target = startFirebaseListenerThread(), args=(publishTemperatureInformation,))
listen_for_config_details.start()