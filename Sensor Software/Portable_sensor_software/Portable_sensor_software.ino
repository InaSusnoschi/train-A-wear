/** @brief Software for train-A-wear sensor microcontrollers.
 *  
 *  @version:    0.5
 *  @author Borislav Gachev 
 *  @date April 2019
 *  @contact:   https://github.com/InaSusnoschi/train-A-wear
 *  @Target mCU: ESP8266
 *  @file
 */

// Doc reference for ticker stuff:  https://arduino-esp8266.readthedocs.io/en/latest/libraries.html
// Doc reference for UDP packets:   https://arduino-esp8266.readthedocs.io/en/latest/esp8266wifi/udp-class.html
// Reference for storing remoteIP as IPAddress (search for IPAddress): https://tttapa.github.io/ESP8266/Chap14%20-%20WebSocket.html


#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <WiFiUDP.h>
#include <Ticker.h>
#include <ArduinoJson.h>
#include <Wire.h>
#include <SPI.h>
#include <SparkFunLSM9DS1.h>

/** Define target UDP port of the server for transmission **/
#define UDP_PORT              31415
/** Period between execution of the timer routine **/
#define ROUTINE_PERIOD_MS     500
/** Maximum connection attempts before the ESP reboots **/
#define MAX_CONNECT_ATTEMPTS  90
/** Name that the sensor would use to identify itself on the server **/
#define SENSOR_NAME           "tAw-1"
/** Enables printing over serial connection for debug purposes **/
#define PRINT_DEBUG           1

Ticker timedRoutines;

// WiFi Variables
ESP8266WiFiMulti wifiManager;
const char *ssid_1 = "x";
const char *pass_1 = "x";
const char *ssid_2 = "rank510iot";
const char *pass_2 = "raspberry";
const char *ssid_3 = "VM7547082 - 2G";
const char *pass_3 = "HobbitTree92";

const char *handshake = "train-A-wear online\n";
int         connectAttempts = 0;


// UDP Variables
WiFiUDP   UDP;
IPAddress serverIP;
bool      gotServerIP;

// Programme variables
char receivedData[UDP_TX_PACKET_MAX_SIZE];

// IMU LSM9DS1
LSM9DS1 imu;
// SDO_XM and SDO_G are both pulled high, so our addresses are:
#define LSM9DS1_M  0x1E // Would be 0x1C if SDO_M is LOW   0x1E if HIGH
#define LSM9DS1_AG  0x6B // Would be 0x6A if SDO_AG is LOW 0x6B if HIGH

const size_t capacity = JSON_ARRAY_SIZE(3) + JSON_ARRAY_SIZE(3) + JSON_ARRAY_SIZE(3) + JSON_OBJECT_SIZE(4);
char * serialOutput;

/**
 * A function that creates JSON string with latest sensor data.
 * Simple function that collates all the sensor data and turns it into 
 * JSON format so it can be transmitted to the server. It uses the global 
 * variable serialOutput to print
 * 
 * @see serialOutput
 */

void makePayload(){
  StaticJsonDocument<capacity> doc;
  doc["sensor"] = SENSOR_NAME;
  
  JsonArray gyro    = doc.createNestedArray("gyro");
  JsonArray accel   = doc.createNestedArray("accel");
  JsonArray magnet  = doc.createNestedArray("magnet");

  gyro.add(imu.calcGyro(imu.gx));
  gyro.add(imu.calcGyro(imu.gy));
  gyro.add(imu.calcGyro(imu.gz));

  accel.add(imu.calcAccel(imu.ax));
  accel.add(imu.calcAccel(imu.ay));
  accel.add(imu.calcAccel(imu.az));

  magnet.add(imu.calcMag(imu.mx));
  magnet.add(imu.calcMag(imu.mx));
  magnet.add(imu.calcMag(imu.mx));

  serialOutput = (char *) malloc(measureJson(doc) + 1);
  serializeJson(doc, serialOutput, measureJson(doc) +1 );

#ifdef PRINT_DEBUG
  Serial.printf("%s\n", serialOutput);
#endif

  free(serialOutput);
}

/**
 * A function that transmits a char pointer to to the master server.
 * 
 * @param payload A char * pointer to the data to be sent.
 * 
 */

void sendUDP(char* payload){
  UDP.beginPacket(serverIP, UDP_PORT);
  UDP.write(payload);
  UDP.endPacket();
}

/** 
 *  A function that reads sensor data over I2C and transmits it over UDP to the server.
 *  
 */

void readAndTransmit(){
  makePayload();
  sendUDP(serialOutput);
}


void setup() {

#ifdef PRINT_DEBUG
  Serial.begin(74880);
  Serial.println();
#endif

  gotServerIP = false;

  // Add known APs to the manager
  wifiManager.addAP(ssid_1, pass_1);
  wifiManager.addAP(ssid_2, pass_2);
  wifiManager.addAP(ssid_3, pass_3);

#ifdef PRINT_DEBUG
  Serial.println("Connecting to known WiFi APs.");
#endif

  // Keep trying to connect to the strongest WiFi network nearby.
  // Reboot after maximum connection attempts.
  while (wifiManager.run() != WL_CONNECTED){
    if(connectAttempts > MAX_CONNECT_ATTEMPTS){

#ifdef PRINT_DEBUG
      Serial.println("\nRebooting ESP8266 now!");
#endif

      ESP.restart();
    }
    delay(500);

#ifdef PRINT_DEBUG
    Serial.print('.');
#endif

    connectAttempts++;
  }

#ifdef PRINT_DEBUG
  Serial.println();

  // Print out network name and local IP address
  Serial.print("Connected to: ");
  Serial.println(WiFi.SSID());
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
  Serial.print("Port: ");
  Serial.println(UDP_PORT);
#endif

  // Start listening to UDP port
  UDP.begin(UDP_PORT);

#ifdef PRINT_DEBUG
  Serial.println("Listening for UDP packets.");
#endif

  makePayload();

  while (!gotServerIP){
    int packetSize = UDP.parsePacket();
    if (packetSize){
      // Read incoming UDP packets

#ifdef PRINT_DEBUG
      Serial.printf("Received %d bytes from %s, port %d\n", packetSize, UDP.remoteIP().toString().c_str(), UDP.remotePort());
#endif

      int len = UDP.read(receivedData, UDP_TX_PACKET_MAX_SIZE);
      if (len > 0)
      {
        receivedData[len] = 0;
      }

#ifdef PRINT_DEBUG
      Serial.printf("UDP packet contents: %s", receivedData);
#endif

      if (strncmp(receivedData, handshake, strlen(handshake)) == 0){
        serverIP = UDP.remoteIP();
        gotServerIP = true;

#ifdef PRINT_DEBUG
        Serial.printf("train-A-wear server discovered at: %s\n", serverIP.toString().c_str());
#endif

      }
    }
  }


  imu.settings.device.commInterface = IMU_MODE_I2C;
  imu.settings.device.mAddress      = LSM9DS1_M;
  imu.settings.device.agAddress     = LSM9DS1_AG;

  if(!imu.begin()){
    sendUDP("Failed to communicate with LSM9DS1. Check existing connections and I2C addresses!\n");
  }

  // Initialises a ticker that calls the routine every so many ms
  timedRoutines.attach_ms(ROUTINE_PERIOD_MS, readAndTransmit);
}

void loop() {
  // put your main code here, to run repeatedly:

}
