import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beriukhov.h3.H3.Companion.geoToH3
import com.beriukhov.h3.H3.Companion.vertices
import com.beriukhov.h3.LatLng
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidIH3Test {

    private val zeroLatLngRes4 = "084754a9ffffffff"
    private val boundaries = listOf(
        LatLng(lat = -0.34749776, lng = 0.04469065),
        LatLng(lat = -0.1490766, lng = 0.13959856),
        LatLng(lat = 0.0197745, lng = 0.04056896),
        LatLng(lat = -0.00969098, lng = -0.15269442),
        LatLng(lat = -0.20742123, lng = -0.247275),
        LatLng(lat = -0.37637541, lng = -0.14892008)
    )

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testGeoToH3() {
        assertEquals(zeroLatLngRes4, geoToH3(LatLng(0.0, 0.0), res = 4).toHexString())
    }

    @Test
    fun testVertices() {
        assertEquals(boundaries, vertices(zeroLatLngRes4))
    }
}
