/*
 * Copyright Matt Palmer 2012, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.domesdaybook.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author matt
 *
 */
public class ParseTreeType {
	
	public static final int BYTE_ID			 			= 1;
	public static final int INT_ID			 			= 2;
	public static final int ALL_BITMASK_ID	 			= 3;
	public static final int ANY_BITMASK_ID	 			= 4;
	public static final int SET_ID	 		 			= 5;
	public static final int INVERTED_SET_ID	 			= 6;
	public static final int ANY_ID			 			= 7;
	public static final int CASE_SENSITIVE_STRING_ID 	= 8;
	public static final int CASE_INSENSITIVE_STRING_ID	= 9;
	public static final int SET_RANGE_ID			    = 10;
	
	
	public static final ParseTreeType BYTE 					  = create(BYTE_ID,         		   "A single byte value");
	public static final ParseTreeType INT 					  = create(INT_ID,         		   	   "A single integer value");
	public static final ParseTreeType ALL_BITMASK 			  = create(ALL_BITMASK_ID,  		   "A bitmask for matching all of its bits");
	public static final ParseTreeType ANY_BITMASK 			  = create(ANY_BITMASK_ID,  		   "A bitmask for matching any of its bits");
	public static final ParseTreeType SET					  = create(SET_ID,  				   "A set of byte values");
	public static final ParseTreeType INVERTED_SET			  = create(INVERTED_SET_ID,  		   "An inverted set of byte values");
	public static final ParseTreeType ANY					  = create(ANY_ID,  				   "A wildcard matching any byte value");
	public static final ParseTreeType CASE_SENSITIVE_STRING   = create(CASE_SENSITIVE_STRING_ID,   "An ASCII string to match case sensitively");
	public static final ParseTreeType CASE_INSENSITIVE_STRING = create(CASE_INSENSITIVE_STRING_ID, "An ASCII string to match case insensitively");
	public static final ParseTreeType SET_RANGE				  = create(SET_RANGE_ID,  				   "A range of values between 0 and 255");
	
	
	
	
	
	
	private static final Map<Integer, ParseTreeType> types = new HashMap<Integer, ParseTreeType>();
	
	public synchronized static ParseTreeType create(final int nodeId, final String nodeName) {
		ParseTreeType result = types.get(nodeId);
		if (result != null) {
			final String message = String.format("Node type %d already exists with description: %s", nodeId, result.getDescription());
			throw new IllegalArgumentException(message);
		}
		result = new ParseTreeType(nodeId, nodeName);
		types.put(nodeId, result);
		return result;
	}
	
	private final int id;
	private final String description;
	
	private ParseTreeType(final int id, final String description) {
		this.id = id;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}

}
