package org.intellij.plugins.ceylon.ide.debugger;

import ceylon.language.Entry;
import ceylon.language.Integer;
import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.engine.requests.RequestManagerImpl;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.psi.PsiFile;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModule;
import org.intellij.plugins.ceylon.ide.ceylonCode.platform.IdeaDocument;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;
import static com.redhat.ceylon.ide.common.debug.getFirstValidLocation_.getFirstValidLocation;
import static com.redhat.ceylon.ide.common.util.toJavaIterable_.toJavaIterable;
import static org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava.needReadAccess;
import static org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil.getDeclaringFile;

class CeylonPositionManager implements PositionManager {

    private DebugProcess process;

    CeylonPositionManager(DebugProcess process) {
        this.process = process;
    }

    @Nullable
    @Override
    public SourcePosition getSourcePosition(Location location) throws NoDataException {
        if (location == null) {
            throw NoDataException.INSTANCE;
        }

        IdeaCeylonProjects projects = process.getProject().getComponent(IdeaCeylonProjects.class);

        if (projects != null) {
            TypeDescriptor td = TypeDescriptor.klass(IdeaCeylonProject.class);
            for (CeylonProject p : toJavaIterable(td, projects.getCeylonProjects())) {
                try {
                    PhasedUnit pu = p.getTypechecker().getPhasedUnitFromRelativePath(location.sourcePath());
                    if (pu != null) {
                        PsiFile file = getDeclaringFile(pu.getUnit(), process.getProject());

                        if (file != null) {
                            return toSourcePosition(location, file);
                        }
                    }

                    for (Module mod : p.getModules().getTypecheckerModules().getListOfModules()) {
                        if (mod instanceof IdeaModule) {
                            ExternalPhasedUnit epu = ((IdeaModule) mod).getPhasedUnitFromRelativePath(location.sourcePath());
                            if (epu != null) {
                                PsiFile file = getDeclaringFile(epu.getUnit(), process.getProject());

                                if (file != null) {
                                    return toSourcePosition(location, file);
                                }
                            }
                        }
                    }
                } catch (AbsentInformationException e) {
                    e.printStackTrace();
                }
            }
        }

        throw NoDataException.INSTANCE;
    }

    private SourcePosition toSourcePosition(Location location, PsiFile file) throws NoDataException {
        int lineNumber = calcLineIndex(location);
        if (lineNumber < 0) {
            throw NoDataException.INSTANCE;
        }
        return SourcePosition.createFromLine(file, lineNumber);
    }

    private int calcLineIndex(Location location) {
        if (location == null) return -1;

        try {
            return location.lineNumber() - 1;
        } catch (InternalError e) {
            return -1;
        }
    }

    @NotNull
    @Override
    public List<ReferenceType> getAllClasses(@NotNull SourcePosition classPosition) throws NoDataException {
        checkCeylonFile(classPosition);
        String cls = findClassBySourcePosition(classPosition);
        return cls == null ? Collections.<ReferenceType>emptyList() : process.getVirtualMachineProxy().classesByName(cls);
    }

    @NotNull
    @Override
    public List<Location> locationsOfLine(@NotNull ReferenceType type, @NotNull SourcePosition position) throws NoDataException {
        checkCeylonFile(position);
        try {
            int line = position.getLine() + 1;
            List<Location> locations = type.locationsOfLine(DebugProcess.JAVA_STRATUM, null, line);
            if (locations == null || locations.isEmpty()) {
                throw NoDataException.INSTANCE;
            }
            return locations;
        } catch (AbsentInformationException e) {
            throw NoDataException.INSTANCE;
        }
    }

    @Nullable
    @Override
    public ClassPrepareRequest createPrepareRequest(@NotNull ClassPrepareRequestor requestor, @NotNull SourcePosition position) throws NoDataException {
        checkCeylonFile(position);

        final CeylonFile file = (CeylonFile) position.getFile();
        Tree.CompilationUnit cu = needReadAccess(new Callable<Tree.CompilationUnit>() {
            @Override
            public Tree.CompilationUnit call() throws Exception {
                return file.getCompilationUnit();
            }
        });
        Entry<? extends Integer, ? extends Node> location = getFirstValidLocation(cu,
                new IdeaDocument(file.getViewProvider().getDocument()), position.getLine() + 1);

        if (location != null) {
            EventRequestManager rm = ((RequestManagerImpl) process.getRequestsManager()).getVMRequestManager();
            ClassPrepareRequest req = rm.createClassPrepareRequest();
            req.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
            req.addSourceNameFilter(position.getFile().getName());
            ((RequestManagerImpl) process.getRequestsManager()).registerRequestInternal(requestor, req);

            return req;
        }
        return null;
    }

    private void checkCeylonFile(@NotNull SourcePosition position) throws NoDataException {
        if (!(position.getFile() instanceof CeylonFile)) {
            throw NoDataException.INSTANCE;
        }
    }

    @Nullable
    private String findClassBySourcePosition(SourcePosition position) {
        CeylonPsi.DeclarationPsi declaration = getParentOfType(position.getElementAt(), CeylonPsi.ClassDefinitionPsi.class, CeylonPsi.InterfaceDefinitionPsi.class);
        boolean useUnderscoreSuffix = false;

        if (declaration == null) {
            declaration = getParentOfType(position.getElementAt(), CeylonPsi.ObjectDefinitionPsi.class);
            useUnderscoreSuffix = true;
        }
        if (declaration == null) {
            declaration = getParentOfType(position.getElementAt(), CeylonPsi.MethodDefinitionPsi.class);
            useUnderscoreSuffix = true;
        }

        if (declaration != null && declaration.getCeylonNode().getDeclarationModel() != null) {
            Declaration model = declaration.getCeylonNode().getDeclarationModel();
            String cls = model.getQualifiedNameString().replace("::", ".") + (useUnderscoreSuffix ? "_" : "");

            if (model instanceof Interface) {
                cls += "$impl";
            }
            System.out.println(String.format("Source position %s:%d => %s", position.getFile().getName(), position.getLine(), cls));
            return cls;
        }

        System.out.println(String.format("Can't find classes for source position %s:%d", position.getFile().getName(), position.getLine()));

        return null;
    }
}
