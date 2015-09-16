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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Factory of {@link uk.org.retep.niosax.charset.Charset} instances. This factory allows for the lookup of
 * Charset instances based on their names.
 *
 * @author peter
 */
public class CharsetFactory {

    private final static List<Charset> CHARSETS;

    static {
        CHARSETS = new ArrayList<> (6);

        CHARSETS.add (new ISO_8859_1());
        CHARSETS.add (new US_ASCII());
        CHARSETS.add (new UTF_16());
        CHARSETS.add (new UTF_16BE());
        CHARSETS.add (new UTF_16LE());
        CHARSETS.add (new UTF_8());
    }

    private final static Map<String, Charset> CHARSET_MAP;

    static {
        CHARSET_MAP = new HashMap<>();


        for (Charset c : CHARSETS) {
            final Encoding e = c.getClass().getAnnotation(Encoding.class);
            if (e != null) {
                for (String enc : e.value()) {
                    if (!CHARSET_MAP.containsKey(enc)) {
                        CHARSET_MAP.put(enc, c);
                    }
                }
            }
        }
    }

    private CharsetFactory() {
    }


    /**
     * Returns a {@link uk.org.retep.niosax.charset.Charset} implementation for the named encoding
     *
     * @param encoding Encoding to lookup
     * @return {@link uk.org.retep.niosax.charset.Charset} or null if not supported.
     */
    public static Charset getCharset(final String encoding) {
        final Charset c = CHARSET_MAP.get(encoding);
        return c == null ? null : c.getInstance();
    }
}
