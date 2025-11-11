package com.khouss.UsersMicroservice.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberUtilsTest {

    @Test
    void normalize_variants() {
        assertEquals("+221770000000", PhoneNumberUtils.normalizeToSenegalFormat("+221770000000"));
        assertEquals("+221770000000", PhoneNumberUtils.normalizeToSenegalFormat("0770000000"));
        assertEquals("+221770000000", PhoneNumberUtils.normalizeToSenegalFormat("770000000"));
        assertEquals("+221770000000", PhoneNumberUtils.normalizeToSenegalFormat("(77) 000-0000"));
        assertEquals("+221700000000", PhoneNumberUtils.normalizeToSenegalFormat("00221700000000"));
        assertNull(PhoneNumberUtils.normalizeToSenegalFormat("12345"));
    }
}

