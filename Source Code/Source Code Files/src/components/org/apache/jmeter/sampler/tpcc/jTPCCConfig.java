package org.apache.jmeter.sampler.tpcc;

import java.text.SimpleDateFormat;

import org.apache.jmeter.sampler.TPCCSampler;

/**
 * Contains all the configuration parameters
 * @author naman
 *
 */

public final class jTPCCConfig {

	
	public static boolean TERMINAL_MESSAGES = true;

	public static enum TransactionType {
		INVALID, NEW_ORDER, PAYMENT, ORDER_STATUS, DELIVERY, STOCK_LEVEL
	}

	public final static int NEW_ORDER = 1, PAYMENT = 2, ORDER_STATUS = 3,
			DELIVERY = 4, STOCK_LEVEL = 5;

	public final static String[] nameTokens = { "BAR", "OUGHT", "ABLE", "PRI",
			"PRES", "ESE", "ANTI", "CALLY", "ATION", "EING" };

	public final static String terminalPrefix = "Term-";
	public final static String reportFilePrefix = "reports/BenchmarkSQL_session_";

	public final static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public final static int configCommitCount = 1000; //the number of records after which to perform commit
	public static int configWhseCount = Integer.parseInt(TPCCSampler.NUM_WHSE);
	public final static int configItemCount = 100000; // as per TPC-C standard
	public final static int configDistPerWhse = 10; // as per TPC-C standard
	public final static int configCustPerDist = 3000; // as per TPC-C standard

	
	public static final int INVALID_ITEM_ID = -12345;//invalid id for Rollback
}
