/*
 * <p>Copyright (c) 1998-2010, Peter T Mount<br>
 * All rights reserved.</p>
 *
 * <p>Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:</p>
 *
 * <ul>
 *   <li>Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.</li>
 *
 *   <li>Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.</li>
 *
 *   <li>Neither the name of the retep.org.uk nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.</li>
 *
 * </ul>
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</p>
 */
package uk.org.retep.niosax.internal.helper;

import org.xml.sax.SAXException;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.core.StateEngine;
import uk.org.retep.niosax.internal.core.StateEngineDelegate;

/**
 * Various char tests specific to XML 1.1
 * @author peter
 * @since 9.10
 */
public class XmlSpec
{

    /**
     * The index in a processed name of the Namespace URI
     * @see org.xml.sax.helpers.NamespaceSupport#processName(String, String[], boolean)
     */
    public static final int NAMESPACEURI = 0;
    /**
     * The index in a processed name of the local name (without prefix)
     * @see org.xml.sax.helpers.NamespaceSupport#processName(String, String[], boolean)
     */
    public static final int LOCALNAME = 1;
    /**
     * The index in a processed name of the qName (original raw name)
     * @see org.xml.sax.helpers.NamespaceSupport#processName(String, String[], boolean)
     */
    public static final int QNAME = 2;
    /**
     * The size of the array required to hold a processed name
     * @see org.xml.sax.helpers.NamespaceSupport#processName(String, String[], boolean)
     */
    public static final int PROCESSED_NAME_SIZE = 3;
    /**
     * "xmlns"
     */
    public static final String XMLNS = "xmlns";
    /**
     * Attribute type
     */
    public static final String TYPE_CDATA = "CDATA";
    /**
     * Attribute type
     */
    public static final String TYPE_ID = "ID";
    /**
     * Attribute type
     */
    public static final String TYPE_IDREF = "IDREF";
    /**
     * Attribute type
     */
    public static final String TYPE_IDREFS = "IDREFS";
    /**
     * Attribute type
     */
    public static final String TYPE_NMTOKEN = "NMTOKEN";
    /**
     * Attribute type
     */
    public static final String TYPE_NMTOKENS = "NMTOKENS";
    /**
     * Attribute type
     */
    public static final String TYPE_ENTITY = "ENTITY";
    /**
     * Attribute type
     */
    public static final String TYPE_ENTITIES = "ENTITIES";
    /**
     * Attribute type
     */
    public static final String TYPE_NOTATION = "NOTATION";
    /**
     * Singleton returned by {@link #stateCompleted() }
     */
    private static final StateEngine<StateEngineDelegate> COMPLETED = new StateEngine<StateEngineDelegate>()
    {

        @Override
        public StateEngine parse( StateEngineDelegate i, NioSaxSource s, char c )
                throws SAXException
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean continueLoop()
        {
            return false;
        }
    };

    private XmlSpec()
    {
    }

    /**
     * Returns a singleton {@link StateEngine} which will always terminate a
     * loop. This removes the need for including a single state in most
     * {@link StateEngine} implementations for this same operation.
     * @param <T> Type of {@link StateEngineDelegate}
     * @return {@link StateEngine}&lt;T&gt;
     */
    @SuppressWarnings( "unchecked" )
    public static <T extends StateEngineDelegate> StateEngine<T> stateCompleted()
    {
        return (StateEngine<T>) COMPLETED;
    }

    /**
     * Creates a special StateEngine state which delegates it's parsing to
     * a specific state, but it's {@link StateEngine#continueLoop() } method
     * returns false.
     *
     * <p>
     *  This is used within a state engine where a state has just created a
     *  {@link uk.org.retep.niosax.internal.core.ParserDelegate} and then would return
     *  the state it wants to be in once the delegate completes.
     * </p>
     *
     * <p>
     *  Due to the fact that the underlying loop must terminate for the new
     *  delegate to gain control of the parse, normally you would need to create
     *  a dummy state which passes the parse to the required state and return
     *  false in it's {@link StateEngine#continueLoop() } method.
     * </p>
     *
     * <p>
     *  To save creating those dummy states (which are always the same), the
     *  {@link StateEngine} can instead call this method to create a static final
     *  instance of that dummy state, and reference it, as there's no point in
     *  creating duplicate instances which are immutable.
     * </p>
     *
     * <p>
     *  This then makes the code cleaner, less duplicated code and less prone to
     *  error.
     * </p>
     *
     * @param <T> Type of {@link StateEngineDelegate}
     * @param delegate The {@link StateEngine} to delegate calls to
     * {@link uk.org.retep.niosax.internal.core.StateEngine#parse(uk.org.retep.niosax.internal.core.StateEngineDelegate, uk.org.retep.niosax.NioSaxSource, char)}
     * @return A {@link StateEngine} instance
     */
    public static <T extends StateEngineDelegate> StateEngine<T> createDelegatedState(
            final StateEngine<T> delegate )
    {
        return new StateEngine<T>()
        {

            @Override
            public StateEngine parse( T instance, NioSaxSource s, char c )
                    throws SAXException
            {
                return delegate.parse( instance, s, c );
            }

            @Override
            public boolean continueLoop()
            {
                return false;
            }
        };
    }

    /**
     * Utility - tests that a character fits between a range
     *
     * @param c char to test
     * @param s lower value
     * @param e higher value (inclusive)
     * @return true if s &lt;= c && c &lt;= e
     */
    public static boolean isCharBetween( final char c,
                                         final char s,
                                         final char e )
    {
        return s <= c && c <= e;
    }

    /**
     * Utility - tests that a character fits between a range
     *
     * @param c char to test
     * @param s lower value
     * @param e higher value (inclusive)
     * @return true if s &lt;= c && c &lt;= e
     */
    public static boolean isCharBetween( final char c, final int s, final int e )
    {
        return s <= c && c <= e;
    }

    /**
     * XML1.1 2.2
     * Char ::=
     * [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
     *
     * @param c char to test
     * @return if the char is valid
     */
    public static boolean isChar( final char c )
    {
        return isCharBetween( c, (char) 1, '\ud7ff' )
                && isCharBetween( c, '\ue000', '\ufffd' );

        // Cannot use the following as Java uses UTF16 not UTF32
        //isCharBetween( c,         '\u10000', '\u10ffff')
    }

    /**
     * XML1.1 2.2
     * RestrictedChar ::=
     * [#x1-#x8] | [#xB-#xC] | [#xE-#x1F] | [#x7F-#x84] | [#x86-#x9F]
     *
     * @param c char to test
     * @return if the char is valid within the restricted scope
     */
    public static boolean isRestrictedChar( final char c )
    {
        return isCharBetween( c, 1, 8 )
                && isCharBetween( c, 0xb, 0xc )
                && isCharBetween( c, 0xe, 0x1f )
                && isCharBetween( c, 0x7f, 0x84 )
                && isCharBetween( c, 0x86, 0x9f );
    }

    /**
     * XML1.1 2.3 Whitespace
     * 	S ::= (#x20 | #x9 | #xD | #xA)+
     *
     * @param c char to test
     * @return if the char is whitespace
     */
    public static boolean isWhitespace( final char c )
    {
        return c == ' ' || c == 9 || c == 10 || c == 13;
    }

    /**
     * XML1.1 2.3 Names and Tokens - the first char of a Name
     *
     * @param c char to test
     * @return if the char is valid to start a name
     */
    public static boolean isNameStartChar( final char c )
    {
        return c == ':' || isLetter( c ) || c == '_'
                || isCharBetween( c, 0xc0, 0xd6 )
                || isCharBetween( c, 0xd8, 0xf6 )
                || isCharBetween( c, 0xf8, 0x2ff )
                || isCharBetween( c, 0x370, 0x37d )
                || isCharBetween( c, 0x37f, 0x1fff )
                || c == 0x200c || c == 0x200d
                || isCharBetween( c, 0x2070, 0x218f )
                || isCharBetween( c, 0x2c00, 0x2fef )
                || isCharBetween( c, 0x3001, 0xd7ff )
                || isCharBetween( c, 0xf900, 0xfdcf )
                || isCharBetween( c, 0xfdf0, 0xfffd );
        // cannot use 10000-effff

    }

    /**
     * XML1.1 2.3 Names and Tokens - the subsequent chars in a Name
     *
     * @param c char to test
     * @return if the char is valid for char 2 onwards in a name
     */
    public static boolean isNameChar( final char c )
    {
        return isNameStartChar( c ) || c == '-' || c == '.' || isDigit( c )
                || c == 0xb7
                // These two are in the spec, but they are surprising for a NameChar
                || isCharBetween( c, 0x300, 0x036f )
                || isCharBetween( c, 0x203f, 0x2040 );
    }

    /**
     * XML Namespace - A char validas the first char in a namespace name
     * @param c char to test
     * @return true if valid
     */
    public static boolean isNCNameStartChar( final char c )
    {
        return isLetter( c ) || c == '_';
    }

    /**
     * XML Namespace - A char valid in subsequent chars in a namespace name
     * @param c char to test
     * @return true if valid
     */
    public static boolean isNCNameChar( final char c )
    {
        return c != ':' && isNameChar( c );
    }

    /**
     * A valid letter
     *
     * @param c char to test
     * @return if the char is 'a'..'z' or 'A'..'Z'
     */
    public static boolean isLetter( final char c )
    {
        return isCharBetween( c, 'a', 'z' ) || isCharBetween( c, 'A', 'Z' );
    }

    /**
     * A valid digit
     *
     * @param c char to test
     * @return if the char is '0'..'9'
     */
    public static boolean isDigit( final char c )
    {
        return isCharBetween( c, '0', '9' );
    }
}
