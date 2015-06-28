bool isStringHead = true;


void printlog(char tag, const char *message){

  checkIfStringHead(tag);
  sendBT(tag, message);

  Serial.print(message);
}

void printlogln(char tag,const char *message){
  checkIfStringHead(tag);
  sendBT(tag, message);

  isStringHead = true;
  Serial.println(message);
}

void printlog(char tag, int message){

  checkIfStringHead(tag);
  sendBT(tag, message);
  Serial.print(message);
}

void printlogln(char tag, int message){
  checkIfStringHead(tag);
  sendBT(tag, message);
  isStringHead = true;
  Serial.println(message);
}

void printlog(char tag, unsigned long message){
  checkIfStringHead(tag);
  sendBT(tag, message);
  Serial.print(message);
}

void printlogln(char tag, unsigned long message){
  checkIfStringHead(tag);
  sendBT(tag, message);
  isStringHead = true;
  Serial.println(message);
}

void printlog(char tag, double message){
  checkIfStringHead(tag);
  sendBT(tag, message);
  Serial.print(message);
}

void printlogln(char tag, double message){
  checkIfStringHead(tag);
  sendBT(tag, message);
  isStringHead = true;
  Serial.println(message);
}

void checkIfStringHead(char tag) {
  if(isStringHead){
      Serial.print(tag);
      Serial.print(" ");
      isStringHead = false;
      unsigned long duration = millis()-globalTime;
      Serial.print(duration);
      Serial.print(" ");
  }
}

void sendBT(char tag, const char *message){
  if(tag == BT){
    Bluetooth.write(tag);
    Bluetooth.write(" ");
    unsigned long duration = millis()-globalTime;

    char s[10];
    sprintf (s, "%u\0", duration);
    // itoa((int)duration, s, 10);
    Bluetooth.write(s);
    Bluetooth.write(" ");
    Bluetooth.write(message);
  }
}

void sendBT(char tag, int message){
  if(tag == BT){
    Bluetooth.write(tag);
    Bluetooth.write(" ");
    unsigned long duration = millis()-globalTime;

    char s[10];
    sprintf (s, "%u\0", duration);
    // itoa((int)duration, s, 10);
    Bluetooth.write(s);
    Bluetooth.write(" ");
    Bluetooth.write(message);
  }
}

void sendBT(char tag, unsigned long message){
  if(tag == BT){
    Bluetooth.write(tag);
    Bluetooth.write(" ");
    unsigned long duration = millis()-globalTime;

    char s[10];
    sprintf (s, "%u\0", duration);
    // itoa((int)duration, s, 10);
    Bluetooth.write(s);
    Bluetooth.write(" ");
    Bluetooth.write(message);
  }
}

void sendBT(char tag, double message){
  if(tag == BT){
    Bluetooth.write(tag);
    Bluetooth.write(" ");
    unsigned long duration = millis()-globalTime;

    char s[10];
    sprintf (s, "%u\0", duration);
      // (int)itoa( duration,s, 10);
    Bluetooth.write(s);
    Bluetooth.write(" ");
    Bluetooth.write(message);
  }
}
