/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
 */
package net.byteseek.searcher.sequence.factory;

import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.sequence.*;

/**
 * A class that defines default factories for all the Searchers defined in byteseek,
 * and provides public static versions of them to use anywhere they are needed (as they are stateless).
 * <p>
 * Using these factories ensures that where the sequence length is just one, the
 * fastest possible searcher for it will be selected.  When the sequence length is
 * greater than one, the factory will create the type of requested searcher.
 * <p>
 * If you always just want a specific searcher, no matter the length, then it's
 * easiest to just create it yourself without using a factory.  If you absolutely
 * need a factory that always creates that type of searcher, it's also easy to implement.
 */
public class SearcherFactories {

    public static final SequenceSearcherFactory SEQUENCEMATCHER_FACTORY = new SequenceMatcherFactory();
    public static final SequenceSearcherFactory SUNDAY_FACTORY = new SundayFactory();
    public static final SequenceSearcherFactory HORSPOOL_FACTORY = new HorspoolFactory();
    public static final SequenceSearcherFactory HORSPOOL_UNROLLED_FACTORY = new HorspoolUnrolledFactory();
    public static final SequenceSearcherFactory SIGNED_HORSPOOL_FACTORY = new SignedHorspoolFactory();
    public static final SequenceSearcherFactory SIGNED_HASH2_FACTORY = new SignedHash2Factory();
    public static final SequenceSearcherFactory SIGNED_HASH3_FACTORY = new SignedHash3Factory();
    public static final SequenceSearcherFactory SIGNED_HASH4_FACTORY = new SignedHash4Factory();
    public static final SequenceSearcherFactory QGRAM_FILTER2_FACTORY = new QGramFilter2Factory();
    public static final SequenceSearcherFactory QGRAM_FILTER3_FACTORY = new QGramFilter3Factory();
    public static final SequenceSearcherFactory QGRAM_FILTER4_FACTORY = new QGramFilter4Factory();
    public static final SequenceSearcherFactory SHIFTOR_FACTORY = new ShiftOrFactory();
    public static final SequenceSearcherFactory SHIFTOR_UNROLLED_FACTORY = new ShiftOrUnrolledFactory();

    public static class SequenceMatcherFactory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new SequenceMatcherSearcher(theSequence);
        }
    }

    public static class SundayFactory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new SundayQuickSearcher(theSequence);
        }
    }
    
    public static class HorspoolFactory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new HorspoolSearcher(theSequence);
        }
    }

    public static class HorspoolUnrolledFactory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new HorspoolUnrolledSearcher(theSequence);
        }
    }
    
    public static class SignedHorspoolFactory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new SignedHorspoolSearcher(theSequence);
        }
    }
    
    public static class ShiftOrFactory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new ShiftOrSearcher(theSequence);
        }
    }

    public static class ShiftOrUnrolledFactory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new ShiftOrUnrolledSearcher(theSequence);
        }
    }

    public static class SignedHash2Factory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new SignedHash2Searcher(theSequence);
        }
    }

    public static class SignedHash3Factory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new SignedHash3Searcher(theSequence);
        }
    }

    public static class SignedHash4Factory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new SignedHash4Searcher(theSequence);
        }
    }

    public static class QGramFilter2Factory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new QgramFilter2Searcher(theSequence);
        }
    }

    public static class QGramFilter3Factory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new QgramFilter3Searcher(theSequence);
        }
    }

    public static class QGramFilter4Factory extends AbstractSequenceFactory {
        @Override
        protected SequenceSearcher createSequenceSearcher(final SequenceMatcher theSequence) {
            return new QgramFilter4Searcher(theSequence);
        }
    }

}
