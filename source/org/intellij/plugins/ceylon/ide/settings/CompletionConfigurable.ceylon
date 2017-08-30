import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}

import java.awt.event {
    ActionEvent
}

import org.intellij.plugins.ceylon.ide.completion {
    completionSettings
}

class CompletionConfigurable()
        extends AbstractCompletionConfigurable() {

    displayName => "Completion";

    createComponent() => myPanel;

    shared actual void apply() {
        value options = completionSettings.options;
        options.inexactMatches = inexactMatches;
        options.parameterTypesInCompletion = displayParameterTypes.selected;
        options.completionMode = completionMode;
        options.linkedModeArguments = useLinkedMode.selected;
        options.chainLinkedModeArguments = proposeChainCompletions.selected;
        ceylonSettings.highlightedLabels = useColoredLabelsInCheckBox.selected;
    }

    shared actual void reset() {
        value options = completionSettings.options;
        displayParameterTypes.selected = options.parameterTypesInCompletion;
        switch (options.inexactMatches)
        case ("none") {
            super.inexactMatches.setSelected(noArgumentListsRadioButton.model, true);
        }
        case ("positional") {
            super.inexactMatches.setSelected(positionalArgumentListsRadioButton.model, true);
        }
        else {
            super.inexactMatches.setSelected(bothPositionalAndNamedRadioButton.model, true);
        }
        if (options.completionMode == "insert") {
            trailingIdentifier.setSelected(insertsRadioButton.model, true);
        } else {
            trailingIdentifier.setSelected(overwritesRadioButton.model, true);
        }
        useLinkedMode.selected = options.linkedModeArguments;
        proposeChainCompletions.selected = options.chainLinkedModeArguments;
        useColoredLabelsInCheckBox.selected = ceylonSettings.highlightedLabels;
    }

    useLinkedMode.addActionListener((ActionEvent e)
            => proposeChainCompletions.setEnabled(useLinkedMode.selected));

    restoreDefaultsButton.addActionListener((ActionEvent e) {
        completionSettings.loadState(CompletionOptions());
        reset();
    });

    String inexactMatches {
        if (noArgumentListsRadioButton.selected) {
            return "none";
        }
        if (positionalArgumentListsRadioButton.selected) {
            return "positional";
        }
        return "both";
    }

    String completionMode {
        if (insertsRadioButton.selected) {
            return "insert";
        }
        return "overwrite";
    }


    shared actual Boolean modified {
        value options = completionSettings.options;
        return options.parameterTypesInCompletion != displayParameterTypes.selected
            || !options.inexactMatches.equals(inexactMatches)
            || options.chainLinkedModeArguments != proposeChainCompletions.selected
            || !options.completionMode.equals(completionMode)
            || options.linkedModeArguments != useLinkedMode.selected
            || options.chainLinkedModeArguments != proposeChainCompletions.selected
            || ceylonSettings.highlightedLabels != useColoredLabelsInCheckBox.selected;
    }

}