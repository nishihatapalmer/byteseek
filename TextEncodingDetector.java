/*
 * Copyright Matt Palmer 2011-12, All rights reserved.
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

package net.byteseek.text

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * This class detects whether an array of bytes is likely to be an encoding of text,
 * and also detects the most likely character set encoding is.
 *
 * Encoding detection is done by simply using the ICU4J library:
 *
 * <a href="http://site.icu-project.org/">http://site.icu-project.org/</a>
 * <p>
 * However, all character set encoding detectors suffer from one problem,
 * which is that they assume you are passing them text of some sort in the first
 * place.  However, it is entirely possible that a byte array is pure binary data.
 * <p>
 * Hence, the first thing that the detector does is to attempt to rule out
 * bytes which are not likely to be text in the first place.  If an array is
 * not ruled out, then if the array only contained pure ASCII bytes
 * the file is flagged as US-ASCII.  If it contains other bytes,
 * then the ICU4J character encoding detector is applied to determine its encoding.
 * <p>
 * The ICU4J detector provides a confidence level for the encoding it detects.
 * If the confidence level for a given encoding falls below a configurable threshold,
 * then the file is deemed not to be text after all.
 * <p>
 * Binary data is initially ruled out by applying two heuristics which detect
 * properties of typical binary files, but not text encodings.
 * A binary file can still pass these heuristics (a false positive), but genuine
 * text encodings are very unlikely to be ruled out by them (false negatives).
 * <p>
 * These heuristics are applied:
 * <p>
 * Heuristic 1: Determine maximum consecutive non-printing symbols:
 * ----------------------------------------------------------------
 *   If the maximum number of consecutive non-printing symbols passes a
 *   configurable threshold, we deem the array to be binary, not text.
 *   ASCII, single-byte UTF-8 or LATIN-1 text encodings tend not to have runs
 *   of these characters even if they contain a few of them.
 *   <p>
 *   Multi-byte encoded unicode text also tend not to have long runs
 *   of these characters due to the innate properties of multi-byte character encoding,
 *   even if a few of these values appear scattered about.  However, binary files
 *   very frequently have runs of non-printing bytes (except compressed or encrypted files
 *   for which see Heuristic 2).
 *<p>
 * Heuristic 2: Rule out high-entropy files:
 * -----------------------------------------
 *   If the data has a high Shannon entropy (compressed or encrypted), it is
 *   deemed to be binary and not text.  We use the 
 *   <a href="http://en.wikipedia.org/wiki/Entropy_%28information_theory%29>Shannon entropy
 *   measure</a>: 
 *   <p>
 *   In simple terms, it detects how many bits per byte are required to represent 
 *   the information in a given sample.  A low number of bits implies that 
 *   there is not much information present.  A high number of bits implies that
 *   the information is very dense, and requires the full space of the byte to 
 *   represent the information in the sample.  Encryption and compression tend
 *   to make the information very dense - having a high entropy.  ASCII text files
 *   often have an entropy of around 2-4 bits per byte (as text doesn't typically
 *   use the full range of values possible in a byte and has a lot of redundancy).
 *   <p>
 *   This heuristic is necessary because data with a high entropy tend not to
 *   have repeating runs of any byte - their values are distributed across all
 *   possible byte values, so are not caught by the first heuristic.
 *   This includes common media types like gif, png, mov, wav, and compressed or
 *   otherwise encrypted files.  However, since a byte array with high entropy
 *   is also extremely unlikely to be textual, we can rule them out.
 *   Note that some multi-byte encodings (e.g. SHIFT_JIS) can have higher entropies
 *   than western ASCII text files, so by default the threshold is set higher
 *   than might be expected in order not to exclude those text files.
 *<p>
 * Heuristic 3: Does the sample only contain US-ASCII byte values?
 * ----------------------------------------------------------------
 *   If the sample only contains bytes within the US-ASCII byte value range of
 *   0x09-0x7F, then flag the file as text with an encoding of US-ASCII.  We 
 *   exclude characters below TAB (0x09) because very few genuine files of this
 *   type have these lower values.  This assumption could be tweaked - possibly
 *   it would be better to just take the entire range of valid ASCII values here.
 *<p>
 * Heuristic 4: Detect encoding, rule out low confidence detections:
 * -----------------------------------------------------------------
 *   Try to detect the encoding as if it was text using the ICU4J library.
 *   If we get a good confidence for the encoding
 *   detected, we decide it is text with the detected encoding,
 *   otherwise we rule it out as a text.
 *
 * @author Matt Palmer
 */
public class TextEncodingDetector {
    
    private static int MIN_LENGTH = 4;
    
    public static String ASCII_CHARSET = "US-ASCII";
    public static int NULL_CHARACTER = 0x00;
    public static int TAB_CHARACTER = 0x09;
    public static int NEW_LINE_CHARACTER = 0x0A;
    public static int CARRIAGE_RETURN_CHARACTER = 0x0D;
    public static int BEFORE_PRINTABLE_CHARACTER = 0x1F;
    public static int DEL_CHARACTER = 0x7F;
    public static int FINAL_VALUE = 0xFF;
    
    private byte[] unlikelyConsecutiveValues = new byte[256]; // byte values to count if they appear consecutively.
    private int unlikelyConsecutiveBytesThreshold = 4;      	// above which we rule out as a text file.
    private double entropyThreshold = 6.5;                  	// above which we rule out as a text file.
    private int encodingConfidenceThresholdPercentage = 15; 	// below which we rule out as a text file.
    private int maxBytesToScan = 2048;						 	// how much of any available text to scan to determine encoding.
    
    /**
     * Constructs a Text Encoding Detector.
     */
    public TextEncodingDetector() {
    	// Set up array of byte flags that indicate which byte values are unlikely if encountered consecutively in a text file.
        addRangeOfUnlikelyConsecutiveByteValues(NULL_CHARACTER, BEFORE_PRINTABLE_CHARACTER);
        removeUnlikelyConsecutiveByteValue(TAB_CHARACTER);
        removeUnlikelyConsecutiveByteValue(NEW_LINE_CHARACTER);
        removeUnlikelyConsecutiveByteValue(CARRIAGE_RETURN_CHARACTER);
        addUnlikelyConsecutiveByteValue(DEL_CHARACTER);
        addUnlikelyConsecutiveByteValue(FINAL_VALUE);
    }


    /**
     * Returns an array of 256 bytes that show which byte values are unlikely if encountered
     * consecutively in text (no matter what encoding scheme is used).
     * 
     * Entries which are set to the value one indicate that the byte value at that index position
     * in the array are unlikely.  If the value is zero, then that byte may appear consecutively.
     * 
     * @return An arrya of bytes flagging which entries are unlikely to be seen consecutively in a text file.
     */
    public byte[] getUnlikelyConsecutiveByteValues() {
        return unlikelyConsecutiveValues;
    }


    /**
     * Returns a threshold below which a byte array may still be text.  The threshold refers to the
     * number of distinct unlikely byte values which appeared consecutively.  For example,
     * a text file may have a run of unlikely byte values which normally don't appear in text
     * files.  However, some legitimate text encodings do have a few runs of these values.  Therefore,
     * we do not rule out as text because it had a run of unlikley consecutive values -
     * but we do if the number of these exceeds this threshold.
     * 
     * @return The number of distinct runs of consecutive unlikely byte values above which we
     *          rule out a byte array as being text.
     */
    public int getUnlikelyConsecutiveBytesThreshold() {
        return unlikelyConsecutiveBytesThreshold;
    }

    /**
     * Returns the entropy threshold (bits-per-byte) above which we do not regard bytes as being text.
     * Byte sequences with high shannon entropy (e.g. from compressed, encrypted or media files) are almost certainly not text.
     * 
     * @return The Shannon entropy threshold (bits-per-byte) above which we do not regard the bytes as a text.
     */
    public double getEntropyThreshold() {
        return entropyThreshold;
    }
    
    /**
     * Returns the maximum number of bytes to scan.
     * 
     * @return The maximum number of bytes to scan.
     */
    public int getMaxBytesToScan() {
    	return maxBytesToScan;
    }

    /**
     * Sets the maximum number of bytes to scan.
     * 
     * @param maxBytesToScan The maximum number of bytes to scan.
     * @throws IllegalArgumentException if the bytes to scan is less than the minimum length allowed (currently 4).
     */
    public void setMaxBytesToScan(int maxBytesToScan) {
    	// Preconditions:
    	if (maxBytesToScan < MIN_LENGTH) {
    		throw new IllegalArgumentException("Max bytes to scan must be greater than the minimum length" + Integer.toString(MIN_LENGTH));
    	}
    	this.maxBytesToScan = maxBytesToScan;
    }
    
    /**
     * Sets the maximum number of consecutive unlikely byte values we will tolerate before ruling out bytes as a text.
     * 
     * @param threshold The maximum number of consecutive unlikely byte values tolerated before ruling out bytes as text.
     * @throws IllegalArgumentException if the threshold value is less than one.
     */
    public void setUnlikelyConsecutiveBytesThreshold(final int threshold) {
        // Preconditions:
        if (threshold < 1) {
            throw new IllegalArgumentException("Consecutive byte threshold must be a positive integer greater than zero.");
        }
        unlikelyConsecutiveBytesThreshold = threshold;
    }

    
    /**
     * Sets the Shannon entropy threshold (bits-per-byte) above which a byte array will not be regarded as representing text.
     * 
     * @param threshold The Shannon entropy threshold above which a byte array will not be regared as representing text.
     * @throws IllegalArgumentExcepiton if the value is not between 0 and 8.
     */
    public void setEntropyThreshold(final double threshold) {
        // Preconditions:
        if (threshold < 0 || threshold > 8) {
            throw new IllegalArgumentException("Entropy threshold must be between 0 and 8 bits per byte.");
        }
        entropyThreshold = threshold;
    }


    /**
     * Adds an inclusive range of byte values which are deemed to be unlikely if encountered consecutively in text encodings.
     * 
     * @param minValue The smallest byte value which is unlikely.
     * @param maxValue The biggest byte value which is unlikely.
     */
    public void addRangeOfUnlikelyConsecutiveByteValues(int minValue, int maxValue) {
    	setRangeOfUnlikelyValues(minValue, maxValue, 1);
    }
    
    
    /**
     * Removes an inclusive range of byte values which are deemed to be unlikely if encountered consecutively in text encodings.
     * 
     * @param minValue The smallest byte value which is not unlikely.
     * @param maxValue The biggest byte value which is not unlikely.
     */
    public void removeRangeOfUnlikelyConsecutiveByteValues(int minValue, int maxValue) {
    	setRangeOfUnlikelyValues(minValue, maxValue, 0);
    }


    /**
     * Adds a single byte value which is unlikely if encountered consecutively in text encodings.
     * 
     * @param value The byte which which is unlikely if encountered consecutively in text encodings.
     */
    public void addUnlikelyConsecutiveByteValue(final int value) {
        // Preconditions:
        if ( value < 0 || value > 255 ) {
            throw new IllegalArgumentException( "Value must be between 0 and 255.");
        }
        unlikelyConsecutiveValues[value] = 1;
    }
    
    

    /**
     * Removes a single byte value which is unlikely if encountered consecutively in text encodings.
     * 
     * @param value A byte which which is likely (or at least, not unlikely) if encountered consecutively in text encodings.
     */    
    public void removeUnlikelyConsecutiveByteValue(final int value) {
    	// Preconditions:
        if ( value < 0 || value > 255 ) {
            throw new IllegalArgumentException( "Value must be between 0 and 255.");
        }    	
    	unlikelyConsecutiveValues[value] = 0;
    }



    private void setRangeOfUnlikelyValues(int from, int to, int value) {
        // Preconditions:
        if ( from < 0 || from > 255 || to < 0 || to > 255 ) {
            throw new IllegalArgumentException( "Values must be between 0 and 255.");
        }

        // Ensure min value is smaller than max value:
        if ( from > to ) {
            final int tempValue = from;
            from = to;
            to = tempValue;
        }

        // Add the range of values:
        byte valueToSet = (byte) (value & 0xFF);
        for (int index = from; index <= to; index++) {
            unlikelyConsecutiveValues[index] = valueToSet;
        }
    }

   
    /**
     * Detects the text encoding of a byte array and returns its encoding as the String name
     * which can be used to return a CharSet in Java (if it is supported), or null if no
     * encoding could be determined.
     * 
     * @param fileBytes the byte array
     * 
     * @returns Returns the text encoding given a byte array a a String.
     *          The encoding is null is the file is not a text file.
     */
    public String detectTextEncoding(final byte[] bytes, int bytesAvailable) {
        String encoding = null;
        int bufferSize = bytes.length < bytesAvailable ? bytes.length : bytesAvailable;
        
        if ( bufferSize > MIN_LENGTH ) {

            // Count the byte values, the maximum consecutive unlikely bytes,
            // and check for whether the sample contains only likely US-ASCII byte values:
            final int fileByteLength = bufferSize > maxBytesToScan ? maxBytesToScan : bufferSize;
            
            final int[] byteValueCounts = new int[256]; // initialised to zero by default.
            final byte[] unlikelyValues = unlikelyConsecutiveValues;
            int consecutiveCount = 0;
            int maxConsecutiveCount = 0;
            boolean isASCII = true;
            for (int byteIndex = 0;  byteIndex < fileByteLength; byteIndex++ ) {
                final int byteValue = bytes[byteIndex] & 0xFF;
                isASCII = isASCII & byteValue >= TAB_CHARACTER & byteValue <= DEL_CHARACTER;
                byteValueCounts[byteValue] = byteValueCounts[byteValue] + 1;
                if (unlikelyValues[byteValue] == 1) {
                    consecutiveCount += 1;
                } else {
                	maxConsecutiveCount = Math.max(maxConsecutiveCount, consecutiveCount);
                    consecutiveCount = 0;
                }
            }
            maxConsecutiveCount = Math.max(maxConsecutiveCount, consecutiveCount);

            // Heuristic 1: if we have a file with few unlikely consecutive bytes:
            if (maxConsecutiveCount <= unlikelyConsecutiveBytesThreshold) {

                // Heuristic 2: calculate the entropy of the file:
                double fileEntropy = calculateShannonEntropy(byteValueCounts, fileByteLength);
                if ( fileEntropy <= entropyThreshold ) {

                    // Heuristic 3: does the file only contain US-ASCII byte values?
                    if (isASCII) {
                        encoding = ASCII_CHARSET;
                    } else {

                        // Try to get an encoding from the ICU4J library:
                        CharsetDetector detector = new CharsetDetector();
                        ByteArrayInputStream stream = new ByteArrayInputStream(bytes, 0, fileByteLength);
                        try {
							detector.setText(stream);
	                        CharsetMatch match = detector.detect();
	                        if ( match != null ) {

	                            // Heuristic 4: only accept if the encoding confidence is above our threshold:
	                            final int confidence = match.getConfidence();
	                            if ( confidence >= encodingConfidenceThresholdPercentage ) {
	                                encoding = match.getName();
	                            }
	                        }
                        } catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                }
            }
        }
        return encoding;
    }


    /*
     * Shannon entropy is a measure of how much information is encoded in a given
     * message.  It gives the number of bits per byte required to encode the information.
     * Messages which have lots of redundancy or only use a limited range of values require
     * fewer bits to encode than those which have little redundancy and use the full range
     * of values in a byte:
     * 
     *   H(x) = -SUM( Fi log2 Fi )
     *            i=0..255
     *  
     *  where Fi is the frequency that the i'th byte value appears in the message.
     */
    private double calculateShannonEntropy(final int[] valueCounts, final int sampleSize) {
        // Preconditions:
        if (sampleSize < 1) {
            throw new IllegalArgumentException("Must have a sample size of at least one to calculate Shannon entropy.");
        }
        if (valueCounts.length < 1) {
            throw new IllegalArgumentException("Must have at least one value counted to calculate Shannon entropy.");
        }

        // Calculate entropy:
        double entropy = 0;
        final double log2 = Math.log(2);
        final double sampleSizeD = (double) sampleSize;
        final int endIndex = valueCounts.length;
        for ( int byteIndex = 0; byteIndex < endIndex; byteIndex++ ) {
            final int byteCount = valueCounts[byteIndex];
            if ( byteCount > 0 ) {
                final double byteFrequency = (double) byteCount / sampleSizeD;
                final double byteEntropy = byteFrequency * ( Math.log(byteFrequency) / log2 );
                entropy = entropy + byteEntropy;
            }
        }
        return -entropy;
    }
    
}
