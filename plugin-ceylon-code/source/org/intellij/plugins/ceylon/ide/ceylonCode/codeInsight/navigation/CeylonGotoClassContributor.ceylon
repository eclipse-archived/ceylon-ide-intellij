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
    ContainerUtil {
        newArrayList
    }
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.ide.common.model {
    AnyJavaUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Module
}

import java.lang {
    JString=String,
    ObjectArray
}
import java.util {
    Set
}

import org.intellij.plugins.ceylon.ide.ceylonCode.codeInsight.navigation {
    CeylonGotoContributor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    IdeaModule,
    concurrencyManager
}


shared class CeylonGotoClassContributor() extends CeylonGotoContributor() {

    shared actual void scanModules(TypeChecker typechecker, Set<Module> modules,
            Boolean includeNonProjectItems, Boolean(Declaration) consumer) {
        for (mod in newArrayList(modules)) {
            if (is IdeaModule mod, includeNonProjectItems || mod.isProjectModule) {
                for (pack in newArrayList(mod.packages)) {
                    for (declaration in newArrayList(pack.members)) {
                        if (!declaration.unit is AnyJavaUnit,
                            includeDeclaration(mod, declaration),
                                !consumer(declaration)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    Boolean includeDeclaration(IdeaModule mod, Declaration dec) {
        try {
            value visibleFromSourceModules
                = dec.toplevel
                then dec.shared || mod.isProjectModule
                else dec.shared;
            return visibleFromSourceModules
                && isPresentable(dec);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    Boolean isPresentable(Declaration d)
            => if (exists name = d.name)
            then !d.anonymous && !isOverloadedVersion(d)
            else false;

    Boolean isOverloadedVersion(Declaration? decl)
            => if (exists decl)
            then decl.overloaded && ! decl.abstraction
            else false;
}

shared class CeylonGotoSymbolContributor() extends CeylonGotoContributor() {

    shared actual void scanModules(TypeChecker typechecker, Set<Module> modules,
            Boolean includeNonProjectItems, Boolean(Declaration) consumer) {
        //pick up stuff from edited source files
        for (pu in typechecker.phasedUnits.phasedUnits) {
            for (declaration in pu.declarations) {
                if (isPresentable(declaration),
                        !consumer(declaration)) {
                    return;
                }
            }
        }
        //scan all the modules
        for (mod in modules) {
            if (is IdeaModule mod, includeNonProjectItems || mod.isProjectModule) {
                for (pu in mod.phasedUnits) {
                    for (declaration in pu.declarations) {
                        if (!declaration.unit is AnyJavaUnit,
                            isPresentable(declaration),
                                !consumer(declaration)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    Boolean isPresentable(Declaration d)
            => if (exists name = d.name)
            then !d.anonymous
            else false;

}

shared abstract class CeylonGotoContributor() satisfies GotoClassContributor {

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

    shared actual String? getQualifiedName(NavigationItem item)
            => if (is DeclarationNavigationItem item)
            then item.decl.qualifiedNameString else null;

    qualifiedNameSeparator => ".";

    shared formal void scanModules(TypeChecker typechecker, Set<Module> modules,
        Boolean includeNonProjectItems, Boolean(Declaration) consumer);

    void processDeclarations(Project project, Boolean includeNonProjectItems, Boolean(Declaration) consumer) {
        concurrencyManager.withAlternateResolution(() {
            if (exists ceylonProjects = project.getComponent(javaClass<IdeaCeylonProjects>())) {
                for (mod in ModuleManager.getInstance(project).modules) {
                    if (exists ceylonProject = ceylonProjects.getProject(mod),
                        exists modules = ceylonProject.modules,
                        exists typechecker = ceylonProject.typechecker) {

                        scanModules(typechecker,
                            modules.typecheckerModules.listOfModules,
                            includeNonProjectItems, consumer);
                    }
                }
            }
        });
    }

}