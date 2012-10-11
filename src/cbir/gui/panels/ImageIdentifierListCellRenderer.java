package cbir.gui.panels;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import cbir.envi.ImageIdentifier;

/**
 * @author Timo van Kessel based on {@link DefaultListCellRenderer}
 */
public class ImageIdentifierListCellRenderer extends JLabel implements
		ListCellRenderer<ImageIdentifier> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1689352791146905069L;

	/**
	 * 
	 */
	public ImageIdentifierListCellRenderer() {
		setName("ImageIdentifier.cellRenderer");
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends ImageIdentifier> list, ImageIdentifier value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null) {
			setText("");
		} else {
			setText(value.tryGetPrettyName());
		}

		Color bg = null;
		Color fg = null;

		if (isSelected) {
			setBackground(bg == null ? list.getSelectionBackground() : bg);
			setForeground(fg == null ? list.getSelectionForeground() : fg);
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setEnabled(list.isEnabled());
		setFont(list.getFont());

		// Border border = null;
		// if (cellHasFocus) {
		// if (isSelected) {
		// border = DefaultLookup.getBorder(this, ui,
		// "List.focusSelectedCellHighlightBorder");
		// }
		// if (border == null) {
		// border = DefaultLookup.getBorder(this, ui,
		// "List.focusCellHighlightBorder");
		// }
		// } else {
		// border = getNoFocusBorder();
		// }
		// setBorder(border);
		return this;

	}

}
