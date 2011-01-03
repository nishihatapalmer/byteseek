/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression;

import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author Matt Palmer
 */
public interface Expression {

    public MatchResults matchForwards(final ByteReader reader, final long fromPosition );

    public MatchResults matchBackwards(final ByteReader reader, final long fromPosition);
}
