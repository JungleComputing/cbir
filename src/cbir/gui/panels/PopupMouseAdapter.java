package cbir.gui.panels;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

public final class PopupMouseAdapter extends MouseAdapter {
	
	private final ResultsPopupMenu popup;
	
	public PopupMouseAdapter(ResultsPopupMenu popup) {
		this.popup = popup;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
//		System.out.println("mouse pressed " );
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
//		System.out.println("mouse released");
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
//			System.out.println("show popup");
			
			ResultElement re = ((JList<ResultElement>)(e.getComponent())).getSelectedValue();
			if(re != null) {
				popup.setSelectedItem(re);
			}
			popup.show(e.getComponent(), e.getX(), e.getY());
			
		} else {
//			System.out.println("do not show popup");
		}
	}
}
