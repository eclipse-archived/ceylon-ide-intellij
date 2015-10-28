import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonTreeUtil
}
import com.intellij.openapi.\imodule {
    IJModule=Module
}
import com.intellij.openapi.editor {
    Document
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    InsertEdit,
    TextEdit,
    TextChange,
    IdeaDocumentChanges
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}
import com.intellij.openapi.util {
    TextRange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}
import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}
import ceylon.interop.java {
    javaClass
}

shared object ideaModuleImportUtils
        extends AbstractModuleImportUtil<CeylonFile,IJModule,Document,InsertEdit,TextEdit,TextChange>()
        satisfies IdeaDocumentChanges {
    
    shared actual Character getChar(Document doc, Integer offset) {
        return doc.getText(TextRange.from(offset, 1)).first else ' ';
    }
    
    shared actual Integer getEditOffset(TextChange change) => 0;
    
    shared actual [CeylonFile, Tree.CompilationUnit, PhasedUnit]
    getUnit(IJModule project, Module mod) {
        value tc = project.getComponent(javaClass<ITypeCheckerProvider>()).typeChecker;
        value pu = tc.getPhasedUnitFromRelativePath(mod.unit.relativePath);
        value file = CeylonTreeUtil.getDeclaringFile(pu.unit, project.project);
        
        assert(is CeylonFile file);
        
        return [file, pu.compilationUnit, pu];
    }
    
    shared actual Indents<Document> indents => ideaIndents;
    
    shared actual TextChange newTextChange(String desc, CeylonFile file)
            => TextChange(file.viewProvider.document);
    
    shared actual void performChange(TextChange change) {
        change.apply();
    }
    
    
    
}