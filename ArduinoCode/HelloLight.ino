#include <SoftwareSerial.h>

// Define the LED pin and Bluetooth module pins
const int ledPin = 13; 
const int bluetoothTx = 11; 
const int bluetoothRx = 10; 

// Set up a new serial port
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

void setup() {
 
 pinMode(ledPin, OUTPUT);

 Serial.begin(9600);
 
//   Start the software serial port for Bluetooth communication
 bluetooth.begin(9600);
 Serial.println("Bluetooth connected, waiting for commands...");
}

void loop() {
 
 // Check if data is available to read from the Bluetooth module
 if (bluetooth.available()) {
   char receivedChar = bluetooth.read();
   
   // Turn the LED on or off based on the received command
   if (receivedChar == '1') {
     digitalWrite(ledPin, HIGH);
     Serial.println("LED turned ON");
   } else if (receivedChar == '0') {
     digitalWrite(ledPin, LOW);
     Serial.println("LED turned OFF");
   }
 } 
}
