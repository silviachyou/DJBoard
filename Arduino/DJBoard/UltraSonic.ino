#include <Ultrasonic.h>
#include <stdio.h>
#define Ultra_Sound_limit 90
#define CountLimit 10
#define delayTime 120

Ultrasonic ultrasonic(TRIGGER_PIN, ECHO_PIN);

bool isControlUltra = false;
bool isStart = false;
int changeSonicCount = 0;
unsigned long soundtime = 0;
float Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM );
float Ultra_Sound_cmMsec_Init = 0;

void checkUltraSound(){
  if( millis() - soundtime > delayTime ) {
    soundtime = millis();
    Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM ); 
//    Serial.println(Ultra_Sound_cmMsec); 
    if( Ultra_Sound_cmMsec < Ultra_Sound_limit && !isControlUltra ) { // first time hand in
      changeSonicCount = 0;
      Ultra_Sound_cmMsec_Init = Ultra_Sound_cmMsec;
//      Serial.print(playUltraSoundMusic_init_add_num);
//      Bluetooth.write(playUltraSoundMusic_init_add_num);
      isControlUltra = true;    
    }
    else if( Ultra_Sound_cmMsec < Ultra_Sound_limit && isControlUltra ) { // control height
      changeSonicCount ++;
      if( changeSonicCount > CountLimit ){
        isStart = true;
        float diff_hand_control = Ultra_Sound_cmMsec_Init;
        diff_hand_control = Ultra_Sound_cmMsec - diff_hand_control;
        char playUltraSoundMusic[80];
        strcpy(playUltraSoundMusic,playUltraSoundMusic_init);
        char s[80];
        char line[4]="\n";
        dtostrf(diff_hand_control,8, 2, s);
        strcat(playUltraSoundMusic,s);  
        strcat(playUltraSoundMusic,line); 
        Serial.print(playUltraSoundMusic); 
        Bluetooth.write(playUltraSoundMusic);     
      }
    }
    if(Ultra_Sound_cmMsec > Ultra_Sound_limit && isControlUltra && isStart) { // stop 
      changeSonicCount = 0;
      Serial.print(stopUltraSoundMusic);
      Bluetooth.write(stopUltraSoundMusic);
      isControlUltra = false;
      isStart = false;
    }
  }
}

