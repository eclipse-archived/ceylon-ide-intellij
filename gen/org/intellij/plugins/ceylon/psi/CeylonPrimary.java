// This is a generated file. Not intended for manual editing.
package org.intellij.plugins.ceylon.psi;

import java.util.List;
import org.jetbrains.annotations.*;

public interface CeylonPrimary extends CeylonCompositeElement {

  @NotNull
  List<CeylonArguments> getArgumentsList();

  @NotNull
  CeylonBase getBase();

  @NotNull
  List<CeylonIndexOrIndexRange> getIndexOrIndexRangeList();

  @NotNull
  List<CeylonQualifiedReference> getQualifiedReferenceList();

}
