package cbir.gui.panels;

import ibis.constellation.extra.SortedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import cbir.backend.MultiArchiveIndex;
import cbir.envi.ImageIdentifier;

public class StoreIndexListModel extends AbstractListModel<ImageIdentifier> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 349486961637818646L;
	private ArrayList<ImageIdentifier> contents;

	public StoreIndexListModel() {
		contents = new ArrayList<ImageIdentifier>();
	}

	@Override
	public int getSize() {
		return contents.size();
	}

	@Override
	public ImageIdentifier getElementAt(int index) {
		if(index < 1 || index > contents.size()) {
			return null;
		}
		return contents.get(index-1);
	}

	public void updateIndex(MultiArchiveIndex mai) {
		Set<ImageIdentifier> images = mai.getUUIDIndex().keySet();
		TreeSet<ImageIdentifier> sortedIdentifiers = new TreeSet<>(images);
		// do not add the elements we already got
		sortedIdentifiers.removeAll(contents);
		if(images.isEmpty()) {
			return;
		}
				
//		contents = images.toArray(new ImageIdentifier[images.size()]);
//		System.out.println(String.format("StoreIndex: %d new images", sortedIdentifiers.size()));
		int index = 0;
		for(ImageIdentifier image: sortedIdentifiers) {
			while(index < contents.size() && contents.get(index).compareTo(image) <= 0) {
				index++;
			}
//			System.out.println(String.format("StoreIndex: Add image %s at position %d", image.getName(), index));
			contents.add(index, image);
			fireIntervalAdded(this, index, index);
			
			// sortedIdentifiers are sorted, so the next one we be larger than this one
			index++;
		}
		
//		System.out.println("indexcontents:");
//		for(ImageIdentifier i: contents) {
//			System.out.println(i.getName());
//		}
//		System.out.println("---");
	}
	
}
