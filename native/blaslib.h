
#ifndef __BLASLIB
#define __BLASLIB

// int isamin(int N, float *X, int INCX);

//extern float snrm2_(int *n, float *x, int* incx);
float Snrm2(int N, float *X, int INCX);

//extern void sgemv_(char *TRANS, int *M, int *N, float *ALPHA, float *A, int *LDA, float *X, int *INCX, float *BETA, float *Y, int *INCY );
void Sgemv(char TRANS, int M, int N, float ALPHA, float *A, int LDA, float *X, int INCX, float BETA, float *Y, int INCY );

void Sscal(int N, float ALPHA, float *X, int INCX);

void Scopy(int N, float *SRC, int INCSRC, float *DST, int INCDST);

void Sgemm(char TRANSA, char TRANSB, int M, int N, int K, float ALPHA, float *A, int LDA, float *B, int LDB, float BETA, float *C, int LDC );

void Dgemm(char TRANSA, char TRANSB, int M, int N, int K, double ALPHA, double *A, int LDA, double *B, int LDB, double BETA, double *C, int LDC );

float Sasum(int N, float *X, int INCX);

void Ssyevr(char JOBZ, char RANGE, char UPLO, int N, float *A, int LDA, 
			float VL, float VU, int IL, int IU, float ABSTOL,
			int *M, float *W, float *Z, int LDZ, int *ISUPPZ, 
			float *WORK, int LWORK, int *IWORK, int LIWORK, int *INFO);

void Dsyevr(char JOBZ, char RANGE, char UPLO, int N, double *A, int LDA, 
			double VL, double VU, int IL, int IU, double ABSTOL,
			int *M, double *W, double *Z, int LDZ, int *ISUPPZ, 
			double *WORK, int LWORK, int *IWORK, int LIWORK, int *INFO);	

/*-------------------------------------------------------------------------------------------------
 SUBROUTINE SSYEVR( JOBZ, RANGE, UPLO, N, A, LDA, VL, VU, IL, IU,
002:      $                   ABSTOL, M, W, Z, LDZ, ISUPPZ, WORK, LWORK,
003:      $                   IWORK, LIWORK, INFO )
004: *
005: *  -- LAPACK driver routine (version 3.2) --
006: *  -- LAPACK is a software package provided by Univ. of Tennessee,    --
007: *  -- Univ. of California Berkeley, Univ. of Colorado Denver and NAG Ltd..--
008: *     November 2006
009: *
010: *     .. Scalar Arguments ..
011:       CHARACTER          JOBZ, RANGE, UPLO
012:       INTEGER            IL, INFO, IU, LDA, LDZ, LIWORK, LWORK, M, N
013:       REAL               ABSTOL, VL, VU
014: *     ..
015: *     .. Array Arguments ..
016:       INTEGER            ISUPPZ( * ), IWORK( * )
017:       REAL               A( LDA, * ), W( * ), WORK( * ), Z( LDZ, * )
018: *     ..
Arguments
082: *  =========
083: *
084: *  JOBZ    (input) CHARACTER*1
085: *          = 'N':  Compute eigenvalues only;
086: *          = 'V':  Compute eigenvalues and eigenvectors.
087: *
088: *  RANGE   (input) CHARACTER*1
089: *          = 'A': all eigenvalues will be found.
090: *          = 'V': all eigenvalues in the half-open interval (VL,VU]
091: *                 will be found.
092: *          = 'I': the IL-th through IU-th eigenvalues will be found.
093: ********** For RANGE = 'V' or 'I' and IU - IL < N - 1, SSTEBZ and
094: ********** SSTEIN are called
095: *
096: *  UPLO    (input) CHARACTER*1
097: *          = 'U':  Upper triangle of A is stored;
098: *          = 'L':  Lower triangle of A is stored.
099: *
100: *  N       (input) INTEGER
101: *          The order of the matrix A.  N >= 0.
102: *
103: *  A       (input/output) REAL array, dimension (LDA, N)
104: *          On entry, the symmetric matrix A.  If UPLO = 'U', the
105: *          leading N-by-N upper triangular part of A contains the
106: *          upper triangular part of the matrix A.  If UPLO = 'L',
107: *          the leading N-by-N lower triangular part of A contains
108: *          the lower triangular part of the matrix A.
109: *          On exit, the lower triangle (if UPLO='L') or the upper
110: *          triangle (if UPLO='U') of A, including the diagonal, is
111: *          destroyed.
112: *
113: *  LDA     (input) INTEGER
114: *          The leading dimension of the array A.  LDA >= max(1,N).
115: *
116: *  VL      (input) REAL
117: *  VU      (input) REAL
118: *          If RANGE='V', the lower and upper bounds of the interval to
119: *          be searched for eigenvalues. VL < VU.
120: *          Not referenced if RANGE = 'A' or 'I'.
121: *
122: *  IL      (input) INTEGER
123: *  IU      (input) INTEGER
124: *          If RANGE='I', the indices (in ascending order) of the
125: *          smallest and largest eigenvalues to be returned.
126: *          1 <= IL <= IU <= N, if N > 0; IL = 1 and IU = 0 if N = 0.
127: *          Not referenced if RANGE = 'A' or 'V'.
128: *
129: *  ABSTOL  (input) REAL
130: *          The absolute error tolerance for the eigenvalues.
131: *          An approximate eigenvalue is accepted as converged
132: *          when it is determined to lie in an interval [a,b]
133: *          of width less than or equal to
134: *
135: *                  ABSTOL + EPS *   max( |a|,|b| ) ,
136: *
137: *          where EPS is the machine precision.  If ABSTOL is less than
138: *          or equal to zero, then  EPS*|T|  will be used in its place,
139: *          where |T| is the 1-norm of the tridiagonal matrix obtained
140: *          by reducing A to tridiagonal form.
141: *
142: *          See "Computing Small Singular Values of Bidiagonal Matrices
143: *          with Guaranteed High Relative Accuracy," by Demmel and
144: *          Kahan, LAPACK Working Note #3.
145: *
146: *          If high relative accuracy is important, set ABSTOL to
147: *          SLAMCH( 'Safe minimum' ).  Doing so will guarantee that
148: *          eigenvalues are computed to high relative accuracy when
149: *          possible in future releases.  The current code does not
150: *          make any guarantees about high relative accuracy, but
151: *          future releases will. See J. Barlow and J. Demmel,
152: *          "Computing Accurate Eigensystems of Scaled Diagonally
153: *          Dominant Matrices", LAPACK Working Note #7, for a discussion
154: *          of which matrices define their eigenvalues to high relative
155: *          accuracy.
156: *
157: *  M       (output) INTEGER
158: *          The total number of eigenvalues found.  0 <= M <= N.
159: *          If RANGE = 'A', M = N, and if RANGE = 'I', M = IU-IL+1.
160: *
161: *  W       (output) REAL array, dimension (N)
162: *          The first M elements contain the selected eigenvalues in
163: *          ascending order.
164: *
165: *  Z       (output) REAL array, dimension (LDZ, max(1,M))
166: *          If JOBZ = 'V', then if INFO = 0, the first M columns of Z
167: *          contain the orthonormal eigenvectors of the matrix A
168: *          corresponding to the selected eigenvalues, with the i-th
169: *          column of Z holding the eigenvector associated with W(i).
170: *          If JOBZ = 'N', then Z is not referenced.
171: *          Note: the user must ensure that at least max(1,M) columns are
172: *          supplied in the array Z; if RANGE = 'V', the exact value of M
173: *          is not known in advance and an upper bound must be used.
174: *          Supplying N columns is always safe.
175: *
176: *  LDZ     (input) INTEGER
177: *          The leading dimension of the array Z.  LDZ >= 1, and if
178: *          JOBZ = 'V', LDZ >= max(1,N).
179: *
180: *  ISUPPZ  (output) INTEGER array, dimension ( 2*max(1,M) )
181: *          The support of the eigenvectors in Z, i.e., the indices
182: *          indicating the nonzero elements in Z. The i-th eigenvector
183: *          is nonzero only in elements ISUPPZ( 2*i-1 ) through
184: *          ISUPPZ( 2*i ).
185: ********** Implemented only for RANGE = 'A' or 'I' and IU - IL = N - 1
186: *
187: *  WORK    (workspace/output) REAL array, dimension (MAX(1,LWORK))
188: *          On exit, if INFO = 0, WORK(1) returns the optimal LWORK.
189: *
190: *  LWORK   (input) INTEGER
191: *          The dimension of the array WORK.  LWORK >= max(1,26*N).
192: *          For optimal efficiency, LWORK >= (NB+6)*N,
193: *          where NB is the max of the blocksize for SSYTRD and SORMTR
194: *          returned by ILAENV.
195: *
196: *          If LWORK = -1, then a workspace query is assumed; the routine
197: *          only calculates the optimal sizes of the WORK and IWORK
198: *          arrays, returns these values as the first entries of the WORK
199: *          and IWORK arrays, and no error message related to LWORK or
200: *          LIWORK is issued by XERBLA.
201: *
202: *  IWORK   (workspace/output) INTEGER array, dimension (MAX(1,LIWORK))
203: *          On exit, if INFO = 0, IWORK(1) returns the optimal LWORK.
204: *
205: *  LIWORK  (input) INTEGER
206: *          The dimension of the array IWORK.  LIWORK >= max(1,10*N).
207: *
208: *          If LIWORK = -1, then a workspace query is assumed; the
209: *          routine only calculates the optimal sizes of the WORK and
210: *          IWORK arrays, returns these values as the first entries of
211: *          the WORK and IWORK arrays, and no error message related to
212: *          LWORK or LIWORK is issued by XERBLA.
213: *
214: *  INFO    (output) INTEGER
215: *          = 0:  successful exit
216: *          < 0:  if INFO = -i, the i-th argument had an illegal value
217: *          > 0:  Internal error

------------------------------------------------------------------------------------------------------*/
		
			
			
#endif


