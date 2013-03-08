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

import uk.org.retep.niosax.internal.helper.Appendable;
import uk.org.retep.niosax.internal.helper.CharAppendable;

/**
 * Abstract implementation for {@link ParserDelegate}'s that need to store
 * parsed characters using the {@link Appendable} api.
 *
 * @param <P> The parent {@link ParserDelegate} type
 * @author peter
 * @since 9.10
 */
public abstract class AppendableParserDelegate<P extends ParserDelegate>
        extends AbstractParserDelegate<P>
{

    private Appendable appendable;

    /**
     * Constructor used by all {@link StateEngineDelegate} implementations.
     * @param parent The parent {@link ParserDelegate}
     */
    public AppendableParserDelegate( final P parent )
    {
        super( parent );
        appendable = new CharAppendable();
    }

    /**
     * The underlying {@link Appendable} used to store parsed characters
     * @param <T> type of {@link Appendable}
     * @return underlying {@link Appendable}
     */
    @SuppressWarnings( "unchecked" )
    public final <T extends Appendable> T getAppendable()
    {
        return (T) appendable;
    }

    /**
     * Replaces the underlying appendable with a new one. The new one will have
     * it's parent set to the original one.
     * @param <T> type of {@link Appendable}
     * @param appendable {@link Appendable} to replace the current one with
     * @return appendable
     */
    public final <T extends Appendable> T setAppendable( final T appendable )
    {
        this.appendable = appendable.setParent( this.appendable );
        return appendable;
    }

    /**
     * Replaces the underlying appendable with it's parent.
     * @param <T> type of {@link Appendable}
     * @return the new underlying {@link Appendable}
     */
    public final <T extends Appendable> T restoreParentAppendable()
    {
        if( appendable != null )
        {
            appendable = appendable.getParent();
        }
        return this.<T>getAppendable();
    }

    /**
     * Convert the content of the writer into a string, then reset it so any
     * new data begins a new string.
     *
     * <p>
     *  This is the equivalent to {@code getAppendable().toString(); getAppendable().reset();}
     * </p>
     *
     * @return String of data in the writer
     */
    public final String getAppendableString()
    {
        final String s = appendable.toString();
        appendable.reset();
        return s;
    }

    /**
     * Convert the content of the writer into a char[], then reset it so any
     * new data begins a new string.
     *
     * <p>
     *  This is the equivalent to {@code getAppendable().toCharArray(); getAppendable().reset();}
     * </p>
     *
     * @return String of data in the writer
     */
    public final char[] getAppendableChars()
    {
        final char[] c = appendable.toCharArray();
        appendable.reset();
        return c;
    }

    /**
     * Append a char to the current {@link Appendable}.
     *
     * <p>
     *  This is the equivalent of {@code getAppendable().append( c );}
     * </p>
     *
     * @param c character to append
     * @return {@link Appendable} to allow chaining
     */
    public final Appendable append( final char c )
    {
        return getAppendable().append( c );
    }
}
