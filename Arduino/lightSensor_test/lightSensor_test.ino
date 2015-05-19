int Led=13;
int iRSensorPin=3;
int val;
void setup()
{
pinMode(Led,OUTPUT);
pinMode(iRSensorPin,INPUT);
}

void loop()
{
  val=digitalRead(iRSensorPin);
  if(val==HIGH)
  {
  digitalWrite(Led,HIGH);
  } else {
    digitalWrite(Led,LOW);
  }
}
