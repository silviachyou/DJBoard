
unsigned long knocktime = 0, stickup_time = 0, checkyield_time = 0;

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

//   printlog(DEV, "knock : ");
//   printlog(DEV, sensorReading1);
//   printlog(DEV, " ");
//   printlog(DEV, sensorReading2);
//   printlog(DEV, " ");
//   printlogln(DEV, sensorReading3);


   if(millis() - stickup_time > 1000){
     printlog(DEV, "sensor3: "); 
     printlog(DEV, sensorReading3);   
      printlog(DEV, " , gyro: ");
      printlogln(DEV, gyro[2]);

      if(sensorReading3 > 190 && gyro[2] >= 1500){
        printlogln(BT, stickUpLeft);
        stickup_time = millis();
      }
      if(sensorReading3 > 190 && gyro[2] <= -1500){
        printlogln(BT, stickUpRight);
        stickup_time = millis();
      }
      
   }

}

