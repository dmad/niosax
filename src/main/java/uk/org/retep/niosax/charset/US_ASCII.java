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
 * Our own implementation of the US_ASCII charset.
 *
 * @author peter
 */
@Encoding(
        {
                "US-ASCII",
                "US_ASCII", // JDK Historical?
                // IANA aliases
                "iso-ir-6",
                "ANSI_X3.4-1986",
                "ISO_646.irv:1991",
                "ASCII",
                "ISO646-US",
                "us",
                "IBM367",
                "cp367",
                "csASCII",
                "default",
                // Other aliases
                "646", // Solaris POSIX locale
                "iso_646.irv:1983",
                "ANSI_X3.4-1968", // Linux POSIX locale (RedHat)
                "ascii7"
        })
public class US_ASCII
        extends AbstractCharset {

    /**
     * {@inheritDoc}
     */
    @Override
    public char decode(final ByteBuffer buffer) {
        if (buffer.hasRemaining()) {
            final int b = buffer.get();
            if (b < 0x80) {
                return (char) b;
            } else {
                return INVALID_CHAR;
            }
        } else {
            return NOT_ENOUGH_DATA;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean encode(final ByteBuffer buffer, final char c) {
        if (buffer.hasRemaining()) {
            buffer.put((byte) (c < 0x80 ? c : ' '));
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(char c) {
        return 1;
    }
}
