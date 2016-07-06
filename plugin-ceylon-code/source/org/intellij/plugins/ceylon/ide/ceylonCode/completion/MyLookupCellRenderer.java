package org.intellij.plugins.ceylon.ide.ceylonCode.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.impl.LookupCellRenderer;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

abstract class MyLookupCellRenderer extends LookupCellRenderer {

    private LookupImpl lookup;

    public MyLookupCellRenderer(LookupImpl lookup) {
        super(lookup);
        this.lookup = lookup;
    }

    public void install() {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Field f = LookupImpl.class.getDeclaredField("myCellRenderer");
                    f.setAccessible(true);
                    f.set(lookup, MyLookupCellRenderer.this);
                    f.setAccessible(false);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
                lookup.getList().setCellRenderer(MyLookupCellRenderer.this);
            }
        });
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
        customize(comp, (LookupElement) value, isSelected);
        return comp;
    }

    abstract void customize(Component comp, LookupElement element, boolean isSelected);

}

