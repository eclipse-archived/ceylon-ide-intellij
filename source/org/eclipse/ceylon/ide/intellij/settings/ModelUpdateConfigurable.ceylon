/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
class ModelUpdateConfigurable()
        extends AbstractModelUpdateConfigurable() {

    displayName => "Model update";

    createComponent() => myPanel;

    function parse(String text) => parseInteger(text) else 0;

    shared actual void apply() {
        ceylonSettings.autoUpdateInterval = parse(modelUpdateDelay.text) * 1000;
        ceylonSettings.modelUpdateTimeoutMinutes = parse(modelUpdateTimeoutMinutes.text);
        ceylonSettings.lowerModelUpdatePriority = decreaseThePriorityOfCheckBox.selected;
    }

    shared actual void reset() {
        modelUpdateDelay.text = (ceylonSettings.autoUpdateInterval / 1000).string;
        modelUpdateTimeoutMinutes.text = (ceylonSettings.modelUpdateTimeoutMinutes).string;
        decreaseThePriorityOfCheckBox.selected = ceylonSettings.lowerModelUpdatePriority;
    }

    shared actual Boolean modified {
        return ceylonSettings.autoUpdateInterval != parse(modelUpdateDelay.text) * 1000
            || ceylonSettings.modelUpdateTimeoutMinutes != parse(modelUpdateTimeoutMinutes.text)
            || ceylonSettings.lowerModelUpdatePriority != decreaseThePriorityOfCheckBox.selected;
    }

}