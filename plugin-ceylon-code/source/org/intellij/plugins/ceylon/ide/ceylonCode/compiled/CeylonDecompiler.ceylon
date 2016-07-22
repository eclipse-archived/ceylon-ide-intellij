import com.intellij.psi.compiled {
    ClassFileDecompilers
}

import org.intellij.plugins.ceylon.ide.ceylonCode.compiled {
    classFileDecompilerUtil
}

shared class CeylonDecompiler() extends ClassFileDecompilers.Full() {
    stubBuilder = CeylonClsStubBuilder();
    createFileViewProvider = CeylonClassFileFileViewProvider;
    accepts = classFileDecompilerUtil.isCeylonCompiledFile;
}
