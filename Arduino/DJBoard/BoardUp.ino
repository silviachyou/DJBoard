
bool isBoardUp = false;

void checkBoardUp(){

  if( TO_DEG(pitch) >= 25 && TO_DEG(pitch) <= 35 && !isBoardUp){
    Serial.println("Board Up");
    Bluetooth.write(playBoardUpMusic);
    isBoardUp = true;
  }
  
  else if( TO_DEG(pitch) >= -5 && TO_DEG(pitch) <= 5 && isBoardUp){
    Serial.println("Board Down");
    Bluetooth.write(stopBoardUpMusic);
    isBoardUp = false;
  }
  
}

