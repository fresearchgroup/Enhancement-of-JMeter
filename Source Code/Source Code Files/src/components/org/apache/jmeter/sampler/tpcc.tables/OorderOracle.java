package org.apache.jmeter.sampler.tpcc.tables;

import java.sql.Timestamp;
/**
 * Defines the fields of the Order table when testing with Oracle database
 * @author naman
 *
 */

public class OorderOracle {
	

	public int o_id;
	public int o_w_id;
	public int o_d_id;
	public int o_c_id;
	public Integer o_carrier_id;
	public int o_ol_cnt;
	public int o_all_local;
	public String o_entry_d;

	@Override
	public String toString() {
		

		return ("\n***************** Oorder ********************"
				+ "\n*         o_id = " + o_id + "\n*       o_w_id = " + o_w_id
				+ "\n*       o_d_id = " + o_d_id + "\n*       o_c_id = "
				+ o_c_id + "\n* o_carrier_id = " + o_carrier_id
				+ "\n*     o_ol_cnt = " + o_ol_cnt + "\n*  o_all_local = "
				+ o_all_local + "\n*    o_entry_d = " + o_entry_d + "\n**********************************************");
	}

}
