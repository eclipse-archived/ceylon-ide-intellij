package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import static org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi.*;

public class CeylonPsiVisitor extends PsiElementVisitor {

    private boolean recursive;

    public CeylonPsiVisitor(boolean recursive) {
        this.recursive = recursive;
    }

    @Override
    public void visitElement(PsiElement element) {
        super.visitElement(element);
        if (false) {

        } else if (element.getNode().getElementType() == CeylonTypes.COMPILATION_UNIT) {
            visitCompilationUnitPsi((CompilationUnitPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MODULE_DESCRIPTOR) {
            visitModuleDescriptorPsi((ModuleDescriptorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PACKAGE_DESCRIPTOR) {
            visitPackageDescriptorPsi((PackageDescriptorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_MODULE_LIST) {
            visitImportModuleListPsi((ImportModuleListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_MODULE) {
            visitImportModulePsi((ImportModulePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_LIST) {
            visitImportListPsi((ImportListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT) {
            visitImportPsi((ImportPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_PATH) {
            visitImportPathPsi((ImportPathPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_MEMBER_OR_TYPE_LIST) {
            visitImportMemberOrTypeListPsi((ImportMemberOrTypeListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_MEMBER_OR_TYPE) {
            visitImportMemberOrTypePsi((ImportMemberOrTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_MEMBER) {
            visitImportMemberPsi((ImportMemberPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_TYPE) {
            visitImportTypePsi((ImportTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ALIAS) {
            visitAliasPsi((AliasPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IMPORT_WILDCARD) {
            visitImportWildcardPsi((ImportWildcardPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DECLARATION) {
            visitDeclarationPsi((DeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MISSING_DECLARATION) {
            visitMissingDeclarationPsi((MissingDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_DECLARATION) {
            visitTypeDeclarationPsi((TypeDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CLASS_OR_INTERFACE) {
            visitClassOrInterfacePsi((ClassOrInterfacePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_ALIAS_DECLARATION) {
            visitTypeAliasDeclarationPsi((TypeAliasDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SATISFIED_TYPES) {
            visitSatisfiedTypesPsi((SatisfiedTypesPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ABSTRACTED_TYPE) {
            visitAbstractedTypePsi((AbstractedTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CASE_TYPES) {
            visitCaseTypesPsi((CaseTypesPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXTENDED_TYPE) {
            visitExtendedTypePsi((ExtendedTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_CONSTRAINT_LIST) {
            visitTypeConstraintListPsi((TypeConstraintListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_CONSTRAINT) {
            visitTypeConstraintPsi((TypeConstraintPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_SPECIFIER) {
            visitTypeSpecifierPsi((TypeSpecifierPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DEFAULT_TYPE_ARGUMENT) {
            visitDefaultTypeArgumentPsi((DefaultTypeArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CLASS_SPECIFIER) {
            visitClassSpecifierPsi((ClassSpecifierPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ANY_CLASS) {
            visitAnyClassPsi((AnyClassPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CLASS_DEFINITION) {
            visitClassDefinitionPsi((ClassDefinitionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ENUMERATED) {
            visitEnumeratedPsi((EnumeratedPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CONSTRUCTOR) {
            visitConstructorPsi((ConstructorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DELEGATED_CONSTRUCTOR) {
            visitDelegatedConstructorPsi((DelegatedConstructorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CLASS_DECLARATION) {
            visitClassDeclarationPsi((ClassDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ANY_INTERFACE) {
            visitAnyInterfacePsi((AnyInterfacePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INTERFACE_DEFINITION) {
            visitInterfaceDefinitionPsi((InterfaceDefinitionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INTERFACE_DECLARATION) {
            visitInterfaceDeclarationPsi((InterfaceDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPED_DECLARATION) {
            visitTypedDeclarationPsi((TypedDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ANY_ATTRIBUTE) {
            visitAnyAttributePsi((AnyAttributePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ATTRIBUTE_DECLARATION) {
            visitAttributeDeclarationPsi((AttributeDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ATTRIBUTE_GETTER_DEFINITION) {
            visitAttributeGetterDefinitionPsi((AttributeGetterDefinitionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ATTRIBUTE_SETTER_DEFINITION) {
            visitAttributeSetterDefinitionPsi((AttributeSetterDefinitionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ANY_METHOD) {
            visitAnyMethodPsi((AnyMethodPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.METHOD_DEFINITION) {
            visitMethodDefinitionPsi((MethodDefinitionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.METHOD_DECLARATION) {
            visitMethodDeclarationPsi((MethodDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.VOID_MODIFIER) {
            visitVoidModifierPsi((VoidModifierPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OBJECT_DEFINITION) {
            visitObjectDefinitionPsi((ObjectDefinitionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PARAMETER_LIST) {
            visitParameterListPsi((ParameterListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PARAMETER) {
            visitParameterPsi((ParameterPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PARAMETER_DECLARATION) {
            visitParameterDeclarationPsi((ParameterDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.VALUE_PARAMETER_DECLARATION) {
            visitValueParameterDeclarationPsi((ValueParameterDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FUNCTIONAL_PARAMETER_DECLARATION) {
            visitFunctionalParameterDeclarationPsi((FunctionalParameterDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INITIALIZER_PARAMETER) {
            visitInitializerParameterPsi((InitializerParameterPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_PARAMETER_LIST) {
            visitTypeParameterListPsi((TypeParameterListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_PARAMETER_DECLARATION) {
            visitTypeParameterDeclarationPsi((TypeParameterDeclarationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_VARIANCE) {
            visitTypeVariancePsi((TypeVariancePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BODY) {
            visitBodyPsi((BodyPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BLOCK) {
            visitBlockPsi((BlockPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CLASS_BODY) {
            visitClassBodyPsi((ClassBodyPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INTERFACE_BODY) {
            visitInterfaceBodyPsi((InterfaceBodyPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE) {
            visitTypePsi((TypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.STATIC_TYPE) {
            visitStaticTypePsi((StaticTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.GROUPED_TYPE) {
            visitGroupedTypePsi((GroupedTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SIMPLE_TYPE) {
            visitSimpleTypePsi((SimpleTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BASE_TYPE) {
            visitBaseTypePsi((BaseTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.QUALIFIED_TYPE) {
            visitQualifiedTypePsi((QualifiedTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.UNION_TYPE) {
            visitUnionTypePsi((UnionTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INTERSECTION_TYPE) {
            visitIntersectionTypePsi((IntersectionTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SEQUENCE_TYPE) {
            visitSequenceTypePsi((SequenceTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ITERABLE_TYPE) {
            visitIterableTypePsi((IterableTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OPTIONAL_TYPE) {
            visitOptionalTypePsi((OptionalTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TUPLE_TYPE) {
            visitTupleTypePsi((TupleTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FUNCTION_TYPE) {
            visitFunctionTypePsi((FunctionTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ENTRY_TYPE) {
            visitEntryTypePsi((EntryTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_CONSTRUCTOR) {
            visitTypeConstructorPsi((TypeConstructorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SUPER_TYPE) {
            visitSuperTypePsi((SuperTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.META_LITERAL) {
            visitMetaLiteralPsi((MetaLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_LITERAL) {
            visitTypeLiteralPsi((TypeLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MEMBER_LITERAL) {
            visitMemberLiteralPsi((MemberLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CLASS_LITERAL) {
            visitClassLiteralPsi((ClassLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NEW_LITERAL) {
            visitNewLiteralPsi((NewLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INTERFACE_LITERAL) {
            visitInterfaceLiteralPsi((InterfaceLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ALIAS_LITERAL) {
            visitAliasLiteralPsi((AliasLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_PARAMETER_LITERAL) {
            visitTypeParameterLiteralPsi((TypeParameterLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.VALUE_LITERAL) {
            visitValueLiteralPsi((ValueLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FUNCTION_LITERAL) {
            visitFunctionLiteralPsi((FunctionLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MODULE_LITERAL) {
            visitModuleLiteralPsi((ModuleLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PACKAGE_LITERAL) {
            visitPackageLiteralPsi((PackageLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DYNAMIC_MODIFIER) {
            visitDynamicModifierPsi((DynamicModifierPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LOCAL_MODIFIER) {
            visitLocalModifierPsi((LocalModifierPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.VALUE_MODIFIER) {
            visitValueModifierPsi((ValueModifierPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FUNCTION_MODIFIER) {
            visitFunctionModifierPsi((FunctionModifierPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SYNTHETIC_VARIABLE) {
            visitSyntheticVariablePsi((SyntheticVariablePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_ARGUMENTS) {
            visitTypeArgumentsPsi((TypeArgumentsPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_ARGUMENT_LIST) {
            visitTypeArgumentListPsi((TypeArgumentListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INFERRED_TYPE_ARGUMENTS) {
            visitInferredTypeArgumentsPsi((InferredTypeArgumentsPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SEQUENCED_TYPE) {
            visitSequencedTypePsi((SequencedTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DEFAULTED_TYPE) {
            visitDefaultedTypePsi((DefaultedTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SPREAD_TYPE) {
            visitSpreadTypePsi((SpreadTypePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DIRECTIVE) {
            visitDirectivePsi((DirectivePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.RETURN) {
            visitReturnPsi((ReturnPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.THROW) {
            visitThrowPsi((ThrowPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CONTINUE) {
            visitContinuePsi((ContinuePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BREAK) {
            visitBreakPsi((BreakPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.STATEMENT_OR_ARGUMENT) {
            visitStatementOrArgumentPsi((StatementOrArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.STATEMENT) {
            visitStatementPsi((StatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.COMPILER_ANNOTATION) {
            visitCompilerAnnotationPsi((CompilerAnnotationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXECUTABLE_STATEMENT) {
            visitExecutableStatementPsi((ExecutableStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ASSERTION) {
            visitAssertionPsi((AssertionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SPECIFIER_STATEMENT) {
            visitSpecifierStatementPsi((SpecifierStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXPRESSION_STATEMENT) {
            visitExpressionStatementPsi((ExpressionStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PATTERN) {
            visitPatternPsi((PatternPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.VARIABLE_PATTERN) {
            visitVariablePatternPsi((VariablePatternPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TUPLE_PATTERN) {
            visitTuplePatternPsi((TuplePatternPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.KEY_VALUE_PATTERN) {
            visitKeyValuePatternPsi((KeyValuePatternPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DESTRUCTURE) {
            visitDestructurePsi((DestructurePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CONTROL_STATEMENT) {
            visitControlStatementPsi((ControlStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CONTROL_CLAUSE) {
            visitControlClausePsi((ControlClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DYNAMIC_STATEMENT) {
            visitDynamicStatementPsi((DynamicStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DYNAMIC_CLAUSE) {
            visitDynamicClausePsi((DynamicClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LET_EXPRESSION) {
            visitLetExpressionPsi((LetExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LET_CLAUSE) {
            visitLetClausePsi((LetClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IF_STATEMENT) {
            visitIfStatementPsi((IfStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IF_CLAUSE) {
            visitIfClausePsi((IfClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ELSE_CLAUSE) {
            visitElseClausePsi((ElseClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SWITCH_STATEMENT) {
            visitSwitchStatementPsi((SwitchStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SWITCH_CLAUSE) {
            visitSwitchClausePsi((SwitchClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SWITCHED) {
            visitSwitchedPsi((SwitchedPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SWITCH_CASE_LIST) {
            visitSwitchCaseListPsi((SwitchCaseListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CASE_CLAUSE) {
            visitCaseClausePsi((CaseClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CASE_ITEM) {
            visitCaseItemPsi((CaseItemPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MATCH_CASE) {
            visitMatchCasePsi((MatchCasePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IS_CASE) {
            visitIsCasePsi((IsCasePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SATISFIES_CASE) {
            visitSatisfiesCasePsi((SatisfiesCasePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TRY_CATCH_STATEMENT) {
            visitTryCatchStatementPsi((TryCatchStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TRY_CLAUSE) {
            visitTryClausePsi((TryClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CATCH_CLAUSE) {
            visitCatchClausePsi((CatchClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FINALLY_CLAUSE) {
            visitFinallyClausePsi((FinallyClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.RESOURCE_LIST) {
            visitResourceListPsi((ResourceListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.RESOURCE) {
            visitResourcePsi((ResourcePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CATCH_VARIABLE) {
            visitCatchVariablePsi((CatchVariablePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FOR_STATEMENT) {
            visitForStatementPsi((ForStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FOR_CLAUSE) {
            visitForClausePsi((ForClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FOR_ITERATOR) {
            visitForIteratorPsi((ForIteratorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.VALUE_ITERATOR) {
            visitValueIteratorPsi((ValueIteratorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PATTERN_ITERATOR) {
            visitPatternIteratorPsi((PatternIteratorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.WHILE_STATEMENT) {
            visitWhileStatementPsi((WhileStatementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.WHILE_CLAUSE) {
            visitWhileClausePsi((WhileClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CONDITION_LIST) {
            visitConditionListPsi((ConditionListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CONDITION) {
            visitConditionPsi((ConditionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BOOLEAN_CONDITION) {
            visitBooleanConditionPsi((BooleanConditionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXISTS_OR_NONEMPTY_CONDITION) {
            visitExistsOrNonemptyConditionPsi((ExistsOrNonemptyConditionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXISTS_CONDITION) {
            visitExistsConditionPsi((ExistsConditionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NONEMPTY_CONDITION) {
            visitNonemptyConditionPsi((NonemptyConditionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IS_CONDITION) {
            visitIsConditionPsi((IsConditionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SATISFIES_CONDITION) {
            visitSatisfiesConditionPsi((SatisfiesConditionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.VARIABLE) {
            visitVariablePsi((VariablePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TERM) {
            visitTermPsi((TermPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OPERATOR_EXPRESSION) {
            visitOperatorExpressionPsi((OperatorExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BINARY_OPERATOR_EXPRESSION) {
            visitBinaryOperatorExpressionPsi((BinaryOperatorExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ARITHMETIC_OP) {
            visitArithmeticOpPsi((ArithmeticOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SUM_OP) {
            visitSumOpPsi((SumOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DIFFERENCE_OP) {
            visitDifferenceOpPsi((DifferenceOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PRODUCT_OP) {
            visitProductOpPsi((ProductOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.QUOTIENT_OP) {
            visitQuotientOpPsi((QuotientOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.POWER_OP) {
            visitPowerOpPsi((PowerOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.REMAINDER_OP) {
            visitRemainderOpPsi((RemainderOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ASSIGNMENT_OP) {
            visitAssignmentOpPsi((AssignmentOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ASSIGN_OP) {
            visitAssignOpPsi((AssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ARITHMETIC_ASSIGNMENT_OP) {
            visitArithmeticAssignmentOpPsi((ArithmeticAssignmentOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ADD_ASSIGN_OP) {
            visitAddAssignOpPsi((AddAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SUBTRACT_ASSIGN_OP) {
            visitSubtractAssignOpPsi((SubtractAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MULTIPLY_ASSIGN_OP) {
            visitMultiplyAssignOpPsi((MultiplyAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DIVIDE_ASSIGN_OP) {
            visitDivideAssignOpPsi((DivideAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.REMAINDER_ASSIGN_OP) {
            visitRemainderAssignOpPsi((RemainderAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BITWISE_ASSIGNMENT_OP) {
            visitBitwiseAssignmentOpPsi((BitwiseAssignmentOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INTERSECT_ASSIGN_OP) {
            visitIntersectAssignOpPsi((IntersectAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.UNION_ASSIGN_OP) {
            visitUnionAssignOpPsi((UnionAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.COMPLEMENT_ASSIGN_OP) {
            visitComplementAssignOpPsi((ComplementAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LOGICAL_ASSIGNMENT_OP) {
            visitLogicalAssignmentOpPsi((LogicalAssignmentOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.AND_ASSIGN_OP) {
            visitAndAssignOpPsi((AndAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OR_ASSIGN_OP) {
            visitOrAssignOpPsi((OrAssignOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LOGICAL_OP) {
            visitLogicalOpPsi((LogicalOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.AND_OP) {
            visitAndOpPsi((AndOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OR_OP) {
            visitOrOpPsi((OrOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BITWISE_OP) {
            visitBitwiseOpPsi((BitwiseOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INTERSECTION_OP) {
            visitIntersectionOpPsi((IntersectionOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.UNION_OP) {
            visitUnionOpPsi((UnionOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.COMPLEMENT_OP) {
            visitComplementOpPsi((ComplementOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EQUALITY_OP) {
            visitEqualityOpPsi((EqualityOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EQUAL_OP) {
            visitEqualOpPsi((EqualOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NOT_EQUAL_OP) {
            visitNotEqualOpPsi((NotEqualOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.COMPARISON_OP) {
            visitComparisonOpPsi((ComparisonOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LARGER_OP) {
            visitLargerOpPsi((LargerOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SMALLER_OP) {
            visitSmallerOpPsi((SmallerOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LARGE_AS_OP) {
            visitLargeAsOpPsi((LargeAsOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SMALL_AS_OP) {
            visitSmallAsOpPsi((SmallAsOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SCALE_OP) {
            visitScaleOpPsi((ScaleOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BOUND) {
            visitBoundPsi((BoundPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OPEN_BOUND) {
            visitOpenBoundPsi((OpenBoundPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CLOSED_BOUND) {
            visitClosedBoundPsi((ClosedBoundPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.WITHIN_OP) {
            visitWithinOpPsi((WithinOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DEFAULT_OP) {
            visitDefaultOpPsi((DefaultOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.THEN_OP) {
            visitThenOpPsi((ThenOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IDENTICAL_OP) {
            visitIdenticalOpPsi((IdenticalOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ENTRY_OP) {
            visitEntryOpPsi((EntryOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.RANGE_OP) {
            visitRangeOpPsi((RangeOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SEGMENT_OP) {
            visitSegmentOpPsi((SegmentOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.COMPARE_OP) {
            visitCompareOpPsi((CompareOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IN_OP) {
            visitInOpPsi((InOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.UNARY_OPERATOR_EXPRESSION) {
            visitUnaryOperatorExpressionPsi((UnaryOperatorExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NOT_OP) {
            visitNotOpPsi((NotOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXISTS) {
            visitExistsPsi((ExistsPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NONEMPTY) {
            visitNonemptyPsi((NonemptyPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NEGATIVE_OP) {
            visitNegativeOpPsi((NegativeOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.POSITIVE_OP) {
            visitPositiveOpPsi((PositiveOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPE_OPERATOR_EXPRESSION) {
            visitTypeOperatorExpressionPsi((TypeOperatorExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IS_OP) {
            visitIsOpPsi((IsOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SATISFIES) {
            visitSatisfiesPsi((SatisfiesPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXTENDS) {
            visitExtendsPsi((ExtendsPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OF_OP) {
            visitOfOpPsi((OfOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PREFIX_OPERATOR_EXPRESSION) {
            visitPrefixOperatorExpressionPsi((PrefixOperatorExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INCREMENT_OP) {
            visitIncrementOpPsi((IncrementOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DECREMENT_OP) {
            visitDecrementOpPsi((DecrementOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.POSTFIX_OPERATOR_EXPRESSION) {
            visitPostfixOperatorExpressionPsi((PostfixOperatorExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.POSTFIX_INCREMENT_OP) {
            visitPostfixIncrementOpPsi((PostfixIncrementOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.POSTFIX_DECREMENT_OP) {
            visitPostfixDecrementOpPsi((PostfixDecrementOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXPRESSION_LIST) {
            visitExpressionListPsi((ExpressionListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXPRESSION) {
            visitExpressionPsi((ExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PRIMARY) {
            visitPrimaryPsi((PrimaryPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.POSTFIX_EXPRESSION) {
            visitPostfixExpressionPsi((PostfixExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INVOCATION_EXPRESSION) {
            visitInvocationExpressionPsi((InvocationExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PARAMETERIZED_EXPRESSION) {
            visitParameterizedExpressionPsi((ParameterizedExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MEMBER_OR_TYPE_EXPRESSION) {
            visitMemberOrTypeExpressionPsi((MemberOrTypeExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXTENDED_TYPE_EXPRESSION) {
            visitExtendedTypeExpressionPsi((ExtendedTypeExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.STATIC_MEMBER_OR_TYPE_EXPRESSION) {
            visitStaticMemberOrTypeExpressionPsi((StaticMemberOrTypeExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BASE_MEMBER_OR_TYPE_EXPRESSION) {
            visitBaseMemberOrTypeExpressionPsi((BaseMemberOrTypeExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BASE_MEMBER_EXPRESSION) {
            visitBaseMemberExpressionPsi((BaseMemberExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.BASE_TYPE_EXPRESSION) {
            visitBaseTypeExpressionPsi((BaseTypeExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.QUALIFIED_MEMBER_OR_TYPE_EXPRESSION) {
            visitQualifiedMemberOrTypeExpressionPsi((QualifiedMemberOrTypeExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.QUALIFIED_MEMBER_EXPRESSION) {
            visitQualifiedMemberExpressionPsi((QualifiedMemberExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.QUALIFIED_TYPE_EXPRESSION) {
            visitQualifiedTypeExpressionPsi((QualifiedTypeExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MEMBER_OPERATOR) {
            visitMemberOperatorPsi((MemberOperatorPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.MEMBER_OP) {
            visitMemberOpPsi((MemberOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SAFE_MEMBER_OP) {
            visitSafeMemberOpPsi((SafeMemberOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SPREAD_OP) {
            visitSpreadOpPsi((SpreadOpPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INDEX_EXPRESSION) {
            visitIndexExpressionPsi((IndexExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ELEMENT_OR_RANGE) {
            visitElementOrRangePsi((ElementOrRangePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ELEMENT) {
            visitElementPsi((ElementPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ELEMENT_RANGE) {
            visitElementRangePsi((ElementRangePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OUTER) {
            visitOuterPsi((OuterPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.PACKAGE) {
            visitPackagePsi((PackagePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ARGUMENT_LIST) {
            visitArgumentListPsi((ArgumentListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NAMED_ARGUMENT_LIST) {
            visitNamedArgumentListPsi((NamedArgumentListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SEQUENCED_ARGUMENT) {
            visitSequencedArgumentPsi((SequencedArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.POSITIONAL_ARGUMENT_LIST) {
            visitPositionalArgumentListPsi((PositionalArgumentListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.POSITIONAL_ARGUMENT) {
            visitPositionalArgumentPsi((PositionalArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LISTED_ARGUMENT) {
            visitListedArgumentPsi((ListedArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SPREAD_ARGUMENT) {
            visitSpreadArgumentPsi((SpreadArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FUNCTION_ARGUMENT) {
            visitFunctionArgumentPsi((FunctionArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OBJECT_EXPRESSION) {
            visitObjectExpressionPsi((ObjectExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IF_EXPRESSION) {
            visitIfExpressionPsi((IfExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SWITCH_EXPRESSION) {
            visitSwitchExpressionPsi((SwitchExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NAMED_ARGUMENT) {
            visitNamedArgumentPsi((NamedArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SPECIFIED_ARGUMENT) {
            visitSpecifiedArgumentPsi((SpecifiedArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TYPED_ARGUMENT) {
            visitTypedArgumentPsi((TypedArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.METHOD_ARGUMENT) {
            visitMethodArgumentPsi((MethodArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ATTRIBUTE_ARGUMENT) {
            visitAttributeArgumentPsi((AttributeArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.OBJECT_ARGUMENT) {
            visitObjectArgumentPsi((ObjectArgumentPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SPECIFIER_OR_INITIALIZER_EXPRESSION) {
            visitSpecifierOrInitializerExpressionPsi((SpecifierOrInitializerExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SPECIFIER_EXPRESSION) {
            visitSpecifierExpressionPsi((SpecifierExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LAZY_SPECIFIER_EXPRESSION) {
            visitLazySpecifierExpressionPsi((LazySpecifierExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INITIALIZER_EXPRESSION) {
            visitInitializerExpressionPsi((InitializerExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ATOM) {
            visitAtomPsi((AtomPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.LITERAL) {
            visitLiteralPsi((LiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.NATURAL_LITERAL) {
            visitNaturalLiteralPsi((NaturalLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FLOAT_LITERAL) {
            visitFloatLiteralPsi((FloatLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.CHAR_LITERAL) {
            visitCharLiteralPsi((CharLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.STRING_LITERAL) {
            visitStringLiteralPsi((StringLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.QUOTED_LITERAL) {
            visitQuotedLiteralPsi((QuotedLiteralPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DOC_LINK) {
            visitDocLinkPsi((DocLinkPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SELF_EXPRESSION) {
            visitSelfExpressionPsi((SelfExpressionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.THIS) {
            visitThisPsi((ThisPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SUPER) {
            visitSuperPsi((SuperPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.SEQUENCE_ENUMERATION) {
            visitSequenceEnumerationPsi((SequenceEnumerationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.TUPLE) {
            visitTuplePsi((TuplePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.DYNAMIC) {
            visitDynamicPsi((DynamicPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.STRING_TEMPLATE) {
            visitStringTemplatePsi((StringTemplatePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ANNOTATION) {
            visitAnnotationPsi((AnnotationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ANONYMOUS_ANNOTATION) {
            visitAnonymousAnnotationPsi((AnonymousAnnotationPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.ANNOTATION_LIST) {
            visitAnnotationListPsi((AnnotationListPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IDENTIFIER) {
            visitIdentifierPsi((IdentifierPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.COMPREHENSION) {
            visitComprehensionPsi((ComprehensionPsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.COMPREHENSION_CLAUSE) {
            visitComprehensionClausePsi((ComprehensionClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.INITIAL_COMPREHENSION_CLAUSE) {
            visitInitialComprehensionClausePsi((InitialComprehensionClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.EXPRESSION_COMPREHENSION_CLAUSE) {
            visitExpressionComprehensionClausePsi((ExpressionComprehensionClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.FOR_COMPREHENSION_CLAUSE) {
            visitForComprehensionClausePsi((ForComprehensionClausePsi) element);
        } else if (element.getNode().getElementType() == CeylonTypes.IF_COMPREHENSION_CLAUSE) {
            visitIfComprehensionClausePsi((IfComprehensionClausePsi) element);
        }

        if (recursive) {
            element.acceptChildren(this);
        }
    }
    public void visitIfComprehensionClausePsi(@NotNull IfComprehensionClausePsi element) { visitInitialComprehensionClausePsi(element); }
    public void visitForComprehensionClausePsi(@NotNull ForComprehensionClausePsi element) { visitInitialComprehensionClausePsi(element); }
    public void visitExpressionComprehensionClausePsi(@NotNull ExpressionComprehensionClausePsi element) { visitComprehensionClausePsi(element); }
    public void visitInitialComprehensionClausePsi(@NotNull InitialComprehensionClausePsi element) { visitComprehensionClausePsi(element); }
    public void visitComprehensionClausePsi(@NotNull ComprehensionClausePsi element) { visitControlClausePsi(element); }
    public void visitComprehensionPsi(@NotNull ComprehensionPsi element) { visitPositionalArgumentPsi(element); }
    public void visitIdentifierPsi(@NotNull IdentifierPsi element) {}
    public void visitAnnotationListPsi(@NotNull AnnotationListPsi element) {}
    public void visitAnonymousAnnotationPsi(@NotNull AnonymousAnnotationPsi element) {}
    public void visitAnnotationPsi(@NotNull AnnotationPsi element) { visitInvocationExpressionPsi(element); }
    public void visitStringTemplatePsi(@NotNull StringTemplatePsi element) { visitAtomPsi(element); }
    public void visitDynamicPsi(@NotNull DynamicPsi element) { visitAtomPsi(element); }
    public void visitTuplePsi(@NotNull TuplePsi element) { visitAtomPsi(element); }
    public void visitSequenceEnumerationPsi(@NotNull SequenceEnumerationPsi element) { visitAtomPsi(element); }
    public void visitSuperPsi(@NotNull SuperPsi element) { visitSelfExpressionPsi(element); }
    public void visitThisPsi(@NotNull ThisPsi element) { visitSelfExpressionPsi(element); }
    public void visitSelfExpressionPsi(@NotNull SelfExpressionPsi element) { visitAtomPsi(element); }
    public void visitDocLinkPsi(@NotNull DocLinkPsi element) {}
    public void visitQuotedLiteralPsi(@NotNull QuotedLiteralPsi element) { visitLiteralPsi(element); }
    public void visitStringLiteralPsi(@NotNull StringLiteralPsi element) { visitLiteralPsi(element); }
    public void visitCharLiteralPsi(@NotNull CharLiteralPsi element) { visitLiteralPsi(element); }
    public void visitFloatLiteralPsi(@NotNull FloatLiteralPsi element) { visitLiteralPsi(element); }
    public void visitNaturalLiteralPsi(@NotNull NaturalLiteralPsi element) { visitLiteralPsi(element); }
    public void visitLiteralPsi(@NotNull LiteralPsi element) { visitAtomPsi(element); }
    public void visitAtomPsi(@NotNull AtomPsi element) { visitPrimaryPsi(element); }
    public void visitInitializerExpressionPsi(@NotNull InitializerExpressionPsi element) { visitSpecifierOrInitializerExpressionPsi(element); }
    public void visitLazySpecifierExpressionPsi(@NotNull LazySpecifierExpressionPsi element) { visitSpecifierExpressionPsi(element); }
    public void visitSpecifierExpressionPsi(@NotNull SpecifierExpressionPsi element) { visitSpecifierOrInitializerExpressionPsi(element); }
    public void visitSpecifierOrInitializerExpressionPsi(@NotNull SpecifierOrInitializerExpressionPsi element) {}
    public void visitObjectArgumentPsi(@NotNull ObjectArgumentPsi element) { visitTypedArgumentPsi(element); }
    public void visitAttributeArgumentPsi(@NotNull AttributeArgumentPsi element) { visitTypedArgumentPsi(element); }
    public void visitMethodArgumentPsi(@NotNull MethodArgumentPsi element) { visitTypedArgumentPsi(element); }
    public void visitTypedArgumentPsi(@NotNull TypedArgumentPsi element) { visitNamedArgumentPsi(element); }
    public void visitSpecifiedArgumentPsi(@NotNull SpecifiedArgumentPsi element) { visitNamedArgumentPsi(element); }
    public void visitNamedArgumentPsi(@NotNull NamedArgumentPsi element) { visitStatementOrArgumentPsi(element); }
    public void visitSwitchExpressionPsi(@NotNull SwitchExpressionPsi element) { visitTermPsi(element); }
    public void visitIfExpressionPsi(@NotNull IfExpressionPsi element) { visitTermPsi(element); }
    public void visitObjectExpressionPsi(@NotNull ObjectExpressionPsi element) { visitPrimaryPsi(element); }
    public void visitFunctionArgumentPsi(@NotNull FunctionArgumentPsi element) { visitTermPsi(element); }
    public void visitSpreadArgumentPsi(@NotNull SpreadArgumentPsi element) { visitPositionalArgumentPsi(element); }
    public void visitListedArgumentPsi(@NotNull ListedArgumentPsi element) { visitPositionalArgumentPsi(element); }
    public void visitPositionalArgumentPsi(@NotNull PositionalArgumentPsi element) {}
    public void visitPositionalArgumentListPsi(@NotNull PositionalArgumentListPsi element) { visitArgumentListPsi(element); }
    public void visitSequencedArgumentPsi(@NotNull SequencedArgumentPsi element) { visitStatementOrArgumentPsi(element); }
    public void visitNamedArgumentListPsi(@NotNull NamedArgumentListPsi element) { visitArgumentListPsi(element); }
    public void visitArgumentListPsi(@NotNull ArgumentListPsi element) {}
    public void visitPackagePsi(@NotNull PackagePsi element) { visitAtomPsi(element); }
    public void visitOuterPsi(@NotNull OuterPsi element) { visitAtomPsi(element); }
    public void visitElementRangePsi(@NotNull ElementRangePsi element) { visitElementOrRangePsi(element); }
    public void visitElementPsi(@NotNull ElementPsi element) { visitElementOrRangePsi(element); }
    public void visitElementOrRangePsi(@NotNull ElementOrRangePsi element) {}
    public void visitIndexExpressionPsi(@NotNull IndexExpressionPsi element) { visitPostfixExpressionPsi(element); }
    public void visitSpreadOpPsi(@NotNull SpreadOpPsi element) { visitMemberOperatorPsi(element); }
    public void visitSafeMemberOpPsi(@NotNull SafeMemberOpPsi element) { visitMemberOperatorPsi(element); }
    public void visitMemberOpPsi(@NotNull MemberOpPsi element) { visitMemberOperatorPsi(element); }
    public void visitMemberOperatorPsi(@NotNull MemberOperatorPsi element) {}
    public void visitQualifiedTypeExpressionPsi(@NotNull QualifiedTypeExpressionPsi element) { visitQualifiedMemberOrTypeExpressionPsi(element); }
    public void visitQualifiedMemberExpressionPsi(@NotNull QualifiedMemberExpressionPsi element) { visitQualifiedMemberOrTypeExpressionPsi(element); }
    public void visitQualifiedMemberOrTypeExpressionPsi(@NotNull QualifiedMemberOrTypeExpressionPsi element) { visitStaticMemberOrTypeExpressionPsi(element); }
    public void visitBaseTypeExpressionPsi(@NotNull BaseTypeExpressionPsi element) { visitBaseMemberOrTypeExpressionPsi(element); }
    public void visitBaseMemberExpressionPsi(@NotNull BaseMemberExpressionPsi element) { visitBaseMemberOrTypeExpressionPsi(element); }
    public void visitBaseMemberOrTypeExpressionPsi(@NotNull BaseMemberOrTypeExpressionPsi element) { visitStaticMemberOrTypeExpressionPsi(element); }
    public void visitStaticMemberOrTypeExpressionPsi(@NotNull StaticMemberOrTypeExpressionPsi element) { visitMemberOrTypeExpressionPsi(element); }
    public void visitExtendedTypeExpressionPsi(@NotNull ExtendedTypeExpressionPsi element) { visitMemberOrTypeExpressionPsi(element); }
    public void visitMemberOrTypeExpressionPsi(@NotNull MemberOrTypeExpressionPsi element) { visitPrimaryPsi(element); }
    public void visitParameterizedExpressionPsi(@NotNull ParameterizedExpressionPsi element) { visitPrimaryPsi(element); }
    public void visitInvocationExpressionPsi(@NotNull InvocationExpressionPsi element) { visitPostfixExpressionPsi(element); }
    public void visitPostfixExpressionPsi(@NotNull PostfixExpressionPsi element) { visitPrimaryPsi(element); }
    public void visitPrimaryPsi(@NotNull PrimaryPsi element) { visitTermPsi(element); }
    public void visitExpressionPsi(@NotNull ExpressionPsi element) { visitAtomPsi(element); }
    public void visitExpressionListPsi(@NotNull ExpressionListPsi element) {}
    public void visitPostfixDecrementOpPsi(@NotNull PostfixDecrementOpPsi element) { visitPostfixOperatorExpressionPsi(element); }
    public void visitPostfixIncrementOpPsi(@NotNull PostfixIncrementOpPsi element) { visitPostfixOperatorExpressionPsi(element); }
    public void visitPostfixOperatorExpressionPsi(@NotNull PostfixOperatorExpressionPsi element) { visitUnaryOperatorExpressionPsi(element); }
    public void visitDecrementOpPsi(@NotNull DecrementOpPsi element) { visitPrefixOperatorExpressionPsi(element); }
    public void visitIncrementOpPsi(@NotNull IncrementOpPsi element) { visitPrefixOperatorExpressionPsi(element); }
    public void visitPrefixOperatorExpressionPsi(@NotNull PrefixOperatorExpressionPsi element) { visitUnaryOperatorExpressionPsi(element); }
    public void visitOfOpPsi(@NotNull OfOpPsi element) { visitTypeOperatorExpressionPsi(element); }
    public void visitExtendsPsi(@NotNull ExtendsPsi element) { visitTypeOperatorExpressionPsi(element); }
    public void visitSatisfiesPsi(@NotNull SatisfiesPsi element) { visitTypeOperatorExpressionPsi(element); }
    public void visitIsOpPsi(@NotNull IsOpPsi element) { visitTypeOperatorExpressionPsi(element); }
    public void visitTypeOperatorExpressionPsi(@NotNull TypeOperatorExpressionPsi element) { visitUnaryOperatorExpressionPsi(element); }
    public void visitPositiveOpPsi(@NotNull PositiveOpPsi element) { visitUnaryOperatorExpressionPsi(element); }
    public void visitNegativeOpPsi(@NotNull NegativeOpPsi element) { visitUnaryOperatorExpressionPsi(element); }
    public void visitNonemptyPsi(@NotNull NonemptyPsi element) { visitUnaryOperatorExpressionPsi(element); }
    public void visitExistsPsi(@NotNull ExistsPsi element) { visitUnaryOperatorExpressionPsi(element); }
    public void visitNotOpPsi(@NotNull NotOpPsi element) { visitUnaryOperatorExpressionPsi(element); }
    public void visitUnaryOperatorExpressionPsi(@NotNull UnaryOperatorExpressionPsi element) { visitOperatorExpressionPsi(element); }
    public void visitInOpPsi(@NotNull InOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitCompareOpPsi(@NotNull CompareOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitSegmentOpPsi(@NotNull SegmentOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitRangeOpPsi(@NotNull RangeOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitEntryOpPsi(@NotNull EntryOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitIdenticalOpPsi(@NotNull IdenticalOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitThenOpPsi(@NotNull ThenOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitDefaultOpPsi(@NotNull DefaultOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitWithinOpPsi(@NotNull WithinOpPsi element) { visitOperatorExpressionPsi(element); }
    public void visitClosedBoundPsi(@NotNull ClosedBoundPsi element) { visitBoundPsi(element); }
    public void visitOpenBoundPsi(@NotNull OpenBoundPsi element) { visitBoundPsi(element); }
    public void visitBoundPsi(@NotNull BoundPsi element) { visitTermPsi(element); }
    public void visitScaleOpPsi(@NotNull ScaleOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitSmallAsOpPsi(@NotNull SmallAsOpPsi element) { visitComparisonOpPsi(element); }
    public void visitLargeAsOpPsi(@NotNull LargeAsOpPsi element) { visitComparisonOpPsi(element); }
    public void visitSmallerOpPsi(@NotNull SmallerOpPsi element) { visitComparisonOpPsi(element); }
    public void visitLargerOpPsi(@NotNull LargerOpPsi element) { visitComparisonOpPsi(element); }
    public void visitComparisonOpPsi(@NotNull ComparisonOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitNotEqualOpPsi(@NotNull NotEqualOpPsi element) { visitEqualityOpPsi(element); }
    public void visitEqualOpPsi(@NotNull EqualOpPsi element) { visitEqualityOpPsi(element); }
    public void visitEqualityOpPsi(@NotNull EqualityOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitComplementOpPsi(@NotNull ComplementOpPsi element) { visitBitwiseOpPsi(element); }
    public void visitUnionOpPsi(@NotNull UnionOpPsi element) { visitBitwiseOpPsi(element); }
    public void visitIntersectionOpPsi(@NotNull IntersectionOpPsi element) { visitBitwiseOpPsi(element); }
    public void visitBitwiseOpPsi(@NotNull BitwiseOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitOrOpPsi(@NotNull OrOpPsi element) { visitLogicalOpPsi(element); }
    public void visitAndOpPsi(@NotNull AndOpPsi element) { visitLogicalOpPsi(element); }
    public void visitLogicalOpPsi(@NotNull LogicalOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitOrAssignOpPsi(@NotNull OrAssignOpPsi element) { visitLogicalAssignmentOpPsi(element); }
    public void visitAndAssignOpPsi(@NotNull AndAssignOpPsi element) { visitLogicalAssignmentOpPsi(element); }
    public void visitLogicalAssignmentOpPsi(@NotNull LogicalAssignmentOpPsi element) { visitAssignmentOpPsi(element); }
    public void visitComplementAssignOpPsi(@NotNull ComplementAssignOpPsi element) { visitBitwiseAssignmentOpPsi(element); }
    public void visitUnionAssignOpPsi(@NotNull UnionAssignOpPsi element) { visitBitwiseAssignmentOpPsi(element); }
    public void visitIntersectAssignOpPsi(@NotNull IntersectAssignOpPsi element) { visitBitwiseAssignmentOpPsi(element); }
    public void visitBitwiseAssignmentOpPsi(@NotNull BitwiseAssignmentOpPsi element) { visitAssignmentOpPsi(element); }
    public void visitRemainderAssignOpPsi(@NotNull RemainderAssignOpPsi element) { visitArithmeticAssignmentOpPsi(element); }
    public void visitDivideAssignOpPsi(@NotNull DivideAssignOpPsi element) { visitArithmeticAssignmentOpPsi(element); }
    public void visitMultiplyAssignOpPsi(@NotNull MultiplyAssignOpPsi element) { visitArithmeticAssignmentOpPsi(element); }
    public void visitSubtractAssignOpPsi(@NotNull SubtractAssignOpPsi element) { visitArithmeticAssignmentOpPsi(element); }
    public void visitAddAssignOpPsi(@NotNull AddAssignOpPsi element) { visitArithmeticAssignmentOpPsi(element); }
    public void visitArithmeticAssignmentOpPsi(@NotNull ArithmeticAssignmentOpPsi element) { visitAssignmentOpPsi(element); }
    public void visitAssignOpPsi(@NotNull AssignOpPsi element) { visitAssignmentOpPsi(element); }
    public void visitAssignmentOpPsi(@NotNull AssignmentOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitRemainderOpPsi(@NotNull RemainderOpPsi element) { visitArithmeticOpPsi(element); }
    public void visitPowerOpPsi(@NotNull PowerOpPsi element) { visitArithmeticOpPsi(element); }
    public void visitQuotientOpPsi(@NotNull QuotientOpPsi element) { visitArithmeticOpPsi(element); }
    public void visitProductOpPsi(@NotNull ProductOpPsi element) { visitArithmeticOpPsi(element); }
    public void visitDifferenceOpPsi(@NotNull DifferenceOpPsi element) { visitArithmeticOpPsi(element); }
    public void visitSumOpPsi(@NotNull SumOpPsi element) { visitArithmeticOpPsi(element); }
    public void visitArithmeticOpPsi(@NotNull ArithmeticOpPsi element) { visitBinaryOperatorExpressionPsi(element); }
    public void visitBinaryOperatorExpressionPsi(@NotNull BinaryOperatorExpressionPsi element) { visitOperatorExpressionPsi(element); }
    public void visitOperatorExpressionPsi(@NotNull OperatorExpressionPsi element) { visitTermPsi(element); }
    public void visitTermPsi(@NotNull TermPsi element) {}
    public void visitVariablePsi(@NotNull VariablePsi element) { visitTypedDeclarationPsi(element); }
    public void visitSatisfiesConditionPsi(@NotNull SatisfiesConditionPsi element) { visitConditionPsi(element); }
    public void visitIsConditionPsi(@NotNull IsConditionPsi element) { visitConditionPsi(element); }
    public void visitNonemptyConditionPsi(@NotNull NonemptyConditionPsi element) { visitExistsOrNonemptyConditionPsi(element); }
    public void visitExistsConditionPsi(@NotNull ExistsConditionPsi element) { visitExistsOrNonemptyConditionPsi(element); }
    public void visitExistsOrNonemptyConditionPsi(@NotNull ExistsOrNonemptyConditionPsi element) { visitConditionPsi(element); }
    public void visitBooleanConditionPsi(@NotNull BooleanConditionPsi element) { visitConditionPsi(element); }
    public void visitConditionPsi(@NotNull ConditionPsi element) {}
    public void visitConditionListPsi(@NotNull ConditionListPsi element) {}
    public void visitWhileClausePsi(@NotNull WhileClausePsi element) { visitControlClausePsi(element); }
    public void visitWhileStatementPsi(@NotNull WhileStatementPsi element) { visitControlStatementPsi(element); }
    public void visitPatternIteratorPsi(@NotNull PatternIteratorPsi element) { visitForIteratorPsi(element); }
    public void visitValueIteratorPsi(@NotNull ValueIteratorPsi element) { visitForIteratorPsi(element); }
    public void visitForIteratorPsi(@NotNull ForIteratorPsi element) { visitStatementOrArgumentPsi(element); }
    public void visitForClausePsi(@NotNull ForClausePsi element) { visitControlClausePsi(element); }
    public void visitForStatementPsi(@NotNull ForStatementPsi element) { visitControlStatementPsi(element); }
    public void visitCatchVariablePsi(@NotNull CatchVariablePsi element) {}
    public void visitResourcePsi(@NotNull ResourcePsi element) {}
    public void visitResourceListPsi(@NotNull ResourceListPsi element) {}
    public void visitFinallyClausePsi(@NotNull FinallyClausePsi element) { visitControlClausePsi(element); }
    public void visitCatchClausePsi(@NotNull CatchClausePsi element) { visitControlClausePsi(element); }
    public void visitTryClausePsi(@NotNull TryClausePsi element) { visitControlClausePsi(element); }
    public void visitTryCatchStatementPsi(@NotNull TryCatchStatementPsi element) { visitControlStatementPsi(element); }
    public void visitSatisfiesCasePsi(@NotNull SatisfiesCasePsi element) { visitCaseItemPsi(element); }
    public void visitIsCasePsi(@NotNull IsCasePsi element) { visitCaseItemPsi(element); }
    public void visitMatchCasePsi(@NotNull MatchCasePsi element) { visitCaseItemPsi(element); }
    public void visitCaseItemPsi(@NotNull CaseItemPsi element) {}
    public void visitCaseClausePsi(@NotNull CaseClausePsi element) { visitControlClausePsi(element); }
    public void visitSwitchCaseListPsi(@NotNull SwitchCaseListPsi element) {}
    public void visitSwitchedPsi(@NotNull SwitchedPsi element) {}
    public void visitSwitchClausePsi(@NotNull SwitchClausePsi element) {}
    public void visitSwitchStatementPsi(@NotNull SwitchStatementPsi element) { visitControlStatementPsi(element); }
    public void visitElseClausePsi(@NotNull ElseClausePsi element) { visitControlClausePsi(element); }
    public void visitIfClausePsi(@NotNull IfClausePsi element) { visitControlClausePsi(element); }
    public void visitIfStatementPsi(@NotNull IfStatementPsi element) { visitControlStatementPsi(element); }
    public void visitLetClausePsi(@NotNull LetClausePsi element) { visitControlClausePsi(element); }
    public void visitLetExpressionPsi(@NotNull LetExpressionPsi element) { visitTermPsi(element); }
    public void visitDynamicClausePsi(@NotNull DynamicClausePsi element) { visitControlClausePsi(element); }
    public void visitDynamicStatementPsi(@NotNull DynamicStatementPsi element) { visitControlStatementPsi(element); }
    public void visitControlClausePsi(@NotNull ControlClausePsi element) {}
    public void visitControlStatementPsi(@NotNull ControlStatementPsi element) { visitExecutableStatementPsi(element); }
    public void visitDestructurePsi(@NotNull DestructurePsi element) { visitExecutableStatementPsi(element); }
    public void visitKeyValuePatternPsi(@NotNull KeyValuePatternPsi element) { visitPatternPsi(element); }
    public void visitTuplePatternPsi(@NotNull TuplePatternPsi element) { visitPatternPsi(element); }
    public void visitVariablePatternPsi(@NotNull VariablePatternPsi element) { visitPatternPsi(element); }
    public void visitPatternPsi(@NotNull PatternPsi element) {}
    public void visitExpressionStatementPsi(@NotNull ExpressionStatementPsi element) { visitExecutableStatementPsi(element); }
    public void visitSpecifierStatementPsi(@NotNull SpecifierStatementPsi element) { visitExecutableStatementPsi(element); }
    public void visitAssertionPsi(@NotNull AssertionPsi element) { visitExecutableStatementPsi(element); }
    public void visitExecutableStatementPsi(@NotNull ExecutableStatementPsi element) { visitStatementPsi(element); }
    public void visitCompilerAnnotationPsi(@NotNull CompilerAnnotationPsi element) {}
    public void visitStatementPsi(@NotNull StatementPsi element) { visitStatementOrArgumentPsi(element); }
    public void visitStatementOrArgumentPsi(@NotNull StatementOrArgumentPsi element) {}
    public void visitBreakPsi(@NotNull BreakPsi element) { visitDirectivePsi(element); }
    public void visitContinuePsi(@NotNull ContinuePsi element) { visitDirectivePsi(element); }
    public void visitThrowPsi(@NotNull ThrowPsi element) { visitDirectivePsi(element); }
    public void visitReturnPsi(@NotNull ReturnPsi element) { visitDirectivePsi(element); }
    public void visitDirectivePsi(@NotNull DirectivePsi element) { visitExecutableStatementPsi(element); }
    public void visitSpreadTypePsi(@NotNull SpreadTypePsi element) { visitTypePsi(element); }
    public void visitDefaultedTypePsi(@NotNull DefaultedTypePsi element) { visitTypePsi(element); }
    public void visitSequencedTypePsi(@NotNull SequencedTypePsi element) { visitTypePsi(element); }
    public void visitInferredTypeArgumentsPsi(@NotNull InferredTypeArgumentsPsi element) { visitTypeArgumentsPsi(element); }
    public void visitTypeArgumentListPsi(@NotNull TypeArgumentListPsi element) { visitTypeArgumentsPsi(element); }
    public void visitTypeArgumentsPsi(@NotNull TypeArgumentsPsi element) {}
    public void visitSyntheticVariablePsi(@NotNull SyntheticVariablePsi element) { visitValueModifierPsi(element); }
    public void visitFunctionModifierPsi(@NotNull FunctionModifierPsi element) { visitLocalModifierPsi(element); }
    public void visitValueModifierPsi(@NotNull ValueModifierPsi element) { visitLocalModifierPsi(element); }
    public void visitLocalModifierPsi(@NotNull LocalModifierPsi element) { visitTypePsi(element); }
    public void visitDynamicModifierPsi(@NotNull DynamicModifierPsi element) { visitTypePsi(element); }
    public void visitPackageLiteralPsi(@NotNull PackageLiteralPsi element) { visitMetaLiteralPsi(element); }
    public void visitModuleLiteralPsi(@NotNull ModuleLiteralPsi element) { visitMetaLiteralPsi(element); }
    public void visitFunctionLiteralPsi(@NotNull FunctionLiteralPsi element) { visitMemberLiteralPsi(element); }
    public void visitValueLiteralPsi(@NotNull ValueLiteralPsi element) { visitMemberLiteralPsi(element); }
    public void visitTypeParameterLiteralPsi(@NotNull TypeParameterLiteralPsi element) { visitTypeLiteralPsi(element); }
    public void visitAliasLiteralPsi(@NotNull AliasLiteralPsi element) { visitTypeLiteralPsi(element); }
    public void visitInterfaceLiteralPsi(@NotNull InterfaceLiteralPsi element) { visitTypeLiteralPsi(element); }
    public void visitNewLiteralPsi(@NotNull NewLiteralPsi element) { visitTypeLiteralPsi(element); }
    public void visitClassLiteralPsi(@NotNull ClassLiteralPsi element) { visitTypeLiteralPsi(element); }
    public void visitMemberLiteralPsi(@NotNull MemberLiteralPsi element) { visitMetaLiteralPsi(element); }
    public void visitTypeLiteralPsi(@NotNull TypeLiteralPsi element) { visitMetaLiteralPsi(element); }
    public void visitMetaLiteralPsi(@NotNull MetaLiteralPsi element) { visitPrimaryPsi(element); }
    public void visitSuperTypePsi(@NotNull SuperTypePsi element) { visitStaticTypePsi(element); }
    public void visitTypeConstructorPsi(@NotNull TypeConstructorPsi element) { visitStaticTypePsi(element); }
    public void visitEntryTypePsi(@NotNull EntryTypePsi element) { visitStaticTypePsi(element); }
    public void visitFunctionTypePsi(@NotNull FunctionTypePsi element) { visitStaticTypePsi(element); }
    public void visitTupleTypePsi(@NotNull TupleTypePsi element) { visitStaticTypePsi(element); }
    public void visitOptionalTypePsi(@NotNull OptionalTypePsi element) { visitStaticTypePsi(element); }
    public void visitIterableTypePsi(@NotNull IterableTypePsi element) { visitStaticTypePsi(element); }
    public void visitSequenceTypePsi(@NotNull SequenceTypePsi element) { visitStaticTypePsi(element); }
    public void visitIntersectionTypePsi(@NotNull IntersectionTypePsi element) { visitStaticTypePsi(element); }
    public void visitUnionTypePsi(@NotNull UnionTypePsi element) { visitStaticTypePsi(element); }
    public void visitQualifiedTypePsi(@NotNull QualifiedTypePsi element) { visitSimpleTypePsi(element); }
    public void visitBaseTypePsi(@NotNull BaseTypePsi element) { visitSimpleTypePsi(element); }
    public void visitSimpleTypePsi(@NotNull SimpleTypePsi element) { visitStaticTypePsi(element); }
    public void visitGroupedTypePsi(@NotNull GroupedTypePsi element) { visitStaticTypePsi(element); }
    public void visitStaticTypePsi(@NotNull StaticTypePsi element) { visitTypePsi(element); }
    public void visitTypePsi(@NotNull TypePsi element) {}
    public void visitInterfaceBodyPsi(@NotNull InterfaceBodyPsi element) { visitBodyPsi(element); }
    public void visitClassBodyPsi(@NotNull ClassBodyPsi element) { visitBodyPsi(element); }
    public void visitBlockPsi(@NotNull BlockPsi element) { visitBodyPsi(element); }
    public void visitBodyPsi(@NotNull BodyPsi element) {}
    public void visitTypeVariancePsi(@NotNull TypeVariancePsi element) {}
    public void visitTypeParameterDeclarationPsi(@NotNull TypeParameterDeclarationPsi element) { visitDeclarationPsi(element); }
    public void visitTypeParameterListPsi(@NotNull TypeParameterListPsi element) {}
    public void visitInitializerParameterPsi(@NotNull InitializerParameterPsi element) { visitParameterPsi(element); }
    public void visitFunctionalParameterDeclarationPsi(@NotNull FunctionalParameterDeclarationPsi element) { visitParameterDeclarationPsi(element); }
    public void visitValueParameterDeclarationPsi(@NotNull ValueParameterDeclarationPsi element) { visitParameterDeclarationPsi(element); }
    public void visitParameterDeclarationPsi(@NotNull ParameterDeclarationPsi element) { visitParameterPsi(element); }
    public void visitParameterPsi(@NotNull ParameterPsi element) {}
    public void visitParameterListPsi(@NotNull ParameterListPsi element) {}
    public void visitObjectDefinitionPsi(@NotNull ObjectDefinitionPsi element) { visitTypedDeclarationPsi(element); }
    public void visitVoidModifierPsi(@NotNull VoidModifierPsi element) { visitTypePsi(element); }
    public void visitMethodDeclarationPsi(@NotNull MethodDeclarationPsi element) { visitAnyMethodPsi(element); }
    public void visitMethodDefinitionPsi(@NotNull MethodDefinitionPsi element) { visitAnyMethodPsi(element); }
    public void visitAnyMethodPsi(@NotNull AnyMethodPsi element) { visitTypedDeclarationPsi(element); }
    public void visitAttributeSetterDefinitionPsi(@NotNull AttributeSetterDefinitionPsi element) { visitTypedDeclarationPsi(element); }
    public void visitAttributeGetterDefinitionPsi(@NotNull AttributeGetterDefinitionPsi element) { visitAnyAttributePsi(element); }
    public void visitAttributeDeclarationPsi(@NotNull AttributeDeclarationPsi element) { visitAnyAttributePsi(element); }
    public void visitAnyAttributePsi(@NotNull AnyAttributePsi element) { visitTypedDeclarationPsi(element); }
    public void visitTypedDeclarationPsi(@NotNull TypedDeclarationPsi element) { visitDeclarationPsi(element); }
    public void visitInterfaceDeclarationPsi(@NotNull InterfaceDeclarationPsi element) { visitAnyInterfacePsi(element); }
    public void visitInterfaceDefinitionPsi(@NotNull InterfaceDefinitionPsi element) { visitAnyInterfacePsi(element); }
    public void visitAnyInterfacePsi(@NotNull AnyInterfacePsi element) { visitClassOrInterfacePsi(element); }
    public void visitClassDeclarationPsi(@NotNull ClassDeclarationPsi element) { visitAnyClassPsi(element); }
    public void visitDelegatedConstructorPsi(@NotNull DelegatedConstructorPsi element) {}
    public void visitConstructorPsi(@NotNull ConstructorPsi element) { visitDeclarationPsi(element); }
    public void visitEnumeratedPsi(@NotNull EnumeratedPsi element) { visitDeclarationPsi(element); }
    public void visitClassDefinitionPsi(@NotNull ClassDefinitionPsi element) { visitAnyClassPsi(element); }
    public void visitAnyClassPsi(@NotNull AnyClassPsi element) { visitClassOrInterfacePsi(element); }
    public void visitClassSpecifierPsi(@NotNull ClassSpecifierPsi element) {}
    public void visitDefaultTypeArgumentPsi(@NotNull DefaultTypeArgumentPsi element) { visitTypeSpecifierPsi(element); }
    public void visitTypeSpecifierPsi(@NotNull TypeSpecifierPsi element) {}
    public void visitTypeConstraintPsi(@NotNull TypeConstraintPsi element) { visitTypeDeclarationPsi(element); }
    public void visitTypeConstraintListPsi(@NotNull TypeConstraintListPsi element) {}
    public void visitExtendedTypePsi(@NotNull ExtendedTypePsi element) {}
    public void visitCaseTypesPsi(@NotNull CaseTypesPsi element) {}
    public void visitAbstractedTypePsi(@NotNull AbstractedTypePsi element) {}
    public void visitSatisfiedTypesPsi(@NotNull SatisfiedTypesPsi element) {}
    public void visitTypeAliasDeclarationPsi(@NotNull TypeAliasDeclarationPsi element) { visitTypeDeclarationPsi(element); }
    public void visitClassOrInterfacePsi(@NotNull ClassOrInterfacePsi element) { visitTypeDeclarationPsi(element); }
    public void visitTypeDeclarationPsi(@NotNull TypeDeclarationPsi element) { visitDeclarationPsi(element); }
    public void visitMissingDeclarationPsi(@NotNull MissingDeclarationPsi element) { visitDeclarationPsi(element); }
    public void visitDeclarationPsi(@NotNull DeclarationPsi element) { visitStatementPsi(element); }
    public void visitImportWildcardPsi(@NotNull ImportWildcardPsi element) {}
    public void visitAliasPsi(@NotNull AliasPsi element) {}
    public void visitImportTypePsi(@NotNull ImportTypePsi element) { visitImportMemberOrTypePsi(element); }
    public void visitImportMemberPsi(@NotNull ImportMemberPsi element) { visitImportMemberOrTypePsi(element); }
    public void visitImportMemberOrTypePsi(@NotNull ImportMemberOrTypePsi element) { visitStatementOrArgumentPsi(element); }
    public void visitImportMemberOrTypeListPsi(@NotNull ImportMemberOrTypeListPsi element) {}
    public void visitImportPathPsi(@NotNull ImportPathPsi element) {}
    public void visitImportPsi(@NotNull ImportPsi element) { visitStatementOrArgumentPsi(element); }
    public void visitImportListPsi(@NotNull ImportListPsi element) {}
    public void visitImportModulePsi(@NotNull ImportModulePsi element) { visitStatementOrArgumentPsi(element); }
    public void visitImportModuleListPsi(@NotNull ImportModuleListPsi element) {}
    public void visitPackageDescriptorPsi(@NotNull PackageDescriptorPsi element) { visitStatementOrArgumentPsi(element); }
    public void visitModuleDescriptorPsi(@NotNull ModuleDescriptorPsi element) { visitStatementOrArgumentPsi(element); }
    public void visitCompilationUnitPsi(@NotNull CompilationUnitPsi element) {}
}
