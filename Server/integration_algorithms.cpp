//following the fixeddemo.cpp by Bernd Porr

//after calculating the filter coefficients with Python
//sampling frequency = 119Hz (for now assumed)

#include <stdio.h>
#include <cmath>


using namespace std;
//function defenition
float F_gyro_x();
float F_gyro_y();
float F_gyro_z();
float F_acc_x();
float F_acc_y();
float F_acc_z();
void complementary_filter(float* angle, float acc);



int main (int, char**)
{


    //10 ms sample (almost 119Hz)
    float dt = 0.01;
    F_gyro_z();
    F_gyro_x();
    //x-axis rotation
    float pitch;
    pitch += F_gyro_x() * dt;
    printf("Original pitch = %f\n",pitch);
    //float acc_correction_x = atan2f(F_acc_x(),sqrt(F_acc_y()*F_acc_y() + F_acc_z()*F_acc_z()))*180/M_PI;
    float acc_correction_x = atan2f(F_acc_y(), F_acc_z())*180/M_PI;
    if (acc_correction_x >180.0){
        acc_correction_x -=(float)360.0;
    }
    printf("%f\n",acc_correction_x);
    complementary_filter(&pitch, acc_correction_x);
    printf("Calibrated pitch = %f\n",pitch);

    //y rotation
    float roll;
    roll += F_gyro_y() * dt;
    printf("Original roll = %f\n",roll);
    float acc_correction_y = atan2f(F_acc_z(),F_acc_x())*180/M_PI;
    complementary_filter(&roll, acc_correction_y);
    printf("Calibrated roll = %f\n",roll);

    //check if the knee angle is beyound the threshold
    float pitches[2] = {0.0,0.0};
    pitches[1] = pitch;
    if (abs(pitches[1]-pitches[0])>0.3){
        printf("Wrong angle");
    }
    pitches[0] = pitches[1];
    pitches[1] = 0.0;
}



void complementary_filter(float* angle, float acc){
    //calculate calibrated pitch with filtered data
    *angle = ((float)0.98)*(*angle) + 0.02*acc;
}

float F_acc_x(){

    //return acceleration x
    //float acc_x = 1.0f/182.0f; //1g with 16 bit
    float acc_x = 160; //ina's samples
    return acc_x;
}

float F_acc_y(){
    //return acceleration y
    //float acc_y = 1.5f/182.0f; //1.5g with 16 bit
    float acc_y = 0.8842; //ina's samples
    return acc_y;
}

float F_acc_z(){
    //return acceleration x
    //float acc_z = 0.5f/182.0f; //0.5g with 16 bit
    float acc_z = 0.8094; //ina's samples
    return acc_z;
}

float F_gyro_x(){
    //return gyro x
    //float gyro_y = 240.0f/131.0f; //250deg/s with 16 bit
    float gyro_x = -0.0588; //ina's samples
    return gyro_x;
}
float F_gyro_y(){
    //return gyro y
    //float gyro_y = 100.0f/131.0f;
    float gyro_y = -130;
    return gyro_y;
}
float F_gyro_z(){
    //return gyro z
    //float gyro_z = 10.0f/131.0f;
    float gyro_z = -150;
    return gyro_z;
}
