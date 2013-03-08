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
package uk.org.retep.niosax.internal.core;

import org.xml.sax.SAXException;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.helper.Appendable;

/**
 * Abstract class for {@link ParserDelegate}'s that have a parent and which use
 * a state engine for managing parsing.
 *
 * <p>
 *  The state engine is usually an enum which implements this interface, so a
 *  minimum implementation of this class would be:
 * </p>
 *
 * <code><pre>
 *  // StateEngineDelegate that captures all text until '*' is found
 *  public class MyParser extends StateEngineDelegate&lt;ParentParser&gt; {
 *      public MyParser( ParentParser parent ) {
 *          super(parent);
 *      }
 *
 *      // The state to use when this instance starts for the first time
 *      protected {@linkplain StateEngine StateEngine} getInitialState() {
 *          return MyState.INITIAL;
 *      }
 *
 *      private enum MyState implements {@linkplain StateEngine StateEngine}&lt;MyParser&gt; {
 *
 *          INITIAL {
 *              public {@linkplain StateEngine StateEngine} parse( MyParser instance, {@linkplain Appendable Appendable} w, char c )
 *                  throws SAXException
 *              {
 *                  if( c=='*' ) {
 *
 *                      // get the captured data
 *                      String s = instance.{@linkplain #getAppendableString() getAppendableString}();
 *                      // do something with it
 *
 *                      // change to the COMPLETE state
 *                      return COMPLETE;
 *                  } else {
 *                      // Append to the buffer
 *                      w.append(c);
 *
 *                      // Stay in this state
 *                      return this;
 *                  }
 *              }
 *          },
 *
 *          COMPLETE {
 *              // This breaks the parsing loop
 *              public boolean continueLoop() {
 *                  return false;
 *              }
 *          };
 *
 *          public {@linkplain StateEngine StateEngine} parse( MyParser instance, {@linkplain Appendable Appendable} w, char c )
 *              throws SAXException
 *          {
 *              throw new UnsupportedOperationException();
 *          }
 *
 *          public boolean continueLoop() {
 *              return true;
 *          }
 *      }
 *  }
 * </pre></code>
 *
 * @param <P> The parent {@link ParserDelegate} type
 * @author peter
 * @since 9.10
 */
public abstract class StateEngineDelegate<P extends ParserDelegate>
        extends AppendableParserDelegate<P>
{

    private StateEngine state;

    /**
     * Returns a {@link uk.org.retep.niosax.internal.core.StateEngineDelegate} which utilises a standalone
     * {@link StateEngine} implementation that does nothing with parsed content.
     *
     * <p>
     *  This is usually used for specific {@link StateEngine} implementation's
     *  which parse content to determine one or more {@link ParserDelegate}'s
     *  should parse content.
     * </p>
     *
     * <p>
     *  For example, comments start with &lt;!-- and CDATA with &lt;[CDATA[
     *  so there's a unique state engine which uses this method to delegate
     *  parsing, and delegates control depending if -- or CDATA[ is found.
     * </p>
     *
     * @param <P> The parent {@link ParserDelegate} type
     * @param parent the parent {@link ParserDelegate}
     * @param initialState The initial state to use
     * @return {@link uk.org.retep.niosax.internal.core.StateEngineDelegate} instance
     */
    public static <P extends ParserDelegate> StateEngineDelegate delegate(
            final P parent,
            final StateEngine<StateEngineDelegate> initialState )
    {
        return new StateEngineDelegate<P>( parent )
        {

            @Override
            protected StateEngine getInitialState()
            {
                return initialState;
            }
        };
    }

    /**
     * Constructor used by all {@link uk.org.retep.niosax.internal.core.StateEngineDelegate} implementations.
     * @param parent The parent {@link ParserDelegate}
     */
    public StateEngineDelegate( final P parent )
    {
        super( parent );
        state = getInitialState();
    }

    /**
     * The initial state for this instance
     * @return initial State value
     */
    protected abstract StateEngine getInitialState();

    /**
     * Parses content by using the current {@link StateEngine}. This method will loop
     * until the {@link StateEngine#continueLoop()} returns false or no more content
     * is available in the buffer.
     *
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public final boolean parse( final NioSaxSource source, final char c )
            throws SAXException
    {
        state = state.parse( this, source, c );
        return state.continueLoop();
    }

    /**
     * The current {@link StateEngine}
     * @return current {@link StateEngine}
     */
    protected final StateEngine getState()
    {
        return state;
    }
}
