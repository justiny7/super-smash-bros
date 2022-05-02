import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
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
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener, MouseMotionListener {
	// game properties
	private final int width = 1456, height = 849;
	private final long knockbackDelay = 200;
	private final int bboxW = 42, bboxH = 40;
	
	private int spawnL = 445, spawnR = 948, spawnH = height / 5;
	private long lstK = -1000, lstM = -1000;
	private boolean resetDeath = false, gameOver = false;
	
	private int KLives = 3, MLives = 3;
	
	// images + music
	private Character K = new Character(spawnL, spawnH, "k"); // Kirby
	private Character M = new Character(spawnR, spawnH, "m"); // Meta Knight
	private Death DK = new Death(), DM = new Death();
	private Background B = new Background(0, 0);
	private Picture KP = new Picture("/imgs/kirby.png", 0.5, spawnL - 155, 635);
	private Picture MP = new Picture("/imgs/metaknight.png", 0.45, spawnR - 185, 595);
	private Picture[] KLife = {new Picture("/imgs/kirbylives.png", 0.08, spawnL - 10, 720),
			new Picture("/imgs/kirbylives.png", 0.08, spawnL + 30, 720),
			new Picture("/imgs/kirbylives.png", 0.08, spawnL + 70, 720)};
	private Picture[] MLife = {new Picture("/imgs/metaknightlives.png", 0.11, spawnR + 30, 722),
			new Picture("/imgs/metaknightlives.png", 0.11, spawnR + 70, 722),
			new Picture("/imgs/metaknightlives.png", 0.11, spawnR + 110, 722)};
	private Picture game = new Picture("/imgs/gameover.png", 2.0, 60, 0);
	
	
	private long now() {
		return System.currentTimeMillis();
	}
	public void paint(Graphics g) {
		super.paintComponent(g);
		B.paint(g);
		K.paint(g);
		M.paint(g);
		KP.paint(g);
		MP.paint(g);
		
		if (gameOver)
			game.paint(g);
		
		for (int i = 0; i < MLives; ++i)
			MLife[i].paint(g);
		for (int i = 0; i < KLives; ++i)
			KLife[i].paint(g);
		
		// show(K, 0, g);
		// show(M, 1, g);
		
	    try {    	
			Font f = Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P.ttf")).deriveFont(30f);
			g.setFont(f);
			g.setColor(Color.WHITE);
			
			g.setColor(getColor(K.getPercentage()));
			g.drawString(String.format("%.1f%%", K.getPercentage()), spawnL - 10, 680);
			g.setColor(getColor(M.getPercentage()));
			g.drawString(String.format("%.1f%%", M.getPercentage()), spawnR + 30, 680);
			
			f = Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P.ttf")).deriveFont(20f);
			g.setFont(f);
			g.setColor(Color.WHITE);
			g.drawString("Kirby", spawnL - 10, 710);
			g.drawString("Meta Knight", spawnR + 30, 710);

		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    if (gameOver)
	    	return;

		if (K.getY() > height + 500) {
			if (!resetDeath) {
				--KLives;
				
				if (KLives > 0) {
					Music death = new Music("kosound.wav", false);
					death.play();
					DK.reset();
					resetDeath = true;
				} else {
					Music death = new Music("deathsound.wav", false);
					death.play();
					DK.reset();
					resetDeath = true;
				}
			}
			
			if (!gameOver)
				DK.paint(g, K.getX(), K.getY());
		} else if (hit(M, K) && now() - lstK > 100) {
			K.knockback(M.isRight());
			lstK = now();
		} else if (now() - lstK > knockbackDelay) {
			updatePositionK();
		}
		
		if (K.getY() > height + 4500 && KLives == 0)
			game.paint(g);
		if (K.getY() > height + 8000) {
			if (KLives > 0) {
				resetDeath = false;
				K.reset(spawnL, spawnH);
			} else {
				gameOver = true;
				K.freeze();
				M.freeze();
				return;
			}
		}
		
		
		if (M.getY() > height + 500) {
			if (!resetDeath) {
				--MLives;
				
				if (MLives > 0) {
					Music death = new Music("kosound.wav", false);
					death.play();
					DM.reset();
					resetDeath = true;
				} else {
					Music death = new Music("deathsound.wav", false);
					death.play();
					DM.reset();
					resetDeath = true;
				}
			}
			
			if (!gameOver)
				DM.paint(g, M.getX(), M.getY());
		} else if (hit(K, M) && now() - lstM > 100) {
			M.knockback(K.isRight());
			lstM = now();
		} else if (now() - lstM > knockbackDelay) {
			updatePositionM();
		}
		
		if (M.getY() > height + 4500 && MLives == 0)
			game.paint(g);
		if (M.getY() > height + 8000) {
			if (MLives > 0) {
				resetDeath = false;
				M.reset(spawnR, spawnH);
			} else {
				gameOver = true;
				K.freeze();
				M.freeze();
				return;
			}
		}
	}
	
	private Color getColor(double p) {
		// H = 60, S = 0, V = 1.0
		if (p < 20) // S goes from 0 - 1.0
			return Color.getHSBColor((float)(1.0 / 6), (float)(p / 20.0), 1);
		else // H drops to 0
			return Color.getHSBColor((float)(Math.max(0.0, 60.0 - (p - 20.0) * .6) / 360), 1, 1);
	}
	
	private void show(Character c, int col, Graphics g) {
		// show bounding box + attack point
		int[] pt = c.attackPoint();
		g.setColor((col == 0) ? Color.red : Color.blue);
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
				((between(al, ar, vl) || between(al, ar, vr)) && (between(ab, at, vb) || between(ab, at, vt))); // characters' bounding boxes intersect
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
		M.setKnock(false);
		if (pressedKeys.contains(KeyEvent.VK_ENTER)) {
			M.setVx(0);
			if (pressedKeys.contains(KeyEvent.VK_QUOTE) ||
				pressedKeys.contains(KeyEvent.VK_L) ||
				pressedKeys.contains(KeyEvent.VK_P)) {
				M.setAttack(true);
			} else {
				M.setAttack(false);
			}
			
			if (pressedKeys.contains(KeyEvent.VK_SEMICOLON))
				M.setVy(20);
			else if (pressedKeys.contains(KeyEvent.VK_P))
				M.jump();
		} else {
			M.setAttack(false);
			if (pressedKeys.contains(KeyEvent.VK_QUOTE))
				M.setVx(5);
			else if (pressedKeys.contains(KeyEvent.VK_L))
				M.setVx(-5);
			else
				M.setVx(0);
			
			if (pressedKeys.contains(KeyEvent.VK_SEMICOLON))
				M.setVy(20);
			else if (pressedKeys.contains(KeyEvent.VK_P))
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
