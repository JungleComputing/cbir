#include <stdlib.h>

#include "cbir_cuda.h"
#include "jni_cbir_cuda.h"
#include <values.h>

JNIEXPORT void JNICALL Java_cbir_kernels_cuda_CudaKernel_initCuda
(JNIEnv *env, jclass jcl) {
	if(initCuda() != 0) {
		jclass Exception = (*env)->FindClass(env, "java/lang/Exception");
		(*env)->ThrowNew(env, Exception,"CUBLAS initialization error");
	}
}

JNIEXPORT jfloatArray JNICALL Java_cbir_kernels_cuda_SPCA_spca(JNIEnv *env,
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
/*
 * Class:     cbir_kernels_cuda_NFindr
 * Method:    nFindr
 * Signature: ([FIIIILjava/lang/String;)[I
 */
JNIEXPORT jintArray JNICALL Java_cbir_kernels_cuda_NFindr_nFindr(JNIEnv *env,
		jclass jcl, jfloatArray inputImage, jint numSamples,
		jint numPrincipalComponents, jint linesSamples,
		jboolean generateRandomValues, jstring nfinderInitFile) {
	const char* nfinder_init_file = NULL;
	int success;
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

/*
 * Class:     cbir_kernels_cuda_LSU
 * Method:    lsu
 * Signature: ([F[IIIIII)[F
 */
JNIEXPORT jfloatArray JNICALL Java_cbir_kernels_cuda_LSU_lsu(JNIEnv *env,
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
