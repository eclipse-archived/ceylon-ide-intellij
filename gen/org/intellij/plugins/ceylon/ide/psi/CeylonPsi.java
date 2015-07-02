package org.intellij.plugins.ceylon.ide.psi;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
/* Generated using Antlr by PsiIntfGen.g */

public class CeylonPsi {

    public static interface CompilationUnitPsi extends CeylonCompositeElement {
        @Override Tree.CompilationUnit getCeylonNode();
    }

    public static interface ModuleDescriptorPsi extends StatementOrArgumentPsi {
        @Override Tree.ModuleDescriptor getCeylonNode();
    }

    public static interface PackageDescriptorPsi extends StatementOrArgumentPsi {
        @Override Tree.PackageDescriptor getCeylonNode();
    }

    public static interface ImportModuleListPsi extends CeylonCompositeElement {
        @Override Tree.ImportModuleList getCeylonNode();
    }

    public static interface ImportModulePsi extends StatementOrArgumentPsi {
        @Override Tree.ImportModule getCeylonNode();
    }

    public static interface ImportListPsi extends CeylonCompositeElement {
        @Override Tree.ImportList getCeylonNode();
    }

    public static interface ImportPsi extends StatementOrArgumentPsi {
        @Override Tree.Import getCeylonNode();
    }

    public static interface ImportPathPsi extends CeylonCompositeElement {
        @Override Tree.ImportPath getCeylonNode();
    }

    public static interface ImportMemberOrTypeListPsi extends CeylonCompositeElement {
        @Override Tree.ImportMemberOrTypeList getCeylonNode();
    }

    public static interface ImportMemberOrTypePsi extends StatementOrArgumentPsi {
        @Override Tree.ImportMemberOrType getCeylonNode();
    }

    public static interface ImportMemberPsi extends ImportMemberOrTypePsi {
        @Override Tree.ImportMember getCeylonNode();
    }

    public static interface ImportTypePsi extends ImportMemberOrTypePsi {
        @Override Tree.ImportType getCeylonNode();
    }

    public static interface AliasPsi extends CeylonCompositeElement {
        @Override Tree.Alias getCeylonNode();
    }

    public static interface ImportWildcardPsi extends CeylonCompositeElement {
        @Override Tree.ImportWildcard getCeylonNode();
    }

    public static interface DeclarationPsi extends StatementPsi {
        @Override Tree.Declaration getCeylonNode();
    }

    public static interface MissingDeclarationPsi extends DeclarationPsi {
        @Override Tree.MissingDeclaration getCeylonNode();
    }

    public static interface TypeDeclarationPsi extends DeclarationPsi {
        @Override Tree.TypeDeclaration getCeylonNode();
    }

    public static interface ClassOrInterfacePsi extends TypeDeclarationPsi {
        @Override Tree.ClassOrInterface getCeylonNode();
    }

    public static interface TypeAliasDeclarationPsi extends TypeDeclarationPsi {
        @Override Tree.TypeAliasDeclaration getCeylonNode();
    }

    public static interface SatisfiedTypesPsi extends CeylonCompositeElement {
        @Override Tree.SatisfiedTypes getCeylonNode();
    }

    public static interface AbstractedTypePsi extends CeylonCompositeElement {
        @Override Tree.AbstractedType getCeylonNode();
    }

    public static interface CaseTypesPsi extends CeylonCompositeElement {
        @Override Tree.CaseTypes getCeylonNode();
    }

    public static interface ExtendedTypePsi extends CeylonCompositeElement {
        @Override Tree.ExtendedType getCeylonNode();
    }

    public static interface TypeConstraintListPsi extends CeylonCompositeElement {
        @Override Tree.TypeConstraintList getCeylonNode();
    }

    public static interface TypeConstraintPsi extends TypeDeclarationPsi {
        @Override Tree.TypeConstraint getCeylonNode();
    }

    public static interface TypeSpecifierPsi extends CeylonCompositeElement {
        @Override Tree.TypeSpecifier getCeylonNode();
    }

    public static interface DefaultTypeArgumentPsi extends TypeSpecifierPsi {
        @Override Tree.DefaultTypeArgument getCeylonNode();
    }

    public static interface ClassSpecifierPsi extends CeylonCompositeElement {
        @Override Tree.ClassSpecifier getCeylonNode();
    }

    public static interface AnyClassPsi extends ClassOrInterfacePsi {
        @Override Tree.AnyClass getCeylonNode();
    }

    public static interface ClassDefinitionPsi extends AnyClassPsi {
        @Override Tree.ClassDefinition getCeylonNode();
    }

    public static interface EnumeratedPsi extends DeclarationPsi {
        @Override Tree.Enumerated getCeylonNode();
    }

    public static interface ConstructorPsi extends DeclarationPsi {
        @Override Tree.Constructor getCeylonNode();
    }

    public static interface DelegatedConstructorPsi extends CeylonCompositeElement {
        @Override Tree.DelegatedConstructor getCeylonNode();
    }

    public static interface ClassDeclarationPsi extends AnyClassPsi {
        @Override Tree.ClassDeclaration getCeylonNode();
    }

    public static interface AnyInterfacePsi extends ClassOrInterfacePsi {
        @Override Tree.AnyInterface getCeylonNode();
    }

    public static interface InterfaceDefinitionPsi extends AnyInterfacePsi {
        @Override Tree.InterfaceDefinition getCeylonNode();
    }

    public static interface InterfaceDeclarationPsi extends AnyInterfacePsi {
        @Override Tree.InterfaceDeclaration getCeylonNode();
    }

    public static interface TypedDeclarationPsi extends DeclarationPsi {
        @Override Tree.TypedDeclaration getCeylonNode();
    }

    public static interface AnyAttributePsi extends TypedDeclarationPsi {
        @Override Tree.AnyAttribute getCeylonNode();
    }

    public static interface AttributeDeclarationPsi extends AnyAttributePsi {
        @Override Tree.AttributeDeclaration getCeylonNode();
    }

    public static interface AttributeGetterDefinitionPsi extends AnyAttributePsi {
        @Override Tree.AttributeGetterDefinition getCeylonNode();
    }

    public static interface AttributeSetterDefinitionPsi extends TypedDeclarationPsi {
        @Override Tree.AttributeSetterDefinition getCeylonNode();
    }

    public static interface AnyMethodPsi extends TypedDeclarationPsi {
        @Override Tree.AnyMethod getCeylonNode();
    }

    public static interface MethodDefinitionPsi extends AnyMethodPsi {
        @Override Tree.MethodDefinition getCeylonNode();
    }

    public static interface MethodDeclarationPsi extends AnyMethodPsi {
        @Override Tree.MethodDeclaration getCeylonNode();
    }

    public static interface VoidModifierPsi extends TypePsi {
        @Override Tree.VoidModifier getCeylonNode();
    }

    public static interface ObjectDefinitionPsi extends TypedDeclarationPsi {
        @Override Tree.ObjectDefinition getCeylonNode();
    }

    public static interface ParameterListPsi extends CeylonCompositeElement {
        @Override Tree.ParameterList getCeylonNode();
    }

    public static interface ParameterPsi extends CeylonCompositeElement {
        @Override Tree.Parameter getCeylonNode();
    }

    public static interface ParameterDeclarationPsi extends ParameterPsi {
        @Override Tree.ParameterDeclaration getCeylonNode();
    }

    public static interface ValueParameterDeclarationPsi extends ParameterDeclarationPsi {
        @Override Tree.ValueParameterDeclaration getCeylonNode();
    }

    public static interface FunctionalParameterDeclarationPsi extends ParameterDeclarationPsi {
        @Override Tree.FunctionalParameterDeclaration getCeylonNode();
    }

    public static interface InitializerParameterPsi extends ParameterPsi {
        @Override Tree.InitializerParameter getCeylonNode();
    }

    public static interface TypeParameterListPsi extends CeylonCompositeElement {
        @Override Tree.TypeParameterList getCeylonNode();
    }

    public static interface TypeParameterDeclarationPsi extends DeclarationPsi {
        @Override Tree.TypeParameterDeclaration getCeylonNode();
    }

    public static interface TypeVariancePsi extends CeylonCompositeElement {
        @Override Tree.TypeVariance getCeylonNode();
    }

    public static interface BodyPsi extends CeylonCompositeElement {
        @Override Tree.Body getCeylonNode();
    }

    public static interface BlockPsi extends BodyPsi {
        @Override Tree.Block getCeylonNode();
    }

    public static interface ClassBodyPsi extends BodyPsi {
        @Override Tree.ClassBody getCeylonNode();
    }

    public static interface InterfaceBodyPsi extends BodyPsi {
        @Override Tree.InterfaceBody getCeylonNode();
    }

    public static interface TypePsi extends CeylonCompositeElement {
        @Override Tree.Type getCeylonNode();
    }

    public static interface StaticTypePsi extends TypePsi {
        @Override Tree.StaticType getCeylonNode();
    }

    public static interface GroupedTypePsi extends StaticTypePsi {
        @Override Tree.GroupedType getCeylonNode();
    }

    public static interface SimpleTypePsi extends StaticTypePsi {
        @Override Tree.SimpleType getCeylonNode();
    }

    public static interface BaseTypePsi extends SimpleTypePsi {
        @Override Tree.BaseType getCeylonNode();
    }

    public static interface QualifiedTypePsi extends SimpleTypePsi {
        @Override Tree.QualifiedType getCeylonNode();
    }

    public static interface UnionTypePsi extends StaticTypePsi {
        @Override Tree.UnionType getCeylonNode();
    }

    public static interface IntersectionTypePsi extends StaticTypePsi {
        @Override Tree.IntersectionType getCeylonNode();
    }

    public static interface SequenceTypePsi extends StaticTypePsi {
        @Override Tree.SequenceType getCeylonNode();
    }

    public static interface IterableTypePsi extends StaticTypePsi {
        @Override Tree.IterableType getCeylonNode();
    }

    public static interface OptionalTypePsi extends StaticTypePsi {
        @Override Tree.OptionalType getCeylonNode();
    }

    public static interface TupleTypePsi extends StaticTypePsi {
        @Override Tree.TupleType getCeylonNode();
    }

    public static interface FunctionTypePsi extends StaticTypePsi {
        @Override Tree.FunctionType getCeylonNode();
    }

    public static interface EntryTypePsi extends StaticTypePsi {
        @Override Tree.EntryType getCeylonNode();
    }

    public static interface TypeConstructorPsi extends StaticTypePsi {
        @Override Tree.TypeConstructor getCeylonNode();
    }

    public static interface SuperTypePsi extends StaticTypePsi {
        @Override Tree.SuperType getCeylonNode();
    }

    public static interface MetaLiteralPsi extends PrimaryPsi {
        @Override Tree.MetaLiteral getCeylonNode();
    }

    public static interface TypeLiteralPsi extends MetaLiteralPsi {
        @Override Tree.TypeLiteral getCeylonNode();
    }

    public static interface MemberLiteralPsi extends MetaLiteralPsi {
        @Override Tree.MemberLiteral getCeylonNode();
    }

    public static interface ClassLiteralPsi extends TypeLiteralPsi {
        @Override Tree.ClassLiteral getCeylonNode();
    }

    public static interface NewLiteralPsi extends TypeLiteralPsi {
        @Override Tree.NewLiteral getCeylonNode();
    }

    public static interface InterfaceLiteralPsi extends TypeLiteralPsi {
        @Override Tree.InterfaceLiteral getCeylonNode();
    }

    public static interface AliasLiteralPsi extends TypeLiteralPsi {
        @Override Tree.AliasLiteral getCeylonNode();
    }

    public static interface TypeParameterLiteralPsi extends TypeLiteralPsi {
        @Override Tree.TypeParameterLiteral getCeylonNode();
    }

    public static interface ValueLiteralPsi extends MemberLiteralPsi {
        @Override Tree.ValueLiteral getCeylonNode();
    }

    public static interface FunctionLiteralPsi extends MemberLiteralPsi {
        @Override Tree.FunctionLiteral getCeylonNode();
    }

    public static interface ModuleLiteralPsi extends MetaLiteralPsi {
        @Override Tree.ModuleLiteral getCeylonNode();
    }

    public static interface PackageLiteralPsi extends MetaLiteralPsi {
        @Override Tree.PackageLiteral getCeylonNode();
    }

    public static interface DynamicModifierPsi extends TypePsi {
        @Override Tree.DynamicModifier getCeylonNode();
    }

    public static interface LocalModifierPsi extends TypePsi {
        @Override Tree.LocalModifier getCeylonNode();
    }

    public static interface ValueModifierPsi extends LocalModifierPsi {
        @Override Tree.ValueModifier getCeylonNode();
    }

    public static interface FunctionModifierPsi extends LocalModifierPsi {
        @Override Tree.FunctionModifier getCeylonNode();
    }

    public static interface SyntheticVariablePsi extends ValueModifierPsi {
        @Override Tree.SyntheticVariable getCeylonNode();
    }

    public static interface TypeArgumentsPsi extends CeylonCompositeElement {
        @Override Tree.TypeArguments getCeylonNode();
    }

    public static interface TypeArgumentListPsi extends TypeArgumentsPsi {
        @Override Tree.TypeArgumentList getCeylonNode();
    }

    public static interface InferredTypeArgumentsPsi extends TypeArgumentsPsi {
        @Override Tree.InferredTypeArguments getCeylonNode();
    }

    public static interface SequencedTypePsi extends TypePsi {
        @Override Tree.SequencedType getCeylonNode();
    }

    public static interface DefaultedTypePsi extends TypePsi {
        @Override Tree.DefaultedType getCeylonNode();
    }

    public static interface SpreadTypePsi extends TypePsi {
        @Override Tree.SpreadType getCeylonNode();
    }

    public static interface DirectivePsi extends ExecutableStatementPsi {
        @Override Tree.Directive getCeylonNode();
    }

    public static interface ReturnPsi extends DirectivePsi {
        @Override Tree.Return getCeylonNode();
    }

    public static interface ThrowPsi extends DirectivePsi {
        @Override Tree.Throw getCeylonNode();
    }

    public static interface ContinuePsi extends DirectivePsi {
        @Override Tree.Continue getCeylonNode();
    }

    public static interface BreakPsi extends DirectivePsi {
        @Override Tree.Break getCeylonNode();
    }

    public static interface StatementOrArgumentPsi extends CeylonCompositeElement {
        @Override Tree.StatementOrArgument getCeylonNode();
    }

    public static interface StatementPsi extends StatementOrArgumentPsi {
        @Override Tree.Statement getCeylonNode();
    }

    public static interface CompilerAnnotationPsi extends CeylonCompositeElement {
        @Override Tree.CompilerAnnotation getCeylonNode();
    }

    public static interface ExecutableStatementPsi extends StatementPsi {
        @Override Tree.ExecutableStatement getCeylonNode();
    }

    public static interface AssertionPsi extends ExecutableStatementPsi {
        @Override Tree.Assertion getCeylonNode();
    }

    public static interface SpecifierStatementPsi extends ExecutableStatementPsi {
        @Override Tree.SpecifierStatement getCeylonNode();
    }

    public static interface ExpressionStatementPsi extends ExecutableStatementPsi {
        @Override Tree.ExpressionStatement getCeylonNode();
    }

    public static interface PatternPsi extends CeylonCompositeElement {
        @Override Tree.Pattern getCeylonNode();
    }

    public static interface VariablePatternPsi extends PatternPsi {
        @Override Tree.VariablePattern getCeylonNode();
    }

    public static interface TuplePatternPsi extends PatternPsi {
        @Override Tree.TuplePattern getCeylonNode();
    }

    public static interface KeyValuePatternPsi extends PatternPsi {
        @Override Tree.KeyValuePattern getCeylonNode();
    }

    public static interface DestructurePsi extends ExecutableStatementPsi {
        @Override Tree.Destructure getCeylonNode();
    }

    public static interface ControlStatementPsi extends ExecutableStatementPsi {
        @Override Tree.ControlStatement getCeylonNode();
    }

    public static interface ControlClausePsi extends CeylonCompositeElement {
        @Override Tree.ControlClause getCeylonNode();
    }

    public static interface DynamicStatementPsi extends ControlStatementPsi {
        @Override Tree.DynamicStatement getCeylonNode();
    }

    public static interface DynamicClausePsi extends ControlClausePsi {
        @Override Tree.DynamicClause getCeylonNode();
    }

    public static interface LetExpressionPsi extends TermPsi {
        @Override Tree.LetExpression getCeylonNode();
    }

    public static interface LetClausePsi extends ControlClausePsi {
        @Override Tree.LetClause getCeylonNode();
    }

    public static interface IfStatementPsi extends ControlStatementPsi {
        @Override Tree.IfStatement getCeylonNode();
    }

    public static interface IfClausePsi extends ControlClausePsi {
        @Override Tree.IfClause getCeylonNode();
    }

    public static interface ElseClausePsi extends ControlClausePsi {
        @Override Tree.ElseClause getCeylonNode();
    }

    public static interface SwitchStatementPsi extends ControlStatementPsi {
        @Override Tree.SwitchStatement getCeylonNode();
    }

    public static interface SwitchClausePsi extends CeylonCompositeElement {
        @Override Tree.SwitchClause getCeylonNode();
    }

    public static interface SwitchedPsi extends CeylonCompositeElement {
        @Override Tree.Switched getCeylonNode();
    }

    public static interface SwitchCaseListPsi extends CeylonCompositeElement {
        @Override Tree.SwitchCaseList getCeylonNode();
    }

    public static interface CaseClausePsi extends ControlClausePsi {
        @Override Tree.CaseClause getCeylonNode();
    }

    public static interface CaseItemPsi extends CeylonCompositeElement {
        @Override Tree.CaseItem getCeylonNode();
    }

    public static interface MatchCasePsi extends CaseItemPsi {
        @Override Tree.MatchCase getCeylonNode();
    }

    public static interface IsCasePsi extends CaseItemPsi {
        @Override Tree.IsCase getCeylonNode();
    }

    public static interface SatisfiesCasePsi extends CaseItemPsi {
        @Override Tree.SatisfiesCase getCeylonNode();
    }

    public static interface TryCatchStatementPsi extends ControlStatementPsi {
        @Override Tree.TryCatchStatement getCeylonNode();
    }

    public static interface TryClausePsi extends ControlClausePsi {
        @Override Tree.TryClause getCeylonNode();
    }

    public static interface CatchClausePsi extends ControlClausePsi {
        @Override Tree.CatchClause getCeylonNode();
    }

    public static interface FinallyClausePsi extends ControlClausePsi {
        @Override Tree.FinallyClause getCeylonNode();
    }

    public static interface ResourceListPsi extends CeylonCompositeElement {
        @Override Tree.ResourceList getCeylonNode();
    }

    public static interface ResourcePsi extends CeylonCompositeElement {
        @Override Tree.Resource getCeylonNode();
    }

    public static interface CatchVariablePsi extends CeylonCompositeElement {
        @Override Tree.CatchVariable getCeylonNode();
    }

    public static interface ForStatementPsi extends ControlStatementPsi {
        @Override Tree.ForStatement getCeylonNode();
    }

    public static interface ForClausePsi extends ControlClausePsi {
        @Override Tree.ForClause getCeylonNode();
    }

    public static interface ForIteratorPsi extends StatementOrArgumentPsi {
        @Override Tree.ForIterator getCeylonNode();
    }

    public static interface ValueIteratorPsi extends ForIteratorPsi {
        @Override Tree.ValueIterator getCeylonNode();
    }

    public static interface PatternIteratorPsi extends ForIteratorPsi {
        @Override Tree.PatternIterator getCeylonNode();
    }

    public static interface WhileStatementPsi extends ControlStatementPsi {
        @Override Tree.WhileStatement getCeylonNode();
    }

    public static interface WhileClausePsi extends ControlClausePsi {
        @Override Tree.WhileClause getCeylonNode();
    }

    public static interface ConditionListPsi extends CeylonCompositeElement {
        @Override Tree.ConditionList getCeylonNode();
    }

    public static interface ConditionPsi extends CeylonCompositeElement {
        @Override Tree.Condition getCeylonNode();
    }

    public static interface BooleanConditionPsi extends ConditionPsi {
        @Override Tree.BooleanCondition getCeylonNode();
    }

    public static interface ExistsOrNonemptyConditionPsi extends ConditionPsi {
        @Override Tree.ExistsOrNonemptyCondition getCeylonNode();
    }

    public static interface ExistsConditionPsi extends ExistsOrNonemptyConditionPsi {
        @Override Tree.ExistsCondition getCeylonNode();
    }

    public static interface NonemptyConditionPsi extends ExistsOrNonemptyConditionPsi {
        @Override Tree.NonemptyCondition getCeylonNode();
    }

    public static interface IsConditionPsi extends ConditionPsi {
        @Override Tree.IsCondition getCeylonNode();
    }

    public static interface SatisfiesConditionPsi extends ConditionPsi {
        @Override Tree.SatisfiesCondition getCeylonNode();
    }

    public static interface VariablePsi extends TypedDeclarationPsi {
        @Override Tree.Variable getCeylonNode();
    }

    public static interface TermPsi extends CeylonCompositeElement {
        @Override Tree.Term getCeylonNode();
    }

    public static interface OperatorExpressionPsi extends TermPsi {
        @Override Tree.OperatorExpression getCeylonNode();
    }

    public static interface BinaryOperatorExpressionPsi extends OperatorExpressionPsi {
        @Override Tree.BinaryOperatorExpression getCeylonNode();
    }

    public static interface ArithmeticOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.ArithmeticOp getCeylonNode();
    }

    public static interface SumOpPsi extends ArithmeticOpPsi {
        @Override Tree.SumOp getCeylonNode();
    }

    public static interface DifferenceOpPsi extends ArithmeticOpPsi {
        @Override Tree.DifferenceOp getCeylonNode();
    }

    public static interface ProductOpPsi extends ArithmeticOpPsi {
        @Override Tree.ProductOp getCeylonNode();
    }

    public static interface QuotientOpPsi extends ArithmeticOpPsi {
        @Override Tree.QuotientOp getCeylonNode();
    }

    public static interface PowerOpPsi extends ArithmeticOpPsi {
        @Override Tree.PowerOp getCeylonNode();
    }

    public static interface RemainderOpPsi extends ArithmeticOpPsi {
        @Override Tree.RemainderOp getCeylonNode();
    }

    public static interface AssignmentOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.AssignmentOp getCeylonNode();
    }

    public static interface AssignOpPsi extends AssignmentOpPsi {
        @Override Tree.AssignOp getCeylonNode();
    }

    public static interface ArithmeticAssignmentOpPsi extends AssignmentOpPsi {
        @Override Tree.ArithmeticAssignmentOp getCeylonNode();
    }

    public static interface AddAssignOpPsi extends ArithmeticAssignmentOpPsi {
        @Override Tree.AddAssignOp getCeylonNode();
    }

    public static interface SubtractAssignOpPsi extends ArithmeticAssignmentOpPsi {
        @Override Tree.SubtractAssignOp getCeylonNode();
    }

    public static interface MultiplyAssignOpPsi extends ArithmeticAssignmentOpPsi {
        @Override Tree.MultiplyAssignOp getCeylonNode();
    }

    public static interface DivideAssignOpPsi extends ArithmeticAssignmentOpPsi {
        @Override Tree.DivideAssignOp getCeylonNode();
    }

    public static interface RemainderAssignOpPsi extends ArithmeticAssignmentOpPsi {
        @Override Tree.RemainderAssignOp getCeylonNode();
    }

    public static interface BitwiseAssignmentOpPsi extends AssignmentOpPsi {
        @Override Tree.BitwiseAssignmentOp getCeylonNode();
    }

    public static interface IntersectAssignOpPsi extends BitwiseAssignmentOpPsi {
        @Override Tree.IntersectAssignOp getCeylonNode();
    }

    public static interface UnionAssignOpPsi extends BitwiseAssignmentOpPsi {
        @Override Tree.UnionAssignOp getCeylonNode();
    }

    public static interface ComplementAssignOpPsi extends BitwiseAssignmentOpPsi {
        @Override Tree.ComplementAssignOp getCeylonNode();
    }

    public static interface LogicalAssignmentOpPsi extends AssignmentOpPsi {
        @Override Tree.LogicalAssignmentOp getCeylonNode();
    }

    public static interface AndAssignOpPsi extends LogicalAssignmentOpPsi {
        @Override Tree.AndAssignOp getCeylonNode();
    }

    public static interface OrAssignOpPsi extends LogicalAssignmentOpPsi {
        @Override Tree.OrAssignOp getCeylonNode();
    }

    public static interface LogicalOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.LogicalOp getCeylonNode();
    }

    public static interface AndOpPsi extends LogicalOpPsi {
        @Override Tree.AndOp getCeylonNode();
    }

    public static interface OrOpPsi extends LogicalOpPsi {
        @Override Tree.OrOp getCeylonNode();
    }

    public static interface BitwiseOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.BitwiseOp getCeylonNode();
    }

    public static interface IntersectionOpPsi extends BitwiseOpPsi {
        @Override Tree.IntersectionOp getCeylonNode();
    }

    public static interface UnionOpPsi extends BitwiseOpPsi {
        @Override Tree.UnionOp getCeylonNode();
    }

    public static interface ComplementOpPsi extends BitwiseOpPsi {
        @Override Tree.ComplementOp getCeylonNode();
    }

    public static interface EqualityOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.EqualityOp getCeylonNode();
    }

    public static interface EqualOpPsi extends EqualityOpPsi {
        @Override Tree.EqualOp getCeylonNode();
    }

    public static interface NotEqualOpPsi extends EqualityOpPsi {
        @Override Tree.NotEqualOp getCeylonNode();
    }

    public static interface ComparisonOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.ComparisonOp getCeylonNode();
    }

    public static interface LargerOpPsi extends ComparisonOpPsi {
        @Override Tree.LargerOp getCeylonNode();
    }

    public static interface SmallerOpPsi extends ComparisonOpPsi {
        @Override Tree.SmallerOp getCeylonNode();
    }

    public static interface LargeAsOpPsi extends ComparisonOpPsi {
        @Override Tree.LargeAsOp getCeylonNode();
    }

    public static interface SmallAsOpPsi extends ComparisonOpPsi {
        @Override Tree.SmallAsOp getCeylonNode();
    }

    public static interface ScaleOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.ScaleOp getCeylonNode();
    }

    public static interface BoundPsi extends TermPsi {
        @Override Tree.Bound getCeylonNode();
    }

    public static interface OpenBoundPsi extends BoundPsi {
        @Override Tree.OpenBound getCeylonNode();
    }

    public static interface ClosedBoundPsi extends BoundPsi {
        @Override Tree.ClosedBound getCeylonNode();
    }

    public static interface WithinOpPsi extends OperatorExpressionPsi {
        @Override Tree.WithinOp getCeylonNode();
    }

    public static interface DefaultOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.DefaultOp getCeylonNode();
    }

    public static interface ThenOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.ThenOp getCeylonNode();
    }

    public static interface IdenticalOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.IdenticalOp getCeylonNode();
    }

    public static interface EntryOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.EntryOp getCeylonNode();
    }

    public static interface RangeOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.RangeOp getCeylonNode();
    }

    public static interface SegmentOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.SegmentOp getCeylonNode();
    }

    public static interface CompareOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.CompareOp getCeylonNode();
    }

    public static interface InOpPsi extends BinaryOperatorExpressionPsi {
        @Override Tree.InOp getCeylonNode();
    }

    public static interface UnaryOperatorExpressionPsi extends OperatorExpressionPsi {
        @Override Tree.UnaryOperatorExpression getCeylonNode();
    }

    public static interface NotOpPsi extends UnaryOperatorExpressionPsi {
        @Override Tree.NotOp getCeylonNode();
    }

    public static interface ExistsPsi extends UnaryOperatorExpressionPsi {
        @Override Tree.Exists getCeylonNode();
    }

    public static interface NonemptyPsi extends UnaryOperatorExpressionPsi {
        @Override Tree.Nonempty getCeylonNode();
    }

    public static interface NegativeOpPsi extends UnaryOperatorExpressionPsi {
        @Override Tree.NegativeOp getCeylonNode();
    }

    public static interface PositiveOpPsi extends UnaryOperatorExpressionPsi {
        @Override Tree.PositiveOp getCeylonNode();
    }

    public static interface TypeOperatorExpressionPsi extends UnaryOperatorExpressionPsi {
        @Override Tree.TypeOperatorExpression getCeylonNode();
    }

    public static interface IsOpPsi extends TypeOperatorExpressionPsi {
        @Override Tree.IsOp getCeylonNode();
    }

    public static interface SatisfiesPsi extends TypeOperatorExpressionPsi {
        @Override Tree.Satisfies getCeylonNode();
    }

    public static interface ExtendsPsi extends TypeOperatorExpressionPsi {
        @Override Tree.Extends getCeylonNode();
    }

    public static interface OfOpPsi extends TypeOperatorExpressionPsi {
        @Override Tree.OfOp getCeylonNode();
    }

    public static interface PrefixOperatorExpressionPsi extends UnaryOperatorExpressionPsi {
        @Override Tree.PrefixOperatorExpression getCeylonNode();
    }

    public static interface IncrementOpPsi extends PrefixOperatorExpressionPsi {
        @Override Tree.IncrementOp getCeylonNode();
    }

    public static interface DecrementOpPsi extends PrefixOperatorExpressionPsi {
        @Override Tree.DecrementOp getCeylonNode();
    }

    public static interface PostfixOperatorExpressionPsi extends UnaryOperatorExpressionPsi {
        @Override Tree.PostfixOperatorExpression getCeylonNode();
    }

    public static interface PostfixIncrementOpPsi extends PostfixOperatorExpressionPsi {
        @Override Tree.PostfixIncrementOp getCeylonNode();
    }

    public static interface PostfixDecrementOpPsi extends PostfixOperatorExpressionPsi {
        @Override Tree.PostfixDecrementOp getCeylonNode();
    }

    public static interface ExpressionListPsi extends CeylonCompositeElement {
        @Override Tree.ExpressionList getCeylonNode();
    }

    public static interface ExpressionPsi extends AtomPsi {
        @Override Tree.Expression getCeylonNode();
    }

    public static interface PrimaryPsi extends TermPsi {
        @Override Tree.Primary getCeylonNode();
    }

    public static interface PostfixExpressionPsi extends PrimaryPsi {
        @Override Tree.PostfixExpression getCeylonNode();
    }

    public static interface InvocationExpressionPsi extends PostfixExpressionPsi {
        @Override Tree.InvocationExpression getCeylonNode();
    }

    public static interface ParameterizedExpressionPsi extends PrimaryPsi {
        @Override Tree.ParameterizedExpression getCeylonNode();
    }

    public static interface MemberOrTypeExpressionPsi extends PrimaryPsi {
        @Override Tree.MemberOrTypeExpression getCeylonNode();
    }

    public static interface ExtendedTypeExpressionPsi extends MemberOrTypeExpressionPsi {
        @Override Tree.ExtendedTypeExpression getCeylonNode();
    }

    public static interface StaticMemberOrTypeExpressionPsi extends MemberOrTypeExpressionPsi {
        @Override Tree.StaticMemberOrTypeExpression getCeylonNode();
    }

    public static interface BaseMemberOrTypeExpressionPsi extends StaticMemberOrTypeExpressionPsi {
        @Override Tree.BaseMemberOrTypeExpression getCeylonNode();
    }

    public static interface BaseMemberExpressionPsi extends BaseMemberOrTypeExpressionPsi {
        @Override Tree.BaseMemberExpression getCeylonNode();
    }

    public static interface BaseTypeExpressionPsi extends BaseMemberOrTypeExpressionPsi {
        @Override Tree.BaseTypeExpression getCeylonNode();
    }

    public static interface QualifiedMemberOrTypeExpressionPsi extends StaticMemberOrTypeExpressionPsi {
        @Override Tree.QualifiedMemberOrTypeExpression getCeylonNode();
    }

    public static interface QualifiedMemberExpressionPsi extends QualifiedMemberOrTypeExpressionPsi {
        @Override Tree.QualifiedMemberExpression getCeylonNode();
    }

    public static interface QualifiedTypeExpressionPsi extends QualifiedMemberOrTypeExpressionPsi {
        @Override Tree.QualifiedTypeExpression getCeylonNode();
    }

    public static interface MemberOperatorPsi extends CeylonCompositeElement {
        @Override Tree.MemberOperator getCeylonNode();
    }

    public static interface MemberOpPsi extends MemberOperatorPsi {
        @Override Tree.MemberOp getCeylonNode();
    }

    public static interface SafeMemberOpPsi extends MemberOperatorPsi {
        @Override Tree.SafeMemberOp getCeylonNode();
    }

    public static interface SpreadOpPsi extends MemberOperatorPsi {
        @Override Tree.SpreadOp getCeylonNode();
    }

    public static interface IndexExpressionPsi extends PostfixExpressionPsi {
        @Override Tree.IndexExpression getCeylonNode();
    }

    public static interface ElementOrRangePsi extends CeylonCompositeElement {
        @Override Tree.ElementOrRange getCeylonNode();
    }

    public static interface ElementPsi extends ElementOrRangePsi {
        @Override Tree.Element getCeylonNode();
    }

    public static interface ElementRangePsi extends ElementOrRangePsi {
        @Override Tree.ElementRange getCeylonNode();
    }

    public static interface OuterPsi extends AtomPsi {
        @Override Tree.Outer getCeylonNode();
    }

    public static interface PackagePsi extends AtomPsi {
        @Override Tree.Package getCeylonNode();
    }

    public static interface ArgumentListPsi extends CeylonCompositeElement {
        @Override Tree.ArgumentList getCeylonNode();
    }

    public static interface NamedArgumentListPsi extends ArgumentListPsi {
        @Override Tree.NamedArgumentList getCeylonNode();
    }

    public static interface SequencedArgumentPsi extends StatementOrArgumentPsi {
        @Override Tree.SequencedArgument getCeylonNode();
    }

    public static interface PositionalArgumentListPsi extends ArgumentListPsi {
        @Override Tree.PositionalArgumentList getCeylonNode();
    }

    public static interface PositionalArgumentPsi extends CeylonCompositeElement {
        @Override Tree.PositionalArgument getCeylonNode();
    }

    public static interface ListedArgumentPsi extends PositionalArgumentPsi {
        @Override Tree.ListedArgument getCeylonNode();
    }

    public static interface SpreadArgumentPsi extends PositionalArgumentPsi {
        @Override Tree.SpreadArgument getCeylonNode();
    }

    public static interface FunctionArgumentPsi extends TermPsi {
        @Override Tree.FunctionArgument getCeylonNode();
    }

    public static interface ObjectExpressionPsi extends PrimaryPsi {
        @Override Tree.ObjectExpression getCeylonNode();
    }

    public static interface IfExpressionPsi extends TermPsi {
        @Override Tree.IfExpression getCeylonNode();
    }

    public static interface SwitchExpressionPsi extends TermPsi {
        @Override Tree.SwitchExpression getCeylonNode();
    }

    public static interface NamedArgumentPsi extends StatementOrArgumentPsi {
        @Override Tree.NamedArgument getCeylonNode();
    }

    public static interface SpecifiedArgumentPsi extends NamedArgumentPsi {
        @Override Tree.SpecifiedArgument getCeylonNode();
    }

    public static interface TypedArgumentPsi extends NamedArgumentPsi {
        @Override Tree.TypedArgument getCeylonNode();
    }

    public static interface MethodArgumentPsi extends TypedArgumentPsi {
        @Override Tree.MethodArgument getCeylonNode();
    }

    public static interface AttributeArgumentPsi extends TypedArgumentPsi {
        @Override Tree.AttributeArgument getCeylonNode();
    }

    public static interface ObjectArgumentPsi extends TypedArgumentPsi {
        @Override Tree.ObjectArgument getCeylonNode();
    }

    public static interface SpecifierOrInitializerExpressionPsi extends CeylonCompositeElement {
        @Override Tree.SpecifierOrInitializerExpression getCeylonNode();
    }

    public static interface SpecifierExpressionPsi extends SpecifierOrInitializerExpressionPsi {
        @Override Tree.SpecifierExpression getCeylonNode();
    }

    public static interface LazySpecifierExpressionPsi extends SpecifierExpressionPsi {
        @Override Tree.LazySpecifierExpression getCeylonNode();
    }

    public static interface InitializerExpressionPsi extends SpecifierOrInitializerExpressionPsi {
        @Override Tree.InitializerExpression getCeylonNode();
    }

    public static interface AtomPsi extends PrimaryPsi {
        @Override Tree.Atom getCeylonNode();
    }

    public static interface LiteralPsi extends AtomPsi {
        @Override Tree.Literal getCeylonNode();
    }

    public static interface NaturalLiteralPsi extends LiteralPsi {
        @Override Tree.NaturalLiteral getCeylonNode();
    }

    public static interface FloatLiteralPsi extends LiteralPsi {
        @Override Tree.FloatLiteral getCeylonNode();
    }

    public static interface CharLiteralPsi extends LiteralPsi {
        @Override Tree.CharLiteral getCeylonNode();
    }

    public static interface StringLiteralPsi extends LiteralPsi {
        @Override Tree.StringLiteral getCeylonNode();
    }

    public static interface QuotedLiteralPsi extends LiteralPsi {
        @Override Tree.QuotedLiteral getCeylonNode();
    }

    public static interface DocLinkPsi extends CeylonCompositeElement {
        @Override Tree.DocLink getCeylonNode();
    }

    public static interface SelfExpressionPsi extends AtomPsi {
        @Override Tree.SelfExpression getCeylonNode();
    }

    public static interface ThisPsi extends SelfExpressionPsi {
        @Override Tree.This getCeylonNode();
    }

    public static interface SuperPsi extends SelfExpressionPsi {
        @Override Tree.Super getCeylonNode();
    }

    public static interface SequenceEnumerationPsi extends AtomPsi {
        @Override Tree.SequenceEnumeration getCeylonNode();
    }

    public static interface TuplePsi extends AtomPsi {
        @Override Tree.Tuple getCeylonNode();
    }

    public static interface DynamicPsi extends AtomPsi {
        @Override Tree.Dynamic getCeylonNode();
    }

    public static interface StringTemplatePsi extends AtomPsi {
        @Override Tree.StringTemplate getCeylonNode();
    }

    public static interface AnnotationPsi extends InvocationExpressionPsi {
        @Override Tree.Annotation getCeylonNode();
    }

    public static interface AnonymousAnnotationPsi extends CeylonCompositeElement {
        @Override Tree.AnonymousAnnotation getCeylonNode();
    }

    public static interface AnnotationListPsi extends CeylonCompositeElement {
        @Override Tree.AnnotationList getCeylonNode();
    }

    public static interface IdentifierPsi extends CeylonCompositeElement {
        @Override Tree.Identifier getCeylonNode();
    }

    public static interface ComprehensionPsi extends PositionalArgumentPsi {
        @Override Tree.Comprehension getCeylonNode();
    }

    public static interface ComprehensionClausePsi extends ControlClausePsi {
        @Override Tree.ComprehensionClause getCeylonNode();
    }

    public static interface InitialComprehensionClausePsi extends ComprehensionClausePsi {
        @Override Tree.InitialComprehensionClause getCeylonNode();
    }

    public static interface ExpressionComprehensionClausePsi extends ComprehensionClausePsi {
        @Override Tree.ExpressionComprehensionClause getCeylonNode();
    }

    public static interface ForComprehensionClausePsi extends InitialComprehensionClausePsi {
        @Override Tree.ForComprehensionClause getCeylonNode();
    }

    public static interface IfComprehensionClausePsi extends InitialComprehensionClausePsi {
        @Override Tree.IfComprehensionClause getCeylonNode();
    }

}
