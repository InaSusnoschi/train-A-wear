<p align="center"> <img src="https://github.com/InaSusnoschi/train-A-wear/blob/master/Images/trainAwear_logo.png" height=150px width=150px alt="train-A-wear logo" /> </p>

## Train-A-Wear
### A wearable workout monitor that provides real time feedback on the user's form while performing basic workouts to improve shape and exercise outcomes.

This uses a Raspberry Pi and three Inertial Measurement Units that detect position and orientation of the body segments they are placed on.

## If you need help or want to brag about your sensor, find us on any social media or join our [![Gitter](https://badges.gitter.im/train-A-wear/community.svg)](https://gitter.im/train-A-wear/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


## Prerequisites
* Inertial Measurement Unit train-box
* Local WiFi network

### Server
* Linux machine with g++ compiler

### Sensor software
* Arduino IDE
* ESP8266 Library
* LSM9DS1 SparkFun Library

### Android app:
* An Android phone
<ul>
  <li> Android SDK 27 </li>
  <li> Android Build tools v27.0.2 </li>
  <li> Android Support Repository </li>
</ul>


## Installing

### Server
To install the server run the following commands on a Linux machine:
```
cd ./Server
make
./tAw-server
```
And you would have the train-A-wear server running on port 31415. The port can be changed in the server file.

### Microcontroller
Open the ino sketch from Sensor Software folder in Arduino IDE. Select settings for programming a generic ESP8266 board.
Modify the following lines:
```
*ssid_1 = "X"
*pass_1 = "X"
```
with the values of your WiFi's SSID and password. Upload the sketch, reset the sensor and it will be up and running.

### Phone application
Import the project from Phone App folder in Android Studio and run the gradle build. You can either generate an APK file or install it straight away on your phone.

## Documentation

### Examples
Using IMU data to obtain body position and orientation.

Decision loops for workout feedback.

Real time data plotting on smartphone (php)
