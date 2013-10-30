
package org.intellij.plugins.ceylon.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.util.text.CharSequenceReader;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.antlr.runtime.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This is a wrapper for PsiBuilder; it:
 * <ul>
 * <li>produces tokens from the original text (by creating a separate TokenStream),
 * <li>simultaneously advances the PsiBuilder (and does rollbacks when necessary)
 * </ul>
 *
 * Basically this uses PsiBuilder markers only for rewinds (roll-backs) since this is the only mechanism in PsiBuilder
 * that enables this.
 */
public class PsiBuilderTokenStream implements TokenStream {
    private PsiBuilder psiBuilder;
    private List<PsiBuilder.Marker> markers = new ArrayList<>();
    private Map<Integer, Integer> myToAnltr = new HashMap<>();
    private final TokenStream delegate;
    // After a seek, the token psi and antlr token streams are out of sync, until a rewind is called.
    private boolean afterSeek = false;

    public PsiBuilderTokenStream(PsiBuilder psiBuilder) {
        this.psiBuilder = psiBuilder;
        try {
            // todo: BufferedTokenStream handles whitespace differently.
            delegate = new CommonTokenStream(new CeylonLexer(new ANTLRReaderStream(new CharSequenceReader(psiBuilder.getOriginalText()))));
        } catch (IOException e) {
            throw new RuntimeException("Error creating lexer", e);
        }
    }

    public int LA(int i) {
        return delegate.LA(i);
    }

    protected PsiBuilder getPsiBuilder() {
        return psiBuilder;
    }

    public Token LT(int k) {
        return delegate.LT(k);
    }

    @Override public int range() {
        return delegate.range();
    }

    public Token get(int i) {
        return delegate.get(i);
    }

    public TokenSource getTokenSource() {
        return delegate.getTokenSource();
    }

    public String toString(int start, int stop) {
        return delegate.toString(start, stop);
    }

    public String toString(Token start, Token stop) {
        return delegate.toString(start, stop);
    }

    public void consume() {
        delegate.consume();
        if (!afterSeek) {
            psiBuilder.advanceLexer();
        }
    }

    protected boolean isAfterSeek() {
        return afterSeek;
    }

    public int index() {
        return delegate.index();
    }

    public int mark() {
        final int antlrMark = delegate.mark();
        markers.add(psiBuilder.mark());
        final int myMark = markers.size() - 1;
        myToAnltr.put(myMark, antlrMark);
        return myMark;
    }

    public void seek(int index) {
        delegate.seek(index);
        afterSeek = true;
    }

    public void rewind(int markerIndex) {
        // IIUC this should release/pop the marker at markerIndex.
        delegate.rewind(myToAnltr.get(markerIndex));
        markers.get(markerIndex).rollbackTo();
        afterSeek = false;
    }

    public void rewind() {
        delegate.rewind();
        rewind(markers.size() - 1);
        // note that this should leave the marker (or remove and create a new one)
        mark();
        afterSeek = false;
    }

    public void release(int markerIndex) {
        delegate.release(myToAnltr.get(markerIndex));
        markers.remove(markerIndex).drop();
    }

    public int size() {
        return delegate.size();
    }

    public String getSourceName() {
        return delegate.getSourceName();
    }
}
