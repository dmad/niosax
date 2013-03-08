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

import java.util.Arrays;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import uk.org.retep.niosax.IllegalCharacterException;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.core.ParserDelegate;
import uk.org.retep.niosax.internal.core.StateEngine;
import uk.org.retep.niosax.internal.core.StateEngineDelegate;
import uk.org.retep.niosax.internal.core.engine.CommentCDataStateEngine;
import uk.org.retep.niosax.internal.helper.Appendable;
import uk.org.retep.niosax.internal.helper.AttributeList;
import static uk.org.retep.niosax.internal.helper.XmlSpec.*;

/**
 * {@link ParserDelegate} implementation for parsing elements.
 * 
 * @author peter
 * @since 9.10
 */
public class Element
        extends StateEngineDelegate<ParserDelegate>
{

    /**
     * True if this element has created a new scope in NamespaceSupport
     */
    private boolean namespaceScope;
    /**
     * The raw qName prior to being parsed
     */
    private String qName;
    /**
     * The qName of this element. This is actually parsed into
     * [uri,localName,qName]
     */
    private String parsedQName[];
    /**
     * The element attributes, lazy init to save memory
     */
    private AttributeList attributeList;
    /**
     * cache of the attribute qName
     */
    private String attrQName;

    private StringBuilder reference;

    /**
     * Delegate of an xml element.
     * 
     * @param parent parent {@link ParserDelegate}
     * @return new instance
     */
    public static Element delegate( final ParserDelegate parent )
    {
        return new Element( parent );
    }

    private Element( final ParserDelegate parent )
    {
        super( parent );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected StateEngine getInitialState()
    {
        return StartState.NAME;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void cleanup()
    {
        // If we pushed a context due to a namespace decl then we must pop it
        if( namespaceScope )
        {
            parser.popNamespaceSupportContext();
        }

        super.cleanup();
    }

    /**
     * Used by {@link uk.org.retep.niosax.internal.core.delegate.Element.StartState} to set the qName and parse any namespaces
     */
    final void setQName()
    {
        qName = getAppendableString();
    }

    /**
     * Used by {@link uk.org.retep.niosax.internal.core.delegate.Element.StartState} to validate a close element against this one
     * @throws org.xml.sax.SAXException if the found qName is not declared then if it does
     * not match the qName of the start element
     */
    final void validateQName()
            throws SAXException
    {
        final String qn[] = parser.processName( getAppendableString(), false );

        if( !Arrays.deepEquals( parsedQName, qn ) )
        {
            throw new SAXException( String.format(
                    "Element <%s> must end with </%<s> but found </%s>",
                    parsedQName[QNAME],
                    qn[QNAME] ) );
        }
    }

    /**
     * Used by {@link uk.org.retep.niosax.internal.core.delegate.Element.StartState} to notify the handler of the start of the element
     */
    final void startElement()
            throws SAXException
    {
        Attributes attrs = null;

        if( attributeList == null || attributeList.isEmpty() )
        {
            // no attributes but we must have an instance when we notify the handler
            attrs = new AttributesImpl();
        }
        else
        {
            // process any namespace declarations in the attributes
            namespaceScope = attributeList.processNames();

            // get the attribtues to pass to the handler
            attrs = attributeList.getAttributes();
        }

        // parse the qName (which may be within the current scope)
        parsedQName = parser.processName( qName, false );

        // Now notify the handler of the start
        getHandler().startElement( parsedQName[NAMESPACEURI],
                                   parsedQName[LOCALNAME],
                                   parsedQName[QNAME],
                                   attrs );
    }

    /**
     * Used by {@link uk.org.retep.niosax.internal.core.delegate.Element.StartState} to notify the handler of the end of the element
     */
    final void endElement()
            throws SAXException
    {
        // If this throws an NPE it's because setQName has not been called
        getHandler().endElement( parsedQName[NAMESPACEURI],
                                 parsedQName[LOCALNAME],
                                 parsedQName[QNAME] );
    }

    /**
     * Used by {@link uk.org.retep.niosax.internal.core.delegate.Element.StartState} to cache an attributes qName
     */
    final void setAttrQName()
    {
        attrQName = getAppendableString();
    }

    /**
     * Used by {@link uk.org.retep.niosax.internal.core.delegate.Element.StartState} to store an attributes value
     */
    final void setAttrValue()
    {
        if( attributeList == null )
        {
            attributeList = new AttributeList( parser );
        }

        attributeList.addAttribute( attrQName, getAppendableString() );
    }

    /**
     * This state engine handles the start of an element.
     */
    private enum StartState
            implements StateEngine<Element>
    {

        /**
         * First character of the elements name
         */
        NAME
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isNameStartChar( c ) )
                {
                    e.append( c );
                    return NAME1;
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }

            }
        },
        /**
         * Subsequent characters of the elements name
         */
        NAME1
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isNameChar( c ) )
                {
                    e.append( c );
                    return this;
                }
                else if( isWhitespace( c ) )
                {
                    // the name is complete so store and start looking for attributes
                    e.setQName();
                    return AttributeState.WHITESPACE;
                }
                else if( c == '/' )
                {
                    // The name is complete so store and expect an empty element
                    e.setQName();
                    return CloseState.EMPTY;
                }
                else if( c == '>' )
                {
                    // The name is complete so store and start on content
                    e.setQName();
                    e.startElement();
                    return ContentState.CONTENT;
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }

            }
        };

        @Override
        public boolean continueLoop()
        {
            return true;
        }
    }

    /**
     * This State engine handles attributes
     */
    private enum AttributeState
            implements StateEngine<Element>
    {

        /**
         * Whitespace before any attributes
         */
        WHITESPACE
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isWhitespace( c ) )
                {
                    return this;
                }
                else if( c == '/' )
                {
                    // Empty element?
                    return CloseState.EMPTY;
                }
                else if( c == '>' )
                {
                    // end of the start element
                    e.startElement();
                    return ContentState.CONTENT;
                }
                else if( isNameStartChar( c ) )
                {
                    // first char of an attribute name
                    e.append( c );
                    return NAME;
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }
            }
        },
        /**
         * Subsequent characters of the attributes name
         */
        NAME
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isNameChar( c ) )
                {
                    e.append( c );
                    return this;
                }
                else if( c == '=' )
                {
                    // End of the attribute name, expect the value
                    e.setAttrQName();
                    return EQ;
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }

            }
        },
        /**
         * = so expect ' or "
         */
        EQ
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '\'' )
                {
                    return QUOTED_VALUE;

                }
                else if( c == '\"' )
                {
                    return DOUBLE_QUOTED_VALUE;

                }
                else
                {
                    throw new IllegalCharacterException( c );
                }

            }
        },
        /**
         * ' so expect value terminated by '
         */
        QUOTED_VALUE
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '\'' )
                {
                    e.setAttrValue();
                    return WHITESPACE;
                }
                else if( c == '&' )
                {
                    // Reference?
                    return QUOTED_REFERENCE;
                }
                else if( c == '<' )
                {
                    // < is illegal
                    throw new IllegalCharacterException( c );
                }
                else
                {
                    e.append( c );
                    return this;
                }
            }
        },
        QUOTED_REFERENCE
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isNCNameStartChar( c ) )
                {
                    // Fixme start reference delegate
                    throw new IllegalCharacterException( c );
                    //return QUOTED_DELEGATE;
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }
            }
        },
        /**
         * ' so expect value terminated by '
         */
        DOUBLE_QUOTED_VALUE
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '\"' )
                {
                    e.setAttrValue();
                    return WHITESPACE;
                }
                else if( c == '&' )
                {
                    // Reference?
                    return DOUBLE_QUOTED_REFERENCE;
                }
                else if( c == '<' )
                {
                    // < is illegal
                    throw new IllegalCharacterException( c );
                }
                else
                {
                    e.append( c );
                    return this;
                }
            }
        },
        DOUBLE_QUOTED_REFERENCE
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isNCNameStartChar( c ) )
                {
                    // Fixme start reference delegate
                    throw new IllegalCharacterException( c );
                    //return DOUBLE_QUOTED_DELEGATE;
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }
            }
        };
        // Delegates for '..' and ".." 
        private static final StateEngine<Element> QUOTED_DELEGATE =
                createDelegatedState( QUOTED_VALUE );
        private static final StateEngine<Element> DOUBLE_QUOTED_DELEGATE =
                createDelegatedState( DOUBLE_QUOTED_VALUE );

        @Override
        public boolean continueLoop()
        {
            return true;
        }
    }

    /**
     * This State engine handles child content
     */
    private enum ContentState
            implements StateEngine<Element>
    {

        /**
         * Append any content, processing child elements as they are found
         */
        CONTENT
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '<' )
                {
                    // A child element of some sort
                    characters( e );
                    return CHILD;
                }
                else if( c == '&' )
                {
                    // Reference (EntityRef or CharRef)
		    e.reference = new StringBuilder ();
		    e.reference.append (c);
		    return REFERENCE;
                }
                else
                {
                    e.append( c );
                    return this;
                }
            }
        },

	REFERENCE
	{
	    @Override
	    public StateEngine parse (final Element e,
				      final NioSaxSource source,
				      final char c)
		throws SAXException
	    {
		e.reference.append (c);

		if (';' == c) {
		    String ref = e.reference.toString ();

		    e.reference = null;

		    if ("&lt;".equals (ref))
			e.append ('<');
		    else if ("&gt;".equals (ref))
			e.append ('>');
		    else if ("&quot;".equals (ref))
			e.append ('"');
		    else if ("&apos;".equals (ref))
			e.append ('\'');
		    else if ("&amp;".equals (ref))
			e.append ('&');

		    return CONTENT;
		}

		return this;
	    }
	},

        /**
         * A Child element
         */
        CHILD
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '/' )
                {
                    // We are closing an element, so switch to the CloseState engine
                    return CloseState.NAME;
                }
                else if( c == '!' )
                {
                    // A comment or cdata
                    CommentCDataStateEngine.delegate( e );
                    return DELEGATED;
                }
                else if( c == '?' )
                {
                    ProcessingInstruction.delegate( e );
                    return DELEGATED;
                }
                else
                {
                    // A child element, pass c to it as its the first char of its name
                    Element.delegate( e ).parse( source, c );
                    return DELEGATED;
                }
            }
        };
        private static final StateEngine<Element> DELEGATED =
                createDelegatedState( CONTENT );

        @Override
        public boolean continueLoop()
        {
            return true;
        }

        /**
         * send any captured content to the handler
         */
        protected void characters( final Element e )
                throws SAXException
        {
            final Appendable a = e.getAppendable();
            if( a.size() > 0 )
            {
                e.getHandler().characters( a.getCharBuffer(),
                                           0,
                                           a.size() );
                a.reset();
            }
        }
    }

    /**
     * This State engine handles a close element (not an empty element)
     */
    private enum CloseState
            implements StateEngine<Element>
    {

        /**
         * First character of the elements name
         */
        NAME
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isNameStartChar( c ) )
                {
                    e.append( c );
                    return NAME1;
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }
            }
        },
        /**
         * Subsequent characters of the elements name
         */
        NAME1
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isNameChar( c ) )
                {
                    e.append( c );
                    return this;
                }
                else if( isWhitespace( c ) )
                {
                    return WHITESPACE;
                }
                else if( c == '>' )
                {
                    e.endElement();
                    e.finish();
                    return stateCompleted();
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }
            }
        },
        /**
         * Ignore until we find the end
         */
        WHITESPACE
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( isWhitespace( c ) )
                {
                    return this;
                }
                else if( c == '>' )
                {
                    e.validateQName();
                    e.endElement();
                    e.finish();
                    return stateCompleted();
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }
            }
        },
        /**
         * An empty element. The next char must be &gt;
         */
        EMPTY
        {

            @Override
            public StateEngine parse( final Element e,
                                      final NioSaxSource source,
                                      final char c )
                    throws SAXException
            {
                if( c == '>' )
                {
                    e.startElement();
                    e.endElement();
                    e.finish();
                    return stateCompleted();
                }
                else
                {
                    throw new IllegalCharacterException( c );
                }
            }
        };

        @Override
        public boolean continueLoop()
        {
            return true;
        }
    }
}
