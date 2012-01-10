/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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


package net.domesdaybook.matcher.automata;

import java.io.IOException;
import net.domesdaybook.automata.State;
import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 *
 * @author Matt Palmer
 */
public class DfaMatcher implements Matcher {

    private final State firstState;


    /**
     * 
     * @param firstState
     */
    public DfaMatcher(final State firstState) {
        this.firstState = firstState;
    }
    
    
    /**
     * 
     * @param reader
     * @param matchPosition
     * @return
     * @throws IOException
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition) 
        throws IOException {
        // Setup 
        long currentPosition = matchPosition;    
        Window window = reader.getWindow(currentPosition);
        State state = firstState;
        //While we have a window on the data to match in:
        while (window != null) {
            final byte[] bytes = window.getArray();            
            final int windowLength = window.length();
            final int windowStart = reader.getWindowOffset(currentPosition);            
            int windowPos = windowStart;
            
            // While we have states to match:
            while (state != null && windowPos < windowLength) {
                
                // See if the active states is final (a match).
                if (state.isFinal()) {
                    return true;
                }
                
                // No match was found, find the next state to follow:
                final byte currentByte = bytes[windowPos++];
                state = state.getNextState(currentByte);
            }
            currentPosition += windowLength - windowStart;
            window = reader.getWindow(currentPosition);
        }
        return false;
    }

    
    /**
     * 
     * @param bytes
     * @param matchPosition
     * @return 
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        // Setup
        final int length = bytes.length;
        if (matchPosition >= 0 && matchPosition < length) {
            int currentPosition = matchPosition;    
            State currentState = firstState;

            // While there is a state to process:
            while (currentState != null && currentPosition < length) {

                // See if the next state is final (a match).
                if (currentState.isFinal()) {
                    return true;
                }

                // No match was found, find the next state to follow:
                final byte currentByte = bytes[currentPosition++];
                currentState = currentState.getNextState(currentByte);
            }
        }
        return false;
    }

}
