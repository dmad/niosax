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
import uk.org.retep.niosax.NioSaxParserHandler;
import uk.org.retep.niosax.NioSaxSource;

/**
 * Abstract class for the individual parsers
 * @param <P> The parent AbstractParserDelegate type
 * @author peter
 * @since 9.10
 */
public abstract class ParserDelegate<P extends ParserDelegate>
{

    protected final AbstractNioSaxParser parser;

    /**
     * @param parser The parser to attach to
     */
    public ParserDelegate( final AbstractNioSaxParser parser )
    {
            this.parser = parser;
            parser.setParserState( this );
    }

    /**
     * Hook called by the parser when the parsers {@link AbstractNioSaxParser#finish()}
     * method is invoked. Subclasses should use this method to release resources
     * and must call {@code super.cleanup()} at the end of their call.
     */
    public void cleanup()
    {
    }

    /**
     * The parent {@link uk.org.retep.niosax.internal.core.ParserDelegate} to this one.
     * @return Parent or null if this is the root. i.e. {@link Prolog}.
     */
    public abstract  P getParent();

    /**
     * The {@link AbstractNioSaxParser} this {@link AbstractParserDelegate} is
     * attached to.
     * @return {@link AbstractNioSaxParser} this {@link AbstractParserDelegate}
     * is attached to.
     */
    public final AbstractNioSaxParser getParser()
    {
        return parser;
    }

    /**
     * Finish this state and return to the parent.
     * Equivalent to {@code getParser().finish()}.
     * 
     * <p>
     *  Normally this is used in a subclass as {@code return finish();} so that
     *  the main loop in {@link #parse(uk.org.retep.niosax.NioSaxSource)} will terminate.
     * </p>
     *
     * @return false
     */
    public final boolean finish()
    {
        parser.finish();
        return false;
    }

    /**
     * Convenience method, identical to {@code getParser().getHandler()}.
     *
     * <p>
     * You must not cache the {@link org.xml.sax.ContentHandler} outside of the scope of
     * the method it is used in as it can change during the lifetime of the document being parsed.
     * </p>
     *
     * @return {@link org.xml.sax.ContentHandler} currently in use
     */
    public final ContentHandler getHandler()
    {
        return parser.getHandler();
    }

    /**
     * Convenience method, identical to {@code getParser().getLexicalHandler()}.
     *
     * <p>
     * You must not cache the {@link org.xml.sax.ext.LexicalHandler} outside of the scope of
     * the method it is used in as it can change during the lifetime of the document being parsed.
     * </p>
     *
     * @return {@link org.xml.sax.ext.LexicalHandler} or null if the handler does not implement
     * the {@link org.xml.sax.ext.LexicalHandler} interface.
     */
    public final LexicalHandler getLexicalHandler()
    {
        return parser.getLexicalHandler();
    }

    /**
     * Convenience method, identical to {@code getParser().getNioSaxParserHandler()}.
     *
     * <p>
     * You must not cache the {@link NioSaxParserHandler} outside of the scope of
     * the method it is used in as it can change during the lifetime of the document being parsed.
     * </p>
     *
     * @return {@link NioSaxParserHandler} or null if the handler does not implement
     * the {@link NioSaxParserHandler} interface.
     */
    public final NioSaxParserHandler getNioSaxParserHandler()
    {
        return parser.getNioSaxParserHandler();
    }

    /**
     * Called by {@link AbstractNioSaxParser} to parse the input.
     *
     * @param source {@link NioSaxSource}
     * @throws org.xml.sax.SAXException if the parse fails
     */
    public abstract void parse( final NioSaxSource source )
            throws SAXException;

}
