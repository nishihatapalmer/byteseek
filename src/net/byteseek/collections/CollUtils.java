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

package net.byteseek.collections;

import java.util.Collection;

import net.byteseek.object.ObjectUtils;

/**
 * A static utility class containing useful methods for collections.
 * 
 * @author Matt Palmer
 *
 */
public final class CollUtils {
	
	/**
	 * Private constructor for a static utility class.
	 */
	private CollUtils() {
	}
	
	
	/**
	 * Returns true if the collection contains any of the values passed in.
	 * 
	 * @param collection The collection to check
	 * @param values The values to check to see if any are in the collection.
	 * @return true if the collection contains any of the values.
	 * @throws IllegalArgumentException if either of the collections passed in are null.
	 */
    public static <T> boolean containsAny(final Collection<T> collection, final Collection<T> values) {
    	ObjectUtils.checkNullCollection(collection, "parameter: collection");
    	ObjectUtils.checkNullCollection(values, "parameter: values");
    	for (final T value : values) {
    		if (collection.contains(value)) {
    			return true;
    		}
    	}
    	return false;
    }

}
