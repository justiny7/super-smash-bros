import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Amogus {
	private Image img; 	
	private AffineTransform tx;
	private double scale = 0.1, aliveScale = 0.1, deadScale = 0.15; // scales for images
	
	private int x, y; // coordinates
	private int col; // current color
	private int angle = 0, aSpeed; // current target angle, rotation speed
	
	private String curCol;

	public Amogus(int x, int y) {
		this.x = x;
		this.y = y;
		
		tx = AffineTransform.getTranslateInstance(0, 0);
		
		col = (int)Math.random() * 6;
		changePicture();
	}
	
	private String getColor(int x) { // code 0 - 5 --> color string
		switch (x) {
		case 0:
			return "red";
		case 1:
			return "black";
		case 2:
			return "cyan";
		case 3:
			return "green";
		case 4:
			return "pink";
		default:
			return "purple";
		}
	}
	public void changePicture() { // change to different color live among us
		scale = aliveScale;
		
		aSpeed = (int)(Math.random() * 5) + 3; // set random rotation speed
		int newCol;
		do {
			newCol = (int)(Math.random() * 6 * 100) % 6;
		} while (newCol == col);
		
		col = newCol;
		curCol = getColor(col);
		String path = "/imgs/" + curCol + "amogus.png";
		img = getImage(path);
		init(0, 0);
	}
	public void kill() { // set image to dead among us when killed
		String path = "/imgs/" + curCol + "body.png";
		img = getImage(path);
		scale = deadScale;
	}
	public void paint(Graphics g) {
		// these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		update();
		g2.drawImage(img, tx, null);
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
		tx.setToTranslation(x, y);
	    tx.rotate(Math.toRadians(angle), 31, 38); // rotate: target angle, coordinates for center of rotation
		tx.scale(scale, scale);
		
		angle = (angle + aSpeed) % 360;
	}
	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Amogus.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
