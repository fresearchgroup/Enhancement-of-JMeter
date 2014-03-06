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

package org.apache.jmeter.gui.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.JMeterGUIComponent;
import org.apache.jmeter.gui.NamePanel;

/**
 * Implements the Edit menu item.
 */
public class EditCommand implements Command {
    private static final Set<String> commands = new HashSet<String>();

    static {
        commands.add(ActionNames.EDIT);
    }

    public EditCommand() {
    }

    @Override
    public void doAction(ActionEvent e) {
        GuiPackage guiPackage = GuiPackage.getInstance();
        JMeterGUIComponent currentGui = guiPackage.getCurrentGui();
        guiPackage.getMainFrame().setMainPanel((javax.swing.JComponent) currentGui);
        guiPackage.getMainFrame().setEditMenu(guiPackage.getTreeListener().getCurrentNode().createPopupMenu());
        // TODO: I believe the following code (to the end of the method) is
        // obsolete,
        // since NamePanel no longer seems to be the GUI for any component:
        if (!(currentGui instanceof NamePanel)) {
            guiPackage.getMainFrame().setFileLoadEnabled(true);
            guiPackage.getMainFrame().setFileSaveEnabled(true);
        } else {
            guiPackage.getMainFrame().setFileLoadEnabled(false);
            guiPackage.getMainFrame().setFileSaveEnabled(false);
        }
    }

    @Override
    public Set<String> getActionNames() {
        return commands;
    }
}
