import java.util.Scanner;

public class HillCipher {
    private static final int MODULUS = 26;
    private static boolean paddingAdded = false;

    public static String encrypt(String plaintext, int[][] keyMatrix) {
        plaintext = plaintext.toUpperCase().replaceAll("[^A-Z]", "");

        // Pad the plaintext if its length is odd
        if (plaintext.length() % 2 != 0) {
            plaintext += "X";
            paddingAdded = true;
        }

        int[][] messageVector = stringToMatrix(plaintext);
        int[][] cipherMatrix = multiplyMatrices(keyMatrix, messageVector);

        return matrixToString(cipherMatrix);
    }

    public static String decrypt(String ciphertext, int[][] keyMatrix) {
        int[][] inverseKey = modInverse(keyMatrix);
        if (inverseKey == null) {
            return "Error: Key matrix is not invertible.";
        }
        int[][] cipherMatrix = stringToMatrix(ciphertext);
        int[][] messageVector = multiplyMatrices(inverseKey, cipherMatrix);
        return matrixToString(messageVector);
    }

    private static int[][] stringToMatrix(String text) {
        int len = text.length();
        int[][] matrix = new int[2][len / 2];

        for (int i = 0; i < len; i++) {
            matrix[i % 2][i / 2] = text.charAt(i) - 'A';
        }

        return matrix;
    }

    private static String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < matrix[0].length; j++) {
            for (int i = 0; i < 2; i++) {
                sb.append((char) ((matrix[i][j] + MODULUS) % MODULUS + 'A'));
            }
        }
        return sb.toString();
    }

    private static int[][] multiplyMatrices(int[][] a, int[][] b) {
        int[][] result = new int[2][b[0].length];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < 2; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
                result[i][j] %= MODULUS;
            }
        }
        return result;
    }

    private static int[][] modInverse(int[][] matrix) {
        int det = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0] + MODULUS) % MODULUS;
        int detInverse = modInverse(det);
        if (detInverse == -1) {
            return null;
        }
        int[][] inverse = new int[2][2];
        inverse[0][0] = (matrix[1][1] * detInverse) % MODULUS;
        inverse[0][1] = (-matrix[0][1] * detInverse + MODULUS) % MODULUS;
        inverse[1][0] = (-matrix[1][0] * detInverse + MODULUS) % MODULUS;
        inverse[1][1] = (matrix[0][0] * detInverse) % MODULUS;
        return inverse;
    }

    private static int modInverse(int a) {
        for (int i = 1; i < MODULUS; i++) {
            if ((a * i) % MODULUS == 1) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[][] key = {{2, 1}, {3, 4}};
//        String plaintext = "hello chickens";
        System.out.println("Enter message: ");
        String plainText = sc.nextLine();
        String ciphertext = encrypt(plainText, key);
        System.out.println("Encrypted: " + ciphertext);

        String decrypted = decrypt(ciphertext, key);
        if(paddingAdded) decrypted = decrypted.substring(0, decrypted.length() - 1);
        System.out.println("Decrypted: " + decrypted);
    }
}