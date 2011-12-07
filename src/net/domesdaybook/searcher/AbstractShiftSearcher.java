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
 * 
 */
package net.domesdaybook.searcher;

/**
 *
 * @author Matt Palmer
 */
public abstract class AbstractShiftSearcher extends AbstractSearcher {
    
    // Volatile arrays are usually a bad idea, as volatile applies to the array
    // reference, not to the contents of the array.  However, we will never change
    // the array contents once it is initialised, so this is safe.
    @SuppressWarnings("VolatileArrayField")
    protected volatile int[] shiftForwardFunction;
    @SuppressWarnings("VolatileArrayField")
    protected volatile int[] shiftBackwardFunction;
    
    
    protected abstract int[] createForwardShifts();
    
    protected abstract int[] createBackwardShifts();
    
    
    /**
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once, but this doesn't really matter.
     * 
     * @return An array of integers, giving the safe shift
     * for a given byte when searching forwards.
     */
    protected int[] getForwardShifts() {
        int[] result = shiftForwardFunction;
        if (result == null) {
            shiftForwardFunction = result = createForwardShifts();
        }
        return result;
    }


    /**
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once, but this doesn't really matter.
     * 
     * @return An array of integers, giving the safe shift
     * for a given byte when searching backwards.
     */
    protected int[] getBackwardShifts() {
        int[] result = shiftBackwardFunction;
        if (result == null) {
            shiftBackwardFunction = result = createBackwardShifts();
        }
        return result;
    }
    
    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareForwards() {
        getForwardShifts();
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareBackwards() {
        getBackwardShifts();
    }    
    
}
