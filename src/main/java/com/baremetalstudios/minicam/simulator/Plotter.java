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

public interface Plotter {
    boolean askContinue(int beginLine, int i);

    void setMode(PlotterMode mode);
    void resetMode(PlotterMode mode);

    void comment(int beginLine, String image);

    void addFlash();

    void setCenter(String string, String string2);

    void setPosition(String string, String string2);

    void setExposure(ExposureMode mode);

    void setAperture(int parseInt);

    void setFormatX(int parseInt, int parseInt2);

    void setFormatY(int parseInt, int parseInt2);

    void addAperture(Aperture aperture);

    ApertureMacro getMacro(String type);

    void addMacro(ApertureMacro macro);

    void selectAxis(String image, String image2);

    void setImagePolarity(String image);

    void setLayerPolarity(String image);

    void setOffset(String a, String b);

    void setScaleFactor(String a, String b);

    void stepAndRepeat(String x, String y, String i, String j);
    
    void done();
}
