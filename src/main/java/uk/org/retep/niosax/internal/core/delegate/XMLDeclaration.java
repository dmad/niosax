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

import com.google.common.base.Strings;
import org.xml.sax.SAXException;
import uk.org.retep.niosax.IllegalCharacterException;
import uk.org.retep.niosax.NioSaxParser;
import uk.org.retep.niosax.NioSaxParserHandler;
import uk.org.retep.niosax.NioSaxSource;
import uk.org.retep.niosax.internal.core.Prolog;
import uk.org.retep.niosax.internal.core.StateEngine;
import uk.org.retep.niosax.internal.core.StateEngineDelegate;
import uk.org.retep.niosax.charset.Charset;
import uk.org.retep.niosax.charset.CharsetFactory;

import static uk.org.retep.niosax.internal.helper.XmlSpec.isNameChar;
import static uk.org.retep.niosax.internal.helper.XmlSpec.isNameStartChar;
import static uk.org.retep.niosax.internal.helper.XmlSpec.isWhitespace;
import static uk.org.retep.niosax.internal.helper.XmlSpec.stateCompleted;

/**
 * {@link uk.org.retep.niosax.internal.core.ParserDelegate} implementation for parsing
 * an xml declaration
 *
 * @author peter
 * @since 9.10
 */
public class XMLDeclaration
        extends StateEngineDelegate<Prolog> {

    private static final String VERSION = "version";
    private static final String ENCODING = "encoding";
    private static final String STANDALONE = "standalone";
    private String version = null;
    private String encoding = null;
    private boolean standalone = false;
    // scratch for the name parsed
    private String name;

    /**
     * Delegate parsining of am xml declaration
     *
     * @param parent the parent ProcessingInstruction
     * @return instance which will be active within the parser
     */
    public static XMLDeclaration delegate(final Prolog parent) {

        return new XMLDeclaration(parent);
    }

    private XMLDeclaration(final Prolog parent) {
        super(parent);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected StateEngine getInitialState() {
        return XMLState.START;
    }

    /**
     * Used by XMLState to store the name of the attribute being parsed
     *
     * @param name
     */
    protected final void setName(final String name) {
        this.name = name;
    }

    /**
     * Used by XMLState to store the stateCompleted attribute into the correct field
     *
     * @throws org.xml.sax.SAXException if the attribute is illegal
     */
    protected final void setAttribute()
            throws SAXException {
        final String value = getAppendableString();

        if (VERSION.equals(name)) {
            version = value;
        } else if (ENCODING.equals(name)) {
            encoding = value;
        } else if (STANDALONE.equals(name)) {
            standalone = parseStandalone(value);
        } else {
            throw new SAXException("Illegal attribute " + name);
        }
    }

    /**
     * Used by XMLState to finish off the declaration. It ensures the {@link NioSaxParser}
     * uses the correct {@link Charset} and notifies any {@link NioSaxParserHandler}.
     *
     * @param source {@link NioSaxSource} to change
     * @throws org.xml.sax.SAXException if the charset is not supported
     */
    protected final void invoke(final NioSaxSource source)
            throws SAXException {
        if (!Strings.isNullOrEmpty(encoding)) {
            final Charset cs = CharsetFactory.getCharset(encoding);
            if (cs == null) {
                throw new SAXException("Unsupported encoding " + encoding);
            } else {
                source.setCharset(cs);
            }
        }

        final NioSaxParserHandler handler = getNioSaxParserHandler();
        if (handler != null) {
            handler.xmlDeclaration(version, encoding, standalone);
        }

        finish();
    }

    private boolean parseStandalone(String s) throws SAXException {
        if (Strings.isNullOrEmpty(s)) {
            throw new SAXException("Unsupported standalone attribute " + s);
        }

        switch (s) {
            case "yes":
                return true;
            case "no":
                return false;
            default:
                throw new SAXException("Unsupported standalone attribute " + s);
        }
    }

    private enum XMLState
            implements StateEngine<XMLDeclaration> {

        /**
         * The initial state, used between attributes
         */
        START {
            @Override
            public StateEngine parse(final XMLDeclaration p,
                                     final NioSaxSource source,
                                     final char c)
                    throws SAXException {
                if (isWhitespace(c)) {
                    return this;
                } else if (c == '?') {
                    // End of the declaration?
                    return QUERY;
                } else if (isNameStartChar(c)) {
                    // Simply append and keep
                    p.append(c);
                    return NAME;
                } else {
                    throw new IllegalCharacterException();
                }
            }
        },
        /**
         * Found ? so the next char must be &gt;
         */
        QUERY {
            @Override
            public StateEngine parse(final XMLDeclaration p,
                                     final NioSaxSource source,
                                     final char c)
                    throws SAXException {
                if (c == '>') {
                    p.invoke(source);
                    return stateCompleted();
                } else {
                    throw new IllegalCharacterException();
                }
            }
        },
        /**
         * Parsing subsequent chars in a name
         */
        NAME {
            @Override
            public StateEngine parse(final XMLDeclaration p,
                                     final NioSaxSource source,
                                     final char c)
                    throws SAXException {
                if (c == '=') {
                    // Store the attribute name
                    p.setName(p.getAppendableString());
                    return EQUALS;
                } else if (isNameChar(c)) {
                    p.append(c);
                    return this;
                } else {
                    throw new IllegalCharacterException();
                }
            }
        },
        /**
         * we have an = so expect ' or "
         */
        EQUALS {
            @Override
            public StateEngine parse(final XMLDeclaration p,
                                     final NioSaxSource source,
                                     final char c)
                    throws SAXException {
                if (c == '\'') {
                    return QUOTE;
                } else if (c == '\"') {
                    return DOUBLEQUOTE;
                } else {
                    throw new IllegalCharacterException();
                }
            }
        },
        QUOTE {
            @Override
            public StateEngine parse(final XMLDeclaration p,
                                     final NioSaxSource source,
                                     final char c)
                    throws SAXException {
                if (c == '\'') {
                    p.setAttribute();
                    return START;
                } else {
                    p.append(c);
                    return this;
                }
            }
        },
        DOUBLEQUOTE {
            @Override
            public StateEngine parse(final XMLDeclaration p,
                                     final NioSaxSource source,
                                     final char c)
                    throws SAXException {
                if (c == '\"') {
                    p.setAttribute();
                    return START;
                } else {
                    p.append(c);
                    return this;
                }
            }
        };

        @Override
        public boolean continueLoop() {
            return true;
        }
    }
}
