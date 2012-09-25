#include <stdlib.h>

#include "cbir.h"
#include "io.h"
#include "java_cbir.h"
#include <values.h>

JNIEXPORT jfloatArray JNICALL Java_cbir_kernels_c_SPCA_spca(JNIEnv *env,
		jclass jcl, jfloatArray inputImage, jint numLines, jint numSamples,
		jint numBands, jint linesSamples, jint numPrincipalComponents,
		jboolean generate, jstring randomVectorFile, jboolean fixedIterations, jint numIterations) {

	const char* random_vector_file = NULL;
	if (randomVectorFile != NULL) {
		random_vector_file
				= (*env)->GetStringUTFChars(env, randomVectorFile, 0);
	}

	// create array for *h_result
	jfloatArray result = (*env)->NewFloatArray(env, linesSamples * numBands);
	// access arrays
	jfloat *inputArray = (*env)->GetFloatArrayElements(env, inputImage, 0);
	jfloat *resultArray = (*env)->GetFloatArrayElements(env, result, 0);

	int res;

	res = spca(inputArray, numLines, numSamples, numBands, linesSamples,
			numPrincipalComponents, (int) generate, random_vector_file,
			fixedIterations, numIterations, resultArray);

	// release arrays
	(*env)->ReleaseFloatArrayElements(env, result, resultArray, 0);
	(*env)->ReleaseFloatArrayElements(env, inputImage, inputArray, 0);
	if (randomVectorFile != NULL) {
		(*env)->ReleaseStringUTFChars(env, randomVectorFile, random_vector_file);
	}
	return result;
}

JNIEXPORT jfloatArray JNICALL Java_cbir_kernels_c_PCA_pca(JNIEnv *env,
		jclass jcl, jfloatArray inputImage, jint numLines, jint numSamples,
		jint numBands, jint linesSamples) {
	// create array for *h_result
	jfloatArray result = (*env)->NewFloatArray(env, linesSamples * numBands);
	// access arrays
	jfloat *inputArray = (*env)->GetFloatArrayElements(env, inputImage, 0);
	jfloat *resultArray = (*env)->GetFloatArrayElements(env, result, 0);

	pca(inputArray, numLines, numSamples, numBands, linesSamples, resultArray);

	// release arrays
	(*env)->ReleaseFloatArrayElements(env, result, resultArray, 0);
	(*env)->ReleaseFloatArrayElements(env, inputImage, inputArray, 0);

	return result;
}

JNIEXPORT jintArray JNICALL Java_cbir_kernels_c_NFindr_nFindr(JNIEnv *env,
		jclass jcl, jfloatArray inputImage, jint numSamples,
		jint numPrincipalComponents, jint linesSamples,
		jboolean generateRandomValues, jstring nfinderInitFile) {

	int success;
	const char* nfinder_init_file = NULL;
	if (nfinderInitFile != NULL) {
		nfinder_init_file = (*env)->GetStringUTFChars(env, nfinderInitFile, 0);
	}

	// create array for *P
	jintArray result = (*env)->NewIntArray(env, numPrincipalComponents + 1);

	// access arrays
	jfloat* inputArray = (*env)->GetFloatArrayElements(env, inputImage, 0);
	jint* P = (*env)->GetIntArrayElements(env, result, 0);

	success = nfindr(inputArray, numSamples, numPrincipalComponents, linesSamples,
			(int) generateRandomValues, nfinder_init_file, P);

	(*env)->ReleaseIntArrayElements(env, result, P, 0);
	(*env)->ReleaseFloatArrayElements(env, inputImage, inputArray, 0);

	if (nfinderInitFile != NULL) {
		(*env)->ReleaseStringUTFChars(env, nfinderInitFile, nfinder_init_file);
	}

	if(success != 0) {
		return NULL;
	}
	//return array
	return result;
}

JNIEXPORT jfloatArray JNICALL Java_cbir_kernels_c_LSU_lsu(JNIEnv *env,
		jclass jcl, jfloatArray image, jintArray P, jint numLines,
		jint numSamples, jint numBands, jint linesSamples, jint numEndmembers) {
	// create array for *Ab
	jfloatArray Ab = (*env)->NewFloatArray(env, linesSamples * numEndmembers);

	// access arrays
	jfloat* AbArray = (*env)->GetFloatArrayElements(env, Ab, 0);
	jint* PArray = (*env)->GetIntArrayElements(env, P, 0);
	jfloat *imageArray = (*env)->GetFloatArrayElements(env, image, 0);

	lsu(imageArray, PArray, numLines, numSamples, numBands, linesSamples,
			numEndmembers, AbArray);

	// release arrays
	(*env)->ReleaseFloatArrayElements(env, image, imageArray, 0);
	(*env)->ReleaseIntArrayElements(env, P, PArray, 0);
	(*env)->ReleaseFloatArrayElements(env, Ab, AbArray, 0);

	//return array
	return Ab;

}

/*
 * Class:     cbir_kernels_c_IO
 * Method:    readHeader
 * Signature: (Ljava/lang/String;)Lcbir/envi/Header;
 */
JNIEXPORT jobject JNICALL Java_cbir_kernels_c_IO_readHeader(JNIEnv *env,
		jobject jobj, jstring header) {
	jint num_lines, num_samples, num_bands, lines_samples, data_type;

	const char *header_file = (*env)->GetStringUTFChars(env, header, 0);
	Read_header(header_file, &num_lines, &num_samples, &num_bands,
			&lines_samples, &data_type);
	(*env)->ReleaseStringUTFChars(env, header, header_file);
	jclass clazz = (*env)->FindClass(env, "Lcbir/envi/Header;");
	jmethodID cid = (*env)->GetMethodID(env, clazz, "<init>", "(IIII)V");

	jobject result = (*env)->NewObject(env, clazz, cid, num_lines, num_samples,
			num_bands, data_type);
	return result;
}

/*
 * Class:     cbir_kernels_c_IO
 * Method:    loadData
 * Signature: (Ljava/lang/String;Ljava/nio/ByteBuffer;IIIII)V
 */
JNIEXPORT void JNICALL Java_cbir_kernels_c_IO_loadData(JNIEnv *env, jobject jobj, jstring filename, jobject buf, jint num_lines, jint num_samples, jint num_bands, jint lines_samples, jint data_type) {
	jfloat* ptr = (*env)->GetDirectBufferAddress(env, buf);

	const char *data_file = (*env)->GetStringUTFChars(env, filename, 0);

	Load_Image(data_file, ptr, num_lines, num_samples, num_bands, lines_samples, data_type);

	(*env)->ReleaseStringUTFChars(env, filename, data_file);

}

/*
 * Class:     cbir_kernels_c_IO
 * Method:    writeImage
 * Signature: ([FLjava/lang/String;III)V
 */
JNIEXPORT void JNICALL Java_cbir_kernels_c_IO_writeImage
(JNIEnv *env, jobject jobj, jfloatArray image, jstring filename, jint num_lines, jint num_samples, jint num_bands) {

	const char *image_file = (*env)->GetStringUTFChars(env, filename, 0);
	jfloat* imageArray = (*env)->GetFloatArrayElements(env, image, 0);

	EscribirResultado(imageArray, image_file, num_lines, num_samples, num_bands);

	(*env)->ReleaseFloatArrayElements(env, image, imageArray, 0);
	(*env)->ReleaseStringUTFChars(env, filename, image_file);
}
