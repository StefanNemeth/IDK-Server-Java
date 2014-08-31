package org.stevewinfield.suja.idk

import org.junit.Test

class InputFilterTest {
    @Test
    void replaceTabBySpace() {
        assert InputFilter.filterString("Hello,${(char) 9}how are you?") == "Hello, how are you?"
    }

    @Test
    void replaceSOHBySpace() {
        assert InputFilter.filterString("Hello,${(char) 1}how are you?") == "Hello, how are you?"
    }

    @Test
    void replaceSTXBySpace() {
        assert InputFilter.filterString("Hello,${(char) 2}how are you?") == "Hello, how are you?"
    }

    @Test
    void replaceETXBySpace() {
        assert InputFilter.filterString("Hello,${(char) 3}how are you?") == "Hello, how are you?"
    }

    @Test
    void noLineBreakReplace() {
        assert InputFilter.filterString("Hello\nHello\r") == "Hello\nHello\r"
    }

    @Test
    void lineBreakReplace() {
        assert InputFilter.filterString("Hello\nHello\r", true) == "Hello Hello "
    }
}
