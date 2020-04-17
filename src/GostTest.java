import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GostTest {

    private Gost gost = new Gost();
    private List<TestData> data;

    @Test
    void testChipher() {
        data = new ArrayList<>();
//        (A 2.4) это номер главы из которой взяты значения
        data.add(new TestData(new int[]{0xfedcba98, 0x76543210}, new int[]{0x4ee901e5, 0xc2d8ca3d}));

        for (TestData d: data) {
            int[] output = gost.encrypt(d.input);
            assertEquals(output[0], d.output[0]);
            assertEquals(output[1], d.output[1]);
        }
    }

    @Test
    void testTabular() {
//        (A.2.1 Преобразование t)
        String output = Integer.toHexString(gost.tabularSubstitution(0xfdb97531));
        assertEquals(output, "2a196f34");
    }

    @Test
    void testF() {
//        (A.2.2 Преобразование g)
        String output = Integer.toHexString(gost.F(0x87654321, 0xfedcba98));
        assertEquals(output, "fdcbc20c");
    }

    class TestData {
        int[] input;
        int[] output;

        TestData(int[] input, int[] output) {
            this.input = input;
            this.output = output;
        }
    }
}