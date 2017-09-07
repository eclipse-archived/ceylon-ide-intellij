import ceylon.language {
    Integer
}

import com.intellij.debugger {
    PositionManagerFactory,
    NoDataException,
    SourcePosition
}
import com.intellij.debugger.engine {
    DebugProcess,
    PositionManagerEx,
    DebugProcessImpl
}
import com.intellij.debugger.engine.evaluation {
    EvaluationContext
}
import com.intellij.debugger.engine.requests {
    RequestManagerImpl
}
import com.intellij.debugger.jdi {
    StackFrameProxyImpl
}
import com.intellij.debugger.requests {
    ClassPrepareRequestor
}
import com.intellij.debugger.ui.impl.watch {
    StackFrameDescriptorImpl,
    MethodsTracker
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.psi.util {
    PsiTreeUtil {
        getParentOfType
    }
}
import com.intellij.util {
    ThreeState
}
import com.intellij.xdebugger.frame {
    XStackFrame
}
import com.redhat.ceylon.ide.common.debug {
    getFirstValidLocation
}
import com.redhat.ceylon.model.typechecker.model {
    Interface
}
import com.sun.jdi {
    AbsentInformationException,
    Location,
    ReferenceType
}
import com.sun.jdi.request {
    ClassPrepareRequest,
    EventRequest
}

import java.lang {
    InternalError
}
import java.util {
    Collections,
    List
}

import org.intellij.plugins.ceylon.ide.model {
    IdeaModule,
    concurrencyManager {
        needReadAccess
    },
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonPsi,
    CeylonTreeUtil {
        getDeclaringFile
    }
}

class CeylonPositionManager(DebugProcess process) extends PositionManagerEx() {

    //value logger = Logger.getInstance(`class`.qualifiedName);

    shared actual SourcePosition getSourcePosition(Location? location) {
        if (exists location,
            exists projects = getCeylonProjects(process.project)) {

            for (p in projects.ceylonProjects) {
                try {
                    if (exists pu = p.typechecker?.getPhasedUnitFromRelativePath(location.sourcePath()),
                        exists file= getDeclaringFile(pu.unit, process.project)) {
                        return toSourcePosition(location, file);
                    }
                    if (exists mods = p.modules?.typecheckerModules?.listOfModules) {
                        for (mod in mods) {
                            if (is IdeaModule mod,
                                exists epu = mod.getPhasedUnitFromRelativePath(location.sourcePath()),
                                exists file= getDeclaringFile(epu.unit, process.project)) {

                                return toSourcePosition(location, file);
                            }
                        }
                    }
                }
                catch (AbsentInformationException e) {
                    e.printStackTrace();
                }
            }
        }
        throw NoDataException.instance;
    }

    SourcePosition toSourcePosition(Location location, PsiFile file) {
        value lineNumber = calcLineIndex(location);
        if (lineNumber<0) {
            throw NoDataException.instance;
        }
        return SourcePosition.createFromLine(file, lineNumber);
    }

    Integer calcLineIndex(Location location) {
        try {
            return location.lineNumber() - 1;
        }
        catch (InternalError e) {
            return - 1;
        }
    }

    shared actual List<ReferenceType> getAllClasses(SourcePosition classPosition) {
        checkCeylonFile(classPosition);

        return if (exists cls = findClassBySourcePosition(classPosition))
        then process.virtualMachineProxy.classesByName(cls)
        else Collections.emptyList<ReferenceType>();
    }

    shared actual List<Location> locationsOfLine(ReferenceType type, SourcePosition position) {
        checkCeylonFile(position);
        try {
            value line = position.line + 1;
            if (exists locations = type.locationsOfLine(DebugProcess.javaStratum, null, line),
                !locations.empty) {
                return locations;
            }
            throw NoDataException.instance;
        }
        catch (AbsentInformationException e) {
            throw NoDataException.instance;
        }
    }

    shared actual ClassPrepareRequest? createPrepareRequest(ClassPrepareRequestor requestor, SourcePosition position) {
        checkCeylonFile(position);

        if (is CeylonFile file = position.file,
            exists doc = needReadAccess(() => file.viewProvider.document),
            exists location = getFirstValidLocation {
                rootNode = needReadAccess(() => file.compilationUnit);
                document = IdeaDocument(doc);
                requestedLine = position.line + 1;
            },
            is RequestManagerImpl requestManager = process.requestsManager) {

            value rm = requestManager.vmRequestManager;
            value req = rm.createClassPrepareRequest();
            req.setSuspendPolicy(EventRequest.suspendEventThread);
            req.addSourceNameFilter(position.file.name);
            requestManager.registerRequestInternal(requestor, req);
            return req;
        }
        return null;
    }

    void checkCeylonFile(SourcePosition position) {
        if (!(position.file is CeylonFile)) {
            throw NoDataException.instance;
        }
    }

    String? findClassBySourcePosition(SourcePosition position) {
        variable CeylonPsi.DeclarationPsi? declaration
                = getParentOfType(position.elementAt, `CeylonPsi.ClassDefinitionPsi`, `CeylonPsi.InterfaceDefinitionPsi`);
        variable Boolean useUnderscoreSuffix = false;
        if (!exists _ = declaration) {
            declaration = getParentOfType(position.elementAt, `CeylonPsi.ObjectDefinitionPsi`);
            useUnderscoreSuffix = true;
        }
        if (!exists _ = declaration) {
            declaration = getParentOfType(position.elementAt, `CeylonPsi.MethodDefinitionPsi`);
            useUnderscoreSuffix = true;
        }
        if (exists decl = declaration,
            exists model = decl.ceylonNode.declarationModel) {
            variable String cls = model.qualifiedNameString.replace("::", ".") + (if (useUnderscoreSuffix) then "_" else "");
            if (is Interface model) {
                cls += "$impl";
            }
            print("Source position ``position.file.name``:``position.line`` => ``cls``");
            return cls;
        }
        print("Can't find classes for source position ``position.file.name``:``position.line``");
        return null;
    }

    shared actual XStackFrame? createStackFrame(StackFrameProxyImpl frame,
        DebugProcessImpl debugProcess, Location location) {

        try {
            if (location.sourceName().endsWith(".ceylon")) {
                return CeylonStackFrame(StackFrameDescriptorImpl(frame, MethodsTracker()), true);
            }
        } catch (AbsentInformationException e) {
            // probably not a Ceylon file anyway
        }

        return null;
    }

    evaluateCondition(EvaluationContext context, StackFrameProxyImpl frame, Location location, String expression)
            => ThreeState.unsure;
}

shared class CeylonPositionManagerFactory() extends PositionManagerFactory() {
    createPositionManager(DebugProcess process) => CeylonPositionManager(process);
}
