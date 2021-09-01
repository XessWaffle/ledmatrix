package graphics;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import file.AnimationManager;
import img.Animation;
import img.Image;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import javax.swing.JSlider;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import java.awt.SystemColor;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ChangeEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.JPopupMenu;
import java.awt.Component;
import javax.swing.JMenuItem;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {

	private JFrame frame;
	private FrameDisplay fd;
	private AnimationDisplay ad;
	private AnimationManager am;
	
	boolean playing = false, copyframe = false;
	private JTextField txtAnimationName;
	private JTextField txtDelay;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	/**
	 * 
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		am = new AnimationManager();
		
		fd = new FrameDisplay();
		fd.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				fd.setMouseOver(new Point(e.getX(), e.getY()));
				fd.repaint();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				fd.dragToggle(new Point(e.getX(), e.getY()));
				fd.repaint();
			}
		});
		fd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fd.repaint();
				fd.toggle();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				fd.enableCursor();
				fd.repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				fd.disableCursor();
				fd.repaint();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				fd.endDragToggle();
				fd.repaint();
			}
		});
		fd.setBounds(5, 5, this.frame.getHeight()-50, this.frame.getHeight()-50);
		frame.getContentPane().add(fd);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(fd, popupMenu);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Invert Row");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.invertRow();
				fd.repaint();
			}
		});
		popupMenu.add(mntmNewMenuItem_3);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Invert Column");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.invertColumn();
				fd.repaint();
			}
		});
		popupMenu.add(mntmNewMenuItem);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(565, 10, 411, 360);
		frame.getContentPane().add(tabbedPane);
		
		JPanel animPanel = new JPanel();
		tabbedPane.addTab("Animation Tools", null, animPanel, null);
		animPanel.setLayout(null);
		
		ad = new AnimationDisplay();
		ad.setBounds(0, 0, 275, 275);
		animPanel.add(ad);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(276, 274, 120, 46);
		animPanel.add(comboBox);
		comboBox.setToolTipText("Frames Per Second");
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.setFPS((Double)comboBox.getSelectedItem());
			}
		});
		comboBox.setMaximumRowCount(60);
		comboBox.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
		comboBox.setModel(new DefaultComboBoxModel(new Double[] {5.0, 10.0, 15.0, 30.0, 60.0}));
		comboBox.setSelectedIndex(2);
		comboBox.setEditable(true);
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnPlay.setBounds(10, 277, 253, 46);
		animPanel.add(btnPlay);
		btnPlay.setBackground(SystemColor.controlHighlight);
		
		JButton btnNewButton = new JButton("Go To");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.goToFrame(ad.getCurrFrame());
				fd.repaint();
			}
		});
		btnNewButton.setBackground(SystemColor.controlHighlight);
		btnNewButton.setBounds(285, 10, 111, 46);
		animPanel.add(btnNewButton);
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(playing) {
					btnPlay.setText("Play");
					ad.stop();
					playing = false;
				} else {
					btnPlay.setText("Pause");
					ad.setAnimation(fd.getAnimation());
					ad.start();
					playing = true;
				}
			}
		});
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBackground(SystemColor.controlHighlight);
		btnGenerate.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String myString = fd.generate();
				StringSelection stringSelection = new StringSelection(myString);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		});
		btnGenerate.setBounds(565, 493, 411, 46);
		frame.getContentPane().add(btnGenerate);
		
	
		JPanel framePanel = new JPanel();
		tabbedPane.addTab("Frame Tools", null, framePanel, null);
		framePanel.setLayout(null);
		
		JToggleButton tglbtnCustomDelay = new JToggleButton("Delay");
		tglbtnCustomDelay.setBackground(SystemColor.controlHighlight);
		tglbtnCustomDelay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnCustomDelay.isSelected()) {
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
					fd.setDelay(-1);
				}
			}
		});
		tglbtnCustomDelay.setBounds(20, 221, 79, 46);
		framePanel.add(tglbtnCustomDelay);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(20, 25, 79, 46);
		framePanel.add(btnClear);
		btnClear.setBackground(SystemColor.controlHighlight);
		
		JButton btnInvert = new JButton("Invert");
		btnInvert.setBounds(20, 137, 79, 46);
		framePanel.add(btnInvert);
		btnInvert.setBackground(SystemColor.controlHighlight);
		
		JButton btnFill = new JButton("Fill");
		btnFill.setBounds(20, 81, 79, 46);
		framePanel.add(btnFill);
		btnFill.setBackground(SystemColor.controlHighlight);
		
		JButton btnCopy = new JButton("Copy");
		btnCopy.setBounds(109, 25, 79, 46);
		framePanel.add(btnCopy);
		btnCopy.setBackground(SystemColor.controlHighlight);
		
		JButton btnPaste = new JButton("Paste");
		btnPaste.setBounds(109, 81, 79, 46);
		framePanel.add(btnPaste);
		btnPaste.setBackground(SystemColor.controlHighlight);
		
		JButton btnCopyPaste = new JButton("Paste Over");
		btnCopyPaste.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnCopyPaste.setBounds(109, 137, 79, 46);
		framePanel.add(btnCopyPaste);
		btnCopyPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(copyframe) {
					fd.copy();
					fd.addFrame();
					fd.paste();
					fd.showFrameNumber();
				} else {
					fd.pasteOver();
				}
				
				
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
				}
					
				
				fd.repaint();
			}
		});
		
		btnCopyPaste.setToolTipText("Copy frame, make new frame, paste previous frame");
		btnCopyPaste.setBackground(SystemColor.controlHighlight);
		
		JButton btnShiftRight = new JButton("Shift Right");
		btnShiftRight.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnShiftRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(copyframe) {
					fd.copy();
					fd.addFrame();
					fd.paste();
					fd.shiftRight();
					fd.showFrameNumber();
				} else {
					fd.shiftRight();
				}
				
				fd.repaint();
			}
		});
		btnShiftRight.setBackground(SystemColor.controlHighlight);
		btnShiftRight.setBounds(306, 81, 79, 46);
		framePanel.add(btnShiftRight);
		
		JButton btnShiftLeft = new JButton("Shift Left");
		btnShiftLeft.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnShiftLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(copyframe) {
					btnShiftLeft.setText("Copy Left");
					fd.copy();
					fd.addFrame();
					fd.paste();
					fd.shiftLeft();
					fd.showFrameNumber();
				} else {
					btnShiftLeft.setText("Shift Left");
					fd.shiftLeft();
				}
				fd.repaint();
			}
		});
		btnShiftLeft.setBackground(SystemColor.controlHighlight);
		btnShiftLeft.setBounds(217, 81, 79, 46);
		framePanel.add(btnShiftLeft);
		
		JButton btnShiftUp = new JButton("Shift Up");
		btnShiftUp.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnShiftUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(copyframe) {
					fd.copy();
					fd.addFrame();
					fd.paste();
					fd.shiftUp();
					fd.showFrameNumber();
				} else {
					fd.shiftUp();
				}
				fd.repaint();
			}
		});
		btnShiftUp.setBackground(SystemColor.controlHighlight);
		btnShiftUp.setBounds(260, 25, 79, 46);
		framePanel.add(btnShiftUp);
		
		JButton btnShiftDown = new JButton("Shift Down");
		btnShiftDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(copyframe) {
					fd.copy();
					fd.addFrame();
					fd.paste();
					fd.shiftDown();
					fd.showFrameNumber();
				} else {
					fd.shiftDown();
				}
				fd.repaint();
			}
		});
		btnShiftDown.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnShiftDown.setBackground(SystemColor.controlHighlight);
		btnShiftDown.setBounds(260, 137, 79, 46);
		framePanel.add(btnShiftDown);
		
		JButton btnMinus = new JButton("Delete Frame");
		btnMinus.setBounds(20, 277, 168, 46);
		framePanel.add(btnMinus);
		btnMinus.setToolTipText("Remove Frame");
		btnMinus.setBackground(SystemColor.controlHighlight);
		
		
		JButton btnPlus = new JButton("New Frame");
		btnPlus.setBounds(209, 277, 176, 46);
		framePanel.add(btnPlus);
		btnPlus.setBackground(SystemColor.controlHighlight);
		btnPlus.setToolTipText("New Frame");
		
		txtDelay = new JTextField();
		txtDelay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					int delay = Integer.parseInt(txtDelay.getText());
					fd.setDelay(delay);
				} catch(Exception j) {
					txtDelay.setText("Must be an integer!");
				}
			}
		});
		txtDelay.setEnabled(false);
		txtDelay.setEditable(false);
		txtDelay.setHorizontalAlignment(SwingConstants.CENTER);
		txtDelay.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
		txtDelay.setText("");
		txtDelay.setBounds(109, 221, 276, 46);
		framePanel.add(txtDelay);
		txtDelay.setColumns(10);
		//btnPlus.setIcon(new ImageIcon("img/plus.png"));
		btnPlus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.addFrame();
				fd.showFrameNumber();
				//System.out.println("Frames:" + fd.getNumFrames());
				
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
				}
					
				
				fd.repaint();
			}
		});
		
		btnMinus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(copyframe) {
					fd.copy();
					fd.removeFrame();
				} else {
					fd.removeFrame();
				}
				//System.out.println("Frames:" + fd.getNumFrames());
				fd.showFrameNumber();
				
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
				}
					
				
				fd.repaint();
			}
		});
		btnPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.paste();
				fd.repaint();
			}
		});
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.copy();
			}
		});
		btnFill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.fill();
				fd.repaint();
			}
		});
		btnInvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.invert();
				fd.repaint();
			}
		});
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.clear();
				fd.repaint();
			}
		});
		
		JButton btnFirst = new JButton("<<");
		btnFirst.setToolTipText("First Frame");
		btnFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.firstFrame();
				fd.showFrameNumber();
				
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
					
				}
				
				fd.repaint();
			}
		});
		btnFirst.setBackground(SystemColor.controlHighlight);
		btnFirst.setBounds(565, 380, 60, 46);
		frame.getContentPane().add(btnFirst);
		
		JButton btnLast = new JButton(">>");
		btnLast.setToolTipText("Last Frame");
		btnLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.lastFrame();
				fd.showFrameNumber();
				
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
				}
					
				
				fd.repaint();
			}
		});
		btnLast.setBackground(SystemColor.controlHighlight);
		btnLast.setBounds(916, 380, 60, 46);
		frame.getContentPane().add(btnLast);
		
		JButton btnSave = new JButton("Save");
		btnSave.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				am.setAnim(fd.getAnimation());
				am.setName(txtAnimationName.getText());
				am.save();
			}
		});
		btnSave.setBackground(SystemColor.controlHighlight);
		btnSave.setBounds(846, 436, 60, 46);
		frame.getContentPane().add(btnSave);
		
		txtAnimationName = new JTextField();
		txtAnimationName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				am.setName(txtAnimationName.getText());
			}
		});
		txtAnimationName.setBackground(SystemColor.window);
		txtAnimationName.setText("Animation Name");
		txtAnimationName.setHorizontalAlignment(SwingConstants.CENTER);
		txtAnimationName.setFont(new Font("Trebuchet MS", Font.PLAIN, 20));
		txtAnimationName.setBounds(565, 436, 271, 47);
		frame.getContentPane().add(txtAnimationName);
		txtAnimationName.setColumns(10);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.setFont(new Font("Tahoma", Font.PLAIN, 9));
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser chooser = new JFileChooser(new File("./animations"));
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Animation Files", "anim");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(frame);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       fd.setAnimation(am.load(chooser.getSelectedFile().getName()));
			       ad.setAnimation(am.load(chooser.getSelectedFile().getName()));
			       txtAnimationName.setText(chooser.getSelectedFile().getName().substring(0, chooser.getSelectedFile().getName().indexOf(".")));
			       fd.repaint();
			       ad.repaint();
			    }
			    
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
				}
					
			}
		});
		btnLoad.setBackground(SystemColor.controlHighlight);
		btnLoad.setBounds(916, 436, 60, 46);
		frame.getContentPane().add(btnLoad);
		
		JButton btnPrev = new JButton("<");
		btnPrev.setToolTipText("Prev Frame");
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.prevFrame();
				fd.showFrameNumber();
				
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
				}
					
				fd.repaint();
			}
		});
		btnPrev.setBackground(SystemColor.controlHighlight);
		btnPrev.setBounds(635, 380, 60, 46);
		frame.getContentPane().add(btnPrev);
		
		JButton btnNext = new JButton(">");
		btnNext.setToolTipText("Next Frame");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fd.nextFrame();
				fd.showFrameNumber();
				
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
				}
				
				fd.repaint();
			}
		});
		btnNext.setBackground(SystemColor.controlHighlight);
		btnNext.setBounds(846, 380, 60, 46);
		frame.getContentPane().add(btnNext);
		
		JButton btnNew = new JButton("New Animation");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Animation n = new Animation(20);
				n.addFrame(new Image());
				
				fd.setAnimation(n);
				ad.setAnimation(n);
				
				if(fd.getDelay() > 0) {
					txtDelay.setText(fd.getDelay() + "");
					tglbtnCustomDelay.setSelected(true);
					txtDelay.setEnabled(true);
					txtDelay.setEditable(true);
				} else {
					txtDelay.setText("");
					tglbtnCustomDelay.setSelected(false);
					txtDelay.setEnabled(false);
					txtDelay.setEditable(false);
				}
					
				
				fd.repaint();
				ad.repaint();
				
			}
		});
		btnNew.setBounds(705, 380, 131, 46);
		frame.getContentPane().add(btnNew);
		btnNew.setBackground(SystemColor.controlHighlight);
		
		KeyboardFocusManager keyManager;

		keyManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		keyManager.addKeyEventDispatcher(new KeyEventDispatcher() {

		  @Override
		  public boolean dispatchKeyEvent(KeyEvent e) {
		    if(e.getID()==KeyEvent.KEY_PRESSED && e.getKeyCode()==KeyEvent.VK_SHIFT){
		    	copyframe = true;
	
		    	btnCopyPaste.setText("Copy Paste");
				btnMinus.setText("Copy Delete");
				btnShiftRight.setText("Copy Right");
				btnShiftLeft.setText("Copy Left");
				btnShiftUp.setText("Copy Up");
				btnShiftDown.setText("Copy Down");
				
				framePanel.repaint();
		    	
		    	return true;
		    } else if(e.getID() == KeyEvent.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_SHIFT) {
		    	
		    	copyframe = false;
		    	
		    	btnCopyPaste.setText("Paste Over");
				btnMinus.setText("Delete Frame");
				btnShiftRight.setText("Shift Right");
				btnShiftLeft.setText("Shift Left");
				btnShiftUp.setText("Shift Up");
				btnShiftDown.setText("Shift Down");
				
				framePanel.repaint();
				return true;
			    	
		    } else if(e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_U) {
		    	fd.undo();
		    	fd.repaint();
		    }
		    return false;
	
		  }

		});
		
		/*JComponent.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("c"), "copymode");
		tabbedPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("released c"), "origmode");
		tabbedPane.getActionMap().put("copymode", copy);
		tabbedPane.getActionMap().put("origmode", orig);		*/
		
		
		/*framePanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				
				System.out.println(e.getKeyCode());
				
				if(e.getKeyCode() == KeyEvent.VK_C) {	
					copyframe = !copyframe;
					
					
				}
				
				if(copyframe) {
					System.out.println(copyframe);
					
					btnCopyPaste.setText("Copy Paste");
					btnMinus.setText("Copy Delete");
					btnShiftRight.setText("Copy Right");
					btnShiftLeft.setText("Copy Left");
					btnShiftUp.setText("Copy Up");
					btnShiftDown.setText("Copy Down");
					
					frame.repaint();
				} else {
					
					btnCopyPaste.setText("Paste Over");
					btnMinus.setText("Delete Frame");
					btnShiftRight.setText("Shift Right");
					btnShiftLeft.setText("Shift Left");
					btnShiftUp.setText("Shift Up");
					btnShiftDown.setText("Shift Down");
					
					frame.repaint();
				}
			}
			
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
					
				}
			}
		});*/
		
	}
		
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
