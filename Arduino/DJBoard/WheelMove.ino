bool isBlack = false;
bool isMoving = false;
const int wheelLen = 15; //cm
double v = 0;
int dis = 0;
const char eol[3]="\0";

unsigned long time, duration;

void wheelMoveSetup() {
  time = millis();
}

void checkWheelMove(){
  
  IRVal=digitalRead(iRSensorPin);

  //HIGH: white
  //LOW: black
 
  if(IRVal==HIGH && isBlack){  //first seen white
     digitalWrite(led,HIGH);
     
     isBlack = false;
     dis = dis + wheelLen;
     Serial.print("distance: ");
     Serial.println(dis);
     char dString[80]="dis= ";
     char sofd[20];
     
     dtostrf(dis, 8, 0, sofd);
     strcat(dString, sofd);  
     strcat(dString, eol);
     Bluetooth.write(dString);

     if(!isMoving){
      Serial.println("Board starts moving");
      Bluetooth.write(playWheelMusic);
      isMoving = true;
    }
    else{
      Serial.println("Board keeps moving");
      duration = millis()-time;
      Serial.print("duration: ");
      Serial.println(duration);
      v = (double)wheelLen*10/(double)duration;
      Serial.print("velocity: ");
      Serial.println(v);
      char vString[80]="v= ";
      char sofv[20];
      
      dtostrf(v, 6, 4, sofv);
      strcat(vString, sofv);  
      strcat(vString, eol);
      Bluetooth.write(vString);
    }
    time = millis();
  }
  else if(IRVal==LOW) {  //isBlack
    digitalWrite(led,LOW);
    isBlack = true;
  }
  if(millis()-time>1000 && isMoving){
     // Serial.println("Board Stopped");
      Bluetooth.write(stopWheelMusic);
      isMoving = false;
      v = 0;
  }  
}
