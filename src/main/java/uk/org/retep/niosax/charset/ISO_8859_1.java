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
                "ISO-8859-1",
                // IANA aliases
                "iso-ir-100",
                "ISO_8859-1",
                "latin1",
                "l1",
                "IBM819",
                "cp819",
                "csISOLatin1",
                // Other aliases
                "819",
                "IBM-819",
                "ISO8859_1",
                "ISO_8859-1:1987",
                "ISO_8859_1",
                "8859_1",
                "ISO8859-1"
        })
public class ISO_8859_1
        extends AbstractCharset {

    /**
     * {@inheritDoc}
     */
    @Override
    public char decode(final ByteBuffer buffer) {
        if (buffer.hasRemaining()) {
            return (char) buffer.get();
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
            buffer.put((byte) (c < 0xff ? c : ' '));
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
