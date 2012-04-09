/*
 * Copyright Matt Palmer 2012, All rights reserved.
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
package net.domesdaybook.searcher.performance;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.domesdaybook.searcher.multisequence.WuManberOneByteFinalFlagSearcher;
import net.domesdaybook.matcher.multisequence.ListMultiSequenceMatcher;
import net.domesdaybook.searcher.multisequence.WuManberUtils;
import net.domesdaybook.searcher.multisequence.SetHorspoolFinalFlagSearcher;
import net.domesdaybook.searcher.multisequence.SetHorspoolSearcher;
import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.searcher.multisequence.MultiSequenceMatcherSearcher;
import net.domesdaybook.matcher.multisequence.TrieMultiSequenceMatcher;
import net.domesdaybook.searcher.performance.SearcherProfiler.ProfileResult;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import net.domesdaybook.searcher.performance.SearcherProfiler.ProfileResults;
import net.domesdaybook.searcher.sequence.HorspoolFinalFlagSearcher;
import net.domesdaybook.searcher.sequence.SundayQuickSearcher;
import net.domesdaybook.searcher.sequence.BoyerMooreHorspoolSearcher;
import net.domesdaybook.searcher.sequence.SequenceMatcherSearcher;
import net.domesdaybook.searcher.matcher.MatcherSearcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import net.domesdaybook.searcher.Searcher;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.sequence.ByteArrayMatcher;
import net.domesdaybook.compiler.sequence.SequenceMatcherCompiler;
import net.domesdaybook.searcher.SearchResult;
import net.domesdaybook.searcher.multisequence.WuManberOneByteSearcher;
import net.domesdaybook.searcher.multisequence.WuManberOneByteTunedSearcher;
import net.domesdaybook.searcher.multisequence.WuManberTwoByteSearcher;

/**
 * Runs the searchers against different files and inputs to search for,
 * and collects average search times for searching forwards and backwards
 * over byte arrays and using the Reader interface.
 * <p>
 * Warms up the searchers first before getting final performance results
 * in order that the JIT compiler has reached a steady state before 
 * getting final results.
 * <p>
 * Use the command line option -XX:+PrintCompilation to see whether the JIT
 * compiler is still making changes during the results collections phase.
 * <p>
 * Use the command line option -verbose:gc to observe whether garbage
 * collection is having an impact on performance statistics.
 * <p>
 * Use the command line option -XX:+UseSerialGC to force the use of the
 * serial garbage collector.  This may be better for micro-benchmarks like this.
 * <p>
 * -XX:+PrintCompilation -XX:+UseSerialGC
 * 
 * @author Matt Palmer
 */
public class SearcherPerformanceTests {
    
    public final static int FIRST_WARMUP_TIMES = 3;
    public final static int SECOND_WARMUP_TIMES = 3;
    public final static int CYCLE_WARMUP_TIMES = 3;
    public final static int TEST_TIMES = 1;//100; 
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting profiling");
        SearcherPerformanceTests tests = new SearcherPerformanceTests();
        
        //warmup(tests);
        
        Thread.sleep(250); 
        System.out.println("Running performance tests " + TEST_TIMES);
        
        Thread.sleep(250); // just let things settle down before running actual tests.        
        tests.profile(TEST_TIMES);
        Thread.sleep(250);
        
        System.out.println("Ending profiling");
        System.out.println("Prevent optimising away results...." + tests.getLastResultCount());
    }
    
    public void profile(int numberOfTimes) {
        try {
            //profileSequenceSearchers(numberOfTimes);
            profileMultiSequenceSearchers(numberOfTimes);      
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearcherPerformanceTests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SearcherPerformanceTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Collection<Searcher> getSequenceSearchers(SequenceMatcher sequence) {
        List<Searcher> searchers = new ArrayList<Searcher>();
        searchers.add(new MatcherSearcher(sequence));
        searchers.add(new SequenceMatcherSearcher(sequence));
        searchers.add(new BoyerMooreHorspoolSearcher(sequence));
        searchers.add(new HorspoolFinalFlagSearcher(sequence));
        searchers.add(new SundayQuickSearcher(sequence)); 
        return searchers;
    }
    
    private Collection<Searcher> getMultiSequenceSearchers(MultiSequenceMatcher multisequence) {
        List<Searcher> searchers = new ArrayList<Searcher>();
        searchers.add(new MatcherSearcher(multisequence));
        searchers.add(new MultiSequenceMatcherSearcher(multisequence));
        searchers.add(new SetHorspoolSearcher(multisequence));
        searchers.add(new SetHorspoolFinalFlagSearcher(multisequence));
        searchers.add(new WuManberOneByteSearcher(multisequence));
        searchers.add(new WuManberOneByteTunedSearcher(multisequence));
        searchers.add(new WuManberTwoByteSearcher(multisequence));
        searchers.add(new WuManberOneByteFinalFlagSearcher(multisequence));
        return searchers;
    }    
    
    private static void warmup(SearcherPerformanceTests tests) {
        System.out.println("First warm up " + FIRST_WARMUP_TIMES + " times to provoke initial JIT compilation.");
        tests.profile(FIRST_WARMUP_TIMES);
        System.out.println("Ending first warmup.");
        
        System.out.println("Second warm up " + SECOND_WARMUP_TIMES + " times to deal with class-loading JIT issues.");
        tests.profile(SECOND_WARMUP_TIMES);
        System.out.println("Ending second warmup.");
        
        cycleWarmup(tests);        
    }
    
    private static void cycleWarmup(SearcherPerformanceTests tests) {
        for (int cycle = 1; cycle <= CYCLE_WARMUP_TIMES; cycle++) {
            System.out.println("Cycling warmup " +cycle + " of "+ CYCLE_WARMUP_TIMES + " times.");            
            tests.profile(2);
        }
    }
    
    private int lastResultCount; // attempt to stop optimiser erroneously getting rid of methods.
    
    public SearcherPerformanceTests() {
    }
    
    public int getLastResultCount() {
        return lastResultCount;
    }
    

    
    public void profileSequenceSearchers(int numberOfTimes) throws FileNotFoundException, IOException {

        // Single long phrase in ascii text:
        profileSequence("I know a banke where the wilde time blowes,", numberOfTimes);
        
        // Single long phrase in ascii text first 4096 bytes:
        profileSequence("Information about Project Gutenberg", numberOfTimes);
        
        // String which crosses a 4096-length window boundary:
        profileSequence("Gutenberg", numberOfTimes);
        
        // Uncommon string in ascii text:
        profileSequence("Midsommer", numberOfTimes);
        
        // Uncommon string ending in a common character (a space):
        profileSequence("Midsommer ", numberOfTimes);
        
        // Fairly common matching string in ascii text:
        profileSequence("Bottome", numberOfTimes);
        
        // Common short word in ascii text:
        profileSequence("and", numberOfTimes);
        
    }
    
    
    public void profileMultiSequenceSearchers(int numberOfTimes) throws IOException {
        
        //profileMultiSequence(numberOfTimes, "Midsommer", "and");
        //profileMultiSequence(numberOfTimes, "Midsommer ", "and");
        
        
        profileMultiSequence(numberOfTimes, 
                "Midsommer", "Oberon", "Titania", "Dreame", "heere",
                "nothing", "perchance", "discretion", "smallest",
                "through", "enough", "Gentleman", "friends");
    }    
    
    
    
    private void profileSequence(String sequence, int numberOfTimes) throws IOException {
        profileSequence("'" + sequence + "'", numberOfTimes, new ByteArrayMatcher(sequence));
    }
    
    private void profileSequence(String description, int numberOfTimes, SequenceMatcher matcher) throws FileNotFoundException, IOException {
        lastResultCount = profileSearchers(description, numberOfTimes, getSequenceSearchers(matcher));
    }
    

    
    
    private void profileMultiSequence(String description, 
                                      int numberOfTimes,
                                      SequenceMatcher... matcherArgs) throws IOException {
        lastResultCount = profileSearchers(description, numberOfTimes, getMultiSequenceSearchers(getMultiSequenceMatcher(matcherArgs)));
    }
    
    private void profileMultiSequence(int numberOfTimes, String... matcherArgs) throws IOException {
        final List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
        final StringBuilder description = new StringBuilder();
        description.append('[');
        boolean first = true;
        for (String str : matcherArgs) {
            matchers.add(new ByteArrayMatcher(str));
            if (!first) description.append(',');
            description.append('\'').append(str).append('\'');
            first = false;
        }
        description.append(']');
        lastResultCount = profileSearchers(description.toString(), numberOfTimes, 
                                           getMultiSequenceSearchers(getMultiSequenceMatcher(matchers)));
    }
    
    
    public MultiSequenceMatcher getMultiSequenceMatcher(SequenceMatcher... matcherArgs) {
        return getMultiSequenceMatcher(Arrays.asList(matcherArgs));
    }
    
    public MultiSequenceMatcher getMultiSequenceMatcher(Collection<? extends SequenceMatcher> matchers) { 
        return new TrieMultiSequenceMatcher(matchers);
    }    
    
    
    private int profileSearchers(String description, int numberOfTimes, Collection<Searcher> searchers) throws FileNotFoundException, IOException {
        SearcherProfiler profiler = new SearcherProfiler();        
        Map<Searcher, ProfileResults> results = profiler.profile(searchers, numberOfTimes);
        return writeResults(description, results);              
    }
    
    
    private int writeResults(String description, Map<Searcher, ProfileResults> results) {
        try {
            Thread.sleep(500); // just so console mode output doesn't get mixed up.
        } catch (InterruptedException ex) {
            Logger.getLogger(SearcherPerformanceTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // work out which results are common to all searchers:
        Map<Long, List<Searcher>> forwardReaderAnalysis = new HashMap<Long, List<Searcher>>();
        Map<Long, List<Searcher>> backReaderAnalysis = new HashMap<Long, List<Searcher>>();
        Map<Long, List<Searcher>> forwardBytesAnalysis = new HashMap<Long, List<Searcher>>();
        Map<Long, List<Searcher>> backBytesAnalysis = new HashMap<Long, List<Searcher>>();
        
        int resultCount = 0;
        String message = "%s\t%s\t%s\t%s\tForward reader:\t%d\t%d\tForward bytes:\t%d\t%d\tBack reader:\t%d\t%d\tBack bytes:\t%d\t%d";
        for (Map.Entry<Searcher, ProfileResults> entry : results.entrySet()) {
            Searcher searcher = entry.getKey();
            ProfileResults info = entry.getValue();
            for (Map.Entry<String, ProfileResult> result : info.getAllResults().entrySet()) {
                String testName = result.getKey();
                ProfileResult testInfo = result.getValue();
                String output = String.format(message, description, 
                                              searcher.getClass().getSimpleName(),
                                              searcher, testName,
                                              testInfo.forwardReaderStats.searchTime,
                                              testInfo.forwardReaderStats.searchMatches.size(),
                                              testInfo.forwardBytesStats.searchTime,
                                              testInfo.forwardBytesStats.searchMatches.size(),
                                              testInfo.backwardReaderStats.searchTime,
                                              testInfo.backwardReaderStats.searchMatches.size(),
                                              testInfo.backwardBytesStats.searchTime,
                                              testInfo.backwardBytesStats.searchMatches.size() );
                // add results to analysis:
                addAnalysis(searcher, testInfo.forwardReaderStats.searchMatches, forwardReaderAnalysis);
                addAnalysis(searcher, testInfo.backwardReaderStats.searchMatches, backReaderAnalysis);
                addAnalysis(searcher, testInfo.forwardBytesStats.searchMatches, forwardBytesAnalysis);
                addAnalysis(searcher, testInfo.backwardBytesStats.searchMatches, backBytesAnalysis);
                
                System.out.println(output);
               resultCount++;
            }
        }
        
        System.out.println("Mismatch analysis starting:");
        
        writeAnalysis("Forward reader", forwardReaderAnalysis, results.keySet());
        writeAnalysis("Forward bytes", forwardBytesAnalysis, results.keySet());        
        writeAnalysis("Backward reader", backReaderAnalysis, results.keySet());
        writeAnalysis("Backward bytes", backBytesAnalysis, results.keySet());
        
        System.out.println("Mismatch analysis finished.");
        
        return resultCount;
    }
    
    private void writeAnalysis(String description, Map<Long, List<Searcher>> analysis, 
                               Set<Searcher> searcherSet) {
        for (Map.Entry<Long, List<Searcher>> entry : analysis.entrySet()) {
            List<Searcher> searchers = entry.getValue();
            if (searchers.size() != searcherSet.size()) {
                String message = description + "\tmatch at\t" + entry.getKey() + "\tnot found by\t";
                Set<Searcher> newSet = new HashSet<Searcher>(searcherSet);
                for (Searcher searcher : searchers) {
                    newSet.remove(searcher);
                }
                for (Searcher searcher : newSet) {
                    message += searcher.getClass().getSimpleName() + " ";
                }
                System.out.println(message);
            }
        }
    }
    
    private void addAnalysis(Searcher searcher, List<SearchResult> results, Map<Long, List<Searcher>> analysis) { 
        for (SearchResult result : results) {
            List<Searcher> searchers = analysis.get(result.getMatchPosition());
            if (searchers == null) {
                searchers = new ArrayList<Searcher>();
                analysis.put(result.getMatchPosition(), searchers);
            }
            searchers.add(searcher);
        }
    }
    
    
    
    
}
