/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.commons.utils;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * At the moment there is no StringUtil class in Java, but there is one in C# and we are testing
 */
@Category(UnitTest.class)
public class StringUtilTest extends ExtendedITextTest {

    private static final char SPLIT_PERIOD = '.';

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6457 fix different behavior of Pattern.split method)
    public void patternSplitTest01() {
        // Pattern.split in Java works differently compared to Regex.Split in C#
        // In C#, empty strings are possible at the beginning of the resultant array for non-capturing groups in
        // split regex
        // Thus, in C# we use a separate utility for splitting to align the implementation with Java
        // This test verifies that the resultant behavior is the same
        Pattern pattern = Pattern.compile("(?=[ab])");
        String source = "a01aa78ab89b";
        String[] expected = new String[] {"a01", "a", "a78", "a", "b89", "b"};
        String[] result = pattern.split(source);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void patternSplitTest02() {
        Pattern pattern = Pattern.compile("(?=[ab])");
        String source = "";
        String[] expected = new String[] {""};
        String[] result = pattern.split(source);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6457 fix different behavior of Pattern.split method)
    public void stringSplitTest01() {
        String source = "a01aa78ab89b";
        String[] expected = new String[] {"a01", "a", "a78", "a", "b89", "b"};
        String[] result = source.split("(?=[ab])");
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void stringSplitTest02() {
        String source = "";
        String[] expected = new String[] {""};
        String[] result = source.split("(?=[ab])");
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void splitKeepEmptyParts01() {
        String source = "";
        String[] expected = new String[]{
                ""
        };
        String[] result = StringSplitUtil.splitKeepTrailingWhiteSpace(source, SPLIT_PERIOD);
        Assert.assertArrayEquals(source.split(String.valueOf(SPLIT_PERIOD)), result);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void splitKeepEmptyParts02() {
        String source = null;
        Assert.assertThrows(Exception.class,
                () -> StringSplitUtil.splitKeepTrailingWhiteSpace(source, SPLIT_PERIOD));
    }

    @Test
    public void splitKeepEmptyParts03() {
        String source = "test.test1";
        String[] expected = new String[] {"test", "test1"};
        String[] result = StringSplitUtil.splitKeepTrailingWhiteSpace(source, SPLIT_PERIOD);
        Assert.assertArrayEquals(expected, result);
    }


    @Test
    public void splitKeepEmptyParts04() {
        String source = "test..test1";
        String[] expected = new String[] {"test", "", "test1"};
        String[] result = StringSplitUtil.splitKeepTrailingWhiteSpace(source, SPLIT_PERIOD);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void splitKeepEmptyParts05() {
        String source = "test...test1";
        String[] expected = new String[] {"test", "", "", "test1"};
        String[] result = StringSplitUtil.splitKeepTrailingWhiteSpace(source, SPLIT_PERIOD);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void splitKeepEmptyParts06() {
        String source = ".test1";
        String[] expected = new String[] {"", "test1"};
        String[] result = StringSplitUtil.splitKeepTrailingWhiteSpace(source, SPLIT_PERIOD);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void splitKeepEmptyPartsDifferentBehaviour01() {
        String source = "test.";
        String[] expected = new String[] {"test", ""};
        String[] result = StringSplitUtil.splitKeepTrailingWhiteSpace(source, SPLIT_PERIOD);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void splitKeepEmptyPartsDifferentBehaviour02() {
        String source = "test..";
        String[] expected = new String[] {"test", "", ""};
        String[] result = StringSplitUtil.splitKeepTrailingWhiteSpace(source, SPLIT_PERIOD);
        Assert.assertArrayEquals(expected, result);
    }

}
