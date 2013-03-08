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
 * A state engine based parser used by {@link StateEngineDelegate}
 * implementations to handle complex parsing.
 *
 * @param <T> The implementing AbstractParserDelegate type
 */
public interface StateEngine<T extends StateEngineDelegate>
{

    /**
     * parse a character
     *
     * @param instance The containing instance calling the engine
     * @param s {@link NioSaxSource} containing the input
     * @param c character to parse
     * @return The state to use after this call
     * @throws org.xml.sax.SAXException if the character is illegal for this state
     */
    StateEngine parse(T instance, NioSaxSource s, char c)
            throws SAXException;

    /**
     * Should the parser continue looping whilst in this state?
     *
     * <p>
     *  The {@link ParserDelegate} will loop whilst this method returns true
     *  so when the state engine indicates it's finished it should switch to
     *  a state where this returns false, then the loop will terminate.
     * </p>
     *
     * @return true to continue, false to exit
     */
    boolean continueLoop();
}
