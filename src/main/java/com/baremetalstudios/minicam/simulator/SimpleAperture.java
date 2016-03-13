/*******************************************************************************
 * Copyright 2014, 2015 Sergiy Yevtushenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.baremetalstudios.minicam.simulator;

import java.util.Iterator;
import java.util.List;

public class SimpleAperture implements Aperture {
    private final int num;
    private final List<Double> modifiers;
    private final ApertureType type;

    public SimpleAperture(ApertureType type, int num, List<Double> modifiers) {
        this.type = type;
        this.num = num;
        this.modifiers = modifiers;
    }
    
    @Override
    public int getId() {
        return num;
    }
    
    @Override
    public ApertureType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(PlotterMode mode) {
        return type.name().toLowerCase() + extraText() + " " + num + " (" + join(modifiers, ",") + ")";
    }

    protected String extraText() {
        return "";
    }
    
    private static<T> String join(Iterable<T> iterable, String delimiter) {
        final Iterator<T> iterator = iterable.iterator();
        final T first = iterator.next();
        if (!iterator.hasNext()) {
            return (first == null) ? "null" : first.toString(); 
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            buf.append(delimiter);
            final T obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }

        return buf.toString();
    }
}
