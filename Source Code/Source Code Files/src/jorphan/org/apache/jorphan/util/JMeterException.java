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

package org.apache.jorphan.util;

/**
 * The rationale for this class was originally to support chained Exceptions in JDK 1.3
 * However, the class is now used in its own right.
 * 
 * @version $Revision: 905024 $
 */
public class JMeterException extends Exception {

    private static final long serialVersionUID = 240L;

    public JMeterException() {
        super();
    }

    public JMeterException(String s) {
        super(s);
    }

    public JMeterException(Throwable cause) {
        super(cause);
    }

    public JMeterException(String message, Throwable cause) {
        super(message, cause);
    }
}
