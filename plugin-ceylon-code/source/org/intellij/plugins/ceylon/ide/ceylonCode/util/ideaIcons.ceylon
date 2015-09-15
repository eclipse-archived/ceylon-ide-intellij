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
    shared Icon local => AllIcons.Nodes.\iVariable;
    shared Icon anonymousFunction => AllIcons.Nodes.\iFunction;
    
    shared Icon? forDeclaration(Tree.Declaration decl) {
        value baseIcon = switch(decl)
        case (is Tree.AnyClass)
            classes
        case (is Tree.AnyInterface)
            interfaces
        case (is Tree.AnyMethod)
            methods
        case (is Tree.ObjectDefinition)
            objects
        else
            null;
        
        if (exists baseIcon, exists model = decl.declarationModel) {
            value layer = if (model.shared) then PlatformIcons.\iPUBLIC_ICON
                                            else PlatformIcons.\iPRIVATE_ICON;
            return LayeredIcon.createHorizontalIcon(baseIcon, layer);
        }
        return baseIcon;
    }
}