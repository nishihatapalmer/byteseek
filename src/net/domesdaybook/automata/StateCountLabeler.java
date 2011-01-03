/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata;

/**
 *
 * @author Matt Palmer
 */
public class StateCountLabeler implements StateLabeler {

    private int count;
    private String name;

    public StateCountLabeler() {
       this.count = 0;
       this.name = "";
    }

    public StateCountLabeler(final String name) {
        this(0, name);
    }

    public StateCountLabeler(final int count) {
        this(count, "");
    }

    public StateCountLabeler(final int count, final String name) {
        this.count = count;
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final int getCount() {
        return count;
    }

    public final void setCount(final int count) {
        this.count = count;
    }

    @Override
    public final void label(State state) {
        final String label = String.format("%s%d", name, count++);
        state.setLabel(label);
    }

}
