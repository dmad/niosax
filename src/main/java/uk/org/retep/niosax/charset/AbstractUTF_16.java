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
 * Base implementation of UTF16 Charsets
 *
 * @author peter
 */
public abstract class AbstractUTF_16
        extends AbstractCharset {

    /**
     * Byte order mark used to indicate a BIG endian stream
     */
    public static final char BYTE_ORDER_MARK = (char) 0xfeff;
    /**
     * Byte order mark used to indicate a LITTLE endian stream
     */
    public static final char REVERSED_MARK = (char) 0xfffe;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Charset getInstance();

    /**
     * Decode a Big endian utf16 character
     *
     * @param b1 byte1 from stream
     * @param b2 byte2 from stream
     * @return decoded char
     */
    protected final char decodeBig(final int b1, final int b2) {
        return (char) ((b1 << 8) | b2);
    }

    /**
     * Decode a Little endian utf16 character
     *
     * @param b1 byte1 from stream
     * @param b2 byte2 from stream
     * @return decoded char
     */
    protected final char decodeLittle(final int b1, final int b2) {
        return (char) ((b2 << 8) | b1);
    }

    /**
     * Encode the character into the specified {@link java.nio.ByteBuffer} at the current
     * position using big endian byte ordering.
     * <p/>
     * <p>
     * The buffer's position will be incremented accordingly.
     * </p>
     *
     * @param buffer {@link java.nio.ByteBuffer} to append to.
     * @param c      char to append
     * @return true if the append succeded, false if the buffer does not have
     *         enough capacity to hold this character.
     */
    protected final boolean encodeBig(final ByteBuffer buffer, final char c) {
        if (buffer.remaining() < 2) {
            return false;
        } else {
            buffer.put((byte) (c >> 8));
            buffer.put((byte) (c & 0xff));
            return true;
        }
    }

    /**
     * Encode the character into the specified {@link java.nio.ByteBuffer} at the current
     * position using little endian byte ordering.
     * <p/>
     * <p>
     * The buffer's position will be incremented accordingly.
     * </p>
     *
     * @param buffer {@link java.nio.ByteBuffer} to append to.
     * @param c      char to append
     * @return true if the append succeded, false if the buffer does not have
     *         enough capacity to hold this character.
     */
    protected final boolean encodeLittle(final ByteBuffer buffer, final char c) {
        if (buffer.remaining() < 2) {
            return false;
        } else {
            buffer.put((byte) (c & 0xff));
            buffer.put((byte) (c >> 8));
            return true;
        }
    }

    @Override
    public int size(char c) {
        return 2;
    }
}
