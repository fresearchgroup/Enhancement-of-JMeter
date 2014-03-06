package org.apache.jmeter.sampler.tpcc;

import java.sql.*;
import java.util.*;

import org.apache.jmeter.sampler.TPCCSampler;
/**
 * Contains the code for creating the database schema along with the procedures
 * when working with Oracle database
 * @author naman
 *
 */

public class CreatorOracle {
   
   
   static  String JDBC_DRIVER = "";			//Driver class for Oracle database
   static String DB_URL = "";				//Database URL for Oracle database
   static  String USER = "";				//Username
   static  String PASS = "";				//Password
   
   public static void main(String[] args) throws SQLException {
	   
}//end main
   public static void create()throws SQLException
   {
	 
	   Connection conn=null;
	   Statement stmt=null;
	   
	   System.out.println(TPCCSampler.PASSWORD);
	   System.out.println(TPCCSampler.NUM_WHSE);
	   //DB_URL=DB_URL+TPCCSampler.HOST+":"+TPCCSampler.PORT+"/";
	   JDBC_DRIVER=JDBC_DRIVER+TPCCSampler.DRIVER_CLASS;
	   DB_URL=DB_URL+TPCCSampler.DBURL+TPCCSampler.DB_NAME;
	   USER=USER+TPCCSampler.USERNAME;
	   PASS=PASS+TPCCSampler.PASSWORD;
	   try{
	     
	      Class.forName(JDBC_DRIVER );								//Register JDBC Driver
	      conn = DriverManager.getConnection(DB_URL, USER, PASS);	//Establish connection with database
	      stmt = conn.createStatement();							//Connect statement object with the connection
	      
      
	      
	      //SQL Query for creating Customer table
	      String customer="CREATE TABLE CUSTOMER (C_ID NUMBER(5, 0), C_D_ID NUMBER(2, 0), C_W_ID NUMBER(4, 0), C_FIRST VARCHAR2(16), C_MIDDLE CHAR(2), C_LAST VARCHAR2(16), C_STREET_1 VARCHAR2(20), C_STREET_2 VARCHAR2(20), C_CITY VARCHAR2(20), C_STATE CHAR(2), C_ZIP CHAR(9), C_PHONE CHAR(16), C_SINCE DATE, C_CREDIT CHAR(2), C_CREDIT_LIM NUMBER(12, 2), C_DISCOUNT NUMBER(4, 4), C_BALANCE NUMBER(12, 2), C_YTD_PAYMENT NUMBER(12, 2), C_PAYMENT_CNT NUMBER(8, 0), C_DELIVERY_CNT NUMBER(8, 0), C_DATA VARCHAR2(500)) INITRANS 4 MAXTRANS 16 PCTFREE 10";
	      stmt.executeUpdate(customer);
	      
	      //SQL Query for creating District table
	      String district="CREATE TABLE DISTRICT (D_ID NUMBER(2, 0), D_W_ID NUMBER(4, 0), D_YTD NUMBER(12, 2), D_TAX NUMBER(4, 4), D_NEXT_O_ID NUMBER, D_NAME VARCHAR2(10), D_STREET_1 VARCHAR2(20), D_STREET_2 VARCHAR2(20), D_CITY VARCHAR2(20), D_STATE CHAR(2), D_ZIP CHAR(9)) INITRANS 4 MAXTRANS 16 PCTFREE 99 PCTUSED 1";
	      stmt.executeUpdate(district);
	      
	      //SQL Query for creating History table
	      String history="CREATE TABLE HISTORY (H_C_ID NUMBER, H_C_D_ID NUMBER, H_C_W_ID NUMBER, H_D_ID NUMBER, H_W_ID NUMBER, H_DATE DATE, H_AMOUNT NUMBER(6, 2), H_DATA VARCHAR2(24)) INITRANS 4 MAXTRANS 16 PCTFREE 10";
	      stmt.executeUpdate(history);
	      
	      //SQL Query for creating Item Table
	      String item="CREATE TABLE ITEM (I_ID NUMBER(6, 0), I_IM_ID NUMBER, I_NAME VARCHAR2(24), I_PRICE NUMBER(5, 2), I_DATA VARCHAR2(50)) INITRANS 4 MAXTRANS 16 PCTFREE 10";
 	      stmt.executeUpdate(item);
	      
	      //SQL Query for creating New_Order table
 	      String newOrder="CREATE TABLE NEW_ORDER (NO_W_ID NUMBER, NO_D_ID NUMBER, NO_O_ID NUMBER, CONSTRAINT INORD PRIMARY KEY (NO_W_ID, NO_D_ID, NO_O_ID) ENABLE ) ORGANIZATION INDEX NOCOMPRESS INITRANS 4 MAXTRANS 16 PCTFREE 10";
 	      stmt.executeUpdate(newOrder);
 	      
 	      //SQL Query for creating Order table
 	      String orders="CREATE TABLE ORDERS (O_ID NUMBER, O_W_ID NUMBER, O_D_ID NUMBER, O_C_ID NUMBER, O_CARRIER_ID NUMBER, O_OL_CNT NUMBER, O_ALL_LOCAL NUMBER, O_ENTRY_D DATE) INITRANS 4 MAXTRANS 16 PCTFREE 10";
 	      stmt.executeUpdate(orders);
 	      
 	      //SQL Query for creating Order_Line table
 	      String order_line="CREATE TABLE ORDER_LINE (OL_W_ID NUMBER, OL_D_ID NUMBER, OL_O_ID NUMBER, OL_NUMBER NUMBER, OL_I_ID NUMBER, OL_DELIVERY_D DATE, OL_AMOUNT NUMBER, OL_SUPPLY_W_ID NUMBER, OL_QUANTITY NUMBER, OL_DIST_INFO CHAR(24), CONSTRAINT IORDL PRIMARY KEY (OL_W_ID, OL_D_ID, OL_O_ID, OL_NUMBER) ENABLE) ORGANIZATION INDEX NOCOMPRESS INITRANS 4 MAXTRANS 16 PCTFREE 10";
 	      stmt.executeUpdate(order_line);
 	      
 	      //SQL Query for creating Warehouse table
 	      String warehouse="CREATE TABLE WAREHOUSE (W_ID NUMBER(4, 0), W_YTD NUMBER(12, 2), W_TAX NUMBER(4, 4), W_NAME VARCHAR2(10), W_STREET_1 VARCHAR2(20), W_STREET_2 VARCHAR2(20), W_CITY VARCHAR2(20), W_STATE CHAR(2), W_ZIP CHAR(9)) INITRANS 4 MAXTRANS 16 PCTFREE 99 PCTUSED 1";
 	      stmt.executeUpdate(warehouse);
 	      
 	      //SQL Query for creating Stock table
 	      String stock="CREATE TABLE STOCK (S_I_ID NUMBER(6, 0), S_W_ID NUMBER(4, 0), S_QUANTITY NUMBER(6, 0), S_DIST_01 CHAR(24), S_DIST_02 CHAR(24), S_DIST_03 CHAR(24), S_DIST_04 CHAR(24), S_DIST_05 CHAR(24), S_DIST_06 CHAR(24), S_DIST_07 CHAR(24), S_DIST_08 CHAR(24), S_DIST_09 CHAR(24), S_DIST_10 CHAR(24), S_YTD NUMBER(10, 0), S_ORDER_CNT NUMBER(6, 0), S_REMOTE_CNT NUMBER(6, 0), S_DATA VARCHAR2(50)) INITRANS 4 MAXTRANS 16 PCTFREE 10";
 	      stmt.executeUpdate(stock);
 	      

 
 	      System.out.println("Created table in given database...");
 	      
 	      
 	//Queries to be executed for creating procedure Stock Level
 	String proc1="";
 	proc1+="CREATE OR REPLACE PROCEDURE SLEV (";
 	proc1+="st_w_id	INTEGER,";
 	proc1+="st_d_id INTEGER,";
 	proc1+="threshold INTEGER )\n";
 	proc1+="IS\n"; 
 	proc1+="st_o_id	NUMBER;\n";	
 	proc1+="stock_count INTEGER;\n";
 	proc1+="not_serializable EXCEPTION;\n";
 	proc1+="PRAGMA EXCEPTION_INIT(not_serializable,-8177);\n";
 	proc1+="deadlock EXCEPTION;\n";
 	proc1+="PRAGMA EXCEPTION_INIT(deadlock,-60);\n";
 	proc1+="snapshot_too_old EXCEPTION;\n";
 	proc1+="PRAGMA EXCEPTION_INIT(snapshot_too_old,-1555);\n";
 	proc1+="BEGIN\n";
 	proc1+="SELECT d_next_o_id INTO st_o_id ";
 	proc1+="FROM district ";
 	proc1+="WHERE d_w_id=st_w_id AND d_id=st_d_id;\n";
 	proc1+="SELECT COUNT(DISTINCT (s_i_id)) INTO stock_count ";
 	proc1+="FROM order_line, stock ";
 	proc1+="WHERE ol_w_id = st_w_id AND ";
 	proc1+="ol_d_id = st_d_id AND (ol_o_id < st_o_id) AND ";
 	proc1+="ol_o_id >= (st_o_id - 20) AND s_w_id = st_w_id AND ";
 	proc1+="s_i_id = ol_i_id AND s_quantity < threshold;\n";
 	proc1+="COMMIT;\n";
 	proc1+="EXCEPTION\n";
 	proc1+="WHEN not_serializable OR deadlock OR snapshot_too_old\n";
 	proc1+="THEN\n";
 	proc1+="ROLLBACK;\n";
 	proc1+="END;";
 	//proc1+="/";
 	stmt.executeUpdate(proc1);
 	
 	
 	//Queries to be executed for creating procedure Order Status
 	String proc2=""; 
 	proc2+="CREATE OR REPLACE PROCEDURE OSTAT (";
 	proc2+="os_w_id INTEGER,";
 	proc2+="os_d_id INTEGER,";
 	proc2+="os_c_id IN OUT INTEGER,";
 	proc2+="byname INTEGER,";
 	proc2+="os_c_last IN OUT VARCHAR2,";
 	proc2+="os_c_first OUT VARCHAR2,";
 	proc2+="os_c_middle OUT VARCHAR2,";
 	proc2+="os_c_balance OUT NUMBER,";
 	proc2+="os_o_id OUT INTEGER,";
 	proc2+="os_entdate OUT DATE,";
 	proc2+="os_o_carrier_id OUT INTEGER )\n";
 	proc2+="IS\n";
 	proc2+="TYPE numbertable IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;\n";
 	proc2+="os_ol_i_id numbertable;\n";	 
 	proc2+="os_ol_supply_w_id numbertable;\n";	
 	proc2+="os_ol_quantity numbertable;\n";	
 	proc2+="TYPE amounttable IS TABLE OF NUMBER(6,2) INDEX BY BINARY_INTEGER;\n";
 	proc2+="os_ol_amount amounttable;\n";
 	proc2+="TYPE datetable IS TABLE OF DATE INDEX BY BINARY_INTEGER;\n";
 	proc2+="os_ol_delivery_d datetable;\n";
 	proc2+="namecnt INTEGER;\n";
 	proc2+="i BINARY_INTEGER;\n";
 	proc2+="CURSOR c_name IS ";
 	proc2+="SELECT c_balance, c_first, c_middle, c_id ";
 	proc2+="FROM customer ";
 	proc2+="WHERE c_last = os_c_last AND c_d_id = os_d_id AND c_w_id = os_w_id ";
 	proc2+="ORDER BY c_first;\n";
 	proc2+="CURSOR c_line IS ";
 	proc2+="SELECT ol_i_id, ol_supply_w_id, ol_quantity, ";
 	proc2+="ol_amount, ol_delivery_d ";
 	proc2+="FROM order_line ";
 	proc2+="WHERE ol_o_id = os_o_id AND ol_d_id = os_d_id AND ol_w_id = os_w_id;\n";
 	proc2+="os_c_line c_line%ROWTYPE;\n";
 	proc2+="not_serializable EXCEPTION;\n";
 	proc2+="PRAGMA EXCEPTION_INIT(not_serializable,-8177);\n";
 	proc2+="deadlock EXCEPTION;\n";
 	proc2+="PRAGMA EXCEPTION_INIT(deadlock,-60);\n";
 	proc2+="snapshot_too_old EXCEPTION;\n";
 	proc2+="PRAGMA EXCEPTION_INIT(snapshot_too_old,-1555);\n";
 	proc2+="BEGIN\n";
 	proc2+="IF ( byname = 1 )\n";
 	proc2+="THEN\n";
 	proc2+="SELECT count(c_id) INTO namecnt ";
 	proc2+="FROM customer ";
 	proc2+="WHERE c_last = os_c_last AND c_d_id = os_d_id AND c_w_id = os_w_id;\n";
 	proc2+="IF ( MOD (namecnt, 2) = 1 )\n";
 	proc2+="THEN\n";
 	proc2+="namecnt := (namecnt + 1);\n";
 	proc2+="END IF;\n";
 	proc2+="OPEN c_name;\n";
 	proc2+="FOR loop_counter IN 0 .. (namecnt/2)\n";
 	proc2+="LOOP\n";
 	proc2+="FETCH c_name ";  
 	proc2+="INTO os_c_balance, os_c_first, os_c_middle, os_c_id;\n";
 	proc2+="END LOOP;\n";
 	proc2+="close c_name;\n";
 	proc2+="ELSE\n";
 	proc2+="SELECT c_balance, c_first, c_middle, c_last ";
 	proc2+="INTO os_c_balance, os_c_first, os_c_middle, os_c_last ";
 	proc2+="FROM customer ";
 	proc2+="WHERE c_id = os_c_id AND c_d_id = os_d_id AND c_w_id = os_w_id;\n";
 	proc2+="END IF;\n";
 	proc2+="BEGIN\n";
 	proc2+="SELECT o_id, o_carrier_id, o_entry_d "; 
 	proc2+="INTO os_o_id, os_o_carrier_id, os_entdate ";
 	proc2+="FROM ";
 	proc2+="(SELECT o_id, o_carrier_id, o_entry_d ";
 	proc2+="FROM orders where o_d_id = os_d_id AND o_w_id = os_w_id and o_c_id=os_c_id ";
 	proc2+="ORDER BY o_id DESC) ";
 	proc2+="WHERE ROWNUM = 1;\n";
 	proc2+="EXCEPTION\n";
 	proc2+="WHEN NO_DATA_FOUND THEN\n";
 	proc2+="dbms_output.put_line('No orders for customer');\n";
 	proc2+="END;\n";
 	proc2+="i := 0;\n";
 	proc2+="FOR os_c_line IN c_line\n";
 	proc2+="LOOP\n";
 	proc2+="os_ol_i_id(i) := os_c_line.ol_i_id;\n";
 	proc2+="os_ol_supply_w_id(i) := os_c_line.ol_supply_w_id;\n";
 	proc2+="os_ol_quantity(i) := os_c_line.ol_quantity;\n";
 	proc2+="os_ol_amount(i) := os_c_line.ol_amount;\n";
 	proc2+="os_ol_delivery_d(i) := os_c_line.ol_delivery_d;\n";
 	proc2+="i := i+1;\n";
 	proc2+="END LOOP;\n";
 	proc2+="EXCEPTION WHEN not_serializable OR deadlock OR snapshot_too_old THEN\n";
 	proc2+="ROLLBACK;\n";
 	proc2+="END;";
 	//proc2+="/";
 	
 	stmt.executeUpdate(proc2);
 	
 	
 	//Queries to be executed for creating procedure Delivery
 	String proc3="";
 	proc3+="CREATE OR REPLACE PROCEDURE DELIVERY (";
 	proc3+="d_w_id INTEGER,";
 	proc3+="d_o_carrier_id INTEGER,";
 	proc3+="timestamp IN DATE )\n";
 	proc3+="IS\n";
 	proc3+="d_no_o_id INTEGER;\n";
 	proc3+="d_d_id INTEGER;\n";
 	proc3+="d_c_id NUMBER;\n";
 	proc3+="d_ol_total NUMBER;\n";
 	proc3+="current_ROWID UROWID;\n";
 	proc3+="CURSOR c_no IS ";
 	proc3+="SELECT no_o_id,ROWID ";
 	proc3+="FROM new_order ";
 	proc3+="WHERE no_d_id = d_d_id AND no_w_id = d_w_id ";
 	proc3+="ORDER BY no_o_id ASC;\n";
 	proc3+="not_serializable EXCEPTION;\n";
 	proc3+="PRAGMA EXCEPTION_INIT(not_serializable,-8177);\n";
 	proc3+="deadlock EXCEPTION;\n";
 	proc3+="PRAGMA EXCEPTION_INIT(deadlock,-60);\n";
 	proc3+="snapshot_too_old EXCEPTION;\n";
 	proc3+="PRAGMA EXCEPTION_INIT(snapshot_too_old,-1555);\n";
 	proc3+="BEGIN\n";
 	proc3+="FOR loop_counter IN 1 .. 10\n";
 	proc3+="LOOP\n";
 	proc3+="d_d_id := loop_counter;\n";
 	proc3+="open c_no;\n";
 	proc3+="FETCH c_no INTO d_no_o_id,current_ROWID;\n";
 	proc3+="EXIT WHEN c_no%NOTFOUND;\n";
 	proc3+="DELETE FROM new_order WHERE rowid = current_ROWID;\n";
 	proc3+="close c_no;\n";
 	proc3+="SELECT o_c_id INTO d_c_id FROM orders ";
 	proc3+="WHERE o_id = d_no_o_id AND o_d_id = d_d_id AND ";
 	proc3+="o_w_id = d_w_id;\n";
 	proc3+="UPDATE orders SET o_carrier_id = d_o_carrier_id ";
 	proc3+="WHERE o_id = d_no_o_id AND o_d_id = d_d_id AND ";
 	proc3+="o_w_id = d_w_id;\n";
 	proc3+="UPDATE order_line SET ol_delivery_d = timestamp ";
 	proc3+="WHERE ol_o_id = d_no_o_id AND ol_d_id = d_d_id AND ";
 	proc3+="ol_w_id = d_w_id;\n";
 	proc3+="SELECT SUM(ol_amount) INTO d_ol_total ";
 	proc3+="FROM order_line ";
 	proc3+="WHERE ol_o_id = d_no_o_id AND ol_d_id = d_d_id ";
 	proc3+="AND ol_w_id = d_w_id;\n";
 	proc3+="UPDATE customer SET c_balance = c_balance + d_ol_total ";
 	proc3+="WHERE c_id = d_c_id AND c_d_id = d_d_id AND ";
 	proc3+="c_w_id = d_w_id;\n";
 	proc3+="COMMIT;\n";
 	proc3+="DBMS_OUTPUT.PUT_LINE('D: ' || d_d_id || 'O: ' || d_no_o_id || 'time ' || timestamp);\n";
 	proc3+="END LOOP;\n";
 	proc3+="EXCEPTION\n";
 	proc3+="WHEN not_serializable OR deadlock OR snapshot_too_old\n";
 	proc3+="THEN\n";
 	proc3+="ROLLBACK;\n";
 	proc3+="END;";
 	//proc3+="/";
 	stmt.executeUpdate(proc3);
 	
 	
 	//Queries to be execcuted for creating procedure New Order
 	String proc4="";
 	proc4+="CREATE OR REPLACE PROCEDURE NEWORD (";
 	proc4+="no_w_id INTEGER,";
 	proc4+="no_max_w_id INTEGER,";
 	proc4+="no_d_id INTEGER,";
 	proc4+="no_c_id INTEGER,";
 	proc4+="no_o_ol_cnt INTEGER,";
 	proc4+="no_c_discount OUT NUMBER,";
 	proc4+="no_c_last OUT VARCHAR2,";
 	proc4+="no_c_credit OUT VARCHAR2,";
 	proc4+="no_d_tax OUT NUMBER,";
 	proc4+="no_w_tax OUT NUMBER,";
 	proc4+="no_d_next_o_id IN OUT INTEGER,";
 	proc4+="timestamp IN DATE )\n";
 	proc4+="IS\n";
 	proc4+="no_ol_supply_w_id INTEGER;\n";
 	proc4+="no_ol_i_id NUMBER;\n";
 	proc4+="no_ol_quantity NUMBER;\n";
 	proc4+="no_o_all_local INTEGER;\n";
 	proc4+="o_id INTEGER;\n";
 	proc4+="no_i_name VARCHAR2(24);\n";
 	proc4+="no_i_price NUMBER(5,2);\n";
 	proc4+="no_i_data VARCHAR2(50);\n";
 	proc4+="no_s_quantity NUMBER(6);\n";
 	proc4+="no_ol_amount NUMBER(6,2);\n";
 	proc4+="no_s_dist_01 CHAR(24);\n";
 	proc4+="no_s_dist_02 CHAR(24);\n";
 	proc4+="no_s_dist_03 CHAR(24);\n";
 	proc4+="no_s_dist_04 CHAR(24);\n";
 	proc4+="no_s_dist_05 CHAR(24);\n";
 	proc4+="no_s_dist_06 CHAR(24);\n";
 	proc4+="no_s_dist_07 CHAR(24);\n";
 	proc4+="no_s_dist_08 CHAR(24);\n";
 	proc4+="no_s_dist_09 CHAR(24);\n";
 	proc4+="no_s_dist_10 CHAR(24);\n";
 	proc4+="no_ol_dist_info CHAR(24);\n";
 	proc4+="no_s_data VARCHAR2(50);\n";
 	proc4+="x NUMBER;\n";
 	proc4+="rbk NUMBER;\n";
 	proc4+="not_serializable EXCEPTION;\n";
 	proc4+="PRAGMA EXCEPTION_INIT(not_serializable,-8177);\n";
 	proc4+="deadlock EXCEPTION;\n";
 	proc4+="PRAGMA EXCEPTION_INIT(deadlock,-60);\n";
 	proc4+="snapshot_too_old EXCEPTION;\n";
 	proc4+="PRAGMA EXCEPTION_INIT(snapshot_too_old,-1555);\n";
 	proc4+="integrity_viol EXCEPTION;\n";
 	proc4+="PRAGMA EXCEPTION_INIT(integrity_viol,-1);\n";
 	proc4+="BEGIN\n";
 	proc4+="no_o_all_local := 0;\n";
 	proc4+="SELECT c_discount, c_last, c_credit, w_tax ";
 	proc4+="INTO no_c_discount, no_c_last, no_c_credit, no_w_tax ";
 	proc4+="FROM customer, warehouse ";
 	proc4+="WHERE warehouse.w_id = no_w_id AND customer.c_w_id = no_w_id AND ";
 	proc4+="customer.c_d_id = no_d_id AND customer.c_id = no_c_id;\n";
 	proc4+="UPDATE district SET d_next_o_id = d_next_o_id + 1 WHERE d_id = no_d_id AND d_w_id = no_w_id RETURNING d_next_o_id, d_tax INTO no_d_next_o_id, no_d_tax;\n";
 	proc4+="o_id := no_d_next_o_id;\n";
 	proc4+="INSERT INTO ORDERS (o_id, o_d_id, o_w_id, o_c_id, o_entry_d, o_ol_cnt, o_all_local) VALUES (o_id, no_d_id, no_w_id, no_c_id, timestamp, no_o_ol_cnt, no_o_all_local);\n";
 	proc4+="INSERT INTO NEW_ORDER (no_o_id, no_d_id, no_w_id) VALUES (o_id, no_d_id, no_w_id);\n";
 	proc4+="rbk := round(DBMS_RANDOM.value(low => 1, high => 100));\n";
 	proc4+="FOR loop_counter IN 1 .. no_o_ol_cnt\n";
 	proc4+="LOOP\n";
 	proc4+="IF ((loop_counter = no_o_ol_cnt) AND (rbk = 1))\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_i_id := 100001;\n";
 	proc4+="ELSE\n";
 	proc4+="no_ol_i_id := round(DBMS_RANDOM.value(low => 1, high => 100000));\n";
 	proc4+="END IF;\n";
 	proc4+="x := round(DBMS_RANDOM.value(low => 1, high => 100));\n";
 	proc4+="IF ( x > 1 )\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_supply_w_id := no_w_id;\n";
 	proc4+="ELSE\n";
 	proc4+="no_ol_supply_w_id := no_w_id;\n";
 	proc4+="no_o_all_local := 0;\n";
 	proc4+="WHILE ((no_ol_supply_w_id = no_w_id) AND (no_max_w_id != 1))\n";
 	proc4+="LOOP\n";
 	proc4+="no_ol_supply_w_id := round(DBMS_RANDOM.value(low => 1, high => no_max_w_id));\n";
 	proc4+="END LOOP;\n";
 	proc4+="END IF;\n";
 	proc4+="no_ol_quantity := round(DBMS_RANDOM.value(low => 1, high => 10));\n";
 	proc4+="SELECT i_price, i_name, i_data INTO no_i_price, no_i_name, no_i_data ";
 	proc4+="FROM item WHERE i_id = no_ol_i_id;\n";
 	proc4+="SELECT s_quantity, s_data, s_dist_01, s_dist_02, s_dist_03, s_dist_04, s_dist_05, s_dist_06, s_dist_07, s_dist_08, s_dist_09, s_dist_10 ";
 	proc4+="INTO no_s_quantity, no_s_data, no_s_dist_01, no_s_dist_02, no_s_dist_03, no_s_dist_04, no_s_dist_05, no_s_dist_06, no_s_dist_07, no_s_dist_08, no_s_dist_09, no_s_dist_10 FROM stock WHERE s_i_id = no_ol_i_id AND s_w_id = no_ol_supply_w_id;\n";
 	proc4+="IF ( no_s_quantity > no_ol_quantity )\n";
 	proc4+="THEN\n";
 	proc4+="no_s_quantity := ( no_s_quantity - no_ol_quantity );\n";
 	proc4+="ELSE\n";
 	proc4+="no_s_quantity := ( no_s_quantity - no_ol_quantity + 91 );\n";
 	proc4+="END IF;\n";
 	proc4+="UPDATE stock SET s_quantity = no_s_quantity ";
 	proc4+="WHERE s_i_id = no_ol_i_id ";
 	proc4+="AND s_w_id = no_ol_supply_w_id;\n";
 	proc4+="no_ol_amount := (  no_ol_quantity * no_i_price * ( 1 + no_w_tax + no_d_tax ) * ( 1 - no_c_discount ) );\n";
 	proc4+="IF no_d_id = 1\n";
 	proc4+="THEN\n"; 
 	proc4+="no_ol_dist_info := no_s_dist_01;\n"; 
 	proc4+="ELSIF no_d_id = 2\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_02;\n";
 	proc4+="ELSIF no_d_id = 3\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_03;\n";
 	proc4+="ELSIF no_d_id = 4\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_04;\n";
 	proc4+="ELSIF no_d_id = 5\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_05;\n";
 	proc4+="ELSIF no_d_id = 6\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_06;\n";
 	proc4+="ELSIF no_d_id = 7\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_07;\n";
 	proc4+="ELSIF no_d_id = 8\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_08;\n";
 	proc4+="ELSIF no_d_id = 9\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_09;\n";
 	proc4+="ELSIF no_d_id = 10\n";
 	proc4+="THEN\n";
 	proc4+="no_ol_dist_info := no_s_dist_10;\n";
 	proc4+="END IF;\n";
 	proc4+="INSERT INTO order_line (ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id, ol_supply_w_id, ol_quantity, ol_amount, ol_dist_info) ";
 	proc4+="VALUES (o_id, no_d_id, no_w_id, loop_counter, no_ol_i_id, no_ol_supply_w_id, no_ol_quantity, no_ol_amount, no_ol_dist_info);\n";
 	proc4+="END LOOP;\n";
 	proc4+="COMMIT;\n";
 	proc4+="EXCEPTION\n";
 	proc4+="WHEN not_serializable OR deadlock OR snapshot_too_old OR integrity_viol OR no_data_found\n";
 	proc4+="THEN\n";
 	proc4+="ROLLBACK;\n";
 	proc4+="END;";
 	//proc4+="/";
 	stmt.executeUpdate(proc4);
 	
 	
 	//Queries to be executed for creating procedure Payment
 	String proc5="";
 	proc5+="CREATE OR REPLACE PROCEDURE PAYMENT (";
 	proc5+="p_w_id INTEGER,";
 	proc5+="p_d_id INTEGER,";
 	proc5+="p_c_w_id INTEGER,";
 	proc5+="p_c_d_id INTEGER,";
 	proc5+="p_c_id IN OUT INTEGER,";
 	proc5+="byname INTEGER,";
 	proc5+="p_h_amount NUMBER,";
 	proc5+="p_c_last IN OUT VARCHAR2,";
 	proc5+="p_w_street_1 OUT VARCHAR2,";
 	proc5+="p_w_street_2 OUT VARCHAR2,";
 	proc5+="p_w_city OUT VARCHAR2,";
 	proc5+="p_w_state OUT VARCHAR2,";
 	proc5+="p_w_zip OUT VARCHAR2,";
 	proc5+="p_d_street_1 OUT VARCHAR2,";
 	proc5+="p_d_street_2 OUT VARCHAR2,";
 	proc5+="p_d_city OUT VARCHAR2,";
 	proc5+="p_d_state OUT VARCHAR2,";
 	proc5+="p_d_zip OUT VARCHAR2,";
 	proc5+="p_c_first OUT VARCHAR2,";
 	proc5+="p_c_middle OUT VARCHAR2,";
 	proc5+="p_c_street_1 OUT VARCHAR2,";
 	proc5+="p_c_street_2 OUT VARCHAR2,";
 	proc5+="p_c_city OUT VARCHAR2,";
 	proc5+="p_c_state OUT VARCHAR2,";
 	proc5+="p_c_zip OUT VARCHAR2,";
 	proc5+="p_c_phone OUT VARCHAR2,";
 	proc5+="p_c_since OUT DATE,";
 	proc5+="p_c_credit IN OUT VARCHAR2,";
 	proc5+="p_c_credit_lim OUT NUMBER,";
 	proc5+="p_c_discount OUT NUMBER,";
 	proc5+="p_c_balance IN OUT NUMBER,";
 	proc5+="p_c_data OUT VARCHAR2,";
 	proc5+="timestamp IN DATE )\n";
 	proc5+="IS\n";
 	proc5+="namecnt INTEGER;\n";
 	proc5+="p_d_name VARCHAR2(11);\n";
 	proc5+="p_w_name VARCHAR2(11);\n";
 	proc5+="p_c_new_data VARCHAR2(500);\n";
 	proc5+="h_data VARCHAR2(30);\n";
 	proc5+="CURSOR c_byname IS ";
 	proc5+="SELECT c_first, c_middle, c_id, ";
 	proc5+="c_street_1, c_street_2, c_city, c_state, c_zip, ";
 	proc5+="c_phone, c_credit, c_credit_lim, ";
 	proc5+="c_discount, c_balance, c_since ";
 	proc5+="FROM customer ";
 	proc5+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND c_last = p_c_last ";
 	proc5+="ORDER BY c_first;\n"; 
 	proc5+="not_serializable EXCEPTION;\n";
 	proc5+="PRAGMA EXCEPTION_INIT(not_serializable,-8177);\n";
 	proc5+="deadlock EXCEPTION;\n";
 	proc5+="PRAGMA EXCEPTION_INIT(deadlock,-60);\n";
 	proc5+="snapshot_too_old EXCEPTION;\n";
 	proc5+="PRAGMA EXCEPTION_INIT(snapshot_too_old,-1555);\n";
 	proc5+="BEGIN\n";
 	proc5+="UPDATE warehouse SET w_ytd = w_ytd + p_h_amount ";
 	proc5+="WHERE w_id = p_w_id;\n";
 	proc5+="SELECT w_street_1, w_street_2, w_city, w_state, w_zip, w_name ";
 	proc5+="INTO p_w_street_1, p_w_street_2, p_w_city, p_w_state, p_w_zip, p_w_name ";
 	proc5+="FROM warehouse ";
 	proc5+="WHERE w_id = p_w_id;\n";
 	proc5+="UPDATE district SET d_ytd = d_ytd + p_h_amount ";
 	proc5+="WHERE d_w_id = p_w_id AND d_id = p_d_id;\n";
 	proc5+="SELECT d_street_1, d_street_2, d_city, d_state, d_zip, d_name ";
 	proc5+="INTO p_d_street_1, p_d_street_2, p_d_city, p_d_state, p_d_zip, p_d_name ";
 	proc5+="FROM district ";
 	proc5+="WHERE d_w_id = p_w_id AND d_id = p_d_id;\n";
 	proc5+="IF ( byname = 1 )\n";
 	proc5+="THEN\n";
 	proc5+="SELECT count(c_id) INTO namecnt ";
 	proc5+="FROM customer ";
 	proc5+="WHERE c_last = p_c_last AND c_d_id = p_c_d_id AND c_w_id = p_c_w_id;\n";
 	proc5+="OPEN c_byname;\n";
 	proc5+="IF ( MOD (namecnt, 2) = 1 )\n";
 	proc5+="THEN\n";
 	proc5+="namecnt := (namecnt + 1);\n";
 	proc5+="END IF;\n";
 	proc5+="FOR loop_counter IN 0 .. (namecnt/2)\n";
 	proc5+="LOOP\n";
 	proc5+="FETCH c_byname\n";
 	proc5+="INTO p_c_first, p_c_middle, p_c_id, p_c_street_1, p_c_street_2, p_c_city, ";
 	proc5+="p_c_state, p_c_zip, p_c_phone, p_c_credit, p_c_credit_lim, p_c_discount, p_c_balance, p_c_since;\n";
 	proc5+="END LOOP;\n";
 	proc5+="CLOSE c_byname;\n";
 	proc5+="ELSE\n";
 	proc5+="SELECT c_first, c_middle, c_last, ";
 	proc5+="c_street_1, c_street_2, c_city, c_state, c_zip, ";
 	proc5+="c_phone, c_credit, c_credit_lim, ";
 	proc5+="c_discount, c_balance, c_since ";
 	proc5+="INTO p_c_first, p_c_middle, p_c_last, ";
 	proc5+="p_c_street_1, p_c_street_2, p_c_city, p_c_state, p_c_zip, ";
 	proc5+="p_c_phone, p_c_credit, p_c_credit_lim, ";
 	proc5+="p_c_discount, p_c_balance, p_c_since ";
 	proc5+="FROM customer ";
 	proc5+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND c_id = p_c_id;\n";
 	proc5+="END IF;\n";
 	proc5+="p_c_balance := ( p_c_balance + p_h_amount );\n";
 	proc5+="IF p_c_credit = 'BC'\n"; 
 	proc5+="THEN\n";
 	proc5+="SELECT c_data INTO p_c_data ";
 	proc5+="FROM customer ";
 	proc5+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND c_id = p_c_id;\n";
 	proc5+="h_data := ( p_w_name || ' ' || p_d_name );\n";
 	proc5+="p_c_new_data := (TO_CHAR(p_c_id) || ' ' || TO_CHAR(p_c_d_id) || ' ' || ";
 	proc5+="TO_CHAR(p_c_w_id) || ' ' || TO_CHAR(p_d_id) || ' ' || TO_CHAR(p_w_id) || ' ' || TO_CHAR(p_h_amount,'9999.99') || TO_CHAR(timestamp) || h_data);\n";
 	proc5+="p_c_new_data := substr(CONCAT(p_c_new_data,p_c_data),1,500-(LENGTH(p_c_new_data)));\n";
 	proc5+="UPDATE customer ";
 	proc5+="SET c_balance = p_c_balance, c_data = p_c_new_data ";
 	proc5+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND ";
 	proc5+="c_id = p_c_id;\n";
 	proc5+="ELSE\n";
 	proc5+="UPDATE customer SET c_balance = p_c_balance ";
 	proc5+="WHERE c_w_id = p_c_w_id AND c_d_id = p_c_d_id AND ";
 	proc5+="c_id = p_c_id;\n";
 	proc5+="END IF;\n";
 	proc5+="h_data := ( p_w_name|| ' ' || p_d_name );\n";
 	proc5+="INSERT INTO history (h_c_d_id, h_c_w_id, h_c_id, h_d_id, ";
 	proc5+="h_w_id, h_date, h_amount, h_data) ";
 	proc5+="VALUES (p_c_d_id, p_c_w_id, p_c_id, p_d_id, ";
 	proc5+="p_w_id, timestamp, p_h_amount, h_data);\n";
 	proc5+="COMMIT;\n";
 	proc5+="EXCEPTION\n";
 	proc5+="WHEN not_serializable OR deadlock OR snapshot_too_old\n";
 	proc5+="THEN\n";
 	proc5+="ROLLBACK;\n";
 	proc5+="END;";
 	//proc5+="/";
 	stmt.executeUpdate(proc5);
 	
 	System.out.println("Procedures created in the database");
 	
 	
 			
 	 		
   }
	   catch(SQLException se)
	   {
      
		   se.printStackTrace();
	   }
	   catch(Exception e)
	   {
            e.printStackTrace();
	   }
	   finally
	   {
		   //close all resources
		   try
		   {
			   if(stmt!=null)
				   stmt.close();
		   }
		   catch(SQLException se2)
		   {
		   }
		   try
		   {
			   if(conn!=null)
				   conn.close();
		   }
		   catch(SQLException se)
		   {
			   se.printStackTrace();
		   }
   }
	   TPCCLoaderOracle l=new TPCCLoaderOracle();			//Calling the Loader class for Oracle
	   l.load();
   System.out.println("Goodbye!");
   }
}