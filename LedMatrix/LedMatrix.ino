#define SER 12
#define CLK 13
#define OE 8
#define SWITCH 7

#define GND_PINS 6
#define PWR_PINS 24

#define MODE_ANIM 1
#define MODE_DEBUG 2
#define MODE_SPECIAL 3

#define STOP 0xDD
#define ALL_ON 0xFF
#define ALL_OFF 0xCC

#define MATRIX_WIDTH 12
#define MATRIX_HEIGHT 12

#define CHANCES 1

#include "Image.h"
#include <avr/pgmspace.h>



const int GND[6] = {3,5,6,9,10,11};
char sec = 0;
int mode = 1;
int chance = 0;
int16_t custDel = -1;
bool screenPause = false, enableDebug = false;

/*
 * FF ==> ALL ON
 * EF ==> RANDOM
 * FE ==> RANDOM_DELAY_ENABLED
 * DF ==> SPIRAL_DELAY_ENABLED
 * FD ==> X
 * CF ==> X_DELAY_ENABLED
 * FC
 * EE
 * DE
 * ED
 * CE
 * EC
 * DD == STOP
 * DC
 * CD
 * CC ==> ALL OFF
 */

//PASTE CODE BELOW HERE
int del = 1000/15.0;
int frames = 5;
const int16_t frame_0[] PROGMEM = { (int16_t) 0x3e8,(int16_t)0x36, (int16_t)0x46, (int16_t)0x56, (int16_t)0x66, (int16_t)0x76, (int16_t)0x86, (int16_t)0x96, (int16_t)0xa6, (int16_t)0xb6, (int16_t) 0xDD};
const int16_t frame_1[] PROGMEM = { (int16_t) 0xffffffff,(int16_t)0x36, (int16_t)0x46, (int16_t)0x56, (int16_t)0x76, (int16_t)0x86, (int16_t)0x96, (int16_t)0xa6, (int16_t)0xb6, (int16_t) 0xDD};
const int16_t frame_2[] PROGMEM = { (int16_t) 0xffffffff,(int16_t)0x176, (int16_t) 0xDD};
const int16_t frame_3[] PROGMEM = { (int16_t) 0xffffffff,(int16_t)0x186, (int16_t) 0xDD};
const int16_t frame_4[] PROGMEM = { (int16_t) 0xffffffff,(int16_t)0x196, (int16_t) 0xDD};
const int16_t* const frame_table[] PROGMEM = {frame_0,frame_1,frame_2,frame_3,frame_4};Image drawBuffer = Image(0);

void setup() {
  // put your setup code here, to run once:
  noInterrupts();
  
  pinMode(SER, OUTPUT);
  pinMode(CLK, OUTPUT);
  pinMode(OE, OUTPUT);
  pinMode(SWITCH, INPUT);

  for(int i = 0; i < GND_PINS; i++){
    pinMode(GND[i], OUTPUT);
    digitalWrite(GND[i], LOW);
  }

  digitalWrite(OE, LOW);

  Serial.begin(9600);
  
  TCCR1A = 0;
  TCCR1B = 0;
  OCR1A = 5556; //Refresh screen at 60 Hz
  OCR1B = 500;
  TCCR1B |= (1 << CS11) | (1 << WGM12);
  TIMSK1 |= (1 << OCIE1A) | (1 << OCIE1B);

  interrupts();
}

ISR(TIMER1_COMPA_vect){
  digitalWrite(GND[sec++], LOW);
  if(sec >= 6) sec = 0;
}

ISR(TIMER1_COMPB_vect){

  if(!screenPause){
    if(!drawBuffer.translated()){
      drawBuffer.translate();
    }
  
    digitalWrite(OE, HIGH);
    
    for(int i = 0; i < PWR_PINS; i++){
      digitalWrite(SER, drawBuffer.translated_img[sec][i]);
      shift();
    }
  
    digitalWrite(OE, LOW);
  }

  if(digitalRead(SWITCH) == HIGH){
    digitalWrite(GND[sec], HIGH);
  }
}



void loop() {
  // put your main code here, to run repeatedly:

  if(enableDebug && chance < CHANCES){
    delay(500);
    if(digitalRead(SWITCH) == HIGH){
      if(mode == MODE_ANIM){mode = MODE_DEBUG;}
      else if(mode == MODE_DEBUG){mode = MODE_SPECIAL;}
      else if(mode == MODE_SPECIAL){mode = MODE_ANIM;}
    } else {
      mode = MODE_ANIM;
    }
    chance++;
  } 
  
  if(mode == MODE_ANIM){
    animation();
    chances();
  } else if(mode == MODE_DEBUG){
    debug();
    chances();
  } else if(mode == MODE_SPECIAL){
    enableDebug = false;
    chances();
    enableDebug = true;
  }


  drawBuffer.off();
}

void chances(){
  if(!enableDebug){
    chance = 0;
  }
}

void animation(){
  for(int i = 0; i < frames; i++){
    if(digitalRead(SWITCH) == HIGH){
      enableDebug = false;  
      prepFrame(i);      
      delay(custDel > 0 ? custDel : del);
    } else {
      enableDebug = true;
      return;    
    }
  }
}

void debug(){
  for(int i = 0; i < MATRIX_WIDTH; i++){
    for(int j = 0; j < MATRIX_HEIGHT; j++){
      if(digitalRead(SWITCH) == HIGH){ 
        enableDebug = false; 
        drawBuffer.set(i, j, 1);
        delay(100);
        drawBuffer.set(i, j, 0);
      } else {
        enableDebug = true;
        return;      
      }
    }
  }
}

void special(){
  
}

void shift(){
  digitalWrite(CLK, HIGH);
  digitalWrite(CLK, LOW);
}

void prepFrame(int frameNumber){

  int offset = 0;
  bool breakFlag = 0;

  bool first = true;
  
  if(frameNumber >= 0 && frameNumber < frames){
    while(true) {  
      char* str = (char *)pgm_read_word(&frame_table[frameNumber]);
      
      if(first){
        custDel = (int16_t) pgm_read_word(str);
        first = false;
      } else {   
        byte inst = pgm_read_byte(str + offset++);
        byte state = pgm_read_byte(str + offset++);
  
        if(breakFlag){
          break;
        }
  
        int x = getX(inst);
        int y = getY(inst);
  
        if(frameNumber == 0){
          if(x >= 0 && x < MATRIX_WIDTH && y >= 0 && y <= MATRIX_HEIGHT){
            drawBuffer.set(x, y, 1);
          } else if(inst == STOP){
            breakFlag = 1;
          }
        } else {
            if(x >= 0 && x < MATRIX_WIDTH && y >= 0 && y <= MATRIX_HEIGHT){
            drawBuffer.set(x, y, state);
          } else if(inst == STOP){
            breakFlag = 1;
          }
        }
      }
    }
  }
}

int getX(int16_t inst) {
  int16_t mask = (int16_t) 0x00F0;
  return (int) ((inst & mask) >> 4);
}

int getY(int16_t inst) {
  int16_t mask = (int16_t) 0x000F;
  return (int) (inst & mask);
}


/*void makeSpiral(){
    for(int j = 0; j < 6; j++){
    for(int i = 0; i < 12 - j; i++){
      drawBuffer.set(i, j, 1);
      delay(del);
    }
  
    for(int i = 0; i < 12 - j; i++){
      drawBuffer.set(11 - j, i, 1);
      delay(del);
    }
  
    for(int i = 0; i < 12 - j; i++){
      drawBuffer.set(11 - j - i, 11 - j, 1);
      delay(del);
    }
  
    for(int i = 0; i < 12 - j; i++){
      drawBuffer.set(j, 11 - i - j, 1);
      delay(del);
    }
  }
}

void makeX(){
  for(int i = 0; i < 12; i++){
    drawBuffer.set(11 - i, i , 1);
  }

  for(int i = 0; i < 12; i++){
    drawBuffer.set(i, i , 1);
  }
}

void makeXDelay(){
  for(int i = 0; i < 12; i++){
    drawBuffer.set(11 - i, i , 1);
    delay(del);
  }

  for(int i = 0; i < 12; i++){
    drawBuffer.set(i, i , 1);
    delay(del);
  }

  for(int i = 12; i >= 0; i--){
    drawBuffer.set(11 - i, i , 0);
    delay(del);
  }

  for(int i = 12; i >= 0; i--){
    drawBuffer.set(i, i , 0);
    delay(del);
  }
}*/
