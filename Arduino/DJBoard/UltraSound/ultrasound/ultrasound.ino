#include <Ultrasonic.h>
#include <stdio.h>
#define TRIGGER_PIN  5
#define ECHO_PIN     6
#define Ultra_Sound_limit 100

float Ultra_Sound_cmMsec;
float Ultra_Sound_cmMsec_Init;
const char playUltraSoundMusic_init[] = "ultrasound:";
const char playUltraSoundMusic_init_add_num[] = "ultrasound:   0.00\n";
const char stopUltraSoundMusic[] = "stop_ultrasound";
bool isControlUltra;

Ultrasonic ultrasonic(TRIGGER_PIN, ECHO_PIN);

void setup()
{
  Serial.begin(9600);
  Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM ); 
  Ultra_Sound_cmMsec_Init = 0;
  isControlUltra = false;
}

void loop()
{
  checkUltraSound();
}

void checkUltraSound(){
  Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM ); // 計算距離，單位: 公分
  if( Ultra_Sound_cmMsec < Ultra_Sound_limit && !isControlUltra ){
      Serial.print("Hand Start!!");
      Ultra_Sound_cmMsec_Init = Ultra_Sound_cmMsec;
      Serial.println(Ultra_Sound_cmMsec_Init);
      Bluetooth.write(playUltraSoundMusic_init_add_num);
      isControlUltra = true;    
  }
  else if( Ultra_Sound_cmMsec < Ultra_Sound_limit && isControlUltra ) { // 手還在 => cm < 300
    float diff_hand_control = Ultra_Sound_cmMsec_Init;
    Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM ); // 計算距離，單位: 公分
    diff_hand_control = Ultra_Sound_cmMsec - diff_hand_control;
    char playUltraSoundMusic[80];
    strcpy(playUltraSoundMusic,playUltraSoundMusic_init);
    char s[80];
    dtostrf(diff_hand_control,8, 2, s);
    strcat(playUltraSoundMusic,s);  
    Serial.println(playUltraSoundMusic);    
    Bluetooth.write(playUltraSoundMusic);     
  }
  if(Ultra_Sound_cmMsec > Ultra_Sound_limit && isControlUltra ) { // 一陣子沒人接觸 => 1s && cm > 300  
    Serial.println("Board Stopped");
    Bluetooth.write(stopUltraSoundMusic);
    isControlUltra = false;
  }
}
