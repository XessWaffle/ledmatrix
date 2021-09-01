package file;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;

import img.Animation;

public class AnimationManager {
	
	private Animation curr;
	private String name;
	
	public AnimationManager() {
		curr = new Animation(5);
		File dir = new File("./animations");
		if(dir.mkdir()) {
			System.out.println("Successful Directory Creation");
		}
	}
	
	public void setAnim(Animation toSet) {
		this.curr = toSet;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void save() {
	    Path p = Paths.get("./animations/"+ this.name + ".anim");

	    try {
	    	FileOutputStream f = new FileOutputStream(new File(p.toString()));
            ObjectOutputStream o = new ObjectOutputStream(f);
            
            o.writeObject(curr);
            
	    } catch (IOException x) {
	      System.err.println(x);
	    }
	}
	
	public Animation load(String name) {
		
		Path file = FileSystems.getDefault().getPath("animations", name);
		try {
			FileInputStream fi = new FileInputStream(new File(file.toString()));
            ObjectInputStream oi = new ObjectInputStream(fi);
            
            this.curr = (Animation) oi.readObject();
            
		} catch (IOException x) {
		    System.err.println(x);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.curr;
	}
	
	
	
	
}
