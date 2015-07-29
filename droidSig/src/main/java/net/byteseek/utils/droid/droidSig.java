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
 * Builds DROID 6 ByteSequence XML from a DROID regular expression.
 * <p>
 * It attempts to make DROID-6 compatible XML, rather than XML identical to that which PRONOM generates.
 * <p>
 *     It omits the generation of the DefaultShift and Shift elements, as DROID 6 onwards
 * no longer requires these - these values are internally generated if required.
 * <p>
 *     It will not split up fragment elements identically to PRONOM in all cases.
 * PRONOM assumes that classes of bytes (enclosed in square brackets) can't be part of a searchable sequence,
 * but from DROID 6 onwards this is no longer the case.  In most cases this results in faster searches, with
 * fewer fragments to process.
 * <p>
 *     It also has an option to strip out default attribute values from the generated XML.  DROID 6 currently
 * will process this XML correctly - and lots of zero attributes makes the XML much harder to read.
 * <p>
 * Note: there is also a signature development service provided by the National Archives at, which should give
 * PRONOM compatible signatures.
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
            "droidSig\t\tdroidSig produces DROID Byte Sequence XML fragments from a DROID regular expression.\n\n" +

                    "\tUsage:\n" +
                    "\t\t[-r BOF|EOF|VAR]\t[Optional]\tSets the anchor reference of the byte sequences:\n" +
                    "\t\t\t\t\t\t\t\t\t\t * BOF\tBeginning of file.  If not specified, this is the default.\n" +
                    "\t\t\t\t\t\t\t\t\t\t * EOF\tEnd of file.\n" +
                    "\t\t\t\t\t\t\t\t\t\t * VAR\tA wildcard search from the beginning of the file.\n" +

                    "\t\t[-s]\t\t\t\t[Optional]\tStrip out default values from the XML - mostly the attributes set to zero.\n" +
                    "\t\t\t\t\t\t\t\t\t\tDROID will still read this XML correctly (currently) and it removes a lot of noise from the XML.\n" +

                    "\t\t{expression}\t\tThe last parameter is the DROID signature regular expression to parse.\n\n" +
            "\tExamples:\n" +
            "\t\tdroidSig \"01 02 03 04\"\n" +
            "\t\tdroidSig -r EOF -s \"01 02 {4} [00:FF] 05 06 07 08 09 0A {1-4} 0B 0C * 01 02 03 04 05\"\n\n";

    public static void main(final String[] args) throws Exception {

        // print help with no parameters:
        if (args == null || args.length == 0) {
            System.out.println(USAGE_HELP);
            System.exit(1);
        }

        // process the parameters:
        String anchor = "BOFoffset";     // default is BOFoffset for the anchor if not specified.
        boolean stripDefaults = false;   // default is not to strip out default values, if not specified.
        int numParameters = args.length;
        int paramIndex = 0;

        while (paramIndex < numParameters - 1) {
            String param = args[paramIndex];
            if ("-r".equals(param)) {
                paramIndex++;
                String argument = args[paramIndex];
                if ("BOF".equals(argument)) {
                    anchor = "BOFoffset";
                } else if ("EOF".equals(argument)) {
                    anchor = "EOFoffset";

                } else if ("VAR".equals(argument)) {
                    anchor = "Variable";
                }
            } else if ("-s".equals(param)) {
                stripDefaults = true;
            }
            paramIndex++;
        }

        String expression = args[paramIndex];
        DroidSequenceBuilder specBuilder = new DroidSequenceBuilder();
        String result = specBuilder.build(expression, anchor).toDROIDXML(stripDefaults);

        System.out.println(result);
    }

}