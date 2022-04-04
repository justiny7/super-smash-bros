import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener, MouseMotionListener {
	// game properties
	private int width = 1456, height = 849;
	private int bottom = 290, left = 95, right = 1170;
	
	// images + music
	Character K = new Character(250, 290); // Kirby
	Background B = new Background(0, 0);
	
	// character properties
	
	private int rand(int lo, int hi) { // random number between lo and hi
		return (int)(Math.random() * (hi - lo + 1)) + lo;
	}
	public void paint(Graphics g) {
		super.paintComponent(g);
		B.paint(g);
		K.paint(g);
		
		updatePosition();
	}
	
	private void updatePosition() {
		if (pressedKeys.contains(KeyEvent.VK_D))
			K.setVx(5);
		else if (pressedKeys.contains(KeyEvent.VK_A))
			K.setVx(-5);
		else
			K.setVx(0);
		
		if (pressedKeys.contains(KeyEvent.VK_S))
			K.setVy(20);
		else if (pressedKeys.contains(KeyEvent.VK_W))
			K.jump();
		
		/*
		if (pressedKeys.contains(KeyEvent.VK_D))
			vx1 = 5;
		if (pressedKeys.contains(KeyEvent.VK_D))
			vx1 = 5;
		if (pressedKeys.contains(KeyEvent.VK_D))
			vx1 = 5;
		if (pressedKeys.contains(KeyEvent.VK_D))
			vx1 = 5;
		*/
	}
	
	public static void main(String[] arg) {
		Music bg = new Music("theme.wav", true);
		bg.play();
		
		Frame f = new Frame();
	}
	
	public Frame() {
		JFrame f = new JFrame("Super Smash Bros");
		f.setSize(new Dimension(width, height));
		f.add(this);
		f.setResizable(false);
		f.setLayout(new GridLayout(1,2));
		f.addMouseListener(this);
		f.addMouseMotionListener(this);
		f.addKeyListener(this);
		
		Timer t = new Timer(10, this);
		t.start();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) { // remove cursor
		// TODO Auto-generated method stub
		Toolkit tk = Toolkit.getDefaultToolkit();
		Point hs = new Point(0, 0);
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
		Cursor c = tk.createCustomCursor(bi, hs, "Invisible");
		setCursor(c);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent arg0) { // mouse clicking logic
		// TODO Auto-generated method stub
		
	}
	private boolean between(int lo, int hi, int x) { // helper function for checking if lo <= x <= hi
		return lo <= x && x <= hi;
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) { // tracking mouse movement for crosshairs
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		repaint();
	}
	
	private HashSet<Integer> pressedKeys = new HashSet<>();
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		pressedKeys.add(arg0.getKeyCode());		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		pressedKeys.remove(arg0.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

}
