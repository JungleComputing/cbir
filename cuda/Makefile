
NVCC		= nvcc
CC		= gcc

INCLUDE_JNI=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
#CU_FLAGS	= -O3 -g -deviceemu
#CU_FLAGS	= -O3 -g --ptxas-options=-v -arch=compute_13 -code=sm_21 -lcublas
CU_FLAGS	= -O3 -g --ptxas-options=-v -arch=compute_13 -code=sm_21 -lcublas --compiler-options '-fPIC,-m64,-Wall'
CC_FLAGS	= -O3 -m64 -Wall -fPIC $(INCLUDE_JNI)


CU_SOURCES	= cbir_cuda.cu
CC_SOURCES	= test.c

# Additional libraries needed by the project
USECUBLAS       = 1

CU_OBJECTS	= $(CU_SOURCES:%.cu=%.o)
CU_PTX		= $(CU_SOURCES:%.cu=%.ptx)
CC_OBJECTS	= $(CC_SOURCES:%.c=%.o)

all: test

%.o:		%.cu
		$(NVCC) $(CU_FLAGS) -c $< -o $@

%.o:		%.c
		$(CC) $(CC_FLAGS) -c $< -o $@

%.ptx:		%.cu
		$(NVCC) $(CU_FLAGS) --ptx $< -o $@

cbir_cuda: $(CU_OBJECTS) jni_cbir_cuda.o 
		$(NVCC) -shared --linker-options -soname,lib$@.so -lcublas $^ -o lib$@.so

ptx:		$(CU_PTX) 

test: cbir_cuda $(CC_OBJECTS)
		$(CC) test.o -L. -lcbir_cuda -o $@
		
clean:
		rm -f *.o *.so *.ptx test *~
