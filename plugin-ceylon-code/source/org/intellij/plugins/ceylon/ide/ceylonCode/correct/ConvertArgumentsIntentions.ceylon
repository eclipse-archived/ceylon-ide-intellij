import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.redhat.ceylon.ide.common.correct {
    convertToNamedArgumentsQuickFix,
    convertToPositionalArgumentsQuickFix
}

shared class ConvertToNamedArgumentsIntention() extends AbstractIntention() {

    familyName => "Convert to Named Arguments";

    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => convertToNamedArgumentsQuickFix.addProposal(data, offset);

}

shared class ConvertToPositionalArgumentsIntention() extends AbstractIntention() {

    familyName => "Convert to Positional Arguments";

    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => convertToPositionalArgumentsQuickFix.addProposal(data, offset);

}
