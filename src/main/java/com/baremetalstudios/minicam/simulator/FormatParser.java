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

import com.baremetalstudios.minicam.parser.ParseException;

public class FormatParser {
    public static final double IMPERIAL_SCALE = 25.4;
    public static final double METRIC_SCALE = 1.0;

    private int trailingDigits;
    private int totalLength;
    private double scale = IMPERIAL_SCALE;

    public void setFormat(int leadingDigits, int trailingDigits) {
        this.trailingDigits = trailingDigits;
        this.totalLength = leadingDigits + trailingDigits;
    }

    public double parse(String text) {
        if (text.length() != totalLength) {
            throw new ParseException("Invalid value " + text);
        }

        Double value = Double.valueOf(text);
        for (int i = 0; i < trailingDigits; i++) {
            value /= 10.0;
        }

        return value*scale;
    }

    public void setModeImperial() {
        scale = IMPERIAL_SCALE;
    }

    public void setModeMetric() {
        scale = METRIC_SCALE;
    }
}
