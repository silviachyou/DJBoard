

bool isControlUltra = false;

float Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM );
float Ultra_Sound_cmMsec_Init = 0;


void checkUltraSound(){
  Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM ); 
//  Serial.println(Ultra_Sound_cmMsec); 
  if( Ultra_Sound_cmMsec < Ultra_Sound_limit && !isControlUltra ) { // first time hand in
     // Serial.print("Hand Start!!");
      Ultra_Sound_cmMsec_Init = Ultra_Sound_cmMsec;
      Serial.print(playUltraSoundMusic_init_add_num);
      Bluetooth.write(playUltraSoundMusic_init_add_num);
      isControlUltra = true;    
  }
  else if( Ultra_Sound_cmMsec < Ultra_Sound_limit && isControlUltra ) { // control height
    float diff_hand_control = Ultra_Sound_cmMsec_Init;
   // Ultra_Sound_cmMsec = ultrasonic.convert( ultrasonic.timing() , Ultrasonic::CM ); 
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
  if(Ultra_Sound_cmMsec > Ultra_Sound_limit && isControlUltra ) { // stop 
    Serial.print(stopUltraSoundMusic);
    Bluetooth.write(stopUltraSoundMusic);
    isControlUltra = false;
  }
}

