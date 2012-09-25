package cbir.backend.repository;

import cbir.envi.EnviHeader;
import cbir.envi.FloatImage;
import cbir.envi.ImageIdentifier;
import cbir.envi.PreviewImage;

/**
 * @author Timo van Kessel
 *
 */
public interface Repository {
	
//	String getName();
	
	EnviHeader getHeader(ImageIdentifier imageID, String... repositories);
	
	FloatImage getImage(EnviHeader header, String... repositories);

	PreviewImage getPreview(EnviHeader header, int red, int green, int blue, String... repositories);

	
}
