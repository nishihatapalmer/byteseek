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


import java.util.Calendar;
import java.util.Date;
import java.util.Random;

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

    //TODO: use standard exit codes if they exist for various conditions....

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
            System.err.println(USAGE_HELP);
            System.exit(1);
        }

        // Set default values:
        Random random         = new Random();
        String anchor         = "BOFoffset";
        String xmlOutput      = "SEQ";
        int    sigId          = 10000 + random.nextInt(990000);
        String formatName     = "Test Signature Format";
        String puid           = "example.com/fmt/x";
        int    formatId       = 10000 + random.nextInt(990000);
        String extension      = "";
        boolean stripDefaults = false;

        // Process any parameters:
        int numParameters = args.length;
        int paramIndex = 0;
        while (paramIndex < numParameters) {
            String param = args[paramIndex];
            Command command = getCommand(param);
            switch(command) {

                case ANCHOR: {
                    paramIndex++;
                    anchor = getAnchor(nextArgument(args, paramIndex));
                    break;
                }

                case XML: {
                    paramIndex++;
                    xmlOutput = getXML(nextArgument(args, paramIndex));
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

                case STRIP: {
                    stripDefaults = true;
                    break;
                }

                case HELP: {
                    paramIndex++;
                    printHelp(nextArgument(args, paramIndex));
                    break;
                }

                case NULL: {
                    if (paramIndex < args.length - 1) {
                        System.err.println("ERROR: Unknown command encountered: " + param);
                        System.exit(6);
                    }
                    break; // the last parameter can be a null command - it will be the expression to process instead.
                }
            }

            paramIndex++;
        }

        // Build the byte sequence from the expression, and wrap it in a signature with a format definition:
        String                expression = args[args.length - 1]; // The expression is always the last parameter if we haven't already exited.
        DroidSequenceBuilder  sequenceBuilder  = new DroidSequenceBuilder();
        ByteSequenceSpec      byteSequenceSpec = sequenceBuilder.build(expression, anchor);
        InternalSignatureSpec signatureSpec    = new InternalSignatureSpec(sigId, byteSequenceSpec);
        FormatSpec            formatSpec       = new FormatSpec(formatId, formatName, puid, sigId, extension);

        // Output the correct level of XML:
        String XML = "";
        if ("SEQ".equals(xmlOutput)) {
            XML = byteSequenceSpec.toDROIDXML(stripDefaults);
        } else if ("SIG".equals(xmlOutput)) {
            XML = signatureSpec.toDROIDXML(stripDefaults);
        } else if ("FMT".equals(xmlOutput)) {
            XML = getFormatXML(stripDefaults, signatureSpec, formatSpec);
        } else if ("ALL".equals(xmlOutput)) {
            XML = getAllXML(stripDefaults, signatureSpec, formatSpec);
        }
        System.out.println(XML);
    }


    /*******************************************************************************************************************
     *            Commands to help parsing the command line parameters.
     */
    private enum Command {

        NULL(    "", ""),
        ANCHOR(  "a", "anchor"),
        XML(     "x", "xml"),
        ID(      "i", "id"),
        NAME(    "n", "name"),
        PUID(    "p", "puid"),
        FORMATID("f", "formatid"),
        EXT(     "e", "ext"),
        STRIP(   "s", "strip"),
        HELP(    "h", "help");

        private final String shortCommand;
        private final String longCommand;

        Command(String shortCommand, String longCommand) {
            this.shortCommand = "-" + shortCommand;
            this.longCommand = "--" + longCommand;
        }

        public boolean equals(String parameter) {
            return shortCommand.equals(parameter) || longCommand.equals(parameter);
        }
    }

    private static Command getCommand(String parameter) {
        Command[] commands = Command.values();
        for (Command command : commands) {
            if (command.equals(parameter)) {
                return command;
            }
        }
        return Command.NULL;
    }



    /*******************************************************************************************************************
     *           Private methods.
     */

    private static String getFormatXML(boolean stripDefaults, InternalSignatureSpec signatureSpec, FormatSpec formatSpec) {
        return signatureSpec.toDROIDXML(stripDefaults) + "\n" +
              formatSpec.toDROIDXML(stripDefaults);
    }

    private static String getAllXML(boolean stripDefaults, InternalSignatureSpec signatureSpec, FormatSpec formatSpec) {
        Date now = new Date();
        return  XML_HEADER +
                String.format(SIGNATURE_FILE_START, now, now, now, now, now, now) +
                SIGNATURE_COLLECTION_START +
                signatureSpec.toDROIDXML(stripDefaults) +
                SIGNATURE_COLLECTION_END +
                FORMAT_COLLECTION_START  +
                formatSpec.toDROIDXML(stripDefaults) +
                FORMAT_COLLECTION_END +
                SIGNATURE_FILE_END;
    }

    private static String nextArgument(String[] args, int paramIndex) {
        if (paramIndex < args.length) {
            return args[paramIndex];
        }
        return "";
    }

    private static String getAnchor(String argument) {
        if ("BOF".equals(argument)) {
            return "BOFoffset";
        }
        if ("EOF".equals(argument)) {
            return "EOFoffset";
        }
        if ("VAR".equals(argument)) {
            return "Variable";
        }
        System.err.println("ERROR: unknown argument for anchor reference -r, must be BOF, EOF or VAR: " + argument);
        System.exit(3);
        return null;
    }

    private static String getXML(String argument) {
        if ("SEQ".equals(argument) ||
            "SIG".equals(argument) ||
            "FMT".equals(argument) ||
            "ALL".equals(argument)) {
            return argument;
        }
        System.err.println("ERROR: unknown argument for xml output -x, must be SEQ, SIG, FMT or ALL: " + argument);
        System.exit(4);
        return null;
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
        }
        System.exit(0);
    }


    /*******************************************************************************************************************
     *                     Help strings.
     */

    public final static String USAGE_HELP =
            "droidSig\tv1.0\tdroidSig produces DROID signature XML from a DROID regular expression.\n\n" +

                    " Usage:\n\n" +
                    "  All parameters are optional and can appear in any order,\n" +
                    "  except the last which is always the expression to be processed.\n\n" +

                    "  -a --anchor BOF|EOF|VAR   Sets the anchor reference of the byte sequence to search from:\n" +
                    "                             * BOF  Beginning of file.  If not specified, this is the default.\n" +
                    "                             * EOF  End of file.\n" +
                    "                             * VAR  A wildcard * search from the beginning of the file.\n" +

                    "  -x --xml SEQ|SIG|FMT|ALL  The type of XML to output:\n" +
                    "                             * SEQ  The byte sequence alone.  If not specified, this is the default.\n" +
                    "                             * SIG  The internal signature ML with the byte sequence XML inside it.\n" +
                    "                             * FMT  The SIG XML defined above followed by a linked format XML definition on the next line.\n" +
                    "                             * ALL  A complete signature file XML with an internal signature and a file format.\n" +

                    "  -i --id   {integer}       The ID of the internal signature if the --xml option specified SIG, FMT or ALL.\n" +
                    "                            If not specified, then the id will be randomly generated above 10000.\n" +

                    "  -n --name {string}        The name of the file format if the --xml option specified FMT or ALL.\n" +
                    "                            If not specified, then the format name will default to \"Test Signature Format\".\n" +

                    "  -p --puid {string}        The PUID of the file format if the --xml option specified FMT or ALL.\n" +
                    "                            If not specified, then the format PUID will default to \"example.com/fmt/x\"\n" +

                    "  -f --formatid  {integer}  The file format id to use if the -xml specified FMT or ALL.\n" +
                    "                            If not specified, then the format id will be randomly generated above 10000.\n" +

                    "  -e --ext  {string}        The file format extension for the file format if the --xml specified FMT or ALL.\n" +
                    "                            If not specified, then there will be no extension associated with the file format.\n" +

                    "  -s --strip                Strip out default attribute values from the XML - if not set then all values will be output.\n" +
                    "                            DROID will still read this XML correctly (currently) and it removes a lot of noise from the XML.\n" +

                    "  -h --help [syntax]        Print this help and exit.\n" +
                    "                            If the optional argument \"syntax\" is also given, help on the expression syntax will be printed as well.\n" +

                    "  {expression}              The last parameter is the DROID signature regular expression string to parse, if help is not being printed.\n\n" +

                    "\tExamples:\n" +
                    "\t\tdroidSig \"01 02 03 04\"\n" +
                    "\t\tdroidSig --strip \"01 02 {4} [00:FF] 05 06 07 08 09 0A {1-4} 0B 0C * 01 02 03 04 05\"\n" +
                    "\t\tdroidSig -a EOF -x SIG -i 9090 \"01 02 03 04 (0D|0A|0A0D) 31\"\n" +
                    "\t\tdroidSig --xml ALL --name \"Acme Report Data\" --puid \"acme.com\\fmt\\5\" \"4F 5E 92 (0A|0D) 20 20 72 [01:02]\"\n\n";

    private static final String XML_HEADER                 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String SIGNATURE_FILE_START       = "<FFSignatureFile DateCreated=\"%tY-%tm-%tdT%tH:%tM:%tS\" Version=\"1\"" +
            " xmlns=\"http://www.nationalarchives.gov.uk/pronom/SignatureFile\">";
    private static final String SIGNATURE_COLLECTION_START = "<InternalSignatureCollection>";
    private static final String SIGNATURE_COLLECTION_END   = "</InternalSignatureCollection>";
    private static final String FORMAT_COLLECTION_START    = "<FileFormatCollection>";
    private static final String FORMAT_COLLECTION_END      = "</FileFormatCollection>";
    private static final String SIGNATURE_FILE_END         = "</FFSignatureFile>";


    private static String SYNTAX_HELP = "Official Expression Syntax\n" +
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