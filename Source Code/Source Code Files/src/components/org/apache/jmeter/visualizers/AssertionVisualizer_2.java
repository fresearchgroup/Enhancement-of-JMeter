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
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.jmeter.assertions.AssertionResult;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;

public class AssertionVisualizer_2 extends AbstractVisualizer implements Clearable {

    private static final long serialVersionUID = 240L;

    private JTextArea textArea;

    public AssertionVisualizer_2() {
        init();
        setName(getStaticLabel());
    }

    @Override
    public String getLabelResource() {
        return "assertion_visualizer_title2"; // $NON-NLS-1$
    }

    @Override
    public void add(SampleResult sample) {
        final StringBuilder sb = new StringBuilder(100);
        sb.append(sample.getSampleLabel());
        sb.append(getAssertionResult(sample));
        sb.append("\n"); // $NON-NLS-1$
        JMeterUtils.runSafe(new Runnable() {
            @Override
            public void run() {
                synchronized (textArea) {
                    textArea.append(sb.toString());
                    textArea.setCaretPosition(textArea.getText().length());
                }                
            }
        });
    }

    @Override
    public void clearData() {
        textArea.setText(""); // $NON-NLS-1$
    }

    private String getAssertionResult(SampleResult res) {
    	String str;
        if (res != null) {
            StringBuilder display = new StringBuilder();
            AssertionResult assertionResults[] = res.getAssertionResults();
            if(assertionResults.length>0)
            {
            	display.append("\n"); // $NON-NLS-1$

            	   display.append(res.getThreadName()); // $NON-NLS-1$
                   display.append("\t");
                   display.append(res.getUrlAsString()); // $NON-NLS-1$
                   /*str= String.valueOf(res.getStartTime());
                   display.append("\tStart time:");
                   display.append(str);
                   str= String.valueOf(res.getEndTime());
                   display.append("\tEnd time:");
                   display.append(str);*/
                   display.append("\n");
                   
            	
            }
            for (int i = 0; i < assertionResults.length; i++) {
                AssertionResult item = assertionResults[i];

                if (item.isFailure() || item.isError()) {
                	display.append("Failure\t");              
                    display.append(item.getName() != null ? item.getName() + " : " : "");// $NON-NLS-1$
                    display.append(item.getFailureMessage());
                    display.append("\n");
                }
                else
                {
                	display.append("Pass\t");
                	 display.append(item.getName() != null ? item.getName() : "");// $NON-NLS-1$
                     //display.append(item.getFailureMessage());
                     display.append("\n");
                	
                }
            }
            return display.toString();
        }
        return "No assertions added ";
    }

    private void init() {
        this.setLayout(new BorderLayout());

        // MAIN PANEL
        Border margin = new EmptyBorder(10, 10, 5, 10);

        this.setBorder(margin);

        // NAME
        this.add(makeTitlePanel(), BorderLayout.NORTH);

        // TEXTAREA LABEL
        JLabel textAreaLabel =
            new JLabel(JMeterUtils.getResString("assertion_textarea_label")); // $NON-NLS-1$
        Box mainPanel = Box.createVerticalBox();
        mainPanel.add(textAreaLabel);

        // TEXTAREA
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        JScrollPane areaScrollPane = new JScrollPane(textArea);

        areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        areaScrollPane.setPreferredSize(new Dimension(mainPanel.getWidth(),mainPanel.getHeight()));
        mainPanel.add(areaScrollPane);
        this.add(mainPanel, BorderLayout.CENTER);
    }
}
