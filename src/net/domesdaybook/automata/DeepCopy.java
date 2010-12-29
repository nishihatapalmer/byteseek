/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata;

import java.util.Map;

/**
 *
 * @author matt
 */
public interface DeepCopy {

    DeepCopy deepCopy();

    DeepCopy deepCopy(Map<DeepCopy,DeepCopy> oldToNewObjects);
}
