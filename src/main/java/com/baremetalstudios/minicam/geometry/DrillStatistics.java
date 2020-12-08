/*******************************************************************************
 * Copyright (c) 2014, 2015, 2020 Sergiy Yevtushenko
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
package com.baremetalstudios.minicam.geometry;

import java.util.ArrayList;
import java.util.List;

import com.baremetalstudios.minicam.processor.OutputGenerator;

public class DrillStatistics {
    private int total = 0;
    private List<SingleDrill> list = new ArrayList<SingleDrill>();

    public void add(double d, int size, double pathLenght) {
        total += size;
        list.add(new SingleDrill(d, size, pathLenght));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        double totalLen = 0;

        for (SingleDrill drill : list) {
            double len = drill.getPathLenght();
            result.append(String.format("Drill %.2f (%.3fmm extra depth), %4d drills, path lenght %.02f\n",
                            drill.getDiameter(), getAdjustmentLength(drill.getDiameter()), drill.getCount(), len));
            totalLen += len;
        }
        result.append(String.format("Total: %d drills, %.02fmm tool path\n", total, totalLen));

        return result.toString();
    }

    private double getAdjustmentLength(double diameter) {
		return diameter/2 * OutputGenerator.DRILL_ADJUST_SCALER;
	}

	private static class SingleDrill {
        private final double diameter;
        private final int count;
        private final double pathLenght;

        public SingleDrill(double diameter, int count, double pathLenght) {
            this.diameter = diameter;
            this.count = count;
            this.pathLenght = pathLenght;
        }

        public double getDiameter() {
            return diameter;
        }

        public int getCount() {
            return count;
        }

        public double getPathLenght() {
            return pathLenght;
        }
    }
}
