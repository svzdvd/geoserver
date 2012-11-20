/* Copyright (c) 2001 - 2011 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog;

import java.util.List;



public interface LayerGroupVisibilityPolicy {

    boolean hideLayerGroup(LayerGroupInfo group, List<LayerInfo> filteredLayers);

}