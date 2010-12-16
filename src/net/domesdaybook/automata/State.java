/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.automata;

/**
 *
 * @author matt
 */
public interface State {

    public String getId();

    public void addTransition(final Transition transition);

    public boolean isFinal();

}

