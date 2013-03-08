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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 * Tests the parsing of the XML declaration
 * 
 * @author peter
 */
public class XMLDeclTest
        extends BaseParserTest
{

    private static final Object XMLDECLS[][] =
    {
        // xml,
        // expectedVersion, expectedEncoding, expectedStandalone
        // with '
        {
            "<?xml version='1.1' encoding='%s'?>",
            "1.1", UTF8, Boolean.FALSE
        },
        {
            "<?xml version='1.1' encoding='%s' standalone='yes'?>",
            "1.1", UTF8, Boolean.TRUE
        },
        {
            "<?xml version='1.1' encoding='%s' standalone='no'?>",
            "1.1", UTF8, Boolean.FALSE
        },
        {
            "<?xml encoding='%s'?>",
            null, UTF8, Boolean.FALSE
        },
        {
            "<?xml version='1.1'?>",
            "1.1", null, Boolean.FALSE
        },
        {
            "<?xml standalone='yes'?>",
            null, null, Boolean.TRUE
        },
        {
            "<?xml standalone='no'?>",
            null, null, Boolean.FALSE
        },
        // with "
        {
            "<?xml version=\"1.1\" encoding=\"%s\"?>",
            "1.1", UTF8, Boolean.FALSE
        },
        {
            "<?xml version=\"1.1\" encoding=\"%s\" standalone=\"yes\"?>",
            "1.1", UTF8, Boolean.TRUE
        },
        {
            "<?xml version=\"1.1\" encoding=\"%s\" standalone=\"no\"?>",
            "1.1", UTF8, Boolean.FALSE
        },
        {
            "<?xml encoding=\"%s\"?>",
            null, UTF8, Boolean.FALSE
        },
        {
            "<?xml version=\"1.1\"?>",
            "1.1", null, Boolean.FALSE
        },
        {
            "<?xml standalone=\"yes\"?>",
            null, null, Boolean.TRUE
        },
        {
            "<?xml standalone=\"no\"?>",
            null, null, Boolean.FALSE
        },
        // mixed ' and " but still valid
        {
            "<?xml version=\"1.1\" encoding='%s'?>",
            "1.1", UTF8, Boolean.FALSE
        },
        {
            "<?xml version='1.1' encoding=\"%s\" standalone='yes'?>",
            "1.1", UTF8, Boolean.TRUE
        },
        {
            "<?xml version='1.1' encoding=\"%s\" standalone='no'?>",
            "1.1", UTF8, Boolean.FALSE
        }
    };

    @Override
    protected void parseDocument( final boolean split )
            throws Exception
    {
        for( int testIndex = 0; testIndex < XMLDECLS.length; testIndex++ )
        {
            final Object[] test = XMLDECLS[testIndex];
            final TestHandler handler = new TestHandlerImpl( testIndex, test );
            parseXML( handler, split, (String) test[0], UTF8 );
        }
    }

    private class TestHandlerImpl
            extends TestHandler
    {

        private int testIndex;
        private Object test[];
        private boolean xmlDeclarationReceived;

        public TestHandlerImpl( final int i, final Object[] test )
        {
            this.test = test;
        }

        @Override
        public void assertHandler()
                throws Exception
        {
            assertTrue( "xmlDeclaration not called " + testIndex,
                        xmlDeclarationReceived );
        }

        // Note: The +testIndex business is so we can look at the specific test data
        // that causes a failure.
        @Override
        public void xmlDeclaration( final String versionInfo,
                                    final String encoding,
                                    final boolean standalone )
        {
            // Fail if this is the second time we've been called
            assertFalse( "xmlDeclaration already been called " + testIndex,
                         xmlDeclarationReceived );

            assertEquals( "Version " + testIndex, test[1],
                          versionInfo );
            assertEquals( "Encoding " + testIndex, test[2], encoding );
            assertEquals( "Standalone " + testIndex, test[3], standalone );

            // record the call
            xmlDeclarationReceived = true;
        }

        @Override
        public void processingInstruction( String target, String data )
                throws SAXException
        {
            // We should never see the decl being passed as a pi
            fail( "Not expecting processingInstruction " + testIndex + " target=\"" + target + "\" data=\"" + data + "\"" );
        }

        @Override
        public void startDTD( String name, String publicId, String systemId )
                throws SAXException
        {
            fail( "Not expecting startDTD " + testIndex + " name=\"" + name + "\" publicId=\"" + publicId + "\" systemId=\"" + systemId + "\"" );
        }

        @Override
        public void endDTD()
                throws SAXException
        {
            fail( "Not expecting endDTD " + testIndex );
        }

        @Override
        public void startElement( String uri, String localName, String qName,
                                  Attributes attributes )
                throws SAXException
        {
            fail( "Not expecting startElement " + testIndex + " uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\"" );
        }

        @Override
        public void endElement( String uri, String localName, String qName )
                throws SAXException
        {
            fail( "Not expecting endElement " + testIndex + " uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\"" );
        }
    }
}
