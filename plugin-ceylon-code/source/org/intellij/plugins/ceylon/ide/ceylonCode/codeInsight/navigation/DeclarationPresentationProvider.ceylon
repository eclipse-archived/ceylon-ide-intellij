import com.intellij.navigation {
    ItemPresentationProvider,
    ItemPresentation
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface
}

shared class DeclarationPresentationProvider()
        satisfies ItemPresentationProvider<DeclarationNavigationItem> {

    getPresentation(DeclarationNavigationItem item) => object satisfies ItemPresentation {

        shared actual Icon? getIcon(Boolean unused) => ideaIcons.forDeclaration(item.decl);

        locationString
            => let (qName = item.decl.container.qualifiedNameString else "default package")
               "(" + qName + ")";

        presentableText 
                => if (is ClassOrInterface container = item.decl.container) 
                then container.name + "." + item.decl.name 
                else item.decl.name;
    };
}
