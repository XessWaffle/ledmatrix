package img;


import java.io.Serializable;
import java.util.ArrayList;


public class Image implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7900547622592125358L;
	public static final int MATRIX_WIDTH = 12;
	public static final int MATRIX_HEIGHT = 12;
	
	private static int id = 0;
	
	
	private boolean[][] view;
	private int currId;
	
	private int delay = -1;
	
	public Image() {
		view = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];
		//cmds.add((byte)0xCC);
		
		currId = id++;
	}
	
	public Image(Image copy) {
		view = new boolean[MATRIX_WIDTH][MATRIX_HEIGHT];
		
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			for(int j = 0; j < MATRIX_HEIGHT; j++) {
				view[i][j] = copy.view[i][j];
			}
		}
	}
	
	public boolean get(int x, int y) {
		if(x >= 0 && x < MATRIX_WIDTH && y >= 0 && y < MATRIX_HEIGHT) {
			return view[x][y];
		} else {
			return false;
		}
	}
	
	public void set(int x, int y, boolean val) {
		if(x >= 0 && x < MATRIX_WIDTH && y >= 0 && y < MATRIX_HEIGHT) {
			view[x][y] = val;
		}
	}
	
	public void setRow(int y, boolean val) {
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			view[i][y] = val;
		}
	}
	
	public void invertRow(int y) {
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			view[i][y] = !view[i][y];
		}
		
	}
	
	public void setCol(int x, boolean val) {
		for(int i = 0; i < MATRIX_HEIGHT; i++) {
			view[x][i] = val;
		}
	}
	
	public void invertCol(int x) {
		for(int i = 0; i < MATRIX_HEIGHT; i++) {
			view[x][i] = !view[x][i];
		}
		
	}
	
	public void setOn() {
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			this.setCol(i, true);
		}
	}
	
	public void setOff() {
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			this.setCol(i, false);
		}

	}
	
	public void invert() {
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			this.invertCol(i);
		}
		
	}
	
	public void shiftLeft() {
		for(int i = 1; i <= MATRIX_WIDTH; i++) {
			for(int j = 0; j < MATRIX_HEIGHT; j++) {
				if(i == MATRIX_WIDTH) {
					view[i - 1][j] = false;
				} else {
					view[i - 1][j] = view[i][j];
				}
			}
		}
	}
	
	public void shiftUp() {
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			for(int j = 1; j <= MATRIX_HEIGHT; j++) {
				if(j == MATRIX_HEIGHT) {
					view[i][j - 1] = false;
				} else {
					view[i][j - 1] = view[i][j];
				}
			}
		}
	}
	
	public void shiftRight() {
		for(int i = MATRIX_WIDTH - 2; i >= -1; i--) {
			for(int j = 0; j < MATRIX_HEIGHT; j++) {
				if(i == -1) {
					view[i + 1][j] = false;
				} else {
					view[i + 1][j] = view[i][j];
				}
			}
		}
	}
	
	public void shiftDown() {
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			for(int j = MATRIX_HEIGHT - 2; j >= -1; j--) {
				if(j == -1) {
					view[i][j + 1] = false;
				} else {
					view[i][j + 1] = view[i][j];
				}
			}
		}
	}
	
	
	public ArrayList<String> difference(Image other, boolean back){
		ArrayList<String> cmds = new ArrayList<>();
		
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			for(int j = 0; j < MATRIX_HEIGHT; j++) {
				if(view[i][j] != other.view[i][j]) {
					
					if(back) {
						if(other.view[i][j]) {
							cmds.add("0x" + Integer.toHexString(1 << 8 | i << 4 | (MATRIX_HEIGHT - j - 1)));
						} else {
							cmds.add("0x" + Integer.toHexString(0 << 8 | i << 4 | (MATRIX_HEIGHT - j - 1)));
						}
					} else {
						if(view[i][j]) {
							cmds.add("0x" + Integer.toHexString(1 << 8 | i << 4 | (MATRIX_HEIGHT - j - 1)));
						} else {
							cmds.add("0x" + Integer.toHexString(0 << 8 | i << 4 | (MATRIX_HEIGHT - j - 1)));
						}
					}
					
					//System.out.println(Integer.toHexString(i << 4 | (MATRIX_HEIGHT - j - 1)));
				}
			}
		}
		
		return cmds;
	}
	
	public ArrayList<String> translate() {
		
		ArrayList<String> cmds = new ArrayList<>();
		
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			for(int j = 0; j < MATRIX_HEIGHT; j++) {
				if(view[i][j]) {
					System.out.println(Integer.toHexString(i << 4 | (MATRIX_HEIGHT - j - 1)));
					cmds.add("0x" + Integer.toHexString(i << 4 | (MATRIX_HEIGHT - j - 1)));
				}
			}
		}
		
		return cmds;
	}
	
	public String cmdString(ArrayList<String> cmds) {
		String ret = "const int16_t frame_" + currId + "[] PROGMEM = { (int16_t) 0x" + Integer.toHexString(delay) + ",";
		
		for(int i = 0; i < cmds.size(); i++) {
			ret += "(int16_t)" +  cmds.get(i) + ", ";
		}
		
		ret += "(int16_t) 0xDD};";
		
		return ret;
	}

	public void setId(int i) {
		// TODO Auto-generated method stub
		this.currId = i;
	}

	public void setDelay(int delay) {
		// TODO Auto-generated method stub
		this.delay = delay;
	}
	
	public double getDelay() {
		return delay;
	}

	public void pasteOver(Image image) {
		// TODO Auto-generated method stub
		for(int i = 0; i < MATRIX_WIDTH; i++) {
			for(int j = 0; j < MATRIX_HEIGHT; j++) {
				if(!this.view[i][j] && image.view[i][j]) {
					this.view[i][j] = true;
				}
			}
		}
	}
	

	/*private static int getX(byte inst) {
		byte mask = (byte) 0xF0;
		return (int) ((inst | mask) >> 4);
	}
	
	private static int getY(byte inst) {
		byte mask = (byte) 0x0F;
		return (int) (inst | mask);
	}*/
}
