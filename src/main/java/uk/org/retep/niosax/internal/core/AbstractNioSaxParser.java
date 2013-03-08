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
package uk.org.retep.niosax.internal.core;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.NamespaceSupport;
import uk.org.retep.niosax.NioSaxParser;
import uk.org.retep.niosax.NioSaxParserHandler;
import uk.org.retep.niosax.UndeclaredNamespaceException;
import uk.org.retep.niosax.internal.helper.XmlSpec;

/**
 * Base implementation of {@link NioSaxParser}
 *
 * @author peter
 * @since 9.10
 */
public abstract class AbstractNioSaxParser
        implements NioSaxParser
{

    private final NamespaceSupport namespaceSupport;
    private ContentHandler handler;
    private LexicalHandler lexicalHandler;
    private NioSaxParserHandler nioSaxParserHandler;
    // The current ParserDelegate in use.
    private ParserDelegate parserState;

    public AbstractNioSaxParser()
    {
        namespaceSupport = new NamespaceSupport();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ContentHandler getHandler()
    {
        return handler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setHandler( final ContentHandler handler )
    {
        if( handler == null )
        {
            throw new NullPointerException();
        }

        this.handler = handler;

        if( handler instanceof LexicalHandler )
        {
            lexicalHandler = (LexicalHandler) handler;
        }

        if( handler instanceof NioSaxParserHandler )
        {
            nioSaxParserHandler = (NioSaxParserHandler) handler;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final LexicalHandler getLexicalHandler()
    {
        return lexicalHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setLexicalHandler( final LexicalHandler lexicalHandler )
    {
        this.lexicalHandler = lexicalHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final NioSaxParserHandler getNioSaxParserHandler()
    {
        return nioSaxParserHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setNioSaxParserHandler(
            final NioSaxParserHandler nioSaxParserHandler )
    {
        this.nioSaxParserHandler = nioSaxParserHandler;
    }

    /**
     * The current {@link ParserDelegate}
     * @param <T> type of {@link ParserDelegate}
     * @return the current {@link ParserDelegate}
     */
    @SuppressWarnings( "unchecked" )
    protected final <T extends ParserDelegate> T getParserState()
    {
        return (T) parserState;
    }

    /**
     * Set the current {@link ParserDelegate} that this instance will delegate
     * parsing to. This is usually called by the delegates constructor
     * @param parserState {@link ParserDelegate} that will handle parsing.
     */
    final void setParserState( final ParserDelegate parserState )
    {
        this.parserState = parserState;
    }

    /**
     * Finishes the current {@link ParserDelegate}, cleaning up any resources
     * it may have and makes its parent the active one.
     * @param <T> type of {@link ParserDelegate}
     * @return the new active {@link ParserDelegate}
     */
    public final <T extends ParserDelegate> T finish()
    {
        parserState.cleanup();
        parserState = parserState.getParent();
        return this.<T>getParserState();
    }

    /**
     * Delegates the parser to {@link Prolog} instance compatible with this parser
     * @return {@link Prolog}
     * @throws org.xml.sax.SAXException on failure
     */
    public abstract Prolog delegateProlog()
            throws SAXException;

    /**
     * {@inheritDoc }
     */
    @Override
    public final void startDocument()
            throws SAXException
    {
        if( parserState != null )
        {
            throw new SAXException( "Document already started" );
        }

        // Reset the NamespaceSupport
        namespaceSupport.reset();
        namespaceSupport.setNamespaceDeclUris( true );
        namespaceSupport.pushContext();

        try
        {
            delegateProlog();
            getHandler().startDocument();
        }
        catch( SAXException sex )
        {
            // Remove the document then rethrow
            finish();
            throw sex;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final void endDocument()
            throws SAXException
    {
        try
        {
            getHandler().endDocument();
        }
        finally
        {
            // Regardless of how endDocument finished, cleanup
            while( parserState != null )
            {
                finish();
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final NamespaceSupport getNamespaceSupport()
    {
        return namespaceSupport;
    }

    /**
     * Declare a Namespace prefix. All prefixes must be declared before they are
     * referenced.
     *
     * <p>
     *  This is similar to {@code getNamespaceSupport().declarePrefix(prefix,uri)}
     *  and throwing SAXException if that method returned false.
     * </p>
     *
     * @param prefix The prefix to declare, or the empty string to indicate the
     * default element namespace. This may never have the value "xml" or "xmlns".
     * @param uri The Namespace URI to associate with the prefix.
     * @throws org.xml.sax.SAXException if the prefix was not legal.
     * @see org.xml.sax.helpers.NamespaceSupport#declarePrefix(String, String)
     */
    public final void declarePrefix( final String prefix, final String uri )
            throws SAXException
    {
        handler.startPrefixMapping( prefix, uri );

        if( !namespaceSupport.declarePrefix( prefix, uri ) )
        {
            throw new SAXException(
                    "Illegal namespace declaration " + prefix + "=\"" + uri + "\"" );
        }

        handler.endPrefixMapping( prefix );
    }

    /**
     * Convenience method to process a raw XML qualified name, after all
     * declarations in the current context have been handled by
     * @param name qName to process
     * @param attribute true if the name is for an attribute, false for an element
     * @return String[] containing the processed qName
     * @throws org.xml.sax.SAXException
     * @see org.xml.sax.helpers.NamespaceSupport#processName(String, String[], boolean)
     * @see XmlSpec#NAMESPACEURI
     * @see XmlSpec#LOCALNAME
     * @see XmlSpec#QNAME
     */
    public final String[] processName( final String name,
                                       final boolean attribute )
            throws SAXException
    {
        final String[] qName = new String[ XmlSpec.PROCESSED_NAME_SIZE ];

        if( namespaceSupport.processName( name, qName, attribute ) == null )
        {
            throw new UndeclaredNamespaceException( name );
        }

        return qName;
    }

    /**
     * Convenience method for {@link org.xml.sax.helpers.NamespaceSupport#pushContext() }
     */
    public final void pushNamespaceSupportContext()
    {
        namespaceSupport.pushContext();
    }

    /**
     * Convenience method for {@link org.xml.sax.helpers.NamespaceSupport#pushContext() }
     */
    public final void popNamespaceSupportContext()
    {
        namespaceSupport.popContext();
    }
}
