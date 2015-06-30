
unsigned long knocktime = 0, stickup_time = 0, checkyield_time = 0;
unsigned long board180_time = 0;

bool knockStatus1 = 0;
bool knockStatus2 = 0;
bool knockStatus3 = 0;

void checkKnock(){
   int sensorReading1 = 0;
   int sensorReading2 = 0;
   int sensorReading3 = 0;
   sensorReading1 = analogRead(knockSensor1);
   sensorReading2 = analogRead(knockSensor2);
   sensorReading3 = analogRead(knockSensor3);
   int yieldDiff = 0, yieldPrev = 0;

  /*
   printlog(DEV, "knock : ");
   printlog(DEV, sensorReading1);
   printlog(DEV, " ");
   printlog(DEV, sensorReading2);
   printlog(DEV, " ");
   printlogln(DEV, sensorReading3);
*/
  //printlog(DEV, "gyro[2]: ");
  //printlogln(DEV, gyro[2]);
  

   if(millis() - stickup_time > 1000){


      if(sensorReading2 > 18 && gyro[2] >= 1500){
        printlogln(BT, stickUpLeft);
         printlogln(DEV, "stick up!");
        stickup_time = millis();
      }
      if(sensorReading2 > 18 && gyro[2] <= -1500){
        printlogln(BT, stickUpRight);
         printlogln(DEV, "stick up!");
        stickup_time = millis();
      }
      
   }


//=== 180
    if(millis() - board180_time > 1000){
   //   printlog(DEV, "sensor3: "); 
   //   printlog(DEV, sensorReading3);
   //   printlog(DEV, " , gyro: ");
   //   printlogln(DEV, gyro[2]);

      if(sensorReading1 < 10 && sensorReading2 < 10 && gyro[2] >= 1500){
        printlogln(BT, boardturn180);
        printlog(DEV, "board turn 180\n");
        board180_time = millis();
      }
      if(sensorReading1 < 10 && sensorReading2 < 10 && gyro[2] <= -1500){
        printlogln(BT, boardturn180);
        printlog(DEV, "board turn 180\n");
        board180_time = millis();
      }
   }

//===

   // if(millis() - knocktime > 500) {

   //     if(sensorReading1 >= 40) {
   //       //printlogln(DEV, sensorReading1);
   //       printlogln(BT, knockFront);
   //     }
   //     if(sensorReading2 >= 40) {
   //       //printlogln(DEV, sensorReading2);
   //       printlogln(BT, knockMid);
   //     }
   //     if(sensorReading3 >= 40) {
   //       //printlogln(DEV, sensorReading3);
   //       printlogln(BT, knockBack);
   //     }

   //     knocktime = millis();
   // }

}

