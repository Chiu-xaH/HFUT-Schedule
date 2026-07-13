package com.hfut.schedule.logic.util.sys.datetime

import org.junit.Assert.assertNotNull
import org.junit.Test

class DayTest {

    @Test
    fun universityPeriod_acceptsDateFormattedInput() {
        val result = getUniversityPeriod(
            startDate = "2024-09-01",
            endDate = "2028-06-30"
        )

        assertNotNull(result)
    }
}
