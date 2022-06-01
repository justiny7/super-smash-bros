import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Picture {
	// Helper class to load in static images
	private Image img; 	
	private AffineTransform tx;
	private double scale = 0.5; // image scale
	
	private int x, y;
	
	public Picture(String image, double scale, int x, int y) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		
		img = getImage(image);
		tx = AffineTransform.getTranslateInstance(0, 0);
		init(x, y);
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		update();
		g2.drawImage(img, tx, null);
	}
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	private void update() {
		tx.setToTranslation(x, y);
		tx.scale(scale, scale);
	}
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scale, scale);
	}
	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Picture.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
