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
package uk.org.retep.niosax;

import java.nio.ByteBuffer;
import uk.org.retep.niosax.charset.Charset;
import uk.org.retep.niosax.charset.CharsetFactory;

/**
 * An source of content passed to an {@link NioSaxParser}.
 *
 * <p>
 *  An NioSaxSource contains a reference to the {@link java.nio.ByteBuffer} that actually
 *  contains the content, and a {@link Charset} which decodes the bytes into
 *  the encoding of the xml being parsed.
 * </p>
 *
 * <p>
 *  The {@link NioSaxParser} does not change the content of {@link java.nio.ByteBuffer}
 *  in any way. However the {@link Charset} will change the {@link java.nio.ByteBuffer#position()}
 *  value as it decodes. When parsing completes, the position will be set to
 *  where parsing stops, which may be before {@link java.nio.ByteBuffer#limit()} if
 *  the remaining content is not a valid character for the {@link Charset}.
 * </p>
 *
 * <p>
 *  Once parsing is complete, you should call {@link java.nio.ByteBuffer#compact()} on
 *  the buffer to remove parsed content. You can then add further content as it
 *  becomes available, and pass that back to the parser.
 * </p>
 *
 * @author peter
 * @since 9.10
 */
public class NioSaxSource
{

    private Charset charset;
    private ByteBuffer buffer;

    /**
     * Create a new NioSaxSource with the default UTF-8 {@link Charset}.
     *
     * <p>
     *  You must call {@link #setByteBuffer(java.nio.ByteBuffer)} before passing
     *  this instance to {@link NioSaxParser#parse(uk.org.retep.niosax.NioSaxSource) }
     * </p>
     *
     */
    public NioSaxSource()
    {
        this( null, null );
    }

    /**
     * Create a new NioSaxSource with the supplied {@link Charset}.
     *
     * <p>
     *  You must call {@link #setByteBuffer(java.nio.ByteBuffer)} before passing
     *  this instance to {@link NioSaxParser#parse(uk.org.retep.niosax.NioSaxSource) }
     * </p>
     *
     * @param charset {@link Charset} to use
     */
    public NioSaxSource( final Charset charset )
    {
        this( charset, null );
    }

    /**
     * Create a new NioSaxSource with the supplied {@link Charset} and
     * the supplied {@link java.nio.ByteBuffer}.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read content from
     */
    public NioSaxSource( final ByteBuffer buffer )
    {
        this( null, buffer );
    }

    /**
     * Create a new NioSaxSource with the default UTF-8 {@link Charset} and
     * the supplied {@link java.nio.ByteBuffer}.
     *
     * @param charset {@link Charset} to use
     * @param buffer {@link java.nio.ByteBuffer} to read content from
     */
    public NioSaxSource( final Charset charset, final ByteBuffer buffer )
    {
        this.charset = charset == null ? CharsetFactory.getCharset( "UTF-8" ) : charset;
        this.buffer = buffer;
    }

    /**
     * Compacts the {@link java.nio.ByteBuffer}.
     *
     * <p>
     *  This is the equivalent to {@code getByteBuffer().compact();}
     * </p>
     *
     * @see java.nio.ByteBuffer#compact()
     */
    public final void compact()
    {
        buffer.compact();
    }
    
    /**
     * The {@link Charset} in use
     * @return {@link Charset}
     */
    public final Charset getCharset()
    {
        return charset;
    }

    /**
     * Set the {@link Charset} to use for subsequent processing
     *
     * @param charset {@link Charset} to use in subsequent processing
     * @return this to allow method chaining
     */
    public final NioSaxSource setCharset( final Charset charset )
    {
        this.charset = charset;
        return this;
    }

    /**
     * The {@link java.nio.ByteBuffer} content is read from
     * @return {@link java.nio.ByteBuffer} content is read from
     */
    public final ByteBuffer getByteBuffer()
    {
        return buffer;
    }

    /**
     * Sets the {@link java.nio.ByteBuffer} content is read from.
     *
     * <p>
     *  If during the lifetime of an {@link NioSaxParser} the buffer is not the
     *  same then use this method to set the buffer prior to calling
     *  {@link NioSaxParser#parse(uk.org.retep.niosax.NioSaxSource) }, however
     *  in that case it is up to the application to ensure that any characters
     *  left from a previous parse (i.e. incomplete characters) are included
     *  in the new buffer.
     * </p>
     *
     * @param buffer {@link java.nio.ByteBuffer} content is read from
     * @return this to allow method chaining
     */
    public final NioSaxSource setByteBuffer( final ByteBuffer buffer )
    {
        this.buffer = buffer;
        return this;
    }

    /**
     * Is the character valid for this {@link Charset}. This will usually always
     * return true for most characters, but will always return false for
     * {@link Charset#NOT_ENOUGH_DATA} and {@link Charset#INVALID_CHAR}.
     *
     * @param c char to test
     * @return true if valid, false if not
     */
    public final boolean isValid( final char c )
    {
        return charset.isValid( c );
    }

    /**
     * Does the buffer contain enough data for a single character. The position
     * is left unchanged.
     *
     * @return true if the ByteBuffer has enough data for that number of characters
     */
    public final boolean hasCharacter()
    {
        return charset.hasCharacter( buffer );
    }

    /**
     * Decode the character at the current position in the {@link java.nio.ByteBuffer}.
     *
     * <p>
     *  If the character can be decoded then the position is moved forward by
     *  thecorrect number of characters.
     * </p>
     *
     * @return decoded character, {@link Charset#INVALID_CHAR} or
     * {@link Charset#NOT_ENOUGH_DATA}
     */
    public final char decode()
    {
        return charset.decode( buffer );
    }
}
