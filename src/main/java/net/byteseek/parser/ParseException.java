/*
 * Copyright Matt Palmer 2009-2019, All rights reserved.
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

package net.byteseek.parser;

/**
 * A checked exception class representing the failure to parse an expression.
 *
 * @author Matt Palmer
 */
public class ParseException extends Exception implements ParseInfo {

    private final String expression;
    private final int position;

    private static final long serialVersionUID = -4081239885659052145L;

    /**
     * Constructs a ParseException with the supplied message.
     *
     * @param message the error message
     * @param info Information about where in parsing the error occurred.
     */
    public ParseException(final String message, final ParseInfo info) {
        super(addContext(message, info));
        this.expression = info.getString();
        this.position = info.getPosition();
    }

    /**
     * Constructs a ParseException with the supplied cause.
     *
     * @param cause the cause of the parse exception
     * @param info Information about where in parsing the error occurred.
     */
    public ParseException(final Throwable cause, final ParseInfo info) {
        super(addContext(cause.getMessage(), info), cause);
        this.expression = info.getString();
        this.position = info.getPosition();
    }

    /**
     * Constructs a ParseException with the supplied message and cause.
     *
     * @param message The error message
     * @param cause   The cause of the parse exception.
     * @param info Information about where in parsing the error occurred.
     */
    public ParseException(final String message, final Throwable cause, final ParseInfo info) {
        super(addContext(message, info), cause);
        this.expression = info.getString();
        this.position = info.getPosition();
    }

    public String getString() {
        return expression;
    }

    public int getPosition() {
        return position;
    }

    private static String addContext(final String message, final ParseInfo info) {
        return "Parse error at position " + info.getPosition() + " in expression " + info.getString() + " : " + message;
    }
}
