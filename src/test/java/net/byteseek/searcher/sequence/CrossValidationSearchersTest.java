package net.byteseek.searcher.sequence;

import net.byteseek.io.reader.FileReader;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.ByteUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.fail;

/**
 * Searches for the same patterns using all sequence searchers and reports any mismatches between them.
 * This helps us to detect bugs in the search algorithms by ensuring they all find the same patterns
 * in the same texts in the same positions.
 *
 * Created by matt on 03/05/16.
 */
public class CrossValidationSearchersTest {

    Random random = new Random(0);

    private List<SequenceSearcher<SequenceMatcher>> searchers;
    SearchData[] data = {
            new SearchData("/romeoandjuliet.txt", "to", "art", "thou", "Going", "search", "swifter", "wherefor", "wherefore", "I see thee", "I see thee now", "with speedy helpe", "as hastie powder fier'd", "Oh bid me leape", "I will stirre about", "See where she comes from shrift", "Searchers of the Towne", "Mens eyes were made to looke", "O Romeo, Romeo, wherefore art thou Romeo", "And there I am, where is my Romeo?", "O Noble Prince, I can discouer all", "Should without eyes, see path-wayes to his will", "Go then, for 'tis in vaine to seeke him here  That meanes not to be found."),
            new SearchData("/hsapiensdna.txt", "AA", "CAG", "GATACA", "TGATCGA", "CAGGAGAG", "ATCGCATGA", "TCCAGAATCT", "ACACTTGCTCTTTAGAAGAGTGCT", "ATGCCTGCAGCAGAGGAGGCACACAGAGTGTTAA", "GCAGCTTTGGCCTCCTGGGTGCAAGCCATCCTCCTGCCCCAGCCTC")
    };

    //TODO: extend to search backwards and using window searching.
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
            for (int randomTest = 0; randomTest < 1000; randomTest++) {
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
            for (int randomTest = 0; randomTest < 1000; randomTest++) {
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
            for (int randomTest = 0; randomTest < 1000; randomTest++) {
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
            for (int randomTest = 0; randomTest < 1000; randomTest++) {
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
            searchers.add(new QgramFilter4Searcher(sequence));
        }
    }

    private void testSearchers(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap = new HashMap<Long, List<SequenceSearcher<SequenceMatcher>>>();
        final List<SequenceSearcher<SequenceMatcher>> usedSearchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            usedSearchers.add(searcher);
            addAllSearchPositionsFor(searcher, dataToSearch.getData(), resultMap);
        }
        findMismatches(usedSearchers, pattern, resultMap, dataToSearch);
    }

    private void testReaderSearchers(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap = new HashMap<Long, List<SequenceSearcher<SequenceMatcher>>>();
        final List<SequenceSearcher<SequenceMatcher>> usedSearchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            usedSearchers.add(searcher);
            addAllSearchPositionsFor(searcher, dataToSearch.getReader(), resultMap);
        }
        findMismatches(usedSearchers, pattern, resultMap, dataToSearch);
    }


    private void testSearchersBackwards(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap = new HashMap<Long, List<SequenceSearcher<SequenceMatcher>>>();
        final List<SequenceSearcher<SequenceMatcher>> usedSearchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            usedSearchers.add(searcher);
            addAllBackwardsSearchPositionsFor(searcher, dataToSearch.getData(), resultMap);
        }
        findMismatches(usedSearchers, pattern, resultMap, dataToSearch);
    }

    private void testReaderSearchersBackwards(byte[] pattern, SearchData dataToSearch) {
        final Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap = new HashMap<Long, List<SequenceSearcher<SequenceMatcher>>>();
        final List<SequenceSearcher<SequenceMatcher>> usedSearchers = new ArrayList<SequenceSearcher<SequenceMatcher>>();
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            usedSearchers.add(searcher);
            addAllBackwardsSearchPositionsFor(searcher, dataToSearch.getReader(), resultMap);
        }
        findMismatches(usedSearchers, pattern, resultMap, dataToSearch);
    }

    private void addAllSearchPositionsFor(SequenceSearcher<SequenceMatcher> searcher,
                                          byte[] dataToSearch,
                                          Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        final int LENGTH = dataToSearch.length;
        int position = 0;
        while (position < LENGTH) {
            int result = searcher.searchSequenceForwards(dataToSearch, position);
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

    private void addAllSearchPositionsFor(SequenceSearcher<SequenceMatcher> searcher,
                                          WindowReader dataToSearch,
                                          Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        try {
            final long LENGTH = dataToSearch.length();
            long position = 0;
            while (position < LENGTH) {
                long result = searcher.searchSequenceForwards(dataToSearch, position);
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
        } catch (IOException ex) {
            throw new RuntimeException("IO Exception ocurred searching forwards with " + searcher + " in " + dataToSearch);
        }
    }

    private void addAllBackwardsSearchPositionsFor(SequenceSearcher<SequenceMatcher> searcher,
                                          byte[] dataToSearch,
                                          Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap) {
        final int LENGTH = dataToSearch.length;
        int position = LENGTH - 1;
        while (position >= 0) {
            int result = searcher.searchSequenceBackwards(dataToSearch, position);
            if (result < 0) {
                return;
            }
            addResult(result, searcher, resultMap);
            if (result > position) {
                // do search again so we can debug if we want to at this point:
                // result = searcher.search(dataToSearch, position);
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
            long position = LENGTH - 1;
            while (position >= 0) {
                long result = searcher.searchSequenceBackwards(dataToSearch, position);
                if (result < 0) {
                    return;
                }
                addResult(result, searcher, resultMap);
                if (result > position) {
                    // do search again so we can debug if we want to at this point:
                    // result = searcher.search(dataToSearch, position);
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

    private void findMismatches(List<SequenceSearcher<SequenceMatcher>> usedSearchers,
                                byte[] pattern,
                                Map<Long, List<SequenceSearcher<SequenceMatcher>>> resultMap,
                                SearchData dataToSearch) {
        final int NUM_SEARCHERS = usedSearchers.size();
        final List<String> errors = new ArrayList<String>();
        for (Map.Entry<Long, List<SequenceSearcher<SequenceMatcher>>> entry : resultMap.entrySet()) {
            //System.out.println("Match found for " + description + " at " + entry.getKey());
            String message = dataToSearch.dataFile + "\t" + ByteUtils.bytesToString(true, pattern) + "\tmatch at\t" + entry.getKey() ;
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
