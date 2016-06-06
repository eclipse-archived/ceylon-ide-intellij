import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaClass,
    createJavaStringArray,
    createJavaObjectArray
}

import com.intellij.navigation {
    GotoClassContributor,
    NavigationItem
}
import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.openapi.\imodule {
    ModuleManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.util.containers {
    ContainerUtil
}
import com.redhat.ceylon.ide.common.model {
    AnyJavaUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.lang {
    JString=String,
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    IdeaModule,
    concurrencyManager
}

shared class CeylonGotoClassContributor() satisfies GotoClassContributor {

    value logger = Logger.getInstance(javaClass<CeylonGotoClassContributor>());

    shared actual ObjectArray<NavigationItem> getItemsByName(String name, String pattern,
        Project project, Boolean includeNonProjectItems) {

        value items = ArrayList<NavigationItem>();

        processDeclarations(project, includeNonProjectItems, (dec) {
            if (dec.name == name) {
                items.add(DeclarationNavigationItem(dec, project));
            }
            return true;
        });

        return createJavaObjectArray(items);
    }

    shared actual ObjectArray<JString> getNames(Project project, Boolean includeNonProjectItems) {
        value names = ArrayList<String>();

        value start = system.nanoseconds;

        processDeclarations(project, includeNonProjectItems, (dec) {
            names.add(dec.name);
            return true;
        });

        logger.debug("Got names in ``system.nanoseconds - start``ns");

        return createJavaStringArray(names);
    }

    shared actual String? getQualifiedName(NavigationItem item) {
        if (is DeclarationNavigationItem item) {
            return item.decl.qualifiedNameString;
        }

        return null;
    }

    qualifiedNameSeparator => ".";

    void processDeclarations(Project project, Boolean includeNonProjectItems, Boolean(Declaration) consumer) {
        concurrencyManager.withAlternateResolution(() {
            if (exists ceylonProjects = project.getComponent(javaClass<IdeaCeylonProjects>())) {
                for (mod in ModuleManager.getInstance(project).modules) {
                    if (exists ceylonProject = ceylonProjects.getProject(mod),
                        exists modules = ceylonProject.modules) {

                        for (m in modules.typecheckerModules.listOfModules) {
                            if (is IdeaModule m,
                                includeNonProjectItems || m.isProjectModule) {

                                for (pack in ContainerUtil.newArrayList(m.packages)) {

                                    for (declaration in ContainerUtil.newArrayList(pack.members)) {
                                        if (exists name = declaration.name,
                                            !is AnyJavaUnit u = declaration.unit,
                                            includeDeclaration(m, declaration)) {

                                            if (!consumer(declaration)) {
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    Boolean includeDeclaration(IdeaModule \imodule, Declaration dec) {
        try {
            variable Boolean visibleFromSourceModules;
            if (dec.toplevel) {
                visibleFromSourceModules = dec.shared || \imodule.isProjectModule;
            } else {
                visibleFromSourceModules = dec.shared;
            }
            return visibleFromSourceModules &&isPresentable(dec);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    Boolean isPresentable(Declaration d) {
        return if (exists name = d.name)
            then !d.anonymous && !isOverloadedVersion(d)
            else false;
    }

    Boolean isOverloadedVersion(Declaration? decl) {
        return if (exists decl)
            then decl.overloaded && ! decl.abstraction
            else false;
    }
}