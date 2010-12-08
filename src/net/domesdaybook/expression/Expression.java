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
public interface Expression {

    public MatchResults matchForwards(final Bytes reader, final long fromPosition );

    public MatchResults matchBackwards(final Bytes reader, final long fromPosition);
}
