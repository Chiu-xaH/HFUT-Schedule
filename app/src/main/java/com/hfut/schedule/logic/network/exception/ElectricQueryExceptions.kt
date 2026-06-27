package com.hfut.schedule.logic.network.exception

import java.io.IOException

/**
 * Thrown when the electric fee API returns HTTP 200 but the response body is empty or blank.
 */
class EmptyElectricResponseException :
    IOException("电费接口响应体为空")

/**
 * Thrown when reading the electric fee API response body fails.
 */
class ElectricResponseReadException(
    cause: Throwable
) : IOException("读取电费接口响应体失败", cause)
