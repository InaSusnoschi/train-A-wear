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

void Integration_algorithms::complementary_filter(double* angle, double acc)
{
    *angle = ((double)0.98)*(*angle) + 0.02*acc;
}



//y axis rotation
double Integration_algorithms::Roll(double gyro_y, double acc_x, double acc_z)
{
    double roll;
    double dt = 0.01;
    roll += gyro_y * dt;
    printf("Original roll = %f\n",roll);
    double acc_correction_y = atan2f(acc_z, acc_x) *180/M_PI;
    complementary_filter(&roll, acc_correction_y);
    printf("Calibrated roll = %f\n",roll);
    return roll;
}

//x axis rotation
double Integration_algorithms::Pitch(double gyro_x, double acc_y, double acc_z)
{
    double pitch;
    double dt = 0.01;
    pitch += gyro_x * dt;
    printf("Original pitch = %f\n",pitch);
    double acc_correction_x = atan2f(acc_y, acc_z)*180/M_PI;
    if (acc_correction_x >180.0){
        acc_correction_x -=(double)360.0;
    }
    printf("%f\n",acc_correction_x);
    complementary_filter(&pitch, acc_correction_x);
    printf("Calibrated pitch = %f\n",pitch);
    return pitch;
}

//rotation of the back (it does not stay stationary during the exercise)
//check for rotation along y axis
int Integration_algorithms::squat_straight_back(double gyro_y, double acc_x, double acc_z)
{
    double Roll = Integration_algorithms::Roll(gyro_y, acc_x, acc_z);
    if (Roll == 0){
        return 1; //message "Bend during exercise!"
    } else{
        return 2; //message "Keep the nice posture"
    }
}

//rotation of the knee (if it bends inwards)
//rotation along x axis
int Integration_algorithms::squat_twist_right_knee(double gyro_x, double acc_y, double acc_z)
{
    sensor1_rotx->buffer(Integration_algorithms::Pitch(gyro_x, acc_y, acc_z));
    double* rotation_x = sensor1_rotx->get_buffer();
    if (abs(rotation_x[1] - rotation_x[0]) > 0.3){
        cout<<"wrong way"<<endl;
        return 3;
    }{
        cout<<"right way"<<endl;
        return 5;
    }
}

//check for twisting (both in and out) of the left knee
int Integration_algorithms::squat_twist_left_knee(double gyro_x, double acc_y, double acc_z)
{
    sensor2_rotx->buffer(Integration_algorithms::Pitch(gyro_x, acc_y, acc_z));
    double* rotation_x = sensor2_rotx->get_buffer();
    if (abs(rotation_x[1] - rotation_x[0]) > 0.3){
        cout<<"wrong way"<<endl;
        return 4;
    }{
        cout<<"right way"<<endl;
        return 5;
    }
}

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

//check if the back and shoulders are aligned during push up
int Integration_algorithms::pushup_shoulder_back(double gyro_x, double acc_y, double acc_z)
{
    sensor2_roty->buffer(Integration_algorithms::Roll(gyro_x, acc_y, acc_z)); //back sensor
    double* rotation1_x = sensor2_roty->get_buffer();
    sensor3_roty->buffer(Integration_algorithms::Roll(gyro_x, acc_y, acc_z)); //shoulder sensor
    double* rotation2_x = sensor3_roty->get_buffer();
    if ((abs(rotation1_x[1] - rotation1_x[0]) - abs(rotation2_x[1] - rotation2_x[0])) > 0.2){
        cout<<"Straighten your shoulders"<<endl;
        return 8;
    }{
        cout<<"right way"<<endl;
        return 9;
    }
}

int Integration_algorithms::pushup_knee_back(double gyro_x, double acc_y, double acc_z)
{
    sensor2_roty->buffer(Integration_algorithms::Roll(gyro_x, acc_y, acc_z)); //back sensor
    double* rotation1_x = sensor2_roty->get_buffer();
    sensor1_roty->buffer(Integration_algorithms::Roll(gyro_x, acc_y, acc_z)); //knee sensor
    double* rotation2_x = sensor1_roty->get_buffer();
    if ((abs(rotation1_x[1] - rotation1_x[0]) - abs(rotation2_x[1] - rotation2_x[0])) > 0.2){
        cout<<"Dont bend your knees"<<endl;
        return 10;
    }{
        cout<<"right way"<<endl;
        return 11;
    }
}
