package org.apache.jmeter.sampler.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;

import org.apache.jmeter.config.*;
import org.apache.jmeter.config.gui.LoginConfigGui;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Start;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.sampler.tpcc.*;
import org.apache.jmeter.sampler.tpcc.jdbc.*;
import org.apache.jmeter.sampler.tpcc.tables.*;
import org.apache.jmeter.sampler.tpcc.util.*;
import org.apache.jmeter.sampler.*;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.engine.*;

import java.util.LinkedList;

import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.HashTreeTraverser;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.jmeter.gui.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;
import org.apache.jmeter.functions.*;
import org.apache.jmeter.control.*;
/**
 * Generates the GUI to be used by the TPC-C Sampler
 * @author naman
 *
 */


public class TPCCSamplerGUI extends AbstractSamplerGui
{
    private static final long serialVersionUID = 240L;
    public static boolean stat;

    private JTextField tdburl;
    private JTextField tdriver_class;
    private JTextField tusername;
    private JPasswordField tpassword;
    static JTextField tdb_name;
    private JTextField tnum_whse;
   
    private JLabel ldburl;
    private JLabel ldriver_class;
    private JLabel lusername;
    private JLabel lpassword;
    private JLabel ldb_name;
    private JLabel lnum_whse;
    static  JLabel message;
    static  JLabel messagefield;
        
    private JButton create_db;
    private JButton start_test;
    private javax.swing.JRadioButton mysqlButton;
    private javax.swing.JRadioButton oracleButton;
    

    public TPCCSamplerGUI() {
        init();
    }
    
    public String getDBurl() {
        return tdburl.getText();
    }

    public String getDriverClass() {
        return tdriver_class.getText();
    }
    
    public String getUser() {
        return tusername.getText();
    }
    
    public String getPass() {
        return tpassword.getPassword().toString();
    }
    
    public String getDB() {
        return tdb_name.getText();
    }
    
    public String getNumWhse() {
        return tnum_whse.getText();
    }
    

    @Override
    public void configure(TestElement element) 
    {
        
        
        tdburl.setText(element.getPropertyAsString(TPCCSampler.DBURL));
        tdriver_class.setText(element.getPropertyAsString(TPCCSampler.DRIVER_CLASS));
        tusername.setText(element.getPropertyAsString(TPCCSampler.USERNAME));
        tpassword.setText(element.getPropertyAsString(TPCCSampler.PASSWORD));
        tdb_name.setText(element.getPropertyAsString(TPCCSampler.DB_NAME));
        tnum_whse.setText(element.getPropertyAsString(TPCCSampler.NUM_WHSE));
        
        super.configure(element);
    }

    @Override
    public TestElement createTestElement() 
    {
        TPCCSampler sampler = new TPCCSampler();
        modifyTestElement(sampler);
        return sampler;
    }
    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    @Override
    public void modifyTestElement(TestElement te) 
    {
        //sampler.clear();
        te.clear();
        super.configureTestElement(te);
        te.setProperty(TPCCSampler.DBURL, getDBurl());
        te.setProperty(TPCCSampler.DRIVER_CLASS, getDriverClass());
        te.setProperty(TPCCSampler.USERNAME, getUser());
        te.setProperty(TPCCSampler.PASSWORD, getPass());
        te.setProperty(TPCCSampler.DB_NAME, getDB());
        te.setProperty(TPCCSampler.NUM_WHSE, getNumWhse());
        System.out.println("inside modify");

    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    @Override
    public void clearGui()
    {
        super.clearGui();
        
    }

    @Override
    public String getLabelResource()
    {
        return "tpcc_testing_title"; // $NON-NLS-1$
    }

    private void init() 
    {
    	GridBagConstraints gridBagConstraints , gridBagConstraintsMain;
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        add(makeTitlePanel(), BorderLayout.NORTH);
        
        ldburl = new JLabel(JMeterUtils.getResString("tpcc_db_url")); // $NON-NLS-1$
        ldriver_class = new JLabel(JMeterUtils.getResString("tpcc_driver_class")); // $NON-NLS-1$
        lusername = new JLabel(JMeterUtils.getResString("tpcc_username")); // $NON-NLS-1$
        lpassword = new JLabel(JMeterUtils.getResString("tpcc_password")); // $NON-NLS-1$
        ldb_name = new JLabel(JMeterUtils.getResString("tpcc_db_name")); // $NON-NLS-1$
        lnum_whse = new JLabel(JMeterUtils.getResString("tpcc_num_whse")); // $NON-NLS-1$
        message=new JLabel(JMeterUtils.getResString("tpcc_message")); // $NON-NLS-1$
        messagefield=new JLabel();
                
        tdburl = new JTextField(60);
        tdriver_class= new JTextField(60);
        tusername = new JTextField(30);
        tpassword = new JPasswordField(30);
        tpassword.setText("");
        tdb_name = new JTextField(30);
        tnum_whse = new JTextField(6);
        
                
        create_db=new JButton(JMeterUtils.getResString("tpcc_create_db"));
        start_test=new JButton(JMeterUtils.getResString("tpcc_start_test"));
        oracleButton=new JRadioButton(JMeterUtils.getResString("tpcc_oracle_radio"));
        oracleButton.setActionCommand("oracle");
        mysqlButton=new JRadioButton(JMeterUtils.getResString("tpcc_mysql_radio"));
        mysqlButton.setActionCommand("mysql");
        mysqlButton.setSelected(true);
        
        
        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(oracleButton);
        group.add(mysqlButton);
        
        RadioListener myListener = new RadioListener();
        oracleButton.addActionListener(myListener);
        mysqlButton.addActionListener(myListener);
        
        tdburl.setText("");
        tpassword.setText("");
        tdriver_class.setText("");
        tdb_name.setText("");
        tnum_whse.setText("");
        tusername.setText("");
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        
        JPanel tpccConfigurations = new JPanel(new GridBagLayout());
        tpccConfigurations.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                JMeterUtils.getResString("tpcc_configuration"))); // $NON-NLS-1$

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        tpccConfigurations.add(ldburl, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        tpccConfigurations.add(tdburl, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        tpccConfigurations.add(ldriver_class, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        tpccConfigurations.add(tdriver_class, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        tpccConfigurations.add(lusername, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        tpccConfigurations.add(tusername, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        tpccConfigurations.add(lpassword, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        tpccConfigurations.add(tpassword, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        tpccConfigurations.add(ldb_name, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        tpccConfigurations.add(tdb_name, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        tpccConfigurations.add(lnum_whse, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        tpccConfigurations.add(tnum_whse, gridBagConstraints);
        

        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        tpccConfigurations.add(mysqlButton, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        tpccConfigurations.add(oracleButton, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        tpccConfigurations.add(create_db, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        tpccConfigurations.add(start_test, gridBagConstraints);
        
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        tpccConfigurations.add(message, gridBagConstraints);
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        tpccConfigurations.add(messagefield, gridBagConstraints);
        
        

        add(tpccConfigurations,BorderLayout.CENTER);
        
        create_db.addActionListener(new ActionListener() 
        {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub

				TPCCSampler.DBURL=tdburl.getText();
				TPCCSampler.DRIVER_CLASS=tdriver_class.getText();
				TPCCSampler.USERNAME=tusername.getText();
				TPCCSampler.PASSWORD=tpassword.getText();
				TPCCSampler.DB_NAME=tdb_name.getText();
				TPCCSampler.NUM_WHSE=tnum_whse.getText();
				if(tnum_whse.getText().equals(""))
					JOptionPane.showMessageDialog(null, "Enter the number of warehouses");
				TPCCSampler samp=new TPCCSampler();
				samp.callCreator(stat);
			}
		});
        start_test.addActionListener(new ActionListener()
        {
			
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// TODO Auto-generated method stub
				
				TPCCSampler.NUM_WHSE=tnum_whse.getText();
				PassingVar.numwhs=Integer.parseInt(TPCCSampler.NUM_WHSE);
				TPCCSampler.DBURL=tdburl.getText();
				PassingVar.dburl=TPCCSampler.DBURL;
				TPCCSampler.DRIVER_CLASS=tdriver_class.getText();
				PassingVar.driver=TPCCSampler.DRIVER_CLASS;
				TPCCSampler.USERNAME=tusername.getText();
				PassingVar.username=TPCCSampler.USERNAME;
				TPCCSampler.PASSWORD=tpassword.getText();
				PassingVar.password=TPCCSampler.PASSWORD;
				TPCCSampler.DB_NAME=tdb_name.getText();
				PassingVar.dbname=TPCCSampler.DB_NAME;
				if(stat)
					PassingVar.status=1;
				else
					PassingVar.status=0;
					
				
				ActionEvent e=new ActionEvent(this, 5, ActionNames.ACTION_START);
				Start st=new Start();
				st.doAction(e);

			}
		});
    }
}
        

class RadioListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
    	if(e.getActionCommand().equalsIgnoreCase("oracle"))
    	{
    		TPCCSamplerGUI.stat=true;
    		org.apache.jmeter.sampler.gui.TPCCSamplerGUI.tdb_name.setText("XE");
    		JOptionPane.showMessageDialog(null, "Please use service name in the field Database Name/Service Name in case of Oracle");
    		TPCCSamplerGUI.messagefield.setText("Enter the DB URL upto the last :(colon) without the name of the database");
    	}
    	
    	else
    	{
    		TPCCSamplerGUI.stat=false;
    		org.apache.jmeter.sampler.gui.TPCCSamplerGUI.tdb_name.setText("");
    		JOptionPane.showMessageDialog(null, "Enter a DB name which you want to create");
    		TPCCSamplerGUI.messagefield.setText("Enter the DB URL upto the last /(forward slash) without the name of the database");
    	}

    }
}



  
