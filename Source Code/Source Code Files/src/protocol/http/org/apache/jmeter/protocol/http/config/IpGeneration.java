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

package org.apache.jmeter.protocol.http.config;

import java.io.IOException;
import java.util.*;

import org.apache.jmeter.util.JMeterUtils;

public class IpGeneration {
	
	public static String IP_ADDRESS = "IpGeneration.ipAddress";

	public static String MASK = "IpGeneration.mask";

	public static String NUM_IP = "IpGeneration.numIp";
	
	public static ArrayList<String> ADDRESSES = new ArrayList<String>();
	
	public static void generate() {
		int index;
		String lastOctet;
		String fixedOctet;
		String newAddr;
		Integer addr;
		int count = 0;
		try {
			int numAdd = Integer.parseInt(NUM_IP);
			if(IP_ADDRESS != null)
			{
				index = IP_ADDRESS.lastIndexOf(".");
				lastOctet = IP_ADDRESS.substring(index+1);
				fixedOctet = IP_ADDRESS.substring(0,index+1);
				//System.out.println(lastOctet+"\t"+fixedOctet);
				//System.out.println(lastOctet);
				addr = Integer.parseInt(lastOctet);
				ADDRESSES.add(IP_ADDRESS);
				count++;
				while(addr < 255 && count < numAdd)
				{
					addr++;
					newAddr = fixedOctet + addr.toString();
					//System.out.println(newAddr + "\n");
					ADDRESSES.add(newAddr);
					count++;					
				}
				System.out.println(ADDRESSES);
				addIpAliases();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void addIpAliases() {
		Iterator itr = ADDRESSES.iterator();
		String address;
		Integer count = 1;
		while(itr.hasNext()) {
			address = itr.next().toString();
			String[] command = {"./aliases", count.toString(), address};
			ProcessBuilder proBuilder = new ProcessBuilder(command);
			try {
				Process process = proBuilder.start();
				int exitValue = process.waitFor();
				System.out.println("\nExit Value : " + exitValue);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			count++;
		}
		System.out.println("ADDED IP ALIASES : " + count);
	}	
}