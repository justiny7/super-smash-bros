import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Character {
	private Image img; 	
	private AffineTransform tx;
	private double scale = 2.0; // scales for images
	
	private final int width = 1456, height = 709;
	private final int bottom = 460, left = 180, right = 1215;
	
	private int x, y; // coordinates
	private int vx, vy, lst, offsetY, offsetX;
	private int accel;
	private int jumpCnt;
	private long lstJump;
	
	private boolean attack, tornado;
	
	private String name;

	public Character(int x, int y, String name) {
		setPosition(x, y);
		
		offsetY = offsetX = 0;
		vx = vy = 0;
		lst = 1;
		accel = 0;
		jumpCnt = 0;
		lstJump = -1000;
		
		attack = false;
		tornado = false;
		
		this.name = name;
		
		if (name.equals("k"))
			img = getImage("/imgs/kwalkright.gif");
		else
			;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		init(x, y);
	}

	public void paint(Graphics g) {
		// these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		update();
		g2.drawImage(img, tx, null);
	}
	
	public void setVx(int vx) {
		if (vx != 0)
			lst = this.vx;
		this.vx = vx;
	}
	public void setVy(int vy) {
		this.vy = vy;
	}
	public void jump() {
		if (jumpCnt < 2 && System.currentTimeMillis() - lstJump > 250) {
			Music jump = new Music(name + "jump" + (1 + (int)(Math.random() * 1000000) % 3) + ".wav", false);
			jump.play();
			
			++jumpCnt;
			lstJump = System.currentTimeMillis();
			vy = -20;
			accel = 1;
		}
	}
	
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		offsetY = 0;
		vx = vy = 0;
		lst = 1;
		accel = 0;
		jumpCnt = 0;
		lstJump = -1000;
	}
	public void setAttack(boolean b) {
		attack = b;
	}
	public void reset(int x, int y) {
		setPosition(x, y);
	}
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scale, scale);
	}
	private void update() {
		// System.out.println(x + " " + y);
		
		// set correct sprite
		if (tornado) {
			img = getImage("/imgs/" + name + "tornado.gif");
			offsetY = -13;
			offsetX = 0;
		} else if (attack) {
			if (y < bottom) {
				if (lst > 0)
					img = getImage("imgs/" + name + "attackupright.gif");
				else
					img = getImage("imgs/" + name + "attackupleft.gif");
				
				offsetY = -28;
				offsetX = 0;
			}  else if (lst > 0) {
				img = getImage("imgs/" + name + "attackright.gif");
				offsetY = 0;
				offsetX = 0;
			} else {
				img = getImage("imgs/" + name + "attackleft.gif");
				offsetY = 0;
				offsetX = -35;
			}
		} else if (vx > 0) {
			img = getImage("/imgs/" + name + "walkright.gif");
			offsetY = -5;
			offsetX = 0;
		} else if (vx < 0) {
			img = getImage("/imgs/" + name + "walkleft.gif");
			offsetY = -5;
			offsetX = 0;
		} else if (lst > 0) {
			img = getImage("/imgs/" + name + "idleright.gif");
			offsetY = 0;
			offsetX = 0;
		} else {
			img = getImage("/imgs/" + name + "idleleft.gif");
			offsetY = 0;
			offsetX = 0;
		}
		
		// handle change in x		
		if (y <= bottom)
			x += vx;
		else { // bounding box under sides of stadium
			if (x < left && x + vx >= left)
				x = left - 1;
			else if (x > right && x + vx <= right)
				x = right + 1;
			else
				x += vx;
		}
		
		// handle change in y
		if (x < left || x > right)
			y += vy;
		else if (y <= bottom) // bounding box on top of stadium
			y = Math.min(y + vy, bottom);
		
		// handle change in acceleration
		if (y == bottom && x >= left && x <= right) {
			accel = 0;
			jumpCnt = 0;
		} else if (y > bottom) {  // fall slower when below stadium
			accel ^= 1;
		} else {
			accel = 1;
		}

		vy += accel;
		
		tx.setToTranslation(x + offsetX, y + offsetY);
		tx.scale(scale, scale);
	}
	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Character.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
