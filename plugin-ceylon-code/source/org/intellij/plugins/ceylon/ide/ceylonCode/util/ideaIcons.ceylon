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

// TODO merge this with Icons.java?
shared object ideaIcons {
    shared Icon imports => IconLoader.getIcon("/icons/ceylonImports.gif");
    shared Icon packages => IconLoader.getIcon("/icons/package.png");
    shared Icon modules => AllIcons.Nodes.\iArtifact;
    shared Icon classes => PlatformIcons.\iCLASS_ICON;
    shared Icon interfaces => PlatformIcons.\iINTERFACE_ICON;
    shared Icon objects => AllIcons.Nodes.\iAnonymousClass;
    shared Icon methods => PlatformIcons.\iMETHOD_ICON;
    
    shared Icon surround => IconLoader.getIcon("/icons/ceylonSurround.png");
    shared Icon refinement => AllIcons.General.\iImplementingMethod;
    shared Icon param => AllIcons.Nodes.\iParameter;
    shared Icon local => AllIcons.Nodes.\iVariable; // TODO should be different from values
    shared Icon values => AllIcons.Nodes.\iVariable;
    shared Icon anonymousFunction => AllIcons.Nodes.\iFunction;
    shared Icon types => IconLoader.getIcon("/icons/ceylonTypes.png");
    
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
        case (is TypeAlias|NothingType)
            types
        case (is TypeParameter)
            param // TODO wrong!
        else
            null;
        
        if (!exists a = baseIcon, is Declaration decl) {
            if (ModelUtil.isConstructor(decl)) {
                baseIcon = null; // TODO
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