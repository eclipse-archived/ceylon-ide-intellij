package org.intellij.plugins.ceylon.ide.settings;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleText;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class JLabelLinkListener extends MouseAdapter {

    public abstract void onLinkClicked(String href);

    @Override
    public void mouseClicked(MouseEvent e) {
        SimpleAttributeSet link = getLink(e);

        if (link != null) {
            String href = link.getAttribute(HTML.Attribute.HREF).toString();

            if (href != null) {
                onLinkClicked(href);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateCursor(e);
    }

    private void updateCursor(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        if (getLink(e) != null) {
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            label.setCursor(Cursor.getDefaultCursor());
        }
    }

    private SimpleAttributeSet getLink(MouseEvent e) {
        Point point = e.getPoint();
        AccessibleText ctx = (AccessibleText) ((JLabel) e.getSource()).getAccessibleContext();
        int pos = ctx.getIndexAtPoint(point);
        AttributeSet as = ctx.getCharacterAttribute(pos);

        return (SimpleAttributeSet) as.getAttribute(HTML.Tag.A);
    }
}
