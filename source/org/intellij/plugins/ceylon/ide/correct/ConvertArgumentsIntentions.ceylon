import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}
import com.redhat.ceylon.ide.common.correct {
    convertToNamedArgumentsQuickFix,
    convertToPositionalArgumentsQuickFix
}

shared class ConvertToNamedArgumentsIntention() extends AbstractIntention() {

    familyName => "Convert to named arguments";

    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => convertToNamedArgumentsQuickFix.addProposal(data, offset);

}

shared class ConvertToPositionalArgumentsIntention() extends AbstractIntention() {

    familyName => "Convert to positional arguments";

    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => convertToPositionalArgumentsQuickFix.addProposal(data, offset);

}
