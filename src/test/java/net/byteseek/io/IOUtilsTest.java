package net.byteseek.io;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class IOUtilsTest {

    File asciiFile;
    File zipFile;

    @Before
    public void setUp() {
        asciiFile = getFile("/TestASCII.txt");
        zipFile   = getFile("/TestASCII.zip");
    }


    private File getFile(final String resourceName) {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }
}