/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.jmeter.timers;

import java.io.Serializable;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.threads.JMeterContextService;

/**
 * The purpose of the SyncTimer is to block threads until X number of threads
 * have been blocked, and then they are all released at once. A SyncTimer can
 * thus create large instant loads at various points of the test plan.
 *
 */
public class SyncTimer extends AbstractTestElement implements Timer, Serializable, TestBean, TestStateListener, ThreadListener {
	
	/**
	 * Wrapper to {@link CyclicBarrier} to allow lazy init of CyclicBarrier when SyncTimer is configured with 0
	 */
	private static final class BarrierWrapper implements Cloneable {

		private CyclicBarrier barrier;

		/**
		 * 
		 */
		public BarrierWrapper() {
			this.barrier = null;
		}
		
		/**
		 * @param parties Number of parties
		 */
		public BarrierWrapper(int parties) {
			this.barrier = new CyclicBarrier(parties);
		}
		
		/**
		 * Synchronized is required to ensure CyclicBarrier is initialized only once per Thread Group
		 * @param parties Number of parties
		 */
		public synchronized void setup(int parties) {
			if(this.barrier== null) {
				this.barrier = new CyclicBarrier(parties);
			}
		}
		
		/**
		 * @see CyclicBarrier#await()
		 * @return int 
		 * @throws InterruptedException
		 * @throws BrokenBarrierException
		 * @see java.util.concurrent.CyclicBarrier#await()
		 */
		public int await() throws InterruptedException, BrokenBarrierException {
			return barrier.await();
		}

		/**
		 * @see java.util.concurrent.CyclicBarrier#reset()
		 */
		public void reset() {
			barrier.reset();
		}

		/**
		 * @see java.lang.Object#clone()
		 */
		@Override
		protected Object clone()  {
			BarrierWrapper barrierWrapper=  null;
			try {
				barrierWrapper = (BarrierWrapper) super.clone();
				barrierWrapper.barrier = this.barrier;
			} catch (CloneNotSupportedException e) {
				//Cannot happen
			}
			return barrierWrapper;
		}	
	}
	
    private static final long serialVersionUID = 2;
    
    private transient BarrierWrapper barrier;

    private int groupSize;

    // Ensure transient object is created by the server
    private Object readResolve(){
    	createBarrier();
        return this;
    }

    /**
     * @return Returns the numThreads.
     */
    public int getGroupSize() {
        return groupSize;
    }

    /**
     * @param numThreads
     *            The numThreads to set.
     */
    public void setGroupSize(int numThreads) {
        this.groupSize = numThreads;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long delay() {
    	if(getGroupSize()>=0) {
    		int arrival = 0;
    		try {
				arrival = this.barrier.await();
			} catch (InterruptedException e) {
				return 0;
			} catch (BrokenBarrierException e) {
				return 0;
			} finally {
				if(arrival == 0) {
					barrier.reset();
				}
			}
    	}
        return 0;
    }

    /**
     * We have to control the cloning process because we need some cross-thread
     * communication if our synctimers are to be able to determine when to block
     * and when to release.
     */
    @Override
    public Object clone() {
        SyncTimer newTimer = (SyncTimer) super.clone();
        newTimer.barrier = barrier;
        return newTimer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void testEnded() {
        this.testEnded(null);        
    }

    /**
     * Reset timerCounter
     */
    @Override
    public void testEnded(String host) {
    	createBarrier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void testStarted() {
        testStarted(null);
    }

    /**
     * Reset timerCounter
     */
    @Override
    public void testStarted(String host) {
        createBarrier();
    }

	/**
	 * 
	 */
	private void createBarrier() {
		if(getGroupSize() == 0) {
			// Lazy init 
			this.barrier = new BarrierWrapper();
        } else {
        	this.barrier = new BarrierWrapper(getGroupSize());
        }
	}

	@Override
    public void threadStarted() {
		if(getGroupSize() == 0) {
	        int numThreadsInGroup = JMeterContextService.getContext().getThreadGroup().getNumThreads();
			// Unique Barrier creation ensured by synchronized setup
			this.barrier.setup(numThreadsInGroup);
        }
	}

	@Override
    public void threadFinished() {
		// NOOP
	}
}