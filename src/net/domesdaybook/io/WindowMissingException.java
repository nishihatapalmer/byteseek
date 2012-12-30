/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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

package net.domesdaybook.io;

import java.io.IOException;

/**
 * An exception thrown when a {@link WindowReader} cannot access a Window which it
 * should be able to return. It extends {@link java.io.IOException}, which means
 * it is a checked exception. However, it will be thrown from methods which can
 * also throw IOExceptions for other reasons, so no specific catch block is
 * technically required, although you can specifically catch this exception if
 * you want to know that this specific problem has occurred.
 * <p>
 * This may be because an inappropriate
 * {@link net.domesdaybook.io.cache.WindowCache} object was used for the
 * input source of the WindowReader and the access pattern used with it.
 * 
 * @author Matt Palmer.
 */
public class WindowMissingException extends IOException {

	/**
	 * Constructs a WindowMissingException with a descriptive message.
	 * 
	 * @param message
	 *            The message to include with the exception.
	 */
	public WindowMissingException(final String message) {
		super(message);
	}

	/**
	 * Constructs a WindowMissingException from a Throwable cause.
	 * 
	 * @param cause
	 *            The Throwable which caused this exception to be thrown.
	 */
	public WindowMissingException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a WindowMissingException from a descriptive message and a
	 * Throwable cause.
	 * 
	 * @param message
	 *            The message to include with the exception.
	 * @param cause
	 *            The Throwable which caused this exception to be thrown.
	 */
	public WindowMissingException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
