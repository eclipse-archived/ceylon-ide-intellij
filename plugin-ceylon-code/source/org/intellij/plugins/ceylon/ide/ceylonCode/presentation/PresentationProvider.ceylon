import com.intellij.navigation {
    ItemPresentation,
    ItemPresentationProvider
}
import com.intellij.psi {
    PsiElement
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Scope,
    TypeDeclaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonClass,
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    DeclarationPsiNameIdOwner,
    SpecifierStatementPsiIdOwner
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

shared class ClassPresentationProvider() 
        satisfies ItemPresentationProvider<CeylonClass> {

    shared actual ItemPresentation getPresentation(CeylonClass item)
            => object satisfies ItemPresentation {

                shared actual String? presentableText {
                    if (exists model = item.ceylonNode?.declarationModel) {
                        value text = StringBuilder().append(model.name);
                        variable Scope container = model.container;
                        while (is TypeDeclaration type = container) {
                            text.append(" in ").append(type.name);
                            container = container.container;
                        }
                        return text.string;
                    } else {
                        if (exists id = item.ceylonNode?.identifier?.text) {
                            value text = StringBuilder().append(id);
                            variable PsiElement container = item.parent;
                            while (is CeylonPsi.ClassOrInterfacePsi classOrInterface = item.parent) {
                                if (exists typeName = classOrInterface.ceylonNode?.identifier?.text) {
                                    text.append(" in ").append(typeName);
                                }
                                container = container.parent;
                            }
                            return text.string;
                        }
                        return null;
                    }
                }

                shared actual String? locationString {
                    if (exists node = item.ceylonNode) {
                        value nameAsString = node.unit.\ipackage.nameAsString;
                        return nameAsString.empty
                            then "(default package)"
                            else "(``nameAsString``)";
                    }
                    else {
                        return null;
                    }
                }

                getIcon(Boolean unused)
                        => if (exists node = item.ceylonNode)
                        then icons.forDeclaration(node)
                        else null;
            };
}

shared class DeclarationPresentationProvider() 
        satisfies ItemPresentationProvider<DeclarationPsiNameIdOwner> {

    shared actual ItemPresentation getPresentation(DeclarationPsiNameIdOwner item)
            => object satisfies ItemPresentation {

                shared actual String? presentableText {
                    if (exists model = item.ceylonNode?.declarationModel) {
                        value name = model.name;
                        if (is ClassOrInterface classOrInterface = model.container) {
                            return classOrInterface.name + "." + name;
                        } else {
                            return name;
                        }
                    } else {
                        if (exists name = item.ceylonNode?.identifier?.text) {
                            if (is CeylonPsi.ClassOrInterfacePsi classOrInterface = item.parent) {
                                if (exists typeName = classOrInterface.ceylonNode?.identifier?.text) {
                                    return typeName + "." + name;
                                }
                            } else {
                                return name;
                            }
                        }
                        return null;
                    }
                }

                shared actual String? locationString {
                    if (exists model = item.ceylonNode?.declarationModel) {
                        value nat = model.native then "``model.nativeBackends`` " else "";
                        value dec
                            = if (is ClassOrInterface container = model.container)
                            then container else model;
                        value qualifiedNameString = dec.container.qualifiedNameString;
                        return qualifiedNameString.empty
                            then nat + "(default package)"
                            else nat + "(``qualifiedNameString``)";
                    } else if (exists unit = item.ceylonNode?.unit) {
                        value nameAsString = unit.\ipackage.nameAsString;
                        return nameAsString.empty
                            then "(default package)"
                            else "(``nameAsString``)";
                    } else {
                        return null;
                    }
                }

                getIcon(Boolean unused)
                        => if (exists node = item.ceylonNode)
                        then icons.forDeclaration(node)
                        else null;
            };
}

shared class SpecifierPresentationProvider() 
        satisfies ItemPresentationProvider<SpecifierStatementPsiIdOwner> {

    shared actual ItemPresentation? getPresentation(SpecifierStatementPsiIdOwner item)
            => item.ceylonNode.refinement then
            object satisfies ItemPresentation {

                shared actual String? presentableText {
                    if (exists model = item.ceylonNode?.declaration) {
                        value name = model.name;
                        if (is ClassOrInterface classOrInterface = model.container) {
                            return classOrInterface.name + "." + name;
                        } else {
                            return name;
                        }
                    } else {
                        if (exists text = item.ceylonNode?.baseMemberExpression?.text) {
                            if (is CeylonPsi.ClassOrInterfacePsi classOrInterface = item.parent) {
                                if (exists typeName = classOrInterface.ceylonNode?.identifier?.text) {
                                    return typeName + "." + text;
                                }
                            } else {
                                return text;
                            }
                        }
                        return null;
                    }
                }

                shared actual String? locationString {
                    if (exists model = item.ceylonNode?.declaration) {
                        value dec
                            = if (is ClassOrInterface container = model.container)
                            then container else model;
                        value qualifiedNameString = dec.container.qualifiedNameString;
                        return if (qualifiedNameString.empty)
                            then "(default package)"
                            else "(``qualifiedNameString``)";
                    } else if (exists unit = item.ceylonNode?.unit) {
                        value nameAsString = unit.\ipackage.nameAsString;
                        return nameAsString.empty
                            then "(default package)"
                            else "(``nameAsString``)";
                    }
                    else {
                        return null;
                    }
                }

                getIcon(Boolean unused)
                        => if (exists node = item.ceylonNode)
                        then icons.forDeclaration(node)
                        else null;

            };
    
}
