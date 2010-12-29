/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression;

import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class RegularExpression implements Expression {

    @Override
    public MatchResults matchForwards(ByteReader reader, long fromPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MatchResults matchBackwards(ByteReader reader, long fromPosition ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}
