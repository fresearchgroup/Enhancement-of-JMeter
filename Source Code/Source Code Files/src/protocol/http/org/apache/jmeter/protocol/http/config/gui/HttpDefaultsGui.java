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

package org.apache.jmeter.protocol.http.config.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPHC4Impl;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.BooleanProperty;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextField;

public class HttpDefaultsGui extends AbstractConfigGui {

    private static final long serialVersionUID = 240L;

    private JCheckBox imageParser;
    
    private JCheckBox concurrentDwn;
    
    private JCheckBox bandwidthThrottle;			// Checkbox for bandwidth throttling
    
    private JCheckBox dynamicThrottle;				// Checkbox for dynamic bandwidth throttling
    
    private JTextField concurrentPool; 
    
    private JTextField bandwidth;					// Text box for bandwidth
    
    private JTextField maxError;					// Text box for maximum permissible error
    
    private JTextField minBandwidth;				// Text box for minimum applicable error

    private UrlConfigGui urlConfig;

    private JLabeledTextField embeddedRE; // regular expression used to match against embedded resource URLs

    public HttpDefaultsGui() {
        super();
        init();
    }

    @Override
    public String getLabelResource() {
        return "url_config_title"; // $NON-NLS-1$
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    @Override
    public TestElement createTestElement() {
        ConfigTestElement config = new ConfigTestElement();
        modifyTestElement(config);
        return config;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    @Override
    public void modifyTestElement(TestElement config) {
        ConfigTestElement cfg = (ConfigTestElement ) config;
        ConfigTestElement el = (ConfigTestElement) urlConfig.createTestElement();
        cfg.clear(); // need to clear because the
        cfg.addConfigElement(el);
        super.configureTestElement(config);
        if (imageParser.isSelected()) {
            config.setProperty(new BooleanProperty(HTTPSamplerBase.IMAGE_PARSER, true));
            enableConcurrentDwn(true);
        } else {
            config.removeProperty(HTTPSamplerBase.IMAGE_PARSER);
            enableConcurrentDwn(false);
            
        }
        if (concurrentDwn.isSelected()) {
            config.setProperty(new BooleanProperty(HTTPSamplerBase.CONCURRENT_DWN, true));
        } else {
            // The default is false, so we can remove the property to simplify JMX files
            // This also allows HTTPDefaults to work for this checkbox
            config.removeProperty(HTTPSamplerBase.CONCURRENT_DWN);
        }
        if(!StringUtils.isEmpty(concurrentPool.getText())) {
        	config.setProperty(new StringProperty(HTTPSamplerBase.CONCURRENT_POOL,
        			concurrentPool.getText()));
        } else {
        	config.setProperty(new StringProperty(HTTPSamplerBase.CONCURRENT_POOL,
        			String.valueOf(HTTPSamplerBase.CONCURRENT_POOL_SIZE)));
        }
        if (!StringUtils.isEmpty(embeddedRE.getText())) {
            config.setProperty(new StringProperty(HTTPSamplerBase.EMBEDDED_URL_RE,
                    embeddedRE.getText()));
        } else {
            config.removeProperty(HTTPSamplerBase.EMBEDDED_URL_RE);
        }
        if (bandwidthThrottle.isSelected()) {													// Testing if bandwidth throttling is selected
        	config.setProperty(new BooleanProperty(HTTPSamplerBase.BANDWIDTH_ENABLE, true));	
        }
        else {
        	config.removeProperty(HTTPSamplerBase.BANDWIDTH_ENABLE);
        }
        if (!StringUtils.isEmpty(bandwidth.getText()) && bandwidthThrottle.isSelected()) {
        	config.setProperty(new StringProperty(HTTPSamplerBase.BANDWIDTH_CPS, bandwidth.getText()));  // Setting bandwidth variable for JMeter if Bandwidth throttling is selected
        	HTTPHC4Impl.RESET = true;
        }
        else {
        	config.setProperty(new StringProperty(HTTPSamplerBase.BANDWIDTH_CPS, "0"));    
        }
        if (dynamicThrottle.isSelected()) {														// testing if dynamic bandwidth throttling is selected
        	config.setProperty(new BooleanProperty(HTTPSamplerBase.DYNAMIC_ENABLE, true));
        }
        else {
        	config.removeProperty(HTTPSamplerBase.DYNAMIC_ENABLE);
        }
        if(!StringUtils.isEmpty(maxError.getText()) && dynamicThrottle.isSelected()) {			// setting the variable for Maximum permissible error if dynamic bandwidth throttling is selected.
        	config.setProperty(new StringProperty(HTTPSamplerBase.MAX_ERROR, maxError.getText()));
        }
        else {
        	config.setProperty(new StringProperty(HTTPSamplerBase.MAX_ERROR, "100"));
        }
        if(!StringUtils.isEmpty(minBandwidth.getText()) && dynamicThrottle.isSelected()) {
        	config.setProperty(new StringProperty(HTTPSamplerBase.MIN_BANDWIDTH, minBandwidth.getText()));
        }
        else {																				   // setting the value of min applicable bandwidth.  
        	config.setProperty(new StringProperty(HTTPSamplerBase.MIN_BANDWIDTH, "0"));
        }
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    @Override
    public void clearGui() {
        super.clearGui();
        urlConfig.clear();
        imageParser.setSelected(false);
        concurrentDwn.setSelected(false);
        concurrentPool.setText(String.valueOf(HTTPSamplerBase.CONCURRENT_POOL_SIZE));
        embeddedRE.setText(""); // $NON-NLS-1$
        bandwidthThrottle.setSelected(false);
        bandwidth.setText("");
        dynamicThrottle.setSelected(false);
        minBandwidth.setText("");
        maxError.setText("");
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        urlConfig.configure(el);
        imageParser.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.IMAGE_PARSER));
        concurrentDwn.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.CONCURRENT_DWN));
        concurrentPool.setText(((AbstractTestElement) el).getPropertyAsString(HTTPSamplerBase.CONCURRENT_POOL));
        embeddedRE.setText(((AbstractTestElement) el).getPropertyAsString(HTTPSamplerBase.EMBEDDED_URL_RE, ""));
        bandwidthThrottle.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.BANDWIDTH_ENABLE));
        if(bandwidthThrottle.isSelected())
        	bandwidth.setText(((AbstractTestElement) el).getPropertyAsString(HTTPSamplerBase.BANDWIDTH_CPS));
        else
        	bandwidth.setEnabled(false);
        dynamicThrottle.setSelected(((AbstractTestElement) el).getPropertyAsBoolean(HTTPSamplerBase.DYNAMIC_ENABLE));
        if(dynamicThrottle.isSelected()) {
        	minBandwidth.setText(((AbstractTestElement) el).getPropertyAsString(HTTPSamplerBase.MIN_BANDWIDTH));
        	maxError.setText(((AbstractTestElement) el).getPropertyAsString(HTTPSamplerBase.MAX_ERROR));
        }        	
        else {
        	minBandwidth.setEnabled(false);
        	maxError.setEnabled(false);
        }
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        add(makeTitlePanel(), BorderLayout.NORTH);

        urlConfig = new UrlConfigGui(false, true, false);
        add(urlConfig, BorderLayout.CENTER);

        // OPTIONAL TASKS
        final JPanel optionalTasksPanel = new VerticalPanel();
        optionalTasksPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                .getResString("optional_tasks"))); // $NON-NLS-1$

        final JPanel checkBoxPanel = new HorizontalPanel();
        imageParser = new JCheckBox(JMeterUtils.getResString("web_testing_retrieve_images")); // $NON-NLS-1$
        checkBoxPanel.add(imageParser);
        imageParser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) { enableConcurrentDwn(true); }
                else { enableConcurrentDwn(false); }
            }
        });
        // Concurrent resources download
        concurrentDwn = new JCheckBox(JMeterUtils.getResString("web_testing_concurrent_download")); // $NON-NLS-1$
        concurrentDwn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (imageParser.isSelected() && e.getStateChange() == ItemEvent.SELECTED) { concurrentPool.setEnabled(true); }
                else { concurrentPool.setEnabled(false); }
            }
        });
        concurrentPool = new JTextField(2); // 2 columns size
        concurrentPool.setMaximumSize(new Dimension(30,20));
        checkBoxPanel.add(concurrentDwn);
        checkBoxPanel.add(concurrentPool);
        optionalTasksPanel.add(checkBoxPanel);
        
        // Embedded URL match regex
        embeddedRE = new JLabeledTextField(JMeterUtils.getResString("web_testing_embedded_url_pattern"),30); // $NON-NLS-1$
        optionalTasksPanel.add(embeddedRE);        
        //        
        //Bandwidth Throttling Panel
        final JPanel bandwidthPanel = new VerticalPanel();
        bandwidthPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils
                .getResString("bandwidth_panel")));

        final JPanel cpsPanel = getCpsPanel();
        
        //Dynamic Bandwidth Throttling
        final JPanel dynamicThrottlePanel = getDynamicThrottlePanel();
        bandwidthPanel.add(cpsPanel);
        bandwidthPanel.add(dynamicThrottlePanel);
        optionalTasksPanel.add(bandwidthPanel);
        //optionalTasksPanel.add(bandwidthCheckBoxPanel);
        
        //add(bandwidthPanel, BorderLayout.EAST);
        add(optionalTasksPanel, BorderLayout.SOUTH);
    }

    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    private void enableConcurrentDwn(final boolean enable) {
        if (enable) {
            concurrentDwn.setEnabled(true);
            embeddedRE.setEnabled(true);
            if (concurrentDwn.isSelected()) {
                concurrentPool.setEnabled(true);
            }
        } else {
            concurrentDwn.setEnabled(false);
            concurrentPool.setEnabled(false);
            embeddedRE.setEnabled(false);
        }
    }

    public void itemStateChanged(final ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            enableConcurrentDwn(true);
        } else {
            enableConcurrentDwn(false);
        }
    }
    
    /**
     * Adding Bandwidth Throttling panel to the HTTP Request Defaults GUI
     * @author Shekhar Saurav
     * 
     * @return JPanel
     */
    
    public JPanel getCpsPanel() {
        final JPanel bandwidthTPanel = new HorizontalPanel();
        bandwidthThrottle = new JCheckBox(JMeterUtils.getResString("bandwidth_throttling"));
        bandwidthThrottle.addItemListener(new ItemListener() {
        	@Override
        	public void itemStateChanged(final ItemEvent e) {
        		if (bandwidthThrottle.isSelected() && e.getStateChange() == ItemEvent.SELECTED) { bandwidth.setEnabled(true); }
        		else { bandwidth.setEnabled(false); }
        	}
        });
        bandwidthTPanel.add(bandwidthThrottle, BorderLayout.NORTH);
        bandwidth = new JTextField(20);
        bandwidth.setEnabled(false);
        JLabel label = new JLabel(JMeterUtils.getResString("bandwidth_cps")); // $NON-NLS-1$
        label.setLabelFor(bandwidth);        
        bandwidthTPanel.add(label, BorderLayout.WEST);
        bandwidthTPanel.add(bandwidth, BorderLayout.CENTER);
        return bandwidthTPanel;
    }
    
    /**
     * Adding Dynamic Bandwidth Throttling Panel HTTP Request Defaults GUI
     * @author Shekhar Saurav
     * @return JPanel
     */
    public JPanel getDynamicThrottlePanel() {
    	JPanel dynamicPanel = new HorizontalPanel();
        dynamicThrottle = new JCheckBox(JMeterUtils.getResString("dynamic_throttle"));
        dynamicPanel.add(dynamicThrottle, BorderLayout.NORTH);
        dynamicThrottle.addItemListener(new ItemListener() {
        	@Override
        	public void itemStateChanged(final ItemEvent e) {
        		if (dynamicThrottle.isSelected() && e.getStateChange() == ItemEvent.SELECTED) {
        			bandwidthThrottle.setSelected(true);
        			bandwidth.setEnabled(true);
        			maxError.setEnabled(true);
        			minBandwidth.setEnabled(true);
        		}
        		else {
        			maxError.setEnabled(false);
        			minBandwidth.setEnabled(false);
        		}
        	}
        });
        maxError = new JTextField(20);
        maxError.setEnabled(false);
        JLabel errorLabel = new JLabel(JMeterUtils.getResString("dynamic_max_error"));
        errorLabel.setLabelFor(maxError);

        JPanel minBandPanel = new VerticalPanel();
        minBandwidth = new JTextField(20);
        minBandwidth.setEnabled(false);
        JLabel bandwidthLabel = new JLabel(JMeterUtils.getResString("dynamic_min_bandwidth"));
        bandwidthLabel.setLabelFor(minBandwidth);
        minBandPanel.add(bandwidthLabel, BorderLayout.WEST);
        minBandPanel.add(minBandwidth, BorderLayout.EAST);
        dynamicPanel.add(minBandPanel, BorderLayout.WEST);//, BorderLayout.BEFORE_LINE_BEGINS);
        JPanel maxErrorPanel = new VerticalPanel();
        maxErrorPanel.add(errorLabel, BorderLayout.WEST);
        maxErrorPanel.add(maxError, BorderLayout.CENTER);
        dynamicPanel.add(maxErrorPanel, BorderLayout.AFTER_LAST_LINE);
        return dynamicPanel;
        
    }
    
    // Setting bandwidth Value in the GUI
    public void setBandwidth(String value) {
    	bandwidth.setText(value);
    }
    
    // Extracting bandwidth value from the GUI
    public String getBanwidth() {
    	return bandwidth.getText();
    }
}
