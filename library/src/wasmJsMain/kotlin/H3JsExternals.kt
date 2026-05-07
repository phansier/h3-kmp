@file:OptIn(ExperimentalWasmJsInterop::class)
@file:JsModule("h3-js")

package com.beriukhov.h3

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsArray
import kotlin.js.JsNumber

internal external fun latLngToCell(lat: Double, lng: Double, res: Int): String

internal external fun cellToBoundary(h3Index: String): JsArray<JsArray<JsNumber>>
