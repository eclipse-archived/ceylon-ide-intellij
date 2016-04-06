import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    IJModule=Module
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker.context {
    TypecheckerUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    InsertEdit,
    TextEdit,
    TextChange,
    IdeaDocumentChanges
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonTreeUtil
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}

shared object ideaModuleImportUtils
        extends AbstractModuleImportUtil<CeylonFile,IJModule,Document,InsertEdit,TextEdit,TextChange>()
        satisfies IdeaDocumentChanges {
    
    getChar(Document doc, Integer offset) => doc.getText(TextRange.from(offset, 1)).first else ' ';
    
    shared actual Integer getEditOffset(TextChange change) => 0;
    
    shared actual [CeylonFile, Tree.CompilationUnit, TypecheckerUnit]
    getUnit(IJModule ijModule, Module mod) {
        value tc = ijModule.getComponent(javaClass<ITypeCheckerProvider>()).typeChecker;
        value pu = tc.getPhasedUnitFromRelativePath(mod.unit.relativePath);
        value file = CeylonTreeUtil.getDeclaringFile(pu.unit, ijModule.project);
        
        assert(is CeylonFile file);
        
        return [file, pu.compilationUnit, pu.unit];
    }
    
    shared actual Indents<Document> indents => ideaIndents;
    
    shared actual TextChange newTextChange(String desc, CeylonFile file)
            => TextChange(file.viewProvider.document);
    
    shared actual void performChange(TextChange change) {
        change.apply();
    }
    
    shared actual void gotoLocation(TypecheckerUnit unit, Integer offset,
        Integer length) {
        
        // TODO?
    }
}
