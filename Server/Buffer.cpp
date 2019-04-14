#include "Buffer.h"

#include <stdio.h>
#include <cmath>
#include <stdlib.h>
#include <iostream>

using namespace std;

//Buffer class to save the last two values from the sensors for later analysis


//Initialize the buffer by allocating memory for two double numbers and
// setting them to 0
Buffer::Buffer()
{
    stage = 0;
    buffer_val = (double*) malloc(sizeof(double)*2);
    if(buffer_val){
        buffer_val[0] = 0;
        buffer_val[1] = 0;
    }
}

Buffer::~Buffer()
{
    if(buffer_val){
        free(buffer_val);
    }
}

void Buffer::print_buffer(void){
    cout << "buffer[0]: " << buffer_val[0] << endl;
    cout << "buffer[1]: " << buffer_val[1] << endl;

}

//creating the buffer with two values which are recorded as one by one comes in
//  it starts filling up the buffer from the first position and
//  when full, the oldest value is replaced with next value and the new value
//  is saved in the last position
void Buffer::buffer(double value2)
{
    if(stage == 0){
        stage++;
        buffer_val[0] = value2;
    }else if(stage == 1){
        stage++;
        buffer_val[1] = value2;
    }else if(stage >= 2){
        buffer_val[0] = buffer_val[1];
        buffer_val[1] = value2;
    }
    cout<<stage<<endl;
}

//accessing the buffer values
double* Buffer::get_buffer(void){
    double* temp= (double*)malloc(sizeof(double));
    temp[0] = buffer_val[0];
    temp[1] = buffer_val[1];
    return temp;
}
