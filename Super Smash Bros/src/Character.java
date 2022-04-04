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
	private double scale = 0.2; // scales for images
	
	private final int bottom = 290, left = 95, right = 1170;
	
	private int x, y; // coordinates
	private int vx, vy;
	private int accel;
	private int jumpCnt;
	private long lstJump;

	public Character(int x, int y) {
		this.x = x;
		this.y = y;
		
		vx = vy = 0;
		accel = 0;
		jumpCnt = 0;
		lstJump = -1000;

		img = getImage("/imgs/char.png"); // load the image for Tree
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
		this.vx = vx;
	}
	public void setVy(int vy) {
		this.vy = vy;
	}
	public void jump() {
		if (jumpCnt < 2 && System.currentTimeMillis() - lstJump > 250) {
			++jumpCnt;
			lstJump = System.currentTimeMillis();
			vy = -20;
			accel = 1;
		}
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scale, scale);
	}
	private void update() {
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
		
		tx.setToTranslation(x, y);
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
