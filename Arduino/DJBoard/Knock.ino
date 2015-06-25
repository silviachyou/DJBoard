
unsigned long knocktime = 0, stickup_time = 0;

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

   printlog(DEV, "knock :");
   printlog(DEV, sensorReading1);
   printlog(DEV, " ");
   printlog(DEV, sensorReading2);
   printlog(DEV, " ");
   printlogln(DEV, sensorReading3);

   if(millis() - stickup_time > 1000){
      if(sensorReading1 > 190){
        printlogln(BT, stickUp);
      }
      stickup_time = millis();
   }

   if(millis() - knocktime > 500) {
//            Serial.print("SENSOR1:");
//       Serial.println(sensorReading1);
//                       Serial.print("SENSOR1:");
//       Serial.println(sensorReading2);
//                       Serial.print("SENSOR1:");
//       Serial.println(sensorReading3);

       if(sensorReading1 >= 40) {
         //printlogln(DEV, sensorReading1);
         printlogln(BT, knockFront);
       }
       if(sensorReading2 >= 40) {
         //printlogln(DEV, sensorReading2);
         printlogln(BT, knockMid);
       }
       if(sensorReading3 >= 40) {
         //printlogln(DEV, sensorReading3);
         printlogln(BT, knockBack);
       }

       knocktime = millis();
   }
}

