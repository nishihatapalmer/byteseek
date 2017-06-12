/*
 * Copyright Matt Palmer 2016, All rights reserved.
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

/**
 * A package containing searchers for single sequences.  The sequences can match more than
 * one byte value in each position, but only one sequence can be searched at a time.
 * <p>
 * Although the performance of these searchers can vary depending on the data and pattern being searched,
 * in general they perform from slowest to fastest (without taking into account any pre-processing time):
 * <ul>
 * <li>SequenceMatcherSearcher  - naive search; no additional memory requirements</li>
 * <li>SundayQuickSearcher      - simple adaption of HorspoolSearcher, not usually faster in practice.</li>
 * <li>HorspoolSearcher         - simpler and faster variant of the classic Boyer-Moore search</li>
 * <li>HorspoolUnrolledSearcher - HorspoolSearcher with shift loop "unrolled" - usually faster than Horspool</li>
 * <li>SignedHorspoolSearcher   - Variant of Horspool using Signed Searching  - usually fastest of Horspool variants.</li>
 * <li>ShiftOrSearcher          - usually fastest for small pattern lengths, e.g. 8 or less in length).</li>
 * <li>QgramFilterSearcher      - usually fastest for most patterns except shorter patterns, where ShiftOr is fastest.</li>
 * <li>ShiftHashSearcher        - usually fastest for very long patterns (e.g. 2048 or higher)</li>
 * </ul>
 * <p>
 * Note that performance can vary depending on whether the pattern matches classes of bytes.
 * Most of the searchers above will search much slower when large numbers of bytes can match
 * in particular positions, particularly towards the end of the pattern (when searching forwards, the reverse for backwards).
 * Also, the results above do not include the time to pre-process the pattern.  For short one-off
 * searches, the SequenceMatcherSearcher may well outperform the others, as it has no
 * pre-processing requirements.  As always, you should profile the searchers for the sort
 * of searching you do to determine which best suits your needs.
 */
package net.byteseek.searcher.sequence;