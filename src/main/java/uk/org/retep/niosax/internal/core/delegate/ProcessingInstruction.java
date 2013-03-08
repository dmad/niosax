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
import uk.org.retep.niosax.internal.core.ParserDelegate;
import uk.org.retep.niosax.internal.core.Prolog;
import uk.org.retep.niosax.internal.core.StateEngine;
import uk.org.retep.niosax.internal.core.StateEngineDelegate;
import static uk.org.retep.niosax.internal.helper.XmlSpec.*;

/**
 * {@link ParserDelegate} for parsing processing instructions.
 *
 * <p>
 *  In normal use the instance returned by the
 *  {@link #delegate(uk.org.retep.niosax.internal.core.ParserDelegate) }
 *  method will handle just processing instructions. If the xml or DOCTYPE
 *  target is found then the parse will fail as the xml specification prohibits
 *  them outside of the prolog.
 * </p>
 *
 * <p>
 *  When the instance returned by the
 *  {@link #delegate(uk.org.retep.niosax.internal.core.Prolog) } method is used
 *  then in addition to processing instructions, the xml or DOCTYPE targets
 *  are possible so the instance will replace itself with the appropriate
 *  {@link ParserDelegate} to parse then. This instance should only be used
 *  by the {@link Prolog} as the XML specification only permits them there.
 * </p>
 *
 * @param <P> Type of parent {@link ParserDelegate}
 * @see #delegate(uk.org.retep.niosax.internal.core.ParserDelegate)
 * @see #delegate(uk.org.retep.niosax.internal.core.Prolog)
 * @author peter
 * @since 9.10
 */
public abstract class ProcessingInstruction<P extends ParserDelegate>
        extends StateEngineDelegate<P>
{

    private static final String XMLU = "XML";
    private static final String XMLL = "xml";
    private static final String DOCTYPE = "DOCTYPE";
    // Cache of the parsed target name
    private String target;

    /**
     * Delegate parsing from inside a {@link Prolog}.
     *
     * <p>
     *  The {@link ParserDelegate} will then handle processingInstructions
     *  xml and doctype declarations.
     * </p>
     *
     * @param doc {@link Prolog} we need a ProcessingInstruction for
     * @return instance which will be active within the parser
     * @see #delegate(uk.org.retep.niosax.internal.core.ParserDelegate)
     */
    public static ProcessingInstruction<Prolog> delegate( final Prolog doc )
    {
        return new ProcessingInstruction<Prolog>( doc )
        {

            @Override
            protected void xmlDecl()
                    throws SAXException
            {
                // Finish ourselves and switch to XMLDeclaration which will
                // replace this parser in the tree, hence the finish()
                finish();
                XMLDeclaration.delegate( getParent() );
            }

            @Override
            protected void docType()
                    throws SAXException
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Delegate parsing of a processing instruction from any {@link ParserDelegate}.
     *
     * <p>
     *  This is the generic implementation which allows a {@link uk.org.retep.niosax.internal.core.delegate.ProcessingInstruction}
     *  within any {@link ParserDelegate}. The {@link ParserDelegate} returned
     *  by this method will not permit either xml or DOCTYPE declarations as they
     *  are only permitted in the {@link Prolog}.
     * </p>
     * 
     * @param <P> Type of parent
     * @param parent parent {@link ParserDelegate}
     * @return instance which will be active within the parser
     * @see #delegate(uk.org.retep.niosax.internal.core.Prolog)
     */
    public static <P extends ParserDelegate> ProcessingInstruction<P> delegate(
            final P parent )
    {
        return new ProcessingInstruction<P>( parent )
        {

            @Override
            protected void xmlDecl()
                    throws SAXException
            {
                throw new SAXException( "Illegal XML declaration found" );
            }

            @Override
            protected void docType()
                    throws SAXException
            {
                throw new SAXException( "Illegal DOCTYPE declaration found" );
            }
        };
    }

    /**
     * Construct a ProcessingInstruction handler.
     * 
     * @param parent
     */
    private ProcessingInstruction( final P parent )
    {
        super( parent );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected StateEngine getInitialState()
    {
        return PIState.PITARGET;
    }

    /**
     * Used internally by PIState to store the target of the processingInstruction.
     */
    protected final void setTarget()
    {
        this.target = getAppendableString();
    }

    /**
     * Used internally by PIState to get the target of the processingInstruction.
     * @return processingInstruction target
     */
    protected final String getTarget()
    {
        return target;
    }

    /**
     * Handle a processingInstruction with the name XML or xml.
     *
     * <p>
     *  Most implementations must throw {@link org.xml.sax.SAXException} as this would be
     *  illegal outside of the prolog.
     * </p>
     *
     * @throws org.xml.sax.SAXException
     */
    protected abstract void xmlDecl()
            throws SAXException;

    /**
     * Handle a processingInstruction with the name DOCTYPE.
     *
     * <p>
     *  Most implementations must throw {@link org.xml.sax.SAXException} as this would be
     *  illegal outside of the prolog.
     * </p>
     *
     * @throws org.xml.sax.SAXException
     */
    protected abstract void docType()
            throws SAXException;

    private enum PIState
            implements StateEngine<ProcessingInstruction>
    {

        /**
         * Parsing the first character of the PITarget
         */
        PITARGET
        {

            @Override
            public StateEngine parse( final ProcessingInstruction p,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isNameStartChar( c ) )
                {
                    // Simply append and keep
                    p.append( c );
                    return PITARGET2;
                }
                else
                {
                    throw new IllegalCharacterException();
                }
            }
        },
        /**
         * Parsing subsequent chars in a PITarget
         */
        PITARGET2
        {

            @Override
            public StateEngine parse( final ProcessingInstruction p,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isWhitespace( c ) )
                {
                    // Set the target and move on to WHITESPACE mode if we have
                    // not delegated the parsing
                    p.setTarget();
                    return parsingDelegated( p ) ? stateCompleted() : WHITESPACE;
                }
                else if( c == '?' )
                {
                    // End the target and expect '>'
                    p.setTarget();
                    return PITARGET_QUERY;
                }
                else if( isNameStartChar( c ) )
                {
                    // Simply append and keep
                    p.append( c );
                    return this;
                }
                else
                {
                    throw new IllegalCharacterException();
                }
            }
        },
        /**
         * The name was terminated by ? so the next char must be &gt - everything
         * else is illegal
         */
        PITARGET_QUERY
        {

            @Override
            public StateEngine parse( final ProcessingInstruction p,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '>' )
                {
                    // If a pure pi then invoke as its not been delegated
                    if( !parsingDelegated( p ) )
                    {
                        p.getHandler().processingInstruction( p.getTarget(), "" );
                    }

                    return stateCompleted();
                }
                else
                {
                    throw new IllegalCharacterException();
                }
            }
        },
        /**
         * Whitespace between the target and data
         */
        WHITESPACE
        {

            @Override
            public StateEngine parse( final ProcessingInstruction p,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isWhitespace( c ) )
                {
                    return this;
                }
                else if( c == '?' )
                {
                    // Expect '>' or is normal data
                    return DATA_QUERY;
                }
                else
                {
                    // append the new char and switch to DATA
                    p.append( c );
                    return DATA;
                }
            }
        },
        /**
         * Parsing into the data part of the instruction
         */
        DATA
        {

            @Override
            public StateEngine parse( final ProcessingInstruction p,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '?' )
                {
                    // Expect '>'
                    return DATA_QUERY;
                }
                else
                {
                    p.append( c );
                    return this;
                }
            }
        },
        /**
         * Found a '?' in the data so this checks the next char for a '&gt;'.
         */
        DATA_QUERY
        {

            @Override
            public StateEngine parse( final ProcessingInstruction p,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '>' )
                {
                    // The instructions complete
                    p.getHandler().processingInstruction( p.getTarget(),
                                                          p.getAppendableString() );
                    return stateCompleted();
                }
                else
                {
                    // The ? is part of the instruction so write it & c then
                    // back to DATA
                    p.append( '?' ).append( c );
                    return DATA;
                }
            }
        };

        @Override
        public boolean continueLoop()
        {
            return true;
        }

        /**
         * Common to several states, if the name is xml, XML or DOCTYPE then
         * pass on to xmlDecl() or docType() and return true, otherwise just
         * return false.
         *
         * @param p ProcessingInstruction
         * @return true if delegated, false if the pi needs invoking
         * @throws org.xml.sax.SAXException if delegation would be illegal
         */
        protected final boolean parsingDelegated( final ProcessingInstruction p )
                throws SAXException
        {
            final String name = p.getTarget();
            if( XMLL.equals( name ) || XMLU.equals( name ) )
            {
                p.xmlDecl();
                return true;
            }
            else if( DOCTYPE.equals( name ) )
            {
                p.docType();
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
