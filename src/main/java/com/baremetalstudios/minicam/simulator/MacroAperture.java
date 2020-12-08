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

import java.util.List;

public class MacroAperture extends SimpleAperture {
    private final ApertureMacro macro;

    public MacroAperture(int num, ApertureMacro macro, List<Double> modifiers) {
        super(ApertureType.MACRO, num, modifiers);
        this.macro = macro;
    }
    
    @Override
    protected String extraText() {
     
        return macro.toString();
    }
}
