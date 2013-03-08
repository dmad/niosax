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
package uk.org.retep.niosax.charset;

import java.nio.ByteBuffer;

/**
 * Our own implementation of Charset.
 * <p/>
 * <p>
 * We use this and not the jdk's {@link java.nio.charset.Charset}
 * as the latter is geared up for handling entire {@link java.nio.ByteBuffer} and
 * {@link java.nio.CharBuffer} where here we need to be able to decode an
 * individual char with and without affecting the
 * {@link java.nio.ByteBuffer#position()} state.
 * </p>
 * <p/>
 * <p>
 * This interface is primarily targetted at decoding characters as thats
 * what's usually required when parsing xml over an nio stream.
 * </p>
 * <p/>
 * <p>
 * The {@link #encode(java.nio.ByteBuffer, char)} method is only provided for
 * completeness - usually it's best to use the jdk's when writing as that's
 * not hampered by the problem of incomplete buffers.
 * </p>
 *
 * @author peter
 */
public interface Charset {

    public static final char NOT_ENOUGH_DATA = (char) -1;
    public static final char INVALID_CHAR = (char) -2;

    /**
     * Returns an instance that can be used for operations. Most {@link uk.org.retep.niosax.charset.Charset}
     * implementations will simply return themselves, however some may have to
     * hold state information ({@link UTF_16} and derivatives} where the stream
     * can determine the endianess}.
     * <p/>
     * <p>
     * The instance returned by {@link CharsetFactory#getCharset(String)}
     * will have already called this method so it's use by user code is usually
     * not required.
     * </p>
     *
     * @return
     */
    Charset getInstance();

    /**
     * Is the character valid for this {@link uk.org.retep.niosax.charset.Charset}. This will usually always
     * return true for most characters, but will always return false for
     * {@link #NOT_ENOUGH_DATA} and {@link #INVALID_CHAR}.
     *
     * @param c char to test
     * @return true if valid, false if not
     */
    boolean isValid(char c);

    /**
     * Does the buffer contain enough data for a single character. The position
     * is left unchanged.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @return true if the ByteBuffer has enough data for that number of characters
     */
    boolean hasCharacter(ByteBuffer buffer);

    /**
     * Does the buffer contain enough data for count characters. The position
     * is left unchanged.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param count  The number of characters required
     * @return true if the ByteBuffer has enough data for that number of characters
     */
    boolean hasCharacters(ByteBuffer buffer, int count);

    /**
     * Decode the character at the current position in the {@link java.nio.ByteBuffer}.
     * <p/>
     * <p>
     * If the character can be decoded then the position is moved forward by
     * thecorrect number of characters.
     * </p>
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @return decoded character or {@link #INVALID_CHAR} or {@link #NOT_ENOUGH_DATA}
     */
    char decode(ByteBuffer buffer);

    /**
     * Encode the character into the specified {@link java.nio.ByteBuffer} at the current
     * position.
     * <p/>
     * <p>
     * The buffer's position will be incremented accordingly.
     * </p>
     *
     * @param buffer {@link java.nio.ByteBuffer} to append to.
     * @param c      char to append
     * @return true if the append succeded, false if the buffer does not have
     *         enough capacity to hold this character.
     */
    boolean encode(ByteBuffer buffer, char c);

    /**
     * Returns the number of bytes required to store a specific character
     *
     * @param c char
     * @return number of bytes required to store a specific character
     */
    int size(char c);

    /**
     * Returns the number of bytes required to store a set of characters
     *
     * @param c char array
     * @return true if success, false if not enough room.
     */
    int size(char[] c);

    /**
     * Returns the number of bytes required to store a set of characters
     *
     * @param c      char array
     * @param length length to read
     * @return true if success, false if not enough room.
     */
    int size(char[] c, int length);

    /**
     * Returns the number of bytes required to store a set of characters
     *
     * @param c      char array
     * @param offset offset in the array
     * @param length length to read
     * @return true if success, false if not enough room.
     */
    int size(char[] c, int offset, int length);

    /**
     * Move the position in the {@link java.nio.ByteBuffer} forward to skip the specified
     * number of characters. If the ByteBuffer does not contain enough bytes
     * then it's state is left unchanged.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @return true if success, false if not enough room.
     */
    boolean skip(ByteBuffer buffer, char[] c);

    /**
     * Move the position in the {@link java.nio.ByteBuffer} forward to skip the specified
     * number of characters. If the ByteBuffer does not contain enough bytes
     * then it's state is left unchanged.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @param length length to read
     * @return true if success, false if not enough room.
     */
    boolean skip(ByteBuffer buffer, char[] c, int length);

    /**
     * Move the position in the {@link java.nio.ByteBuffer} forward to skip the specified
     * number of characters. If the ByteBuffer does not contain enough bytes
     * then it's state is left unchanged.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @param offset offset in the array
     * @param length length to read
     * @return true if success, false if not enough room.
     */
    boolean skip(ByteBuffer buffer, char[] c, int offset, int length);

    /**
     * Read characters from the {@link java.nio.ByteBuffer} into a char array if the
     * ByteBuffer has the required data. If it does then its position is moved
     * forward, otherwise it's state is left unchanged.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @return true if success, false if not enough room.
     */
    boolean read(ByteBuffer buffer, char[] c);

    /**
     * Read characters from the {@link java.nio.ByteBuffer} into a char array if the
     * ByteBuffer has the required data. If it does then its position is moved
     * forward, otherwise it's state is left unchanged.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @param length length to read
     * @return true if success, false if not enough room.
     */
    boolean read(ByteBuffer buffer, char[] c, int length);

    /**
     * Read characters from the {@link java.nio.ByteBuffer} into a char array if the
     * ByteBuffer has the required data. If it does then its position is moved
     * forward, otherwise it's state is left unchanged.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @param offset offset in the array
     * @param length length to read
     * @return true if success, false if not enough room.
     */
    boolean read(ByteBuffer buffer, char[] c, int offset, int length);

    /**
     * Return the next character in the ByteBuffer without changing the buffer's
     * state.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @return decoded character or {@link #INVALID_CHAR} or {@link #NOT_ENOUGH_DATA}
     */
    char peek(ByteBuffer buffer);

    /**
     * Read characters from the {@link java.nio.ByteBuffer} into a char array if the
     * ByteBuffer has the required data. In either case the byte buffer's state
     * is not modified.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @return true if success, false if not enough room.
     */
    boolean peek(ByteBuffer buffer, char[] c);

    /**
     * Read characters from the {@link java.nio.ByteBuffer} into a char array if the
     * ByteBuffer has the required data. In either case the byte buffer's state
     * is not modified.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @param length length to read
     * @return true if success, false if not enough room.
     */
    boolean peek(ByteBuffer buffer, char[] c, int length);

    /**
     * Read characters from the {@link java.nio.ByteBuffer} into a char array if the
     * ByteBuffer has the required data. In either case the byte buffer's state
     * is not modified.
     *
     * @param buffer {@link java.nio.ByteBuffer} to read from
     * @param c      char array
     * @param offset offset in the array
     * @param length length to read
     * @return true if success, false if not enough room.
     */
    boolean peek(ByteBuffer buffer, char[] c, int offset, int length);

    /**
     * Write characters from a char array into a {@link java.nio.ByteBuffer}. This method
     * returns the number of characters actually written to the ByteBuffer.
     * <p/>
     * <p>
     * The position in the ByteBuffer will point to the position after the
     * last character written.
     * </p>
     *
     * @param buffer {@link java.nio.ByteBuffer} to write to
     * @param c      char array
     * @return number of characters from the array actually written
     */
    int write(ByteBuffer buffer, char[] c);

    /**
     * Write characters from a char array into a {@link java.nio.ByteBuffer}. This method
     * returns the number of characters actually written to the ByteBuffer.
     * <p/>
     * <p>
     * The position in the ByteBuffer will point to the position after the
     * last character written.
     * </p>
     *
     * @param buffer {@link java.nio.ByteBuffer} to write to
     * @param c      char array
     * @param length length to write
     * @return number of characters from the array actually written
     */
    int write(ByteBuffer buffer, char[] c, int length);

    /**
     * Write characters from a char array into a {@link java.nio.ByteBuffer}. This method
     * returns the number of characters actually written to the ByteBuffer.
     * <p/>
     * <p>
     * The position in the ByteBuffer will point to the position after the
     * last character written.
     * </p>
     *
     * @param buffer {@link java.nio.ByteBuffer} to write to
     * @param c      char array
     * @param offset offset in the array
     * @param length length to write
     * @return number of characters from the array actually written
     */
    int write(ByteBuffer buffer, char[] c, int offset, int length);
}
