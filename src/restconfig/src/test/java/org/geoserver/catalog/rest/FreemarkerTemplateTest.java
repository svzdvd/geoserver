/* Copyright (c) 2001 - 2009 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Before;
import org.junit.Test;


public class FreemarkerTemplateTest extends GeoServerSystemTestSupport {

    @Before
    public void login() throws Exception {
        login("admin", "geoserver", "ROLE_ADMINISTRATOR");
    }

    private void testGetPutGetDeleteGet(String path, String content) throws Exception {
        // GET
        Assert.assertEquals("File Not Found", getAsString(path).trim());
        
        // PUT
        put(path, content).close();
        
        // GET
        Assert.assertEquals(content, getAsString(path).trim());
        
        // DELETE
        Assert.assertEquals(200, deleteAsServletResponse(path).getStatusCode());
        
        // GET
        Assert.assertEquals("File Not Found", getAsString(path).trim());
    }
    
    @Test
    public void testGetPutGetDeleteGet() throws Exception {
        String path = "/rest/workspaces/templates/my_template.ftl";
        testGetPutGetDeleteGet(path, "hello world");
    }

    private List<String> getAllPaths() {
        List<String> paths = new ArrayList<String>();
        paths.add("/rest/workspaces/templates/aTemplate.ftl");
        paths.add("/rest/workspaces/templates/anotherTemplate.ftl");
        
        paths.add("/rest/workspaces/myWorkspace/templates/aTemplate.ftl");
        paths.add("/rest/workspaces/myWorkspace/templates/anotherTemplate.ftl");
        
        paths.add("/rest/workspaces/myWorkspace/datastores/myDataStore/templates/aTemplate.ftl");
        paths.add("/rest/workspaces/myWorkspace/datastores/myDataStore/templates/anotherTemplate.ftl");
        
        paths.add("/rest/workspaces/myWorkspace/datastores/myDataStore/featuretypes/myFeatureType/templates/aTemplate.ftl");
        paths.add("/rest/workspaces/myWorkspace/datastores/myDataStore/featuretypes/myFeatureType/templates/anotherTemplate.ftl");
        
        paths.add("/rest/workspaces/myWorkspace/coveragestores/myCoverageStore/coverages/templates/aTemplate.ftl");
        paths.add("/rest/workspaces/myWorkspace/coveragestores/myCoverageStore/coverages/templates/anotherTemplate.ftl");
        
        paths.add("/rest/workspaces/myWorkspace/coveragestores/myCoverageStore/coverages/myCoverage/templates/aTemplate.ftl");
        paths.add("/rest/workspaces/myWorkspace/coveragestores/myCoverageStore/coverages/myCoverage/templates/anotherTemplate.ftl");
               
        return paths;
    }
    
    @Test
    public void testAllPathsSequentially() throws Exception {
        Random random = new Random();        
        for (String path : getAllPaths()) {
            System.out.println("Checking path: " + path);
            testGetPutGetDeleteGet(path, "hello test " + random.nextInt(1000));
        }
    }

    @Test
    public void testAllPaths() throws Exception {
        String contentHeader = "hello path ";                
        List<String> paths = new ArrayList<String>();        
        
        for (String path : paths) {
            // GET
            Assert.assertEquals("File Not Found", getAsString(path).trim());            
        }        
        
        for (String path : paths) {
            // PUT
            put(path, contentHeader + path).close();
        }                
        
        for (String path : paths) {
            // GET
            Assert.assertEquals(contentHeader + path, getAsString(path).trim());
        }                        
        
        for (String path : paths) {
            // DELETE
            Assert.assertEquals(200, deleteAsServletResponse(path).getStatusCode());
        }                                
        
        for (String path : paths) {
            // GET
            Assert.assertEquals("File Not Found", getAsString(path).trim());            
        }
    }
}
