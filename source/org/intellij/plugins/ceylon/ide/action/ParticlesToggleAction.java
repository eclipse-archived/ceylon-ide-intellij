package org.intellij.plugins.ceylon.ide.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.intellij.plugins.ceylon.ide.editor.Particles;

public class ParticlesToggleAction extends ToggleAction {
    @Override
    public boolean isSelected(AnActionEvent e) {
        return Particles.ENABLED;
    }

    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        Particles.ENABLED = state;
    }
}
