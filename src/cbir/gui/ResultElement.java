package cbir.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cbir.MatchTable;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.gui.panels.ImagePanel;

public class ResultElement {

	public static final int SAMPLE_SIZE = 256;

	private final MatchTable table;
	private PreviewImage preview;
	private BufferedImage sample;

	private Color defaultBackGround;

	public ResultElement(MatchTable table) {
		this.table = table;
		preview = null;
		sample = null;
		defaultBackGround = null;
	}
	
	public void addPreview(PreviewImage preview) {
		this.preview = preview;
		createSample();
	}

	private void createSample() {
//		System.out.println("ResultElement.createSample() called for " + table.referenceImageID());
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
		sample = new BufferedImage(width, height, bi.getType());
		// Paint scaled version of image to new image
		Graphics2D graphics2D = sample.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(bi, 0, 0, width, height, null);
		// clean up
		graphics2D.dispose();
	}

	public ImageIdentifier getImageID() {
		return table.referenceImageID();
	}

	
	public ImagePanel getSamplePanel() {
		return new ImagePanel(sample);
	}

	public ImageIcon getIcon() {
		return new ImageIcon(sample);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public JComponent getResultTile(boolean isSelected, boolean hasFocus) {
//		System.out.println("ResultElement.getResultTile() called for " + table.referenceImageID());
		JPanel panel = new JPanel();
		JPanel innerPanel = new JPanel();
		panel.add(innerPanel);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		innerPanel.add(getSamplePanel());
		defaultBackGround = panel.getBackground();
		JLabel label = new JLabel(getImageID().getName());
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		innerPanel.add(label);
		
		Color bg = panel.getBackground();
		if(isSelected) {
			innerPanel.setBackground(Color.LIGHT_GRAY);
			if(!bg.equals(Color.LIGHT_GRAY)) {
				panel.setBackground(Color.LIGHT_GRAY);
			}
		} else if(hasFocus) {
			innerPanel.setBackground(Color.WHITE);
			if(!bg.equals(Color.WHITE)) {
				panel.setBackground(Color.WHITE);
			}
		} else {
			if(!bg.equals(defaultBackGround)) {
				panel.setBackground(defaultBackGround);
			}
		}
		return panel;
	}
	
	public MatchTable getTable() {
		return table;
	}
}
