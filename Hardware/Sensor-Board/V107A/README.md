## IMU Sensor Board V1.07A

Changes to previous version:

* This board is now based on ESP-WROOM-O2, not S2!!
* Fixed incorret IO15, IO2, IO0 pin connections for booting in flash mode
* Moved inductor to the top of the board
* Removed pins for programming the microcontroller while soldered on the board. Programming has to be done prior to soldering on the PCB
* Added powerplane to the bottom of the PCB
* Correct pull up on the Reset pin which is active low

Bill of Materials is included in the accompanying txt file in the folder. Jumper 2 is where a standard LiPo battery can be connected to the PCB to power it up. The board features smart battery charging capabilities for LiPo batteries.
