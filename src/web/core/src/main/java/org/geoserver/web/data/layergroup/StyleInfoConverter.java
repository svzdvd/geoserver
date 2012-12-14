/* Copyright (c) 2012 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.data.layergroup;

import java.util.Locale;

import org.apache.wicket.util.convert.IConverter;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.web.GeoServerApplication;


@SuppressWarnings("serial")
public class StyleInfoConverter implements IConverter {

    @Override
    public Object convertToObject(String name, Locale locale) {
        return GeoServerApplication.get().getCatalog().getStyleByName(name);
    }

    @Override
    public String convertToString(Object obj, Locale locale) {
        if (obj == null) return "";
        else return ((StyleInfo) obj).getName();
    }
}