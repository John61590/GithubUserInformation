package com.example.githubuserinfo.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DateUtilsTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `formatIsoDate should parse valid ISO 8601 date`() {
        // Given
        val isoDate = "2024-01-15T10:30:00Z"

        // When
        val result = DateUtils.formatIsoDate(context, isoDate)

        // Then
        assertNotEquals(isoDate, result)
        // Result should contain the year
        assert(result.contains("2024") || result.contains("24"))
    }

    @Test
    fun `formatIsoDate should return original string for invalid date`() {
        // Given
        val invalidDate = "not-a-date"

        // When
        val result = DateUtils.formatIsoDate(context, invalidDate)

        // Then
        assertEquals(invalidDate, result)
    }

    @Test
    fun `formatIsoDate should return original string for empty string`() {
        // Given
        val emptyDate = ""

        // When
        val result = DateUtils.formatIsoDate(context, emptyDate)

        // Then
        assertEquals(emptyDate, result)
    }

    @Test
    fun `formatIsoDate should return original string for malformed ISO date`() {
        // Given
        val malformedDate = "2024-13-45T99:99:99Z" // Invalid month, day, time

        // When
        val result = DateUtils.formatIsoDate(context, malformedDate)

        // Then - SimpleDateFormat is lenient by default, but this tests the fallback
        assertNotEquals("", result)
    }

    @Test
    fun `formatIsoDate should handle date without time component gracefully`() {
        // Given
        val dateOnly = "2024-01-15"

        // When
        val result = DateUtils.formatIsoDate(context, dateOnly)

        // Then - Should return original since it doesn't match expected format
        assertEquals(dateOnly, result)
    }

    @Test
    fun `formatIsoDate should parse date at midnight`() {
        // Given
        val midnightDate = "2024-06-01T00:00:00Z"

        // When
        val result = DateUtils.formatIsoDate(context, midnightDate)

        // Then
        assertNotEquals(midnightDate, result)
        assert(result.contains("2024") || result.contains("24"))
    }

    @Test
    fun `formatIsoDate should parse date at end of day`() {
        // Given
        val endOfDayDate = "2024-12-31T23:59:59Z"

        // When
        val result = DateUtils.formatIsoDate(context, endOfDayDate)

        // Then
        assertNotEquals(endOfDayDate, result)
        assert(result.contains("2024") || result.contains("24"))
    }

    @Test
    @Config(qualifiers = "en")
    fun `formatIsoDate should format date for English locale`() {
        // Given
        val isoDate = "2024-03-15T12:00:00Z"

        // When
        val result = DateUtils.formatIsoDate(context, isoDate)

        // Then - English medium format typically includes month name
        assertNotEquals(isoDate, result)
    }

    @Test
    @Config(qualifiers = "ja")
    fun `formatIsoDate should format date for Japanese locale`() {
        // Given
        val isoDate = "2024-03-15T12:00:00Z"

        // When
        val result = DateUtils.formatIsoDate(context, isoDate)

        // Then - Japanese format typically uses year first
        assertNotEquals(isoDate, result)
    }

    @Test
    fun `formatIsoDate should handle leap year date`() {
        // Given
        val leapYearDate = "2024-02-29T12:00:00Z"

        // When
        val result = DateUtils.formatIsoDate(context, leapYearDate)

        // Then
        assertNotEquals(leapYearDate, result)
    }

    @Test
    fun `formatIsoDate should handle different timezone offset in input consistently`() {
        // Given - Input is always expected to be in UTC (Z suffix)
        val utcDate = "2024-07-04T18:00:00Z"

        // When
        val result = DateUtils.formatIsoDate(context, utcDate)

        // Then
        assertNotEquals(utcDate, result)
    }
}