/*
 * Copyright Matt Palmer, Temujin Palmer, 2017, All rights reserved.
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
package net.byteseek.searcher.sequence;

import net.byteseek.io.reader.FileReader;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.utils.ByteUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Searches for the same patterns using all sequence searchers and reports any mismatches between them.
 * This helps us to detect bugs in the search algorithms by ensuring they all find the same patterns
 * in the same texts in the same positions.
 * <p>
 * It also does some very unscientific timings of the search algorithms.  This should not be relied on as
 * accurate benchmarks of the algorithms, but it does flag when algorithms have severely degraded performance.
 * There is another JMH (Java Microbenchmarking Harness) project to benchmark the search algorithms against each other
 * and over different types of data and pattern lengths.
 *
 * Created by matt on 03/05/16.
 */
public class CrossValidationSearchersTest extends SearchersToTest {

    //private static int[] windowSizes = {4096, 4095, 4097, 128, 127, 15, 16}; // more complex tests take ages to run, look for boundary conditions.
    private static int[] windowSizes = {4096}; // simple test with window of 4096 byte size (default).

    public static final int NUM_RANDOM_TESTS = 1000; // 5000 has detected issues which 1000 did not, but takes a fair amount of time to run.
    public static final int TIMING_UPDATE_INTERVAL = 100; // refresh timings every number of tests...

    Random random = new Random(0);

    SearchData[] data = {
         //   new SearchData("/romeoandjuliet.txt", "textAlphabet", "to", "art", "thou", "Going", "search", "swifter", "wherefor", "wherefore", "I see thee", "I see thee now", "with speedy helpe", "as hastie powder fier'd", "Oh bid me leape", "I will stirre about", "See where she comes from shrift", "Searchers of the Towne", "Mens eyes were made to looke", "O Romeo, Romeo, wherefore art thou Romeo", "And there I am, where is my Romeo?", "O Noble Prince, I can discouer all", "Should without eyes, see path-wayes to his will", "Go then, for 'tis in vaine to seeke him here  That meanes not to be found."),
            new SearchData("/romeoandjuliet.txt", "textAlphabet", "Going", "search", "swifter", "wherefor", "wherefore", "I see thee", "I see thee now", "with speedy helpe", "as hastie powder fier'd", "Oh bid me leape", "I will stirre about", "See where she comes from shrift", "Searchers of the Towne", "Mens eyes were made to looke", "O Romeo, Romeo, wherefore art thou Romeo", "And there I am, where is my Romeo?", "O Noble Prince, I can discouer all", "Should without eyes, see path-wayes to his will", "Go then, for 'tis in vaine to seeke him here  That meanes not to be found."),
            new SearchData("/hsapiensdna.txt", "lowAlphabet", "AA", "CAG", "GATACA", "TGATCGA", "CAGGAGAG", "ATCGCATGA", "TCCAGAATCT", "ACACTTGCTCTTTAGAAGAGTGCT", "ATGCCTGCAGCAGAGGAGGCACACAGAGTGTTAA", "GCAGCTTTGGCCTCCTGGGTGCAAGCCATCCTCCTGCCCCAGCCTC")
    };

    //TODO: extend to compile patterns involving byte classes rather than just simple strings.
    //      have case insensitive tests now, but still need more involved tests with more complex classes.

    //TODO: cross validate reader results against array results (load entire file into a single array).

    @Test
    public void testSearchByteArrayForwards() throws Exception {
        final Map<String, Long> searcherTimings = new HashMap<String, Long>();
        int numTimes = 0;
        for (SearchData searchData : data) {
            // test defined patterns:
            System.out.println("Running byte array forwards defined tests for " + searchData.dataFile);
            for (String pattern : searchData.patterns) {
                createSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testSearchers(pattern.getBytes(), searchData));
                numTimes++;
            }
            // test randomly selected patterns:
            for (int randomTest = 1; randomTest < NUM_RANDOM_TESTS; randomTest++) {
                if (randomTest % TIMING_UPDATE_INTERVAL == 0) {
                    outputTimes(searcherTimings, searchData.dataFile + ": " + randomTest + " of " + NUM_RANDOM_TESTS);
                }
                byte[] pattern = getRandomPattern(searchData.getData(), randomTest);
                createSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testSearchers(pattern, searchData));
                numTimes++;

                createCaseInsensitiveSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testSearchers(pattern, searchData));
                numTimes++;
            }
        }
    }

   // @Test
    public void testSearchByteArrayBackwards() throws Exception {
        final Map<String, Long> searcherTimings = new HashMap<String, Long>();
        int numTimes = 0;
        for (SearchData searchData : data) {
            // test defined patterns:
            System.out.println("Running byte array backwards defined tests for " + searchData.dataFile);
            for (String pattern : searchData.patterns) {
                createSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testSearchersBackwards(pattern.getBytes(), searchData));
                numTimes++;
            }
            // test randomly selected patterns:
            for (int randomTest = 1; randomTest < NUM_RANDOM_TESTS; randomTest++) {
                if (randomTest % TIMING_UPDATE_INTERVAL == 0) {
                    outputTimes(searcherTimings, searchData.dataFile + ": " + randomTest + " of " + NUM_RANDOM_TESTS);
                }
                byte[] pattern = getRandomPattern(searchData.getData(), randomTest);
                createSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testSearchersBackwards(pattern, searchData));
                numTimes++;

                createCaseInsensitiveSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testSearchersBackwards(pattern, searchData));
                numTimes++;
            }
        }
    }

//    @Test
    public void testSearchReaderForwards() throws Exception {
        final Map<String, Long> searcherTimings = new HashMap<String, Long>();
        int numTimes = 0;
        for (SearchData searchData : data) {
            // test defined patterns:
            System.out.println("Running reader forwards defined tests for " + searchData.dataFile);
            for (String pattern : searchData.patterns) {
                createSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testReaderSearchers(pattern.getBytes(), searchData));
                numTimes++;
            }
            // test randomly selected patterns:
            for (int randomTest = 1; randomTest < NUM_RANDOM_TESTS; randomTest++) {
                if (randomTest % TIMING_UPDATE_INTERVAL == 0) {
                    outputTimes(searcherTimings, searchData.dataFile + ": " + randomTest + " of " + NUM_RANDOM_TESTS);
                }
                byte[] pattern = getRandomPattern(searchData.getData(), randomTest);
                createSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testReaderSearchers(pattern, searchData));
                numTimes++;

                createCaseInsensitiveSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testReaderSearchers(pattern, searchData));
                numTimes++;
            }
        }
    }

//    @Test
    public void testSearchReaderBackwards() throws Exception {
        final Map<String, Long> searcherTimings = new HashMap<String, Long>();
        int numTimes = 0;
        for (SearchData searchData : data) {
            System.out.println("Running reader backwards defined tests for " + searchData.dataFile);

            // test defined patterns:
            for (String pattern : searchData.patterns) {
                createSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testReaderSearchersBackwards(pattern.getBytes(), searchData));
                numTimes++;
            }
            // test randomly selected patterns:
            for (int randomTest = 1; randomTest < NUM_RANDOM_TESTS; randomTest++) {
                if (randomTest % TIMING_UPDATE_INTERVAL == 0) {
                    outputTimes(searcherTimings, searchData.dataFile + ": " + randomTest + " of " + NUM_RANDOM_TESTS);
                }
                byte[] pattern = getRandomPattern(searchData.getData(), randomTest);
                createSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testReaderSearchersBackwards(pattern, searchData));
                numTimes++;

                createCaseInsensitiveSearchers(pattern, searchData.lowAlphabet());
                updateTimes(searcherTimings, testReaderSearchersBackwards(pattern, searchData));
                numTimes++;
            }
        }
    }

    private void updateTimes(Map<String, Long> oldTimes, Map<String, Long> newTimes) {
        for (String searcher : newTimes.keySet()) {
            final long newTime = newTimes.get(searcher);
            final Long existingTime = oldTimes.get(searcher);
            if (existingTime == null) {
                oldTimes.put(searcher, newTime);
            } else {
                oldTimes.put(searcher, existingTime + newTime);
            }
        }
    }

    private void outputTimes(Map<String, Long> timings, String searchData) {
        System.out.println("Av time\tNum times\tTotal time\tSearcher\t\tData: " + searchData);
        Map<String, Long> sortedSearchers = MapUtil.sortByValue(timings);
        for (String searcher : sortedSearchers.keySet()) {
            final long time = timings.get(searcher);
            System.out.println(time / TIMING_UPDATE_INTERVAL + "\t" + TIMING_UPDATE_INTERVAL + "\t" + time + "\t" + searcher);
        }
        timings.clear(); // start afresh after outputting current timings.
    }

    /**
     * Returns a map whose keys are sorted on its values.
     * Taken from code published at StackOverflow, stating it can be used freely:
     * https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java#2581754
     */
    public static class MapUtil
    {
        public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
        {
            List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
            Collections.sort( list, new Comparator<Map.Entry<K, V>>()
            {
                public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
                {
                    return (o1.getValue()).compareTo( o2.getValue() );
                }
            } );

            Map<K, V> result = new LinkedHashMap<K, V>();
            for (Map.Entry<K, V> entry : list)
            {
                result.put( entry.getKey(), entry.getValue() );
            }
            return result;
        }
    }

    private Map<String, Long> testSearchers(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher>> resultMap = new HashMap<Long, List<SequenceSearcher>>();
        final Map<String, Long> searcherTimings = new HashMap<String, Long>();
        final List<SequenceSearcher> usedSearchers = new ArrayList<SequenceSearcher>();
        for (SequenceSearcher searcher : searchers) {
            usedSearchers.add(searcher);
            long startTime = System.nanoTime();
            addAllSearchPositionsFor(searcher, dataToSearch.getData(), resultMap);
            long endTime = System.nanoTime();
            searcherTimings.put(searcher.getClass().getSimpleName(), endTime - startTime);
        }
        findMismatches("array forwards", usedSearchers, pattern, resultMap, dataToSearch);
        return searcherTimings;
    }

    private Map<String, Long> testReaderSearchers(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher>> resultMap = new HashMap<Long, List<SequenceSearcher>>();
        final Map<String, Long> searcherTimings = new HashMap<String, Long>();
        final List<SequenceSearcher> usedSearchers = new ArrayList<SequenceSearcher>();
        for (WindowReader reader: dataToSearch.getReaders()) {
            for (SequenceSearcher searcher : searchers) {
                usedSearchers.add(searcher);
                long startTime = System.nanoTime();
                addAllSearchPositionsFor(searcher, reader, resultMap);
                long endTime = System.nanoTime();
                searcherTimings.put(searcher.getClass().getSimpleName(), endTime - startTime);
            }
            findMismatches("reader forwards", usedSearchers, pattern, resultMap, dataToSearch);
        }
        return searcherTimings;
    }

    private Map<String, Long> testSearchersBackwards(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher>> resultMap = new HashMap<Long, List<SequenceSearcher>>();
        final Map<String, Long> searcherTimings = new HashMap<String, Long>();
        final List<SequenceSearcher> usedSearchers = new ArrayList<SequenceSearcher>();
        for (SequenceSearcher searcher : searchers) {
            usedSearchers.add(searcher);
            long startTime = System.nanoTime();
            addAllBackwardsSearchPositionsFor(searcher, dataToSearch.getData(), resultMap);
            long endTime = System.nanoTime();
            searcherTimings.put(searcher.getClass().getSimpleName(), endTime - startTime);
        }
        findMismatches("array backwards", usedSearchers, pattern, resultMap, dataToSearch);
        return searcherTimings;
    }

    private Map<String, Long> testReaderSearchersBackwards(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher>> resultMap = new HashMap<Long, List<SequenceSearcher>>();
        final Map<String, Long> searcherTimings = new HashMap<String, Long>();
        final List<SequenceSearcher> usedSearchers = new ArrayList<SequenceSearcher>();
        for (WindowReader reader: dataToSearch.getReaders()) {
            for (SequenceSearcher searcher : searchers) {
                usedSearchers.add(searcher);
                long startTime = System.nanoTime();
                addAllBackwardsSearchPositionsFor(searcher, reader, resultMap);
                long endTime = System.nanoTime();
                searcherTimings.put(searcher.getClass().getSimpleName(), endTime - startTime);
            }
            findMismatches("reader backwards", usedSearchers, pattern, resultMap, dataToSearch);
        }
        return searcherTimings;
    }

    private void addAllSearchPositionsFor(SequenceSearcher searcher,
                                          byte[] dataToSearch,
                                          Map<Long, List<SequenceSearcher>> resultMap) {
        final int LENGTH = dataToSearch.length;
        int result = searcher.searchSequenceForwards(dataToSearch);
        List<MatchResult> resultList = searcher.searchForwards(dataToSearch);
        assertTrue(resultList.size() < 2); // no more than one result.
        long result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
        if (result < 0) {
            assertTrue(result2 < 0);
            return;
        }
        assertEquals(result, result2);
        addResult(result, searcher, resultMap);
        int position = result + 1;
        while (position < LENGTH) {
            result = searcher.searchSequenceForwards(dataToSearch, position);
            resultList = searcher.searchForwards(dataToSearch, position);
            assertTrue(resultList.size() < 2); // no more than one result.
            result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
            if (result < 0) {
                assertTrue(result2 < 0);       // both are negative.
            } else {
                assertEquals(result, result2); // value is the same if positive.
            }

            if (result < 0) {
                return;
            }
            addResult(result, searcher, resultMap);
            if (result < position) {
                // do search again so we can debug if we want to at this point:
                result = searcher.searchSequenceForwards(dataToSearch, position);
                fail("Searcher " + searcher + " returned a match at " + result + " before current search position at " + position);
            }
            position = result + 1;
        }
    }

    private void addAllSearchPositionsFor(SequenceSearcher searcher,
                                          WindowReader dataToSearch,
                                          Map<Long, List<SequenceSearcher>> resultMap) {
        try {
            final long LENGTH = dataToSearch.length();

            long result = searcher.searchSequenceForwards(dataToSearch);
            List<MatchResult> resultList = searcher.searchForwards(dataToSearch);
            assertTrue(resultList.size() < 2); // no more than one result.
            long result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
            assertEquals(result, result2);
            if (result < 0) {
                return;
            }
            addResult(result, searcher, resultMap);
            long position =  result + 1;
            while (position < LENGTH) {
                result = searcher.searchSequenceForwards(dataToSearch, position);
                resultList = searcher.searchForwards(dataToSearch, position);
                assertTrue(resultList.size() < 2); // no more than one result.
                result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
                assertEquals(result, result2);
                if (result < 0) {
                    return;
                }
                addResult(result, searcher, resultMap);
                if (result < position) {
                    // do search again so we can debug if we want to at this point:
                    //result = searcher.searchSequenceForwards(dataToSearch, position);
                    fail("Searcher " + searcher + " returned a match at " + result + " before current search position at " + position);
                }
                position = result + 1;
            }
        } catch (IOException ex) {
            throw new RuntimeException("IO Exception ocurred searching forwards with " + searcher + " in " + dataToSearch);
        }
    }

    private void addAllBackwardsSearchPositionsFor(SequenceSearcher searcher,
                                          byte[] dataToSearch,
                                          Map<Long, List<SequenceSearcher>> resultMap) {
        final int LENGTH = dataToSearch.length;

        int result = searcher.searchSequenceBackwards(dataToSearch);
        List<MatchResult> resultList = searcher.searchBackwards(dataToSearch);
        assertTrue(resultList.size() < 2); // no more than one result.
        long result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
        if (result < 0) {
            assertTrue(result2 < 0);
            return;
        }
        assertEquals(result, result2);
        addResult(result, searcher, resultMap);
        int position = result - 1;
        while (position >= 0) {
            result = searcher.searchSequenceBackwards(dataToSearch, position);
            resultList = searcher.searchBackwards(dataToSearch, position);
            assertTrue(resultList.size() < 2); // no more than one result.
            result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
            if (result < 0) {
                assertTrue(result2 < 0);
                return;
            }
            assertEquals(result, result2);
            addResult(result, searcher, resultMap);
            if (result > position) {
                // do search again so we can debug if we want to at this point:
                //result = searcher.searchSequenceBackwards(dataToSearch, position);
                fail("Searcher " + searcher + " returned a match at " + result + " after current search position at " + position);
            }
            position = result - 1;
        }
    }

    private void addAllBackwardsSearchPositionsFor(SequenceSearcher searcher,
                                                   WindowReader dataToSearch,
                                                   Map<Long, List<SequenceSearcher>> resultMap) {
        try {
            final long LENGTH = dataToSearch.length();
            long result = searcher.searchSequenceBackwards(dataToSearch);
            List<MatchResult> resultList = searcher.searchBackwards(dataToSearch);
            assertTrue(resultList.size() < 2); // no more than one result.
            long result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
            if (result < 0) {
                assertTrue(result2 < 0);
                return;
            }
            assertEquals(result, result2);
            addResult(result, searcher, resultMap);

            long position = result - 1;
            while (position >= 0) {
                result = searcher.searchSequenceBackwards(dataToSearch, position);
                resultList = searcher.searchBackwards(dataToSearch, position);
                assertTrue(resultList.size() < 2); // no more than one result.
                result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
                if (result < 0) {
                    assertTrue(result2 < 0);
                    return;
                }
                assertEquals(result, result2);
                addResult(result, searcher, resultMap);
                if (result > position) {
                    // do search again so we can debug if we want to at this point:
                    //result = searcher.searchSequenceBackwards(dataToSearch, position);
                    fail("Searcher " + searcher + " returned a match at " + result + " after current search position at " + position);
                }
                position = result - 1;
            }
        } catch (IOException io) {
            throw new RuntimeException("IO Exception ocurred searching backwards with " + searcher + " in " + dataToSearch);
        }
    }

    private void addResult(long value,
                           SequenceSearcher searcher,
                           Map<Long, List<SequenceSearcher>> resultMap) {
        List<SequenceSearcher> wrapperList = resultMap.get(value);
        if (wrapperList == null) {
            wrapperList = new ArrayList<SequenceSearcher>();
            resultMap.put(value, wrapperList);
        }
        wrapperList.add(searcher);
    }

    private void findMismatches(String searchDescription,
                                List<SequenceSearcher> usedSearchers,
                                byte[] pattern,
                                Map<Long, List<SequenceSearcher>> resultMap,
                                SearchData dataToSearch) {
        final int NUM_SEARCHERS = usedSearchers.size();
        final List<String> errors = new ArrayList<String>();
        for (Map.Entry<Long, List<SequenceSearcher>> entry : resultMap.entrySet()) {
            //System.out.println("Match found for " + description + " at " + entry.getKey());
            String message = searchDescription + "\t" + dataToSearch.dataFile + "\t" + ByteUtils.bytesToString(true, pattern) + "\tmatch at\t" + entry.getKey() ;
            List<SequenceSearcher> resultsForEntry = entry.getValue();
            if (resultsForEntry.size() != NUM_SEARCHERS) {
                Set<SequenceSearcher> newSet = new HashSet<SequenceSearcher>(usedSearchers);
                message += "\tfound by\t";
                boolean first = true;
                for (SequenceSearcher searcher : resultsForEntry) {
                    if (!first) message += ','; first = false;
                    message += searcher.getClass().getSimpleName();
                    newSet.remove(searcher);
                }
                if (newSet.size() > 0) {
                    message += "\tnot found by\t";
                    first = true;
                    for (SequenceSearcher searcher : newSet) {
                        if (!first) message += ','; first = false;
                        message += searcher.getClass().getSimpleName();

                        debugFailedSearcher(searcher, entry.getKey(), dataToSearch.dataFile);
                    }
                    //System.out.println(message);
                    errors.add(message + "\n");
                    //fail("Mismatches occurred:\n" + errors);
                }
            }
        }
        if (errors.size() > 0) {
            fail("Mismatches occurred:\n" + errors);
        }
    }

    private void debugFailedSearcher(SequenceSearcher searcher, long failedAtPosition, String dataToSearch)  {
        debugFailedSearcherBytes(searcher, failedAtPosition, dataToSearch);
        //debugFailedSearcherWindow(searcher, failedAtPosition, dataToSearch);
    }

    private void debugFailedSearcherBytes(SequenceSearcher searcher, long failedAtPosition, String dataToSearch)  {
        byte[] data = loadDataToSearch(dataToSearch);
        int result = searcher.searchSequenceForwards(data, 99780);
        //searcher.searchSequenceBackwards(data);
    }

    private void debugFailedSearcherWindow(SequenceSearcher searcher, long failedAtPosition, String dataToSearch) {
        try {
            WindowReader reader = loadFileReader(dataToSearch);
            searcher.searchSequenceForwards(reader, failedAtPosition - 11);
            //searcher.searchSequenceBackwards(reader, failedAtPosition);
        } catch (IOException ex) {
            fail("IO Exception when reading");
        }
    }

    private void findCountMismatches(byte[] pattern, Map<Integer, List<SequenceSearcher>> resultMap) {
        if (resultMap.size() == 0) {
            throw new RuntimeException("Did not manage to get any counts at all - test bug.");
        }
        if (resultMap.size() > 1) { // more than one count - mismatches exist.
            String message = "Search counts do not return the same value.\tPattern = " + Arrays.toString(pattern);
            for (Map.Entry<Integer, List<SequenceSearcher>> entry : resultMap.entrySet()) {
                message += "\tCount=" + entry.getKey() + "\t";
                List<SequenceSearcher> resultsForEntry = entry.getValue();
                message += "\tfound by\t";
                boolean first = true;
                for (SequenceSearcher searcher : resultsForEntry) {
                    if (!first) message += ','; first = false;
                    message += searcher.getClass().getSimpleName();
                }
            }
            System.out.println(message);
            fail("Mismatches occurred: " + message);
        }
    }

    private class SearchData {
        private String dataFile;
        private  String[] patterns;
        private byte[] dataToSearch;
        private WindowReader reader;
        private List<WindowReader> readers;
        private boolean lowAlphabet;

        public SearchData(String resourceName, String lowAlphabet, String... patterns) {
            this.dataFile = resourceName;
            this.patterns = patterns;
            this.lowAlphabet = lowAlphabet.equals("lowAlphabet");
        }
        public byte[] getData()  {
            if (dataToSearch == null) {
                dataToSearch = loadDataToSearch(dataFile);
            }
            return dataToSearch;
        }

        public boolean lowAlphabet() {
            return lowAlphabet;
        }

        public WindowReader getReader() {
            if (reader == null) {
                reader = loadFileReader(dataFile);
            }
            return reader;
        }
        public List<WindowReader> getReaders() {
            if (readers == null) {
                readers = new ArrayList<WindowReader>();
                for (int size : windowSizes) {
                    readers.add(loadFileReader(dataFile, size));
                }
            }
            return readers;
        }
    }

    private byte[] getRandomPattern(byte[] dataToSearch, int length) {
        length = length > 0? length : 1; // ensure we have at least a length of one.
        int position = random.nextInt(dataToSearch.length - length - 1);
        byte[] result = Arrays.copyOfRange(dataToSearch, position, position + length);
        return result;
    }

    private byte[] loadDataToSearch(String resourceName)  {
        return getBytes(resourceName);
    }

    private WindowReader loadFileReader(String resourceName) {
        try {
            return new FileReader(getFile(resourceName));
        } catch (IOException io) {
            throw new RuntimeException("IO Exception occured reading file", io);
        }
    }

    private WindowReader loadFileReader(String resourceName, int windowSize) {
        try {
            return new FileReader(getFile(resourceName), windowSize);
        } catch (IOException io) {
            throw new RuntimeException("IO Exception occured reading file", io);
        }
    }

    private byte[] getBytes(final String resourceName) {
        try {
            File file = getFile(resourceName);
            InputStream is = new FileInputStream(file);
            long length = file.length();
            byte[] bytes = new byte[(int) length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            is.close();
            return bytes;
        } catch (IOException io) {
            throw new RuntimeException("IO Exception occured reading data", io);
        }
    }

    private File getFile(final String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        return new File(url.getPath());
    }

}
