package org.intellij.plugins.ceylon.ide.debugger;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;

public class CeylonPositionManager implements PositionManager {

    private DebugProcess process;

    public CeylonPositionManager(DebugProcess process) {
        this.process = process;
    }

    @Nullable
    @Override
    public SourcePosition getSourcePosition(Location location) throws NoDataException {
        if (location == null) {
            throw NoDataException.INSTANCE;
        }

        PsiFile psiFile = getPsiFileByLocation(process.getProject(), location);
        if (psiFile == null) throw NoDataException.INSTANCE;

        int lineNumber = calcLineIndex(location);
        if (lineNumber < 0) {
            throw NoDataException.INSTANCE;
        }
        return SourcePosition.createFromLine(psiFile, lineNumber);
    }

    @Nullable
    private PsiFile getPsiFileByLocation(final Project project, final Location location) {
        if (location == null) return null;

        final ReferenceType refType = location.declaringType();
        if (refType == null) return null;

        try {
            for (PsiFile file : FilenameIndex.getFilesByName(project, refType.sourceName(), GlobalSearchScope.projectScope(project))) {
                if (file instanceof CeylonFile) {
                    return file;
                }
            }
        } catch (AbsentInformationException e) {
            // intentionally empty
        }

        return null;
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
        String cls = findClassBySourcePosition(position);

        return cls == null ? null : process.getRequestsManager().createClassPrepareRequest(requestor, cls);
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

        if (declaration != null) {
            String cls = declaration.getCeylonNode().getDeclarationModel().getQualifiedNameString().replace("::", ".") + (useUnderscoreSuffix ? "_" : "");

            System.out.println(String.format("Source position %s:%d => %s", position.getFile().getName(), position.getLine(), cls));
            return cls;
        }

        System.out.println(String.format("Can't find classes for source position %s:%d", position.getFile().getName(), position.getLine()));

        return null;
    }
}
