// A UDP server file that listens to all train-A-wear sensors and 
// provides updates to the phone app.
#include <cstring>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <unistd.h>
#include <iostream>

//Needed for figuring out machine IP address as per https://www.includehelp.com/c-programs/get-ip-address-in-linux.aspx
#include <sys/ioctl.h>
#include <net/if.h>

#include <thread>
#include <chrono>

#include <mutex>

#define BUFFER_LENGTH 1500
#define PORT 31415
#define BROADCAST_DELAY_S 45

using namespace std;

char*					handshake = "train-A-wear online\n";
mutex mut;

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
		sendto(socket_d, handshake, strlen(handshake), MSG_CONFIRM, (const struct sockaddr *) &multicast_addr, sizeof(multicast_addr));
		time_t now_time = chrono::system_clock::to_time_t(chrono::system_clock::now());
		mut.lock();
		cout << "Broadcast sent @ " << ctime(&now_time);
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
		int i;
		mut.lock();
		cout << inet_ntoa(client_addr.sin_addr) << " -> ";
		
		// Filter out the message or print it
		message = (char*) malloc(rlen);
		memcpy(message, buffer, rlen);
		if(strncmp(message, handshake, strlen(handshake)) == 0)
			cout << "I FOUND YOU!" << endl;
		else
			cout << message;
		free(message);
		mut.unlock();
	}
	// Close the socket
	broadcast_thread.join();
	cout << "Broadcast thread has been stopped." << endl;
	close(fd);

	return 0;
}