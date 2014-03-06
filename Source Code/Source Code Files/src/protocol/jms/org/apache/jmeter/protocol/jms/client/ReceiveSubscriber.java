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

package org.apache.jmeter.protocol.jms.client;

import java.io.Closeable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.jmeter.protocol.jms.Utils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Generic MessageConsumer class, which has two possible strategies.
 * <ul>
 * <li>Use MessageConsumer.receive(timeout) to fetch messages.</li>
 * <li>Use MessageListener.onMessage() to cache messages in a local queue.</li>
 * </ul>
 * In both cases, the {@link #getMessage(long)} method is used to return the next message,
 * either directly using receive(timeout) or from the queue using poll(timeout).
 */
public class ReceiveSubscriber implements Closeable, MessageListener {

    private static final Logger log = LoggingManager.getLoggerForClass();

    private final Connection connection;

    private final Session session;

    private final MessageConsumer subscriber;

    /*
     * We use a LinkedBlockingQueue (rather than a ConcurrentLinkedQueue) because it has a
     * poll-with-wait method that avoids the need to use a polling loop.
     */
    private final LinkedBlockingQueue<Message> queue;

    /**
     * Constructor takes the necessary JNDI related parameters to create a
     * connection and prepare to begin receiving messages.
     * <br/>
     * The caller must then invoke {@link #start()} to enable message reception.
     * 
     * @param useProps if true, use jndi.properties instead of 
     * initialContextFactory, providerUrl, securityPrincipal, securityCredentials
     * @param initialContextFactory
     * @param providerUrl
     * @param connfactory
     * @param destinationName
     * @param durableSubscriptionId
     * @param clientId
     * @param jmsSelector Message Selector
     * @param useAuth
     * @param securityPrincipal
     * @param securityCredentials
     * @throws JMSException if could not create context or other problem occurred.
     * @throws NamingException 
     */
    public ReceiveSubscriber(boolean useProps, 
            String initialContextFactory, String providerUrl, String connfactory, String destinationName,
            String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
            String securityPrincipal, String securityCredentials) throws NamingException, JMSException {
        this(0, useProps, 
                initialContextFactory, providerUrl, connfactory, destinationName,
                durableSubscriptionId, clientId, jmsSelector, useAuth, 
                securityPrincipal, securityCredentials, false);
    }

    /**
     * Constructor takes the necessary JNDI related parameters to create a
     * connection and create an onMessageListener to prepare to begin receiving messages.
     * <br/>
     * The caller must then invoke {@link #start()} to enable message reception.
     * 
     * @param queueSize maximum queue size <=0 == no limit
     * @param useProps if true, use jndi.properties instead of 
     * initialContextFactory, providerUrl, securityPrincipal, securityCredentials
     * @param initialContextFactory
     * @param providerUrl
     * @param connfactory
     * @param destinationName
     * @param durableSubscriptionId
     * @param clientId
     * @param jmsSelector Message Selector
     * @param useAuth
     * @param securityPrincipal
     * @param securityCredentials
     * @throws JMSException if could not create context or other problem occurred.
     * @throws NamingException 
     */
    public ReceiveSubscriber(int queueSize, boolean useProps, 
            String initialContextFactory, String providerUrl, String connfactory, String destinationName,
            String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
            String securityPrincipal, String securityCredentials) throws NamingException, JMSException {
        this(queueSize,  useProps, 
             initialContextFactory, providerUrl, connfactory, destinationName,
             durableSubscriptionId, clientId, jmsSelector, useAuth, 
             securityPrincipal,  securityCredentials, true);
    }
    
    
    /**
     * Constructor takes the necessary JNDI related parameters to create a
     * connection and create an onMessageListener to prepare to begin receiving messages.
     * <br/>
     * The caller must then invoke {@link #start()} to enable message reception.
     * 
     * @param queueSize maximum queue size <=0 == no limit
     * @param useProps if true, use jndi.properties instead of 
     * initialContextFactory, providerUrl, securityPrincipal, securityCredentials
     * @param initialContextFactory
     * @param providerUrl
     * @param connfactory
     * @param destinationName
     * @param durableSubscriptionId
     * @param clientId
     * @param jmsSelector Message Selector
     * @param useAuth
     * @param securityPrincipal
     * @param securityCredentials
     * @param useMessageListener if true create an onMessageListener to prepare to begin receiving messages, otherwise queue will be null
     * @throws JMSException if could not create context or other problem occurred.
     * @throws NamingException 
     */
    private ReceiveSubscriber(int queueSize, boolean useProps, 
            String initialContextFactory, String providerUrl, String connfactory, String destinationName,
            String durableSubscriptionId, String clientId, String jmsSelector, boolean useAuth, 
            String securityPrincipal, String securityCredentials, boolean useMessageListener) throws NamingException, JMSException {
        boolean initSuccess = false;
        try{
            Context ctx = InitialContextFactory.getContext(useProps, 
                    initialContextFactory, providerUrl, useAuth, securityPrincipal, securityCredentials);
            connection = Utils.getConnection(ctx, connfactory);
            if(!isEmpty(clientId)) {
                connection.setClientID(clientId);
            }
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = Utils.lookupDestination(ctx, destinationName);
            subscriber = createSubscriber(session, dest, durableSubscriptionId, jmsSelector);
            if(useMessageListener) {
                if (queueSize <=0) {
                    queue = new LinkedBlockingQueue<Message>();
                } else {
                    queue = new LinkedBlockingQueue<Message>(queueSize);            
                }
                subscriber.setMessageListener(this);
            } else {
                queue = null;
            }
            log.debug("<init> complete");
            initSuccess = true;
        }
        finally {
            if(!initSuccess) {
                close();
            }
        }
    }
    
    /**
     * Return a simple MessageConsumer or a TopicSubscriber (as a durable subscription)
     * @param session
     * 				JMS session	
     * @param destination
     * 				JMS destination, can be either topic or queue
     * @param durableSubscriptionId 
     * 				If neither empty nor null, this means that a durable 
     * 				subscription will be used
     * @param jmsSelector JMS Selector
     * @return
     * @throws JMSException
     */
    private MessageConsumer createSubscriber(Session session, 
    		Destination destination, String durableSubscriptionId, 
    		String jmsSelector) throws JMSException {
    	if (isEmpty(durableSubscriptionId)) {
    	    if(isEmpty(jmsSelector)) {
    	        return session.createConsumer(destination);
    	    } else {
    	        return session.createConsumer(destination, jmsSelector);
    	    }
        } else {
            if(isEmpty(jmsSelector)) {
                return session.createDurableSubscriber((Topic) destination, durableSubscriptionId); 
            } else {
                return session.createDurableSubscriber((Topic) destination, durableSubscriptionId, jmsSelector, false);                 
            }
        }	
    }

    /**
     * Calls Connection.start() to begin receiving inbound messages.
     * @throws JMSException 
     */
    public void start() throws JMSException {
        log.debug("start()");
        connection.start();
    }

    /**
     * Calls Connection.stop() to stop receiving inbound messages.
     * @throws JMSException 
     */
    public void stop() throws JMSException {
        log.debug("stop()");
        connection.stop();
    }

    /**
     * Get the next message or null.
     * Never blocks for longer than the specified timeout.
     * 
     * @param timeout in milliseconds
     * @return the next message or null
     * 
     * @throws JMSException
     */
    public Message getMessage(long timeout) throws JMSException {
        Message message = null;
        if (queue != null) { // Using onMessage Listener
            try {
                if (timeout < 10) { // Allow for short/negative times
                    message = queue.poll();                    
                } else {
                    message = queue.poll(timeout, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                // Ignored
            }
            return message;
        }
        if (timeout < 10) { // Allow for short/negative times
            message = subscriber.receiveNoWait();                
        } else {
            message = subscriber.receive(timeout);
        }
        return message;
    }
    /**
     * close() will stop the connection first. 
     * Then it closes the subscriber, session and connection.
     */
    @Override
    public void close() { // called from threadFinished() thread
        log.debug("close()");
        try {
            if(connection != null) {
                connection.stop();
            }
        } catch (JMSException e) {
            log.error(e.getMessage());
        }
        Utils.close(subscriber, log);
        Utils.close(session, log);
        Utils.close(connection, log);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(Message message) {
        if (!queue.offer(message)){
            log.warn("Could not add message to queue");
        }
    }
    
    
    /**
     * Checks whether string is empty
     * 
     * @param s1
     * @return True if input is null, an empty string, 
     * 				or a white space-only string
     */
    private boolean isEmpty(String s1) {
    	return (s1 == null || s1.trim().equals(""));
    }
}
