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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Frame extends JPanel implements ActionListener, MouseListener, KeyListener, MouseMotionListener {
	// game properties
	private int aCnt = 2;
	private int width = 900, height = 600;
	private int mX, mY;
	private int[] frames = new int[aCnt];
	private int cR1 = 10, cR2 = 30, cL = 40; // crosshair radii, crosshair length
	private int score = 0;
	
	// images + music
	Amogus[] A = new Amogus[aCnt];
	Background B = new Background(0, 0);
	Lost L = new Lost();
	Knife[] knives = new Knife[3];
	
	// character properties
	private int[] aX = new int[aCnt], aY = new int[aCnt];
	private int[] dX = new int[aCnt], dY = new int[aCnt];
	private int aW = 55, aH = 73;
	private int lives = 3, lostID;
	private boolean[] dead = new boolean[aCnt], caught = new boolean [aCnt];
	private boolean lost = false, tooLong = false;
	private boolean first = true;
	
	private void reset(int i) { // reset round
		aY[i] = height - 100;
		aX[i] = rand(0, width);
		dX[i] = rand(4, 12);
		dY[i] = dX[i] - 16;
		
		if (rand(0, 100) % 2 == 0)
			dX[i] *= -1;

		frames[i] = 0;
	}
	private int rand(int lo, int hi) { // random number between lo and hi
		return (int)(Math.random() * (hi - lo + 1)) + lo;
	}
	public void paint(Graphics g) {
		super.paintComponent(g);
		B.paint(g);
		
		if (first) {
			for (int i = 0; i < aCnt; ++i) {
				caught[i] = true;
				A[i] = new Amogus(0, 0);
			}
		}
				
		if (lost) { // losing animation
			++frames[lostID];
			
			if (frames[lostID] >= 35)
				L.paint(g);
			
			if (frames[lostID] >= 140) {
				L.changePicture();
				
				Font font = new Font("Courier New", Font.BOLD, 60);
				g.setFont(font);
				
				g.setColor(Color.WHITE);
				g.drawString("Score: " + score, 300, 350);
			}
		} else if (tooLong) { // have we took too long to hit the target?
			aY[lostID] -= 5;
			
			if (aY[lostID] + 100 < 0) // floating up animation
				lost = true;
		} else {
			for (int i = 0; i < aCnt; ++i) {
				if (dead[i]) { // dying animation
					aY[i] += 5;
					
					if (aY[i] >= height) {
						reset(i);
						
						A[i].changePicture();
						dead[i] = false;
					}
				} else if (!caught[i]) {
					// handle wall collisions
					if ((aY[i] + 100 >= height && dY[i] > 0) || (aY[i] <= 0 && dY[i] < 0))
						dY[i] *= -1;
					if ((aX[i] + 62 >= width && dX[i] > 0) || (aX[i] <= 0 && dX[i] < 0))
						dX[i] *= -1;
					
					aX[i] += dX[i];
					aY[i] += dY[i];
					
					++frames[i];
				} else {
					caught[i] = false;
					
					if (first) {
						reset(i);
						
						for (int j = 0; j < lives; ++j)
							knives[j] = new Knife(10 + 55 * j, 500);
					} else {
						++score;
						
						Music death = new Music("kill.wav", false);
						death.play();
						
						A[i].kill();
						dead[i] = true;
					}
				}
				
				if (frames[i] == 500) {
					Music lost = new Music("lost.wav", false);
					lost.play();
					
					lostID = i;
					
					tooLong = true;
					++frames[i];
				}
			}
		}
		
		if (first)
			first = false;
		
		if (!lost) { // show score and number of hits left
			for (int i = 0; i < aCnt; ++i) {
				A[i].setPosition(aX[i], aY[i]);
				A[i].paint(g);
			}
			
			crosshairs(g);
			for (int i = 0; i < lives; ++i)
				knives[i].paint(g);
			
			Font font = new Font("Courier New", Font.BOLD, 25);
			g.setFont(font);
			
			g.setColor(Color.WHITE);
			g.drawString("Score: " + score, 10, 475);
		}
	}
	
	private void crosshairs(Graphics g) { // draw crosshairs
		Graphics2D g2 = (Graphics2D) g;
		
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(3));
		g2.drawOval(mX - cR1, mY - cR1, cR1 * 2, cR1 * 2);
		g2.drawOval(mX - cR2, mY - cR2, cR2 * 2, cR2 * 2);
		
		g2.setStroke(new BasicStroke(5));
		g2.drawLine(mX - cL, mY, mX + cL, mY);
		g2.drawLine(mX, mY - cL, mX, mY + cL);
	}
	
	public static void main(String[] arg) {
		Music bg = new Music("drip.wav", true);
		bg.play();
		
		Frame f = new Frame();
	}
	
	public Frame() {
		JFrame f = new JFrame("Among Us Hunt");
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
		if (lost || tooLong)
			return;
		
		mX = arg0.getX() - 6;
		mY = arg0.getY() - 27;
		
		boolean hit = false;
		
		for (int i = 0; i < aCnt; ++i) {
			if (between(aX[i], aX[i] + aW, mX) && between(aY[i], aY[i] + aH, mY)) { // hit the target
				caught[i] = hit = true;
				lives = 3;
			}
		}
		
		if (!hit) { // didn't hit either target
			--lives;
			if (lives == 0) {
				for (int i = 0; i < aCnt; ++i)
					frames[i] = 0;
				lost = true;
				
				Music lost = new Music("lost.wav", false);
				lost.play();
			} else {
				Music miss = new Music("miss.wav", false);
				miss.play();
			}
		}
	}
	private boolean between(int lo, int hi, int x) { // helper function for checking if lo <= x <= hi
		return lo <= x && x <= hi;
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) { // tracking mouse movement for crosshairs
		mX = arg0.getX() - 6;
		mY = arg0.getY() - 27;
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

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
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
