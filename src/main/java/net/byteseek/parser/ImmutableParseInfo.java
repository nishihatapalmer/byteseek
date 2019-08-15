/*
 * Copyright Matt Palmer 2019, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.byteseek.parser;

import net.byteseek.utils.ArgUtils;

/**
 * A simple immutable implementation of the ParseInfo interface.
 * It is open for subclassing, but closed for modification of its core functionality.
 */
public class ImmutableParseInfo implements ParseInfo {

    private final String string;
    private final int position;

    /**
     * Constructs an ImmutableParseInfo from string and a position.
     * @param string  The string
     * @param position The position
     * @throws IllegalArgumentException if the string passed in is null.
     */
    public ImmutableParseInfo(final String string, final int position) {
        ArgUtils.checkNullString(string);
        this.string = string;
        this.position = position;
    }

    /**
     * Copy constructor from another ParseInfo object.
     *
     * @param other The other ParseInfo object to construct this one from.
     */
    public ImmutableParseInfo(final ParseInfo other) {
        this(other.getString(), other.getPosition());
    }

    @Override
    public final String getString() {
        return string;
    }

    @Override
    public final int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(position: " + position + " string: " + string + ')';
    }

    @Override
    public int hashCode() {
        return string.hashCode() * position;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ImmutableParseInfo)) {
            return false;
        }

        ImmutableParseInfo other = (ImmutableParseInfo) obj;
        return position == other.position && string.equals(other.string);
    }

}
