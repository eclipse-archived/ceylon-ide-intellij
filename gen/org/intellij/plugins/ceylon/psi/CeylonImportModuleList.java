// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonImportModuleList extends CeylonCompositeElement {

  @NotNull
  List<CeylonAnnotations> getAnnotationsList();

  @NotNull
  List<CeylonCompilerAnnotations> getCompilerAnnotationsList();

  @NotNull
  List<CeylonImportModule> getImportModuleList();

}
