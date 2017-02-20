import ceylon.interop.java {
    javaString,
    createJavaObjectArray,
    javaClass
}

import com.intellij.ide.util.projectWizard {
    ModuleWizardStep
}
import com.intellij.ui {
    ScrollPaneFactory,
    TableUtil,
    BooleanTableCellEditor,
    BooleanTableCellRenderer
}
import com.intellij.ui.table {
    JBTable
}
import com.intellij.util.execution {
    ParametersListUtil
}
import com.intellij.util.ui {
    ListTableModel,
    ColumnInfo
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    Warning
}

import java.awt {
    BorderLayout,
    Component
}
import java.awt.event {
    ActionListener,
    ActionEvent,
    KeyEvent
}
import java.lang {
    JString=String,
    JBoolean=Boolean
}
import java.util {
    EnumSet,
    Arrays,
    JList=List,
    Collections,
    TreeSet,
    ArrayList
}

import javax.swing {
    KeyStroke,
    JComponent,
    ScrollPaneConstants,
    JCheckBox,
    JTable
}
import javax.swing.table {
    TableCellRenderer,
    DefaultTableCellRenderer
}

import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.settings {
    ceylonSettings
}

shared class CeylonPageOne() extends PageOne() satisfies CeylonConfigForm {

    panel => super.myPanel;

    String defaultVm = ceylonSettings.defaultTargetVm;

    compileForJvm.selected = !defaultVm.equals("js");
    compileToJs.selected = !defaultVm.equals("jvm");

    value warnings = ArrayList<Warning>();

    value table = object extends JBTable() {
        suppressWarnings("uncheckedTypeArguments")
        shared actual TableCellRenderer? getCellRenderer(Integer row, Integer column) {
            if (is ListTableModel<out Anything> mod = this.model) {
                assert(is ColumnInfo<Warning, out Anything> columnInfo = mod.columnInfos[column]);
                assert(is Warning item = mod.getItem(row));
                return columnInfo.getRenderer(item);
            }
            return null;
        }
    };
    table.setShowGrid(false);
    table.tableHeader = null;
    table.rowSelectionAllowed = true;
    table.registerKeyboardAction(object satisfies ActionListener {

        shared actual void actionPerformed(ActionEvent e) {
            value selectedRows = table.selectedRows;
            for (selectedRow in selectedRows) {
                value warning = model.getRowValue(selectedRow);
                if (warnings.contains(warning)) {
                    warnings.remove(warning);
                } else {
                    warnings.add(warning);
                }
            }
            table.repaint();
        }
    }, KeyStroke.getKeyStroke(KeyEvent.vkSpace, 0), JComponent.whenFocused);

    warningsPanel.add(
        ScrollPaneFactory.createScrollPane(
            table,
            ScrollPaneConstants.verticalScrollbarAsNeeded,
            ScrollPaneConstants.horizontalScrollbarAsNeeded
        ),
        javaString(BorderLayout.center)
    );

    void createTableModel(ArrayList<Warning> warnings) {
        model = ListTableModel(createJavaObjectArray<ColumnInfo<out Object, out Object>>({
            object extends ColumnInfo<Warning,String>("Suppressed warnings") {
                valueOf(Warning o) => o.description;

                shared actual TableCellRenderer getRenderer(Warning warning) {
                    return object extends DefaultTableCellRenderer() {

                        shared actual Component getTableCellRendererComponent(JTable table, Object val, Boolean isSelected, Boolean hasFocus, Integer row, Integer column) {
                            value cmp = super.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, column);
                            this.border = null;
                            return cmp;
                        }
                    };
                }
            },
            object extends ColumnInfo<Warning,JBoolean>("") {
                valueOf(Warning o) => JBoolean(warnings.contains(o));

                shared actual void setValue(Warning warning, JBoolean val) {
                    if (val.booleanValue()) {
                        if (!warnings.contains(warning)) {
                            warnings.add(warning);
                        }
                    } else {
                        warnings.remove(warning);
                    }
                }

                isCellEditable(Warning warning) => true;

                columnClass => javaClass<JBoolean>();

                shared actual TableCellRenderer getRenderer(Warning warning) {
                    return object extends BooleanTableCellRenderer() {
                        shared actual Component getTableCellRendererComponent(JTable table, Object val, Boolean isSel, Boolean hasFocus, Integer row, Integer column) {
                            value cmp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, row, column);
                            this.border = null;
                            return cmp;
                        }
                    };
                }

                getEditor(Warning warning) => BooleanTableCellEditor();
            }
        }), Arrays.asList(*Warning.values()));

        table.setModel(model);
        value cbWidth = JCheckBox().preferredSize.width + 4;
        table.columnModel.getColumn(1).minWidth = cbWidth.integer;
        table.columnModel.getColumn(1).maxWidth = cbWidth.integer;

        variable value i = 0;
        while (i<model.columnCount) {
            value column = table.columnModel.getColumn(i);
            value columnInfo = model.columnInfos.get(i);
            column.cellEditor = columnInfo.getEditor(null);
            if (columnInfo.columnClass == javaClass<Boolean>()) {
                TableUtil.setupCheckboxColumn(column);
            }
            i ++;
        }
    }

    shared actual void apply(IdeaCeylonProject project) {
        project.ideConfiguration.compileToJvm = compileForJvm.selected;
        project.ideConfiguration.compileToJs = compileToJs.selected;
        project.configuration.projectOffline = workOffline.selected;
        value enumSet = if (warnings.empty)
            then EnumSet.noneOf(`Warning`)
            else EnumSet.copyOf(warnings);

        project.configuration.projectSuppressWarningsEnum = enumSet;
        value opts = ParametersListUtil.defaultLineParser.fun(javaString(javacOptions.text));
        project.configuration.javacOptions = {for (opt in opts) opt.string};
    }

    shared actual Boolean isModified(IdeaCeylonProject project) {
        return !project.ideConfiguration.compileToJvm exists
            || !project.ideConfiguration.compileToJs exists
            || !project.configuration.projectOffline exists
            || (project.ideConfiguration.compileToJvm else false) != compileForJvm.selected
            || (project.ideConfiguration.compileToJs else false) != compileToJs.selected
            || (project.configuration.projectOffline else false) != workOffline.selected
            || !project.configuration.suppressWarningsEnum.equals(TreeSet(warnings))
            || !getJavacOpts(project).equals(javacOptions.text);
    }

    shared actual void load(IdeaCeylonProject project) {
        value defaultVm = ceylonSettings.defaultTargetVm;
        compileForJvm.selected = project.ideConfiguration.compileToJvm else !defaultVm.equals("js");
        compileToJs.selected = project.ideConfiguration.compileToJs else !defaultVm.equals("jvm");
        workOffline.selected = project.configuration.projectOffline else false;

        warnings.clear();
        warnings.addAll(project.configuration.suppressWarningsEnum);

        createTableModel(warnings);

        javacOptions.text = getJavacOpts(project);
    }

    String getJavacOpts(IdeaCeylonProject project) {
        value it = project.configuration.javacOptions;
        variable JList<JString> opts;
        if (exists it) {
            opts = Arrays.asList(*{for(str in it) javaString(str.string)});
        } else {
            opts = Collections.emptyList<JString>();
        }
        return ParametersListUtil.defaultLineJoiner.fun(opts).string;
    }
}

shared class PageOneWizardStep(CeylonModuleBuilder moduleBuilder) extends ModuleWizardStep() {

    value step = CeylonPageOne();

    component => step.panel;

    updateDataModel() => moduleBuilder.setPageOne(step);
}
