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

/**
 * A class which stores an extensible char array for storing parsed content.
 * 
 * @author peter
 * @since 9.10
 */
public interface Appendable
{

    /**
     * Appends the specified character to this writer.
     * @param c character to append
     * @return this to allow method chaining
     */
    Appendable append(final char c);

    /**
     * Writes characters to the buffer.
     * @param c the data to be written
     * @param off the start offset in the data
     * @param len the number of chars that are written
     * @return this to allow method chaining
     */
    Appendable append(final char[] c, final int off, final int len);

    /**
     * Returns a copy of the input data.
     *
     * @return an array of chars copied from the input data.
     * @see #getCharBuffer() 
     */
    char[] toCharArray();

    /**
     * Returns the underlying char array. This method is used for performance
     * purposes where the array is only needed momentarily. The array may be
     * larger than the size of the Appendable.
     *
     * <p>
     *  Be warned, the char array may be modified by further updates to the
     *  Appendable, so should be used only where a copy is made and
     *  {@link #toCharArray() } would be expensive in making an unwanted copy
     *  of data thats just thrown away.
     * </p>
     *
     * @return underlying volatile char array
     * @see #toCharArray()
     */
    char[] getCharBuffer();

    /**
     * Resets the buffer so that you can use it again without
     * throwing away the already allocated buffer.
     * @return this to allow method chaining
     */
    Appendable reset();

    /**
     * Returns the current size of the buffer.
     *
     * @return an int representing the current size of the buffer.
     */
    int size();

    /**
     * Converts input data to a string.
     * @return the string.
     */
    @Override
    String toString();

    /**
     * Returns the parent {@link uk.org.retep.niosax.internal.helper.Appendable} instance or null if the root.
     * This allows for Appendable's to be temporarily replaced by another one
     * during parsing.
     *
     * @param <T> type of {@link uk.org.retep.niosax.internal.helper.Appendable}
     * @return the parent {@link uk.org.retep.niosax.internal.helper.Appendable}
     */
    <T extends Appendable> T getParent();

    /**
     * Sets the parent {@link uk.org.retep.niosax.internal.helper.Appendable} instance or null if the root.
     *
     * @param <T> type of {@link uk.org.retep.niosax.internal.helper.Appendable}
     * @param  parent {@link uk.org.retep.niosax.internal.helper.Appendable}
     * @return parent to allow method chaining
     */
    <T extends Appendable> Appendable setParent(T parent);
}
