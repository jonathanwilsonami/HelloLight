# HelloLight
A simple hello world for an Android app and IoT device communication 

# Demo
https://youtube.com/shorts/62Dc0xWrAro?feature=share

# Diagram
![Diagram](HelloLight)

# Technology Used
- Mega2560 Arduino Board
- HC-06 (Bluetooth module)
- LED light
- Wires
- Development env was Ubuntu Linux and Android Samsung A51
-  

# Setup & Notes
- Setup Udev rules -
  - Can use lsusb - list USB devices connected to system. Also contains the product and vender IDs
  - Setting up udev rules for an Arduino Mega 2560 on a Linux system can help ensure that the device is consistently recognized and assigned the same device file every time it's plugged in. This can be especially useful if you're working with multiple USB devices.
  - /etc/udev/rules.d/99-arduino.rules
  - SUBSYSTEM=="tty", ATTRS{idVendor}=="2341", ATTRS{idProduct}=="0042", MODE="0666", GROUP="dialout", SYMLINK+="arduino_mega"
  - sudo udevadm control --reload (restarts the rule) 
- Arduino Settings & Notes:
  - Make sure you have the right Board selected in the settings 
  - When uploading code to the Arduino remove the RX and TX pins else there could be an interference with data transfer
- Add user permissions in the Manifest (See Manifest)
- Use "hcitool scan" to get the module MAC address.
