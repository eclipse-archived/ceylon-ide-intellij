// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface CeylonCompilationUnit extends CeylonCompositeElement {

  @Nullable
  CeylonAnnotations getAnnotations();

  @NotNull
  List<CeylonCompilerAnnotations> getCompilerAnnotationsList();

  @NotNull
  List<CeylonDeclaration> getDeclarationList();

  @Nullable
  CeylonImportList getImportList();

  @Nullable
  CeylonModuleDescriptor getModuleDescriptor();

  @Nullable
  CeylonPackageDescriptor getPackageDescriptor();

}
