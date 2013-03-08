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
package uk.org.retep.niosax.internal.core.engine;

import org.xml.sax.SAXException;
import uk.org.retep.niosax.IllegalCharacterException;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.core.StateEngine;
import uk.org.retep.niosax.internal.core.StateEngineDelegate;
import uk.org.retep.niosax.internal.core.ParserDelegate;

/**
 * {@link StateEngine} which determines if the content being parsed is a
 * comment or a cdata section, passing control to {@link CommentStateEngine}
 * or {@link CDataStateEngine} respectively.
 * 
 * @author peter
 */
public enum CommentCDataStateEngine
        implements StateEngine<StateEngineDelegate>
{

    /**
     * Check the first char to see what we are getting
     */
    START
    {

        /**
         * If the char is '-' then switches to {@link #COMMENT}
         * If the char is '[' then switches to {@link #CDATA1}
         *
         * @param e {@link StateEngineDelegate}
         * @param c char to parse
         * @return {@link StateEngine} to switch to
         * @throws org.xml.sax.SAXException if c is not the expected character
         */
        @Override
        public StateEngine parse( final StateEngineDelegate e,
                                  final NioSaxSource source,
                                  final char c )
                throws SAXException
        {
            if( c == '-' )
            {
                // Start of a comment?
                return COMMENT;
            }
            else if( c == '[' )
            {
                // Start of a CData?
                return CDATA1;
            }
            else
            {
                throw new IllegalCharacterException( c );
            }
        }
    },
    //
    // Found &lt;!- expecting another - to trigger a comment
    //
    COMMENT( '-', CommentStateEngine.NORMAL ),
    //
    // Found &lt;![ expecting CDATA[
    //
    // NOTE - These must be defined in reverse as forward refs cannot be
    // used in constructors
    CDATA6( '[', CDataStateEngine.START ),
    CDATA5( 'A', CDATA6 ),
    CDATA4( 'T', CDATA5 ),
    CDATA3( 'A', CDATA4 ),
    CDATA2( 'D', CDATA3 ),
    CDATA1( 'C', CDATA2 );
    /**
     * The expected char
     */
    private final char expect;
    /**
     * The next StateEngine if the expected char was found
     */
    private final StateEngine next;

    /**
     * Used by START only, as we are not expecting anything
     */
    private CommentCDataStateEngine()
    {
        expect = 0;
        next = null;
    }

    /**
     * For all enums other than START
     * @param expect char expected
     * @param next StateEngine to switch two if expected char seen
     */
    private CommentCDataStateEngine( final char expect, final StateEngine next )
    {
        this.expect = expect;
        this.next = next;
    }

    /**
     * Default, if the char is the expected one then switch state, if not then
     * throw {@link IllegalCharacterException}
     * 
     * @param e {@link StateEngineDelegate}
     * @param source {@link NioSaxSource} containing the input
     * @param c char to parse
     * @return {@link StateEngine} to switch to
     * @throws org.xml.sax.SAXException if c is not the expected character
     */
    @Override
    public StateEngine parse( final StateEngineDelegate e,
                              final NioSaxSource source,
                              final char c )
            throws SAXException
    {
        if( c == expect )
        {
            return next;
        }
        else
        {
            throw new IllegalCharacterException( c );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final boolean continueLoop()
    {
        return true;
    }

    /**
     * Returns a {@link StateEngineDelegate} which will handle a comment or CDATA
     *
     *
     * @param <P> The parent {@link ParserDelegate} type
     * @param parent the parent {@link ParserDelegate}
     * @return {@link StateEngineDelegate} instance
     */
    public static <P extends ParserDelegate> StateEngineDelegate delegate(
            final P parent )
    {
        return StateEngineDelegate.<P>delegate( parent, START );
    }
}
