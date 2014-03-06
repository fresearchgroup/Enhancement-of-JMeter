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

package org.apache.jmeter.protocol.http.modifier;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML Parseing errors for XML parameters file are handled here.
 *
 */
public class UserParameterXMLErrorHandler implements ErrorHandler {
    private static final Logger log = LoggingManager.getLoggerForClass();

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        log.warn("**Parsing Warning**\n" + "  line:    " + exception.getLineNumber() + "\n" + "  URI:    :"
                + exception.getSystemId() + "\n" + "  Message: " + exception.getMessage());
        throw new SAXException("Warning encountered");
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        log.error("**Parsing Warning**\n" + "  line:    " + exception.getLineNumber() + "\n" + "  URI:    :"
                + exception.getSystemId() + "\n" + "  Message: " + exception.getMessage());
        throw new SAXException("Error encountered");
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        log.error("**Parsing Warning**\n" + "  line:    " + exception.getLineNumber() + "\n" + "  URI:    :"
                + exception.getSystemId() + "\n" + "  Message: " + exception.getMessage());
        throw new SAXException("Fatal Error encountered");
    }
}
