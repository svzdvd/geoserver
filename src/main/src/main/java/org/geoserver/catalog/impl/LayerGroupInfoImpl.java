/* Copyright (c) 2001 - 2013 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import org.geoserver.catalog.AuthorityURLInfo;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerIdentifierInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;


public class LayerGroupInfoImpl implements LayerGroupInfo {

    protected String id;
    protected String name;
    protected Mode mode = Mode.SINGLE;
    
    /**
     * This property in 2.2.x series is stored under the metadata map with key 'title'.
     */
    protected String title;
    
    /**
     * This property in 2.2.x series is stored under the metadata map with key 'abstract'.
     */    
    protected String abstractTxt;
    
    protected WorkspaceInfo workspace;
    protected String path;
    protected LayerInfo rootLayer;
    protected StyleInfo rootLayerStyle;
    protected List<PublishedInfo> layers = new ArrayList<PublishedInfo>();
    protected List<StyleInfo> styles = new ArrayList<StyleInfo>();
    protected ReferencedEnvelope bounds;
    protected MetadataMap metadata = new MetadataMap();

    /**
     * This property is transient in 2.1.x series and stored under the metadata map with key
     * "authorityURLs", and a not transient in the 2.2.x series.
     * 
     * @since 2.1.3
     */
    protected List<AuthorityURLInfo> authorityURLs = new ArrayList<AuthorityURLInfo>(2);

    /**
     * This property is transient in 2.1.x series and stored under the metadata map with key
     * "identifiers", and a not transient in the 2.2.x series.
     * 
     * @since 2.1.3
     */
    protected List<LayerIdentifierInfo> identifiers = new ArrayList<LayerIdentifierInfo>(2);
    
    public LayerGroupInfoImpl() {
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public void setId( String id ) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }
       
    @Override
    public String getTitle() {
        if(title == null && metadata != null) {
            title = metadata.get("title", String.class);
        }
        return title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String getAbstract() {
        if(abstractTxt == null && metadata != null) {
            abstractTxt = metadata.get("title", String.class);
        }
        return abstractTxt;
    }
    
    @Override
    public void setAbstract(String abstractTxt) {
        this.abstractTxt = abstractTxt;
    }
    
    @Override
    public WorkspaceInfo getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(WorkspaceInfo workspace) {
        this.workspace = workspace;
    }

    @Override
    public String prefixedName() {
        return workspace != null ? workspace.getName()+":"+name : name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public LayerInfo getRootLayer() {
        return rootLayer;
    }
    
    @Override
    public void setRootLayer(LayerInfo rootLayer) {
        this.rootLayer = rootLayer;
    }
    
    @Override
    public StyleInfo getRootLayerStyle() {
        return rootLayerStyle;
    }

    @Override
    public void setRootLayerStyle(StyleInfo style) {
        this.rootLayerStyle = style;
    }
    
    @Override
    public List<PublishedInfo> getLayers() {
        return layers;
    }

    public void setLayers(List<PublishedInfo> layers) {
        this.layers = layers;
    }
    
    @Override
    public List<StyleInfo> getStyles() {
        return styles;
    }
    
    public void setStyles(List<StyleInfo> styles) {
        this.styles = styles;
    }    
    
    @Override
    public List<LayerInfo> layers() {
        return layers(this);
    }

    @Override
    public List<StyleInfo> styles() {
        return styles(this);
    }    
    
    public static List<LayerInfo> layers(LayerGroupInfo group) {
        switch (group.getMode()) {
        case CONTAINER:
            return new ArrayList<LayerInfo>(0);
        case EO:
            List<LayerInfo> rootLayerList = new ArrayList<LayerInfo>(1);
            rootLayerList.add(group.getRootLayer());
            return rootLayerList;
        default:
            return layers(group, new ArrayList<LayerInfo>());
        }
    }    
  
    public static List<StyleInfo> styles(LayerGroupInfo group) {
        switch (group.getMode()) {
        case CONTAINER:
            return new ArrayList<StyleInfo>(0);
        case EO:
            List<StyleInfo> rootLayerStyleList = new ArrayList<StyleInfo>(1);
            rootLayerStyleList.add(group.getRootLayerStyle());
            return rootLayerStyleList;
        default:
            return styles(group, new ArrayList<StyleInfo>());
        }        
    }      
    
    private static List<LayerInfo> layers(LayerGroupInfo group, List<LayerInfo> layers) {
        for (PublishedInfo p : group.getLayers()) {
            if (p instanceof LayerInfo) {
                LayerInfo l = (LayerInfo) p;
                layers.add(l);
            } else {
                LayerGroupInfo g = (LayerGroupInfo) p;
                layers.addAll(g.layers());
            }
        }        
        return layers;
    }    

    private static List<StyleInfo> styles(LayerGroupInfo group, List<StyleInfo> styles) {
        int size = group.getLayers().size();
        for (int i = 0; i < size; i++) {
            PublishedInfo p = group.getLayers().get(i);
            if (p instanceof LayerInfo) {
                styles.add(group.getStyles().get(i));
            } else {
                LayerGroupInfo g = (LayerGroupInfo) p;
                styles.addAll(g.styles());
            }
        }        
        return styles;
    }        
    
    @Override
    public ReferencedEnvelope getBounds() {
        return bounds;
    }
    
    @Override
    public void setBounds(ReferencedEnvelope bounds) {
        this.bounds = bounds;
    }
    
    @Override
    public MetadataMap getMetadata() {
        return metadata;
    }
    
    public void setMetadata(MetadataMap metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bounds == null) ? 0 : bounds.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((layers == null) ? 0 : layers.hashCode());
        result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((abstractTxt == null) ? 0 : abstractTxt.hashCode());
        result = prime * result + ((workspace == null) ? 0 : workspace.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((styles == null) ? 0 : styles.hashCode());
        result = prime * result + ((rootLayer == null) ? 0 : rootLayer.hashCode());
        result = prime * result + ((rootLayerStyle == null) ? 0 : rootLayerStyle.hashCode());        
        result = prime * result + ((authorityURLs == null) ? 0 : authorityURLs.hashCode());
        result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!( obj instanceof LayerGroupInfo) ) 
            return false;
        LayerGroupInfo other = (LayerGroupInfo) obj;
        if (bounds == null) {
            if (other.getBounds() != null)
                return false;
        } else if (!bounds.equals(other.getBounds()))
            return false;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        if (layers == null) {
            if (other.getLayers() != null)
                return false;
        } else if (!layers.equals(other.getLayers()))
            return false;
        if (metadata == null) {
            if (other.getMetadata() != null)
                return false;
        } else if (!metadata.equals(other.getMetadata()))
            return false;
        if (name == null) {
            if (other.getName() != null)
                return false;
        } else if (!name.equals(other.getName()))
            return false;
        if (mode == null) {
            if (other.getMode() != null)
                return false;
        } else if (!mode.equals(other.getMode()))
            return false;        
        if (title == null) {
            if (other.getTitle() != null) {
                return false;
            }
        } else if (!title.equals(other.getTitle())) 
            return false;
        if (abstractTxt == null) {
            if (other.getAbstract() != null) {
                return false;
            }
        } else if (!abstractTxt.equals(other.getAbstract())) 
            return false;        
        if (workspace == null) {
            if (other.getWorkspace() != null)
                return false;
        } else if (!workspace.equals(other.getWorkspace()))
            return false;
        if (styles == null) {
            if (other.getStyles() != null)
                return false;
        } else if (!styles.equals(other.getStyles()))
            return false;
        if(authorityURLs == null){
            if (other.getAuthorityURLs() != null)
                return false;
        } else if (!authorityURLs.equals(other.getAuthorityURLs()))
            return false;
        
        if(identifiers == null){
            if (other.getIdentifiers() != null)
                return false;
        } else if (!identifiers.equals(other.getIdentifiers()))
            return false;

        if(rootLayer == null){
            if (other.getRootLayer() != null)
                return false;
        } else if (!rootLayer.equals(other.getRootLayer()))
            return false;
        
        if(rootLayerStyle == null){
            if (other.getRootLayerStyle() != null)
                return false;
        } else if (!rootLayerStyle.equals(other.getRootLayerStyle()))
            return false;
        
        return true;
    }

    @Override
    public List<AuthorityURLInfo> getAuthorityURLs() {
        return authorityURLs;
    }

    public void setAuthorityURLs(List<AuthorityURLInfo> authorities){
        this.authorityURLs = authorities;
    }
    
    @Override
    public List<LayerIdentifierInfo> getIdentifiers() {
        return identifiers;
    }
    
    public void setIdentifiers(List<LayerIdentifierInfo> identifiers){
        this.identifiers = identifiers;
    }

    public static void calculateBounds(LayerGroupInfo group, CoordinateReferenceSystem crs) throws Exception {
        List<LayerInfo> layers = layers(group);        
        if (layers.isEmpty()) {
            return;
        }        
        
        LayerInfo l = layers.get(0);
        ReferencedEnvelope bounds = transform(l.getResource().getLatLonBoundingBox(), crs);

        for (int i = 1; i < layers.size(); i++) {
            l = layers.get(i);
            bounds.expandToInclude(transform(l.getResource().getLatLonBoundingBox(), crs));
        }
        
        group.setBounds(bounds);
    }
    
    public static void calculateBounds(LayerGroupInfo layerGroup) throws Exception {
        List<LayerInfo> layers = layers(layerGroup);       
        if (layers.isEmpty()) {
            return;
        }
        
        LayerInfo l = layers.get(0);
        ReferencedEnvelope bounds = l.getResource().boundingBox();
        boolean latlon = false;
        if (bounds == null) {
            bounds = l.getResource().getLatLonBoundingBox();
            latlon = true;
        }

        if (bounds == null) {
            throw new IllegalArgumentException(
                    "Could not calculate bounds from layer with no bounds, " + l.getName());
        }

        for (int i = 1; i < layers.size(); i++) {
            l = layers.get(i);

            ReferencedEnvelope re;
            if (latlon) {
                re = l.getResource().getLatLonBoundingBox();
            } else {
                re = l.getResource().boundingBox();
            }

            re = transform(re, bounds.getCoordinateReferenceSystem());
            if (re == null) {
                throw new IllegalArgumentException(
                        "Could not calculate bounds from layer with no bounds, " + l.getName());
            }
            bounds.expandToInclude(re);
        }

        layerGroup.setBounds(bounds);
    }

    /**
     * Helper method for transforming an envelope.
     */
    private static ReferencedEnvelope transform(ReferencedEnvelope e, CoordinateReferenceSystem crs) throws TransformException, FactoryException {
        if (!CRS.equalsIgnoreMetadata(crs, e.getCoordinateReferenceSystem())) {
            return e.transform(crs, true);
        }
        return e;
    }    
    
    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append('[').append(name).append(']')
                .toString();
    }
}