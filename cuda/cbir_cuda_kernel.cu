
#define N  30
#define TAMANIO_MATRIZ_C 224
#define BLOCKSIZE_MEDIA 512

#define ABS(a)	   (((a) < 0) ? -(a) : (a))

////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
//											Kernels SPCA							  //
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////

__global__ void NormalizeX(float* d_image, float *d_pixel, int num_bands, int num_lines, int num_samples, int iterations){
	__shared__ float sdata[BLOCKSIZE_MEDIA];
	__shared__ float smean[1];
	__shared__ float svar[1];
	int it, s;
	unsigned int tid = threadIdx.x;
	int element;
	if(tid==0){
		smean[0]=0;
		svar[0]=0;
	}
	
	for (it=0; it<iterations; it++){
		element=(num_lines*num_samples*blockIdx.x)+(blockDim.x*it);
		if((it*blockDim.x)+tid<num_lines*num_samples){
			sdata[tid]=d_image[element+tid];
		}
		else{
			sdata[tid]=0;
		}
		__syncthreads();
		
		for(s=blockDim.x/2; s>0; s=s/2){
			if (tid < s){
				sdata[tid]+=sdata[tid+s];
			}
			__syncthreads();
		}
		
		if(tid==0){
			smean[0]+=sdata[0];
		}
		__syncthreads();
				
	}
	if(tid==0){
		smean[0]/=(num_lines*num_samples);
		
	}
	__syncthreads();
	
	
	for (it=0; it<iterations; it++){
		element=(num_lines*num_samples*blockIdx.x)+(blockDim.x*it);
		if((it*blockDim.x)+tid<num_lines*num_samples){
			sdata[tid]=(d_image[element+tid]-smean[0])*(d_image[element+tid]-smean[0]);
		}
		else{
			sdata[tid]=0;
		}
		__syncthreads();
		
		for(s=blockDim.x/2; s>0; s=s/2){
			if (tid < s){
				sdata[tid]+=sdata[tid+s];
			}
			__syncthreads();
		}
		
		if(tid==0){
			svar[0]+=sdata[0];
		}
		__syncthreads();
				
	}
	if(tid==0){
		svar[0]/=((num_lines*num_samples)-1);
		//d_pixel[blockIdx.x]=svar[0];
		
	}
	__syncthreads();

	for (it=0; it<iterations; it++){
		element=(num_lines*num_samples*blockIdx.x)+(blockDim.x*it);
		if((it*blockDim.x)+tid<num_lines*num_samples){
			d_image[element+tid]/=svar[0];
		}
	}

}


////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
//											Kernels NFINDR							  //
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////



/*Este kernel calcula el volumen conseguido al agregar cada pixel al conjunto de endmembers.
Realiza la multiplicaci�n de d_aux * d_HIM2x2, ademas calcula el valor absoluto de cada volumen.*/

/*This kernel compute the getting volume obtained by adding each pixel to the set of 
endmembers. It makes the multiplication d_VVolume = d_aux * d_HIM2x2, also it gets 
the absolute value of each volume. */
__global__ void VolumeCalculation(double *d_aux, double *d_HIM2x2, double *d_Vvolume, 
											 double tmp2,int lines_samples, int n_end){
	int idx =  blockDim.x * blockIdx.x+threadIdx.x;
	__shared__ double s_aux[N];
	double a;
	if (idx<lines_samples){
		if(threadIdx.x<n_end){
			s_aux[threadIdx.x]=d_aux[threadIdx.x];
		}
		syncthreads();
		a=0;
		for(int i=0; i<n_end; i++){
			a+=s_aux[i]*d_HIM2x2[i*lines_samples+idx];
		}
		a=a*tmp2;
		d_Vvolume[idx]=ABS(a);
	}
}

/*Este kernel obtiene los I volumenes mayores calculados en el kernel anterior siendo I el n�mero
de bloques con que se estructura el lanzamiento del kernel. Adem�s obtiene los �ndices de los pixel
que otienen dichos volumenes.*/

/*This kernel gets the I higher volumes obtained by VolumeCalculation kernel, 
where I is the number of blocks that we configure the kernel launch. Also gets the index 
of the pixels that get this volumes.*/


__global__ void Reduction_vol(double *d_Vvolume, double *d_volumenes, int *d_indices, int lines_samples){

	__shared__ double s_v[512];
	__shared__ int s_i[512];

	unsigned int tid = threadIdx.x;
	unsigned int i = blockIdx.x * (blockDim.x * 2) + threadIdx.x;

	if((i+blockDim.x)>=lines_samples){
			s_v[tid]=d_Vvolume[i];
			s_i[tid]=i;
	}
	else{
		if(d_Vvolume[i]>d_Vvolume[i + blockDim.x]){
			s_v[tid]=d_Vvolume[i];
			s_i[tid]=i;
		}
		else{
			s_v[tid]=d_Vvolume[i + blockDim.x];
			s_i[tid]=i+ blockDim.x;
		}
	}
	__syncthreads();

	for (unsigned int s = blockDim.x / 2; s > 0; s>>=1){
		if (tid < s){
			if(s_v[tid]<=s_v[tid+s]){
				s_v[tid]=s_v[tid+s];
				s_i[tid]=s_i[tid+s];
			}
		}
		__syncthreads();
	}
	d_volumenes[blockIdx.x]=s_v[0];
	d_indices[blockIdx.x]=s_i[0];
	__syncthreads();
}

////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
//											Kernels Unmixing						  //
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////



/*Este kernel realiza la fase final del unmixing, es decir multiplicar cada pixel por la
matriz de c�mputo obtenida a partir de la matriz de endmembers, y as� obtener las abundancias.*/

/*This kernel multiplicates the compute_matrix by each pixel of the hyperspectral image, 
thus obtain a set of abundance vectors , each contain the fractional abundances of the 
endmembers in each pixel.*/
__global__ void Unmixing(float *d_imagen, float *d_imagen_unmixing,float *d_matriz_computo, int num_lines, int num_samples, int num_bands, int N_END)

{
	int pixel =  blockDim.x * blockIdx.x+threadIdx.x;
	
	__shared__ float matriz_c[TAMANIO_MATRIZ_C];
	float l_pixel[TAMANIO_MATRIZ_C];
	float a;
	if(pixel<num_lines*num_samples){
		for(int t=0; t<num_bands; t++){
			l_pixel[t]=d_imagen[pixel+(num_lines*num_samples*t)];
		}
		for(int it=0; it<N_END; it++){
			if(threadIdx.x==0){
				for(int i=0; i<num_bands; i++){
					matriz_c[i]=d_matriz_computo[it*num_bands+i];
				}
			}
			syncthreads();
			a=0;
			for(int k=0; k<num_bands; k++){	
				a+=matriz_c[k]*l_pixel[k];
			}
			d_imagen_unmixing[pixel+(num_lines*num_samples*it)]=a;
		}
	}

}
