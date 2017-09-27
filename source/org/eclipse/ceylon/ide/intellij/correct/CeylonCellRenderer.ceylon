import com.intellij.ui {
    ColoredListCellRenderer,
    SimpleTextAttributes
}

import java.awt {
    ...
}

import javax.swing {
    ...
}

shared class CeylonListItem(icon, color, label, qualifier, extraQualifier, payload) {
    shared Icon? icon;
    shared String label;
    shared String? qualifier;
    shared String? extraQualifier;
    shared Object payload;
    shared Color color;
}

shared class CeylonCellRenderer()
        extends ColoredListCellRenderer<Object>() {

    setToolTipText(String s) => toolTipText = s;

    shared actual void customizeCellRenderer(JList<out Object> list,
            Object item, Integer index, Boolean selected, Boolean hasFocus) {

        assert (is CeylonListItem item);

        this.setIcon(item.icon);

        if (selected) {
            this.append(item.label);
            if (exists qualifier = item.qualifier) {
                this.append(" (``qualifier``)");
            }
        } else {
            this.append(item.label, SimpleTextAttributes(SimpleTextAttributes.stylePlain, item.color));
            if (exists qualifier = item.qualifier) {
                this.append(" (``qualifier``)", SimpleTextAttributes.grayedAttributes);
            }
        }
    }
}
