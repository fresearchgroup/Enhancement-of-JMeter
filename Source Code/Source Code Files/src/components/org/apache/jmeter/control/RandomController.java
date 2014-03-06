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

package org.apache.jmeter.control;

import java.io.Serializable;
import java.util.Random;

public class RandomController extends InterleaveControl implements Serializable {
    private static final long serialVersionUID = 240L;

    private static final Random RAND = new Random();

    public RandomController() {
    }

    /**
     * @see org.apache.jmeter.control.GenericController#resetCurrent()
     */
    @Override
    protected void resetCurrent() {
        if (getSubControllers().size() > 0) {
            current = RAND.nextInt(this.getSubControllers().size());
        } else {
            current = 0;
        }
    }

    /**
     * @see org.apache.jmeter.control.GenericController#incrementCurrent()
     */
    @Override
    protected void incrementCurrent() {
        super.incrementCurrent();
        current = RAND.nextInt(this.getSubControllers().size());
    }

}
