package org.intellij.plugins.ceylon.ide.ceylonCode.correct;

import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;
import java.awt.*;


public class CeylonCellRenderer extends ColoredListCellRenderer {

    public static class Item {
        public Icon icon;

        public Item(Icon icon, Color color, String label, String qualifier, Object payload) {
            this.icon = icon;
            this.color = color;
            this.label = label;
            this.qualifier = qualifier;
            this.payload = payload;
        }

        public String label;
        public String qualifier;
        public Object payload;
        public Color color;
    }

    public void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {

        Item item = (Item) value;
        this.setIcon(item.icon);
        if (selected) {
            this.append(item.label);
            if (item.qualifier != null) {
                this.append(" (" + item.qualifier + ")");
            }
        } else {
            this.append(item.label,
                    new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, item.color));
            if (item.qualifier != null) {
                this.append(" (" + item.qualifier + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
            }
        }
    }
}
