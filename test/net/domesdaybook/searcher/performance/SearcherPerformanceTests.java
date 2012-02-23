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
import net.domesdaybook.searcher.Searcher;
import java.util.Collection;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.sequence.ByteArrayMatcher;
import net.domesdaybook.compiler.sequence.SequenceMatcherCompiler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matt Palmer
 */
public class SearcherPerformanceTests {
    
    public SearcherPerformanceTests() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    
    @Test
    public void profileSequenceSearchers() throws FileNotFoundException, IOException {

        // warm up search classes:
        warmup();
        
        // Test of uncommon matching string in ascii text:
        SequenceMatcher matcher = new ByteArrayMatcher("Midsommer");
        Collection<Searcher> searchers = getSearchers(matcher);
        profileSearchers(searchers);

        // Test of common short word in ascii text:
        matcher = new ByteArrayMatcher("and");
        searchers = getSearchers(matcher);
        profileSearchers(searchers);
    }
    
    private void warmup() throws IOException {
        
        // warmup searchers:
        SequenceMatcher warmup = new ByteArrayMatcher("warmup");
        Collection<Searcher> searchers = getSearchers(warmup);
        SearcherProfiler profiler = new SearcherProfiler();
        System.out.println("warming up...");
        profiler.profile(searchers, 10100);
        System.out.println("finished warming up...");
    }
    
    
    private void profileSearchers(Collection<Searcher> searchers) throws FileNotFoundException, IOException {
        SearcherProfiler profiler = new SearcherProfiler();        
        Map<Searcher, ProfileResults> results = profiler.profile(searchers, 1000);
        writeResults(results);              
    }
    
    
    // bug in backwards searching for sequencesearcher (probably abstract) - infinite loop.
    private Collection<Searcher> getSearchers(SequenceMatcher sequence) {
        List<Searcher> searchers = new ArrayList<Searcher>();
//        searchers.add(new MatcherSearcher(sequence));
//        searchers.add(new SequenceMatcherSearcher(sequence));
//        searchers.add(new BoyerMooreHorspoolSearcher(sequence));
        searchers.add(new HorspoolFinalFlagSearcher(sequence));
//        searchers.add(new SundayQuickSearcher(sequence)); 
        return searchers;
    }
    
    private void writeResults(Map<Searcher, ProfileResults> results) {
        String message = "%s\t%s\tForward reader:\t%d\t%d\tForward bytes:\t%d\t%d\tBack reader:\t%d\t%d\tBack bytes:\t%d\t%d";
        for (Map.Entry<Searcher, ProfileResults> entry : results.entrySet()) {
            Searcher searcher = entry.getKey();
            ProfileResults info = entry.getValue();
            for (Map.Entry<String, ProfileResult> result : info.getAllResults().entrySet()) {
                String testName = result.getKey();
                ProfileResult testInfo = result.getValue();
                String output = String.format(message, searcher, testName,
                                              testInfo.forwardReaderStats.searchTime,
                                              testInfo.forwardReaderStats.searchMatches.size(),
                                              testInfo.forwardBytesStats.searchTime,
                                              testInfo.forwardBytesStats.searchMatches.size(),
                                              testInfo.backwardReaderStats.searchTime,
                                              testInfo.backwardReaderStats.searchMatches.size(),
                                              testInfo.backwardBytesStats.searchTime,
                                              testInfo.backwardBytesStats.searchMatches.size() );
               System.out.println(output);
            }
        }
    }
    
    
}
