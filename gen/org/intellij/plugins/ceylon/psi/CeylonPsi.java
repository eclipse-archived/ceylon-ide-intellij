package org.intellij.plugins.ceylon.psi;

import com.intellij.lang.ASTNode;

import org.intellij.plugins.ceylon.psi.impl.CeylonCompositeElementImpl;

public class CeylonPsi {

    public static class CompilationUnitPsi extends CeylonCompositeElementImpl {
        public CompilationUnitPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ModuleDescriptorPsi extends StatementOrArgumentPsi {
        public ModuleDescriptorPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class PackageDescriptorPsi extends StatementOrArgumentPsi {
        public PackageDescriptorPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportModuleListPsi extends CeylonCompositeElementImpl {
        public ImportModuleListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportModulePsi extends StatementOrArgumentPsi {
        public ImportModulePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportListPsi extends CeylonCompositeElementImpl {
        public ImportListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportPsi extends StatementOrArgumentPsi {
        public ImportPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportPathPsi extends CeylonCompositeElementImpl {
        public ImportPathPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportMemberOrTypeListPsi extends CeylonCompositeElementImpl {
        public ImportMemberOrTypeListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportMemberOrTypePsi extends StatementOrArgumentPsi {
        public ImportMemberOrTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportMemberPsi extends ImportMemberOrTypePsi {
        public ImportMemberPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportTypePsi extends ImportMemberOrTypePsi {
        public ImportTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AliasPsi extends CeylonCompositeElementImpl {
        public AliasPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ImportWildcardPsi extends CeylonCompositeElementImpl {
        public ImportWildcardPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class DeclarationPsi extends StatementPsi {
        public DeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class MissingDeclarationPsi extends DeclarationPsi {
        public MissingDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class TypeDeclarationPsi extends DeclarationPsi {
        public TypeDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ClassOrInterfacePsi extends TypeDeclarationPsi {
        public ClassOrInterfacePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeAliasDeclarationPsi extends TypeDeclarationPsi {
        public TypeAliasDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SatisfiedTypesPsi extends CeylonCompositeElementImpl {
        public SatisfiedTypesPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AbstractedTypePsi extends CeylonCompositeElementImpl {
        public AbstractedTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AdaptedTypesPsi extends CeylonCompositeElementImpl {
        public AdaptedTypesPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class CaseTypesPsi extends CeylonCompositeElementImpl {
        public CaseTypesPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExtendedTypePsi extends CeylonCompositeElementImpl {
        public ExtendedTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeConstraintListPsi extends CeylonCompositeElementImpl {
        public TypeConstraintListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeConstraintPsi extends TypeDeclarationPsi {
        public TypeConstraintPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeSpecifierPsi extends CeylonCompositeElementImpl {
        public TypeSpecifierPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DefaultTypeArgumentPsi extends TypeSpecifierPsi {
        public DefaultTypeArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ClassSpecifierPsi extends CeylonCompositeElementImpl {
        public ClassSpecifierPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AnyClassPsi extends ClassOrInterfacePsi {
        public AnyClassPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ClassDefinitionPsi extends AnyClassPsi {
        public ClassDefinitionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ClassDeclarationPsi extends AnyClassPsi {
        public ClassDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AnyInterfacePsi extends ClassOrInterfacePsi {
        public AnyInterfacePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InterfaceDefinitionPsi extends AnyInterfacePsi {
        public InterfaceDefinitionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InterfaceDeclarationPsi extends AnyInterfacePsi {
        public InterfaceDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class TypedDeclarationPsi extends DeclarationPsi {
        public TypedDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class AnyAttributePsi extends TypedDeclarationPsi {
        public AnyAttributePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AttributeDeclarationPsi extends AnyAttributePsi {
        public AttributeDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AttributeGetterDefinitionPsi extends AnyAttributePsi {
        public AttributeGetterDefinitionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AttributeSetterDefinitionPsi extends TypedDeclarationPsi {
        public AttributeSetterDefinitionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AnyMethodPsi extends TypedDeclarationPsi {
        public AnyMethodPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class MethodDefinitionPsi extends AnyMethodPsi {
        public MethodDefinitionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class MethodDeclarationPsi extends AnyMethodPsi {
        public MethodDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class VoidModifierPsi extends TypePsi {
        public VoidModifierPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ObjectDefinitionPsi extends TypedDeclarationPsi {
        public ObjectDefinitionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ParameterListPsi extends CeylonCompositeElementImpl {
        public ParameterListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ParameterPsi extends CeylonCompositeElementImpl {
        public ParameterPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ParameterDeclarationPsi extends ParameterPsi {
        public ParameterDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ValueParameterDeclarationPsi extends ParameterDeclarationPsi {
        public ValueParameterDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class FunctionalParameterDeclarationPsi extends ParameterDeclarationPsi {
        public FunctionalParameterDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InitializerParameterPsi extends ParameterPsi {
        public InitializerParameterPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeParameterListPsi extends CeylonCompositeElementImpl {
        public TypeParameterListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeParameterDeclarationPsi extends DeclarationPsi {
        public TypeParameterDeclarationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeVariancePsi extends CeylonCompositeElementImpl {
        public TypeVariancePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class BodyPsi extends CeylonCompositeElementImpl {
        public BodyPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class BlockPsi extends BodyPsi {
        public BlockPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ClassBodyPsi extends BodyPsi {
        public ClassBodyPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InterfaceBodyPsi extends BodyPsi {
        public InterfaceBodyPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class TypePsi extends CeylonCompositeElementImpl {
        public TypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class StaticTypePsi extends TypePsi {
        public StaticTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class SimpleTypePsi extends StaticTypePsi {
        public SimpleTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class BaseTypePsi extends SimpleTypePsi {
        public BaseTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class UnionTypePsi extends StaticTypePsi {
        public UnionTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IntersectionTypePsi extends StaticTypePsi {
        public IntersectionTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SequenceTypePsi extends StaticTypePsi {
        public SequenceTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IterableTypePsi extends StaticTypePsi {
        public IterableTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class OptionalTypePsi extends StaticTypePsi {
        public OptionalTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TupleTypePsi extends StaticTypePsi {
        public TupleTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class FunctionTypePsi extends StaticTypePsi {
        public FunctionTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class EntryTypePsi extends StaticTypePsi {
        public EntryTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class QualifiedTypePsi extends SimpleTypePsi {
        public QualifiedTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SuperTypePsi extends StaticTypePsi {
        public SuperTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class MetaLiteralPsi extends PrimaryPsi {
        public MetaLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeLiteralPsi extends MetaLiteralPsi {
        public TypeLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class MemberLiteralPsi extends MetaLiteralPsi {
        public MemberLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ClassLiteralPsi extends TypeLiteralPsi {
        public ClassLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InterfaceLiteralPsi extends TypeLiteralPsi {
        public InterfaceLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AliasLiteralPsi extends TypeLiteralPsi {
        public AliasLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeParameterLiteralPsi extends TypeLiteralPsi {
        public TypeParameterLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ValueLiteralPsi extends MemberLiteralPsi {
        public ValueLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class FunctionLiteralPsi extends MemberLiteralPsi {
        public FunctionLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ModuleLiteralPsi extends MetaLiteralPsi {
        public ModuleLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class PackageLiteralPsi extends MetaLiteralPsi {
        public PackageLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DynamicModifierPsi extends TypePsi {
        public DynamicModifierPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class LocalModifierPsi extends TypePsi {
        public LocalModifierPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ValueModifierPsi extends LocalModifierPsi {
        public ValueModifierPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class FunctionModifierPsi extends LocalModifierPsi {
        public FunctionModifierPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SyntheticVariablePsi extends ValueModifierPsi {
        public SyntheticVariablePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeArgumentsPsi extends CeylonCompositeElementImpl {
        public TypeArgumentsPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TypeArgumentListPsi extends TypeArgumentsPsi {
        public TypeArgumentListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InferredTypeArgumentsPsi extends TypeArgumentsPsi {
        public InferredTypeArgumentsPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SequencedTypePsi extends TypePsi {
        public SequencedTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DefaultedTypePsi extends TypePsi {
        public DefaultedTypePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class DirectivePsi extends ExecutableStatementPsi {
        public DirectivePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ReturnPsi extends DirectivePsi {
        public ReturnPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ThrowPsi extends DirectivePsi {
        public ThrowPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ContinuePsi extends DirectivePsi {
        public ContinuePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class BreakPsi extends DirectivePsi {
        public BreakPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class StatementOrArgumentPsi extends CeylonCompositeElementImpl {
        public StatementOrArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class StatementPsi extends StatementOrArgumentPsi {
        public StatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class CompilerAnnotationPsi extends CeylonCompositeElementImpl {
        public CompilerAnnotationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ExecutableStatementPsi extends StatementPsi {
        public ExecutableStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AssertionPsi extends ExecutableStatementPsi {
        public AssertionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SpecifierStatementPsi extends ExecutableStatementPsi {
        public SpecifierStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExpressionStatementPsi extends ExecutableStatementPsi {
        public ExpressionStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ControlStatementPsi extends ExecutableStatementPsi {
        public ControlStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ControlClausePsi extends CeylonCompositeElementImpl {
        public ControlClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DynamicStatementPsi extends ControlStatementPsi {
        public DynamicStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DynamicClausePsi extends ControlClausePsi {
        public DynamicClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IfStatementPsi extends ControlStatementPsi {
        public IfStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IfClausePsi extends ControlClausePsi {
        public IfClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ElseClausePsi extends ControlClausePsi {
        public ElseClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SwitchStatementPsi extends ControlStatementPsi {
        public SwitchStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SwitchClausePsi extends CeylonCompositeElementImpl {
        public SwitchClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SwitchCaseListPsi extends CeylonCompositeElementImpl {
        public SwitchCaseListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class CaseClausePsi extends ControlClausePsi {
        public CaseClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class CaseItemPsi extends CeylonCompositeElementImpl {
        public CaseItemPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class MatchCasePsi extends CaseItemPsi {
        public MatchCasePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IsCasePsi extends CaseItemPsi {
        public IsCasePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SatisfiesCasePsi extends CaseItemPsi {
        public SatisfiesCasePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TryCatchStatementPsi extends ControlStatementPsi {
        public TryCatchStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TryClausePsi extends ControlClausePsi {
        public TryClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class CatchClausePsi extends ControlClausePsi {
        public CatchClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class FinallyClausePsi extends ControlClausePsi {
        public FinallyClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ResourceListPsi extends CeylonCompositeElementImpl {
        public ResourceListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ResourcePsi extends CeylonCompositeElementImpl {
        public ResourcePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class CatchVariablePsi extends CeylonCompositeElementImpl {
        public CatchVariablePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ForStatementPsi extends ControlStatementPsi {
        public ForStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ForClausePsi extends ControlClausePsi {
        public ForClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ForIteratorPsi extends StatementOrArgumentPsi {
        public ForIteratorPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ValueIteratorPsi extends ForIteratorPsi {
        public ValueIteratorPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class KeyValueIteratorPsi extends ForIteratorPsi {
        public KeyValueIteratorPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class WhileStatementPsi extends ControlStatementPsi {
        public WhileStatementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class WhileClausePsi extends ControlClausePsi {
        public WhileClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ConditionListPsi extends CeylonCompositeElementImpl {
        public ConditionListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ConditionPsi extends CeylonCompositeElementImpl {
        public ConditionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class BooleanConditionPsi extends ConditionPsi {
        public BooleanConditionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ExistsOrNonemptyConditionPsi extends ConditionPsi {
        public ExistsOrNonemptyConditionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExistsConditionPsi extends ExistsOrNonemptyConditionPsi {
        public ExistsConditionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class NonemptyConditionPsi extends ExistsOrNonemptyConditionPsi {
        public NonemptyConditionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IsConditionPsi extends ConditionPsi {
        public IsConditionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SatisfiesConditionPsi extends ConditionPsi {
        public SatisfiesConditionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class VariablePsi extends TypedDeclarationPsi {
        public VariablePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class TermPsi extends CeylonCompositeElementImpl {
        public TermPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class OperatorExpressionPsi extends TermPsi {
        public OperatorExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class BinaryOperatorExpressionPsi extends OperatorExpressionPsi {
        public BinaryOperatorExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ArithmeticOpPsi extends BinaryOperatorExpressionPsi {
        public ArithmeticOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SumOpPsi extends ArithmeticOpPsi {
        public SumOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DifferenceOpPsi extends ArithmeticOpPsi {
        public DifferenceOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ProductOpPsi extends ArithmeticOpPsi {
        public ProductOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class QuotientOpPsi extends ArithmeticOpPsi {
        public QuotientOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class PowerOpPsi extends ArithmeticOpPsi {
        public PowerOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class RemainderOpPsi extends ArithmeticOpPsi {
        public RemainderOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class AssignmentOpPsi extends BinaryOperatorExpressionPsi {
        public AssignmentOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AssignOpPsi extends AssignmentOpPsi {
        public AssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ArithmeticAssignmentOpPsi extends AssignmentOpPsi {
        public ArithmeticAssignmentOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AddAssignOpPsi extends ArithmeticAssignmentOpPsi {
        public AddAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SubtractAssignOpPsi extends ArithmeticAssignmentOpPsi {
        public SubtractAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class MultiplyAssignOpPsi extends ArithmeticAssignmentOpPsi {
        public MultiplyAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DivideAssignOpPsi extends ArithmeticAssignmentOpPsi {
        public DivideAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class RemainderAssignOpPsi extends ArithmeticAssignmentOpPsi {
        public RemainderAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class BitwiseAssignmentOpPsi extends AssignmentOpPsi {
        public BitwiseAssignmentOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IntersectAssignOpPsi extends BitwiseAssignmentOpPsi {
        public IntersectAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class UnionAssignOpPsi extends BitwiseAssignmentOpPsi {
        public UnionAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class XorAssignOpPsi extends BitwiseAssignmentOpPsi {
        public XorAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ComplementAssignOpPsi extends BitwiseAssignmentOpPsi {
        public ComplementAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class LogicalAssignmentOpPsi extends AssignmentOpPsi {
        public LogicalAssignmentOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AndAssignOpPsi extends LogicalAssignmentOpPsi {
        public AndAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class OrAssignOpPsi extends LogicalAssignmentOpPsi {
        public OrAssignOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class LogicalOpPsi extends BinaryOperatorExpressionPsi {
        public LogicalOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AndOpPsi extends LogicalOpPsi {
        public AndOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class OrOpPsi extends LogicalOpPsi {
        public OrOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class BitwiseOpPsi extends BinaryOperatorExpressionPsi {
        public BitwiseOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IntersectionOpPsi extends BitwiseOpPsi {
        public IntersectionOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class UnionOpPsi extends BitwiseOpPsi {
        public UnionOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class XorOpPsi extends BitwiseOpPsi {
        public XorOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ComplementOpPsi extends BitwiseOpPsi {
        public ComplementOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class EqualityOpPsi extends BinaryOperatorExpressionPsi {
        public EqualityOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class EqualOpPsi extends EqualityOpPsi {
        public EqualOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class NotEqualOpPsi extends EqualityOpPsi {
        public NotEqualOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ComparisonOpPsi extends BinaryOperatorExpressionPsi {
        public ComparisonOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class LargerOpPsi extends ComparisonOpPsi {
        public LargerOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SmallerOpPsi extends ComparisonOpPsi {
        public SmallerOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class LargeAsOpPsi extends ComparisonOpPsi {
        public LargeAsOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SmallAsOpPsi extends ComparisonOpPsi {
        public SmallAsOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ScaleOpPsi extends BinaryOperatorExpressionPsi {
        public ScaleOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class BoundPsi extends TermPsi {
        public BoundPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class OpenBoundPsi extends BoundPsi {
        public OpenBoundPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ClosedBoundPsi extends BoundPsi {
        public ClosedBoundPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class WithinOpPsi extends OperatorExpressionPsi {
        public WithinOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DefaultOpPsi extends BinaryOperatorExpressionPsi {
        public DefaultOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ThenOpPsi extends BinaryOperatorExpressionPsi {
        public ThenOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IdenticalOpPsi extends BinaryOperatorExpressionPsi {
        public IdenticalOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class EntryOpPsi extends BinaryOperatorExpressionPsi {
        public EntryOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class RangeOpPsi extends BinaryOperatorExpressionPsi {
        public RangeOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SegmentOpPsi extends BinaryOperatorExpressionPsi {
        public SegmentOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class CompareOpPsi extends BinaryOperatorExpressionPsi {
        public CompareOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InOpPsi extends BinaryOperatorExpressionPsi {
        public InOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class UnaryOperatorExpressionPsi extends OperatorExpressionPsi {
        public UnaryOperatorExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class NotOpPsi extends UnaryOperatorExpressionPsi {
        public NotOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExistsPsi extends UnaryOperatorExpressionPsi {
        public ExistsPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class NonemptyPsi extends UnaryOperatorExpressionPsi {
        public NonemptyPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class NegativeOpPsi extends UnaryOperatorExpressionPsi {
        public NegativeOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class PositiveOpPsi extends UnaryOperatorExpressionPsi {
        public PositiveOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class TypeOperatorExpressionPsi extends UnaryOperatorExpressionPsi {
        public TypeOperatorExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IsOpPsi extends TypeOperatorExpressionPsi {
        public IsOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SatisfiesPsi extends TypeOperatorExpressionPsi {
        public SatisfiesPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExtendsPsi extends TypeOperatorExpressionPsi {
        public ExtendsPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class OfOpPsi extends TypeOperatorExpressionPsi {
        public OfOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class PrefixOperatorExpressionPsi extends UnaryOperatorExpressionPsi {
        public PrefixOperatorExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IncrementOpPsi extends PrefixOperatorExpressionPsi {
        public IncrementOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DecrementOpPsi extends PrefixOperatorExpressionPsi {
        public DecrementOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class PostfixOperatorExpressionPsi extends UnaryOperatorExpressionPsi {
        public PostfixOperatorExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class PostfixIncrementOpPsi extends PostfixOperatorExpressionPsi {
        public PostfixIncrementOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class PostfixDecrementOpPsi extends PostfixOperatorExpressionPsi {
        public PostfixDecrementOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExpressionListPsi extends CeylonCompositeElementImpl {
        public ExpressionListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExpressionPsi extends AtomPsi {
        public ExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class PrimaryPsi extends TermPsi {
        public PrimaryPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class PostfixExpressionPsi extends PrimaryPsi {
        public PostfixExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InvocationExpressionPsi extends PostfixExpressionPsi {
        public InvocationExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ParameterizedExpressionPsi extends PrimaryPsi {
        public ParameterizedExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class MemberOrTypeExpressionPsi extends PrimaryPsi {
        public MemberOrTypeExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExtendedTypeExpressionPsi extends MemberOrTypeExpressionPsi {
        public ExtendedTypeExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class StaticMemberOrTypeExpressionPsi extends MemberOrTypeExpressionPsi {
        public StaticMemberOrTypeExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class BaseMemberOrTypeExpressionPsi extends StaticMemberOrTypeExpressionPsi {
        public BaseMemberOrTypeExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class BaseMemberExpressionPsi extends BaseMemberOrTypeExpressionPsi {
        public BaseMemberExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class BaseTypeExpressionPsi extends BaseMemberOrTypeExpressionPsi {
        public BaseTypeExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class QualifiedMemberOrTypeExpressionPsi extends StaticMemberOrTypeExpressionPsi {
        public QualifiedMemberOrTypeExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class QualifiedMemberExpressionPsi extends QualifiedMemberOrTypeExpressionPsi {
        public QualifiedMemberExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class QualifiedTypeExpressionPsi extends QualifiedMemberOrTypeExpressionPsi {
        public QualifiedTypeExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class MemberOperatorPsi extends CeylonCompositeElementImpl {
        public MemberOperatorPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class MemberOpPsi extends MemberOperatorPsi {
        public MemberOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SafeMemberOpPsi extends MemberOperatorPsi {
        public SafeMemberOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SpreadOpPsi extends MemberOperatorPsi {
        public SpreadOpPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IndexExpressionPsi extends PostfixExpressionPsi {
        public IndexExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ElementOrRangePsi extends CeylonCompositeElementImpl {
        public ElementOrRangePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ElementPsi extends ElementOrRangePsi {
        public ElementPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ElementRangePsi extends ElementOrRangePsi {
        public ElementRangePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class OuterPsi extends AtomPsi {
        public OuterPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class PackagePsi extends AtomPsi {
        public PackagePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ArgumentListPsi extends CeylonCompositeElementImpl {
        public ArgumentListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class NamedArgumentListPsi extends ArgumentListPsi {
        public NamedArgumentListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SequencedArgumentPsi extends StatementOrArgumentPsi {
        public SequencedArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class PositionalArgumentListPsi extends ArgumentListPsi {
        public PositionalArgumentListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class PositionalArgumentPsi extends CeylonCompositeElementImpl {
        public PositionalArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ListedArgumentPsi extends PositionalArgumentPsi {
        public ListedArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SpreadArgumentPsi extends PositionalArgumentPsi {
        public SpreadArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class FunctionArgumentPsi extends TermPsi {
        public FunctionArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class NamedArgumentPsi extends StatementOrArgumentPsi {
        public NamedArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SpecifiedArgumentPsi extends NamedArgumentPsi {
        public SpecifiedArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class TypedArgumentPsi extends NamedArgumentPsi {
        public TypedArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class MethodArgumentPsi extends TypedArgumentPsi {
        public MethodArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AttributeArgumentPsi extends TypedArgumentPsi {
        public AttributeArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ObjectArgumentPsi extends TypedArgumentPsi {
        public ObjectArgumentPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class SpecifierOrInitializerExpressionPsi extends CeylonCompositeElementImpl {
        public SpecifierOrInitializerExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SpecifierExpressionPsi extends SpecifierOrInitializerExpressionPsi {
        public SpecifierExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class LazySpecifierExpressionPsi extends SpecifierExpressionPsi {
        public LazySpecifierExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class InitializerExpressionPsi extends SpecifierOrInitializerExpressionPsi {
        public InitializerExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class AtomPsi extends PrimaryPsi {
        public AtomPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class LiteralPsi extends AtomPsi {
        public LiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class NaturalLiteralPsi extends LiteralPsi {
        public NaturalLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class FloatLiteralPsi extends LiteralPsi {
        public FloatLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class CharLiteralPsi extends LiteralPsi {
        public CharLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class StringLiteralPsi extends LiteralPsi {
        public StringLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class QuotedLiteralPsi extends LiteralPsi {
        public QuotedLiteralPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DocLinkPsi extends CeylonCompositeElementImpl {
        public DocLinkPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class SelfExpressionPsi extends AtomPsi {
        public SelfExpressionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ThisPsi extends SelfExpressionPsi {
        public ThisPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SuperPsi extends SelfExpressionPsi {
        public SuperPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class SequenceEnumerationPsi extends AtomPsi {
        public SequenceEnumerationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class TuplePsi extends AtomPsi {
        public TuplePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class DynamicPsi extends AtomPsi {
        public DynamicPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class StringTemplatePsi extends AtomPsi {
        public StringTemplatePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AnnotationPsi extends InvocationExpressionPsi {
        public AnnotationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AnonymousAnnotationPsi extends CeylonCompositeElementImpl {
        public AnonymousAnnotationPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class AnnotationListPsi extends CeylonCompositeElementImpl {
        public AnnotationListPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IdentifierPsi extends CeylonCompositeElementImpl {
        public IdentifierPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ComprehensionPsi extends PositionalArgumentPsi {
        public ComprehensionPsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static abstract class ComprehensionClausePsi extends ControlClausePsi {
        public ComprehensionClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ExpressionComprehensionClausePsi extends ComprehensionClausePsi {
        public ExpressionComprehensionClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class ForComprehensionClausePsi extends ComprehensionClausePsi {
        public ForComprehensionClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

    public static class IfComprehensionClausePsi extends ComprehensionClausePsi {
        public IfComprehensionClausePsi(ASTNode astNode) {
            super(astNode);
        }
    }

}
