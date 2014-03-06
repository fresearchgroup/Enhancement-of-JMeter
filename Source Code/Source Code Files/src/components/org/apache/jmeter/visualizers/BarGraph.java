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

import java.awt.Color;

import javax.swing.JCheckBox;

import org.apache.jmeter.util.JMeterUtils;

public class BarGraph {
    
    private String label;
    
    private JCheckBox chkBox;
    
    private Color backColor;

    /**
     * @param resString
     * @param checked
     * @param backColor
     */
    public BarGraph(String resString, boolean checked, Color backColor) {
        super();
        this.label = JMeterUtils.getResString(resString);
        this.chkBox = new JCheckBox(this.label, checked);
        this.backColor = backColor;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the chkBox
     */
    public JCheckBox getChkBox() {
        return chkBox;
    }

    /**
     * @param chkBox the chkBox to set
     */
    public void setChkBox(JCheckBox chkBox) {
        this.chkBox = chkBox;
    }

    /**
     * @return the backColor
     */
    public Color getBackColor() {
        return backColor;
    }

    /**
     * @param backColor the backColor to set
     */
    public void setBackColor(Color backColor) {
        this.backColor = backColor;
    }
    
}
