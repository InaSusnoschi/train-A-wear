/*!
 * @file Integration_algorithms.h
 * @author Margarita Ivanova
 *  \brief Algorithms used for the measurement of the posture of the person wearing the train-A-wear
 *         sensor system
 */

#ifndef INTEGRATION_ALGORITHMS_H
#define INTEGRATION_ALGORITHMS_H

#include "Buffer.h"

class Integration_algorithms
{
    public:
        Integration_algorithms();
        ~Integration_algorithms();
        void complementary_filter(double* angle, double acc);
        double Roll(double gyro_y, double acc_x, double acc_z);
        double Pitch(double gyro_x, double acc_y, double acc_z);
        int squat_straight_back(double gyro_y, double acc_x, double acc_z);
        int squat_twist_right_knee(double gyro_x, double acc_y, double acc_z);
        int squat_twist_left_knee(double gyro_x, double acc_y, double acc_z);
        int plank_bend_back(double gyro_y, double acc_x, double acc_z);
        int pushup_shoulder_back(double s1_gyro_y, double s1_acc_x, double s1_acc_z, double s2_gyro_y, double s2_acc_x, double s2_acc_z);
        int pushup_knee_back(double s1_gyro_y, double s1_acc_x, double s1_acc_z, double s3_gyro_y, double s3_acc_x, double s3_acc_z);
    protected:

    private:
        Buffer* sensor1_rotx;
        Buffer* sensor1_roty;
        Buffer* sensor2_rotx;
        Buffer* sensor2_roty;
        Buffer* sensor3_rotx;
        Buffer* sensor3_roty;
};

#endif // INTEGRATION_ALGORITHMS_H
