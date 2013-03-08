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
 * Our own implementation of the UTF-8 charset. Unlike
 * {@link java.nio.charset.Charset} this does not support UCS characters, just
 * 16 bit Unicode characters.
 * <p/>
 * # Bits   Bit pattern
 * 1    7   0xxxxxxx
 * 2   11   110xxxxx 10xxxxxx
 * 3   16   1110xxxx 10xxxxxx 10xxxxxx
 * 4   21   11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
 * 5   26   111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
 * 6   31   1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
 * <p/>
 * UCS-2 uses 1-3, UTF-16 uses 1-4, UCS-4 uses 1-6
 *
 * @author peter
 */
@Encoding(
        {
                "UTF-8",
                "UTF_8",
                "UTF8", // JDK historical
                "unicode-1-1-utf-8"
        })
public class UTF_8
        extends AbstractCharset {

    /**
     * Is the character a continuation char
     *
     * @param b inbound byte
     * @return true if this is a continuation byte
     */
    public final boolean isContinuation(final int b) {
        return ((b & 0xc0) == 0x80);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char decode(final ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return NOT_ENOUGH_DATA;
        }

        final int pos = buffer.position();

        int b1 = buffer.get();
        switch ((b1 >> 4) & 0x0f) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                // 1 byte, 7 bits: 0xxxxxxx
                return (char) (b1 & 0x7f);

            case 12:
            case 13:
                // 2 bytes, 11 bits: 110xxxxx 10xxxxxx
                if (buffer.hasRemaining()) {
                    int b2 = buffer.get();
                    if (isContinuation(b2)) {
                        return (char) (((b1 & 0x1f) << 6) | ((b2 & 0x3f) << 0));
                    }
                }
                break;

            case 14:
                // 3 bytes, 16 bits: 1110xxxx 10xxxxxx 10xxxxxx
                if (buffer.remaining() > 2) {
                    int b2 = buffer.get();
                    int b3 = buffer.get();
                    if (isContinuation(b2) && isContinuation(b3)) {
                        return (char) (((b1 & 0x0f) << 12) | ((b2 & 0x3f) << 06) | ((b3 & 0x3f) << 0));
                    }
                }
                break;

            default:
                return INVALID_CHAR;
        }

        // Not enough data so reset the buffer and return
        buffer.position(pos);
        return NOT_ENOUGH_DATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean encode(final ByteBuffer buffer, final char c) {
        if (c < 0x80) {
            // Have at most seven bits
            if (buffer.hasRemaining()) {
                buffer.put((byte) c);
                return true;
            }
        } // should do a Surrogate check here
        else if (c < 0x800) {
            // 2 bytes, 11 bits
            if (buffer.remaining() > 1) {
                buffer.put((byte) (0xc0 | ((c >> 06))));
                buffer.put((byte) (0x80 | ((c >> 00) & 0x3f)));
                return true;
            }
        } else if (c <= '\uFFFF') {
            // 3 bytes, 16 bits
            if (buffer.remaining() > 2) {
                buffer.put((byte) (0xe0 | ((c >> 12))));
                buffer.put((byte) (0x80 | ((c >> 06) & 0x3f)));
                buffer.put((byte) (0x80 | ((c >> 00) & 0x3f)));

            }
        }

        // Not enough room or unsupported char
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(final char c) {
        if (c < 0x80) {
            // Have at most seven bits
            return 1;
        } // should do a Surrogate check here
        else if (c < 0x800) {
            // 2 bytes, 11 bits
            return 2;
        } else if (c <= '\uFFFF') {
            // 3 bytes, 16 bits
            return 3;
        }

        // Illegal char, should do something correct here
        return 0;
    }
}
