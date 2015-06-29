import ceylon.interop.java {
    javaString
}

import com.intellij.codeInsight.completion {
    InsertionContext
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.extensions {
    ExtensionPointName
}
import com.intellij.psi {
    PsiFile,
    PsiDocumentManager
}
import com.intellij.psi.codeStyle {
    CodeStyleManager
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.completion {
    FindImportNodeVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

ExtensionPointName<ImportHandler> importHandlerEpName = ExtensionPointName.create<ImportHandler>("org.intellij.plugins.ceylon.ide.importHandler");

shared abstract class ImportHandler() {
    
    shared ExtensionPointName<ImportHandler> epName => importHandlerEpName;
    
    shared formal Tree.CompilationUnit getCompilationUnit(PsiFile file);
    
    shared void importDeclaration(Declaration decl, InsertionContext ctx) {
        value cu = getCompilationUnit(ctx.file);
        value doc = ctx.document;
        value pkg = decl.unit.\ipackage.qualifiedNameString;
        value visitor = FindImportNodeVisitor(pkg);
        cu.visit(visitor);
        
        if (exists node = visitor.result) {
            value imtl = node.importMemberOrTypeList;
            
            if (exists w = imtl.importWildcard) {
                // Do nothing
            } else {
                Integer insertPosition = getBestImportMemberInsertPosition(node);
                insertAndFormatImport(ctx.file, doc, "," + decl.name, insertPosition);
                
            }
        } else {
            value insertPosition = (cu.importList?.stopIndex?.intValue() else -1) + 1;
            value prefix = if (insertPosition == 0) then "" else "\n";
            insertAndFormatImport(ctx.file, doc, "``prefix``import ``pkg`` {``decl.name``}\n", insertPosition);
        }
        
    }
    
    void insertAndFormatImport(PsiFile file, Document doc, String imp, Integer position) {
        doc.insertString(position, javaString(imp));

        PsiDocumentManager.getInstance(file.project).commitDocument(doc);
        
        CodeStyleManager.getInstance(file.project).reformatRange(file, position, position + imp.size);
    }
    
    Integer getBestImportMemberInsertPosition(Tree.Import importNode) {
        value imtl = importNode.importMemberOrTypeList;
        if (exists w = imtl.importWildcard) {
            return w.startIndex.intValue();
        }
        else {
            value imts = imtl.importMemberOrTypes;
            if (imts.empty) {
                return imtl.startIndex.intValue() + 1;
            }
            else {
                return imts.get(imts.size()-1).stopIndex.intValue() + 1;
            }
        }
    }
}