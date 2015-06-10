bool isBlack = false;
bool isMoving = false;

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
    duration = millis()-time;
    Serial.println(duration);
    time = millis();
    isBlack = false;
    if(!isMoving){
      Serial.println("Board Move");
      Bluetooth.write(playWheelMusic);
      isMoving = true;
    }
    
  }
  else if(IRVal==LOW) {  //isBlack
    digitalWrite(led,LOW);
    isBlack = true;
  }
  if(millis()-time>1000 && isMoving){
     // Serial.println("Board Stopped");
      Bluetooth.write(stopWheelMusic);
      isMoving = false;
  }  
}
