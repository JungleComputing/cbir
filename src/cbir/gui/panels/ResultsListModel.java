package cbir.gui.panels;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JPopupMenu;

import cbir.MatchTable;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;
import cbir.gui.Gui;

public class ResultsListModel extends AbstractListModel<ResultElement> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6817570621344857254L;
	private ArrayList<ResultElement> contents;

	public ResultsListModel() {
		contents = new ArrayList<ResultElement>();
	}

	@Override
	public int getSize() {
		return contents.size();
	}

	@Override
	public ResultElement getElementAt(int index) {
		return contents.get(index);
	}

	public void clear() {
		int lastElement = contents.size()-1;
		contents.clear();
		if(lastElement > 0) {
			fireIntervalRemoved(this, 0, lastElement);
		}
		
	}

	public void add(ResultElement element) {
		int index = contents.size();
		contents.add(element);
		fireIntervalAdded(this, index, index);
	}

	public void deliverPreview(PreviewImage preview) {
		int i = 0;
		ImageIdentifier id = preview.getImageID();
		for(ResultElement element: contents) {
			if(id.equals(element.getImageID())) {
				element.addPreview(preview);
				fireContentsChanged(this, i, i);
			}
			i++;
		}
		
	}

	public void updateResults(MatchTable[] matchData, Gui gui) {
		clear();
		for(MatchTable table: matchData) {
			if(table != null) {
				contents.add(new ResultElement(table, gui));
				
			}
		}
		fireIntervalAdded(this, 0, contents.size()-1);
	}

	public ResultElement[] getContents() {
		return contents.toArray(new ResultElement[contents.size()]);
	}
	
}
