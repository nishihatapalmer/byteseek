/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public abstract class ByteClassMatcher implements SequenceMatcher {
    
    boolean negated = false;
    int numBytesInClass = 0;

    @Override
    public int length() {
        return 1; // a byte class only ever matches a single byte position.
    }
    
    public boolean isNegated() {
        return negated;
    }

    public int getNumBytesInClass() {
        return numBytesInClass;
    }

}
