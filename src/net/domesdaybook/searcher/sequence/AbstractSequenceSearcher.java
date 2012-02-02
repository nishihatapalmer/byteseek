/*
 * Copyright Matt Palmer 2011-12, All rights reserved.
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

package net.domesdaybook.searcher.sequence;

import java.io.IOException;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;
import net.domesdaybook.searcher.AbstractSearcher;
import net.domesdaybook.searcher.SearchResult;

/**
 * This abstract base class for sequence searchers holds the sequence to be
 * searched for and provides generic implementations of:
 * <ul>
 * <li>{@link #searchForwards(net.domesdaybook.reader.Reader, long, long)}
 * <li>{@link #searchBackwards(net.domesdaybook.reader.Reader, long, long)}
 * </ul>
 * These allocate searching for sequences efficiently between searching in the
 * byte arrays provided by {@link Window}s when the sequence fits in a single window,
 * only using the less efficient reader interface on the sequence for times when
 * the sequence crosses over Window boundaries.
 * <p>
 * It defines two new abstract methods:
 * <ul>
 * <li>{@link #doSearchForwards(net.domesdaybook.reader.Reader, long, long) }
 * <li>{@link #doSearchBackwards(net.domesdaybook.reader.Reader, long, long) }
 * </ul>
 * which require the implementor to use the reader interface on the sequence for
 * matching (or otherwise provide for searching sequences which cross window boundaries).
 * 
 * @author Matt Palmer
 */
public abstract class AbstractSequenceSearcher extends AbstractSearcher<SequenceMatcher> {
    
    protected final SequenceMatcher matcher;
    
    /**
     * Constructs a sequence searcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param sequence 
     */
    public AbstractSequenceSearcher(final SequenceMatcher sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException("Null sequence passed in to searcher.");
        }        
        this.matcher = sequence;
    }    
    
    
    /**
     * Returns the {@link SequenceMatcher} to be searched for.
     * 
     * @return SequenceMatcher the sequence matcher to be searched for.
     */
    public SequenceMatcher getMatcher() {
        return matcher;
    }
    
    
    /**
     * {@inheritDoc}
     * <p>
     * This implementation of searchForwards allocates forward searching between
     * searching directly on a window byte array when the sequence fits inside
     * a window, and using the abstract search method:
     * {@link #doSearchForwards(net.domesdaybook.reader.Reader, long, long) }
     * for searching across window boundaries.
     * <p>
     * This method does no searching itself - it simply calculates how to
     * efficiently search using a sequence, and calls the appropriate search
     * methods on the search implementation.  Therefore, this is entirely generic for
     * any search algorithm that operates over sequences.
     * 
     * @throws IOException If the reader encounters a problem reading bytes.
     */
    @Override
    public SearchResult<SequenceMatcher> searchForwards(final Reader reader, 
            final long fromPosition, final long toPosition) throws IOException {
        // Initialise:
        final int sequenceLength = matcher.length();
        final int lastSequencePosition = sequenceLength - 1;
        long searchPosition = fromPosition > 0?
                              fromPosition : 0;
        
        // While there is data to search in:
        Window window = reader.getWindow(searchPosition);        
        while (window != null && searchPosition <= toPosition) {
            
            // Does the sequence fit into the searchable bytes of this window?
            // It may not if the start position of the window is already close
            // to the end of the window, or the sequence is long (potentially
            // could be longer than any single window - but mostly won't be):
            final long windowStartPosition = window.getWindowPosition();
            final int windowLength = window.length();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);  
            final int arrayLastPosition = windowLength - 1;             
            if (arrayStartPosition + lastSequencePosition <= arrayLastPosition) {

                // Find the last point in the array where the sequence still fits
                // inside the array, or the toPosition if it is smaller.
                final int lastMatchingPosition = arrayLastPosition - lastSequencePosition;
                final long distanceToEnd = toPosition - windowStartPosition;                
                final int arrayMaxPosition = distanceToEnd < lastMatchingPosition?
                                       (int) distanceToEnd : lastMatchingPosition; 
                        
                // Search forwards in the byte array of the window:
                final SearchResult<SequenceMatcher> arrayResult = 
                    searchForwards(window.getArray(), arrayStartPosition, arrayMaxPosition);

                // Did we find a match?
                if (arrayResult.matched()) {
                    final long matchPosition = searchPosition + 
                            arrayResult.getMatchPosition() - arrayStartPosition;
                    return new SearchResult<SequenceMatcher>(matchPosition, matcher);
                }
                
                // Continue the search one on from where we last looked:
                searchPosition += (arrayMaxPosition - arrayStartPosition + 1);

                // Did we pass the final toPosition?  In which case, we're finished.
                if (searchPosition > toPosition) {
                    return SearchResult.noMatch();
                }
            }

            // From the current search position, the sequence crosses over in to
            // the next window, so we can't search directly in the window byte array.
            // We must use the reader interface on the sequence to let it match
            // over more bytes than this window has available.
            
            // Search up to the last position in the window, or the toPosition,
            // whichever comes first:
            final long lastWindowPosition = windowStartPosition + arrayLastPosition;
            final long lastSearchPosition = toPosition < lastWindowPosition?
                                            toPosition : lastWindowPosition;
            final SearchResult<SequenceMatcher> readerResult = 
                    doSearchForwards(reader, searchPosition, lastSearchPosition);
            
            // Did we find a match?
            if (readerResult.matched()) {
                return readerResult;
            }
            
            // Continue the search one on from where we last looked:
            searchPosition = lastSearchPosition + 1;
            
            // Did we pass the final toPosition?  In which case, we're finished.
            if (searchPosition > toPosition) {
                return SearchResult.noMatch();
            }
            
            // Get the next window of data to search:
            window = reader.getWindow(searchPosition);
        }
        
        return SearchResult.noMatch();
    }

    
    /**
     * This method searches forwards crossing window boundaries.  It is
     * called by the {@link #searchForwards(net.domesdaybook.reader.Reader, long, long)}
     * method when it encounters a sequence which crosses from one window to another.
     * <p>
     * A simple way to implement this method is to use the Reader interface on the
     * matcher sequence. This at least removes window boundaries from validating
     * that a match exists. It will still be necessary to deal with window management
     * in the operation of the search algorithm itself.
     * <p>
     * Implementations of this method do not need to worry about whether the search
     * position parameters are within the reader, as this bounds checking is done
     * by the searchForwards method which calls it.
     * 
     * @param reader The reader providing bytes to search in.
     * @param searchPosition The search position to search from.
     * @param lastSearchPosition The search position to search to.
     * @return The position of a match, or a negative number if no match was found.
     * @throws IOException If the reader encounters difficulties reading bytes.
     */
    protected abstract SearchResult<SequenceMatcher> doSearchForwards(Reader reader, 
            long searchPosition, long lastSearchPosition) throws IOException;

    
    
    /**
     * {@inheritDoc}
     * <p>
     * This implementation of searchBackwards allocates backwards searching between
     * searching directly on a window byte array when the sequence fits inside
     * a window, and using the abstract search method:
     * {@link #doSearchBackwards(net.domesdaybook.reader.Reader, long, long) }
     * for searching across window boundaries.
     * <p>
     * This method does no searching itself - it simply calculates how to
     * efficiently search using a sequence, and calls the appropriate search
     * methods on the search implementation.  Therefore, this is entirely generic for
     * any search algorithm that operates over sequences.
     * 
     * @throws IOException If the reader encounters a problem reading bytes.
     */    
    @Override
    public SearchResult<SequenceMatcher> searchBackwards(final Reader reader, 
            final long fromPosition, final long toPosition) throws IOException {
        // Initialise:
        final int sequenceLength = matcher.length();
        final int lastSequencePosition = sequenceLength - 1;
        final long finalSearchPosition = toPosition > 0?
                                         toPosition : 0;
        long searchPosition = withinLength(reader, fromPosition);
        
        // While there is data to search in:
        Window window = reader.getWindow(searchPosition);        
        while (window != null && searchPosition >= finalSearchPosition) {
            
            // Does the sequence fit into the searchable bytes of this window?
            // It may not if the start position of the window is already close
            // to the end of the window, or the sequence is long (potentially
            // could be longer than any single window - but mostly won't be):
            final long windowStartPosition = window.getWindowPosition();
            final int windowLength = window.length();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);  
            final int arrayLastPosition = windowLength - 1;             
            if (arrayStartPosition + lastSequencePosition <= arrayLastPosition) {

                // Find the last place in the array to search in (either zero, or
                // the final search position, whichever is closer):
                final long distanceToEnd = finalSearchPosition - windowStartPosition;                
                final int arrayMinPosition = distanceToEnd > 0?
                                       (int) distanceToEnd : 0; 
                        
                // Search backwards in the byte array of the window:
                final SearchResult<SequenceMatcher> arrayResult = 
                        searchBackwards(window.getArray(), arrayStartPosition, arrayMinPosition);
                
                // Did we find a match?
                if (arrayResult.matched()) {
                    final long matchPosition = searchPosition + 
                            arrayResult.getMatchPosition() - arrayStartPosition;
                    return new SearchResult<SequenceMatcher>(matchPosition, matcher);
                }
                
                // Continue the search one on from where we last looked:
                searchPosition -= (arrayStartPosition - arrayMinPosition + 1);

                // Did we pass the final search position?  In which case, we're finished.
                if (searchPosition < finalSearchPosition) {
                    return SearchResult.noMatch();
                }
            }

            // From the current search position, the sequence crosses over in to
            // the next window, so we can't search directly in the window byte array.
            // We must use the reader interface on the sequence to let it match
            // over more bytes than this window has available.
            
            // Search back to the first position in the window where the sequence 
            // would fit inside it, the window start, or the final search position, 
            // whichever comes first (maning bigger as we search backwards):
            final long firstFitPosition = windowStartPosition + arrayLastPosition - lastSequencePosition;
            final long windowSearchPosition = firstFitPosition > windowStartPosition?
                                              firstFitPosition : windowStartPosition;
            final long lastSearchPosition = finalSearchPosition > windowSearchPosition?
                                            finalSearchPosition : windowSearchPosition;
            final SearchResult<SequenceMatcher> readerResult =
                    doSearchBackwards(reader, searchPosition, lastSearchPosition);
            
            // Did we find a match?
            if (readerResult.matched()) {
                return readerResult;
            }
            
            // Continue the search one on from where we last looked:
            searchPosition = lastSearchPosition - 1;
            
            // Did we pass the final toPosition?  In which case, we're finished.
            if (searchPosition < finalSearchPosition) {
                return SearchResult.noMatch();
            }
            
            // Get the next window of data to search:
            window = reader.getWindow(searchPosition);
        }
        
        return SearchResult.noMatch();
    }
    

   /**
     * This abstract method searches backwards crossing window boundaries.  It is
     * called by the {@link #searchBackwards(net.domesdaybook.reader.Reader, long, long)}
     * method when it encounters s sequence which crosses from one window to another.
     * <p>
     * A simple way to implement this method is to use the Reader interface on the
     * matcher sequence.  This at least removes window boundaries from validating
     * that a match exists.  It may still be necessary to deal with window management
     * in the operation of the search algorithm itself.
     * 
     * @param reader The reader providing bytes to search in.
     * @param searchPosition The search position to search from.
     * @param lastSearchPosition The search position to search to.
     * @return The position of a match, or a negative number if no match was found.
     * @throws IOException If the reader encounters difficulties reading bytes.
     */    
    protected abstract SearchResult<SequenceMatcher> doSearchBackwards(Reader reader,
            long searchPosition, long lastSearchPosition) throws IOException;
    
    
    
}
