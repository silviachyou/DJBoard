
unsigned long knocktime=0;

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

   if(millis() - knocktime > 500) {
            Serial.print("SENSOR1:");        
       Serial.println(sensorReading1);        
                       Serial.print("SENSOR1:");        
       Serial.println(sensorReading2);   
                       Serial.print("SENSOR1:");        
       Serial.println(sensorReading3);   
         
       if(sensorReading1 >= 40) {
         Serial.println("Knock Front");
         Bluetooth.write(knockFront);
       }
       if(sensorReading2 >= 40) {
         Serial.println("Knock Mid");
         Bluetooth.write(knockMid);
       }
       if(sensorReading3 >= 40) {
         Serial.println("Knock Back");
         Bluetooth.write(knockBack);
       }
       
       knocktime = millis();
   }
}

