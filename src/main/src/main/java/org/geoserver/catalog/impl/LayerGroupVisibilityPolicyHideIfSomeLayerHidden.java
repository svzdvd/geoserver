/* Copyright (c) 2001 - 2011 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;

import java.util.List;

import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerGroupVisibilityPolicy;
import org.geoserver.catalog.LayerInfo;

public class LayerGroupVisibilityPolicyHideIfSomeLayerHidden implements LayerGroupVisibilityPolicy {

    @Override
    public boolean hideLayerGroup(LayerGroupInfo group, List<LayerInfo> filteredLayers) {
        return filteredLayers.size() < group.getLayers().size();
    }
}