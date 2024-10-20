import java.util.Arrays;

public class S_DES {
    private static int[] permutationTen(int[] key){
        int[] p10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
        int[] result = new int[10];

        for(int i = 0; i < p10.length; i++){
            result[i] = key[p10[i] - 1];
        }

        return result;
    }

    private static int[] shiftOperation(int[] arr){
        int[] key = arr.clone();
        int mid = key.length / 2;
        int msb = key[0];
        for(int i = 1;i < mid;i++){
            key[i - 1] = key[i];
        }
        key[mid - 1] = msb;

        msb = key[mid];
        for(int i = mid + 1;i < key.length;i++){
            key[i - 1] = key[i];
        }
        key[key.length - 1] = msb;
        return key;
    }

    private static int[] permutationEight(int[] key){
        int[] p8 = {6, 3, 7, 4, 8, 5, 10, 9};

        int[] result = new int[8];

        for(int i = 0;i < p8.length;i++){
            result[i] = key[p8[i] - 1];
        }

        return result;
    }

    public static int[][] generateKeys(int[] key){
        int[][] keys = new int[2][8];
        int[] resultP10 = permutationTen(key);

        int[] shiftOp = shiftOperation(resultP10);
        keys[0] = permutationEight(shiftOp);

        shiftOp = shiftOperation(shiftOp);
        shiftOp = shiftOperation(shiftOp);

        keys[1] = permutationEight(shiftOp);
        return keys;
    }

    private static int[] initialPermutation(int[] plainText){
        int[] ip = {2, 6, 3, 1, 4, 8, 5, 7};

        int[] result = new int[8];

        for(int i = 0;i < ip.length;i++){
            result[i] = plainText[ip[i] - 1];
        }
        System.out.println("ip: " + Arrays.toString(result));
        return result;
    }

    private static int[] expansionPermutation(int[] key){
        int[] ep = {4, 1, 2, 3, 2, 3, 4, 1};

        int[] result = new int[8];

        for(int i = 0;i < ep.length;i++){
            result[i] = key[ep[i] - 1];
        }
        System.out.println("ep: " + Arrays.toString(result));
        return result;
    }

    private static int[] XOR(int[] a, int[] b){
        int[] result = new int[a.length];

        for(int i = 0;i < a.length;i++){
            result[i] = a[i] ^ b[i];
        }
        System.out.println("XOR: " + Arrays.toString(result));
        return result;
    }

    private static int binaryToDec(int[] arr){
        int num = 0;
        for(int i : arr){
            num *= 10;
            num += i;
        }

        int dec = 0;
        int count = 0;
        while (num > 0) {
            int d = num % 10;
            dec += (int) (d * (Math.pow(2, count)));
            num /= 10;
            count++;
        }

        return dec;
    }

    private static int[] decToBinary(int data) {
        int[] binaryNum = new int[2];
        int index = 1;

        while (data > 0) {
            binaryNum[index--] = data % 2;
            data /= 2;
        }

        return binaryNum;
    }

    private static int[] SubstitutionBox(int[] key){
        int[][] s0 = {
                {1, 0, 3, 2},
                {3, 2, 1, 0},
                {0 ,2, 1, 0},
                {3, 1, 3, 2}
        };

        int[][] s1 = {
                {0 ,1, 2, 3},
                {2, 0, 1, 3},
                {3, 0, 1, 0},
                {2, 1, 0, 3}
        };

        int[] result = new int[4];

        int[] lhs = new int[key.length / 2];
        int[] rhs = new int[key.length / 2];

        for(int i = 0;i < key.length / 2;i++){
            lhs[i] = key[i];
            rhs[i] = key[i + (key.length / 2)];
        }

        int[] row_s0 =  new int[2];
        int[] row_s1 =  new int[2];
        int[] col_s0 =  new int[2];
        int[] col_s1 =  new int[2];

        row_s0[0] = lhs[0];
        row_s0[1] = lhs[lhs.length - 1];

        col_s0[0] = lhs[1];
        col_s0[1] = lhs[2];

        row_s1[0] = rhs[0];
        row_s1[1] = rhs[rhs.length - 1];

        col_s1[0] = rhs[1];
        col_s1[1] = rhs[2];

        int row_dec = binaryToDec(row_s0);
        int col_dec = binaryToDec(col_s0);

        System.out.println("s0 Row: " + row_dec + "Col: " + col_dec);

        int data = s0[row_dec][col_dec];
        int[] data_binary = decToBinary(data);

        for(int i = 0;i < data_binary.length;i++){
            result[i] = data_binary[i];
        }

        row_dec = binaryToDec(row_s1);
        col_dec = binaryToDec(col_s1);
        System.out.println("s1 Row: " + row_dec + "Col: " + col_dec);
        data = s1[row_dec][col_dec];
        data_binary = decToBinary(data);
        result[2] = data_binary[0];
        result[3] = data_binary[1];

        System.out.println("sbox: " + Arrays.toString(result));
        return result;
    }

    private static int[] permutationFour(int[] key){
        int[] p4 = {2, 4, 3, 1};

        int[] result = new int[4];

        for(int i = 0;i < p4.length;i++){
            result[i] = key[p4[i] - 1];
        }
        System.out.println("p4: " + Arrays.toString(result));
        return result;
    }

    private static int[] function(int[] rhs, int[] key){
        int[] resultEp = expansionPermutation(rhs);
        int[] resultXOR = XOR(resultEp, key);

        int[] result_Sbox = SubstitutionBox(resultXOR);
        int[] resultP4= permutationFour(result_Sbox);
        return resultP4;
    }

    private static int[] addRHS(int[] a, int[] b){
        int[] newArr = new int[a.length + b.length];
        int idx = 0;
        for(int i = 0;i < a.length;i++){
            newArr[idx++] = a[i];
        }
        for(int i = 0;i < b.length;i++){
            newArr[idx++] = b[i];
        }
        System.out.println("adding Rhs: " + Arrays.toString(newArr));
        return newArr;
    }

    private static int[] swap(int[] key){
        int mid = key.length / 2;
        int i = 0;
        while (mid < key.length) {
            int temp = key[i];
            key[i] = key[mid];
            key[mid] = temp;
            i++;
            mid++;
        }
        System.out.println("swap: " + Arrays.toString(key));
        return key;
    }

    private static int[] inverseInitialPermutation(int[] key){
        int[] ipInv = {4, 1, 3, 5, 7, 2, 8, 6};
        int[] result = new int[8];

        for(int i = 0;i < ipInv.length;i++){
            result[i] = key[ipInv[i] - 1];
        }

        return result;
    }

    public static int[] encryption(int[] plainText ,int[] key1, int[] key2){
        int[] resultIp = initialPermutation(plainText);
        System.out.println("------------------------------------------");

        int[] lhs = new int[resultIp.length / 2];
        int[] rhs = new int[resultIp.length / 2];

        for(int i = 0;i < resultIp.length / 2;i++){
            lhs[i] = resultIp[i];
            rhs[i] = resultIp[i + (resultIp.length / 2)];
        }

        int[] resultFx = function(rhs, key1);

        int[] resultXOR = XOR(lhs, resultFx);

        int[] resultAddRHS = addRHS(resultXOR, rhs);

        System.out.println("------------------------------------------");

        int[] resultSwap = swap(resultAddRHS);

        System.out.println("------------------------------------------");


        lhs = new int[resultSwap.length / 2];
        rhs = new int[resultSwap.length / 2];

        for(int i = 0;i < resultSwap.length / 2;i++){
            lhs[i] = resultSwap[i];
            rhs[i] = resultSwap[i + (resultSwap.length / 2)];
        }

        resultFx = function(rhs, key2);

        resultXOR = XOR(lhs, resultFx);
        System.out.println("------------------------------------------");

        resultAddRHS = addRHS(resultXOR, rhs);
        System.out.println("------------------------------------------");

        int[] resultIpInverse = inverseInitialPermutation(resultAddRHS);
        return resultIpInverse;
    }

    public static int[] decryption(int[] cipherText, int[] key1, int[] key2){
        return encryption(cipherText, key2, key1);
    }

    public static void main(String[] args) {
        int[] plainText = {1, 0, 1, 0, 0, 1, 0, 1};
        int[] key = {0, 0, 1, 0, 0, 1, 0, 1, 1, 1};


        int[][] keys = generateKeys(key);
        System.out.println("key1: " + Arrays.toString(keys[0]));
        System.out.println("key2: " + Arrays.toString(keys[1]));

        System.out.println(  );
        System.out.println("\t\t\tEncryption");
        System.out.println(  );
        int[] cipherText = encryption(plainText ,keys[0] ,keys[1]);

        System.out.println("------------------------------------------");
        System.out.println("Cipher Text: " + Arrays.toString(cipherText));
        System.out.println("------------------------------------------");
        System.out.println("------------------------------------------");


        System.out.println(  );
        System.out.println(  );
        System.out.println(  );
        System.out.println("\t\t\tDecryption");
        System.out.println(  );
        int[] decrypted_Text = decryption(cipherText, keys[0], keys[1]);
        System.out.println("------------------------------------------");
        System.out.println("------------------------------------------");
        System.out.println("Decrypted text: " + Arrays.toString(decrypted_Text));
        System.out.println("------------------------------------------");
        System.out.println("------------------------------------------");
    }
}
