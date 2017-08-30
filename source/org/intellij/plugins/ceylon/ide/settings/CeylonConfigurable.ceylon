import com.intellij.ide {
    DataManager
}
import com.intellij.openapi.options.ex {
    Settings
}

import java.awt.event {
    ActionEvent
}

shared class CeylonConfigurable() extends AbstractCeylonConfigurable() {

    displayName => "Ceylon";

    void selectConfigurable(String id) {
        value context = DataManager.instance.dataContextFromFocus.result;
        if (exists context,
            exists settings = Settings.key.getData(context)) {
            settings.select(settings.find(id));
        }
    }

    void setupLinks() {
        value listener = object extends JLabelLinkListener() {
            shared actual Null onLinkClicked(String href) {
                selectConfigurable(href);
                return null;
            }
        };

        for (link in [editorLink, completionLink, compilerLink]) {
            link.addMouseListener(listener);
            link.addMouseMotionListener(listener);
        }
    }

    shared actual void reset() {
        value settings = ceylonSettings;
        switch (settings.defaultTargetVm)
        case ("jvm") {
            targetVM.setSelected(jvmTarget.model, true);
        }
        case ("js") {
            targetVM.setSelected(jsTarget.model, true);
        }
        else {
            targetVM.setSelected(crossTarget.model, true);
        }
        sourceName.text = settings.defaultSourceFolder;
        resourceName.text = settings.defaultResourceFolder;
    }

    setupLinks();

    restoreDefaultsButton.addActionListener((ActionEvent e) {
        ceylonSettings.loadState(CeylonOptions());
        reset();
    });

    createComponent() => myPanel;

    String targetVm {
        if (jvmTarget.selected) {
            return "jvm";
        } else if (jsTarget.selected) {
            return "js";
        }
        else {
            return "cross";
        }
    }

    shared actual void apply() {
        ceylonSettings.defaultTargetVm = targetVm;
        ceylonSettings.defaultSourceFolder = sourceName.text;
        ceylonSettings.defaultResourceFolder = resourceName.text;
    }

    modified => !ceylonSettings.defaultTargetVm == targetVm
             || !ceylonSettings.defaultSourceFolder == sourceName.text
             || !ceylonSettings.defaultResourceFolder == resourceName.text;

}