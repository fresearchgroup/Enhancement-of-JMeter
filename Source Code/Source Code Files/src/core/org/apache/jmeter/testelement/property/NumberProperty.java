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

/*
 * Created on May 5, 2003
 */
package org.apache.jmeter.testelement.property;

public abstract class NumberProperty extends AbstractProperty {
    private static final long serialVersionUID = 240L;

    public NumberProperty() {
        super();
    }

    public NumberProperty(String name) {
        super(name);
    }

    /**
     * Set the value of the property with a Number object.
     */
    protected abstract void setNumberValue(Number n);

    /**
     * Set the value of the property with a String object.
     */
    protected abstract void setNumberValue(String n) throws NumberFormatException;

    @Override
    public void setObjectValue(Object v) {
        if (v instanceof Number) {
            setNumberValue((Number) v);
        } else {
            try {
                setNumberValue(v.toString());
            } catch (RuntimeException e) {
            }
        }
    }

    /**
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(JMeterProperty arg0) {
        double compareValue = getDoubleValue() - arg0.getDoubleValue();

        if (compareValue < 0) {
            return -1;
        } else if (compareValue == 0) {
            return 0;
        } else {
            return 1;
        }
    }
}
