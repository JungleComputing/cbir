#ifndef __UTIL__
#define __UTIL__



void svd (int nmeas, float * C, float *eigenvectors, float *eigenvalues);

void avg_X(float *X, int num_lines, int num_samples, int num_bands);

void generarAleatorios(float *aleatorios, int n);

double Absoluto(double a);

void CambiarFilas(double* A, double* L, double* E, int n, int p);

int LU(double *A, double *L, double *U, double *Per, int p);

void InvTri(double* L, int p);

void normalize_X(float *X, int num_lines, int num_samples, int num_bands);

void generarAleatoriosNfindr(int *aleatorios, int n, int lines_samples);

void matrixInv (int nmeas, float * C, float *etxei);


#endif
