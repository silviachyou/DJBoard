bool isBlack = false;
bool isMoving = false;
const int wheelLen = 15; //cm
double v = 0;
int dis = 0;
const char eol[3]="\n";

int IRVal;
unsigned long time, duration;


void wheelMoveSetup() {
  time = millis();
}

void checkWheelMove(){
  
  IRVal=digitalRead(iRSensorPin);

  //HIGH: white
  //LOW: black
 
  if(IRVal==HIGH && isBlack){  //first seen white
     
    isBlack = false;
    /* distance */
    dis = dis + wheelLen; // += 15cm
     
    printlog(DEV, "distance: ");
    printlogln(DEV, dis);
     
    char dString[80]="dis= ";
    char sofd[20];
    dtostrf(dis, 8, 0, sofd);
    strcat(dString, sofd);  
    strcat(dString, eol);
    printlogln(BT, dString);


    if(!isMoving){ // previous state: stop
      printlogln(BT, playWheelMusic);
      isMoving = true;
    }
    else{ // previous state: moving
            
      /* velocity */
      v = (double)wheelLen*10/(double)duration; // cm/millis -> m/s
      
      printlog(DEV,"velocity: ");
      printlogln(DEV, v);
      
      char vString[80]="v= ";
      char sofv[20];
      dtostrf(v, 6, 4, sofv);
      strcat(vString, sofv);  
      strcat(vString, eol);
      printlogln(BT, vString);
      
    }
    time = millis();
  }
  else if(IRVal==LOW) {  //isBlack 
    isBlack = true;
  }
  if(millis()-time>1000 && isMoving){ // stop
      printlogln(BT, stopWheelMusic);
      isMoving = false;
      v = 0;
  }  
}
