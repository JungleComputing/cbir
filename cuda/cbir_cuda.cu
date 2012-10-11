// includes, system
#include <stdio.h> 
#include <assert.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "cbir_cuda.h"
#include "cbir_cuda_kernel.cu"
//#include "cutil.h"
#include <sys/time.h>
#include <values.h>

#define THRESHOLD 0.00001

#define MIN_DOUBLE ((double) (1 << (sizeof(double) * 8 - 1)))//Minimum double
#define EPS 1.0e-10//Minimim distance between two numbers
#define ABS(a) (((a) < 0) ? -(a) : (a))

//static void Reshape(float *image, float* X, int elementos, int num_bands,
//		long int lines_samples) {
//	for (int i = 0; i < lines_samples; i++) {
//		for (int j = 0; j < num_bands; j++) {
//			X[j * elementos + i] = image[j * lines_samples + i];
//			//X[j*elementos+i]=1;
//		}
//	}
//}

static void LeerAleatorios(const char* random_filename, float *aleatorios,
		int n) {
	FILE *fp;
	float rdm;

	if ((fp = fopen(random_filename, "r")) == NULL) {
		printf(
				"ERROR %d. No se ha podido abrir el fichero de aleatorios: %s \n",
				errno, random_filename);
//		int status = system("PAUSE");
		exit(1);
	} else {
		fseek(fp, 0L, SEEK_CUR);
		int i = 0;
		while (i < n) {

			int result = fscanf(fp, "%f", &rdm);
			aleatorios[i] = rdm;
			i++;
		}

	}
	fclose(fp);
}

//static void checkCUDAError(const char *msg) {
//	cudaError_t err = cudaGetLastError();
//	if (cudaSuccess != err) {
//		fprintf(stderr, "Cuda error: %s: %s.\n", msg, cudaGetErrorString(err));
//		exit(-1);
//	}
//}

static void generarAleatorios(float *aleatorios, int n) {
	srand (time(NULL));
//	srand(10);
int	i;
//Generamos a con numeros aleatorios entre 0 y 1

	for (i = 0; i < n; i++) {
		//a[i]=drand48();
		aleatorios[i] = rand();
		//a[i]=1;
		//printf("%f\n", a[i]);
	}
}

//Function: GenerarAleatorios: Generates a set of n random numbers.
//Input: 	int *aleatorios: vector for store the random numbers.
//				int n: amount of random numbers.
//Output:	int *aleatorios:: vector of random numbers.
static void generarAleatoriosNfindr(int *aleatorios, int n,
		long int lines_samples) {
	srand (time(NULL));
//	srand(10);
for	(int i = 0; i < n; i++) {
		aleatorios[i] = rand() % lines_samples;
		//printf("Aleatorio %d = %d\n", i+1, aleatorios[i]); 
	}
}

//Function: LeerAleatorios: Reads random numbers set from a disk file.
//Input: 	char random_filename[200]:name of file with random numbers.
//				int n: amount of random numbers to read.
//				int *aleatorios: vector for store the random numbers.
//Output:	int *aleatorios:: vector of random numbers.
static void LeerAleatoriosNfindr(const char *random_filename, int *aleatorios,
		int n, int num_samples) {

	FILE *fp;
	int fila;
	int columna;
	int pixel = 0;

	if ((fp = fopen(random_filename, "r")) == NULL) {
		printf(
				"ERROR %d. No se ha podido abrir el fichero de aleatorios: %s \n",
				errno, random_filename);
//		int status = system("PAUSE");
		exit(1);
	} else {
		fseek(fp, 0L, SEEK_CUR);
		int i = 0;
		while (i < n) {
			int result = fscanf(fp, "%d", &fila);
			//   		printf("Fila= %d\n", fila);
			fila--;
			pixel += (fila * num_samples);

			result = fscanf(fp, "%d", &columna);
			//   		printf("Columna= %d\n", columna);
			columna--;
			pixel += columna;

			aleatorios[i] = pixel;
			i++;
			pixel = 0;
		}
	}
	fclose(fp);
}

//Function: Absoluto: computes the absolute value of a number.
//Input: 	double a: the number.
//Output:	double a: absolute value of the "a".
static double Absoluto(double a) {
	if (a < 0) {
		a = a * -1;
	}
	return a;
}
//Function: CambiarFilas: .
//Input: 	double *A:.
//				double *L:.
//				double *E:.
//				int n:.
//				int p:.
//Output:	:.
static void CambiarFilas(double* A, double* L, double* E, int n, int p) {

	int I;
	double max = -1;
	double elemento;
	for (int i = n; i < p; i++) {
		elemento = Absoluto(A[i * p + n]); //Columna n a partir de la fila n
		if (elemento > max) {
			max = elemento;
			I = i;
		}
	}

	double tmp;
	for (int i = 0; i < p; i++) {
		//Se cambia la fila I por la fila n
		tmp = A[n * p + i]; //elemento i de la fila n
		A[n * p + i] = A[I * p + i];
		A[I * p + i] = tmp;

		tmp = L[n * p + i]; //elemento i de la fila n
		L[n * p + i] = L[I * p + i];
		L[I * p + i] = tmp;

		E[n * p + i] = 0;
		E[I * p + i] = 0;
	}

	E[n * p + I] = 1;
	E[I * p + n] = 1;
}

//Function: LU: Makes the LU factorization of a  matrix .
//Input: 	double *L: L matrix of factorization.
//				double *U:U matrix of factorization.
//				double *Per: Permutations matrix.
//				int p:size of matrix.
//Output:	:L, U and Per.
// result == o: OK
// result != 0: error
static int LU(double *A, double *L, double *U, double *Per, int p) {

	double currentPivot;
	double maxPivot;
	double *E;
	double *PerAux;
	//double tmp;
	E = (double*) malloc(sizeof(double) * p * p);
	PerAux = (double*) malloc(sizeof(double) * p * p);

	for (int i = 0; i < p * p; i++) {
		Per[i] = 0;
		U[i] = 0;
		L[i] = 0;
	}
	for (int i = 0; i < p; i++) {
		Per[i * p + i] = 1;

	}
	for (int n = 0; n < p - 1; n++) {
		for (int i = 0; i < p * p; i++) {
			E[i] = 0;
		}
		for (int i = 0; i < p; i++) {
			E[i * p + i] = 1;
		}
		currentPivot = A[n * p + n];
		maxPivot = MIN_DOUBLE;
		double elemento;
		for (int i = n + 1; i < p; i++) {
			elemento = (A[i * p + n]); //Columna n a partir de la fila n
			if (elemento > maxPivot) {
				maxPivot = elemento;
			}
		}
		if (Absoluto(currentPivot) < EPS) { //zero, do row exchage always
			if (Absoluto(maxPivot) < EPS) { //not possible to exchange
				printf("unable to complete LU decomposition, bad A\n");
//				exit(-1);
				free(E);
				free(PerAux);
				return -1;
			} else {
				CambiarFilas(A, L, E, n, p);
			}
		} else {
			if (Absoluto(currentPivot) < Absoluto(maxPivot)) {
				CambiarFilas(A, L, E, n, p);
			}

		}

		for (int i = 0; i < p; i++) {
			for (int j = 0; j < p; j++) {
				PerAux[i * p + j] = 0;
				for (int k = 0; k < p; k++) {
					PerAux[i * p + j] += Per[i * p + k] * E[k * p + j];
				}
			}
		}
		memcpy(Per, PerAux, p * p * sizeof(double));
		for (int i = n + 1; i < p; i++) {
			L[i * p + n] = A[i * p + n] / A[n * p + n];
			A[i * p + n] = 0;
			for (int j = n + 1; j < p; j++) {
				A[i * p + j] = A[i * p + j] - L[i * p + n] * A[n * p + j];
			}
		}
	} //for n
	for (int i = 0; i < p; i++) {
		L[i * p + i]++;
	}
	for (int i = 0; i < p; i++) {
		for (int j = 0; j < p; j++) {
			Per[i * p + j] = PerAux[j * p + i];
		}
	}
	memcpy(U, A, p * p * sizeof(double));
	free(E);
	free(PerAux);
	return 0;
}

//Function: InvTri: Makes the inversion of a triangular matrix .
//Input: 	double *L: the triangular matrix.
//				int p:size of L matrix.
//Output:	double *L:inverse of L.
static void InvTri(double* L, int p) {
	double *I;
	I = (double*) calloc(p * p, sizeof(double));
	for (int i = 0; i < p; i++) {
		I[i * p + i] = 1;
	}

	for (int k = 0; k < p - 1; k++) {

		for (int i = k + 1; i < p; i++) {

			for (int j = 0; j <= k; j++) {
				L[i * p + j] -= L[k * p + i] * L[k * p + j];
				I[i * p + j] -= L[i * p + k] * I[k * p + j];
			}

		}

	}
	memcpy(L, I, p * p * sizeof(double));
	free(I);
}

////////////////////////////////////////////////////////////////////////////////
// Program main
////////////////////////////////////////////////////////////////////////////////

int getDeviceCount() {
	int deviceCount = -1;
	cudaGetDeviceCount(&deviceCount);
	return deviceCount;
}

int initDevice(deviceIdentifier **deviceID, int device) {
	cublasStatus_t status;
	*deviceID = (deviceIdentifier *) malloc(sizeof(deviceIdentifier));
	cudaSetDevice(device);

	(*deviceID)->device = device;
	status = cublasCreate(&((*deviceID)->handle));
	if (status != CUBLAS_STATUS_SUCCESS) {
		free(*deviceID);
		return EXIT_FAILURE;
	}
	fprintf(stderr, "deviceIdentifier for device %d done!\n", (*deviceID)->device);
	fflush(stderr);
	return 0;
}

void destroyDevice(deviceIdentifier *deviceID) {
	cublasDestroy((deviceID->handle));
	free(deviceID);
}

int spca(deviceIdentifier *deviceID, const float* h_image, int num_lines,
		int num_samples, int num_bands, int lines_samples, int n_pc,
		int generate, const char* random_vector_file, int fixed_n_iterations,
		int n_iterations, float* h_B) {
	cublasStatus_t status;
	cudaError_t error;

	cudaSetDevice(deviceID->device);
	int iterations;
	float max;
	int max_i;
	float aux;

	// Pointers to host memory
	float *h_EIGEN;
	float *h_EIGENold;
	float *h_P;
	float *h_Pcoeffs;
	float *h_defl;
	float *h_sumFi;
	float *h_Fitmp;
	float *h_Ytmp_aux;
	float *h_P_aux;

	float *d_X;
	float *d_P;
	float *d_Y;
	float *d_Y2;
	float *d_Pcoeffs;
	float *d_defl;
	float *d_sumFi;
	float *d_Ytmp;
	float *d_Fitmp;
	float *d_Ytmp_aux;
	float *d_pixel;
	float *d_P_aux;
	float *d_B;
	float *d_XX;
	float *d_deltaP;

	h_EIGEN = (float*) calloc(n_pc, sizeof(float));
	h_EIGENold = (float*) calloc(n_pc, sizeof(float));
	h_P = (float*) malloc(num_bands * n_pc * sizeof(float));
	h_Pcoeffs = (float*) malloc(n_pc * n_pc * sizeof(float));
	h_defl = (float*) malloc(n_pc * n_pc * sizeof(float));
	h_sumFi = (float*) malloc(n_pc * num_bands * sizeof(float));
	h_Fitmp = (float*) malloc(n_pc * num_bands * sizeof(float));
	h_Ytmp_aux = (float*) malloc(n_pc * n_pc * sizeof(float));
	h_P_aux = (float*) malloc(n_pc * n_pc * sizeof(float));

	/* Allocate device memory for the matrices */
	error = cudaMalloc((void**) &d_X,
			lines_samples * num_bands * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (X)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_P, num_bands * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (P)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_Y, lines_samples * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (Y)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_Y2, lines_samples * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (Y2)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_Pcoeffs, n_pc * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (Pcoeffs)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_defl, n_pc * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (defl)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_sumFi, n_pc * num_bands * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (sumFi)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_Ytmp, n_pc * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (Ytmp)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_Fitmp, n_pc * num_bands * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (Fitmp)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_Ytmp_aux, n_pc * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (Ytmp_aux)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_pixel, num_bands * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (pixel)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_P_aux, n_pc * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (P_aux)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_B, lines_samples * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (B)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_XX, num_bands * num_bands * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (XX)\n");
		return EXIT_FAILURE;
	}

	error = cudaMalloc((void**) &d_deltaP, num_bands * n_pc * sizeof(float));
	if (error != cudaSuccess) {
		fprintf(stderr, "!!!! device memory allocation error (deltaP)\n");
		return EXIT_FAILURE;
	}

	if (generate == 0) {
		//Random vector reading
		LeerAleatorios(random_vector_file, h_P, num_bands * n_pc);
	} else if (generate == 1) {
		generarAleatorios(h_P, num_bands * n_pc);
		//printf("Se generan los números aleatorios\n");
	}

	cudaThreadSynchronize();

	status = cublasSetVector(num_lines * num_samples * num_bands, sizeof(float),
			h_image, 1, d_X, 1);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr, "!!!! device access error (write X)\n");
		return EXIT_FAILURE;
	}

	int num_blocks_NormalizeX = num_bands;
	//printf("num_blocks_NormalizeX %d\n", num_blocks_NormalizeX);

	int num_threads_NormalizeX = 512;
	//printf("num_threads_NormalizeX %d\n", num_threads_NormalizeX);

	iterations = (int) ceil(
			((float) lines_samples / (float) num_threads_NormalizeX));
	//printf("Iterations NormalizeX %d\n", iterations);

	NormalizeX<<<num_blocks_NormalizeX, num_threads_NormalizeX>>>(d_X, d_pixel, num_bands, num_lines, num_samples, iterations);

	cudaThreadSynchronize();

	status = cublasSetVector(num_bands * n_pc, sizeof(float), h_P, 1, d_P, 1);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr, "!!!! device access error (write P)\n");
		return EXIT_FAILURE;
	}

	/* P_aux= P' * P */
	float alpha = 1;
	float beta = 0;
	status = cublasSgemm(deviceID->handle, CUBLAS_OP_T, CUBLAS_OP_N, n_pc, n_pc,
			num_bands, &alpha, d_P, num_bands, d_P, num_bands, &beta, d_P_aux,
			n_pc);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr, "!!!! kernel execution error (P_aux= P' * P).\n");
		return EXIT_FAILURE;
	}

	/* diag(diag(P_aux))^-0.5 */
	status = cublasGetVector(n_pc * n_pc, sizeof(float), d_P_aux, 1, h_P_aux,
			1);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr, "!!!! device access error (read P_aux)\n");
		return EXIT_FAILURE;
	}

	for (int i = 0; i < n_pc; i++) { //columna
		for (int j = 0; j < n_pc; j++) { //fila
			if (j != i) {
				h_P_aux[(i * n_pc) + j] = 0;
			} else {
				h_P_aux[(i * n_pc) + j] = pow(h_P_aux[(i * n_pc) + j], -0.5);
			}
		}
	}

	status = cublasSetVector(n_pc * n_pc, sizeof(float), h_P_aux, 1, d_P_aux,
			1);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr, "!!!! device access error (write P_aux)\n");
		return EXIT_FAILURE;
	}

	/* P = P * (diag(diag(P'*P))^-0.5) */
	alpha = 1;
	beta = 0;
	status = cublasSgemm(deviceID->handle, CUBLAS_OP_N, CUBLAS_OP_N, num_bands,
			n_pc, n_pc, &alpha, d_P, num_bands, d_P_aux, n_pc, &beta, d_P,
			num_bands);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr,
				"!!!! kernel execution error (P = P * (diag(diag(P'*P))^-0.5)).\n");
		return EXIT_FAILURE;
	}

	/* XX = X'*X */
	alpha = 1;
	beta = 0;
	status = cublasSgemm(deviceID->handle, CUBLAS_OP_T, CUBLAS_OP_N, num_bands,
			num_bands, lines_samples, &alpha, d_X, lines_samples, d_X,
			lines_samples, &beta, d_XX, num_bands);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr, "!!!! kernel execution error (XX = X'*X.\n");
		return EXIT_FAILURE;
	}

	cudaThreadSynchronize();

	int iter;
	if (fixed_n_iterations == 1) {
		//printf("Numero de iteraciones fijadas a %d\n", n_iterations);
	}

	for (iter = 0; iter < n_iterations; iter++) {

		/*Compute deflation matrix*/
		/* Pcoeffs = P' * P */
		alpha = 1;
		beta = 0;
		status = cublasSgemm(deviceID->handle, CUBLAS_OP_T, CUBLAS_OP_N, n_pc,
				n_pc, num_bands, &alpha, d_P, num_bands, d_P, num_bands, &beta,
				d_Pcoeffs, n_pc);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr,
					"!!!! kernel execution error (Pcoeffs = P' * P ).\n");
			return EXIT_FAILURE;
		}
		//
		cudaThreadSynchronize();

		/* defl = -Pcoeffs*/
		cudaMemcpy(h_Pcoeffs, d_Pcoeffs, n_pc * n_pc * sizeof(float),
				cudaMemcpyDeviceToHost);

		for (int i = 0; i < n_pc * n_pc; i++) {
			h_defl[i] = -h_Pcoeffs[i];
		}

		/* defl = triu(defl,+1) + eye(size(defl)) */
		for (int i = 0; i < n_pc; i++) { //col
			for (int j = 0; j < n_pc; j++) { //fil
				if (i == j) {
					h_defl[(i * n_pc) + j] = 1;
				} else {
					if (j > i) {
						h_defl[(i * n_pc) + j] = 0;
					}
				}
			}
		}

		for (int i = 0; i < n_pc; i++) {
			for (int j = i + 2; j < n_pc; j++) {
				for (int k = i + 2; k <= j; k++) {
					h_defl[i + (j * n_pc)] -= h_defl[i + ((k - 1) * n_pc)]
							* h_Pcoeffs[(k - 1) + (j * n_pc)];
				}
			}
		}
		status = cublasSetVector(n_pc * n_pc, sizeof(float), h_defl, 1, d_defl,
				1);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! device access error (write defl)\n");
			return EXIT_FAILURE;
		}

		cudaThreadSynchronize();

		/* deltaP = P * defl */

		alpha = 1;
		beta = 0;
		status = cublasSgemm(deviceID->handle, CUBLAS_OP_N, CUBLAS_OP_N,
				num_bands, n_pc, n_pc, &alpha, d_P, num_bands, d_defl, n_pc,
				&beta, d_deltaP, num_bands);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr,
					"!!!! kernel execution error (deltaP = P * defl ).\n");
			return EXIT_FAILURE;
		}

		cudaThreadSynchronize();

		/* sumFi = deltaP' * XX */
		alpha = 1;
		beta = 0;
		status = cublasSgemm(deviceID->handle, CUBLAS_OP_T, CUBLAS_OP_N, n_pc,
				num_bands, num_bands, &alpha, d_deltaP, num_bands, d_XX,
				num_bands, &beta, d_sumFi, n_pc);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr,
					"!!!! kernel execution error (sumFi = deltaP' * XX).\n");
			return EXIT_FAILURE;
		}

		cudaThreadSynchronize();

		/* Ytmp = sumFi * deltaP */
		alpha = 1;
		beta = 0;
		status = cublasSgemm(deviceID->handle, CUBLAS_OP_N, CUBLAS_OP_N, n_pc,
				n_pc, num_bands, &alpha, d_sumFi, n_pc, d_deltaP, num_bands,
				&beta, d_Ytmp, n_pc);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr,
					"!!!! kernel execution error (Ytmp = sumFi * deltaP).\n");
			return EXIT_FAILURE;
		}

		cudaThreadSynchronize();

		cudaMemcpy(h_Ytmp_aux, d_Ytmp, n_pc * n_pc * sizeof(float),
				cudaMemcpyDeviceToHost);

		for (int i = 0; i < n_pc; i++) { //col
			for (int j = 0; j < n_pc; j++) { //fil
				if (i == j) {
					//Aprovechamos para formar EIGEN a partir de la diagonal de Ytmp
					h_EIGEN[i] = h_Ytmp_aux[(i * n_pc) + j];
					//printf("EIGEN[%d] = %f\n", i, h_EIGEN[i]);
				}
				if (i >= j) {
					h_Ytmp_aux[(i * n_pc) + j] = 0;
				}

			}
		}
		status = cublasSetVector(n_pc * n_pc, sizeof(float), h_Ytmp_aux, 1,
				d_Ytmp_aux, 1);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! device access error (write Ytmp_aux)\n");
			return EXIT_FAILURE;
		}

		/* Fitmp = Ytmp_aux * P' */
		alpha = 1;
		beta = 0;
		status = cublasSgemm(deviceID->handle, CUBLAS_OP_N, CUBLAS_OP_T, n_pc,
				num_bands, n_pc, &alpha, d_Ytmp_aux, n_pc, d_P, num_bands,
				&beta, d_Fitmp, n_pc);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr,
					"!!!! kernel execution error (Fitmp = Ytmp_aux * P').\n");
			return EXIT_FAILURE;
		}
		//
		cudaThreadSynchronize();

		/* sumFi = sumFi - Fitmp */
		status = cublasGetVector(n_pc * num_bands, sizeof(float), d_sumFi, 1,
				h_sumFi, 1);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! device access error (read sumFi)\n");
			return EXIT_FAILURE;
		}
		status = cublasGetVector(n_pc * num_bands, sizeof(float), d_Fitmp, 1,
				h_Fitmp, 1);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! device access error (read Fitmp)\n");
			return EXIT_FAILURE;
		}

		for (int i = 0; i < n_pc * num_bands; i++) {
			h_sumFi[i] -= h_Fitmp[i];
		}
		cudaThreadSynchronize();

		/* P = sumFi ' */
		for (int i = 0; i < num_bands; i++) {
			for (int j = 0; j < n_pc; j++) {
				h_P[(j * num_bands) + i] = h_sumFi[(i * n_pc) + j];
			}
		}

		status = cublasSetVector(n_pc * num_bands, sizeof(float), h_sumFi, 1,
				d_sumFi, 1);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! device access error (write sumFi)\n");
			return EXIT_FAILURE;
		}

		status = cublasSetVector(num_bands * n_pc, sizeof(float), h_P, 1, d_P,
				1);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! device access error (write P)\n");
			return EXIT_FAILURE;
		}

		cudaThreadSynchronize();

		/* P_aux= P' * P */
		alpha = 1;
		beta = 0;
		status = cublasSgemm(deviceID->handle, CUBLAS_OP_T, CUBLAS_OP_N, n_pc,
				n_pc, num_bands, &alpha, d_P, num_bands, d_P, num_bands, &beta,
				d_P_aux, n_pc);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! kernel execution error (P_aux= P' * P).\n");
			return EXIT_FAILURE;
		}

		/* diag(diag(P_aux))^-0.5 */
		status = cublasGetVector(n_pc * n_pc, sizeof(float), d_P_aux, 1,
				h_P_aux, 1);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! device access error (read P_aux)\n");
			return EXIT_FAILURE;
		}

		for (int i = 0; i < n_pc; i++) { //columna
			for (int j = 0; j < n_pc; j++) { //fila
				if (j != i) {
					h_P_aux[(i * n_pc) + j] = 0;
				} else {
					h_P_aux[(i * n_pc) + j] = pow(h_P_aux[(i * n_pc) + j],
							-0.5);
				}
			}
		}

		status = cublasSetVector(n_pc * n_pc, sizeof(float), h_P_aux, 1,
				d_P_aux, 1);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr, "!!!! device access error (write P_aux)\n");
			return EXIT_FAILURE;
		}

		/* P = P * (diag(diag(P'*P))^-0.5) */
		alpha = 1;
		beta = 0;
		status = cublasSgemm(deviceID->handle, CUBLAS_OP_N, CUBLAS_OP_N,
				num_bands, n_pc, n_pc, &alpha, d_P, num_bands, d_P_aux, n_pc,
				&beta, d_P, num_bands);
		if (status != CUBLAS_STATUS_SUCCESS) {
			fprintf(stderr,
					"!!!! kernel execution error (P = P * (diag(diag(P'*P))^-0.5)).\n");
			return EXIT_FAILURE;
		}
		//
		cudaThreadSynchronize();

		/* EIGEN = diag(Ytmp) */
		//ya está hecho
		max = -1;
		max_i = -1;
		for (int i = 0; i < n_pc; i++) {
			//aux=ABS(h_EIGEN[i]-h_EIGENold[i])/ABS(h_EIGENold[i]);
			//aux=ABS(h_EIGEN[i]-h_EIGENold[i]);
			aux = ABS(h_EIGEN[i]-h_EIGENold[i]) / ABS(h_EIGENold[i]);
			//printf("aux = %f\n", aux);
			if (max < aux) {
				max = aux;
				max_i = i;
			}
		}
		//printf("max = %f\n", max);
		//printf("max_i = %d\n", max_i);

		if (fixed_n_iterations == 0) {
			if (max < THRESHOLD
					|| ABS(h_EIGEN[max_i]-h_EIGENold[max_i]) < THRESHOLD) {
				//printf("ITER = %d\n", iter);
				break;
			}
		}
		for (int i = 0; i < n_pc; i++) {
			h_EIGENold[i] = h_EIGEN[i];
			//if(iter==87 || iter==88 || iter==89){
			//printf("EIGENold[%d] = %f\n", i, h_EIGENold[i]);
			//}
		}
	}

	//printf("ITER\t%d\n", iter);

	/* B = X * P */
	alpha = 1;
	beta = 0;
	status = cublasSgemm(deviceID->handle, CUBLAS_OP_N, CUBLAS_OP_N,
			lines_samples, n_pc, num_bands, &alpha, d_X, lines_samples, d_P,
			num_bands, &beta, d_B, lines_samples);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr, "!!!! kernel execution error (B = X * P)\n");
		return EXIT_FAILURE;
	}
	cudaThreadSynchronize();

	status = cublasGetVector(lines_samples * n_pc, sizeof(float), d_B, 1, h_B,
			1);
	if (status != CUBLAS_STATUS_SUCCESS) {
		fprintf(stderr, "!!!! device access error (read B)\n");
		return EXIT_FAILURE;
	}

	free(h_EIGEN);
	free(h_EIGENold);
	free(h_P);
	free(h_Pcoeffs);
	free(h_defl);
	free(h_sumFi);
	free(h_Fitmp);
	free(h_Ytmp_aux);
	free(h_P_aux);

	cudaFree(d_X);
	cudaFree(d_P);
	cudaFree(d_Y);
	cudaFree(d_Y2);
	cudaFree(d_Pcoeffs);
	cudaFree(d_defl);
	cudaFree(d_sumFi);
	cudaFree(d_Ytmp);
	cudaFree(d_Fitmp);
	cudaFree(d_Ytmp_aux);
	cudaFree(d_pixel);
	cudaFree(d_P_aux);
	cudaFree(d_B);
	cudaFree(d_XX);
	cudaFree(d_deltaP);

	cudaThreadSynchronize();
	return 0;
}

int nfindr(deviceIdentifier *deviceID, const float* h_image,
		const int num_samples, const int n_pc, const int lines_samples,
		const int g_aleatorios, const char* nfinder_init_file, int *P) {
	cudaSetDevice(deviceID->device);

	int p = n_pc + 1;		//Numbers of reduced image principal components + 1.

	int *aleatorios;

	double *MatrixTest;
	double *MatrixTestLU;
	double *matrix;
	double volumeactual = 0;
	int maxit;
	int it;
	double v1, v2;

	int bloques_reduccion;
	//int hilos_reduccion=atoi(argv[13]);
	int hilos_reduccion = 512;
	bloques_reduccion = ceil(
			(double) lines_samples / (double) 2 / (double) hilos_reduccion);

	//printf ("bloques_reduccion = %d\n", bloques_reduccion);
	//printf ("hilos_reduccion = %d\n", hilos_reduccion);

	//int hilos_multiplicacion=atoi(argv[14]);
	int hilos_multiplicacion = 512;
	int bloques_multiplicacion;
	bloques_multiplicacion = ceil(
			(double) lines_samples / (double) hilos_multiplicacion);
	//printf ("hilos_multiplicacion = %d\n", hilos_multiplicacion);
	//printf ("bloques_multiplicacion = %d\n", bloques_multiplicacion);
	int elementos_Vvolume = hilos_reduccion * bloques_reduccion * 2;
	//num_ceros=elementos_Vvolume-lines_samples;

	double *HIM2x2c;
	double *HIM2x2;

	double *pixelactual;

	double *Ldet;
	double *Udet;
	double *Pdet;
	double sUdet;

	double *aux2;
	bool comp = false;
	//double tmp;
	double tmp2;

	aleatorios = (int*) malloc(sizeof(int) * p);
	MatrixTest = (double*) malloc(sizeof(double) * p * p);
	MatrixTestLU = (double*) malloc(sizeof(double) * p * p);
	matrix = (double*) malloc(sizeof(double) * p * p);

	HIM2x2c = (double*) calloc(lines_samples * p, sizeof(double));
	HIM2x2 = (double*) malloc(lines_samples * p * sizeof(double));

	pixelactual = (double*) malloc(sizeof(double) * n_pc);

	Ldet = (double*) malloc(p * p * sizeof(double));
	Udet = (double*) malloc(p * p * sizeof(double));
	Pdet = (double*) malloc(p * p * sizeof(double));

	aux2 = (double*) malloc(p * sizeof(double));

	///////////////////////////////////////////////////////////////////////////////////////////

	double *d_aux2;
	double *d_HIM2x2;
	double *d_Vvolume;

	int *h_indices;
	int *d_indices;

	double *h_volumenes;
	double *d_volumenes;

	cudaMalloc((void**) &d_aux2, (p * sizeof(double)));
	cudaMalloc((void**) &d_HIM2x2, (lines_samples * p * sizeof(double)));
	cudaMalloc((void**) &d_Vvolume, (elementos_Vvolume * sizeof(double)));

	h_indices = (int*) malloc(bloques_reduccion * sizeof(int));
	cudaMalloc((void**) &d_indices, (bloques_reduccion * sizeof(int)));
	cudaMemcpy(d_indices, h_indices, (bloques_reduccion * sizeof(int)),
			cudaMemcpyHostToDevice);

	h_volumenes = (double*) malloc(bloques_reduccion * sizeof(double));
	cudaMalloc((void**) &d_volumenes, (bloques_reduccion * sizeof(double)));
	cudaMemcpy(d_volumenes, h_volumenes, (bloques_reduccion * sizeof(double)),
			cudaMemcpyHostToDevice);

	if (g_aleatorios == 0) {
		LeerAleatoriosNfindr(nfinder_init_file, aleatorios, p, num_samples);
	} else {
		generarAleatoriosNfindr(aleatorios, p, lines_samples);
	}

	//Make the initial matrix
	for (int k = 0; k < p; k++) {
		P[k] = aleatorios[k];
		MatrixTest[k] = 1;
		matrix[k] = 1;
		for (int i = 1; i < p; i++) {	//from 1 to 19
			MatrixTest[k + (i * p)] = (double) h_image[P[k]
					+ ((i - 1) * lines_samples)];
			matrix[k + (i * p)] = (double) h_image[P[k]
					+ ((i - 1) * lines_samples)];
		}
	}

	cudaThreadSynchronize();

	//Compute initial matrix volume
	for (int k = 0; k < p - 1; k++) {
		for (int i = k + 1; i < p; i++) {
			for (int j = k + 1; j < p; j++) {
				matrix[p * i + j] -= matrix[p * i + k] * matrix[p * k + j]
						/ matrix[p * k + k];
			}
		}
	}
	double deter = 1.0;
	for (int i = 0; i < p; i++) {
		deter *= matrix[p * i + i];
	}

	volumeactual = Absoluto(deter);

	cudaThreadSynchronize();
	//printf("VOLUMEN INICIAL %e\n", volumeactual);

	maxit = 3 * p;
	it = 1;
	v1 = -1;
	v2 = volumeactual;

	for (int i = 0; i < lines_samples; i++) {
		HIM2x2c[i] = 1;
	}
	for (int i = 0; i < lines_samples * n_pc; i++) {
		HIM2x2c[lines_samples + i] = (double) h_image[i];
	}

	while ((it <= maxit) && (v2 > v1)) {
		memcpy(HIM2x2, HIM2x2c, lines_samples * p * sizeof(double));
		for (int k = 0; k < p; k++) {
			for (int i = 0; i < n_pc; i++) {//Change the endmember k for the endmember p
				pixelactual[i] = MatrixTest[(i + 1) * p + k];
				MatrixTest[(i + 1) * p + k] = MatrixTest[(i + 1) * p + (p - 1)];
				MatrixTest[(i + 1) * p + (p - 1)] = pixelactual[i];
			}
			for (int i = 0; i < p; i++) {	//Set the last column 0 0 0 ... 1
				MatrixTest[i * p + (p - 1)] = 0;
			}
			MatrixTest[p * p - 1] = 1;
			memcpy(MatrixTestLU, MatrixTest, p * p * sizeof(double));//Not to overwrite MatrixTest doing LU
			if (LU(MatrixTestLU, Ldet, Udet, Pdet, p) != 0) {
				free(h_indices);
				free(h_volumenes);

				cudaFree(d_aux2);
				cudaFree(d_HIM2x2);
				cudaFree(d_Vvolume);
				cudaFree(d_indices);
				cudaFree(d_volumenes);

				cudaThreadSynchronize();
				return -1;
			}
			sUdet = 1;
			for (int i = 0; i < p; i++) {
				sUdet *= Udet[i * p + i];
			}
			sUdet = Absoluto(sUdet);

			InvTri(Ldet, p);	//inv(Ldet)

			for (int j = 0; j < p; j++) {
				aux2[j] = 0;
				for (int k = 0; k < p; k++) {
					aux2[j] += Ldet[(p - 1) * p + k] * Pdet[k * p + j];
				}
			}

			cudaMemcpy(d_aux2, aux2, (p * sizeof(double)),
					cudaMemcpyHostToDevice);
			if (k == 0) {
				cudaMemcpy(d_HIM2x2, HIM2x2,
						(p * lines_samples * sizeof(double)),
						cudaMemcpyHostToDevice);
			}
			tmp2 = (sUdet / Udet[p * p - 1]);

			VolumeCalculation<<<bloques_multiplicacion, hilos_multiplicacion>>>(d_aux2, d_HIM2x2, d_Vvolume, tmp2, lines_samples, p);
			Reduction_vol<<<bloques_reduccion, hilos_reduccion>>>(d_Vvolume, d_volumenes, d_indices, lines_samples);

			cudaMemcpy(h_volumenes, d_volumenes,
					(bloques_reduccion * sizeof(double)),
					cudaMemcpyDeviceToHost);
			cudaMemcpy(h_indices, d_indices, (bloques_reduccion * sizeof(int)),
					cudaMemcpyDeviceToHost);

			for (int i = 0; i < bloques_reduccion; i++) {
				//printf("--------- = %e\n", h_volumenes[i]);
				if (h_volumenes[i] > volumeactual) {
					volumeactual = h_volumenes[i];
					P[k] = h_indices[i];
					comp = true;
				}
			}
			if (comp == true) {
				//printf("---> update with pixel @ %d | abs(det(E))= %e\n", P[k], Vvolume[P[k]]);
				for (int j = 0; j < n_pc; j++) {
					pixelactual[j] = h_image[j * lines_samples + P[k]];
				}
				comp = false;
			}
			for (int i = 0; i < n_pc; i++) {
				MatrixTest[(i + 1) * p + (p - 1)] = MatrixTest[(i + 1) * p + k];
				MatrixTest[(i + 1) * p + k] = pixelactual[i];
			}
		}	//for k

		//for(int i=0; i<p; i++){
		//	printf("Pixel %d -> [%d, %d]\n", i, P[i]/num_samples, P[i]%num_samples);
		//}

		it++;
		v1 = v2;
		v2 = volumeactual;
	}		//while and

	int nit = it - 1;
	if (nit < maxit) {
		//printf("End, convergence @ iteration [%d]. Final abs(det(E)) = %e\n", nit, volumeactual);
		//printf("NFINDR_IT\t%d\n", nit);
	} else {
		printf(
				"End, NO convergence until iteration # %d. the abs(det(E)) = %e\n",
				nit, volumeactual);
	}
	//printf("The NFINDR found solution is:\n");
	for (int i = 0; i < p; i++) {
		//printf("Pixel %d -> [%d, %d]\n", i, P[i]/num_samples, P[i]%num_samples);
	}

	/*		FILE *f_end = fopen(argv[10],"w+");*/
	/*		for(int i=0; i<p; i++){*/
	/*			fprintf(f_end, "===== Endmembers %d =====\n", i);*/
	/*			for(int j=0; j<num_bands_orig; j++){*/
	/*				fprintf(f_end, "%f\n", h_X[P[i]+(lines_samples*j)]);*/
	/*			}*/
	/*		}*/
	/*		fclose(f_end);*/

	free(h_indices);
	free(h_volumenes);

	cudaFree(d_aux2);
	cudaFree(d_HIM2x2);
	cudaFree(d_Vvolume);
	cudaFree(d_indices);
	cudaFree(d_volumenes);

	cudaThreadSynchronize();
	return 0;
}

void lsu(deviceIdentifier *deviceID, const float* h_image, const int* P,
		const int num_lines, const int num_samples, const int num_bands,
		const int lines_samples, const int num_endmembers,
		float* abundance_map) {

	cudaSetDevice(deviceID->device);

	//Pointers to Host memory
	double *h_end;
	double *h_endt;
	double *h_etxe;
	double *h_etxei;
	double *h_matriz_computo;
	float *h_matriz_computo2;

	//Pointers to Device memory
	float *d_imagen;
	float *d_matriz_computo;
	float *d_imagen_unmixing;

	//Memory assignmrnt for Device and Host
	h_end = (double*) malloc(num_bands * num_endmembers * sizeof(double));//Matriz de Endmembers
	h_endt = (double*) malloc(num_bands * num_endmembers * sizeof(double));	//Matriz de Endmembers Traspuesta
	h_etxe = (double*) malloc(num_endmembers * num_endmembers * sizeof(double));// h_endt * h_end
	h_etxei = (double*) malloc(
			num_endmembers * num_endmembers * sizeof(double));// Inversa(h_etxe)
	h_matriz_computo = (double*) malloc(
			num_endmembers * num_bands * sizeof(double));	// h_etxei * h_endt
	h_matriz_computo2 = (float*) malloc(
			num_endmembers * num_bands * sizeof(float));

	//Copy CPU -> GPU
	cudaMalloc((void**) &d_imagen,
			(num_lines * num_samples * num_bands * sizeof(float)));
	cudaMalloc((void**) &d_matriz_computo,
			(num_endmembers * num_bands * sizeof(float)));
	cudaMalloc((void**) &d_imagen_unmixing,
			(num_lines * num_samples * num_endmembers * sizeof(float)));

	int fila, columna;
	//FILE *fpe;
	//fpe = fopen(argv[9], "w");
	for (int k = 0; k < num_endmembers; k++) {
		//fprintf(fpe,"======== Endmember %d ========\n", k);
		for (int l = 0; l < num_bands; l++) {
			//fprintf(fpe,"%f\n", h_imagen[l*lines_samples+P[k]]);
			h_endt[k * num_bands + l] = h_image[l * lines_samples + P[k]];

			fila = (k * num_bands + l) / num_bands;

			columna = (k * num_bands + l) % num_bands;

			h_end[columna * num_endmembers + fila] = h_endt[fila * num_bands
					+ columna];
		}
	}
	//fclose(fpe);
	cudaThreadSynchronize();

	//h_endt x h_end = h_etxe
	for (int i = 0; i < num_endmembers; i++) {
		for (int j = 0; j < num_endmembers; j++) {
			h_etxe[i * num_endmembers + j] = 0;
			for (int k = 0; k < num_bands; k++) {
				h_etxe[i * num_endmembers + j] += h_endt[i * num_bands + k]
						* h_end[k * num_endmembers + j];
				//printf("%f\n", h_etxe[i*N_END+j]);
				//system("PAUSE");
			}
		}
	}

	cudaThreadSynchronize();

	//h_etxei = inv(h_etxe)
	double *b;
	//float *c;
	int n = num_endmembers;
	b = (double*) malloc(num_endmembers * num_endmembers * sizeof(double));	//matriz de los términos independientes
	//c = (float*) malloc (N_END * N_END * sizeof(float));
	for (int i = 0; i < num_endmembers * num_endmembers; i++) {
		b[i] = 0;
		h_etxei[i] = 0;
		//c[i]=h_etxe[i];
	}
	//identity matrix
	for (int i = 0; i < n; i++) {
		b[i * num_endmembers + i] = 1.0;
	}

	//Matrix and independent terms transformation
	for (int k = 0; k < n - 1; k++) {
		for (int i = k + 1; i < n; i++) {
			//independent terms
			for (int s = 0; s < n; s++) {
				b[i * num_endmembers + s] -= h_etxe[i * num_endmembers + k]
						* b[k * num_endmembers + s]
						/ h_etxe[k * num_endmembers + k];
			}

			//matrix elements
			for (int j = k + 1; j < n; j++) {
				h_etxe[i * num_endmembers + j] -= h_etxe[i * num_endmembers + k]
						* h_etxe[k * num_endmembers + j]
						/ h_etxe[k * num_endmembers + k];
			}
		}
	}

	//unknown calculation, inverse matrix elements
	for (int s = 0; s < n; s++) {
		h_etxei[(n - 1) * num_endmembers + s] = b[(n - 1) * num_endmembers + s]
				/ h_etxe[(n - 1) * num_endmembers + n - 1];
		for (int i = n - 2; i >= 0; i--) {
			h_etxei[i * num_endmembers + s] = b[i * num_endmembers + s]
					/ h_etxe[i * num_endmembers + i];
			for (int k = n - 1; k > i; k--) {
				h_etxei[i * num_endmembers + s] -=
						h_etxe[i * num_endmembers + k]
								* h_etxei[k * num_endmembers + s]
								/ h_etxe[i * num_endmembers + i];
			}
		}
	}

	cudaThreadSynchronize();

	//h_etxei x h_endt = h_matriz_computo
	for (int i = 0; i < num_endmembers; i++) {
		for (int j = 0; j < num_bands; j++) {
			h_matriz_computo[i * num_bands + j] = 0;
			for (int k = 0; k < num_endmembers; k++) {
				h_matriz_computo[i * num_bands + j] += h_etxei[i
						* num_endmembers + k] * h_endt[k * num_bands + j];
			}
		}
	}

	for (int i = 0; i < num_endmembers; i++) {
		for (int j = 0; j < num_bands; j++) {
			h_matriz_computo2[i * num_bands + j] = (float) h_matriz_computo[i
					* num_bands + j];
		}
	}

	cudaThreadSynchronize();

	//Copy CPU -> GPU
	cudaMemcpy(d_imagen, h_image,
			(num_lines * num_samples * num_bands * sizeof(float)),
			cudaMemcpyHostToDevice);
	// Timo: no need to copy this one: it will only contain result values.
	//	cudaMemcpy(d_imagen_unmixing, abundance_map,
	//			(num_lines * num_samples * num_endmembers * sizeof(float)),
	//			cudaMemcpyHostToDevice);
	cudaMemcpy(d_matriz_computo, h_matriz_computo2,
			(num_endmembers * num_bands * sizeof(float)),
			cudaMemcpyHostToDevice);

	//int num_bloques_lsu = atoi(argv[11]);
	//int num_hilos_lsu = atoi(argv[12]);

	int num_hilos_lsu = 512;
	int num_bloques_lsu = (int) ceil(
			((float) lines_samples / (float) num_hilos_lsu));
	//printf ("num_bloques_lsu = %d\n", num_bloques_lsu);
	//printf ("num_hilos_lsu = %d\n", num_hilos_lsu);

	//Kernel execution
	Unmixing<<<num_bloques_lsu, num_hilos_lsu>>>(d_imagen, d_imagen_unmixing, d_matriz_computo, num_lines, num_samples, num_bands, num_endmembers);

	cudaThreadSynchronize();

	//Copy GPU -> CPU
	cudaMemcpy(abundance_map, d_imagen_unmixing,
			(num_lines * num_samples * num_endmembers * sizeof(float)),
			cudaMemcpyDeviceToHost);

	cudaFree(d_imagen);
	cudaFree(d_matriz_computo);
	cudaFree(d_imagen_unmixing);

	cudaThreadSynchronize();
}
