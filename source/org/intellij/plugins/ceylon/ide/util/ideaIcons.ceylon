import ceylon.collection {
    ArrayList
}

import com.intellij.icons {
    AllIcons
}
import com.intellij.openapi.util {
    IconLoader
}
import com.intellij.psi {
    PsiModifier,
    CommonClassNames,
    PsiModifierListOwner
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl,
    ClsMethodImpl,
    ClsFieldImpl
}
import com.intellij.psi.util {
    InheritanceUtil
}
import com.intellij.ui {
    RowIcon,
    LayeredIcon
}
import com.intellij.util {
    PlatformIcons
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.model {
    AnyModifiableSourceFile
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Class,
    Interface,
    Function,
    Value,
    ModelUtil,
    TypeParameter,
    TypeAlias,
    NothingType,
    Setter,
    Constructor,
    Referenceable,
    Package
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.model {
    Annotations
}

shared object icons {
    
    shared Icon imports => IconLoader.getIcon("/icons/ceylonImports.png");
    shared Icon singleImport => IconLoader.getIcon("/icons/ceylonImport.png");
//    shared Icon packages => IconLoader.getIcon("/icons/ceylonPackage.png");
    shared Icon packageFolders => IconLoader.getIcon("/icons/sourceFolder.png");
    shared Icon packageArchives => PlatformIcons.packageIcon;
    shared Icon moduleFolders => IconLoader.getIcon("/icons/moduleFolder.png");
    shared Icon moduleArchives => IconLoader.getIcon("/icons/moduleArchive.png");
    shared Icon moduleDescriptors => IconLoader.getIcon("/icons/descriptor.png");
    shared Icon packageDescriptors => IconLoader.getIcon("/icons/packageDescriptor.png");
    shared Icon classes => PlatformIcons.classIcon;
    shared Icon abstractClasses => PlatformIcons.abstractClassIcon;
    shared Icon interfaces => PlatformIcons.interfaceIcon;
    shared Icon objects => PlatformIcons.anonymousClassIcon;
    shared Icon methods => PlatformIcons.methodIcon;
    shared Icon formalMethods => PlatformIcons.abstractMethodIcon;
    shared Icon attributes = PlatformIcons.fieldIcon;
    shared Icon enumerations = PlatformIcons.enumIcon;
    shared Icon exceptions = AllIcons.Nodes.exceptionClass;
    shared Icon abstractExceptions = AllIcons.Nodes.abstractException;
    shared Icon annotationClasses = PlatformIcons.annotationTypeIcon;
    shared Icon param => PlatformIcons.parameterIcon;
    shared Icon local => IconLoader.getIcon("/icons/ceylonLocal.png");
    shared Icon values => PlatformIcons.variableIcon;
    shared Icon formalValues => IconLoader.getIcon("/icons/formalValue.png");
    shared Icon anonymousFunction => AllIcons.Nodes.\ifunction;
    shared Icon constructors => PlatformIcons.classInitializer;
    shared Icon setters => PlatformIcons.classInitializer;
    shared Icon annotations => AllIcons.Gutter.extAnnotation;

    shared Icon refinement => AllIcons.Gutter.implementingMethod;
    shared Icon extendedType => AllIcons.Gutter.overridingMethod;
    shared Icon satisfiedTypes => AllIcons.General.implementingMethod;
    shared Icon types => IconLoader.getIcon("/icons/ceylonTypes.png");
    
    shared Icon surround => IconLoader.getIcon("/icons/ceylonSurround.png");
    shared Icon correction => AllIcons.Actions.redo;
    shared Icon addCorrection => AllIcons.General.add;
    shared Icon see => AllIcons.Actions.share;
    shared Icon returns => AllIcons.Actions.stepOut;

    shared Icon project => IconLoader.getIcon("/icons/module.png");
    shared Icon ceylon => IconLoader.getIcon("/icons/ceylon.png");
    shared Icon file => IconLoader.getIcon("/icons/ceylonFile.png");
    shared Icon newFile => IconLoader.getIcon("/icons/ceylonNewFile.png");

    shared Icon problemsViewOk => IconLoader.getIcon("/icons/ceylonProblemsOk.png");
    shared Icon problemsViewErrors => IconLoader.getIcon("/icons/ceylonProblemsErrors.png");
    shared Icon problemsViewWarnings => IconLoader.getIcon("/icons/ceylonProblemsWarnings.png");

    shared alias DeclarationNode
            => Tree.Declaration
             | Tree.TypedArgument
             | Tree.SpecifierStatement
             | Tree.ObjectExpression;

    shared Icon? getBaseIcon(DeclarationNode|Referenceable obj) {
        if (is Tree.SpecifierStatement obj, !obj.refinement) {
            return null;
        }
        value decl 
            = switch (obj) 
            case (is Tree.Declaration) (obj.declarationModel else obj)
            case (is Tree.TypedArgument) (obj.declarationModel else obj)
            case (is Tree.ObjectExpression) (obj.anonymousClass else obj)
            case (is Tree.SpecifierStatement) (obj.declaration else obj)
            else obj;
        return switch (decl)
            //models:
            case (is Interface)
                interfaces
            case (is Class)
                if (decl.objectClass) then objects
                else if (decl.inherits(decl.unit.throwableDeclaration))
                    then (decl.abstract then abstractExceptions else exceptions)
                else if (decl.annotation) then annotationClasses
                else if (decl.abstract) then abstractClasses
                else classes
            case (is Function)
                if (ModelUtil.isConstructor(decl)) then constructors
                else if (decl.annotation) then annotations
                else if (decl.parameter) then param
                else if (decl.formal) then formalMethods
                else if (decl.annotation) then annotationClasses
                else methods
            case (is Value)
                if (ModelUtil.isConstructor(decl)) then constructors
                else if (ModelUtil.isObject(decl)) then objects
                else if (decl.parameter) then param
                else if (decl.formal) then formalValues
                else values
            case (is Setter)
                setters
            case (is Constructor) constructors
            case (is TypeAlias|NothingType)
                types
            case (is TypeParameter)
                param // TODO wrong!
            //AST nodes:
            case (is Tree.AnyClass)
                classes
            case (is Tree.AnyInterface)
                interfaces
            case (is Tree.AnyMethod|Tree.MethodArgument)
                methods
            case (is Tree.AnyAttribute|Tree.Variable|Tree.AttributeArgument)
                values
            case (is Tree.ObjectDefinition|Tree.ObjectArgument|Tree.ObjectExpression)
                objects
            case (is Tree.Constructor|Tree.Enumerated)
                constructors
            case (is Tree.TypeAliasDeclaration)
                types
            case (is Tree.SpecifierStatement)
                (decl.baseMemberExpression is Tree.StaticMemberOrTypeExpression
                    then values else methods)
            else
                null;
    }
    
    shared Icon? forDeclaration(DeclarationNode|Referenceable obj) {
        value baseIcon = getBaseIcon(obj);
        if (!exists baseIcon) {
            print("Missing icon for ``obj``");
            return null;
        }

        Referenceable? model =
            switch (obj) 
            case (is Tree.Declaration) obj.declarationModel
            case (is Tree.TypedArgument) obj.declarationModel
            case (is Tree.SpecifierStatement) obj.declaration
            case (is Tree.ObjectExpression) obj.anonymousClass
            else obj;

        if (exists model) {
            switch (model)
            case (is Declaration) {
                value decorations = ArrayList<Icon>();
                value layer
                        = if (model.shared)
                        then PlatformIcons.publicIcon
                        else PlatformIcons.privateIcon;
                value final
                        = switch (model)
                        case (is Class) model.final
                        //case (is FunctionOrValue) model.classOrInterfaceMember && !model.actual
                        else false;
                if (final) {
                    decorations.add(AllIcons.Nodes.finalMark);
                }
                if (model.static) {
                    decorations.add(AllIcons.Nodes.staticMark);
                }
                value readonly
                        = model.toplevel
                        && !model.unit is AnyModifiableSourceFile;
                if (readonly) {
                    decorations.add(PlatformIcons.lockedIcon);
                }
                return createIcon(decorations, baseIcon, layer);
            }
            case (is Package) {
                value layer
                        = if (model.shared)
                        then PlatformIcons.publicIcon
                        else PlatformIcons.privateIcon;
                //TODO: PlatformIcons.lockedIcon
                return createIcon([], baseIcon, layer);
            }
            else {
                //TODO: PlatformIcons.lockedIcon
                return baseIcon;
            }
        }
        else {
            switch (obj)
            case (is Tree.Declaration) {
                value annotations = obj.annotationList.annotations;
                Icon visibility;
                for (a in annotations) {
                    if (a.primary.token.text=="shared") {
                        visibility = PlatformIcons.publicIcon;
                        break;
                    }
                }
                else {
                    visibility = PlatformIcons.privateIcon;
                }
                value decorations = ArrayList<Icon>();
                for (a in annotations) {
                    switch (a.primary.token.text)
                    case ("static") {
                        decorations.add(AllIcons.Nodes.staticMark);
                    }
                    case ("final") {
                        decorations.add(AllIcons.Nodes.finalMark);
                    }
                    else {}
                }
                return createIcon(decorations, baseIcon, visibility);
            }
            case (is Tree.SpecifierStatement) {
                return createIcon([], baseIcon, PlatformIcons.publicIcon);
            }
            else {
                return baseIcon;
            }
        }
    }

    function isException(ClsClassImpl cls)
            => InheritanceUtil.isInheritor(cls,
                    CommonClassNames.javaLangThrowable);

    function decorateIcon(PsiModifierListOwner cls, Icon baseIcon) {
        value visibility
                = cls.hasModifierProperty(PsiModifier.public)
                then PlatformIcons.publicIcon
                else PlatformIcons.privateIcon;
        value decorations = ArrayList<Icon>();
        if (cls.hasModifierProperty(PsiModifier.final)
                && baseIcon!=interfaces
            || baseIcon==annotationClasses) {
            decorations.add(AllIcons.Nodes.finalMark);
        }
        if (cls.hasModifierProperty(PsiModifier.static)) {
            decorations.add(AllIcons.Nodes.staticMark);
        }
        decorations.add(PlatformIcons.lockedIcon);
        return createIcon(decorations, baseIcon, visibility);
    }

    shared Icon forClass(ClsClassImpl cls) {

        function has(Annotations ann)
                => cls.modifierList.findAnnotation(ann.className) exists;

        Icon baseIcon;
        if (has(Annotations.moduleDescriptor)) {
            baseIcon = moduleDescriptors;
        } else if (has(Annotations.packageDescriptor)) {
            baseIcon = packageDescriptors;
        } else if (cls.\iinterface || cls.name.endsWith("$impl")) {
            baseIcon
                    = cls.name.endsWith("$annotation$")
                    then annotationClasses
                    else interfaces;
        } else if (has(Annotations.\iobject)) {
            baseIcon = objects;
        } else if (has(Annotations.method)) {
            baseIcon
                    = if (has(Annotations.annotationInstantiation))
                    then annotations
                    else methods;
        } else if (has(Annotations.attribute)) {
            baseIcon = attributes;
        } else {
            baseIcon
                    = if (has(Annotations.annotationType))
                    then annotationClasses
                    else if (cls.hasModifierProperty(PsiModifier.abstract))
                    then (isException(cls) then abstractExceptions else abstractClasses)
                    else (isException(cls) then exceptions else classes);
        }

        return decorateIcon(cls, baseIcon);
    }

    function isGetter(ClsMethodImpl cls)
            => let (name = cls.name)
            name.longerThan(3) &&
            name.startsWith("get") &&
            cls.parameterList.parametersCount==0;

    function isSetter(ClsMethodImpl cls)
            => let (name = cls.name)
            name.longerThan(3) &&
            name.startsWith("set") &&
            cls.parameterList.parametersCount==1;

    shared Icon forMethod(ClsMethodImpl cls)
            => decorateIcon {
                cls = cls;
                baseIcon
                    = isGetter(cls) || isSetter(cls)
                    then attributes
                    else methods;
            };

    shared Icon forField(ClsFieldImpl cls)
            //TODO: this results in the wrong visibility
            //      we should look at the getter for that
            => decorateIcon {
                cls = cls;
                baseIcon = attributes;
            };

    Icon createIcon(List<Icon> decorations, Icon icon, Icon visibility)
            => RowIcon(LayeredIcon(icon, *decorations), visibility);
}
