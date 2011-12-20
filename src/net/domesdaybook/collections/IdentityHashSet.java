/*
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
 */

package net.domesdaybook.collections;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * A {@link Set} implementation backed by an {@link java.util.IdentityHashMap}. 
 * Membership in the set is determined by comparing object identity (==), rather 
 * than by invoking the equals() method on the set members.
 * <p>
 * It extends {@link java.util.AbstractSet} to provide most of its implementation, 
 * only overriding the methods necessary to add to, remove from and query the 
 * underlying IdentityHashMap.
 * <p>
 * This class will be most useful when working with graphs of objects, where
 * the object identity is most important in determining set membership.
 * 
 * @see java.util.Set
 * @see java.util.IdentityHashMap
 * @see java.util.AbstractSet
 * 
 * @author Matt Palmer
 */
public final class IdentityHashSet<T> extends AbstractSet<T> implements Set<T>, Cloneable {
    
    private final Map<T, Object> map;
    
    
    /**
     * Default constructor for the IdentityHashSet
     */
    public IdentityHashSet() {
        super();
        map = new IdentityHashMap<T, Object>();
    }
    
    
    /**
     * Constructor for the IdentityHashSet taking a collection of initial objects
     * to add to the set.
     * 
     * @param initialObjects A collection of objects which will be added to the set 
     *                       on construction.
     */
    public IdentityHashSet(final Collection<T> initialObjects) {
        super();
        map = new IdentityHashMap<T, Object>();
        addAll(initialObjects);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(T object) {
        return map.put(object, object) == object;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(final Object object) {
        return map.remove((T)object) != null;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(final Object object) {
        return map.containsKey((T)object);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator iterator() {
        return map.keySet().iterator();
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return map.size();
    }
    
}
