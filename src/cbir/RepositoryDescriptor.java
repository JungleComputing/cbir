package cbir;

import java.io.Serializable;

import org.gridlab.gat.URI;

public class RepositoryDescriptor implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1589214930380665204L;
    private final String name;
    private final URI baseURI;
    
    public RepositoryDescriptor(String name, URI baseURI) {
        this.name = name;
        this.baseURI = baseURI;
    }
    
    public String getName() {
        return name;
    }
    
    public URI getBaseURI() {
        return baseURI;
    }
}
