import com.redhat.ceylon.ide.common.platform {
    DocumentServices,
    CommonDocument
}
import com.intellij.psi.codeStyle {
    CodeStyleSettings
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}

object ideaDocumentServices satisfies DocumentServices {
    createTextChange(String desc, CommonDocument|PhasedUnit input)
            => IdeaTextChange(input);
    
    createCompositeChange(String desc) => IdeaCompositeChange();
    
    // TODO take the settings from the current project
    indentSpaces => CodeStyleSettings().indentOptions?.indentSize else 4;
    
    indentWithSpaces => true;
}