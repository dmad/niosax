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

import org.xml.sax.SAXException;
import uk.org.retep.niosax.NioSaxSource;

/**
 * Abstract class for all {@link ParserDelegate} implementations that have a
 * parent.
 *
 * <p>
 *  By convention it's usual for implementations to have either private or
 *  protected constructors and have a public static method to obtain a new
 *  instance. For example:
 * </p>
 *
 * <code><pre>
 *  public static &lt;P extends {@linkplain ParserDelegate ParserDelegate}&gt; Comment&lt;P&gt; delegate( final P parent )
 *      {
 *          return new Comment&lt;P&gt;( parent );
 *      }
 * </pre></code>
 *
 * <p>
 *  The reason for this is to allow for custom implementations to be provided
 *  based on the parent. For example the {@link uk.org.retep.niosax.internal.core.delegate.ProcessingInstruction} class
 *  provides two delegate methods, one for normal use the other specifically
 *  for use by {@link uk.org.retep.niosax.internal.core.Prolog}. The latter instance allows for xml and DOCTYPE
 *  declarations whilst the former prohibits them.
 * </p>
 *
 * @param <P> The parent {@link ParserDelegate} type
 * @author peter
 * @since 9.10
 */
public abstract class AbstractParserDelegate<P extends ParserDelegate>
        extends ParserDelegate
{

    private final P parent;

    /**
     * Constructor used by all {@link ParserDelegate} implementations.
     * @param parent The parent {@link ParserDelegate}
     * @throws NullPointerException if parent is null
     */
    public AbstractParserDelegate( final P parent )
    {
        super( parent.getParser() );
        this.parent = parent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public final P getParent()
    {
        return parent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void parse( final NioSaxSource source )
            throws SAXException
    {
        char c = source.decode();
        while( source.isValid( c ) && parse( source, c ) )
        {
            c = source.decode();
        }
    }

    /**
     * Parse a character provided by the main {@link #parse(uk.org.retep.niosax.NioSaxSource) }
     * method.
     *
     * @param source {@link NioSaxSource}
     * @param c char read
     * @return true if processing should continue, false to abort the loop
     * @throws org.xml.sax.SAXException if the parse fails
     */
    public abstract boolean parse( final NioSaxSource source, final char c )
            throws SAXException;
}
