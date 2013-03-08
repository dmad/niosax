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
package uk.org.retep.niosax.internal.core.delegate.element;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import uk.org.retep.niosax.internal.core.delegate.BaseSaxTest;

/**
 * Test elements with namespace declarations
 *
 * @author peter
 */
public class NamespaceTest
        extends BaseSaxTest
{

    /**
     * XML Based on RFC3920 4.8
     */
    private static final String STREAM_START = "<stream:stream\n       to='example.com'\n       xmlns='jabber:client'\n       xmlns:stream='http://etherx.jabber.org/streams'\n       version='1.0'>";
    //private static final String STREAM_START = "<stream:stream\n       to='example.com'\n       xmlns:stream='http://etherx.jabber.org/streams'\n       version='1.0'>";
    private static final String MESSAGE = "<message from='juliet@example.com'to='romeo@example.net'xml:lang='en'><body>Art thou not Romeo, and a Montague?</body></message>";
    private static final String STREAM_END = "</stream:stream>";
    private static final String NS_XML = STREAM_START + MESSAGE + STREAM_END;

    /**
     * Tests SIMPLE_XML without splitting
     * @throws Exception
     */
    @Test
    public void testNSElement()
            throws Exception
    {
        nsElement( false );
    }

    /**
     * Tests SIMPLE_XML with splitting
     * @throws Exception
     */
    @Test
    public void testNSElementSplit()
            throws Exception
    {
        nsElement( true );
    }

    private void nsElement( final boolean split )
            throws Exception
    {
        parseDecl( new NSElement(), split, NS_XML );
    }

    /**
     * TestHandler for simple tests
     */
    private class NSElement
            extends TestHandler
    {
        

        @Override
        public void resetHandler()
                throws Exception
        {
            super.resetHandler();
        }

        @Override
        public void assertHandler()
                throws Exception
        {
        }

        @Override
        public void startPrefixMapping( String prefix, String uri )
                throws SAXException
        {
        }

        @Override
        public void endPrefixMapping( String prefix )
                throws SAXException
        {
        }

        @Override
        public void startElement( String uri, String localName, String qName,
                                  Attributes attributes )
                throws SAXException
        {


        }
    }
}
