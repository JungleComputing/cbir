#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

//Funcion: Read_header: Lee la cabecera del fichero .hdr y obtiene las bandas, lineas y samples que tiene
// la imagen.
//Entrada: char filename_header[200]: Nombre del fichero de cabecera
//Salida: variables globales con las lineas, bandas y samples, num_lines, num_bands y num_samples

void Read_header(const char filename_header[200], int *num_lines, int *num_samples, int *num_bands, int *lines_samples, int *data_type){
	FILE *fp;
	char line[20];

	if(strstr(filename_header, ".hdr")==NULL){
		printf("ERROR: El fichero %s no contiene el formato adecuado.Debe tener extension hdr\n", filename_header);
		system("PAUSE");
		exit(1);
	}

	if ((fp=fopen(filename_header,"r"))==NULL){
		printf("ERROR %d. No se ha podido abrir el fichero .hdr de la imagen: %s \n", errno, filename_header);
		system("PAUSE");
		exit(1);
	}
	else{
		fseek(fp,0L,SEEK_SET);
		while(fgets(line, 20, fp)!='\0'){
			if(strstr(line, "samples")!=NULL)
				(*num_samples) = atoi(strtok(strstr(line, " = "), " = "));
			
			if(strstr(line, "lines")!=NULL)

				(*num_lines) = atoi(strtok(strstr(line, " = "), " = "));

			if(strstr(line, "bands")!=NULL)

				(*num_bands) = atoi(strtok(strstr(line, " = "), " = "));

			if(strstr(line, "data type")!=NULL)

				(*data_type) = atoi(strtok(strstr(line, " = "), " = "));

		}//while
		(*lines_samples)=(*num_lines)*(*num_samples);	
		fclose(fp);
	}//else 
}



//Funcion: Load_image: Carga la imagen contenida en el fichero .bsq y la almacena en image_vector.

//Entrada: 	char image_filename[200]: nombre del fichero que contiene las imagen. Formato bsq

//			float *image_vector: Vector que contendra la imagen.

//Salida:	Devuelve un vector float con los datos de la imagen.

void Load_Image(const char image_filename[200], float *h_imagen, int num_lines, int num_samples, int num_bands, int lines_samples, int data_type){
	FILE *fp;
	//FILE *fo;
	short int *tipo_short_int;
	double *tipo_double;
	float *tipo_float;
	int i;

	if(strstr(image_filename, ".bsq")==NULL){
		printf("WARNING: File %s does not have the .bsq extension.\n", image_filename);
//		exit(1);
	}

	if ((fp=fopen(image_filename,"rb"))==NULL){
		printf("ERROR %d. No se ha podido abrir el fichero .bsq que contiene la imagen: %s \n", errno, image_filename);
		exit(1);
	}
	else{
		fseek(fp,0L,SEEK_SET);
		switch(data_type){
			case 5:{ //doubles
				tipo_double = (double *) malloc (num_lines * num_samples * num_bands * sizeof(double));
				fread(tipo_double,1,(sizeof(double)*lines_samples*num_bands),fp); 
		        //printf("Cargados %d bytes.\n", (sizeof(double)*lines_samples*num_bands));
                //Pasamos los datos de la imagen a float
				for(i=0; i<num_lines * num_samples * num_bands; i++){
					h_imagen[i]=(float)tipo_double[i];
				}
				free(tipo_double);
		        //system("PAUSE");
				break;
			}

			case 2:{ //shorts
				tipo_short_int = (short int *) malloc (num_lines * num_samples * num_bands * sizeof(short int));
				fread(tipo_short_int,1,(sizeof(short int)*lines_samples*num_bands),fp); 
		        //printf("Cargados %d bytes.\n", (sizeof(short int)*lines_samples*num_bands));
                //Pasamos los datos de la imagen a float
				for( i=0; i<num_lines * num_samples * num_bands; i++){
					h_imagen[i]=(float)tipo_short_int[i];
				}
				free(tipo_short_int);
		        //system("PAUSE");
				break;
			}

			case 4:{ //floats
				tipo_float = (float *) malloc (num_lines * num_samples * num_bands * sizeof(float));
				fread(tipo_float,1,(sizeof(float)*lines_samples*num_bands),fp); 
		        //printf("Cargados %d bytes.\n", (sizeof(short int)*lines_samples*num_bands));
                //Pasamos los datos de la imagen a float
				for(i=0; i<num_lines * num_samples * num_bands; i++){
					h_imagen[i]=tipo_float[i];
				}
				free(tipo_float);
		        //system("PAUSE");
				break;
			}
		}
		fclose(fp);
	}
}

//Write result
void EscribirResultado( float *imagen, const char resultado_filename[200], int num_lines, int num_samples, int num_bands){
	FILE *fp;
	if ((fp=fopen(resultado_filename,"wb"))==NULL){
		printf("ERROR %d. No se ha podido abrir el fichero resultados: %s \n", errno, resultado_filename);
		system("PAUSE");
		exit(1);
	}
	else{
		fseek(fp,0L,SEEK_SET);
		fwrite(imagen,1,(num_lines * num_samples * num_bands * sizeof(float)),fp); 
	}
	fclose(fp);
}

//read random numbers
void LeerAleatorios(const char *random_filename, float *aleatorios, int n){
	FILE *fp;
	float rdm;
	
	if ((fp=fopen(random_filename,"r"))==NULL){
		printf("ERROR. No se ha podido abrir el fichero de aleatorios: %s \n", random_filename);
		system("PAUSE");
		exit(1);
	}
	else{
		fseek(fp,0L,SEEK_CUR);
		int i=0;
		while (i<n){
			
			fscanf( fp, "%f", &rdm);
			aleatorios[i]=rdm;
			i++;
		}

	}
	fclose(fp);
}


//read random numbers N-Findr
void LeerAleatoriosNfindr(const char *random_filename, int *aleatorios, int n, int num_samples){
	FILE *fp;
	int fila;
	int columna;
	int pixel=0;
    
   if ((fp=fopen(random_filename,"r"))==NULL){
   	printf("No se ha podido abrir el fichero de aleatorios: %s \n", random_filename);
      system("PAUSE");
      exit(1);
   }
   else{
		fseek(fp,0L,SEEK_CUR);
		int i=0;
      while (i<n){
       
   		fscanf( fp, "%d", &fila);
//   		printf("Fila= %d\n", fila);
   		fila--;
   		pixel+=(fila*num_samples);
   		
   		fscanf( fp, "%d", &columna);
//   		printf("Columna= %d\n", columna);
   		columna--;
   		pixel+=columna;
   		
   		aleatorios[i]=pixel;
   		i++;
   		pixel=0;
   	}

	}
	fclose(fp);
}
