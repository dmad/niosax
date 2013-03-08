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

import static org.junit.Assert.*;

/**
 *
 * @author peter
 */
public class ElementTest
        extends BaseSaxTest
{

    static final String STREAM = "stream";
    static final String NODE1 = "node1";
    static final String NODE11 = "node11";
    // SIMPLE_XML - tests empty elements at the root
    private static final String SIMPLE_XML = "<stream/>";
    private static final String CHILD1_XML = "<stream><node1/></stream>";
    private static final String XML = "<?xml version='1.1' encoding='%s'?>\n<stream><node1>\n    <node11/>\n</node1></stream>";

    /**
     * Tests SIMPLE_XML without splitting
     * @throws Exception
     */
    @Test
    public void testSimpleElement()
            throws Exception
    {
        simpleElement( false );
    }

    /**
     * Tests SIMPLE_XML with splitting
     * @throws Exception
     */
    @Test
    public void testSimpleElementSplit()
            throws Exception
    {
        simpleElement( true );
    }

    private void simpleElement( final boolean split )
            throws Exception
    {
        parseDecl( new SimpleElement(), split, SIMPLE_XML );
    }

    /**
     * Tests CHILD1_XML without splitting
     * @throws Exception
     */
    @Test
    public void testChildElement1()
            throws Exception
    {
        childElement1( false );
    }

    /**
     * Tests CHILD1_XML with splitting
     * @throws Exception
     */
    @Test
    public void testChildElement1Split()
            throws Exception
    {
        childElement1( true );
    }

    private void childElement1( final boolean split )
            throws Exception
    {
        parseDecl( new ChildElement1(), split, CHILD1_XML );
    }

    /**
     * TestHandler for simple tests
     */
    private class SimpleElement
            extends TestHandler
    {

        boolean startStream;
        boolean endStream;

        private final String getMsg( final String uri, final String localName,
                                     final String qName )
        {
            return uri + " " + localName + " " + qName;
        }

        @Override
        public void resetHandler()
                throws Exception
        {
            startStream = endStream = false;
            super.resetHandler();
        }

        @Override
        public void assertHandler()
                throws Exception
        {
            assertTrue( "stream start missing", startStream );
            assertTrue( "stream end missing", endStream );
        }

        @Override
        public void elementDecl( final String name, final String model )
                throws SAXException
        {
            super.elementDecl( name, model );
        }

        private boolean isStream( final String localName )
        {
            return STREAM.equals( localName );
        }

        @Override
        public void startElement( final String uri,
                                  final String localName,
                                  final String qName,
                                  Attributes attributes )
                throws SAXException
        {

            final String msg = getMsg( uri, localName, qName );
            assertNotNull( "start localname " + msg, localName );

            // The SAX api declares this, attributes may be empty not null
            assertNotNull( "start attributes " + msg, attributes );

            if( isStream( localName ) )
            {
                startStream = true;
            }
        }

        @Override
        public void endElement( final String uri,
                                final String localName,
                                final String qName )
                throws SAXException
        {

            final String msg = getMsg( uri, localName, qName );

            // We have had an end before a start - FIXME this needs to nest
            assertTrue( "end without start " + msg, startStream );

            assertNotNull( "end localname " + msg, localName );

            if( isStream( localName ) )
            {
                endStream = true;
            }
        }
    }

    /**
     * TestHandler for child1 tests, inherits simple
     */
    private class ChildElement1
            extends SimpleElement
    {

        boolean startNode1;
        boolean endNode1;

        @Override
        public void resetHandler()
                throws Exception
        {
            startNode1 = endNode1 = false;
            super.resetHandler();
        }

        @Override
        public void assertHandler()
                throws Exception
        {
            super.assertHandler();

            assertTrue( "node1 start missing", startNode1 );
            assertTrue( "node1 end missing", endNode1 );
        }

        private boolean isNode1( final String localName )
        {
            return NODE1.equals( localName );
        }

        @Override
        public void startElement( final String uri,
                                  final String localName,
                                  final String qName,
                                  Attributes attributes )
                throws SAXException
        {
            super.startElement( uri, localName, qName, attributes );

            if( isNode1( localName ) )
            {
                assertFalse( "node1 start already received?", startNode1 );
                assertTrue( "node1 start before stream start", startStream );
                assertFalse( "node1 end before it started", endStream );
                startNode1 = true;
            }
        }

        @Override
        public void endElement( final String uri,
                                final String localName,
                                final String qName )
                throws SAXException
        {
            super.endElement( uri, localName, qName );

            if( isNode1( localName ) )
            {
                assertFalse( "node1 end already received?", endNode1 );
                assertTrue( "node1 end before stream start", startStream );
                assertFalse( "node1 end after stream end", endStream );
                endNode1 = true;
            }
        }
    }
}
