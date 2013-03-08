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
 * Our own implementation of the UTF-16 charset. This simply delegages the
 * encoding to {@link java.nio.ByteBuffer#getChar()} and {@link java.nio.ByteBuffer#putChar(char)}
 *
 * @author peter
 */
@Encoding(
        {
                "UTF-16",
                "UTF_16", // JDK historical
                "utf16",
                "unicode",
                "UnicodeBig"
        })
public class UTF_16
        extends AbstractUTF_16 {

    private Endian endian = Endian.NONE;
    private boolean requiresBOM;

    public boolean isRequiresBOM() {
        return requiresBOM;
    }

    public void setRequiresBOM(final boolean requiresBOM) {
        if (endian == Endian.NONE) {
            this.requiresBOM = requiresBOM;
        } else {
            throw new IllegalStateException(
                    "Cannot set requiresDOM once the stream has been used");
        }
    }

    public static enum Endian {

        /**
         * No endian, assume little endian until told otherwise
         */
        NONE,
        /**
         * Big endian, so order is b[0],b[1]
         */
        BIG,
        /**
         * Little endian, so order is b[1],b[0]
         */
        LITTLE
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Charset getInstance() {
        return new UTF_16();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public char decode(final ByteBuffer buffer) {
        if (buffer.remaining() < 2) {
            return NOT_ENOUGH_DATA;
        } else if (endian == Endian.BIG) {
            return decodeBig(buffer.get(), buffer.get());
        } else {
            return decodeLittle(buffer.get(), buffer.get());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean encode(final ByteBuffer buffer, final char c) {
        if (endian == Endian.BIG) {
            return encodeBig(buffer, c);
        } else if (endian == Endian.LITTLE) {
            return encodeLittle(buffer, c);
        } else {
            // Check for a BOM... Note BOM's are always encoded BIG_ENDIAN in
            // the stream
            if (c == REVERSED_MARK) {
                // Switch into Little endian mode but record the BOM in Big endian...
                endian = Endian.LITTLE;
            } else if (c == BYTE_ORDER_MARK) {
                // Switch into Big endian mode
                endian = Endian.BIG;
            } else {
                // The default is to run in BigEndian so switch and encode
                // however if a BOM is required then try to send that first
                // Then switch.
                if (requiresBOM && !encodeBig(buffer, BYTE_ORDER_MARK)) {
                    return false;
                }
            }
            endian = Endian.BIG;
        }

        return encodeBig(buffer, c);
    }
}
