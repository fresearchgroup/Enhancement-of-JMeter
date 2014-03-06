package org.apache.jmeter.sampler.tpcc.tables;

import java.io.Serializable;
/**
 * Defines the fields of the NewOrder table
 * @author naman
 *
 */

public class NewOrder implements Serializable {
	
	public int no_w_id;
	public int no_d_id;
	public int no_o_id;

	@Override
	public String toString() {
		return ("\n***************** NewOrder ********************"
				+ "\n*      no_w_id = " + no_w_id + "\n*      no_d_id = "
				+ no_d_id + "\n*      no_o_id = " + no_o_id + "\n**********************************************");
	}

}
