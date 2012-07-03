/* Includes, system */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <sys/time.h>


#include "io.h"
#include "cbir.h"

double elapsedTime(struct timeval *start, struct timeval *end) {
	double t_sec, t_usec;
	t_sec = (double) (end->tv_sec - start->tv_sec);
	t_usec = (double) (end->tv_usec - start->tv_usec);
	return t_sec + t_usec / 1.0e+6;
}

/* Main */
int main(int argc, char** argv) {

	//timing variables
	struct timeval t_ini, t_fin;
	double t_rd, t_pca, t_nfindr, t_lsu, t_wd, t_total;

	// command line arguments
	char* header_file = argv[1];
	char* data_file = argv[2];
	char* result_file = argv[3]; //unused right now?
	int num_principal_components = atoi(argv[4]);
	int num_endmembers = num_principal_components + 1;
	char* nfinder_init_file = argv[5];
	char* nfinder_endmembers_file = argv[6];
	int g_aleatorios = atoi(argv[7]);
	char* unmixed_image_file = argv[8];

	float *input_image;
	float *pca_result;

	/*************************************
	 Data dimensions variables
	 **************************************/
	int num_lines, num_samples, num_bands, lines_samples, data_type;
	/*************************************
	 Data dimensions variables
	 **************************************/
	gettimeofday(&t_ini, NULL);
	Read_header(header_file, &num_lines, &num_samples, &num_bands,
			&lines_samples, &data_type);

	input_image = (float*) malloc(num_lines * num_samples * num_bands * sizeof(float));
	Load_Image(data_file, input_image, num_lines, num_samples, num_bands, lines_samples, data_type);

	pca_result = (float*) malloc(num_lines * num_samples * num_bands * sizeof(float));

	//printf("lines = %d\nsamples = %d\nbands = %d\npixels = %d\ndata_type = %d\n", num_lines, num_samples, num_bands, lines_samples, data_type);
	gettimeofday(&t_fin, NULL);
	t_rd = elapsedTime(&t_ini, &t_fin);

	gettimeofday(&t_ini, NULL);
	pca(input_image, num_lines, num_samples, num_bands, lines_samples, pca_result);
	gettimeofday(&t_fin, NULL);
	t_pca = elapsedTime(&t_ini, &t_fin);

	gettimeofday(&t_ini, NULL);
	int* P = (int*) malloc(sizeof(int) * num_endmembers);
	nfindr(pca_result, num_samples, num_principal_components, lines_samples, g_aleatorios, nfinder_init_file, P);
	gettimeofday(&t_fin, NULL);
	t_nfindr = elapsedTime(&t_ini, &t_fin);

	free(pca_result);

	gettimeofday(&t_ini, NULL);
	float *Ab; //Abundance maps
	Ab = (float*) malloc(num_lines * num_samples * num_endmembers * sizeof(float));
	lsu(input_image, P, num_lines, num_samples, num_bands, lines_samples, num_endmembers, Ab);
	gettimeofday(&t_fin, NULL);
	t_lsu = elapsedTime(&t_ini, &t_fin);

	gettimeofday(&t_ini, NULL);
	EscribirResultado(Ab, unmixed_image_file, num_lines, num_samples, num_endmembers);
	gettimeofday(&t_fin, NULL);
	t_wd = elapsedTime(&t_ini, &t_fin);

	free(Ab);
	free(P);
	free(input_image);

	t_total = t_rd + t_pca + t_nfindr + t_lsu + t_wd;

	printf("READ\t%f\n", t_rd);
	printf("PCA\t%f\n", t_pca);
	printf("NFINDR\t%f\n", t_nfindr);//mostramos el tiempo por pantalla
	printf("LSU\t%f\n", t_lsu);//mostramos el tiempo por pantalla
	printf("WRITE\t%f\n", t_wd);
	printf("TOTAL\t%f\n", t_total);//mostramos el tiempo por pantalla

	return 0;
}
