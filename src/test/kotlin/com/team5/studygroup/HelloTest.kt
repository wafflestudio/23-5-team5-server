package com.team5.studygroup

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HelloTest {
    @Test
    fun helloWorldTest() {
        val expected = "Hello World"
        val actual = "Hello World"

        assertEquals(expected, actual)
    }
}
