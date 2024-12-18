public class MatrixMultiplication {
    public static void main(String[] args) {
        int N = 500; 

        int[][] matrixA = new int[N][N];
        int[][] matrixB = new int[N][N];
        int[][] resultMatrix = new int[N][N];

        initializeMatrix(matrixA);
        initializeMatrix(matrixB);

        multiplyMatrices(matrixA, matrixB, resultMatrix);

        System.out.println("Executed...");
    }

    private static void initializeMatrix(int[][] matrix) {
        int N = matrix.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = (int) (Math.random() * 100);
            }
        }
    }

    private static void multiplyMatrices(int[][] matrixA, int[][] matrixB, int[][] resultMatrix) {
        int N = matrixA.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int sum = 0;
                for (int k = 0; k < N; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                resultMatrix[i][j] = sum;
            }
        }
    }
}
