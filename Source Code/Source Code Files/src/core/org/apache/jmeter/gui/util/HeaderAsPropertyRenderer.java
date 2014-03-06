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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jmeter.gui.util;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.apache.jmeter.util.JMeterUtils;

/**
 * Renders items in a JTable by converting from resource names.
 */
public class HeaderAsPropertyRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 240L;

    public HeaderAsPropertyRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null){
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
            setText(getText(value, row, column));
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        return this;
    }

    /**
     * Get the text for the value as the translation of the resource name.
     *
     * @param value
     * @param column
     * @param row
     * @return the text
     */
    protected String getText(Object value, int row, int column) {
        if (value == null){
            return "";
        }
        return JMeterUtils.getResString(value.toString());
    }
}