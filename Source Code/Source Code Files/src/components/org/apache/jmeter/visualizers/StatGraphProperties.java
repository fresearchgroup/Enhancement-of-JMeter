/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jmeter.visualizers;

import java.awt.Font;
import java.awt.Shape;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.jmeter.util.JMeterUtils;
import org.jCharts.properties.LegendAreaProperties;
import org.jCharts.properties.PointChartProperties;

public class StatGraphProperties {

    public static final String[] fontSize = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "24", "28", "32"};

    public static final String[] strokeWidth = { "1.0f", "1.5f", "2.0f", "2.5f", "3.0f", "3.5f", "4.0f", "4.5f", "5.0f", "5.5f", "6.0f", "6.5f"};

    public static Map<String, String> getFontNameMap() {
        Map<String, String> fontNameMap = new HashMap<String, String>(2);
        fontNameMap.put(JMeterUtils.getResString("font.sansserif"), "SansSerif");
        fontNameMap.put(JMeterUtils.getResString("font.serif"), "Serif");
        return fontNameMap;
    }

    @SuppressWarnings("boxing")
    public static Map<String, Integer> getFontStyleMap() {
        Map<String, Integer> fontStyleMap = new HashMap<String, Integer>(3);
        fontStyleMap.put(JMeterUtils.getResString("fontstyle.normal"), Font.PLAIN);
        fontStyleMap.put(JMeterUtils.getResString("fontstyle.bold"), Font.BOLD);
        fontStyleMap.put(JMeterUtils.getResString("fontstyle.italic"), Font.ITALIC);
        return fontStyleMap;
    }

    @SuppressWarnings("boxing")
    public static Map<String, Integer> getPlacementNameMap() {
        Map<String, Integer> placementNameMap = new HashMap<String, Integer>(4);
        placementNameMap.put(JMeterUtils.getResString("aggregate_graph_legend.placement.bottom"), LegendAreaProperties.BOTTOM);
        placementNameMap.put(JMeterUtils.getResString("aggregate_graph_legend.placement.right"), LegendAreaProperties.RIGHT);
        placementNameMap.put(JMeterUtils.getResString("aggregate_graph_legend.placement.left"), LegendAreaProperties.LEFT);
        placementNameMap.put(JMeterUtils.getResString("aggregate_graph_legend.placement.top"), LegendAreaProperties.TOP);
        return placementNameMap;
    }
    
    public static Map<String, Shape> getPointShapeMap() {
        // We want to retain insertion order, so LinkedHashMap is necessary
        Map<String, Shape> pointShapeMap = new LinkedHashMap<String, Shape>(5);
        pointShapeMap.put(JMeterUtils.getResString("graph_pointshape_circle"), PointChartProperties.SHAPE_CIRCLE);
        pointShapeMap.put(JMeterUtils.getResString("graph_pointshape_diamond"), PointChartProperties.SHAPE_DIAMOND);
        pointShapeMap.put(JMeterUtils.getResString("graph_pointshape_square"), PointChartProperties.SHAPE_SQUARE);
        pointShapeMap.put(JMeterUtils.getResString("graph_pointshape_triangle"), PointChartProperties.SHAPE_TRIANGLE);
        pointShapeMap.put(JMeterUtils.getResString("graph_pointshape_none"), null);
        return pointShapeMap;
    }
}