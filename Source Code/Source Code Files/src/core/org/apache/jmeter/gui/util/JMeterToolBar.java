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

package org.apache.jmeter.gui.util;

import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.ActionRouter;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;
import org.apache.jmeter.util.LocaleChangeListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * The JMeter main toolbar class
 *
 */
public class JMeterToolBar extends JToolBar implements LocaleChangeListener {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4591210341986068907L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final String TOOLBAR_ENTRY_SEP = ",";  //$NON-NLS-1$

    private static final String TOOLBAR_PROP_NAME = "toolbar"; //$NON-NLS-1$

    // protected fields: JMeterToolBar class can be use to create another toolbar (plugin, etc.)    
    protected static final String DEFAULT_TOOLBAR_PROPERTY_FILE = "org/apache/jmeter/images/toolbar/icons-toolbar.properties"; //$NON-NLS-1$

    protected static final String USER_DEFINED_TOOLBAR_PROPERTY_FILE = "jmeter.toolbar.icons"; //$NON-NLS-1$
    
    private static final String TOOLBAR_LIST = "jmeter.toolbar";
    
    /**
     * Create the default JMeter toolbar
     * @return the JMeter toolbar
     */
    public static JMeterToolBar createToolbar(boolean visible) {
        JMeterToolBar toolBar = new JMeterToolBar();
        toolBar.setFloatable(false);
        toolBar.setVisible(visible);

        setupToolbarContent(toolBar);
        JMeterUtils.addLocaleChangeListener(toolBar);
        // implicit return empty toolbar if icons == null
        return toolBar;
    }

    /**
     * Setup toolbar content
     * @param toolBar {@link JMeterToolBar}
     */
    private static void setupToolbarContent(JMeterToolBar toolBar) {
        List<IconToolbarBean> icons = getIconMappings();
        if (icons != null) {
            for (IconToolbarBean iconToolbarBean : icons) {
                if (iconToolbarBean == null) {
                    toolBar.addSeparator();
                } else {
                    toolBar.add(makeButtonItemRes(iconToolbarBean));
                }
            }
            toolBar.setTestStarted(false);
        }
    }
    
    /**
     * Generate a button component from icon bean
     * @param iconBean contains I18N key, ActionNames, icon path, optional icon path pressed
     * @return a button for toolbar
     */
    private static JButton makeButtonItemRes(IconToolbarBean iconBean) {
        final URL imageURL = JMeterUtils.class.getClassLoader().getResource(iconBean.getIconPath());
        JButton button = new JButton(new ImageIcon(imageURL));
        button.setToolTipText(JMeterUtils.getResString(iconBean.getI18nKey()));
        final URL imageURLPressed = JMeterUtils.class.getClassLoader().getResource(iconBean.getIconPathPressed());
        button.setPressedIcon(new ImageIcon(imageURLPressed));
        button.addActionListener(ActionRouter.getInstance());
        button.setActionCommand(iconBean.getActionNameResolve());
        return button;
    }
    
    /**
     * Parse icon set file.
     * @return List of icons/action definition
     */
    private static List<IconToolbarBean> getIconMappings() {
        // Get the standard toolbar properties
        Properties defaultProps = JMeterUtils.loadProperties(DEFAULT_TOOLBAR_PROPERTY_FILE);
        if (defaultProps == null) {
            JOptionPane.showMessageDialog(null, 
                    JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                    JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Properties p;
        String userProp = JMeterUtils.getProperty(USER_DEFINED_TOOLBAR_PROPERTY_FILE); 
        if (userProp != null){
            p = JMeterUtils.loadProperties(userProp, defaultProps);
        } else {
            p=defaultProps;
        }

        String order = JMeterUtils.getPropDefault(TOOLBAR_LIST, p.getProperty(TOOLBAR_PROP_NAME));

        if (order == null) {
            log.warn("Could not find toolbar definition list");
            JOptionPane.showMessageDialog(null, 
                    JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                    JMeterUtils.getResString("toolbar_icon_set_not_found"), // $NON-NLS-1$
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String[] oList = order.split(TOOLBAR_ENTRY_SEP);

        List<IconToolbarBean> listIcons = new ArrayList<IconToolbarBean>();
        for (String key : oList) {
            log.debug("Toolbar icon key: " + key); //$NON-NLS-1$
            String trimmed = key.trim();
            if (trimmed.equals("|")) { //$NON-NLS-1$
                listIcons.add(null);
            } else {
                String property = p.getProperty(trimmed);
                if (property == null) {
                    log.warn("No definition for toolbar entry: " + key);
                } else {
                    try {
                        IconToolbarBean itb = new IconToolbarBean(property);
                        listIcons.add(itb);
                    } catch (IllegalArgumentException e) {
                        // already reported by IconToolbarBean
                    }
                }
            }
        }
        return listIcons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void localeChanged(LocaleChangeEvent event) {
        this.removeAll();
        setupToolbarContent(this);
    }

    /**
     * Change state of buttons
     * @param started
     */
    public void setTestStarted(boolean started) {
        Map<String, Boolean> buttonStates = new HashMap<String, Boolean>();
        buttonStates.put(ActionNames.ACTION_START,Boolean.valueOf(!started));
        buttonStates.put(ActionNames.ACTION_START_NO_TIMERS,Boolean.valueOf(!started));
        buttonStates.put(ActionNames.ACTION_STOP,Boolean.valueOf(started));
        buttonStates.put(ActionNames.ACTION_SHUTDOWN,Boolean.valueOf(started));
        buttonStates.put(ActionNames.REMOTE_START_ALL,Boolean.valueOf(!started));
        buttonStates.put(ActionNames.REMOTE_STOP_ALL,Boolean.valueOf(started));
        buttonStates.put(ActionNames.REMOTE_SHUT_ALL,Boolean.valueOf(started));
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if(components[i]instanceof JButton) {
                JButton button = (JButton) components[i];
                Boolean enabled = buttonStates.get(button.getActionCommand());
                if(enabled != null) {
                    button.setEnabled(enabled.booleanValue());
                }
            }
        }
    }
}