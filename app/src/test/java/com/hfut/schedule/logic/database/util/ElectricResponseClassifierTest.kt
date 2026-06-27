package com.hfut.schedule.logic.database.util

import com.hfut.schedule.logic.network.util.ElectricFeeResponseClassifier
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [ElectricFeeResponseClassifier] which classifies
 * HuiXin electric fee API responses as business success or failure.
 * All tests directly call the production classifier.
 */
class ElectricResponseClassifierTest {

    // === Should be SUCCESS ===

    @Test
    fun `msg success with map showData`() {
        assertTrue(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"msg":"success","map":{"showData":{"a":"1"}}}"""
            )
        )
    }

    @Test
    fun `msg Success case insensitive`() {
        assertTrue(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"msg":"Success","map":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `success true boolean`() {
        assertTrue(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"success":true}"""
            )
        )
    }

    @Test
    fun `map with showData only`() {
        assertTrue(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"map":{"showData":{"a":"1"}}}"""
            )
        )
    }

    // === Should be FAILURE ===

    @Test
    fun `success false overrides map showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"success":false,"map":{"showData":{"a":"1"}}}"""
            )
        )
    }

    @Test
    fun `msg failed overrides map showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"map":{"showData":{"a":"1"}},"msg":"failed"}"""
            )
        )
    }

    @Test
    fun `showData outside map is not success`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"map":{},"other":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `message unsuccessful is failure`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"message":"unsuccessful"}"""
            )
        )
    }

    @Test
    fun `success text inside json string is failure`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"message":"response contains \"success\":true"}"""
            )
        )
    }

    @Test
    fun `non json text containing msg success is failure`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """server output: "msg":"success" end"""
            )
        )
    }

    @Test
    fun `null body is failure`() {
        assertFalse(ElectricFeeResponseClassifier.isBusinessSuccess(null))
    }

    @Test
    fun `empty string is failure`() {
        assertFalse(ElectricFeeResponseClassifier.isBusinessSuccess(""))
    }

    @Test
    fun `blank string is failure`() {
        assertFalse(ElectricFeeResponseClassifier.isBusinessSuccess("   "))
    }

    @Test
    fun `json array root is failure`() {
        assertFalse(ElectricFeeResponseClassifier.isBusinessSuccess("[]"))
    }

    @Test
    fun `json number root is failure`() {
        assertFalse(ElectricFeeResponseClassifier.isBusinessSuccess("123"))
    }

    @Test
    fun `json boolean root is failure`() {
        assertFalse(ElectricFeeResponseClassifier.isBusinessSuccess("true"))
    }

    @Test
    fun `json with map but no showData is failure`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"map":{"data":{}}}"""
            )
        )
    }

    @Test
    fun `non json text is failure`() {
        assertFalse(ElectricFeeResponseClassifier.isBusinessSuccess("this is not json"))
    }

    @Test
    fun `json without success field and without map is failure`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"data":"some value"}"""
            )
        )
    }

    // === Field type boundary tests ===

    @Test
    fun `numeric success field is failure even with showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"success":1,"map":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `numeric msg field is failure even with showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"msg":123,"map":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `null success field is failure even with showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"success":null,"map":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `null msg field is failure even with showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"msg":null,"map":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `string success field is failure even with showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"success":"true","map":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `boolean msg field is failure even with showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"msg":true,"map":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `object success field is failure even with showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"success":{},"map":{"showData":{}}}"""
            )
        )
    }

    @Test
    fun `array msg field is failure even with showData`() {
        assertFalse(
            ElectricFeeResponseClassifier.isBusinessSuccess(
                """{"msg":[],"map":{"showData":{}}}"""
            )
        )
    }
}
