/* Copyright (c) 2001 - 2011 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerGroupVisibilityPolicy;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.Predicates;
import org.geoserver.catalog.ResourceInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.Request;
import org.geoserver.security.decorators.DecoratingLayerGroupInfo;
import org.geotools.filter.expression.InternalVolatileFunction;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;


/**
 * Filters out the non advertised layers and resources.
 */
public class AdvertisedCatalog extends AbstractFilteredCatalog {

    private LayerGroupVisibilityPolicy layerGroupPolicy = new LayerGroupVisibilityPolicyNeverHide();
    

    public AdvertisedCatalog(Catalog catalog) throws Exception {
        super(catalog);
    }

    
    public void setLayerGroupVisibilityPolicy(LayerGroupVisibilityPolicy layerGroupPolicy) {
        this.layerGroupPolicy = layerGroupPolicy;
    }
    
    private boolean hideLayer(LayerInfo layer) {
        if (!layer.isAdvertised()) {
            return isOgcCapabilitiesRequest();
        } else {
            return hideResource(layer.getResource());
        }
    }
    
    private boolean hideResource(ResourceInfo resource) {
        if (!resource.isAdvertised()) {
            return isOgcCapabilitiesRequest();
        } else {
            return false;
        }
    }
    
    private boolean isOgcCapabilitiesRequest() {
        Request request = Dispatcher.REQUEST.get();
        return request != null && "GetCapabilities".equalsIgnoreCase(request.getRequest());
    }

    @Override
    protected <T extends ResourceInfo> T checkAccess(T resource) {
        if (resource == null || hideResource(resource)) {
            return null;
        } else {
            return resource;
        }
    }
    
    @Override
    protected LayerInfo checkAccess(LayerInfo layer) {
        if (layer == null || hideLayer(layer)) {
            return null;
        } else {
            return layer;
        }
    }

    @Override
    protected LayerGroupInfo checkAccess(LayerGroupInfo group) {
        if (group == null) {
            return null;
        }

        final List<LayerInfo> filteredLayers = filterLayers(group.getLayers());
        
        if (layerGroupPolicy.hideLayerGroup(group, filteredLayers)) {
            return null;
        } else {
            if (group.getLayers().size() != filteredLayers.size()) {
                return new DecoratingLayerGroupInfo(group) {
                    @Override
                    public List<LayerInfo> getLayers() {
                        return filteredLayers;
                    }
                };
            } else {
                return group;
            }            
        }
    }

    @Override
    protected <T extends ResourceInfo> List<T> filterResources(List<T> resources) {
        List<T> filtered = new ArrayList<T>(resources.size());
        for (T resource : resources) {
            resource = checkAccess(resource);
            if (resource != null) {
                filtered.add(resource);
            }
        }
        return filtered;
    }

    @Override
    protected List<LayerGroupInfo> filterGroups(List<LayerGroupInfo> groups) {
        List<LayerGroupInfo> filtered = new ArrayList<LayerGroupInfo>(groups.size());
        for (LayerGroupInfo group : groups) {
            group = checkAccess(group);
            if (group != null) {
                filtered.add(group);
            }
        }
        return filtered;
    }

    @Override
    protected List<LayerInfo> filterLayers(List<LayerInfo> layers) {
        List<LayerInfo> filtered = new ArrayList<LayerInfo>(layers.size());
        for (LayerInfo layer : layers) {
            layer = checkAccess(layer);
            if (layer != null) {
                filtered.add(layer);
            }
        }
        return filtered;
    }

    @Override
    protected <T extends CatalogInfo> Filter securityFilter(Class<T> infoType, Filter filter) {
        if (!ResourceInfo.class.isAssignableFrom(infoType) && 
            !LayerInfo.class.isAssignableFrom(infoType) &&
            !LayerGroupInfo.class.isAssignableFrom(infoType)) 
        {
            // these kind of objects are not secured
            return filter;
        }

        org.opengis.filter.expression.Function visible = new InternalVolatileFunction() {
            /**
             * Returns {@code false} if the catalog info shall be hidden, {@code true} otherwise.
             */
            @Override
            public Boolean evaluate(Object info) {
                if (info instanceof ResourceInfo) {
                    return !hideResource((ResourceInfo) info);
                } else if (info instanceof LayerInfo) {
                    return !hideLayer((LayerInfo) info);
                } else if (info instanceof LayerGroupInfo) {
                    return checkAccess((LayerGroupInfo) info) != null;
                } else {
                    throw new IllegalArgumentException("Can't build filter for objects of type "
                            + info.getClass().getName());
                }                
            }
        };

        FilterFactory factory = Predicates.factory;

        // create a filter combined with the security credentials check
        Filter securityFilter = factory.equals(factory.literal(Boolean.TRUE), visible);
        return Predicates.and(filter, securityFilter);
    }    
    
    @Override
    protected <T extends StoreInfo> T checkAccess(T store) {
        return store;
    }

    @Override
    protected <T extends NamespaceInfo> T checkAccess(T ns) {
        return ns;
    }

    @Override
    protected <T extends WorkspaceInfo> T checkAccess(T ws) {
        return ws;
    }

    @Override
    protected StyleInfo checkAccess(StyleInfo style) {
        return style;
    }

    @Override
    protected <T extends StoreInfo> List<T> filterStores(List<T> stores) {
        return stores;
    }    

    @Override
    protected List<StyleInfo> filterStyles(List<StyleInfo> styles) {
        return styles;
    }

    @Override
    protected <T extends NamespaceInfo> List<T> filterNamespaces(List<T> namespaces) {
        return namespaces;
    }

    @Override
    protected <T extends WorkspaceInfo> List<T> filterWorkspaces(List<T> workspaces) {
        return workspaces;
    }
}