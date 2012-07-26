#	0.-	Ruta ejecutable.
#	1.-	Ruta imagen .hdr
#	2.-	Ruta imagen .bsq
#	3.-	Ruta imagen resultado.
#	4.-	Nº de Principals Components.
#	5.-	Fichero con pixels aleatorios. Inicialización NFINDR.
#	6.- Fichero con endmembers resultados
#	7.- 0 -> Cargar aleatorios Nfindr de fichero
#		1 -> Generar aleatorios Nfindr
#	8.- Ruta imagen desmezclada

./test ../Cuprite.hdr ../Cuprite.bsq ../../run/RESULTADOS/PCA/PCA_Cuprite_SERIE 18 random.txt endmembers.txt 1 ../CC_PCA_SERIE_Cuprite_unmix_test
