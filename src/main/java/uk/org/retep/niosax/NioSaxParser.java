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
package uk.org.retep.niosax;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * A SAX style XML Parser that takes its input from a {@link java.nio.ByteBuffer} and
 * passes events to a {@link org.xml.sax.ContentHandler}.
 *
 * @author peter
 * @see NioSaxParserFactory
 * @see org.xml.sax.ContentHandler
 * @see org.xml.sax.ext.LexicalHandler
 * @see uk.org.retep.niosax.NioSaxParserHandler
 * @since 9.10
 */
public interface NioSaxParser
{

    /**
     * The SAX {@link org.xml.sax.ContentHandler} to receive events
     * @return SAX {@link org.xml.sax.ContentHandler} to receive events
     */
    ContentHandler getHandler();

    /**
     * Set the SAX {@link org.xml.sax.ContentHandler} to receive events.
     *
     * <p>
     *  If the handler also implements {@link org.xml.sax.ext.LexicalHandler} then this method
     *  will also call {@link #setLexicalHandler(org.xml.sax.ext.LexicalHandler)}.
     * </p>
     *
     * <p>
     *  If the handler also implements {@link NioSaxParserHandler} then this method
     *  will also call {@link #setNioSaxParserHandler(uk.org.retep.niosax.NioSaxParserHandler)}.
     * </p>
     *
     * @param handler SAX {@link org.xml.sax.ContentHandler} to receive events
     * @throws NullPointerException if handler is null
     */
    void setHandler(ContentHandler handler);

    /**
     * The SAX {@link org.xml.sax.ext.LexicalHandler} to receive events.
     * 
     * @return {@link org.xml.sax.ext.LexicalHandler} or null if not in use.
     */
    LexicalHandler getLexicalHandler();

    /**
     * Set the SAX {@link org.xml.sax.ext.LexicalHandler} to receive events.
     * @param lexicalHandler the SAX {@link org.xml.sax.ext.LexicalHandler} to receive events
     */
    void setLexicalHandler(LexicalHandler lexicalHandler);

    /**
     * The {@link NioSaxParserHandler} to receive events.
     * 
     * @return {@link NioSaxParserHandler} or null if not in use.
     */
    NioSaxParserHandler getNioSaxParserHandler();

    /**
     * Set the {@link NioSaxParserHandler} to receive events.
     *
     * @param nioSaxParserHandler {@link NioSaxParserHandler} or null if none.
     */
    void setNioSaxParserHandler(NioSaxParserHandler nioSaxParserHandler);

    /**
     * This must be called by client code before passing any data to this
     * {@link uk.org.retep.niosax.NioSaxParser}. It initialises the parser for a new document and
     * notifies the attached {@link org.xml.sax.ContentHandler}.
     * 
     * @throws org.xml.sax.SAXException on failure
     */
    void startDocument()
            throws SAXException;

    /**
     * Client code should call this once the document has been completed. It
     * notifies the attached {@link org.xml.sax.ContentHandler} and cleans up the parser.
     *
     * @throws org.xml.sax.SAXException on failure
     */
    void endDocument()
            throws SAXException;

    /**
     * Parse the content of a {@link java.nio.ByteBuffer} for SAX events
     * @param input {@link java.nio.ByteBuffer} containing data to parse
     * @throws org.xml.sax.SAXException if the content is invalid
     */
    void parse(NioSaxSource input)
            throws SAXException;

    /**
     * The {@link org.xml.sax.helpers.NamespaceSupport} instance attached to this parser
     * @return {@link org.xml.sax.helpers.NamespaceSupport} instance attached to this parser
     */
    NamespaceSupport getNamespaceSupport();
}
