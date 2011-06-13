/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.Set;

/**
 * An interface for objects which implement a factory for SingleByteMatchers.
 *
 * @author matt
 */
public interface SingleByteMatcherFactory {

    SingleByteMatcher create(Set<Byte> bytes, boolean inverted);

}
