/** Software for train-A-wear sensor microcontrollers.
 *  
 *  Version:    0.3
 *  Maintainer: Borko 
 *  Contacts:   https://github.com/InaSusnoschi/train-A-wear
 *  Target mCU: ESP8266
 *  
 */

// Doc reference for ticker stuff:  https://arduino-esp8266.readthedocs.io/en/latest/libraries.html
// Doc reference for UDP packets:   https://arduino-esp8266.readthedocs.io/en/latest/esp8266wifi/udp-class.html
// Reference for storing remoteIP as IPAddress (search for IPAddress): https://tttapa.github.io/ESP8266/Chap14%20-%20WebSocket.html


#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <WiFiUDP.h>
#include <Ticker.h>
#include <ArduinoJson.h>


#define UDP_PORT          31415
#define ROUTINE_PERIOD_MS 50 

Ticker timedRoutines;

// WiFi Variables
ESP8266WiFiMulti wifiManager;
const char *ssid_1 = "x";
const char *pass_1 = "x";
const char *ssid_2 = "rank510iot";
const char *pass_2 = "raspberry";

const char *handshake = "train-A-wear online\n";

// UDP Variables
WiFiUDP   UDP;
IPAddress serverIP;
bool      gotServerIP;

// Programme variables
char newData[UDP_TX_PACKET_MAX_SIZE];
char receivedData[UDP_TX_PACKET_MAX_SIZE];


const size_t capacity = JSON_ARRAY_SIZE(3) + JSON_ARRAY_SIZE(3) + JSON_ARRAY_SIZE(3) + JSON_OBJECT_SIZE(4);
StaticJsonDocument<capacity> doc;
char serialOutput[128];

/**
 *  A function that polls the sensor over I2C. It updates the 
 *  existing programme variables with the results, instead of returning them.
 *  
 * param:   None
 * returns: void
 */

void pollSensor(){
  
}

/**
 * Simple function that collates all the sensor data and turns it into 
 * the format from the server so it can be transmitted.
 * 
 * param:   None
 * returns: char []
 */

void makePayload(char* dataBuffer){
  //Testing JSON serialization
  doc["sensor"] = "tAw-1";
  JsonArray gyro    = doc.createNestedArray("gyro");
  JsonArray accel   = doc.createNestedArray("accel");
  JsonArray magnet  = doc.createNestedArray("magnet");

  gyro.add(3.248);
  gyro.add(7.8454);
  gyro.add(-6.584);

  accel.add(0.007);
  accel.add(4);
  accel.add(-8.254);

  magnet.add(1.874);
  magnet.add(-0.004);
  magnet.add(-5);

  serializeJson(doc, serialOutput);
  Serial.printf("%s\n", serialOutput);
  //JSON done
}

/**
 * A function that takes a payload and transmits it as UDP packet to the master server.
 * 
 * param:   char[] payload
 * returns: void
 */

void sendUDP(char* payload){
  UDP.beginPacket(serverIP, UDP_PORT);
  UDP.write(payload);
  UDP.endPacket();
}

/** 
 *  A function that reads sensor data over I2C and transmits it over UDP to the server.
 *  
 *  param:    none
 *  returns:  void
 */

void readAndTransmit(){
  pollSensor();
  makePayload(newData);
  sendUDP(newData);
}


void setup() {
  Serial.begin(74880);
  Serial.println();

  gotServerIP = false;

  // Add known APs to the manager
  wifiManager.addAP(ssid_1, pass_1);
  wifiManager.addAP(ssid_2, pass_2);

  Serial.println("Connecting to known WiFi APs.");

  // Keep trying to connect to the strongest WiFi network nearby
  while (wifiManager.run() != WL_CONNECTED){
    delay(500);
    Serial.print('.');
  }
  Serial.println();

  // Print out network name and local IP address
  Serial.print("Connected to: ");
  Serial.println(WiFi.SSID());
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
  Serial.print("Port: ");
  Serial.println(UDP_PORT);
   
  // Start listening to UDP port
  UDP.begin(UDP_PORT);
  Serial.println("Listening for UDP packets.");

  makePayload(newData);
 
  while (!gotServerIP){
    int packetSize = UDP.parsePacket();
    if (packetSize){
      // Read incoming UDP packets
      Serial.printf("Received %d bytes from %s, port %d\n", packetSize, UDP.remoteIP().toString().c_str(), UDP.remotePort());
      int len = UDP.read(receivedData, UDP_TX_PACKET_MAX_SIZE);
      if (len > 0)
      {
        receivedData[len] = 0;
      }
      Serial.printf("UDP packet contents: %s", receivedData);
      if (strncmp(receivedData, handshake, strlen(handshake)) == 0){
        serverIP = UDP.remoteIP();
        gotServerIP = true;
        Serial.printf("train-A-wear server discovered at: %s\n", serverIP.toString().c_str());
      }
    }
  }

  sendUDP(serialOutput);

  // Initialises a ticker that calls the routine every so many ms
  //timedRoutines.attach_ms(ROUTINE_PERIOD_MS, readAndTransmit);
}

void loop() {
  // put your main code here, to run repeatedly:

}
