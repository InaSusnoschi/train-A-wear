#include "Integration_algorithms.h"

#include <stdio.h>
#include <cmath>

using namespace std;

void Integration_algorithms::complementary_filter(float* angle, float acc)
{
    *angle = ((float)0.98)*(*angle) + 0.02*acc;
}


//y axis rotation
float Integration_algorithms::Roll(float gyro_y, float acc_x, float acc_z)
{
    float roll;
    float dt = 0.01;
    roll += gyro_y() * dt;
    printf("Original roll = %f\n",roll);
    float acc_correction_y = atan2f(acc_z(), acc_x())*180/M_PI;
    complementary_filter(&roll, acc_correction_y);
    printf("Calibrated roll = %f\n",roll);
    return roll;
}

//x axis rotation
float Integration_algorithms::Pitch(float gyro_x, float acc_y, float acc_z)
{
    float pitch;
    float dt = 0.01;
    pitch += gyro_x() * dt;
    printf("Original pitch = %f\n",pitch);
    float acc_correction_x = atan2f(acc_y(), acc_z())*180/M_PI;
    if (acc_correction_x >180.0){
        acc_correction_x -=(float)360.0;
    }
    printf("%f\n",acc_correction_x);
    complementary_filter(&pitch, acc_correction_x);
    printf("Calibrated pitch = %f\n",pitch);
    return pitch;
}

//rotation of the back (it does not stay stationary during the exercise)
//check for rotation along y axis
int Integration_algorithms::squat_straight_back(float gyro_y, float acc_x, float acc_z)
{
    float Roll(gyro_y, acc_x, acc_z);
    if (Roll == 0){
        return 1; //message "Bend during exercise!"
    }
}

//rotation of the knee (if it bends inwards)
//rotation along x axis
int Integration_algorithms::squat_bend_right_knee(float gyro_x, float acc_y, float acc_z)
{
    float pitches[2] = {0.0,0.0};
    pitches[1] = float Pitch(gyro_x, acc_y, acc_z);
    if (abs(pitches[1]-pitches[0])>0.3){
        printf("Wrong angle");
    }
    pitches[0] = pitches[1];
    pitches[1] = 0.0;
}
