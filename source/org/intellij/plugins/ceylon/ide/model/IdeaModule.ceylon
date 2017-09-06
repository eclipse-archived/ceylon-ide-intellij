import ceylon.collection {
    HashSet,
    MutableSet,
    MutableList
}
import ceylon.language {
    langNull=null,
    langFalse=false
}

import com.intellij.codeInsight.completion.impl {
    CamelHumpMatcher
}
import com.intellij.openapi.application {
    ApplicationManager {
        application
    }
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VirtualFileManager,
    JarFileSystem
}
import com.redhat.ceylon.cmr.api {
    RepositoryManager,
    ArtifactContext
}
import com.redhat.ceylon.ide.common.model {
    IdeModule,
    BaseIdeModule
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
}
import com.redhat.ceylon.model.typechecker.model {
    DeclarationWithProximity,
    Cancellable,
    Unit,
    CeylonModule=Module
}

import java.io {
    File
}
import java.lang {
    Types {
        nativeString
    },
    JString=String
}
import java.util {
    JMap=Map,
    Collections
}

shared class IdeaModule(
    shared actual IdeaModuleManager moduleManager,
    shared actual IdeaModuleSourceMapper moduleSourceMapper,
    MutableList<VirtualFile> arrayList)
        extends IdeModule<Module,VirtualFile,VirtualFile,VirtualFile>() {

    value packageList = HashSet<String>();

    shared actual JMap<JString,DeclarationWithProximity> getAvailableDeclarations
            (Unit unit, String startingWith, Integer proximity, Cancellable? canceller) {

        if (canceller?.cancelled else false) {
            return Collections.emptyMap<JString,DeclarationWithProximity>();
        }

        if (exists project = moduleManager.ceylonProject,
            project.compileToJava,
            startingWith.size>0) {

            value proposals = scanJavaIndex {
                that = this;
                sourceUnit = unit;
                moduleManager = moduleManager;
                mod = project.ideArtifact;
                startingWith = startingWith;
                proximity = proximity;
            };

            proposals.putAll(super.getAvailableDeclarationsInCurrentModule(unit, startingWith, proximity, canceller));

            addJavaArrays(proposals, unit, startingWith, proximity);

            return proposals;
        }
        return super.getAvailableDeclarations(unit, startingWith, proximity, canceller);
    }

    void addJavaArrays(JMap<JString,DeclarationWithProximity> proposals, Unit unit,
        String startingWith, Integer proximity) {

        if ("java.base" in transitiveDependencies.map(CeylonModule.nameAsString)) {
            value arrays = [
                "ObjectArray" -> unit.javaObjectArrayDeclaration,
                "BooleanArray" -> unit.javaBooleanArrayDeclaration,
                "ByteArray" -> unit.javaByteArrayDeclaration,
                "ShortArray" -> unit.javaShortArrayDeclaration,
                "IntArray" -> unit.javaIntArrayDeclaration,
                "LongArray" -> unit.javaLongArrayDeclaration,
                "FloatArray" -> unit.javaFloatArrayDeclaration,
                "DoubleArray" -> unit.javaDoubleArrayDeclaration,
                "CharArray" -> unit.javaCharArrayDeclaration,
                "Types" -> unit.javaLangPackage?.getMember("Types", null, false),
                "overloaded" -> unit.javaLangPackage?.getMember("overloaded", null, false)
            ];

            value matcher = CamelHumpMatcher(startingWith);

            for (name -> declaration in arrays) {
                if (matcher.isStartMatch(name)) {
                    proposals[nativeString("java.lang." + name)]
                        = DeclarationWithProximity(declaration, proximity+4);
                }
            }
        }
    }

    shared actual void encloseOnTheFlyTypechecking(void typechecking()) {
        if (application.readAccessAllowed) {
            concurrencyManager.withAlternateResolution(() {
                typechecking();
            });
        } else {
            concurrencyManager.withUpToDateIndexes(() {
                typechecking();
            });
        }
    }

    shared actual Set<String> listPackages() {
        value name = nameAsString;
        if (JDKUtils.isJDKModule(name)) {
            for (p in JDKUtils.getJDKPackagesByModule(name)) {
                packageList.add(p.string);
            }
        }
        else if (JDKUtils.isOracleJDKModule(name)) {
            for (p in JDKUtils.getOracleJDKPackagesByModule(name)) {
                packageList.add(p.string);
            }
        }
        else if (java,
            !moduleManager.isExternalModuleLoadedFromSource(nameAsString),
            exists mod = ceylonProject,
            packageList.empty) {
            scanPackages(mod.ideArtifact, packageList);
        }

        return packageList;
    }

    // TODO copy paste from CeylonProjectModulesContainer, should be in ide-common
    File? getModuleArtifact(RepositoryManager provider, BaseIdeModule mod) {

        if (!mod.isSourceArchive,
            exists moduleFile = mod.artifact,
            moduleFile.\iexists()) {
            return moduleFile;
        }

        value carCtx
                = ArtifactContext(null,
                                  mod.nameAsString, mod.version,
                                  ArtifactContext.car);
        value jarCtx
                = ArtifactContext(null,
                                  mod.nameAsString, mod.version,
                                  ArtifactContext.car);
        if (exists artifact = provider.getArtifact(carCtx)) {
            return artifact;
        }
        else if (exists artifact = provider.getArtifact(jarCtx)) {
            return artifact;
        }
        else {
            return null;
        }
    }

    void scanPackages(Module mod, MutableSet<String> packageList) {
        if (exists jarToSearch = returnCarFile()
            else getModuleArtifact(moduleManager.repositoryManager, this),
            exists vfile
                    = VirtualFileManager.instance.findFileByUrl(
                        JarFileSystem.protocolPrefix
                        + jarToSearch.absolutePath
                        + JarFileSystem.jarSeparator)) {
            listPackagesInternal(vfile, packageList);
        }
    }

    void listPackagesInternal(VirtualFile vfile, MutableSet<String> packageList,
        String parentPackage = "") {

        for (child in vfile.children) {
            if (child.directory) {
                String pack =
                        (parentPackage.empty then "" else parentPackage + ".")
                        + child.name;
                packageList.add(pack);
                listPackagesInternal(child, packageList, pack);
            }
        }
    }

    modelLoader => moduleManager.modelLoader;

    shared actual void refreshJavaModel() {
        // TODO
        print("TODO Refresh model");
    }
}
