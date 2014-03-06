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

package org.apache.jmeter.swing;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Implements an HTML Pane with local hyperlinking enabled.
 */
public class HtmlPane extends JTextPane {
    private static final long serialVersionUID = 240L;

    private static final Logger log = LoggingManager.getLoggerForClass();

    public HtmlPane() {
        this.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    String ref = e.getURL().getRef();
                    if (ref != null && ref.length() > 0) {
                        log.debug("reference to scroll to = " + ref);
                        scrollToReference(ref);
                    }
                }
            }
        });
    }
}
