/*
 * Copyright Matt Palmer 2015, All rights reserved.
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


package net.byteseek.utils.droid;


/**
 *
 * Builds DROID ByteSequence XML from a DROID regular expression.
 * <p>
 * Note: there is a signature development service provided by the National Archives at:
 * <p>
 *     http://test.linkeddatapronom.nationalarchives.gov.uk/sigdev/index.htm
 * </p>
 * Also see the same service at:<p>
 *     http://exponentialdecay.co.uk/sd/index.htm
 * </p>
 * These allow you to also generate signature XML by specifying the main components and their
 * expressions.
 * </p>
 *
 * Created by matt on 25/07/15.
 */
public class droidSig {

    public final static String USAGE_HELP =
            "droidSig usage:\n" +
            "---------------\n\n" +
            "droidSig produces DROID Byte Sequence XML fragments from a DROID regular expression.\n\n" +
                    "\t[BOF|EOF|VAR]\t[Optional]\tThe anchoring of the byte sequence.\n" +
                    "\t\t\t\t\tBOF\tBeginning of file.  If not specified, this is the default.\n" +
                    "\t\t\t\t\tEOF\tEnd of file.\n" +
                    "\t\t\t\t\tVAR\tA wildcard search from the beginning of the file.\n" +
                    "\t{expression}\tA DROID signature regular expression.\n\n" +
            "Examples:\n" +
            "\tdroidSig \"01 02 03 04\"\n" +
            "\tdroidSig EOF \"01 02 {4} [00:FF] 05 06 07 08 09 0A {1-4} 0B 0C * 01 02 03 04 05\n\n";

    public static void main(final String[] args) throws Exception {

        if (args == null || args.length == 0) {
            System.out.println(USAGE_HELP);
            System.exit(0);
        }

        int paramIndex = 0;
        String firstParam = args[paramIndex];
        String anchor = "BOFoffset";
        if ("BOF".equals(firstParam)) {
            paramIndex++;
        } else if ("EOF".equals(firstParam)) {
            anchor = "EOFoffset";
            paramIndex++;
        } else if ("VAR".equals(firstParam)) {
            anchor = "Variable";
            paramIndex++;
        }

        String expression = args[paramIndex];
        DroidExpressionBuilder specBuilder = new DroidExpressionBuilder();
        String result = specBuilder.build(expression, anchor).toDROIDXML();

        System.out.println("<!-- \n" + expression + "\n-->");
        System.out.println(result);
    }

}