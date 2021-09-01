package img;

import java.io.Serializable;
import java.util.ArrayList;

public class Animation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6558564449850666003L;
	private ArrayList<Image> frames;
	private double fps;
	
	public Animation(double fps) {
		this.fps = fps;
		this.frames = new ArrayList<>();
	}
	
	public double getFPS() {
		return fps;
	}
	
	public void setFPS(double fps) {
		this.fps = fps;
	}
	
	public Image removeFrame(int frameNumber) {
		return this.frames.remove(frameNumber);
	}
	
	public void setFrame(int frameNumber, Image img) {
		this.frames.set(frameNumber, img);
	}
	
	public void addFrame(Image img) {
		this.frames.add(img);
	}
	
	public void addFrame(int index, Image img) {
		this.frames.add(index, img);
	}
	
	public Image getFrame(int frameNumber) {
		return this.frames.get(frameNumber);
	}
	
	public int getNumFrames() {
		return this.frames.size();
	}
	
	public String generateCommand() {
		
		for(int i = 0; i < this.getNumFrames(); i++) {
			frames.get(i).setId(i);
		}
		
		String ret = "int del = 1000/" + fps + ";\n";
		ret += "int frames = " + this.getNumFrames() + ";\n";
		
		for(int i = 0; i < frames.size(); i++) {
			if(i == 0){
				ret += frames.get(i).cmdString(frames.get(i).translate()) + "\n";
			} else {
				ret += frames.get(i).cmdString(frames.get(i).difference(frames.get(i - 1), false)) + "\n";
			}
		}
		
		ret += "const int16_t* const frame_table[] PROGMEM = {";
		
		for(int i = 0; i < this.getNumFrames() - 1; i++) {
			ret += "frame_" + i + ",";
		}
		
		ret += "frame_" + (this.getNumFrames() - 1) + "};";
		
		return ret;
	}

	public void setDelay(int currFrame, int delay) {
		// TODO Auto-generated method stub
		frames.get(currFrame).setDelay(delay);
	}

	public void pasteOver(Image image, int currFrame) {
		// TODO Auto-generated method stub
		frames.get(currFrame).pasteOver(image);
	}
	
}
