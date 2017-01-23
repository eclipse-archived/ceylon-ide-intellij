import com.intellij.psi.compiled {
    ClassFileDecompilers
}

import org.intellij.plugins.ceylon.ide.compiled {
    classFileDecompilerUtil
}

"This decompiler tries to determine if a class was compiled by the Ceylon compiler.
 It will also ignore “internal“ classes ($impl, anonymous classes etc)."
shared class CeylonDecompiler() extends ClassFileDecompilers.Full() {
    stubBuilder = CeylonClsStubBuilder();
    createFileViewProvider = CeylonClassFileFileViewProvider;
    accepts = classFileDecompilerUtil.isCeylonCompiledFile;
}
