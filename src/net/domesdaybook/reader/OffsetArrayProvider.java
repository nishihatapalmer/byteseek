/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 * An interface for classes which can provide OffsetArray objects for a given 
 * position in an underlying byte source.
 * <p/>
 * The rationale behind this interface is that the performance of reading bytes
 * directly from an array is far higher than invoking a method call of readByte
 * (from the ByteReader interface) for each byte you read.  In intensive matching
 * or searching scenarios, profiling has shown that these method calls are very
 * performance sensitive, as they are invoked millions of times, so small differences
 * add up quickly.
 * <p/>
 * However, reading bytes from an array also carries a major disadvantage when matching
 * or searching byte sources where you cannot load the entire byte source into a 
 * byte array (e.g. large files).  In these cases, you normally read the bytes into 
 * a byte buffer.  The problem occurs when you want to match or search across the
 * boundaries of these byte buffers.
 * <p/>
 * The OffsetArrayProvider is designed to solve this issue.  Given a position, it returns
 * a {@link OffsetArray} object, which is a simple wrapper object that contains a byte
 * array, and an offset into that array which contains the byte the position indexes.
 * <p/>
 * Matching and searching objects can then query the underlying byte source for a byte
 * array (and an offset into it), and search efficiently across the byte array, until
 * the boundary of the array is reached.  
 * <p/>
 * To bridge the gap between a byte array and
 * the next logical array, a {@link BridgingByteArrayReader} is used, which although
 * less efficient than reading from the byte arrays themselves, means that a separate
 * copy of the bytes that bridge the gap does not have to be made. The amount of data
 * required to bridge matching or searching will typically be bounded by the maximum
 * length of the pattern being looked for.  As long as this is small relative to the
 * size of the byte array buffers, a performance advantage might be gained. 
 * <p/>
 * This needs to be profiled to determine if this is actually the case.
 * 
 * 
 * @author matt
 */
public interface OffsetArrayProvider {
    
    OffsetArray getByteArray(final long position);
    
    long length();
}
