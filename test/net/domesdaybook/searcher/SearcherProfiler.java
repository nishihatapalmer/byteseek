/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.FileReader;
import net.domesdaybook.reader.Utilities;
import net.domesdaybook.searcher.SearchIterator.Direction;

/**
 *
 * @author matt
 */
public final class SearcherProfiler {
    private static final String ASCIIFilePath = "/home/matt/dev/search/byteSeek/test/resources/TestASCII.txt";
    private static final String ZIPFILEPATH = "/home/matt/dev/search/byteSeek/test/resources/TestZIPdASCII.zip";


    private SearcherProfiler() {
        // static utility class - no public constructor.
    }
    
    
    public static Map<Searcher, ProfileResults> profile(final Collection<Searcher> searchers) throws FileNotFoundException, IOException {
        final Map<Searcher, ProfileResults> searcherResults 
                = new IdentityHashMap<Searcher, ProfileResults>();
  
        for (final Searcher searcher : searchers) {
            searcherResults.put(searcher, getProfileResults(searcher));
        }
        
        return searcherResults;
    }
    
    
    private static ProfileResults getProfileResults(final Searcher searcher) throws FileNotFoundException, IOException {
        final ProfileResults results = new ProfileResults();
        
        byte[] bytes = Utilities.getByteArray(ASCIIFilePath);
        results.profile("ASCII byte array", bytes, searcher);

        FileReader reader = new FileReader(ASCIIFilePath);
        results.profile("ASCII FileByteReader", reader, searcher);

        bytes = Utilities.getByteArray(ZIPFILEPATH);
        results.profile("ZIP byte array", bytes, searcher);  
        
        reader = new FileReader(ZIPFILEPATH);
        results.profile("ZIP FileByteReader", reader, searcher);        
        
        return results;
    }  
    
    
    public static class ProfileResults {
        private Map<String, ProfileResult> results = new LinkedHashMap<String, ProfileResult>();
        private ProfileResult currentProfile;
        private String description;
        
        public Map<String, ProfileResult> getAllResults() {
            return results;
        }


        private void profile(String description, Reader reader, Searcher searcher) {
            startProfiling(description);
            currentProfile.profile(searcher, reader);
            logProfileResults();
        }
        

        private void profile(String description, byte[] bytes, Searcher searcher) {
            startProfiling(description);
            currentProfile.profile(searcher, bytes);
            logProfileResults();
        }
        
        
        private void startProfiling(final String description) {
            this.description = description;            
            currentProfile = new ProfileResult();
        }        

        
        private void logProfileResults() {
            results.put(description, currentProfile);
        }

    }
    
    
    public static class ProfileResult {
        public static final int NO_OF_SEARCHES = 10;
        
        public ProfileStats forwardBytesStats = new ProfileStats();
        public ProfileStats forwardReaderStats = new ProfileStats();
        public ProfileStats backwardBytesStats = new ProfileStats();
        public ProfileStats backwardReaderStats = new ProfileStats();

        public ProfileResult() {
        }
        

        public void profile(Searcher searcher, byte[] bytes) {
            
            // Profile forwards statistics:
            
            // log forward preparation time.
            long startNano = System.nanoTime();
            searcher.prepareForwards();
            forwardBytesStats.preparationTime = System.nanoTime() - startNano;
            
            // time repeated forward searches:
            List<Long> positions = null;
            startNano = System.nanoTime();
            for (int repeat = 0; repeat < NO_OF_SEARCHES; repeat++) {
                positions = searchEntireArrayForwards(searcher, bytes);
            }
            forwardBytesStats.searchTime = System.nanoTime() - startNano;
            
            
            // Record forward matching positions;
            forwardBytesStats.searchMatches = positions;
            
            // Profile backwards statistics:
            
            // log forward preparation time.
            startNano = System.nanoTime();
            searcher.prepareBackwards();
            backwardBytesStats.preparationTime = System.nanoTime() - startNano;
            
            // time repeated forward searches:
            positions = null;
            startNano = System.nanoTime();
            for (int repeat = 0; repeat < NO_OF_SEARCHES; repeat++) {
                positions = searchEntireArrayBackwards(searcher, bytes);
            }
            backwardBytesStats.searchTime = System.nanoTime() - startNano;
            
            
            // Record backward matching positions;
            backwardBytesStats.searchMatches = positions;            
        }

        
        public void profile(Searcher searcher, Reader reader) {
            
            // log forward preparation time.
            long startNano = System.nanoTime();
            searcher.prepareForwards();
            forwardReaderStats.preparationTime = System.nanoTime() - startNano;
            
            // time repeated forward searches:
            List<Long> positions = null;
            startNano = System.nanoTime();
            for (int repeat = 0; repeat < NO_OF_SEARCHES; repeat++) {
                positions = searchEntireReaderForwards(searcher, reader);
            }
            forwardReaderStats.searchTime = System.nanoTime() - startNano;
            
            // Record forward matching positions;
            forwardReaderStats.searchMatches = positions;
            
            // log backwards preparation time.
            startNano = System.nanoTime();
            searcher.prepareBackwards();
            backwardReaderStats.preparationTime = System.nanoTime() - startNano;
            
            // time repeated forward searches:
            positions = null;
            startNano = System.nanoTime();
            for (int repeat = 0; repeat < NO_OF_SEARCHES; repeat++) {
                positions = searchEntireReaderBackwards(searcher, reader);
            }
            backwardReaderStats.searchTime = System.nanoTime() - startNano;
            
            // Record backward matching positions;
            backwardReaderStats.searchMatches = positions;            
        }

        
        private List<Long> searchEntireArrayForwards(Searcher searcher, byte[] bytes) {
            final List<Long> positions = new ArrayList<Long>();
            final SearchIterator iterator = new SearchIterator(searcher, bytes);
            while (iterator.hasNext()) {
                positions.add(iterator.next());
            }
            /*
            final int lastPos = bytes.length - 1;
            int searchPos = 0;
            while (searchPos <= lastPos) {
                searchPos = searcher.searchForwards(bytes, searchPos, lastPos);
                if (searchPos >= 0) {
                    positions.add((long) searchPos);
                    searchPos += 1;
                }
            }
             * 
             */
            return positions;
        }

        
        private List<Long> searchEntireReaderForwards(Searcher searcher, Reader reader) {
            final List<Long> positions = new ArrayList<Long>();
            final SearchIterator iterator = new SearchIterator(searcher, reader);
            while (iterator.hasNext()) {
                positions.add(iterator.next());
            }
            /*
            final long lastPos = reader.length() - 1;
            long searchPos = 0;
            while (searchPos <= lastPos) {
                searchPos = searcher.searchForwards(reader, searchPos, lastPos);
                if (searchPos >= 0) {
                    positions.add(searchPos);
                    searchPos += 1;
                }
            }
             * 
             */
            return positions;
        }

        
        private List<Long> searchEntireArrayBackwards(Searcher searcher, byte[] bytes) {
            final List<Long> positions = new ArrayList<Long>();
            final SearchIterator iterator = new SearchIterator(searcher, Direction.BACKWARDS, bytes);
            while (iterator.hasNext()) {
                positions.add(iterator.next());
            }
            /*
            final int lastPos = bytes.length - 1;
            int searchPos = lastPos;
            while (searchPos >= 0) {
                searchPos = searcher.searchBackwards(bytes, searchPos, lastPos);
                if (searchPos != Searcher.NOT_FOUND) {
                    positions.add((long) searchPos);
                    searchPos -= 1;
                }
            }
             * 
             */
            return positions;
        }

        
        private List<Long> searchEntireReaderBackwards(Searcher searcher, Reader reader) {
            final List<Long> positions = new ArrayList<Long>();
            final SearchIterator iterator = new SearchIterator(searcher, Direction.BACKWARDS, reader);
            while (iterator.hasNext()) {
                positions.add(iterator.next());
            }          
            /*
            final long lastPos = reader.length() - 1;
            long searchPos = lastPos;
            while (searchPos >= 0) {
                searchPos = searcher.searchBackwards(reader, searchPos, lastPos);
                if (searchPos >= 0) {
                    positions.add(searchPos);
                    searchPos -= 1;
                }
            }
             * 
             */
            return positions;
        }
        
    }
    
    public static class ProfileStats {
        public long preparationTime;
        public long searchTime;
        public List<Long> searchMatches;
    }
    
}
