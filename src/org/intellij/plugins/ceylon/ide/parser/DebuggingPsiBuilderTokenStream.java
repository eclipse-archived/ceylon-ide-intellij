package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.intellij.plugins.ceylon.ide.psi.TokenTypes;

import java.util.Objects;


// NOTE! The Javadocs are copied frome Antlr 4 (because they are much more extensive and clear than in Antlr 3.4) so they may be inaccurate!

/**
 * This is useful for debugging the lexer/parser during development, not to be used otherwise.
 */
public class DebuggingPsiBuilderTokenStream extends PsiBuilderTokenStream {
//    private CommonToken currentToken = null;

    public DebuggingPsiBuilderTokenStream(PsiBuilder psiBuilder) {
        super(psiBuilder);
    }

    /**
     * Gets the value of the symbol at offset {@code i} from the current
     * position. When {@code i==1}, this method returns the value of the current
     * symbol in the stream (which is the next symbol to be consumed). When
     * {@code i==-1}, this method returns the value of the previously read
     * symbol in the stream. It is not valid to call this method with
     * {@code i==0}, but the specific behavior is unspecified because this
     * method is frequently called from performance-critical code.
     * <p/>
     * This method is guaranteed to succeed if any of the following are true:
     * <p/>
     * <ul>
     * <li>{@code i>0}</li>
     * <li>{@code i==-1} and {@link #index index()} returns a value greater
     * than the value of {@code index()} after the stream was constructed
     * and {@code LA(1)} was called in that order. Specifying the current
     * {@code index()} relative to the index after the stream was created
     * allows for filtering implementations that do not return every symbol
     * from the underlying source. Specifying the call to {@code LA(1)}
     * allows for lazily initialized streams.</li>
     * <li>{@code LA(i)} refers to a symbol consumed within a marked region
     * that has not yet been released.</li>
     * </ul>
     * <p/>
     * If {@code i} represents a position at or beyond the end of the stream,
     * this method returns {@link #EOF}.
     * <p/>
     * The return value is unspecified if {@code i&lt;0} and fewer than {@code -i}
     * calls to {@link #consume consume()} have occurred from the beginning of
     * the stream before calling this method.
     *
     * @throws UnsupportedOperationException if the stream does not support
     *                                       retrieving the value of the specified symbol
     */
    public int LA(int i) {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.LA  %s", i);
        final int superTokenType = super.LA(i);
        if (!isAfterSeek()) {
            IElementType elementType = getPsiBuilder().lookAhead(i - 1);
            final int psiTokenType = getTokenTypeIntValue(elementType);
            assertEquals(psiTokenType, superTokenType);
        }
        assertEqualNext();
        return superTokenType;
    }

    /**
     * Get the {@link org.antlr.runtime.Token} instance associated with the value returned by
     * {@link #LA LA(k)}. This method has the same pre- and post-conditions as
     * {@link org.antlr.runtime.IntStream#LA}. In addition, when the preconditions of this method
     * are met, the return value is non-null and the value of
     * {@code LT(k).getType()==LA(k)}.
     *
     * @see org.antlr.runtime.IntStream#LA
     */
    public Token LT(int k) {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.LT  %s", k);
        assertEqualNext();
        final Token delegateToken = super.LT(k);
/*
        if (k == 1) {
            assertEquals(getCurrentToken(), delegateToken);
        }
*/
        return delegateToken;
    }

    private <T> void assertEquals(T my, T antlr) {
        if (!Objects.equals(my, antlr)) {
            assert false : String.format("Objects are different: my = %s -- antlr = %s", my, antlr);
        } else {
            print("Objects equal: my = %s -- antlr = %s", my, antlr);
        }
    }

/*
    private Token getCurrentToken() {
        if (currentToken == null) {
            buildCurrentToken();
        }
        return currentToken;
    }
*/

    @Override
    public int range() {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.range");
        return super.range();
//        return currentToken == null ? 0 : 1;
    }

    /**
     * Gets the {@link org.antlr.runtime.Token} at the specified {@code index} in the stream. When
     * the preconditions of this method are met, the return value is non-null.
     * <p/>
     * The preconditions for this method are the same as the preconditions of
     * {@link org.antlr.runtime.IntStream#seek}. If the behavior of {@code seek(index)} is
     * unspecified for the current state and given {@code index}, then the
     * behavior of this method is also unspecified.
     * <p/>
     * The symbol referred to by {@code index} differs from {@code seek()} only
     * in the case of filtering streams where {@code index} lies before the end
     * of the stream. Unlike {@code seek()}, this method does not adjust
     * {@code index} to point to a non-ignored symbol.
     *
     * @throws IllegalArgumentException      if {code index} is less than 0
     * @throws UnsupportedOperationException if the stream does not support
     *                                       retrieving the token at the specified index
     */
    public Token get(int i) {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.get  %s", i);
        return super.get(i);
//        throw new UnsupportedOperationException();
    }

    /**
     * Gets the underlying {@link org.antlr.runtime.TokenSource} which provides tokens for this
     * stream.
     */
    public TokenSource getTokenSource() {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.getTokenSource");
        return super.getTokenSource();
//        throw new UnsupportedOperationException();
    }

    public String toString(int start, int stop) {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.toString");
        return super.toString(start, stop);
//        throw new UnsupportedOperationException();
    }

    /**
     * Return the text of all tokens in this stream between {@code start} and
     * {@code stop} (inclusive).
     * <p/>
     * If the specified {@code start} or {@code stop} token was not provided by
     * this stream, or if the {@code stop} occurred before the {@code start}
     * token, the behavior is unspecified.
     * <p/>
     * For streams which ensure that the {@link org.antlr.runtime.Token#getTokenIndex} method is
     * accurate for all of its provided tokens, this method behaves like the
     * following code. Other streams may implement this method in other ways
     * provided the behavior is consistent with this at a high level.
     * <p/>
     * <pre>
     * TokenStream stream = ...;
     * String text = "";
     * for (int i = start.getTokenIndex(); i <= stop.getTokenIndex(); i++) {
     *   text += stream.get(i).getText();
     * }
     * </pre>
     *
     * @param start The first token in the interval to get text for.
     * @param stop  The last token in the interval to get text for (inclusive).
     * @return The text of all tokens lying between the specified {@code start}
     * and {@code stop} tokens.
     * @throws UnsupportedOperationException if this stream does not support
     *                                       this method for the specified tokens
     */
    public String toString(Token start, Token stop) {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.toString2");
        return super.toString(start, stop);
//        throw new UnsupportedOperationException();
    }

    /**
     * Consumes the current symbol in the stream. This method has the following
     * effects:
     * <p/>
     * <ul>
     * <li><strong>Forward movement:</strong> The value of {@link #index index()}
     * before calling this method is less than the value of {@code index()}
     * after calling this method.</li>
     * <li><strong>Ordered lookahead:</strong> The value of {@code LA(1)} before
     * calling this method becomes the value of {@code LA(-1)} after calling
     * this method.</li>
     * </ul>
     * <p/>
     * Note that calling this method does not guarantee that {@code index()} is
     * incremented by exactly 1, as that would preclude the ability to implement
     * filtering streams (e.g. {@link org.antlr.runtime.CommonTokenStream} which distinguishes
     * between "on-channel" and "off-channel" tokens).
     *
     * @throws IllegalStateException if an attempt is made to consume the the
     *                               end of the stream (i.e. if {@code LA(1)==}{@link #EOF EOF} before calling
     *                               {@code consume}).
     */
    public void consume() {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.consume");
        assertEqualNext();
        super.consume();
//        buildCurrentToken();
        assertEqualNext();
    }

    private void assertEqualNext() {
        // After a seek, the token streams are out of sync, until a rewind is called.
        if (!isAfterSeek()) {
            final int mytt = getTokenTypeIntValue(getPsiBuilder().getTokenType());
            assertEquals(mytt, super.LA(1));
            final String myText = getPsiBuilder().getTokenText();
            final String antlrTxt = super.LT(1).getText();
            if (mytt != TokenTypes.EOF.getValue()) {
                assertEquals(myText, antlrTxt);
            } else {
                assert myText == null;
                assert antlrTxt.equals("<EOF>");
            }
        }
    }

    /*
    private void buildCurrentToken() {
        System.out.printf("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.buildCurrentToken" + "%n");
        currentToken = new CommonToken(getTokenTypeIntValue(psiBuilder.getTokenType()), psiBuilder.getTokenText());
    }
*/

    private int getTokenTypeIntValue(IElementType iElementType) {
//        System.out.printf("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.getTokenTypeIntValue  %s" + "%n", iElementType);
        if (iElementType == null) {
            return TokenTypes.EOF.getValue();
        }
        return TokenTypes.get(iElementType).getValue();
    }

    /**
     * Return the index into the stream of the input symbol referred to by
     * {@code LA(1)}.
     * <p/>
     * The behavior of this method is unspecified if no call to an
     * {@link org.antlr.runtime.IntStream initializing method} has occurred after this stream was
     * constructed.
     */
    public int index() {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.index");
        return super.index();
        //        return tokenIdx;
//        return psiBuilder.getCurrentOffset();
    }

    /*
    private int getAssertedIdx() {
        final int delegateIdx = delegate.index();
        assert tokenIdx == delegateIdx : String.format("Token indices differ. Antlr: %d, my: %d", delegateIdx, tokenIdx);
        return delegateIdx;
    }
*/


    /**
     * A mark provides a guarantee that {@link #seek seek()} operations will be
     * valid over a "marked range" extending from the index where {@code mark()}
     * was called to the current {@link #index index()}. This allows the use of
     * streaming input sources by specifying the minimum buffering requirements
     * to support arbitrary lookahead during prediction.
     * <p/>
     * The returned mark is an opaque handle (type {@code int}) which is passed
     * to {@link #release release()} when the guarantees provided by the marked
     * range are no longer necessary. When calls to
     * {@code mark()}/{@code release()} are nested, the marks must be released
     * in reverse order of which they were obtained. Since marked regions are
     * used during performance-critical sections of prediction, the specific
     * behavior of invalid usage is unspecified (i.e. a mark is not released, or
     * a mark is released twice, or marks are not released in reverse order from
     * which they were created).
     * <p/>
     * The behavior of this method is unspecified if no call to an
     * {@link org.antlr.runtime.IntStream initializing method} has occurred after this stream was
     * constructed.
     * <p/>
     * This method does not change the current position in the input stream.
     * <p/>
     * The following example shows the use of {@link #mark mark()},
     * {@link #release release(mark)}, {@link #index index()}, and
     * {@link #seek seek(index)} as part of an operation to safely work within a
     * marked region, then restore the stream position to its original value and
     * release the mark.
     * <pre>
     * IntStream stream = ...;
     * int index = -1;
     * int mark = stream.mark();
     * try {
     *   index = stream.index();
     *   // perform work here...
     * } finally {
     *   if (index != -1) {
     *     stream.seek(index);
     *   }
     *   stream.release(mark);
     * }
     * </pre>
     *
     * @return An opaque marker which should be passed to
     * {@link #release release()} when the marked range is no longer required.
     */
    public int mark() {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.mark");
        assertEqualNext();
        final int mark = super.mark();
        assertEqualNext();
        return mark;
    }

    /**
     * Set the input cursor to the position indicated by {@code index}. If the
     * specified index lies past the end of the stream, the operation behaves as
     * though {@code index} was the index of the EOF symbol. After this method
     * returns without throwing an exception, the at least one of the following
     * will be true.
     * <p/>
     * <ul>
     * <li>{@link #index index()} will return the index of the first symbol
     * appearing at or after the specified {@code index}. Specifically,
     * implementations which filter their sources should automatically
     * adjust {@code index} forward the minimum amount required for the
     * operation to target a non-ignored symbol.</li>
     * <li>{@code LA(1)} returns {@link #EOF}</li>
     * </ul>
     * <p/>
     * This operation is guaranteed to not throw an exception if {@code index}
     * lies within a marked region. For more information on marked regions, see
     * {@link #mark}. The behavior of this method is unspecified if no call to
     * an {@link org.antlr.runtime.IntStream initializing method} has occurred after this stream
     * was constructed.
     *
     * @param index The absolute index to seek to.
     * @throws IllegalArgumentException      if {@code index} is less than 0
     * @throws UnsupportedOperationException if the stream does not support
     *                                       seeking to the specified index
     */
    public void seek(int index) {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.seek  %s", index);
        super.seek(index);
/*
        if (index < tokenIdx) {
            psiBuilder. //  This is a problem..
            throw new UnsupportedOperationException();
        }
        while (index > tokenIdx) {
            consume();
        }
*/
    }

    public void rewind(int markerIndex) {
        // IIUC this should release/pop the marker at markerIndex.
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.rewind  %s", markerIndex);
        super.rewind(markerIndex);
        assertEqualNext();
    }

    public void rewind() {
        print("org.intellij.plugins.ceylon.parser.PsiBuilderTokenStream.rewind ");
        super.rewind();
        assertEqualNext();
        // note that this should leave the marker (or remove and create a new one)
    }

    /**
     * This method releases a marked range created by a call to
     * {@link #mark mark()}. Calls to {@code release()} must appear in the
     * reverse order of the corresponding calls to {@code mark()}. If a mark is
     * released twice, or if marks are not released in reverse order of the
     * corresponding calls to {@code mark()}, the behavior is unspecified.
     * <p/>
     * For more information and an example, see {@link #mark}.
     *
     * @param marker A marker returned by a call to {@code mark()}.
     * @see #mark
     */
    public void release(int markerIndex) {
        super.release(markerIndex);
        assertEqualNext();
    }

    /**
     * Returns the total number of symbols in the stream, including a single EOF
     * symbol.
     *
     * @throws UnsupportedOperationException if the size of the stream is
     *                                       unknown.
     */
    public int size() {
        return super.size();
//        throw new UnsupportedOperationException();
    }

    /**
     * Gets the name of the underlying symbol source. This method returns a
     * non-null, non-empty string. If such a name is not known, this method
     * returns {@link #UNKNOWN_SOURCE_NAME}.
     */
    public String getSourceName() {
        return super.getSourceName();
    }

    private void print(String msgTemplate, Object... params) {
        System.out.printf("" + hashCode() + " " + msgTemplate + "%n", params);
    }
}
