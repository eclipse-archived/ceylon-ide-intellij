package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.*;
import com.redhat.ceylon.model.typechecker.model.Package;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlighter_;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModule;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RunnableChooserDialog extends DialogWrapper {
    private final IdeaModule module;
    private JPanel contentPane;
    private JList<Declaration> runnableList;

    RunnableChooserDialog(@Nullable final Project project, IdeaModule module) {
        super(project, false);
        init();
        setTitle("Select a Runnable Declaration");

        this.module = module;

        runnableList.setModel(createModel());
        runnableList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component cmp = super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
                if (value instanceof Declaration) {
                    setIcon(icons_.get_().forDeclaration(value));
                    setText("<html>" + highlighter_.get_().highlight(getText(), project) + "</html>");
                }
                return cmp;
            }
        });
        setOKActionEnabled(false);
        runnableList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                setOKActionEnabled(runnableList.getSelectedValue() != null);
            }
        });
        runnableList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Rectangle r = runnableList.getCellBounds(0, runnableList.getLastVisibleIndex());
                    if (r != null && r.contains(e.getPoint())) {
                        int index = runnableList.locationToIndex(e.getPoint());
                        if (index != -1) {
                            doOKAction();
                        }
                    }
                }
            }
        });
    }

    @Nullable
    Declaration getSelectedDeclaration() {
        return runnableList.getSelectedValue();
    }

    @NotNull
    private DefaultListModel<Declaration> createModel() {
        DefaultListModel<Declaration> model = new DefaultListModel<>();

        for (Package pkg : module.getPackages()) {
            for (Declaration declaration : pkg.getMembers()) {
                if (declaration.isToplevel()
                        && declaration.isShared()
                        && isRunnable(declaration)) {
                    model.addElement(declaration);
                }
            }
        }

        return model;
    }

    private boolean isRunnable(Declaration decl) {
        if (decl instanceof Class) {
            Class cls = (Class) decl;
            if (cls.isObjectClass()) {
                return false;
            }
            ParameterList plist = cls.getParameterList();
            return plist == null || plist.getParameters().size() == 0;
        } else if (decl instanceof Function) {
            Function fun = (Function) decl;
            if (fun.getParameterLists().size() == 1) {
                return fun.getFirstParameterList().getParameters().size() == 0;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return runnableList;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
