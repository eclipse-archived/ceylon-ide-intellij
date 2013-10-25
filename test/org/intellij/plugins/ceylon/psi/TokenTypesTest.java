package org.intellij.plugins.ceylon.psi;

import org.junit.Test;

/**
 * @author Matija Mazi <br/>
 */
public class TokenTypesTest {
    @Test
    public void testTokens() throws Exception {
        assert TokenTypes.byIElementType.size() >= 129 : TokenTypes.byIElementType.size();
        assert TokenTypes.byIElementType.size() < 200 : TokenTypes.byIElementType.size();
        assert TokenTypes.byIndex.size() >= 129 : TokenTypes.byIndex.size();
        assert TokenTypes.byIndex.size() < 200 : TokenTypes.byIndex.size();

        assert TokenTypes.get(CeylonTokens.IMPORT) == TokenTypes.IMPORT;
        try {
            TokenTypes.get(CeylonTypes.IMPORT);
            assert false : "An exception should be thrown.";
        } catch (IllegalArgumentException expected) { }
    }
}
