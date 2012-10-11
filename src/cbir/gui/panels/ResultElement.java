package cbir.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cbir.MatchTable;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.gui.Gui;

public class ResultElement extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6738910752473359749L;

	public static final int SAMPLE_SIZE = 128;

	private final MatchTable table;
	private PreviewImage preview;
	private ImagePanel samplePanel;

	private Color defaultBackGround;

	public ResultElement(MatchTable table, Gui gui) {
		this.table = table;
		preview = null;
		init(gui);
	}

	private void init(Gui gui) {
		JPanel innerPanel = new JPanel();
		innerPanel.setOpaque(false);
		add(innerPanel);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		samplePanel = new ImagePanel(table.referenceImageID().tryGetPrettyName());
		innerPanel.add(samplePanel);
//		JLabel label = new JLabel(getImageID().getName());
//		label.setAlignmentX(Component.CENTER_ALIGNMENT);
//		innerPanel.add(label);
		setOpaque(true);
		defaultBackGround = getBackground();

//		ResultsPopupMenu popup = new ResultsPopupMenu(this, gui);
//		MouseAdapter ma = new PopupMouseAdapter(popup);
//		innerPanel.addMouseListener(ma);
//		samplePanel.addMouseListener(ma);
//		label.addMouseListener(ma);
	}

	public void addPreview(PreviewImage preview) {
		this.preview = preview;
		samplePanel.setImage(createSample());
	}

	private BufferedImage createSample() {
		// System.out.println("ResultElement.createSample() called for " +
		// table.referenceImageID());
		int width, height;
		BufferedImage bi = preview.getImage();
		if (bi.getWidth() > bi.getHeight()) {
			width = SAMPLE_SIZE;
			height = SAMPLE_SIZE * bi.getHeight() / bi.getWidth();
		} else {
			height = SAMPLE_SIZE;
			width = SAMPLE_SIZE * bi.getWidth() / bi.getHeight();
		}
		// Create new (blank) image of required (scaled) size
		BufferedImage sample = new BufferedImage(width, height, bi.getType());
		// Paint scaled version of image to new image
		Graphics2D graphics2D = sample.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(bi, 0, 0, width, height, null);
		// clean up
		graphics2D.dispose();
		return sample;
	}

	public ImageIdentifier getImageID() {
		return table.referenceImageID();
	}

	public JComponent getResultTile(boolean isSelected, boolean hasFocus) {
//		System.out.println("getResultTile for " + table.referenceImageID().getName());
		Color bg = getBackground();
		if (isSelected) {
			if (!bg.equals(Color.LIGHT_GRAY)) {
				setBackground(Color.LIGHT_GRAY);
				repaint();
			}
		} else if (hasFocus) {
			if (!bg.equals(Color.YELLOW)) {
				setBackground(Color.YELLOW);
				repaint();
			}
		} else {
			if (!bg.equals(Color.WHITE)) {
				setBackground(Color.WHITE);
				repaint();
			}
		}
		return this;
	}

	public MatchTable getTable() {
		return table;
	}

}
