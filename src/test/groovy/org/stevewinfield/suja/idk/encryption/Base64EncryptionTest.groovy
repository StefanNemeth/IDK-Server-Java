package org.stevewinfield.suja.idk.encryption

import org.junit.Test


class Base64EncryptionTest {
    @Test
    void decodeDoubleAtIs0() {
        assert 0 == Base64Encryption.decode("@@")
    }

    @Test
    void decodeAtAndAIs1() {
        assert 1 == Base64Encryption.decode("@A")
    }

    @Test
    void encode0IsDoubleAt() {
        assert "@@" == new String(Base64Encryption.encode(0))
    }

    @Test
    void encode1IsAtAndA() {
        assert "@A" == new String(Base64Encryption.encode(1))
    }
}
