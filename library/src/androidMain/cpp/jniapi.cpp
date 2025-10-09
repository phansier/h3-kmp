#include <jni.h>
#include <string>
#include "h3api.h"

static jclass com_beriukhov_h3_H3Exception;
static jmethodID com_beriukhov_h3_H3Exception_init;
static jclass java_lang_OutOfMemoryError;
static jmethodID java_lang_OutOfMemoryError_init;
/**
* Triggers an H3Exception
*/
void ThrowH3Exception(JNIEnv *env, H3Error err) {
    auto h3eInstance = (jthrowable)(env->NewObject(
            com_beriukhov_h3_H3Exception,
            com_beriukhov_h3_H3Exception_init, err));

    if (h3eInstance != NULL) {
        env->Throw(h3eInstance);
        env->DeleteLocalRef(h3eInstance);
    }
}

/**
 * Triggers an OutOfMemoryError.
 *
 * Calling function should return the Java control immediately after calling
 * this.
 */
void ThrowOutOfMemoryError(JNIEnv *env) {
    // Alternately, we could call the JNI function FatalError(JNIEnv *env, const
    // char *msg)
    jthrowable oomeInstance = (jthrowable)(env->NewObject(
            java_lang_OutOfMemoryError, java_lang_OutOfMemoryError_init));

    if (oomeInstance != NULL) {
        env->ExceptionClear();
        env->Throw(oomeInstance);
        env->DeleteLocalRef(oomeInstance);
    }
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_beriukhov_h3_Jni_latLngToCell(JNIEnv *env, jobject thiz, jdouble lat, jdouble lng,
                                       jint res) {
    LatLng geo = {lat, lng};
    H3Index out;
    H3Error err = latLngToCell(&geo, res, &out);
    if (err) {
        ThrowH3Exception(env, err);
    }
    return out;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_beriukhov_h3_Jni_cellToBoundary(JNIEnv *env, jobject thiz, jlong h3, jdoubleArray verts) {
    CellBoundary boundary;
    H3Error err = cellToBoundary(h3, &boundary);
    if (err) {
        ThrowH3Exception(env, err);
        return -1;
    }

    jsize sz = env->GetArrayLength(verts);
    jdouble *vertsElements = env->GetDoubleArrayElements(verts, 0);

    if (vertsElements != NULL) {
        // if sz is too small, we will fail to write all the elements
        for (jsize i = 0; i < sz && i < boundary.numVerts * 2; i += 2) {
            vertsElements[i] = boundary.verts[i / 2].lat;
            vertsElements[i + 1] = boundary.verts[i / 2].lng;
        }

        env->ReleaseDoubleArrayElements(verts, vertsElements, 0);

        return boundary.numVerts;
    } else {
        ThrowOutOfMemoryError(env);
        return -1;
    }
}