#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include <string.h>
#include <values.h>
#include "blaslib.h"
#include "io.h"
#include <mkl.h>
//#include "cblas.h"

//#define MIN_DOUBLE ((double) (1 << (sizeof(double) * 8 - 1)))//Double m�nimo
#define MIN_DOUBLE MINDOUBLE
#define EPS 1.0e-10 //Minimal distance between two numbers

void svd(int nmeas, float * C, float *eigenvectors, float *eigenvalues) {
	int ISUPPZ[2 * nmeas];
	float WORK[50 * nmeas];
	int IWORK[20 * nmeas];
	int INFO, M;

	//compute eigenvalues and eigenvectors of covariance matrix
	Ssyevr('V', 'A', 'U', nmeas, C, nmeas, 0.0, 1.0, 0, 1, 0.0, &M,
			eigenvalues, eigenvectors, nmeas, ISUPPZ, WORK, 50 * nmeas, IWORK,
			20 * nmeas, &INFO);

}

void avg_X(float *X, int num_lines, int num_samples, int num_bands) {
	int lines_samples = num_lines * num_samples;
	int i, j;
	float mean;

	for (i = 0; i < num_bands; i++) {
		mean = 0;
		for (j = 0; j < lines_samples; j++) {
			mean = mean + (X[(i * lines_samples) + j]);
		}
		mean = mean / lines_samples;
		//printf("pixel_media[%d] = %f\n", j+1, mean);
		for (j = 0; j < lines_samples; j++) {
			X[(i * lines_samples) + j] = X[(i * lines_samples) + j] - mean;
		}
	}

}

double Absoluto(double a) {
	if (a < 0) {
		a = a * -1;
	}
	return a;
}

void CambiarFilas(double* A, double* L, double* E, int n, int p) {
	// switch rows
	int I = -1, i;
	double max = -1;
	double elemento;
	for (i = n; i < p; i++) {
		elemento = Absoluto(A[i * p + n]); //Columna n a partir de la fila n
		if (elemento > max) {
			max = elemento;
			I = i;
		}
	}

	double tmp;
	for (i = 0; i < p; i++) {
		//Se cambia la fila I por la fila n
		tmp = A[n * p + i];//elemento i de la fila n
		A[n * p + i] = A[I * p + i];
		A[I * p + i] = tmp;

		tmp = L[n * p + i];//elemento i de la fila n
		L[n * p + i] = L[I * p + i];
		L[I * p + i] = tmp;

		E[n * p + i] = 0;
		E[I * p + i] = 0;
	}

	E[n * p + I] = 1;
	E[I * p + n] = 1;
}

int LU(double *A, double *L, double *U, double *Per, int p) {

	double currentPivot;
	double maxPivot;
	double *E;
	double *PerAux;
	E = (double*) malloc(sizeof(double) * p * p);
	PerAux = (double*) malloc(sizeof(double) * p * p);
	int i, j, k, n;

	for (i = 0; i < p * p; i++) {
		Per[i] = 0;
		U[i] = 0;
		L[i] = 0;
	}
	for (i = 0; i < p; i++) {
		Per[i * p + i] = 1;

	}

	for (n = 0; n < p - 1; n++) {
		for (i = 0; i < p * p; i++) {
			E[i] = 0;
		}
		for (i = 0; i < p; i++) {
			E[i * p + i] = 1;
		}

		currentPivot = A[n * p + n];
		maxPivot = MIN_DOUBLE;
		double elemento;
		for (i = n + 1; i < p; i++) {
			elemento = (A[i * p + n]); //Columna n a partir de la fila n
			if (elemento > maxPivot) {
				maxPivot = elemento;
			}
		}

		if (Absoluto(currentPivot) < EPS) {//zero, do row exchange always
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

		for (i = 0; i < p; i++) {
			for (j = 0; j < p; j++) {
				PerAux[i * p + j] = 0;
				for (k = 0; k < p; k++) {
					PerAux[i * p + j] += Per[i * p + k] * E[k * p + j];
				}
			}
		}
		memcpy(Per, PerAux, p * p * sizeof(double));

		for (i = n + 1; i < p; i++) {
			L[i * p + n] = A[i * p + n] / A[n * p + n];
			A[i * p + n] = 0;
			for (j = n + 1; j < p; j++) {
				A[i * p + j] = A[i * p + j] - L[i * p + n] * A[n * p + j];
			}
		}
	}//for n		

	for (i = 0; i < p; i++) {
		L[i * p + i]++;
	}

	for (i = 0; i < p; i++) {
		for (j = 0; j < p; j++) {
			Per[i * p + j] = PerAux[j * p + i];
		}
	}

	memcpy(U, A, p * p * sizeof(double));

	free(E);
	free(PerAux);
	return 0;
}

void InvTri(double* L, int p) {
	int i, j, k;
	double *I;
	I = (double*) calloc(p * p, sizeof(double));
	for (i = 0; i < p; i++) {
		I[i * p + i] = 1;
	}

	for (k = 0; k < p - 1; k++) {
		for (i = k + 1; i < p; i++) {
			for (j = 0; j <= k; j++) {
				L[i * p + j] -= L[k * p + i] * L[k * p + j];
				I[i * p + j] -= L[i * p + k] * I[k * p + j];
			}
		}
	}
	memcpy(L, I, p * p * sizeof(double));
	free(I);
}

void normalize_X(float *X, int num_lines, int num_samples, int num_bands) {

	int lines_samples = num_lines * num_samples;
	int i, j;
	float mean;
	float var;

	for (i = 0; i < num_bands; i++) {
		mean = 0;
		for (j = 0; j < lines_samples; j++) {
			mean = mean + (X[(i * lines_samples) + j]);
		}
		mean = mean / lines_samples;
		var = 0;
		//printf("pixel_media[%d] = %f\n", j+1, mean);
		for (j = 0; j < lines_samples; j++) {
			var = var + ((X[(i * lines_samples) + j] - mean) * (X[(i
					* lines_samples) + j] - mean));
		}
		var = var / (lines_samples - 1);
		for (j = 0; j < lines_samples; j++) {
			X[(i * lines_samples) + j] = X[(i * lines_samples) + j] / var;
		}

	}

}

//generates random numbers for N-Findr endmember initialization
void generarAleatorios(float *aleatorios, int n) {
//	srand(time(NULL));
	srand(10);
	int i;
	//Generamos a con numeros aleatorios entre 0 y 1

	for (i = 0; i < n; i++) {
		//a[i]=drand48();
		aleatorios[i] = rand();
		//a[i]=1;
		//printf("%f\n", a[i]);
	}
}

void generarAleatoriosNfindr(int *aleatorios, int n, int lines_samples) {
//	srand(time(NULL)); //Asignar semilla
	srand(10);
	int i;
	for (i = 0; i < n; i++) {
		aleatorios[i] = rand() % lines_samples;
		//printf("Aleatorio %d = %d\n", i+1, aleatorios[i]); 
	}
}

void matrixInv(int nmeas, float * C, float *etxei) {
	int ISUPPZ[2 * nmeas];
	float WORK[50 * nmeas];
	int IWORK[20 * nmeas];
	int INFO, M;
	float *eigenvectors;
	float *eigenvalues;
	float *D;
	float *aux;
	int i;

	D = (float*) calloc(nmeas * nmeas, sizeof(float));
	aux = (float*) malloc(nmeas * nmeas * sizeof(float));
	eigenvectors = (float*) malloc(nmeas * nmeas * sizeof(float));
	eigenvalues = (float*) malloc(nmeas * sizeof(float));

	//compute eigenvalues and eigenvectors of covariance matrix
	Ssyevr('V', 'A', 'U', nmeas, C, nmeas, 0.0, 1.0, 0, 1, 0.0, &M,
			eigenvalues, eigenvectors, nmeas, ISUPPZ, WORK, 50 * nmeas, IWORK,
			20 * nmeas, &INFO);

	//formamos la matriz diagonal D
	for (i = 0; i < nmeas; i++) {
		D[i * nmeas + i] = 1 / eigenvalues[i];
		//printf("D[%d] = %22.16g\n", i, D[i*nmeas+i]);
	}

	cblas_sgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, nmeas, nmeas, nmeas,
			1, eigenvectors, nmeas, D, nmeas, 0, aux, nmeas);
	cblas_sgemm(CblasColMajor, CblasNoTrans, CblasTrans, nmeas, nmeas, nmeas,
			1, aux, nmeas, eigenvectors, nmeas, 0, etxei, nmeas);

	free(D);
	free(aux);
	free(eigenvectors);
	free(eigenvalues);

}
