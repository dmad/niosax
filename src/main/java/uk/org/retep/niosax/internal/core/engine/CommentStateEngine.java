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
import uk.org.retep.niosax.IllegalCharacterException;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.core.StateEngine;
import uk.org.retep.niosax.internal.core.StateEngineDelegate;
import uk.org.retep.niosax.internal.helper.Appendable;
import static uk.org.retep.niosax.internal.helper.XmlSpec.*;

/**
 * {@link StateEngine} implementation which parses an xml comment
 *
 * @author peter
 * @since 9.10
 */
public enum CommentStateEngine
        implements StateEngine<StateEngineDelegate>
{

    /**
     * If the char is '-' then switch to {@link #END1} otherwise append to
     * the buffer
     */
    NORMAL
    {

        @Override
        public StateEngine parse( final StateEngineDelegate e,
                                  final NioSaxSource source,
                                  final char c )
                throws SAXException
        {
            if( c == '-' )
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
     * State when the first '-' is found. If the next char is also '-' then we
     * switch to {@link #END2}, otherwise simply but both '-' and the char into the
     * buffer and resume {@link #NORMAL}
     */
    END1
    {

        @Override
        public StateEngine parse( final StateEngineDelegate e,
                                  final NioSaxSource source,
                                  final char c )
                throws SAXException
        {
            if( c == '-' )
            {
                return END2;
            }
            else
            {
                // Push back the preceding '-' and return back to NORMAL.

                // Note: if c=='>' here the XML 1.1 grammar allows for -&gt;
                // just not --&gt; so this is still valid.

                e.append( '-' ).append( c );
                return NORMAL;
            }
        }
    },
    /**
     * State when a '-' is found whilst in {@link #END1}. The next char must then
     * be '&gt;'
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
                // Notify the LexicalHandler (if supported),
                // finish this state and change to COMPLETED

                final LexicalHandler lh = e.getLexicalHandler();
                if( lh != null )
                {
                    final Appendable a = e.getAppendable();
                    lh.comment( a.getCharBuffer(), 0, a.size() );
                }

                e.finish();

                return stateCompleted();
            }
            else
            {
                throw new IllegalCharacterException();
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
