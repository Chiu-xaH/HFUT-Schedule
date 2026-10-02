package com.xah.common.logic.util

const val EMPTY_STRING = ""

fun String.isEmptyOrBlank() = this.isEmpty() || this.isBlank()
fun String?.isNullOrEmptyOrBlank() = this?.isEmptyOrBlank() ?: true

fun String.isNotEmptyAndBlank() = !this.isEmptyOrBlank()
fun String?.isNotNullAndEmptyAndBlank() = !this.isNullOrEmptyOrBlank()

fun String.remove(string: String,ignoreCase: Boolean = false) = this.replace(string,EMPTY_STRING,ignoreCase)
fun String.remove(regex: Regex) = this.replace(regex,EMPTY_STRING)
