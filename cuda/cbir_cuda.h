#ifndef __CBIR_CUDA
#define __CBIR_CUDA

#ifdef __cplusplus
extern "C" {
#endif

int initCuda();

int spca(const float* h_image, const int num_lines, const int num_samples,
		const int num_bands, const int lines_samples, const int n_pc,
		const int generate, const char* random_vector_file, const int fixed_n_iterations,
		const int n_iterations, float* h_B);

int nfindr(const float* h_image, const int num_samples, const int n_pc,
		const int lines_samples, const int g_aleatorios,
		const char* nfinder_init_file, int *P);

void lsu(const float* image, const int* P, const int num_lines,
		const int num_samples, const int num_bands, const int lines_samples,
		const int num_endmembers, float* abundance_map);

#ifdef __cplusplus
}
#endif

#endif
