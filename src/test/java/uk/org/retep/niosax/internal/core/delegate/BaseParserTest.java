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
package uk.org.retep.niosax.internal.core.delegate;

import org.junit.Test;

/**
 * This generates two tests, parseWhole() and parseSplit(). These then call an
 * abstract test parseDocument() with a single parameter with false and true respectively.
 *
 * The reason for this is that the class then implements two tests. When that
 * parameter is false, then the xml is passed to the parser in its entirety.
 *
 * When true, it is split in two before testing. This allows us to test partial
 * content easily.
 *
 * @author peter
 */
public abstract class BaseParserTest
        extends BaseSaxTest
{

    @Test
    public final void parseWhole()
            throws Exception
    {
        parseDocument( false );
    }

    @Test
    public final void parseSplit()
            throws Exception
    {
        parseDocument( false );
    }

    /**
     * Implement the actual test. JUnit will run this twice, one for parseWhole()
     * with split==false, and once for parseSplit() with split==true.
     *
     * @param split Should the xml in the test be broken up to simulate
     * incomplete content?
     * @throws Exception on failure
     */
    protected abstract void parseDocument( boolean split )
            throws Exception;

}
