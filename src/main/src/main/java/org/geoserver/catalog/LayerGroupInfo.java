/* Copyright (c) 2001 - 2008 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;

import java.io.Serializable;
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * A map in which the layers grouped together can be referenced as 
 * a regular layer.
 * 
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public interface LayerGroupInfo extends CatalogInfo {

    /**
     * Enumeration for type of layer group.
     */
    public enum Type {
        SINGLE {
            public String getName() {
                return "Single";
            }
            
            public Integer getCode() {
                return 0;
            }
        },
        NAMED {
            public String getName() {
                return "Named Tree";
            }
                        
            public Integer getCode() {
                return 1;
            }
        },
        CONTAINER {
            public String getName() {
                return "Container Tree";
            }
                                    
            public Integer getCode() {
                return 2;
            }

        },
        EO {
            public String getName() {
                return "Earth Observation Tree";
            }
                            
            public Integer getCode() {
                return 3;
            }
        };

        public abstract String getName();
        public abstract Integer getCode();
    }
	
    /**
     * The name of the layer group.
     */
    String getName();

    /**
     * Sets the name of the layer group.
     */
    void setName( String name );
    
    /**
     * The type of the layer group.
     */
    Type getType();

    /**
     * Sets the type of the layer group.
     */
    void setType( Type type );    
    
    /**
     * The title of the layer group.
     */
    String getTitle();
    
    /**
     * Sets the title of the layer group.
     */
    void setTitle(String title);
    
    /**
     * The abstract of the layer group.
     */
    String getAbstract();
    
    /**
     * Sets the abstract of the layer group.
     */
    void setAbstract(String abstractTxt);
    
    /**
     * The workspace the layer group is part of, or <code>null</code> if the layer group is global.
     */
    WorkspaceInfo getWorkspace();

    /**
     * Sets the workspace the layer group is part of.
     */
    void setWorkspace(WorkspaceInfo workspace);

    /**
     * The derived prefixed name of the layer group.
     * <p>
     * If a workspace is set for the layer group this method returns:
     * <pre>
     *   getWorkspace().getName() + ":" + getName();
     * </pre>
     * Otherwise it simply returns: <pre>getName()</pre>
     * </p>
     */
    String prefixedName();

    /**
     * Get root layer.
     */
    LayerInfo getRootLayer();
    
    /**
     * Set root layer.
     */
    void setRootLayer(LayerInfo rootLayer);
    
    /**
     * Get root layer style.
     */
    StyleInfo getRootLayerStyle();

    /**
     * Set root layer style.
     */
    void setRootLayerStyle(StyleInfo style);
    
    /**
     * The layers in the group.
     */
    List<LayerInfo> getLayers();
    
    /**
     * The styles for the layers in the group.
     * <p>
     * This list is a 1-1 correspondence to {@link #getLayers()}.
     * </p>
     */
    List<StyleInfo> getStyles();

    /**
     * The layers that should be rendered.
     */
    List<LayerInfo> renderingLayers();
    
    /**
     * The styles for the layers that should be rendered.
     * <p>
     * This list is a 1-1 correspondence to {@link #renderingLayers()}.
     * </p>
     */
    List<StyleInfo> renderingStyles();
    
    /**
     * The bounds for the base map.
     */
    ReferencedEnvelope getBounds();

    /**
     * Sets the bounds for the base map.
     */
    void setBounds( ReferencedEnvelope bounds );
    
    /**
     * A persistent map of metadata.
     * <p>
     * Data in this map is intended to be persisted. Common case of use is to
     * have services associate various bits of data with a particular layer group. 
     * An example might include caching information.
     * </p>
     * <p>
     * The key values of this map are of type {@link String} and values are of
     * type {@link Serializable}.
     * </p>
     * 
     */
    MetadataMap getMetadata();

    /**
     * @return the list of this layer's authority URLs
     */
    List<AuthorityURLInfo> getAuthorityURLs();
    
    /**
     * @return the list of this layer's identifiers
     */
    List<LayerIdentifierInfo> getIdentifiers();
    
}
