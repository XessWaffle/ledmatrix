package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;

import img.Animation;
import img.Image;

public class FrameDisplay extends JComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4364823665348479234L;
	private static final int SPACING = 4;
	private static final int OFFSET = 20 - SPACING;
	
	private int currFrame = 0;
	private Animation anim;
	
	private boolean enableCursors = false;
	private boolean dragging = false;
	private boolean hideFrameNumber = false;
	
	private Point currMouseLoc;
	private ArrayList<Point> draggedLocs;
	private HashSet<Point> draggedLeds;
	private Point ledOver, prevValidOver;
	
	private Image copied;
	private Stack<Pair<Integer, Image>> deleteStack;
	
	
	public FrameDisplay() {
		this.anim = new Animation(20);
		this.anim.addFrame(new Image());
		draggedLocs = new ArrayList<>();
		draggedLeds = new HashSet<>();
		deleteStack = new Stack<>();
		
		prevValidOver = new Point(0,0);
		
		this.currMouseLoc = new Point(0,0);
		
		TimerTask task = new TimerTask() {
	        public void run() {
				hideFrameNumber();
				repaint();
	        }
	    };
	    Timer timer = new Timer("Timer");
	    
	    long delay = 1500L;
	    timer.schedule(task, delay);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Image currImg = anim.getFrame(currFrame);
		
		int width = (this.getWidth() - 2 * OFFSET - 11 * SPACING)/12;
		int height = (this.getHeight() - 2 * OFFSET - 11 * SPACING)/12;
		
		if(!hideFrameNumber) {
			String fn = Integer.toString(this.currFrame);
			
			Font font = new Font("Serif", Font.BOLD, 600/fn.length());
			
			g2d.setColor(new Color(0.65f, 0.65f, 0.65f, 0.5f));
			
			Rectangle rect = new Rectangle(OFFSET + 20, OFFSET, width * 12, height * 12);
			
			this.drawCenteredString(g2d, fn, rect, font);
		}
		
		ledOver = findLedOver(currMouseLoc);
		
		for(int i = 0; i < Image.MATRIX_WIDTH; i++) {
			for(int j = 0; j < Image.MATRIX_HEIGHT; j++) {
				if(currImg.get(i, j)) {
					g2d.setColor(new Color(0x3777FF));
					g2d.fillOval(i * (width + SPACING) + OFFSET, j * (height + SPACING) + OFFSET, width, height);
				} else {
					g2d.setColor(new Color(0x3777FF));
					g2d.drawOval(i * (width + SPACING) + OFFSET, j * (height + SPACING) + OFFSET, width, height);
				}
			}
		}
		
		if(enableCursors) {
			g2d.setColor(Color.BLACK);
			
			g2d.fillRect((int)((prevValidOver.x + 1) * (width + SPACING) + OFFSET - (width + SPACING)/2 - (5 + SPACING/2)), 5, 10, 10);
			g2d.fillRect(5, (int)((prevValidOver.y + 1) * (height + SPACING) + OFFSET - (height + SPACING)/2 - (5 + SPACING/2)), 10, 10);
			
			if(ledOver.x >= 0 && ledOver.x < 12 && ledOver.y >= 0 && ledOver.y < 12) {
				g2d.setColor(new Color(0x8A1C7C));
				g2d.fillOval((ledOver.x) * (width + SPACING) + OFFSET, (ledOver.y) * (height + SPACING) + OFFSET, width, height);
				prevValidOver = ledOver;
			}
		}
		
		
		
		if(dragging) {
			g2d.setColor(Color.RED);
			
			for(int i = 1; i < draggedLocs.size(); i++) {
				g2d.drawLine(draggedLocs.get(i - 1).x, draggedLocs.get(i - 1).y, draggedLocs.get(i).x, draggedLocs.get(i).y);
			}
		}
		
	}
	
	public void enableCursor() {
		this.enableCursors = true;
	}
	
	public void disableCursor() {
		this.enableCursors = false;
	}
	
	public void showFrameNumber() {
		this.hideFrameNumber = false;
		
		TimerTask task = new TimerTask() {
	        public void run() {
				hideFrameNumber();
				repaint();
	        }
	    };
	    Timer timer = new Timer("Timer");
	    
	    long delay = 1500L;
	    timer.schedule(task, delay);
	}
	
	public void hideFrameNumber() {
		this.hideFrameNumber = true;
	}
	
	public boolean isFrameNumberHidden() {
		return this.hideFrameNumber;
	}
	
	public void setMouseOver(Point p) {
		this.currMouseLoc = p;
	}
	
	public void toggle() {
		anim.getFrame(currFrame).set(ledOver.x, ledOver.y, !anim.getFrame(currFrame).get(ledOver.x, ledOver.y));
	}
	
	public void nextFrame() {
		if(this.currFrame < anim.getNumFrames() - 1)
			this.currFrame += 1;
		
	}
	
	public void prevFrame() {
		if(this.currFrame > 0) {
			this.currFrame -= 1;
		}
	}
	
	public void firstFrame() {
		// TODO Auto-generated method stub
		this.currFrame = 0;
	}
	
	public void lastFrame() {
		this.currFrame = anim.getNumFrames() - 1;
	}
	
	public void clear() {
		anim.getFrame(currFrame).setOff();
	}
	
	public void invert() {
		anim.getFrame(currFrame).invert();
	}
	
	public void fill() {
		anim.getFrame(currFrame).setOn();
	}
	
	public void copy() {
		this.copied = anim.getFrame(currFrame);
	}
	
	public void paste() {
		anim.setFrame(currFrame, new Image(copied));
		
		System.out.println("Paste");
	}
	
	public void shiftUp() {
		anim.getFrame(currFrame).shiftUp();
	}
	
	public void shiftDown() {
		anim.getFrame(currFrame).shiftDown();
	}
	
	public void shiftRight() {
		anim.getFrame(currFrame).shiftRight();
	}
	
	public void shiftLeft() {
		anim.getFrame(currFrame).shiftLeft();
	}
	
	public void invertRow() {
		Point ledOver = findLedOver(this.currMouseLoc);
		
		if(ledOver.y >= 0)
			anim.getFrame(currFrame).invertRow(ledOver.y);
	}
	
	public void invertColumn() {
		Point ledOver = findLedOver(this.currMouseLoc);
		
		if(ledOver.x >= 0)
			anim.getFrame(currFrame).invertCol(ledOver.x);
	}
	
	public void goToFrame(int frame) {
		if(frame >= 0 && frame < anim.getNumFrames()) {
			this.currFrame = frame;
		}
	}
	
	public int getNumFrames() {
		return this.anim.getNumFrames();		
	}
	
	public void addFrame() {
		this.anim.addFrame(this.currFrame + 1, new Image());
		this.nextFrame();
	}
	
	public void removeFrame() {
		if(this.anim.getNumFrames() > 1) {
			deleteStack.push(new Pair<Integer, Image>(this.currFrame, this.anim.removeFrame(this.currFrame)));
			this.prevFrame();
		}
	}
	
	public void undo() {
		if(!deleteStack.empty()) {
			Pair<Integer, Image> pop = deleteStack.pop();
			this.anim.addFrame(pop.getKey(), pop.getValue());
			this.goToFrame(pop.getKey());
		}
	}
	
	public String generate() {
		return anim.generateCommand();
	}
	
	public void setFPS(double fps) {
		anim.setFPS(fps);
		System.out.println(fps);
	}
	
	public double getFPS() {
		return anim.getFPS();
	}
	

	public void setDelay(int delay) {
		// TODO Auto-generated method stub
		anim.setDelay(this.currFrame, delay);
	}
	
	public double getDelay() {
		return anim.getFrame(this.currFrame).getDelay();
	}


	public Animation getAnimation() {
		// TODO Auto-generated method stub
		return anim;
	}
	
	public void setAnimation(Animation anim) {
		this.anim = anim;
		this.currFrame = 0;
	}
	
	public void dragToggle(Point point) {
		
		dragging = true;
		
		/*boolean ret = false;
		
		for(Point p: draggedLocs) {
			if(Math.abs(p.x - point.x) < width && Math.abs(p.y - point.y) < height) {
				ret = true;
			}
		}
		
		if(ret) return;*/
		
		Point ledOver = findLedOver(point);
		
		if(ledOver.x >= 0 && ledOver.y >= 0)
			draggedLeds.add(ledOver);
		
		draggedLocs.add(point);
	}
	
	public void endDragToggle() {
		
		if(dragging) {
			dragging = false;
			
			draggedLocs.clear();
			
			for(Point p: draggedLeds) {
				anim.getFrame(currFrame).set(p.x, p.y, !anim.getFrame(currFrame).get(p.x, p.y));
			}
			
			draggedLeds.clear();
		}
	}
	
	public void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font) {
	
		// Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	}
	
	public Point findLedOver(Point mouse) {
		Point ledOver = new Point(-1,-1);
		
		int width = (this.getWidth() - 2 * OFFSET - 11 * SPACING)/12;
		int height = (this.getHeight() - 2 * OFFSET - 11 * SPACING)/12;
		
		double minDist = 300 * 300;
		
		for(int i = 0; i < Image.MATRIX_WIDTH; i++) {
			
			int center_x = i * (width + SPACING) + OFFSET + width/2;
			
			for(int j = 0; j < Image.MATRIX_HEIGHT; j++) {
				
				int center_y = j * (height + SPACING) + OFFSET + height/2;
				
				double distance = Math.sqrt((mouse.x - center_x) * (mouse.x - center_x) + (mouse.y - center_y) * (mouse.y - center_y)); 
				
				if(distance < minDist && distance < width/2) {
					minDist = distance;
					ledOver = new Point(i , j);
				}
			}
		}
	
		return ledOver;
	}

	public void pasteOver() {
		// TODO Auto-generated method stub
		if(copied != null)
			anim.pasteOver(new Image(copied), this.currFrame);
	}


	
	
}

class Pair<X,Y>{
	private X left;
	private Y right;
	
	
	public Pair(X left, Y right) {
		this.left = left;
		this.right = right;
	}
	
	public X getKey() {
		return left;
	}
	
	public Y getValue() {
		return right;
	}
	
	
}
