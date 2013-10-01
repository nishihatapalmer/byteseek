/*
 * Copyright Matt Palmer 2013. All rights reserved.
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

package net.byteseek.object.factory;


/**
 * This class creates objects using double-check
 * lazy initialisation, with volatile references.  This means that
 * if two threads attempt to get the object at the same time before it has
 * been fully initialised, the object will only be created once.
 * 
 * @param <T> The type of object to instantiate lazily.
 * 
 * @author Matt Palmer
 */
public final class DoubleCheckImmutableLazyObject<T> implements LazyObject<T> {

    private final ObjectFactory<T> factory;
    private T object; // since the object is immutable, this field does not have to be volatile.

    /**
     * Constructs a DoubleCheckLazyObject with an object factory to create the 
     * object lazily.
     * 
     * @param factory A factory which can create an instance of type T.
     */
    public DoubleCheckImmutableLazyObject(ObjectFactory<T> factory) {
    	this.factory = factory;
    }
    
   
    /**
     * Uses Double-Check lazy initialisation.  Only one instance will be created, no matter
     * how many threads call this method.
     * 
     * @return An object of type T.
     */
    @Override
    public final T get() {
        if (object == null) {
        	synchronized(this) {
        		if (object == null) {
        			object = factory.create();
        		}
        	}
        }
        return object;
    }

}