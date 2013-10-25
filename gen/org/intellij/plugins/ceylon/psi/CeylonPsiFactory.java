package org.intellij.plugins.ceylon.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.psi.CeylonPsiImpl.*;

import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;

/* Generated using Antlr by PsiFactoryGen.g */
public class CeylonPsiFactory {

    public static PsiElement createElement(ASTNode node) {
        IElementType type = node.getElementType();
        if (false) {
        } else if (type == COMPILATION_UNIT) {
            return new CompilationUnitPsiImpl(node);
        } else if (type == MODULE_DESCRIPTOR) {
            return new ModuleDescriptorPsiImpl(node);
        } else if (type == PACKAGE_DESCRIPTOR) {
            return new PackageDescriptorPsiImpl(node);
        } else if (type == IMPORT_MODULE_LIST) {
            return new ImportModuleListPsiImpl(node);
        } else if (type == IMPORT_MODULE) {
            return new ImportModulePsiImpl(node);
        } else if (type == IMPORT_LIST) {
            return new ImportListPsiImpl(node);
        } else if (type == IMPORT) {
            return new ImportPsiImpl(node);
        } else if (type == IMPORT_PATH) {
            return new ImportPathPsiImpl(node);
        } else if (type == IMPORT_MEMBER_OR_TYPE_LIST) {
            return new ImportMemberOrTypeListPsiImpl(node);
        } else if (type == IMPORT_MEMBER_OR_TYPE) {
            return new ImportMemberOrTypePsiImpl(node);
        } else if (type == IMPORT_MEMBER) {
            return new ImportMemberPsiImpl(node);
        } else if (type == IMPORT_TYPE) {
            return new ImportTypePsiImpl(node);
        } else if (type == ALIAS) {
            return new AliasPsiImpl(node);
        } else if (type == IMPORT_WILDCARD) {
            return new ImportWildcardPsiImpl(node);
        } else if (type == DECLARATION) {
            return new DeclarationPsiImpl(node);
        } else if (type == MISSING_DECLARATION) {
            return new MissingDeclarationPsiImpl(node);
        } else if (type == TYPE_DECLARATION) {
            return new TypeDeclarationPsiImpl(node);
        } else if (type == CLASS_OR_INTERFACE) {
            return new ClassOrInterfacePsiImpl(node);
        } else if (type == TYPE_ALIAS_DECLARATION) {
            return new TypeAliasDeclarationPsiImpl(node);
        } else if (type == SATISFIED_TYPES) {
            return new SatisfiedTypesPsiImpl(node);
        } else if (type == ABSTRACTED_TYPE) {
            return new AbstractedTypePsiImpl(node);
        } else if (type == ADAPTED_TYPES) {
            return new AdaptedTypesPsiImpl(node);
        } else if (type == CASE_TYPES) {
            return new CaseTypesPsiImpl(node);
        } else if (type == EXTENDED_TYPE) {
            return new ExtendedTypePsiImpl(node);
        } else if (type == TYPE_CONSTRAINT_LIST) {
            return new TypeConstraintListPsiImpl(node);
        } else if (type == TYPE_CONSTRAINT) {
            return new TypeConstraintPsiImpl(node);
        } else if (type == TYPE_SPECIFIER) {
            return new TypeSpecifierPsiImpl(node);
        } else if (type == DEFAULT_TYPE_ARGUMENT) {
            return new DefaultTypeArgumentPsiImpl(node);
        } else if (type == CLASS_SPECIFIER) {
            return new ClassSpecifierPsiImpl(node);
        } else if (type == ANY_CLASS) {
            return new AnyClassPsiImpl(node);
        } else if (type == CLASS_DEFINITION) {
            return new ClassDefinitionPsiImpl(node);
        } else if (type == CLASS_DECLARATION) {
            return new ClassDeclarationPsiImpl(node);
        } else if (type == ANY_INTERFACE) {
            return new AnyInterfacePsiImpl(node);
        } else if (type == INTERFACE_DEFINITION) {
            return new InterfaceDefinitionPsiImpl(node);
        } else if (type == INTERFACE_DECLARATION) {
            return new InterfaceDeclarationPsiImpl(node);
        } else if (type == TYPED_DECLARATION) {
            return new TypedDeclarationPsiImpl(node);
        } else if (type == ANY_ATTRIBUTE) {
            return new AnyAttributePsiImpl(node);
        } else if (type == ATTRIBUTE_DECLARATION) {
            return new AttributeDeclarationPsiImpl(node);
        } else if (type == ATTRIBUTE_GETTER_DEFINITION) {
            return new AttributeGetterDefinitionPsiImpl(node);
        } else if (type == ATTRIBUTE_SETTER_DEFINITION) {
            return new AttributeSetterDefinitionPsiImpl(node);
        } else if (type == ANY_METHOD) {
            return new AnyMethodPsiImpl(node);
        } else if (type == METHOD_DEFINITION) {
            return new MethodDefinitionPsiImpl(node);
        } else if (type == METHOD_DECLARATION) {
            return new MethodDeclarationPsiImpl(node);
        } else if (type == VOID_MODIFIER) {
            return new VoidModifierPsiImpl(node);
        } else if (type == OBJECT_DEFINITION) {
            return new ObjectDefinitionPsiImpl(node);
        } else if (type == PARAMETER_LIST) {
            return new ParameterListPsiImpl(node);
        } else if (type == PARAMETER) {
            return new ParameterPsiImpl(node);
        } else if (type == PARAMETER_DECLARATION) {
            return new ParameterDeclarationPsiImpl(node);
        } else if (type == VALUE_PARAMETER_DECLARATION) {
            return new ValueParameterDeclarationPsiImpl(node);
        } else if (type == FUNCTIONAL_PARAMETER_DECLARATION) {
            return new FunctionalParameterDeclarationPsiImpl(node);
        } else if (type == INITIALIZER_PARAMETER) {
            return new InitializerParameterPsiImpl(node);
        } else if (type == TYPE_PARAMETER_LIST) {
            return new TypeParameterListPsiImpl(node);
        } else if (type == TYPE_PARAMETER_DECLARATION) {
            return new TypeParameterDeclarationPsiImpl(node);
        } else if (type == TYPE_VARIANCE) {
            return new TypeVariancePsiImpl(node);
        } else if (type == BODY) {
            return new BodyPsiImpl(node);
        } else if (type == BLOCK) {
            return new BlockPsiImpl(node);
        } else if (type == CLASS_BODY) {
            return new ClassBodyPsiImpl(node);
        } else if (type == INTERFACE_BODY) {
            return new InterfaceBodyPsiImpl(node);
        } else if (type == TYPE) {
            return new TypePsiImpl(node);
        } else if (type == STATIC_TYPE) {
            return new StaticTypePsiImpl(node);
        } else if (type == SIMPLE_TYPE) {
            return new SimpleTypePsiImpl(node);
        } else if (type == BASE_TYPE) {
            return new BaseTypePsiImpl(node);
        } else if (type == UNION_TYPE) {
            return new UnionTypePsiImpl(node);
        } else if (type == INTERSECTION_TYPE) {
            return new IntersectionTypePsiImpl(node);
        } else if (type == SEQUENCE_TYPE) {
            return new SequenceTypePsiImpl(node);
        } else if (type == ITERABLE_TYPE) {
            return new IterableTypePsiImpl(node);
        } else if (type == OPTIONAL_TYPE) {
            return new OptionalTypePsiImpl(node);
        } else if (type == TUPLE_TYPE) {
            return new TupleTypePsiImpl(node);
        } else if (type == FUNCTION_TYPE) {
            return new FunctionTypePsiImpl(node);
        } else if (type == ENTRY_TYPE) {
            return new EntryTypePsiImpl(node);
        } else if (type == QUALIFIED_TYPE) {
            return new QualifiedTypePsiImpl(node);
        } else if (type == SUPER_TYPE) {
            return new SuperTypePsiImpl(node);
        } else if (type == META_LITERAL) {
            return new MetaLiteralPsiImpl(node);
        } else if (type == TYPE_LITERAL) {
            return new TypeLiteralPsiImpl(node);
        } else if (type == MEMBER_LITERAL) {
            return new MemberLiteralPsiImpl(node);
        } else if (type == CLASS_LITERAL) {
            return new ClassLiteralPsiImpl(node);
        } else if (type == INTERFACE_LITERAL) {
            return new InterfaceLiteralPsiImpl(node);
        } else if (type == ALIAS_LITERAL) {
            return new AliasLiteralPsiImpl(node);
        } else if (type == TYPE_PARAMETER_LITERAL) {
            return new TypeParameterLiteralPsiImpl(node);
        } else if (type == VALUE_LITERAL) {
            return new ValueLiteralPsiImpl(node);
        } else if (type == FUNCTION_LITERAL) {
            return new FunctionLiteralPsiImpl(node);
        } else if (type == MODULE_LITERAL) {
            return new ModuleLiteralPsiImpl(node);
        } else if (type == PACKAGE_LITERAL) {
            return new PackageLiteralPsiImpl(node);
        } else if (type == DYNAMIC_MODIFIER) {
            return new DynamicModifierPsiImpl(node);
        } else if (type == LOCAL_MODIFIER) {
            return new LocalModifierPsiImpl(node);
        } else if (type == VALUE_MODIFIER) {
            return new ValueModifierPsiImpl(node);
        } else if (type == FUNCTION_MODIFIER) {
            return new FunctionModifierPsiImpl(node);
        } else if (type == SYNTHETIC_VARIABLE) {
            return new SyntheticVariablePsiImpl(node);
        } else if (type == TYPE_ARGUMENTS) {
            return new TypeArgumentsPsiImpl(node);
        } else if (type == TYPE_ARGUMENT_LIST) {
            return new TypeArgumentListPsiImpl(node);
        } else if (type == INFERRED_TYPE_ARGUMENTS) {
            return new InferredTypeArgumentsPsiImpl(node);
        } else if (type == SEQUENCED_TYPE) {
            return new SequencedTypePsiImpl(node);
        } else if (type == DEFAULTED_TYPE) {
            return new DefaultedTypePsiImpl(node);
        } else if (type == DIRECTIVE) {
            return new DirectivePsiImpl(node);
        } else if (type == RETURN) {
            return new ReturnPsiImpl(node);
        } else if (type == THROW) {
            return new ThrowPsiImpl(node);
        } else if (type == CONTINUE) {
            return new ContinuePsiImpl(node);
        } else if (type == BREAK) {
            return new BreakPsiImpl(node);
        } else if (type == STATEMENT_OR_ARGUMENT) {
            return new StatementOrArgumentPsiImpl(node);
        } else if (type == STATEMENT) {
            return new StatementPsiImpl(node);
        } else if (type == COMPILER_ANNOTATION) {
            return new CompilerAnnotationPsiImpl(node);
        } else if (type == EXECUTABLE_STATEMENT) {
            return new ExecutableStatementPsiImpl(node);
        } else if (type == ASSERTION) {
            return new AssertionPsiImpl(node);
        } else if (type == SPECIFIER_STATEMENT) {
            return new SpecifierStatementPsiImpl(node);
        } else if (type == EXPRESSION_STATEMENT) {
            return new ExpressionStatementPsiImpl(node);
        } else if (type == CONTROL_STATEMENT) {
            return new ControlStatementPsiImpl(node);
        } else if (type == CONTROL_CLAUSE) {
            return new ControlClausePsiImpl(node);
        } else if (type == DYNAMIC_STATEMENT) {
            return new DynamicStatementPsiImpl(node);
        } else if (type == DYNAMIC_CLAUSE) {
            return new DynamicClausePsiImpl(node);
        } else if (type == IF_STATEMENT) {
            return new IfStatementPsiImpl(node);
        } else if (type == IF_CLAUSE) {
            return new IfClausePsiImpl(node);
        } else if (type == ELSE_CLAUSE) {
            return new ElseClausePsiImpl(node);
        } else if (type == SWITCH_STATEMENT) {
            return new SwitchStatementPsiImpl(node);
        } else if (type == SWITCH_CLAUSE) {
            return new SwitchClausePsiImpl(node);
        } else if (type == SWITCH_CASE_LIST) {
            return new SwitchCaseListPsiImpl(node);
        } else if (type == CASE_CLAUSE) {
            return new CaseClausePsiImpl(node);
        } else if (type == CASE_ITEM) {
            return new CaseItemPsiImpl(node);
        } else if (type == MATCH_CASE) {
            return new MatchCasePsiImpl(node);
        } else if (type == IS_CASE) {
            return new IsCasePsiImpl(node);
        } else if (type == SATISFIES_CASE) {
            return new SatisfiesCasePsiImpl(node);
        } else if (type == TRY_CATCH_STATEMENT) {
            return new TryCatchStatementPsiImpl(node);
        } else if (type == TRY_CLAUSE) {
            return new TryClausePsiImpl(node);
        } else if (type == CATCH_CLAUSE) {
            return new CatchClausePsiImpl(node);
        } else if (type == FINALLY_CLAUSE) {
            return new FinallyClausePsiImpl(node);
        } else if (type == RESOURCE_LIST) {
            return new ResourceListPsiImpl(node);
        } else if (type == RESOURCE) {
            return new ResourcePsiImpl(node);
        } else if (type == CATCH_VARIABLE) {
            return new CatchVariablePsiImpl(node);
        } else if (type == FOR_STATEMENT) {
            return new ForStatementPsiImpl(node);
        } else if (type == FOR_CLAUSE) {
            return new ForClausePsiImpl(node);
        } else if (type == FOR_ITERATOR) {
            return new ForIteratorPsiImpl(node);
        } else if (type == VALUE_ITERATOR) {
            return new ValueIteratorPsiImpl(node);
        } else if (type == KEY_VALUE_ITERATOR) {
            return new KeyValueIteratorPsiImpl(node);
        } else if (type == WHILE_STATEMENT) {
            return new WhileStatementPsiImpl(node);
        } else if (type == WHILE_CLAUSE) {
            return new WhileClausePsiImpl(node);
        } else if (type == CONDITION_LIST) {
            return new ConditionListPsiImpl(node);
        } else if (type == CONDITION) {
            return new ConditionPsiImpl(node);
        } else if (type == BOOLEAN_CONDITION) {
            return new BooleanConditionPsiImpl(node);
        } else if (type == EXISTS_OR_NONEMPTY_CONDITION) {
            return new ExistsOrNonemptyConditionPsiImpl(node);
        } else if (type == EXISTS_CONDITION) {
            return new ExistsConditionPsiImpl(node);
        } else if (type == NONEMPTY_CONDITION) {
            return new NonemptyConditionPsiImpl(node);
        } else if (type == IS_CONDITION) {
            return new IsConditionPsiImpl(node);
        } else if (type == SATISFIES_CONDITION) {
            return new SatisfiesConditionPsiImpl(node);
        } else if (type == VARIABLE) {
            return new VariablePsiImpl(node);
        } else if (type == TERM) {
            return new TermPsiImpl(node);
        } else if (type == OPERATOR_EXPRESSION) {
            return new OperatorExpressionPsiImpl(node);
        } else if (type == BINARY_OPERATOR_EXPRESSION) {
            return new BinaryOperatorExpressionPsiImpl(node);
        } else if (type == ARITHMETIC_OP) {
            return new ArithmeticOpPsiImpl(node);
        } else if (type == SUM_OP) {
            return new SumOpPsiImpl(node);
        } else if (type == DIFFERENCE_OP) {
            return new DifferenceOpPsiImpl(node);
        } else if (type == PRODUCT_OP) {
            return new ProductOpPsiImpl(node);
        } else if (type == QUOTIENT_OP) {
            return new QuotientOpPsiImpl(node);
        } else if (type == POWER_OP) {
            return new PowerOpPsiImpl(node);
        } else if (type == REMAINDER_OP) {
            return new RemainderOpPsiImpl(node);
        } else if (type == ASSIGNMENT_OP) {
            return new AssignmentOpPsiImpl(node);
        } else if (type == ASSIGN_OP) {
            return new AssignOpPsiImpl(node);
        } else if (type == ARITHMETIC_ASSIGNMENT_OP) {
            return new ArithmeticAssignmentOpPsiImpl(node);
        } else if (type == ADD_ASSIGN_OP) {
            return new AddAssignOpPsiImpl(node);
        } else if (type == SUBTRACT_ASSIGN_OP) {
            return new SubtractAssignOpPsiImpl(node);
        } else if (type == MULTIPLY_ASSIGN_OP) {
            return new MultiplyAssignOpPsiImpl(node);
        } else if (type == DIVIDE_ASSIGN_OP) {
            return new DivideAssignOpPsiImpl(node);
        } else if (type == REMAINDER_ASSIGN_OP) {
            return new RemainderAssignOpPsiImpl(node);
        } else if (type == BITWISE_ASSIGNMENT_OP) {
            return new BitwiseAssignmentOpPsiImpl(node);
        } else if (type == INTERSECT_ASSIGN_OP) {
            return new IntersectAssignOpPsiImpl(node);
        } else if (type == UNION_ASSIGN_OP) {
            return new UnionAssignOpPsiImpl(node);
        } else if (type == XOR_ASSIGN_OP) {
            return new XorAssignOpPsiImpl(node);
        } else if (type == COMPLEMENT_ASSIGN_OP) {
            return new ComplementAssignOpPsiImpl(node);
        } else if (type == LOGICAL_ASSIGNMENT_OP) {
            return new LogicalAssignmentOpPsiImpl(node);
        } else if (type == AND_ASSIGN_OP) {
            return new AndAssignOpPsiImpl(node);
        } else if (type == OR_ASSIGN_OP) {
            return new OrAssignOpPsiImpl(node);
        } else if (type == LOGICAL_OP) {
            return new LogicalOpPsiImpl(node);
        } else if (type == AND_OP) {
            return new AndOpPsiImpl(node);
        } else if (type == OR_OP) {
            return new OrOpPsiImpl(node);
        } else if (type == BITWISE_OP) {
            return new BitwiseOpPsiImpl(node);
        } else if (type == INTERSECTION_OP) {
            return new IntersectionOpPsiImpl(node);
        } else if (type == UNION_OP) {
            return new UnionOpPsiImpl(node);
        } else if (type == XOR_OP) {
            return new XorOpPsiImpl(node);
        } else if (type == COMPLEMENT_OP) {
            return new ComplementOpPsiImpl(node);
        } else if (type == EQUALITY_OP) {
            return new EqualityOpPsiImpl(node);
        } else if (type == EQUAL_OP) {
            return new EqualOpPsiImpl(node);
        } else if (type == NOT_EQUAL_OP) {
            return new NotEqualOpPsiImpl(node);
        } else if (type == COMPARISON_OP) {
            return new ComparisonOpPsiImpl(node);
        } else if (type == LARGER_OP) {
            return new LargerOpPsiImpl(node);
        } else if (type == SMALLER_OP) {
            return new SmallerOpPsiImpl(node);
        } else if (type == LARGE_AS_OP) {
            return new LargeAsOpPsiImpl(node);
        } else if (type == SMALL_AS_OP) {
            return new SmallAsOpPsiImpl(node);
        } else if (type == SCALE_OP) {
            return new ScaleOpPsiImpl(node);
        } else if (type == BOUND) {
            return new BoundPsiImpl(node);
        } else if (type == OPEN_BOUND) {
            return new OpenBoundPsiImpl(node);
        } else if (type == CLOSED_BOUND) {
            return new ClosedBoundPsiImpl(node);
        } else if (type == WITHIN_OP) {
            return new WithinOpPsiImpl(node);
        } else if (type == DEFAULT_OP) {
            return new DefaultOpPsiImpl(node);
        } else if (type == THEN_OP) {
            return new ThenOpPsiImpl(node);
        } else if (type == IDENTICAL_OP) {
            return new IdenticalOpPsiImpl(node);
        } else if (type == ENTRY_OP) {
            return new EntryOpPsiImpl(node);
        } else if (type == RANGE_OP) {
            return new RangeOpPsiImpl(node);
        } else if (type == SEGMENT_OP) {
            return new SegmentOpPsiImpl(node);
        } else if (type == COMPARE_OP) {
            return new CompareOpPsiImpl(node);
        } else if (type == IN_OP) {
            return new InOpPsiImpl(node);
        } else if (type == UNARY_OPERATOR_EXPRESSION) {
            return new UnaryOperatorExpressionPsiImpl(node);
        } else if (type == NOT_OP) {
            return new NotOpPsiImpl(node);
        } else if (type == EXISTS) {
            return new ExistsPsiImpl(node);
        } else if (type == NONEMPTY) {
            return new NonemptyPsiImpl(node);
        } else if (type == NEGATIVE_OP) {
            return new NegativeOpPsiImpl(node);
        } else if (type == POSITIVE_OP) {
            return new PositiveOpPsiImpl(node);
        } else if (type == TYPE_OPERATOR_EXPRESSION) {
            return new TypeOperatorExpressionPsiImpl(node);
        } else if (type == IS_OP) {
            return new IsOpPsiImpl(node);
        } else if (type == SATISFIES) {
            return new SatisfiesPsiImpl(node);
        } else if (type == EXTENDS) {
            return new ExtendsPsiImpl(node);
        } else if (type == OF_OP) {
            return new OfOpPsiImpl(node);
        } else if (type == PREFIX_OPERATOR_EXPRESSION) {
            return new PrefixOperatorExpressionPsiImpl(node);
        } else if (type == INCREMENT_OP) {
            return new IncrementOpPsiImpl(node);
        } else if (type == DECREMENT_OP) {
            return new DecrementOpPsiImpl(node);
        } else if (type == POSTFIX_OPERATOR_EXPRESSION) {
            return new PostfixOperatorExpressionPsiImpl(node);
        } else if (type == POSTFIX_INCREMENT_OP) {
            return new PostfixIncrementOpPsiImpl(node);
        } else if (type == POSTFIX_DECREMENT_OP) {
            return new PostfixDecrementOpPsiImpl(node);
        } else if (type == EXPRESSION_LIST) {
            return new ExpressionListPsiImpl(node);
        } else if (type == EXPRESSION) {
            return new ExpressionPsiImpl(node);
        } else if (type == PRIMARY) {
            return new PrimaryPsiImpl(node);
        } else if (type == POSTFIX_EXPRESSION) {
            return new PostfixExpressionPsiImpl(node);
        } else if (type == INVOCATION_EXPRESSION) {
            return new InvocationExpressionPsiImpl(node);
        } else if (type == PARAMETERIZED_EXPRESSION) {
            return new ParameterizedExpressionPsiImpl(node);
        } else if (type == MEMBER_OR_TYPE_EXPRESSION) {
            return new MemberOrTypeExpressionPsiImpl(node);
        } else if (type == EXTENDED_TYPE_EXPRESSION) {
            return new ExtendedTypeExpressionPsiImpl(node);
        } else if (type == STATIC_MEMBER_OR_TYPE_EXPRESSION) {
            return new StaticMemberOrTypeExpressionPsiImpl(node);
        } else if (type == BASE_MEMBER_OR_TYPE_EXPRESSION) {
            return new BaseMemberOrTypeExpressionPsiImpl(node);
        } else if (type == BASE_MEMBER_EXPRESSION) {
            return new BaseMemberExpressionPsiImpl(node);
        } else if (type == BASE_TYPE_EXPRESSION) {
            return new BaseTypeExpressionPsiImpl(node);
        } else if (type == QUALIFIED_MEMBER_OR_TYPE_EXPRESSION) {
            return new QualifiedMemberOrTypeExpressionPsiImpl(node);
        } else if (type == QUALIFIED_MEMBER_EXPRESSION) {
            return new QualifiedMemberExpressionPsiImpl(node);
        } else if (type == QUALIFIED_TYPE_EXPRESSION) {
            return new QualifiedTypeExpressionPsiImpl(node);
        } else if (type == MEMBER_OPERATOR) {
            return new MemberOperatorPsiImpl(node);
        } else if (type == MEMBER_OP) {
            return new MemberOpPsiImpl(node);
        } else if (type == SAFE_MEMBER_OP) {
            return new SafeMemberOpPsiImpl(node);
        } else if (type == SPREAD_OP) {
            return new SpreadOpPsiImpl(node);
        } else if (type == INDEX_EXPRESSION) {
            return new IndexExpressionPsiImpl(node);
        } else if (type == ELEMENT_OR_RANGE) {
            return new ElementOrRangePsiImpl(node);
        } else if (type == ELEMENT) {
            return new ElementPsiImpl(node);
        } else if (type == ELEMENT_RANGE) {
            return new ElementRangePsiImpl(node);
        } else if (type == OUTER) {
            return new OuterPsiImpl(node);
        } else if (type == PACKAGE) {
            return new PackagePsiImpl(node);
        } else if (type == ARGUMENT_LIST) {
            return new ArgumentListPsiImpl(node);
        } else if (type == NAMED_ARGUMENT_LIST) {
            return new NamedArgumentListPsiImpl(node);
        } else if (type == SEQUENCED_ARGUMENT) {
            return new SequencedArgumentPsiImpl(node);
        } else if (type == POSITIONAL_ARGUMENT_LIST) {
            return new PositionalArgumentListPsiImpl(node);
        } else if (type == POSITIONAL_ARGUMENT) {
            return new PositionalArgumentPsiImpl(node);
        } else if (type == LISTED_ARGUMENT) {
            return new ListedArgumentPsiImpl(node);
        } else if (type == SPREAD_ARGUMENT) {
            return new SpreadArgumentPsiImpl(node);
        } else if (type == FUNCTION_ARGUMENT) {
            return new FunctionArgumentPsiImpl(node);
        } else if (type == NAMED_ARGUMENT) {
            return new NamedArgumentPsiImpl(node);
        } else if (type == SPECIFIED_ARGUMENT) {
            return new SpecifiedArgumentPsiImpl(node);
        } else if (type == TYPED_ARGUMENT) {
            return new TypedArgumentPsiImpl(node);
        } else if (type == METHOD_ARGUMENT) {
            return new MethodArgumentPsiImpl(node);
        } else if (type == ATTRIBUTE_ARGUMENT) {
            return new AttributeArgumentPsiImpl(node);
        } else if (type == OBJECT_ARGUMENT) {
            return new ObjectArgumentPsiImpl(node);
        } else if (type == SPECIFIER_OR_INITIALIZER_EXPRESSION) {
            return new SpecifierOrInitializerExpressionPsiImpl(node);
        } else if (type == SPECIFIER_EXPRESSION) {
            return new SpecifierExpressionPsiImpl(node);
        } else if (type == LAZY_SPECIFIER_EXPRESSION) {
            return new LazySpecifierExpressionPsiImpl(node);
        } else if (type == INITIALIZER_EXPRESSION) {
            return new InitializerExpressionPsiImpl(node);
        } else if (type == ATOM) {
            return new AtomPsiImpl(node);
        } else if (type == LITERAL) {
            return new LiteralPsiImpl(node);
        } else if (type == NATURAL_LITERAL) {
            return new NaturalLiteralPsiImpl(node);
        } else if (type == FLOAT_LITERAL) {
            return new FloatLiteralPsiImpl(node);
        } else if (type == CHAR_LITERAL) {
            return new CharLiteralPsiImpl(node);
        } else if (type == STRING_LITERAL) {
            return new StringLiteralPsiImpl(node);
        } else if (type == QUOTED_LITERAL) {
            return new QuotedLiteralPsiImpl(node);
        } else if (type == DOC_LINK) {
            return new DocLinkPsiImpl(node);
        } else if (type == SELF_EXPRESSION) {
            return new SelfExpressionPsiImpl(node);
        } else if (type == THIS) {
            return new ThisPsiImpl(node);
        } else if (type == SUPER) {
            return new SuperPsiImpl(node);
        } else if (type == SEQUENCE_ENUMERATION) {
            return new SequenceEnumerationPsiImpl(node);
        } else if (type == TUPLE) {
            return new TuplePsiImpl(node);
        } else if (type == DYNAMIC) {
            return new DynamicPsiImpl(node);
        } else if (type == STRING_TEMPLATE) {
            return new StringTemplatePsiImpl(node);
        } else if (type == ANNOTATION) {
            return new AnnotationPsiImpl(node);
        } else if (type == ANONYMOUS_ANNOTATION) {
            return new AnonymousAnnotationPsiImpl(node);
        } else if (type == ANNOTATION_LIST) {
            return new AnnotationListPsiImpl(node);
        } else if (type == IDENTIFIER) {
            return new IdentifierPsiImpl(node);
        } else if (type == COMPREHENSION) {
            return new ComprehensionPsiImpl(node);
        } else if (type == COMPREHENSION_CLAUSE) {
            return new ComprehensionClausePsiImpl(node);
        } else if (type == EXPRESSION_COMPREHENSION_CLAUSE) {
            return new ExpressionComprehensionClausePsiImpl(node);
        } else if (type == FOR_COMPREHENSION_CLAUSE) {
            return new ForComprehensionClausePsiImpl(node);
        } else if (type == IF_COMPREHENSION_CLAUSE) {
            return new IfComprehensionClausePsiImpl(node);
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
