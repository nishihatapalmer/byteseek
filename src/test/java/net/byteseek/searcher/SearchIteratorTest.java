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
package net.byteseek.searcher;

import net.byteseek.io.IOUtils;
import net.byteseek.io.reader.FileReader;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class SearchIteratorTest extends SearchersToTest {

    byte[] data;
    WindowReader reader;
    Random random = new Random();

    @Parameterized.Parameters
    public static Collection patterns() {
        return Arrays.asList(new String[][] {
                {"one"}, {"longer pattern to search"}
        });
    }

    public SearchIteratorTest(String pattern) {
        createSearchers(pattern, false);
        try {
            createSearchData();
        } catch (IOException e) {
            throw new RuntimeException("IO Exception occured: " + e.getMessage(), e);
        }
    }

    //TODO: test backward iteration with from/to methods.

    @Test
    public void testSearchAllByteArray() throws IOException {
        Map<Searcher, List<MatchResult>> allResults = new HashMap<>();
        for (Searcher searcher : searchers) {
            SearchIterator iterator = new SearchIterator(searcher, data);
            List<MatchResult> iteratorResults = runSearchIterator(iterator);
            allResults.put(searcher, iteratorResults);

            List<MatchResult> ourResults = searchWith(searcher, data, 0, Integer.MAX_VALUE);
            compareResults(searcher, ourResults, iteratorResults);
        }
        validateResults(allResults);
    }

    @Test
    public void testSearchAllWindowReader() throws IOException {
        Map<Searcher, List<MatchResult>> allResults = new HashMap<>();
        for (Searcher searcher : searchers) {
            SearchIterator iterator = new SearchIterator(searcher, reader);
            List<MatchResult> iteratorResults = runSearchIterator(iterator);
            allResults.put(searcher, iteratorResults);

            List<MatchResult> ourResults = searchWith(searcher, reader, 0, Long.MAX_VALUE);
            compareResults(searcher, ourResults, iteratorResults);
        }
        validateResults(allResults);
    }



    @Test
    public void testSearchFromByteArray() throws IOException {
        for (int testNo = 0; testNo < 10; testNo++) {
            int from = random.nextInt(data.length / 2);
            Map<Searcher, List<MatchResult>> allResults = new HashMap<>();
            for (Searcher searcher : searchers) {
                SearchIterator iterator = new SearchIterator(searcher, data, from);
                List<MatchResult> iteratorResults = runSearchIterator(iterator);
                allResults.put(searcher, iteratorResults);

                List<MatchResult> ourResults = searchWith(searcher, data, from, Integer.MAX_VALUE);
                compareResults(searcher, ourResults, iteratorResults);
            }
            validateResults(allResults);
        }
    }

    @Test
    public void testSearchFromWindowReader() throws IOException {
        for (int testNo = 0; testNo < 10; testNo++) {
            long from = random.nextInt(data.length / 2);
            Map<Searcher, List<MatchResult>> allResults = new HashMap<>();
            for (Searcher searcher : searchers) {
                SearchIterator iterator = new SearchIterator(searcher, reader, from);
                List<MatchResult> iteratorResults = runSearchIterator(iterator);
                allResults.put(searcher, iteratorResults);

                List<MatchResult> ourResults = searchWith(searcher, reader, from, Long.MAX_VALUE);
                compareResults(searcher, ourResults, iteratorResults);
            }
            validateResults(allResults);
        }
    }

    @Test
    public void testSearchFromToByteArray() throws IOException {
        for (int testNo = 0; testNo < 10; testNo++) {
            int from = random.nextInt(data.length / 2);
            int to   = from + random.nextInt(data.length / 2);
            Map<Searcher, List<MatchResult>> allResults = new HashMap<>();
            for (Searcher searcher : searchers) {
                SearchIterator iterator = new SearchIterator(searcher, data, from, to);
                List<MatchResult> iteratorResults = runSearchIterator(iterator);
                allResults.put(searcher, iteratorResults);

                List<MatchResult> ourResults = searchWith(searcher, data, from, to);
                compareResults(searcher, ourResults, iteratorResults);
            }
            validateResults(allResults);
        }
    }

    @Test
    public void testSearchFromToWindowReader() throws IOException {
        for (int testNo = 0; testNo < 10; testNo++) {
            int from = random.nextInt(data.length / 2);
            long to   = from + random.nextInt(data.length / 2);
            Map<Searcher, List<MatchResult>> allResults = new HashMap<>();
            for (Searcher searcher : searchers) {
                SearchIterator iterator = new SearchIterator(searcher, reader, from, to);
                List<MatchResult> iteratorResults = runSearchIterator(iterator);

                allResults.put(searcher, iteratorResults);
                List<MatchResult> ourResults = searchWith(searcher, reader, from, to);
                compareResults(searcher, ourResults, iteratorResults);
            }
            validateResults(allResults);
        }
    }


    /*
     ******************************** private methods **********************************
     */


    private List<MatchResult> runSearchIterator(SearchIterator iterator) throws IOException {
        List<MatchResult> allResults = new ArrayList<>();
        while (iterator.hasNext()) {
            allResults.addAll(iterator.next());
        }
        return allResults;
    }

    /**
     * Validates that the result of iterating over the data with multiple searchers
     * always returns the same results.
     *
     * @param allResults a map of searcher to the list of search results obtained from them.
     */
    private void validateResults(Map<Searcher, List<MatchResult>> allResults) {
        List<MatchResult> lastResults = null;
        for (Searcher searcher : searchers) {
            List<MatchResult> results = allResults.get(searcher);
            if (lastResults != null) {
                compareResults(searcher, lastResults, results);
            }
            lastResults = results;
        }
    }

    private void compareResults(Searcher searcher, List<MatchResult> lastResults, List<MatchResult> results) {
        assertEquals("size of results for: " + searcher + " should match last size of " +
                        lastResults.size(), lastResults.size(), results.size());
        for (int i = 0; i < results.size(); i++) {
            // We assume that results always come back in the same order...
            // This may not be a safe assumption for some search algorithms - but for
            // simple sequence searchers it should hold.
            MatchResult result = results.get(i);
            MatchResult lastResult = lastResults.get(i);
            assertTrue("Comparing searcher: " + searcher + " result: " + result + " with " + lastResult,
                    result.equals(lastResult));
        }
    }

    private List<MatchResult> searchWith(Searcher searcher, byte[] data, int from, int to) {
        List<MatchResult> results = new ArrayList<MatchResult>();
        List<MatchResult> result = searcher.searchForwards(data, from, to);
        if (!result.isEmpty()) {
            results.addAll(result);
            int nextPos = (int) result.get(0).getMatchPosition() + 1;  // assume we only get single matches.
            while (!(result = searcher.searchForwards(data, nextPos, to)).isEmpty()) {
                results.addAll(result);
                nextPos = (int) result.get(0).getMatchPosition() + 1;  // assume we only get single matches.
            }
        }
        return results;
    }

    private List<MatchResult> searchWith(Searcher searcher, WindowReader reader, long from, long to) throws IOException {
        List<MatchResult> results = new ArrayList<MatchResult>();
        List<MatchResult> result = searcher.searchForwards(reader, from, to);
        if (!result.isEmpty()) {
            results.addAll(result);
            int nextPos = (int) result.get(0).getMatchPosition() + 1;  // assume we only get single matches.
            while (!(result = searcher.searchForwards(reader, nextPos, to)).isEmpty()) {
                results.addAll(result);
                nextPos = (int) result.get(0).getMatchPosition() + 1;  // assume we only get single matches.
            }
        }
        return results;
    }

    private void createSearchData() throws IOException {
        File file = getFile("/romeoandjuliet.txt");
        reader = new FileReader(file);
        data = new byte[(int) file.length()];
        IOUtils.readBytes(new FileInputStream(file), data);
    }

    private File getFile(final String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        return new File(url.getPath());
    }

}