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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import uk.org.retep.niosax.internal.helper.DefaultNioSaxParserHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * A {@link uk.org.retep.niosax.NioSaxParserHandler} implementation which
 * generates a DOM {@link org.w3c.dom.Document} from an {@link uk.org.retep.niosax.NioSaxParser}.
 *
 * @author peter
 * @since 9.10
 */
public abstract class AbstractNioDomParser
        extends DefaultNioSaxParserHandler {

    private final Listener listener;
    /**
     * The {@link org.w3c.dom.Document}
     */
    protected final Document document;
    /**
     * The current {@link org.w3c.dom.Node}
     */
    protected Node node;
    /**
     * Are we within a CDATA block
     */
    boolean inCData;

    /**
     * Construct a handler using the supplied listener.
     * This will create a standard namespace aware {@link org.w3c.dom.Document}.
     *
     * @param listener {@link uk.org.retep.niosax.internal.helper.dom.AbstractNioDomParser.Listener} to receive events
     * @throws javax.xml.parsers.ParserConfigurationException
     *          if the {@link org.w3c.dom.Document} could not
     *          be created
     */
    public AbstractNioDomParser(final Listener listener)
            throws ParserConfigurationException {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        document = dbf.newDocumentBuilder().newDocument();
        this.listener = listener;
    }

    /**
     * Construct a handler using the supplied listener and document
     *
     * @param document {@link org.w3c.dom.Document} to use
     * @param listener {@link uk.org.retep.niosax.internal.helper.dom.AbstractNioDomParser.Listener} to receive events
     */
    public AbstractNioDomParser(final Document document,
                                final Listener listener) {
        this.document = document;
        this.listener = listener;
    }

    @SuppressWarnings("unchecked")
    protected final <T extends Listener> T getListener() {
        return (T) listener;
    }

    /**
     * The {@link org.w3c.dom.Document}
     *
     * @return the {@link org.w3c.dom.Document}
     */
    public final Document getDocument() {
        return document;
    }

    @Override
    public void startElement(final String uri,
                             final String localName,
                             final String qName,
                             final Attributes attributes)
            throws SAXException {
        Element e = null;

        if (null == uri || uri.isEmpty ()) {
            e = document.createElement(localName);
        } else {
            e = document.createElementNS(uri, qName);
        }
        // e.setPrefix() ?

        final int count = attributes.getLength();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                e.setAttributeNS(attributes.getURI(i),
                        attributes.getLocalName(i),
                        attributes.getValue(i));
            }
        }

        appendElement(node);
    }

    /**
     * Append the child {@link org.w3c.dom.Node} to the document
     *
     * @param child the child {@link org.w3c.dom.Node} to append
     */
    protected abstract void appendElement(final Node child);

    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException {
        final String s = String.valueOf(ch, start, length);
        if (inCData) {
            node.appendChild(document.createCDATASection(s));
        } else {
            node.appendChild(document.createTextNode(s));
        }
    }

    @Override
    public void startCDATA()
            throws SAXException {
        inCData = true;
    }

    @Override
    public void endCDATA()
            throws SAXException {
        inCData = false;
    }

    @Override
    public void comment(final char[] ch, final int start, final int length)
            throws SAXException {
        if (node != null) {
            node.appendChild(document.createComment(String.valueOf(ch,
                    start,
                    length)));

        }
    }

    @Override
    public void startDocument()
            throws SAXException {
        if (listener != null) {
            listener.startDocument(document);
        }
    }

    @Override
    public void endDocument()
            throws SAXException {
        if (listener != null) {
            listener.endDocument(document);
        }
    }

    /**
     * Listener called by this class to notify a class when the document is ready.
     */
    public static interface Listener {

        /**
         * Called when the document is started.
         *
         * @param document {@link org.w3c.dom.Document}
         */
        void startDocument(Document document);

        /**
         * Called when the document is complete.
         *
         * @param document {@link org.w3c.dom.Document}
         */
        void endDocument(Document document);
    }

    /**
     * An adapter class implementing the methods in the {@link uk.org.retep.niosax.internal.helper.dom.AbstractNioDomParser.Listener}
     * interface. The methods do nothing.
     */
    public static class ListenerAdapter
            implements Listener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void startDocument(Document document) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endDocument(Document document) {
        }
    }
}
