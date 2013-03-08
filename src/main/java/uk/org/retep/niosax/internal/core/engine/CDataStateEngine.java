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
import org.xml.sax.ext.LexicalHandler;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.core.StateEngine;
import uk.org.retep.niosax.internal.core.StateEngineDelegate;
import uk.org.retep.niosax.internal.helper.Appendable;
import static uk.org.retep.niosax.internal.helper.XmlSpec.*;

/**
 * A {@link StateEngine} implementation which parses a CData section.
 *
 * @author peter
 * @since 9.10
 */
public enum CDataStateEngine
        implements StateEngine<StateEngineDelegate>
{

    /**
     * The entry point. On the first char, call {@link org.xml.sax.ext.LexicalHandler#startCDATA()}
     * append the caracter and then switch to {@link #NORMAL}
     */
    START
    {

        @Override
        public StateEngine parse( final StateEngineDelegate e,
                                  final NioSaxSource source,
                                  final char c )
                throws SAXException
        {
            // Notify the start of the CDATA section
            final LexicalHandler lh = e.getLexicalHandler();
            if( lh != null )
            {
                lh.startCDATA();
            }

            return NORMAL.parse( e, source, c );
        }
    },
    /**
     * If the char is ']' switch to {@link #END1} otherwise append to the buffer.
     */
    NORMAL
    {

        @Override
        public StateEngine parse( final StateEngineDelegate e,
                                  final NioSaxSource source,
                                  final char c )
                throws SAXException
        {
            if( c == ']' )
            {
                return END1;
            }
            else
            {
                e.append( c );
                return this;
            }
        }
    },
    /**
     * State when the first ']' is found. If the next char is also ']' then
     * switch to {@link #END1} otherwise append the previous ']' and the char
     * to the buffer and switch to {@link #NORMAL}
     */
    END1
    {

        @Override
        public StateEngine parse( final StateEngineDelegate e,
                                  final NioSaxSource source,
                                  final char c )
                throws SAXException
        {
            if( c == ']' )
            {
                return END2;
            }
            else
            {
                // Push back the preceding ']' and return back to NORMAL.
                e.append( ']' ).append( c );
                return NORMAL;
            }
        }
    },
    /**
     * State when two ']' have been found.
     *
     * <p>
     * If the next char is '&gt;' then we call
     * {@link org.xml.sax.ContentHandler#characters(char[], int, int) }
     * with the buffer content, call {@link org.xml.sax.ext.LexicalHandler#endCDATA() } to
     * notify the end of the CDATA section, tell the {@link StateEngineDelegate}
     * to finish and return {@link uk.org.retep.niosax.internal.helper.XmlSpec#stateCompleted()}.
     * </p>
     *
     * <p>
     *  If the next char is not '&gt;' then we append the previous two ']'
     *  characters and switch back to {@link #NORMAL}
     * </p>
     *
     */
    END2
    {

        @Override
        public StateEngine parse( final StateEngineDelegate e,
                                  final NioSaxSource source,
                                  final char c )
                throws SAXException
        {
            if( c == '>' )
            {
                // Notify the handler of the characters
                final Appendable a = e.getAppendable();
                e.getHandler().characters( a.getCharBuffer(), 0, a.size() );

                // Notify the end of the CDATA section
                final LexicalHandler lh = e.getLexicalHandler();
                if( lh != null )
                {
                    lh.endCDATA();
                }

                e.finish();

                return stateCompleted();
            }
            else
            {
                // Push back the preceding ']]' and return back to NORMAL.
                e.append( ']' ).append( ']' ).append( c );
                return NORMAL;
            }
        }
    };

    /**
     * {@inheritDoc }
     */
    @Override
    public final boolean continueLoop()
    {
        return true;
    }
}
