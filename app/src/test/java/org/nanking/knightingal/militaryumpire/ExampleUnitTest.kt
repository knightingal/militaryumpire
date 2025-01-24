package org.nanking.knightingal.militaryumpire

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun chequerTest() {
        val w1 = Chequer.valueOf("军长").weight
        val w2 = Chequer.valueOf("旅长").weight
        assertTrue(w2 > w1)
    }
}