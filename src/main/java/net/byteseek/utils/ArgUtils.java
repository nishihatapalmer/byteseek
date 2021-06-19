/*
 * Copyright Matt Palmer 2013-19, All rights reserved.
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

package net.byteseek.utils;

import java.util.Collection;

/**
 * A static utility class for testing arguments passed in to methods, which throw
 * an appropriate exception if the conditions aren't met.  Normally this is an IllegalArgumentException
 * although some methods may throw an IndexOutOfBoundsException or other runtime exception as appropriate.
 * <p>
 * Byteseek tries to ensure that classes are correct on construction, or when used, to ensure that
 * classes obey their contracts and to fail early with an appropriate message if used incorrectly.
 */
public final class ArgUtils {

	private static final String OBJECT_CANNOT_BE_NULL               = "The object cannot be null";
	private static final String COLLECTION_CANNOT_BE_NULL           = "The collection cannot be null";
	private static final String COLLECTION_MUST_BE_RIGHT_SIZE       = "The collection of size %d must be exactly %d in size";
	private static final String COLLECTION_CANNOT_BE_EMPTY          = "The collection cannot be empty";
	private static final String COLLECTION_ELEMENT_CANNOT_BE_NULL   = "Elements cannot be null";
	private static final String ARRAY_CANNOT_BE_NULL                = "The array cannot be null";
	private static final String ARRAY_CANNOT_BE_EMPTY		        = "The array cannot be empty";
	private static final String STRING_CANNOT_BE_NULL               = "The string cannot be null";
	private static final String STRING_CANNOT_BE_EMPTY              = "The string cannot be empty";
    private static final String END_PAST_LENGTH_ERROR               = "The end %d is past the end, length = %d";
    private static final String START_LESS_THAN_ZERO_ERROR          = "The start %d is less than zero.";
    private static final String START_PAST_END_INDEX_ERROR          = "The start index %d is equal to or greater than the end index %d";
    private static final String START_PAST_LENGTH_ERROR             = "Start position %d is past the end, length = %d.";
    private static final String POSITIVE_INTEGER				    = "The number %d should be a positive integer.";
	private static final String NUMBER_OUT_OF_RANGE                 = "The number %d is outside the range %d to %d : %s";

    /**
     * Checks that the object passed in is not null.
     * @param object the object reference to test.
     * @throws IllegalArgumentException if the object reference passed in is null.
     */
	public static void checkNullObject(final Object object) {
		if (object == null) {
			throw new IllegalArgumentException(OBJECT_CANNOT_BE_NULL);
		}
	}

    /**
     * Checks that the object passed in is not null.
     * @param object the object reference to test.
     * @param description a description which will be added to the exception message.
     * @throws IllegalArgumentException if the object reference passed in is null.
     */
	public static void checkNullObject(final Object object, final String description) {
		if (object == null) {
			throw new IllegalArgumentException(OBJECT_CANNOT_BE_NULL + ' ' + description);
		}
	}

    /**
     * Checks that a collection of objects of type T is not null.
     * @param collection A reference to a collection of objects.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the object reference passed in is null.
     */
	public static <T> void checkNullCollection(final Collection<T> collection) {
		if (collection == null) {
    		throw new IllegalArgumentException(COLLECTION_CANNOT_BE_NULL);
    	}
	}

    /**
     * Checks that a collection of objects of type T is not null.
     * @param collection A reference to a collection of objects.
     * @param description a description which will be added to the exception message.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the object reference passed in is null.
     */
	public static <T> void checkNullCollection(final Collection<T> collection, final String description) {
		if (collection == null) {
    		throw new IllegalArgumentException(COLLECTION_CANNOT_BE_NULL + ' ' + description);
    	}
	}

    /**
     * Checks that a collection of objects is not null or empty.
     * @param collection A reference to a collection of objects.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the object reference passed in is null or empty.
     */
	public static <T> void checkNullOrEmptyCollection(final Collection<T> collection) {
		checkNullCollection(collection);
		if (collection.isEmpty()) {
			throw new IllegalArgumentException(COLLECTION_CANNOT_BE_EMPTY);
		}
	}

    /**
     * Checks that a collection of objects is not null and has the correct size.
     * @param collection A reference to a collection of objects.
     * @param size The size that the collection must have.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the collection reference passed in is null or not the correct size.
     */
	public static <T> void checkCollectionSize(final Collection<T> collection, final int size) {
		checkNullCollection(collection);
		if (collection.size() != size) {
			throw new IllegalArgumentException(String.format(COLLECTION_MUST_BE_RIGHT_SIZE, collection.size(), size));
		}
	}

    /**
     * Checks that a collection of objects is not null and has the correct size, and that none of the
     * collection elements are null.
     * @param collection A reference to a collection of objects.
     * @param size The size that the collection must have.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the collection reference passed in is null or not the correct size,
     *                                  or any of the objects in the collection are null.
     */
	public static <T> void checkCollectionSizeNoNullElements(final Collection<T> collection, final int size) {
		checkCollectionSize(collection, size);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}

    /**
     * Checks that a collection of objects is not null or empty.
     * @param collection A reference to a collection of objects.
     * @param description a description which will be added to the exception message.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the object reference passed in is null or empty.
     */
	public static <T> void checkNullOrEmptyCollection(final Collection<T> collection, final String description) {
		checkNullCollection(collection, description);
		if (collection.isEmpty()) {
			throw new IllegalArgumentException(COLLECTION_CANNOT_BE_EMPTY + ' ' + description);
		}
	}

    /**
     * Checks that a collection passed in is not null and none of the elements of the collection are null.
     * @param collection A collection of objects of type T.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the collection passed in is null or any of its elements are null.
     */
	public static <T> void checkNullCollectionElements(final Collection<T> collection) {
		checkNullCollection(collection);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}

    /**
     * Checks that a collection passed in is not null and none of the elements of the collection are null.
     * @param collection A collection of objects of type T.
     * @param description a description which will be added to the exception message.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the collection passed in is null or any of its elements are null.
     */
	public static <T> void checkNullCollectionElements(final Collection<T> collection, final String description) {
		checkNullCollection(collection, description);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL + ' ' + description);
		}
	}

    /**
     * Checks that a collection passed in is not null or empty, and none of the elements of the collection are null.
     * @param collection A collection of objects of type T.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the collection passed in is null or empty,
     *         or any of its elements are null.
     */
	public static <T> void checkNullOrEmptyCollectionNoNullElements(final Collection<T> collection) {
		checkNullOrEmptyCollection(collection);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}

    /**
     * Checks that a collection passed in is not null or empty, and none of the elements of the collection are null.
     * @param collection A collection of objects of type T.
     * @param description a description which will be added to the exception message.
     * @param <T> The type of the objects in the collection.
     * @throws IllegalArgumentException if the collection passed in is null or empty,
     *         or any of its elements are null.
     */
	public static <T> void checkNullOrEmptyCollectionNoNullElements(final Collection<T> collection, final String description) {
		checkNullOrEmptyCollection(collection, description);
		for (T element : collection) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL + ' ' + description);
		}
	}

    /**
     * Checks that a byte array passed in is not null.
     * @param bytes The byte array to check.
     * @throws IllegalArgumentException if the array passed in is null.
     */
	public static void checkNullByteArray(final byte[] bytes) {
		if (bytes == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL);
    	}
	}

    /**
     * Checks that a byte array passed in is not null.
     * @param bytes The byte array to check.
     * @param description A description which will be added to the exception message.
     * @throws IllegalArgumentException if the array passed in is null.
     */
	public static void checkNullByteArray(final byte[] bytes, final String description) {
		if (bytes == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL + ' ' + description);
    	}
	}

    /**
     * Checks that an int array passed in is not null.
     * @param integers The int array to check.
     * @param description A description which will be added to the exception message.
     * @throws IllegalArgumentException if the array passed in is null.
     */
	public static void checkNullIntArray(final int[] integers, final String description) {
		if (integers == null) {
			throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL + ' ' + description);
		}
	}

    /**
     * Checks that a byte array passed in is not null or empty.
     * @param bytes The byte array to check.
     * @throws IllegalArgumentException if the array passed in is null or empty.
     */
	public static void checkNullOrEmptyByteArray(final byte[] bytes) {
		checkNullByteArray(bytes);
		if (bytes.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY);
    	}
	}

    /**
     * Checks that a byte array passed in is not null or empty.
     * @param bytes The byte array to check.
     * @param description A description which will be added to the exception message.
     * @throws IllegalArgumentException if the array passed in is null or empty.
     */
	public static void checkNullOrEmptyByteArray(final byte[] bytes, final String description) {
		checkNullByteArray(bytes);
		if (bytes.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY + ' ' + description);
    	}
	}

    /**
     * Checks that an array of object type T is not null.
     * @param array The array to test.
     * @param <T> The type of the object in the array.
     * @throws IllegalArgumentException if the array passed in is null.
     */
	public static <T> void checkNullArray(final T[] array) {
		if (array == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL);
    	}
	}

    /**
     * Checks that an array of object type T is not null.
     * @param array The array to test.
     * @param description A description which will be added to the exception message.
     * @param <T> The type of the object in the array.
     * @throws IllegalArgumentException if the array passed in is null.
     */
	public static <T> void checkNullArray(final T[] array, final String description) {
		if (array == null) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_NULL + ' ' + description);
    	}
	}

    /**
     * Checks that an array of object type T is not null or empty.
     * @param array The array to test.
     * @param <T> The type of the object in the array.
     * @throws IllegalArgumentException if the array passed in is null or empty.
     */
	public static <T> void checkNullOrEmptyArray(final T[] array) {
		checkNullArray(array);
		if (array.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY);
    	}
	}

    /**
     * Checks that an array of object type T is not null or empty.
     * @param array The array to test.
     * @param description A description which will be added to the exception message.
     * @param <T> The type of the object in the array.
     * @throws IllegalArgumentException if the array passed in is null or empty.
     */
	public static <T> void checkNullOrEmptyArray(final T[] array, final String description) {
		checkNullArray(array, description);
		if (array.length == 0) {
    		throw new IllegalArgumentException(ARRAY_CANNOT_BE_EMPTY + ' ' + description);
    	}
	}

    /**
     * Checks that an array of object type T is not null or empty and has no null elements.
     * @param array The array to test.
     * @param description A description which will be added to the exception message.
     * @param <T> The type of the object in the array.
     * @throws IllegalArgumentException if the array passed in is null or empty or it has null elements.
     */
	public static <T> void checkNullOrEmptyArrayNoNullElements(final T[] array, final String description) {
		checkNullOrEmptyArray(array, description);
		for (T element : array) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL + ' ' + description);
		}
	}

    /**
     * Checks that an array of object type T is not null or empty and has no null elements.
     * @param array The array to test.
     * @param <T> The type of the object in the array.
     * @throws IllegalArgumentException if the array passed in is null or empty or it has null elements.
     */
	public static <T> void checkNullOrEmptyArrayNoNullElements(final T[] array) {
		checkNullOrEmptyArray(array);
		for (T element : array) {
			checkNullObject(element, COLLECTION_ELEMENT_CANNOT_BE_NULL);
		}
	}

    /**
     * Checks that a number lies in the range start to end inclusive.
     * @param number The number to check.
     * @param start  The start of a range.
     * @param end    The end of a range.
     * @param description A description added to the exception message.
     * @throws IllegalArgumentException if the number does not lie in the range from start to end inclusive.
     */
	public static void checkRangeInclusive(final int number, final int start, final int end, final String description) {
		if (number < start || number > end) {
			throw new IllegalArgumentException(String.format(NUMBER_OUT_OF_RANGE, number, start, end, description));
		}
	}

    /**
     * Checks that a string is not null.
     * @param string The string to check.
     * @throws IllegalArgumentException if the string is null
     */
	public static void checkNullString(final String string) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL);
    	}
	}

    /**
     * Checks that a string is not null.
     * @param string The string to check.
     * @param description A description added to the exception message.
     * @throws IllegalArgumentException if the string is null
     */
	public static void checkNullString(final String string, final String description) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL + ' ' + description);
    	}
	}

    /**
     * Checks that a string is not null or empty.
     * @param string The string to check.
     * @throws IllegalArgumentException if the string is null or empty.
     */
	public static void checkNullOrEmptyString(final String string) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL);
    	}
		if (string.isEmpty()) {
			throw new IllegalArgumentException(STRING_CANNOT_BE_EMPTY);
		}
	}

    /**
     * Checks that a string is not null or empty.
     * @param string The string to check.
     * @param description A description added to the exception message.
     * @throws IllegalArgumentException if the string is null or empty.
     */
	public static void checkNullOrEmptyString(final String string, final String description) {
		if (string == null) {
    		throw new IllegalArgumentException(STRING_CANNOT_BE_NULL + ' ' + description);
    	}
		if (string.isEmpty()) {
			throw new IllegalArgumentException(STRING_CANNOT_BE_EMPTY + ' ' + description);
		}
	}

    /**
     * Checks whether a position is within the range 0 to length, not inclusive.
     * @param length The length of something zero indexed.
     * @param position The position to check
     * @throws IndexOutOfBoundsException if the position is not within the zero indexed range.
     */
	public static void checkIndexOutOfBounds(final int length, final int position) {
        if (position < 0) {
        	throw new IndexOutOfBoundsException(String.format(START_LESS_THAN_ZERO_ERROR, position));
        }
        if (position >= length) {
            throw new IndexOutOfBoundsException(String.format(START_PAST_LENGTH_ERROR, position, length));
        }
	}

    /**
     * Checks whether a range is within a length, zero indexed, and that the range
     * start is less than the range end.  The end index is exclusive (so can be equal to the length).
     *
     * @param length  The length of something zero-indexed.
     * @param startIndex The start position of a range within the zero indexed thing, inclusive.
     * @param endIndex   The end position of a range within the zero indexed thing, exclusive.
     * @throws IndexOutOfBoundsException if the range of positions do not lie within the length of a zero-
     *         indexed thing, or that the range start is greater than the range end.
     */
	public static void checkIndexOutOfBounds(final long length, final long startIndex, final long endIndex) {
        if (startIndex < 0) {
        	throw new IndexOutOfBoundsException(String.format(START_LESS_THAN_ZERO_ERROR, startIndex));
        }
		if (startIndex >= endIndex) {
            throw new IndexOutOfBoundsException(String.format(START_PAST_END_INDEX_ERROR, startIndex, endIndex));
        }
        if (startIndex >= length) {
            throw new IndexOutOfBoundsException(String.format(START_PAST_LENGTH_ERROR, startIndex, length));
        }
        if (endIndex > length) {
            throw new IndexOutOfBoundsException(String.format(END_PAST_LENGTH_ERROR, endIndex, length));
        }
	}

    /**
     * Checks that an integer is one or greater.
     * @param number The number to check.
     * @throws IllegalArgumentException if the number is less than one.
     */
	public static void checkPositiveInteger(final int number) {
		if (number < 1) {
			throw new IllegalArgumentException("The number " + number + " should be a positive integer.");
		}
	}

    /**
     * Checks that an integer is not negative
     * @param number The number to check.
     * @throws IllegalArgumentException if the number is less than zero.
     */
	public static void checkNotNegative(final long number)  {
		if (number < 0) {
			throw new IllegalArgumentException("The number " + number + " must not be negative.");
		}
	}

	/**
	 * Throws an IllegalArgumentException if the number passed in is less than 1.
	 *
	 * @param number The number to check.
	 * @param description A description of the number parameter which will appear in the exception.
     * @throws IllegalArgumentException if the number passed in is less than 1.
	 */
	public static void checkPositiveInteger(final int number, final String description) {
		if (number < 1) {
			throw new IllegalArgumentException(String.format(POSITIVE_INTEGER + ' ' + description, number));		}
	}

    /**
     * Checks that a long is one or greater.
     * @param value The value to check.
     * @throws IllegalArgumentException if the value is less than 1.
     */
	public static void checkGreaterThanZero(final long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("The value must be greater than zero." + value);
        }
    }

    /**
     * Checks that a long is one or greater.
     * @param value The value to check.
     * @param description A description of the number parameter which will appear in the exception.
     * @throws IllegalArgumentException if the value is less than 1.
     */
    public static void checkGreaterThanZero(final long value, final String description) {
        if (value <= 0) {
            throw new IllegalArgumentException("The value must be greater than zero. " + value + ' ' + description);
        }
    }

	/**
	 * Checks a value is at least the min value.
	 * @param value The value to check
	 * @param minValue The minimum the value can be.
	 * @param description A description of the parameter which will appear in the exception.
	 */
	public static void checkAtLeast(final long value, final long minValue, final String description) {
    	if (value < minValue) {
    		throw new IllegalArgumentException("The value " + value + " must be at least " + minValue + ' ' + description);
		}
	}

	/**
	 * Checks a value is at least the min value.
	 * @param value The value to check
	 * @param lessThan what the value must be less than.
	 * @param description A description of the parameter which will appear in the exception.
	 */
	public static void checkLessThan(final long value, final long lessThan, final String description) {
		if (value >= lessThan) {
			throw new IllegalArgumentException("The value " + value + " must be less than " + lessThan + ' ' + description);
		}
	}

	/**
	 * Checks a value is at least the min value.
	 * @param value The value to check
	 * @param lessThanOrEqual what the value must be less than.
	 * @param description A description of the parameter which will appear in the exception.
	 */
	public static void checkLessThanOrEqual(final long value, final long lessThanOrEqual, final String description) {
		if (value > lessThanOrEqual) {
			throw new IllegalArgumentException("The value " + value + " must be less than or equal to " + lessThanOrEqual + ' ' + description);
		}
	}

    /**
     * Checks that a start and end range lies within a byte array, inclusive, and that the array is not null.
     * @param array The array to check if null.
     * @param startIndex A start position within the array.
     * @param endIndex An end of the range within the array, inclusive (end pos can be same as length).
     * @throws IllegalArgumentException if the range does not lie within the array, inclusive, or that the
     *         array is null.
     */
	public static void checkBounds(final byte[] array, final int startIndex, final int endIndex) {
        checkNullByteArray(array);
        if (startIndex < 0 || startIndex >= endIndex || endIndex > array.length) {
            throw new IllegalArgumentException("The start index must be between 0 inclusive and the array length exclusive" +
                                               ",end index must be greater than the start index and not greater than the length. " +
                                               "Array length is " + array.length + " start index is " + startIndex + " end index is " + endIndex);
        }
    }

    /**
     * Checks that a range of values lies within the range 0 to 255 (valid values for a byte).
     * The start and end of the range can be greater or less than each other.
     * @param from The start of the range of byte values.
     * @param to The end of the range of byte values.
     * @throws IllegalArgumentException if the from or to is less than zero, or the from or to is
     *         greater than 255.
     */
	public static void checkIntToByteRange(final int from, final int to) {
        if (from < 0 || from > 255 || to < 0 || to > 255) {
            final String message = "The from and to values must be in the range 0 to 255.  Values provided were %d and %d";
            throw new IllegalArgumentException(String.format(message, from, to));
        }
    }

	/**
	 * Checks that a char is an extended ASCII byte in the range 0 - 255.
	 * @param aChar The char to check.
	 * @param description A description to be attached to any exception which is thrown.
	 * @throws IllegalArgumentException if the char is not an extended ASCII byte.
	 */
	public static void checkExtendedAsciiByte(final char aChar, final String description) {
		if (aChar > 255) {
			throw new IllegalArgumentException("The char " + aChar + " is not an ASCII byte 0 - 255. " + description);
		}
	}

}
