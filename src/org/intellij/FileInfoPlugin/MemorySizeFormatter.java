package org.intellij.FileInfoPlugin;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class MemorySizeFormatter extends Format {

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("###,###,###,##0");
    public static final String SUFFIX_BYTE = "byte";
    public static final String SUFFIX_BYTES = "bytes";
    public static final String SUFFIX_KILOBYTE = "KB";

    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public String format(long fileSize) {
        if (fileSize == 1) return prettifyNumber(fileSize, SUFFIX_BYTE);
        if (fileSize < 1024) {
            return prettifyNumber(fileSize, SUFFIX_BYTES);
        }
        fileSize = fileSize >> 10;
        return prettifyNumber(fileSize, SUFFIX_KILOBYTE);
    }

    private String prettifyNumber(long number, String suffix) {
        return NUMBER_FORMAT.format(number) + " " + suffix;
    }

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Number == false) throw new IllegalArgumentException("Can only format numeric objects");
        if (toAppendTo == null) throw new NullPointerException("toAppendTo cannot be null");
        if (pos == null) throw new NullPointerException("pos cannot be null");
        Number size = (Number) obj;
        String formattedSize = format(size.longValue());
        toAppendTo.append(formattedSize);
        return toAppendTo;
    }

}
