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
	private double percentage;
	
	private boolean attack, knocked;
	
	private String name;

	public Character(int x, int y, String name) {	
		offsetY = offsetX = 0;
		vx = vkx = vy = 0;
		lst = 1;
		ay = 0;
		ax = 1;
		jumpCnt = 0;
		lstJump = -1000;
		percentage = 0;
		
		attack = knocked = false;
		
		this.name = name;
		setPosition(x, y);
		
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
		if (jumpCnt < 2 && System.currentTimeMillis() - lstJump > 500) {
			if (name.equals("k")) {
				Music jump = new Music(name + "jump" + (1 + (int)(Math.random() * 1000000) % 3) + ".wav", false);
				jump.play();
			}
			
			++jumpCnt;
			lstJump = System.currentTimeMillis();
			vy = -20;
			ay = 1;
		}
	}
	public void setKnock(boolean knocked) {
		this.knocked = knocked;
	}
	public void knockback(boolean right) {
		percentage += (Math.random() * 5) + 2.5;
		vkx = (int)(12 * (1 + 0.01 * percentage) * (right ? 1 : -1)); // eventually, knockback depends on character percentage
		knocked = true;
	}
	public boolean isRight() {
		return lst > 0;
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
		vx = vy = vkx = 0;
		lst = (name.equals("m") ? -1 : 1);
		ay = ax = 0;
		jumpCnt = 0;
		lstJump = -1000;
		percentage = 0;
	}
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
	public void setAttack(boolean b) {
		attack = b;
	}
	public double getPercentage() {
		return percentage;
	}
	public void reset(int x, int y) {
		if (name.equals("k"))
			img = getImage("/imgs/kidleright.gif");
		else
			img = getImage("/imgs/midleleft.gif");
		
		setPosition(x, y);
	}
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scale, scale);
	}
	private void update() {	
		// set correct sprite
		if (knocked) {
			if (lst > 0) {
				img = getImage("imgs/" + name + "idleright.gif");
				offsetY = 0;
				offsetX = 0;
			} else {
				img = getImage("imgs/" + name + "idleleft.gif");
				offsetY = 0;
				offsetX = 0;
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
						; // DO ATTACKUPLEFT
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
				offsetY = -15;
				offsetX = 10;
			}
		} else if (vx < 0) {
			img = getImage("/imgs/" + name + "walkleft.gif");
			if (name.equals("k"))
				offsetY = -5;
			else
				offsetY = -15;
			offsetX = 0;
		} else if (lst > 0) {
			img = getImage("/imgs/" + name + "idleright.gif");
			offsetY = 0;
			if (name.equals("k"))
				offsetX = 0;
			else
				offsetX = 10;
		} else {
			img = getImage("/imgs/" + name + "idleleft.gif");
			offsetY = 0;
			offsetX = 0;
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
