package org.intellij.plugins.ceylon.ide.project;

import ceylon.interop.java.CeylonStringIterable;
import ceylon.language.Iterable;
import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.TableUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.redhat.ceylon.compiler.typechecker.analyzer.Warning;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.ceylonSettings_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static ceylon.interop.java.createJavaStringArray_.createJavaStringArray;
import static com.intellij.ui.ScrollPaneFactory.createScrollPane;
import static java.util.Arrays.asList;

public class PageOne implements CeylonConfigForm {
    private JCheckBox compileForJvm;
    private JCheckBox compileToJs;
    private JPanel panel;
    private JCheckBox workOffline;
    private JPanel warningsPanel;
    private RawCommandLineEditor javacOptions;
    private JBTable table;
    private List<Warning> warnings;

    public PageOne() {
        String defaultVm = ceylonSettings_.get_().getDefaultTargetVm();
        compileForJvm.setSelected(!defaultVm.equals("js"));
        compileToJs.setSelected(!defaultVm.equals("jvm"));
        table = new JBTable();
        table.setShowGrid(false);
        JScrollPane installedScrollPane = createScrollPane(
                table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        warningsPanel.add(installedScrollPane, BorderLayout.CENTER);
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void apply(IdeaCeylonProject project) {
        project.getIdeConfiguration().setCompileToJvm(ceylon.language.Boolean.instance(compileForJvm.isSelected()));
        project.getIdeConfiguration().setCompileToJs(ceylon.language.Boolean.instance(compileToJs.isSelected()));
        project.getConfiguration().setProjectOffline(ceylon.language.Boolean.instance(workOffline.isSelected()));
        EnumSet<Warning> enumSet = warnings.isEmpty() ? EnumSet.noneOf(Warning.class) : EnumSet.copyOf(warnings);
        project.getConfiguration().setProjectSuppressWarningsEnum(enumSet);

        List<String> opts = ParametersListUtil.DEFAULT_LINE_PARSER.fun(javacOptions.getText());
        project.getConfiguration().setJavacOptions(new CeylonStringIterable(opts));
    }

    @Override
    public boolean isModified(IdeaCeylonProject project) {
        return project.getIdeConfiguration().getCompileToJvm() == null
                || project.getIdeConfiguration().getCompileToJs() == null
                || project.getConfiguration().getProjectOffline() == null
                || project.getIdeConfiguration().getCompileToJvm().booleanValue() != compileForJvm.isSelected()
                || project.getIdeConfiguration().getCompileToJs().booleanValue() != compileToJs.isSelected()
                || project.getConfiguration().getProjectOffline().booleanValue() != workOffline.isSelected()
                // TODO || warningsAsString(project.getConfiguration().getSuppressWarningsEnum()) != suppressedWarnings.getText()
                || !getJavacOpts(project).equals(javacOptions.getText())
                ;
    }

    @Override
    public void load(IdeaCeylonProject project) {
        String defaultVm = ceylonSettings_.get_().getDefaultTargetVm();
        compileForJvm.setSelected(safeNullBoolean(project.getIdeConfiguration().getCompileToJvm(), !defaultVm.equals("js")));
        compileToJs.setSelected(safeNullBoolean(project.getIdeConfiguration().getCompileToJs(), !defaultVm.equals("jvm")));
        workOffline.setSelected(safeNullBoolean(project.getConfiguration().getProjectOffline(), false));

        warnings = new ArrayList<>(project.getConfiguration().getSuppressWarningsEnum());
        createTableModel(warnings);

        javacOptions.setText(getJavacOpts(project));
    }

    @NotNull
    private String getJavacOpts(IdeaCeylonProject project) {
        Iterable<? extends ceylon.language.String, ?> it = project.getConfiguration().getJavacOptions();
        List<String> opts;
        if (it != null) {
            opts = asList(createJavaStringArray(it));
        } else {
            opts = Collections.emptyList();
        }
        return ParametersListUtil.DEFAULT_LINE_JOINER.fun(opts);
    }

    private void createTableModel(final List<Warning> warnings) {
        ListTableModel<Warning> model = new ListTableModel<>(
                new ColumnInfo[]{
                        new ColumnInfo<Warning, String>("Suppressed warnings") {
                            @Nullable
                            @Override
                            public String valueOf(Warning o) {
                                return o.getDescription();
                            }
                        },
                        new ColumnInfo<Warning, Boolean>("") {
                            @Nullable
                            @Override
                            public Boolean valueOf(Warning o) {
                                return warnings.contains(o);
                            }

                            @Override
                            public void setValue(Warning warning, Boolean value) {
                                if (value == Boolean.TRUE) {
                                    if (!warnings.contains(warning)) {
                                        warnings.add(warning);
                                    }
                                } else {
                                    warnings.remove(warning);
                                }
                            }

                            @Override
                            public boolean isCellEditable(Warning warning) {
                                return true;
                            }

                            @Override
                            public Class<?> getColumnClass() {
                                return Boolean.class;
                            }

                            @Nullable
                            @Override
                            public TableCellEditor getEditor(Warning warning) {
                                return new BooleanTableCellEditor();
                            }

                            @Override
                            public int getWidth(JTable table) {
                                return new JCheckBox().getPreferredSize().width;
                            }
                        }
                },
                asList(Warning.values())
        );
        table.setModel(model);
        table.setTableHeader(null);
        table.setRowSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        for (int i = 0; i < model.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            final ColumnInfo columnInfo = model.getColumnInfos()[i];
            column.setCellEditor(columnInfo.getEditor(null));
            if (columnInfo.getColumnClass() == Boolean.class) {
                TableUtil.setupCheckboxColumn(column);
            }
        }
    }

    private boolean safeNullBoolean(ceylon.language.Boolean bool, boolean def) {
        return bool == null ? def : bool.booleanValue();
    }
}
