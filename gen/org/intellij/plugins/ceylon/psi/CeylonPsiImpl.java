package org.intellij.plugins.ceylon.psi;

import com.intellij.lang.ASTNode;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.codeInsight.resolve.CeylonResolvable;
import org.intellij.plugins.ceylon.codeInsight.resolve.DeclarationPsiNameIdOwner;
import org.intellij.plugins.ceylon.codeInsight.resolve.IdentifiableBaseMemberExpression;
import org.intellij.plugins.ceylon.codeInsight.resolve.ParameterDeclarationPsiIdOwner;
import org.intellij.plugins.ceylon.psi.impl.CeylonCompositeElementImpl;
/* Generated using Antlr by PsiImplGen.g */

public class CeylonPsiImpl {

    public static class CompilationUnitPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CompilationUnitPsi {
        public CompilationUnitPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CompilationUnit getCeylonNode() { return (Tree.CompilationUnit) super.getCeylonNode(); }
    }

    public static class ModuleDescriptorPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ModuleDescriptorPsi {
        public ModuleDescriptorPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ModuleDescriptor getCeylonNode() { return (Tree.ModuleDescriptor) super.getCeylonNode(); }
    }

    public static class PackageDescriptorPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.PackageDescriptorPsi {
        public PackageDescriptorPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PackageDescriptor getCeylonNode() { return (Tree.PackageDescriptor) super.getCeylonNode(); }
    }

    public static class ImportModuleListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportModuleListPsi {
        public ImportModuleListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportModuleList getCeylonNode() { return (Tree.ImportModuleList) super.getCeylonNode(); }
    }

    public static class ImportModulePsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ImportModulePsi {
        public ImportModulePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportModule getCeylonNode() { return (Tree.ImportModule) super.getCeylonNode(); }
    }

    public static class ImportListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportListPsi {
        public ImportListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportList getCeylonNode() { return (Tree.ImportList) super.getCeylonNode(); }
    }

    public static class ImportPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ImportPsi {
        public ImportPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Import getCeylonNode() { return (Tree.Import) super.getCeylonNode(); }
    }

    public static class ImportPathPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportPathPsi {
        public ImportPathPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportPath getCeylonNode() { return (Tree.ImportPath) super.getCeylonNode(); }
    }

    public static class ImportMemberOrTypeListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportMemberOrTypeListPsi {
        public ImportMemberOrTypeListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportMemberOrTypeList getCeylonNode() { return (Tree.ImportMemberOrTypeList) super.getCeylonNode(); }
    }

    public static class ImportMemberOrTypePsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ImportMemberOrTypePsi {
        public ImportMemberOrTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportMemberOrType getCeylonNode() { return (Tree.ImportMemberOrType) super.getCeylonNode(); }
    }

    public static class ImportMemberPsiImpl extends ImportMemberOrTypePsiImpl
            implements CeylonPsi.ImportMemberPsi {
        public ImportMemberPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportMember getCeylonNode() { return (Tree.ImportMember) super.getCeylonNode(); }
    }

    public static class ImportTypePsiImpl extends ImportMemberOrTypePsiImpl
            implements CeylonPsi.ImportTypePsi {
        public ImportTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportType getCeylonNode() { return (Tree.ImportType) super.getCeylonNode(); }
    }

    public static class AliasPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AliasPsi {
        public AliasPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Alias getCeylonNode() { return (Tree.Alias) super.getCeylonNode(); }
    }

    public static class ImportWildcardPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ImportWildcardPsi {
        public ImportWildcardPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ImportWildcard getCeylonNode() { return (Tree.ImportWildcard) super.getCeylonNode(); }
    }

    public static abstract class DeclarationPsiImpl extends StatementPsiImpl
            implements CeylonPsi.DeclarationPsi {
        public DeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Declaration getCeylonNode() { return (Tree.Declaration) super.getCeylonNode(); }
    }

    public static class MissingDeclarationPsiImpl extends DeclarationPsiNameIdOwner
            implements CeylonPsi.MissingDeclarationPsi {
        public MissingDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MissingDeclaration getCeylonNode() { return (Tree.MissingDeclaration) super.getCeylonNode(); }
    }

    public static abstract class TypeDeclarationPsiImpl extends DeclarationPsiNameIdOwner
            implements CeylonPsi.TypeDeclarationPsi {
        public TypeDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeDeclaration getCeylonNode() { return (Tree.TypeDeclaration) super.getCeylonNode(); }
    }

    public static abstract class ClassOrInterfacePsiImpl extends TypeDeclarationPsiImpl
            implements CeylonPsi.ClassOrInterfacePsi {
        public ClassOrInterfacePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ClassOrInterface getCeylonNode() { return (Tree.ClassOrInterface) super.getCeylonNode(); }
    }

    public static class TypeAliasDeclarationPsiImpl extends TypeDeclarationPsiImpl
            implements CeylonPsi.TypeAliasDeclarationPsi {
        public TypeAliasDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeAliasDeclaration getCeylonNode() { return (Tree.TypeAliasDeclaration) super.getCeylonNode(); }
    }

    public static class SatisfiedTypesPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.SatisfiedTypesPsi {
        public SatisfiedTypesPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SatisfiedTypes getCeylonNode() { return (Tree.SatisfiedTypes) super.getCeylonNode(); }
    }

    public static class AbstractedTypePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AbstractedTypePsi {
        public AbstractedTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AbstractedType getCeylonNode() { return (Tree.AbstractedType) super.getCeylonNode(); }
    }

    public static class CaseTypesPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CaseTypesPsi {
        public CaseTypesPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CaseTypes getCeylonNode() { return (Tree.CaseTypes) super.getCeylonNode(); }
    }

    public static class ExtendedTypePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ExtendedTypePsi {
        public ExtendedTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ExtendedType getCeylonNode() { return (Tree.ExtendedType) super.getCeylonNode(); }
    }

    public static class TypeConstraintListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeConstraintListPsi {
        public TypeConstraintListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeConstraintList getCeylonNode() { return (Tree.TypeConstraintList) super.getCeylonNode(); }
    }

    public static class TypeConstraintPsiImpl extends TypeDeclarationPsiImpl
            implements CeylonPsi.TypeConstraintPsi {
        public TypeConstraintPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeConstraint getCeylonNode() { return (Tree.TypeConstraint) super.getCeylonNode(); }
    }

    public static class TypeSpecifierPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeSpecifierPsi {
        public TypeSpecifierPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeSpecifier getCeylonNode() { return (Tree.TypeSpecifier) super.getCeylonNode(); }
    }

    public static class DefaultTypeArgumentPsiImpl extends TypeSpecifierPsiImpl
            implements CeylonPsi.DefaultTypeArgumentPsi {
        public DefaultTypeArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DefaultTypeArgument getCeylonNode() { return (Tree.DefaultTypeArgument) super.getCeylonNode(); }
    }

    public static class ClassSpecifierPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ClassSpecifierPsi {
        public ClassSpecifierPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ClassSpecifier getCeylonNode() { return (Tree.ClassSpecifier) super.getCeylonNode(); }
    }

    public static class AnyClassPsiImpl extends ClassOrInterfacePsiImpl
            implements CeylonPsi.AnyClassPsi {
        public AnyClassPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AnyClass getCeylonNode() { return (Tree.AnyClass) super.getCeylonNode(); }
    }

    public static class ClassDefinitionPsiImpl extends AnyClassPsiImpl
            implements CeylonPsi.ClassDefinitionPsi {
        public ClassDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ClassDefinition getCeylonNode() { return (Tree.ClassDefinition) super.getCeylonNode(); }
    }

    public static class ClassDeclarationPsiImpl extends AnyClassPsiImpl
            implements CeylonPsi.ClassDeclarationPsi {
        public ClassDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ClassDeclaration getCeylonNode() { return (Tree.ClassDeclaration) super.getCeylonNode(); }
    }

    public static class AnyInterfacePsiImpl extends ClassOrInterfacePsiImpl
            implements CeylonPsi.AnyInterfacePsi {
        public AnyInterfacePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AnyInterface getCeylonNode() { return (Tree.AnyInterface) super.getCeylonNode(); }
    }

    public static class InterfaceDefinitionPsiImpl extends AnyInterfacePsiImpl
            implements CeylonPsi.InterfaceDefinitionPsi {
        public InterfaceDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InterfaceDefinition getCeylonNode() { return (Tree.InterfaceDefinition) super.getCeylonNode(); }
    }

    public static class InterfaceDeclarationPsiImpl extends AnyInterfacePsiImpl
            implements CeylonPsi.InterfaceDeclarationPsi {
        public InterfaceDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InterfaceDeclaration getCeylonNode() { return (Tree.InterfaceDeclaration) super.getCeylonNode(); }
    }

    public static abstract class TypedDeclarationPsiImpl extends DeclarationPsiNameIdOwner
            implements CeylonPsi.TypedDeclarationPsi {
        public TypedDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypedDeclaration getCeylonNode() { return (Tree.TypedDeclaration) super.getCeylonNode(); }
    }

    public static abstract class AnyAttributePsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.AnyAttributePsi {
        public AnyAttributePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AnyAttribute getCeylonNode() { return (Tree.AnyAttribute) super.getCeylonNode(); }
    }

    public static class AttributeDeclarationPsiImpl extends AnyAttributePsiImpl
            implements CeylonPsi.AttributeDeclarationPsi {
        public AttributeDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AttributeDeclaration getCeylonNode() { return (Tree.AttributeDeclaration) super.getCeylonNode(); }
    }

    public static class AttributeGetterDefinitionPsiImpl extends AnyAttributePsiImpl
            implements CeylonPsi.AttributeGetterDefinitionPsi {
        public AttributeGetterDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AttributeGetterDefinition getCeylonNode() { return (Tree.AttributeGetterDefinition) super.getCeylonNode(); }
    }

    public static class AttributeSetterDefinitionPsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.AttributeSetterDefinitionPsi {
        public AttributeSetterDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AttributeSetterDefinition getCeylonNode() { return (Tree.AttributeSetterDefinition) super.getCeylonNode(); }
    }

    public static class AnyMethodPsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.AnyMethodPsi {
        public AnyMethodPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AnyMethod getCeylonNode() { return (Tree.AnyMethod) super.getCeylonNode(); }
    }

    public static class MethodDefinitionPsiImpl extends AnyMethodPsiImpl
            implements CeylonPsi.MethodDefinitionPsi {
        public MethodDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MethodDefinition getCeylonNode() { return (Tree.MethodDefinition) super.getCeylonNode(); }
    }

    public static class MethodDeclarationPsiImpl extends AnyMethodPsiImpl
            implements CeylonPsi.MethodDeclarationPsi {
        public MethodDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MethodDeclaration getCeylonNode() { return (Tree.MethodDeclaration) super.getCeylonNode(); }
    }

    public static class VoidModifierPsiImpl extends TypePsiImpl
            implements CeylonPsi.VoidModifierPsi {
        public VoidModifierPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.VoidModifier getCeylonNode() { return (Tree.VoidModifier) super.getCeylonNode(); }
    }

    public static class ObjectDefinitionPsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.ObjectDefinitionPsi {
        public ObjectDefinitionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ObjectDefinition getCeylonNode() { return (Tree.ObjectDefinition) super.getCeylonNode(); }
    }

    public static class ParameterListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ParameterListPsi {
        public ParameterListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ParameterList getCeylonNode() { return (Tree.ParameterList) super.getCeylonNode(); }
    }

    public static abstract class ParameterPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ParameterPsi {
        public ParameterPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Parameter getCeylonNode() { return (Tree.Parameter) super.getCeylonNode(); }
    }

    public static abstract class ParameterDeclarationPsiImpl extends ParameterPsiImpl
            implements CeylonPsi.ParameterDeclarationPsi {
        public ParameterDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ParameterDeclaration getCeylonNode() { return (Tree.ParameterDeclaration) super.getCeylonNode(); }
    }

    public static class ValueParameterDeclarationPsiImpl extends ParameterDeclarationPsiIdOwner
            implements CeylonPsi.ValueParameterDeclarationPsi {
        public ValueParameterDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ValueParameterDeclaration getCeylonNode() { return (Tree.ValueParameterDeclaration) super.getCeylonNode(); }
    }

    public static class FunctionalParameterDeclarationPsiImpl extends ParameterDeclarationPsiIdOwner
            implements CeylonPsi.FunctionalParameterDeclarationPsi {
        public FunctionalParameterDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.FunctionalParameterDeclaration getCeylonNode() { return (Tree.FunctionalParameterDeclaration) super.getCeylonNode(); }
    }

    public static class InitializerParameterPsiImpl extends ParameterPsiImpl
            implements CeylonPsi.InitializerParameterPsi {
        public InitializerParameterPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InitializerParameter getCeylonNode() { return (Tree.InitializerParameter) super.getCeylonNode(); }
    }

    public static class TypeParameterListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeParameterListPsi {
        public TypeParameterListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeParameterList getCeylonNode() { return (Tree.TypeParameterList) super.getCeylonNode(); }
    }

    public static class TypeParameterDeclarationPsiImpl extends DeclarationPsiNameIdOwner
            implements CeylonPsi.TypeParameterDeclarationPsi {
        public TypeParameterDeclarationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeParameterDeclaration getCeylonNode() { return (Tree.TypeParameterDeclaration) super.getCeylonNode(); }
    }

    public static class TypeVariancePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeVariancePsi {
        public TypeVariancePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeVariance getCeylonNode() { return (Tree.TypeVariance) super.getCeylonNode(); }
    }

    public static abstract class BodyPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.BodyPsi {
        public BodyPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Body getCeylonNode() { return (Tree.Body) super.getCeylonNode(); }
    }

    public static class BlockPsiImpl extends BodyPsiImpl
            implements CeylonPsi.BlockPsi {
        public BlockPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Block getCeylonNode() { return (Tree.Block) super.getCeylonNode(); }
    }

    public static class ClassBodyPsiImpl extends BodyPsiImpl
            implements CeylonPsi.ClassBodyPsi {
        public ClassBodyPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ClassBody getCeylonNode() { return (Tree.ClassBody) super.getCeylonNode(); }
    }

    public static class InterfaceBodyPsiImpl extends BodyPsiImpl
            implements CeylonPsi.InterfaceBodyPsi {
        public InterfaceBodyPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InterfaceBody getCeylonNode() { return (Tree.InterfaceBody) super.getCeylonNode(); }
    }

    public static abstract class TypePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypePsi {
        public TypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Type getCeylonNode() { return (Tree.Type) super.getCeylonNode(); }
    }

    public static abstract class StaticTypePsiImpl extends TypePsiImpl
            implements CeylonPsi.StaticTypePsi {
        public StaticTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.StaticType getCeylonNode() { return (Tree.StaticType) super.getCeylonNode(); }
    }

    public static class GroupedTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.GroupedTypePsi {
        public GroupedTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.GroupedType getCeylonNode() { return (Tree.GroupedType) super.getCeylonNode(); }
    }

    public static abstract class SimpleTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.SimpleTypePsi {
        public SimpleTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SimpleType getCeylonNode() { return (Tree.SimpleType) super.getCeylonNode(); }
    }

    public static class BaseTypePsiImpl extends SimpleTypePsiImpl
            implements CeylonPsi.BaseTypePsi {
        public BaseTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.BaseType getCeylonNode() { return (Tree.BaseType) super.getCeylonNode(); }
    }

    public static class QualifiedTypePsiImpl extends SimpleTypePsiImpl
            implements CeylonPsi.QualifiedTypePsi {
        public QualifiedTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.QualifiedType getCeylonNode() { return (Tree.QualifiedType) super.getCeylonNode(); }
    }

    public static class UnionTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.UnionTypePsi {
        public UnionTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.UnionType getCeylonNode() { return (Tree.UnionType) super.getCeylonNode(); }
    }

    public static class IntersectionTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.IntersectionTypePsi {
        public IntersectionTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IntersectionType getCeylonNode() { return (Tree.IntersectionType) super.getCeylonNode(); }
    }

    public static class SequenceTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.SequenceTypePsi {
        public SequenceTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SequenceType getCeylonNode() { return (Tree.SequenceType) super.getCeylonNode(); }
    }

    public static class IterableTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.IterableTypePsi {
        public IterableTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IterableType getCeylonNode() { return (Tree.IterableType) super.getCeylonNode(); }
    }

    public static class OptionalTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.OptionalTypePsi {
        public OptionalTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.OptionalType getCeylonNode() { return (Tree.OptionalType) super.getCeylonNode(); }
    }

    public static class TupleTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.TupleTypePsi {
        public TupleTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TupleType getCeylonNode() { return (Tree.TupleType) super.getCeylonNode(); }
    }

    public static class FunctionTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.FunctionTypePsi {
        public FunctionTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.FunctionType getCeylonNode() { return (Tree.FunctionType) super.getCeylonNode(); }
    }

    public static class EntryTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.EntryTypePsi {
        public EntryTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.EntryType getCeylonNode() { return (Tree.EntryType) super.getCeylonNode(); }
    }

    public static class SuperTypePsiImpl extends StaticTypePsiImpl
            implements CeylonPsi.SuperTypePsi {
        public SuperTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SuperType getCeylonNode() { return (Tree.SuperType) super.getCeylonNode(); }
    }

    public static abstract class MetaLiteralPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.MetaLiteralPsi {
        public MetaLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MetaLiteral getCeylonNode() { return (Tree.MetaLiteral) super.getCeylonNode(); }
    }

    public static class TypeLiteralPsiImpl extends MetaLiteralPsiImpl
            implements CeylonPsi.TypeLiteralPsi {
        public TypeLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeLiteral getCeylonNode() { return (Tree.TypeLiteral) super.getCeylonNode(); }
    }

    public static class MemberLiteralPsiImpl extends MetaLiteralPsiImpl
            implements CeylonPsi.MemberLiteralPsi {
        public MemberLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MemberLiteral getCeylonNode() { return (Tree.MemberLiteral) super.getCeylonNode(); }
    }

    public static class ClassLiteralPsiImpl extends TypeLiteralPsiImpl
            implements CeylonPsi.ClassLiteralPsi {
        public ClassLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ClassLiteral getCeylonNode() { return (Tree.ClassLiteral) super.getCeylonNode(); }
    }

    public static class InterfaceLiteralPsiImpl extends TypeLiteralPsiImpl
            implements CeylonPsi.InterfaceLiteralPsi {
        public InterfaceLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InterfaceLiteral getCeylonNode() { return (Tree.InterfaceLiteral) super.getCeylonNode(); }
    }

    public static class AliasLiteralPsiImpl extends TypeLiteralPsiImpl
            implements CeylonPsi.AliasLiteralPsi {
        public AliasLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AliasLiteral getCeylonNode() { return (Tree.AliasLiteral) super.getCeylonNode(); }
    }

    public static class TypeParameterLiteralPsiImpl extends TypeLiteralPsiImpl
            implements CeylonPsi.TypeParameterLiteralPsi {
        public TypeParameterLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeParameterLiteral getCeylonNode() { return (Tree.TypeParameterLiteral) super.getCeylonNode(); }
    }

    public static class ValueLiteralPsiImpl extends MemberLiteralPsiImpl
            implements CeylonPsi.ValueLiteralPsi {
        public ValueLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ValueLiteral getCeylonNode() { return (Tree.ValueLiteral) super.getCeylonNode(); }
    }

    public static class FunctionLiteralPsiImpl extends MemberLiteralPsiImpl
            implements CeylonPsi.FunctionLiteralPsi {
        public FunctionLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.FunctionLiteral getCeylonNode() { return (Tree.FunctionLiteral) super.getCeylonNode(); }
    }

    public static class ModuleLiteralPsiImpl extends MetaLiteralPsiImpl
            implements CeylonPsi.ModuleLiteralPsi {
        public ModuleLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ModuleLiteral getCeylonNode() { return (Tree.ModuleLiteral) super.getCeylonNode(); }
    }

    public static class PackageLiteralPsiImpl extends MetaLiteralPsiImpl
            implements CeylonPsi.PackageLiteralPsi {
        public PackageLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PackageLiteral getCeylonNode() { return (Tree.PackageLiteral) super.getCeylonNode(); }
    }

    public static class DynamicModifierPsiImpl extends TypePsiImpl
            implements CeylonPsi.DynamicModifierPsi {
        public DynamicModifierPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DynamicModifier getCeylonNode() { return (Tree.DynamicModifier) super.getCeylonNode(); }
    }

    public static abstract class LocalModifierPsiImpl extends TypePsiImpl
            implements CeylonPsi.LocalModifierPsi {
        public LocalModifierPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.LocalModifier getCeylonNode() { return (Tree.LocalModifier) super.getCeylonNode(); }
    }

    public static class ValueModifierPsiImpl extends LocalModifierPsiImpl
            implements CeylonPsi.ValueModifierPsi {
        public ValueModifierPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ValueModifier getCeylonNode() { return (Tree.ValueModifier) super.getCeylonNode(); }
    }

    public static class FunctionModifierPsiImpl extends LocalModifierPsiImpl
            implements CeylonPsi.FunctionModifierPsi {
        public FunctionModifierPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.FunctionModifier getCeylonNode() { return (Tree.FunctionModifier) super.getCeylonNode(); }
    }

    public static class SyntheticVariablePsiImpl extends ValueModifierPsiImpl
            implements CeylonPsi.SyntheticVariablePsi {
        public SyntheticVariablePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SyntheticVariable getCeylonNode() { return (Tree.SyntheticVariable) super.getCeylonNode(); }
    }

    public static class TypeArgumentsPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TypeArgumentsPsi {
        public TypeArgumentsPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeArguments getCeylonNode() { return (Tree.TypeArguments) super.getCeylonNode(); }
    }

    public static class TypeArgumentListPsiImpl extends TypeArgumentsPsiImpl
            implements CeylonPsi.TypeArgumentListPsi {
        public TypeArgumentListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeArgumentList getCeylonNode() { return (Tree.TypeArgumentList) super.getCeylonNode(); }
    }

    public static class InferredTypeArgumentsPsiImpl extends TypeArgumentsPsiImpl
            implements CeylonPsi.InferredTypeArgumentsPsi {
        public InferredTypeArgumentsPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InferredTypeArguments getCeylonNode() { return (Tree.InferredTypeArguments) super.getCeylonNode(); }
    }

    public static class SequencedTypePsiImpl extends TypePsiImpl
            implements CeylonPsi.SequencedTypePsi {
        public SequencedTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SequencedType getCeylonNode() { return (Tree.SequencedType) super.getCeylonNode(); }
    }

    public static class DefaultedTypePsiImpl extends TypePsiImpl
            implements CeylonPsi.DefaultedTypePsi {
        public DefaultedTypePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DefaultedType getCeylonNode() { return (Tree.DefaultedType) super.getCeylonNode(); }
    }

    public static abstract class DirectivePsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.DirectivePsi {
        public DirectivePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Directive getCeylonNode() { return (Tree.Directive) super.getCeylonNode(); }
    }

    public static class ReturnPsiImpl extends DirectivePsiImpl
            implements CeylonPsi.ReturnPsi {
        public ReturnPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Return getCeylonNode() { return (Tree.Return) super.getCeylonNode(); }
    }

    public static class ThrowPsiImpl extends DirectivePsiImpl
            implements CeylonPsi.ThrowPsi {
        public ThrowPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Throw getCeylonNode() { return (Tree.Throw) super.getCeylonNode(); }
    }

    public static class ContinuePsiImpl extends DirectivePsiImpl
            implements CeylonPsi.ContinuePsi {
        public ContinuePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Continue getCeylonNode() { return (Tree.Continue) super.getCeylonNode(); }
    }

    public static class BreakPsiImpl extends DirectivePsiImpl
            implements CeylonPsi.BreakPsi {
        public BreakPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Break getCeylonNode() { return (Tree.Break) super.getCeylonNode(); }
    }

    public static abstract class StatementOrArgumentPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.StatementOrArgumentPsi {
        public StatementOrArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.StatementOrArgument getCeylonNode() { return (Tree.StatementOrArgument) super.getCeylonNode(); }
    }

    public static abstract class StatementPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.StatementPsi {
        public StatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Statement getCeylonNode() { return (Tree.Statement) super.getCeylonNode(); }
    }

    public static class CompilerAnnotationPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CompilerAnnotationPsi {
        public CompilerAnnotationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CompilerAnnotation getCeylonNode() { return (Tree.CompilerAnnotation) super.getCeylonNode(); }
    }

    public static abstract class ExecutableStatementPsiImpl extends StatementPsiImpl
            implements CeylonPsi.ExecutableStatementPsi {
        public ExecutableStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ExecutableStatement getCeylonNode() { return (Tree.ExecutableStatement) super.getCeylonNode(); }
    }

    public static class AssertionPsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.AssertionPsi {
        public AssertionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Assertion getCeylonNode() { return (Tree.Assertion) super.getCeylonNode(); }
    }

    public static class SpecifierStatementPsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.SpecifierStatementPsi {
        public SpecifierStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SpecifierStatement getCeylonNode() { return (Tree.SpecifierStatement) super.getCeylonNode(); }
    }

    public static class ExpressionStatementPsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.ExpressionStatementPsi {
        public ExpressionStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ExpressionStatement getCeylonNode() { return (Tree.ExpressionStatement) super.getCeylonNode(); }
    }

    public static class ControlStatementPsiImpl extends ExecutableStatementPsiImpl
            implements CeylonPsi.ControlStatementPsi {
        public ControlStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ControlStatement getCeylonNode() { return (Tree.ControlStatement) super.getCeylonNode(); }
    }

    public static class ControlClausePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ControlClausePsi {
        public ControlClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ControlClause getCeylonNode() { return (Tree.ControlClause) super.getCeylonNode(); }
    }

    public static class DynamicStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.DynamicStatementPsi {
        public DynamicStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DynamicStatement getCeylonNode() { return (Tree.DynamicStatement) super.getCeylonNode(); }
    }

    public static class DynamicClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.DynamicClausePsi {
        public DynamicClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DynamicClause getCeylonNode() { return (Tree.DynamicClause) super.getCeylonNode(); }
    }

    public static class IfStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.IfStatementPsi {
        public IfStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IfStatement getCeylonNode() { return (Tree.IfStatement) super.getCeylonNode(); }
    }

    public static class IfClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.IfClausePsi {
        public IfClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IfClause getCeylonNode() { return (Tree.IfClause) super.getCeylonNode(); }
    }

    public static class ElseClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.ElseClausePsi {
        public ElseClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ElseClause getCeylonNode() { return (Tree.ElseClause) super.getCeylonNode(); }
    }

    public static class SwitchStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.SwitchStatementPsi {
        public SwitchStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SwitchStatement getCeylonNode() { return (Tree.SwitchStatement) super.getCeylonNode(); }
    }

    public static class SwitchClausePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.SwitchClausePsi {
        public SwitchClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SwitchClause getCeylonNode() { return (Tree.SwitchClause) super.getCeylonNode(); }
    }

    public static class SwitchCaseListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.SwitchCaseListPsi {
        public SwitchCaseListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SwitchCaseList getCeylonNode() { return (Tree.SwitchCaseList) super.getCeylonNode(); }
    }

    public static class CaseClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.CaseClausePsi {
        public CaseClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CaseClause getCeylonNode() { return (Tree.CaseClause) super.getCeylonNode(); }
    }

    public static class CaseItemPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CaseItemPsi {
        public CaseItemPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CaseItem getCeylonNode() { return (Tree.CaseItem) super.getCeylonNode(); }
    }

    public static class MatchCasePsiImpl extends CaseItemPsiImpl
            implements CeylonPsi.MatchCasePsi {
        public MatchCasePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MatchCase getCeylonNode() { return (Tree.MatchCase) super.getCeylonNode(); }
    }

    public static class IsCasePsiImpl extends CaseItemPsiImpl
            implements CeylonPsi.IsCasePsi {
        public IsCasePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IsCase getCeylonNode() { return (Tree.IsCase) super.getCeylonNode(); }
    }

    public static class SatisfiesCasePsiImpl extends CaseItemPsiImpl
            implements CeylonPsi.SatisfiesCasePsi {
        public SatisfiesCasePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SatisfiesCase getCeylonNode() { return (Tree.SatisfiesCase) super.getCeylonNode(); }
    }

    public static class TryCatchStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.TryCatchStatementPsi {
        public TryCatchStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TryCatchStatement getCeylonNode() { return (Tree.TryCatchStatement) super.getCeylonNode(); }
    }

    public static class TryClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.TryClausePsi {
        public TryClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TryClause getCeylonNode() { return (Tree.TryClause) super.getCeylonNode(); }
    }

    public static class CatchClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.CatchClausePsi {
        public CatchClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CatchClause getCeylonNode() { return (Tree.CatchClause) super.getCeylonNode(); }
    }

    public static class FinallyClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.FinallyClausePsi {
        public FinallyClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.FinallyClause getCeylonNode() { return (Tree.FinallyClause) super.getCeylonNode(); }
    }

    public static class ResourceListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ResourceListPsi {
        public ResourceListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ResourceList getCeylonNode() { return (Tree.ResourceList) super.getCeylonNode(); }
    }

    public static class ResourcePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ResourcePsi {
        public ResourcePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Resource getCeylonNode() { return (Tree.Resource) super.getCeylonNode(); }
    }

    public static class CatchVariablePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.CatchVariablePsi {
        public CatchVariablePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CatchVariable getCeylonNode() { return (Tree.CatchVariable) super.getCeylonNode(); }
    }

    public static class ForStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.ForStatementPsi {
        public ForStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ForStatement getCeylonNode() { return (Tree.ForStatement) super.getCeylonNode(); }
    }

    public static class ForClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.ForClausePsi {
        public ForClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ForClause getCeylonNode() { return (Tree.ForClause) super.getCeylonNode(); }
    }

    public static class ForIteratorPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.ForIteratorPsi {
        public ForIteratorPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ForIterator getCeylonNode() { return (Tree.ForIterator) super.getCeylonNode(); }
    }

    public static class ValueIteratorPsiImpl extends ForIteratorPsiImpl
            implements CeylonPsi.ValueIteratorPsi {
        public ValueIteratorPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ValueIterator getCeylonNode() { return (Tree.ValueIterator) super.getCeylonNode(); }
    }

    public static class KeyValueIteratorPsiImpl extends ForIteratorPsiImpl
            implements CeylonPsi.KeyValueIteratorPsi {
        public KeyValueIteratorPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.KeyValueIterator getCeylonNode() { return (Tree.KeyValueIterator) super.getCeylonNode(); }
    }

    public static class WhileStatementPsiImpl extends ControlStatementPsiImpl
            implements CeylonPsi.WhileStatementPsi {
        public WhileStatementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.WhileStatement getCeylonNode() { return (Tree.WhileStatement) super.getCeylonNode(); }
    }

    public static class WhileClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.WhileClausePsi {
        public WhileClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.WhileClause getCeylonNode() { return (Tree.WhileClause) super.getCeylonNode(); }
    }

    public static class ConditionListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ConditionListPsi {
        public ConditionListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ConditionList getCeylonNode() { return (Tree.ConditionList) super.getCeylonNode(); }
    }

    public static abstract class ConditionPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ConditionPsi {
        public ConditionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Condition getCeylonNode() { return (Tree.Condition) super.getCeylonNode(); }
    }

    public static class BooleanConditionPsiImpl extends ConditionPsiImpl
            implements CeylonPsi.BooleanConditionPsi {
        public BooleanConditionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.BooleanCondition getCeylonNode() { return (Tree.BooleanCondition) super.getCeylonNode(); }
    }

    public static abstract class ExistsOrNonemptyConditionPsiImpl extends ConditionPsiImpl
            implements CeylonPsi.ExistsOrNonemptyConditionPsi {
        public ExistsOrNonemptyConditionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ExistsOrNonemptyCondition getCeylonNode() { return (Tree.ExistsOrNonemptyCondition) super.getCeylonNode(); }
    }

    public static class ExistsConditionPsiImpl extends ExistsOrNonemptyConditionPsiImpl
            implements CeylonPsi.ExistsConditionPsi {
        public ExistsConditionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ExistsCondition getCeylonNode() { return (Tree.ExistsCondition) super.getCeylonNode(); }
    }

    public static class NonemptyConditionPsiImpl extends ExistsOrNonemptyConditionPsiImpl
            implements CeylonPsi.NonemptyConditionPsi {
        public NonemptyConditionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.NonemptyCondition getCeylonNode() { return (Tree.NonemptyCondition) super.getCeylonNode(); }
    }

    public static class IsConditionPsiImpl extends ConditionPsiImpl
            implements CeylonPsi.IsConditionPsi {
        public IsConditionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IsCondition getCeylonNode() { return (Tree.IsCondition) super.getCeylonNode(); }
    }

    public static class SatisfiesConditionPsiImpl extends ConditionPsiImpl
            implements CeylonPsi.SatisfiesConditionPsi {
        public SatisfiesConditionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SatisfiesCondition getCeylonNode() { return (Tree.SatisfiesCondition) super.getCeylonNode(); }
    }

    public static class VariablePsiImpl extends TypedDeclarationPsiImpl
            implements CeylonPsi.VariablePsi {
        public VariablePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Variable getCeylonNode() { return (Tree.Variable) super.getCeylonNode(); }
    }

    public static abstract class TermPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.TermPsi {
        public TermPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Term getCeylonNode() { return (Tree.Term) super.getCeylonNode(); }
    }

    public static abstract class OperatorExpressionPsiImpl extends TermPsiImpl
            implements CeylonPsi.OperatorExpressionPsi {
        public OperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.OperatorExpression getCeylonNode() { return (Tree.OperatorExpression) super.getCeylonNode(); }
    }

    public static abstract class BinaryOperatorExpressionPsiImpl extends OperatorExpressionPsiImpl
            implements CeylonPsi.BinaryOperatorExpressionPsi {
        public BinaryOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.BinaryOperatorExpression getCeylonNode() { return (Tree.BinaryOperatorExpression) super.getCeylonNode(); }
    }

    public static abstract class ArithmeticOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.ArithmeticOpPsi {
        public ArithmeticOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ArithmeticOp getCeylonNode() { return (Tree.ArithmeticOp) super.getCeylonNode(); }
    }

    public static class SumOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.SumOpPsi {
        public SumOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SumOp getCeylonNode() { return (Tree.SumOp) super.getCeylonNode(); }
    }

    public static class DifferenceOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.DifferenceOpPsi {
        public DifferenceOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DifferenceOp getCeylonNode() { return (Tree.DifferenceOp) super.getCeylonNode(); }
    }

    public static class ProductOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.ProductOpPsi {
        public ProductOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ProductOp getCeylonNode() { return (Tree.ProductOp) super.getCeylonNode(); }
    }

    public static class QuotientOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.QuotientOpPsi {
        public QuotientOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.QuotientOp getCeylonNode() { return (Tree.QuotientOp) super.getCeylonNode(); }
    }

    public static class PowerOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.PowerOpPsi {
        public PowerOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PowerOp getCeylonNode() { return (Tree.PowerOp) super.getCeylonNode(); }
    }

    public static class RemainderOpPsiImpl extends ArithmeticOpPsiImpl
            implements CeylonPsi.RemainderOpPsi {
        public RemainderOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.RemainderOp getCeylonNode() { return (Tree.RemainderOp) super.getCeylonNode(); }
    }

    public static abstract class AssignmentOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.AssignmentOpPsi {
        public AssignmentOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AssignmentOp getCeylonNode() { return (Tree.AssignmentOp) super.getCeylonNode(); }
    }

    public static class AssignOpPsiImpl extends AssignmentOpPsiImpl
            implements CeylonPsi.AssignOpPsi {
        public AssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AssignOp getCeylonNode() { return (Tree.AssignOp) super.getCeylonNode(); }
    }

    public static abstract class ArithmeticAssignmentOpPsiImpl extends AssignmentOpPsiImpl
            implements CeylonPsi.ArithmeticAssignmentOpPsi {
        public ArithmeticAssignmentOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ArithmeticAssignmentOp getCeylonNode() { return (Tree.ArithmeticAssignmentOp) super.getCeylonNode(); }
    }

    public static class AddAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.AddAssignOpPsi {
        public AddAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AddAssignOp getCeylonNode() { return (Tree.AddAssignOp) super.getCeylonNode(); }
    }

    public static class SubtractAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.SubtractAssignOpPsi {
        public SubtractAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SubtractAssignOp getCeylonNode() { return (Tree.SubtractAssignOp) super.getCeylonNode(); }
    }

    public static class MultiplyAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.MultiplyAssignOpPsi {
        public MultiplyAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MultiplyAssignOp getCeylonNode() { return (Tree.MultiplyAssignOp) super.getCeylonNode(); }
    }

    public static class DivideAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.DivideAssignOpPsi {
        public DivideAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DivideAssignOp getCeylonNode() { return (Tree.DivideAssignOp) super.getCeylonNode(); }
    }

    public static class RemainderAssignOpPsiImpl extends ArithmeticAssignmentOpPsiImpl
            implements CeylonPsi.RemainderAssignOpPsi {
        public RemainderAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.RemainderAssignOp getCeylonNode() { return (Tree.RemainderAssignOp) super.getCeylonNode(); }
    }

    public static abstract class BitwiseAssignmentOpPsiImpl extends AssignmentOpPsiImpl
            implements CeylonPsi.BitwiseAssignmentOpPsi {
        public BitwiseAssignmentOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.BitwiseAssignmentOp getCeylonNode() { return (Tree.BitwiseAssignmentOp) super.getCeylonNode(); }
    }

    public static class IntersectAssignOpPsiImpl extends BitwiseAssignmentOpPsiImpl
            implements CeylonPsi.IntersectAssignOpPsi {
        public IntersectAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IntersectAssignOp getCeylonNode() { return (Tree.IntersectAssignOp) super.getCeylonNode(); }
    }

    public static class UnionAssignOpPsiImpl extends BitwiseAssignmentOpPsiImpl
            implements CeylonPsi.UnionAssignOpPsi {
        public UnionAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.UnionAssignOp getCeylonNode() { return (Tree.UnionAssignOp) super.getCeylonNode(); }
    }

    public static class ComplementAssignOpPsiImpl extends BitwiseAssignmentOpPsiImpl
            implements CeylonPsi.ComplementAssignOpPsi {
        public ComplementAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ComplementAssignOp getCeylonNode() { return (Tree.ComplementAssignOp) super.getCeylonNode(); }
    }

    public static abstract class LogicalAssignmentOpPsiImpl extends AssignmentOpPsiImpl
            implements CeylonPsi.LogicalAssignmentOpPsi {
        public LogicalAssignmentOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.LogicalAssignmentOp getCeylonNode() { return (Tree.LogicalAssignmentOp) super.getCeylonNode(); }
    }

    public static class AndAssignOpPsiImpl extends LogicalAssignmentOpPsiImpl
            implements CeylonPsi.AndAssignOpPsi {
        public AndAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AndAssignOp getCeylonNode() { return (Tree.AndAssignOp) super.getCeylonNode(); }
    }

    public static class OrAssignOpPsiImpl extends LogicalAssignmentOpPsiImpl
            implements CeylonPsi.OrAssignOpPsi {
        public OrAssignOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.OrAssignOp getCeylonNode() { return (Tree.OrAssignOp) super.getCeylonNode(); }
    }

    public static abstract class LogicalOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.LogicalOpPsi {
        public LogicalOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.LogicalOp getCeylonNode() { return (Tree.LogicalOp) super.getCeylonNode(); }
    }

    public static class AndOpPsiImpl extends LogicalOpPsiImpl
            implements CeylonPsi.AndOpPsi {
        public AndOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AndOp getCeylonNode() { return (Tree.AndOp) super.getCeylonNode(); }
    }

    public static class OrOpPsiImpl extends LogicalOpPsiImpl
            implements CeylonPsi.OrOpPsi {
        public OrOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.OrOp getCeylonNode() { return (Tree.OrOp) super.getCeylonNode(); }
    }

    public static abstract class BitwiseOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.BitwiseOpPsi {
        public BitwiseOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.BitwiseOp getCeylonNode() { return (Tree.BitwiseOp) super.getCeylonNode(); }
    }

    public static class IntersectionOpPsiImpl extends BitwiseOpPsiImpl
            implements CeylonPsi.IntersectionOpPsi {
        public IntersectionOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IntersectionOp getCeylonNode() { return (Tree.IntersectionOp) super.getCeylonNode(); }
    }

    public static class UnionOpPsiImpl extends BitwiseOpPsiImpl
            implements CeylonPsi.UnionOpPsi {
        public UnionOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.UnionOp getCeylonNode() { return (Tree.UnionOp) super.getCeylonNode(); }
    }

    public static class ComplementOpPsiImpl extends BitwiseOpPsiImpl
            implements CeylonPsi.ComplementOpPsi {
        public ComplementOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ComplementOp getCeylonNode() { return (Tree.ComplementOp) super.getCeylonNode(); }
    }

    public static abstract class EqualityOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.EqualityOpPsi {
        public EqualityOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.EqualityOp getCeylonNode() { return (Tree.EqualityOp) super.getCeylonNode(); }
    }

    public static class EqualOpPsiImpl extends EqualityOpPsiImpl
            implements CeylonPsi.EqualOpPsi {
        public EqualOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.EqualOp getCeylonNode() { return (Tree.EqualOp) super.getCeylonNode(); }
    }

    public static class NotEqualOpPsiImpl extends EqualityOpPsiImpl
            implements CeylonPsi.NotEqualOpPsi {
        public NotEqualOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.NotEqualOp getCeylonNode() { return (Tree.NotEqualOp) super.getCeylonNode(); }
    }

    public static abstract class ComparisonOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.ComparisonOpPsi {
        public ComparisonOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ComparisonOp getCeylonNode() { return (Tree.ComparisonOp) super.getCeylonNode(); }
    }

    public static class LargerOpPsiImpl extends ComparisonOpPsiImpl
            implements CeylonPsi.LargerOpPsi {
        public LargerOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.LargerOp getCeylonNode() { return (Tree.LargerOp) super.getCeylonNode(); }
    }

    public static class SmallerOpPsiImpl extends ComparisonOpPsiImpl
            implements CeylonPsi.SmallerOpPsi {
        public SmallerOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SmallerOp getCeylonNode() { return (Tree.SmallerOp) super.getCeylonNode(); }
    }

    public static class LargeAsOpPsiImpl extends ComparisonOpPsiImpl
            implements CeylonPsi.LargeAsOpPsi {
        public LargeAsOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.LargeAsOp getCeylonNode() { return (Tree.LargeAsOp) super.getCeylonNode(); }
    }

    public static class SmallAsOpPsiImpl extends ComparisonOpPsiImpl
            implements CeylonPsi.SmallAsOpPsi {
        public SmallAsOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SmallAsOp getCeylonNode() { return (Tree.SmallAsOp) super.getCeylonNode(); }
    }

    public static class ScaleOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.ScaleOpPsi {
        public ScaleOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ScaleOp getCeylonNode() { return (Tree.ScaleOp) super.getCeylonNode(); }
    }

    public static abstract class BoundPsiImpl extends TermPsiImpl
            implements CeylonPsi.BoundPsi {
        public BoundPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Bound getCeylonNode() { return (Tree.Bound) super.getCeylonNode(); }
    }

    public static class OpenBoundPsiImpl extends BoundPsiImpl
            implements CeylonPsi.OpenBoundPsi {
        public OpenBoundPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.OpenBound getCeylonNode() { return (Tree.OpenBound) super.getCeylonNode(); }
    }

    public static class ClosedBoundPsiImpl extends BoundPsiImpl
            implements CeylonPsi.ClosedBoundPsi {
        public ClosedBoundPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ClosedBound getCeylonNode() { return (Tree.ClosedBound) super.getCeylonNode(); }
    }

    public static class WithinOpPsiImpl extends OperatorExpressionPsiImpl
            implements CeylonPsi.WithinOpPsi {
        public WithinOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.WithinOp getCeylonNode() { return (Tree.WithinOp) super.getCeylonNode(); }
    }

    public static class DefaultOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.DefaultOpPsi {
        public DefaultOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DefaultOp getCeylonNode() { return (Tree.DefaultOp) super.getCeylonNode(); }
    }

    public static class ThenOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.ThenOpPsi {
        public ThenOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ThenOp getCeylonNode() { return (Tree.ThenOp) super.getCeylonNode(); }
    }

    public static class IdenticalOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.IdenticalOpPsi {
        public IdenticalOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IdenticalOp getCeylonNode() { return (Tree.IdenticalOp) super.getCeylonNode(); }
    }

    public static class EntryOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.EntryOpPsi {
        public EntryOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.EntryOp getCeylonNode() { return (Tree.EntryOp) super.getCeylonNode(); }
    }

    public static class RangeOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.RangeOpPsi {
        public RangeOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.RangeOp getCeylonNode() { return (Tree.RangeOp) super.getCeylonNode(); }
    }

    public static class SegmentOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.SegmentOpPsi {
        public SegmentOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SegmentOp getCeylonNode() { return (Tree.SegmentOp) super.getCeylonNode(); }
    }

    public static class CompareOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.CompareOpPsi {
        public CompareOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CompareOp getCeylonNode() { return (Tree.CompareOp) super.getCeylonNode(); }
    }

    public static class InOpPsiImpl extends BinaryOperatorExpressionPsiImpl
            implements CeylonPsi.InOpPsi {
        public InOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InOp getCeylonNode() { return (Tree.InOp) super.getCeylonNode(); }
    }

    public static abstract class UnaryOperatorExpressionPsiImpl extends OperatorExpressionPsiImpl
            implements CeylonPsi.UnaryOperatorExpressionPsi {
        public UnaryOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.UnaryOperatorExpression getCeylonNode() { return (Tree.UnaryOperatorExpression) super.getCeylonNode(); }
    }

    public static class NotOpPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.NotOpPsi {
        public NotOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.NotOp getCeylonNode() { return (Tree.NotOp) super.getCeylonNode(); }
    }

    public static class ExistsPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.ExistsPsi {
        public ExistsPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Exists getCeylonNode() { return (Tree.Exists) super.getCeylonNode(); }
    }

    public static class NonemptyPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.NonemptyPsi {
        public NonemptyPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Nonempty getCeylonNode() { return (Tree.Nonempty) super.getCeylonNode(); }
    }

    public static class NegativeOpPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.NegativeOpPsi {
        public NegativeOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.NegativeOp getCeylonNode() { return (Tree.NegativeOp) super.getCeylonNode(); }
    }

    public static class PositiveOpPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.PositiveOpPsi {
        public PositiveOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PositiveOp getCeylonNode() { return (Tree.PositiveOp) super.getCeylonNode(); }
    }

    public static abstract class TypeOperatorExpressionPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.TypeOperatorExpressionPsi {
        public TypeOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypeOperatorExpression getCeylonNode() { return (Tree.TypeOperatorExpression) super.getCeylonNode(); }
    }

    public static class IsOpPsiImpl extends TypeOperatorExpressionPsiImpl
            implements CeylonPsi.IsOpPsi {
        public IsOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IsOp getCeylonNode() { return (Tree.IsOp) super.getCeylonNode(); }
    }

    public static class SatisfiesPsiImpl extends TypeOperatorExpressionPsiImpl
            implements CeylonPsi.SatisfiesPsi {
        public SatisfiesPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Satisfies getCeylonNode() { return (Tree.Satisfies) super.getCeylonNode(); }
    }

    public static class ExtendsPsiImpl extends TypeOperatorExpressionPsiImpl
            implements CeylonPsi.ExtendsPsi {
        public ExtendsPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Extends getCeylonNode() { return (Tree.Extends) super.getCeylonNode(); }
    }

    public static class OfOpPsiImpl extends TypeOperatorExpressionPsiImpl
            implements CeylonPsi.OfOpPsi {
        public OfOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.OfOp getCeylonNode() { return (Tree.OfOp) super.getCeylonNode(); }
    }

    public static abstract class PrefixOperatorExpressionPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.PrefixOperatorExpressionPsi {
        public PrefixOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PrefixOperatorExpression getCeylonNode() { return (Tree.PrefixOperatorExpression) super.getCeylonNode(); }
    }

    public static class IncrementOpPsiImpl extends PrefixOperatorExpressionPsiImpl
            implements CeylonPsi.IncrementOpPsi {
        public IncrementOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IncrementOp getCeylonNode() { return (Tree.IncrementOp) super.getCeylonNode(); }
    }

    public static class DecrementOpPsiImpl extends PrefixOperatorExpressionPsiImpl
            implements CeylonPsi.DecrementOpPsi {
        public DecrementOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DecrementOp getCeylonNode() { return (Tree.DecrementOp) super.getCeylonNode(); }
    }

    public static abstract class PostfixOperatorExpressionPsiImpl extends UnaryOperatorExpressionPsiImpl
            implements CeylonPsi.PostfixOperatorExpressionPsi {
        public PostfixOperatorExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PostfixOperatorExpression getCeylonNode() { return (Tree.PostfixOperatorExpression) super.getCeylonNode(); }
    }

    public static class PostfixIncrementOpPsiImpl extends PostfixOperatorExpressionPsiImpl
            implements CeylonPsi.PostfixIncrementOpPsi {
        public PostfixIncrementOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PostfixIncrementOp getCeylonNode() { return (Tree.PostfixIncrementOp) super.getCeylonNode(); }
    }

    public static class PostfixDecrementOpPsiImpl extends PostfixOperatorExpressionPsiImpl
            implements CeylonPsi.PostfixDecrementOpPsi {
        public PostfixDecrementOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PostfixDecrementOp getCeylonNode() { return (Tree.PostfixDecrementOp) super.getCeylonNode(); }
    }

    public static class ExpressionListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ExpressionListPsi {
        public ExpressionListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ExpressionList getCeylonNode() { return (Tree.ExpressionList) super.getCeylonNode(); }
    }

    public static class ExpressionPsiImpl extends AtomPsiImpl
            implements CeylonPsi.ExpressionPsi {
        public ExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Expression getCeylonNode() { return (Tree.Expression) super.getCeylonNode(); }
    }

    public static abstract class PrimaryPsiImpl extends TermPsiImpl
            implements CeylonPsi.PrimaryPsi {
        public PrimaryPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Primary getCeylonNode() { return (Tree.Primary) super.getCeylonNode(); }
    }

    public static abstract class PostfixExpressionPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.PostfixExpressionPsi {
        public PostfixExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PostfixExpression getCeylonNode() { return (Tree.PostfixExpression) super.getCeylonNode(); }
    }

    public static class InvocationExpressionPsiImpl extends PostfixExpressionPsiImpl
            implements CeylonPsi.InvocationExpressionPsi {
        public InvocationExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InvocationExpression getCeylonNode() { return (Tree.InvocationExpression) super.getCeylonNode(); }
    }

    public static class ParameterizedExpressionPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.ParameterizedExpressionPsi {
        public ParameterizedExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ParameterizedExpression getCeylonNode() { return (Tree.ParameterizedExpression) super.getCeylonNode(); }
    }

    public static abstract class MemberOrTypeExpressionPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.MemberOrTypeExpressionPsi {
        public MemberOrTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MemberOrTypeExpression getCeylonNode() { return (Tree.MemberOrTypeExpression) super.getCeylonNode(); }
    }

    public static class ExtendedTypeExpressionPsiImpl extends MemberOrTypeExpressionPsiImpl
            implements CeylonPsi.ExtendedTypeExpressionPsi {
        public ExtendedTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ExtendedTypeExpression getCeylonNode() { return (Tree.ExtendedTypeExpression) super.getCeylonNode(); }
    }

    public static abstract class StaticMemberOrTypeExpressionPsiImpl extends MemberOrTypeExpressionPsiImpl
            implements CeylonPsi.StaticMemberOrTypeExpressionPsi {
        public StaticMemberOrTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.StaticMemberOrTypeExpression getCeylonNode() { return (Tree.StaticMemberOrTypeExpression) super.getCeylonNode(); }
    }

    public static abstract class BaseMemberOrTypeExpressionPsiImpl extends StaticMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.BaseMemberOrTypeExpressionPsi {
        public BaseMemberOrTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.BaseMemberOrTypeExpression getCeylonNode() { return (Tree.BaseMemberOrTypeExpression) super.getCeylonNode(); }
    }

    public static class BaseMemberExpressionPsiImpl extends IdentifiableBaseMemberExpression
            implements CeylonPsi.BaseMemberExpressionPsi {
        public BaseMemberExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.BaseMemberExpression getCeylonNode() { return (Tree.BaseMemberExpression) super.getCeylonNode(); }
    }

    public static class BaseTypeExpressionPsiImpl extends BaseMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.BaseTypeExpressionPsi {
        public BaseTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.BaseTypeExpression getCeylonNode() { return (Tree.BaseTypeExpression) super.getCeylonNode(); }
    }

    public static abstract class QualifiedMemberOrTypeExpressionPsiImpl extends StaticMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.QualifiedMemberOrTypeExpressionPsi {
        public QualifiedMemberOrTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.QualifiedMemberOrTypeExpression getCeylonNode() { return (Tree.QualifiedMemberOrTypeExpression) super.getCeylonNode(); }
    }

    public static class QualifiedMemberExpressionPsiImpl extends QualifiedMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.QualifiedMemberExpressionPsi {
        public QualifiedMemberExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.QualifiedMemberExpression getCeylonNode() { return (Tree.QualifiedMemberExpression) super.getCeylonNode(); }
    }

    public static class QualifiedTypeExpressionPsiImpl extends QualifiedMemberOrTypeExpressionPsiImpl
            implements CeylonPsi.QualifiedTypeExpressionPsi {
        public QualifiedTypeExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.QualifiedTypeExpression getCeylonNode() { return (Tree.QualifiedTypeExpression) super.getCeylonNode(); }
    }

    public static abstract class MemberOperatorPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.MemberOperatorPsi {
        public MemberOperatorPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MemberOperator getCeylonNode() { return (Tree.MemberOperator) super.getCeylonNode(); }
    }

    public static class MemberOpPsiImpl extends MemberOperatorPsiImpl
            implements CeylonPsi.MemberOpPsi {
        public MemberOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MemberOp getCeylonNode() { return (Tree.MemberOp) super.getCeylonNode(); }
    }

    public static class SafeMemberOpPsiImpl extends MemberOperatorPsiImpl
            implements CeylonPsi.SafeMemberOpPsi {
        public SafeMemberOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SafeMemberOp getCeylonNode() { return (Tree.SafeMemberOp) super.getCeylonNode(); }
    }

    public static class SpreadOpPsiImpl extends MemberOperatorPsiImpl
            implements CeylonPsi.SpreadOpPsi {
        public SpreadOpPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SpreadOp getCeylonNode() { return (Tree.SpreadOp) super.getCeylonNode(); }
    }

    public static class IndexExpressionPsiImpl extends PostfixExpressionPsiImpl
            implements CeylonPsi.IndexExpressionPsi {
        public IndexExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IndexExpression getCeylonNode() { return (Tree.IndexExpression) super.getCeylonNode(); }
    }

    public static abstract class ElementOrRangePsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ElementOrRangePsi {
        public ElementOrRangePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ElementOrRange getCeylonNode() { return (Tree.ElementOrRange) super.getCeylonNode(); }
    }

    public static class ElementPsiImpl extends ElementOrRangePsiImpl
            implements CeylonPsi.ElementPsi {
        public ElementPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Element getCeylonNode() { return (Tree.Element) super.getCeylonNode(); }
    }

    public static class ElementRangePsiImpl extends ElementOrRangePsiImpl
            implements CeylonPsi.ElementRangePsi {
        public ElementRangePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ElementRange getCeylonNode() { return (Tree.ElementRange) super.getCeylonNode(); }
    }

    public static class OuterPsiImpl extends AtomPsiImpl
            implements CeylonPsi.OuterPsi {
        public OuterPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Outer getCeylonNode() { return (Tree.Outer) super.getCeylonNode(); }
    }

    public static class PackagePsiImpl extends AtomPsiImpl
            implements CeylonPsi.PackagePsi {
        public PackagePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Package getCeylonNode() { return (Tree.Package) super.getCeylonNode(); }
    }

    public static abstract class ArgumentListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.ArgumentListPsi {
        public ArgumentListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ArgumentList getCeylonNode() { return (Tree.ArgumentList) super.getCeylonNode(); }
    }

    public static class NamedArgumentListPsiImpl extends ArgumentListPsiImpl
            implements CeylonPsi.NamedArgumentListPsi {
        public NamedArgumentListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.NamedArgumentList getCeylonNode() { return (Tree.NamedArgumentList) super.getCeylonNode(); }
    }

    public static class SequencedArgumentPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.SequencedArgumentPsi {
        public SequencedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SequencedArgument getCeylonNode() { return (Tree.SequencedArgument) super.getCeylonNode(); }
    }

    public static class PositionalArgumentListPsiImpl extends ArgumentListPsiImpl
            implements CeylonPsi.PositionalArgumentListPsi {
        public PositionalArgumentListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PositionalArgumentList getCeylonNode() { return (Tree.PositionalArgumentList) super.getCeylonNode(); }
    }

    public static abstract class PositionalArgumentPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.PositionalArgumentPsi {
        public PositionalArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.PositionalArgument getCeylonNode() { return (Tree.PositionalArgument) super.getCeylonNode(); }
    }

    public static class ListedArgumentPsiImpl extends PositionalArgumentPsiImpl
            implements CeylonPsi.ListedArgumentPsi {
        public ListedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ListedArgument getCeylonNode() { return (Tree.ListedArgument) super.getCeylonNode(); }
    }

    public static class SpreadArgumentPsiImpl extends PositionalArgumentPsiImpl
            implements CeylonPsi.SpreadArgumentPsi {
        public SpreadArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SpreadArgument getCeylonNode() { return (Tree.SpreadArgument) super.getCeylonNode(); }
    }

    public static class FunctionArgumentPsiImpl extends TermPsiImpl
            implements CeylonPsi.FunctionArgumentPsi {
        public FunctionArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.FunctionArgument getCeylonNode() { return (Tree.FunctionArgument) super.getCeylonNode(); }
    }

    public static abstract class NamedArgumentPsiImpl extends StatementOrArgumentPsiImpl
            implements CeylonPsi.NamedArgumentPsi {
        public NamedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.NamedArgument getCeylonNode() { return (Tree.NamedArgument) super.getCeylonNode(); }
    }

    public static class SpecifiedArgumentPsiImpl extends NamedArgumentPsiImpl
            implements CeylonPsi.SpecifiedArgumentPsi {
        public SpecifiedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SpecifiedArgument getCeylonNode() { return (Tree.SpecifiedArgument) super.getCeylonNode(); }
    }

    public static abstract class TypedArgumentPsiImpl extends NamedArgumentPsiImpl
            implements CeylonPsi.TypedArgumentPsi {
        public TypedArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.TypedArgument getCeylonNode() { return (Tree.TypedArgument) super.getCeylonNode(); }
    }

    public static class MethodArgumentPsiImpl extends TypedArgumentPsiImpl
            implements CeylonPsi.MethodArgumentPsi {
        public MethodArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.MethodArgument getCeylonNode() { return (Tree.MethodArgument) super.getCeylonNode(); }
    }

    public static class AttributeArgumentPsiImpl extends TypedArgumentPsiImpl
            implements CeylonPsi.AttributeArgumentPsi {
        public AttributeArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AttributeArgument getCeylonNode() { return (Tree.AttributeArgument) super.getCeylonNode(); }
    }

    public static class ObjectArgumentPsiImpl extends TypedArgumentPsiImpl
            implements CeylonPsi.ObjectArgumentPsi {
        public ObjectArgumentPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ObjectArgument getCeylonNode() { return (Tree.ObjectArgument) super.getCeylonNode(); }
    }

    public static abstract class SpecifierOrInitializerExpressionPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.SpecifierOrInitializerExpressionPsi {
        public SpecifierOrInitializerExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SpecifierOrInitializerExpression getCeylonNode() { return (Tree.SpecifierOrInitializerExpression) super.getCeylonNode(); }
    }

    public static class SpecifierExpressionPsiImpl extends SpecifierOrInitializerExpressionPsiImpl
            implements CeylonPsi.SpecifierExpressionPsi {
        public SpecifierExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SpecifierExpression getCeylonNode() { return (Tree.SpecifierExpression) super.getCeylonNode(); }
    }

    public static class LazySpecifierExpressionPsiImpl extends SpecifierExpressionPsiImpl
            implements CeylonPsi.LazySpecifierExpressionPsi {
        public LazySpecifierExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.LazySpecifierExpression getCeylonNode() { return (Tree.LazySpecifierExpression) super.getCeylonNode(); }
    }

    public static class InitializerExpressionPsiImpl extends SpecifierOrInitializerExpressionPsiImpl
            implements CeylonPsi.InitializerExpressionPsi {
        public InitializerExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InitializerExpression getCeylonNode() { return (Tree.InitializerExpression) super.getCeylonNode(); }
    }

    public static abstract class AtomPsiImpl extends PrimaryPsiImpl
            implements CeylonPsi.AtomPsi {
        public AtomPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Atom getCeylonNode() { return (Tree.Atom) super.getCeylonNode(); }
    }

    public static abstract class LiteralPsiImpl extends AtomPsiImpl
            implements CeylonPsi.LiteralPsi {
        public LiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Literal getCeylonNode() { return (Tree.Literal) super.getCeylonNode(); }
    }

    public static class NaturalLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.NaturalLiteralPsi {
        public NaturalLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.NaturalLiteral getCeylonNode() { return (Tree.NaturalLiteral) super.getCeylonNode(); }
    }

    public static class FloatLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.FloatLiteralPsi {
        public FloatLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.FloatLiteral getCeylonNode() { return (Tree.FloatLiteral) super.getCeylonNode(); }
    }

    public static class CharLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.CharLiteralPsi {
        public CharLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.CharLiteral getCeylonNode() { return (Tree.CharLiteral) super.getCeylonNode(); }
    }

    public static class StringLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.StringLiteralPsi {
        public StringLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.StringLiteral getCeylonNode() { return (Tree.StringLiteral) super.getCeylonNode(); }
    }

    public static class QuotedLiteralPsiImpl extends LiteralPsiImpl
            implements CeylonPsi.QuotedLiteralPsi {
        public QuotedLiteralPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.QuotedLiteral getCeylonNode() { return (Tree.QuotedLiteral) super.getCeylonNode(); }
    }

    public static class DocLinkPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.DocLinkPsi {
        public DocLinkPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.DocLink getCeylonNode() { return (Tree.DocLink) super.getCeylonNode(); }
    }

    public static abstract class SelfExpressionPsiImpl extends AtomPsiImpl
            implements CeylonPsi.SelfExpressionPsi {
        public SelfExpressionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SelfExpression getCeylonNode() { return (Tree.SelfExpression) super.getCeylonNode(); }
    }

    public static class ThisPsiImpl extends SelfExpressionPsiImpl
            implements CeylonPsi.ThisPsi {
        public ThisPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.This getCeylonNode() { return (Tree.This) super.getCeylonNode(); }
    }

    public static class SuperPsiImpl extends SelfExpressionPsiImpl
            implements CeylonPsi.SuperPsi {
        public SuperPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Super getCeylonNode() { return (Tree.Super) super.getCeylonNode(); }
    }

    public static class SequenceEnumerationPsiImpl extends AtomPsiImpl
            implements CeylonPsi.SequenceEnumerationPsi {
        public SequenceEnumerationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.SequenceEnumeration getCeylonNode() { return (Tree.SequenceEnumeration) super.getCeylonNode(); }
    }

    public static class TuplePsiImpl extends AtomPsiImpl
            implements CeylonPsi.TuplePsi {
        public TuplePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Tuple getCeylonNode() { return (Tree.Tuple) super.getCeylonNode(); }
    }

    public static class DynamicPsiImpl extends AtomPsiImpl
            implements CeylonPsi.DynamicPsi {
        public DynamicPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Dynamic getCeylonNode() { return (Tree.Dynamic) super.getCeylonNode(); }
    }

    public static class StringTemplatePsiImpl extends AtomPsiImpl
            implements CeylonPsi.StringTemplatePsi {
        public StringTemplatePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.StringTemplate getCeylonNode() { return (Tree.StringTemplate) super.getCeylonNode(); }
    }

    public static class AnnotationPsiImpl extends InvocationExpressionPsiImpl
            implements CeylonPsi.AnnotationPsi {
        public AnnotationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Annotation getCeylonNode() { return (Tree.Annotation) super.getCeylonNode(); }
    }

    public static class AnonymousAnnotationPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AnonymousAnnotationPsi {
        public AnonymousAnnotationPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AnonymousAnnotation getCeylonNode() { return (Tree.AnonymousAnnotation) super.getCeylonNode(); }
    }

    public static class AnnotationListPsiImpl extends CeylonCompositeElementImpl
            implements CeylonPsi.AnnotationListPsi {
        public AnnotationListPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.AnnotationList getCeylonNode() { return (Tree.AnnotationList) super.getCeylonNode(); }
    }

    public static class IdentifierPsiImpl extends CeylonResolvable
            implements CeylonPsi.IdentifierPsi {
        public IdentifierPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Identifier getCeylonNode() { return (Tree.Identifier) super.getCeylonNode(); }
    }

    public static class ComprehensionPsiImpl extends PositionalArgumentPsiImpl
            implements CeylonPsi.ComprehensionPsi {
        public ComprehensionPsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.Comprehension getCeylonNode() { return (Tree.Comprehension) super.getCeylonNode(); }
    }

    public static abstract class ComprehensionClausePsiImpl extends ControlClausePsiImpl
            implements CeylonPsi.ComprehensionClausePsi {
        public ComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ComprehensionClause getCeylonNode() { return (Tree.ComprehensionClause) super.getCeylonNode(); }
    }

    public static abstract class InitialComprehensionClausePsiImpl extends ComprehensionClausePsiImpl
            implements CeylonPsi.InitialComprehensionClausePsi {
        public InitialComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.InitialComprehensionClause getCeylonNode() { return (Tree.InitialComprehensionClause) super.getCeylonNode(); }
    }

    public static class ExpressionComprehensionClausePsiImpl extends ComprehensionClausePsiImpl
            implements CeylonPsi.ExpressionComprehensionClausePsi {
        public ExpressionComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ExpressionComprehensionClause getCeylonNode() { return (Tree.ExpressionComprehensionClause) super.getCeylonNode(); }
    }

    public static class ForComprehensionClausePsiImpl extends InitialComprehensionClausePsiImpl
            implements CeylonPsi.ForComprehensionClausePsi {
        public ForComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.ForComprehensionClause getCeylonNode() { return (Tree.ForComprehensionClause) super.getCeylonNode(); }
    }

    public static class IfComprehensionClausePsiImpl extends InitialComprehensionClausePsiImpl
            implements CeylonPsi.IfComprehensionClausePsi {
        public IfComprehensionClausePsiImpl(ASTNode astNode) { super(astNode); }
        @Override public Tree.IfComprehensionClause getCeylonNode() { return (Tree.IfComprehensionClause) super.getCeylonNode(); }
    }

}
