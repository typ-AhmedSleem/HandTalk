package com.typ.handtalk.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

fun <T> Flow<T?>.filterNotNull(): Flow<T> = filter { it != null }.map { it!! }