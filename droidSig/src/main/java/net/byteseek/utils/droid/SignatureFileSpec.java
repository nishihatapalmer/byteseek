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

import java.util.Date;

/**
 * Created by matt on 21/08/15.
 */
public class SignatureFileSpec {

    private final InternalSignatureSpec internalSigSpec;
    private final FormatSpec formatSpec;

    public SignatureFileSpec(InternalSignatureSpec internalSigSpec, FormatSpec formatSpec) {
        this.internalSigSpec = internalSigSpec;
        this.formatSpec      = formatSpec;
    }

    public String toDROIDXML(boolean stripDefaults) {
        Date now = new Date();
        return  XML_HEADER +
                String.format(SIGNATURE_FILE_START, now, now, now, now, now, now) +
                SIGNATURE_COLLECTION_START +
                internalSigSpec.toDROIDXML(stripDefaults) +
                SIGNATURE_COLLECTION_END +
                FORMAT_COLLECTION_START  +
                formatSpec.toDROIDXML(stripDefaults) +
                FORMAT_COLLECTION_END +
                SIGNATURE_FILE_END;    }

    public String toDROIDXMLFragments(boolean stripDefaults) {
        return internalSigSpec.toDROIDXML(stripDefaults) + "\n" +
               formatSpec.toDROIDXML(stripDefaults);
    }


    private static final String XML_HEADER                 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String SIGNATURE_FILE_START       = "<FFSignatureFile DateCreated=\"%tY-%tm-%tdT%tH:%tM:%tS\" Version=\"1\"" +
            " xmlns=\"http://www.nationalarchives.gov.uk/pronom/SignatureFile\">";
    private static final String SIGNATURE_COLLECTION_START = "<InternalSignatureCollection>";
    private static final String SIGNATURE_COLLECTION_END   = "</InternalSignatureCollection>";
    private static final String FORMAT_COLLECTION_START    = "<FileFormatCollection>";
    private static final String FORMAT_COLLECTION_END      = "</FileFormatCollection>";
    private static final String SIGNATURE_FILE_END         = "</FFSignatureFile>";

}
