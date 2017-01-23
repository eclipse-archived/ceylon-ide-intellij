package org.intellij.plugins.ceylon.ide.util;

import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * JBList became parameterized in 2016.3, so to be able to build the
 * plugin with older versions of the IntelliJ SDK, we have to keep
 * an unparameterized version of that class.
 */
public class UnparameterizedJBList extends JBList {

    public UnparameterizedJBList() {
    }

    public UnparameterizedJBList(@NotNull ListModel dataModel) {
        super(dataModel);
    }

    public UnparameterizedJBList(@NotNull Object... listData) {
        super(listData);
    }
}
