import serial
import sys

## Boolean variable that will represent whether or not the arduino is connected
connected = False

## establish connection to the serial port that your arduino is connected to.
'''
locations=['/dev/cu.usbmodem1411 (Arduino Uno)']
for device in locations:
   try:
print "Trying...",device
'''
ser = serial.Serial('/dev/cu.usbmodem1411', 9600) # edit serial port and rate here!

'''
        break
    except:
        print "Failed to connect on",device
'''
## loop until the arduino tells us it is ready
while not connected:
    serin = ser.read()
    connected = True

text_file = open("serialRecord.txt", 'w')

## read serial data from arduino and write it to the text file 'position.txt'
#sys.stdout.write("start")
while 1:
    if ser.inWaiting():
        x=ser.read()
        if x=="serialEnd":
            break
        sys.stdout.write(x)
        text_file.write(x)
        '''
        if x=="\n":
             text_file.seek(0)
             text_file.truncate()
        '''
        text_file.flush()
## close the serial connection and text file
#print("serialEnd!") 
text_file.close()
ser.close()



