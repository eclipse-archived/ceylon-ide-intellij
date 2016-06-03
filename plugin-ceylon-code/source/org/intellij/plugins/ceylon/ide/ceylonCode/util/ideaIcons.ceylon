import com.intellij.icons {
    AllIcons
}
import com.intellij.openapi.util {
    IconLoader
}
import com.intellij.ui {
    RowIcon
}
import com.intellij.util {
    PlatformIcons
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
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
    NothingType
}

import javax.swing {
    Icon
}

shared object ideaIcons {
    shared Icon imports => IconLoader.getIcon("/icons/ceylonImports.png");
    shared Icon singleImport => IconLoader.getIcon("/icons/ceylonImport.png");
    shared Icon packages => IconLoader.getIcon("/icons/ceylonPackage.png");
    shared Icon modules => IconLoader.getIcon("/icons/ceylonModule.png");
    shared Icon classes => PlatformIcons.classIcon;
    shared Icon abstractClasses => PlatformIcons.abstractClassIcon;
    shared Icon interfaces => PlatformIcons.interfaceIcon;
    shared Icon objects => AllIcons.Nodes.anonymousClass;
    shared Icon methods => PlatformIcons.methodIcon;
    shared Icon formalMethods => PlatformIcons.abstractMethodIcon;
    shared Icon attributes = PlatformIcons.fieldIcon;
    shared Icon enumerations = PlatformIcons.enumIcon;
    shared Icon exceptions = AllIcons.Nodes.exceptionClass;
    shared Icon param => AllIcons.Nodes.parameter;
    shared Icon local => IconLoader.getIcon("/icons/ceylonLocal.png");
    shared Icon values => AllIcons.Nodes.variable;
    shared Icon formalValues => IconLoader.getIcon("/icons/formalValue.png");
    shared Icon anonymousFunction => AllIcons.Nodes.\ifunction;
    shared Icon annotations => AllIcons.Gutter.extAnnotation;
    shared Icon constructors => AllIcons.Nodes.classInitializer;
    
    shared Icon refinement => AllIcons.Gutter.implementingMethod;
    shared Icon extendedType => AllIcons.Gutter.overridingMethod;
    shared Icon satisfiedTypes => AllIcons.General.implementingMethod;
    shared Icon types => IconLoader.getIcon("/icons/ceylonTypes.png");
    
    shared Icon surround => IconLoader.getIcon("/icons/ceylonSurround.png");
    shared Icon correction => AllIcons.Actions.redo;
    shared Icon addCorrection => AllIcons.General.add;
    shared Icon see => AllIcons.Actions.share;
    shared Icon returns => AllIcons.Actions.stepOut;
    
    shared Icon ceylon => IconLoader.getIcon("/icons/ceylon.png");
    shared Icon file => IconLoader.getIcon("/icons/ceylonFile.png");

    shared Icon problemsViewOk => IconLoader.getIcon("/icons/ceylonProblemsOk.png");
    shared Icon problemsViewErrors => IconLoader.getIcon("/icons/ceylonProblemsErrors.png");
    shared Icon problemsViewWarnings => IconLoader.getIcon("/icons/ceylonProblemsWarnings.png");

    shared Icon? forDeclaration(Tree.Declaration|Declaration obj) {
        value decl = if (is Tree.Declaration obj, exists model = obj.declarationModel)
        then model
        else obj;

        variable value baseIcon = switch(decl)
        case (is Tree.AnyClass)
            classes
        case (is Class)
            if (decl.anonymous)
            then objects
            else if (decl.abstract)
            then abstractClasses
            else classes
        case (is Tree.AnyInterface|Interface)
            interfaces
        case (is Tree.AnyMethod)
            methods
        case (is Function)
            if (decl.formal) then formalMethods else methods
        case (is Tree.ObjectDefinition)
            objects
        case (is Value)
            if (ModelUtil.isObject(decl))
            then objects
            else if (decl.formal)
            then formalValues
            else values
        case (is Tree.TypeAliasDeclaration|TypeAlias|NothingType)
            types
        case (is TypeParameter)
            param // TODO wrong!
        case (is Tree.AttributeDeclaration)
            values
        else
            null;
        
        if (!exists a = baseIcon, is Declaration decl) {
            if (ModelUtil.isConstructor(decl)) {
                baseIcon = constructors;
            } else if (decl.parameter) {
                baseIcon = param;
            }
        }
        Declaration? model = if (is Declaration decl) then decl else decl.declarationModel;
        
        if (exists icon = baseIcon, exists model) {
            value layer = if (model.shared) then PlatformIcons.publicIcon
                                            else PlatformIcons.privateIcon;
            return createHorizontalIcon(icon, layer);
        }
        
        if (baseIcon is Null) {
            print("Missing icon for ``decl``");
        }
        
        return baseIcon;
    }

    Icon createHorizontalIcon(Icon *icons) {
        value icon = RowIcon(icons.size, RowIcon.Alignment.center);
        icons.indexed.each((idx -> ic) => icon.setIcon(ic, idx));
        return icon;
    }
}
