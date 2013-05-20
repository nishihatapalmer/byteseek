/*
 * Copyright Matt Palmer 2012, All rights reserved.
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
package net.byteseek.util.collections;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator over a list which prevents removal of items from the list.
 * <p>
 * Calling {@link #remove()} on this iterator will throw an {@link UnsupportedOperationException}.
 * 
 * @author Matt Palmer
 */
public class UnmodifiableListIterator<T> implements Iterator<T> {

	private final List<T> list;
	private int           index;

	/**
	 * Constructs an UnmodifiableListIterator.
	 * 
	 * @param list The list to iterate over.
	 */
	public UnmodifiableListIterator(final List<T> list) {
		this.list = list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return index < list.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T next() {
		if (hasNext()) {
			return list.get(index++);
		}
		throw new NoSuchElementException(String.format(
				"Index position %d is greater than or equal to the list size %d", index,
				list.size()));
	}

	/**
	 * Removal is not supported by the UnmodifiableListIterator.  Calling this method will
	 * always throw a {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException if the method is called.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"Removal not supported by the UnmodifiableListIterator.");
	}

}
