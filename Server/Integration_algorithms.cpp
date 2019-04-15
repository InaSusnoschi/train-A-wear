/*!
 * @file Integration_algorithms.cpp
 * @author Margarita Ivanova
 *  \brief Algorithms used for the measurement of the posture of the person wearing the train-A-wear
 *         sensor system
 */

#include "Integration_algorithms.h"
#include "Buffer.h"

#include <stdio.h>
#include <cmath>
#include <stdlib.h>
#include <iostream>

using namespace std;



Integration_algorithms::Integration_algorithms(){
    sensor1_rotx = new Buffer();
    sensor1_roty = new Buffer();
    sensor2_rotx = new Buffer();
    sensor2_roty = new Buffer();
    sensor3_rotx = new Buffer();
    sensor3_roty = new Buffer();
}

Integration_algorithms::~Integration_algorithms(){

}

/**
 * @brief Function to create a filtered version of the calculated change of rotation
 * @param angle A pointer to the rotational angle
 * @param acc The acceleration used for the filtering
*/
// idea taken from https://sites.google.com/site/myimuestimationexperience/filters/complementary-filter
void Integration_algorithms::complementary_filter(double* angle, double acc)
{
    *angle = ((double)0.98)*(*angle) + 0.02*acc;
}

/**
 * @brief Calculation of the roll (y-axis sensor rotation)
 * @param gyro_y The value from the y-axis of the gyroscope
 * @param acc_x The value from the x-axis of the accelerometer
 * @param acc_z The value from the z-axis of the accelerometer
*/
//y axis rotation
double Integration_algorithms::Roll(double gyro_y, double acc_x, double acc_z)
{
    double roll;
    double dt = 0.01;
    roll += gyro_y * dt;
    double acc_correction_y = atan2f(acc_z, acc_x) *180/M_PI;
    complementary_filter(&roll, acc_correction_y);
    return roll;
}


/**
 * @brief Calculation of the roll (x-axis sensor rotation)
 * @param gyro_x The value from the x-axis of the gyroscope
 * @param acc_y The value from the y-axis of the accelerometer
 * @param acc_z The value from the z-axis of the accelerometer
*/
//x axis rotation
double Integration_algorithms::Pitch(double gyro_x, double acc_y, double acc_z)
{
    double pitch;
    double dt = 0.01;
    pitch += gyro_x * dt;
    double acc_correction_x = atan2f(acc_y, acc_z)*180/M_PI;
    if (acc_correction_x >180.0){
        acc_correction_x -=(double)360.0;
    }
    complementary_filter(&pitch, acc_correction_x);
    return pitch;
}


/**
 * @brief Algorithm checking for back rotation during squats
 * @param gyro_y The value from the sensor on the back the y-axis of the gyroscope
 * @param acc_x The value from the sensor on the back the x-axis of the accelerometer
 * @param acc_z The value from the sensor on the back the z-axis of the accelerometer
*/
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

/**
 * @brief Algorithm checking for twisting of the right knee
 * @param gyro_x The value from the sensor on the right knee for the x-axis of the gyroscope
 * @param acc_y The value from the sensor on the right knee for the y-axis of the accelerometer
 * @param acc_z The value from the sensor on the right knee for the z-axis of the accelerometer
*/
//rotation of the knee (if it bends inwards)
//rotation along x axis
int Integration_algorithms::squat_twist_right_knee(double gyro_x, double acc_y, double acc_z)
{
    sensor1_rotx->buffer(Integration_algorithms::Pitch(gyro_x, acc_y, acc_z));
    double* rotation_x = sensor1_rotx->get_buffer();
    if (abs(rotation_x[1] - rotation_x[0]) > 0.3){
        return 3; //wrong way
    }{
        return 5; //right way
    }
}

/**
 * @brief Algorithm checking for twisting of the right knee
 * @param gyro_x The value from the sensor on the left knee for the x-axis of the gyroscope
 * @param acc_y The value from the sensor on the left knee for the y-axis of the accelerometer
 * @param acc_z The value from the sensor on the left knee for the z-axis of the accelerometer
*/
//check for twisting (both in and out) of the left knee
int Integration_algorithms::squat_twist_left_knee(double gyro_x, double acc_y, double acc_z)
{
    sensor2_rotx->buffer(Integration_algorithms::Pitch(gyro_x, acc_y, acc_z));
    double* rotation_x = sensor2_rotx->get_buffer();
    if (abs(rotation_x[1] - rotation_x[0]) > 0.3){
        return 4; //wrong way
    }{
        return 5; //right way
    }
}

/**
 * @brief Algorithm checking for bent back during planks/push ups
 * @param gyro_y The value from the sensor on the back for the y-axis of the gyroscope
 * @param acc_x The value from the sensor on the back for the x-axis of the accelerometer
 * @param acc_z The value from the sensor on the back for the z-axis of the accelerometer
*/
//check for bend back during planks/push up
int Integration_algorithms::plank_bend_back(double gyro_y, double acc_x, double acc_z)
{
    double Roll = Integration_algorithms::Roll(gyro_y, acc_x, acc_z);
    if (Roll > 0){
        return 6; //message "Straighten you back during exercise!"
    } else{
        return 7; //message "Keep the nice posture"
    }
}


/**
 * @brief Algorithm checking for rate of change between the back and the shoulders during push ups
 * @param s1_gyro_y The value from the sensor on the back for the y-axis of the gyroscope
 * @param s1_acc_x The value from the sensor on the back for the x-axis of the accelerometer
 * @param s1_acc_z The value from the sensor on the back for the z-axis of the accelerometer
 * @param s2_gyro_y The value from the sensor on the shoulders for the y-axis of the gyroscope
 * @param s2_acc_x The value from the sensor on the shoulders for the x-axis of the accelerometer
 * @param s2_acc_z The value from the sensor on the shoulders for the z-axis of the accelerometer
*/
//check if the back and shoulders are aligned during push up
int Integration_algorithms::pushup_shoulder_back(double s1_gyro_y, double s1_acc_x, double s1_acc_z, double s2_gyro_y, double s2_acc_x, double s2_acc_z)
{
    sensor1_roty->buffer(Integration_algorithms::Roll(s1_gyro_y, s1_acc_x, s1_acc_z)); //back sensor
    double* rotation1_x = sensor1_roty->get_buffer();
    sensor2_roty->buffer(Integration_algorithms::Roll(s2_gyro_y, s2_acc_x, s2_acc_z)); //shoulder sensor
    double* rotation2_x = sensor2_roty->get_buffer();
    if ((abs(rotation1_x[1] - rotation1_x[0]) - abs(rotation2_x[1] - rotation2_x[0])) > 0.2){
        cout<<"Straighten your shoulders"<<endl;
        return 8;
    }{
        cout<<"right way"<<endl;
        return 9;
    }
}

/**
 * @brief Algorithm checking for rate of change between the back and the knees during push ups
 * @param s1_gyro_y The value from the sensor on the back for the y-axis of the gyroscope
 * @param s1_acc_x The value from the sensor on the back for the x-axis of the accelerometer
 * @param s1_acc_z The value from the sensor on the back for the z-axis of the accelerometer
 * @param s3_gyro_y The value from the sensor on the right knee for the y-axis of the gyroscope
 * @param s3_acc_x The value from the sensor on the right knee for the x-axis of the accelerometer
 * @param s3_acc_z The value from the sensor on the right knee for the z-axis of the accelerometer
*/
int Integration_algorithms::pushup_knee_back(double s1_gyro_y, double s1_acc_x, double s1_acc_z, double s3_gyro_y, double s3_acc_x, double s3_acc_z)
{
    sensor1_roty->buffer(Integration_algorithms::Roll(s1_gyro_y, s1_acc_x, s1_acc_z)); //back sensor
    double* rotation1_x = sensor1_roty->get_buffer();
    sensor3_roty->buffer(Integration_algorithms::Roll(s3_gyro_y, s3_acc_x, s3_acc_z)); //knee sensor
    double* rotation3_x = sensor3_roty->get_buffer();
    if ((abs(rotation1_x[1] - rotation1_x[0]) - abs(rotation3_x[1] - rotation3_x[0])) > 0.2){
        cout<<"Dont bend your knees"<<endl;
        return 10;
    }{
        cout<<"right way"<<endl;
        return 11;
    }
}
