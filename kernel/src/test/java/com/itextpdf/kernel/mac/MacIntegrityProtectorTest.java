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
package com.itextpdf.kernel.mac;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.util.EnumUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.mac.MacProperties.KeyWrappingAlgorithm;
import com.itextpdf.kernel.mac.MacProperties.MacAlgorithm;
import com.itextpdf.kernel.mac.MacProperties.MacDigestAlgorithm;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class MacIntegrityProtectorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/mac/MacIntegrityProtectorTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/mac/MacIntegrityProtectorTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/kernel/mac/MacIntegrityProtectorTest/certs/";
    private static final byte[] PASSWORD = "123".getBytes();
    private static final String PROVIDER_NAME = BouncyCastleFactoryCreator.getFactory().getProviderName();

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void standaloneMacStandardEncryptionTest() throws IOException, InterruptedException {
        String fileName = "standaloneMacStandardEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_256, MacAlgorithm.HMAC_WITH_SHA_256,
                KeyWrappingAlgorithm.AES_256_NO_PADD);
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256, macProperties);

        if ("BC".equals(PROVIDER_NAME)) {
            try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
            }
            Assertions.assertNull(new CompareTool().compareByContent(
                    outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
        } else {
            String exceptionMessage = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
            }).getMessage();
            Assertions.assertEquals("HKDF algorithm is not supported in bouncy-castle FIPS mode.", exceptionMessage);
        }
    }

    @Test
    public void standaloneMacUnwritableStreamTest() throws IOException {
        String fileName = "standaloneMacStandardEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_256, MacAlgorithm.HMAC_WITH_SHA_256,
                KeyWrappingAlgorithm.AES_256_NO_PADD);
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256, macProperties);
        ByteArrayOutputStream unwritableStream = new ByteArrayOutputStream() {
            @Override
            public void write(byte[] b, int off, int len) {
                throw new RuntimeException("expected");
            }
        };

        if ("BC".equals(PROVIDER_NAME)) {
            String exceptionMessage = Assertions.assertThrows(RuntimeException.class, () -> {
                try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(unwritableStream, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
            }).getMessage();
            Assertions.assertEquals("expected", exceptionMessage);
        } else {
            String exceptionMessage = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
            }).getMessage();
            Assertions.assertEquals("HKDF algorithm is not supported in bouncy-castle FIPS mode.", exceptionMessage);
        }
        unwritableStream.close();
    }

    @Test
    public void standaloneMacWithAllHashAlgorithmsTest() throws IOException, InterruptedException {
        for (int i = 0; i < EnumUtil.getAllValuesOfEnum(MacDigestAlgorithm.class).size(); i++) {
            String fileName = "standaloneMacWithAllHashAlgorithmsTest" + (i + 1) + ".pdf";
            String outputFileName = DESTINATION_FOLDER + fileName;
            String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

            MacProperties macProperties = new MacProperties(EnumUtil.getAllValuesOfEnum(MacDigestAlgorithm.class).get(i),
                    MacAlgorithm.HMAC_WITH_SHA_256, KeyWrappingAlgorithm.AES_256_NO_PADD);
            WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                    .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256,
                            macProperties);

            if ("BC".equals(PROVIDER_NAME)) {
                try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
                Assertions.assertNull(new CompareTool().compareByContent(
                        outputFileName, cmpFileName, DESTINATION_FOLDER, "diff", PASSWORD, PASSWORD));
            } else {
                String exceptionMessage = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                    try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                        pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                    }
                }).getMessage();
                Assertions.assertEquals("HKDF algorithm is not supported in bouncy-castle FIPS mode.", exceptionMessage);
            }
        }
    }

    @Test
    public void standaloneMacPdfVersionNotSetTest() {
        String fileName = "standaloneMacPdfVersionNotSetTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_256, MacAlgorithm.HMAC_WITH_SHA_256,
                KeyWrappingAlgorithm.AES_256_NO_PADD);
        WriterProperties writerProperties = new WriterProperties()
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_256, macProperties);

        if ("BC".equals(PROVIDER_NAME)) {
            String exceptionMessage = Assertions.assertThrows(PdfException.class, () -> {
                try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
            }).getMessage();
            Assertions.assertEquals("MAC integrity protection is only supported for PDF 2.0 or higher.", exceptionMessage);
        } else {
            String exceptionMessage = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
            }).getMessage();
            Assertions.assertEquals("HKDF algorithm is not supported in bouncy-castle FIPS mode.", exceptionMessage);
        }
    }

    @Test
    public void standaloneMacOldEncryptionAlgorithmTest() {
        String fileName = "standaloneMacOldEncryptionAlgorithmTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_256, MacAlgorithm.HMAC_WITH_SHA_256,
                KeyWrappingAlgorithm.AES_256_NO_PADD);
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                .setStandardEncryption(PASSWORD, PASSWORD, 0, EncryptionConstants.ENCRYPTION_AES_128, macProperties);

        if ("BC".equals(PROVIDER_NAME)) {
            String exceptionMessage = Assertions.assertThrows(PdfException.class, () -> {
                try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
            }).getMessage();
            Assertions.assertEquals("MAC integrity protection is only supported for encryption algorithms of version 5 or higher.", exceptionMessage);
        } else {
            String exceptionMessage = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
            }).getMessage();
            Assertions.assertEquals("HKDF algorithm is not supported in bouncy-castle FIPS mode.", exceptionMessage);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = VersionConforming.DEPRECATED_AES256_REVISION), ignore = true)
    public void standaloneMacPublicKeyEncryptionTest() throws Exception {
        String fileName = "standaloneMacPublicKeyEncryptionTest.pdf";
        String outputFileName = DESTINATION_FOLDER + fileName;
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName;

        MacProperties macProperties = new MacProperties(MacDigestAlgorithm.SHA_256, MacAlgorithm.HMAC_WITH_SHA_256,
                KeyWrappingAlgorithm.AES_256_NO_PADD);
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(CERTS_SRC + "SHA256withRSA.cer"));

        if ("BC".equals(PROVIDER_NAME)) {
            WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                    .setPublicKeyEncryption(new Certificate[] {certificate}, new int[] {-1}, EncryptionConstants.ENCRYPTION_AES_256, macProperties);
            try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputFileName, writerProperties))) {
                pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
            }
            PrivateKey privateKey = getPrivateKey("SHA256withRSA.key");
            CompareTool compareTool = new CompareTool();
            compareTool.getCmpReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);
            compareTool.getOutReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);

            Assertions.assertNull(compareTool.compareByContent(outputFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
        } else {
            Assertions.assertThrows(Exception.class, () -> {
                WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)
                        .setPublicKeyEncryption(new Certificate[] {certificate}, new int[] {-1}, EncryptionConstants.ENCRYPTION_AES_256, macProperties);
                try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outputFileName, writerProperties))) {
                    pdfDoc.addNewPage().addAnnotation(new PdfTextAnnotation(new Rectangle(100, 100, 100, 100)));
                }
            });
        }
    }

    public PrivateKey getPrivateKey(String keyName) throws IOException, AbstractPKCSException, AbstractOperatorCreationException {
        return PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(CERTS_SRC + keyName), "testpassphrase".toCharArray());
    }
}
