/*
 * 
 * Copyright Matt Palmer 2011, All rights reserved.
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
 * 
 * 
 */
package net.domesdaybook.collections;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Matt Palmer
 */
public class LastItemCollection<T> extends AbstractCollection<T> {

    private T lastItem;
    
    public void LastItemCollection() {
    }
    
    
    public void LastItemCollectdion(Collection<T> others) {
        addAll(others);
    }
    
    
    @Override
    public Iterator<T> iterator() {
        return new LastItemIterator();
    }
    

    @Override
    public int size() {
        return lastItem == null? 0 : 1;
    }
    
    
    @Override
    public boolean add(T item) {
        lastItem = item;
        return true;
    }
        
        
    @Override
    public void clear() {
        lastItem = null;
    }  
    
    
    @Override
    public boolean contains(final Object object) {
        return lastItem == null? object == null : lastItem.equals(object);
    }
    
    
    public T getItem() {
        return lastItem;
    }
    
    
    private class LastItemIterator implements Iterator<T> {

        boolean returnedItem;
        
        @Override
        public boolean hasNext() {
            return lastItem != null && !returnedItem;
        }

        
        @Override
        public T next() {
            if (lastItem == null || returnedItem) {
                throw new NoSuchElementException();
            }
            returnedItem = true;
            return lastItem;
        }

        
        @Override
        public void remove() {
            lastItem = null;
        }
        
    }
    
}
