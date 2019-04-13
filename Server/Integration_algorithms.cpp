#include "Integration_algorithms.h"

#include <stdio.h>
#include <cmath>

using namespace std;

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
        return 2;
    }
}

//rotation of the knee (if it bends inwards)
//rotation along x axis
int Integration_algorithms::squat_bend_right_knee(double gyro_x, double acc_y, double acc_z)
{
    double pitches[2] = {0.0,0.0};
    pitches[1] = Integration_algorithms::Pitch(gyro_x, acc_y, acc_z);
    if (abs(pitches[1]-pitches[0])>0.3){
        printf("Wrong angle");
    }
    pitches[0] = pitches[1];
    pitches[1] = 0.0;
    return 10;
}
