package org.apache.jmeter.sampler.tpcc;



import static org.apache.jmeter.sampler.tpcc.jTPCCConfig.nameTokens;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.text.SimpleDateFormat;
import org.apache.jmeter.sampler.tpcc.tables.CustomerOracle;
import org.apache.jmeter.sampler.tpcc.util.*;

/**
 * Contains all utility functions for TPC-C testing with Oracle database
 * @author naman
 *
 */

public class TPCCUtilOracle {
	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
	

   /**
    * 
    * @param rs ResultSet for extracting values
    * @return CustomerOracle oblect
    * @throws SQLException for errors in SQL queries and syntax
    */
	public static CustomerOracle newCustomerFromResults(ResultSet rs) throws SQLException 
	{
		CustomerOracle c = new CustomerOracle();
		c.c_first = rs.getString("c_first");
		c.c_middle = rs.getString("c_middle");
		c.c_street_1 = rs.getString("c_street_1");
		c.c_street_2 = rs.getString("c_street_2");
		c.c_city = rs.getString("c_city");
		c.c_state = rs.getString("c_state");
		c.c_zip = rs.getString("c_zip");
		c.c_phone = rs.getString("c_phone");
		c.c_credit = rs.getString("c_credit");
		c.c_credit_lim = rs.getFloat("c_credit_lim");
		c.c_discount = rs.getFloat("c_discount");
		c.c_balance = rs.getFloat("c_balance");
		c.c_ytd_payment = rs.getFloat("c_ytd_payment");
		c.c_payment_cnt = rs.getInt("c_payment_cnt");
		c.c_since = rs.getString("c_since");
		return c;
	}
	private static final RandomGenerator ran = new RandomGenerator(0);

	public static String randomStr(int strLen) 
	{
	    if (strLen > 1) 
	        return ran.astring(strLen - 1, strLen - 1);
	    else
	        return "";
	}

	public static String randomNStr(int stringLength) 
	{
		if (stringLength > 0)
		    return ran.nstring(stringLength, stringLength);
		else
		    return "";
	}

	public static String getCurrentTime()
	{
		return dateFormat.format(new java.util.Date());
	}

	public static String formattedDouble(double d)
	{
		String dS = "" + d;
		return dS.length() > 6 ? dS.substring(0, 6) : dS;
	}

	private static final int OL_I_ID_C = 7911; // in range [0, 8191]
	private static final int C_ID_C = 259; // in range [0, 1023]
	private static final int C_LAST_LOAD_C = 157; // in range [0, 255]
	private static final int C_LAST_RUN_C = 223; // in range [0, 255]

	public static int getItemID(Random r) 
	{
		return nonUniformRandom(8191, OL_I_ID_C, 1, 100000, r);
	}

	public static int getCustomerID(Random r) 
	{
		return nonUniformRandom(1023, C_ID_C, 1, 3000, r);
	}

	public static String getLastName(int num) 
	{
		return nameTokens[num / 100] + nameTokens[(num / 10) % 10]+ nameTokens[num % 10];
	}

	public static String getNonUniformRandomLastNameForRun(Random r) {
		return getLastName(nonUniformRandom(255, C_LAST_RUN_C, 0, 999, r));
	}

	public static String getNonUniformRandomLastNameForLoad(Random r)
	{
		return getLastName(nonUniformRandom(255, C_LAST_LOAD_C, 0, 999, r));
	}

	public static int randomNumber(int min, int max, Random r) 
	{
		return (int) (r.nextDouble() * (max - min + 1) + min);
	}

	public static int nonUniformRandom(int A, int C, int min, int max, Random r) 
	{
		return (((randomNumber(0, A, r) | randomNumber(min, max, r)) + C) % (max- min + 1))+ min;
	}

}
