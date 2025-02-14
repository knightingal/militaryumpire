package org.nanking.knightingal.militaryumpire

import kotlinx.serialization.json.Json
import org.junit.Test

import org.junit.Assert.*
import org.nanking.knightingal.militaryumpire.bean.Chequer
import org.nanking.knightingal.militaryumpire.bean.OcrResponse

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

    @Test
    fun parseOcrResponse() {
        val obj = Json.decodeFromString<OcrResponse>("""{"text":"军长", "trust_rate": 0.9}""")
        println(obj)
        val objArray = Json.decodeFromString<List<OcrResponse>>("""[{"text":"军长", "trust_rate": 0.9}]""")
        println(objArray)
    }
}