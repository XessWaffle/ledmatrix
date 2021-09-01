#include "Image.h"

Image::Image(){
  _id = -1;
  this->off();
}

Image::Image(int id){
  _id = id;

  for(int i = 0; i < MATRIX_WIDTH; i++){
    for(int j = 0; j < MATRIX_HEIGHT; j++){
      _img[i][j] = 0;
    }
  }
  
}

void Image::set(int x, int y, bool HL){
  if(x >= 0 && x < MATRIX_WIDTH && y >= 0 && y < MATRIX_HEIGHT){
    _img[y][x] = HL;
    _translated = false;
  }
}

void Image::off(){
  for(int i = 0; i < MATRIX_WIDTH; i++){
    for(int j = 0; j < MATRIX_HEIGHT; j++){
      _img[i][j] = 0;
    }
  }

  _translated = false;
}

void Image::on(){
  for(int i = 0; i < MATRIX_WIDTH; i++){
    for(int j = 0; j < MATRIX_HEIGHT; j++){
      _img[i][j] = 1;
    }
  }
  
  _translated = false;
}

void Image::invert(){
  for(int i = 0; i < MATRIX_WIDTH; i++){
    for(int j = 0; j < MATRIX_HEIGHT; j++){
      _img[i][j] = !_img[i][j];
    }
  }

  _translated = false;
}

void Image::translate(){

  int x = 1, y = 0;
  
  for(int i = 0; i < GND_PINS; i++){
    for(int j = 0; j < PWR_PINS; j++){

      translated_img[i][j] = _img[x++][y];

      
      if(x >= MATRIX_WIDTH){
        x = 0;
        y++;
      }
      

      if(y >= MATRIX_HEIGHT){
        y = 0;
      }
    }
  }

  _translated = true;
}

bool Image::translated(){
  return _translated;
}
