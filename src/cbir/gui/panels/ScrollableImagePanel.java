package cbir.gui.panels;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class ScrollableImagePanel extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5628800007410427784L;
	private ImagePanel imagePanel;
	
	public ScrollableImagePanel(String title) {
		this();
		setBorder(new TitledBorder(null, title,
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		setPreferredSize(new Dimension(400, 400));
//		setMinimumSize(new Dimension(400, 400));
	}
	
	public ScrollableImagePanel(String title, Dimension viewPortSize) {
		this(title);
		getViewport().setPreferredSize(viewPortSize);
		getViewport().setMinimumSize(viewPortSize);
//		setPreferredSize(new Dimension(400, 400));
//		setMinimumSize(new Dimension(400, 400));
	}
	
	public ScrollableImagePanel(Dimension viewPortSize) {
		this();
		getViewport().setPreferredSize(viewPortSize);
		getViewport().setMinimumSize(viewPortSize);
//		setPreferredSize(new Dimension(400, 400));
//		setMinimumSize(new Dimension(400, 400));
	}
	
	public ScrollableImagePanel() {
		super();
		imagePanel = new ImagePanel();
		setViewportView(imagePanel);
//		setPreferredSize(new Dimension(400, 400));
//		setMinimumSize(new Dimension(400, 400));
	}
	
	public ScrollableImagePanel(String title, BufferedImage image) {
		this(title);
		setImage(image);
	}
	
	public void setImage(BufferedImage image) {
		imagePanel.setImage(image);
//		revalidate();
//		repaint();
	}
}
