import com.intellij.openapi.actionSystem {
    DataContext,
    CommonDataKeys
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.editor.colors {
    EditorColorsManager,
    EditorFontType
}
import com.intellij.openapi.editor.event {
    DocumentListener,
    DocumentEvent
}
import com.intellij.openapi.fileTypes {
    FileType
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui {
    DialogWrapper,
    ValidationInfo
}
import com.intellij.psi {
    PsiElement,
    PsiFile,
    PsiDocumentManager
}
import com.intellij.psi.impl.source {
    PsiTypeCodeFragmentImpl
}
import com.intellij.refactoring {
    BaseRefactoringProcessor
}
import com.intellij.refactoring.changeSignature {
    ChangeSignatureHandler,
    ChangeSignatureDialogBase,
    MethodDescriptor,
    ParameterInfo,
    ParameterTableModelBase,
    ParameterTableModelItemBase
}
import com.intellij.refactoring.ui {
    ComboBoxVisibilityPanel
}
import com.intellij.ui {
    EditorTextField,
    EditorSettingsProvider,
    DottedBorder
}
import com.intellij.ui.treeStructure {
    IJTree=Tree
}
import com.intellij.util {
    Consumer
}
import com.intellij.util.ui {
    ColumnInfo,
    UIUtil
}
import com.intellij.util.ui.table {
    JBTableRowEditor,
    JBTableRow
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit,
    TypecheckerUnit
}
import com.redhat.ceylon.ide.common.refactoring {
    ChangeParametersRefactoring,
    parseTypeExpression
}
import com.redhat.ceylon.ide.common.util {
    nodes,
    escaping
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Functional
}

import java.awt {
    GridLayout,
    BorderLayout
}
import java.awt.event {
    ActionListener,
    ActionEvent
}
import java.lang {
    Types {
        nativeString
    },
    ObjectArray,
    JString=String,
    overloaded
}
import java.util {
    List,
    Set,
    JArrayList=ArrayList
}

import javax.swing {
    JComponent,
    JTable,
    JCheckBox,
    JPanel
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.lang {
    ceylonFileType
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject,
    getCeylonProject,
    getModelManager
}
import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument,
    IdeaCompositeChange
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class CeylonChangeSignatureHandler() satisfies ChangeSignatureHandler {

    overloaded
    shared actual PsiElement? findTargetMember(PsiFile file, Editor editor)
            => findTargetMember(file.findElementAt(editor.caretModel.offset));

    overloaded
    shared actual PsiElement? findTargetMember(PsiElement? element) {
        if (exists element,
            is CeylonFile file = element.containingFile,
            exists analysisResult = file.availableAnalysisResult,
            exists node = nodes.findNode {
                node = analysisResult.parsedRootNode;
                tokens = analysisResult.tokens;
                startOffset = element.textOffset;
            }) {

            return element;
        }

        return null;
    }

    overloaded
    shared actual void invoke(Project project, ObjectArray<PsiElement> elements, DataContext? ctx) {
        assert (exists modelManager = getModelManager(project));
        try {
            modelManager.pauseAutomaticModelUpdate();

            if (exists ctx,
                exists editor = CommonDataKeys.editor.getData(ctx),
                exists file = CommonDataKeys.psiFile.getData(ctx)) {
                
                invoke(project, editor, file, ctx);
            }

        } finally {
            modelManager.resumeAutomaticModelUpdate(0);
        }
    }

    overloaded
    shared actual void invoke(Project p, Editor editor, PsiFile file, DataContext? dataContext) {
        if (is CeylonFile file,
            exists localAnalysisResult = file.localAnalyzer?.result,
            exists phasedUnit = localAnalysisResult.typecheckedPhasedUnit,
            exists ceylonProject = getCeylonProject(file)) {

            value refacto = IdeaChangeParameterRefactoring(phasedUnit, localAnalysisResult.tokens, editor, ceylonProject);
            if (refacto.enabled,
                exists params = refacto.computeParameters()) {

                value dialog = object extends ChangeParameterDialog(params, p) {
                    shared actual void invokeRefactoring(BaseRefactoringProcessor processor) {
                        if (is IdeaCompositeChange chg = refacto.build(params)) {
                            object extends WriteCommandAction<Nothing>(p) {
                                run(Result<Nothing> result) => chg.applyChanges(project);
                            }.execute();
                        }

                        close(DialogWrapper.okExitCode);
                    }
                };

                dialog.show();
            }

        }
    }

    targetNotFoundMessage => "Caret should be positioned at the name of method or class to be refactored";

}

class IdeaChangeParameterRefactoring(
    PhasedUnit phasedUnit,
    List<CommonToken> theTokens,
    Editor editor,
    IdeaCeylonProject project
) extends ChangeParametersRefactoring(
    phasedUnit.compilationUnit,
    editor.selectionModel.selectionStart,
    editor.selectionModel.selectionEnd,
    theTokens,
    IdeaDocument(editor.document),
    phasedUnit,
    { if (exists units = project.typechecker?.phasedUnits?.phasedUnits)
      for (unit in units)
      unit }
) {

    inSameProject(Functional&Declaration declaration) => true;

    searchInEditor() => true;

    searchInFile(PhasedUnit pu) => pu.unit != phasedUnit.unit;

}

class MyParameterInfo(shared IdeaChangeParameterRefactoring.Param param) satisfies ParameterInfo {

    defaultValue => param.defaultArgs;

    shared actual String name => param.name;

    assign name {
        param.name = name;
    }

    oldIndex => -1;

    typeText => param.model.type.asString();

    shared actual variable Boolean useAnySingleVariable = false;

    shared variable String? typeError = null;
}

class MyMethodDescriptor(params, project) satisfies MethodDescriptor<MyParameterInfo, Object> {

    shared Project project;
    shared IdeaChangeParameterRefactoring.ParameterList params;

    canChangeName() => false;

    canChangeParameters() => true;

    canChangeVisibility() => false;

    method => null;

    name => params.declaration.name;

    parameters = JArrayList<MyParameterInfo>();

    params.parameters.each((p) => parameters.add(MyParameterInfo(p)));

    canChangeReturnType() => ReadWriteOption.none;

    parametersCount => params.size;

    visibility => null;

}

class MyParameterTableModel(IdeaChangeParameterRefactoring.ParameterList params, Project project)
        extends ParameterTableModelBase<MyParameterInfo, MyParameterTableModelItem>
        (null, null) {

    class MyNameColumn() extends NameColumn<MyParameterInfo, MyParameterTableModelItem>(project) {}

    class MyTypeColumn() extends TypeColumn<MyParameterInfo, MyParameterTableModelItem>(project, ceylonFileType) {
        isCellEditable(MyParameterTableModelItem model) => model.parameter.param.position == -1;
    }

    alias AnyColumnInfo => ColumnInfo<out Object, out Object>;

    shared MyParameterTableModel init() {
        value typeColumn = MyTypeColumn();
        value nameColumn = MyNameColumn();

        setColumnInfos(ObjectArray<AnyColumnInfo>.with {typeColumn, nameColumn});

        return this;
    }

    createRowItem(MyParameterInfo? parameterInfo)
            => MyParameterTableModelItem(parameterInfo else MyParameterInfo(params.create()), project);

    shared actual void removeRow(Integer index) {
        super.removeRow(index);
        params.delete(index);
    }

    shared actual void exchangeRows(Integer oldIndex, Integer newIndex) {
        super.exchangeRows(oldIndex, newIndex);
        if (oldIndex == newIndex - 1) {
            params.moveDown(oldIndex);
        } else if (newIndex == oldIndex - 1) {
            params.moveUp(oldIndex);
        }
    }

    shared actual void setValueAtWithoutUpdate(Object? aValue, Integer rowIndex, Integer columnIndex) {
        super.setValueAtWithoutUpdate(aValue, rowIndex, columnIndex);

        if (columnIndex == 0,
            is JString aValue,
            is TypecheckerUnit u = params.declaration.unit) {

            assert(exists param = params.parameters.get(rowIndex));
            value item = getItem(rowIndex);

            // TODO parse additional imports taken from item.typeCodeFragment
            switch (result = parseTypeExpression(aValue.string, u, params.declaration.scope))
            case (is String) {
                item.parameter.typeError = result;
            }
            else {
                item.parameter.typeError = null;
                param.model.model.type = result;
            }
        }
    }
//
//    String buildCodeFragment(String type, MyParameterTableModelItem item) {
//        if (is PsiTypeCodeFragmentImpl frag = item.typeCodeFragment) {
//            frag.importsToString()
//        }
//    }
}

class MyParameterTableModelItem(MyParameterInfo param, Project project)
        extends ParameterTableModelItemBase<MyParameterInfo>
        (param,
         PsiTypeCodeFragmentImpl(project, true, "type.ceylon", nativeString(param.typeText), 0, null),
         null) {

    ellipsisType => false;
}

class ChangeParameterDialog(IdeaChangeParameterRefactoring.ParameterList params, Project project)
        extends ChangeSignatureDialogBase<MyParameterInfo,PsiElement,Object,MyMethodDescriptor,MyParameterTableModelItem,MyParameterTableModel>
        (project, MyMethodDescriptor(params, project), false, null) {

    calculateSignature() => myMethod.params.previewSignature();

    createCallerChooser(String? title, IJTree? treeToReuse, Consumer<Set<PsiElement>>? callback)
            => null;

    createParametersInfoModel(MyMethodDescriptor method)
            => MyParameterTableModel(method.params, method.project).init();

    createRefactoringProcessor() => null;

    createReturnTypeCodeFragment() => null;

    createVisibilityControl()
            => ComboBoxVisibilityPanel("", ObjectArray<Object>(0), ObjectArray<JString>(0));

    fileType => ceylonFileType;

    shared actual ValidationInfo? doValidate() {
        for (item in myParametersTableModel.items) {
            if (exists error = item.parameter.typeError) {
                return ValidationInfo(error);
            }
            if (item.parameter.name in escaping.keywords) {
                return ValidationInfo("``item.parameter.name`` is a reserved keyword");
            }
        }
        return super.doValidate();
    }

    postponeValidation() => false;

    validateAndCommitData() => null;

    listTableViewSupported => true;

    JComponent createEditorTextFieldPresentation(
            Project proj, FileType type, String txt,
            Boolean selected, Boolean focused) {
        value panel = JPanel(BorderLayout());
        object field extends EditorTextField(txt, proj, type) {
            shouldHaveBorder() => false;
        }
        value font = EditorColorsManager.instance.globalScheme.getFont(EditorFontType.plain);
//        font = Font(font.fontName, font.style, 12);
        field.setFont(font);
        field.addSettingsProvider(EditorSettingsProvider.noWhitespace);
        if (selected, focused) {
            panel.setBackground(UIUtil.tableSelectionBackground);
            field.setAsRendererWithSelection(UIUtil.tableSelectionBackground,
                                             UIUtil.tableSelectionForeground);
        } else {
            panel.setBackground(UIUtil.tableBackground);
            if (selected) {
                panel.border = DottedBorder(UIUtil.tableForeground);
            }
        }
        panel.add(field, nativeString("West"));
        return panel;
    }

    getRowPresentation(ParameterTableModelItemBase<MyParameterInfo> item,
                Boolean selected, Boolean focused)
            => createEditorTextFieldPresentation {
                proj = project;
                type = ceylonFileType;
                txt = let (value param = item.parameter.param)
                        param.model.type.asString() + " " + param.name
                        + (if (exists arg = param.defaultArgs) then " = " + arg else "");
                selected = selected;
                focused = focused;
            };

    getTableEditor(JTable table, ParameterTableModelItemBase<MyParameterInfo> item)
        => object extends JBTableRowEditor() {
            late variable EditorTextField myTypeEditor;
            late variable EditorTextField myNameEditor;
            late variable JCheckBox myDefaultedCb;
            late variable EditorTextField myDefaultArgEditor;

            focusableComponents
                    => ObjectArray<JComponent>.with {
                        myTypeEditor.focusTarget,
                        myNameEditor.focusTarget,
                        myDefaultedCb,
                        myDefaultArgEditor.focusTarget
                    };

            preferredFocusedComponent => myTypeEditor.focusTarget;

            shared actual void prepareEditor(JTable tbl, Integer row) {
                this.layout = GridLayout(2, 2);
                value doc = PsiDocumentManager.getInstance(project).getDocument(item.typeCodeFragment);
                myTypeEditor = EditorTextField(doc, project, fileType);
                myTypeEditor.addDocumentListener(mySignatureUpdater);
                myTypeEditor.setPreferredWidth(table.width / 2);
                myTypeEditor.addDocumentListener(RowEditorChangeListener(0));
                myTypeEditor.setEnabled(item.parameter.param.position == -1);
                add(createLabeledPanel("Type:", myTypeEditor));

                myNameEditor = EditorTextField(item.parameter.name, project, fileType);
                myTypeEditor.addDocumentListener(mySignatureUpdater);
                myNameEditor.addDocumentListener(RowEditorChangeListener(1));
                add(createLabeledPanel("Name:", myNameEditor));

                myDefaultedCb = JCheckBox("Use default argument", item.parameter.param.defaulted);
                myDefaultedCb.addActionListener(object satisfies ActionListener {
                    shared actual void actionPerformed(ActionEvent e) {
                        item.parameter.param.defaulted = myDefaultedCb.selected;
                        updateSignature();
                    }
                });
                add(createLabeledPanel("Defaulted:", myDefaultedCb));

                myDefaultArgEditor = EditorTextField(item.parameter.param.defaultArgs else "", project, fileType);
                myDefaultArgEditor.addDocumentListener(mySignatureUpdater);
                myDefaultArgEditor.addDocumentListener(object satisfies DocumentListener {
                    shared actual void beforeDocumentChange(DocumentEvent? event) {}

                    shared actual void documentChanged(DocumentEvent? event) {
                        item.parameter.param.defaultArgs =
                            if (myDefaultArgEditor.text.empty)
                            then null
                            else myDefaultArgEditor.text;
                        updateSignature();
                    }

                });
                add(createLabeledPanel("Default argument:", myDefaultArgEditor));
            }

            \ivalue => object satisfies JBTableRow {
                shared actual Object? getValueAt(Integer column) {
                    return switch (column)
                    case (0) item.typeCodeFragment
                    case (1) nativeString(myNameEditor.text.trimmed)
                    else null;
                }
            };
        };
}
