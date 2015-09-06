droidSig v1.0
=============

droidSig creates DROID-6 compatible XML given a DROID regular expression.
This XML can be inserted into a DROID signature file to allow DROID to search for that expression.
DROID itself cannot process the regular expression syntax directly.  Instead, the PRONOM service
normally pre-processes the expression, outputting the DROID signature XML.


Requirements
------------
* Java 7 or above.

Usage
-----



Adding to a DROID signature file.
---------------------------------
droidSig only converts a regular expression into the appropriate <ByteSequence> XML.  In order for DROID
to use this, the <ByteSequence> XML must be inserted into an <InternalSignature> definition,
and that in turn linked to a <FileFormat>.

For example, if we generate a <ByteSequence> for the expression "01 02 03 04",
anchored to the start of the text being scanned (BOF), we get:

<ByteSequence Reference="BOFoffset">
  <SubSequence Position="1" SubSeqMaxOffset="0" SubSeqMinOffset="0">
    <Sequence>01 02 03 04</Sequence>
  </SubSequence>
<ByteSequence>

To allow DROID to search for this, we must add it inside an <InternalSignature>, giving it an ID which is not used
elsewhere in the signature file if it's a new signature.  droidSig doesn't do this, as it doesn't know what ids are
available in the signature file you're going to insert it in.

<InternalSignature ID="99999">
  <ByteSequence Reference="BOFoffset">
    <SubSequence Position="1" SubSeqMaxOffset="0" SubSeqMinOffset="0">
      <Sequence>01 02 03 04</Sequence>
    </SubSequence>
  <ByteSequence>
</InternalSignature>

Then we must link the internal signature to a file format we want to recognise.
Again, assuming this is a new format entirely, we create a new <FileFormat> element
inside the existing <FileFormatCollection> element, with an ID not used by another FileFormat,
a name for the format and a PUID to identify it unambiguously.  If you like, you can also specify
the format version, and a mime type for the format; see a signature file for examples of this.

For PUIDs not already allocated by PRONOM, you should use a different format to avoid ambiguity.
It is just a string, so it can be anything you like.  I suggest prefixing a normal looking PUID
with your domain name and a forward slash, in a URI-like syntax.
For example, if your organisation is "Acme Ltd.", then you could have a PUID of "acme.com/fmt/1".
Do not use "fmt" or "x-fmt" as a prefix, as these are allocated by the National Archives.

We link it to our InternalSignature with the <InternalSignatureID> element inside it.
You can also optionally specify a filename extension for the file format if you like:

<FileFormat ID="10000" Name="Acme Test Format 1" PUID="acme.com/fmt/1">
  <InternalSignatureID>99999</InternalSignatureID>
  <Extension>tst</Extension>
</FileFormat>

Now DROID will be able to identify files having your new signature.


Official Expression Syntax
--------------------------
DROID regular expressions are a simplified subset of general regular expressions, and they
have a slightly different syntax.  DROID expressions are byte-oriented rather than text-oriented.

Expression            Format                  Examples (comma separated)
--------------------  ----------------------- -----------------------------------
Byte                  2-digit hexadecimal     01,A2,C3,FF
Unknown byte          Two question marks      ??
Sequence              HexByte+                FE3D101E,7F,09101112??EE
Zero to many unknown  An asterisk             *
Number unknown        {n}                     {1},{32},{256}
Min to max unknown    {n-m}                   {0-9},{1-100},{50-60}
Min to many unknown   {n-*}                   {1-*},{32-*},{256-*}
Byte range            [HexByte:HexByte]       [20:7F],[01:1F],[7F:FF]
Not byte range        [!HexByte:HexByte]      [!20:7F],[!01:1F],[!7F:FF]
Not byte              [!HexByte]              [!01],[!A2],[!C3],[!FF]
Alternative sequences (X|Y|Z)                 (09|0A|0D|20),(0D|OA|OAOD),(01|010203FF7F|01023E)

Alternative sequences can only be composed of bytes, although DROID 6 can also handle anything that
specifies a set of bytes.  A signature only composed of alternative sequences is not valid; DROID requires
at least one unambiguous sequence in each SubSequence, even if this only a single byte.  You

To see examples of real signatures, look at the PRONOM service at the UK National Archives.  Signatures
for the formats managed there are given on a Signatures tab for each format.


Unofficial byteseek syntax
--------------------------
DROID 6 currently uses the byteseek library (also created by the droidSig author) to parse and search for byte sequences.
This allows the use of some byteseek-specific syntax within DROID 6 expressions.  Most of this is not officially
supported by the National Archives, although the string and whitespace byteseek syntax is already used within "Container"
signatures, so this syntax at least is unlikely to change.

You can separate elements in an expression with any whitespace: tab, space, newline or carriage return.
These will be ignored by byteseek when processing an expression; it just makes expressions easier to read.
For example, instead of: "ea277b2b" you can write: "ea 27 7b 2b".

Expression              Format               Examples (comma separated)
----------------------- -------------------- ---------------------------------------------------
String                  'ascii chars'        'ABC123','pdf:','<html>'
Case-insensitive string `ascii chars`        `aBc123`,`pDf:`,`<HtMl>`
Arbitrary byte sets     [bytes and ranges]   [00 7f:ff],[09 0A 0D ' '],['A'-'Z' 'a'-'z' '0':'9']
All Bitmasks            &HexByte             &7f,&80,&55,&AA
Any Bitmasks            ~HexByte             ~0f,~aa,~1f

Arbitrary byte sets are syntactically the same as byte ranges in the official syntax, except you can have any number
of arbitrary bytes or ranges in the set.  Just give the bytes or ranges of bytes you want to match inside the square brackets.

Bitmasks deserve a bit more explanation.  Bitmasks take a byte value, expressed as a hexadecimal digit.
It is the binary bits of this value, rather than the absolute value, which are matched against a byte being scanned in a file.

 * For an "All" bitmask value to match:
    - all the bits set to one in the bitmask must also be set in the target file byte.
    - e.g. the all bitmask &0F = 00001111.  A value such as 0x8F = 10001111 would match.

 * For any "Any" bitmask value to match:
    - at least one of the bits set to one in the bitmask must also be set in the target file byte.
    - e.g. the any bitmask &0F = 00001111.  A value such as 0x81 = 10000001 would match.

Binary file formats often have bit mask values within them, reflecting different options within the format.
This may be useful in distinguishing variants of formats from each other.

* TODO: negation ^


Differences to PRONOM XML
-------------------------
droidSig produces DROID 6 compatible XML.  The XML produced may work with DROID 5,
but it will definitely not work with any version of DROID below 5.

* Redundant elements removed:
   <Shift>          This element specifies the search shift to use on encountering a byte value.
                    DROID 6 calculates these itself; these elements are ignored by DROID 6 if present.
   <DefaultShift>   This element specifies the default search shift to use for other bytes values.
                    Like <Shift>, DROID 6 calculates this value itself and the element is ignored if present.

* Redundant attributes removed:
   MinFragLength    This attribute of the <SubSequence> element is calculated by DROID 6 and is no longer required.

* Strip default attribute values [optional]
  PRONOM includes all attribute values, even those which are already at the default value.  By default, droidSig
  will do the same.  However, this creates much bigger XML, with lots of attribute values set to zero.  Using the
  -s switch, droidSig will not write out attribute values which are already at their default.  Currently, DROID 6
  will process this XML correctly, and it's smaller and easier to read.  There is no guarantee that future versions
  of DROID will be able to process XML with default values stripped out (but no reason to suspect this will change either).


Bibliography
------------
The syntax of DROID regular expressions, the XML format, and the algorithm PRONOM uses to split up
the expression into the XML is fully described in:

  "Automatic Format Identification using PRONOM and DROID" by Adrian Brown.

This can be obtained from the UK National Archives at:

  https://www.nationalarchives.gov.uk/aboutapps/fileformat/pdf/automatic_format_identification.pdf

