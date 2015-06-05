//#include <Ultrasonic.h>
#include <stdio.h>
#define TRIGGER_PIN  5
#define ECHO_PIN     6
#define Ultra_Sound_limit 150

float Ultra_Sound_cmMsec;
float Ultra_Sound_cmMsec_Init;
const char playUltraSoundMusic_init[] = "ultrasound:";
const char playUltraSoundMusic_init_add_num[] = "ultrasound:   0.00\n";
const char stopUltraSoundMusic[] = "stop_ultrasound";
bool isControlUltra;
const int inter_time = 100;

//Ultrasonic ultrasonic(TRIGGER_PIN, ECHO_PIN);

void setup()
{
  Serial.begin(9600);
  Ultra_Sound_cmMsec = convertUltraSonic(); // 計算距離，單位: 公分
//  Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM ); 
  Ultra_Sound_cmMsec_Init = 0;
  isControlUltra = false;
}

void loop()
{
  checkUltraSound();
  delay(200);
}

void checkUltraSound(){
  Ultra_Sound_cmMsec = convertUltraSonic(); // 計算距離，單位: 公分
//  Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM ); // 計算距離，單位: 公分   Serial.println(Ultra_Sound_cmMsec);

  if( Ultra_Sound_cmMsec < Ultra_Sound_limit && !isControlUltra ){
      Serial.println("Hand Start!!");
      Ultra_Sound_cmMsec_Init = Ultra_Sound_cmMsec;
      Serial.print(playUltraSoundMusic_init_add_num);
     // Bluetooth.write(playUltraSoundMusic_init_add_num);
      isControlUltra = true;    
  }
  else if( Ultra_Sound_cmMsec < Ultra_Sound_limit && isControlUltra ) { // 手還在 => cm < 300
    float diff_hand_control = Ultra_Sound_cmMsec_Init;
    diff_hand_control = Ultra_Sound_cmMsec - diff_hand_control;
    char playUltraSoundMusic[80];
    strcpy(playUltraSoundMusic,playUltraSoundMusic_init);
    char s[80];
    dtostrf(diff_hand_control,8, 2, s);
    char line[4]="\n";
    strcat(playUltraSoundMusic,s); 
    strcat(playUltraSoundMusic,line); 
    Serial.print(playUltraSoundMusic);    
 //   Bluetooth.write(playUltraSoundMusic);     
  }
  if(Ultra_Sound_cmMsec > Ultra_Sound_limit && isControlUltra ) { // 一陣子沒人接觸 => 1s && cm > 300  
    Serial.print(stopUltraSoundMusic);
  //  Bluetooth.write(stopUltraSoundMusic);
    isControlUltra = false;
  }
}
float convertUltraSonic(){
  float duration, distance;
  digitalWrite(TRIGGER_PIN, HIGH);
  delayMicroseconds(1000);
  digitalWrite(TRIGGER_PIN, LOW);
  duration = pulseIn (ECHO_PIN, HIGH);
  distance = (duration/2)/29;
  Serial.println(distance);
  return distance;
}
