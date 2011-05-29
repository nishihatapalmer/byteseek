/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
