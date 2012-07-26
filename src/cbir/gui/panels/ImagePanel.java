package cbir.gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8797681774859111758L;
	private BufferedImage image;

	public ImagePanel() {
		setPreferredSize(new Dimension(100, 100));
		setMinimumSize(getPreferredSize());
	}

	public ImagePanel(BufferedImage image) {
		this();
		setImage(image);
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		setMinimumSize(getPreferredSize());
		revalidate();
		repaint();
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (image != null) {
			// center the image if the paint area is larger than the image
			int hOffset = Math.max(0, (getWidth() - image.getWidth()) / 2);
			int vOffset = Math.max(0, (getHeight() - image.getHeight()) / 2);
			// if(vOffset > 0 || hOffset > 0) {
			// g.setColor(Color.LIGHT_GRAY);
			// g.fillRect(0, 0, getWidth()-1, getHeight()-1);
			// }
			g.drawImage(image, hOffset, vOffset, this);
		} else {
			g.setColor(Color.PINK);
			g.drawString("no file loaded", 20, 20);
		}
	}
}
