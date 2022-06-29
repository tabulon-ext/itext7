package com.itextpdf.bouncycastlefips.asn1;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Enumerated;

import org.bouncycastle.asn1.ASN1Enumerated;

public class ASN1EnumeratedBCFips extends ASN1PrimitiveBCFips implements IASN1Enumerated {
    public ASN1EnumeratedBCFips(int i) {
        super(new ASN1Enumerated(i));
    }
}