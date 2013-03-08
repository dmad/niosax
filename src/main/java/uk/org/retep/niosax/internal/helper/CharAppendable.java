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

import java.util.Arrays;

/**
 * A base {@link Appendable} implementation which stores an extensible char
 * array for storing parsed content.
 *
 * <p>
 * It has a similar operation to the {@link java.io.CharAppendable} without the
 * added baggage of ThreadSafety, IO etc as these are not necessary
 * </p>
 * 
 * @author peter
 * @since 9.10
 */
public class CharAppendable
        implements Appendable
{

    private Appendable parent;
    /**
     * The buffer where data is stored.
     */
    protected char buf[];
    /**
     * The number of chars in the buffer.
     */
    protected int count;

    /**
     * Creates a new CharAppendable.
     */
    public CharAppendable()
    {
        this( null );
    }

    /**
     * Creates a new CharAppendable.
     *
     * @param parent The parent {@link Appendable} or null
     */
    public CharAppendable( final Appendable parent )
    {
        this( null, 32 );
    }

    /**
     * Creates a new CharAppendable with the specified initial size.
     *
     * @param initialSize  an int specifying the initial buffer size.
     * @exception IllegalArgumentException if initialSize is negative
     */
    public CharAppendable( final int initialSize )
    {
        this( null, initialSize );
    }

    /**
     * Creates a new CharAppendable with the specified initial size.
     *
     * @param parent The parent {@link Appendable} or null
     * @param initialSize  an int specifying the initial buffer size.
     * @exception IllegalArgumentException if initialSize is negative
     */
    public CharAppendable( final Appendable parent, final int initialSize )
    {
        if( initialSize < 0 )
        {
            throw new IllegalArgumentException( "Negative initial size: "
                    + initialSize );
        }
        this.parent = parent;
        buf = new char[ initialSize ];
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public final <T extends Appendable> T getParent()
    {
        return (T) parent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final <T extends Appendable> T setParent( final T parent )
    {
        this.parent = parent;
        return parent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final CharAppendable append( final char c )
    {
        final int newcount = count + 1;
        if( newcount > buf.length )
        {
            buf = Arrays.copyOf( buf, Math.max( buf.length << 1, newcount ) );
        }

        buf[count] = c;
        count = newcount;

        return this;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final CharAppendable append( final char[] c, final int off,
                                        final int len )
    {
        if( (off < 0) || (off > c.length) || (len < 0) || ((off + len) > c.length) || ((off + len) < 0) )
        {
            throw new IndexOutOfBoundsException();
        }
        else if( len > 0 )
        {
            final int newcount = count + len;

            if( newcount > buf.length )
            {
                buf = Arrays.copyOf( buf, Math.max( buf.length << 1, newcount ) );
            }

            System.arraycopy( c, off, buf, count, len );
            count = newcount;
        }

        return this;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final char[] toCharArray()
    {
        return Arrays.copyOf( buf, count );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final char[] getCharBuffer()
    {
        return buf;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CharAppendable reset()
    {
        count = 0;
        return this;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final int size()
    {
        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final String toString()
    {
        return String.valueOf( buf, 0, count );
    }
}
