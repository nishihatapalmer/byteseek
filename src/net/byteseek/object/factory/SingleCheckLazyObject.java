/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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
 * This class creates objects using single-check
 * lazy initialisation, with volatile references.  This means that
 * if two threads attempt to get the object at the same time before it has
 * been fully initialised, it is possible for the object to be created 
 * more than once.  
 * 
 * @param <T> The type of object to instantiate lazily.
 * 
 * @author Matt Palmer
 */
public final class SingleCheckLazyObject<T> implements LazyObject<T> {

    private final ObjectFactory<T> factory;
    private volatile T object;

    /**
     * Constructs a SingleCheckLazyObject with an object factory to create the 
     * object lazily.
     * 
     * @param factory A factory which can create an instance of type T.
     */
    public SingleCheckLazyObject(ObjectFactory<T> factory) {
    	this.factory = factory;
    }

    
    /**
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once.
     * 
     * @return An object of type T.
     */
    @Override
    public final T get() {
        T result = object;
        if (result == null) {
            object = result = factory.create();
        }
        return result;
    }

}
