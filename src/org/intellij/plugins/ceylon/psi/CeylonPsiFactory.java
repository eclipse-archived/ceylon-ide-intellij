package org.intellij.plugins.ceylon.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import org.intellij.plugins.ceylon.psi.impl.*;

import org.intellij.plugins.ceylon.psi.CeylonPsi.*;

import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;

/* Generated using Antlr by PsiFactoryGen.g */
public class CeylonPsiFactory {

    public static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if (false) {
        } else if (type == COMPILATION_UNIT) {
            return new CompilationUnitPsi(node);
        } else if (type == MODULE_DESCRIPTOR) {
            return new ModuleDescriptorPsi(node);
        } else if (type == PACKAGE_DESCRIPTOR) {
            return new PackageDescriptorPsi(node);
        } else if (type == IMPORT_MODULE_LIST) {
            return new ImportModuleListPsi(node);
        } else if (type == IMPORT_MODULE) {
            return new ImportModulePsi(node);
        } else if (type == IMPORT_LIST) {
            return new ImportListPsi(node);
        } else if (type == IMPORT) {
            return new ImportPsi(node);
        } else if (type == IMPORT_PATH) {
            return new ImportPathPsi(node);
        } else if (type == IMPORT_MEMBER_OR_TYPE_LIST) {
            return new ImportMemberOrTypeListPsi(node);
        } else if (type == IMPORT_MEMBER_OR_TYPE) {
            return new ImportMemberOrTypePsi(node);
        } else if (type == IMPORT_MEMBER) {
            return new ImportMemberPsi(node);
        } else if (type == IMPORT_TYPE) {
            return new ImportTypePsi(node);
        } else if (type == ALIAS) {
            return new AliasPsi(node);
        } else if (type == IMPORT_WILDCARD) {
            return new ImportWildcardPsi(node);
        } else if (type == MISSING_DECLARATION) {
            return new MissingDeclarationPsi(node);
        } else if (type == TYPE_ALIAS_DECLARATION) {
            return new TypeAliasDeclarationPsi(node);
        } else if (type == SATISFIED_TYPES) {
            return new SatisfiedTypesPsi(node);
        } else if (type == ABSTRACTED_TYPE) {
            return new AbstractedTypePsi(node);
        } else if (type == ADAPTED_TYPES) {
            return new AdaptedTypesPsi(node);
        } else if (type == CASE_TYPES) {
            return new CaseTypesPsi(node);
        } else if (type == EXTENDED_TYPE) {
            return new ExtendedTypePsi(node);
        } else if (type == TYPE_CONSTRAINT_LIST) {
            return new TypeConstraintListPsi(node);
        } else if (type == TYPE_CONSTRAINT) {
            return new TypeConstraintPsi(node);
        } else if (type == TYPE_SPECIFIER) {
            return new TypeSpecifierPsi(node);
        } else if (type == DEFAULT_TYPE_ARGUMENT) {
            return new DefaultTypeArgumentPsi(node);
        } else if (type == CLASS_SPECIFIER) {
            return new ClassSpecifierPsi(node);
        } else if (type == ANY_CLASS) {
            return new AnyClassPsi(node);
        } else if (type == CLASS_DEFINITION) {
            return new ClassDefinitionPsi(node);
        } else if (type == CLASS_DECLARATION) {
            return new ClassDeclarationPsi(node);
        } else if (type == ANY_INTERFACE) {
            return new AnyInterfacePsi(node);
        } else if (type == INTERFACE_DEFINITION) {
            return new InterfaceDefinitionPsi(node);
        } else if (type == INTERFACE_DECLARATION) {
            return new InterfaceDeclarationPsi(node);
        } else if (type == ATTRIBUTE_DECLARATION) {
            return new AttributeDeclarationPsi(node);
        } else if (type == ATTRIBUTE_GETTER_DEFINITION) {
            return new AttributeGetterDefinitionPsi(node);
        } else if (type == ATTRIBUTE_SETTER_DEFINITION) {
            return new AttributeSetterDefinitionPsi(node);
        } else if (type == ANY_METHOD) {
            return new AnyMethodPsi(node);
        } else if (type == METHOD_DEFINITION) {
            return new MethodDefinitionPsi(node);
        } else if (type == METHOD_DECLARATION) {
            return new MethodDeclarationPsi(node);
        } else if (type == VOID_MODIFIER) {
            return new VoidModifierPsi(node);
        } else if (type == OBJECT_DEFINITION) {
            return new ObjectDefinitionPsi(node);
        } else if (type == PARAMETER_LIST) {
            return new ParameterListPsi(node);
        } else if (type == VALUE_PARAMETER_DECLARATION) {
            return new ValueParameterDeclarationPsi(node);
        } else if (type == FUNCTIONAL_PARAMETER_DECLARATION) {
            return new FunctionalParameterDeclarationPsi(node);
        } else if (type == INITIALIZER_PARAMETER) {
            return new InitializerParameterPsi(node);
        } else if (type == TYPE_PARAMETER_LIST) {
            return new TypeParameterListPsi(node);
        } else if (type == TYPE_PARAMETER_DECLARATION) {
            return new TypeParameterDeclarationPsi(node);
        } else if (type == TYPE_VARIANCE) {
            return new TypeVariancePsi(node);
        } else if (type == BLOCK) {
            return new BlockPsi(node);
        } else if (type == CLASS_BODY) {
            return new ClassBodyPsi(node);
        } else if (type == INTERFACE_BODY) {
            return new InterfaceBodyPsi(node);
        } else if (type == BASE_TYPE) {
            return new BaseTypePsi(node);
        } else if (type == UNION_TYPE) {
            return new UnionTypePsi(node);
        } else if (type == INTERSECTION_TYPE) {
            return new IntersectionTypePsi(node);
        } else if (type == SEQUENCE_TYPE) {
            return new SequenceTypePsi(node);
        } else if (type == ITERABLE_TYPE) {
            return new IterableTypePsi(node);
        } else if (type == OPTIONAL_TYPE) {
            return new OptionalTypePsi(node);
        } else if (type == TUPLE_TYPE) {
            return new TupleTypePsi(node);
        } else if (type == FUNCTION_TYPE) {
            return new FunctionTypePsi(node);
        } else if (type == ENTRY_TYPE) {
            return new EntryTypePsi(node);
        } else if (type == QUALIFIED_TYPE) {
            return new QualifiedTypePsi(node);
        } else if (type == SUPER_TYPE) {
            return new SuperTypePsi(node);
        } else if (type == TYPE_LITERAL) {
            return new TypeLiteralPsi(node);
        } else if (type == MEMBER_LITERAL) {
            return new MemberLiteralPsi(node);
        } else if (type == CLASS_LITERAL) {
            return new ClassLiteralPsi(node);
        } else if (type == INTERFACE_LITERAL) {
            return new InterfaceLiteralPsi(node);
        } else if (type == ALIAS_LITERAL) {
            return new AliasLiteralPsi(node);
        } else if (type == TYPE_PARAMETER_LITERAL) {
            return new TypeParameterLiteralPsi(node);
        } else if (type == VALUE_LITERAL) {
            return new ValueLiteralPsi(node);
        } else if (type == FUNCTION_LITERAL) {
            return new FunctionLiteralPsi(node);
        } else if (type == MODULE_LITERAL) {
            return new ModuleLiteralPsi(node);
        } else if (type == PACKAGE_LITERAL) {
            return new PackageLiteralPsi(node);
        } else if (type == DYNAMIC_MODIFIER) {
            return new DynamicModifierPsi(node);
        } else if (type == VALUE_MODIFIER) {
            return new ValueModifierPsi(node);
        } else if (type == FUNCTION_MODIFIER) {
            return new FunctionModifierPsi(node);
        } else if (type == SYNTHETIC_VARIABLE) {
            return new SyntheticVariablePsi(node);
        } else if (type == TYPE_ARGUMENTS) {
            return new TypeArgumentsPsi(node);
        } else if (type == TYPE_ARGUMENT_LIST) {
            return new TypeArgumentListPsi(node);
        } else if (type == INFERRED_TYPE_ARGUMENTS) {
            return new InferredTypeArgumentsPsi(node);
        } else if (type == SEQUENCED_TYPE) {
            return new SequencedTypePsi(node);
        } else if (type == DEFAULTED_TYPE) {
            return new DefaultedTypePsi(node);
        } else if (type == RETURN) {
            return new ReturnPsi(node);
        } else if (type == THROW) {
            return new ThrowPsi(node);
        } else if (type == CONTINUE) {
            return new ContinuePsi(node);
        } else if (type == BREAK) {
            return new BreakPsi(node);
        } else if (type == COMPILER_ANNOTATION) {
            return new CompilerAnnotationPsi(node);
        } else if (type == ASSERTION) {
            return new AssertionPsi(node);
        } else if (type == SPECIFIER_STATEMENT) {
            return new SpecifierStatementPsi(node);
        } else if (type == EXPRESSION_STATEMENT) {
            return new ExpressionStatementPsi(node);
        } else if (type == CONTROL_STATEMENT) {
            return new ControlStatementPsi(node);
        } else if (type == CONTROL_CLAUSE) {
            return new ControlClausePsi(node);
        } else if (type == DYNAMIC_STATEMENT) {
            return new DynamicStatementPsi(node);
        } else if (type == DYNAMIC_CLAUSE) {
            return new DynamicClausePsi(node);
        } else if (type == IF_STATEMENT) {
            return new IfStatementPsi(node);
        } else if (type == IF_CLAUSE) {
            return new IfClausePsi(node);
        } else if (type == ELSE_CLAUSE) {
            return new ElseClausePsi(node);
        } else if (type == SWITCH_STATEMENT) {
            return new SwitchStatementPsi(node);
        } else if (type == SWITCH_CLAUSE) {
            return new SwitchClausePsi(node);
        } else if (type == SWITCH_CASE_LIST) {
            return new SwitchCaseListPsi(node);
        } else if (type == CASE_CLAUSE) {
            return new CaseClausePsi(node);
        } else if (type == CASE_ITEM) {
            return new CaseItemPsi(node);
        } else if (type == MATCH_CASE) {
            return new MatchCasePsi(node);
        } else if (type == IS_CASE) {
            return new IsCasePsi(node);
        } else if (type == SATISFIES_CASE) {
            return new SatisfiesCasePsi(node);
        } else if (type == TRY_CATCH_STATEMENT) {
            return new TryCatchStatementPsi(node);
        } else if (type == TRY_CLAUSE) {
            return new TryClausePsi(node);
        } else if (type == CATCH_CLAUSE) {
            return new CatchClausePsi(node);
        } else if (type == FINALLY_CLAUSE) {
            return new FinallyClausePsi(node);
        } else if (type == RESOURCE_LIST) {
            return new ResourceListPsi(node);
        } else if (type == RESOURCE) {
            return new ResourcePsi(node);
        } else if (type == CATCH_VARIABLE) {
            return new CatchVariablePsi(node);
        } else if (type == FOR_STATEMENT) {
            return new ForStatementPsi(node);
        } else if (type == FOR_CLAUSE) {
            return new ForClausePsi(node);
        } else if (type == FOR_ITERATOR) {
            return new ForIteratorPsi(node);
        } else if (type == VALUE_ITERATOR) {
            return new ValueIteratorPsi(node);
        } else if (type == KEY_VALUE_ITERATOR) {
            return new KeyValueIteratorPsi(node);
        } else if (type == WHILE_STATEMENT) {
            return new WhileStatementPsi(node);
        } else if (type == WHILE_CLAUSE) {
            return new WhileClausePsi(node);
        } else if (type == CONDITION_LIST) {
            return new ConditionListPsi(node);
        } else if (type == BOOLEAN_CONDITION) {
            return new BooleanConditionPsi(node);
        } else if (type == EXISTS_CONDITION) {
            return new ExistsConditionPsi(node);
        } else if (type == NONEMPTY_CONDITION) {
            return new NonemptyConditionPsi(node);
        } else if (type == IS_CONDITION) {
            return new IsConditionPsi(node);
        } else if (type == SATISFIES_CONDITION) {
            return new SatisfiesConditionPsi(node);
        } else if (type == VARIABLE) {
            return new VariablePsi(node);
        } else if (type == SUM_OP) {
            return new SumOpPsi(node);
        } else if (type == DIFFERENCE_OP) {
            return new DifferenceOpPsi(node);
        } else if (type == PRODUCT_OP) {
            return new ProductOpPsi(node);
        } else if (type == QUOTIENT_OP) {
            return new QuotientOpPsi(node);
        } else if (type == POWER_OP) {
            return new PowerOpPsi(node);
        } else if (type == REMAINDER_OP) {
            return new RemainderOpPsi(node);
        } else if (type == ASSIGN_OP) {
            return new AssignOpPsi(node);
        } else if (type == ADD_ASSIGN_OP) {
            return new AddAssignOpPsi(node);
        } else if (type == SUBTRACT_ASSIGN_OP) {
            return new SubtractAssignOpPsi(node);
        } else if (type == MULTIPLY_ASSIGN_OP) {
            return new MultiplyAssignOpPsi(node);
        } else if (type == DIVIDE_ASSIGN_OP) {
            return new DivideAssignOpPsi(node);
        } else if (type == REMAINDER_ASSIGN_OP) {
            return new RemainderAssignOpPsi(node);
        } else if (type == INTERSECT_ASSIGN_OP) {
            return new IntersectAssignOpPsi(node);
        } else if (type == UNION_ASSIGN_OP) {
            return new UnionAssignOpPsi(node);
        } else if (type == XOR_ASSIGN_OP) {
            return new XorAssignOpPsi(node);
        } else if (type == COMPLEMENT_ASSIGN_OP) {
            return new ComplementAssignOpPsi(node);
        } else if (type == AND_ASSIGN_OP) {
            return new AndAssignOpPsi(node);
        } else if (type == OR_ASSIGN_OP) {
            return new OrAssignOpPsi(node);
        } else if (type == AND_OP) {
            return new AndOpPsi(node);
        } else if (type == OR_OP) {
            return new OrOpPsi(node);
        } else if (type == INTERSECTION_OP) {
            return new IntersectionOpPsi(node);
        } else if (type == UNION_OP) {
            return new UnionOpPsi(node);
        } else if (type == XOR_OP) {
            return new XorOpPsi(node);
        } else if (type == COMPLEMENT_OP) {
            return new ComplementOpPsi(node);
        } else if (type == EQUAL_OP) {
            return new EqualOpPsi(node);
        } else if (type == NOT_EQUAL_OP) {
            return new NotEqualOpPsi(node);
        } else if (type == LARGER_OP) {
            return new LargerOpPsi(node);
        } else if (type == SMALLER_OP) {
            return new SmallerOpPsi(node);
        } else if (type == LARGE_AS_OP) {
            return new LargeAsOpPsi(node);
        } else if (type == SMALL_AS_OP) {
            return new SmallAsOpPsi(node);
        } else if (type == SCALE_OP) {
            return new ScaleOpPsi(node);
        } else if (type == OPEN_BOUND) {
            return new OpenBoundPsi(node);
        } else if (type == CLOSED_BOUND) {
            return new ClosedBoundPsi(node);
        } else if (type == WITHIN_OP) {
            return new WithinOpPsi(node);
        } else if (type == DEFAULT_OP) {
            return new DefaultOpPsi(node);
        } else if (type == THEN_OP) {
            return new ThenOpPsi(node);
        } else if (type == IDENTICAL_OP) {
            return new IdenticalOpPsi(node);
        } else if (type == ENTRY_OP) {
            return new EntryOpPsi(node);
        } else if (type == RANGE_OP) {
            return new RangeOpPsi(node);
        } else if (type == SEGMENT_OP) {
            return new SegmentOpPsi(node);
        } else if (type == COMPARE_OP) {
            return new CompareOpPsi(node);
        } else if (type == IN_OP) {
            return new InOpPsi(node);
        } else if (type == NOT_OP) {
            return new NotOpPsi(node);
        } else if (type == EXISTS) {
            return new ExistsPsi(node);
        } else if (type == NONEMPTY) {
            return new NonemptyPsi(node);
        } else if (type == NEGATIVE_OP) {
            return new NegativeOpPsi(node);
        } else if (type == POSITIVE_OP) {
            return new PositiveOpPsi(node);
        } else if (type == IS_OP) {
            return new IsOpPsi(node);
        } else if (type == SATISFIES) {
            return new SatisfiesPsi(node);
        } else if (type == EXTENDS) {
            return new ExtendsPsi(node);
        } else if (type == OF_OP) {
            return new OfOpPsi(node);
        } else if (type == INCREMENT_OP) {
            return new IncrementOpPsi(node);
        } else if (type == DECREMENT_OP) {
            return new DecrementOpPsi(node);
        } else if (type == POSTFIX_INCREMENT_OP) {
            return new PostfixIncrementOpPsi(node);
        } else if (type == POSTFIX_DECREMENT_OP) {
            return new PostfixDecrementOpPsi(node);
        } else if (type == EXPRESSION_LIST) {
            return new ExpressionListPsi(node);
        } else if (type == EXPRESSION) {
            return new ExpressionPsi(node);
        } else if (type == INVOCATION_EXPRESSION) {
            return new InvocationExpressionPsi(node);
        } else if (type == PARAMETERIZED_EXPRESSION) {
            return new ParameterizedExpressionPsi(node);
        } else if (type == EXTENDED_TYPE_EXPRESSION) {
            return new ExtendedTypeExpressionPsi(node);
        } else if (type == BASE_MEMBER_EXPRESSION) {
            return new BaseMemberExpressionPsi(node);
        } else if (type == BASE_TYPE_EXPRESSION) {
            return new BaseTypeExpressionPsi(node);
        } else if (type == QUALIFIED_MEMBER_EXPRESSION) {
            return new QualifiedMemberExpressionPsi(node);
        } else if (type == QUALIFIED_TYPE_EXPRESSION) {
            return new QualifiedTypeExpressionPsi(node);
        } else if (type == MEMBER_OP) {
            return new MemberOpPsi(node);
        } else if (type == SAFE_MEMBER_OP) {
            return new SafeMemberOpPsi(node);
        } else if (type == SPREAD_OP) {
            return new SpreadOpPsi(node);
        } else if (type == INDEX_EXPRESSION) {
            return new IndexExpressionPsi(node);
        } else if (type == ELEMENT) {
            return new ElementPsi(node);
        } else if (type == ELEMENT_RANGE) {
            return new ElementRangePsi(node);
        } else if (type == OUTER) {
            return new OuterPsi(node);
        } else if (type == PACKAGE) {
            return new PackagePsi(node);
        } else if (type == NAMED_ARGUMENT_LIST) {
            return new NamedArgumentListPsi(node);
        } else if (type == SEQUENCED_ARGUMENT) {
            return new SequencedArgumentPsi(node);
        } else if (type == POSITIONAL_ARGUMENT_LIST) {
            return new PositionalArgumentListPsi(node);
        } else if (type == LISTED_ARGUMENT) {
            return new ListedArgumentPsi(node);
        } else if (type == SPREAD_ARGUMENT) {
            return new SpreadArgumentPsi(node);
        } else if (type == FUNCTION_ARGUMENT) {
            return new FunctionArgumentPsi(node);
        } else if (type == SPECIFIED_ARGUMENT) {
            return new SpecifiedArgumentPsi(node);
        } else if (type == METHOD_ARGUMENT) {
            return new MethodArgumentPsi(node);
        } else if (type == ATTRIBUTE_ARGUMENT) {
            return new AttributeArgumentPsi(node);
        } else if (type == OBJECT_ARGUMENT) {
            return new ObjectArgumentPsi(node);
        } else if (type == SPECIFIER_EXPRESSION) {
            return new SpecifierExpressionPsi(node);
        } else if (type == LAZY_SPECIFIER_EXPRESSION) {
            return new LazySpecifierExpressionPsi(node);
        } else if (type == INITIALIZER_EXPRESSION) {
            return new InitializerExpressionPsi(node);
        } else if (type == NATURAL_LITERAL) {
            return new NaturalLiteralPsi(node);
        } else if (type == FLOAT_LITERAL) {
            return new FloatLiteralPsi(node);
        } else if (type == CHAR_LITERAL) {
            return new CharLiteralPsi(node);
        } else if (type == STRING_LITERAL) {
            return new StringLiteralPsi(node);
        } else if (type == QUOTED_LITERAL) {
            return new QuotedLiteralPsi(node);
        } else if (type == DOC_LINK) {
            return new DocLinkPsi(node);
        } else if (type == THIS) {
            return new ThisPsi(node);
        } else if (type == SUPER) {
            return new SuperPsi(node);
        } else if (type == SEQUENCE_ENUMERATION) {
            return new SequenceEnumerationPsi(node);
        } else if (type == TUPLE) {
            return new TuplePsi(node);
        } else if (type == DYNAMIC) {
            return new DynamicPsi(node);
        } else if (type == STRING_TEMPLATE) {
            return new StringTemplatePsi(node);
        } else if (type == ANNOTATION) {
            return new AnnotationPsi(node);
        } else if (type == ANONYMOUS_ANNOTATION) {
            return new AnonymousAnnotationPsi(node);
        } else if (type == ANNOTATION_LIST) {
            return new AnnotationListPsi(node);
        } else if (type == IDENTIFIER) {
            return new IdentifierPsi(node);
        } else if (type == COMPREHENSION) {
            return new ComprehensionPsi(node);
        } else if (type == EXPRESSION_COMPREHENSION_CLAUSE) {
            return new ExpressionComprehensionClausePsi(node);
        } else if (type == FOR_COMPREHENSION_CLAUSE) {
            return new ForComprehensionClausePsi(node);
        } else if (type == IF_COMPREHENSION_CLAUSE) {
            return new IfComprehensionClausePsi(node);
        }

        return new CeylonCompositeElementImpl(node);
//        throw new AssertionError("Unknown element type: " + type);

    }
}
