package org.apache.lucene.document;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Lucene" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact [EMAIL PROTECTED]
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Lucene", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/**
 * Provides support for converting longs to Strings, and back again.
 * The strings are structured so that lexicographic sorting order is preserved.
 *
 * <p>
 * That is, if l1 is less than l2 for any two longs l1 and l2, then
 * LongField.longToString(l1) is lexicographically less than
 * LongField.longToString(l2). (Similarly for "greater than" and "equals".)
 *
 * <p>
 * This class handles <b>all</b> long values (unlike [EMAIL PROTECTED] org.apache.lucene.document.DateField}).
 *
 * @author Matt Quail (spud at madbean dot com)
 */
public class LongField {
    private static final int RADIX = 36;

    private static final char NEGATIVE_PREFIX = '-';

    // NB: NEGATIVE_PREFIX must be < POSITIVE_PREFIX
    private static final char POSITIVE_PREFIX = '0';

    //NB: this must be less than
    /**
     * Equivalent to longToString(Long.MIN_VALUE)
     */
    public static final String MIN_STRING_VALUE = NEGATIVE_PREFIX + "0000000000000";
    /**
     * Equivalent to longToString(Long.MAX_VALUE)
     */
    public static final String MAX_STRING_VALUE = POSITIVE_PREFIX + "1y2p0ij32e8e7";

    /**
     * the length of (all) strings returned by [EMAIL PROTECTED] #longToString}
     */
    public static final int STR_SIZE = MIN_STRING_VALUE.length();


    /**
     * Converts a long to a String suitable for indexing.
     */
    public static String longToString(long l) {

        if (l == Long.MIN_VALUE) {
            // special case, because long is not symetric around zero
            return MIN_STRING_VALUE;
        }

        StringBuffer buf = new StringBuffer(STR_SIZE);

        if (l < 0) {
            buf.append(NEGATIVE_PREFIX);
            l = Long.MAX_VALUE + l + 1;
        } else {
            buf.append(POSITIVE_PREFIX);
        }
        String num = Long.toString(l, RADIX);

        int padLen = STR_SIZE - num.length() - buf.length();
        while (padLen-- > 0) {
            buf.append('0');
        }
        buf.append(num);

        return buf.toString();
    }

    /**
     * Converts a String that was returned by [EMAIL PROTECTED] #longToString} back to
     * a long.
     *
     * @throws IllegalArgumentException if the input is null
     * @throws NumberFormatException if the input does not parse (it was not a String
     *   returned by longToString()).
     */
    public static long stringToLong(String str) {
        if (str == null) {
            throw new IllegalArgumentException("string cannot be null");
        }
        if (str.length() != STR_SIZE) {
            throw new NumberFormatException("string is the wrong size");
        }

        if (str.equals(MIN_STRING_VALUE)) {
            return Long.MIN_VALUE;
        }

        char prefix = str.charAt(0);
        long l = Long.parseLong(str.substring(1), RADIX);

        if (prefix == POSITIVE_PREFIX) {
            // nop
        } else if (prefix == NEGATIVE_PREFIX) {
            l = l - Long.MAX_VALUE - 1;
        } else {
            throw new NumberFormatException("string does not begin with the correct prefix");
        }

        return l;
    }
}