/*********************************************************************************************************
 *
 *	Reading and writing files functions
 *
 *********************************************************************************************************/

#ifndef __CBIR_IO
#define __CBIR_IO

void Read_header(const char *filename_header, int *num_lines, int *num_samples, int *num_bands, int *lines_samples, int *data_type);
void Load_Image(const char *image_filename, float *h_imagen, int num_lines, int num_samples, int num_bands, int lines_samples, int data_type);
void EscribirResultado(const float *imagen, const char *resultado_filename, int num_lines, int num_samples, int num_bands);
void LeerAleatorios(const char *random_filename, float *aleatorios, int n);
void LeerAleatoriosNfindr(const char *random_filename, int *aleatorios, int n, int num_samples);

#endif
