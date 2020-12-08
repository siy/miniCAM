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
package com.baremetalstudios.minicam.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;

public class DrillProcessor {
    private OutputConfig config;
    private Comparator<DrillGroup> comparator = new Comparator<DrillGroup>() {
        @Override
        public int compare(DrillGroup o1, DrillGroup o2) {
            return (int) Math.signum(o1.getDiameter() - o2.getDiameter());
        }
    };

    public DrillProcessor(OutputConfig config) {
        this.config = config;
    }

    public List<DrillGroup> process(List<DrillGroup> drills) {
        drills.sort(comparator);
        List<DrillGroup> newDrillGroups = mergeCloseDiameters(drills);
        snapDiameters(newDrillGroups);
        newDrillGroups.sort(comparator);
        return newDrillGroups;
    }

    private void snapDiameters(List<DrillGroup> drills) {
        for (DrillGroup group : drills) {
            group.setDiameter(snapDiameters(group.getDiameter()));
        }
    }

    private double snapDiameters(double diameter) {
        double step = config.getDrillDiameterStep();
        long inSteps = Math.round(diameter/step);
        return step * inSteps;
    }

    private List<DrillGroup> mergeCloseDiameters(List<DrillGroup> drills) {
        List<DrillGroup> newDrillGroups = new ArrayList<>();

        while (!drills.isEmpty()) {
            if (drills.size() == 1) {
                newDrillGroups.add(drills.remove(0));
            } else {
                DrillGroup group1 = drills.get(0);
                DrillGroup group2 = drills.get(1);

                double diamDelta = (group2.getDiameter() - group1.getDiameter());
                if(diamDelta < config.getDrillDiameterStep()/2) {
                    //merge two groups
                    List<Point> list = new ArrayList<>();
                    list.addAll(group1.getDrills());
                    list.addAll(group2.getDrills());
                    double newDiameter = (group1.getDiameter() + group2.getDiameter())/2;
                    newDrillGroups.add(new DrillGroup(group1.getId(), newDiameter, list));
                    drills.remove(0);
                    drills.remove(0);
                } else {
                    newDrillGroups.add(drills.remove(0));
                }
            }
        }
        return newDrillGroups;
    }

    public List<DrillGroup> optimize(List<DrillGroup> drills) {
        for (DrillGroup group : drills) {
            group.optimize(config);
        }
        return drills;
    }

    public OutputConfig getConfig() {
        return config;
    }
}
