package com.beriukhov.h3

/**
 * An exception from the H3 core library.
 *
 *
 * The error code contained in an H3Exception comes from the H3 core library. The H3
 * documentation contains a [table of error codes](https://h3geo.org/docs/library/errors/#table-of-error-codes).
 */
class H3Exception(val code: Int) : RuntimeException(
    codeToMessage(
        code
    )
) {
    companion object {
        fun codeToMessage(code: Int): String {
            when (code) {
                0 -> return "Success"
                1 -> return "The operation failed but a more specific error is not available"
                2 -> return "Argument was outside of acceptable range"
                3 -> return "Latitude or longitude arguments were outside of acceptable range"
                4 -> return "Resolution argument was outside of acceptable range"
                5 -> return "Cell argument was not valid"
                6 -> return "Directed edge argument was not valid"
                7 -> return "Undirected edge argument was not valid"
                8 -> return "Vertex argument was not valid"
                9 -> return "Pentagon distortion was encountered"
                10 -> return "Duplicate input"
                11 -> return "Cell arguments were not neighbors"
                12 -> return "Cell arguments had incompatible resolutions"
                13 -> return "Memory allocation failed"
                14 -> return "Bounds of provided memory were insufficient"
                15 -> return "Mode or flags argument was not valid"
                else -> return "Unknown error"
            }
        }
    }
}