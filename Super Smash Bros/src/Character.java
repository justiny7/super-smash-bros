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
	private double scale; // scales for images
	
	private final int bottom = 460, left = 180, right = 1215;
	
	private int x, y; // coordinates
	private int vx, vkx, vy, lst, offsetY, offsetX, charOffset; // vkx = knockback velocity
	private int ax, ay;
	private int jumpCnt;
	private long lstJump;
	private int hitCnt;
	private long lstHit;
	private double percentage;
	
	private boolean attack, knocked;
	
	private String name;
	
	// Constructor: initial position + name ("m" or "k" for Meta Knight or Kirby)
	public Character(int x, int y, String name) {	
		offsetY = offsetX = 0;
		vx = vkx = vy = 0;
		lst = 1;
		ay = 0;
		ax = 1;
		jumpCnt = 0;
		lstJump = -1000;
		percentage = 0;
		hitCnt = 0;
		lstHit = 0;

		attack = knocked = false;
		
		this.name = name;
		setPosition(x, y);
		
		// sets the correct sprite given the name
		if (name.equals("k")) {
			charOffset = 0;
			scale = 2.0;
			lst = 1;
			img = getImage("/imgs/kidleright.gif");
		} else {
			charOffset = -20;
			scale = 1.5;
			lst = -1;
			img = getImage("/imgs/midleleft.gif");
		}
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		init(x, y);
	}
	
	// paint function
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		update();
		g2.drawImage(img, tx, null);
	}
	
	// setters for x and y velocities
	public void setVx(int vx) {
		if (vx != 0)
			lst = this.vx;
		this.vx = vx;
	}
	public void setVy(int vy) {
		this.vy = vy;
	}
	
	// handles logic for jumping + double jumping
	public void jump() {
		if (jumpCnt < 2 && System.currentTimeMillis() - lstJump > 500) {
			Music jump = new Music(name + "jump" +
					(1 + (int)(Math.random() * 1000000) % 3) + ".wav", false);
			jump.play();
			
			++jumpCnt;
			lstJump = System.currentTimeMillis();
			vy = -20;
			ay = 1;
		}
	}
	
	// setter for whether the player is become knocked back
	public void setKnock(boolean knocked) {
		this.knocked = knocked;
	}
	// handles knockback movement + updates percentage
	public void knockback(boolean right) {
		if (now() - lstHit < 1500)
			++hitCnt;
		else
			hitCnt = 0;
		
		double mult = 5.0;
		if (hitCnt == 3) {
			mult = 25.0;
			hitCnt = 0;
		}
		lstHit = now();
		
		percentage += (Math.random() * mult) + 2.5;
		vkx = (int)(12 * (1 + 0.01 * percentage * 2) * (right ? 1 : -1));
		knocked = true;
	}
	
	// stops the player's motion
	public void freeze() {
		vx = vkx = vy = ax = ay = 0;
	}
	// checks if the player is facing right
	public boolean isRight() {
		return lst > 0;
	}
	
	// getters for player position
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	// resets the player to a certain position
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		vx = vy = vkx = 0;
		lst = (name.equals("m") ? -1 : 1);
		ay = ax = 0;
		jumpCnt = 0;
		lstJump = -1000;
		percentage = 0;
		hitCnt = 0;
		lstHit = 0;
	}
	
	// returns a pair of coordinates for the player's attacking bounding box
	public int[] attackPoint() {
		if (!attack)
			return new int[] {-10000, -10000};
		
		if (vy < 0) {
			if (lst > 0)
				return new int[] {x + 48, y - 15};
			else
				return new int[] {x - 5, y - 15};
		} else if (lst > 0) {
			return new int[] {x + 65, y + 25};
		} else {
			return new int[] {x - 27, y + 25};
		}
	}
	
	// setter for whether the player is attacking
	public void setAttack(boolean b) {
		attack = b;
	}
	// getter for percentage
	public double getPercentage() {
		return percentage;
	}
	
	// resets the player to a certain position
	public void reset(int x, int y) {
		if (name.equals("k"))
			img = getImage("/imgs/kidleright.gif");
		else
			img = getImage("/imgs/midleleft.gif");
		
		setPosition(x, y);
	}
	
	// initialization function
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scale, scale);
	}
	
	// updates character movement
	private void update() {	
		// set correct sprite
		if (knocked) {
			if (lst > 0) {
				img = getImage("/imgs/" + name + "idleright.gif");
				if (name.equals("k")) {
					offsetY = 0;
					offsetX = 0;
				} else {
					offsetY = -100;
					offsetX = -175;
				}
			} else {
				img = getImage("/imgs/" + name + "idleleft.gif");
				if (name.equals("k")) {
					offsetY = 0;
					offsetX = 0;
				} else {
					offsetY = -100;
					offsetX = -185;
				}
			}
		} else if (attack) {
			if (vy < 0) {
				if (lst > 0) {
					img = getImage("imgs/" + name + "attackupright.gif");
					if (name.equals("k"))
						offsetX = -20;
					else
						offsetX = 15;
				} else {
					img = getImage("imgs/" + name + "attackupleft.gif");
					if (name.equals("k"))
						offsetX = -10;
					else
						offsetX = 8;
				}
				
				if (name.equals("k"))
					offsetY = -26;
				else
					offsetY = -69;
			}  else if (lst > 0) {
				img = getImage("imgs/" + name + "attackright.gif");
				if (name.equals("k")) {
					offsetY = 0;
					offsetX = -9;
				} else {
					offsetY = -17;
					offsetX = -5;
				}
			} else {
				img = getImage("imgs/" + name + "attackleft.gif");
				if (name.equals("k")) {
					offsetY = 0;
					offsetX = -35;
				} else {
					offsetY = -17;
					offsetX = -46;
				}
			}
		} else if (vx > 0) {
			img = getImage("/imgs/" + name + "walkright.gif");
			if (name.equals("k")) {
				offsetY = -5;
				offsetX = 0;
			} else {
				offsetY = 0;
				offsetX = 10;
			}
		} else if (vx < 0) {
			img = getImage("/imgs/" + name + "walkleft.gif");
			if (name.equals("k"))
				offsetY = -5;
			else
				offsetY = 0;
			offsetX = 0;
		} else if (lst > 0) {
			img = getImage("/imgs/" + name + "idleright.gif");
			if (name.equals("k")) {
				offsetY = 0;
				offsetX = 0;
			} else {
				offsetY = -100;
				offsetX = -175;
			}
		} else {
			img = getImage("/imgs/" + name + "idleleft.gif");
			if (name.equals("k")) {
				offsetY = 0;
				offsetX = 0;
			} else {
				offsetY = -100;
				offsetX = -185;
			}
		}
		
		// handle change in x		
		if (y <= bottom)
			x += vx + vkx;
		else { // bounding box under sides of stadium
			if (x < left && x + vx + vkx >= left)
				x = left - 1;
			else if (x > right && x + vx + vkx <= right)
				x = right + 1;
			else
				x += vx + vkx;
		}
		
		// handle change in y
		if (x < left || x > right)
			y += vy;
		else if (y <= bottom) // bounding box on top of stadium
			y = Math.min(y + vy, bottom);
		
		// handle change in acceleration
		if (y == bottom && x >= left && x <= right) {
			ay = 0;
			jumpCnt = 0;
		} else if (y > bottom) {  // fall slower when below stadium
			ay ^= 1;
		} else {
			ay = 1;
		}

		vy += ay;
		
		if (vkx < 0)
			vkx += ax;
		else if (vkx > 0)
			vkx -= ax;
		
		ax ^= 1;
		
		tx.setToTranslation(x + offsetX + charOffset, y + offsetY);
		tx.scale(scale, scale);
	}
	
	// helper function to get current time
	private long now() {
		return System.currentTimeMillis();
	}
	
	// helper function to retrieve an image
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
