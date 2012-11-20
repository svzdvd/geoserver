package org.geoserver.security.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.impl.AdvertisedCatalog;
import org.geoserver.catalog.impl.LayerGroupVisibilityPolicyHideIfEmpty;
import org.geoserver.catalog.impl.LayerGroupVisibilityPolicyNeverHide;
import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AdvertisedCatalogTest extends AbstractAuthorizationTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Dispatcher.REQUEST.set(new Request());
        Dispatcher.REQUEST.get().setRequest("GetCapabilities");

        populateCatalog();
    }

    @After
    public void tearDown() throws Exception {
        Dispatcher.REQUEST.set(null);        
    }
    
    @Test
    public void testNotAdvertisedLayersInGroupWithHideGroupIfEmptyPolicy() throws Exception {        
        AdvertisedCatalog sc = new AdvertisedCatalog(catalog);
        sc.setLayerGroupVisibilityPolicy(new LayerGroupVisibilityPolicyHideIfEmpty());
        
        assertNull(sc.getLayerByName("topp:states"));
        assertNull(sc.getLayerByName("topp:roads"));
        LayerGroupInfo layerGroup = sc.getLayerGroupByName("topp", "layerGroupWithSomeLockedLayer");        
        assertNull(layerGroup);
    }

    @Test
    public void testNotAdvertisedLayersInGroupWithNeverHideGroupPolicy() throws Exception {        
        AdvertisedCatalog sc = new AdvertisedCatalog(catalog);
        sc.setLayerGroupVisibilityPolicy(new LayerGroupVisibilityPolicyNeverHide());
        
        assertNull(sc.getLayerByName("topp:states"));
        assertNull(sc.getLayerByName("topp:roads"));
        LayerGroupInfo layerGroup = sc.getLayerGroupByName("topp", "layerGroupWithSomeLockedLayer"); 
        assertNotNull(layerGroup);
        assertEquals(0, layerGroup.getLayers().size());
    }
}