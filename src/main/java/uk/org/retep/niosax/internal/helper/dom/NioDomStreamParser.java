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
 * <p>
 *  Unlike {@link NioDomParser}, this class will send notifications to the
 *  application when the root node has either started, finished or if a node at
 *  a specific depth within the dom tree has completed.
 * </p>
 *
 * <p>
 *  In addition, {@link org.w3c.dom.Node}'s are only attached to their parent when their
 *  depth is below the triggerDepth. This means that when the handler is notified
 *  of the node, that node and it's children are available to be garbage collected
 *  as they are no longer of interest.
 * </p>
 *
 * <p>
 *  This class is mainly of use when parsing an xml stream like
 *  <a href="http://xmpp.org/rfcs/rfc3920.html#streams">XMPP / RFC3920</a>.
 * </p>
 *
 * @author peter
 * @see NioDomParser
 * @since 9.10
 */
public class NioDomStreamParser
        extends AbstractNioDomParser
{

    private final int triggerDepth;
    private int depth;

    /**
     * Construct a handler using the supplied listener.
     * This will create a standard namespace aware {@link org.w3c.dom.Document}.
     *
     * @param triggerDepth the depth in the document to trigger events
     * @param listener {@link Listener} to receive events
     * @throws javax.xml.parsers.ParserConfigurationException if the {@link org.w3c.dom.Document} could not
     * be created
     * @throws IllegalArgumentException if triggerDepth &lt; 1
     */
    public NioDomStreamParser( final int triggerDepth,
                               final StreamListener listener )
            throws ParserConfigurationException
    {
        super( listener );
        this.triggerDepth = triggerDepth;
        if( triggerDepth < 1 )
        {
            throw new IllegalArgumentException( "triggerDepth < 1" );
        }
    }

    /**
     * Construct a handler using the supplied listener and document
     * @param triggerDepth the depth in the document to trigger events
     * @param listener {@link Listener} to receive events
     * @param document {@link org.w3c.dom.Document} to use
     * @throws IllegalArgumentException if triggerDepth &lt; 1
     */
    public NioDomStreamParser( final int triggerDepth,
                               final Document document,
                               final StreamListener listener )
    {
        super( document, listener );
        this.triggerDepth = triggerDepth;
        if( triggerDepth < 1 )
        {
            throw new IllegalArgumentException( "triggerDepth < 1" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void appendElement( final Node child )
    {
        depth++;

        if( depth == 1 )
        {
            this.<StreamListener>getListener().startRootNode( node );
        }

        if( depth > triggerDepth )
        {
            node.appendChild( child );
        }

        node = child;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement( final String uri,
                            final String localName,
                            final String qName )
            throws SAXException
    {
        if( depth == triggerDepth )
        {
            this.<StreamListener>getListener().nodeTriggered( node );
        }

        if( depth == 1 )
        {
            this.<StreamListener>getListener().endRootNode( node );
        }

        if( depth > triggerDepth )
        {
            node = node.getParentNode();
        }
        else
        {
            node = null;
        }

        depth--;
    }

    /**
     * The current depth in the document. The root element has depth 1, it's
     * immediate children 2 and so on.
     *
     * @return the current depth in the document
     */
    public final int getDepth()
    {
        return depth;
    }

    /**
     * Interface a class needs to implement to be notified when a node at the
     * correct depth in a document has been completed
     */
    public static interface StreamListener
            extends Listener
    {

        /**
         * A node at the defined depth within a document has been completed.
         * Once this method has been called, the {@link org.w3c.dom.Node} is no longer
         * referenced by the Document so the handler can use that node as it
         * sees fit.
         *
         * @param node {@link org.w3c.dom.Node}
         */
        void nodeTriggered(final Node node);

        /**
         * Called when the root node has started. All of it's attributes will
         * be present but it will not have any children.
         *
         * <p>
         *  The implementing class must not alter the provided node.
         * </p>
         *
         * @param node the root {@link org.w3c.dom.Node}
         */
        void startRootNode(final Node node);

        /**
         * Called when the root node has completed. Although it is the root,
         * unless trigger depth was set to 1, it will not have any children.
         *
         * @param node
         */
        void endRootNode(final Node node);
    }

    /**
     * An adapter class implementing the methods in the {@link uk.org.retep.niosax.internal.helper.dom.NioDomStreamParser.StreamListener}
     * interface. The methods do nothing.
     */
    public static class StreamListenerAdapter
            extends ListenerAdapter
            implements StreamListener
    {

        /**
         * {@inheritDoc}
         */
        @Override
        public void nodeTriggered( Node node )
        {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startRootNode( Node node )
        {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endRootNode( Node node )
        {
        }
    }
}
