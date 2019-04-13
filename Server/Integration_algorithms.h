#ifndef INTEGRATION_ALGORITHMS_H
#define INTEGRATION_ALGORITHMS_H


class Integration_algorithms
{
    public:
        void complementary_filter(double* angle, double acc);
        double Roll(double gyro_y, double acc_x, double acc_z);
        double Pitch(double gyro_x, double acc_y, double acc_z);
        int squat_straight_back(double gyro_y, double acc_x, double acc_z);
        int squat_bend_right_knee(double gyro_x, double acc_y, double acc_z);

    protected:

    private:
};

#endif // INTEGRATION_ALGORITHMS_H
