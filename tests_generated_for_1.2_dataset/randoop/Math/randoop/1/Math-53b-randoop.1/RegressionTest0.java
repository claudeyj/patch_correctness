import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest0 {

    public static boolean debug = false;

    @Test
    public void test001() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test001");
        int i2 = org.apache.commons.math.util.MathUtils.pow((int) (byte) 10, (long) ' ');
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test002() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test002");
        double d1 = org.apache.commons.math.util.MathUtils.factorialLog((int) 'a');
        org.junit.Assert.assertTrue(d1 == 349.9541180407703d);
    }

    @Test
    public void test003() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test003");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) (byte) 1, (double) ' ', (double) 1);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test004() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test004");
        try {
            long long1 = org.apache.commons.math.util.MathUtils.factorial((int) '4');
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test005() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test005");
        int i2 = org.apache.commons.math.util.MathUtils.lcm((int) (byte) -1, (int) (short) 100);
        org.junit.Assert.assertTrue(i2 == 100);
    }

    @Test
    public void test006() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test006");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 1.0f, 10);
        org.junit.Assert.assertTrue(d2 == 1.0d);
    }

    @Test
    public void test007() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test007");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) ' ');
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test008() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test008");
        int i1 = org.apache.commons.math.util.MathUtils.hash((double) (-1.0f));
        org.junit.Assert.assertTrue(i1 == (-1074790400));
    }

    @Test
    public void test009() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test009");
        double d0 = org.apache.commons.math.util.MathUtils.SAFE_MIN;
        org.junit.Assert.assertTrue(d0 == 2.2250738585072014E-308d);
    }

    @Test
    public void test010() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test010");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck((-1L), (long) (byte) 10);
        org.junit.Assert.assertTrue(long2 == (-10L));
    }

    @Test
    public void test011() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test011");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        try {
            java.util.List<org.apache.commons.math.complex.Complex> list_complex3 = complex1.nthRoot((int) (short) 0);
            org.junit.Assert.fail("Expected anonymous exception");
        } catch (java.lang.IllegalArgumentException e) {
            if (!e.getClass().isAnonymousClass()) {
                org.junit.Assert.fail("Expected anonymous exception, got " + e.getClass().getCanonicalName());
            }
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
    }

    @Test
    public void test012() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test012");
        int i2 = org.apache.commons.math.util.MathUtils.lcm(1, 0);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test013() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test013");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((double) (short) 100);
        org.junit.Assert.assertTrue(d1 == 1.3440585709080678E43d);
    }

    @Test
    public void test014() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test014");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) (short) 10, (long) (short) 100);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test015() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test015");
        double[] d_array0 = null;
        double[] d_array7 = new double[] { 349.9541180407703d, 1.0f, (byte) 100, '4', '4', (-1.0f) };
        try {
            double d8 = org.apache.commons.math.util.MathUtils.distance(d_array0, d_array7);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(d_array7);
    }

    @Test
    public void test016() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test016");
        int i1 = org.apache.commons.math.util.MathUtils.hash((double) 1.0f);
        org.junit.Assert.assertTrue(i1 == 1072693248);
    }

    @Test
    public void test017() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test017");
        double d2 = org.apache.commons.math.util.MathUtils.round(1.0d, 0);
        org.junit.Assert.assertTrue(d2 == 1.0d);
    }

    @Test
    public void test018() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test018");
        int i1 = org.apache.commons.math.util.MathUtils.hash(0.0d);
        org.junit.Assert.assertTrue(i1 == 0);
    }

    @Test
    public void test019() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test019");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((float) (byte) -1, (float) 0);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test020() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test020");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((-1L), 0L);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test021() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test021");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle((double) 100.0f, (double) 100.0f);
        org.junit.Assert.assertTrue(d2 == 100.0d);
    }

    @Test
    public void test022() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test022");
        long long2 = org.apache.commons.math.util.MathUtils.gcd((long) 10, (long) (byte) 1);
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test023() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test023");
        int i2 = org.apache.commons.math.util.MathUtils.pow(1072693248, (long) 0);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test024() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test024");
        double d0 = org.apache.commons.math.util.MathUtils.EPSILON;
        org.junit.Assert.assertTrue(d0 == 1.1102230246251565E-16d);
    }

    @Test
    public void test025() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test025");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 0L, (java.lang.Number) 100.0d, true);
    }

    @Test
    public void test026() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test026");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck((long) (byte) -1, (long) 10);
        org.junit.Assert.assertTrue(long2 == 9L);
    }

    @Test
    public void test027() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test027");
        double d1 = org.apache.commons.math.util.MathUtils.sinh(100.0d);
        org.junit.Assert.assertTrue(d1 == 1.3440585709080678E43d);
    }

    @Test
    public void test028() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test028");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((double) 100.0f, (double) (short) 10);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test029() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test029");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
    }

    @Test
    public void test030() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test030");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) ' ', (float) 1L, 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test031() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test031");
        int i2 = org.apache.commons.math.util.MathUtils.lcm((int) (short) 10, (-1));
        org.junit.Assert.assertTrue(i2 == 10);
    }

    @Test
    public void test032() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test032");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (int) ' ');
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test033() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test033");
        byte byte1 = org.apache.commons.math.util.MathUtils.sign((byte) 1);
        org.junit.Assert.assertTrue(byte1 == (byte) 1);
    }

    @Test
    public void test034() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test034");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 1072693248, (-1));
        try {
            java.lang.String str4 = dimensionMismatchException3.toString();
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test035() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test035");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array19 = new double[] {};
        double[] d_array23 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array27 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array31 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array36 = new double[][] { d_array23, d_array27, d_array31, d_array35 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array19, d_array_array36);
        int i38 = org.apache.commons.math.util.MathUtils.hash(d_array19);
        double d39 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array19);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection40 = null;
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection40, false);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array_array36);
        org.junit.Assert.assertTrue(i38 == 1);
        org.junit.Assert.assertTrue(d39 == 0.0d);
    }

    @Test
    public void test036() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test036");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round(1.0f, 10, (int) (byte) 10);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test037() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test037");
        int i1 = org.apache.commons.math.util.MathUtils.sign(1);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test038() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test038");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) (byte) 0);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test039() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test039");
        int[] i_array6 = new int[] { '4', 0, (byte) 10, 1, (short) 10, '4' };
        int[] i_array7 = new int[] {};
        int[] i_array8 = org.apache.commons.math.util.MathUtils.copyOf(i_array7);
        int[] i_array14 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i15 = org.apache.commons.math.util.MathUtils.distanceInf(i_array7, i_array14);
        try {
            int i16 = org.apache.commons.math.util.MathUtils.distance1(i_array6, i_array7);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array6);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertNotNull(i_array8);
        org.junit.Assert.assertNotNull(i_array14);
        org.junit.Assert.assertTrue(i15 == 0);
    }

    @Test
    public void test040() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test040");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals(100.0d, (double) (short) 10, (double) 1L);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test041() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test041");
        java.lang.Number number1 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 100.0f, number1, false);
    }

    @Test
    public void test042() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test042");
        int[] i_array6 = new int[] { (short) 1, (byte) 1, 1072693248, '4', (byte) 1, (-1) };
        int[] i_array8 = new int[] { (short) 1 };
        try {
            int i9 = org.apache.commons.math.util.MathUtils.distance1(i_array6, i_array8);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array6);
        org.junit.Assert.assertNotNull(i_array8);
    }

    @Test
    public void test043() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test043");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ONE;
        org.junit.Assert.assertNotNull(complex0);
    }

    @Test
    public void test044() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test044");
        double[] d_array1 = new double[] { '4' };
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection2 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        double[] d_array3 = new double[] {};
        double[] d_array7 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array11 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array15 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array19 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array20 = new double[][] { d_array7, d_array11, d_array15, d_array19 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array3, d_array_array20);
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array1, orderDirection2, d_array_array20);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.DimensionMismatchException");
        } catch (org.apache.commons.math.exception.DimensionMismatchException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertTrue("'" + orderDirection2 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection2.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array_array20);
    }

    @Test
    public void test045() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test045");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        java.lang.Object[] obj_array4 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException5 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable2, (java.lang.Number) (short) 100, obj_array4);
        org.apache.commons.math.complex.Complex complex10 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = complex10.divide(complex11);
        java.lang.Object[] obj_array13 = new java.lang.Object[] { obj_array4, 1, ' ', 0.0d, false, complex12 };
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException14 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable0, (java.lang.Number) 100L, obj_array4);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(obj_array13);
    }

    @Test
    public void test046() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test046");
        int i2 = org.apache.commons.math.util.MathUtils.pow((int) (short) 100, (long) (byte) 0);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test047() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test047");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 10.0f, (int) (byte) 10);
        org.junit.Assert.assertTrue(d2 == 10.0d);
    }

    @Test
    public void test048() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test048");
        double[] d_array0 = null;
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection1 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        try {
            boolean b4 = org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection1, true, true);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertTrue("'" + orderDirection1 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection1.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test049() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test049");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) 10);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test050() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test050");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (int) (byte) 0);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test051() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test051");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex12 = complex9.pow(complex11);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex11);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
    }

    @Test
    public void test052() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test052");
        long long1 = org.apache.commons.math.util.MathUtils.factorial(10);
        org.junit.Assert.assertTrue(long1 == 3628800L);
    }

    @Test
    public void test053() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test053");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) (short) -1, (long) 1);
        org.junit.Assert.assertTrue(long2 == (-1L));
    }

    @Test
    public void test054() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test054");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient((int) 'a', (-1074790369));
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test055() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test055");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo(0.0d, (double) '#', 0);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test056() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test056");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) (byte) 0, 0);
        org.junit.Assert.assertTrue(f2 == 0.0f);
    }

    @Test
    public void test057() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test057");
        byte byte1 = org.apache.commons.math.util.MathUtils.indicator((byte) 10);
        org.junit.Assert.assertTrue(byte1 == (byte) 1);
    }

    @Test
    public void test058() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test058");
        long long1 = org.apache.commons.math.util.MathUtils.sign((long) 'a');
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test059() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test059");
        int i1 = org.apache.commons.math.util.MathUtils.indicator(100);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test060() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test060");
        int i1 = org.apache.commons.math.util.MathUtils.sign((int) (short) 1);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test061() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test061");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = complex0.subtract(complex14);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex18 = complex0.nthRoot((int) '4');
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(list_complex18);
    }

    @Test
    public void test062() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test062");
        double d0 = org.apache.commons.math.util.MathUtils.TWO_PI;
        org.junit.Assert.assertTrue(d0 == 6.283185307179586d);
    }

    @Test
    public void test063() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test063");
        long long1 = org.apache.commons.math.util.MathUtils.factorial((int) (byte) 1);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test064() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test064");
        java.lang.Number number1 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) (byte) 100, number1, false);
    }

    @Test
    public void test065() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test065");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 10, (float) 1);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test066() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test066");
        double d1 = org.apache.commons.math.util.MathUtils.factorialDouble((int) (short) 10);
        org.junit.Assert.assertTrue(d1 == 3628800.0d);
    }

    @Test
    public void test067() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test067");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round((float) 10, 100, (int) ' ');
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test068() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test068");
        double[] d_array0 = null;
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection1 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection1, false);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertTrue("'" + orderDirection1 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection1.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test069() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test069");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) (short) 0, (int) (byte) 100);
        org.junit.Assert.assertEquals((float) f2, Float.NaN, 0);
    }

    @Test
    public void test070() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test070");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((double) 1072693248, 100.0d);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test071() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test071");
        double[] d_array0 = null;
        try {
            org.apache.commons.math.util.MathUtils.checkFinite(d_array0);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test072() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test072");
        int i1 = org.apache.commons.math.util.MathUtils.sign(1072693248);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test073() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test073");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck(10, (-1));
        org.junit.Assert.assertTrue(i2 == 9);
    }

    @Test
    public void test074() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test074");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals(10.0d, (double) 10.0f);
        org.junit.Assert.assertTrue(b2 == true);
    }

    @Test
    public void test075() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test075");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(0.0d, (double) (-1.0f), (int) 'a');
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test076() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test076");
        int i2 = org.apache.commons.math.util.MathUtils.pow((-1074790369), (long) (short) 10);
        org.junit.Assert.assertTrue(i2 == 1636086465);
    }

    @Test
    public void test077() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test077");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) 9L, (java.lang.Number) 1, true);
        try {
            java.lang.String str5 = numberIsTooLargeException4.toString();
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test078() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test078");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog((int) (short) 0, (int) ' ');
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test079() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test079");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array19 = new double[] {};
        double[] d_array23 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array27 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array31 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array36 = new double[][] { d_array23, d_array27, d_array31, d_array35 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array19, d_array_array36);
        double[] d_array38 = new double[] {};
        double[] d_array42 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array46 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array50 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array54 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array55 = new double[][] { d_array42, d_array46, d_array50, d_array54 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array38, d_array_array55);
        int i57 = org.apache.commons.math.util.MathUtils.hash(d_array38);
        double d58 = org.apache.commons.math.util.MathUtils.distance1(d_array19, d_array38);
        double d59 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array19);
        double[] d_array63 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d64 = org.apache.commons.math.util.MathUtils.safeNorm(d_array63);
        double[] d_array65 = new double[] {};
        double[] d_array69 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array73 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array77 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array81 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array82 = new double[][] { d_array69, d_array73, d_array77, d_array81 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array65, d_array_array82);
        int i84 = org.apache.commons.math.util.MathUtils.hash(d_array65);
        boolean b85 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array63, d_array65);
        double d86 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array65);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection87 = null;
        try {
            boolean b90 = org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection87, false, true);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array_array36);
        org.junit.Assert.assertNotNull(d_array38);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertNotNull(d_array50);
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertNotNull(d_array_array55);
        org.junit.Assert.assertTrue(i57 == 1);
        org.junit.Assert.assertTrue(d58 == 0.0d);
        org.junit.Assert.assertTrue(d59 == 0.0d);
        org.junit.Assert.assertNotNull(d_array63);
        org.junit.Assert.assertTrue(d64 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array65);
        org.junit.Assert.assertNotNull(d_array69);
        org.junit.Assert.assertNotNull(d_array73);
        org.junit.Assert.assertNotNull(d_array77);
        org.junit.Assert.assertNotNull(d_array81);
        org.junit.Assert.assertNotNull(d_array_array82);
        org.junit.Assert.assertTrue(i84 == 1);
        org.junit.Assert.assertTrue(b85 == false);
        org.junit.Assert.assertTrue(d86 == 0.0d);
    }

    @Test
    public void test080() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test080");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array19 = new double[] {};
        double[] d_array23 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array27 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array31 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array36 = new double[][] { d_array23, d_array27, d_array31, d_array35 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array19, d_array_array36);
        int i38 = org.apache.commons.math.util.MathUtils.hash(d_array19);
        double d39 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array19);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array19);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array_array36);
        org.junit.Assert.assertTrue(i38 == 1);
        org.junit.Assert.assertTrue(d39 == 0.0d);
    }

    @Test
    public void test081() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test081");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Number number1 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, number1, (java.lang.Number) 1, true);
        boolean b5 = numberIsTooLargeException4.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b5 == true);
    }

    @Test
    public void test082() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test082");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        double d5 = complex4.abs();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertEquals((double) d5, Double.NaN, 0);
    }

    @Test
    public void test083() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test083");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (-1074790400), 349.9541180407703d, (int) (short) 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test084() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test084");
        double d3 = org.apache.commons.math.util.MathUtils.reduce(0.0d, (double) Float.NaN, 0.0d);
        org.junit.Assert.assertEquals((double) d3, Double.NaN, 0);
    }

    @Test
    public void test085() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test085");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException5 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i6 = nonMonotonousSequenceException5.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable7 = null;
        java.lang.Object[] obj_array9 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException10 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable7, (java.lang.Number) (short) 100, obj_array9);
        java.lang.Number number11 = notFiniteNumberException10.getArgument();
        org.apache.commons.math.complex.Complex complex12 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex13 = complex12.cosh();
        org.apache.commons.math.complex.Complex complex14 = complex13.exp();
        java.lang.Object[] obj_array16 = new java.lang.Object[] { i6, notFiniteNumberException10, complex14, 10.0f };
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException17 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable0, (java.lang.Number) (-1.0d), obj_array16);
        org.junit.Assert.assertTrue(i6 == 97);
        org.junit.Assert.assertTrue("'" + number11 + "' != '" + (short) 100 + "'", number11.equals((short) 100));
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(obj_array16);
    }

    @Test
    public void test086() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test086");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = new double[] {};
        double[] d_array9 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array22 = new double[][] { d_array9, d_array13, d_array17, d_array21 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array22);
        int i24 = org.apache.commons.math.util.MathUtils.hash(d_array5);
        boolean b25 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array3);
        double[] d_array28 = new double[] { (byte) -1 };
        double[] d_array30 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array28, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection31 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b34 = org.apache.commons.math.util.MathUtils.checkOrder(d_array30, orderDirection31, true, false);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection31, false);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NonMonotonousSequenceException");
        } catch (org.apache.commons.math.exception.NonMonotonousSequenceException e) {
        }
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array_array22);
        org.junit.Assert.assertTrue(i24 == 1);
        org.junit.Assert.assertTrue(b25 == false);
        org.junit.Assert.assertNotNull(d_array28);
        org.junit.Assert.assertNotNull(d_array30);
        org.junit.Assert.assertTrue("'" + orderDirection31 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection31.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b34 == true);
    }

    @Test
    public void test087() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test087");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (short) 100, (float) 0);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test088() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test088");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) (short) 100, (int) (byte) 10);
        org.junit.Assert.assertTrue(long2 == 7766279631452241920L);
    }

    @Test
    public void test089() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test089");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) 3628800L);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test090() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test090");
        int i1 = org.apache.commons.math.util.MathUtils.indicator((int) ' ');
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test091() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test091");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        double d5 = complex4.getReal();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertEquals((double) d5, Double.NaN, 0);
    }

    @Test
    public void test092() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test092");
        long long2 = org.apache.commons.math.util.MathUtils.pow(0L, (long) ' ');
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test093() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test093");
        long long2 = org.apache.commons.math.util.MathUtils.gcd((long) (byte) -1, (long) (byte) 100);
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test094() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test094");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) (short) 100, (int) '4');
        org.junit.Assert.assertEquals((float) f2, Float.NaN, 0);
    }

    @Test
    public void test095() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test095");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        boolean b5 = complex3.isInfinite();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertTrue(b5 == false);
    }

    @Test
    public void test096() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test096");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 100L, (float) 3628800L, 1);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test097() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test097");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) 1636086465, 97);
        org.junit.Assert.assertTrue(long2 == (-7500783708504147263L));
    }

    @Test
    public void test098() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test098");
        long long2 = org.apache.commons.math.util.MathUtils.lcm(0L, (long) 1636086465);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test099() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test099");
        double d2 = org.apache.commons.math.util.MathUtils.round(0.0d, 1072693248);
        org.junit.Assert.assertTrue(d2 == 0.0d);
    }

    @Test
    public void test100() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test100");
        try {
            long long1 = org.apache.commons.math.util.MathUtils.factorial((int) '#');
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test101() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test101");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((float) 0L, (float) '#');
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test102() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test102");
        int i1 = org.apache.commons.math.util.MathUtils.sign((-1));
        org.junit.Assert.assertTrue(i1 == (-1));
    }

    @Test
    public void test103() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test103");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (int) (byte) -1);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test104() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test104");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((long) (byte) -1, (long) '#');
        org.junit.Assert.assertTrue(long2 == 35L);
    }

    @Test
    public void test105() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test105");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow(97, (int) (byte) -1);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test106() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test106");
        int i2 = org.apache.commons.math.util.MathUtils.pow(1072693248, (int) (short) 0);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test107() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test107");
        int i1 = org.apache.commons.math.util.MathUtils.hash((double) 9);
        org.junit.Assert.assertTrue(i1 == 1075970048);
    }

    @Test
    public void test108() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test108");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(1.0f, (float) 1636086465);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test109() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test109");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals(0.0d, 0.0d, (double) 1075970048);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test110() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test110");
        double d2 = org.apache.commons.math.util.MathUtils.round(1.3440585709080678E43d, 0);
        org.junit.Assert.assertTrue(d2 == 1.3440585709080678E43d);
    }

    @Test
    public void test111() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test111");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) (-7500783708504147263L), (double) (-1L), (double) (-10L));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test112() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test112");
        short s1 = org.apache.commons.math.util.MathUtils.indicator((short) (byte) 1);
        org.junit.Assert.assertTrue(s1 == (short) 1);
    }

    @Test
    public void test113() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test113");
        double d2 = org.apache.commons.math.util.MathUtils.log((double) 97, 100.0d);
        org.junit.Assert.assertTrue(d2 == 1.0066581708938198d);
    }

    @Test
    public void test114() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test114");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Number number1 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, number1);
        java.lang.Number number3 = notPositiveException2.getMin();
        org.junit.Assert.assertTrue("'" + number3 + "' != '" + 0 + "'", number3.equals(0));
    }

    @Test
    public void test115() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test115");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, 0);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test116() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test116");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 1.3440585709080678E43d, (java.lang.Number) (byte) 1, false);
        java.lang.Number number4 = numberIsTooLargeException3.getMax();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + (byte) 1 + "'", number4.equals((byte) 1));
    }

    @Test
    public void test117() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test117");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, 1L);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test118() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test118");
        float f1 = org.apache.commons.math.util.MathUtils.sign((float) 1636086465);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test119() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test119");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck(3628800L, (long) 100);
        org.junit.Assert.assertTrue(long2 == 362880000L);
    }

    @Test
    public void test120() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test120");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 1636086465, (float) 9L, 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test121() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test121");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (int) (short) 10, (int) (short) 0);
    }

    @Test
    public void test122() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test122");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = complex1.asin();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
    }

    @Test
    public void test123() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test123");
        byte byte1 = org.apache.commons.math.util.MathUtils.sign((byte) 100);
        org.junit.Assert.assertTrue(byte1 == (byte) 1);
    }

    @Test
    public void test124() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test124");
        double[] d_array3 = new double[] { (-1074790369), 0L, (-10L) };
        double[] d_array4 = new double[] {};
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array20 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array21 = new double[][] { d_array8, d_array12, d_array16, d_array20 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array4, d_array_array21);
        double[] d_array23 = new double[] {};
        double[] d_array27 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array31 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array39 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array40 = new double[][] { d_array27, d_array31, d_array35, d_array39 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array23, d_array_array40);
        double[] d_array42 = new double[] {};
        double[] d_array46 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array50 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array54 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array58 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array59 = new double[][] { d_array46, d_array50, d_array54, d_array58 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array42, d_array_array59);
        int i61 = org.apache.commons.math.util.MathUtils.hash(d_array42);
        double d62 = org.apache.commons.math.util.MathUtils.distance1(d_array23, d_array42);
        double d63 = org.apache.commons.math.util.MathUtils.distance1(d_array4, d_array23);
        double[] d_array67 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d68 = org.apache.commons.math.util.MathUtils.safeNorm(d_array67);
        double[] d_array69 = new double[] {};
        double[] d_array73 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array77 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array81 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array85 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array86 = new double[][] { d_array73, d_array77, d_array81, d_array85 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array69, d_array_array86);
        int i88 = org.apache.commons.math.util.MathUtils.hash(d_array69);
        boolean b89 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array67, d_array69);
        double d90 = org.apache.commons.math.util.MathUtils.distance1(d_array4, d_array69);
        try {
            double d91 = org.apache.commons.math.util.MathUtils.distanceInf(d_array3, d_array69);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array_array21);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array39);
        org.junit.Assert.assertNotNull(d_array_array40);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertNotNull(d_array50);
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertNotNull(d_array58);
        org.junit.Assert.assertNotNull(d_array_array59);
        org.junit.Assert.assertTrue(i61 == 1);
        org.junit.Assert.assertTrue(d62 == 0.0d);
        org.junit.Assert.assertTrue(d63 == 0.0d);
        org.junit.Assert.assertNotNull(d_array67);
        org.junit.Assert.assertTrue(d68 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array69);
        org.junit.Assert.assertNotNull(d_array73);
        org.junit.Assert.assertNotNull(d_array77);
        org.junit.Assert.assertNotNull(d_array81);
        org.junit.Assert.assertNotNull(d_array85);
        org.junit.Assert.assertNotNull(d_array_array86);
        org.junit.Assert.assertTrue(i88 == 1);
        org.junit.Assert.assertTrue(b89 == false);
        org.junit.Assert.assertTrue(d90 == 0.0d);
    }

    @Test
    public void test125() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test125");
        int i2 = org.apache.commons.math.util.MathUtils.lcm((-1074790400), (int) ' ');
        org.junit.Assert.assertTrue(i2 == 1074790400);
    }

    @Test
    public void test126() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test126");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (byte) -1, (float) 9, (float) 1636086465);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test127() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test127");
        byte byte1 = org.apache.commons.math.util.MathUtils.indicator((byte) 0);
        org.junit.Assert.assertTrue(byte1 == (byte) 1);
    }

    @Test
    public void test128() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test128");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) (-1.0f), (double) ' ', 2.2250738585072014E-308d);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test129() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test129");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals(0.0f, 0.0f);
        org.junit.Assert.assertTrue(b2 == true);
    }

    @Test
    public void test130() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test130");
        int i2 = org.apache.commons.math.util.MathUtils.pow(0, (int) (byte) 0);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test131() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test131");
        long long1 = org.apache.commons.math.util.MathUtils.indicator(0L);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test132() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test132");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck((int) (short) 0, 0);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test133() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test133");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo(0.0d, (double) 1636086465, 349.9541180407703d);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test134() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test134");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (-1074790369));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test135() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test135");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(0, 10);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test136() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test136");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck(1075970048, (int) (short) 0);
        org.junit.Assert.assertTrue(i2 == 1075970048);
    }

    @Test
    public void test137() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test137");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = complex3.sqrt1z();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
    }

    @Test
    public void test138() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test138");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((double) (short) 10);
        org.junit.Assert.assertTrue(d1 == 11013.232874703393d);
    }

    @Test
    public void test139() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test139");
        float f1 = org.apache.commons.math.util.MathUtils.sign((float) 1072693248);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test140() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test140");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck((int) ' ', (int) (short) 10);
        org.junit.Assert.assertTrue(i2 == 42);
    }

    @Test
    public void test141() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test141");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        double d5 = org.apache.commons.math.util.MathUtils.distance(i_array2, i_array4);
        int[] i_array12 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array14 = org.apache.commons.math.util.MathUtils.copyOf(i_array12, (int) '4');
        int i15 = org.apache.commons.math.util.MathUtils.distanceInf(i_array4, i_array12);
        int[] i_array16 = new int[] {};
        int[] i_array17 = org.apache.commons.math.util.MathUtils.copyOf(i_array16);
        int[] i_array18 = org.apache.commons.math.util.MathUtils.copyOf(i_array16);
        try {
            int i19 = org.apache.commons.math.util.MathUtils.distance1(i_array12, i_array18);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertTrue(d5 == 0.0d);
        org.junit.Assert.assertNotNull(i_array12);
        org.junit.Assert.assertNotNull(i_array14);
        org.junit.Assert.assertTrue(i15 == 0);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertNotNull(i_array17);
        org.junit.Assert.assertNotNull(i_array18);
    }

    @Test
    public void test142() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test142");
        float f3 = org.apache.commons.math.util.MathUtils.round(0.0f, (int) (byte) 0, (int) (short) 0);
        org.junit.Assert.assertTrue(f3 == 1.0f);
    }

    @Test
    public void test143() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test143");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 1074790400, (float) 0, Float.NaN);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test144() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test144");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((double) (short) 1);
        org.junit.Assert.assertTrue(d1 == 1.1752011936438014d);
    }

    @Test
    public void test145() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test145");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (-1L), (float) '#');
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test146() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test146");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck((int) ' ', (int) (byte) 10);
        org.junit.Assert.assertTrue(i2 == 22);
    }

    @Test
    public void test147() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test147");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) 100, 0L);
        org.junit.Assert.assertTrue(long2 == 100L);
    }

    @Test
    public void test148() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test148");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        org.apache.commons.math.complex.Complex complex7 = complex1.atan();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
    }

    @Test
    public void test149() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test149");
        short s1 = org.apache.commons.math.util.MathUtils.indicator((short) -1);
        org.junit.Assert.assertTrue(s1 == (short) -1);
    }

    @Test
    public void test150() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test150");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = complex2.acos();
        org.apache.commons.math.complex.Complex complex7 = complex5.multiply((double) 100.0f);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex7);
    }

    @Test
    public void test151() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test151");
        float f2 = org.apache.commons.math.util.MathUtils.round(0.0f, (int) 'a');
        org.junit.Assert.assertEquals((float) f2, Float.NaN, 0);
    }

    @Test
    public void test152() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test152");
        long long1 = org.apache.commons.math.util.MathUtils.sign((long) 22);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test153() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test153");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 'a', 6.283185307179586d, 1636086465);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test154() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test154");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (int) (short) 1, (int) ' ');
    }

    @Test
    public void test155() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test155");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(3628800.0d, (double) 1072693248, (double) (short) 10);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test156() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test156");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException(9, (int) (short) 10);
    }

    @Test
    public void test157() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test157");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round((float) 100, (int) 'a', 1636086465);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test158() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test158");
        long long1 = org.apache.commons.math.util.MathUtils.indicator((long) (short) -1);
        org.junit.Assert.assertTrue(long1 == (-1L));
    }

    @Test
    public void test159() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test159");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 0.0d, (java.lang.Number) 1636086465, false);
    }

    @Test
    public void test160() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test160");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array19 = new double[] {};
        double[] d_array23 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array27 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array31 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array36 = new double[][] { d_array23, d_array27, d_array31, d_array35 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array19, d_array_array36);
        double[] d_array38 = new double[] {};
        double[] d_array42 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array46 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array50 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array54 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array55 = new double[][] { d_array42, d_array46, d_array50, d_array54 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array38, d_array_array55);
        int i57 = org.apache.commons.math.util.MathUtils.hash(d_array38);
        double d58 = org.apache.commons.math.util.MathUtils.distance1(d_array19, d_array38);
        double d59 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array19);
        int i60 = org.apache.commons.math.util.MathUtils.hash(d_array0);
        double[] d_array61 = null;
        double d62 = org.apache.commons.math.util.MathUtils.distanceInf(d_array0, d_array61);
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array_array36);
        org.junit.Assert.assertNotNull(d_array38);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertNotNull(d_array50);
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertNotNull(d_array_array55);
        org.junit.Assert.assertTrue(i57 == 1);
        org.junit.Assert.assertTrue(d58 == 0.0d);
        org.junit.Assert.assertTrue(d59 == 0.0d);
        org.junit.Assert.assertTrue(i60 == 1);
        org.junit.Assert.assertTrue(d62 == 0.0d);
    }

    @Test
    public void test161() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test161");
        int i2 = org.apache.commons.math.util.MathUtils.pow(0, (int) (short) 100);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test162() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test162");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((double) 7766279631452241920L);
        org.junit.Assert.assertTrue(d1 == Double.POSITIVE_INFINITY);
    }

    @Test
    public void test163() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test163");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 1074790400, (double) 0, 10.0d);
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test164() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test164");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = complex0.asin();
        org.apache.commons.math.complex.Complex complex15 = null;
        try {
            org.apache.commons.math.complex.Complex complex16 = complex0.divide(complex15);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
    }

    @Test
    public void test165() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test165");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 0L, 100.0f, (float) (short) 1);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test166() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test166");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 10, (double) (short) 1, 100.0d);
        org.junit.Assert.assertTrue(i3 == 0);
    }

    @Test
    public void test167() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test167");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) (-1074790369), (double) 100.0f, 1072693248);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test168() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test168");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        org.apache.commons.math.complex.Complex complex5 = complex4.cosh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
    }

    @Test
    public void test169() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test169");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck(9L, 7766279631452241920L);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test170() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test170");
        float f1 = org.apache.commons.math.util.MathUtils.sign((float) (byte) 100);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test171() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test171");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog((int) 'a', 1636086465);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test172() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test172");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger1 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, bigInteger1);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test173() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test173");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck((long) 0, 0L);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test174() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test174");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient((int) (byte) 10, 1636086465);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test175() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test175");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient(0, 1);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test176() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test176");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((float) (-1074790369), (float) (byte) 10);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test177() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test177");
        float[] f_array0 = null;
        float[] f_array1 = null;
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array0, f_array1);
        org.junit.Assert.assertTrue(b2 == true);
    }

    @Test
    public void test178() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test178");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (-1074790369), Float.NaN, (int) ' ');
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test179() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test179");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.lcm((int) '4', (-1074790369));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test180() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test180");
        long long1 = org.apache.commons.math.util.MathUtils.indicator((long) (-1074790369));
        org.junit.Assert.assertTrue(long1 == (-1L));
    }

    @Test
    public void test181() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test181");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array4 = new double[] {};
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array20 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array21 = new double[][] { d_array8, d_array12, d_array16, d_array20 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array4, d_array_array21);
        double[] d_array23 = new double[] {};
        double[] d_array27 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array31 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array39 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array40 = new double[][] { d_array27, d_array31, d_array35, d_array39 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array23, d_array_array40);
        double[] d_array42 = new double[] {};
        double[] d_array46 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array50 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array54 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array58 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array59 = new double[][] { d_array46, d_array50, d_array54, d_array58 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array42, d_array_array59);
        int i61 = org.apache.commons.math.util.MathUtils.hash(d_array42);
        double d62 = org.apache.commons.math.util.MathUtils.distance1(d_array23, d_array42);
        double d63 = org.apache.commons.math.util.MathUtils.distance1(d_array4, d_array23);
        double[] d_array67 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d68 = org.apache.commons.math.util.MathUtils.safeNorm(d_array67);
        double[] d_array69 = new double[] {};
        double[] d_array73 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array77 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array81 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array85 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array86 = new double[][] { d_array73, d_array77, d_array81, d_array85 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array69, d_array_array86);
        int i88 = org.apache.commons.math.util.MathUtils.hash(d_array69);
        boolean b89 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array67, d_array69);
        double d90 = org.apache.commons.math.util.MathUtils.distance1(d_array4, d_array69);
        try {
            double d91 = org.apache.commons.math.util.MathUtils.distance1(d_array1, d_array69);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array_array21);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array39);
        org.junit.Assert.assertNotNull(d_array_array40);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertNotNull(d_array50);
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertNotNull(d_array58);
        org.junit.Assert.assertNotNull(d_array_array59);
        org.junit.Assert.assertTrue(i61 == 1);
        org.junit.Assert.assertTrue(d62 == 0.0d);
        org.junit.Assert.assertTrue(d63 == 0.0d);
        org.junit.Assert.assertNotNull(d_array67);
        org.junit.Assert.assertTrue(d68 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array69);
        org.junit.Assert.assertNotNull(d_array73);
        org.junit.Assert.assertNotNull(d_array77);
        org.junit.Assert.assertNotNull(d_array81);
        org.junit.Assert.assertNotNull(d_array85);
        org.junit.Assert.assertNotNull(d_array_array86);
        org.junit.Assert.assertTrue(i88 == 1);
        org.junit.Assert.assertTrue(b89 == false);
        org.junit.Assert.assertTrue(d90 == 0.0d);
    }

    @Test
    public void test182() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test182");
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException0 = new org.apache.commons.math.exception.MathArithmeticException();
        java.lang.String str1 = mathArithmeticException0.toString();
        org.apache.commons.math.exception.NotPositiveException notPositiveException3 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        org.apache.commons.math.exception.NotPositiveException notPositiveException5 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        notPositiveException3.addSuppressed((java.lang.Throwable) notPositiveException5);
        mathArithmeticException0.addSuppressed((java.lang.Throwable) notPositiveException5);
        org.apache.commons.math.exception.util.Localizable localizable9 = null;
        org.apache.commons.math.exception.util.Localizable localizable10 = null;
        float[] f_array16 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array18 = new float[] { (-1074790400) };
        boolean b19 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array16, f_array18);
        java.lang.Object[] obj_array25 = new java.lang.Object[] { b19, 0, 0, (-1.0f), 100.0f, (short) 1 };
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException26 = new org.apache.commons.math.exception.MathArithmeticException(localizable10, obj_array25);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException27 = new org.apache.commons.math.exception.MathArithmeticException(localizable9, obj_array25);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException28 = new org.apache.commons.math.exception.NotFiniteNumberException((java.lang.Number) 1074790400, obj_array25);
        notPositiveException5.addSuppressed((java.lang.Throwable) notFiniteNumberException28);
        org.junit.Assert.assertTrue("'" + str1 + "' != '" + "org.apache.commons.math.exception.MathArithmeticException: arithmetic exception" + "'", str1.equals("org.apache.commons.math.exception.MathArithmeticException: arithmetic exception"));
        org.junit.Assert.assertNotNull(f_array16);
        org.junit.Assert.assertNotNull(f_array18);
        org.junit.Assert.assertTrue(b19 == false);
        org.junit.Assert.assertNotNull(obj_array25);
    }

    @Test
    public void test183() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test183");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow(1074790400, (int) (byte) -1);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test184() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test184");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (byte) 1, (float) 1075970048, (float) (byte) 100);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test185() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test185");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) (-2064878497), (double) Float.NaN, 1.0066581708938198d);
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test186() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test186");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (int) (short) 0);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test187() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test187");
        int i2 = org.apache.commons.math.util.MathUtils.gcd((int) (byte) 100, 0);
        org.junit.Assert.assertTrue(i2 == 100);
    }

    @Test
    public void test188() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test188");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) 1.0066581708938198d);
        java.lang.Number number2 = notPositiveException1.getMin();
        org.junit.Assert.assertTrue("'" + number2 + "' != '" + 0 + "'", number2.equals(0));
    }

    @Test
    public void test189() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test189");
        int i2 = org.apache.commons.math.util.MathUtils.pow(9, 0L);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test190() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test190");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((double) 10L, (double) (-1));
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test191() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test191");
        double d1 = org.apache.commons.math.util.MathUtils.sinh(0.0d);
        org.junit.Assert.assertTrue(d1 == 0.0d);
    }

    @Test
    public void test192() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test192");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Object[] obj_array2 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException3 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable0, (java.lang.Number) (-1.0d), obj_array2);
    }

    @Test
    public void test193() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test193");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        java.lang.Object[] obj_array8 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException9 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable6, (java.lang.Number) (short) 100, obj_array8);
        java.lang.Number number10 = notFiniteNumberException9.getArgument();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException9);
        int i12 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException16 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i17 = nonMonotonousSequenceException16.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection18 = nonMonotonousSequenceException16.getDirection();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException16);
        java.lang.String str20 = nonMonotonousSequenceException3.toString();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number10 + "' != '" + (short) 100 + "'", number10.equals((short) 100));
        org.junit.Assert.assertTrue(i12 == 97);
        org.junit.Assert.assertTrue(i17 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection18 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection18.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue("'" + str20 + "' != '" + "org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)" + "'", str20.equals("org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)"));
    }

    @Test
    public void test194() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test194");
        try {
            double d1 = org.apache.commons.math.util.MathUtils.factorialLog((-2064878497));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test195() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test195");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 42, (java.lang.Number) 1.0066581708938198d, (int) (short) 100);
    }

    @Test
    public void test196() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test196");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex1.sin();
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex6 = complex5.cosh();
        double d7 = complex6.getReal();
        org.apache.commons.math.complex.Complex complex8 = complex6.cos();
        org.apache.commons.math.complex.Complex complex9 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex10 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex11 = complex9.divide(complex10);
        org.apache.commons.math.complex.Complex complex12 = complex8.multiply(complex9);
        double d13 = complex12.getImaginary();
        boolean b14 = complex4.equals((java.lang.Object) d13);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertTrue(d7 == 1.0d);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertEquals((double) d13, Double.NaN, 0);
        org.junit.Assert.assertTrue(b14 == false);
    }

    @Test
    public void test197() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test197");
        double[] d_array0 = null;
        try {
            double[] d_array2 = org.apache.commons.math.util.MathUtils.copyOf(d_array0, (int) ' ');
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test198() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test198");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = new double[] {};
        double[] d_array9 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array22 = new double[][] { d_array9, d_array13, d_array17, d_array21 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array22);
        int i24 = org.apache.commons.math.util.MathUtils.hash(d_array5);
        boolean b25 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        double[] d_array29 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d30 = org.apache.commons.math.util.MathUtils.safeNorm(d_array29);
        double[] d_array31 = new double[] {};
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array39 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array43 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array47 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array48 = new double[][] { d_array35, d_array39, d_array43, d_array47 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array31, d_array_array48);
        int i50 = org.apache.commons.math.util.MathUtils.hash(d_array31);
        boolean b51 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array29, d_array31);
        boolean b52 = org.apache.commons.math.util.MathUtils.equals(d_array5, d_array29);
        double[] d_array53 = org.apache.commons.math.util.MathUtils.copyOf(d_array5);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array53);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array_array22);
        org.junit.Assert.assertTrue(i24 == 1);
        org.junit.Assert.assertTrue(b25 == false);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertTrue(d30 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array39);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array_array48);
        org.junit.Assert.assertTrue(i50 == 1);
        org.junit.Assert.assertTrue(b51 == false);
        org.junit.Assert.assertTrue(b52 == false);
        org.junit.Assert.assertNotNull(d_array53);
    }

    @Test
    public void test199() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test199");
        int i1 = org.apache.commons.math.util.MathUtils.sign(1074790400);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test200() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test200");
        int i2 = org.apache.commons.math.util.MathUtils.gcd(0, 0);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test201() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test201");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) (-1074790400));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test202() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test202");
        double d1 = org.apache.commons.math.util.MathUtils.cosh(349.9541180407703d);
        org.junit.Assert.assertTrue(d1 == 4.809637984124462E151d);
    }

    @Test
    public void test203() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test203");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 35L, (int) (byte) 10);
        org.junit.Assert.assertTrue(d2 == 35.0d);
    }

    @Test
    public void test204() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test204");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck(1, (int) (byte) 1);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test205() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test205");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex14 = complex13.cos();
        org.apache.commons.math.complex.Complex complex15 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex16 = complex15.cos();
        org.apache.commons.math.complex.Complex complex17 = complex13.subtract(complex15);
        org.apache.commons.math.complex.Complex complex18 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex19 = complex18.cos();
        org.apache.commons.math.complex.Complex complex20 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex21 = complex20.cos();
        org.apache.commons.math.complex.Complex complex22 = complex18.subtract(complex20);
        org.apache.commons.math.complex.Complex complex23 = complex13.add(complex22);
        org.apache.commons.math.complex.Complex complex24 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex25 = complex22.pow(complex24);
        org.apache.commons.math.complex.Complex complex26 = complex0.multiply(complex24);
        org.apache.commons.math.complex.Complex complex27 = complex24.atan();
        boolean b28 = complex24.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complex24);
        org.junit.Assert.assertNotNull(complex25);
        org.junit.Assert.assertNotNull(complex26);
        org.junit.Assert.assertNotNull(complex27);
        org.junit.Assert.assertTrue(b28 == false);
    }

    @Test
    public void test206() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test206");
        long long1 = org.apache.commons.math.util.MathUtils.sign(1L);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test207() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test207");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 0.0f, 97);
        org.junit.Assert.assertTrue(d2 == 0.0d);
    }

    @Test
    public void test208() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test208");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((float) (byte) 10, (float) 1075970048);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test209() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test209");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array1);
        double[] d_array5 = new double[] {};
        double[] d_array9 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array22 = new double[][] { d_array9, d_array13, d_array17, d_array21 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array22);
        double[] d_array24 = new double[] {};
        double[] d_array28 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array32 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array36 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array40 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array41 = new double[][] { d_array28, d_array32, d_array36, d_array40 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array24, d_array_array41);
        int i43 = org.apache.commons.math.util.MathUtils.hash(d_array24);
        double d44 = org.apache.commons.math.util.MathUtils.distance1(d_array5, d_array24);
        try {
            double d45 = org.apache.commons.math.util.MathUtils.distance(d_array1, d_array5);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 1.0d);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array_array22);
        org.junit.Assert.assertNotNull(d_array24);
        org.junit.Assert.assertNotNull(d_array28);
        org.junit.Assert.assertNotNull(d_array32);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertNotNull(d_array_array41);
        org.junit.Assert.assertTrue(i43 == 1);
        org.junit.Assert.assertTrue(d44 == 0.0d);
    }

    @Test
    public void test210() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test210");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) (-1), (int) ' ');
        org.junit.Assert.assertTrue(d2 == (-1.0d));
    }

    @Test
    public void test211() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test211");
        int i2 = org.apache.commons.math.util.MathUtils.gcd(42, (-1));
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test212() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test212");
        org.apache.commons.math.util.MathUtils.checkFinite((double) 1072693248);
    }

    @Test
    public void test213() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test213");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = new double[] {};
        double[] d_array9 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array22 = new double[][] { d_array9, d_array13, d_array17, d_array21 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array22);
        int i24 = org.apache.commons.math.util.MathUtils.hash(d_array5);
        boolean b25 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        double[] d_array29 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d30 = org.apache.commons.math.util.MathUtils.safeNorm(d_array29);
        double[] d_array31 = new double[] {};
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array39 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array43 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array47 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array48 = new double[][] { d_array35, d_array39, d_array43, d_array47 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array31, d_array_array48);
        int i50 = org.apache.commons.math.util.MathUtils.hash(d_array31);
        boolean b51 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array29, d_array31);
        boolean b52 = org.apache.commons.math.util.MathUtils.equals(d_array5, d_array29);
        double[] d_array53 = org.apache.commons.math.util.MathUtils.copyOf(d_array5);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection54 = null;
        double[][] d_array_array55 = null;
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array53, orderDirection54, d_array_array55);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array_array22);
        org.junit.Assert.assertTrue(i24 == 1);
        org.junit.Assert.assertTrue(b25 == false);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertTrue(d30 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array39);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array_array48);
        org.junit.Assert.assertTrue(i50 == 1);
        org.junit.Assert.assertTrue(b51 == false);
        org.junit.Assert.assertTrue(b52 == false);
        org.junit.Assert.assertNotNull(d_array53);
    }

    @Test
    public void test214() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test214");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) 10L, (double) ' ');
    }

    @Test
    public void test215() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test215");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) ' ');
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test216() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test216");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck(0, (int) (byte) 1);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test217() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test217");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        java.lang.Object[] obj_array8 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException9 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable6, (java.lang.Number) (short) 100, obj_array8);
        java.lang.Number number10 = notFiniteNumberException9.getArgument();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException9);
        int i12 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException16 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i17 = nonMonotonousSequenceException16.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection18 = nonMonotonousSequenceException16.getDirection();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException16);
        int i20 = nonMonotonousSequenceException3.getIndex();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number10 + "' != '" + (short) 100 + "'", number10.equals((short) 100));
        org.junit.Assert.assertTrue(i12 == 97);
        org.junit.Assert.assertTrue(i17 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection18 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection18.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i20 == 97);
    }

    @Test
    public void test218() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test218");
        float f1 = org.apache.commons.math.util.MathUtils.indicator(0.0f);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test219() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test219");
        double d1 = org.apache.commons.math.util.MathUtils.sign((double) (-1.0f));
        org.junit.Assert.assertTrue(d1 == (-1.0d));
    }

    @Test
    public void test220() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test220");
        long long2 = org.apache.commons.math.util.MathUtils.pow(10L, (int) (byte) 10);
        org.junit.Assert.assertTrue(long2 == 10000000000L);
    }

    @Test
    public void test221() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test221");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 0.0f, 0.0d, (double) 0);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test222() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test222");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) (short) 100, (int) (short) 10);
        org.junit.Assert.assertTrue(d2 == 100.0d);
    }

    @Test
    public void test223() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test223");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((long) (-1074790369), (long) (byte) 0);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test224() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test224");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        try {
            int[] i_array3 = org.apache.commons.math.util.MathUtils.copyOf(i_array0, (-2064878497));
            org.junit.Assert.fail("Expected exception of type java.lang.NegativeArraySizeException");
        } catch (java.lang.NegativeArraySizeException e) {
        }
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
    }

    @Test
    public void test225() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test225");
        double d1 = org.apache.commons.math.util.MathUtils.factorialLog((int) (byte) 1);
        org.junit.Assert.assertTrue(d1 == 0.0d);
    }

    @Test
    public void test226() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test226");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (-1), 3628800.0d, (double) 9L);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test227() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test227");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle((double) '#', (double) 1074790400);
        org.junit.Assert.assertTrue(d2 == 1.0747903976294017E9d);
    }

    @Test
    public void test228() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test228");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) '4', (float) 0, 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test229() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test229");
        double d1 = org.apache.commons.math.util.MathUtils.factorialDouble(0);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test230() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test230");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) (byte) 100);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException6 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i7 = nonMonotonousSequenceException6.getIndex();
        notPositiveException2.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException6);
        boolean b9 = nonMonotonousSequenceException6.getStrict();
        int i10 = nonMonotonousSequenceException6.getIndex();
        org.junit.Assert.assertTrue(i7 == 97);
        org.junit.Assert.assertTrue(b9 == true);
        org.junit.Assert.assertTrue(i10 == 97);
    }

    @Test
    public void test231() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test231");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow(1072693248, (-1L));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test232() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test232");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex0.asin();
        org.apache.commons.math.complex.Complex complex3 = complex2.sqrt1z();
        double d4 = complex2.getImaginary();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertTrue(d4 == (-0.0d));
    }

    @Test
    public void test233() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test233");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        java.lang.Object[] obj_array8 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException9 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable6, (java.lang.Number) (short) 100, obj_array8);
        java.lang.Number number10 = notFiniteNumberException9.getArgument();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException9);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection12 = nonMonotonousSequenceException3.getDirection();
        java.lang.Number number13 = nonMonotonousSequenceException3.getPrevious();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number10 + "' != '" + (short) 100 + "'", number10.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection12 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection12.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue("'" + number13 + "' != '" + 2.2250738585072014E-308d + "'", number13.equals(2.2250738585072014E-308d));
    }

    @Test
    public void test234() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test234");
        int i2 = org.apache.commons.math.util.MathUtils.pow(97, (int) ' ');
        org.junit.Assert.assertTrue(i2 == (-447362047));
    }

    @Test
    public void test235() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test235");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) (short) -1, (java.lang.Number) 1.0d, true);
        try {
            java.lang.String str5 = numberIsTooLargeException4.toString();
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test236() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test236");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = new double[] {};
        double[] d_array9 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array22 = new double[][] { d_array9, d_array13, d_array17, d_array21 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array22);
        int i24 = org.apache.commons.math.util.MathUtils.hash(d_array5);
        boolean b25 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        double[] d_array26 = new double[] {};
        double[] d_array30 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array34 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array38 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array42 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array43 = new double[][] { d_array30, d_array34, d_array38, d_array42 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array26, d_array_array43);
        double[] d_array45 = new double[] {};
        double[] d_array49 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array53 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array57 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array61 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array62 = new double[][] { d_array49, d_array53, d_array57, d_array61 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array45, d_array_array62);
        double[] d_array64 = new double[] {};
        double[] d_array68 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array72 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array76 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array80 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array81 = new double[][] { d_array68, d_array72, d_array76, d_array80 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array64, d_array_array81);
        int i83 = org.apache.commons.math.util.MathUtils.hash(d_array64);
        double d84 = org.apache.commons.math.util.MathUtils.distance1(d_array45, d_array64);
        double d85 = org.apache.commons.math.util.MathUtils.distance1(d_array26, d_array45);
        double d86 = org.apache.commons.math.util.MathUtils.distanceInf(d_array5, d_array26);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array_array22);
        org.junit.Assert.assertTrue(i24 == 1);
        org.junit.Assert.assertTrue(b25 == false);
        org.junit.Assert.assertNotNull(d_array26);
        org.junit.Assert.assertNotNull(d_array30);
        org.junit.Assert.assertNotNull(d_array34);
        org.junit.Assert.assertNotNull(d_array38);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array_array43);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array57);
        org.junit.Assert.assertNotNull(d_array61);
        org.junit.Assert.assertNotNull(d_array_array62);
        org.junit.Assert.assertNotNull(d_array64);
        org.junit.Assert.assertNotNull(d_array68);
        org.junit.Assert.assertNotNull(d_array72);
        org.junit.Assert.assertNotNull(d_array76);
        org.junit.Assert.assertNotNull(d_array80);
        org.junit.Assert.assertNotNull(d_array_array81);
        org.junit.Assert.assertTrue(i83 == 1);
        org.junit.Assert.assertTrue(d84 == 0.0d);
        org.junit.Assert.assertTrue(d85 == 0.0d);
        org.junit.Assert.assertTrue(d86 == 0.0d);
    }

    @Test
    public void test237() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test237");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (int) '4');
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test238() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test238");
        float f2 = org.apache.commons.math.util.MathUtils.round(0.0f, (int) (byte) 10);
        org.junit.Assert.assertTrue(f2 == 0.0f);
    }

    @Test
    public void test239() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test239");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) 0, 97);
        org.junit.Assert.assertEquals((float) f2, Float.NaN, 0);
    }

    @Test
    public void test240() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test240");
        short s1 = org.apache.commons.math.util.MathUtils.indicator((short) 0);
        org.junit.Assert.assertTrue(s1 == (short) 1);
    }

    @Test
    public void test241() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test241");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 35L, 100.0f, (float) 100L);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test242() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test242");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck((long) 100, 35L);
        org.junit.Assert.assertTrue(long2 == 3500L);
    }

    @Test
    public void test243() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test243");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((float) 1, (float) (short) 10);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test244() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test244");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck(0L, (long) 1);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test245() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test245");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        java.lang.Object[] obj_array4 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException5 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable2, (java.lang.Number) 100.0d, obj_array4);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException6 = new org.apache.commons.math.exception.NullArgumentException(localizable1, obj_array4);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException7 = new org.apache.commons.math.exception.MathArithmeticException(localizable0, obj_array4);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext8 = mathArithmeticException7.getContext();
        org.junit.Assert.assertNotNull(obj_array4);
        org.junit.Assert.assertNotNull(exceptionContext8);
    }

    @Test
    public void test246() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test246");
        short s1 = org.apache.commons.math.util.MathUtils.indicator((short) (byte) 10);
        org.junit.Assert.assertTrue(s1 == (short) 1);
    }

    @Test
    public void test247() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test247");
        int i2 = org.apache.commons.math.util.MathUtils.gcd((-2064878497), 1072693248);
        org.junit.Assert.assertTrue(i2 == 11);
    }

    @Test
    public void test248() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test248");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException4 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection5 = nonMonotonousSequenceException4.getDirection();
        java.lang.Throwable[] throwable_array6 = nonMonotonousSequenceException4.getSuppressed();
        org.apache.commons.math.exception.MathIllegalArgumentException mathIllegalArgumentException7 = new org.apache.commons.math.exception.MathIllegalArgumentException(localizable0, (java.lang.Object[]) throwable_array6);
        org.junit.Assert.assertTrue("'" + orderDirection5 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection5.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertNotNull(throwable_array6);
    }

    @Test
    public void test249() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test249");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 1072693248, (-1));
        int i4 = dimensionMismatchException3.getDimension();
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext5 = dimensionMismatchException3.getContext();
        org.junit.Assert.assertTrue(i4 == (-1));
        org.junit.Assert.assertNotNull(exceptionContext5);
    }

    @Test
    public void test250() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test250");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((double) 35L, (double) 9L);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test251() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test251");
        int[] i_array6 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array8 = org.apache.commons.math.util.MathUtils.copyOf(i_array6, (int) '4');
        int[] i_array9 = new int[] {};
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array9);
        try {
            double d11 = org.apache.commons.math.util.MathUtils.distance(i_array6, i_array9);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array6);
        org.junit.Assert.assertNotNull(i_array8);
        org.junit.Assert.assertNotNull(i_array9);
        org.junit.Assert.assertNotNull(i_array10);
    }

    @Test
    public void test252() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test252");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex12 = complex9.pow(complex11);
        org.apache.commons.math.complex.Complex complex13 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex13.divide(complex14);
        org.apache.commons.math.complex.Complex complex16 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex17 = complex16.tanh();
        org.apache.commons.math.complex.Complex complex18 = complex15.multiply(complex17);
        org.apache.commons.math.complex.Complex complex19 = complex9.multiply(complex17);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
    }

    @Test
    public void test253() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test253");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) 3500L);
    }

    @Test
    public void test254() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test254");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) Float.NaN);
        org.junit.Assert.assertEquals((double) d1, Double.NaN, 0);
    }

    @Test
    public void test255() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test255");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck((int) (byte) 10, 0);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test256() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test256");
        double d2 = org.apache.commons.math.util.MathUtils.log(1.0747903976294017E9d, 0.0d);
        org.junit.Assert.assertTrue(d2 == Double.NEGATIVE_INFINITY);
    }

    @Test
    public void test257() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test257");
        double d3 = org.apache.commons.math.util.MathUtils.reduce((double) (byte) 1, (double) 1.0f, (double) 10L);
        org.junit.Assert.assertTrue(d3 == 0.0d);
    }

    @Test
    public void test258() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test258");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 1, 100.0f, 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test259() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test259");
        double d2 = org.apache.commons.math.util.MathUtils.log((double) (short) 100, 1.1752011936438014d);
        org.junit.Assert.assertTrue(d2 == 0.035056111946177054d);
    }

    @Test
    public void test260() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test260");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 1.3440585709080678E43d, (java.lang.Number) (byte) 1, false);
        java.lang.Number number4 = numberIsTooLargeException3.getArgument();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 1.3440585709080678E43d + "'", number4.equals(1.3440585709080678E43d));
    }

    @Test
    public void test261() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test261");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        double d5 = org.apache.commons.math.util.MathUtils.distance(i_array2, i_array4);
        int[] i_array12 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array14 = org.apache.commons.math.util.MathUtils.copyOf(i_array12, (int) '4');
        int i15 = org.apache.commons.math.util.MathUtils.distanceInf(i_array4, i_array12);
        int[] i_array16 = new int[] {};
        int[] i_array17 = org.apache.commons.math.util.MathUtils.copyOf(i_array16);
        int[] i_array23 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i24 = org.apache.commons.math.util.MathUtils.distanceInf(i_array16, i_array23);
        try {
            int i25 = org.apache.commons.math.util.MathUtils.distance1(i_array12, i_array23);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertTrue(d5 == 0.0d);
        org.junit.Assert.assertNotNull(i_array12);
        org.junit.Assert.assertNotNull(i_array14);
        org.junit.Assert.assertTrue(i15 == 0);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertNotNull(i_array17);
        org.junit.Assert.assertNotNull(i_array23);
        org.junit.Assert.assertTrue(i24 == 0);
    }

    @Test
    public void test262() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test262");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck((-2064878497), (int) (short) 0);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test263() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test263");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck(97, (int) (byte) -1);
        org.junit.Assert.assertTrue(i2 == 98);
    }

    @Test
    public void test264() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test264");
        int[] i_array6 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array8 = org.apache.commons.math.util.MathUtils.copyOf(i_array6, (int) '4');
        int[] i_array9 = new int[] {};
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array9);
        int[] i_array11 = org.apache.commons.math.util.MathUtils.copyOf(i_array9);
        int[] i_array12 = new int[] {};
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array12);
        double d14 = org.apache.commons.math.util.MathUtils.distance(i_array11, i_array13);
        try {
            int i15 = org.apache.commons.math.util.MathUtils.distance1(i_array8, i_array13);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array6);
        org.junit.Assert.assertNotNull(i_array8);
        org.junit.Assert.assertNotNull(i_array9);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertNotNull(i_array11);
        org.junit.Assert.assertNotNull(i_array12);
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertTrue(d14 == 0.0d);
    }

    @Test
    public void test265() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test265");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((-10L), 0L);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test266() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test266");
        double d1 = org.apache.commons.math.util.MathUtils.sign(349.9541180407703d);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test267() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test267");
        java.lang.Number number0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException(number0, (java.lang.Number) Double.NaN, true);
        boolean b4 = numberIsTooSmallException3.getBoundIsAllowed();
        java.lang.Number number5 = numberIsTooSmallException3.getMin();
        org.junit.Assert.assertTrue(b4 == true);
        org.junit.Assert.assertEquals((double) number5, Double.NaN, 0);
    }

    @Test
    public void test268() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test268");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck(0L, (long) 1074790400);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test269() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test269");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) (byte) 1);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test270() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test270");
        double d1 = org.apache.commons.math.util.MathUtils.cosh(Double.NEGATIVE_INFINITY);
        org.junit.Assert.assertTrue(d1 == Double.POSITIVE_INFINITY);
    }

    @Test
    public void test271() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test271");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (byte) 0, (double) 1075970048, 42);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test272() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test272");
        float f1 = org.apache.commons.math.util.MathUtils.sign((float) 1075970048);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test273() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test273");
        int i1 = org.apache.commons.math.util.MathUtils.sign((-1074790369));
        org.junit.Assert.assertTrue(i1 == (-1));
    }

    @Test
    public void test274() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test274");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        java.lang.Object[] obj_array8 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException9 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable6, (java.lang.Number) (short) 100, obj_array8);
        java.lang.Number number10 = notFiniteNumberException9.getArgument();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException9);
        int i12 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException16 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i17 = nonMonotonousSequenceException16.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection18 = nonMonotonousSequenceException16.getDirection();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException16);
        boolean b20 = nonMonotonousSequenceException16.getStrict();
        boolean b21 = nonMonotonousSequenceException16.getStrict();
        int i22 = nonMonotonousSequenceException16.getIndex();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number10 + "' != '" + (short) 100 + "'", number10.equals((short) 100));
        org.junit.Assert.assertTrue(i12 == 97);
        org.junit.Assert.assertTrue(i17 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection18 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection18.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b20 == true);
        org.junit.Assert.assertTrue(b21 == true);
        org.junit.Assert.assertTrue(i22 == 97);
    }

    @Test
    public void test275() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test275");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (-1), (float) 362880000L, 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test276() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test276");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble((int) (byte) 100, (-1074790369));
        org.junit.Assert.assertTrue(d2 == 1.0d);
    }

    @Test
    public void test277() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test277");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) (short) 100, (long) (byte) 1);
        org.junit.Assert.assertTrue(long2 == 100L);
    }

    @Test
    public void test278() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test278");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b7 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection4, true, false);
        double[] d_array8 = new double[] {};
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array20 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array24 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array25 = new double[][] { d_array12, d_array16, d_array20, d_array24 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array8, d_array_array25);
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array3, d_array_array25);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.DimensionMismatchException");
        } catch (org.apache.commons.math.exception.DimensionMismatchException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b7 == true);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array24);
        org.junit.Assert.assertNotNull(d_array_array25);
    }

    @Test
    public void test279() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test279");
        try {
            long long1 = org.apache.commons.math.util.MathUtils.factorial(1075970048);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test280() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test280");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((-0.0d), (double) 10, (double) 1);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test281() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test281");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        org.apache.commons.math.complex.Complex complex5 = complex3.cosh();
        org.apache.commons.math.complex.Complex complex6 = complex5.negate();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
    }

    @Test
    public void test282() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test282");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) 10.0f, (double) (byte) 10, 0.0d);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test283() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test283");
        java.lang.Number number1 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 1.0747903976294017E9d, number1, false);
    }

    @Test
    public void test284() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test284");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        boolean b2 = complex0.isNaN();
        double d3 = complex0.getReal();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(b2 == true);
        org.junit.Assert.assertEquals((double) d3, Double.NaN, 0);
    }

    @Test
    public void test285() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test285");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient(1075970048, (-1));
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test286() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test286");
        long long1 = org.apache.commons.math.util.MathUtils.factorial((int) (short) 1);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test287() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test287");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        java.lang.String str5 = complex4.toString();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertTrue("'" + str5 + "' != '" + "(NaN, NaN)" + "'", str5.equals("(NaN, NaN)"));
    }

    @Test
    public void test288() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test288");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.tanh();
        boolean b2 = complex0.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test289() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test289");
        int i2 = org.apache.commons.math.util.MathUtils.pow((int) (short) 10, (int) (short) 0);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test290() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test290");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((double) (byte) 10, (double) 1);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test291() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test291");
        short s1 = org.apache.commons.math.util.MathUtils.sign((short) (byte) 100);
        org.junit.Assert.assertTrue(s1 == (short) 1);
    }

    @Test
    public void test292() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test292");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.lang.String str5 = complex2.toString();
        try {
            java.util.List<org.apache.commons.math.complex.Complex> list_complex7 = complex2.nthRoot(0);
            org.junit.Assert.fail("Expected anonymous exception");
        } catch (java.lang.IllegalArgumentException e) {
            if (!e.getClass().isAnonymousClass()) {
                org.junit.Assert.fail("Expected anonymous exception, got " + e.getClass().getCanonicalName());
            }
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertTrue("'" + str5 + "' != '" + "(NaN, NaN)" + "'", str5.equals("(NaN, NaN)"));
    }

    @Test
    public void test293() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test293");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(10.0f, (float) (short) -1);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test294() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test294");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, 1636086465);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test295() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test295");
        double d1 = org.apache.commons.math.util.MathUtils.sinh(35.0d);
        org.junit.Assert.assertTrue(d1 == 7.930067261567154E14d);
    }

    @Test
    public void test296() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test296");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) (byte) 100, (long) 22);
        org.junit.Assert.assertTrue(long2 == 78L);
    }

    @Test
    public void test297() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test297");
        int i2 = org.apache.commons.math.util.MathUtils.gcd(1636086465, 42);
        org.junit.Assert.assertTrue(i2 == 3);
    }

    @Test
    public void test298() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test298");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.acos();
        org.apache.commons.math.complex.Complex complex2 = complex1.log();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
    }

    @Test
    public void test299() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test299");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.copyOf(d_array5);
        double[] d_array9 = new double[] { 35.0d, (-1.0f) };
        double[] d_array12 = new double[] { 35.0d, (-1.0f) };
        double[] d_array15 = new double[] { 35.0d, (-1.0f) };
        double[] d_array18 = new double[] { 35.0d, (-1.0f) };
        double[] d_array21 = new double[] { 35.0d, (-1.0f) };
        double[][] d_array_array22 = new double[][] { d_array9, d_array12, d_array15, d_array18, d_array21 };
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array22);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.DimensionMismatchException");
        } catch (org.apache.commons.math.exception.DimensionMismatchException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array_array22);
    }

    @Test
    public void test300() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test300");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 3628800L, 0.0f, 98);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test301() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test301");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) 97);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test302() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test302");
        int i1 = org.apache.commons.math.util.MathUtils.sign((int) (short) 0);
        org.junit.Assert.assertTrue(i1 == 0);
    }

    @Test
    public void test303() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test303");
        int i1 = org.apache.commons.math.util.MathUtils.sign(100);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test304() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test304");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck(78L, (long) 1636086465);
        org.junit.Assert.assertTrue(long2 == 1636086543L);
    }

    @Test
    public void test305() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test305");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException((int) (byte) 100, 0);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext3 = dimensionMismatchException2.getContext();
        org.junit.Assert.assertNotNull(exceptionContext3);
    }

    @Test
    public void test306() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test306");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) 1L, 1636086465);
        org.junit.Assert.assertEquals((float) f2, Float.NaN, 0);
    }

    @Test
    public void test307() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test307");
        long long1 = org.apache.commons.math.util.MathUtils.indicator((-10L));
        org.junit.Assert.assertTrue(long1 == (-1L));
    }

    @Test
    public void test308() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test308");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 1072693248, (float) (-1074790369));
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test309() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test309");
        double d2 = org.apache.commons.math.util.MathUtils.log((double) (short) -1, (double) (-10L));
        org.junit.Assert.assertEquals((double) d2, Double.NaN, 0);
    }

    @Test
    public void test310() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test310");
        double d3 = org.apache.commons.math.util.MathUtils.reduce(1.0d, 35.0d, (double) (byte) 10);
        org.junit.Assert.assertTrue(d3 == 26.0d);
    }

    @Test
    public void test311() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test311");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 100L, 97);
        org.junit.Assert.assertTrue(d2 == 100.0d);
    }

    @Test
    public void test312() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test312");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck((int) (byte) -1, 9);
        org.junit.Assert.assertTrue(i2 == (-9));
    }

    @Test
    public void test313() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test313");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 1, (float) (byte) -1, 10.0f);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test314() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test314");
        int i1 = org.apache.commons.math.util.MathUtils.indicator((int) '#');
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test315() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test315");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck(1074790400, (int) (short) 1);
        org.junit.Assert.assertTrue(i2 == 1074790400);
    }

    @Test
    public void test316() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test316");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (-10L), 0.0f, (float) (byte) 100);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test317() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test317");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 1636086543L, (double) 10000000000L, 100);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test318() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test318");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = new double[] {};
        double[] d_array9 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array22 = new double[][] { d_array9, d_array13, d_array17, d_array21 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array22);
        int i24 = org.apache.commons.math.util.MathUtils.hash(d_array5);
        boolean b25 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        double[] d_array29 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d30 = org.apache.commons.math.util.MathUtils.safeNorm(d_array29);
        double[] d_array31 = new double[] {};
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array39 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array43 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array47 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array48 = new double[][] { d_array35, d_array39, d_array43, d_array47 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array31, d_array_array48);
        int i50 = org.apache.commons.math.util.MathUtils.hash(d_array31);
        boolean b51 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array29, d_array31);
        boolean b52 = org.apache.commons.math.util.MathUtils.equals(d_array5, d_array29);
        double[] d_array53 = org.apache.commons.math.util.MathUtils.copyOf(d_array5);
        double[] d_array55 = new double[] { (-1.0f) };
        double[] d_array57 = org.apache.commons.math.util.MathUtils.copyOf(d_array55, 1);
        int i58 = org.apache.commons.math.util.MathUtils.hash(d_array57);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException62 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection63 = nonMonotonousSequenceException62.getDirection();
        int i64 = nonMonotonousSequenceException62.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable65 = null;
        java.lang.Object[] obj_array67 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException68 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable65, (java.lang.Number) (short) 100, obj_array67);
        java.lang.Number number69 = notFiniteNumberException68.getArgument();
        nonMonotonousSequenceException62.addSuppressed((java.lang.Throwable) notFiniteNumberException68);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection71 = nonMonotonousSequenceException62.getDirection();
        boolean b74 = org.apache.commons.math.util.MathUtils.checkOrder(d_array57, orderDirection71, true, false);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array53, orderDirection71, false);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array_array22);
        org.junit.Assert.assertTrue(i24 == 1);
        org.junit.Assert.assertTrue(b25 == false);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertTrue(d30 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array39);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array_array48);
        org.junit.Assert.assertTrue(i50 == 1);
        org.junit.Assert.assertTrue(b51 == false);
        org.junit.Assert.assertTrue(b52 == false);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array55);
        org.junit.Assert.assertNotNull(d_array57);
        org.junit.Assert.assertTrue(i58 == (-1074790369));
        org.junit.Assert.assertTrue("'" + orderDirection63 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection63.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i64 == 97);
        org.junit.Assert.assertTrue("'" + number69 + "' != '" + (short) 100 + "'", number69.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection71 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection71.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b74 == true);
    }

    @Test
    public void test319() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test319");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble((-1074790400), 1074790400);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test320() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test320");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array19 = new double[] {};
        double[] d_array23 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array27 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array31 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array36 = new double[][] { d_array23, d_array27, d_array31, d_array35 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array19, d_array_array36);
        int i38 = org.apache.commons.math.util.MathUtils.hash(d_array19);
        double d39 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array19);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array_array36);
        org.junit.Assert.assertTrue(i38 == 1);
        org.junit.Assert.assertTrue(d39 == 0.0d);
    }

    @Test
    public void test321() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test321");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((double) 1.0f);
        org.junit.Assert.assertTrue(d1 == 1.1752011936438014d);
    }

    @Test
    public void test322() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test322");
        double[] d_array0 = null;
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test323() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test323");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 35L, 0.0f, 3);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test324() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test324");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = complex0.asin();
        org.apache.commons.math.complex.Complex complex15 = complex14.asin();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
    }

    @Test
    public void test325() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test325");
        long long1 = org.apache.commons.math.util.MathUtils.factorial((int) (byte) 10);
        org.junit.Assert.assertTrue(long1 == 3628800L);
    }

    @Test
    public void test326() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test326");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Object[] obj_array1 = null;
        org.apache.commons.math.exception.NullArgumentException nullArgumentException2 = new org.apache.commons.math.exception.NullArgumentException(localizable0, obj_array1);
    }

    @Test
    public void test327() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test327");
        int i2 = org.apache.commons.math.util.MathUtils.pow((int) 'a', 0);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test328() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test328");
        double d3 = org.apache.commons.math.util.MathUtils.reduce((double) 'a', 0.0d, (double) 100.0f);
        org.junit.Assert.assertEquals((double) d3, Double.NaN, 0);
    }

    @Test
    public void test329() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test329");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
    }

    @Test
    public void test330() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test330");
        long long1 = org.apache.commons.math.util.MathUtils.factorial(0);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test331() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test331");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex1.cos();
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex4 = complex3.cos();
        org.apache.commons.math.complex.Complex complex5 = complex1.subtract(complex3);
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex7 = complex6.cos();
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = complex8.cos();
        org.apache.commons.math.complex.Complex complex10 = complex6.subtract(complex8);
        org.apache.commons.math.complex.Complex complex11 = complex1.add(complex10);
        double d12 = complex1.getImaginary();
        org.apache.commons.math.complex.Complex complex13 = complex1.negate();
        org.apache.commons.math.complex.Complex complex14 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex15 = complex1.conjugate();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertEquals((double) d12, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
    }

    @Test
    public void test332() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test332");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient((int) (byte) 100, (int) 'a');
        org.junit.Assert.assertTrue(long2 == 161700L);
    }

    @Test
    public void test333() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test333");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) 1.0f, (double) 161700L, (double) (byte) 1);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test334() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test334");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        float[] f_array8 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array10 = new float[] { (-1074790400) };
        boolean b11 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array8, f_array10);
        java.lang.Object[] obj_array17 = new java.lang.Object[] { b11, 0, 0, (-1.0f), 100.0f, (short) 1 };
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException18 = new org.apache.commons.math.exception.MathArithmeticException(localizable2, obj_array17);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException19 = new org.apache.commons.math.exception.MathArithmeticException(localizable1, obj_array17);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException20 = new org.apache.commons.math.exception.MathArithmeticException(localizable0, obj_array17);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext21 = mathArithmeticException20.getContext();
        org.junit.Assert.assertNotNull(f_array8);
        org.junit.Assert.assertNotNull(f_array10);
        org.junit.Assert.assertTrue(b11 == false);
        org.junit.Assert.assertNotNull(obj_array17);
        org.junit.Assert.assertNotNull(exceptionContext21);
    }

    @Test
    public void test335() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test335");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex12 = complex9.pow(complex11);
        double d13 = complex12.getImaginary();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertEquals((double) d13, Double.NaN, 0);
    }

    @Test
    public void test336() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test336");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Number number2 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) 11013.232874703393d, number2, false);
    }

    @Test
    public void test337() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test337");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 1074790400, Double.POSITIVE_INFINITY, 0.0d);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test338() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test338");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = complex0.asin();
        org.apache.commons.math.complex.Complex complex15 = complex0.acos();
        org.apache.commons.math.complex.Complex complex16 = complex0.log();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
    }

    @Test
    public void test339() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test339");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.tan();
        org.apache.commons.math.complex.Complex complex7 = complex6.asin();
        boolean b8 = complex6.isInfinite();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertTrue(b8 == false);
    }

    @Test
    public void test340() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test340");
        int i1 = org.apache.commons.math.util.MathUtils.sign(0);
        org.junit.Assert.assertTrue(i1 == 0);
    }

    @Test
    public void test341() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test341");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.lcm(100, (-1074790369));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test342() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test342");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 10.04987562112089d, (java.lang.Number) 1075970048, true);
        java.lang.Throwable[] throwable_array5 = numberIsTooSmallException4.getSuppressed();
        org.junit.Assert.assertNotNull(throwable_array5);
    }

    @Test
    public void test343() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test343");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex6 = complex2.nthRoot((int) (short) 10);
        org.apache.commons.math.complex.Complex complex7 = complex2.sin();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(list_complex6);
        org.junit.Assert.assertNotNull(complex7);
    }

    @Test
    public void test344() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test344");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = complex0.subtract(complex14);
        org.apache.commons.math.complex.Complex complex17 = complex16.cosh();
        org.apache.commons.math.complex.Complex complex18 = complex16.tanh();
        org.apache.commons.math.complex.Complex complex19 = complex16.atan();
        org.apache.commons.math.complex.Complex complex20 = complex19.sin();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
    }

    @Test
    public void test345() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test345");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getImaginary();
        org.apache.commons.math.complex.Complex complex12 = complex0.negate();
        double d13 = complex12.getImaginary();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertEquals((double) d13, Double.NaN, 0);
    }

    @Test
    public void test346() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test346");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle(2.0d, (double) 0);
        org.junit.Assert.assertTrue(d2 == 2.0d);
    }

    @Test
    public void test347() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test347");
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException0 = new org.apache.commons.math.exception.MathArithmeticException();
        java.lang.String str1 = mathArithmeticException0.toString();
        org.apache.commons.math.exception.NotPositiveException notPositiveException3 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        org.apache.commons.math.exception.NotPositiveException notPositiveException5 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        notPositiveException3.addSuppressed((java.lang.Throwable) notPositiveException5);
        mathArithmeticException0.addSuppressed((java.lang.Throwable) notPositiveException5);
        java.lang.Throwable throwable8 = null;
        try {
            mathArithmeticException0.addSuppressed(throwable8);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertTrue("'" + str1 + "' != '" + "org.apache.commons.math.exception.MathArithmeticException: arithmetic exception" + "'", str1.equals("org.apache.commons.math.exception.MathArithmeticException: arithmetic exception"));
    }

    @Test
    public void test348() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test348");
        float f1 = org.apache.commons.math.util.MathUtils.indicator((float) (-1074790400));
        org.junit.Assert.assertTrue(f1 == (-1.0f));
    }

    @Test
    public void test349() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test349");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (-1074790400), (float) (short) 10, (-1074790400));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test350() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test350");
        int i2 = org.apache.commons.math.util.MathUtils.pow((int) (short) -1, 1);
        org.junit.Assert.assertTrue(i2 == (-1));
    }

    @Test
    public void test351() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test351");
        int[] i_array0 = null;
        int[] i_array1 = new int[] {};
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array1);
        try {
            double d3 = org.apache.commons.math.util.MathUtils.distance(i_array0, i_array1);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
    }

    @Test
    public void test352() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test352");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog(1636086465, (int) (byte) 10);
        org.junit.Assert.assertTrue(d2 == 197.05131665139353d);
    }

    @Test
    public void test353() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test353");
        float f1 = org.apache.commons.math.util.MathUtils.sign(Float.NaN);
        org.junit.Assert.assertEquals((float) f1, Float.NaN, 0);
    }

    @Test
    public void test354() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test354");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals(0.0f, (float) 11, 0.0f);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test355() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test355");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) (-1074790400), (java.lang.Number) 35.0d, false);
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException7 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 6.283185307179586d, (java.lang.Number) 1.0f, true);
        java.lang.String str8 = numberIsTooLargeException7.toString();
        java.lang.Number number9 = numberIsTooLargeException7.getMax();
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext10 = numberIsTooLargeException7.getContext();
        numberIsTooSmallException3.addSuppressed((java.lang.Throwable) numberIsTooLargeException7);
        org.junit.Assert.assertTrue("'" + str8 + "' != '" + "org.apache.commons.math.exception.NumberIsTooLargeException: 6.283 is larger than the maximum (1)" + "'", str8.equals("org.apache.commons.math.exception.NumberIsTooLargeException: 6.283 is larger than the maximum (1)"));
        org.junit.Assert.assertTrue("'" + number9 + "' != '" + 1.0f + "'", number9.equals(1.0f));
        org.junit.Assert.assertNotNull(exceptionContext10);
    }

    @Test
    public void test356() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test356");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 100, (float) 100, 1636086465);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test357() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test357");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array19 = new double[] {};
        double[] d_array23 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array27 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array31 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array35 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array36 = new double[][] { d_array23, d_array27, d_array31, d_array35 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array19, d_array_array36);
        double[] d_array38 = new double[] {};
        double[] d_array42 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array46 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array50 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array54 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array55 = new double[][] { d_array42, d_array46, d_array50, d_array54 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array38, d_array_array55);
        int i57 = org.apache.commons.math.util.MathUtils.hash(d_array38);
        double d58 = org.apache.commons.math.util.MathUtils.distance1(d_array19, d_array38);
        double d59 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array19);
        double[] d_array63 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d64 = org.apache.commons.math.util.MathUtils.safeNorm(d_array63);
        double[] d_array65 = new double[] {};
        double[] d_array69 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array73 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array77 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array81 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array82 = new double[][] { d_array69, d_array73, d_array77, d_array81 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array65, d_array_array82);
        int i84 = org.apache.commons.math.util.MathUtils.hash(d_array65);
        boolean b85 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array63, d_array65);
        double d86 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array65);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException90 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i91 = nonMonotonousSequenceException90.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection92 = nonMonotonousSequenceException90.getDirection();
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array65, orderDirection92, true);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array_array36);
        org.junit.Assert.assertNotNull(d_array38);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertNotNull(d_array50);
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertNotNull(d_array_array55);
        org.junit.Assert.assertTrue(i57 == 1);
        org.junit.Assert.assertTrue(d58 == 0.0d);
        org.junit.Assert.assertTrue(d59 == 0.0d);
        org.junit.Assert.assertNotNull(d_array63);
        org.junit.Assert.assertTrue(d64 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array65);
        org.junit.Assert.assertNotNull(d_array69);
        org.junit.Assert.assertNotNull(d_array73);
        org.junit.Assert.assertNotNull(d_array77);
        org.junit.Assert.assertNotNull(d_array81);
        org.junit.Assert.assertNotNull(d_array_array82);
        org.junit.Assert.assertTrue(i84 == 1);
        org.junit.Assert.assertTrue(b85 == false);
        org.junit.Assert.assertTrue(d86 == 0.0d);
        org.junit.Assert.assertTrue(i91 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection92 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection92.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test358() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test358");
        try {
            long long1 = org.apache.commons.math.util.MathUtils.factorial((int) ' ');
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test359() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test359");
        int i1 = org.apache.commons.math.util.MathUtils.indicator((-2064878497));
        org.junit.Assert.assertTrue(i1 == (-1));
    }

    @Test
    public void test360() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test360");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.tan();
        org.apache.commons.math.complex.Complex complex7 = complex6.asin();
        org.apache.commons.math.complex.Complex complex8 = complex7.negate();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
    }

    @Test
    public void test361() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test361");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round((float) (byte) 100, (int) (byte) 0, (-1074790400));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test362() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test362");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((double) (short) -1);
        org.junit.Assert.assertTrue(d1 == (-1.1752011936438014d));
    }

    @Test
    public void test363() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test363");
        org.apache.commons.math.util.MathUtils.checkFinite((double) 9L);
    }

    @Test
    public void test364() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test364");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 10.04987562112089d, (java.lang.Number) 1075970048, true);
        try {
            java.lang.String str5 = numberIsTooSmallException4.toString();
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test365() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test365");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck((long) 22, 35L);
        org.junit.Assert.assertTrue(long2 == 57L);
    }

    @Test
    public void test366() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test366");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.copyOf(d_array3);
        double[] d_array8 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, 349.9541180407703d);
        double[] d_array10 = new double[] { (-1.0f) };
        double[] d_array12 = org.apache.commons.math.util.MathUtils.copyOf(d_array10, 1);
        double[] d_array14 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array12, (double) 0L);
        double[] d_array16 = new double[] { (byte) -1 };
        double[] d_array18 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array16, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection19 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b22 = org.apache.commons.math.util.MathUtils.checkOrder(d_array18, orderDirection19, true, false);
        boolean b23 = org.apache.commons.math.util.MathUtils.equals(d_array12, d_array18);
        double[] d_array24 = new double[] {};
        double[] d_array28 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array32 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array36 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array40 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array41 = new double[][] { d_array28, d_array32, d_array36, d_array40 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array24, d_array_array41);
        double[] d_array44 = new double[] { (byte) -1 };
        double[] d_array46 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array44, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException50 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection51 = nonMonotonousSequenceException50.getDirection();
        int i52 = nonMonotonousSequenceException50.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable53 = null;
        java.lang.Object[] obj_array55 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException56 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable53, (java.lang.Number) (short) 100, obj_array55);
        java.lang.Number number57 = notFiniteNumberException56.getArgument();
        nonMonotonousSequenceException50.addSuppressed((java.lang.Throwable) notFiniteNumberException56);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection59 = nonMonotonousSequenceException50.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array46, orderDirection59, false);
        double d62 = org.apache.commons.math.util.MathUtils.distance1(d_array24, d_array46);
        double[] d_array64 = new double[] { (byte) -1 };
        double[] d_array66 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array64, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException70 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i71 = nonMonotonousSequenceException70.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection72 = nonMonotonousSequenceException70.getDirection();
        boolean b75 = org.apache.commons.math.util.MathUtils.checkOrder(d_array64, orderDirection72, true, false);
        double d76 = org.apache.commons.math.util.MathUtils.distance(d_array46, d_array64);
        double d77 = org.apache.commons.math.util.MathUtils.distance1(d_array18, d_array46);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection78 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.util.MathUtils.checkOrder(d_array18, orderDirection78, true);
        boolean b83 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection78, false, true);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array14);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertTrue("'" + orderDirection19 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection19.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b22 == true);
        org.junit.Assert.assertTrue(b23 == false);
        org.junit.Assert.assertNotNull(d_array24);
        org.junit.Assert.assertNotNull(d_array28);
        org.junit.Assert.assertNotNull(d_array32);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertNotNull(d_array_array41);
        org.junit.Assert.assertNotNull(d_array44);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertTrue("'" + orderDirection51 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection51.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i52 == 97);
        org.junit.Assert.assertTrue("'" + number57 + "' != '" + (short) 100 + "'", number57.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection59 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection59.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d62 == 0.0d);
        org.junit.Assert.assertNotNull(d_array64);
        org.junit.Assert.assertNotNull(d_array66);
        org.junit.Assert.assertTrue(i71 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection72 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection72.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b75 == true);
        org.junit.Assert.assertTrue(d76 == 2.0d);
        org.junit.Assert.assertTrue(d77 == 0.0d);
        org.junit.Assert.assertTrue("'" + orderDirection78 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection78.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b83 == true);
    }

    @Test
    public void test367() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test367");
        float[] f_array0 = null;
        float[] f_array6 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array8 = new float[] { (-1074790400) };
        boolean b9 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array6, f_array8);
        float[] f_array15 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array17 = new float[] { (-1074790400) };
        boolean b18 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array15, f_array17);
        float[] f_array24 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array26 = new float[] { (-1074790400) };
        boolean b27 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array24, f_array26);
        boolean b28 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array17, f_array24);
        float[] f_array34 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array36 = new float[] { (-1074790400) };
        boolean b37 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array34, f_array36);
        float[] f_array43 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array45 = new float[] { (-1074790400) };
        boolean b46 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array43, f_array45);
        boolean b47 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array36, f_array43);
        boolean b48 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array17, f_array43);
        boolean b49 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array8, f_array43);
        float[] f_array55 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array57 = new float[] { (-1074790400) };
        boolean b58 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array55, f_array57);
        boolean b59 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array43, f_array55);
        boolean b60 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array0, f_array55);
        float[] f_array66 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array68 = new float[] { (-1074790400) };
        boolean b69 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array66, f_array68);
        float[] f_array75 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array77 = new float[] { (-1074790400) };
        boolean b78 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array75, f_array77);
        boolean b79 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array68, f_array75);
        boolean b80 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array0, f_array75);
        org.junit.Assert.assertNotNull(f_array6);
        org.junit.Assert.assertNotNull(f_array8);
        org.junit.Assert.assertTrue(b9 == false);
        org.junit.Assert.assertNotNull(f_array15);
        org.junit.Assert.assertNotNull(f_array17);
        org.junit.Assert.assertTrue(b18 == false);
        org.junit.Assert.assertNotNull(f_array24);
        org.junit.Assert.assertNotNull(f_array26);
        org.junit.Assert.assertTrue(b27 == false);
        org.junit.Assert.assertTrue(b28 == false);
        org.junit.Assert.assertNotNull(f_array34);
        org.junit.Assert.assertNotNull(f_array36);
        org.junit.Assert.assertTrue(b37 == false);
        org.junit.Assert.assertNotNull(f_array43);
        org.junit.Assert.assertNotNull(f_array45);
        org.junit.Assert.assertTrue(b46 == false);
        org.junit.Assert.assertTrue(b47 == false);
        org.junit.Assert.assertTrue(b48 == false);
        org.junit.Assert.assertTrue(b49 == false);
        org.junit.Assert.assertNotNull(f_array55);
        org.junit.Assert.assertNotNull(f_array57);
        org.junit.Assert.assertTrue(b58 == false);
        org.junit.Assert.assertTrue(b59 == true);
        org.junit.Assert.assertTrue(b60 == false);
        org.junit.Assert.assertNotNull(f_array66);
        org.junit.Assert.assertNotNull(f_array68);
        org.junit.Assert.assertTrue(b69 == false);
        org.junit.Assert.assertNotNull(f_array75);
        org.junit.Assert.assertNotNull(f_array77);
        org.junit.Assert.assertTrue(b78 == false);
        org.junit.Assert.assertTrue(b79 == false);
        org.junit.Assert.assertTrue(b80 == false);
    }

    @Test
    public void test368() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test368");
        int i1 = org.apache.commons.math.util.MathUtils.hash((double) 0L);
        org.junit.Assert.assertTrue(i1 == 0);
    }

    @Test
    public void test369() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test369");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow((int) (byte) -1, (long) (-1074790400));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test370() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test370");
        try {
            double d3 = org.apache.commons.math.util.MathUtils.round((double) 0L, 100, (int) ' ');
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @Test
    public void test371() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test371");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = complex0.subtract(complex14);
        org.apache.commons.math.complex.Complex complex17 = complex16.asin();
        org.apache.commons.math.complex.Complex complex18 = complex17.log();
        boolean b19 = complex17.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertTrue(b19 == true);
    }

    @Test
    public void test372() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test372");
        int i1 = org.apache.commons.math.util.MathUtils.hash((double) 1636086543L);
        org.junit.Assert.assertTrue(i1 == (-2112331476));
    }

    @Test
    public void test373() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test373");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 0, (int) (short) -1);
    }

    @Test
    public void test374() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test374");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient((-2112331476), 97);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test375() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test375");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (short) 10, (float) (short) 1);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test376() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test376");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array1);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, 1.1752011936438014d);
        double[] d_array7 = new double[] {};
        double[] d_array11 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array15 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array19 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array23 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array24 = new double[][] { d_array11, d_array15, d_array19, d_array23 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array7, d_array_array24);
        double[] d_array26 = new double[] {};
        double[] d_array30 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array34 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array38 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array42 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array43 = new double[][] { d_array30, d_array34, d_array38, d_array42 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array26, d_array_array43);
        double[] d_array45 = new double[] {};
        double[] d_array49 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array53 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array57 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array61 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array62 = new double[][] { d_array49, d_array53, d_array57, d_array61 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array45, d_array_array62);
        int i64 = org.apache.commons.math.util.MathUtils.hash(d_array45);
        double d65 = org.apache.commons.math.util.MathUtils.distance1(d_array26, d_array45);
        double d66 = org.apache.commons.math.util.MathUtils.distance1(d_array7, d_array26);
        int i67 = org.apache.commons.math.util.MathUtils.hash(d_array7);
        try {
            double d68 = org.apache.commons.math.util.MathUtils.distanceInf(d_array1, d_array7);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 1.0d);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array_array24);
        org.junit.Assert.assertNotNull(d_array26);
        org.junit.Assert.assertNotNull(d_array30);
        org.junit.Assert.assertNotNull(d_array34);
        org.junit.Assert.assertNotNull(d_array38);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array_array43);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array57);
        org.junit.Assert.assertNotNull(d_array61);
        org.junit.Assert.assertNotNull(d_array_array62);
        org.junit.Assert.assertTrue(i64 == 1);
        org.junit.Assert.assertTrue(d65 == 0.0d);
        org.junit.Assert.assertTrue(d66 == 0.0d);
        org.junit.Assert.assertTrue(i67 == 1);
    }

    @Test
    public void test377() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test377");
        double d1 = org.apache.commons.math.util.MathUtils.factorialDouble((int) (byte) 100);
        org.junit.Assert.assertTrue(d1 == 9.332621544395286E157d);
    }

    @Test
    public void test378() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test378");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) (short) 0, (java.lang.Number) 9L, false);
    }

    @Test
    public void test379() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test379");
        double d2 = org.apache.commons.math.util.MathUtils.round(0.0d, 11);
        org.junit.Assert.assertTrue(d2 == 0.0d);
    }

    @Test
    public void test380() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test380");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        boolean b5 = complex3.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertTrue(b5 == false);
    }

    @Test
    public void test381() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test381");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (-1074790400), (-9));
    }

    @Test
    public void test382() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test382");
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) 1L);
    }

    @Test
    public void test383() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test383");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((long) (-9), (long) (-2112331476));
        org.junit.Assert.assertTrue(long2 == 6336994428L);
    }

    @Test
    public void test384() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test384");
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) 10.0f);
    }

    @Test
    public void test385() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test385");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 35L, (float) 100L, (float) '4');
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test386() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test386");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) 98);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test387() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test387");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck(22, (int) (short) 1);
        org.junit.Assert.assertTrue(i2 == 22);
    }

    @Test
    public void test388() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test388");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex1.cos();
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex4 = complex3.cos();
        org.apache.commons.math.complex.Complex complex5 = complex1.subtract(complex3);
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex7 = complex6.cos();
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = complex8.cos();
        org.apache.commons.math.complex.Complex complex10 = complex6.subtract(complex8);
        org.apache.commons.math.complex.Complex complex11 = complex1.add(complex10);
        double d12 = complex1.getArgument();
        org.apache.commons.math.complex.Complex complex13 = complex1.exp();
        org.apache.commons.math.complex.Complex complex14 = complex1.negate();
        org.apache.commons.math.complex.Complex complex15 = complex1.asin();
        org.apache.commons.math.complex.Complex complex16 = complex1.acos();
        org.apache.commons.math.exception.util.Localizable localizable17 = null;
        double[] d_array18 = new double[] {};
        double[] d_array22 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array26 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array30 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array34 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array35 = new double[][] { d_array22, d_array26, d_array30, d_array34 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array18, d_array_array35);
        double[] d_array37 = new double[] {};
        double[] d_array41 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array45 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array49 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array53 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array54 = new double[][] { d_array41, d_array45, d_array49, d_array53 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array37, d_array_array54);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array18, d_array_array54);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex1, localizable17, (java.lang.Object[]) d_array_array54);
        org.apache.commons.math.exception.MathIllegalArgumentException mathIllegalArgumentException58 = new org.apache.commons.math.exception.MathIllegalArgumentException(localizable0, (java.lang.Object[]) d_array_array54);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext59 = mathIllegalArgumentException58.getContext();
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertEquals((double) d12, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertNotNull(d_array26);
        org.junit.Assert.assertNotNull(d_array30);
        org.junit.Assert.assertNotNull(d_array34);
        org.junit.Assert.assertNotNull(d_array_array35);
        org.junit.Assert.assertNotNull(d_array37);
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array_array54);
        org.junit.Assert.assertNotNull(exceptionContext59);
    }

    @Test
    public void test389() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test389");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b7 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection4, true, false);
        double[] d_array9 = org.apache.commons.math.util.MathUtils.copyOf(d_array3, 97);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array9);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NonMonotonousSequenceException");
        } catch (org.apache.commons.math.exception.NonMonotonousSequenceException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b7 == true);
        org.junit.Assert.assertNotNull(d_array9);
    }

    @Test
    public void test390() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test390");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((double) 100);
        org.junit.Assert.assertTrue(d1 == 1.3440585709080678E43d);
    }

    @Test
    public void test391() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test391");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex12 = complex9.pow(complex11);
        double d13 = complex9.getReal();
        try {
            java.util.List<org.apache.commons.math.complex.Complex> list_complex15 = complex9.nthRoot(0);
            org.junit.Assert.fail("Expected anonymous exception");
        } catch (java.lang.IllegalArgumentException e) {
            if (!e.getClass().isAnonymousClass()) {
                org.junit.Assert.fail("Expected anonymous exception, got " + e.getClass().getCanonicalName());
            }
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertEquals((double) d13, Double.NaN, 0);
    }

    @Test
    public void test392() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test392");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        java.math.BigInteger bigInteger3 = null;
        java.math.BigInteger bigInteger5 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, (long) 0);
        try {
            java.math.BigInteger bigInteger6 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, bigInteger5);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger2);
        org.junit.Assert.assertNotNull(bigInteger5);
    }

    @Test
    public void test393() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test393");
        double[] d_array5 = new double[] { (-1074790400), 10.04987562112089d, (-2112331476), 3500L, (byte) 100 };
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array5);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NonMonotonousSequenceException");
        } catch (org.apache.commons.math.exception.NonMonotonousSequenceException e) {
        }
        org.junit.Assert.assertNotNull(d_array5);
    }

    @Test
    public void test394() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test394");
        long long1 = org.apache.commons.math.util.MathUtils.sign(78L);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test395() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test395");
        double d3 = org.apache.commons.math.util.MathUtils.reduce(0.0d, 1.0747903976294017E9d, (-0.0d));
        org.junit.Assert.assertTrue(d3 == 0.0d);
    }

    @Test
    public void test396() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test396");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 7766279631452241920L, (java.lang.Number) (byte) 1, true);
    }

    @Test
    public void test397() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test397");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle((double) (-447362047), (double) (byte) 100);
        org.junit.Assert.assertTrue(d2 == 99.70309990644455d);
    }

    @Test
    public void test398() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test398");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog(1075970048, 22);
        org.junit.Assert.assertTrue(d2 == 409.0515645945562d);
    }

    @Test
    public void test399() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test399");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient((int) '#', (int) '#');
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test400() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test400");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) 0.0f, 100.0d, (double) (short) -1);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test401() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test401");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) (-9), (long) (-1));
        org.junit.Assert.assertTrue(long2 == (-8L));
    }

    @Test
    public void test402() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test402");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        java.lang.Number number4 = nonMonotonousSequenceException3.getPrevious();
        java.lang.Number number5 = nonMonotonousSequenceException3.getPrevious();
        int i6 = nonMonotonousSequenceException3.getIndex();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 2.2250738585072014E-308d + "'", number4.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + 2.2250738585072014E-308d + "'", number5.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue(i6 == 97);
    }

    @Test
    public void test403() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test403");
        short s1 = org.apache.commons.math.util.MathUtils.indicator((short) (byte) -1);
        org.junit.Assert.assertTrue(s1 == (short) -1);
    }

    @Test
    public void test404() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test404");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck((long) (-2112331476), (long) '#');
        org.junit.Assert.assertTrue(long2 == (-73931601660L));
    }

    @Test
    public void test405() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test405");
        long long1 = org.apache.commons.math.util.MathUtils.sign((long) 1);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test406() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test406");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(1636086465, (-2112331476));
        org.junit.Assert.assertTrue(d2 == 1.0d);
    }

    @Test
    public void test407() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test407");
        double[] d_array0 = null;
        double[] d_array2 = new double[] { (byte) -1 };
        double[] d_array4 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array2, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection5 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b8 = org.apache.commons.math.util.MathUtils.checkOrder(d_array4, orderDirection5, true, false);
        org.apache.commons.math.exception.util.Localizable localizable9 = null;
        double[] d_array10 = new double[] {};
        double[] d_array14 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array18 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array22 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array26 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array27 = new double[][] { d_array14, d_array18, d_array22, d_array26 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array10, d_array_array27);
        double[] d_array29 = new double[] {};
        double[] d_array33 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array37 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array41 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array45 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array46 = new double[][] { d_array33, d_array37, d_array41, d_array45 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array29, d_array_array46);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array10, d_array_array46);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException49 = new org.apache.commons.math.exception.MathArithmeticException(localizable9, (java.lang.Object[]) d_array_array46);
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, orderDirection5, d_array_array46);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(d_array2);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertTrue("'" + orderDirection5 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection5.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b8 == true);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertNotNull(d_array14);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertNotNull(d_array26);
        org.junit.Assert.assertNotNull(d_array_array27);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array33);
        org.junit.Assert.assertNotNull(d_array37);
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertNotNull(d_array_array46);
    }

    @Test
    public void test408() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test408");
        long long1 = org.apache.commons.math.util.MathUtils.indicator((long) (-2112331476));
        org.junit.Assert.assertTrue(long1 == (-1L));
    }

    @Test
    public void test409() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test409");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient((-447362047), 1636086465);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test410() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test410");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (-2064878497), 0.0d, (-0.0d));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test411() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test411");
        org.apache.commons.math.util.MathUtils.checkFinite(2.2250738585072014E-308d);
    }

    @Test
    public void test412() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test412");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.tan();
        org.apache.commons.math.complex.Complex complex7 = complex1.cosh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
    }

    @Test
    public void test413() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test413");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        boolean b2 = complex0.isInfinite();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test414() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test414");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((double) 362880000L);
        org.junit.Assert.assertTrue(d1 == Double.POSITIVE_INFINITY);
    }

    @Test
    public void test415() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test415");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex6 = complex2.nthRoot((int) (short) 10);
        org.apache.commons.math.complex.Complex complex7 = complex2.sqrt1z();
        boolean b8 = complex7.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(list_complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertTrue(b8 == true);
    }

    @Test
    public void test416() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test416");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        java.lang.Object[] obj_array4 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException5 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable2, (java.lang.Number) 100.0d, obj_array4);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException6 = new org.apache.commons.math.exception.NullArgumentException(localizable1, obj_array4);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException7 = new org.apache.commons.math.exception.NullArgumentException(localizable0, obj_array4);
        org.junit.Assert.assertNotNull(obj_array4);
    }

    @Test
    public void test417() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test417");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 6336994428L, (java.lang.Number) (short) 10, false);
    }

    @Test
    public void test418() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test418");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException7 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection8 = nonMonotonousSequenceException7.getDirection();
        int i9 = nonMonotonousSequenceException7.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable10 = null;
        java.lang.Object[] obj_array12 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException13 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable10, (java.lang.Number) (short) 100, obj_array12);
        java.lang.Number number14 = notFiniteNumberException13.getArgument();
        nonMonotonousSequenceException7.addSuppressed((java.lang.Throwable) notFiniteNumberException13);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection16 = nonMonotonousSequenceException7.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection16, false);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array3);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue("'" + orderDirection8 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection8.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i9 == 97);
        org.junit.Assert.assertTrue("'" + number14 + "' != '" + (short) 100 + "'", number14.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection16 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection16.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test419() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test419");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient(100, (int) '4');
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test420() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test420");
        long long1 = org.apache.commons.math.util.MathUtils.indicator((long) 3);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test421() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test421");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (-9), 0);
    }

    @Test
    public void test422() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test422");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex1.cos();
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex4 = complex3.cos();
        org.apache.commons.math.complex.Complex complex5 = complex1.subtract(complex3);
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex7 = complex6.cos();
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = complex8.cos();
        org.apache.commons.math.complex.Complex complex10 = complex6.subtract(complex8);
        org.apache.commons.math.complex.Complex complex11 = complex1.add(complex10);
        double d12 = complex1.getArgument();
        org.apache.commons.math.complex.Complex complex13 = complex1.exp();
        org.apache.commons.math.complex.Complex complex14 = complex1.negate();
        org.apache.commons.math.complex.Complex complex15 = complex1.asin();
        org.apache.commons.math.complex.Complex complex16 = complex1.acos();
        org.apache.commons.math.exception.util.Localizable localizable17 = null;
        double[] d_array18 = new double[] {};
        double[] d_array22 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array26 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array30 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array34 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array35 = new double[][] { d_array22, d_array26, d_array30, d_array34 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array18, d_array_array35);
        double[] d_array37 = new double[] {};
        double[] d_array41 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array45 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array49 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array53 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array54 = new double[][] { d_array41, d_array45, d_array49, d_array53 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array37, d_array_array54);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array18, d_array_array54);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex1, localizable17, (java.lang.Object[]) d_array_array54);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException58 = new org.apache.commons.math.exception.NullArgumentException(localizable0, (java.lang.Object[]) d_array_array54);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertEquals((double) d12, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertNotNull(d_array26);
        org.junit.Assert.assertNotNull(d_array30);
        org.junit.Assert.assertNotNull(d_array34);
        org.junit.Assert.assertNotNull(d_array_array35);
        org.junit.Assert.assertNotNull(d_array37);
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array_array54);
    }

    @Test
    public void test423() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test423");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Object[] obj_array1 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NullArgumentException nullArgumentException2 = new org.apache.commons.math.exception.NullArgumentException(localizable0, obj_array1);
        try {
            java.lang.String str3 = nullArgumentException2.toString();
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(obj_array1);
    }

    @Test
    public void test424() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test424");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = new double[] {};
        double[] d_array9 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array22 = new double[][] { d_array9, d_array13, d_array17, d_array21 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array22);
        int i24 = org.apache.commons.math.util.MathUtils.hash(d_array5);
        boolean b25 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        double[] d_array27 = new double[] { (-1.0f) };
        double[] d_array29 = org.apache.commons.math.util.MathUtils.copyOf(d_array27, 1);
        double[] d_array31 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array29, (double) 0L);
        double[] d_array33 = new double[] { (byte) -1 };
        double[] d_array35 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array33, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection36 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b39 = org.apache.commons.math.util.MathUtils.checkOrder(d_array35, orderDirection36, true, false);
        boolean b40 = org.apache.commons.math.util.MathUtils.equals(d_array29, d_array35);
        double[] d_array41 = new double[] {};
        double[] d_array45 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array49 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array53 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array57 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array58 = new double[][] { d_array45, d_array49, d_array53, d_array57 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array41, d_array_array58);
        double[] d_array61 = new double[] { (byte) -1 };
        double[] d_array63 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array61, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException67 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection68 = nonMonotonousSequenceException67.getDirection();
        int i69 = nonMonotonousSequenceException67.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable70 = null;
        java.lang.Object[] obj_array72 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException73 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable70, (java.lang.Number) (short) 100, obj_array72);
        java.lang.Number number74 = notFiniteNumberException73.getArgument();
        nonMonotonousSequenceException67.addSuppressed((java.lang.Throwable) notFiniteNumberException73);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection76 = nonMonotonousSequenceException67.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array63, orderDirection76, false);
        double d79 = org.apache.commons.math.util.MathUtils.distance1(d_array41, d_array63);
        double[] d_array81 = new double[] { (byte) -1 };
        double[] d_array83 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array81, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException87 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i88 = nonMonotonousSequenceException87.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection89 = nonMonotonousSequenceException87.getDirection();
        boolean b92 = org.apache.commons.math.util.MathUtils.checkOrder(d_array81, orderDirection89, true, false);
        double d93 = org.apache.commons.math.util.MathUtils.distance(d_array63, d_array81);
        double d94 = org.apache.commons.math.util.MathUtils.distance1(d_array35, d_array63);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection95 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.util.MathUtils.checkOrder(d_array35, orderDirection95, true);
        double d98 = org.apache.commons.math.util.MathUtils.distance(d_array5, d_array35);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array_array22);
        org.junit.Assert.assertTrue(i24 == 1);
        org.junit.Assert.assertTrue(b25 == false);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array31);
        org.junit.Assert.assertNotNull(d_array33);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertTrue("'" + orderDirection36 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection36.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b39 == true);
        org.junit.Assert.assertTrue(b40 == false);
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array57);
        org.junit.Assert.assertNotNull(d_array_array58);
        org.junit.Assert.assertNotNull(d_array61);
        org.junit.Assert.assertNotNull(d_array63);
        org.junit.Assert.assertTrue("'" + orderDirection68 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection68.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i69 == 97);
        org.junit.Assert.assertTrue("'" + number74 + "' != '" + (short) 100 + "'", number74.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection76 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection76.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d79 == 0.0d);
        org.junit.Assert.assertNotNull(d_array81);
        org.junit.Assert.assertNotNull(d_array83);
        org.junit.Assert.assertTrue(i88 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection89 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection89.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b92 == true);
        org.junit.Assert.assertTrue(d93 == 2.0d);
        org.junit.Assert.assertTrue(d94 == 0.0d);
        org.junit.Assert.assertTrue("'" + orderDirection95 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection95.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d98 == 0.0d);
    }

    @Test
    public void test425() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test425");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) 6336994428L, 97);
        org.junit.Assert.assertEquals((float) f2, Float.NaN, 0);
    }

    @Test
    public void test426() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test426");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((-0.0d));
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test427() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test427");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(0, (int) (byte) 1);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test428() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test428");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex3.subtract(complex9);
        org.apache.commons.math.complex.Complex complex11 = complex10.sqrt1z();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
    }

    @Test
    public void test429() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test429");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) (short) 1, Double.NaN, (int) (short) 0);
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test430() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test430");
        try {
            long long1 = org.apache.commons.math.util.MathUtils.factorial((-1074790369));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test431() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test431");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.sqrt();
        org.apache.commons.math.complex.Complex complex2 = complex0.sinh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
    }

    @Test
    public void test432() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test432");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getImaginary();
        double d12 = complex0.getReal();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertEquals((double) d12, Double.NaN, 0);
    }

    @Test
    public void test433() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test433");
        long long1 = org.apache.commons.math.util.MathUtils.factorial((int) (short) 0);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test434() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test434");
        byte byte1 = org.apache.commons.math.util.MathUtils.sign((byte) 0);
        org.junit.Assert.assertTrue(byte1 == (byte) 0);
    }

    @Test
    public void test435() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test435");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (int) '4', 1636086465);
        try {
            java.lang.String str4 = dimensionMismatchException3.toString();
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test436() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test436");
        long long2 = org.apache.commons.math.util.MathUtils.pow(6336994428L, (long) ' ');
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test437() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test437");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        org.apache.commons.math.complex.Complex complex7 = complex6.tanh();
        double d8 = complex7.getReal();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertEquals((double) d8, Double.NaN, 0);
    }

    @Test
    public void test438() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test438");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) (short) 100, (long) 'a');
        org.junit.Assert.assertTrue(long2 == 3L);
    }

    @Test
    public void test439() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test439");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) 362880000L, 0);
        org.junit.Assert.assertTrue(f2 == 3.6288E8f);
    }

    @Test
    public void test440() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test440");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle((double) 0L, 100.0d);
        org.junit.Assert.assertTrue(d2 == 100.53096491487338d);
    }

    @Test
    public void test441() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test441");
        int i1 = org.apache.commons.math.util.MathUtils.sign((int) '#');
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test442() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test442");
        try {
            double d3 = org.apache.commons.math.util.MathUtils.round((double) 1072693248, (int) (short) 0, (-9));
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @Test
    public void test443() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test443");
        int i2 = org.apache.commons.math.util.MathUtils.gcd(10, (int) '4');
        org.junit.Assert.assertTrue(i2 == 2);
    }

    @Test
    public void test444() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test444");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.I;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
    }

    @Test
    public void test445() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test445");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) (short) -1, (int) (byte) 1);
        org.junit.Assert.assertTrue(long2 == (-1L));
    }

    @Test
    public void test446() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test446");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getImaginary();
        boolean b12 = complex0.isInfinite();
        org.apache.commons.math.complex.Complex complex13 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex14 = complex13.cos();
        org.apache.commons.math.complex.Complex complex15 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex16 = complex15.cos();
        org.apache.commons.math.complex.Complex complex17 = complex13.subtract(complex15);
        org.apache.commons.math.complex.Complex complex18 = complex15.acos();
        double d19 = complex15.getReal();
        org.apache.commons.math.complex.Complex complex20 = complex0.divide(complex15);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertTrue(b12 == false);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertEquals((double) d19, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex20);
    }

    @Test
    public void test447() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test447");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 1636086465, (float) 1072693248, 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test448() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test448");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) 1636086465, 0);
        org.junit.Assert.assertTrue(f2 == 1.63608653E9f);
    }

    @Test
    public void test449() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test449");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex(Double.NEGATIVE_INFINITY, (double) (byte) 100);
    }

    @Test
    public void test450() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test450");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex6 = complex2.nthRoot((int) (short) 10);
        double d7 = complex2.abs();
        org.apache.commons.math.complex.Complex complex8 = complex2.atan();
        org.apache.commons.math.complex.Complex complex9 = complex2.acos();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(list_complex6);
        org.junit.Assert.assertEquals((double) d7, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
    }

    @Test
    public void test451() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test451");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 3.6288E8f, 1.1752011936438014d);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test452() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test452");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble((-2064878497), 0);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test453() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test453");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b7 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection4, true, false);
        double[] d_array11 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d12 = org.apache.commons.math.util.MathUtils.safeNorm(d_array11);
        int i13 = org.apache.commons.math.util.MathUtils.hash(d_array11);
        boolean b14 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array11);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array11);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NonMonotonousSequenceException");
        } catch (org.apache.commons.math.exception.NonMonotonousSequenceException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b7 == true);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertTrue(d12 == 10.04987562112089d);
        org.junit.Assert.assertTrue(i13 == (-2064878497));
        org.junit.Assert.assertTrue(b14 == false);
    }

    @Test
    public void test454() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test454");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) 1.0d);
        java.lang.Number number3 = notPositiveException2.getMin();
        org.junit.Assert.assertTrue("'" + number3 + "' != '" + 0 + "'", number3.equals(0));
    }

    @Test
    public void test455() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test455");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex3.subtract(complex9);
        double d11 = complex9.abs();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
    }

    @Test
    public void test456() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test456");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 10.0d, (java.lang.Number) 98, false);
    }

    @Test
    public void test457() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test457");
        try {
            double d3 = org.apache.commons.math.util.MathUtils.round((double) 6336994428L, 1075970048, (-447362047));
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @Test
    public void test458() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test458");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck(1, (int) ' ');
        org.junit.Assert.assertTrue(i2 == 33);
    }

    @Test
    public void test459() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test459");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.exp();
        org.apache.commons.math.complex.Complex complex3 = complex2.sqrt();
        org.apache.commons.math.complex.Complex complex4 = complex3.exp();
        org.apache.commons.math.complex.Complex complex5 = complex4.acos();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
    }

    @Test
    public void test460() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test460");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = complex0.subtract(complex14);
        org.apache.commons.math.complex.Complex complex17 = complex16.cosh();
        org.apache.commons.math.complex.Complex complex18 = complex17.sinh();
        boolean b19 = complex18.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertTrue(b19 == true);
    }

    @Test
    public void test461() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test461");
        short s1 = org.apache.commons.math.util.MathUtils.sign((short) 0);
        org.junit.Assert.assertTrue(s1 == (short) 0);
    }

    @Test
    public void test462() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test462");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        try {
            java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, 1);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger2);
    }

    @Test
    public void test463() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test463");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex12.acos();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
    }

    @Test
    public void test464() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test464");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((long) 1636086465, (long) '4');
        org.junit.Assert.assertTrue(long2 == 6544345860L);
    }

    @Test
    public void test465() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test465");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = complex0.asin();
        org.apache.commons.math.complex.Complex complex15 = complex14.sinh();
        org.apache.commons.math.complex.Complex complex16 = complex15.cosh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
    }

    @Test
    public void test466() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test466");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = complex0.subtract(complex14);
        double d17 = complex16.getReal();
        double d18 = complex16.getImaginary();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertEquals((double) d17, Double.NaN, 0);
        org.junit.Assert.assertEquals((double) d18, Double.NaN, 0);
    }

    @Test
    public void test467() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test467");
        long long1 = org.apache.commons.math.util.MathUtils.sign((long) (-1));
        org.junit.Assert.assertTrue(long1 == (-1L));
    }

    @Test
    public void test468() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test468");
        double d1 = org.apache.commons.math.util.MathUtils.cosh((double) 6336994428L);
        org.junit.Assert.assertTrue(d1 == Double.POSITIVE_INFINITY);
    }

    @Test
    public void test469() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test469");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) 1075970048, (int) (byte) 0);
        org.junit.Assert.assertTrue(f2 == 1.07597005E9f);
    }

    @Test
    public void test470() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test470");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((-0.0d));
        org.junit.Assert.assertTrue(d1 == 0.0d);
    }

    @Test
    public void test471() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test471");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 2, (float) (byte) 100, (float) (-1074790400));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test472() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test472");
        float f1 = org.apache.commons.math.util.MathUtils.indicator((float) (-10L));
        org.junit.Assert.assertTrue(f1 == (-1.0f));
    }

    @Test
    public void test473() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test473");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 10L, (double) 1.0f, (double) (-9));
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test474() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test474");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 3.6288E8f, 9.332621544395286E157d, (double) (-1074790369));
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test475() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test475");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) 1.0066581708938198d);
        boolean b2 = notPositiveException1.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b2 == true);
    }

    @Test
    public void test476() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test476");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals(11013.232874703393d, (double) (byte) 0, (double) (byte) 100);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test477() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test477");
        double[] d_array0 = null;
        double[] d_array2 = new double[] { (-1.0f) };
        double[] d_array4 = org.apache.commons.math.util.MathUtils.copyOf(d_array2, 1);
        double[] d_array6 = new double[] { (-1.0f) };
        double[] d_array8 = org.apache.commons.math.util.MathUtils.copyOf(d_array6, 1);
        double d9 = org.apache.commons.math.util.MathUtils.safeNorm(d_array6);
        double[] d_array11 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array6, 1.1752011936438014d);
        double d12 = org.apache.commons.math.util.MathUtils.distance1(d_array4, d_array6);
        double[] d_array13 = org.apache.commons.math.util.MathUtils.copyOf(d_array6);
        try {
            double d14 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array13);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(d_array2);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertTrue(d9 == 1.0d);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertTrue(d12 == 0.0d);
        org.junit.Assert.assertNotNull(d_array13);
    }

    @Test
    public void test478() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test478");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo(10.04987562112089d, (double) 78L, 0.0d);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test479() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test479");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble((int) (short) 100, 98);
        org.junit.Assert.assertTrue(d2 == 4950.0d);
    }

    @Test
    public void test480() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test480");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.acos();
        double d4 = complex3.getImaginary();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertTrue(d4 == (-0.0d));
    }

    @Test
    public void test481() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test481");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        try {
            java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, 97);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger2);
    }

    @Test
    public void test482() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test482");
        double d1 = org.apache.commons.math.util.MathUtils.sign((double) 78L);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test483() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test483");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array7 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i8 = org.apache.commons.math.util.MathUtils.distanceInf(i_array0, i_array7);
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array0, 1);
        int[] i_array11 = new int[] {};
        int[] i_array12 = org.apache.commons.math.util.MathUtils.copyOf(i_array11);
        try {
            double d13 = org.apache.commons.math.util.MathUtils.distance(i_array10, i_array11);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertTrue(i8 == 0);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertNotNull(i_array11);
        org.junit.Assert.assertNotNull(i_array12);
    }

    @Test
    public void test484() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test484");
        org.apache.commons.math.util.MathUtils.checkFinite((double) '4');
    }

    @Test
    public void test485() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test485");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = complex0.add(complex9);
        double d11 = complex0.getArgument();
        org.apache.commons.math.complex.Complex complex12 = complex0.exp();
        org.apache.commons.math.complex.Complex complex13 = complex0.negate();
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = complex0.subtract(complex14);
        org.apache.commons.math.complex.Complex complex17 = complex16.cosh();
        org.apache.commons.math.complex.Complex complex18 = complex16.tanh();
        org.apache.commons.math.complex.Complex complex19 = complex18.log();
        org.apache.commons.math.complex.Complex complex20 = complex19.cos();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertEquals((double) d11, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
    }

    @Test
    public void test486() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test486");
        long long1 = org.apache.commons.math.util.MathUtils.sign(10L);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test487() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test487");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog(1074790400, (int) (short) 100);
        org.junit.Assert.assertTrue(d2 == 1715.7997701160305d);
    }

    @Test
    public void test488() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test488");
        org.apache.commons.math.util.MathUtils.checkFinite(0.0d);
    }

    @Test
    public void test489() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test489");
        float[] f_array1 = new float[] { 78L };
        float[] f_array2 = null;
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array1, f_array2);
        float[] f_array9 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array11 = new float[] { (-1074790400) };
        boolean b12 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array9, f_array11);
        float[] f_array18 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array20 = new float[] { (-1074790400) };
        boolean b21 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array18, f_array20);
        boolean b22 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array11, f_array18);
        boolean b23 = org.apache.commons.math.util.MathUtils.equals(f_array1, f_array18);
        float[] f_array29 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array31 = new float[] { (-1074790400) };
        boolean b32 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array29, f_array31);
        float[] f_array38 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array40 = new float[] { (-1074790400) };
        boolean b41 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array38, f_array40);
        float[] f_array47 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array49 = new float[] { (-1074790400) };
        boolean b50 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array47, f_array49);
        boolean b51 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array40, f_array47);
        float[] f_array57 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array59 = new float[] { (-1074790400) };
        boolean b60 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array57, f_array59);
        float[] f_array66 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array68 = new float[] { (-1074790400) };
        boolean b69 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array66, f_array68);
        boolean b70 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array59, f_array66);
        boolean b71 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array40, f_array66);
        boolean b72 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array31, f_array66);
        boolean b73 = org.apache.commons.math.util.MathUtils.equals(f_array1, f_array31);
        org.junit.Assert.assertNotNull(f_array1);
        org.junit.Assert.assertTrue(b3 == false);
        org.junit.Assert.assertNotNull(f_array9);
        org.junit.Assert.assertNotNull(f_array11);
        org.junit.Assert.assertTrue(b12 == false);
        org.junit.Assert.assertNotNull(f_array18);
        org.junit.Assert.assertNotNull(f_array20);
        org.junit.Assert.assertTrue(b21 == false);
        org.junit.Assert.assertTrue(b22 == false);
        org.junit.Assert.assertTrue(b23 == false);
        org.junit.Assert.assertNotNull(f_array29);
        org.junit.Assert.assertNotNull(f_array31);
        org.junit.Assert.assertTrue(b32 == false);
        org.junit.Assert.assertNotNull(f_array38);
        org.junit.Assert.assertNotNull(f_array40);
        org.junit.Assert.assertTrue(b41 == false);
        org.junit.Assert.assertNotNull(f_array47);
        org.junit.Assert.assertNotNull(f_array49);
        org.junit.Assert.assertTrue(b50 == false);
        org.junit.Assert.assertTrue(b51 == false);
        org.junit.Assert.assertNotNull(f_array57);
        org.junit.Assert.assertNotNull(f_array59);
        org.junit.Assert.assertTrue(b60 == false);
        org.junit.Assert.assertNotNull(f_array66);
        org.junit.Assert.assertNotNull(f_array68);
        org.junit.Assert.assertTrue(b69 == false);
        org.junit.Assert.assertTrue(b70 == false);
        org.junit.Assert.assertTrue(b71 == false);
        org.junit.Assert.assertTrue(b72 == false);
        org.junit.Assert.assertTrue(b73 == false);
    }

    @Test
    public void test490() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test490");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) (byte) 10, (long) 1636086465);
        org.junit.Assert.assertTrue(long2 == (-1636086455L));
    }

    @Test
    public void test491() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test491");
        byte byte1 = org.apache.commons.math.util.MathUtils.sign((byte) 10);
        org.junit.Assert.assertTrue(byte1 == (byte) 1);
    }

    @Test
    public void test492() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test492");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array1);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, 1.1752011936438014d);
        double[] d_array7 = org.apache.commons.math.util.MathUtils.copyOf(d_array1);
        double[] d_array8 = org.apache.commons.math.util.MathUtils.copyOf(d_array7);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection9 = null;
        double[][] d_array_array10 = null;
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array8, orderDirection9, d_array_array10);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 1.0d);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array8);
    }

    @Test
    public void test493() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test493");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex0.abs();
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex0.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex0.cos();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 0.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
    }

    @Test
    public void test494() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test494");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck(78L, 0L);
        org.junit.Assert.assertTrue(long2 == 78L);
    }

    @Test
    public void test495() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test495");
        int i1 = org.apache.commons.math.util.MathUtils.sign(42);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test496() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test496");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) 26.0d, (java.lang.Number) 10000000000L, false);
    }

    @Test
    public void test497() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test497");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 6336994428L, (float) (short) -1);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test498() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test498");
        int i1 = org.apache.commons.math.util.MathUtils.indicator((int) (byte) 0);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test499() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test499");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        org.apache.commons.math.exception.util.Localizable localizable3 = null;
        float[] f_array9 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array11 = new float[] { (-1074790400) };
        boolean b12 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array9, f_array11);
        java.lang.Object[] obj_array18 = new java.lang.Object[] { b12, 0, 0, (-1.0f), 100.0f, (short) 1 };
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException19 = new org.apache.commons.math.exception.MathArithmeticException(localizable3, obj_array18);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException20 = new org.apache.commons.math.exception.MathArithmeticException(localizable2, obj_array18);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException21 = new org.apache.commons.math.exception.MathArithmeticException(localizable1, obj_array18);
        org.apache.commons.math.exception.MathIllegalArgumentException mathIllegalArgumentException22 = new org.apache.commons.math.exception.MathIllegalArgumentException(localizable0, obj_array18);
        org.junit.Assert.assertNotNull(f_array9);
        org.junit.Assert.assertNotNull(f_array11);
        org.junit.Assert.assertTrue(b12 == false);
        org.junit.Assert.assertNotNull(obj_array18);
    }

    @Test
    public void test500() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test500");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array7 = new double[] { (byte) -1 };
        double[] d_array9 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array7, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException13 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection14 = nonMonotonousSequenceException13.getDirection();
        int i15 = nonMonotonousSequenceException13.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable16 = null;
        java.lang.Object[] obj_array18 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException19 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable16, (java.lang.Number) (short) 100, obj_array18);
        java.lang.Number number20 = notFiniteNumberException19.getArgument();
        nonMonotonousSequenceException13.addSuppressed((java.lang.Throwable) notFiniteNumberException19);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection22 = nonMonotonousSequenceException13.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array9, orderDirection22, false);
        org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection22, true);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array3);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertTrue("'" + orderDirection14 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection14.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i15 == 97);
        org.junit.Assert.assertTrue("'" + number20 + "' != '" + (short) 100 + "'", number20.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection22 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection22.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }
}

