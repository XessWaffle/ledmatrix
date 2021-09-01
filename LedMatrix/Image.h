#define SER 12
#define CLK 13
#define OE 8
#define MATRIX_WIDTH 12
#define MATRIX_HEIGHT 12
#define GND_PINS 6
#define PWR_PINS 24

class Image{
  private:
    bool _img[MATRIX_WIDTH][MATRIX_HEIGHT];
    bool _translated;
    int _id;
    
  public:
    bool translated_img[GND_PINS][PWR_PINS];

    Image();
    Image(int id);
    
    void set(int x, int y, bool HL);
    void invert();

    void off();
    void on();
    
    void translate();
    bool translated();
};
