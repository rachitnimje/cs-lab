import java.util.Arrays;

public class SimplifiedAES {

    private static final int[][] S_BOX = {
            {9, 4, 10, 11, 13, 1, 8, 5, 6, 2, 0, 3, 12, 14, 15, 7},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}
    };

    private static final int[][] INV_S_BOX = {
            {10, 5, 9, 11, 1, 7, 8, 15, 6, 0, 2, 3, 12, 4, 13, 14},
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}
    };

    private static final int[] RCON = {1, 2};

    private static final int[] MIX_COLUMNS = {1, 4};

    private static final int[] INV_MIX_COLUMNS = {9, 2};

    public static void main(String[] args) {
        int[] plaintext = {0x6, 0x5, 0x0, 0xC};  // example plaintext (64 bits)
        int[] key = {0xA, 0x9, 0x4, 0x6};        // example key (16 bits)

        int[] encrypted = encrypt(plaintext, key);
        System.out.println("Encrypted: " + Arrays.toString(encrypted));

        int[] decrypted = decrypt(encrypted, key);
        System.out.println("Decrypted: " + Arrays.toString(decrypted));
    }

    private static int[] encrypt(int[] plaintext, int[] key) {
        int[][] roundKeys = keyExpansion(key);

        int[] state = addRoundKey(plaintext, roundKeys[0]);

        for (int i = 1; i < roundKeys.length - 1; i++) {
            state = subBytes(state);
            state = shiftRows(state);
            state = mixColumns(state, MIX_COLUMNS);
            state = addRoundKey(state, roundKeys[i]);
        }

        state = subBytes(state);
        state = shiftRows(state);
        state = addRoundKey(state, roundKeys[roundKeys.length - 1]);

        return state;
    }

    private static int[] decrypt(int[] ciphertext, int[] key) {
        int[][] roundKeys = keyExpansion(key);

        int[] state = addRoundKey(ciphertext, roundKeys[roundKeys.length - 1]);

        for (int i = roundKeys.length - 2; i > 0; i--) {
            state = invShiftRows(state);
            state = invSubBytes(state);
            state = addRoundKey(state, roundKeys[i]);
            state = mixColumns(state, INV_MIX_COLUMNS);
        }

        state = invShiftRows(state);
        state = invSubBytes(state);
        state = addRoundKey(state, roundKeys[0]);

        return state;
    }

    private static int[][] keyExpansion(int[] key) {
        int[][] roundKeys = new int[3][key.length];

        roundKeys[0] = key;

        for (int i = 1; i < roundKeys.length; i++) {
            int[] temp = new int[key.length];
            temp[0] = key[0] ^ S_BOX[0][key[3]] ^ RCON[i - 1];
            temp[1] = key[1] ^ S_BOX[0][key[0]];
            temp[2] = key[2] ^ S_BOX[0][key[1]];
            temp[3] = key[3] ^ S_BOX[0][key[2]];

            roundKeys[i] = temp;
        }

        return roundKeys;
    }

    private static int[] addRoundKey(int[] state, int[] roundKey) {
        int[] result = new int[state.length];
        for (int i = 0; i < state.length; i++) {
            result[i] = state[i] ^ roundKey[i];
        }
        return result;
    }

    private static int[] subBytes(int[] state) {
        int[] result = new int[state.length];
        for (int i = 0; i < state.length; i++) {
            result[i] = S_BOX[0][state[i]];
        }
        return result;
    }

    private static int[] invSubBytes(int[] state) {
        int[] result = new int[state.length];
        for (int i = 0; i < state.length; i++) {
            result[i] = INV_S_BOX[0][state[i]];
        }
        return result;
    }

    private static int[] shiftRows(int[] state) {
        return new int[]{state[0], state[1], state[3], state[2]};
    }

    private static int[] invShiftRows(int[] state) {
        return new int[]{state[0], state[1], state[3], state[2]};
    }

    private static int[] mixColumns(int[] state, int[] mixColumns) {
        int[] result = new int[state.length];
        result[0] = galoisMult(state[0], mixColumns[0]) ^ galoisMult(state[2], mixColumns[1]);
        result[1] = galoisMult(state[1], mixColumns[0]) ^ galoisMult(state[3], mixColumns[1]);
        result[2] = galoisMult(state[2], mixColumns[0]) ^ galoisMult(state[0], mixColumns[1]);
        result[3] = galoisMult(state[3], mixColumns[0]) ^ galoisMult(state[1], mixColumns[1]);
        return result;
    }

    private static int galoisMult(int a, int b) {
        int p = 0;
        for (int counter = 0; counter < 4; counter++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            boolean hi_bit_set = (a & 0x8) != 0;
            a <<= 1;
            if (hi_bit_set) {
                a ^= 0x3;
            }
            b >>= 1;
        }
        return p % 16;
    }
}