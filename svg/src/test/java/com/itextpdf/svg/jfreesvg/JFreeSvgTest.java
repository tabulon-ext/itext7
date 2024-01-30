/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.jfreesvg;

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;


@Category(IntegrationTest.class)
public class JFreeSvgTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/JFreeSvgTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/JFreeSvgTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void usingJFreeSvgFromStringTest() throws IOException, InterruptedException {
        //TODO: update cmp-file when DEVSIX-2246 will be fixed
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleFromStringTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void usingJFreeSvgFromFileTest() throws IOException, InterruptedException {
        //TODO: update cmp-file when DEVSIX-2246 will be fixed
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleFromFileTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void pieChartByJFreeSvgTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgPieChartFromFileTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    //TODO: update cmp file when DEVSIX-4043 will be fixed
    public void barChartByJFreeSvgFromStringTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgBarChartFromStringTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    //TODO: update cmp file when DEVSIX-4043 will be fixed
    public void barChartByJFreeSvgFromFileTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgBarChartFromFileTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void lineChartByJFreeSvgFromStringTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgLineChartFromStringTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void lineChartByJFreeSvgFromFileTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgLineChartFromFileTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG
    @Test
    public void xyChartByJFreeSvgFromStringTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgXYChartFromStringTest");
    }

    //Do not make changes in svg file because it was generated by JFreeSVG

    @Test
    public void xyChartByJFreeSvgFromFileTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "usingJFreeSvgXYChartFromFileTest");
    }
}
