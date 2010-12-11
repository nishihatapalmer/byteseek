/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher;

import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public interface Matcher {

    /* matches an entire sequence of bytes or not.
     * @returns whether the byte matcher matched a sequence of bytes or not.
    */
    public boolean matches(final ByteReader reader, final long matchFrom);
}
