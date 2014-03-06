package org.apache.jmeter.sampler.tpcc;

import static org.apache.jmeter.sampler.tpcc.jTPCCConfig.configCommitCount;
import static org.apache.jmeter.sampler.tpcc.jTPCCConfig.configCustPerDist;
import static org.apache.jmeter.sampler.tpcc.jTPCCConfig.configDistPerWhse;
import static org.apache.jmeter.sampler.tpcc.jTPCCConfig.configItemCount;
import static org.apache.jmeter.sampler.tpcc.jTPCCConfig.configWhseCount;


import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;
import java.sql.*;
import java.text.SimpleDateFormat;


import org.apache.jmeter.sampler.tpcc.tables.*;
import org.apache.jmeter.sampler.tpcc.jdbc.*;
import org.apache.jmeter.sampler.TPCCSampler;


/**
 * Contains the code for loading the all the tables with the  required data
 * when working with Oracle databse
 * @author naman
 *
 */
public class TPCCLoaderOracle
{
    private Connection conn = null;
	public TPCCLoaderOracle()
	{
	          
        
        if (numWarehouses == 0) 
        {            
            numWarehouses = 1;
        }
        outputFiles= false;
        try 
        {
            Class.forName(TPCCSampler.DRIVER_CLASS).newInstance();
            final String dburl=TPCCSampler.DBURL+TPCCSampler.DB_NAME;
            this.conn = DriverManager.getConnection(dburl,TPCCSampler.USERNAME,TPCCSampler.PASSWORD);
            this.conn.setAutoCommit(false);
            Statement s1=null;
            s1=this.conn.createStatement();
        } 
        catch (ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) 
        {
            e.printStackTrace();
        } 
        catch (InstantiationException e) 
        {
            e.printStackTrace();
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
        catch (java.lang.IllegalArgumentException e) 
        {
            e.printStackTrace();
        } 
        catch (java.lang.NullPointerException e) 
        {
        	e.printStackTrace();
            System.exit(1);
        }
        catch (ExceptionInInitializerError e) 
        {
           e.printStackTrace();
        }
        catch (java.lang.SecurityException e) 
        {
            e.printStackTrace();
        }
	}

	static boolean fastLoad;
	static String fastLoaderBaseDir;

	
	private static java.util.Date now = null;
	private static java.util.Date startDate = null;
	private static java.util.Date endDate = null;

	private static Random gen;
	private static int numWarehouses = 0;
	private static String fileLocation = "";
	private static boolean outputFiles = false;
	private static PrintWriter out = null;
	private static long lastTimeMS = 0;

	private static final int FIRST_UNPROCESSED_O_ID = 2101;
	
	/**
	 * method for executing rollback
	 */
	protected void transRollback() 
	{
		if (outputFiles == false) 
		{
			try 
			{
				conn.rollback();
			}
			catch (SQLException se) 
			{          
                se.printStackTrace();            
				
			}
		} 
		else 
		{
			out.close();
		}
	}

	/**
	 * method for executing commit
	 */
	protected void transCommit()
	{
		if (outputFiles == false) 
		{
			try 
			{
				conn.commit();
			} 
			catch (SQLException se)
			{
				se.printStackTrace();
				transRollback();
			}
		} 
		else 
		{
			out.close();
		}
	}

	/**
	 * method for truncating a table
	 * @param strTable name of table to be truncated
	 */
	protected void truncateTable(String strTable) 
	{

		
		try 
		{
			this.conn.createStatement().execute("TRUNCATE TABLE " + strTable);
			transCommit();
		} 
		catch (SQLException se)
		{
			se.printStackTrace();
			transRollback();
		}

	}

	/**
	 * method for loading the item table
	 * @param itemKount number of items
	 * @return
	 */
	protected int loadItem(int itemKount)
	{

		int k = 0;
		int t = 0;
		int randPct = 0;
		int len = 0;
		int startORIGINAL = 0;
		

		try 
		{
			Class.forName(TPCCSampler.DRIVER_CLASS).newInstance();
            final String dburl=TPCCSampler.DBURL+TPCCSampler.DB_NAME;
            this.conn = DriverManager.getConnection(dburl,TPCCSampler.USERNAME,TPCCSampler.PASSWORD);
            this.conn.setAutoCommit(false);
            Statement s1=null;
            s1=this.conn.createStatement();
            now = new java.util.Date();
            t = itemKount;
            if (outputFiles == true) 
            {
            	out = new PrintWriter(new FileOutputStream(fileLocation+ "item.csv"));
            }

            Item item = new Item();
            for (int i = 1; i <= itemKount; i++) 
            {
            	item.i_id = i;
                item.i_name = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(14, 24,gen));
                item.i_price = (float) (TPCCUtilOracle.randomNumber(100, 10000, gen) / 100.0);
                randPct = TPCCUtilOracle.randomNumber(1, 100, gen);
                len = TPCCUtilOracle.randomNumber(26, 50, gen);
                if (randPct > 10) 
                {
                	
                	// 90% of time i_data isa random string of length [26 .. 50]
                    item.i_data = TPCCUtilOracle.randomStr(len);
                 } 
                 else 
                 {
                     // 10% of time i_data has "ORIGINAL" crammed somewhere in middle
                     startORIGINAL = TPCCUtilOracle.randomNumber(2, (len - 8), gen);
                     item.i_data = TPCCUtilOracle.randomStr(startORIGINAL - 1)+ "ORIGINAL"
                        				+ TPCCUtilOracle.randomStr(len - startORIGINAL - 9);
                 }            	 
                        	      
                  item.i_im_id = TPCCUtilOracle.randomNumber(1, 10000, gen);
                  k++;
                  if (outputFiles == false) 
                  {
                  }
                  String ss1;
                  ss1="insert into item values ("+item.i_id+","+item.i_im_id+",'"+item.i_name+"',"+item.i_price+",'"+item.i_data+"')";
                  s1.executeUpdate(ss1);
                                        
                  if ((k % configCommitCount) == 0) 
                  {
                	  	long tmpTime = new java.util.Date().getTime();
                	  	String etStr = "  Elasped Time(ms): "
                	  	+ ((tmpTime - lastTimeMS) / 1000.000)
						+ "                    ";
                        lastTimeMS = tmpTime;
                        transCommit();
                  }
                  else 
                  {
                	  String str = "";
                      str = str + item.i_id + ",";
                      str = str + item.i_name + ",";
                      str = str + item.i_price + ",";
                      str = str + item.i_data + ",";
                      str = str + item.i_im_id;
                      if ((k % configCommitCount) == 0) 
                      {
                    	  	long tmpTime = new java.util.Date().getTime();
                            String etStr = "  Elasped Time(ms): "
                            + ((tmpTime - lastTimeMS) / 1000.000)
                            + "                    ";
                            lastTimeMS = tmpTime;
                       }
                    }

                  
            } 

			long tmpTime = new java.util.Date().getTime();
			String etStr = "  Elasped Time(ms): "
					+ ((tmpTime - lastTimeMS) / 1000.000)
					+ "                    ";
			lastTimeMS = tmpTime;

			if (outputFiles == false) {
			
			}

			transCommit();
			now = new java.util.Date();
			

		} 
        catch (SQLException se)
        {
			
			se.printStackTrace();
			transRollback();
                        
                        
		} 
        
        catch (Exception e)
        {
              e.printStackTrace();
        }

		return (k);

	} 

	/**
	 * method for loading a warehouse
	 * @param whseKount number of warehouses
	 * @return
	 */
	protected int loadWhse(int whseKount) 
	{

		try 
		{
						Class.forName(TPCCSampler.DRIVER_CLASS).newInstance();
						final String dburl=TPCCSampler.DBURL+TPCCSampler.DB_NAME;
						this.conn = DriverManager.getConnection(dburl,TPCCSampler.USERNAME,TPCCSampler.PASSWORD);
                        this.conn.setAutoCommit(false);
                        Statement s1=null;
                        s1=this.conn.createStatement();
                        now = new java.util.Date();
                        
                        if (outputFiles == true)
                        {
                        	out = new PrintWriter(new FileOutputStream(fileLocation+ "warehouse.csv"));
                        	
                        }

                        Warehouse warehouse = new Warehouse();
                        for (int i = 1; i <= whseKount; i++) 
                        {

                        	warehouse.w_id = i;
                        	warehouse.w_ytd = 300000;

                        	warehouse.w_tax = (float) ((TPCCUtilOracle.randomNumber(0, 2000, gen)) / 10000.0);

                        	warehouse.w_name = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(6,10, gen));
                        	warehouse.w_street_1 = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 20, gen));
                        	warehouse.w_street_2 = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 20, gen));
                        	warehouse.w_city = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10,20, gen));
                        	warehouse.w_state = TPCCUtilOracle.randomStr(3).toUpperCase();
                        	warehouse.w_zip = "123456789";

                            String ss2="";
                            if (outputFiles == false) 
                            {
                            	ss2="insert into warehouse values ("+warehouse.w_id+","+warehouse.w_ytd+","+warehouse.w_tax+",'"+ warehouse.w_name+"','"+warehouse.w_street_1+"','"+warehouse.w_street_2+"','"+warehouse.w_city+"','"+warehouse.w_state+"','"+warehouse.w_zip+"')";
                                s1.executeUpdate(ss2);              
                            }
                            else 
                            {
                            	String str = "";
                            	str = str + warehouse.w_id + ",";
                            	str = str + warehouse.w_ytd + ",";
                            	str = str + warehouse.w_tax + ",";
                            	str = str + warehouse.w_name + ",";
                            	str = str + warehouse.w_street_1 + ",";
                            	str= str + warehouse.w_street_2 + ",";
                            	str = str + warehouse.w_city + ",";
                            	str = str + warehouse.w_state + ",";
                            	str = str + warehouse.w_zip;
                            	out.println(str);
                            }

                        
                        } 

			transCommit();
			now = new java.util.Date();

			long tmpTime = new java.util.Date().getTime();
			lastTimeMS = tmpTime;
			

		} 
		catch (SQLException se)
		{
			
			se.printStackTrace();
			transRollback();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			transRollback();
		}

		return (whseKount);

	} 

	/**
	 * method for loading the stock table
	 * @param whseKount number of warehouses
	 * @param itemKount number of items
	 * @return
	 */
	protected int loadStock(int whseKount, int itemKount) 
	{

		int k = 0;
		int t = 0;
		int randPct = 0;
		int len = 0;
		int startORIGINAL = 0;

		try
		{
						Class.forName(TPCCSampler.DRIVER_CLASS).newInstance();
						final String dburl=TPCCSampler.DBURL+TPCCSampler.DB_NAME;
						this.conn = DriverManager.getConnection(dburl,TPCCSampler.USERNAME,TPCCSampler.PASSWORD);
                        this.conn.setAutoCommit(false);
                        Statement s1=null;
                        s1=this.conn.createStatement();
                        now = new java.util.Date();
                        t = (whseKount * itemKount);
                        if (outputFiles == true) 
                        {
                        	out = new PrintWriter(new FileOutputStream(fileLocation+ "stock.csv"));
                        	
                        }

                        Stock stock = new Stock();

                        for (int i = 1; i <= itemKount; i++) 
                        {

                        	for (int w = 1; w <= whseKount; w++) 
                        	{

                        		stock.s_i_id = i;
                        		stock.s_w_id = w;
                        		stock.s_quantity = TPCCUtilOracle.randomNumber(10, 100, gen);
                        		stock.s_ytd = 0;
                        		stock.s_order_cnt = 0;
                        		stock.s_remote_cnt = 0;

                        		
                        		randPct = TPCCUtilOracle.randomNumber(1, 100, gen);
                        		len = TPCCUtilOracle.randomNumber(26, 50, gen);
                        		if (randPct > 10) 
                        		{
                        			// 90% of time i_data is a random string of length [26 ..50]
                        			stock.s_data = TPCCUtilOracle.randomStr(len);
                        		} 
                        		else
                        		{
                        			// 10% of time i_data has "ORIGINAL" crammed somewhere in middle
                        			startORIGINAL = TPCCUtilOracle.randomNumber(2, (len - 8), gen);
                        			stock.s_data = TPCCUtilOracle.randomStr(startORIGINAL - 1)+ "ORIGINAL"+ TPCCUtilOracle.randomStr(len - startORIGINAL - 9);
                        		}

                        		stock.s_dist_01 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_02 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_03 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_04 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_05 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_06 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_07 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_08 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_09 = TPCCUtilOracle.randomStr(24);
                        		stock.s_dist_10 = TPCCUtilOracle.randomStr(24);

                        		k++;
                        		if (outputFiles == false) 
                        		{
                        			
                                    String ss2;
                                    ss2="insert into stock values ("+stock.s_i_id+","+stock.s_w_id+","+stock.s_quantity+",'"+stock.s_dist_01+"','"+stock.s_dist_02+"','"+stock.s_dist_03+"','"+stock.s_dist_04+"','"+stock.s_dist_05+"','"+stock.s_dist_06+"','"+stock.s_dist_07+"','"+stock.s_dist_08+"','"+stock.s_dist_09+"','"+stock.s_dist_10+"',"+ stock.s_ytd+","+stock.s_order_cnt+","+stock.s_remote_cnt+",'"+stock.s_data+"')";
                                    s1.executeUpdate(ss2);
                                    if ((k % configCommitCount) == 0)
                                    {
                                    	long tmpTime = new java.util.Date().getTime();
                                    	String etStr = "  Elasped Time(ms): "+ ((tmpTime - lastTimeMS) / 1000.000)+ "                    ";
                                    	lastTimeMS = tmpTime;
                                    	transCommit();
                                    }
                        		}
                        		else 
                        		{
                        			String str = "";
                        			str = str + stock.s_i_id + ",";
                        			str = str + stock.s_w_id + ",";
                        			str = str + stock.s_quantity + ",";
                        			str = str + stock.s_ytd + ",";
                        			str = str + stock.s_order_cnt + ",";
                        			str = str + stock.s_remote_cnt + ",";
                        			str = str + stock.s_data + ",";
                        			str = str + stock.s_dist_01 + ",";
                        			str = str + stock.s_dist_02 + ",";
                        			str = str + stock.s_dist_03 + ",";
                        			str = str + stock.s_dist_04 + ",";
                        			str = str + stock.s_dist_05 + ",";
                        			str = str + stock.s_dist_06 + ",";
                        			str = str + stock.s_dist_07 + ",";
                        			str = str + stock.s_dist_08 + ",";
                        			str = str + stock.s_dist_09 + ",";
                        			str = str + stock.s_dist_10;
                        			out.println(str);

                        			if ((k % configCommitCount) == 0) 
                        			{
                        				long tmpTime = new java.util.Date().getTime();
                        				String etStr = "  Elasped Time(ms): "
                        				+ ((tmpTime - lastTimeMS) / 1000.000)
                        				+ "                    ";
                        				lastTimeMS = tmpTime;
                        			}
                        		}

                        	} 

                        } 

			long tmpTime = new java.util.Date().getTime();
			String etStr = "  Elasped Time(ms): "
					+ ((tmpTime - lastTimeMS) / 1000.000)
					+ "                    ";
			lastTimeMS = tmpTime;
			if (outputFiles == false) {
			
			}
			transCommit();

			now = new java.util.Date();
			

		} 
		catch (SQLException se)
		{
			se.printStackTrace();
            transRollback();

		} 
		catch (Exception e)
		{
			e.printStackTrace();
			transRollback();
		}

		return (k);

	} 
	
	
	/**
	 * method for loading the district table
	 * @param whseKount number of warehouses
	 * @param distWhseKount districts per warehouse
	 * @return
	 */
	protected int loadDist(int whseKount, int distWhseKount) 
	{

		int k = 0;
		int t = 0;

		try 
		{
						Class.forName(TPCCSampler.DRIVER_CLASS).newInstance();
						final String dburl=TPCCSampler.DBURL+TPCCSampler.DB_NAME;
						this.conn = DriverManager.getConnection(dburl,TPCCSampler.USERNAME,TPCCSampler.PASSWORD);
                        this.conn.setAutoCommit(false);
                        Statement s1=null;
                        s1=this.conn.createStatement();
                        	    
                        now = new java.util.Date();

                        if (outputFiles == true)
                        {
                        	out = new PrintWriter(new FileOutputStream(fileLocation	+ "district.csv"));
                        	
                        }

                        District district = new District();

                        t = (whseKount * distWhseKount);
                        for (int w = 1; w <= whseKount; w++) 
                        {

                        	for (int d = 1; d <= distWhseKount; d++) 
                        	{		    
				    
                        		district.d_id = d;
                        		district.d_w_id = w;
                        		district.d_ytd = 30000;

                        		// random within [0.0000 .. 0.2000]
                        		district.d_tax = (float) ((TPCCUtilOracle.randomNumber(0, 2000,gen)) / 10000.0);

                        		district.d_next_o_id = 3001;
                        		district.d_name = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(6, 10, gen));
                        		district.d_street_1 = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 20, gen));
                        		district.d_street_2 = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 20, gen));
                        		district.d_city = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 20, gen));
                        		district.d_state = TPCCUtilOracle.randomStr(3).toUpperCase();
                        		district.d_zip = "123456789";

                        		k++;
                        		if (outputFiles == false) 
                        		{
                        			
                                    String ss2;
                                    ss2="insert into district values ("+district.d_id+","+district.d_w_id+","+district.d_ytd+","+ district.d_tax+","+district.d_next_o_id+",'"+district.d_name+"','"+district.d_street_1+"','"+district.d_street_2+"','"+district.d_city+"','"+district.d_state+"','"+district.d_zip+"')";
                                    s1.executeUpdate(ss2);
                                                
                                                
                        		}
                        		else
                        		{
                        			String str = "";
                        			str = str + district.d_id + ",";
                        			str = str + district.d_w_id + ",";
                        			str = str + district.d_ytd + ",";
                        			str = str + district.d_tax + ",";
                        			str = str + district.d_next_o_id + ",";
                        			str = str + district.d_name + ",";
                        			str = str + district.d_street_1 + ",";
                        			str = str + district.d_street_2 + ",";
                        			str = str + district.d_city + ",";
                        			str = str + district.d_state + ",";
                        			str = str + district.d_zip;
                        			out.println(str);
                        		}

                        	} 

                        }

                        long tmpTime = new java.util.Date().getTime();
                        String etStr = "  Elasped Time(ms): "
                        		+ ((tmpTime - lastTimeMS) / 1000.000)
                        		+ "                    ";
                        
                        lastTimeMS = tmpTime;
                        transCommit();
                        now = new java.util.Date();
                        
                        

		} 
		catch (SQLException se) 
		{
			se.printStackTrace();
			transRollback();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			transRollback();
		}

		return (k);

	} 
	
	
	/**
	 * method for loading the customer table
	 * @param whseKount number of warehouses
	 * @param distWhseKount districts per warehouse
	 * @param custDistKount customers per district
	 * @return
	 */
	protected int loadCust(int whseKount, int distWhseKount, int custDistKount)
	{

		int k = 0;
		int t = 0;

		CustomerOracle customer = new CustomerOracle();
		HistoryOracle history = new HistoryOracle();
		PrintWriter outHist = null;

		try 
		{
                    
						Class.forName(TPCCSampler.DRIVER_CLASS).newInstance();
						final String dburl=TPCCSampler.DBURL+TPCCSampler.DB_NAME;
						this.conn = DriverManager.getConnection(dburl,TPCCSampler.USERNAME,TPCCSampler.PASSWORD);
                        this.conn.setAutoCommit(false);
                        Statement s1=null;
                        s1=this.conn.createStatement();
                        now = new java.util.Date();

                        if (outputFiles == true) 
                        {
                        	out = new PrintWriter(new FileOutputStream(fileLocation
                        			+ "customer.csv"));
                        	
                        	outHist = new PrintWriter(new FileOutputStream(fileLocation
                        			+ "cust-hist.csv"));
                        	
                        }

                        t = (whseKount * distWhseKount * custDistKount * 2);
                        

                        for (int w = 1; w <= whseKount; w++) 
                        {

                        	for (int d = 1; d <= distWhseKount; d++) 
                        	{

                        		for (int c = 1; c <= custDistKount; c++) 
                        		{

                        			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                        			String sysdate=dateFormat.format(new java.util.Date());
                        			customer.c_id = c;
                        			customer.c_d_id = d;
                        			customer.c_w_id = w;

                        			// discount is random between [0.0000 ... 0.5000]
                        			customer.c_discount = (float) (TPCCUtilOracle.randomNumber(1,5000, gen) / 10000.0);

                        			if (TPCCUtilOracle.randomNumber(1, 100, gen) <= 10) 
                        			{
                        				customer.c_credit = "BC"; // 10% Bad Credit
                        			} 
                        			else 
                        			{
                        				customer.c_credit = "GC"; // 90% Good Credit
                        			}
                        			if (c <= 1000) 
                        			{
                        				customer.c_last = TPCCUtilOracle.getLastName(c - 1);
                        			}
                        			else 
                        			{
                        				customer.c_last = TPCCUtilOracle.getNonUniformRandomLastNameForLoad(gen);
                        			}
                        			customer.c_first = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(8, 16, gen));
                        			customer.c_credit_lim = 50000;

                        			customer.c_balance = -10;
                        			customer.c_ytd_payment = 10;
                        			customer.c_payment_cnt = 1;
                        			customer.c_delivery_cnt = 0;

                        			customer.c_street_1 = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 20, gen));
                        			customer.c_street_2 = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 20, gen));
                        			customer.c_city = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 20, gen));
                        			customer.c_state = TPCCUtilOracle.randomStr(3).toUpperCase();
                        			customer.c_zip = TPCCUtilOracle.randomNStr(4) + "11111";

                        			customer.c_phone = TPCCUtilOracle.randomNStr(16);

                        			customer.c_since = sysdate;
                        			customer.c_middle = "OE";
                        			customer.c_data = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(300, 500, gen));

                        			history.h_c_id = c;
                        			history.h_c_d_id = d;
                        			history.h_c_w_id = w;
                        			history.h_d_id = d;
                        			history.h_w_id = w;
                        			history.h_date = sysdate;
                        			history.h_amount = 10;
                        			history.h_data = TPCCUtilOracle.randomStr(TPCCUtilOracle.randomNumber(10, 24, gen));

                        			k = k + 2;
                        			if (outputFiles == false)
                        			{
                     				
                        				String ss2;
                        				ss2="insert into customer values ("+customer.c_id+","+customer.c_d_id+","+customer.c_w_id+",'"+ customer.c_first+"','"+customer.c_middle+"','"+customer.c_last+"','"+customer.c_street_1+"','"+customer.c_street_2+"','"+customer.c_city+"','"+customer.c_state+"','"+customer.c_zip+"','"+customer.c_phone+"','"+customer.c_since+"','"+customer.c_credit+"',"+customer.c_credit_lim+","+customer.c_discount+","+customer.c_balance+","+customer.c_ytd_payment+","+customer.c_payment_cnt+","+customer.c_delivery_cnt+",'"+customer.c_data+"')";
                        				s1.executeUpdate(ss2);
                                                
					
                        				ss2="insert into history values ("+history.h_c_id+","+history.h_c_d_id+","+history.h_c_w_id+","+ history.h_d_id+","+history.h_w_id+",'"+history.h_date+"',"+history.h_amount+",'"+history.h_data+"')";
                        				s1.executeUpdate(ss2);

                        				if ((k % configCommitCount) == 0) 
                        				{
                        					long tmpTime = new java.util.Date().getTime();
                        					String etStr = "  Elasped Time(ms): "
											+ ((tmpTime - lastTimeMS) / 1000.000)
											+ "                    ";
			
                        					lastTimeMS = tmpTime;

                        					transCommit();
                        				}
                        			}
                        			else 
                        			{
                        				String str = "";
                        				str = str + customer.c_id + ",";
                        				str = str + customer.c_d_id + ",";
                        				str = str + customer.c_w_id + ",";
                        				str = str + customer.c_discount + ",";
                        				str = str + customer.c_credit + ",";
                        				str = str + customer.c_last + ",";
                        				str = str + customer.c_first + ",";
                        				str = str + customer.c_credit_lim + ",";
                        				str = str + customer.c_balance + ",";
                        				str = str + customer.c_ytd_payment + ",";
                        				str = str + customer.c_payment_cnt + ",";
                        				str = str + customer.c_delivery_cnt + ",";
                        				str = str + customer.c_street_1 + ",";
                        				str = str + customer.c_street_2 + ",";
                        				str = str + customer.c_city + ",";
                        				str = str + customer.c_state + ",";
                        				str = str + customer.c_zip + ",";
                        				str = str + customer.c_phone;
                        				out.println(str);

                        				str = "";
                        				str = str + history.h_c_id + ",";
                        				str = str + history.h_c_d_id + ",";
                        				str = str + history.h_c_w_id + ",";
                        				str = str + history.h_d_id + ",";
                        				str = str + history.h_w_id + ",";
                        				str = str + history.h_date + ",";
                        				str = str + history.h_amount + ",";
                        				str = str + history.h_data;
                        				outHist.println(str);
                     			
                        				if ((k % configCommitCount) == 0)
                        				{
                        					long tmpTime = new java.util.Date().getTime();
                        					String etStr = "  Elasped Time(ms): "
                     						+ ((tmpTime - lastTimeMS) / 1000.000)
                     						+ "                    ";
                     				
                        					lastTimeMS = tmpTime;

                        				}
                        			}

                        		}

                        	} 

                        }

			long tmpTime = new java.util.Date().getTime();
			String etStr = "  Elasped Time(ms): "
					+ ((tmpTime - lastTimeMS) / 1000.000)
					+ "                    ";
			
			lastTimeMS = tmpTime;
			transCommit();
			now = new java.util.Date();
			if (outputFiles == true) {
				outHist.close();
			}
			

		}
		catch (SQLException se)
		{
			
            se.printStackTrace();
			transRollback();
			if (outputFiles == true) {
				outHist.close();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			transRollback();
			if (outputFiles == true) 
			{
				outHist.close();
			}
		}

		return (k);

	}

	protected int loadOrder(int whseKount, int distWhseKount, int custDistKount)
	{

		int k = 0;
		int t = 0;
		PrintWriter outLine = null;
		PrintWriter outNewOrder = null;

		try 
		{
						Class.forName(TPCCSampler.DRIVER_CLASS).newInstance();
						final String dburl=TPCCSampler.DBURL+TPCCSampler.DB_NAME;
						this.conn = DriverManager.getConnection(dburl,TPCCSampler.USERNAME,TPCCSampler.PASSWORD);
                        this.conn.setAutoCommit(false);
                        Statement s1=null;
                        s1=this.conn.createStatement();
                        

                        if (outputFiles == true) 
                        {
                        	out = new PrintWriter(new FileOutputStream(fileLocation+ "order.csv"));
                        	
                        	outLine = new PrintWriter(new FileOutputStream(fileLocation+ "order-line.csv"));
                        	
                        	outNewOrder = new PrintWriter(new FileOutputStream(fileLocation+ "new-order.csv"));
                        	
                        }

                        now = new java.util.Date();
                        OorderOracle oorder = new OorderOracle();
                        NewOrder new_order = new NewOrder();
                        OrderLineOracle order_line = new OrderLineOracle();
                        

                        t = (whseKount * distWhseKount * custDistKount);
                        t = (t * 11) + (t / 3);
                        

                        for (int w = 1; w <= whseKount; w++) 
                        {

                        	for (int d = 1; d <= distWhseKount; d++) 
                        	{
                        		
                        		int[] c_ids = new int[custDistKount];
                        		for (int i = 0; i < custDistKount; ++i)
                        		{
                        			c_ids[i] = i + 1;
                        		}
                        		
                        		for (int i = 0; i < c_ids.length - 1; ++i)
                        		{
                        			int remaining = c_ids.length - i - 1;
                        			int swapIndex = gen.nextInt(remaining) + i + 1;
                        			assert i < swapIndex;
                        			int temp = c_ids[swapIndex];
                        			c_ids[swapIndex] = c_ids[i];
                        			c_ids[i] = temp;
                        		}

                        		for (int c = 1; c <= custDistKount; c++)
                        		{

                        			oorder.o_id = c;
                        			oorder.o_w_id = w;
                        			oorder.o_d_id = d;
                        			oorder.o_c_id = c_ids[c - 1];
                        			
                        			if (oorder.o_id < FIRST_UNPROCESSED_O_ID) 
                        			{
                        				oorder.o_carrier_id = TPCCUtilOracle.randomNumber(1, 10,gen);
                                                        
                        			}
                        			else
                        			{
                        				oorder.o_carrier_id = null;
                        			}
                        			oorder.o_ol_cnt = TPCCUtilOracle.randomNumber(5, 15, gen);
                        			oorder.o_all_local = 1;
                        			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                        			String sysdate2=dateFormat.format(new java.util.Date());
                        			
                                    //Timestamp sysdate2 = new java.sql.Timestamp(System.currentTimeMillis());
                                    oorder.o_entry_d = sysdate2;

                                    k++;
                                    if (outputFiles == false) 
                                    {
                                    	
                                        String ss2;
                                        if (oorder.o_id < FIRST_UNPROCESSED_O_ID)
                                        	ss2="insert into orders values ("+oorder.o_id+","+oorder.o_w_id+","+oorder.o_d_id+","+ oorder.o_c_id+","+oorder.o_carrier_id+","+oorder.o_ol_cnt+","+oorder.o_all_local+",'"+oorder.o_entry_d+"')";
                                        else
                                            ss2="insert into orders(o_id,o_w_id,o_d_id,o_c_id,o_ol_cnt,o_all_local,o_entry_d) values ("+oorder.o_id+","+oorder.o_w_id+","+oorder.o_d_id+","+ oorder.o_c_id+","+oorder.o_ol_cnt+","+oorder.o_all_local+",'"+oorder.o_entry_d+"')";
                                              
                                        s1.executeUpdate(ss2);

                                                        
                                    }
                                    else 
                                    {
                                    	String str = "";
                                    	str = str + oorder.o_id + ",";
                                    	str = str + oorder.o_w_id + ",";
                                    	str = str + oorder.o_d_id + ",";
                                    	str = str + oorder.o_c_id + ",";
                                    	str = str + oorder.o_carrier_id + ",";
                                    	str = str + oorder.o_ol_cnt + ",";
                                    	str = str + oorder.o_all_local + ",";
                                    	//Timestamp entry_d = new java.sql.Timestamp(
                                    	//oorder.o_entry_d);
                                    	str = str + oorder.o_entry_d;
                                    	out.println(str);
                                    }

                                   
						if (c >= FIRST_UNPROCESSED_O_ID) 
						{
                            System.out.println("enterd");
							new_order.no_w_id = w;
							new_order.no_d_id = d;
							new_order.no_o_id = c;

							k++;
							if (outputFiles == false) 
							{
								
								String ss2;
                                ss2="insert into new_order values ("+new_order.no_w_id+","+new_order.no_d_id+","+new_order.no_o_id+")";
                                s1.executeUpdate(ss2);
                                                                
							} 
							else 
							{
								String str = "";
								str = str + new_order.no_w_id + ",";
								str = str + new_order.no_d_id + ",";
								str = str + new_order.no_o_id;
								outNewOrder.println(str);
							}

						} 
                        System.out.println(oorder.o_ol_cnt);
						for (int l = 1; l <= oorder.o_ol_cnt; l++) 
						{
							order_line.ol_w_id = w;
							order_line.ol_d_id = d;
							order_line.ol_o_id = c;
							order_line.ol_number = l; 
							order_line.ol_i_id = TPCCUtilOracle.randomNumber(1,100000, gen);
							if (order_line.ol_o_id < FIRST_UNPROCESSED_O_ID) 
							{
								order_line.ol_delivery_d = oorder.o_entry_d;
								order_line.ol_amount = 0;
							}
							else 
							{
								order_line.ol_delivery_d = null;
								order_line.ol_amount = (float) (TPCCUtilOracle.randomNumber(1, 999999, gen) / 100.0);
							}

							order_line.ol_supply_w_id = order_line.ol_w_id;
							order_line.ol_quantity = 5;
							order_line.ol_dist_info = TPCCUtilOracle.randomStr(24);

							k++;
							if (outputFiles == false)
							{

								
                                System.out.println("enter orderline\n");
                                String ss2;
                                if (order_line.ol_o_id < FIRST_UNPROCESSED_O_ID)
                                	ss2="insert into order_line values ("+order_line.ol_w_id+","+order_line.ol_d_id+","+order_line.ol_o_id+","+ order_line.ol_number+","+order_line.ol_i_id+",'"+order_line.ol_delivery_d+"',"+order_line.ol_amount+","+order_line.ol_supply_w_id+","+order_line.ol_quantity+",'"+order_line.ol_dist_info+"')";
                                else
                                	ss2="insert into order_line(ol_w_id,ol_d_id,ol_o_id,ol_number,ol_i_id,ol_amount,ol_supply_w_id,ol_quantity,ol_dist_info) values ("+order_line.ol_w_id+","+order_line.ol_d_id+","+order_line.ol_o_id+","+ order_line.ol_number+","+order_line.ol_i_id+","+order_line.ol_amount+","+order_line.ol_supply_w_id+","+order_line.ol_quantity+",'"+order_line.ol_dist_info+"')";
                                s1.executeUpdate(ss2);
                                                                
                                                                
							} 
							else
							{
								String str = "";
								str = str + order_line.ol_w_id + ",";
								str = str + order_line.ol_d_id + ",";
								str = str + order_line.ol_o_id + ",";
								str = str + order_line.ol_number + ",";
								str = str + order_line.ol_i_id + ",";
								str = str + order_line.ol_amount + ",";
								str = str + order_line.ol_supply_w_id + ",";
								str = str + order_line.ol_quantity + ",";
								str = str + order_line.ol_dist_info;
								outLine.println(str);
							}

							if ((k % configCommitCount) == 0) 
							{
								long tmpTime = new java.util.Date().getTime();
								String etStr = "  Elasped Time(ms): "
										+ ((tmpTime - lastTimeMS) / 1000.000)
										+ "                    ";
			
								lastTimeMS = tmpTime;
								if (outputFiles == false) 
								{		
									transCommit();
								}
							}

						} 

					}

				}

			} 
			
			if (outputFiles == false)
			{
		
			} else 
			{
			    outLine.close();
			    outNewOrder.close();
			}
			transCommit();
			now = new java.util.Date();
			

		}
		catch(SQLException se)
        {
                    se.printStackTrace();
        }
        catch (Exception e)
        {
			e.printStackTrace();
			transRollback();
			if (outputFiles == true) 
			{
				outLine.close();
				outNewOrder.close();
			}
                        
		}

		return (k);

	} 
	public static final class NotImplementedException extends UnsupportedOperationException {

        private static final long serialVersionUID = 1958656852398867984L;
	}

	/**
	 * method which first truncates all the tables and then calls the corresponding loading method
	 * for each table
	 * @throws SQLException
	 */
	//@Override
	public void load() throws SQLException
	{

		if (outputFiles == false) 
		{
			// Clearout the tables
			truncateTable(TPCCConstants.TABLENAME_ITEM);
			truncateTable(TPCCConstants.TABLENAME_WAREHOUSE);
			truncateTable(TPCCConstants.TABLENAME_STOCK);
			truncateTable(TPCCConstants.TABLENAME_DISTRICT);
			truncateTable(TPCCConstants.TABLENAME_CUSTOMER);
			truncateTable(TPCCConstants.TABLENAME_HISTORY);
			truncateTable("orders");
			truncateTable(TPCCConstants.TABLENAME_ORDERLINE);
			truncateTable(TPCCConstants.TABLENAME_NEWORDER);
		}

		
		gen = new Random(System.currentTimeMillis());

		
		startDate = new java.util.Date();
		

		long startTimeMS = new java.util.Date().getTime();
		lastTimeMS = startTimeMS;

		long totalRows = loadWhse(numWarehouses);
		totalRows += loadItem(configItemCount);
		totalRows += loadStock(numWarehouses, configItemCount);
		totalRows += loadDist(numWarehouses, configDistPerWhse);
		totalRows += loadCust(numWarehouses, configDistPerWhse,configCustPerDist);
		totalRows += loadOrder(numWarehouses, configDistPerWhse,configCustPerDist);

		long runTimeMS = (new java.util.Date().getTime()) + 1 - startTimeMS;
		endDate = new java.util.Date();
	
	}
}
