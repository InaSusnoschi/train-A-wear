// A UDP server file that listens to all train-A-wear sensors and 
// provides updates to the phone app.
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

#define BUFFER_LENGTH 1500
#define PORT 31415
#define BROADCAST_DELAY_S 45

using namespace std;
using namespace rapidjson;

const char*	handshake = "train-A-wear online\n";
mutex mut;


struct sensor_data {
	double 	gyro[3];
	double 	accelerometer[3];
	double 	magnetometer[3];
};

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

	//Fillout the server information
	multicast_addr.sin_family 		= AF_INET;
	multicast_addr.sin_port 		= htons(PORT);
	multicast_addr.sin_addr.s_addr 	= inet_addr("255.255.255.255");


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
	
	int  					fd;
	ssize_t					rlen;
	struct sockaddr_in 		addr, conn_addr;
	socklen_t 				conn_addr_len;
	char 					buffer[BUFFER_LENGTH];
	int 					flags = 0;

	// Needed for UDP connection as per https:linux.die.net/man/3/getaddrinfo
	struct sockaddr_in 		client_addr;
	socklen_t 				client_addr_len;
	char*					message;

	// Determining local IP
	char 			ip_address[15];
	struct ifreq 	ifr;

	//Network IP discovery
	int broadcast = 1;

	//JSON transmission variables
	Document receivedDocument;
	map<string, sensor_data> sensorRecords;
	string 	sensorName;


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

			cout << "Sensor: " << sensorName << endl;
			cout << "Gyro: \t\t" << sensorRecords[sensorName].gyro[0] << "\t" << sensorRecords[sensorName].gyro[1] << "\t" << sensorRecords[sensorName].gyro [2] << endl;
			cout << "Accelerometer:  " << sensorRecords[sensorName].accelerometer[0] << "\t" << sensorRecords[sensorName].accelerometer[1] << "\t" << sensorRecords[sensorName].accelerometer [2] << endl;
			cout << "Gyro: \t\t" << sensorRecords[sensorName].magnetometer[0] << "\t" << sensorRecords[sensorName].magnetometer[1] << "\t" << sensorRecords[sensorName].magnetometer [2] << endl;
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