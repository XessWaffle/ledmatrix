package graphics;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import img.Animation;
import img.Image;
public class AnimationDisplay extends JComponent implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6160836368823226305L;
	
	private Animation anim;
	private int currFrame = 0;
	private boolean stop = true;
	
	public AnimationDisplay() {
		anim = new Animation(5);
		anim.addFrame(new Image());
		
		(new Thread(this)).start();
	}
	
	public void setAnimation(Animation anim) {
		this.anim = anim;
		this.currFrame = 0;
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Image currImg = anim.getFrame(currFrame);
		
		int offset = 10;
		int spacing = 3;
		
		int width = (this.getWidth() - 2 * offset)/12;
		int height = (this.getHeight() - 2 * offset)/12;
		
		
		for(int i = 0; i < Image.MATRIX_WIDTH; i++) {
			for(int j = 0; j < Image.MATRIX_HEIGHT; j++) {
				if(currImg.get(i, j)) {
					g2d.setColor(new Color(0x3777FF));
					g2d.fillOval(i * width + offset, j * height + offset, width, height);
				} else {
					g2d.setColor(new Color(0x3777FF));
					g2d.drawOval(i * width + offset, j * height + offset, width, height);
				}
				
			}
		}
		
	}
	
	public int getCurrFrame() {
		// TODO Auto-generated method stub
		return currFrame;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			if(!stop) {
				if(currFrame < anim.getNumFrames() - 1) {
					currFrame++;
				} else {
					currFrame = 0;
				}
				
				this.repaint();
			}
			
			try {
				Thread.sleep((long) (anim.getFrame(currFrame).getDelay() > 0 ? anim.getFrame(currFrame).getDelay() : 1000.0/anim.getFPS()));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void start() {
		stop = false;
	}
	
	public void stop() {
		stop = true;
	}

}
