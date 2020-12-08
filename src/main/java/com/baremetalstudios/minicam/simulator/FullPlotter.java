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
package com.baremetalstudios.minicam.simulator;

public class FullPlotter extends AbstractPlotter {

    @Override
    public void setCenter(String string, String string2) {
        System.out.format("setCenter(%s, %s)\n", string, string2);
    }

    @Override
    public void setPosition(String string, String string2) {
        System.out.format("setPosition(%s, %s)\n", string, string2);
    }

    @Override
    public void done() {
        System.out.format("done()\n");
    }

    @Override
    public void setExposure(ExposureMode mode) {
        System.out.format("setExposure(%s)\n", mode);
        super.setExposure(mode);
    }

    @Override
    public void setMode(PlotterMode mode) {
        System.out.format("setMode(%s)\n", mode);
        super.setMode(mode);
    }

    @Override
    public void resetMode(PlotterMode mode) {
        System.out.format("resetMode(%s)\n", mode);
        super.resetMode(mode);
    }

    @Override
    public void comment(int beginLine, String image) {
        System.out.format("comment(%d, %s)\n", beginLine, image);
        super.comment(beginLine, image);
    }

    @Override
    public void addFlash() {
        System.out.format("addFlash()\n");
        super.addFlash();
    }

    @Override
    public void setAperture(int parseInt) {
        System.out.format("setAperture(%d)\n", parseInt);
        super.setAperture(parseInt);
    }

    @Override
    public void setFormatX(int parseInt, int parseInt2) {
        System.out.format("setFormatX(%d, %d)\n", parseInt, parseInt2);
        super.setFormatX(parseInt, parseInt2);
    }

    @Override
    public void setFormatY(int parseInt, int parseInt2) {
        System.out.format("setFormatY(%s, %s)\n", parseInt, parseInt2);
        super.setFormatY(parseInt, parseInt2);
    }

    @Override
    public ApertureMacro getMacro(String id) {
        System.out.format("getMacro(%s)\n", id);
        return super.getMacro(id);
    }

    @Override
    public void addAperture(Aperture aperture) {
        System.out.format("addAperture(%s)\n", aperture);
        super.addAperture(aperture);
    }

    @Override
    public void addMacro(ApertureMacro macro) {
        System.out.format("addMacro(%s)\n", macro);
        super.addMacro(macro);
    }

    @Override
    public void selectAxis(String image, String image2) {
        System.out.format("selectAxis(%s, %s)\n", image, image2);
    }

    @Override
    public void setImagePolarity(String image) {
        System.out.format("setImagePolarity(%s)\n", image);
    }

    @Override
    public void setLayerPolarity(String image) {
        System.out.format("setLayerPolarity(%s)\n", image);
    }

    @Override
    public void setOffset(String a, String b) {
        System.out.format("setOffset(%s, %s)\n", a, b);
    }

    @Override
    public void setScaleFactor(String a, String b) {
        System.out.format("setScaleFactor(%s, %s)\n", a, b);
    }

    @Override
    public void stepAndRepeat(String x, String y, String i, String j) {
        System.out.format("stepAndRepeat(%s, %s, %s, %s)\n", x, y, i, j);
    }
}
