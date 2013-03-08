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
package uk.org.retep.niosax.internal.helper;

import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import uk.org.retep.niosax.internal.core.AbstractNioSaxParser;
import static uk.org.retep.niosax.internal.helper.XmlSpec.*;

/**
 * A collection of qName/value pairs. This collection is used to hold attributes
 * whilst they are being parsed. Once parsing is complete, it parses the qNames
 * for namespace declarations, declares them in the current scope
 * @author peter
 */
public class AttributeList
{

    private final AbstractNioSaxParser parser;
    private String[][] buf;
    private int count;

    public AttributeList( final AbstractNioSaxParser parser )
    {
        this.parser = parser;
        buf = new String[ 10 ][];
        count = 0;
    }

    public void addAttribute( final String qName, final String value )
    {
        final int newcount = count + 1;
        if( newcount > buf.length )
        {
            buf = Arrays.copyOf( buf, Math.max( buf.length << 1, newcount ) );
        }

        buf[count] = new String[]
                {
                    qName, value
                };

        count = newcount;
    }

    public int size()
    {
        return count;
    }

    public boolean isEmpty()
    {
        return count == 0;
    }

    public boolean processNames()
            throws SAXException
    {
        boolean newContext = false;

        for( int i = 0; i < count; i++ )
        {
            final String qName = buf[i][0];
            if( qName.startsWith( XMLNS ) )
            {
                if( !newContext )
                {
                    parser.pushNamespaceSupportContext();
                    newContext = true;
                }

                final int idx = qName.indexOf( ':' ) + 1;
                if( idx == 0 )
                {
                    // The default namespace
                    parser.declarePrefix( "", buf[i][1] );
                }
                else
                {
                    parser.declarePrefix( qName.substring( idx ), buf[i][1] );
                }
            }
        }

        return newContext;
    }

    public Attributes getAttributes()
            throws SAXException
    {
        final AttributesImpl ai = new AttributesImpl();

        for( int i = 0; i < count; i++ )
        {
            final String qName[] = parser.processName( buf[i][0], true );
            ai.addAttribute( qName[NAMESPACEURI],
                             qName[LOCALNAME],
                             qName[QNAME],
                             // CDATA" as stated in the XML 1.0 Recommentation
                             // clause 3.3.3, Attribute-Value Normalization
                             TYPE_CDATA,
                             buf[i][1] );
        }

        return ai;
    }
}
