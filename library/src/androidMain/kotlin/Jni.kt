package com.beriukhov.h3

class Jni {
    init {
        System.loadLibrary("h3kmp")
    }

    /**
     * Find the H3 index of the resolution <code>res</code> cell containing the lat/lon (in degrees)
     *
     * @param lat Latitude in degrees.
     * @param lng Longitude in degrees.
     * @param res Resolution, 0 &lt;= res &lt;= 15
     * @return The H3 index.
     * @throws IllegalArgumentException latitude, longitude, or resolution are out of range.
     */
    external fun latLngToCell(lat: Double, lng: Double, res: Int): Long

    /** Find the cell boundary in latitude, longitude (degrees) coordinates for the cell */
    external fun cellToBoundary(h3: Long, verts: DoubleArray): Int

}


