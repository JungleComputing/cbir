//Parámetros de la ejecución;
//	1.-	Ruta de la cabecera de la imagen .hdr
//	2.-	Ruta de la imagen .bsq
// 	3.-	Ruta de la imagen resultado
//	4.-	Número de componentes principales
// 	5.-	Número de iteraciones
//	6.-	Número de hilos

// includes, system
#include <stdio.h>
//#include <assert.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
//#include <math.h>
#include <time.h>
#include "cbir_cuda.h"
//#include "cutil.h"
#include <sys/time.h>

#define THRESHOLD 0.00001

#define MIN_DOUBLE ((double) (1 << (sizeof(double) * 8 - 1)))//Minimum double
#define EPS 1.0e-10//Minimim distance between two numbers
#define ABS(a) (((a) < 0) ? -(a) : (a))


//Funcion: Read_header: Lee la cabecera del fichero .hdr y obtiene las bandas, lineas y samples que tiene
// la imagen.
//Entrada: char filename_header[200]: Nombre del fichero de cabecera
//Salida: variables globales con las lineas, bandas y samples, num_lines, num_bands y num_samples
static void Read_header(char *filename_header, int *num_samples, int *num_lines,
		int *num_bands, int *data_type) {
	FILE *fp;
	char line[20];

	if (strstr(filename_header, ".hdr") == NULL) {
		printf(
				"ERROR: El fichero %s no contiene el formato adecuado.Debe tener extension hdr\n",
				filename_header);
		int status = system("PAUSE");
		exit(1);
	}
	if ((fp = fopen(filename_header, "r")) == NULL) {
		printf(
				"ERROR %d. No se ha podido abrir el fichero .hdr de la imagen: %s \n",
				errno, filename_header);
		int status = system("PAUSE");
		exit(1);
	} else {
		fseek(fp, 0L, SEEK_SET);
		while (fgets(line, 20, fp) != '\0') {
			if (strstr(line, "samples") != NULL)
				*num_samples = atoi(strtok(strstr(line, " = "), " = "));

			if (strstr(line, "lines") != NULL)
				*num_lines = atoi(strtok(strstr(line, " = "), " = "));

			if (strstr(line, "bands") != NULL)
				*num_bands = atoi(strtok(strstr(line, " = "), " = "));

			if (strstr(line, "data type") != NULL)
				*data_type = atoi(strtok(strstr(line, " = "), " = "));

		}//while
		fclose(fp);
	}//else
}

//Funcion: Load_image: Carga la imagen contenida en el fichero .bsq y la almacena en image_vector.
//Entrada: 	char image_filename[200]: nombre del fichero que contiene las imagen. Formato bsq
//			float *image_vector: Vector que contendra la imagen.
//Salida:	Devuelve un vector float con los datos de la imagen.
static float *Load_Image(char image_filename[200], float *h_imagen, int num_samples,
		int num_lines, int num_bands, long int lines_samples, int data_type) {
	FILE *fp;
	//FILE *fo;
	short int *tipo_short_int;
	double *tipo_double;
	float *tipo_float;

	if (strstr(image_filename, ".bsq") == NULL) {
		printf(
				"ERROR: El fichero %s no contiene el formato adecuado. Debe tener extension bsq\n",
				image_filename);
		exit(1);
	}
	if ((fp = fopen(image_filename, "rb")) == NULL) {
		printf(
				"ERROR %d. No se ha podido abrir el fichero .bsq que contiene la imagen: %s \n",
				errno, image_filename);
		exit(1);
	} else {
		fseek(fp, 0L, SEEK_SET);
		switch (data_type) {
		case 5: {
			tipo_double = (double *) malloc(
					num_lines * num_samples * num_bands * sizeof(double));
			size_t bytes = fread(tipo_double, 1,
					(sizeof(double) * lines_samples * num_bands), fp);
			//printf("Cargados %d bytes.\n", (sizeof(double)*lines_samples*num_bands));
			//Pasamos los datos de la imagen a float
			int i;
			for (i = 0; i < num_lines * num_samples * num_bands; i++) {
				h_imagen[i] = (float) tipo_double[i];
			}
			free(tipo_double);
			//system("PAUSE");
			break;
		}
		case 2: {
			tipo_short_int = (short int *) malloc(
					num_lines * num_samples * num_bands * sizeof(short int));
			size_t bytes = fread(tipo_short_int, 1,
					(sizeof(short int) * lines_samples * num_bands), fp);
			//printf("Cargados %d bytes.\n", (sizeof(short int)*lines_samples*num_bands));
			//Pasamos los datos de la imagen a float
			int i;
			for (i = 0; i < num_lines * num_samples * num_bands; i++) {
				h_imagen[i] = (float) tipo_short_int[i];
			}
			free(tipo_short_int);
			//system("PAUSE");
			break;
		}
		case 4: {
			tipo_float = (float *) malloc(
					num_lines * num_samples * num_bands * sizeof(float));
			size_t bytes = fread(tipo_float, 1,
					(sizeof(float) * lines_samples * num_bands), fp);
			//printf("Cargados %d bytes.\n", (sizeof(short int)*lines_samples*num_bands));
			//Pasamos los datos de la imagen a float
			int i;
			for (i = 0; i < num_lines * num_samples * num_bands; i++) {
				h_imagen[i] = tipo_float[i];
			}
			free(tipo_float);
			//system("PAUSE");
			break;
		}
		}
		fclose(fp);
	}
	return h_imagen;
}

static double elapsedTime(struct timeval *start, struct timeval *end) {
	double t_sec, t_usec;
	t_sec = (double) (end->tv_sec - start->tv_sec);
	t_usec = (double) (end->tv_usec - start->tv_usec);
	return t_sec + t_usec / 1.0e+6;
}

static void EscribirResultado(float *imagen, char resultado_filename[200], int n_pc,
		int num_samples, int num_lines, int num_bands, long int lines_samples) {
	FILE *fp;
	if ((fp = fopen(resultado_filename, "wb")) == NULL) {
		printf("ERROR %d. No se ha podido abrir el fichero resultados: %s \n",
				errno, resultado_filename);
		int status = system("PAUSE");
		exit(1);
	} else {
		int result = fseek(fp, 0L, SEEK_SET);
		size_t bytes = fwrite(imagen, 1, (num_lines * num_samples * n_pc * sizeof(float)), fp);
	}
	fclose(fp);
}

int main(int argc, char** argv)
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////              SPCA               /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
{
	struct timeval t_ini, t_fin, t_start;
	double t_nfindr, t_lsu, t_spca, t_total;
	float* h_image;
	float* h_B;
	int* P;

	int init = initCuda();
	if (init != 0) {
		//failure
		return init;
	}

	//Header file reading
	// Variables globales

	int num_samples;//Numero de samples
	int num_lines;//Numero de lineas
	int num_bands;//Numero de bandas
	int data_type;//Tipo de datos
	long int lines_samples;

	char* header_file = argv[1];
	char* image_file = argv[2];

	char* random_vector_file = argv[4];
	int num_principal_components = atoi(argv[5]);
	int num_endmembers = num_principal_components +1;
	int n_it = atoi(argv[6]);
	int generate = atoi(argv[7]);
	int it_fijas = atoi(argv[8]);
	char* aleatorios_Nfindr_file = argv[9];

	int g_aleatorios = atoi(argv[11]);
	char* unmixed_image_file = argv[12];

	gettimeofday(&t_start, NULL);

	Read_header(header_file, &num_samples, &num_lines, &num_bands, &data_type);
	lines_samples = num_lines * num_samples; // num_lines*num_samples

	//Image reading
	h_image = (float*) malloc(lines_samples * num_bands * sizeof(float));
	Load_Image(image_file, h_image, num_samples, num_lines, num_bands,
			lines_samples, data_type);

	gettimeofday(&t_ini, NULL);
	h_B = (float*) malloc(lines_samples * num_principal_components * sizeof(float));
//	printf("SPCA\n");
//	fflush(stdout);

	spca(h_image, num_samples, num_lines, num_bands, lines_samples, num_principal_components, generate,
			random_vector_file, it_fijas, n_it, h_B);
	gettimeofday(&t_fin, NULL);
	t_spca = elapsedTime(&t_ini, &t_fin);
	gettimeofday(&t_ini, NULL);
	P = (int*) malloc(sizeof(int) * num_endmembers);
//	printf("NFindr\n");
//	fflush(stdout);
	NFindr(h_B, num_samples, num_principal_components, lines_samples, g_aleatorios, aleatorios_Nfindr_file, P);
	gettimeofday(&t_fin, NULL);
	t_nfindr = elapsedTime(&t_ini, &t_fin);
	gettimeofday(&t_ini, NULL);
//	printf("lsu\n");
//	fflush(stdout);
	float *abundance_map = (float*) malloc(num_lines * num_samples * num_endmembers * sizeof(float));
	lsu(h_image, num_samples, num_lines, num_bands, lines_samples, P, num_endmembers, unmixed_image_file);
	//Write the result to a file in disk.
	EscribirResultado(abundance_map, unmixed_image_file, num_endmembers, num_samples, num_lines, num_bands, lines_samples);

	gettimeofday(&t_fin, NULL);
//	printf("done\n");
//	fflush(stdout);
	t_lsu = elapsedTime(&t_ini, &t_fin);
	t_total = elapsedTime(&t_start, &t_fin);
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////              SPCA               /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////              NFINDR             /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////              LSU                /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	printf("SPCA_RUI_NEW\t%f\n", t_spca);
	printf("NFINDR\t%f\n", t_nfindr);//mostramos el tiempo por pantalla
	printf("LSU\t%f\n", t_lsu);//mostramos el tiempo por pantalla
	printf("TOTAL\t%f\n", t_total);//mostramos el tiempo por pantalla
	return 0;
}

