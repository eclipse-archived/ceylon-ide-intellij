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
    ClassOrInterface,
    Scope
}

shared class DeclarationPresentationProvider()
        satisfies ItemPresentationProvider<DeclarationNavigationItem> {

    getPresentation(DeclarationNavigationItem item) 
            => object satisfies ItemPresentation {

        shared actual Icon? getIcon(Boolean unused) 
                => ideaIcons.forDeclaration(item.decl);

        function locationAsString(Scope container)
                => "(``container.qualifiedNameString else "default package"``)";

        locationString
                => let (dec = item.decl)
                if (is ClassOrInterface type = dec.container)
                then locationAsString(type.container)
                else locationAsString(dec.container);

        presentableText 
                => let (dec = item.decl)
                if (is ClassOrInterface type = dec.container)
                then type.name + "." + dec.name
                else dec.name;
    };
}
