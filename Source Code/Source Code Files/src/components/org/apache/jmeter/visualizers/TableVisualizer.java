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

package org.apache.jmeter.visualizers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.Format;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.reporters.MailerModel;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.Calculator;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.gui.RendererUtils;
import org.apache.jorphan.gui.RightAlignRenderer;
import org.apache.jorphan.gui.layout.VerticalLayout;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.reflect.Functor;
import org.apache.log.Logger;

/**
 * This class implements a statistical analyser that calculates both the average
 * and the standard deviation of the sampling process. The samples are displayed
 * in a JTable, and the statistics are displayed at the bottom of the table.
 *
 * created March 10, 2002
 *
 */
public class TableVisualizer extends AbstractVisualizer implements Clearable {

    private static final long serialVersionUID = 240L;
    
    private static final Logger log = LoggingManager.getLoggerForClass();

    // Note: the resource string won't respond to locale-changes,
    // however this does not matter as it is only used when pasting to the clipboard
    private static final ImageIcon imageSuccess = JMeterUtils.getImage(
            JMeterUtils.getPropDefault("viewResultsTree.success",  //$NON-NLS-1$
                                       "icon_success_sml.gif"),    //$NON-NLS-1$
            JMeterUtils.getResString("table_visualizer_success")); //$NON-NLS-1$

    private static final ImageIcon imageFailure = JMeterUtils.getImage(
            JMeterUtils.getPropDefault("viewResultsTree.failure",  //$NON-NLS-1$
                                       "icon_warning_sml.gif"),    //$NON-NLS-1$
            JMeterUtils.getResString("table_visualizer_warning")); //$NON-NLS-1$

//    private final JPanel filterPanel;
    private final JTextField lowerLimit = new JTextField(5);
    private JComboBox filterColumnName;
    private JComboBox filterOperation;
    
    private static final String[] COLUMNS = new String[] {
            "table_visualizer_sample_num",  // $NON-NLS-1$
            "table_visualizer_start_time",  // $NON-NLS-1$
            "table_visualizer_thread_name", // $NON-NLS-1$
            "sampler_label",                // $NON-NLS-1$
            "table_visualizer_sample_time", // $NON-NLS-1$
            "table_visualizer_status",      // $NON-NLS-1$
            "table_visualizer_bytes",       // $NON-NLS-1$
            "table_visualizer_latency"};    // $NON-NLS-1$

    private ObjectTableModel model = null;

    private JTable table = null;

    private JTextField dataField = null;

    private JTextField averageField = null;

    private JTextField deviationField = null;

    private JTextField noSamplesField = null;

    private JScrollPane tableScrollPanel = null;

    private JCheckBox autoscroll = null;

    private JCheckBox childSamples = null;

    private transient Calculator calc = new Calculator();

    private Format format = new SimpleDateFormat("HH:mm:ss.SSS"); //$NON-NLS-1$

    // Column renderers
    private static final TableCellRenderer[] RENDERERS =
        new TableCellRenderer[]{
            new RightAlignRenderer(), // Sample number (string)
            new RightAlignRenderer(), // Start Time
            null, // Thread Name
            null, // Label
            null, // Sample Time
            null, // Status
            null, // Bytes
        };

    /**
     * Constructor for the TableVisualizer object.
     */
    public TableVisualizer() {
        super();
        model = new ObjectTableModel(COLUMNS,
                TableSample.class,         // The object used for each row
                
                new Functor[] {
                new Functor("getSampleNumberString"), // $NON-NLS-1$
                new Functor("getStartTimeFormatted",  // $NON-NLS-1$
                        new Object[]{format}),
                new Functor("getThreadName"), // $NON-NLS-1$
                new Functor("getLabel"), // $NON-NLS-1$
                new Functor("getElapsed"), // $NON-NLS-1$
                new SampleSuccessFunctor("isSuccess"), // $NON-NLS-1$
                new Functor("getBytes"), // $NON-NLS-1$
                new Functor("getLatency") }, // $NON-NLS-1$
                
                new Functor[] { null, null, null, null, null, null, null, null },
                new Class[] {
                String.class, String.class, String.class, String.class, Long.class, ImageIcon.class, Long.class, Long.class });
        init();
    }

    public static boolean testFunctors(){
        TableVisualizer instance = new TableVisualizer();
        return instance.model.checkFunctors(null,instance.getClass());
    }


    @Override
    public String getLabelResource() {
        return "view_results_in_table"; // $NON-NLS-1$
    }

    protected synchronized void updateTextFields(SampleResult res) {
        noSamplesField.setText(Long.toString(calc.getCount()));
        dataField.setText(Long.toString(res.getTime()/res.getSampleCount()));
        averageField.setText(Long.toString((long) calc.getMean()));
        deviationField.setText(Long.toString((long) calc.getStandardDeviation()));
    }

    @Override
    public void add(final SampleResult res) {
        JMeterUtils.runSafe(new Runnable() {
            @Override
            public void run() {
                if (childSamples.isSelected()) {
                    SampleResult[] subResults = res.getSubResults();
                    if (subResults.length > 0) {
                        for (SampleResult sr : subResults) {
                            add(sr);
                        }
                        return;
                    }
                }
                synchronized (calc) {
                	int Filtervalue = 0; 
                	String sValue = lowerLimit.getText();
                	sValue = sValue.trim();
                	if(sValue!="" && sValue!=null)
                	{
                		Filtervalue = Integer.parseInt(sValue);
                		log.info("here svalue ="+sValue+"----");
                	}
                	long getValue = 0;
                	switch(filterColumnName.getSelectedIndex()){
	            		case 1:
	            			getValue = res.getTime();
	            			break;
	            		case 2:
	            			getValue = res.getBytes();
	            			break;
	            		case 3:
	            			getValue = res.getLatency();
	            			break;
	            	}
	            	
                    calc.addSample(res);
                    int count = calc.getCount();
                    TableSample newS = new TableSample(
                            count, 
                            res.getSampleCount(), 
                            res.getStartTime(), 
                            res.getThreadName(), 
                            res.getSampleLabel(),
                            res.getTime(),
                            res.isSuccessful(),
                            res.getBytes(),
                            res.getLatency());
                    switch(filterOperation.getSelectedIndex()) {
            		case 1:
            			if(getValue==Filtervalue)
            				model.addRow(newS);
            			break;
            		case 2:
            			if(getValue<Filtervalue)
            				model.addRow(newS);
            			break;
            		case 3:
            			if(getValue>Filtervalue)
            				model.addRow(newS);
            			break;
            		default:
            			model.addRow(newS);
            			log.info("here------------------------------------>>>>>>>>>>>>>>>>>>>>>>>");
            			break;
                    }
                }
                updateTextFields(res);
                if (autoscroll.isSelected()) {
                    table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
                }
            }
        });
    }

    @Override
    public synchronized void clearData() {
        model.clearData();
        calc.clear();
        noSamplesField.setText("0"); // $NON-NLS-1$
        dataField.setText("0"); // $NON-NLS-1$
        averageField.setText("0"); // $NON-NLS-1$
        deviationField.setText("0"); // $NON-NLS-1$
        repaint();
    }

    @Override
    public String toString() {
        return "Show the samples in a table";
    }

    private void init() {
        this.setLayout(new BorderLayout());

        // MAIN PANEL
        JPanel mainPanel = new JPanel();
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton testerButton = new JButton("Filter"); // $NON-NLS-1$
        filterColumnName = new JComboBox(new Object[] {
        		" ",
                "Response Time", 
                "Packet Size",
                "Latency"});
        filterColumnName.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXX");
        filterOperation = new JComboBox(new Object[] {
        		" ",
                "=", 
                "<",
                ">"});;
        filterOperation.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXX");
        Border margin = new EmptyBorder(10, 10, 5, 10);
        
        mainPanel.setBorder(margin);
        mainPanel.setLayout(new VerticalLayout(5, VerticalLayout.BOTH));

        // NAME
        
        mainPanel.add(makeTitlePanel());
        log.info("creating filter panel");
//        lowerLimit.setPreferredSize(new Dimension(100, 30));
        filterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Filter Results")); // $NON-NLS-1$
        
        filterPanel.add(new JLabel("Filter Limit"));
        filterPanel.add(filterColumnName);
        filterPanel.add(filterOperation);
        lowerLimit.setText("0");
        filterPanel.add(lowerLimit);
        filterPanel.add(testerButton);
        
        log.info("created filter panel");
        mainPanel.add(filterPanel);
        
        // Set up the table itself
        table = new JTable(model);
        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        // table.getTableHeader().setReorderingAllowed(false);
        RendererUtils.applyRenderers(table, RENDERERS);

        tableScrollPanel = new JScrollPane(table);
        tableScrollPanel.setViewportBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        autoscroll = new JCheckBox(JMeterUtils.getResString("view_results_autoscroll")); //$NON-NLS-1$

        childSamples = new JCheckBox(JMeterUtils.getResString("view_results_childsamples")); //$NON-NLS-1$

        // Set up footer of table which displays numerics of the graphs
        JPanel dataPanel = new JPanel();
        JLabel dataLabel = new JLabel(JMeterUtils.getResString("graph_results_latest_sample")); // $NON-NLS-1$
        dataLabel.setForeground(Color.black);
        dataField = new JTextField(5);
        dataField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        dataField.setEditable(false);
        dataField.setForeground(Color.black);
        dataField.setBackground(getBackground());
        dataPanel.add(dataLabel);
        dataPanel.add(dataField);

        JPanel averagePanel = new JPanel();
        JLabel averageLabel = new JLabel(JMeterUtils.getResString("graph_results_average")); // $NON-NLS-1$
        averageLabel.setForeground(Color.blue);
        averageField = new JTextField(5);
        averageField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        averageField.setEditable(false);
        averageField.setForeground(Color.blue);
        averageField.setBackground(getBackground());
        averagePanel.add(averageLabel);
        averagePanel.add(averageField);

        JPanel deviationPanel = new JPanel();
        JLabel deviationLabel = new JLabel(JMeterUtils.getResString("graph_results_deviation")); // $NON-NLS-1$
        deviationLabel.setForeground(Color.red);
        deviationField = new JTextField(5);
        deviationField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        deviationField.setEditable(false);
        deviationField.setForeground(Color.red);
        deviationField.setBackground(getBackground());
        deviationPanel.add(deviationLabel);
        deviationPanel.add(deviationField);

        JPanel noSamplesPanel = new JPanel();
        JLabel noSamplesLabel = new JLabel(JMeterUtils.getResString("graph_results_no_samples")); // $NON-NLS-1$

        noSamplesField = new JTextField(8);
        noSamplesField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        noSamplesField.setEditable(false);
        noSamplesField.setForeground(Color.black);
        noSamplesField.setBackground(getBackground());
        noSamplesPanel.add(noSamplesLabel);
        noSamplesPanel.add(noSamplesField);

        JPanel tableInfoPanel = new JPanel();
        tableInfoPanel.setLayout(new FlowLayout());
        tableInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tableInfoPanel.add(noSamplesPanel);
        tableInfoPanel.add(dataPanel);
        tableInfoPanel.add(averagePanel);
        tableInfoPanel.add(deviationPanel);

        JPanel tableControlsPanel = new JPanel(new BorderLayout());
        tableControlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel jp = new HorizontalPanel();
        jp.add(autoscroll);
        jp.add(childSamples);
        tableControlsPanel.add(jp, BorderLayout.WEST);
        tableControlsPanel.add(tableInfoPanel, BorderLayout.CENTER);

        // Set up the table with footer
        JPanel tablePanel = new JPanel();

        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(tableScrollPanel, BorderLayout.CENTER);
        tablePanel.add(tableControlsPanel, BorderLayout.SOUTH);

        // Add the main panel and the graph
        this.add(mainPanel, BorderLayout.NORTH);
        this.add(tablePanel, BorderLayout.CENTER);
    }

    public static class SampleSuccessFunctor extends Functor {
        public SampleSuccessFunctor(String methodName) {
            super(methodName);
        }

        @Override
        public Object invoke(Object p_invokee) {
            Boolean success = (Boolean)super.invoke(p_invokee);

            if(success != null) {
                if(success.booleanValue()) {
                    return imageSuccess;
                }
                else {
                    return imageFailure;
                }
            }
            else {
                return null;
            }
        }
    }
}
