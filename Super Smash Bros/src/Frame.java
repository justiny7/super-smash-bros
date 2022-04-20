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
	private final int width = 1456, height = 849;
	private final int bottom = 290, left = 95, right = 1170;
	private final long knockbackDelay = 200;
	private final int bboxW = 42, bboxH = 40;
	
	private int spawnL = 445, spawnR = 948, spawnH = height / 5;
	private long lstK = -1000, lstM = -1000;
	private boolean resetDeath = false;
	
	// images + music
	Character K = new Character(spawnL, spawnH, "k"); // Kirby
	Character M = new Character(spawnR, spawnH, "k"); // Meta Knight
	Death DK = new Death(), DM = new Death();
	Background B = new Background(0, 0);
	
	// character properties
	
	private int rand(int lo, int hi) { // random number between lo and hi
		return (int)(Math.random() * (hi - lo + 1)) + lo;
	}
	private long now() {
		return System.currentTimeMillis();
	}
	public void paint(Graphics g) {
		super.paintComponent(g);
		B.paint(g);
		K.paint(g);
		M.paint(g);
		
		show(K, g);
		show(M, g);
		
		if (K.getY() > height + 500) {
			if (!resetDeath) {
				Music death = new Music("kosound.wav", false);
				death.play();
				DK.reset();
				resetDeath = true;
			}
			DK.paint(g, K.getX(), K.getY());
		} else if (hit(M, K)) {
			System.out.println("M hit K!");
		} else if (now() - lstK > knockbackDelay) {
			updatePositionK();
		}
		
		if (K.getY() > height + 8000) {
			resetDeath = false;
			K.reset(spawnL, spawnH);
		}
		
		
		if (M.getY() > height + 500) {
			if (!resetDeath) {
				Music death = new Music("kosound.wav", false);
				death.play();
				DM.reset();
				resetDeath = true;
			}
			DM.paint(g, M.getX(), M.getY());
		} else if (hit(K, M)) {
			System.out.println("K hit M!");
		} else if (now() - lstM > knockbackDelay) {
			updatePositionM();
		}
		
		if (M.getY() > height + 8000) {
			resetDeath = false;
			M.reset(spawnR, spawnH);
		}
	}
	
	private void show(Character c, Graphics g) {
		// show bounding box + attack point
		int[] pt = c.attackPoint();
		g.setColor(Color.red);
		g.fillRect(pt[0], pt[1], 10, 10);
		g.drawRect(c.getX() + 3, c.getY() + 10, bboxW, bboxH);
	}
	private boolean between(int lo, int hi, int x) { // helper function for checking if lo <= x <= hi
		return lo <= x && x <= hi;
	}
	
	private boolean hit(Character attacker, Character victim) {
		int[] pt = attacker.attackPoint();
		if (pt[0] == -10000)
			return false;
		
		int hl = pt[0], hr = pt[0] + 10, hb = pt[1], ht = pt[1] + 10;
		int al = attacker.getX() + 3, ar = al + bboxW, ab = attacker.getY() + 10, at = ab + bboxH;
		int vl = victim.getX() + 3, vr = vl + bboxW, vb = victim.getY() + 10, vt = vb + bboxH;
		
		return (hl >= vl && hr <= vr && hb >= vb && ht <= vt) || // attack point inside bounding box
				((between(al, ar, vl) || between(al, ar, vr)) && (between(ab, at, vb) || between(ab, at, vt)));
	}
	private void updatePositionK() {
		K.setKnock(false);
		if (pressedKeys.contains(KeyEvent.VK_F)) {
			K.setVx(0);
			if (pressedKeys.contains(KeyEvent.VK_D) ||
				pressedKeys.contains(KeyEvent.VK_A) ||
				pressedKeys.contains(KeyEvent.VK_W)) {
				K.setAttack(true);
			} else {
				K.setAttack(false);
			}
			
			if (pressedKeys.contains(KeyEvent.VK_S))
				K.setVy(20);
			else if (pressedKeys.contains(KeyEvent.VK_W))
				K.jump();
		} else {
			K.setAttack(false);
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
		}
	}
	
	private void updatePositionM() {
		if (pressedKeys.contains(KeyEvent.VK_SHIFT)) {
			M.setVx(0);
			if (pressedKeys.contains(KeyEvent.VK_RIGHT) ||
				pressedKeys.contains(KeyEvent.VK_LEFT) ||
				pressedKeys.contains(KeyEvent.VK_UP)) {
				M.setAttack(true);
			} else {
				M.setAttack(false);
			}
			
			if (pressedKeys.contains(KeyEvent.VK_DOWN))
				M.setVy(20);
			else if (pressedKeys.contains(KeyEvent.VK_UP))
				M.jump();
		} else {
			M.setAttack(false);
			if (pressedKeys.contains(KeyEvent.VK_RIGHT))
				M.setVx(5);
			else if (pressedKeys.contains(KeyEvent.VK_LEFT))
				M.setVx(-5);
			else
				M.setVx(0);
			
			if (pressedKeys.contains(KeyEvent.VK_DOWN))
				M.setVy(20);
			else if (pressedKeys.contains(KeyEvent.VK_UP))
				M.jump();
		}
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
