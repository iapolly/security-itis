import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Gost {

    private static int[] key = new int[8];
    private static int R, L;
    private static byte[][] box = new byte[8][16];
    private static int[] LR = new int[]{0xfedcba98, 0x76543210}; //из Vectors

    public static void main(String[] args) throws FileNotFoundException {
        initBoxAndKeys();
        testTime();

        int[] LRoutput = decrypt(encrypt(LR));
        System.out.println("test (is not failed): " + testChipher(LR, LRoutput));
        System.out.println("output: " + Integer.toHexString(LRoutput[0]) + ' ' + Integer.toHexString(LRoutput[1]));
    }


    private static boolean testChipher(int[] input, int[] output) {
        return input[0] == output[0] && input[1] == output[1];
    }


    private static void testTime() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            encrypt(LR);
        }
        long finish = System.currentTimeMillis();
        // 0.006 сек
        System.out.println("Encrypt time: " + ((double)(finish - start) )/ 1000);

        start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            decrypt(LR);
        }
        finish = System.currentTimeMillis();
        // 0.02 сек
        System.out.println("Decrypt time: " + ((double)(finish - start)) / 100);
    }

    private static void initBoxAndKeys() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("box.txt"));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 16; j++) {
                byte b = sc.nextByte(16);
                box[i][j] = b;
            }
        }

        key[0] = 0x61733238;
        key[1] = 0x7a773337;
        key[2] = 0x71383339;
        key[3] = 0x37333432;
        key[4] = 0x75693233;
        key[5] = 0x38653274;
        key[6] = 0x77716d32;
        key[7] = 0x65777031;
    }

    private static int[] encrypt(int[] inputLR) {
        L = inputLR[0];
        R = inputLR[1];

        for (int i = 0; i < 31; i++) {
            int j;
            if (i <= 23) {
                j = i % 8;
            }
            else {
                j = 31 - i;
            }

            int V = R;
            R = (L) ^ (F(R, key[j]));
            L = V;
        }
        L = (L) ^ (F(R, key[0]));

        return new int[]{L, R};
    }

    private static int[] decrypt(int[] encriptResult) {
        L = encriptResult[0];
        R = encriptResult[1];

        for (int i = 0; i < 31; i++) {
            int j;
            if (i <= 7) {
                j =  i;
            }
            else {
                j = 7 - (i % 8);
            }

            int V = R;
            R = (L) ^ (F(R, key[j]));
            L = V;
        }
        L = (L) ^ (F(R, key[0]));

        return new int[]{L, R};
    }



    private static int F(int a, int k) {
        a = a ^ k;
        a = tabularSubstitution(a);
        a = Integer.rotateLeft(a, 11);
        return a;
    }

    private static int tabularSubstitution(int blockV) {
        byte[] substBytes = new byte[8];
        byte[] halfBytes = getHalfBytes(blockV);
        for (int i = 0; i < 8; i++) {
            substBytes[i] = box[i][halfBytes[i]];
        }
        int result = 0;
        for (int i = 7; i >= 0; i--) {
            result = Integer.rotateLeft(result, 4);
            result += substBytes[i];
        }
        return result;
    }

    private static byte[] getHalfBytes(int block) {
        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (block & 0x0000000f);
            block = Integer.rotateRight(block, 4);
        }

        return bytes;
    }
}