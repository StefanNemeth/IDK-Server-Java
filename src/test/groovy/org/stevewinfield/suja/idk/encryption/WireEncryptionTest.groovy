package org.stevewinfield.suja.idk.encryption

import org.junit.Test


class WireEncryptionTest {
    @Test
    void encode1IsI() {
        assert "I" == new String(WireEncryption.encode(1))
    }

    @Test
    void encode8123732() {
        assert "hUuoG" == new String(WireEncryption.encode(8123732))
    }

    @Test
    void decodeIIs1() {
        assert 1 == WireEncryption.decode("I")
    }

    @Test
    void decodePAIs4() {
        assert 4 == WireEncryption.decode("PA")
    }
}
