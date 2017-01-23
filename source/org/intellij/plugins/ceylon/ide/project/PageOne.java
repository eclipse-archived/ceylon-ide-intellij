package org.intellij.plugins.ceylon.ide.project;

import ceylon.interop.java.CeylonStringIterable;
import ceylon.language.Iterable;
import com.intellij.ui.BooleanTableCellEditor;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.TableUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.redhat.ceylon.compiler.typechecker.analyzer.Warning;
import org.intellij.plugins.ceylon.ide.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.settings.ceylonSettings_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;
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
    private ListTableModel<Warning> model;

    public PageOne() {
        String defaultVm = ceylonSettings_.get_().getDefaultTargetVm();
        warnings = new ArrayList<>();
        compileForJvm.setSelected(!defaultVm.equals("js"));
        compileToJs.setSelected(!defaultVm.equals("jvm"));
        table = new JBTable() {
            public TableCellRenderer getCellRenderer(final int row, final int column) {
                final ColumnInfo columnInfo = ((ListTableModel) getModel()).getColumnInfos()[column];
                return columnInfo.getRenderer(((ListTableModel) getModel()).getItem(row));
            }
        };
        table.setShowGrid(false);
        table.setTableHeader(null);
        table.setRowSelectionAllowed(true);
        table.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                for (int selectedRow : selectedRows) {
                    Warning warning = model.getRowValue(selectedRow);
                    if (warnings.contains(warning)) {
                        warnings.remove(warning);
                    } else {
                        warnings.add(warning);
                    }
                }
                table.repaint();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), JComponent.WHEN_FOCUSED);

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
                || !project.getConfiguration().getSuppressWarningsEnum().equals(new TreeSet<>(warnings))
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
        model = new ListTableModel<>(
                new ColumnInfo[]{
                        new ColumnInfo<Warning, String>("Suppressed warnings") {
                            @Nullable
                            @Override
                            public String valueOf(Warning o) {
                                return o.getDescription();
                            }

                            @Nullable
                            @Override
                            public TableCellRenderer getRenderer(Warning warning) {
                                return new DefaultTableCellRenderer() {
                                    @Override
                                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                                        Component cmp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                        setBorder(null);
                                        return cmp;
                                    }
                                };
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
                            public TableCellRenderer getRenderer(Warning warning) {
                                return new BooleanTableCellRenderer() {
                                    @Override
                                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int column) {
                                        Component cmp = super.getTableCellRendererComponent(table, value, isSel, hasFocus, row, column);
                                        setBorder(null);
                                        return cmp;
                                    }
                                };
                            }

                            @Nullable
                            @Override
                            public TableCellEditor getEditor(Warning warning) {
                                return new BooleanTableCellEditor();
                            }
                        }
                },
                asList(Warning.values())
        );
        table.setModel(model);
        int cbWidth = new JCheckBox().getPreferredSize().width + 4;
        table.getColumnModel().getColumn(1).setMinWidth(cbWidth);
        table.getColumnModel().getColumn(1).setMaxWidth(cbWidth);

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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("Platform"));
        compileToJs = new JCheckBox();
        compileToJs.setText("Compile project to JavaScript");
        panel1.add(compileToJs, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        compileForJvm = new JCheckBox();
        compileForJvm.setSelected(true);
        compileForJvm.setText("Compile project for JVM");
        panel1.add(compileForJvm, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("Compiler settings"));
        final JLabel label1 = new JLabel();
        label1.setText("Javac options:");
        panel2.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        javacOptions = new RawCommandLineEditor();
        javacOptions.setDialogCaption("Enter Javac options");
        panel2.add(javacOptions, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        workOffline = new JCheckBox();
        workOffline.setText("Work offline (disable connection to remote module repositories)");
        panel2.add(workOffline, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder("Suppressed Warnings"));
        warningsPanel = new JPanel();
        warningsPanel.setLayout(new BorderLayout(0, 0));
        panel3.add(warningsPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        warningsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
