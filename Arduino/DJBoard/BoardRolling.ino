
float previousRoll;
int previousDrt=1;
int count=0, s=0, r=0;
unsigned long rolltime=0;

void boardRollingSetup() {
  
  previousRoll=roll;
  rolltime=millis();
}

void checkRolling() {
  if(millis()-rolltime > 100){
    rolling();
    rolltime=millis();
  }
}

void rolling(){
  //Serial.println("rolling");
 /* Serial.print("roll:");
  Serial.print(TO_DEG(roll));
  Serial.print(", previousRoll:");
  Serial.print(TO_DEG(previousRoll));
*/
  float diff=roll-previousRoll;
/*
  Serial.print(", diff:");
  Serial.println(TO_DEG(diff));
*/
  int direction;
  
  if(TO_DEG(diff) > 13){
    direction=1; 
   
  }
  else if(TO_DEG(diff) < -13){
    direction=-1;
   
  }
 
  if(previousDrt==direction){
    count+=2;
    if(count>4){ 
      if(direction ==1 ){
        //Serial.println("right"); 
        if(r==0){
          printlogln(BT, turnRight);
          r=1;
        }
      }
      else if(direction ==-1 ){
        //Serial.println("left");
        if(r==0){
          printlogln(BT, turnLeft);
          r=1;
        }
      }
    } //play music
  }
  else{
    previousDrt=direction;
    count=count-1;
    //Serial.println("stop");
    if(r==1){
      printlogln(BT, changeDrt);
      r=0;
    }
  }
  
  previousRoll=roll;
}


