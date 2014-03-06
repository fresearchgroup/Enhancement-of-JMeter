/* Licensed to the Apache Software Foundation (ASF) under one or more
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.protocol.http.config.gui.UrlConfigGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.protocol.http.config.IpGeneration;

public class IpConfig extends AbstractConfigGui implements ActionListener {
	
	private JTextField IpAddress;
	
	private JTextField Mask;
	
	private JTextField NumIp;
	
	private JButton createIp;
	
	public IpConfig() {
		super();
		init();
	}
	
	@Override
	public String getLabelResource() {
		return "ip_config_title";
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
    	configureTestElement(config);
    }
    
    /**
     * Implements JMeterGUIComponent.clearGui
     */
    @Override
    public void clearGui() {
    	super.clearGui();
    }
    
    @Override
    public void configure(TestElement el) {
    	super.configure(el);
    }
    
    private void init() {
    	setLayout(new BorderLayout(0, 5));
    	setBorder(makeBorder());
    	add(makeTitlePanel(), BorderLayout.NORTH);
    	VerticalPanel mainPanel = new VerticalPanel();
    	JPanel ipPanel = new VerticalPanel();
    	ipPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), JMeterUtils.getResString("ip_settings_panel")));
    	ipPanel.add(setIpTo(), BorderLayout.WEST);
    	ipPanel.add(setMaskTo(), BorderLayout.CENTER);
    	ipPanel.add(setNumIpTo(), BorderLayout.EAST);
    	ipPanel.add(setButton(), BorderLayout.SOUTH);
    	mainPanel.add(ipPanel);
    	add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel setIpTo() {
    	JLabel ipLabel = new JLabel(JMeterUtils.getResString("ip_address_label"));
    	IpAddress = new JTextField(15);
    	ipLabel.setLabelFor(IpAddress);
    	JPanel ipPanel = new JPanel(new BorderLayout(5, 0));
    	ipPanel.add(ipLabel, BorderLayout.WEST);
    	ipPanel.add(IpAddress, BorderLayout.CENTER);
    	return ipPanel;
    }
    
    private JPanel setMaskTo() {
    	JLabel maskLabel = new JLabel(JMeterUtils.getResString("ip_mask_label"));
    	Mask = new JTextField(15);
    	maskLabel.setLabelFor(Mask);
    	JPanel maskPanel = new JPanel(new BorderLayout(5, 0));
    	maskPanel.add(maskLabel, BorderLayout.WEST);
    	maskPanel.add(Mask, BorderLayout.CENTER);
    	return maskPanel;
    }
    
    private JPanel setNumIpTo() {
    	JLabel numIpLabel = new JLabel(JMeterUtils.getResString("ip_num_label"));
    	NumIp = new JTextField(15);
    	numIpLabel.setLabelFor(NumIp);
    	JPanel numPanel = new JPanel(new BorderLayout(5, 0));
    	numPanel.add(numIpLabel, BorderLayout.WEST);
    	numPanel.add(NumIp, BorderLayout.CENTER);
    	return numPanel;
    }
    
    private JPanel setButton() {
    	createIp = new JButton(JMeterUtils.getResString("ip_create_addresses"));
    	createIp.addActionListener(this);
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 0));
    	buttonPanel.add(createIp, BorderLayout.WEST);
    	return buttonPanel;
    }
    
    public void actionPerformed(ActionEvent ae)
    {
    	System.out.println("You Pressed the button");
    	IpGeneration.IP_ADDRESS = IpAddress.getText();
    	IpGeneration.MASK = Mask.getText();
    	IpGeneration.NUM_IP = NumIp.getText();
    	IpGeneration.generate();
    }
}