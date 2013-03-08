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
package uk.org.retep.niosax.internal.helper.dom;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * A {@link AbstractNioDomParser} implementation which will generate a DOM tree
 * which is populated by a {@link uk.org.retep.niosax.NioSaxParser}.
 *
 * @author peter
 * @see NioDomStreamParser
 * @since 9.10
 */
public class NioDomParser
        extends AbstractNioDomParser
{

    /**
     * Construct a handler using the supplied listener.
     * This will create a standard namespace aware {@link org.w3c.dom.Document}.
     *
     * @throws javax.xml.parsers.ParserConfigurationException if the {@link org.w3c.dom.Document} could not
     * be created
     */
    public NioDomParser()
            throws ParserConfigurationException
    {
        this( new ListenerAdapter() );
    }

    /**
     * Construct a handler using the supplied listener and document
     * @param document {@link org.w3c.dom.Document} to use
     */
    public NioDomParser( final Document document )
    {
        this( document, new ListenerAdapter() );
    }

    /**
     * Construct a handler using the supplied listener.
     * This will create a standard namespace aware {@link org.w3c.dom.Document}.
     *
     * @param listener {@link uk.org.retep.niosax.internal.helper.dom.AbstractNioDomParser.Listener} to receive events
     * @throws javax.xml.parsers.ParserConfigurationException if the {@link org.w3c.dom.Document} could not
     * be created
     */
    public NioDomParser( final Listener listener )
            throws ParserConfigurationException
    {
        super( listener );
    }

    /**
     * Construct a handler using the supplied listener and document
     * @param document {@link org.w3c.dom.Document} to use
     * @param listener {@link uk.org.retep.niosax.internal.helper.dom.AbstractNioDomParser.Listener} to receive events
     */
    public NioDomParser( final Document document, final Listener listener )
    {
        super( document, listener );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void appendElement( final Node child )
    {
        if( node == null )
        {
            document.appendChild( child );
        }
        else
        {
            node.appendChild( child );
        }
        node = child;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement( final String uri, final String localName,
                            final String qName )
            throws SAXException
    {
        node = node.getParentNode();
    }
}
