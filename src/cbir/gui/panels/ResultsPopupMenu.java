package cbir.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cbir.gui.Gui;

public class ResultsPopupMenu extends JPopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6372841419708030716L;
	
	private ResultElement re;
	private JLabel label;
	
	public ResultsPopupMenu(final Gui gui) {
		re = null;
		ActionListener menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(re != null) {
					gui.setQueryFromResults(re);
				}
			}
		};
		label = new JLabel("");
		add(label);
		JMenuItem forQuery = new JMenuItem("Set as Query");
		forQuery.addActionListener(menuListener);
		add(forQuery);
	}

	public void setSelectedItem(ResultElement selectedValue) {
		re = selectedValue;
		setLabel(re.getImageID().getPrettyName());
		label.setText(re.getImageID().getPrettyName());
		label.revalidate();
	}
}
