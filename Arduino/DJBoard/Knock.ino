
unsigned long knocktime=0;

bool knockStatus1 = 0;
bool knockStatus2 = 0;
bool knockStatus3 = 0;

void checkKnock(){
   int sensorReading1 = 0; 
   int sensorReading2 = 0;
   int sensorReading3 = 0;
   sensorReading1 = digitalRead(knockSensor1);
   sensorReading2 = digitalRead(knockSensor2);
   sensorReading3 = digitalRead(knockSensor3);

   if(millis() - knocktime > 1000) {
     if(knockStatus1 != sensorReading1) {
       knocktime = millis();
       knockStatus1 = sensorReading1;
       if(knockStatus1 == 0) {
         Serial.println("Knock Front");
         Bluetooth.write(knockFront);
       }
     }
     if(knockStatus2 != sensorReading2) {
       knocktime = millis();
       knockStatus2 = sensorReading2;
       if(knockStatus2 == 0) {
         Serial.println("Knock Mid");
         Bluetooth.write(knockMid);
       }
     }
     if(knockStatus3 != sensorReading3) {
       knocktime = millis();
       knockStatus3 = sensorReading3;
       if(knockStatus3 == 0) {
         Serial.println("Knock Back");
         Bluetooth.write(knockBack);
       }
     }
   }
}
