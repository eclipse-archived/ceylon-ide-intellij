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