package com.imaec.mypay

import com.imaec.mypay.utils.Calculator
import com.imaec.mypay.utils.DateUtil
import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        Calculator.apply {
            this.pay = 3000000
            this.payDay = "25일"
            this.startTime = "오전 9시"
            this.endTime = "오후 6시"
        }

        val a = Calculator.getWorkDay()
        val b = Calculator.getWorkedDay()
        println("$a / $b")

        assertEquals(4, 2 + 2)
    }
}
