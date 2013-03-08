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

/**
 * A factory of {@link NioSaxParser}'s. It is advised to use this class so that
 * the underlying implementation can be changed with little code changes.
 *
 * <p>
 *  This will use the default implementation unless the system property
 *  "retep.niosax.factory" is set. If so then it will use the class
 *  defined by that property as the factory instance.
 * </p>
 *
 * @author peter
 * @since 9.10
 */
public abstract class NioSaxParserFactory
{

    private static final String FACTORY_KEY = "retep.nioparser.factory";
    private static final String DEFAULT_FACTORY = "uk.org.retep.niosax.internal.core.DefaultNioSaxFactory";

    /**
     * Get the current {@link uk.org.retep.niosax.NioSaxParserFactory} implementation.
     * 
     * @return {@link uk.org.retep.niosax.NioSaxParserFactory} implementation.
     */
    public static NioSaxParserFactory getInstance()
    {
        return FactoryHolder.getInstance();
    }

    /**
     * Create a new {@link NioSaxParser}
     * @return {@link NioSaxParser}
     */
    public abstract NioSaxParser newInstance();

    /**
     * Create a new {@link NioSaxParser}
     * @param handler {@link org.xml.sax.ContentHandler} to receive events
     * @return {@link NioSaxParser}
     */
    public final NioSaxParser newInstance( final ContentHandler handler )
    {
        final NioSaxParser parser = newInstance();
        parser.setHandler( handler );
        return parser;
    }

    /**
     * Manages the instantiation of the factory using a initialise-on-demand
     * holder pattern. This ensures that the factory is only created when it is
     * first used, not when the main class is loaded.
     */
    private static class FactoryHolder
    {

        private static NioSaxParserFactory instance;

        static
        {
            final String className = System.getProperty( FACTORY_KEY,
                                                         DEFAULT_FACTORY );

            try
            {
                instance = (NioSaxParserFactory) Class.forName( className ).newInstance();
            }
            catch( Exception ex )
            {
                throw new RuntimeException(
                        "Unable to load NioSaxParserFactory " + className,
                        ex );
            }
        }

        private FactoryHolder()
        {
        }

        static NioSaxParserFactory getInstance()
        {
            return instance;
        }
    }
}
