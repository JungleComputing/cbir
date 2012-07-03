
CC = gcc
CFLAGS=-O3 -m64 -fPIC -Wall -D __MKL

INCLUDE_JNI=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
INCLUDE_BLAS=-I$(MKLROOT)/include
LIB_MKL=-L$(MKL) -L$(MKLROOT)/../compiler/lib/intel64

all: libjava_cbir

blaslib.o:
	$(CC) $(CFLAGS) $(INCLUDE_BLAS) -c blaslib.c -o blaslib.o

test: io.o util.o blaslib.o cbir.o test.o
	$(CC) $(CFLAGS) -o test io.o util.o blaslib.o cbir.o test.o \
	$(INCLUDE_BLAS) $(LIB_MKL) \
	 -lmkl_intel_lp64 -lmkl_intel_thread -lmkl_core -liomp5 -lpthread -lm 

libjava_cbir: io.c util.c blaslib.c cbir.c java_cbir.c
	$(CC) $(CFLAGS) -shared -Wl,-soname,libjava_cbir.so -o libjava_cbir.so \
            io.c util.c blaslib.c cbir.c java_cbir.c \
            $(INCLUDE_JNI) $(INCLUDE_BLAS) $(LIB_MKL) \
            -lmkl_intel_lp64 -lmkl_intel_thread -lmkl_core -liomp5 -lpthread -lm 

clean:
	rm -f *.o *core *~ *.so test
	
rebuild: clean test libjava_cbir 
