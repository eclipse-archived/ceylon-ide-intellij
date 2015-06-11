package org.intellij.plugins.ceylon.ide.debugger;

import com.intellij.debugger.PositionManager;
import com.intellij.debugger.PositionManagerFactory;
import com.intellij.debugger.engine.DebugProcess;
import org.jetbrains.annotations.Nullable;

public class CeylonPositionManagerFactory extends PositionManagerFactory {
    @Nullable
    @Override
    public PositionManager createPositionManager(DebugProcess process) {
        return new CeylonPositionManager(process);
    }
}
