package com.khouss.UsersMicroservice.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QrCodeServiceTest {

    @Test
    public void testGenerateQrCodePng() throws Exception {
        QrCodeService service = new QrCodeService();
        byte[] png = service.generateQrCodePng("+221774730039", 200, 200);
        assertNotNull(png);
        assertTrue(png.length > 0);
        // PNG signature
        assertEquals((byte)0x89, png[0]);
        assertEquals((byte)0x50, png[1]);
        assertEquals((byte)0x4E, png[2]);
        assertEquals((byte)0x47, png[3]);
    }
}

