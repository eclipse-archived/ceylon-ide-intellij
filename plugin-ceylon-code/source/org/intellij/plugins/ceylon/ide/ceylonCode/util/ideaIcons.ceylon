import com.intellij.icons {
    AllIcons
}
import com.intellij.openapi.util {
    IconLoader
}
import com.intellij.ui {
    LayeredIcon
}
import com.intellij.util {
    PlatformIcons
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}

import javax.swing {
    Icon
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

shared object ideaIcons {
    shared Icon imports => IconLoader.getIcon("/icons/ceylonImports.png");
    shared Icon singleImport => IconLoader.getIcon("/icons/ceylonImport.png");
    shared Icon packages => IconLoader.getIcon("/icons/ceylonPackage.png");
    shared Icon modules => IconLoader.getIcon("/icons/ceylonModule.png");
    shared Icon classes => PlatformIcons.\iCLASS_ICON;
    shared Icon interfaces => PlatformIcons.\iINTERFACE_ICON;
    shared Icon objects => AllIcons.Nodes.\iAnonymousClass;
    shared Icon methods => PlatformIcons.\iMETHOD_ICON;
    shared Icon attributes = PlatformIcons.\iFIELD_ICON;
    shared Icon enumerations = PlatformIcons.\iENUM_ICON;
    shared Icon exceptions = AllIcons.Nodes.\iExceptionClass;
    shared Icon param => AllIcons.Nodes.\iParameter;
    shared Icon local => IconLoader.getIcon("/icons/ceylonLocal.png");
    shared Icon values => AllIcons.Nodes.\iVariable;
    shared Icon anonymousFunction => AllIcons.Nodes.\iFunction;
    shared Icon annotations => AllIcons.Gutter.\iExtAnnotation;
    shared Icon constructors => AllIcons.Nodes.\iClassInitializer;
    
    shared Icon refinement => AllIcons.General.\iImplementingMethod;
    shared Icon extendedType => AllIcons.General.\iOverridingMethod;
    shared Icon satisfiedTypes => AllIcons.General.\iImplementingMethod;
    shared Icon types => IconLoader.getIcon("/icons/ceylonTypes.png");
    
    shared Icon surround => IconLoader.getIcon("/icons/ceylonSurround.png");
    shared Icon correction => AllIcons.Actions.\iRedo;
    shared Icon addCorrection => AllIcons.General.\iAdd;
    shared Icon see => AllIcons.Actions.\iShare;
    shared Icon returns => AllIcons.Actions.\iStepOut;
    
    shared Icon ceylon => IconLoader.getIcon("/icons/ceylon.png");
    shared Icon file => IconLoader.getIcon("/icons/ceylonFile.png");
    
    shared Icon? forDeclaration(Tree.Declaration|Declaration decl) {
        variable value baseIcon = switch(decl)
        case (is Tree.AnyClass)
            classes
        case (is Class)
            if (decl.anonymous) then objects else classes
        case (is Tree.AnyInterface|Interface)
            interfaces
        case (is Tree.AnyMethod|Function)
            methods
        case (is Tree.ObjectDefinition)
            objects
        case (is Value)
            if (ModelUtil.isObject(decl)) then objects else values
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
            value layer = if (model.shared) then PlatformIcons.\iPUBLIC_ICON
                                            else PlatformIcons.\iPRIVATE_ICON;
            return LayeredIcon.createHorizontalIcon(icon, layer);
        }
        
        if (baseIcon is Null) {
            print("Missing icon for ``decl``");
        }
        
        return baseIcon;
    }
}
