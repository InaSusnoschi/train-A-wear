/**
 *  @brief train-A-wear server file.
 *  A UDP server that binds to port 31415 to listen for any train-A-wear active devices 
 *  and transmit results to the coresponding phone apps.
 *
 *  @author Borislav Gachev
 *  @author Margarita Ivanova
 *
 *  @date April 2019
 *  @file
 *
 */

#include <cstring>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <unistd.h>
#include <iostream>
#include <map>

//Needed for figuring out machine IP address as per https://www.includehelp.com/c-programs/get-ip-address-in-linux.aspx
#include <sys/ioctl.h>
#include <net/if.h>

//Broadcast message timestamping
#include <thread>
#include <chrono>

//JSON Parsing
#include "rapidjson/document.h"
#include "rapidjson/prettywriter.h"
#include <cstring>
#include <mutex>

#include "Integration_algorithms.h"

/** Maximum length of a single UDP packet to be read in from the socket **/
#define BUFFER_LENGTH 1500
/** Default port that the server would establish a socket to listen on **/
#define PORT 31415 //Because it's going to run on Raspberry 3.1415 ;)
/** Delay in seconds between successive multicast messages sent from the server **/
#define BROADCAST_DELAY_S 45

using namespace std;
using namespace rapidjson;

const char*	handshake = "train-A-wear online\n"; /**< constant char array. It holds our pre-defined text used for handshaking between server, sensors and phones. */
mutex mut;

/**
 * A structure that holds the latest sensor readings, after parsing them.
 */
struct sensor_data {
	double 	gyro[3]; /**< Array containing gyroscope readings in X, Y, Z order. */
	double 	accelerometer[3]; /**< Array containing accelerometer readings in X, Y, Z order. */
	double 	magnetometer[3]; /**< Array containing magnetometer readings in X, Y, Z order. */
};


/**
 * A thread function that sends a predefined message to Multicast IP address over UDP. Used for
 * for automatic network discovery of train-A-wear active devices. After sending the message 
 * the function sleeps for a predefined number of seconds.
 *
 * @see BROADCAST_DELAY_S
 * @see handshake
 */
int broadcast_server(){
	int 				socket_d;
	struct sockaddr_in 	multicast_addr;
	int 				broadcast = 1;

	// Create the socket
	if((socket_d = socket(AF_INET, SOCK_DGRAM, 0)) == -1){
		perror("Unable to create the multicast UDP socket.");
		return 1;
	}

	if(setsockopt(socket_d, SOL_SOCKET, SO_BROADCAST, &broadcast, sizeof(broadcast)) < 0){
        perror("Error in setting multicast broadcast option");
        close(socket_d);
        return 3;
    }

	//Fillout the server information for multicasting to "255.255.255.255"
	multicast_addr.sin_family 		= AF_INET;
	multicast_addr.sin_port 		= htons(PORT);
	multicast_addr.sin_addr.s_addr 	= inet_addr("255.255.255.255");


	//Run this indefinetely until the thread is closed. Sleed for BROADCAST_DELAY_S between executions
	for(;;){
		mut.lock();
		sendto(socket_d, handshake, strlen(handshake), MSG_CONFIRM, (const struct sockaddr *) &multicast_addr, sizeof(multicast_addr));
		time_t now_time = chrono::system_clock::to_time_t(chrono::system_clock::now());
		cout << "Broadcast sent @ " << ctime(&now_time);
		cout << endl;
		mut.unlock();
		this_thread::sleep_for(chrono::seconds(BROADCAST_DELAY_S));
	}
}


int main(void){
	
	int  					fd; /** Int field holding the socket descriptor */
	ssize_t					rlen; /** Length of received packet */
	struct sockaddr_in 		addr, conn_addr; /** sockaddr_in structures for determining local IP address and receiving UDP packets */
	char 					buffer[BUFFER_LENGTH]; /** Buffer for holding the data from the received UDP packet */
	int 					flags = 0; /** int variable holding the flags from recv function */

	// Needed for UDP connection as per https:linux.die.net/man/3/getaddrinfo
	struct sockaddr_in 		client_addr; /** sockaddr_in structure holding the address of the client */
	socklen_t 				client_addr_len; /** socklen_t for holding the length of the client address */
	char*					message; /** char * the will hold the received message from the client */

	// Determining local IP
	char 			ip_address[15]; /** 16-digit variable for holding the local machine's IP address */
	struct ifreq 	ifr; /** Structure for holding the result of IOCTL call */

	//Network IP discovery
	int broadcast = 1; /** Flag used for allowing sending and receiving multicast messages */

	//JSON transmission variables
	Document receivedDocument; /** Document variable that holds the received JSON documents after transmission */
	map<string, sensor_data> sensorRecords; /** map record that holds known sensor data stored. Sensor names are keys, sensor_data structs are values. @see sensor_data */
	string 	sensorName; /** Holds the name of the sensor received on each transmission */


	fd = socket(AF_INET, SOCK_DGRAM, 0);
	ifr.ifr_addr.sa_family = AF_INET;
	memcpy(ifr.ifr_name, "wlan0", IFNAMSIZ-1);
	ioctl(fd, SIOCGIFADDR, &ifr);
	close(fd);

	strcpy(ip_address,inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr));
	cout << "Server IP Address is: " << ip_address << endl;

	// Create a UDP (SOCK_DGRAM) over IP scoket
	fd = socket (AF_INET, SOCK_DGRAM, 0);
	if(fd == -1){
		perror("Unable to create socket.");
		return 1;
	}


	if(setsockopt(fd,SOL_SOCKET,SO_BROADCAST,&broadcast,sizeof(broadcast)) < 0){
        perror("Error in setting Broadcast option");
        close(fd);
        return 3;
    }

	// Attempt to bind to port 31415
	addr.sin_family 		= AF_INET;
	addr.sin_port  			= htons(PORT);
	addr.sin_addr.s_addr 	= INADDR_ANY;

	if(bind(fd, (struct sockaddr *) &addr, sizeof(addr)) == -1) {
		perror("Unable to bind to port 31415");
		return 2;
	}

	thread broadcast_thread(broadcast_server);

	// Retrieve the data in the packet
	client_addr_len = sizeof(struct sockaddr_storage);
	while((rlen = recvfrom(fd, buffer, BUFFER_LENGTH, flags, (struct sockaddr *) &client_addr, &client_addr_len)) > 0){
		mut.lock();
		time_t now_time = chrono::system_clock::to_time_t(chrono::system_clock::now());
		cout << inet_ntoa(client_addr.sin_addr) << " @ " << ctime(&now_time);
		
		// Filter out the message or print it
		message = (char*) malloc(rlen);
		memcpy(message, buffer, rlen);
		if(strncmp(message, handshake, strlen(handshake)) == 0){
			cout << "I FOUND YOU!" << endl;
			cout << endl;
		}
		else{
			// Proper JSON parsing
			receivedDocument.Parse(message).HasParseError();
			assert(receivedDocument.IsObject());

			assert(receivedDocument.HasMember("sensor"));
			assert(receivedDocument["sensor"].IsString());
			sensorName = receivedDocument["sensor"].GetString();

			//Receiving an array of values
			const Value& aGyro = receivedDocument["gyro"];
			const Value& aAcce = receivedDocument["accel"];
			const Value& aMagn = receivedDocument["magnet"];

			assert(aGyro.IsArray());
			assert(aGyro.Size() == 3);
			assert(aAcce.IsArray());
			assert(aAcce.Size() == 3);
			assert(aMagn.IsArray());
			assert(aMagn.Size() == 3);

			if(sensorRecords.count(sensorName) == 0){
				sensor_data newRecord;
				sensorRecords.emplace(sensorName, newRecord);
			}

			for (SizeType i = 0; i<aGyro.Size(); i++){
				sensorRecords[sensorName].gyro[i] 			= aGyro[i].GetDouble();
				sensorRecords[sensorName].accelerometer[i] 	= aAcce[i].GetDouble();
				sensorRecords[sensorName].magnetometer[i] 	= aMagn[i].GetDouble();
			}

			Integration_algorithms obj;
			int result = obj.squat_straight_back(sensorRecords[sensorName].gyro[1], sensorRecords[sensorName].accelerometer[0], sensorRecords[sensorName].accelerometer[3]);

			cout << "Sensor: " << sensorName << endl;
			cout << "Gyro: \t\t" << sensorRecords[sensorName].gyro[0] << "\t" << sensorRecords[sensorName].gyro[1] << "\t" << sensorRecords[sensorName].gyro [2] << endl;
			cout << "Accelerometer:  " << sensorRecords[sensorName].accelerometer[0] << "\t" << sensorRecords[sensorName].accelerometer[1] << "\t" << sensorRecords[sensorName].accelerometer [2] << endl;
			cout << "Gyro: \t\t" << sensorRecords[sensorName].magnetometer[0] << "\t" << sensorRecords[sensorName].magnetometer[1] << "\t" << sensorRecords[sensorName].magnetometer [2] << endl;
			cout << "Result: " << result << endl;
			cout << endl;

		}

		free(message);
		mut.unlock();
	}
	// Close the socket
	broadcast_thread.join();
	cout << "Broadcast thread has been stopped." << endl;
	close(fd);

	return 0;
}