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

//Headers for multi-threading as per https://dzone.com/articles/parallel-tcpip-socket-server-with-multi-threading
#include <cstdlib>
#include <netinet/in.h>
#include <fcntl.h>
#include <pthread.h>

#define BUFFER_LENGTH 1500
#define PORT 31415

using namespace std;



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

	fd = socket(AF_INET, SOCK_DGRAM, 0);
	ifr.ifr_addr.sa_family = AF_INET;
	memcpy(ifr.ifr_name, "wlan0", IFNAMSIZ-1);
	ioctl(fd, SIOCGIFADDR, &ifr);
	close(fd);

	strcpy(ip_address,inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr));
	cout << "Server IP Address is: " << ip_address << endl;


	//Network IP discovery
	int broadcast = 1;

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

	
	// Retrieve the data in the packet
	client_addr_len = sizeof(struct sockaddr_storage);
	while((rlen = recvfrom(fd, buffer, BUFFER_LENGTH, flags, (struct sockaddr *) &client_addr, &client_addr_len)) > 0){
		int i;
		cout << inet_ntoa(client_addr.sin_addr) << " -> ";
		
		// Filter out the message or print it
		message = (char*) malloc(rlen-1);
		memcpy(message, buffer, rlen-1);
		if(strcmp(message, "train-A-wear online") == 0)
			cout << "I FOUND YOU!" << endl;
		else
			cout << message << endl;
		free(message);	}

	// Close the socket
	close(fd);

	return 0;
}