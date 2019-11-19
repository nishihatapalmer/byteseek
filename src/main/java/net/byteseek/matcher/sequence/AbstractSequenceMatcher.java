/*
 * Copyright Matt Palmer 2009-2017, All rights reserved.
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
package net.byteseek.matcher.sequence;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * An abstract helper subclass that implements the methods that require a  list of MatchResults.
 * Peformms the addition of a new result into the result list for all sequence matchers.
 *
 * Created by matt on 08/06/17.
 */
public abstract class AbstractSequenceMatcher implements SequenceMatcher {

    @Override
    public int matches(final WindowReader reader, final long matchPosition, final Collection<MatchResult> results) throws IOException {
        if (matches(reader, matchPosition)) {
            results.add(new MatchResult(matchPosition, length()));
            return 1;
        }
        return 0;
    }

    @Override
    public int matches(final byte[] bytes, final int matchPosition, final Collection<MatchResult> results) {
        if (matches(bytes, matchPosition)) {
            results.add(new MatchResult(matchPosition, length()));
            return 1;
        }
        return 0;
    }

    /**
     * Returns a string representation of this matcher.  The format is subject
     * to change, but it will generally return the name of the matching class
     * and a regular expression defining the bytes matched by the matcher.
     *
     * @return A string representing this matcher.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + toRegularExpression(true) + ')';
    }

}
