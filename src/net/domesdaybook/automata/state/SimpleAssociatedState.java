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
 * 
 */

package net.domesdaybook.automata.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.AssociatedState;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.object.copy.DeepCopy;

/**
 * An implementation of the {@link AssociatedState} interface, which also 
 * inherits most of its implementation from the {@link SimpleState} class.
 * Only managing the association with other objects is added.
 * <p>
 * This class is intentionally not final, to allow other States to inherit from it.
 * 
 * @param <T> The type of object this State can be associated with.
 * @see SimpleState
 * @see net.domesdaybook.automata.State
 * @see net.domesdaybook.automata.AssociatedState
 * 
 * @author Matt Palmer
 */
public class SimpleAssociatedState<T> extends SimpleState implements AssociatedState {
    
    private List<T> associations;
    
    
    /**
     * Default constructor for the SimpleAssociatedState as a non-final State.
     */
    public SimpleAssociatedState() {
        this(State.NON_FINAL);
    }    
    
    
    /**
     * Constructor for the SimpleAssociatedState taking a parameter controlling
     * whether it is final or not.
     * 
     * @param isFinal Whether the state is final or not.
     */
    public SimpleAssociatedState(final boolean isFinal) {
        super(isFinal);
        // It is likely that most associated states will only have zero or one 
        // object associated with them, so create an arraylist with a very small
        // initial capacity.
        this.associations = new ArrayList<T>(1); 
    }

    
    /**
     * Copy constructor for the SimpleAssociatedState from another AssociatedState
     * 
     * @param other The AssociatedState to construct ourselves from.
     */
    public SimpleAssociatedState(final AssociatedState<T> other) {
        super(other);
        this.associations = new ArrayList<T>(other.getAssociations());
    }
 

    
    /**
     * This is a convenience method, providing the initial map to:
     * <CODE>deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)</CODE>
     *
     * @return SimpleAssociatedState a deep copy of this object.
     * @see #deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)
     */
    @Override
    public SimpleAssociatedState<T> deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new IdentityHashMap<DeepCopy,DeepCopy>();
        return deepCopy(oldToNewObjects);
    }
    

    /**
     * This method is inherited from the {@link DeepCopy} interface,
     * and is redeclared here with a return type of SimpleAssociatedState 
     * (rather than DeepCopy), to make using the method easier.
     *
     * @param oldToNewObjects A map of the original objects to their new deep copies.
     * @return SimpleAssociatedState A deep copy of this SimpleAssociatedState 
     *         and any Transitions and States reachable from this State.
     */
    @Override
    public SimpleAssociatedState<T> deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
        SimpleAssociatedState<T> stateCopy = (SimpleAssociatedState<T>) oldToNewObjects.get(this);
        if (stateCopy == null) {
            stateCopy = new SimpleAssociatedState<T>(isFinal());
            oldToNewObjects.put(this, stateCopy);
            for (Transition transition : getTransitions()) {
                final Transition transitionCopy = transition.deepCopy(oldToNewObjects);
                stateCopy.addTransition(transitionCopy);
            }
            stateCopy.setTransitionStrategy(getTransitionStrategy().deepCopy(oldToNewObjects));
            stateCopy.setAssociations(associations); // does not deep copy associations.
        }
        return stateCopy;
    }

    
    /**
     * Returns the list of objects associated with this state.
     * This list is the actual list used by the SimpleAssociatedState, not a copy.
     * 
     * @return The list of objects associated with this state.
     */
    @Override
    public Collection<T> getAssociations() {
        return associations;
    }

    
    /**
     * Adds an object to the list of associated objects held by this state.
     * 
     * @param object The object to add to this State.
     */
    @Override
    public void addObject(final Object object) {
        associations.add((T) object);
    }

    
    /**
     * Removes an object associated with this State.
     * 
     * @param object The object to remove from this State.
     * @return boolean Whether the object was associated with the State.
     */
    @Override
    public boolean removeObject(final Object object) {
        return associations.remove((T) object);
    }

    
    /**
     * Sets a new list of associations, copying them from the collection
     * passed in.
     * 
     * @param associations A collection of associations to associate with this State.
     */
    @Override
    public void setAssociations(final Collection associations) {
        this.associations = new ArrayList<T>(associations);
    }
    
   
}

