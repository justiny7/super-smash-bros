import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Lost {
	private Image img; 	
	private AffineTransform tx;
	private double scale = 1.8; // image scale
	private boolean changed = false; // have we finished the gif yet?

	public Lost() {
		img = getImage("/imgs/lost.gif");
		tx = AffineTransform.getTranslateInstance(0, 0);
		init(0, 30); // initialize the location of the image
	}
	
	public void changePicture() { // change from gif to still "defeat" image
		if (changed)
			return;
		
		changed = true;
		scale = 0.697;
		img = getImage("/imgs/lost.jpg");
		init(0, 30);
	}
	public void paint(Graphics g) {
		// these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(img, tx, null);
	}
	private void init(double a, double b) {
		tx.setToTranslation(a, b);
		tx.scale(scale, scale);
	}
	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Lost.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
