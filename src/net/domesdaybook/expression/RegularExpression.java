/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.expression;

import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
public class RegularExpression implements Expression {

    @Override
    public MatchResults matchForwards(Bytes reader, long fromPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MatchResults matchBackwards(Bytes reader, long fromPosition ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
