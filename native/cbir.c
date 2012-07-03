/* Includes, system */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <sys/time.h>

#include "blaslib.h"
#include "io.h"
#include "util.h"
#include "cblas.h"

enum bool {
	false, true
};

#define ABS(a) (((a) < 0) ? -(a) : (a))
#define THRESHOLD 0.00001

int spca(const float* image, const int num_lines, const int num_samples,
		const int num_bands, const int lines_samples, const int n_pc,
		const int generate, const char* random_vector_file,
		const int fixed_n_iterations, const int n_iterations, float* B) {
	float max;
	int max_i;
	float aux;
	int i, j, k, iter;

//	printf("spca 0\n");
//	fflush(stdout);

	// Pointers to host memory
	float *EIGEN;
	float *EIGENold;
	float *X;
	float *P1;
	float *P2;
	float *Pcoeffs;
	float *defl;
	float *sumFi;
	float *Ytmp;
	float *Fitmp;
	float *Ytmp_aux;
	float *P_aux;
	float *XX;
	float *deltaP;

	EIGEN = (float*) calloc(n_pc, sizeof(float));
	EIGENold = (float*) calloc(n_pc, sizeof(float));
	X = (float*) malloc(lines_samples * num_bands * sizeof(float));
	P1 = (float*) malloc(num_bands * n_pc * sizeof(float));
	P2 = (float*) malloc(num_bands * n_pc * sizeof(float));
	Pcoeffs = (float*) malloc(n_pc * n_pc * sizeof(float));
	defl = (float*) malloc(n_pc * n_pc * sizeof(float));
	sumFi = (float*) malloc(n_pc * num_bands * sizeof(float));
	Ytmp = (float*) malloc(n_pc * n_pc * sizeof(float));
	Fitmp = (float*) malloc(n_pc * num_bands * sizeof(float));
	Ytmp_aux = (float*) malloc(n_pc * n_pc * sizeof(float));
	P_aux = (float*) malloc(n_pc * n_pc * sizeof(float));
	XX = (float*) malloc(num_bands * num_bands * sizeof(float));
	deltaP = (float*) malloc(num_bands * n_pc * sizeof(float));

	//printf("spca 0.1\n");
	//fflush(stdout);


	memcpy(X, image, lines_samples * num_bands * sizeof(float));
	//Random vector reading
	//LeerAleatorios(argv[3], P2, num_bands * n_pc);

	if (generate == 0) {
		//Random vector reading
		LeerAleatorios(random_vector_file, P2, num_bands * n_pc);
	} else {
//		printf("spca: generate randoms\n");
//		fflush(stdout);

		//printf("Se generan los números aleatorios\n");
		generarAleatorios(P2, num_bands * n_pc);
	}

//	printf("spca 1\n");
//	fflush(stdout);

	normalize_X(X, num_lines, num_samples, num_bands);

	/* P_aux= P' * P */
	cblas_sgemm(CblasColMajor, CblasTrans, CblasNoTrans, n_pc, n_pc, num_bands,
			1, P2, num_bands, P2, num_bands, 0, P_aux, n_pc);

	for (i = 0; i < n_pc; i++) {//columna
		for (j = 0; j < n_pc; j++) {//fila
			if (j != i) {
				P_aux[(i * n_pc) + j] = 0;
			} else {
				P_aux[(i * n_pc) + j] = pow(P_aux[(i * n_pc) + j], -0.5);
			}
		}
	}

	cblas_sgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, num_bands, n_pc,
			n_pc, 1, P2, num_bands, P_aux, n_pc, 0, P1, num_bands);
	//memcpy(P, P2, num_bands*n_pc*sizeof(float));
//	printf("spca 2\n");
//	fflush(stdout);

	/* XX = X'*X */
	cblas_sgemm(CblasColMajor, CblasTrans, CblasNoTrans, num_bands, num_bands,
			lines_samples, 1, X, lines_samples, X, lines_samples, 0, XX,
			num_bands);

	if (fixed_n_iterations == 1) {
		//printf("Numero de iteraciones fijadas a %d\n", n_iterations);
	}
	for (iter = 0; iter < n_iterations; iter++) {
		/*Compute deflation matrix*/
		/* Pcoeffs = P' * P */
		cblas_sgemm(CblasColMajor, CblasTrans, CblasNoTrans, n_pc, n_pc,
				num_bands, 1, P1, num_bands, P1, num_bands, 0, Pcoeffs, n_pc);
		//
		/* defl = -Pcoeffs*/
		for (i = 0; i < n_pc * n_pc; i++) {
			defl[i] = -Pcoeffs[i];
		}

		/* defl = triu(defl,+1) + eye(size(defl)) */
		for (i = 0; i < n_pc; i++) {//col
			for (j = 0; j < n_pc; j++) {//fil
				if (i == j) {
					defl[(i * n_pc) + j] = 1;
				} else {
					if (j > i) {
						defl[(i * n_pc) + j] = 0;
					}
				}
			}
		}

		/* defl(i,j) = +defl(i,j) - defl(i,k-1)*Pcoeffs(k-1,j); */
		for (i = 0; i < n_pc; i++) {
			for (j = i + 2; j < n_pc; j++) {
				for (k = i + 2; k <= j; k++) {
					defl[i + (j * n_pc)] -= defl[i + ((k - 1) * n_pc)]
							* Pcoeffs[(k - 1) + (j * n_pc)];
				}
			}
		}
		//

		/* deltaP = P * defl */
		cblas_sgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, num_bands, n_pc,
				n_pc, 1, P1, num_bands, defl, n_pc, 0, deltaP, num_bands);

		/* sumFi = deltaP' * XX */
		cblas_sgemm(CblasColMajor, CblasTrans, CblasNoTrans, n_pc, num_bands,
				num_bands, 1, deltaP, num_bands, XX, num_bands, 0, sumFi, n_pc);

		/* Ytmp = sumFi * deltaP */
		cblas_sgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, n_pc, n_pc,
				num_bands, 1, sumFi, n_pc, deltaP, num_bands, 0, Ytmp, n_pc);

		memcpy(Ytmp_aux, Ytmp, n_pc * n_pc * sizeof(float));

		/* Ytmp_aux = tril( (Ytmp) ,-1) */
		for (i = 0; i < n_pc; i++) {//col
			for (j = 0; j < n_pc; j++) {//fil
				if (i == j) {
					//Aprovechamos para formar EIGEN a partir de la diagonal de Ytmp
					EIGEN[i] = Ytmp_aux[(i * n_pc) + j];
					//printf("EIGEN[%d] = %f\n", i, h_EIGEN[i]);
				}
				if (i >= j) {
					Ytmp_aux[(i * n_pc) + j] = 0;
				}

			}
		}


		/* Fitmp = Ytmp_aux * P' */
		cblas_sgemm(CblasColMajor, CblasNoTrans, CblasTrans, n_pc, num_bands,
				n_pc, 1, Ytmp_aux, n_pc, P1, num_bands, 0, Fitmp, n_pc);
		//
		/* sumFi = sumFi - Fitmp */
		for (i = 0; i < n_pc * num_bands; i++) {
			sumFi[i] -= Fitmp[i];
		}
		//
		/* P = sumFi ' */
		for (i = 0; i < num_bands; i++) {
			for (j = 0; j < n_pc; j++) {
				P2[(j * num_bands) + i] = sumFi[(i * n_pc) + j];
				//printf("P[%d] = %f\n", (j*num_bands)+i, P[(j*num_bands)+i]);
			}
		}
		//
		/* P_aux= P' * P */
		cblas_sgemm(CblasColMajor, CblasTrans, CblasNoTrans, n_pc, n_pc,
				num_bands, 1, P2, num_bands, P2, num_bands, 0, P_aux, n_pc);

		for (i = 0; i < n_pc; i++) {//columna
			for (j = 0; j < n_pc; j++) {//fila
				if (j != i) {
					P_aux[(i * n_pc) + j] = 0;
				} else {
					P_aux[(i * n_pc) + j] = pow(P_aux[(i * n_pc) + j], -0.5);
				}
			}
		}

		cblas_sgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, num_bands, n_pc,
				n_pc, 1, P2, num_bands, P_aux, n_pc, 0, P1, num_bands);
		/* EIGEN = diag(Ytmp) */
		//ya está hecho
		//

		max = -1;
		max_i = -1;
		for (i = 0; i < n_pc; i++) {
			aux = ABS(EIGEN[i] - EIGENold[i]) / ABS(EIGENold[i]);
			//printf("aux = %f\n", aux);
			if (max < aux) {
				max = aux;
				max_i = i;
			}
		}
		//printf("%f\n", max);
		if (fixed_n_iterations == 0) {
			if (max < THRESHOLD || ABS(EIGEN[max_i] - EIGENold[max_i])
					< THRESHOLD) {
				//printf("ITER = %d\n", iter);
				break;
			}
		}
		for (i = 0; i < n_pc; i++) {
			EIGENold[i] = EIGEN[i];
			//printf("EIGENold[%d] = %f\n", i, h_EIGENold[i]);
		}
	}
	//printf("ITER\t%d\n", iter);

	/* B = X * P */
	cblas_sgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, lines_samples, n_pc,
			num_bands, 1, X, lines_samples, P1, num_bands, 0, B, lines_samples);
	//

	//EscribirResultado( B, argv[4],num_lines, num_samples, n_pc);

	free(EIGEN);
	free(EIGENold);
	free(X);
	free(P1);
	free(P2);
	free(Pcoeffs);
	free(defl);
	free(sumFi);
	free(Ytmp);
	free(Fitmp);
	free(Ytmp_aux);
	free(P_aux);
	free(XX);
	free(deltaP);

	return 0;
}

void pca(const float *input_image, const int num_lines, const int num_samples,
		const int num_bands, const int lines_samples, float* h_result) {

	float *h_X;
	float *h_X2;
	float *h_eigenvectors;
	float *h_eigenvalues;

	h_X = (float*) malloc(num_lines * num_samples * num_bands * sizeof(float));
	memcpy(h_X, input_image, lines_samples * num_bands * sizeof(float));
	h_X2 = (float*) malloc(num_bands * num_bands * sizeof(float));
	h_eigenvectors = (float*) malloc(num_bands * num_bands * sizeof(float));
	h_eigenvalues = (float*) malloc(num_bands * sizeof(float));

	// cudaHostAlloc((void **)&h_image, num_lines * num_samples * num_bands * sizeof(float),cudaHostAllocDefault);
	// cudaHostAlloc((void **)&h_X2, num_bands * num_bands * sizeof(float),cudaHostAllocDefault);
	// cudaHostAlloc((void **)&h_eigenvectors, num_bands * num_bands * sizeof(float),cudaHostAllocDefault);
	// cudaHostAlloc((void **)&h_eigenvalues, num_bands * sizeof(float),cudaHostAllocDefault);

	// For each individual pixel, mean over bands is shifted to 0
	avg_X(h_X, num_lines, num_samples, num_bands);

	//cblas_sgemm(CblasColMajor, (transa=='t' ? CblasTrans : CblasNoTrans), (transb=='t' ? CblasTrans : CblasNoTrans), m, n, k, alpha, A, lda, B, ldb, beta, C, ldc);


	// Dimensions of h_image in C-style: num_bands x lines_samples.
	// Wrong for here, so treat them as in Column major order, which is actually transposing the matrix.
	//matrix multiplication
	/*         X2 = X' * X                 */
	cblas_sgemm(CblasColMajor, CblasTrans, CblasNoTrans, num_bands, num_bands,
			lines_samples, 1, h_X, lines_samples, h_X, lines_samples, 0, h_X2,
			num_bands);

	//singular value decomposition?? At least computes eigenvalues and eigenvectors of X2.
	svd(num_bands, h_X2, h_eigenvectors, h_eigenvalues);

	//matrix multiplication: multiplies X with eigenvectors(X' * X)
	cblas_sgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, lines_samples,
			num_bands, num_bands, 1, h_X, lines_samples, h_eigenvectors,
			num_bands, 0, h_result, lines_samples);

	// for( i=num_bands*(num_bands-1); i<num_bands*num_bands; i++){
	// printf("h_eigenvectors[%d] = %f\n", i+1, h_eigenvectors[i]);
	// }

	// for( i=0; i<num_bands; i++){
	// printf("h_eigenvalues[%d] = %f\n", i+1, h_eigenvalues[i]);
	// }

	//EscribirResultado(h_image, argv[3], num_lines, num_samples, num_bands);
	free(h_X);
	free(h_X2);
	free(h_eigenvectors);
	free(h_eigenvalues);
}

int nfindr(const float *h_image, const int num_samples,
		const int num_principal_components, const int lines_samples,
		const int g_aleatorios, const char* nfinder_init_file, int* P) {

	//	int p = atoi(argv[4]) + 1;//N� de componentes principales de la imagen reducida + 1
	//	num_bands = atoi(argv[4]);

	const int p = num_principal_components + 1;//N� de componentes principales de la imagen reducida + 1
	int *aleatorios_nfindr;

	//float *image_vector;
	double *MatrixTest;
	double *MatrixTestLU;
	double *matrix;
	double volumeactual = 0;
	int maxit;
	int it;
	double v1, v2;

	double *Vvolume;
	double *HIM2x2c;
	double *HIM2x2;

	double *pixelactual;

	double *Ldet;
	double *Udet;
	double *Pdet;
	double sUdet;

	double *aux2;
	enum bool comp = false;
	double tmp_n;

	int i, j, k, l;

	aleatorios_nfindr = (int*) malloc(sizeof(int) * p);
	//image_vector = (float *) malloc (sizeof(float )*(lines_samples*num_bands));//reservamos espacio para almacenar la imagen
	MatrixTest = (double*) malloc(sizeof(double) * p * p);
	MatrixTestLU = (double*) malloc(sizeof(double) * p * p);
	matrix = (double*) malloc(sizeof(double) * p * p);

	Vvolume = (double*) calloc(lines_samples, sizeof(double));
	HIM2x2c = (double*) calloc(lines_samples * p, sizeof(double));
	HIM2x2 = (double*) malloc(lines_samples * p * sizeof(double));

	pixelactual = (double*) malloc(sizeof(double) * num_principal_components);

	Ldet = (double*) malloc(p * p * sizeof(double));
	Udet = (double*) malloc(p * p * sizeof(double));
	Pdet = (double*) malloc(p * p * sizeof(double));

	aux2 = (double*) malloc(lines_samples * p * sizeof(double));
//	printf("g_aleatorios %d\n", g_aleatorios);
	if (g_aleatorios == 0) {
		//printf("read aleatorios_nfindr from file: %s\n", nfinder_init_file);
		//printf("dimensions: %d x %d\n", p, num_samples);
		//fflush(stdout);
		LeerAleatoriosNfindr(nfinder_init_file, aleatorios_nfindr, p,
				num_samples);
		//printf("nfinder_init_file imported\n");
		//fflush(stdout);
	} else {
		//printf("Generate aleatorios_nfindr\n");
		//fflush(stdout);
		generarAleatoriosNfindr(aleatorios_nfindr, p, lines_samples);
	}

	//Formamos la matriz inicial
	for (k = 0; k < p; k++) {
		P[k] = aleatorios_nfindr[k];
		MatrixTest[k] = 1;
		matrix[k] = 1;
		for (i = 1; i < p; i++) {//de 1 a 19
			MatrixTest[k + (i * p)] = (double) h_image[P[k] + ((i - 1)
					* lines_samples)];
			matrix[k + (i * p)] = (double) h_image[P[k] + ((i - 1)
					* lines_samples)];
		}
	}

	//Calculamos el volumen de la matriz inicial.
	for (k = 0; k < p - 1; k++) {
		for (i = k + 1; i < p; i++) {
			for (j = k + 1; j < p; j++) {
				matrix[p * i + j] -= matrix[p * i + k] * matrix[p * k + j]
						/ matrix[p * k + k];
			}
		}
	}
	double deter = 1.0;
	for (i = 0; i < p; i++) {
		deter *= matrix[p * i + i];
	}

	volumeactual = Absoluto(deter);
	//printf("VOLUMEN INICIAL %e\n", volumeactual);

	maxit = 3 * p;

	it = 1;
	v1 = -1;
	v2 = volumeactual;

	for (i = 0; i < lines_samples; i++) {
		HIM2x2c[i] = 1;
	}
	for (i = 0; i < lines_samples * num_principal_components; i++) {
		HIM2x2c[lines_samples + i] = (double) h_image[i];
	}

	while ((it <= maxit) && (v2 > v1)) {
		//printf("Start iteration # %d, with abs(det(E)) = %e\n", it, v2);
		//printf("Functional abs(det(E)): @ v1= %e @v2= %e, Ratio v1/v2= %e\n", v1, v2, v1/v2);
		memcpy(HIM2x2, HIM2x2c, lines_samples * p * sizeof(double));
		for (k = 0; k < p; k++) {
			memcpy(HIM2x2, HIM2x2c, lines_samples * p * sizeof(double));
			//printf("Loop @ endmember # %d\n", k);

			for (i = 0; i < num_principal_components; i++) {//Cambiamos el endmember k por el endmember p
				pixelactual[i] = MatrixTest[(i + 1) * p + k];
				MatrixTest[(i + 1) * p + k] = MatrixTest[(i + 1) * p + (p - 1)];
				MatrixTest[(i + 1) * p + (p - 1)] = pixelactual[i];
			}

			for (i = 0; i < p; i++) {//ponemos en la ultima columna 0 0 0 ... 1
				MatrixTest[i * p + (p - 1)] = 0;
			}
			MatrixTest[p * p - 1] = 1;

			memcpy(MatrixTestLU, MatrixTest, p * p * sizeof(double));//Para no sobreescribir MatrixTest al hacer la LU

			if(LU(MatrixTestLU, Ldet, Udet, Pdet, p) != 0) {
				free(aleatorios_nfindr);
				free(MatrixTest);
				free(MatrixTestLU);
				free(matrix);

				free(Vvolume);
				free(HIM2x2c);
				free(HIM2x2);

				free(pixelactual);

				free(Ldet);
				free(Udet);
				free(Pdet);
				free(aux2);

				return -1;
			}
			sUdet = 1;
			for (i = 0; i < p; i++) {
				sUdet *= Udet[i * p + i];
			}
			sUdet = Absoluto(sUdet);

			//HIM2c2 = Ldet \ (Pdet * HIM2x2) = inv(Ldet)*(Pdet * HIM2x2)
			InvTri(Ldet, p);//inv(Ldet)

			for (j = 0; j < p; j++) {
				aux2[j] = 0;
				for (l = 0; l < p; l++) {
					aux2[j] += Ldet[(p - 1) * p + l] * Pdet[l * p + j];
				}
			}

			for (j = 0; j < lines_samples; j++) {
				tmp_n = 0;
				for (l = 0; l < p; l++) {
					tmp_n += aux2[l] * HIM2x2[l * lines_samples + j];
				}
				HIM2x2[(p - 1) * lines_samples + j] = tmp_n;
			}

			for (i = 0; i < lines_samples; i++) {
				Vvolume[i] = Absoluto(
						(sUdet / Udet[p * p - 1]) * HIM2x2[(p - 1)
								* lines_samples + i]);
			}

			for (i = 0; i < lines_samples; i++) {
				if (Vvolume[i] > volumeactual) {
					volumeactual = Vvolume[i];
					P[k] = i;
					comp = true;
				}
			}
			if (comp == true) {
				//printf("---> update with pixel @ %d | abs(det(E))= %e\n", P[k], Vvolume[P[k]]);
				for (j = 0; j < num_principal_components; j++) {
					pixelactual[j] = h_image[j * lines_samples + P[k]];
					volumeactual = Vvolume[P[k]];
				}
				comp = false;
			}
			for (i = 0; i < num_principal_components; i++) {
				MatrixTest[(i + 1) * p + (p - 1)] = MatrixTest[(i + 1) * p + k];
				MatrixTest[(i + 1) * p + k] = pixelactual[i];
			}

		}//for k
		//printf("End of iteration # [%d]: abs(det(E))= %e, the pixels are:\n", it, volumeactual);
		for (i = 0; i < p; i++) {
			//printf("Pixel %d -> [%d, %d]\n", i, P[i]/num_samples, P[i]%num_samples);
		}

		it++;
		v1 = v2;
		v2 = volumeactual;
	}//while and

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
	for (i = 0; i < p; i++) {
		//printf("Pixel %d -> [%d, %d]\n", i, P[i]/num_samples, P[i]%num_samples);
	}

	/*	FILE *f_end = fopen(argv[6],"w+");*/
	/*	for(i=0; i<p; i++){*/
	/*		fprintf(f_end, "===== Endmembers %d =====\n", i);*/
	/*		for(j=0; j<num_bands_orig; j++){*/
	/*			fprintf(f_end, "%f\n", image[P[i]+(lines_samples*j)]);*/
	/*		}*/
	/*	}*/
	/*	fclose(f_end);*/

	free(aleatorios_nfindr);
	free(MatrixTest);
	free(MatrixTestLU);
	free(matrix);

	free(Vvolume);
	free(HIM2x2c);
	free(HIM2x2);

	free(pixelactual);

	free(Ldet);
	free(Udet);
	free(Pdet);
	free(aux2);

	return 0;
}

void lsu(const float* image, const int* P, const int num_lines,
		const int num_samples, const int num_bands, const int lines_samples,
		const int num_endmembers, float *Ab) {

	//	const int p = num_endmembers;//N� de componentes principales de la imagen reducida + 1
	int k, l;
	float *e; //E
	float *etxe; //E'*E
	float *etxei; //inv(E'*E)
	float *exetxeit; //E*inv(E'*E)'

	e = (float*) malloc(num_bands * num_endmembers * sizeof(float));
	etxe = (float*) malloc(num_endmembers * num_endmembers * sizeof(float));
	etxei = (float*) malloc(num_endmembers * num_endmembers * sizeof(float));
	exetxeit = (float*) malloc(num_bands * num_endmembers * sizeof(float));

	for (k = 0; k < num_endmembers; k++) {
		for (l = 0; l < num_bands; l++) {
			e[k * num_bands + l] = image[l * lines_samples + P[k]];
		}
	}

	cblas_sgemm(CblasColMajor, CblasTrans, CblasNoTrans, num_endmembers,
			num_endmembers, num_bands, 1, e, num_bands, e, num_bands, 0, etxe,
			num_endmembers);

	matrixInv(num_endmembers, etxe, etxei);

	cblas_sgemm(CblasColMajor, CblasNoTrans, CblasTrans, num_bands,
			num_endmembers, num_endmembers, 1, e, num_bands, etxei,
			num_endmembers, 0, exetxeit, num_bands);

	cblas_sgemm(CblasColMajor, CblasNoTrans, CblasNoTrans, lines_samples,
			num_endmembers, num_bands, 1, image, lines_samples, exetxeit,
			num_bands, 0, Ab, lines_samples);

	free(e);
	free(etxe);
	free(etxei);
	free(exetxeit);
}
