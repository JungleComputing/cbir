package cbir.gui.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

import cbir.backend.MultiArchiveIndex;
import cbir.backend.SingleArchiveIndex;
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
        if (index < 1 || index > contents.size()) {
            return null;
        }
        return contents.get(index - 1);
    }

    public void updateIndex(SingleArchiveIndex sai) {
        
        ImageIdentifier[] images = sai.getImageIDs();
        Arrays.sort(images);

        int index = 0;
        for (ImageIdentifier image : images) {
            while (index < contents.size()
                    && contents.get(index).compareTo(image) < 0) {
                index++;
            }
            
            if(index == contents.size() || contents.get(index).compareTo(image) != 0) {
                //we do not have this image yet
                contents.add(index, image);
                fireIntervalAdded(this, index, index);
                // images are sorted, so the next one will be larger than
                // this one
                index++;
            } else {
                System.out.println("Cannot add image to StoreIndexList: " +image.tryGetPrettyName());
            }
        }

        // System.out.println("indexcontents:");
        // for(ImageIdentifier i: contents) {
        // System.out.println(i.getName());
        // }
        // System.out.println("---");
    }

    // public void updateIndex(MultiArchiveIndex mai) {
    // Set<ImageIdentifier> images = mai.getUUIDIndex().keySet();
    // TreeSet<ImageIdentifier> sortedIdentifiers = new TreeSet<>(images);
    // // do not add the elements we already got
    // sortedIdentifiers.removeAll(contents);
    // if(sortedIdentifiers.isEmpty()) {
    // return;
    // }
    //
    // // contents = images.toArray(new ImageIdentifier[images.size()]);
    // // System.out.println(String.format("StoreIndex: %d new images",
    // sortedIdentifiers.size()));
    // int index = 0;
    // for(ImageIdentifier image: sortedIdentifiers) {
    // while(index < contents.size() && contents.get(index).compareTo(image) <=
    // 0) {
    // index++;
    // }
    // //
    // System.out.println(String.format("StoreIndex: Add image %s at position %d",
    // image.getName(), index));
    // contents.add(index, image);
    // fireIntervalAdded(this, index, index);
    //
    // // sortedIdentifiers are sorted, so the next one will be larger than this
    // one
    // index++;
    // }
    //
    // // System.out.println("indexcontents:");
    // // for(ImageIdentifier i: contents) {
    // // System.out.println(i.getName());
    // // }
    // // System.out.println("---");
    // }

}
