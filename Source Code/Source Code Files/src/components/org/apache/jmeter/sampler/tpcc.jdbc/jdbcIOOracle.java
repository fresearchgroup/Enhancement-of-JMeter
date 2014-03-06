package org.apache.jmeter.sampler.tpcc.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.jmeter.sampler.tpcc.tables.NewOrder;
import org.apache.jmeter.sampler.tpcc.tables.OorderOracle;
import org.apache.jmeter.sampler.tpcc.tables.OrderLineOracle;

/**
 * handles the execution of the JDBC statements
 * when working with Oracle database
 * @author naman
 *
 */

public class jdbcIOOracle {
	

	public void insertOrder(PreparedStatement ordrPrepStmt, OorderOracle oorder) {

		try {

			ordrPrepStmt.setInt(1, oorder.o_w_id);
			ordrPrepStmt.setInt(2, oorder.o_d_id);
			ordrPrepStmt.setInt(3, oorder.o_id);
			ordrPrepStmt.setInt(4, oorder.o_c_id);
			if (oorder.o_carrier_id != null) {
				ordrPrepStmt.setInt(5, oorder.o_carrier_id);
			} else {
				ordrPrepStmt.setNull(5, Types.INTEGER);
			}
			ordrPrepStmt.setInt(6, oorder.o_ol_cnt);
			ordrPrepStmt.setInt(7, oorder.o_all_local);
			ordrPrepStmt.setString(8, oorder.o_entry_d);
			ordrPrepStmt.addBatch();
			
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}

	}

	public void insertNewOrder(PreparedStatement nworPrepStmt,NewOrder new_order)
	{

		try 
		{
			nworPrepStmt.setInt(1, new_order.no_w_id);
			nworPrepStmt.setInt(2, new_order.no_d_id);
			nworPrepStmt.setInt(3, new_order.no_o_id);

			nworPrepStmt.addBatch();

		} 
		catch (SQLException se) 
		{
			throw new RuntimeException(se);
		}

	}

	public void insertOrderLine(PreparedStatement orlnPrepStmt,	OrderLineOracle order_line) 
	{

		try
		{
			orlnPrepStmt.setInt(1, order_line.ol_w_id);
			orlnPrepStmt.setInt(2, order_line.ol_d_id);
			orlnPrepStmt.setInt(3, order_line.ol_o_id);
			orlnPrepStmt.setInt(4, order_line.ol_number);
			orlnPrepStmt.setLong(5, order_line.ol_i_id);

			Timestamp delivery_d = null;
			if (order_line.ol_delivery_d != null)
			orlnPrepStmt.setString(6, order_line.ol_delivery_d);
			orlnPrepStmt.setDouble(7, order_line.ol_amount);
			orlnPrepStmt.setLong(8, order_line.ol_supply_w_id);
			orlnPrepStmt.setDouble(9, order_line.ol_quantity);
			orlnPrepStmt.setString(10, order_line.ol_dist_info);
			orlnPrepStmt.addBatch();

		}
		catch (SQLException se) 
		{
			throw new RuntimeException(se);
		}

	} 

} 
