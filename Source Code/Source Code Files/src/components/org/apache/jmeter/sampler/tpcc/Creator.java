package org.apache.jmeter.sampler.tpcc;

import java.sql.*;
import java.util.*;

import org.apache.jmeter.sampler.TPCCSampler;
import org.apache.jmeter.sampler.gui.*;
/**
 * Contains the code for creating the database schema along with the procedures when testing with MySQL database
 * @author naman
 *
 */

public class Creator 
{
   static  String JDBC_DRIVER = "";		//JDBC Driver class
   static String DB_URL = "";			//database URL
   static  String USER = "";			//database username
   static  String PASS = "";			//databse password
   
   public static void main(String[] args) throws SQLException
   {
	   
   }//end main
   public static void create() throws SQLException
   {
//	   System.out.println(TPCCSampler.PASSWORD);
//	   System.out.println(TPCCSampler.NUM_WHSE);
	   JDBC_DRIVER=JDBC_DRIVER+TPCCSampler.DRIVER_CLASS;	
	   DB_URL=DB_URL+TPCCSampler.DBURL;
	   USER=USER+TPCCSampler.USERNAME;
	   PASS=PASS+TPCCSampler.PASSWORD;
	   
	   Connection conn1 = null;
	   Connection conn=null;
	   Statement stmt1 = null;
	   Statement stmt=null;
	   try
	   {
	      //STEP 2: Register JDBC driver
	      Class.forName(JDBC_DRIVER);

	      //STEP 3: Open a connection
	      
	      System.out.println("Connecting to database...");
	      conn1 = DriverManager.getConnection(DB_URL, USER, PASS);

	      //STEP 4: Execute a query
	      
	      stmt1 = conn1.createStatement();	      
	      String sql = "CREATE DATABASE "+TPCCSampler.DB_NAME;
	      stmt1.executeUpdate(sql);
	      System.out.println("Database created successfully...");
	      conn1.close();
	      DB_URL+=TPCCSampler.DB_NAME;
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);
	      stmt = conn.createStatement();
      
	      //SQL Query for creating Customer table
	      String customer = "CREATE TABLE customer ("+
	    		  			"c_id INT(5) NULL,"+
	    		  			"c_d_id INT(2) NULL,"+
	    		  			"c_w_id INT(4) NULL,"+
	    		  			"c_first VARCHAR(16) BINARY NULL,"+
	    		  			"c_middle CHAR(2) BINARY NULL,"+
	    		  			"c_last VARCHAR(16) BINARY NULL,"+
	    		  			"c_street_1 VARCHAR(20) BINARY NULL,"+
	    		  			"c_street_2 VARCHAR(20) BINARY NULL,"+
	    		  			"c_city VARCHAR(20) BINARY NULL,"+
	    		  			"c_state CHAR(2) BINARY NULL,"+
	    		  			"c_zip CHAR(9) BINARY NULL,"+
	    		  			"c_phone CHAR(16) BINARY NULL,"+
	    		  			"c_since DATETIME NULL,"+
	    		  			"c_credit CHAR(2) BINARY NULL,"+
	    		  			"c_credit_lim DECIMAL(12, 2) NULL,"+
	    		  			"c_discount DECIMAL(4, 4) NULL,"+
	    		  			"c_balance DECIMAL(12, 2) NULL,"+
	    		  			"c_ytd_payment DECIMAL(12, 2) NULL,"+
	    		  			"c_payment_cn INT(8) NULL,"+
	    		  			"c_delivery_cnt INT(8) NULL,"+
	    		  			"c_data VARCHAR(500) BINARY NULL,"+
	    		  			"PRIMARY KEY (c_w_id,c_d_id,c_id),"+
	    		  			"KEY c_w_id (c_w_id,c_d_id,c_last(16),c_first(16))"+
	    		  			")";
	      stmt.executeUpdate(customer);
	      
	      //SQL Query for creating District table
	      String district=	"CREATE TABLE district ("+
	    		  			"d_id INT(2) NULL,"+
	    		  			"d_w_id INT(4) NULL,"+
	    		  			"d_ytd DECIMAL(12, 2) NULL,"+
	    		  			"d_tax DECIMAL(4, 4) NULL,"+
	    		  			"d_next_o_id INT NULL,"+
	    		  			"d_name VARCHAR(10) BINARY NULL,"+
	    		  			"d_street_1 VARCHAR(20) BINARY NULL,"+
	    		  			"d_street_2 VARCHAR(20) BINARY NULL,"+
	    		  			"d_city VARCHAR(20) BINARY NULL,"+
	    		  			"d_state CHAR(2) BINARY NULL,"+
	    		  			"d_zip CHAR(9) BINARY NULL,"+
	    		  			"PRIMARY KEY (d_w_id,d_id)"+
	    		  			")";
	      stmt.executeUpdate(district);
	      
	      //SQl query for creating History table
	      String history=	"CREATE TABLE history ("+
	    		  			"h_c_id INT NULL,"+
	    		  			"h_c_d_id INT NULL,"+
	    		  			"h_c_w_id INT NULL,"+
	    		  			"h_d_id INT NULL,"+
	    		  			"h_w_id INT NULL,"+
	    		  			"h_date DATETIME NULL,"+
	    		  			"h_amount DECIMAL(6, 2) NULL,"+
	    		  			"h_data VARCHAR(24) BINARY NULL"+
	    		  			")";
	      stmt.executeUpdate(history);
	      
	      //SQL Query for creating Item table
	      String item=	"CREATE TABLE item ("+
	    		  		"i_id INT(6) NULL,"+
	    		  		"i_im_id INT NULL,"+
	    		  		"i_name VARCHAR(24) BINARY NULL,"+
	    		  		"i_price DECIMAL(5, 2) NULL,"+
	    		  		"i_data VARCHAR(50) BINARY NULL,"+
	    		  		"PRIMARY KEY (i_id)"+
	    		  		")";
	      stmt.executeUpdate(item);
	      
	      //SQL Query for creating New_Order table
	      String newOrder=	"CREATE TABLE new_order ("+
	    		  			"no_w_id INT NOT NULL,"+
	    		  			"no_d_id INT NOT NULL,"+
	    		  			"no_o_id INT NOT NULL,"+
	    		  			"PRIMARY KEY (no_w_id, no_d_id, no_o_id)"+
	    		  			")";
	      stmt.executeUpdate(newOrder);
	      
	      //SQL Query for creating Orders table
	      String orders=	"CREATE TABLE orders ("+
	    		  			"o_id INT NULL,"+
	    		  			"o_w_id INT NULL,"+
	    		  			"o_d_id INT NULL,"+
	    		  			"o_c_id INT NULL,"+
	    		  			"o_carrier_id INT NULL,"+
	    		  			"o_ol_cnt INT NULL,"+
	    		  			"o_all_local INT NULL,"+
	    		  			"o_entry_d DATETIME NULL,"+
	    		  			"PRIMARY KEY (o_w_id,o_d_id,o_id),"+
	    		  			"KEY o_w_id (o_w_id,o_d_id,o_c_id,o_id)"+
	    		  			")";
	      stmt.executeUpdate(orders);
	      
	      //SQL Query for creating Order_Line table
	      String order_line=	"CREATE TABLE order_line ("+
	    		  				"ol_w_id INT NOT NULL,"+
	    		  				"ol_d_id INT NOT NULL,"+
	    		  				"ol_o_id iNT NOT NULL,"+
	    		  				"ol_number INT NOT NULL,"+
	    		  				"ol_i_id INT NULL,"+
	    		  				"ol_delivery_d DATETIME NULL,"+
	    		  				"ol_amount INT NULL,"+
	    		  				"ol_supply_w_id INT NULL,"+
	    		  				"ol_quantity INT NULL,"+
	    		  				"ol_dist_info CHAR(24) BINARY NULL,"+
	    		  				"PRIMARY KEY (ol_w_id,ol_d_id,ol_o_id,ol_number)"+
	    		  				")";
	      stmt.executeUpdate(order_line);
	      
	      //SQL Query for creating Stock table
	      String stock=	"CREATE TABLE stock ("+
	    		  		"s_i_id INT(6) NULL,"+
	    		  		"s_w_id INT(4) NULL,"+
	    		  		"s_quantity INT(6) NULL,"+
	    		  		"s_dist_01 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_02 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_03 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_04 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_05 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_06 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_07 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_08 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_09 CHAR(24) BINARY NULL,"+
	    		  		"s_dist_10 CHAR(24) BINARY NULL,"+
	    		  		"s_ytd BIGINT(10) NULL,"+
	    		  		"s_order_cnt INT(6) NULL,"+
	    		  		"s_remote_cnt INT(6) NULL,"+
	    		  		"s_data VARCHAR(50) BINARY NULL,"+
	    		  		"PRIMARY KEY (s_w_id,s_i_id)"+
	    		  		")";
	      stmt.executeUpdate(stock);
	      
	      //SQL Query for creating Warehouse table
	      String warehouse=	"CREATE TABLE warehouse ("+
	    		  			"w_id INT(4) NULL,"+
	    		  			"w_ytd DECIMAL(12, 2) NULL,"+
	    		  			"w_tax DECIMAL(4, 4) NULL,"+
	    		  			"w_name VARCHAR(10) BINARY NULL,"+
	    		  			"w_street_1 VARCHAR(20) BINARY NULL,"+
	    		  			"w_street_2 VARCHAR(20) BINARY NULL,"+
	    		  			"w_city VARCHAR(20) BINARY NULL,"+
	    		  			"w_state CHAR(2) BINARY NULL,"+
	    		  			"w_zip CHAR(9) BINARY NULL,"+
	    		  			"PRIMARY KEY (w_id)"+
	    		  			")";
	      stmt.executeUpdate(warehouse);
 
	      System.out.println("Created table in given database...");
 	
	      //SQL Queries to be executed for creating Stock Level procedure
	      System.out.println("1");String 	proc5="";
	      			proc5+="CREATE PROCEDURE `SLEV` (" ;
 	 		 		proc5+="st_w_id INTEGER," ;
 	 		 		proc5+="st_d_id INTEGER," ;
 	 		 		proc5+="threshold INTEGER" ;
 	 		 		proc5+=")\n"; 
 	 		 		proc5+="BEGIN\n";
 	 		 		proc5+="DECLARE st_o_id INTEGER;\n";
 	 		 		proc5+="DECLARE stock_count INTEGER;\n";
 	 		 		proc5+="DECLARE `Constraint Violation` CONDITION FOR SQLSTATE '23000';\n";
 	 		 		proc5+="DECLARE EXIT HANDLER FOR `Constraint Violation` ROLLBACK;\n";
 	 		 		proc5+="DECLARE EXIT HANDLER FOR NOT FOUND ROLLBACK;\n";
 	 		 		proc5+="SELECT d_next_o_id INTO st_o_id ";
 	 		 		proc5+="FROM district " ;
 	 		 		proc5+="WHERE d_w_id=st_w_id AND d_id=st_d_id;\n" ;
 	 		 		proc5+="SELECT COUNT(DISTINCT (s_i_id)) INTO stock_count ";
 	 		 		proc5+="FROM order_line, stock ";
 	 		 		proc5+="WHERE ol_w_id = st_w_id AND ";
 	 		 		proc5+="ol_d_id = st_d_id AND (ol_o_id < st_o_id) AND ";
 	 		 		proc5+="ol_o_id >= (st_o_id - 20) AND s_w_id = st_w_id AND ";
 	 		 		proc5+="s_i_id = ol_i_id AND s_quantity < threshold;\n";
 	 		 		proc5+="END\n";
 	 		 		
 	 		 		stmt.executeUpdate(proc5);
 	 		 		
 	 		//SQL Queries to be executed to create procedure NEW ORDER	 		
 	 		String 	proc1="";
 	 		 		proc1+="CREATE PROCEDURE `NEWORD` (";
 	 				proc1+="no_w_id INTEGER,";
 	 				proc1+="no_max_w_id INTEGER,";
 	 				proc1+= "no_d_id INTEGER,";
 	 				proc1+= "no_c_id INTEGER,";
 	 				proc1+= "no_o_ol_cnt INTEGER,";
 	 				proc1+= "OUT no_c_discount DECIMAL(4,4),";
 	 				proc1+="OUT no_c_last VARCHAR(16),";
 	 				proc1+= "OUT no_c_credit VARCHAR(2),";
 	 				proc1+= "OUT no_d_tax DECIMAL(4,4),";
 	 				proc1+="OUT no_w_tax DECIMAL(4,4),";
 	 				proc1+="INOUT no_d_next_o_id INTEGER,";
 	 				proc1+="IN timestamp DATE";
 	 				proc1+= ")\n";
 	 				proc1+="BEGIN\n";
 	 				proc1+="DECLARE no_ol_supply_w_id INTEGER;\n";
 	 				proc1+="DECLARE no_ol_i_id INTEGER;\n";
 	 				proc1+="DECLARE no_ol_quantity INTEGER;\n";
 	 				proc1+="DECLARE no_o_all_local INTEGER;\n";
 	 				proc1+="DECLARE o_id INTEGER;\n";
 	 				proc1+="DECLARE no_i_name VARCHAR(24);\n";
 	 				proc1+="DECLARE no_i_price DECIMAL(5,2);\n";
 	 				proc1+="DECLARE no_i_data VARCHAR(50);\n";
 	 				proc1+="DECLARE no_s_quantity DECIMAL(6);\n";
 	 				proc1+="DECLARE no_ol_amount DECIMAL(6,2);\n";
 	 				proc1+="DECLARE no_s_dist_01 CHAR(24);\n";
 	 				proc1+="DECLARE no_s_dist_02 CHAR(24);\n";
 	 				proc1+="DECLARE no_s_dist_03 CHAR(24);\n";
 	 				proc1+="DECLARE no_s_dist_04 CHAR(24);\n";
 	 				proc1+="DECLARE no_s_dist_05 CHAR(24);\n";
 	 				proc1+="DECLARE no_s_dist_06 CHAR(24);\n";
 	 				proc1+= "DECLARE no_s_dist_07 CHAR(24);\n";
 	 				proc1+="DECLARE no_s_dist_08 CHAR(24);\n";
 	 				proc1+= "DECLARE no_s_dist_09 CHAR(24);\n";
 	 				proc1+= "DECLARE no_s_dist_10 CHAR(24);\n";
 	 				proc1+= "DECLARE no_ol_dist_info CHAR(24);\n";
 	 				proc1+= "DECLARE no_s_data VARCHAR(50);\n";
 	 				proc1+= "DECLARE x INTEGER;\n";
 	 				proc1+= "DECLARE rbk INTEGER;\n";
 	 				proc1+= "DECLARE loop_counter INT;\n";
 	 				proc1+="DECLARE `Constraint Violation` CONDITION FOR SQLSTATE '23000';\n";
 	 				proc1+= "DECLARE EXIT HANDLER FOR `Constraint Violation` ROLLBACK;\n";
 	 				proc1+= "DECLARE EXIT HANDLER FOR NOT FOUND ROLLBACK;\n";
 	 				proc1+= "SET no_o_all_local = 0;\n";
 	 				proc1+= "SELECT c_discount, c_last, c_credit, w_tax ";
 	 				proc1+= "INTO no_c_discount, no_c_last, no_c_credit, no_w_tax ";
 	 				proc1+= "FROM customer, warehouse ";
 	 				proc1+= "WHERE warehouse.w_id = no_w_id AND customer.c_w_id = no_w_id AND ";
 	 				proc1+= "customer.c_d_id = no_d_id AND customer.c_id = no_c_id;\n";
 	 				proc1+= "START TRANSACTION;\n";
 	 				proc1+= "SELECT d_next_o_id, d_tax INTO no_d_next_o_id, no_d_tax ";
 	 				proc1+= "FROM district ";
 	 				proc1+= "WHERE d_id = no_d_id AND d_w_id = no_w_id FOR UPDATE;\n";
 	 				proc1+= "UPDATE district SET d_next_o_id = d_next_o_id + 1 WHERE d_id = no_d_id AND d_w_id = no_w_id;\n";
 	 				proc1+= "SET o_id = no_d_next_o_id;\n";
 	 				proc1+= "INSERT INTO orders (o_id, o_d_id, o_w_id, o_c_id, o_entry_d, o_ol_cnt, o_all_local) VALUES (o_id, no_d_id, no_w_id,"; 			
 	 				proc1+=	"no_c_id, timestamp, no_o_ol_cnt, no_o_all_local);\n";
 	 				proc1+="INSERT INTO new_order (no_o_id, no_d_id, no_w_id) VALUES (o_id, no_d_id, no_w_id);\n";
 	 				proc1+="SET rbk = FLOOR(1 + (RAND() * 99));\n";
 	 				proc1+="SET loop_counter = 1;\n";
 	 				proc1+= "WHILE loop_counter <= no_o_ol_cnt DO\n";
 	 				proc1+="IF ((loop_counter = no_o_ol_cnt) AND (rbk = 1))\n";
 	 				proc1+="THEN\n";
 	 				proc1+= "SET no_ol_i_id = 100001;\n";
 	 				proc1+="ELSE\n";
 	 				proc1+= "SET no_ol_i_id = FLOOR(1 + (RAND() * 100000));\n";
 	 				proc1+="END IF;\n";
 	 				proc1+= "SET x = FLOOR(1 + (RAND() * 100));\n";
 	 				proc1+= "IF ( x > 1 )\n";
 	 				proc1+= "THEN\n";
 	 				proc1+="SET no_ol_supply_w_id = no_w_id;\n";
 	 				proc1+= "ELSE\n";
 	 				proc1+="SET no_ol_supply_w_id = no_w_id;\n";
 	 				proc1+= "SET no_o_all_local = 0;\n";
 	 				proc1+="WHILE ((no_ol_supply_w_id = no_w_id) AND (no_max_w_id != 1)) DO\n";
 	 				proc1+= "SET no_ol_supply_w_id = FLOOR(1 + (RAND() * no_max_w_id));\n";
 	 				proc1+= "END WHILE;\n";
 	 				proc1+="END IF;\n";
 	 				proc1+="SET no_ol_quantity = FLOOR(1 + (RAND() * 10));\n";
 	 				proc1+= "SELECT i_price, i_name, i_data INTO no_i_price, no_i_name, no_i_data ";
 	 				proc1+="FROM item WHERE i_id = no_ol_i_id;\n";
 	 				proc1+="SELECT s_quantity, s_data, s_dist_01, s_dist_02, s_dist_03, s_dist_04, s_dist_05, s_dist_06, s_dist_07, s_dist_08,"; 			proc1+="s_dist_09, s_dist_10 ";
 	 				proc1+="INTO no_s_quantity, no_s_data, no_s_dist_01, no_s_dist_02, no_s_dist_03, no_s_dist_04, no_s_dist_05, no_s_dist_06,"; 			proc1+="no_s_dist_07, no_s_dist_08, no_s_dist_09, no_s_dist_10 ";
 	 				proc1+= "FROM stock WHERE s_i_id = no_ol_i_id AND s_w_id = no_ol_supply_w_id;\n";
 	 				proc1+= "IF ( no_s_quantity > no_ol_quantity )\n";
 	 				proc1+="THEN\n";
 	 				proc1+= "SET no_s_quantity = ( no_s_quantity - no_ol_quantity );\n";
 	 				proc1+= "ELSE\n";
 	 				proc1+= "SET no_s_quantity = ( no_s_quantity - no_ol_quantity + 91 );\n";
 	 				proc1+= "END IF;\n";
 	 				proc1+= "UPDATE stock SET s_quantity = no_s_quantity ";
 	 				proc1+="WHERE s_i_id = no_ol_i_id ";
 	 				proc1+= "AND s_w_id = no_ol_supply_w_id;\n";
 	 				proc1+= "SET no_ol_amount = (  no_ol_quantity * no_i_price * ( 1 + no_w_tax + no_d_tax ) * ( 1 - no_c_discount ) );\n";
 	 				proc1+="CASE no_d_id\n";
 	 				proc1+= "WHEN 1 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_01;\n";
 	 				proc1+= "WHEN 2 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_02;\n";
 	 				proc1+= "WHEN 3 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_03;\n";
 	 				proc1+= "WHEN 4 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_04;\n";
 	 				proc1+=	"WHEN 5 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_05;\n";
 	 				proc1+= "WHEN 6 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_06;\n";
 	 				proc1+= "WHEN 7 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_07;\n";
 	 				proc1+="WHEN 8 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_08;\n";
 	 				proc1+= "WHEN 9 THEN\n";
 	 				proc1+= "SET no_ol_dist_info = no_s_dist_09;\n";
 	 				proc1+= "WHEN 10 THEN\n";
 	 				proc1+="SET no_ol_dist_info = no_s_dist_10;\n";
 	 				proc1+="END CASE;\n";
 	 				proc1+= "INSERT INTO order_line (ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id, ol_supply_w_id, ol_quantity, ol_amount,"; 		proc1+=	"ol_dist_info) ";
 	 				proc1+= "VALUES (o_id, no_d_id, no_w_id, loop_counter, no_ol_i_id, no_ol_supply_w_id, no_ol_quantity, no_ol_amount,"; 
 	 				proc1+="no_ol_dist_info);\n";
 	 				proc1+= "set loop_counter = loop_counter + 1;\n";
 	 				proc1+= "END WHILE;\n";
 	 				proc1+="COMMIT;\n";
 	 				proc1+= "END\n";
 	 				
 	 		stmt.executeUpdate(proc1);
 	 	
 	 		//SQL Queries to be executed to create procedure Delivery
 	 	String 	proc2="";
 	 			proc2+="CREATE PROCEDURE `DELIVERY`(";
 	 			proc2+="d_w_id INTEGER,";
 	 			proc2+="d_o_carrier_id INTEGER,";
 	 			proc2+="IN timestamp DATE";
 	 			proc2+=")\n";
 	 			proc2+= "BEGIN\n";
 	 			proc2+="DECLARE d_no_o_id INTEGER;\n";
 	 			proc2+="DECLARE current_rowid INTEGER;\n";
 	 			proc2+="DECLARE d_d_id INTEGER;\n";
 	 			proc2+="DECLARE d_c_id INTEGER;\n";
 	 			proc2+="DECLARE d_ol_total INTEGER;\n";
 	 			proc2+="DECLARE deliv_data VARCHAR(100);\n";
 	 			proc2+= "DECLARE loop_counter INT;\n";
 	 			proc2+="DECLARE `Constraint Violation` CONDITION FOR SQLSTATE '23000';\n";
 	 			proc2+= "DECLARE EXIT HANDLER FOR `Constraint Violation` ROLLBACK;\n";
 	 			proc2+="SET loop_counter = 1;\n";
 	 			proc2+="WHILE loop_counter <= 10 DO\n";
 	 			proc2+="SET d_d_id = loop_counter;\n";
 	 			proc2+="SELECT no_o_id INTO d_no_o_id FROM new_order WHERE no_w_id = d_w_id AND no_d_id = d_d_id LIMIT 1;\n";
 	 			proc2+="DELETE FROM new_order WHERE no_w_id = d_w_id AND no_d_id = d_d_id AND no_o_id = d_no_o_id;\n";
 	 			proc2+="SELECT o_c_id INTO d_c_id FROM orders ";
 	 			proc2+="WHERE o_id = d_no_o_id AND o_d_id = d_d_id AND ";
 	 			proc2+="o_w_id = d_w_id;\n";
 	 			proc2+= "UPDATE orders SET o_carrier_id = d_o_carrier_id ";
 	 			proc2+="WHERE o_id = d_no_o_id AND o_d_id = d_d_id AND ";
 	 			proc2+="o_w_id = d_w_id;\n";
 	 			proc2+="UPDATE order_line SET ol_delivery_d = timestamp ";
 	 			proc2+="WHERE ol_o_id = d_no_o_id AND ol_d_id = d_d_id AND ";
 	 			proc2+="ol_w_id = d_w_id;\n";
 	 			proc2+="SELECT SUM(ol_amount) INTO d_ol_total ";
 	 			proc2+="FROM order_line ";
 	 			proc2+="WHERE ol_o_id = d_no_o_id AND ol_d_id = d_d_id ";
 	 			proc2+="AND ol_w_id = d_w_id;\n";
 	 			proc2+="UPDATE customer SET c_balance = c_balance + d_ol_total ";
 	 			proc2+="WHERE c_id = d_c_id AND c_d_id = d_d_id AND ";
 	 			proc2+="c_w_id = d_w_id;\n";
 	 			proc2+="SET deliv_data = CONCAT(d_d_id,' ',d_no_o_id,' ',timestamp);\n";
 	 			proc2+= "COMMIT;\n";
 	 			proc2+="set loop_counter = loop_counter + 1;\n";
 	 			proc2+="END WHILE;\n";
 	 			proc2+="END\n";
 	 			
 	 	stmt.executeUpdate(proc2);
 			 
 		//SQL Queries to be executed for creating procedure Payment	 
 	 	String 	proc3="";
 	 			proc3+="CREATE PROCEDURE `PAYMENT` (";
 	 			proc3+="p_w_id INTEGER,";
 	 			proc3+="p_d_id INTEGER,";
 	 			proc3+="p_c_w_id INTEGER,";
 	 			proc3+="p_c_d_id INTEGER,";
 	 			proc3+="INOUT p_c_id INTEGER,";
 	 			proc3+="byname INTEGER,";
 	 			proc3+="p_h_amount DECIMAL(6,2),";
 	 			proc3+="INOUT p_c_last VARCHAR(16),";
 	 			proc3+="OUT p_w_street_1 VARCHAR(20),";
 	 			proc3+="OUT p_w_street_2 VARCHAR(20),";
 	 			proc3+="OUT p_w_city VARCHAR(20),";
 	 			proc3+="OUT p_w_state CHAR(2),";
 	 			proc3+="OUT p_w_zip CHAR(9),";
 	 			proc3+="OUT p_d_street_1 VARCHAR(20),";
 	 			proc3+="OUT p_d_street_2 VARCHAR(20),";
 	 			proc3+="OUT p_d_city VARCHAR(20),";
 	 			proc3+="OUT p_d_state CHAR(2),";
 	 			proc3+="OUT p_d_zip CHAR(9),";
 	 			proc3+="OUT p_c_first VARCHAR(16),";
 	 			proc3+= "OUT p_c_middle CHAR(2),";
 	 			proc3+="OUT p_c_street_1 VARCHAR(20),";
 	 			proc3+="OUT p_c_street_2 VARCHAR(20),";
 	 			proc3+="OUT p_c_city VARCHAR(20),";
 	 			proc3+="OUT p_c_state CHAR(2),";
 	 			proc3+="OUT p_c_zip CHAR(9),";
 	 			proc3+="OUT p_c_phone CHAR(16),";
 	 			proc3+="OUT p_c_since DATE,";
 	 			proc3+="INOUT p_c_credit CHAR(2),";
 	 			proc3+="OUT p_c_credit_lim DECIMAL(12,2),";
 	 			proc3+="OUT p_c_discount DECIMAL(4,4),";
 	 			proc3+="INOUT p_c_balance DECIMAL(12,2),";
 	 			proc3+="OUT p_c_data VARCHAR(500),";
 	 			proc3+="IN timestamp DATE";
 	 			proc3+= ")\n";
 	 			proc3+="BEGIN\n";
 	 			proc3+= "DECLARE done INT DEFAULT 0;\n";
 	 			proc3+="DECLARE namecnt INTEGER;\n";
 	 			proc3+= "DECLARE p_d_name VARCHAR(11);\n";
 	 			proc3+="DECLARE p_w_name VARCHAR(11);\n";
 	 			proc3+="DECLARE p_c_new_data VARCHAR(500);\n";
 	 			proc3+="DECLARE h_data VARCHAR(30);\n";
 	 			proc3+="DECLARE loop_counter INT;\n";
 	 			proc3+="DECLARE `Constraint Violation` CONDITION FOR SQLSTATE '23000';\n";
 	 			proc3+="DECLARE c_byname CURSOR FOR\n";
 	 			proc3+="SELECT c_first, c_middle, c_id, c_street_1, c_street_2, c_city, c_state, c_zip, c_phone, c_credit, c_credit_lim,"; 			proc3+="c_discount, c_balance, c_since ";
 	 			proc3+="FROM customer ";
 	 			proc3+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND c_last = p_c_last ";
 	 			proc3+="ORDER BY c_first;\n";
 	 			proc3+="DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;\n";
 	 			proc3+="DECLARE EXIT HANDLER FOR `Constraint Violation` ROLLBACK;\n";
 	 			proc3+="START TRANSACTION;\n";
 	 			proc3+="UPDATE warehouse SET w_ytd = w_ytd + p_h_amount ";
 	 			proc3+="WHERE w_id = p_w_id;\n";
 	 			proc3+= "SELECT w_street_1, w_street_2, w_city, w_state, w_zip, w_name ";
 	 			proc3+="INTO p_w_street_1, p_w_street_2, p_w_city, p_w_state, p_w_zip, p_w_name ";
 	 			proc3+="FROM warehouse ";
 	 			proc3+="WHERE w_id = p_w_id;\n";
 	 			proc3+="UPDATE district SET d_ytd = d_ytd + p_h_amount ";
 	 			proc3+="WHERE d_w_id = p_w_id AND d_id = p_d_id;\n";
 	 			proc3+="SELECT d_street_1, d_street_2, d_city, d_state, d_zip, d_name ";
 	 			proc3+="INTO p_d_street_1, p_d_street_2, p_d_city, p_d_state, p_d_zip, p_d_name ";
 	 			proc3+="FROM district ";
 	 			proc3+="WHERE d_w_id = p_w_id AND d_id = p_d_id;\n";
 	 			proc3+="IF (byname = 1)\n";
 	 			proc3+="THEN\n";
 	 			proc3+="SELECT count(c_id) INTO namecnt ";
 	 			proc3+="FROM customer ";
 	 			proc3+="WHERE c_last = p_c_last AND c_d_id = p_c_d_id AND c_w_id = p_c_w_id;\n";
 	 			proc3+="OPEN c_byname;\n";
 	 			proc3+="IF ( MOD (namecnt, 2) = 1 )\n";
 	 			proc3+="THEN\n";
 	 			proc3+="SET namecnt = (namecnt + 1);\n";
 	 			proc3+="END IF;\n";
 	 			proc3+="SET loop_counter = 0;\n";
 	 			proc3+="WHILE loop_counter <= (namecnt/2) DO\n";
 	 			proc3+="FETCH c_byname ";
 	 			proc3+="INTO p_c_first, p_c_middle, p_c_id, p_c_street_1, p_c_street_2, p_c_city, ";
 	 			proc3+="p_c_state, p_c_zip, p_c_phone, p_c_credit, p_c_credit_lim, p_c_discount, p_c_balance, p_c_since;\n";
 	 			proc3+="set loop_counter = loop_counter + 1;\n";
 	 			proc3+="END WHILE;\n";
 	 			proc3+="CLOSE c_byname;\n";
 	 			proc3+="ELSE\n";
 	 			proc3+="SELECT c_first, c_middle, c_last, ";
 	 			proc3+="c_street_1, c_street_2, c_city, c_state, c_zip, ";
 	 			proc3+="c_phone, c_credit, c_credit_lim, ";
 	 			proc3+="c_discount, c_balance, c_since ";
 	 			proc3+="INTO p_c_first, p_c_middle, p_c_last, ";
 	 			proc3+="p_c_street_1, p_c_street_2, p_c_city, p_c_state, p_c_zip, ";
 	 			proc3+="p_c_phone, p_c_credit, p_c_credit_lim, ";
 	 			proc3+="p_c_discount, p_c_balance, p_c_since ";
 	 			proc3+="FROM customer ";
 	 			proc3+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND c_id = p_c_id;\n";
 	 			proc3+="END IF;\n";
 	 			proc3+="SET p_c_balance = ( p_c_balance + p_h_amount );\n";
 	 			proc3+= "IF p_c_credit = 'BC'\n";
 	 			proc3+="THEN\n";
 	 			proc3+="SELECT c_data INTO p_c_data ";
 	 			proc3+="FROM customer ";
 	 			proc3+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND c_id = p_c_id;\n";
 	 			proc3+="SET h_data = CONCAT(p_w_name,' ',p_d_name);\n";
 	 			proc3+="SET p_c_new_data = CONCAT(CAST(p_c_id AS CHAR),' ',CAST(p_c_d_id AS CHAR),' ',CAST(p_c_w_id AS CHAR),' ',";
 	 			proc3+="CAST(p_d_id AS CHAR),' ',CAST(p_w_id AS CHAR),' ',CAST(FORMAT(p_h_amount,2) AS CHAR),CAST(timestamp AS CHAR),h_data);";
 	 			proc3+="SET p_c_new_data = SUBSTR(CONCAT(p_c_new_data,p_c_data),1,500-(LENGTH(p_c_new_data)));\n";
 	 			proc3+= "UPDATE customer ";
 	 			proc3+="SET c_balance = p_c_balance, c_data = p_c_new_data ";
 	 			proc3+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND ";
 	 			proc3+="c_id = p_c_id;\n";
 	 			proc3+="ELSE\n";
 	 			proc3+="UPDATE customer SET c_balance = p_c_balance ";
 	 			proc3+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND ";
 	 			proc3+="c_id = p_c_id;\n";
 	 			proc3+="END IF;\n";
 	 			proc3+="SET h_data = CONCAT(p_w_name,' ',p_d_name);\n";
 	 			proc3+="INSERT INTO history (h_c_d_id, h_c_w_id, h_c_id, h_d_id, h_w_id, h_date, h_amount, h_data) ";
 	 			proc3+="VALUES (p_c_d_id, p_c_w_id, p_c_id, p_d_id, p_w_id, timestamp, p_h_amount, h_data);\n";
 	 			proc3+="COMMIT;\n";
 	 			proc3+="END\n ";
 	 			
 	 	stmt.executeUpdate(proc3);
 			 
 		//SQL Queries for creating procedure Order Status	 
 		String 	proc4= "";
 				proc4+="CREATE PROCEDURE `OSTAT` (";
 				proc4+="os_w_id INTEGER,";
 				proc4+="os_d_id INTEGER,";
 				proc4+="INOUT os_c_id INTEGER,";
 				proc4+="byname INTEGER,";
 				proc4+="INOUT os_c_last VARCHAR(16),";
 				proc4+="OUT os_c_first VARCHAR(16),";
 				proc4+="OUT os_c_middle CHAR(2),";
 				proc4+="OUT os_c_balance DECIMAL(12,2),";
 				proc4+="OUT os_o_id INTEGER,";
 				proc4+="OUT os_entdate DATE,";
 				proc4+="OUT os_o_carrier_id INTEGER";
 				proc4+=")\n";
 	 			proc4+="BEGIN\n";
 	 			proc4+="DECLARE  os_ol_i_id INTEGER;\n";
 	 			proc4+="DECLARE  os_ol_supply_w_id INTEGER;\n";
 	 			proc4+="DECLARE  os_ol_quantity INTEGER;\n";
 	 			proc4+="DECLARE  os_ol_amount INTEGER;\n";
 	 			proc4+="DECLARE  os_ol_delivery_d DATE;\n";
 	 			proc4+="DECLARE done INT DEFAULT 0;\n";
 	 			proc4+="DECLARE namecnt INTEGER;\n";
 	 			proc4+="DECLARE i INTEGER;\n";
 	 			proc4+="DECLARE loop_counter INTEGER;\n";
 	 			proc4+="DECLARE no_order_status VARCHAR(100);\n";
 	 			proc4+="DECLARE os_ol_i_id_array VARCHAR(200);\n";
 	 			proc4+="DECLARE os_ol_supply_w_id_array VARCHAR(200);\n";
 	 			proc4+="DECLARE os_ol_quantity_array VARCHAR(200);\n";
 	 			proc4+="DECLARE os_ol_amount_array VARCHAR(200);\n";
 	 			proc4+="DECLARE os_ol_delivery_d_array VARCHAR(210);\n";
 	 			proc4+="DECLARE `Constraint Violation` CONDITION FOR SQLSTATE '23000';\n";
 	 			proc4+="DECLARE c_name CURSOR FOR\n";
 	 			proc4+="SELECT c_balance, c_first, c_middle, c_id ";
 	 			proc4+="FROM customer ";
 	 			proc4+="WHERE c_last = os_c_last AND c_d_id = os_d_id AND c_w_id = os_w_id ";
 	 			proc4+="ORDER BY c_first;\n";
 	 			proc4+="DECLARE c_line CURSOR FOR ";
 	 			proc4+="SELECT ol_i_id, ol_supply_w_id, ol_quantity, ";
 	 			proc4+="ol_amount, ol_delivery_d ";
 	 			proc4+="FROM order_line ";
 	 			proc4+="WHERE ol_o_id = os_o_id AND ol_d_id = os_d_id AND ol_w_id = os_w_id;\n";
 	 			proc4+="DECLARE EXIT HANDLER FOR `Constraint Violation` ROLLBACK;\n";
 	 			proc4+="DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;\n";
 	 			proc4+="set no_order_status = '';\n";
 	 			proc4+="set os_ol_i_id_array = 'CSV,';\n";
 	 			proc4+="set os_ol_supply_w_id_array = 'CSV,';\n";
 	 			proc4+="set os_ol_quantity_array = 'CSV,';\n";
 	 			proc4+="set os_ol_amount_array = 'CSV,';\n";
 	 			proc4+="set os_ol_delivery_d_array = 'CSV,';\n";
 	 			proc4+="IF ( byname = 1 )\n";
 	 			proc4+="THEN\n";
 	 			proc4+="SELECT count(c_id) INTO namecnt ";
 	 			proc4+="FROM customer ";
 	 			proc4+="WHERE c_last = os_c_last AND c_d_id = os_d_id AND c_w_id = os_w_id;\n";
 	 			proc4+="IF ( MOD (namecnt, 2) = 1 )\n";
 	 			proc4+="THEN\n";
 	 			proc4+="SET namecnt = (namecnt + 1);\n";
 	 			proc4+="END IF;\n";
 	 			proc4+="OPEN c_name;\n";
 	 			proc4+="SET loop_counter = 0;\n";
 	 			proc4+="WHILE loop_counter <= (namecnt/2) DO\n";
 	 			proc4+="FETCH c_name ";
 	 			proc4+="INTO os_c_balance, os_c_first, os_c_middle, os_c_id;\n";
 	 			proc4+="set loop_counter = loop_counter + 1;\n";
 	 			proc4+="END WHILE;\n";
 	 			proc4+="close c_name;\n";
 	 			proc4+="ELSE\n";
 	 			proc4+="SELECT c_balance, c_first, c_middle, c_last ";
 	 			proc4+="INTO os_c_balance, os_c_first, os_c_middle, os_c_last ";
 	 			proc4+="FROM customer ";
 	 			proc4+="WHERE c_id = os_c_id AND c_d_id = os_d_id AND c_w_id = os_w_id;\n";
 	 			proc4+="END IF;\n";
 	 			proc4+="set done = 0;\n";
 	 			proc4+="SELECT o_id, o_carrier_id, o_entry_d ";
 	 			proc4+="INTO os_o_id, os_o_carrier_id, os_entdate ";
 	 			proc4+="FROM ";
 	 			proc4+="(SELECT o_id, o_carrier_id, o_entry_d ";
 	 			proc4+="FROM orders where o_d_id = os_d_id AND o_w_id = os_w_id and o_c_id = os_c_id ";
 	 			proc4+="ORDER BY o_id DESC) AS sb LIMIT 1;\n";
 	 			proc4+="IF done THEN\n";
 	 			proc4+="set no_order_status = 'No orders for customer';\n";
 	 			proc4+="END IF;\n";
 	 			proc4+="set done = 0;\n";
 	 			proc4+="set i = 0;\n";
 	 			proc4+="OPEN c_line;\n";
 	 			proc4+="REPEAT\n";
 	 			proc4+="FETCH c_line INTO os_ol_i_id, os_ol_supply_w_id, os_ol_quantity, os_ol_amount, os_ol_delivery_d;\n";
 	 			proc4+="IF NOT done THEN\n";
 	 			proc4+="set os_ol_i_id_array = CONCAT(os_ol_i_id_array,',',CAST(i AS CHAR),',',CAST(os_ol_i_id AS CHAR));\n";
 	 			proc4+="set os_ol_supply_w_id_array = CONCAT(os_ol_supply_w_id_array,',',CAST(i AS CHAR),',',CAST(os_ol_supply_w_id AS CHAR));\n";
 	 			proc4+="set os_ol_quantity_array = CONCAT(os_ol_quantity_array,',',CAST(i AS CHAR),',',CAST(os_ol_quantity AS CHAR));\n";
 	 			proc4+="set os_ol_amount_array = CONCAT(os_ol_amount_array,',',CAST(i AS CHAR),',',CAST(os_ol_amount AS CHAR));\n";
 	 			proc4+="set os_ol_delivery_d_array = CONCAT(os_ol_delivery_d_array,',',CAST(i AS CHAR),',',CAST(os_ol_delivery_d AS CHAR));\n";
 	 			proc4+="set i = i+1;\n";
 	 			proc4+="END IF;\n";
 	 			proc4+="UNTIL done END REPEAT;\n";
 	 			proc4+="CLOSE c_line;\n";
 	 			proc4+="END\n";
 	 	
 	 	stmt.executeUpdate(proc4); 			 
 	 		
	   }
	   catch(SQLException se)
	   {
		   
		   se.printStackTrace();
		   System.out.println("2");
	   }
	   catch(Exception e)
	   {
		   
		   e.printStackTrace();
		   System.out.println("3");
	   }
	   finally
	   {
		   
		   try
		   {
			   if(stmt!=null)
				   stmt.close();
		   }
		   catch(SQLException se2)
		   {
			   se2.printStackTrace();
			   System.out.println("3");
		   }
		   try
		   {
			   if(conn!=null)
				   conn.close();
		   }
		   catch(SQLException se)
		   {
			   se.printStackTrace();
			   System.out.println("4");
		   }
	   }
	   TPCCLoader l=new TPCCLoader();
	   l.load();
   System.out.println("Goodbye!");
   }
}