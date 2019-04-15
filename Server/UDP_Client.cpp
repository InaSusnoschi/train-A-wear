// Simple UDP Client for testing connectivity to the train-A-wear server

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

//Needed for figuring out machine IP address as per https://www.includehelp.com/c-programs/get-ip-address-in-linux.aspx
#include <sys/ioctl.h>
#include <net/if.h>


#define MAXLINE 1500
#define PORT 31415
#define MAXINPUT 128


int main(int argc, char* argv[]) {
	int 				fd;
	char 				buffer[MAXLINE];
	char 				*hello = "Hello from the other side of the internet!\n";
	struct sockaddr_in 	server_addr;
	char 				str[MAXINPUT]; //Array for reading stdin input
	char 				*destination;

	// Determine local IP by callin ioctl
	char 			ip_address[15];
	struct ifreq 	ifr;
	int broadcast = 1;

	fd = socket(AF_INET, SOCK_DGRAM, 0); 
	ifr.ifr_addr.sa_family = AF_INET;
	memcpy(ifr.ifr_name, "wlan0", IFNAMSIZ-1);
	ioctl(fd, SIOCGIFADDR, &ifr);
	close(fd);

	strcpy(ip_address,inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr));
	printf("Client IP Address is: %s\n", ip_address);

	// If no IP address is passed as an argument, the client defaults to localhost
	if(argc==2){
		destination = argv[1];
	} else{
		destination = "127.0.0.1";
	}

	// Create the socket
	if((fd = socket(AF_INET, SOCK_DGRAM, 0)) == -1){
		perror("Unable to create the UDP socket.");
		return 1;
	}

	if(setsockopt(fd,SOL_SOCKET,SO_BROADCAST,&broadcast,sizeof(broadcast)) < 0){
        perror("Error in setting Broadcast option");
        close(fd);
        return 3;
    }

	// Fillout the server information
	server_addr.sin_family 		= AF_INET;
	server_addr.sin_port 		= htons(PORT);
	server_addr.sin_addr.s_addr = inet_addr(destination); 

	// Send data to the server
	while(fgets(str, MAXINPUT, stdin) != NULL){
		sendto(fd, str, strlen(str), MSG_CONFIRM, (const struct sockaddr *) &server_addr, sizeof(server_addr));
	}
	
	printf("Greeting has been sent :)\n");

	// Close the socket
	close(fd);
	return 0;
}
