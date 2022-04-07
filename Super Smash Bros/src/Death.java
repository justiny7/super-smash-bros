import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.net.URL;

public class Death {
	private Image[] img = new Image[10000];
	private AffineTransform tx;
	private double scale = 3.5; // image scale
	
	private final int width = 1456, height = 849;
	private final int cx = width / 2, cy = height / 2; // center x, center y
	private final int pw = (int)(524 * scale), ph = (int)(156 * scale); // picture width/height
	private int imageCnt = 0;
	
	public Death() {		
		//y += (int)(300 * Math.sin(angle));
		//x += (int)(300 * Math.cos(angle));
		
		img[0] = getImage("/imgs/ringdeath.gif"); // load the image for Tree
		tx = AffineTransform.getTranslateInstance(0, 0);
	}
	
	public void paint(Graphics g, int x, int y) {
		// these are the 2 lines of code needed draw an image on the screen
		Graphics2D g2 = (Graphics2D) g;
		
		update(x, y);
		g2.drawImage(img[imageCnt], tx, null);
	}
	public void reset() {
		img[imageCnt++].flush();
		img[imageCnt] = getImage("/imgs/ringdeath.gif");
	}
	private void update(int x, int y) {
		if (x < 0)
			x = 0;
		if (x > width)
			x = width;
		if (y < 0)
			y = 0;
		if (y > height)
			y = height;
		
		double angle = Math.atan2(cy - y - 25, cx - x - 25);
		double len = Math.sqrt(pw * pw + ph * ph);
		int offsetX = (int)(Math.cos(angle) * len / 5);
		int offsetY = (int)(Math.sin(angle) * len / 5);
		tx.setToTranslation(x + 25 - pw / 2 + offsetX, y + 25 - ph / 2 + offsetY);
	    tx.rotate(angle, pw / 2, ph / 2); // rotate: target angle, coordinates for center of rotation
		tx.scale(scale, scale);
	}
	private Image getImage(String path) {
		Image tempImage = null;
		try {
			URL imageURL = Death.class.getResource(path);
			tempImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempImage;
	}

}
