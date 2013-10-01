/*
 * Copyright Matt Palmer 2013, All rights reserved.
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

package net.byteseek.object;

import java.util.Collection;

public final class ObjectUtils {

	private static final String OBJECT_CANNOT_BE_NULL               = "The object cannot be null";
	private static final String COLLECTION_CANNOT_BE_NULL           = "The collection cannot be null";
	private static final String COLLECTION_CANNOT_BE_EMPTY          = "The collection cannot be empty";
	private static final String COLLECTION_ELEMENT_CANNOT_BE_NULL   = "Elements of the collection cannot be null";
	private static final String ARRAY_CANNOT_BE_NULL                = "The array cannot be null";
	private static final String ARRAY_CANNOT_BE_EMPTY		        = "The array cannot be empty";
	private static final String STRING_CANNOT_BE_NULL               = "The string cannot be null";

	public static void checkNullObject(final Object object) {
		if (object == null) {
			throw new IllegalArgumentException(OBJECT_CANNOT_BE_NULL);
		}
	}

	public static void checkNullObject(final Object object, final String description) {
		if (object == null) {
			throw new IllegalArgumentException(OBJECT_CANNOT_BE_NULL + ' ' + description);
		}
	}
	
	public static <T> void checkNullCollection(final Collection<T> collection) {
		if (collection == null) {
    		throw new IllegalArgumentException(COLLECTION_CANNOT_BE_NULL);
    	}
	}

	
	public static <T> void checkNullCollection(final Collection<T> collection, final String description) {
		if (collection == null) {
    		throw new IllegalArgumentException(COLLECTION_CANNOT_BE_NULL + ' ' + description);
    	}
	}
 
	
	public static <T> void checkNullOrEmptyCollection(final Collection<T> collection) {
		checkNullCollection(collection);
		if (collection.isEmpty()) {
			throw new IllegalArgumentException(COLLECTION_CANNOT_BE_EMPTY);
		}
	}
	

	public static <T> void checkNullOrEmptyCollection(final Collection<T> collection, final String description) {
		checkNullCollection(collection, description);
		if (collection.isEmpty()) {
			throw new IllegalArgumentException(COLLECTION_CANNOT_BE_EMPTY + ' ' + description);
		}
	}
	
	
	public static <T> void checkNullCollectionElements(final Collection<T> collection) {
		checkNullCollection(collection);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}
	
	public static <T> void checkNullCollectionElements(final Collection<T> collection, final String description) {
		checkNullCollection(collection, description);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL + ' ' + description);
		}
	}

	
	public static void checkNullByteArray(final byte[] bytes) {
		if (bytes == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL);
    	}
	}    	
	

	public static void checkNullByteArray(final byte[] bytes, final String description) {
		if (bytes == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL + ' ' + description);
    	}
	}  

	
	public static void checkNullOrEmptyByteArray(final byte[] bytes) {
		checkNullByteArray(bytes);
		if (bytes.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY);
    	}
	}    	

	
	public static void checkNullOrEmptyByteArray(final byte[] bytes, final String description) {
		checkNullByteArray(bytes);
		if (bytes.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY + ' ' + description);
    	}
	}    
	

	public static void checkNullString(final String string) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL);
    	}
	} 
	
	public static void checkNullString(final String string, final String description) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL + ' ' + description);
    	}
	} 
	
}
