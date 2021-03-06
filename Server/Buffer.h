/*!
 * @file Buffer.h
 * @author Margarita Ivanova
 *  \brief Buffer for storing the last two values of a sensor
 */

#ifndef BUFFER_H
#define BUFFER_H


class Buffer
{
    public:
        Buffer();
        ~Buffer();
        void buffer(double value2);
        double* get_buffer(void);
        void print_buffer(void);
    protected:

    private:
        double* buffer_val;
        int stage;
};

#endif // BUFFER_H
