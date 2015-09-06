package net.byteseek.utils.droidSig;

import net.byteseek.utils.droidSig.specs.*;

import java.util.Date;
import java.util.List;

/**
 * Created by matt on 06/09/15.
 */
public class XMLUtils {

    public static String toXML(List<ByteSequenceSpec> expressions, boolean stripDefaults) {
        String XML = "";
        for (ByteSequenceSpec byteSequenceSpec: expressions) {
            XML += XMLUtils.toXML(byteSequenceSpec, stripDefaults) + '\n';
        }
        return XML;
    }

    public static String toXML(InternalSignatureSpec signatureSpec,
                               FormatSpec            formatSpec, boolean stripDefaults) {
        return toXML(signatureSpec, stripDefaults) + "\n" + toXML(formatSpec, stripDefaults);
    }

    public static String toXML(ByteSequenceSpec spec, boolean stripDefaults) {
        StringBuilder builder = new StringBuilder(2048);
        builder.append("<ByteSequence Reference=\"").append(spec.anchor).append("\">");
        int subSequencePosition = 1;
        for (SubSequenceSpec subSequence : spec.subSequences) {
            builder.append(toXML(subSequence, subSequencePosition++, stripDefaults));
        }
        builder.append("</ByteSequence>");
        return builder.toString();
    }

    public static String toXML(InternalSignatureSpec spec, boolean stripDefaults) {
        String xml = "<InternalSignature ID=\"" + spec.sigId + "\">";
        for (ByteSequenceSpec sequenceSpec : spec.sequenceSpecs) {
            xml += toXML(sequenceSpec, stripDefaults);
        }
        xml += "</InternalSignature>";
        return xml;
    }

    public static String toXML(FormatSpec spec, boolean stripDefaults) {
        String baseXML =  "<FileFormat ID=\"" + spec.formatId + "\" Name=\"" + spec.formatName + "\" PUID=\"" + spec.puid + "\">";
        if (spec.sigId != null) {
            baseXML += "<InternalSignatureID>" + spec.sigId + "</InternalSignatureID>";
        }
        if (!spec.extension.isEmpty()) {
            baseXML += "<Extension>" + spec.extension + "</Extension>";
        }
        return baseXML + "</FileFormat";
    }

    public static String toXML(FragmentSpec spec, String elementName, boolean stripDefaults) {
        StringBuilder builder = new StringBuilder(2048);
        builder.append("<").append(elementName).append(' ');
        if (!stripDefaults || spec.maxFragOffset > 0) { // this is currently valid behaviour, but not strict
            builder.append("MaxOffset=\"").append(spec.maxFragOffset).append("\" ");
        }
        if (!stripDefaults || spec.minFragOffset > 0) { // this is currently valid behaviour, but not strict
            builder.append("MinOffset=\"").append(spec.minFragOffset).append("\" ");
        }
        builder.append("Position=\"").append(spec.position).append("\">");
        builder.append(Utils.escapeXml(spec.fragmentExpression));
        builder.append("</").append(elementName).append(">");
        return builder.toString();
    }

    public static String toXML(SubSequenceSpec spec, int position, boolean stripDefaults) {
        StringBuilder builder = new StringBuilder(2048);
        builder.append("<SubSequence Position=\"").append(position).append("\" ");
        if (!stripDefaults || spec.maxSeqOffset > 0) {
            builder.append("SubSeqMaxOffset=\"").append(spec.maxSeqOffset).append("\" ");
        }
        if (!stripDefaults || spec.minSeqOffset > 0) {
            builder.append("SubSeqMinOffset=\"").append(spec.minSeqOffset).append("\">");
        }
        builder.append("<Sequence>").append(Utils.escapeXml(spec.mainExpression)).append("</Sequence>");
        for (FragmentSpec fragment : spec.leftFragments) {
            builder.append(XMLUtils.toXML(fragment, "LeftFragment", stripDefaults));
        }
        for (FragmentSpec fragment : spec.rightFragments) {
            builder.append(XMLUtils.toXML(fragment, "RightFragment", stripDefaults));
        }
        builder.append("</SubSequence>");
        return builder.toString();
    }

    public static String toXML(SignatureFileSpec spec, boolean stripDefaults) {
        Date now = new Date();
        return  XML_HEADER +
                String.format(SIGNATURE_FILE_START, now, now, now, now, now, now) +
                SIGNATURE_COLLECTION_START +
                toXML(spec.internalSigSpec, stripDefaults) +
                SIGNATURE_COLLECTION_END +
                FORMAT_COLLECTION_START  +
                toXML(spec.formatSpec, stripDefaults) +
                FORMAT_COLLECTION_END +
                SIGNATURE_FILE_END;
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
