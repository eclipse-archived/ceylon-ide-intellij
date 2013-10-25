package org.intellij.plugins.ceylon.psi;

import com.intellij.lang.ASTNode;
import org.intellij.plugins.ceylon.psi.impl.CeylonCompositeElementImpl;
/* Generated using Antlr by PsiImplGen.g */

public class CeylonPsiImpl {

    public static class CompilationUnitPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CompilationUnitPsi {
        public CompilationUnitPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ModuleDescriptorPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ModuleDescriptorPsi {
        public ModuleDescriptorPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class PackageDescriptorPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.PackageDescriptorPsi {
        public PackageDescriptorPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportModuleListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportModuleListPsi {
        public ImportModuleListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportModulePsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ImportModulePsi {
        public ImportModulePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportListPsi {
        public ImportListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ImportPsi {
        public ImportPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportPathPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportPathPsi {
        public ImportPathPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportMemberOrTypeListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportMemberOrTypeListPsi {
        public ImportMemberOrTypeListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportMemberOrTypePsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ImportMemberOrTypePsi {
        public ImportMemberOrTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportMemberPsiImpl extends ImportMemberOrTypePsiImpl
            implements CeylonPsi.ImportMemberPsi {
        public ImportMemberPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportTypePsiImpl extends ImportMemberOrTypePsiImpl
            implements CeylonPsi.ImportTypePsi {
        public ImportTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AliasPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AliasPsi {
        public AliasPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ImportWildcardPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportWildcardPsi {
        public ImportWildcardPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class DeclarationPsiImpl extends StatementPsiImpl
            implements CeylonPsi.DeclarationPsi {
        public DeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class MissingDeclarationPsiImpl extends DeclarationPsiImpl
            implements CeylonPsi.MissingDeclarationPsi {
        public MissingDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class TypeDeclarationPsiImpl extends DeclarationPsiImpl
            implements CeylonPsi.TypeDeclarationPsi {
        public TypeDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ClassOrInterfacePsiImpl extends TypeDeclarationPsiImpl
            implements CeylonPsi.ClassOrInterfacePsi {
        public ClassOrInterfacePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeAliasDeclarationPsiImpl extends TypeDeclarationPsiImpl
            implements CeylonPsi.TypeAliasDeclarationPsi {
        public TypeAliasDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SatisfiedTypesPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.SatisfiedTypesPsi {
        public SatisfiedTypesPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AbstractedTypePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AbstractedTypePsi {
        public AbstractedTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AdaptedTypesPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AdaptedTypesPsi {
        public AdaptedTypesPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class CaseTypesPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CaseTypesPsi {
        public CaseTypesPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExtendedTypePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ExtendedTypePsi {
        public ExtendedTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeConstraintListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeConstraintListPsi {
        public TypeConstraintListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeConstraintPsiImpl extends TypeDeclarationPsiImpl
            implements CeylonPsi.TypeConstraintPsi {
        public TypeConstraintPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeSpecifierPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeSpecifierPsi {
        public TypeSpecifierPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DefaultTypeArgumentPsiImpl extends TypeSpecifierPsiImpl
            implements CeylonPsi.DefaultTypeArgumentPsi {
        public DefaultTypeArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ClassSpecifierPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ClassSpecifierPsi {
        public ClassSpecifierPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AnyClassPsiImpl extends ClassOrInterfacePsiImpl
            implements CeylonPsi.AnyClassPsi {
        public AnyClassPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ClassDefinitionPsiImpl extends AnyClassPsiImpl
            implements CeylonPsi.ClassDefinitionPsi {
        public ClassDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ClassDeclarationPsiImpl extends AnyClassPsiImpl
            implements CeylonPsi.ClassDeclarationPsi {
        public ClassDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AnyInterfacePsiImpl extends ClassOrInterfacePsiImpl
            implements CeylonPsi.AnyInterfacePsi {
        public AnyInterfacePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InterfaceDefinitionPsiImpl extends AnyInterfacePsiImpl
            implements CeylonPsi.InterfaceDefinitionPsi {
        public InterfaceDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InterfaceDeclarationPsiImpl extends AnyInterfacePsiImpl
            implements CeylonPsi.InterfaceDeclarationPsi {
        public InterfaceDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class TypedDeclarationPsiImpl extends DeclarationPsiImpl
            implements CeylonPsi.TypedDeclarationPsi {
        public TypedDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class AnyAttributePsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.AnyAttributePsi {
        public AnyAttributePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AttributeDeclarationPsiImpl extends AnyAttributePsiImpl
            implements CeylonPsi.AttributeDeclarationPsi {
        public AttributeDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AttributeGetterDefinitionPsiImpl extends AnyAttributePsiImpl
            implements CeylonPsi.AttributeGetterDefinitionPsi {
        public AttributeGetterDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AttributeSetterDefinitionPsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.AttributeSetterDefinitionPsi {
        public AttributeSetterDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AnyMethodPsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.AnyMethodPsi {
        public AnyMethodPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class MethodDefinitionPsiImpl extends AnyMethodPsiImpl
            implements CeylonPsi.MethodDefinitionPsi {
        public MethodDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class MethodDeclarationPsiImpl extends AnyMethodPsiImpl
            implements CeylonPsi.MethodDeclarationPsi {
        public MethodDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class VoidModifierPsiImpl extends TypePsiImpl
            implements CeylonPsi.VoidModifierPsi {
        public VoidModifierPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ObjectDefinitionPsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.ObjectDefinitionPsi {
        public ObjectDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ParameterListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ParameterListPsi {
        public ParameterListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ParameterPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ParameterPsi {
        public ParameterPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ParameterDeclarationPsiImpl extends ParameterPsiImpl
            implements CeylonPsi.ParameterDeclarationPsi {
        public ParameterDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ValueParameterDeclarationPsiImpl extends ParameterDeclarationPsiImpl
            implements CeylonPsi.ValueParameterDeclarationPsi {
        public ValueParameterDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class FunctionalParameterDeclarationPsiImpl extends ParameterDeclarationPsiImpl
            implements CeylonPsi.FunctionalParameterDeclarationPsi {
        public FunctionalParameterDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InitializerParameterPsiImpl extends ParameterPsiImpl
            implements CeylonPsi.InitializerParameterPsi {
        public InitializerParameterPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeParameterListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeParameterListPsi {
        public TypeParameterListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeParameterDeclarationPsiImpl extends DeclarationPsiImpl
            implements CeylonPsi.TypeParameterDeclarationPsi {
        public TypeParameterDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeVariancePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeVariancePsi {
        public TypeVariancePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class BodyPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.BodyPsi {
        public BodyPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class BlockPsiImpl extends BodyPsiImpl
            implements CeylonPsi.BlockPsi {
        public BlockPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ClassBodyPsiImpl extends BodyPsiImpl
            implements CeylonPsi.ClassBodyPsi {
        public ClassBodyPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InterfaceBodyPsiImpl extends BodyPsiImpl
            implements CeylonPsi.InterfaceBodyPsi {
        public InterfaceBodyPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class TypePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypePsi {
        public TypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class StaticTypePsiImpl extends TypePsiImpl
            implements CeylonPsi.StaticTypePsi {
        public StaticTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class SimpleTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.SimpleTypePsi {
        public SimpleTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class BaseTypePsiImpl extends SimpleTypePsiImpl
            implements CeylonPsi.BaseTypePsi {
        public BaseTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class UnionTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.UnionTypePsi {
        public UnionTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IntersectionTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.IntersectionTypePsi {
        public IntersectionTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SequenceTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.SequenceTypePsi {
        public SequenceTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IterableTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.IterableTypePsi {
        public IterableTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class OptionalTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.OptionalTypePsi {
        public OptionalTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TupleTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.TupleTypePsi {
        public TupleTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class FunctionTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.FunctionTypePsi {
        public FunctionTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class EntryTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.EntryTypePsi {
        public EntryTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class QualifiedTypePsiImpl extends SimpleTypePsiImpl
            implements CeylonPsi.QualifiedTypePsi {
        public QualifiedTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SuperTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.SuperTypePsi {
        public SuperTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class MetaLiteralPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.MetaLiteralPsi {
        public MetaLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeLiteralPsiImpl extends MetaLiteralPsiImpl
            implements CeylonPsi.TypeLiteralPsi {
        public TypeLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class MemberLiteralPsiImpl extends MetaLiteralPsiImpl
            implements CeylonPsi.MemberLiteralPsi {
        public MemberLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ClassLiteralPsiImpl extends TypeLiteralPsiImpl
            implements CeylonPsi.ClassLiteralPsi {
        public ClassLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InterfaceLiteralPsiImpl extends TypeLiteralPsiImpl
            implements CeylonPsi.InterfaceLiteralPsi {
        public InterfaceLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AliasLiteralPsiImpl extends TypeLiteralPsiImpl
            implements CeylonPsi.AliasLiteralPsi {
        public AliasLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeParameterLiteralPsiImpl extends TypeLiteralPsiImpl
            implements CeylonPsi.TypeParameterLiteralPsi {
        public TypeParameterLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ValueLiteralPsiImpl extends MemberLiteralPsiImpl
            implements CeylonPsi.ValueLiteralPsi {
        public ValueLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class FunctionLiteralPsiImpl extends MemberLiteralPsiImpl
            implements CeylonPsi.FunctionLiteralPsi {
        public FunctionLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ModuleLiteralPsiImpl extends MetaLiteralPsiImpl
            implements CeylonPsi.ModuleLiteralPsi {
        public ModuleLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class PackageLiteralPsiImpl extends MetaLiteralPsiImpl
            implements CeylonPsi.PackageLiteralPsi {
        public PackageLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DynamicModifierPsiImpl extends TypePsiImpl
            implements CeylonPsi.DynamicModifierPsi {
        public DynamicModifierPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class LocalModifierPsiImpl extends TypePsiImpl
            implements CeylonPsi.LocalModifierPsi {
        public LocalModifierPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ValueModifierPsiImpl extends LocalModifierPsiImpl
            implements CeylonPsi.ValueModifierPsi {
        public ValueModifierPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class FunctionModifierPsiImpl extends LocalModifierPsiImpl
            implements CeylonPsi.FunctionModifierPsi {
        public FunctionModifierPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SyntheticVariablePsiImpl extends ValueModifierPsiImpl
            implements CeylonPsi.SyntheticVariablePsi {
        public SyntheticVariablePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeArgumentsPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeArgumentsPsi {
        public TypeArgumentsPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TypeArgumentListPsiImpl extends TypeArgumentsPsiImpl
            implements CeylonPsi.TypeArgumentListPsi {
        public TypeArgumentListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InferredTypeArgumentsPsiImpl extends TypeArgumentsPsiImpl
            implements CeylonPsi.InferredTypeArgumentsPsi {
        public InferredTypeArgumentsPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SequencedTypePsiImpl extends TypePsiImpl
            implements CeylonPsi.SequencedTypePsi {
        public SequencedTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DefaultedTypePsiImpl extends TypePsiImpl
            implements CeylonPsi.DefaultedTypePsi {
        public DefaultedTypePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class DirectivePsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.DirectivePsi {
        public DirectivePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ReturnPsiImpl extends DirectivePsiImpl
            implements CeylonPsi.ReturnPsi {
        public ReturnPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ThrowPsiImpl extends DirectivePsiImpl
            implements CeylonPsi.ThrowPsi {
        public ThrowPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ContinuePsiImpl extends DirectivePsiImpl
            implements CeylonPsi.ContinuePsi {
        public ContinuePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class BreakPsiImpl extends DirectivePsiImpl
            implements CeylonPsi.BreakPsi {
        public BreakPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class StatementOrArgumentPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.StatementOrArgumentPsi {
        public StatementOrArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class StatementPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.StatementPsi {
        public StatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class CompilerAnnotationPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CompilerAnnotationPsi {
        public CompilerAnnotationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ExecutableStatementPsiImpl extends StatementPsiImpl
            implements CeylonPsi.ExecutableStatementPsi {
        public ExecutableStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AssertionPsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.AssertionPsi {
        public AssertionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SpecifierStatementPsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.SpecifierStatementPsi {
        public SpecifierStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExpressionStatementPsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.ExpressionStatementPsi {
        public ExpressionStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ControlStatementPsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.ControlStatementPsi {
        public ControlStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ControlClausePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ControlClausePsi {
        public ControlClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DynamicStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.DynamicStatementPsi {
        public DynamicStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DynamicClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.DynamicClausePsi {
        public DynamicClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IfStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.IfStatementPsi {
        public IfStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IfClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.IfClausePsi {
        public IfClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ElseClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.ElseClausePsi {
        public ElseClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SwitchStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.SwitchStatementPsi {
        public SwitchStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SwitchClausePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.SwitchClausePsi {
        public SwitchClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SwitchCaseListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.SwitchCaseListPsi {
        public SwitchCaseListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class CaseClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.CaseClausePsi {
        public CaseClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class CaseItemPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CaseItemPsi {
        public CaseItemPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class MatchCasePsiImpl extends CaseItemPsiImpl
            implements CeylonPsi.MatchCasePsi {
        public MatchCasePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IsCasePsiImpl extends CaseItemPsiImpl
            implements CeylonPsi.IsCasePsi {
        public IsCasePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SatisfiesCasePsiImpl extends CaseItemPsiImpl
            implements CeylonPsi.SatisfiesCasePsi {
        public SatisfiesCasePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TryCatchStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.TryCatchStatementPsi {
        public TryCatchStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TryClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.TryClausePsi {
        public TryClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class CatchClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.CatchClausePsi {
        public CatchClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class FinallyClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.FinallyClausePsi {
        public FinallyClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ResourceListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ResourceListPsi {
        public ResourceListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ResourcePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ResourcePsi {
        public ResourcePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class CatchVariablePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CatchVariablePsi {
        public CatchVariablePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ForStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.ForStatementPsi {
        public ForStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ForClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.ForClausePsi {
        public ForClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ForIteratorPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ForIteratorPsi {
        public ForIteratorPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ValueIteratorPsiImpl extends ForIteratorPsiImpl
            implements CeylonPsi.ValueIteratorPsi {
        public ValueIteratorPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class KeyValueIteratorPsiImpl extends ForIteratorPsiImpl
            implements CeylonPsi.KeyValueIteratorPsi {
        public KeyValueIteratorPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class WhileStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.WhileStatementPsi {
        public WhileStatementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class WhileClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.WhileClausePsi {
        public WhileClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ConditionListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ConditionListPsi {
        public ConditionListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ConditionPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ConditionPsi {
        public ConditionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class BooleanConditionPsiImpl extends ConditionPsiImpl
            implements CeylonPsi.BooleanConditionPsi {
        public BooleanConditionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ExistsOrNonemptyConditionPsiImpl extends ConditionPsiImpl
            implements CeylonPsi.ExistsOrNonemptyConditionPsi {
        public ExistsOrNonemptyConditionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExistsConditionPsiImpl extends ExistsOrNonemptyConditionPsiImpl
            implements CeylonPsi.ExistsConditionPsi {
        public ExistsConditionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class NonemptyConditionPsiImpl extends ExistsOrNonemptyConditionPsiImpl
            implements CeylonPsi.NonemptyConditionPsi {
        public NonemptyConditionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IsConditionPsiImpl extends ConditionPsiImpl
            implements CeylonPsi.IsConditionPsi {
        public IsConditionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SatisfiesConditionPsiImpl extends ConditionPsiImpl
            implements CeylonPsi.SatisfiesConditionPsi {
        public SatisfiesConditionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class VariablePsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.VariablePsi {
        public VariablePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class TermPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TermPsi {
        public TermPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class OperatorExpressionPsiImpl extends TermPsiImpl
            implements CeylonPsi.OperatorExpressionPsi {
        public OperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class BinaryOperatorExpressionPsiImpl extends OperatorExpressionPsiImpl
            implements CeylonPsi.BinaryOperatorExpressionPsi {
        public BinaryOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ArithmeticOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.ArithmeticOpPsi {
        public ArithmeticOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SumOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.SumOpPsi {
        public SumOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DifferenceOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.DifferenceOpPsi {
        public DifferenceOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ProductOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.ProductOpPsi {
        public ProductOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class QuotientOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.QuotientOpPsi {
        public QuotientOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class PowerOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.PowerOpPsi {
        public PowerOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class RemainderOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.RemainderOpPsi {
        public RemainderOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class AssignmentOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.AssignmentOpPsi {
        public AssignmentOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AssignOpPsiImpl extends AssignmentOpPsiImpl
            implements CeylonPsi.AssignOpPsi {
        public AssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ArithmeticAssignmentOpPsiImpl extends AssignmentOpPsiImpl
            implements CeylonPsi.ArithmeticAssignmentOpPsi {
        public ArithmeticAssignmentOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AddAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.AddAssignOpPsi {
        public AddAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SubtractAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.SubtractAssignOpPsi {
        public SubtractAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class MultiplyAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.MultiplyAssignOpPsi {
        public MultiplyAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DivideAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.DivideAssignOpPsi {
        public DivideAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class RemainderAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.RemainderAssignOpPsi {
        public RemainderAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class BitwiseAssignmentOpPsiImpl extends AssignmentOpPsiImpl
            implements CeylonPsi.BitwiseAssignmentOpPsi {
        public BitwiseAssignmentOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IntersectAssignOpPsiImpl extends BitwiseAssignmentOpPsiImpl
            implements CeylonPsi.IntersectAssignOpPsi {
        public IntersectAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class UnionAssignOpPsiImpl extends BitwiseAssignmentOpPsiImpl
            implements CeylonPsi.UnionAssignOpPsi {
        public UnionAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class XorAssignOpPsiImpl extends BitwiseAssignmentOpPsiImpl
            implements CeylonPsi.XorAssignOpPsi {
        public XorAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ComplementAssignOpPsiImpl extends BitwiseAssignmentOpPsiImpl
            implements CeylonPsi.ComplementAssignOpPsi {
        public ComplementAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class LogicalAssignmentOpPsiImpl extends AssignmentOpPsiImpl
            implements CeylonPsi.LogicalAssignmentOpPsi {
        public LogicalAssignmentOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AndAssignOpPsiImpl extends LogicalAssignmentOpPsiImpl
            implements CeylonPsi.AndAssignOpPsi {
        public AndAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class OrAssignOpPsiImpl extends LogicalAssignmentOpPsiImpl
            implements CeylonPsi.OrAssignOpPsi {
        public OrAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class LogicalOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.LogicalOpPsi {
        public LogicalOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AndOpPsiImpl extends LogicalOpPsiImpl
            implements CeylonPsi.AndOpPsi {
        public AndOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class OrOpPsiImpl extends LogicalOpPsiImpl
            implements CeylonPsi.OrOpPsi {
        public OrOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class BitwiseOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.BitwiseOpPsi {
        public BitwiseOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IntersectionOpPsiImpl extends BitwiseOpPsiImpl
            implements CeylonPsi.IntersectionOpPsi {
        public IntersectionOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class UnionOpPsiImpl extends BitwiseOpPsiImpl
            implements CeylonPsi.UnionOpPsi {
        public UnionOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class XorOpPsiImpl extends BitwiseOpPsiImpl
            implements CeylonPsi.XorOpPsi {
        public XorOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ComplementOpPsiImpl extends BitwiseOpPsiImpl
            implements CeylonPsi.ComplementOpPsi {
        public ComplementOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class EqualityOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.EqualityOpPsi {
        public EqualityOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class EqualOpPsiImpl extends EqualityOpPsiImpl
            implements CeylonPsi.EqualOpPsi {
        public EqualOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class NotEqualOpPsiImpl extends EqualityOpPsiImpl
            implements CeylonPsi.NotEqualOpPsi {
        public NotEqualOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ComparisonOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.ComparisonOpPsi {
        public ComparisonOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class LargerOpPsiImpl extends ComparisonOpPsiImpl
            implements CeylonPsi.LargerOpPsi {
        public LargerOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SmallerOpPsiImpl extends ComparisonOpPsiImpl
            implements CeylonPsi.SmallerOpPsi {
        public SmallerOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class LargeAsOpPsiImpl extends ComparisonOpPsiImpl
            implements CeylonPsi.LargeAsOpPsi {
        public LargeAsOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SmallAsOpPsiImpl extends ComparisonOpPsiImpl
            implements CeylonPsi.SmallAsOpPsi {
        public SmallAsOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ScaleOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.ScaleOpPsi {
        public ScaleOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class BoundPsiImpl extends TermPsiImpl
            implements CeylonPsi.BoundPsi {
        public BoundPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class OpenBoundPsiImpl extends BoundPsiImpl
            implements CeylonPsi.OpenBoundPsi {
        public OpenBoundPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ClosedBoundPsiImpl extends BoundPsiImpl
            implements CeylonPsi.ClosedBoundPsi {
        public ClosedBoundPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class WithinOpPsiImpl extends OperatorExpressionPsiImpl
            implements CeylonPsi.WithinOpPsi {
        public WithinOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DefaultOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.DefaultOpPsi {
        public DefaultOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ThenOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.ThenOpPsi {
        public ThenOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IdenticalOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.IdenticalOpPsi {
        public IdenticalOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class EntryOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.EntryOpPsi {
        public EntryOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class RangeOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.RangeOpPsi {
        public RangeOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SegmentOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.SegmentOpPsi {
        public SegmentOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class CompareOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.CompareOpPsi {
        public CompareOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.InOpPsi {
        public InOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class UnaryOperatorExpressionPsiImpl extends OperatorExpressionPsiImpl
            implements CeylonPsi.UnaryOperatorExpressionPsi {
        public UnaryOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class NotOpPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.NotOpPsi {
        public NotOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExistsPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.ExistsPsi {
        public ExistsPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class NonemptyPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.NonemptyPsi {
        public NonemptyPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class NegativeOpPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.NegativeOpPsi {
        public NegativeOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class PositiveOpPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.PositiveOpPsi {
        public PositiveOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class TypeOperatorExpressionPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.TypeOperatorExpressionPsi {
        public TypeOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IsOpPsiImpl extends TypeOperatorExpressionPsiImpl
            implements CeylonPsi.IsOpPsi {
        public IsOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SatisfiesPsiImpl extends TypeOperatorExpressionPsiImpl
            implements CeylonPsi.SatisfiesPsi {
        public SatisfiesPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExtendsPsiImpl extends TypeOperatorExpressionPsiImpl
            implements CeylonPsi.ExtendsPsi {
        public ExtendsPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class OfOpPsiImpl extends TypeOperatorExpressionPsiImpl
            implements CeylonPsi.OfOpPsi {
        public OfOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class PrefixOperatorExpressionPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.PrefixOperatorExpressionPsi {
        public PrefixOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IncrementOpPsiImpl extends PrefixOperatorExpressionPsiImpl
            implements CeylonPsi.IncrementOpPsi {
        public IncrementOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DecrementOpPsiImpl extends PrefixOperatorExpressionPsiImpl
            implements CeylonPsi.DecrementOpPsi {
        public DecrementOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class PostfixOperatorExpressionPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.PostfixOperatorExpressionPsi {
        public PostfixOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class PostfixIncrementOpPsiImpl extends PostfixOperatorExpressionPsiImpl
            implements CeylonPsi.PostfixIncrementOpPsi {
        public PostfixIncrementOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class PostfixDecrementOpPsiImpl extends PostfixOperatorExpressionPsiImpl
            implements CeylonPsi.PostfixDecrementOpPsi {
        public PostfixDecrementOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExpressionListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ExpressionListPsi {
        public ExpressionListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExpressionPsiImpl extends AtomPsiImpl
            implements CeylonPsi.ExpressionPsi {
        public ExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class PrimaryPsiImpl extends TermPsiImpl
            implements CeylonPsi.PrimaryPsi {
        public PrimaryPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class PostfixExpressionPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.PostfixExpressionPsi {
        public PostfixExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InvocationExpressionPsiImpl extends PostfixExpressionPsiImpl
            implements CeylonPsi.InvocationExpressionPsi {
        public InvocationExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ParameterizedExpressionPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.ParameterizedExpressionPsi {
        public ParameterizedExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class MemberOrTypeExpressionPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.MemberOrTypeExpressionPsi {
        public MemberOrTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExtendedTypeExpressionPsiImpl extends MemberOrTypeExpressionPsiImpl
            implements CeylonPsi.ExtendedTypeExpressionPsi {
        public ExtendedTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class StaticMemberOrTypeExpressionPsiImpl extends MemberOrTypeExpressionPsiImpl
            implements CeylonPsi.StaticMemberOrTypeExpressionPsi {
        public StaticMemberOrTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class BaseMemberOrTypeExpressionPsiImpl extends StaticMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.BaseMemberOrTypeExpressionPsi {
        public BaseMemberOrTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class BaseMemberExpressionPsiImpl extends BaseMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.BaseMemberExpressionPsi {
        public BaseMemberExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class BaseTypeExpressionPsiImpl extends BaseMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.BaseTypeExpressionPsi {
        public BaseTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class QualifiedMemberOrTypeExpressionPsiImpl extends StaticMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.QualifiedMemberOrTypeExpressionPsi {
        public QualifiedMemberOrTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class QualifiedMemberExpressionPsiImpl extends QualifiedMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.QualifiedMemberExpressionPsi {
        public QualifiedMemberExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class QualifiedTypeExpressionPsiImpl extends QualifiedMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.QualifiedTypeExpressionPsi {
        public QualifiedTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class MemberOperatorPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.MemberOperatorPsi {
        public MemberOperatorPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class MemberOpPsiImpl extends MemberOperatorPsiImpl
            implements CeylonPsi.MemberOpPsi {
        public MemberOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SafeMemberOpPsiImpl extends MemberOperatorPsiImpl
            implements CeylonPsi.SafeMemberOpPsi {
        public SafeMemberOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SpreadOpPsiImpl extends MemberOperatorPsiImpl
            implements CeylonPsi.SpreadOpPsi {
        public SpreadOpPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IndexExpressionPsiImpl extends PostfixExpressionPsiImpl
            implements CeylonPsi.IndexExpressionPsi {
        public IndexExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ElementOrRangePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ElementOrRangePsi {
        public ElementOrRangePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ElementPsiImpl extends ElementOrRangePsiImpl
            implements CeylonPsi.ElementPsi {
        public ElementPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ElementRangePsiImpl extends ElementOrRangePsiImpl
            implements CeylonPsi.ElementRangePsi {
        public ElementRangePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class OuterPsiImpl extends AtomPsiImpl
            implements CeylonPsi.OuterPsi {
        public OuterPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class PackagePsiImpl extends AtomPsiImpl
            implements CeylonPsi.PackagePsi {
        public PackagePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ArgumentListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ArgumentListPsi {
        public ArgumentListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class NamedArgumentListPsiImpl extends ArgumentListPsiImpl
            implements CeylonPsi.NamedArgumentListPsi {
        public NamedArgumentListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SequencedArgumentPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.SequencedArgumentPsi {
        public SequencedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class PositionalArgumentListPsiImpl extends ArgumentListPsiImpl
            implements CeylonPsi.PositionalArgumentListPsi {
        public PositionalArgumentListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class PositionalArgumentPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.PositionalArgumentPsi {
        public PositionalArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ListedArgumentPsiImpl extends PositionalArgumentPsiImpl
            implements CeylonPsi.ListedArgumentPsi {
        public ListedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SpreadArgumentPsiImpl extends PositionalArgumentPsiImpl
            implements CeylonPsi.SpreadArgumentPsi {
        public SpreadArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class FunctionArgumentPsiImpl extends TermPsiImpl
            implements CeylonPsi.FunctionArgumentPsi {
        public FunctionArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class NamedArgumentPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.NamedArgumentPsi {
        public NamedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SpecifiedArgumentPsiImpl extends NamedArgumentPsiImpl
            implements CeylonPsi.SpecifiedArgumentPsi {
        public SpecifiedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class TypedArgumentPsiImpl extends NamedArgumentPsiImpl
            implements CeylonPsi.TypedArgumentPsi {
        public TypedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class MethodArgumentPsiImpl extends TypedArgumentPsiImpl
            implements CeylonPsi.MethodArgumentPsi {
        public MethodArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AttributeArgumentPsiImpl extends TypedArgumentPsiImpl
            implements CeylonPsi.AttributeArgumentPsi {
        public AttributeArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ObjectArgumentPsiImpl extends TypedArgumentPsiImpl
            implements CeylonPsi.ObjectArgumentPsi {
        public ObjectArgumentPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class SpecifierOrInitializerExpressionPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.SpecifierOrInitializerExpressionPsi {
        public SpecifierOrInitializerExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SpecifierExpressionPsiImpl extends SpecifierOrInitializerExpressionPsiImpl
            implements CeylonPsi.SpecifierExpressionPsi {
        public SpecifierExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class LazySpecifierExpressionPsiImpl extends SpecifierExpressionPsiImpl
            implements CeylonPsi.LazySpecifierExpressionPsi {
        public LazySpecifierExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class InitializerExpressionPsiImpl extends SpecifierOrInitializerExpressionPsiImpl
            implements CeylonPsi.InitializerExpressionPsi {
        public InitializerExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class AtomPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.AtomPsi {
        public AtomPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class LiteralPsiImpl extends AtomPsiImpl
            implements CeylonPsi.LiteralPsi {
        public LiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class NaturalLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.NaturalLiteralPsi {
        public NaturalLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class FloatLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.FloatLiteralPsi {
        public FloatLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class CharLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.CharLiteralPsi {
        public CharLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class StringLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.StringLiteralPsi {
        public StringLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class QuotedLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.QuotedLiteralPsi {
        public QuotedLiteralPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DocLinkPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.DocLinkPsi {
        public DocLinkPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class SelfExpressionPsiImpl extends AtomPsiImpl
            implements CeylonPsi.SelfExpressionPsi {
        public SelfExpressionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ThisPsiImpl extends SelfExpressionPsiImpl
            implements CeylonPsi.ThisPsi {
        public ThisPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SuperPsiImpl extends SelfExpressionPsiImpl
            implements CeylonPsi.SuperPsi {
        public SuperPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class SequenceEnumerationPsiImpl extends AtomPsiImpl
            implements CeylonPsi.SequenceEnumerationPsi {
        public SequenceEnumerationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class TuplePsiImpl extends AtomPsiImpl
            implements CeylonPsi.TuplePsi {
        public TuplePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class DynamicPsiImpl extends AtomPsiImpl
            implements CeylonPsi.DynamicPsi {
        public DynamicPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class StringTemplatePsiImpl extends AtomPsiImpl
            implements CeylonPsi.StringTemplatePsi {
        public StringTemplatePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AnnotationPsiImpl extends InvocationExpressionPsiImpl
            implements CeylonPsi.AnnotationPsi {
        public AnnotationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AnonymousAnnotationPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AnonymousAnnotationPsi {
        public AnonymousAnnotationPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class AnnotationListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AnnotationListPsi {
        public AnnotationListPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IdentifierPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.IdentifierPsi {
        public IdentifierPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ComprehensionPsiImpl extends PositionalArgumentPsiImpl
            implements CeylonPsi.ComprehensionPsi {
        public ComprehensionPsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static abstract class ComprehensionClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.ComprehensionClausePsi {
        public ComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ExpressionComprehensionClausePsiImpl extends ComprehensionClausePsiImpl
            implements CeylonPsi.ExpressionComprehensionClausePsi {
        public ExpressionComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class ForComprehensionClausePsiImpl extends ComprehensionClausePsiImpl
            implements CeylonPsi.ForComprehensionClausePsi {
        public ForComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

    public static class IfComprehensionClausePsiImpl extends ComprehensionClausePsiImpl
            implements CeylonPsi.IfComprehensionClausePsi {
        public IfComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
    }

}
