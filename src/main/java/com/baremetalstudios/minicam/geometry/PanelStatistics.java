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

import java.util.List;

public class PanelStatistics {

    private Point panelSize;
    private List<Point> outlineDimensions;
    private DrillStatistics drillStats;

    public PanelStatistics(Point panelSize, List<Point> outlineDimensions, DrillStatistics drillStats) {
        this.panelSize = panelSize;
        this.outlineDimensions = outlineDimensions;
        this.drillStats = drillStats;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append(String.format("Panel dimension %.3fmm x %.3fmm\n", panelSize.getX(), panelSize.getY()));
//        int cnt = 0;
//        for (Point point : outlineDimensions) {
//            builder.append(String.format("Board %d %.3fmm x %.3fmm\n", ++cnt, point.getX(), point.getY()));
//        }
        builder.append(String.format("Total: %d boards on the panel\n\n", outlineDimensions.size()));
        builder.append(drillStats.toString());
        return builder.toString();
    }
}
