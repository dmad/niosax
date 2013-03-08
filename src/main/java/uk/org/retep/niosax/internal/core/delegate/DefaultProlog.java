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
package uk.org.retep.niosax.internal.core.delegate;

import org.xml.sax.SAXException;
import uk.org.retep.niosax.IllegalCharacterException;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.core.AbstractNioSaxParser;
import uk.org.retep.niosax.internal.core.ParserDelegate;
import uk.org.retep.niosax.internal.core.Prolog;
import uk.org.retep.niosax.internal.core.engine.CommentCDataStateEngine;
import static uk.org.retep.niosax.internal.helper.XmlSpec.*;

/**
 * {@link ParserDelegate} implementation for parsing the document's prolog.
 *
 * @author peter
 * @since 9.10
 */
public class DefaultProlog
        extends Prolog
{

    private State state;

    /**
     * Creates a new instance of {@link uk.org.retep.niosax.internal.core.delegate.DefaultProlog}
     * @param parser The containing parser
     * @return instance
     */
    public static Prolog delegate( final AbstractNioSaxParser parser )
    {
        return new DefaultProlog( parser );
    }

    private DefaultProlog( final AbstractNioSaxParser parser )
    {
        super( parser );
        state = State.PROLOG;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void parse( final NioSaxSource source )
            throws SAXException
    {
        final char c = source.decode();
        if( source.isValid( c ) )
        {
            state = state.parse( this, source, c );
        }
    }

    /**
     * State engine to handle the prolog.
     * 
     * This is not a {@link StateEngine} because we dont extend {@link StateEngineDelegate}
     */
    private enum State
    {

        /**
         * Running within the prolog, followed by ELEMENT only.
         */
        PROLOG
        {

            @Override
            public State parse( final DefaultProlog p,
                                final NioSaxSource s,
                                final char c )
                    throws SAXException
            {
                if( c == '<' )
                {
                    return ELEMENT;
                }
                else if( isWhitespace( c ) )
                {
                    // Ignore
                    return PROLOG;
                }
                else
                {
                    throw new IllegalCharacterException();
                }
            }
        },
        /**
         * Start of an element, comment or processing instruction.
         * Followed by PROLOG or COMMENT1
         */
        ELEMENT
        {

            @Override
            public State parse( final DefaultProlog p,
                                final NioSaxSource s,
                                final char c )
                    throws SAXException
            {
                if( c == '!' )
                {
                    // A comment or cdata
                    CommentCDataStateEngine.delegate( p );
                }
                else if( c == '?' )
                {
                    // processing instruction
                    ProcessingInstruction.delegate( p );
                }
                else
                {
                    // Start a root element, forwarding the char as it's the
                    // first char of the element
                    Element.delegate( p ).parse( s, c );
                }

                // Back to prolog
                return PROLOG;
            }
        };

        /**
         * Called by {@link uk.org.retep.niosax.internal.core.delegate.DefaultProlog}
         * @param p {@link uk.org.retep.niosax.internal.core.delegate.DefaultProlog}
         * @param c char to parse
         * @return The next state
         * @throws org.xml.sax.SAXException if c is invalid
         */
        public abstract State parse( final DefaultProlog p,
                                     final NioSaxSource s,
                                     final char c )
                throws SAXException;
    }
}
