
bool isBoardUp = false;

void checkBoardUp(){
  
  if( TO_DEG(pitch) >= 25 && TO_DEG(pitch) <= 35 && !isBoardUp){
    printlogln(BT, playBoardUpMusic);
    isBoardUp = true;
  }
  
  else if( TO_DEG(pitch) >= -5 && TO_DEG(pitch) <= 5 && isBoardUp){
    printlogln(BT, stopBoardUpMusic);
    isBoardUp = false;
  }
  
}

