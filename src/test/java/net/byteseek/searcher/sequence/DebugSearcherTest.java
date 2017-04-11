package net.byteseek.searcher.sequence;

import net.byteseek.io.reader.FileReader;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.searcher.Searcher;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by matt on 11/04/17.
 */
public class DebugSearcherTest {

    private SequenceSearcher searcher;
    private String resourceName = "/romeoandjuliet.txt";
    //String private resourceName = "/hsapiensdna.txt";
    private String pattern      = "ABCD";

    @Before
    public void createSearcher() {
        //searcher = new SequenceMatcherSearcher(pattern);
        searcher = new QgramFilter4Searcher(pattern);
    }

    @Test
    public void testSearcherBytesForwards() {
        int result = searcher.searchSequenceForwards(bytesFrom(resourceName),
                0 ,0 );
    }

    @Test
    public void testSearcherBytesBackwards() {
        int result = searcher.searchSequenceBackwards(bytesFrom(resourceName),
                0 ,0 );
    }

    @Test
    public void testSearcherReaderForwards() throws IOException {
        long result = searcher.searchSequenceForwards(readerFrom(resourceName),
                0 ,0 );
    }

    @Test
    public void testSearcherReaderBackwards() throws IOException {
        long result = searcher.searchSequenceBackwards(readerFrom(resourceName),
                0 ,0 );
    }


    //---------------------------------------------------------------------------------------------

    private byte[] bytesFrom(String resourceName)  {
        return getBytes(resourceName);
    }

    private WindowReader readerFrom(String resourceName) {
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
