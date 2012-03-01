/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher.performance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.FileReader;
import net.domesdaybook.searcher.BackwardSearchIterator;
import net.domesdaybook.searcher.ForwardSearchIterator;
import net.domesdaybook.searcher.SearchResult;
import net.domesdaybook.searcher.Searcher;

/**
 *
 * @author matt
 */
public final class SearcherProfiler {

    public SearcherProfiler() {
        // static utility class - no public constructor.
    }
    
    
    /**
     * 
     * @param searchers
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Map<Searcher, ProfileResults> profile(final Collection<Searcher> searchers,
                                                 final int numberOfSearches) throws FileNotFoundException, IOException {
        final Map<Searcher, ProfileResults> searcherResults 
                = new IdentityHashMap<Searcher, ProfileResults>();
  
        for (final Searcher searcher : searchers) {
            //System.out.println("Running: " + searcher + " number of times: " + numberOfSearches);
            searcherResults.put(searcher, getProfileResults(searcher, numberOfSearches));
        }
        
        return searcherResults;
    }
    
    
    private ProfileResults getProfileResults(final Searcher searcher,
                                             final int numberOfSearches) throws FileNotFoundException, IOException {
        final ProfileResults results = new ProfileResults();
        
        //System.out.println("Profiling " + searcher + " over ASCII file.");
        FileReader reader = new FileReader(getFile("/TestASCII.txt"));
        results.profile("ASCII file", reader, searcher, numberOfSearches);

        //System.out.println("Profiling " + searcher + " over ZIP file.");
        reader = new FileReader(getFile("/TestASCII.zip"));
        results.profile("ZIP file", reader, searcher, numberOfSearches);        
        
        return results;
    }  
    
    
    private File getFile(final String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        return new File(url.getPath());
    }  
    
    
    /**
     * 
     */
    public static class ProfileResults {
        private Map<String, ProfileResult> results = new LinkedHashMap<String, ProfileResult>();
        private ProfileResult currentProfile;
        private String description;
        
        /**
         * 
         * @return
         */
        public Map<String, ProfileResult> getAllResults() {
            return results;
        }


        private void profile(String description, Reader reader, Searcher searcher, int numberOfSearches) throws IOException {
            startProfiling(description);
            currentProfile.profile(searcher, reader, numberOfSearches);
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
    
    
    /**
     * 
     */
    public static class ProfileResult {
        
        /**
         * 
         */
        public ProfileStats forwardBytesStats = new ProfileStats();
        /**
         * 
         */
        public ProfileStats forwardReaderStats = new ProfileStats();
        /**
         * 
         */
        public ProfileStats backwardBytesStats = new ProfileStats();
        /**
         * 
         */
        public ProfileStats backwardReaderStats = new ProfileStats();

        /**
         * 
         */
        public ProfileResult() {
        }
        

        /**
         * 
         * @param searcher
         * @param bytes
         */
        public void profile(Searcher searcher, byte[] bytes, int numberOfSearches) {
            
            // Profile forwards statistics:
            
            // log forward preparation time.
            long startNano = System.nanoTime();
            searcher.prepareForwards();
            forwardBytesStats.preparationTime = System.nanoTime() - startNano;
            
            // time repeated forward searches:
            List<SearchResult> positions = Collections.emptyList();
            startNano = System.nanoTime();
            for (int repeat = 0; repeat < numberOfSearches; repeat++) {
                positions = searchEntireArrayForwards(searcher, bytes);
            }
            forwardBytesStats.searchTime = (long)((System.nanoTime() - startNano) / numberOfSearches);
            
            
            // Record forward matching positions;
            forwardBytesStats.searchMatches = positions;
            
            // Profile backwards statistics:
            
            // log forward preparation time.
            startNano = System.nanoTime();
            searcher.prepareBackwards();
            backwardBytesStats.preparationTime = System.nanoTime() - startNano;
            
            // time repeated forward searches:
            positions = Collections.emptyList();
            startNano = System.nanoTime();
            for (int repeat = 0; repeat < numberOfSearches; repeat++) {
                positions = searchEntireArrayBackwards(searcher, bytes);
            }
            backwardBytesStats.searchTime = (long)((System.nanoTime() - startNano) / numberOfSearches);
            
            
            // Record backward matching positions;
            backwardBytesStats.searchMatches = positions;            
        }

        
        /**
         * 
         * @param searcher
         * @param reader
         * @throws IOException
         */
        public void profile(Searcher searcher, Reader reader, int numberOfSearches) throws IOException {
            
            /*
            
            // log forward preparation time.
            long startNano = System.nanoTime();
            searcher.prepareForwards();
            forwardReaderStats.preparationTime = System.nanoTime() - startNano;
            
            // time repeated forward searches:
            List<SearchResult> positions = Collections.emptyList();
            startNano = System.nanoTime();
            for (int repeat = 0; repeat < numberOfSearches; repeat++) {
                positions = searchEntireReaderForwards(searcher, reader);
            }
            forwardReaderStats.searchTime = (long)((System.nanoTime() - startNano) / numberOfSearches);
            
            // Record forward matching positions;
            forwardReaderStats.searchMatches = positions;
            
            // log backwards preparation time.
            startNano = System.nanoTime();
            searcher.prepareBackwards();
            backwardReaderStats.preparationTime = System.nanoTime() - startNano;
            
            // time repeated forward searches:
            positions = Collections.emptyList();
            startNano = System.nanoTime();
            for (int repeat = 0; repeat < numberOfSearches; repeat++) {
                positions = searchEntireReaderBackwards(searcher, reader);
            }
            backwardReaderStats.searchTime = (long)((System.nanoTime() - startNano) / numberOfSearches);
            
            // Record backward matching positions;
            backwardReaderStats.searchMatches = positions; 
            */
            
            byte[] bytes = reader.getWindow(0).getArray();
            profile(searcher, bytes, numberOfSearches);
      }

        
        private List<SearchResult> searchEntireArrayForwards(Searcher searcher, byte[] bytes) {
            final List<SearchResult> positions = new ArrayList<SearchResult>();
            final ForwardSearchIterator iterator = new ForwardSearchIterator(searcher, bytes);
            while (iterator.hasNext()) {
                positions.addAll(iterator.next());
            }
            return positions;
        }

        
        private List<SearchResult> searchEntireReaderForwards(Searcher searcher, Reader reader) throws IOException {
            final List<SearchResult> positions = new ArrayList<SearchResult>();
            final ForwardSearchIterator iterator = new ForwardSearchIterator(searcher, reader);
            while (iterator.hasNext()) {
                positions.addAll(iterator.next());
            }
            return positions;
        }

        
        private List<SearchResult> searchEntireArrayBackwards(Searcher searcher, byte[] bytes) {
            final List<SearchResult> positions = new ArrayList<SearchResult>();
            final BackwardSearchIterator iterator = new BackwardSearchIterator(searcher, bytes);
            while (iterator.hasNext()) {
                positions.addAll(iterator.next());
            }
            return positions;
        }

        
        private List<SearchResult> searchEntireReaderBackwards(Searcher searcher, Reader reader) throws IOException {
            final List<SearchResult> positions = new ArrayList<SearchResult>();
            final BackwardSearchIterator iterator = new BackwardSearchIterator(searcher, reader);
            while (iterator.hasNext()) {
                
                positions.addAll(iterator.next());
            }          
            return positions;
        }
        
    }
    
    /**
     * 
     */
    public static class ProfileStats {
        /**
         * 
         */
        public long preparationTime;
        /**
         * 
         */
        public long searchTime;
        /**
         * 
         */
        public List<SearchResult> searchMatches = Collections.emptyList();
    }
    
}
