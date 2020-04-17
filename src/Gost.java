import com.sun.deploy.util.ArrayUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Gost {

    private static int[] key = new int[8];
    private static int R, L;
    private static byte[][] box = new byte[8][16];
    private static int[] LR = new int[]{0xfedcba98, 0x76543210}; //из Vectors

    public static void main(String[] args) throws FileNotFoundException {
        Gost gost = new Gost();
        gost.initBoxAndKeys();
        gost.testTime();

        int[] LRoutput = gost.decrypt(gost.encrypt(LR));

        System.out.println("test (is not failed): " + gost.testChipher(LR, LRoutput));
        System.out.println("output: " + Integer.toHexString(LRoutput[0]) + ' ' + Integer.toHexString(LRoutput[1]));

        System.out.println(Integer.toHexString(gost.tabularSubstitution(0xfdb97531)));
//      должно быть 2a196f34 (A.2.1 Преобразование t)
        System.out.println(Integer.toHexString(gost.F(0x87654321, 0xfedcba98)));
//      должно быть fdcbc20c (A.2.2 Преобразование g)
    }

    Gost() {
        try {
            initBoxAndKeys();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean testChipher(int[] input, int[] output) {
        return input[0] == output[0] && input[1] == output[1];
    }

    private void testTime() {
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

    void initBoxAndKeys() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("box.txt"));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 16; j++) {
                byte b = sc.nextByte();
                box[i][j] = b;
            }
        }

        key[0] = 0xffeeddcc;
        key[1] = 0xbbaa9988;
        key[2] = 0x77665544;
        key[3] = 0x33221100;
        key[4] = 0xf0f1f2f3;
        key[5] = 0xf4f5f6f7;
        key[6] = 0xf8f9fafb;
        key[7] = 0xfcfdfeff;
    }

    int[] encrypt(int[] inputLR) {
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

    int[] decrypt(int[] encriptResult) {
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


    int F(int a, int k) {
        long aL = (a & 0xffffffffL);
        long kL = (k & 0xffffffffL);

        a = (int)(((aL + kL) & 0xffffffffL));
        a = tabularSubstitution(a);
        a = Integer.rotateLeft(a, 11);
        return a;
    }

    int tabularSubstitution(int blockV) {
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

    private byte[] getHalfBytes(int block) {
        byte[] bytes = new byte[8];

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (block & 0x0000000f);
            block = Integer.rotateRight(block, 4);
        }

        return bytes;
    }
}