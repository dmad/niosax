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

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.org.retep.niosax.NioSaxParser;
import uk.org.retep.niosax.NioSaxParserFactory;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.helper.DefaultNioSaxParserHandler;
import uk.org.retep.niosax.charset.Charset;
import uk.org.retep.niosax.charset.CharsetFactory;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the parsing of the XML declaration
 * 
 * @author peter
 */
public abstract class BaseSaxTest

{

    public static final String UTF8 = "UTF-8";
    public static final String DECL = "<?xml version='1.1' encoding='" + UTF8 + "'?>\n";

    protected final Charset getCharset( final String name )
    {
        final Charset cs = CharsetFactory.getCharset( name );
        assertNotNull( "Unsupported charset " + name, cs );
        return cs;
    }

    protected final ByteBuffer createBuffer( final NioSaxSource source,
                                             final String xml,
                                             final Object... args )
    {
        return createBuffer( source.getCharset(), xml, args );
    }

    protected final ByteBuffer createBuffer( final String cs,
                                             final String xml,
                                             final Object... args )
    {
        return createBuffer( getCharset( cs ), xml, args );
    }

    protected final ByteBuffer createBuffer( final Charset cs,
                                             final String xml,
                                             final Object... args )
    {
        ByteBuffer buffer = ByteBuffer.allocate( 1024 );
        buffer.clear();

        cs.write( buffer, String.format( xml, args ).toCharArray() );

        buffer.flip();
        return buffer;
    }

    protected final NioSaxParser createParser( final ContentHandler handler )
    {
        return NioSaxParserFactory.getInstance().newInstance( handler );
    }

    /**
     * Common tests, parses without and with an xml declaration
     *
     * @param handler TestHandler for this test
     * @param split to split or not to split
     * @param xml xml to parse
     * @param args args for xml
     * @throws Exception on failure
     */
    protected void parseDecl( final TestHandler handler,
                              final boolean split,
                              final String xml,
                              final Object... args )
            throws Exception
    {
            // A raw element (no xml declaration)
        parseXML( handler, split, xml, args );

        // Include the xml declaration
        parseXML( handler, split, DECL + xml, args );
    }

    /**
     * The same as {@code parser.parse(buffer);} but this will break the buffer
     * into two to simulate incomplete data being received - a major requirement
     * of the parser.
     * 
     * @param parser NioSaxParser
     * @param source NioSaxSource
     * @param splitContent if true, then the buffer is split in two before parsing
     * @throws org.xml.sax.SAXException if the parse fails
     */
    protected final void parse( final NioSaxParser parser,
                                final NioSaxSource source,
                                final boolean splitContent )
            throws SAXException
    {
        if( splitContent )
        {
            final ByteBuffer buffer = source.getByteBuffer();

            // Work out where to split
            final int split = buffer.limit() >> 1;
            assertTrue( "Buffer cannot be split, length=" + buffer.limit(),
                        split > 0 );

            // Now split. Here we set the position to the split point and create
            // a slice which will hold the right hand side of the split.
            buffer.position( split );
            final ByteBuffer right = buffer.slice();

            // We now flip the original buffer. position will revert back to 0 and
            // the limit will be at the split point
            buffer.flip();

            // Now parse the two in the correct order
            parser.parse( source );

            source.setByteBuffer( right );
            parser.parse( source );
        }
        else
        {
            // Pass to the parser in one go
            parser.parse( source );
        }
    }

    /**
     * Convenience test case used for the majority of tests
     * 
     * @param handler {@link uk.org.retep.niosax.internal.core.delegate.BaseSaxTest.TestHandler}
     * @param splitContent if true, then the buffer is split in two before parsing
     * @param xml XML to parse
     * @param args optional args for the xml
     * @throws Exception if the test fails
     */
    protected final void parseXML( final TestHandler handler,
                                   final boolean splitContent,
                                   final String xml,
                                   final Object... args )
            throws Exception
    {
        // Create a parser
        final NioSaxParser parser = createParser( handler );

        // Form the xml we are going to test
        final NioSaxSource source = new NioSaxSource();
        source.setByteBuffer( createBuffer( source, xml, args ) );

        // Now parse it
        handler.resetHandler();
        parser.startDocument();
        try
        {
            parse( parser, source, splitContent );
        }
        finally
        {
            parser.endDocument();
        }

        // ensure we received the responses
        handler.assertHandler();
    }

    /**
     *  {@link DefaultNioSaxParserHandler} useful for testing
     */
    public abstract class TestHandler
            extends DefaultNioSaxParserHandler
    {

        /**
         * Called prior to a test to reset the handler
         * @throws Exception
         */
        public void resetHandler()
                throws Exception
        {
        }

        /**
         * Called by a test case to run asserts specific to this handler
         * @throws Exception
         */
        public abstract void assertHandler()
                throws Exception;
    }
}
