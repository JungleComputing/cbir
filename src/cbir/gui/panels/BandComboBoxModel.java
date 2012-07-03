package cbir.gui.panels;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * @author Timo van Kessel
 * 
 */
public class BandComboBoxModel<E> extends AbstractListModel<E> implements
		ComboBoxModel<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6181665307916747672L;

	private E[] elements;
	private E selectedItem;

	/**
	 * 
	 */
	public BandComboBoxModel(E[] elements) {
		this.elements = elements;
		if(elements != null && elements.length > 0) {
			selectedItem = elements[0];
		}
	}

	public BandComboBoxModel(E[] elements, int selectedIndex) {
		this.elements = elements;
		if(elements != null && elements.length > 0) {
			if(selectedIndex >= 0 && selectedIndex < elements.length) {
				selectedItem = elements[selectedIndex];
			} else {
				selectedItem = elements[0];
			}
		}
	}

	public void setElements(E[] elements) {
		int lastElement = Math.max(this.elements.length, elements.length) -1;
		this.elements = elements;
		fireContentsChanged(this, 0, lastElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		if (elements == null) {
			return 0;
		}
		return elements.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public E getElementAt(int index) {
		if (index < 0 || elements == null || index >= elements.length) {
			return null;
		}
		return elements[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object anItem) {
		if ((selectedItem != null && !selectedItem.equals(anItem))
				|| selectedItem == null && anItem != null) {
			selectedItem = (E) anItem;
			fireContentsChanged(this, -1, -1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	@Override
	public E getSelectedItem() {
		return selectedItem;
	}

}
