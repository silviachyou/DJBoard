#include <SoftwareSerial.h>
const char playWheelMusic = 'p';
const char stopWheelMusic = 's';

int rx = 10;
int tx = 11;
int led=13;
int iRSensorPin=3;
int IRVal;
bool isBlack;
unsigned long time, duration;
bool isMoving;
SoftwareSerial Bluetooth(rx,tx);//定義PIN10及PIN11分別為RX及TX腳位

void setup()
{
  Serial.begin(9600);
  Bluetooth.begin(115200);  //Baud Rate: 9600
  pinMode(led,OUTPUT);
  pinMode(iRSensorPin,INPUT);
  time = millis();
  isBlack = false;
  isMoving = false;
}

void loop()
{
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
      Serial.println("Board Stopped");
      Bluetooth.write(stopWheelMusic);
      isMoving = false;
  }
}


