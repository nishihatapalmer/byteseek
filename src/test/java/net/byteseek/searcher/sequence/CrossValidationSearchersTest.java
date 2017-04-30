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
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.SearchResult;
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
 *
 * Created by matt on 03/05/16.
 */
public class CrossValidationSearchersTest {

    private static int[] windowSizes = {4096, 4095, 4097, 128, 127, 15, 16, 17};

    public static final int NUM_RANDOM_TESTS = 500; // 5000 has detected issues which 1000 did not, but takes a fair amount of time to run.
    Random random = new Random(0);

    private List<SequenceSearcher<SequenceMatcher>> searchers;

    SearchData[] data = {
            new SearchData("/romeoandjuliet.txt", "to", "art", "thou", "Going", "search", "swifter", "wherefor", "wherefore", "I see thee", "I see thee now", "with speedy helpe", "as hastie powder fier'd", "Oh bid me leape", "I will stirre about", "See where she comes from shrift", "Searchers of the Towne", "Mens eyes were made to looke", "O Romeo, Romeo, wherefore art thou Romeo", "And there I am, where is my Romeo?", "O Noble Prince, I can discouer all", "Should without eyes, see path-wayes to his will", "Go then, for 'tis in vaine to seeke him here  That meanes not to be found."),
            new SearchData("/hsapiensdna.txt", "AA", "CAG", "GATACA", "TGATCGA", "CAGGAGAG", "ATCGCATGA", "TCCAGAATCT", "ACACTTGCTCTTTAGAAGAGTGCT", "ATGCCTGCAGCAGAGGAGGCACACAGAGTGTTAA", "GCAGCTTTGGCCTCCTGGGTGCAAGCCATCCTCCTGCCCCAGCCTC")
    };

    //TODO: extend to compile patterns involving byte classes rather than just simple strings.

    @Test
    public void testSearchByteArrayForwards() throws IOException {
        for (SearchData searchData : data) {
            // test defined patterns:
            for (String pattern : searchData.patterns) {
                createSearchers(pattern);
                testSearchers(pattern.getBytes(), searchData);
            }
            // test randomly selected patterns:
            for (int randomTest = 0; randomTest < NUM_RANDOM_TESTS; randomTest++) {
                byte[] pattern = getRandomPattern(searchData.getData(), randomTest);
                createSearchers(pattern);
                testSearchers(pattern, searchData);
            }
        }
    }

    @Test
    public void testSearchByteArrayBackwards() throws IOException {
        for (SearchData searchData : data) {
            // test defined patterns:
            for (String pattern : searchData.patterns) {
                createSearchers(pattern);
                testSearchersBackwards(pattern.getBytes(), searchData);
            }
            // test randomly selected patterns:
            for (int randomTest = 0; randomTest < NUM_RANDOM_TESTS; randomTest++) {
                byte[] pattern = getRandomPattern(searchData.getData(), randomTest);
                createSearchers(pattern);
                testSearchersBackwards(pattern, searchData);
            }
        }
    }

    @Test
    public void testSearchReaderForwards() throws IOException {
        for (SearchData searchData : data) {
            // test defined patterns:
            for (String pattern : searchData.patterns) {
                createSearchers(pattern);
                testReaderSearchers(pattern.getBytes(), searchData);
            }
            // test randomly selected patterns:
            for (int randomTest = 0; randomTest < NUM_RANDOM_TESTS; randomTest++) {
                byte[] pattern = getRandomPattern(searchData.getData(), randomTest);
                createSearchers(pattern);
                testReaderSearchers(pattern, searchData);
            }
        }
    }

    @Test
    public void testSearchReaderBackwards() throws IOException {
        for (SearchData searchData : data) {
            // test defined patterns:
            for (String pattern : searchData.patterns) {
                createSearchers(pattern);
                testReaderSearchersBackwards(pattern.getBytes(), searchData);
            }
            // test randomly selected patterns:
            for (int randomTest = 0; randomTest < NUM_RANDOM_TESTS; randomTest++) {
                byte[] pattern = getRandomPattern(searchData.getData(), randomTest);
                createSearchers(pattern);
                testReaderSearchersBackwards(pattern, searchData);
            }
        }
    }


    private void createSearchers(String sequence) {
        createSearchers(sequence.getBytes());
    }

    private void createSearchers(byte[] sequence) {
        searchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        searchers.add(new SequenceMatcherSearcher(sequence));
        searchers.add(new SundayQuickSearcher(sequence));
        searchers.add(new HorspoolSearcher(sequence));
        searchers.add(new UnrolledHorspoolSearcher(sequence));
        searchers.add(new SignedHorspoolSearcher(sequence));
        searchers.add(new ShiftOrSearcher(sequence));
        if (sequence.length > 3) {
            //searchers.add(new QgramFilter4Searcher(sequence));
        }
    }

    private void testSearchers(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap = new HashMap<Long, List<SequenceSearcher<SequenceMatcher>>>();
        final List<SequenceSearcher<SequenceMatcher>> usedSearchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            usedSearchers.add(searcher);
            addAllSearchPositionsFor(searcher, dataToSearch.getData(), resultMap);
        }
        findMismatches("array forwards", usedSearchers, pattern, resultMap, dataToSearch);
    }

    private void testReaderSearchers(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap = new HashMap<Long, List<SequenceSearcher<SequenceMatcher>>>();
        final List<SequenceSearcher<SequenceMatcher>> usedSearchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        for (WindowReader reader: dataToSearch.getReaders()) {
            for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
                usedSearchers.add(searcher);
                addAllSearchPositionsFor(searcher, reader, resultMap);
            }
            findMismatches("reader forwards", usedSearchers, pattern, resultMap, dataToSearch);
        }
    }

    private void testSearchersBackwards(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap = new HashMap<Long, List<SequenceSearcher<SequenceMatcher>>>();
        final List<SequenceSearcher<SequenceMatcher>> usedSearchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            usedSearchers.add(searcher);
            addAllBackwardsSearchPositionsFor(searcher, dataToSearch.getData(), resultMap);
        }
        findMismatches("array backwards", usedSearchers, pattern, resultMap, dataToSearch);
    }

    private void testReaderSearchersBackwards(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap = new HashMap<Long, List<SequenceSearcher<SequenceMatcher>>>();
        final List<SequenceSearcher<SequenceMatcher>> usedSearchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        for (WindowReader reader: dataToSearch.getReaders()) {
            for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
                usedSearchers.add(searcher);
                addAllBackwardsSearchPositionsFor(searcher, reader, resultMap);
            }
            findMismatches("reader backwards", usedSearchers, pattern, resultMap, dataToSearch);
        }
    }

    private void addAllSearchPositionsFor(SequenceSearcher<SequenceMatcher> searcher,
                                          byte[] dataToSearch,
                                          Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        final int LENGTH = dataToSearch.length;
        int result = searcher.searchSequenceForwards(dataToSearch);
        List<SearchResult<SequenceMatcher>> resultList = searcher.searchForwards(dataToSearch);
        assertTrue(resultList.size() < 2); // no more than one result.
        long result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
        assertEquals(result, result2);
        if ( result < 0) {
            return;
        }
        addResult(result, searcher, resultMap);
        int position = result + 1;
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
    }

    private void addAllSearchPositionsFor(SequenceSearcher<SequenceMatcher> searcher,
                                          WindowReader dataToSearch,
                                          Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        try {
            final long LENGTH = dataToSearch.length();

            long result = searcher.searchSequenceForwards(dataToSearch);
            List<SearchResult<SequenceMatcher>> resultList = searcher.searchForwards(dataToSearch);
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

    private void addAllBackwardsSearchPositionsFor(SequenceSearcher<SequenceMatcher> searcher,
                                          byte[] dataToSearch,
                                          Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        final int LENGTH = dataToSearch.length;

        int result = searcher.searchSequenceBackwards(dataToSearch);
        List<SearchResult<SequenceMatcher>> resultList = searcher.searchBackwards(dataToSearch);
        assertTrue(resultList.size() < 2); // no more than one result.
        long result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
        assertEquals(result, result2);
        if (result < 0) {
            return;
        }
        addResult(result, searcher, resultMap);
        int position = result - 1;
        while (position >= 0) {
            result = searcher.searchSequenceBackwards(dataToSearch, position);
            resultList = searcher.searchBackwards(dataToSearch, position);
            assertTrue(resultList.size() < 2); // no more than one result.
            result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
            assertEquals(result, result2);
            if (result < 0) {
                return;
            }
            addResult(result, searcher, resultMap);
            if (result > position) {
                // do search again so we can debug if we want to at this point:
                //result = searcher.searchSequenceBackwards(dataToSearch, position);
                fail("Searcher " + searcher + " returned a match at " + result + " after current search position at " + position);
            }
            position = result - 1;
        }
    }

    private void addAllBackwardsSearchPositionsFor(SequenceSearcher<SequenceMatcher> searcher,
                                                   WindowReader dataToSearch,
                                                   Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        try {
            final long LENGTH = dataToSearch.length();
            long result = searcher.searchSequenceBackwards(dataToSearch);
            List<SearchResult<SequenceMatcher>> resultList = searcher.searchBackwards(dataToSearch);
            assertTrue(resultList.size() < 2); // no more than one result.
            long result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
            assertEquals(result, result2);
            if (result < 0) {
                return;
            }
            addResult(result, searcher, resultMap);

            long position = result - 1;
            while (position >= 0) {
                result = searcher.searchSequenceBackwards(dataToSearch, position);
                resultList = searcher.searchBackwards(dataToSearch, position);
                assertTrue(resultList.size() < 2); // no more than one result.
                result2 = resultList.isEmpty()? -1 : resultList.get(0).getMatchPosition();
                assertEquals(result, result2);
                if (result < 0) {
                    return;
                }
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
                           SequenceSearcher<SequenceMatcher> searcher,
                           Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        List<SequenceSearcher<SequenceMatcher>> wrapperList = resultMap.get(value);
        if (wrapperList == null) {
            wrapperList = new ArrayList<SequenceSearcher<SequenceMatcher>>();
            resultMap.put(value, wrapperList);
        }
        wrapperList.add(searcher);
    }

    private void findMismatches(String searchDescription,
                                List<SequenceSearcher<SequenceMatcher>> usedSearchers,
                                byte[] pattern,
                                Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap,
                                SearchData dataToSearch) {
        final int NUM_SEARCHERS = usedSearchers.size();
        final List<String> errors = new ArrayList<String>();
        for (Map.Entry<Long, List<SequenceSearcher<SequenceMatcher>>> entry : resultMap.entrySet()) {
            //System.out.println("Match found for " + description + " at " + entry.getKey());
            String message = searchDescription + "\t" + dataToSearch.dataFile + "\t" + ByteUtils.bytesToString(true, pattern) + "\tmatch at\t" + entry.getKey() ;
            List<SequenceSearcher<SequenceMatcher>> resultsForEntry = entry.getValue();
            if (resultsForEntry.size() != NUM_SEARCHERS) {
                Set<SequenceSearcher<SequenceMatcher>> newSet = new HashSet<SequenceSearcher<SequenceMatcher>>(usedSearchers);
                message += "\tfound by\t";
                boolean first = true;
                for (SequenceSearcher<SequenceMatcher> searcher : resultsForEntry) {
                    if (!first) message += ','; first = false;
                    message += searcher.getClass().getSimpleName();
                    newSet.remove(searcher);
                }
                if (newSet.size() > 0) {
                    message += "\tnot found by\t";
                    first = true;
                    for (SequenceSearcher<SequenceMatcher> searcher : newSet) {
                        if (!first) message += ','; first = false;
                        message += searcher.getClass().getSimpleName();

                        debugFailedSearcher(searcher, entry.getKey(), dataToSearch.dataFile);
                    }
                    //System.out.println(message);
                    errors.add(message);
                    fail("Mismatches occurred: " + errors);
                }
            }
        }
        if (errors.size() > 0) {
            fail("Mismatches occurred: " + errors);
        }
    }

    private void debugFailedSearcher(SequenceSearcher searcher, long failedAtPosition, String dataToSearch)  {
        //debugFailedSearcherBytes(searcher, failedAtPosition, dataToSearch);
        //debugFailedSearcherWindow(searcher, failedAtPosition, dataToSearch);
    }

    private void debugFailedSearcherBytes(SequenceSearcher searcher, long failedAtPosition, String dataToSearch)  {
        //byte[] data = loadDataToSearch(dataToSearch);
        //searcher.searchSequenceForwards(data);
        //searcher.searchSequenceBackwards(data);
    }

    private void debugFailedSearcherWindow(SequenceSearcher searcher, long failedAtPosition, String dataToSearch) {
        try {
            WindowReader reader = loadFileReader(dataToSearch);
            searcher.searchSequenceForwards(reader);
            //searcher.searchSequenceBackwards(reader, 102408);
        } catch (IOException ex) {
            fail("IO Exception when reading");
        }
    }

    private void findCountMismatches(byte[] pattern, Map<Integer, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        if (resultMap.size() == 0) {
            throw new RuntimeException("Did not manage to get any counts at all - test bug.");
        }
        if (resultMap.size() > 1) { // more than one count - mismatches exist.
            String message = "Search counts do not return the same value.\tPattern = " + Arrays.toString(pattern);
            for (Map.Entry<Integer, List<SequenceSearcher<SequenceMatcher>>> entry : resultMap.entrySet()) {
                message += "\tCount=" + entry.getKey() + "\t";
                List<SequenceSearcher<SequenceMatcher>> resultsForEntry = entry.getValue();
                message += "\tfound by\t";
                boolean first = true;
                for (SequenceSearcher<SequenceMatcher> searcher : resultsForEntry) {
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

        public SearchData(String resourceName, String... patterns) {
            this.dataFile = resourceName;
            this.patterns = patterns;
        }
        public byte[] getData()  {
            if (dataToSearch == null) {
                dataToSearch = loadDataToSearch(dataFile);
            }
            return dataToSearch;
        };
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
        if (length < 2) {
            length = 2; // can't have a length less than 2 for SignedSuffixSearcher.
        }
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
