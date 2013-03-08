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
package uk.org.retep.niosax.charset;

import java.nio.ByteBuffer;

/**
 * Abstract implementation of {@link Charset} implementing methods that are
 * identical to all {@link Charset} implementations.
 *
 * @author peter
 */
public abstract class AbstractCharset
        implements Charset {

    /**
     * {@inheritDoc}
     * <p/>
     * <p>
     * Most implementations would not need to override this method as the
     * default implementation returns the same instance.
     * </p>
     */
    @Override
    public Charset getInstance() {
        return this;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>
     * The default implementation will test for {@link #NOT_ENOUGH_DATA}
     * and {@link #INVALID_CHAR} only, returning true for all other values.
     * </p>
     */
    @Override
    public boolean isValid(final char c) {
        return c != NOT_ENOUGH_DATA && c != INVALID_CHAR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasCharacter(final ByteBuffer buffer) {
        // FIXME determine if INVALID_CHAR should be part of the result here
        return isValid(peek(buffer));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasCharacters(final ByteBuffer buffer, final int count) {
        final int pos = buffer.position();
        try {
            // Return on the first invalid char found
            for (int i = 0; i < count; i++) {
                // FIXME determine if INVALID_CHAR should be part of the result here
                if (!isValid(decode(buffer))) {
                    return false;
                }
            }

            return true;
        } finally {
            // Always reset the buffer
            buffer.position(pos);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final char peek(final ByteBuffer buffer) {
        final int pos = buffer.position();
        try {
            return decode(buffer);
        } finally {
            // Always reset the buffer
            buffer.position(pos);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int size(final char[] c) {
        return size(c, 0, c.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int size(final char[] c, final int length) {
        return size(c, 0, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int size(final char[] c, final int offset, final int length) {
        int size = 0;
        for (int i = offset, j = 0; j < length; j++) {
            size += size(c[i++]);
        }
        return size;
    }

    private final boolean skip(final ByteBuffer buffer, final int length) {
        if (buffer.remaining() >= length) {
            buffer.position(buffer.position() + length);
            return true;
        }
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean skip(final ByteBuffer buffer, final char[] c) {
        return skip(buffer, size(c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean skip(final ByteBuffer buffer,
                              final char[] c,
                              final int length) {
        return skip(buffer, size(c, length));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean skip(final ByteBuffer buffer,
                              final char[] c,
                              final int offset,
                              final int length) {
        return skip(buffer, size(c, offset, length));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean peek(final ByteBuffer buffer, final char[] c) {
        return peek(buffer, c, 0, c.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean peek(final ByteBuffer buffer,
                              final char[] c,
                              final int length) {
        return peek(buffer, c, 0, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean peek(final ByteBuffer buffer,
                              final char[] c,
                              final int offset,
                              final int length) {
        final int pos = buffer.position();

        boolean valid = true;
        int p = offset;
        for (int l = 0; l < length && valid; l++) {
            final char d = decode(buffer);
            if (d < 0) {
                // not enough data
                valid = false;
                break;
            }
            c[p++] = d;
            l--;
        }

        // Always reset the buffer
        buffer.position(pos);
        return valid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean read(final ByteBuffer buffer, final char[] c) {
        return read(buffer, c, 0, c.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean read(final ByteBuffer buffer,
                              final char[] c,
                              final int length) {
        return read(buffer, c, 0, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean read(final ByteBuffer buffer,
                              final char[] c,
                              final int offset,
                              final int length) {
        final int pos = buffer.position();

        int p = offset;
        for (int l = 0; l < length; l++) {
            final char d = decode(buffer);
            if (d < 0) {
                // not enough data so restore the buffer and bomb out
                buffer.position(pos);
                return false;
            }
            c[p++] = d;
            l--;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int write(final ByteBuffer buffer, final char[] c) {
        return write(buffer, c, 0, c.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int write(final ByteBuffer buffer, final char[] c, int length) {
        return write(buffer, c, 0, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int write(final ByteBuffer buffer,
                           final char[] c,
                           final int offset,
                           final int length) {
        int s = 0;
        for (int i = 0; i < length; i++) {
            if (encode(buffer, c[offset + i])) {
                s++;
            } else {
                break;
            }
        }
        return s;
    }
}
