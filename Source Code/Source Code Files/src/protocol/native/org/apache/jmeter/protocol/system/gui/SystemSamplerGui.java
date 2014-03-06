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

package org.apache.jmeter.protocol.system.gui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.gui.util.FilePanelEntry;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.protocol.system.SystemSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextField;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;

/**
 * GUI for {@link SystemSampler}
 */
public class SystemSamplerGui extends AbstractSamplerGui implements ItemListener {

    /**
     * 
     */
    private static final long serialVersionUID = -2413845772703695934L;
    
    private JCheckBox checkReturnCode;
    private JLabeledTextField desiredReturnCode;
    private final FilePanelEntry stdin = new FilePanelEntry(JMeterUtils.getResString("system_sampler_stdin")); // $NON-NLS-1$
    private final FilePanelEntry stdout = new FilePanelEntry(JMeterUtils.getResString("system_sampler_stdout")); // $NON-NLS-1$
    private final FilePanelEntry stderr = new FilePanelEntry(JMeterUtils.getResString("system_sampler_stderr")); // $NON-NLS-1$
    private JLabeledTextField directory;
    private JLabeledTextField command;
    private ArgumentsPanel argsPanel;
    private ArgumentsPanel envPanel;
    
    /**
     * Constructor for JavaTestSamplerGui
     */
    public SystemSamplerGui() {
        super();
        init();
    }

    @Override
    public String getLabelResource() {
        return "system_sampler_title"; // $NON-NLS-1$
    }

    @Override
    public String getStaticLabel() {
        return JMeterUtils.getResString(getLabelResource());
    }

    /**
     * Initialize the GUI components and layout.
     */
    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        add(makeTitlePanel(), BorderLayout.NORTH);
        add(makeCommandPanel(), BorderLayout.CENTER);
        
        JPanel streamsCodePane = new JPanel(new BorderLayout());
        streamsCodePane.add(makeStreamsPanel(), BorderLayout.NORTH);
        streamsCodePane.add(makeReturnCodePanel(), BorderLayout.SOUTH);
        add(streamsCodePane, BorderLayout.SOUTH);
    }

    /* Implements JMeterGuiComponent.createTestElement() */
    @Override
    public TestElement createTestElement() {
        SystemSampler sampler = new SystemSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement sampler) {
        super.configureTestElement(sampler);
        SystemSampler systemSampler = (SystemSampler)sampler;
        systemSampler.setCheckReturnCode(checkReturnCode.isSelected());
        if(checkReturnCode.isSelected()) {
            if(!StringUtils.isEmpty(desiredReturnCode.getText())) {
                systemSampler.setExpectedReturnCode(Integer.parseInt(desiredReturnCode.getText()));
            } else {
                systemSampler.setExpectedReturnCode(SystemSampler.DEFAULT_RETURN_CODE);
            }
        } else {
            systemSampler.setExpectedReturnCode(SystemSampler.DEFAULT_RETURN_CODE);
        }
        systemSampler.setCommand(command.getText());
        systemSampler.setArguments((Arguments)argsPanel.createTestElement());
        systemSampler.setEnvironmentVariables((Arguments)envPanel.createTestElement());
        systemSampler.setDirectory(directory.getText());
        systemSampler.setStdin(stdin.getFilename());
        systemSampler.setStdout(stdout.getFilename());
        systemSampler.setStderr(stderr.getFilename());
    }

    /* Overrides AbstractJMeterGuiComponent.configure(TestElement) */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        SystemSampler systemSampler = (SystemSampler) el;
        checkReturnCode.setSelected(systemSampler.getCheckReturnCode());
        desiredReturnCode.setText(Integer.toString(systemSampler.getExpectedReturnCode()));
        desiredReturnCode.setEnabled(checkReturnCode.isSelected());
        command.setText(systemSampler.getCommand());
        argsPanel.configure(systemSampler.getArguments());
        envPanel.configure(systemSampler.getEnvironmentVariables());
        directory.setText(systemSampler.getDirectory());
        stdin.setFilename(systemSampler.getStdin());
        stdout.setFilename(systemSampler.getStdout());
        stderr.setFilename(systemSampler.getStderr());
    }

    /**
     * @return JPanel return code config
     */
    private JPanel makeReturnCodePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                JMeterUtils.getResString("return_code_config_box_title"))); // $NON-NLS-1$
        checkReturnCode = new JCheckBox(JMeterUtils.getResString("check_return_code_title")); // $NON-NLS-1$
        checkReturnCode.addItemListener(this);
        desiredReturnCode = new JLabeledTextField(JMeterUtils.getResString("expected_return_code_title")); // $NON-NLS-1$
        desiredReturnCode.setSize(desiredReturnCode.getSize().height, 30);
        panel.add(checkReturnCode);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(desiredReturnCode);
        checkReturnCode.setSelected(true);
        return panel;
    }
    
    /**
     * @return JPanel Command + directory
     */
    private JPanel makeCommandPanel() {       
        JPanel cmdPanel = new JPanel();
        cmdPanel.setLayout(new BoxLayout(cmdPanel, BoxLayout.X_AXIS));

        JPanel cmdWkDirPane = new JPanel(new BorderLayout());
        command = new JLabeledTextField(JMeterUtils.getResString("command_field_title")); // $NON-NLS-1$
        cmdWkDirPane.add(command, BorderLayout.CENTER);
        directory = new JLabeledTextField(JMeterUtils.getResString("directory_field_title")); // $NON-NLS-1$
        cmdWkDirPane.add(directory, BorderLayout.EAST);
        cmdPanel.add(cmdWkDirPane);
        
        JPanel panel = new VerticalPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                JMeterUtils.getResString("command_config_box_title"))); // $NON-NLS-1$
        panel.add(cmdPanel, BorderLayout.NORTH);
        panel.add(makeArgumentsPanel(), BorderLayout.CENTER);
        panel.add(makeEnvironmentPanel(), BorderLayout.SOUTH);
        return panel;
    }
    
    /**
     * @return JPanel Arguments Panel
     */
    private JPanel makeArgumentsPanel() {
        argsPanel = new ArgumentsPanel(JMeterUtils.getResString("arguments_panel_title"), null, true, false ,  // $NON-NLS-1$
                new ObjectTableModel(new String[] { ArgumentsPanel.COLUMN_RESOURCE_NAMES_1 },
                        Argument.class,
                        new Functor[] {
                        new Functor("getValue") },  // $NON-NLS-1$
                        new Functor[] {
                        new Functor("setValue") }, // $NON-NLS-1$
                        new Class[] {String.class }));
        return argsPanel;
    }
    
    /**
     * @return JPanel Environment Panel
     */
    private JPanel makeEnvironmentPanel() {
        envPanel = new ArgumentsPanel(JMeterUtils.getResString("environment_panel_title")); // $NON-NLS-1$
        return envPanel;
    }

    /**
     * @return JPanel Streams Panel
     */
    private JPanel makeStreamsPanel() {
        JPanel stdPane = new JPanel(new BorderLayout());
        stdPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                JMeterUtils.getResString("command_config_std_streams_title"))); // $NON-NLS-1$
        stdPane.add(stdin, BorderLayout.NORTH);
        stdPane.add(stdout, BorderLayout.CENTER);
        stdPane.add(stderr, BorderLayout.SOUTH);
        return stdPane;
    }

    /**
     * @see org.apache.jmeter.gui.AbstractJMeterGuiComponent#clearGui()
     */
    @Override
    public void clearGui() {
        super.clearGui();
        directory.setText(""); // $NON-NLS-1$
        command.setText(""); // $NON-NLS-1$
        argsPanel.clearGui();
        envPanel.clearGui();
        desiredReturnCode.setText(""); // $NON-NLS-1$
        checkReturnCode.setSelected(false);
        desiredReturnCode.setEnabled(false);
        stdin.clearGui();
        stdout.clearGui();
        stderr.clearGui();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource()==checkReturnCode) {
            desiredReturnCode.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        }
    }
}