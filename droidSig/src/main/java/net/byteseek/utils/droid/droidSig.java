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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Builds DROID 6 ByteSequence XML from a DROID regular expression.
 * The syntax of DROID regular expressions is fully defined in the document:
 * https://www.nationalarchives.gov.uk/aboutapps/fileformat/pdf/automatic_format_identification.pdf
 * Help is available from the command line on usage (pass in no parameters or -h or --help).
 * There is also help on the DROID regular expression syntax by passing -h syntax.
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

    //TODO: check exit codes, consistent, standard if such a thing exists.

    //TODO: update existing XML file.

    /**
     * Processes the command line arguments passed in, either building some xml output,
     * or outputting help on the utility or regular expression syntax.
     *
     * @param args The command line parameters
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {

        // print help with no parameters to standard error:
        if (args == null || args.length == 0) {
            System.err.println("ERROR: no parameters specified; printing the standard help:\n");
            System.err.println(USAGE_HELP);
            System.exit(1);
        }

        // Set default values:
        XmlOutput xmlOutput                = XmlOutput.FILE;
        int       sigId                    = 100000;
        String    formatName               = "Test Signature Format";
        String    puid                     = "example.com/fmt/x";
        int       formatId                 = 100000;
        String    extension                = "";
        boolean   stripDefaults            = false;
        String    updateFileName           = "";

        // Prepare our list of expressions:
        DroidSequenceBuilder sequenceBuilder = new DroidSequenceBuilder();
        List<ByteSequenceSpec> expressions = new ArrayList<ByteSequenceSpec>();

        // Process the parameters
        int numParameters = args.length;
        int paramIndex = 0;
        int firstExpressionIndex = -1;
        while (paramIndex < numParameters) {
            String param = args[paramIndex];
            Command command = Command.getCommand(param);
            switch(command) {

                case XML: {
                    paramIndex++;
                    xmlOutput = getXMLOutput(nextArgument(args, paramIndex));
                    break;
                }

                case ID: {
                    paramIndex++;
                    sigId = getInteger(nextArgument(args, paramIndex));
                    break;
                }

                case NAME: {
                    paramIndex++;
                    formatName = nextArgument(args, paramIndex);
                    break;
                }

                case PUID: {
                    paramIndex++;
                    puid = nextArgument(args, paramIndex);
                    break;
                }

                case FORMATID: {
                    paramIndex++;
                    formatId = getInteger(nextArgument(args, paramIndex));
                    break;
                }

                case EXT: {
                    paramIndex++;
                    extension = nextArgument(args, paramIndex);
                    break;
                }

                case STRIP: { //TODO: does this strip XML in updated files too?
                    stripDefaults = true;
                    break;
                }

                case UPDATE: {
                    paramIndex++;
                    updateFileName = nextArgument(args, paramIndex);
                    break;
                }

                case HELP: {
                    paramIndex++;
                    printHelp(nextArgument(args, paramIndex));
                    break;
                }

                case NOT_RECOGNISED: {
                    expressions.add(sequenceBuilder.build(param));
                    break;
                }
            }

            paramIndex++;
        }

        String XML = "";
        if (updateFileName.isEmpty()) {
            InternalSignatureSpec signatureSpec = new InternalSignatureSpec(sigId, expressions);
            FormatSpec            formatSpec    = new FormatSpec(formatId, formatName, puid, sigId, extension);
            SignatureFileSpec     sigFileSpec   = new SignatureFileSpec(signatureSpec, formatSpec);
            switch (xmlOutput) {
                case SEQ:
                    XML = getSequenceFragmentXML(expressions, stripDefaults);
                    break;
                case SIG:
                    XML = signatureSpec.toDROIDXML(stripDefaults);
                    break;
                case FRAGS:
                    XML = sigFileSpec.toDROIDXMLFragments(stripDefaults);
                    break;
                case FILE:
                    XML = sigFileSpec.toDROIDXML(stripDefaults);
                    break;
            }
        } else {
            //TODO: update the XML and print it out...
        }

        System.out.println(XML);
    }

    private static String getSequenceFragmentXML(List<ByteSequenceSpec> expressions, boolean stripDefaults) {
        String XML = "";
        for (ByteSequenceSpec byteSequenceSpec: expressions) {
            XML += byteSequenceSpec.toDROIDXML(stripDefaults) + '\n';
        }
        return XML;
    }


    /*******************************************************************************************************************
     *            Enums to help parsing the command line commands and command arguments
     */
    private enum Command {

        NOT_RECOGNISED( "", ""),
        XML(            "x", "xml"),
        ID(             "i", "id"),
        NAME(           "n", "name"),
        PUID(           "p", "puid"),
        FORMATID(       "f", "formatid"),
        EXT(            "e", "ext"),
        STRIP(          "s", "strip"),
        UPDATE(         "u", "update"),
        HELP(           "h", "help");

        private final String shortCommand;
        private final String longCommand;

        Command(String shortCommand, String longCommand) {
            this.shortCommand = "-" + shortCommand;
            this.longCommand = "--" + longCommand;
        }

        public boolean hasName(String commandName) {
            return shortCommand.equals(commandName) || longCommand.equals(commandName);
        }

        public static Command getCommand(String commandName) {
            for (Command command : Command.values()) {
                if (command.hasName(commandName)) {
                    return command;
                }
            }
            return Command.NOT_RECOGNISED;
        }
    }

    private enum XmlOutput {
        SEQ,
        SIG,
        FRAGS,
        FILE;

        public static XmlOutput getXmlOutput(String argument) {
            try {
                return XmlOutput.valueOf(argument);
            } catch (IllegalArgumentException ignoreReturnNull) {
            }
            return null;
        }
    }


    /*******************************************************************************************************************
     *           Private methods.
     */

    private static String nextArgument(String[] args, int paramIndex) {
        if (paramIndex < args.length) {
            return args[paramIndex];
        }
        return "";
    }

    private static XmlOutput getXMLOutput(String argument) {
        XmlOutput xmlOutput = XmlOutput.getXmlOutput(argument);
        if (xmlOutput == null) {
            System.err.println("ERROR: unknown argument for xml output -x, must be SEQ, SIG, FMT or ALL: " + argument);
            System.exit(4);
        }
        return xmlOutput;
    }

    private static int getInteger(String argument) {
        int result = -1;
        try {
            result = Integer.valueOf(argument);
        } catch (NumberFormatException ex) {
            System.err.println("ERROR: the argument was not an integer as expected: " + argument);
            System.exit(5);
        }
        return result;
    }

    private static void printHelp(String argument) {
        System.out.println("\n\n" + USAGE_HELP + "\n");
        if ("syntax".equals(argument)) {
            System.out.println(SYNTAX_HELP + "\n\n");
        } else if ("license".equals(argument)) {
            System.out.println(LICENCE);
        }
        System.exit(0);
    }


    /*******************************************************************************************************************
     *                     Help strings.
     */

    //TODO: multiple byte sequences as separate parameters within a single internalsignature.

    public final static String USAGE_HELP =
            "droidSig v1.0\t (c) Matt Palmer 2015, all rights reserved.\n" +
                    " droidSig produces DROID signature XML from a DROID regular expression.\n" +
                    " It can also update an existing signature XML file.\n" +

                    "\n License:\n" +
                    "   This code is released under a BSD license, see help on the license for details.\n" +

                    "\n Output:\n" +
                    "   XML is written to standard out with a zero exit code.\n" +
                    "   Error messages are written to standard error, with a non-zero exit code.\n" +

                    "\n Usage:\n" +
                    "\n     droidSig [options] {expression} {expression} ...\n\n" +

                    "   All options are optional and can appear in any order.  They all have default values if not specified.\n" +
                    "   After any options, all remaining parameters are expressions which are parsed into <ByteSequence> elements.\n" +

                    "\n Options:\n" +

                    "   -i --id        {integer}         Set the ID of the internal signature, defaulting to 100000 if not specified.\n" +

                    "   -n --name      {string}          Set the name of the file format, defaulting to \"Test Signature Format\" if not specified.\n" +

                    "   -p --puid      {string}          Set the PUID of the file format, defaulting to \"example.com/fmt/x\" if not specified.\n" +

                    "   -f --formatid  {integer}         Set file format id to use, defaulting to 100000 if not specified.\n" +

                    "   -e --ext       {string}          Set the file format extension for the file format, defaulting to no extension if not specified.\n" +

                    "   -x --xml SEQ|SIG|FMT|FRAGS|FILE  Set the type of XML to write to standard out, defaulting to FILE if not specified:\n" +
                    "                                      SEQ    A <ByteSequence> XML fragment only.\n" +
                    "                                      SIG    An <InternalSignature> XML fragment with the <ByteSequence> XML inside it.\n" +
                    "                                      FMT    A <FileFormat> XML fragment linked to an internal signature with the signature id you have.\n" +
                    "                                      FRAGS  An <InternalSignature> fragment followed by the <FileFormat> fragment on the next line.\n" +
                    "                                      FILE   A new signature XML file containing the <InternalSignature> and <FileFormat> specified.\n" +

                    "   -u --update    {sig filename}    Set the filename of a signature file to update, creating it if it doesn't exist.\n" +
                    "                                    The XML type set by the -x option determines how the file is updated:\n" +
                    "                                      SEQ    Adds <ByteSequence>s into an <InternalSignature> with the current id, without disturbing any existing sequences.\n" +
                    "                                      SIG    Replaces the <InternalSignature> element with the current id with one containing only the <ByteSequences> specified.\n" +
                    "                                      FMT    Updates the <FileFormat> of the format id with the values you specify, leaving the others unchanged.\n" +
                    "                                             It will be linked with a signature id only if you have specified one with the -i option.\n" +
                    "                                             If no <FileFormat> exists with your format id, a new one is created.\n" +
                    "                                      FRAGS  Adds <ByteSequence>s into an <InternalSignature> with the current id, without disturbing any existing sequences.\n" +
                    "                                             Updates the <FileFormat> element as for the FMT option.\n" +
                    "                                      FILE   Replaces the <InternalSignature> as for the SIG option.\n" +
                    "                                             Updates <FileFormat> as for the FMT option.\n" +

                    "   -s --strip                       Strip out default attribute values from the XML - if not set then all values will be output.\n" +
                    "                                    DROID will still read this XML correctly (currently) and it removes a lot of noise from the XML.\n" +

                    "   -h --help [syntax|license]       Print this help, and optionally help on one other topic:\n" +
                    "                                      syntax  Help on the expression syntax will be printed.\n" +
                    "                                      license The license for the use of this code will be printed.\n" +

                    "\n Expressions:\n" +
                    "   {expression} {expression} ...    After the options above, all remaining parameters are interpreted as <ByteSequence> expressions to be parsed.\n" +
                    "                                    If there are multiple expressions, then any signature will contain multiple byte sequences.\n" +

                    "\n Anchoring expressions:\n" +
                    "   Byte sequence expressions can be matched at the beginning of a file or the end of a file.\n" +
                    "   They can also be at some fixed offset from the start or end of a file, or a variable offset.\n" +
                    "   In order to specify these, the normal DROID expression syntax is extended a little using common conventions:\n" +
                    "     \\A     If this appears at the start of an expression, it means anchor to the beginning of the file (the default).\n" +
                    "     \\Z     If this appears at the end of an expression, it means anchor to the end of the file.\n" +
                    "     *      If the star wildcard appears at the start of an expression, it means to search from the beginning of the file.\n" +
                    "     {n}    Fixed gap: add the gap number inside curly brackets.\n" +
                    "            If anchored to the beginning, it should appear at the start of the expression.  If anchored to the end, at the end.\n" +
                    "     {n-m}  Variable gap: add the minimum and maximum gap inside curly brackets separated by a hyphen.\n" +
                    "            If anchored to the beginning, it should appear at the start of the expression.  If anchored to the end, at the end.\n" +

                    "\n Examples:\n" +
                    "   droidSig \"01 02 03 04\"\n" +
                    "   droidSig --strip \"01 02 {4} [00:FF] 05 06 07 08 09 0A {1-4} 0B 0C * 01 02 03 04 05\"\n" +
                    "   droidSig -a EOF -x SIG -i 9090 \"01 02 03 04 (0D|0A|0A0D) 31\"\n" +
                    "   droidSig --xml ALL --name \"Acme Report Data\" --puid \"acme.com\\fmt\\5\" \"4F 5E 92 (0A|0D) 20 20 72 [01:02]\"\n\n";


    public static final String LICENCE = "droidSig\n" +
            " Copyright Matt Palmer 2015, All rights reserved.\n" +
            "\n" +
            " This code is licensed under a standard 3-clause BSD license:\n" +
            "\n" +
            " Redistribution and use in source and binary forms, with or without modification,\n" +
            " are permitted provided that the following conditions are met:\n" +
            "\n" +
            "  * Redistributions of source code must retain the above copyright notice,\n" +
            "    this list of conditions and the following disclaimer.\n" +
            "\n" +
            "  * Redistributions in binary form must reproduce the above copyright notice,\n" +
            "    this list of conditions and the following disclaimer in the documentation\n" +
            "    and/or other materials provided with the distribution.\n" +
            "\n" +
            "  * The names of its contributors may not be used to endorse or promote products\n" +
            "    derived from this software without specific prior written permission.\n" +
            "\n" +
            " THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"\n" +
            " AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE\n" +
            " IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE\n" +
            " ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE\n" +
            " LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n" +
            " CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF\n" +
            " SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS\n" +
            " INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN\n" +
            " CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)\n" +
            " ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE\n" +
            " POSSIBILITY OF SUCH DAMAGE.\n\n";

    public static final String SYNTAX_HELP = "Official Expression Syntax\n" +
            "--------------------------\n" +
            "DROID regular expressions are a simplified subset of general regular expressions, and they\n" +
            "have a slightly different syntax.  DROID expressions are byte-oriented rather than text-oriented.\n" +
            "\n" +
            "Expression            Format                  Examples (comma separated)\n" +
            "--------------------  ----------------------- -----------------------------------\n" +
            "Byte                  2-digit hexadecimal     01,A2,C3,FF\n" +
            "Unknown byte          Two question marks      ??\n" +
            "Sequence              HexByte+                FE3D101E,7F,09101112??EE\n" +
            "Zero to many unknown  An asterisk             *\n" +
            "Number unknown        {n}                     {1},{32},{256}\n" +
            "Min to max unknown    {n-m}                   {0-9},{1-100},{50-60}\n" +
            "Min to many unknown   {n-*}                   {1-*},{32-*},{256-*}\n" +
            "Byte range            [HexByte:HexByte]       [20:7F],[01:1F],[7F:FF]\n" +
            "Not byte range        [!HexByte:HexByte]      [!20:7F],[!01:1F],[!7F:FF]\n" +
            "Not byte              [!HexByte]              [!01],[!A2],[!C3],[!FF]\n" +
            "Alternative sequences (X|Y|Z)                 (09|0A|0D|20),(0D|OA|OAOD),(01|010203FF7F|01023E)\n" +
            "\n" +
            "Alternative sequences can only be composed of bytes, although DROID 6 can also handle anything that\n" +
            "specifies a set of bytes.  A signature only composed of alternative sequences is not valid; DROID requires\n" +
            "at least one unambiguous sequence in each SubSequence, even if this only a single byte.  You\n" +
            "\n" +
            "To see examples of real signatures, look at the PRONOM service at the UK National Archives.  Signatures\n" +
            "for the formats managed there are given on a Signatures tab for each format.\n" +
            "\n" +
            "\n" +
            "Unofficial byteseek syntax\n" +
            "--------------------------\n" +
            "DROID 6 currently uses the byteseek library (also created by the droidSig author) to parse and search for byte sequences.\n" +
            "This allows the use of some byteseek-specific syntax within DROID 6 expressions.  Most of this is not officially\n" +
            "supported by the National Archives, although the string and whitespace byteseek syntax is already used within \"Container\"\n" +
            "signatures, so this syntax at least is unlikely to change.\n" +
            "\n" +
            "You can separate elements in an expression with any whitespace: tab, space, newline or carriage return.\n" +
            "These will be ignored by byteseek when processing an expression; it just makes expressions easier to read.\n" +
            "For example, instead of: \"ea277b2b\" you can write: \"ea 27 7b 2b\".\n" +
            "\n" +
            "Expression              Format               Examples (comma separated)\n" +
            "----------------------- -------------------- ---------------------------------------------------\n" +
            "String                  'ascii chars'        'ABC123','pdf:','<html>'\n" +
            "Case-insensitive string `ascii chars`        `aBc123`,`pDf:`,`<HtMl>`\n" +
            "Arbitrary byte sets     [bytes and ranges]   [00 7f:ff],[09 0A 0D ' '],['A'-'Z' 'a'-'z' '0':'9']\n" +
            "All Bitmasks            &HexByte             &7f,&80,&55,&AA\n" +
            "Any Bitmasks            ~HexByte             ~0f,~aa,~1f\n" +
            "\n" +
            "Arbitrary byte sets are syntactically the same as byte ranges in the official syntax, except you can have any number\n" +
            "of arbitrary bytes or ranges in the set.  Just give the bytes or ranges of bytes you want to match inside the square brackets.\n" +
            "\n" +
            "Bitmasks deserve a bit more explanation.  Bitmasks take a byte value, expressed as a hexadecimal digit.\n" +
            "It is the binary bits of this value, rather than the absolute value, which are matched against a byte being scanned in a file.\n" +
            "\n" +
            " * For an \"All\" bitmask value to match:\n" +
            "    - all the bits set to one in the bitmask must also be set in the target file byte.\n" +
            "    - e.g. the all bitmask &0F = 00001111.  A value such as 0x8F = 10001111 would match.\n" +
            "\n" +
            " * For any \"Any\" bitmask value to match:\n" +
            "    - at least one of the bits set to one in the bitmask must also be set in the target file byte.\n" +
            "    - e.g. the any bitmask &0F = 00001111.  A value such as 0x81 = 10000001 would match.\n" +
            "\n" +
            "Binary file formats often have bit mask values within them, reflecting different options within the format.\n" +
            "This may be useful in distinguishing variants of formats from each other.";


}