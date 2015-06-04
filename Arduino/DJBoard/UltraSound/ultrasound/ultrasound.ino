const int trig = 5;
const int echo = 6;
const int inter_time = 100;
int time = 0;

void setup() {
  Serial.begin(9600);
  pinMode (trig, OUTPUT);
  pinMode (echo, INPUT);
}

void loop() {
  float duration, distance;
  digitalWrite(trig, HIGH);
  delayMicroseconds(100);
  digitalWrite(trig, LOW);
  duration = pulseIn (echo, HIGH);
  distance = (duration/2)/29;
  Serial.print("Data:");
  Serial.print (time/100);
  Serial.print(", d = ");
  Serial.print(distance);
  Serial.println(" cm");
  time = time + inter_time;
  delay(inter_time);
}
