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

package org.apache.jorphan.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public final class GuiUtils {

    /**
     * Create a scroll panel that sets its preferred size to its minimum size.
     * Explicitly for scroll panes that live inside other scroll panes, or
     * within containers that stretch components to fill the area they exist in.
     * Use this for any component you would put in a scroll pane (such as
     * TextAreas, tables, JLists, etc). It is here for convenience and to avoid
     * duplicate code. JMeter displays best if you follow this custom.
     *
     * @param comp
     *            the component which should be placed inside the scroll pane
     * @return a JScrollPane containing the specified component
     */
    public static JScrollPane makeScrollPane(Component comp) {
        JScrollPane pane = new JScrollPane(comp);
        pane.setPreferredSize(pane.getMinimumSize());
        return pane;
    }

    /**
     * Fix the size of a column according to the header text.
     * 
     * @param column to be resized
     * @param table containing the column
     */
    public static void fixSize(TableColumn column, JTable table) {
        TableCellRenderer rndr;
        rndr = column.getHeaderRenderer();
        if (rndr == null){
            rndr = table.getTableHeader().getDefaultRenderer();
        }
        Component c = rndr.getTableCellRendererComponent(
                table, column.getHeaderValue(), false, false, -1, column.getModelIndex());
        int width = c.getPreferredSize().width+10;
        column.setMaxWidth(width);
        column.setPreferredWidth(width);
        column.setResizable(false);        
    }
    
    /**
     * Create a GUI component JLabel + JComboBox with a left and right margin (5px)
     * @param label
     * @param comboBox
     * @return the JComponent (margin+JLabel+margin+JComboBox)
     */
    public static JComponent createLabelCombo(String label, JComboBox comboBox) {
        JPanel labelCombo = new JPanel();
        labelCombo.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel caption = new JLabel(label);
        caption.setBorder(new EmptyBorder(0, 5, 0, 5));
        labelCombo.add(caption);
        labelCombo.add(comboBox);
        return labelCombo;
    }

    /**
     * Stop any editing that is currently being done on the table. This will
     * save any changes that have already been made.
     */
    public static void stopTableEditing(JTable table) {
        if (table.isEditing()) {
            TableCellEditor cellEditor = table.getCellEditor(table.getEditingRow(), table.getEditingColumn());
            cellEditor.stopCellEditing();
        }
    }
    
    /**
     * Get pasted text from clipboard
     * @return String Pasted text
     * @throws UnsupportedFlavorException
     * @throws IOException
     */
    public static String getPastedText() throws UnsupportedFlavorException, IOException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = clipboard.getContents(null);
        DataFlavor[] flavourList = trans.getTransferDataFlavors();
        Collection<DataFlavor> flavours = new ArrayList<DataFlavor>(flavourList.length);
        if (Collections.addAll(flavours, flavourList) && flavours.contains(DataFlavor.stringFlavor)) {
            return (String) trans.getTransferData(DataFlavor.stringFlavor);
        } else {
            return null;
        }
    }
}
