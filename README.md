<p align="center"> <img src="https://github.com/InaSusnoschi/train-A-wear/blob/master/Images/trainAwear_logo.png" height=150px width=150px alt="train-A-wear logo" /> </p>

## Train-A-Wear
### A wearable workout monitor that provides real time feedback on the user's form while performing basic workouts to improve shape and exercise outcomes.

This uses a Raspberry Pi and three Inertial Measurement Units that detect position and orientation of the body segments they are placed on.

## If you need help or want to brag about your sensor, find us on any social media or join our [![Gitter](https://badges.gitter.im/train-A-wear/community.svg)](https://gitter.im/train-A-wear/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


## Prerequisites
* Inertial Measurement Unit train-box
* Local WiFi network
* Android Smartphone

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

## Using the real time sensor system
Once everything is set up on the Raspberry Pi and the application is installed, the user can start using the features provided. The system requires a specific number and location for the inertial sensors, depending on the exercise type.

### Setup
To obtain the correct sensor placement, consult the Wear it and go section inside each workout. This provides descriptions of the location and orientation, as well as images. The top of the sensor carcass displays two logos and two LEDs, which allows the user to determine the correct placement.
### Quick start
Star the application -> Workout -> exercise of choice -> START

### Obtaining feedback
Enter a workout using the steps quoted in <i>Quick start</i>. When the sensors are ready to go, the message "Ready to go!" is displayed. Then, the workout can be started anytime by pressing START, and stopped  
The sensors send datagrams to the RPI, whose algorithms process it and send instructions to the app. These are displayed on the screen in each workout activity.

### Additional features
To get help on <b>network or sensor faults</b>: start the app -> Help -> FAULTY SYSTEM
For more detail on <b>pre- and post-workout stretching</b>: start the app -> Help -> RECOMMENDED STRETCHES
Advice on <b>healthy lifestyle<b> is available: start the app -> Help -> HEALTH & LIFESTYLE

### Sensor power
When the sensor is powered, the green LED turns on. The red LED is on when the battery is connected.

## Documentation

### Examples
Using IMU data to obtain body position and orientation.
This yields the roll of the body segment
```ruby
double Integration_algorithms::Roll(double gyro_y, double acc_x, double acc_z)
{
    double roll;
    double dt = 0.01;
    roll += gyro_y * dt;
    double acc_correction_y = atan2f(acc_z, acc_x) *180/M_PI;
    complementary_filter(&roll, acc_correction_y);
    return roll;
}
```
Which can be used to monitor back posture while squatting.
#### Checking for lower back bending during squats
```
//rotation of the back (it does not stay stationary during the exercise)
//check for rotation along y axis
int Integration_algorithms::squat_straight_back(double gyro_y, double acc_x, double acc_z)
{
    double roll = Integration_algorithms::Roll(gyro_y, acc_x, acc_z);
    if (roll < 0.2 || roll > 0.2){
        return 1; //message "Bend during exercise!"
    } else{
        return 2; //message "Keep the nice posture"
    }
}
```
### Decision loops for workout feedback on the application
The app receives continuous data after pressing the START button and implements a switch such as the one below, used for squat monitoring, to display feedback.

```ruby
    public static String showFeedback (String instructionDef){
        switch (instructionDef){
            case "0":
                return("Ready to start");

            case "1":
                return ("Let's go!");
                
            case "2":
                return("Your back is bending, keep it straight while tensing your abs");

            case "3":
                return("Knees caving in: Keep your knees in line with your toes");

            case "4":
                return ("It looks like your form is asymmetrical when lowering in squat");

            default:
                return("You're doing great, keep going!");
        }
    }```
