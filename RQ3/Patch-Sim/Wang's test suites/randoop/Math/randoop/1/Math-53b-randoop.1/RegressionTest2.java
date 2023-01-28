import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest2 {

    public static boolean debug = false;

    @Test
    public void test001() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test001");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = new double[] { (-1.0f) };
        double[] d_array7 = org.apache.commons.math.util.MathUtils.copyOf(d_array5, 1);
        double d8 = org.apache.commons.math.util.MathUtils.safeNorm(d_array5);
        double[] d_array10 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array5, 1.1752011936438014d);
        double d11 = org.apache.commons.math.util.MathUtils.distance1(d_array3, d_array5);
        double[] d_array12 = org.apache.commons.math.util.MathUtils.copyOf(d_array5);
        int i13 = org.apache.commons.math.util.MathUtils.hash(d_array12);
        double[] d_array15 = org.apache.commons.math.util.MathUtils.copyOf(d_array12, 0);
        double d16 = org.apache.commons.math.util.MathUtils.safeNorm(d_array12);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertTrue(d8 == 1.0d);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertTrue(d11 == 0.0d);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertTrue(i13 == (-1074790369));
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertTrue(d16 == 1.0d);
    }

    @Test
    public void test002() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test002");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array7 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i8 = org.apache.commons.math.util.MathUtils.distanceInf(i_array0, i_array7);
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array0, 1);
        int[] i_array11 = new int[] {};
        int[] i_array12 = org.apache.commons.math.util.MathUtils.copyOf(i_array11);
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array11);
        int[] i_array14 = new int[] {};
        int[] i_array15 = org.apache.commons.math.util.MathUtils.copyOf(i_array14);
        double d16 = org.apache.commons.math.util.MathUtils.distance(i_array13, i_array15);
        int[] i_array23 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array25 = org.apache.commons.math.util.MathUtils.copyOf(i_array23, (int) '4');
        int i26 = org.apache.commons.math.util.MathUtils.distanceInf(i_array15, i_array23);
        int i27 = org.apache.commons.math.util.MathUtils.distance1(i_array10, i_array23);
        int[] i_array28 = new int[] {};
        int[] i_array29 = org.apache.commons.math.util.MathUtils.copyOf(i_array28);
        int[] i_array30 = org.apache.commons.math.util.MathUtils.copyOf(i_array28);
        int[] i_array31 = new int[] {};
        int[] i_array32 = org.apache.commons.math.util.MathUtils.copyOf(i_array31);
        int[] i_array38 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i39 = org.apache.commons.math.util.MathUtils.distanceInf(i_array31, i_array38);
        int[] i_array41 = org.apache.commons.math.util.MathUtils.copyOf(i_array31, 1);
        int i42 = org.apache.commons.math.util.MathUtils.distance1(i_array28, i_array31);
        int[] i_array43 = new int[] {};
        int[] i_array44 = org.apache.commons.math.util.MathUtils.copyOf(i_array43);
        int[] i_array50 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i51 = org.apache.commons.math.util.MathUtils.distanceInf(i_array43, i_array50);
        int[] i_array53 = org.apache.commons.math.util.MathUtils.copyOf(i_array43, 1);
        int i54 = org.apache.commons.math.util.MathUtils.distanceInf(i_array31, i_array53);
        int[] i_array55 = org.apache.commons.math.util.MathUtils.copyOf(i_array31);
        try {
            double d56 = org.apache.commons.math.util.MathUtils.distance(i_array23, i_array31);
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
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertNotNull(i_array14);
        org.junit.Assert.assertNotNull(i_array15);
        org.junit.Assert.assertTrue(d16 == 0.0d);
        org.junit.Assert.assertNotNull(i_array23);
        org.junit.Assert.assertNotNull(i_array25);
        org.junit.Assert.assertTrue(i26 == 0);
        org.junit.Assert.assertTrue(i27 == 1);
        org.junit.Assert.assertNotNull(i_array28);
        org.junit.Assert.assertNotNull(i_array29);
        org.junit.Assert.assertNotNull(i_array30);
        org.junit.Assert.assertNotNull(i_array31);
        org.junit.Assert.assertNotNull(i_array32);
        org.junit.Assert.assertNotNull(i_array38);
        org.junit.Assert.assertTrue(i39 == 0);
        org.junit.Assert.assertNotNull(i_array41);
        org.junit.Assert.assertTrue(i42 == 0);
        org.junit.Assert.assertNotNull(i_array43);
        org.junit.Assert.assertNotNull(i_array44);
        org.junit.Assert.assertNotNull(i_array50);
        org.junit.Assert.assertTrue(i51 == 0);
        org.junit.Assert.assertNotNull(i_array53);
        org.junit.Assert.assertTrue(i54 == 0);
        org.junit.Assert.assertNotNull(i_array55);
    }

    @Test
    public void test003() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test003");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array20 = new double[] { (byte) -1 };
        double[] d_array22 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array20, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException26 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection27 = nonMonotonousSequenceException26.getDirection();
        int i28 = nonMonotonousSequenceException26.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable29 = null;
        java.lang.Object[] obj_array31 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException32 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable29, (java.lang.Number) (short) 100, obj_array31);
        java.lang.Number number33 = notFiniteNumberException32.getArgument();
        nonMonotonousSequenceException26.addSuppressed((java.lang.Throwable) notFiniteNumberException32);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection35 = nonMonotonousSequenceException26.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array22, orderDirection35, false);
        double d38 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array22);
        double[] d_array40 = new double[] { (byte) -1 };
        double[] d_array42 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array40, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException46 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i47 = nonMonotonousSequenceException46.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection48 = nonMonotonousSequenceException46.getDirection();
        boolean b51 = org.apache.commons.math.util.MathUtils.checkOrder(d_array40, orderDirection48, true, false);
        double d52 = org.apache.commons.math.util.MathUtils.distance(d_array22, d_array40);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection56 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException58 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) Double.POSITIVE_INFINITY, (java.lang.Number) 1.0f, 0, orderDirection56, true);
        org.apache.commons.math.util.MathUtils.checkOrder(d_array40, orderDirection56, true);
        double[] d_array62 = org.apache.commons.math.util.MathUtils.copyOf(d_array40, 0);
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertTrue("'" + orderDirection27 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection27.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i28 == 97);
        org.junit.Assert.assertTrue("'" + number33 + "' != '" + (short) 100 + "'", number33.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection35 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection35.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d38 == 0.0d);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertTrue(i47 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection48 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection48.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b51 == true);
        org.junit.Assert.assertTrue(d52 == 2.0d);
        org.junit.Assert.assertTrue("'" + orderDirection56 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection56.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertNotNull(d_array62);
    }

    @Test
    public void test004() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test004");
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
        org.apache.commons.math.complex.Complex complex27 = complex26.asin();
        org.apache.commons.math.complex.Complex complex28 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex29 = complex28.cos();
        org.apache.commons.math.complex.Complex complex30 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex31 = complex30.cos();
        org.apache.commons.math.complex.Complex complex32 = complex28.subtract(complex30);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex34 = complex30.nthRoot((int) (short) 10);
        double d35 = complex30.abs();
        org.apache.commons.math.complex.Complex complex36 = complex30.atan();
        org.apache.commons.math.complex.Complex complex37 = complex30.exp();
        org.apache.commons.math.complex.Complex complex38 = complex26.multiply(complex37);
        org.apache.commons.math.complex.Complex complex39 = complex37.negate();
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
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(list_complex34);
        org.junit.Assert.assertEquals((double) d35, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex36);
        org.junit.Assert.assertNotNull(complex37);
        org.junit.Assert.assertNotNull(complex38);
        org.junit.Assert.assertNotNull(complex39);
    }

    @Test
    public void test005() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test005");
        java.lang.Number number0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException(number0, (java.lang.Number) (-10L), false);
    }

    @Test
    public void test006() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test006");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.sqrt1z();
        try {
            java.util.List<org.apache.commons.math.complex.Complex> list_complex3 = complex0.nthRoot((-2112331476));
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
    public void test007() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test007");
        try {
            double d3 = org.apache.commons.math.util.MathUtils.round((double) (-8L), (int) (short) 10, (-447362041));
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @Test
    public void test008() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test008");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 1072693248, 0.0d);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test009() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test009");
        float f3 = org.apache.commons.math.util.MathUtils.round(0.0f, 9, 0);
        org.junit.Assert.assertTrue(f3 == 1.0E-9f);
    }

    @Test
    public void test010() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test010");
        double d1 = org.apache.commons.math.util.MathUtils.sign(2.0d);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test011() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test011");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = new double[] { (-1.0f) };
        double[] d_array7 = org.apache.commons.math.util.MathUtils.copyOf(d_array5, 1);
        double d8 = org.apache.commons.math.util.MathUtils.safeNorm(d_array5);
        double[] d_array10 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array5, 1.1752011936438014d);
        double d11 = org.apache.commons.math.util.MathUtils.distance1(d_array3, d_array5);
        double[] d_array12 = org.apache.commons.math.util.MathUtils.copyOf(d_array5);
        int i13 = org.apache.commons.math.util.MathUtils.hash(d_array12);
        int i14 = org.apache.commons.math.util.MathUtils.hash(d_array12);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertTrue(d8 == 1.0d);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertTrue(d11 == 0.0d);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertTrue(i13 == (-1074790369));
        org.junit.Assert.assertTrue(i14 == (-1074790369));
    }

    @Test
    public void test012() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test012");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.acos();
        org.apache.commons.math.complex.Complex complex2 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex4 = complex3.cos();
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = complex3.subtract(complex5);
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = complex8.cos();
        org.apache.commons.math.complex.Complex complex10 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex11 = complex10.cos();
        org.apache.commons.math.complex.Complex complex12 = complex8.subtract(complex10);
        org.apache.commons.math.complex.Complex complex13 = complex3.add(complex12);
        double d14 = complex3.getArgument();
        org.apache.commons.math.complex.Complex complex15 = complex3.exp();
        org.apache.commons.math.complex.Complex complex16 = complex3.negate();
        org.apache.commons.math.complex.Complex complex17 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex18 = complex17.cos();
        org.apache.commons.math.complex.Complex complex19 = complex3.subtract(complex17);
        org.apache.commons.math.complex.Complex complex20 = complex19.cosh();
        org.apache.commons.math.complex.Complex complex21 = complex2.multiply(complex19);
        double d22 = complex21.getArgument();
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
        org.junit.Assert.assertEquals((double) d14, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertEquals((double) d22, Double.NaN, 0);
    }

    @Test
    public void test013() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test013");
        java.lang.Number number1 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 0.0f, number1, (-1074790400));
    }

    @Test
    public void test014() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test014");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 19683, (float) 3187121676L, (float) 3L);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test015() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test015");
        double d3 = org.apache.commons.math.util.MathUtils.reduce(99.70309990644455d, (double) 3628800L, 0.0d);
        org.junit.Assert.assertTrue(d3 == 99.70309990644455d);
    }

    @Test
    public void test016() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test016");
        int[] i_array0 = null;
        try {
            int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test017() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test017");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array20 = new double[] { (byte) -1 };
        double[] d_array22 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array20, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException26 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection27 = nonMonotonousSequenceException26.getDirection();
        int i28 = nonMonotonousSequenceException26.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable29 = null;
        java.lang.Object[] obj_array31 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException32 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable29, (java.lang.Number) (short) 100, obj_array31);
        java.lang.Number number33 = notFiniteNumberException32.getArgument();
        nonMonotonousSequenceException26.addSuppressed((java.lang.Throwable) notFiniteNumberException32);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection35 = nonMonotonousSequenceException26.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array22, orderDirection35, false);
        double d38 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array22);
        double d39 = org.apache.commons.math.util.MathUtils.safeNorm(d_array22);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException43 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i44 = nonMonotonousSequenceException43.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection45 = nonMonotonousSequenceException43.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array22, orderDirection45, false);
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertTrue("'" + orderDirection27 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection27.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i28 == 97);
        org.junit.Assert.assertTrue("'" + number33 + "' != '" + (short) 100 + "'", number33.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection35 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection35.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d38 == 0.0d);
        org.junit.Assert.assertTrue(d39 == 1.0d);
        org.junit.Assert.assertTrue(i44 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection45 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection45.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test018() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test018");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck((int) 'a', (int) '4');
        org.junit.Assert.assertTrue(i2 == 149);
    }

    @Test
    public void test019() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test019");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array10 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i11 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array10);
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 1);
        int i14 = org.apache.commons.math.util.MathUtils.distance1(i_array0, i_array3);
        int[] i_array15 = new int[] {};
        int[] i_array16 = org.apache.commons.math.util.MathUtils.copyOf(i_array15);
        int[] i_array22 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i23 = org.apache.commons.math.util.MathUtils.distanceInf(i_array15, i_array22);
        int[] i_array25 = org.apache.commons.math.util.MathUtils.copyOf(i_array15, 1);
        int i26 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array25);
        int[] i_array33 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array35 = org.apache.commons.math.util.MathUtils.copyOf(i_array33, (int) '4');
        double d36 = org.apache.commons.math.util.MathUtils.distance(i_array3, i_array33);
        int[] i_array37 = new int[] {};
        int[] i_array38 = org.apache.commons.math.util.MathUtils.copyOf(i_array37);
        int[] i_array44 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i45 = org.apache.commons.math.util.MathUtils.distanceInf(i_array37, i_array44);
        int i46 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array44);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertTrue(i11 == 0);
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertTrue(i14 == 0);
        org.junit.Assert.assertNotNull(i_array15);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertNotNull(i_array22);
        org.junit.Assert.assertTrue(i23 == 0);
        org.junit.Assert.assertNotNull(i_array25);
        org.junit.Assert.assertTrue(i26 == 0);
        org.junit.Assert.assertNotNull(i_array33);
        org.junit.Assert.assertNotNull(i_array35);
        org.junit.Assert.assertTrue(d36 == 0.0d);
        org.junit.Assert.assertNotNull(i_array37);
        org.junit.Assert.assertNotNull(i_array38);
        org.junit.Assert.assertNotNull(i_array44);
        org.junit.Assert.assertTrue(i45 == 0);
        org.junit.Assert.assertTrue(i46 == 0);
    }

    @Test
    public void test020() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test020");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex4 = complex3.cos();
        org.apache.commons.math.exception.util.Localizable localizable5 = null;
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        float[] f_array12 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array14 = new float[] { (-1074790400) };
        boolean b15 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array12, f_array14);
        java.lang.Object[] obj_array21 = new java.lang.Object[] { b15, 0, 0, (-1.0f), 100.0f, (short) 1 };
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException22 = new org.apache.commons.math.exception.MathArithmeticException(localizable6, obj_array21);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex4, localizable5, obj_array21);
        org.apache.commons.math.exception.MathIllegalArgumentException mathIllegalArgumentException24 = new org.apache.commons.math.exception.MathIllegalArgumentException(localizable2, obj_array21);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException25 = new org.apache.commons.math.exception.NotFiniteNumberException((java.lang.Number) 1075970048, obj_array21);
        org.apache.commons.math.exception.MathIllegalArgumentException mathIllegalArgumentException26 = new org.apache.commons.math.exception.MathIllegalArgumentException(localizable0, obj_array21);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(f_array12);
        org.junit.Assert.assertNotNull(f_array14);
        org.junit.Assert.assertTrue(b15 == false);
        org.junit.Assert.assertNotNull(obj_array21);
    }

    @Test
    public void test021() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test021");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(0.0d, 2764.17927763715d);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test022() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test022");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        org.apache.commons.math.complex.Complex complex5 = complex3.cosh();
        org.apache.commons.math.complex.Complex complex6 = complex3.cosh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
    }

    @Test
    public void test023() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test023");
        long long1 = org.apache.commons.math.util.MathUtils.indicator(35L);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test024() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test024");
        double d3 = org.apache.commons.math.util.MathUtils.round((double) '#', 10, 0);
        org.junit.Assert.assertTrue(d3 == 35.0d);
    }

    @Test
    public void test025() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test025");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 3.6288E8f, (java.lang.Number) 1.0d, false);
    }

    @Test
    public void test026() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test026");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo(0.0d, (double) Float.NaN, 100);
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test027() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test027");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Number number2 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) 362880L, number2, false);
    }

    @Test
    public void test028() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test028");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 100L, (java.lang.Number) 2764.17927763715d, true);
    }

    @Test
    public void test029() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test029");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) (byte) 100);
        java.lang.Number number3 = notPositiveException2.getArgument();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException7 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection8 = nonMonotonousSequenceException7.getDirection();
        int i9 = nonMonotonousSequenceException7.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable10 = null;
        java.lang.Object[] obj_array12 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException13 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable10, (java.lang.Number) (short) 100, obj_array12);
        java.lang.Number number14 = notFiniteNumberException13.getArgument();
        nonMonotonousSequenceException7.addSuppressed((java.lang.Throwable) notFiniteNumberException13);
        notPositiveException2.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException7);
        int i17 = nonMonotonousSequenceException7.getIndex();
        int i18 = nonMonotonousSequenceException7.getIndex();
        org.junit.Assert.assertTrue("'" + number3 + "' != '" + (byte) 100 + "'", number3.equals((byte) 100));
        org.junit.Assert.assertTrue("'" + orderDirection8 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection8.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i9 == 97);
        org.junit.Assert.assertTrue("'" + number14 + "' != '" + (short) 100 + "'", number14.equals((short) 100));
        org.junit.Assert.assertTrue(i17 == 97);
        org.junit.Assert.assertTrue(i18 == 97);
    }

    @Test
    public void test030() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test030");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) (-8), (java.lang.Number) 3L, true);
    }

    @Test
    public void test031() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test031");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) (byte) 100);
        java.lang.Number number3 = notPositiveException2.getArgument();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException7 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection8 = nonMonotonousSequenceException7.getDirection();
        int i9 = nonMonotonousSequenceException7.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable10 = null;
        java.lang.Object[] obj_array12 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException13 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable10, (java.lang.Number) (short) 100, obj_array12);
        java.lang.Number number14 = notFiniteNumberException13.getArgument();
        nonMonotonousSequenceException7.addSuppressed((java.lang.Throwable) notFiniteNumberException13);
        notPositiveException2.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException7);
        int i17 = nonMonotonousSequenceException7.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection18 = nonMonotonousSequenceException7.getDirection();
        java.lang.Number number19 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException22 = new org.apache.commons.math.exception.NumberIsTooLargeException(number19, (java.lang.Number) 1.0066581708938198d, false);
        nonMonotonousSequenceException7.addSuppressed((java.lang.Throwable) numberIsTooLargeException22);
        org.junit.Assert.assertTrue("'" + number3 + "' != '" + (byte) 100 + "'", number3.equals((byte) 100));
        org.junit.Assert.assertTrue("'" + orderDirection8 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection8.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i9 == 97);
        org.junit.Assert.assertTrue("'" + number14 + "' != '" + (short) 100 + "'", number14.equals((short) 100));
        org.junit.Assert.assertTrue(i17 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection18 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection18.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test032() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test032");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(0, (int) (short) 100);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test033() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test033");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (-1L));
    }

    @Test
    public void test034() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test034");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 1L, 98);
        org.junit.Assert.assertTrue(d2 == 1.0d);
    }

    @Test
    public void test035() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test035");
        double[] d_array0 = null;
        java.lang.Number number1 = null;
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException6 = new org.apache.commons.math.exception.NonMonotonousSequenceException(number1, (java.lang.Number) (-2064878497), 1636086465, orderDirection4, false);
        try {
            boolean b9 = org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection4, true, true);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test036() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test036");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) 42, (java.lang.Number) (-1.0d), false);
        java.lang.Number number5 = numberIsTooLargeException4.getMax();
        java.lang.Number number6 = numberIsTooLargeException4.getArgument();
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + (-1.0d) + "'", number5.equals((-1.0d)));
        org.junit.Assert.assertTrue("'" + number6 + "' != '" + 42 + "'", number6.equals(42));
    }

    @Test
    public void test037() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test037");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (int) (byte) 100, (-8));
    }

    @Test
    public void test038() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test038");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        java.lang.Object[] obj_array8 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException9 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable6, (java.lang.Number) (short) 100, obj_array8);
        java.lang.Number number10 = notFiniteNumberException9.getArgument();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException9);
        boolean b12 = nonMonotonousSequenceException3.getStrict();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection13 = nonMonotonousSequenceException3.getDirection();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number10 + "' != '" + (short) 100 + "'", number10.equals((short) 100));
        org.junit.Assert.assertTrue(b12 == true);
        org.junit.Assert.assertTrue("'" + orderDirection13 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection13.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test039() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test039");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.acos();
        org.apache.commons.math.complex.Complex complex4 = complex3.conjugate();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
    }

    @Test
    public void test040() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test040");
        int i1 = org.apache.commons.math.util.MathUtils.sign((-1067941888));
        org.junit.Assert.assertTrue(i1 == (-1));
    }

    @Test
    public void test041() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test041");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array7 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i8 = org.apache.commons.math.util.MathUtils.distanceInf(i_array0, i_array7);
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array0, 1);
        int[] i_array11 = new int[] {};
        int[] i_array12 = org.apache.commons.math.util.MathUtils.copyOf(i_array11);
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array11);
        int[] i_array14 = new int[] {};
        int[] i_array15 = org.apache.commons.math.util.MathUtils.copyOf(i_array14);
        double d16 = org.apache.commons.math.util.MathUtils.distance(i_array13, i_array15);
        int[] i_array23 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array25 = org.apache.commons.math.util.MathUtils.copyOf(i_array23, (int) '4');
        int i26 = org.apache.commons.math.util.MathUtils.distanceInf(i_array15, i_array23);
        int i27 = org.apache.commons.math.util.MathUtils.distance1(i_array10, i_array23);
        int[] i_array28 = org.apache.commons.math.util.MathUtils.copyOf(i_array10);
        int[] i_array29 = new int[] {};
        int[] i_array30 = org.apache.commons.math.util.MathUtils.copyOf(i_array29);
        int[] i_array31 = org.apache.commons.math.util.MathUtils.copyOf(i_array29);
        int[] i_array32 = new int[] {};
        int[] i_array33 = org.apache.commons.math.util.MathUtils.copyOf(i_array32);
        int[] i_array39 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i40 = org.apache.commons.math.util.MathUtils.distanceInf(i_array32, i_array39);
        int[] i_array42 = org.apache.commons.math.util.MathUtils.copyOf(i_array32, 1);
        int i43 = org.apache.commons.math.util.MathUtils.distance1(i_array29, i_array32);
        int[] i_array44 = new int[] {};
        int[] i_array45 = org.apache.commons.math.util.MathUtils.copyOf(i_array44);
        int[] i_array51 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i52 = org.apache.commons.math.util.MathUtils.distanceInf(i_array44, i_array51);
        int[] i_array54 = org.apache.commons.math.util.MathUtils.copyOf(i_array44, 1);
        int i55 = org.apache.commons.math.util.MathUtils.distanceInf(i_array32, i_array54);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) i_array32);
        int[] i_array57 = new int[] {};
        int[] i_array58 = org.apache.commons.math.util.MathUtils.copyOf(i_array57);
        int[] i_array59 = org.apache.commons.math.util.MathUtils.copyOf(i_array57);
        int[] i_array60 = new int[] {};
        int[] i_array61 = org.apache.commons.math.util.MathUtils.copyOf(i_array60);
        int[] i_array62 = org.apache.commons.math.util.MathUtils.copyOf(i_array60);
        int i63 = org.apache.commons.math.util.MathUtils.distanceInf(i_array57, i_array60);
        int[] i_array69 = new int[] { (-447362047), (-1074790369), (byte) 1, (short) 1, (byte) -1 };
        double d70 = org.apache.commons.math.util.MathUtils.distance(i_array57, i_array69);
        double d71 = org.apache.commons.math.util.MathUtils.distance(i_array32, i_array69);
        try {
            int i72 = org.apache.commons.math.util.MathUtils.distanceInf(i_array10, i_array32);
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
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertNotNull(i_array14);
        org.junit.Assert.assertNotNull(i_array15);
        org.junit.Assert.assertTrue(d16 == 0.0d);
        org.junit.Assert.assertNotNull(i_array23);
        org.junit.Assert.assertNotNull(i_array25);
        org.junit.Assert.assertTrue(i26 == 0);
        org.junit.Assert.assertTrue(i27 == 1);
        org.junit.Assert.assertNotNull(i_array28);
        org.junit.Assert.assertNotNull(i_array29);
        org.junit.Assert.assertNotNull(i_array30);
        org.junit.Assert.assertNotNull(i_array31);
        org.junit.Assert.assertNotNull(i_array32);
        org.junit.Assert.assertNotNull(i_array33);
        org.junit.Assert.assertNotNull(i_array39);
        org.junit.Assert.assertTrue(i40 == 0);
        org.junit.Assert.assertNotNull(i_array42);
        org.junit.Assert.assertTrue(i43 == 0);
        org.junit.Assert.assertNotNull(i_array44);
        org.junit.Assert.assertNotNull(i_array45);
        org.junit.Assert.assertNotNull(i_array51);
        org.junit.Assert.assertTrue(i52 == 0);
        org.junit.Assert.assertNotNull(i_array54);
        org.junit.Assert.assertTrue(i55 == 0);
        org.junit.Assert.assertNotNull(i_array57);
        org.junit.Assert.assertNotNull(i_array58);
        org.junit.Assert.assertNotNull(i_array59);
        org.junit.Assert.assertNotNull(i_array60);
        org.junit.Assert.assertNotNull(i_array61);
        org.junit.Assert.assertNotNull(i_array62);
        org.junit.Assert.assertTrue(i63 == 0);
        org.junit.Assert.assertNotNull(i_array69);
        org.junit.Assert.assertTrue(d70 == 0.0d);
        org.junit.Assert.assertTrue(d71 == 0.0d);
    }

    @Test
    public void test042() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test042");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (int) (short) 1, (int) (short) 1);
        org.apache.commons.math.exception.util.Localizable localizable4 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException6 = new org.apache.commons.math.exception.NotPositiveException(localizable4, (java.lang.Number) 100.0d);
        java.lang.Number number7 = notPositiveException6.getMin();
        dimensionMismatchException3.addSuppressed((java.lang.Throwable) notPositiveException6);
        org.junit.Assert.assertTrue("'" + number7 + "' != '" + 0 + "'", number7.equals(0));
    }

    @Test
    public void test043() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test043");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.tanh();
        org.apache.commons.math.complex.Complex complex4 = new org.apache.commons.math.complex.Complex((double) 0.0f, 0.0d);
        org.apache.commons.math.complex.Complex complex5 = complex0.subtract(complex4);
        org.apache.commons.math.complex.Complex complex7 = complex4.multiply((double) (-447362047));
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException10 = new org.apache.commons.math.exception.DimensionMismatchException(0, 9);
        java.lang.String str11 = dimensionMismatchException10.toString();
        boolean b12 = complex4.equals((java.lang.Object) dimensionMismatchException10);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertTrue("'" + str11 + "' != '" + "org.apache.commons.math.exception.DimensionMismatchException: 0 != 9" + "'", str11.equals("org.apache.commons.math.exception.DimensionMismatchException: 0 != 9"));
        org.junit.Assert.assertTrue(b12 == false);
    }

    @Test
    public void test044() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test044");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (-447362047), 0.0f, (float) 362880L);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test045() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test045");
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection3 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException5 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 92561040L, (java.lang.Number) 7766279631452241920L, 0, orderDirection3, false);
        org.junit.Assert.assertTrue("'" + orderDirection3 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection3.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test046() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test046");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        java.lang.Object[] obj_array4 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException5 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable2, (java.lang.Number) 100.0d, obj_array4);
        java.lang.Throwable[] throwable_array6 = notFiniteNumberException5.getSuppressed();
        org.apache.commons.math.exception.MathIllegalArgumentException mathIllegalArgumentException7 = new org.apache.commons.math.exception.MathIllegalArgumentException(localizable1, (java.lang.Object[]) throwable_array6);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException8 = new org.apache.commons.math.exception.NullArgumentException(localizable0, (java.lang.Object[]) throwable_array6);
        org.junit.Assert.assertNotNull(obj_array4);
        org.junit.Assert.assertNotNull(throwable_array6);
    }

    @Test
    public void test047() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test047");
        double d1 = org.apache.commons.math.util.MathUtils.factorialDouble((int) 'a');
        org.junit.Assert.assertTrue(d1 == 9.619275968248924E151d);
    }

    @Test
    public void test048() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test048");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) (-1067941888));
        org.junit.Assert.assertTrue(d1 == (-1.0d));
    }

    @Test
    public void test049() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test049");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.exception.util.Localizable localizable4 = null;
        org.apache.commons.math.exception.util.Localizable localizable5 = null;
        java.lang.Number number6 = null;
        org.apache.commons.math.exception.util.Localizable localizable7 = null;
        org.apache.commons.math.exception.util.Localizable localizable8 = null;
        org.apache.commons.math.exception.util.Localizable localizable9 = null;
        float[] f_array15 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array17 = new float[] { (-1074790400) };
        boolean b18 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array15, f_array17);
        java.lang.Object[] obj_array24 = new java.lang.Object[] { b18, 0, 0, (-1.0f), 100.0f, (short) 1 };
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException25 = new org.apache.commons.math.exception.MathArithmeticException(localizable9, obj_array24);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException26 = new org.apache.commons.math.exception.MathArithmeticException(localizable8, obj_array24);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException27 = new org.apache.commons.math.exception.MathArithmeticException(localizable7, obj_array24);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException28 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable5, number6, obj_array24);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex3, localizable4, obj_array24);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(f_array15);
        org.junit.Assert.assertNotNull(f_array17);
        org.junit.Assert.assertTrue(b18 == false);
        org.junit.Assert.assertNotNull(obj_array24);
    }

    @Test
    public void test050() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test050");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 42);
        java.math.BigInteger bigInteger6 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 1);
        java.math.BigInteger bigInteger7 = null;
        java.math.BigInteger bigInteger9 = org.apache.commons.math.util.MathUtils.pow(bigInteger7, (long) 0);
        java.math.BigInteger bigInteger11 = org.apache.commons.math.util.MathUtils.pow(bigInteger9, 42);
        java.math.BigInteger bigInteger13 = org.apache.commons.math.util.MathUtils.pow(bigInteger9, 1);
        java.math.BigInteger bigInteger14 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, bigInteger13);
        java.math.BigInteger bigInteger16 = org.apache.commons.math.util.MathUtils.pow(bigInteger14, (int) (short) 1);
        org.junit.Assert.assertNotNull(bigInteger2);
        org.junit.Assert.assertNotNull(bigInteger4);
        org.junit.Assert.assertNotNull(bigInteger6);
        org.junit.Assert.assertNotNull(bigInteger9);
        org.junit.Assert.assertNotNull(bigInteger11);
        org.junit.Assert.assertNotNull(bigInteger13);
        org.junit.Assert.assertNotNull(bigInteger14);
        org.junit.Assert.assertNotNull(bigInteger16);
    }

    @Test
    public void test051() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test051");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.math.BigInteger bigInteger1 = null;
        java.math.BigInteger bigInteger3 = org.apache.commons.math.util.MathUtils.pow(bigInteger1, (long) 0);
        java.math.BigInteger bigInteger5 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 42);
        java.math.BigInteger bigInteger7 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 1);
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException10 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) bigInteger3, (java.lang.Number) 9.332621544395286E157d, true);
        java.math.BigInteger bigInteger12 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, (long) 'a');
        org.junit.Assert.assertNotNull(bigInteger3);
        org.junit.Assert.assertNotNull(bigInteger5);
        org.junit.Assert.assertNotNull(bigInteger7);
        org.junit.Assert.assertNotNull(bigInteger12);
    }

    @Test
    public void test052() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test052");
        int i2 = org.apache.commons.math.util.MathUtils.lcm(4, 52);
        org.junit.Assert.assertTrue(i2 == 52);
    }

    @Test
    public void test053() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test053");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.math.BigInteger bigInteger1 = null;
        java.math.BigInteger bigInteger3 = org.apache.commons.math.util.MathUtils.pow(bigInteger1, (long) 0);
        java.math.BigInteger bigInteger5 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 42);
        java.math.BigInteger bigInteger7 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 1);
        org.apache.commons.math.exception.util.Localizable localizable8 = null;
        org.apache.commons.math.exception.util.Localizable localizable10 = null;
        org.apache.commons.math.exception.util.Localizable localizable11 = null;
        org.apache.commons.math.exception.util.Localizable localizable12 = null;
        java.lang.Object[] obj_array14 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException15 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable12, (java.lang.Number) 100.0d, obj_array14);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException16 = new org.apache.commons.math.exception.NullArgumentException(localizable11, obj_array14);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException17 = new org.apache.commons.math.exception.MathArithmeticException(localizable10, obj_array14);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException18 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable8, (java.lang.Number) 4950.0d, obj_array14);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException19 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable0, (java.lang.Number) bigInteger3, obj_array14);
        org.junit.Assert.assertNotNull(bigInteger3);
        org.junit.Assert.assertNotNull(bigInteger5);
        org.junit.Assert.assertNotNull(bigInteger7);
        org.junit.Assert.assertNotNull(obj_array14);
    }

    @Test
    public void test054() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test054");
        double d2 = org.apache.commons.math.util.MathUtils.log((double) (short) -1, (double) (-447362041));
        org.junit.Assert.assertEquals((double) d2, Double.NaN, 0);
    }

    @Test
    public void test055() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test055");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        org.apache.commons.math.exception.util.Localizable localizable3 = null;
        float[] f_array9 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array11 = new float[] { (-1074790400) };
        boolean b12 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array9, f_array11);
        java.lang.Object[] obj_array18 = new java.lang.Object[] { b12, 0, 0, (-1.0f), 100.0f, (short) 1 };
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException19 = new org.apache.commons.math.exception.MathArithmeticException(localizable3, obj_array18);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex1, localizable2, obj_array18);
        double d21 = complex1.getReal();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(f_array9);
        org.junit.Assert.assertNotNull(f_array11);
        org.junit.Assert.assertTrue(b12 == false);
        org.junit.Assert.assertNotNull(obj_array18);
        org.junit.Assert.assertEquals((double) d21, Double.NaN, 0);
    }

    @Test
    public void test056() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test056");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 57L, (java.lang.Number) Double.NEGATIVE_INFINITY, false);
    }

    @Test
    public void test057() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test057");
        int[] i_array0 = null;
        int[] i_array1 = new int[] {};
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array1);
        int[] i_array3 = org.apache.commons.math.util.MathUtils.copyOf(i_array1);
        int[] i_array4 = new int[] {};
        int[] i_array5 = org.apache.commons.math.util.MathUtils.copyOf(i_array4);
        int[] i_array11 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i12 = org.apache.commons.math.util.MathUtils.distanceInf(i_array4, i_array11);
        int[] i_array14 = org.apache.commons.math.util.MathUtils.copyOf(i_array4, 1);
        int i15 = org.apache.commons.math.util.MathUtils.distance1(i_array1, i_array4);
        int[] i_array16 = new int[] {};
        int[] i_array17 = org.apache.commons.math.util.MathUtils.copyOf(i_array16);
        int[] i_array23 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i24 = org.apache.commons.math.util.MathUtils.distanceInf(i_array16, i_array23);
        int[] i_array26 = org.apache.commons.math.util.MathUtils.copyOf(i_array16, 1);
        int i27 = org.apache.commons.math.util.MathUtils.distanceInf(i_array4, i_array26);
        int[] i_array34 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array36 = org.apache.commons.math.util.MathUtils.copyOf(i_array34, (int) '4');
        double d37 = org.apache.commons.math.util.MathUtils.distance(i_array4, i_array34);
        try {
            double d38 = org.apache.commons.math.util.MathUtils.distance(i_array0, i_array4);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array5);
        org.junit.Assert.assertNotNull(i_array11);
        org.junit.Assert.assertTrue(i12 == 0);
        org.junit.Assert.assertNotNull(i_array14);
        org.junit.Assert.assertTrue(i15 == 0);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertNotNull(i_array17);
        org.junit.Assert.assertNotNull(i_array23);
        org.junit.Assert.assertTrue(i24 == 0);
        org.junit.Assert.assertNotNull(i_array26);
        org.junit.Assert.assertTrue(i27 == 0);
        org.junit.Assert.assertNotNull(i_array34);
        org.junit.Assert.assertNotNull(i_array36);
        org.junit.Assert.assertTrue(d37 == 0.0d);
    }

    @Test
    public void test058() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test058");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck((long) 1100, (-2064879597L));
        org.junit.Assert.assertTrue(long2 == (-2064878497L));
    }

    @Test
    public void test059() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test059");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex1.divide(complex2);
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex5 = complex4.acos();
        org.apache.commons.math.complex.Complex complex6 = complex2.divide(complex4);
        org.apache.commons.math.complex.Complex complex7 = complex2.tan();
        boolean b8 = complex0.equals((java.lang.Object) complex7);
        org.apache.commons.math.complex.Complex complex9 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex10 = complex9.cos();
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = complex11.cos();
        org.apache.commons.math.complex.Complex complex13 = complex9.subtract(complex11);
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex17 = complex16.cos();
        org.apache.commons.math.complex.Complex complex18 = complex14.subtract(complex16);
        org.apache.commons.math.complex.Complex complex19 = complex9.add(complex18);
        double d20 = complex9.getArgument();
        org.apache.commons.math.complex.Complex complex21 = complex9.exp();
        org.apache.commons.math.complex.Complex complex22 = complex9.negate();
        org.apache.commons.math.complex.Complex complex23 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex24 = complex23.cos();
        org.apache.commons.math.complex.Complex complex25 = complex9.subtract(complex23);
        org.apache.commons.math.complex.Complex complex26 = complex25.cosh();
        org.apache.commons.math.complex.Complex complex27 = complex25.tanh();
        org.apache.commons.math.complex.Complex complex28 = complex0.multiply(complex27);
        org.apache.commons.math.complex.Complex complex29 = complex28.sin();
        org.apache.commons.math.complex.Complex complex30 = complex28.sqrt();
        org.apache.commons.math.complex.Complex complex31 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex32 = complex31.cos();
        org.apache.commons.math.complex.Complex complex33 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex34 = complex33.cos();
        org.apache.commons.math.complex.Complex complex35 = complex31.subtract(complex33);
        org.apache.commons.math.complex.Complex complex36 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex37 = complex36.cos();
        org.apache.commons.math.complex.Complex complex38 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex39 = complex38.cos();
        org.apache.commons.math.complex.Complex complex40 = complex36.subtract(complex38);
        org.apache.commons.math.complex.Complex complex41 = complex31.add(complex40);
        double d42 = complex31.getArgument();
        org.apache.commons.math.complex.Complex complex43 = complex31.exp();
        org.apache.commons.math.complex.Complex complex44 = complex31.negate();
        org.apache.commons.math.complex.Complex complex45 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex46 = complex45.cos();
        org.apache.commons.math.complex.Complex complex47 = complex31.subtract(complex45);
        org.apache.commons.math.complex.Complex complex48 = complex31.tan();
        org.apache.commons.math.complex.Complex complex49 = complex48.asin();
        org.apache.commons.math.complex.Complex complex50 = complex30.multiply(complex48);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertTrue(b8 == true);
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
        org.junit.Assert.assertEquals((double) d20, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complex24);
        org.junit.Assert.assertNotNull(complex25);
        org.junit.Assert.assertNotNull(complex26);
        org.junit.Assert.assertNotNull(complex27);
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(complex34);
        org.junit.Assert.assertNotNull(complex35);
        org.junit.Assert.assertNotNull(complex36);
        org.junit.Assert.assertNotNull(complex37);
        org.junit.Assert.assertNotNull(complex38);
        org.junit.Assert.assertNotNull(complex39);
        org.junit.Assert.assertNotNull(complex40);
        org.junit.Assert.assertNotNull(complex41);
        org.junit.Assert.assertEquals((double) d42, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex43);
        org.junit.Assert.assertNotNull(complex44);
        org.junit.Assert.assertNotNull(complex45);
        org.junit.Assert.assertNotNull(complex46);
        org.junit.Assert.assertNotNull(complex47);
        org.junit.Assert.assertNotNull(complex48);
        org.junit.Assert.assertNotNull(complex49);
        org.junit.Assert.assertNotNull(complex50);
    }

    @Test
    public void test060() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test060");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 3L, 1.63608653E9f);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test061() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test061");
        int i1 = org.apache.commons.math.util.MathUtils.hash((double) '4');
        org.junit.Assert.assertTrue(i1 == 1078591488);
    }

    @Test
    public void test062() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test062");
        try {
            long long1 = org.apache.commons.math.util.MathUtils.factorial((-447362047));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test063() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test063");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        int i19 = org.apache.commons.math.util.MathUtils.hash(d_array0);
        int i20 = org.apache.commons.math.util.MathUtils.hash(d_array0);
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertTrue(i19 == 1);
        org.junit.Assert.assertTrue(i20 == 1);
    }

    @Test
    public void test064() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test064");
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
        org.apache.commons.math.complex.Complex complex13 = complex9.conjugate();
        org.apache.commons.math.complex.Complex complex14 = complex13.tanh();
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
    }

    @Test
    public void test065() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test065");
        float f3 = org.apache.commons.math.util.MathUtils.round((float) 36288000L, (-8), 2);
        org.junit.Assert.assertTrue(f3 == 1.0E8f);
    }

    @Test
    public void test066() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test066");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        double[] d_array3 = new double[] {};
        double[] d_array7 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array11 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array15 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array19 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array20 = new double[][] { d_array7, d_array11, d_array15, d_array19 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array3, d_array_array20);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException22 = new org.apache.commons.math.exception.NullArgumentException(localizable2, (java.lang.Object[]) d_array_array20);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException23 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable0, (java.lang.Number) (byte) 0, (java.lang.Object[]) d_array_array20);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array_array20);
    }

    @Test
    public void test067() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test067");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array20 = org.apache.commons.math.util.MathUtils.copyOf(d_array0, 98);
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array20);
    }

    @Test
    public void test068() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test068");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(0.0f, (float) (short) 0);
        org.junit.Assert.assertTrue(b2 == true);
    }

    @Test
    public void test069() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test069");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 99.35576372122958d, (java.lang.Number) (-1.0434034937813494d), (int) 'a');
    }

    @Test
    public void test070() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test070");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array20 = new double[] { (byte) -1 };
        double[] d_array22 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array20, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException26 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection27 = nonMonotonousSequenceException26.getDirection();
        int i28 = nonMonotonousSequenceException26.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable29 = null;
        java.lang.Object[] obj_array31 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException32 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable29, (java.lang.Number) (short) 100, obj_array31);
        java.lang.Number number33 = notFiniteNumberException32.getArgument();
        nonMonotonousSequenceException26.addSuppressed((java.lang.Throwable) notFiniteNumberException32);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection35 = nonMonotonousSequenceException26.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array22, orderDirection35, false);
        double d38 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array22);
        double[] d_array40 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array22, 197.05131665139353d);
        try {
            double[] d_array42 = org.apache.commons.math.util.MathUtils.copyOf(d_array22, (-738962753));
            org.junit.Assert.fail("Expected exception of type java.lang.NegativeArraySizeException");
        } catch (java.lang.NegativeArraySizeException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertTrue("'" + orderDirection27 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection27.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i28 == 97);
        org.junit.Assert.assertTrue("'" + number33 + "' != '" + (short) 100 + "'", number33.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection35 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection35.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d38 == 0.0d);
        org.junit.Assert.assertNotNull(d_array40);
    }

    @Test
    public void test071() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test071");
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
        double[] d_array43 = new double[] {};
        double[] d_array47 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array51 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array55 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array59 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array60 = new double[][] { d_array47, d_array51, d_array55, d_array59 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array43, d_array_array60);
        int i62 = org.apache.commons.math.util.MathUtils.hash(d_array43);
        double d63 = org.apache.commons.math.util.MathUtils.distance1(d_array24, d_array43);
        double d64 = org.apache.commons.math.util.MathUtils.distance1(d_array5, d_array24);
        double[] d_array68 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d69 = org.apache.commons.math.util.MathUtils.safeNorm(d_array68);
        double[] d_array70 = new double[] {};
        double[] d_array74 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array78 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array82 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array86 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array87 = new double[][] { d_array74, d_array78, d_array82, d_array86 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array70, d_array_array87);
        int i89 = org.apache.commons.math.util.MathUtils.hash(d_array70);
        boolean b90 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array68, d_array70);
        double d91 = org.apache.commons.math.util.MathUtils.distance1(d_array5, d_array70);
        double d92 = org.apache.commons.math.util.MathUtils.safeNorm(d_array5);
        try {
            double d93 = org.apache.commons.math.util.MathUtils.distance(d_array1, d_array5);
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
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array51);
        org.junit.Assert.assertNotNull(d_array55);
        org.junit.Assert.assertNotNull(d_array59);
        org.junit.Assert.assertNotNull(d_array_array60);
        org.junit.Assert.assertTrue(i62 == 1);
        org.junit.Assert.assertTrue(d63 == 0.0d);
        org.junit.Assert.assertTrue(d64 == 0.0d);
        org.junit.Assert.assertNotNull(d_array68);
        org.junit.Assert.assertTrue(d69 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array70);
        org.junit.Assert.assertNotNull(d_array74);
        org.junit.Assert.assertNotNull(d_array78);
        org.junit.Assert.assertNotNull(d_array82);
        org.junit.Assert.assertNotNull(d_array86);
        org.junit.Assert.assertNotNull(d_array_array87);
        org.junit.Assert.assertTrue(i89 == 1);
        org.junit.Assert.assertTrue(b90 == false);
        org.junit.Assert.assertTrue(d91 == 0.0d);
        org.junit.Assert.assertTrue(d92 == 0.0d);
    }

    @Test
    public void test072() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test072");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array10 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i11 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array10);
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 1);
        int i14 = org.apache.commons.math.util.MathUtils.distance1(i_array0, i_array3);
        int[] i_array15 = new int[] {};
        int[] i_array16 = org.apache.commons.math.util.MathUtils.copyOf(i_array15);
        int[] i_array22 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i23 = org.apache.commons.math.util.MathUtils.distanceInf(i_array15, i_array22);
        int[] i_array25 = org.apache.commons.math.util.MathUtils.copyOf(i_array15, 1);
        int[] i_array26 = new int[] {};
        int[] i_array27 = org.apache.commons.math.util.MathUtils.copyOf(i_array26);
        int[] i_array28 = org.apache.commons.math.util.MathUtils.copyOf(i_array26);
        int[] i_array29 = new int[] {};
        int[] i_array30 = org.apache.commons.math.util.MathUtils.copyOf(i_array29);
        int[] i_array36 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i37 = org.apache.commons.math.util.MathUtils.distanceInf(i_array29, i_array36);
        int[] i_array39 = org.apache.commons.math.util.MathUtils.copyOf(i_array29, 1);
        int i40 = org.apache.commons.math.util.MathUtils.distance1(i_array26, i_array29);
        int i41 = org.apache.commons.math.util.MathUtils.distance1(i_array15, i_array29);
        double d42 = org.apache.commons.math.util.MathUtils.distance(i_array3, i_array29);
        int[] i_array49 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array51 = org.apache.commons.math.util.MathUtils.copyOf(i_array49, (int) '4');
        double d52 = org.apache.commons.math.util.MathUtils.distance(i_array3, i_array49);
        int[] i_array59 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array61 = org.apache.commons.math.util.MathUtils.copyOf(i_array59, (int) '4');
        int i62 = org.apache.commons.math.util.MathUtils.distance1(i_array49, i_array61);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertTrue(i11 == 0);
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertTrue(i14 == 0);
        org.junit.Assert.assertNotNull(i_array15);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertNotNull(i_array22);
        org.junit.Assert.assertTrue(i23 == 0);
        org.junit.Assert.assertNotNull(i_array25);
        org.junit.Assert.assertNotNull(i_array26);
        org.junit.Assert.assertNotNull(i_array27);
        org.junit.Assert.assertNotNull(i_array28);
        org.junit.Assert.assertNotNull(i_array29);
        org.junit.Assert.assertNotNull(i_array30);
        org.junit.Assert.assertNotNull(i_array36);
        org.junit.Assert.assertTrue(i37 == 0);
        org.junit.Assert.assertNotNull(i_array39);
        org.junit.Assert.assertTrue(i40 == 0);
        org.junit.Assert.assertTrue(i41 == 0);
        org.junit.Assert.assertTrue(d42 == 0.0d);
        org.junit.Assert.assertNotNull(i_array49);
        org.junit.Assert.assertNotNull(i_array51);
        org.junit.Assert.assertTrue(d52 == 0.0d);
        org.junit.Assert.assertNotNull(i_array59);
        org.junit.Assert.assertNotNull(i_array61);
        org.junit.Assert.assertTrue(i62 == 0);
    }

    @Test
    public void test073() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test073");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 10.04987562112089d, (java.lang.Number) 1075970048, true);
        java.lang.Number number5 = numberIsTooSmallException4.getMin();
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + 1075970048 + "'", number5.equals(1075970048));
    }

    @Test
    public void test074() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test074");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck((-1067941888), 22);
        org.junit.Assert.assertTrue(i2 == (-1067941866));
    }

    @Test
    public void test075() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test075");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.acos();
        org.apache.commons.math.complex.Complex complex2 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex3);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
    }

    @Test
    public void test076() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test076");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 362880L, (float) 161700L, (float) (-1636086455L));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test077() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test077");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 42);
        java.math.BigInteger bigInteger6 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 1);
        java.math.BigInteger bigInteger8 = org.apache.commons.math.util.MathUtils.pow(bigInteger6, (long) (byte) 10);
        java.math.BigInteger bigInteger10 = org.apache.commons.math.util.MathUtils.pow(bigInteger6, 22);
        org.apache.commons.math.exception.util.Localizable localizable11 = null;
        java.lang.Number number12 = null;
        java.math.BigInteger bigInteger13 = null;
        java.math.BigInteger bigInteger15 = org.apache.commons.math.util.MathUtils.pow(bigInteger13, (long) 0);
        java.math.BigInteger bigInteger17 = org.apache.commons.math.util.MathUtils.pow(bigInteger15, 42);
        java.math.BigInteger bigInteger19 = org.apache.commons.math.util.MathUtils.pow(bigInteger15, 1);
        java.math.BigInteger bigInteger21 = org.apache.commons.math.util.MathUtils.pow(bigInteger19, (long) (byte) 10);
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException23 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable11, number12, (java.lang.Number) bigInteger19, false);
        java.math.BigInteger bigInteger24 = org.apache.commons.math.util.MathUtils.pow(bigInteger10, bigInteger19);
        org.junit.Assert.assertNotNull(bigInteger2);
        org.junit.Assert.assertNotNull(bigInteger4);
        org.junit.Assert.assertNotNull(bigInteger6);
        org.junit.Assert.assertNotNull(bigInteger8);
        org.junit.Assert.assertNotNull(bigInteger10);
        org.junit.Assert.assertNotNull(bigInteger15);
        org.junit.Assert.assertNotNull(bigInteger17);
        org.junit.Assert.assertNotNull(bigInteger19);
        org.junit.Assert.assertNotNull(bigInteger21);
        org.junit.Assert.assertNotNull(bigInteger24);
    }

    @Test
    public void test078() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test078");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array3);
        org.apache.commons.math.util.MathUtils.checkOrder(d_array3);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
    }

    @Test
    public void test079() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test079");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        java.lang.Number number6 = nonMonotonousSequenceException3.getPrevious();
        boolean b7 = nonMonotonousSequenceException3.getStrict();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection8 = nonMonotonousSequenceException3.getDirection();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number6 + "' != '" + 2.2250738585072014E-308d + "'", number6.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue(b7 == true);
        org.junit.Assert.assertTrue("'" + orderDirection8 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection8.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test080() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test080");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = new double[] { (-1.0f) };
        double[] d_array7 = org.apache.commons.math.util.MathUtils.copyOf(d_array5, 1);
        double[] d_array9 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array7, (double) 0L);
        double[] d_array11 = new double[] { (byte) -1 };
        double[] d_array13 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array11, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection14 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b17 = org.apache.commons.math.util.MathUtils.checkOrder(d_array13, orderDirection14, true, false);
        boolean b18 = org.apache.commons.math.util.MathUtils.equals(d_array7, d_array13);
        int i19 = org.apache.commons.math.util.MathUtils.hash(d_array7);
        double d20 = org.apache.commons.math.util.MathUtils.distance1(d_array3, d_array7);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertTrue("'" + orderDirection14 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection14.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b17 == true);
        org.junit.Assert.assertTrue(b18 == false);
        org.junit.Assert.assertTrue(i19 == (-1074790369));
        org.junit.Assert.assertTrue(d20 == 0.0d);
    }

    @Test
    public void test081() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test081");
        java.lang.Number number0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException(number0, (java.lang.Number) 2.2250738585072014E-308d, true);
        java.lang.Number number4 = numberIsTooSmallException3.getArgument();
        org.apache.commons.math.exception.util.Localizable localizable5 = null;
        java.lang.Number number7 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException9 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable5, (java.lang.Number) 3, number7, true);
        numberIsTooSmallException3.addSuppressed((java.lang.Throwable) numberIsTooLargeException9);
        org.junit.Assert.assertNull(number4);
    }

    @Test
    public void test082() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test082");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        org.apache.commons.math.complex.Complex complex9 = complex5.subtract(complex7);
        org.apache.commons.math.complex.Complex complex10 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex11 = complex10.cos();
        org.apache.commons.math.complex.Complex complex12 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex13 = complex12.cos();
        org.apache.commons.math.complex.Complex complex14 = complex10.subtract(complex12);
        org.apache.commons.math.complex.Complex complex15 = complex5.add(complex14);
        org.apache.commons.math.complex.Complex complex16 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex17 = complex14.pow(complex16);
        org.apache.commons.math.complex.Complex complex18 = complex14.sqrt();
        org.apache.commons.math.complex.Complex complex19 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex20 = complex19.cos();
        org.apache.commons.math.complex.Complex complex21 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex22 = complex21.cos();
        org.apache.commons.math.complex.Complex complex23 = complex19.subtract(complex21);
        org.apache.commons.math.complex.Complex complex24 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex25 = complex24.cos();
        org.apache.commons.math.complex.Complex complex26 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex27 = complex26.cos();
        org.apache.commons.math.complex.Complex complex28 = complex24.subtract(complex26);
        org.apache.commons.math.complex.Complex complex29 = complex19.add(complex28);
        double d30 = complex19.getArgument();
        org.apache.commons.math.complex.Complex complex31 = complex19.exp();
        org.apache.commons.math.complex.Complex complex32 = complex19.negate();
        org.apache.commons.math.complex.Complex complex33 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex34 = complex33.cos();
        org.apache.commons.math.complex.Complex complex35 = complex19.subtract(complex33);
        org.apache.commons.math.complex.Complex complex36 = complex35.cosh();
        org.apache.commons.math.complex.Complex complex37 = complex36.sinh();
        org.apache.commons.math.complex.Complex complex38 = complex14.pow(complex36);
        org.apache.commons.math.complex.Complex complex39 = complex3.multiply(complex38);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
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
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complex24);
        org.junit.Assert.assertNotNull(complex25);
        org.junit.Assert.assertNotNull(complex26);
        org.junit.Assert.assertNotNull(complex27);
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertEquals((double) d30, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(complex34);
        org.junit.Assert.assertNotNull(complex35);
        org.junit.Assert.assertNotNull(complex36);
        org.junit.Assert.assertNotNull(complex37);
        org.junit.Assert.assertNotNull(complex38);
        org.junit.Assert.assertNotNull(complex39);
    }

    @Test
    public void test083() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test083");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 98, (float) (short) -1, (-1067941866));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test084() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test084");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b7 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection4, true, false);
        double[] d_array11 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d12 = org.apache.commons.math.util.MathUtils.safeNorm(d_array11);
        int i13 = org.apache.commons.math.util.MathUtils.hash(d_array11);
        boolean b14 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array11);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection15 = org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING;
        org.apache.commons.math.exception.util.Localizable localizable16 = null;
        org.apache.commons.math.exception.util.Localizable localizable17 = null;
        org.apache.commons.math.complex.Complex complex18 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex19 = complex18.cos();
        org.apache.commons.math.complex.Complex complex20 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex21 = complex20.cos();
        org.apache.commons.math.complex.Complex complex22 = complex18.subtract(complex20);
        org.apache.commons.math.complex.Complex complex23 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex24 = complex23.cos();
        org.apache.commons.math.complex.Complex complex25 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex26 = complex25.cos();
        org.apache.commons.math.complex.Complex complex27 = complex23.subtract(complex25);
        org.apache.commons.math.complex.Complex complex28 = complex18.add(complex27);
        double d29 = complex18.getArgument();
        org.apache.commons.math.complex.Complex complex30 = complex18.exp();
        org.apache.commons.math.complex.Complex complex31 = complex18.negate();
        org.apache.commons.math.complex.Complex complex32 = complex18.asin();
        org.apache.commons.math.complex.Complex complex33 = complex18.acos();
        org.apache.commons.math.exception.util.Localizable localizable34 = null;
        double[] d_array35 = new double[] {};
        double[] d_array39 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array43 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array47 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array51 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array52 = new double[][] { d_array39, d_array43, d_array47, d_array51 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array35, d_array_array52);
        double[] d_array54 = new double[] {};
        double[] d_array58 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array62 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array66 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array70 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array71 = new double[][] { d_array58, d_array62, d_array66, d_array70 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array54, d_array_array71);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array35, d_array_array71);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex18, localizable34, (java.lang.Object[]) d_array_array71);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException75 = new org.apache.commons.math.exception.MathArithmeticException(localizable17, (java.lang.Object[]) d_array_array71);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException76 = new org.apache.commons.math.exception.MathArithmeticException(localizable16, (java.lang.Object[]) d_array_array71);
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array3, orderDirection15, d_array_array71);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.DimensionMismatchException");
        } catch (org.apache.commons.math.exception.DimensionMismatchException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b7 == true);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertTrue(d12 == 10.04987562112089d);
        org.junit.Assert.assertTrue(i13 == (-2064878497));
        org.junit.Assert.assertTrue(b14 == false);
        org.junit.Assert.assertTrue("'" + orderDirection15 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING + "'", orderDirection15.equals(org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING));
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
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertEquals((double) d29, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array39);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array51);
        org.junit.Assert.assertNotNull(d_array_array52);
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertNotNull(d_array58);
        org.junit.Assert.assertNotNull(d_array62);
        org.junit.Assert.assertNotNull(d_array66);
        org.junit.Assert.assertNotNull(d_array70);
        org.junit.Assert.assertNotNull(d_array_array71);
    }

    @Test
    public void test085() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test085");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog(1072693248, (-738962753));
        org.junit.Assert.assertTrue(d2 == 0.0d);
    }

    @Test
    public void test086() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test086");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 1072693248, (double) 10L, (double) (byte) -1);
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test087() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test087");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck(10000000000L, (long) 'a');
        org.junit.Assert.assertTrue(long2 == 970000000000L);
    }

    @Test
    public void test088() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test088");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException6 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection7 = nonMonotonousSequenceException6.getDirection();
        int i8 = nonMonotonousSequenceException6.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection9 = nonMonotonousSequenceException6.getDirection();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException11 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 0L, (java.lang.Number) 1.0f, (int) (short) 1, orderDirection9, true);
        org.junit.Assert.assertTrue("'" + orderDirection7 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection7.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i8 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection9 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection9.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test089() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test089");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (-1636086454L), (float) 92561040L, (float) 11);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test090() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test090");
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
        double d18 = complex16.getReal();
        org.apache.commons.math.complex.Complex complex19 = complex16.tanh();
        org.apache.commons.math.complex.Complex complex21 = complex16.multiply(3628800.0d);
        org.apache.commons.math.complex.Complex complex22 = complex16.cos();
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
        org.junit.Assert.assertEquals((double) d18, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
    }

    @Test
    public void test091() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test091");
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
        org.apache.commons.math.complex.Complex complex20 = complex18.multiply((double) 100.0f);
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
        org.junit.Assert.assertNotNull(complex20);
    }

    @Test
    public void test092() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test092");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex1.divide(complex2);
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex5 = complex4.acos();
        org.apache.commons.math.complex.Complex complex6 = complex2.divide(complex4);
        org.apache.commons.math.complex.Complex complex7 = complex2.tan();
        boolean b8 = complex0.equals((java.lang.Object) complex7);
        org.apache.commons.math.complex.Complex complex9 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex10 = complex9.cos();
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = complex11.cos();
        org.apache.commons.math.complex.Complex complex13 = complex9.subtract(complex11);
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex17 = complex16.cos();
        org.apache.commons.math.complex.Complex complex18 = complex14.subtract(complex16);
        org.apache.commons.math.complex.Complex complex19 = complex9.add(complex18);
        double d20 = complex9.getArgument();
        org.apache.commons.math.complex.Complex complex21 = complex9.exp();
        org.apache.commons.math.complex.Complex complex22 = complex9.negate();
        org.apache.commons.math.complex.Complex complex23 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex24 = complex23.cos();
        org.apache.commons.math.complex.Complex complex25 = complex9.subtract(complex23);
        org.apache.commons.math.complex.Complex complex26 = complex25.cosh();
        org.apache.commons.math.complex.Complex complex27 = complex25.tanh();
        org.apache.commons.math.complex.Complex complex28 = complex0.multiply(complex27);
        org.apache.commons.math.complex.Complex complex29 = complex28.sin();
        org.apache.commons.math.complex.Complex complex30 = complex28.sqrt();
        org.apache.commons.math.complex.Complex complex31 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex32 = complex31.cos();
        org.apache.commons.math.complex.Complex complex33 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex34 = complex33.cos();
        org.apache.commons.math.complex.Complex complex35 = complex31.subtract(complex33);
        org.apache.commons.math.complex.Complex complex36 = complex30.divide(complex33);
        org.apache.commons.math.complex.Complex complex37 = complex33.acos();
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex33);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertTrue(b8 == true);
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
        org.junit.Assert.assertEquals((double) d20, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complex24);
        org.junit.Assert.assertNotNull(complex25);
        org.junit.Assert.assertNotNull(complex26);
        org.junit.Assert.assertNotNull(complex27);
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(complex34);
        org.junit.Assert.assertNotNull(complex35);
        org.junit.Assert.assertNotNull(complex36);
        org.junit.Assert.assertNotNull(complex37);
    }

    @Test
    public void test093() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test093");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        java.lang.Number number2 = notPositiveException1.getArgument();
        boolean b3 = notPositiveException1.getBoundIsAllowed();
        java.lang.Number number4 = notPositiveException1.getMin();
        org.junit.Assert.assertTrue("'" + number2 + "' != '" + (short) 1 + "'", number2.equals((short) 1));
        org.junit.Assert.assertTrue(b3 == true);
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 0 + "'", number4.equals(0));
    }

    @Test
    public void test094() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test094");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 1L, (java.lang.Number) 36.0d, true);
        java.lang.Number number4 = numberIsTooLargeException3.getMax();
        boolean b5 = numberIsTooLargeException3.getBoundIsAllowed();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 36.0d + "'", number4.equals(36.0d));
        org.junit.Assert.assertTrue(b5 == true);
    }

    @Test
    public void test095() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test095");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 11L, (java.lang.Number) (byte) 100, 22);
    }

    @Test
    public void test096() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test096");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.lang.String str5 = complex2.toString();
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = complex7.divide(complex8);
        org.apache.commons.math.complex.Complex complex10 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex11 = complex10.acos();
        org.apache.commons.math.complex.Complex complex12 = complex8.divide(complex10);
        org.apache.commons.math.complex.Complex complex13 = complex8.tan();
        boolean b14 = complex6.equals((java.lang.Object) complex13);
        org.apache.commons.math.complex.Complex complex15 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex16 = complex15.cos();
        org.apache.commons.math.complex.Complex complex17 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex18 = complex17.cos();
        org.apache.commons.math.complex.Complex complex19 = complex15.subtract(complex17);
        org.apache.commons.math.complex.Complex complex20 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex21 = complex20.cos();
        org.apache.commons.math.complex.Complex complex22 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex23 = complex22.cos();
        org.apache.commons.math.complex.Complex complex24 = complex20.subtract(complex22);
        org.apache.commons.math.complex.Complex complex25 = complex15.add(complex24);
        double d26 = complex15.getArgument();
        org.apache.commons.math.complex.Complex complex27 = complex15.exp();
        org.apache.commons.math.complex.Complex complex28 = complex15.negate();
        org.apache.commons.math.complex.Complex complex29 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex30 = complex29.cos();
        org.apache.commons.math.complex.Complex complex31 = complex15.subtract(complex29);
        org.apache.commons.math.complex.Complex complex32 = complex31.cosh();
        org.apache.commons.math.complex.Complex complex33 = complex31.tanh();
        org.apache.commons.math.complex.Complex complex34 = complex6.multiply(complex33);
        boolean b35 = complex2.equals((java.lang.Object) complex6);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertTrue("'" + str5 + "' != '" + "(NaN, NaN)" + "'", str5.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertTrue(b14 == true);
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
        org.junit.Assert.assertEquals((double) d26, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex27);
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(complex34);
        org.junit.Assert.assertTrue(b35 == true);
    }

    @Test
    public void test097() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test097");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals(1.0f, (float) 970000000000L);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test098() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test098");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException(0, (-447362044));
    }

    @Test
    public void test099() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test099");
        double d2 = org.apache.commons.math.util.MathUtils.log((double) 7766279631452241962L, Double.NaN);
        org.junit.Assert.assertEquals((double) d2, Double.NaN, 0);
    }

    @Test
    public void test100() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test100");
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
        org.apache.commons.math.complex.Complex complex18 = complex17.tanh();
        double d19 = complex18.getReal();
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
        org.junit.Assert.assertEquals((double) d19, Double.NaN, 0);
    }

    @Test
    public void test101() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test101");
        long long2 = org.apache.commons.math.util.MathUtils.lcm(1300L, (long) 20);
        org.junit.Assert.assertTrue(long2 == 1300L);
    }

    @Test
    public void test102() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test102");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo(0.28318530717958623d, (double) (short) 1, 19683);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test103() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test103");
        java.lang.Number number0 = null;
        java.math.BigInteger bigInteger1 = null;
        java.math.BigInteger bigInteger3 = org.apache.commons.math.util.MathUtils.pow(bigInteger1, (long) 0);
        java.math.BigInteger bigInteger5 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 42);
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException7 = new org.apache.commons.math.exception.NumberIsTooSmallException(number0, (java.lang.Number) bigInteger5, true);
        org.junit.Assert.assertNotNull(bigInteger3);
        org.junit.Assert.assertNotNull(bigInteger5);
    }

    @Test
    public void test104() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test104");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) 1078591488, 11L);
        org.junit.Assert.assertTrue(long2 == 1078591477L);
    }

    @Test
    public void test105() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test105");
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
        org.apache.commons.math.complex.Complex complex27 = complex24.cos();
        org.apache.commons.math.complex.Complex complex28 = complex27.exp();
        double d29 = complex27.getReal();
        org.apache.commons.math.complex.ComplexField complexField30 = complex27.getField();
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
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertTrue(d29 == 1.0d);
        org.junit.Assert.assertNotNull(complexField30);
    }

    @Test
    public void test106() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test106");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex1.divide(complex2);
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex5 = complex4.acos();
        org.apache.commons.math.complex.Complex complex6 = complex2.divide(complex4);
        org.apache.commons.math.complex.Complex complex7 = complex2.tan();
        boolean b8 = complex0.equals((java.lang.Object) complex7);
        org.apache.commons.math.complex.Complex complex9 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex10 = complex9.cos();
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = complex11.cos();
        org.apache.commons.math.complex.Complex complex13 = complex9.subtract(complex11);
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex17 = complex16.cos();
        org.apache.commons.math.complex.Complex complex18 = complex14.subtract(complex16);
        org.apache.commons.math.complex.Complex complex19 = complex9.add(complex18);
        double d20 = complex9.getArgument();
        org.apache.commons.math.complex.Complex complex21 = complex9.exp();
        org.apache.commons.math.complex.Complex complex22 = complex9.negate();
        org.apache.commons.math.complex.Complex complex23 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex24 = complex23.cos();
        org.apache.commons.math.complex.Complex complex25 = complex9.subtract(complex23);
        org.apache.commons.math.complex.Complex complex26 = complex25.cosh();
        org.apache.commons.math.complex.Complex complex27 = complex25.tanh();
        org.apache.commons.math.complex.Complex complex28 = complex0.multiply(complex27);
        org.apache.commons.math.complex.Complex complex29 = complex28.sin();
        org.apache.commons.math.complex.Complex complex30 = complex28.sqrt();
        org.apache.commons.math.complex.Complex complex31 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex32 = complex31.cos();
        org.apache.commons.math.complex.Complex complex33 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex34 = complex33.cos();
        org.apache.commons.math.complex.Complex complex35 = complex31.subtract(complex33);
        org.apache.commons.math.complex.Complex complex36 = complex30.divide(complex33);
        org.apache.commons.math.complex.Complex complex37 = complex33.acos();
        org.apache.commons.math.complex.Complex complex38 = null;
        try {
            org.apache.commons.math.complex.Complex complex39 = complex37.pow(complex38);
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
        org.junit.Assert.assertTrue(b8 == true);
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
        org.junit.Assert.assertEquals((double) d20, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complex24);
        org.junit.Assert.assertNotNull(complex25);
        org.junit.Assert.assertNotNull(complex26);
        org.junit.Assert.assertNotNull(complex27);
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(complex34);
        org.junit.Assert.assertNotNull(complex35);
        org.junit.Assert.assertNotNull(complex36);
        org.junit.Assert.assertNotNull(complex37);
    }

    @Test
    public void test107() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test107");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array20 = new double[] { (byte) -1 };
        double[] d_array22 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array20, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException26 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection27 = nonMonotonousSequenceException26.getDirection();
        int i28 = nonMonotonousSequenceException26.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable29 = null;
        java.lang.Object[] obj_array31 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException32 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable29, (java.lang.Number) (short) 100, obj_array31);
        java.lang.Number number33 = notFiniteNumberException32.getArgument();
        nonMonotonousSequenceException26.addSuppressed((java.lang.Throwable) notFiniteNumberException32);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection35 = nonMonotonousSequenceException26.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array22, orderDirection35, false);
        double d38 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array22);
        double[] d_array40 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array22, 197.05131665139353d);
        double[] d_array41 = null;
        boolean b42 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array40, d_array41);
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertTrue("'" + orderDirection27 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection27.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i28 == 97);
        org.junit.Assert.assertTrue("'" + number33 + "' != '" + (short) 100 + "'", number33.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection35 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection35.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d38 == 0.0d);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertTrue(b42 == false);
    }

    @Test
    public void test108() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test108");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((-4.47362041E8d), 1.1102230246251565E-16d, (-738962753));
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test109() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test109");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.math.BigInteger bigInteger1 = null;
        java.math.BigInteger bigInteger3 = org.apache.commons.math.util.MathUtils.pow(bigInteger1, (long) 0);
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException6 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) bigInteger1, (java.lang.Number) 8170176069297290577L, true);
        try {
            java.lang.String str7 = numberIsTooLargeException6.toString();
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger3);
    }

    @Test
    public void test110() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test110");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex6 = complex2.nthRoot((int) (short) 10);
        double d7 = complex2.abs();
        org.apache.commons.math.complex.Complex complex8 = complex2.atan();
        org.apache.commons.math.complex.Complex complex9 = complex2.atan();
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
    public void test111() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test111");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array5 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array6 = new int[] {};
        int[] i_array7 = org.apache.commons.math.util.MathUtils.copyOf(i_array6);
        int[] i_array13 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i14 = org.apache.commons.math.util.MathUtils.distanceInf(i_array6, i_array13);
        int[] i_array16 = org.apache.commons.math.util.MathUtils.copyOf(i_array6, 1);
        int i17 = org.apache.commons.math.util.MathUtils.distance1(i_array3, i_array6);
        int i18 = org.apache.commons.math.util.MathUtils.distance1(i_array0, i_array3);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array5);
        org.junit.Assert.assertNotNull(i_array6);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertTrue(i14 == 0);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertTrue(i17 == 0);
        org.junit.Assert.assertTrue(i18 == 0);
    }

    @Test
    public void test112() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test112");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 11L, (java.lang.Number) 10.0f, false);
    }

    @Test
    public void test113() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test113");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 92561040L, (double) 3628800L, 1.0d);
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test114() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test114");
        double[] d_array0 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException4 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        java.lang.Number number5 = nonMonotonousSequenceException4.getPrevious();
        java.lang.String str6 = nonMonotonousSequenceException4.toString();
        boolean b7 = nonMonotonousSequenceException4.getStrict();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection8 = nonMonotonousSequenceException4.getDirection();
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection8, false);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + 2.2250738585072014E-308d + "'", number5.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + str6 + "' != '" + "org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)" + "'", str6.equals("org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)"));
        org.junit.Assert.assertTrue(b7 == true);
        org.junit.Assert.assertTrue("'" + orderDirection8 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection8.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test115() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test115");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = complex1.acos();
        java.util.List<org.apache.commons.math.complex.Complex> list_complex5 = complex3.nthRoot((int) '#');
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(list_complex5);
    }

    @Test
    public void test116() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test116");
        int i1 = org.apache.commons.math.util.MathUtils.sign(1078591488);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test117() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test117");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (-2112331276), 3);
    }

    @Test
    public void test118() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test118");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) 100.0f);
        java.lang.Number number2 = notPositiveException1.getArgument();
        org.junit.Assert.assertTrue("'" + number2 + "' != '" + 100.0f + "'", number2.equals(100.0f));
    }

    @Test
    public void test119() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test119");
        double d1 = org.apache.commons.math.util.MathUtils.cosh(36.0d);
        org.junit.Assert.assertTrue(d1 == 2.1556157735575975E15d);
    }

    @Test
    public void test120() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test120");
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
        org.apache.commons.math.complex.Complex complex15 = complex0.sin();
        java.util.List<org.apache.commons.math.complex.Complex> list_complex17 = complex0.nthRoot(149);
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
        org.junit.Assert.assertNotNull(list_complex17);
    }

    @Test
    public void test121() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test121");
        double[] d_array0 = null;
        double[] d_array2 = new double[] { (byte) -1 };
        double[] d_array4 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array2, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection5 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b8 = org.apache.commons.math.util.MathUtils.checkOrder(d_array4, orderDirection5, true, false);
        try {
            boolean b11 = org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection5, true, true);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(d_array2);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertTrue("'" + orderDirection5 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection5.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b8 == true);
    }

    @Test
    public void test122() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test122");
        float f1 = org.apache.commons.math.util.MathUtils.indicator((float) 1);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test123() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test123");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex4.divide(complex5);
        org.apache.commons.math.complex.Complex complex7 = complex3.multiply(complex4);
        org.apache.commons.math.complex.Complex complex8 = complex4.sqrt();
        java.lang.String str9 = complex4.toString();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertTrue("'" + str9 + "' != '" + "(NaN, NaN)" + "'", str9.equals("(NaN, NaN)"));
    }

    @Test
    public void test124() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test124");
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
        double d20 = complex16.abs();
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
        org.junit.Assert.assertEquals((double) d20, Double.NaN, 0);
    }

    @Test
    public void test125() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test125");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck((-1074790400), 98);
        org.junit.Assert.assertTrue(i2 == (-1074790302));
    }

    @Test
    public void test126() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test126");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (-1), (int) ' ');
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext4 = dimensionMismatchException3.getContext();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException8 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 26.0d, (java.lang.Number) (-1.1752011936438014d), (-1074790369));
        dimensionMismatchException3.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException8);
        int i10 = dimensionMismatchException3.getDimension();
        org.junit.Assert.assertNotNull(exceptionContext4);
        org.junit.Assert.assertTrue(i10 == 32);
    }

    @Test
    public void test127() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test127");
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        float[] f_array8 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array10 = new float[] { (-1074790400) };
        boolean b11 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array8, f_array10);
        java.lang.Object[] obj_array17 = new java.lang.Object[] { b11, 0, 0, (-1.0f), 100.0f, (short) 1 };
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException18 = new org.apache.commons.math.exception.MathArithmeticException(localizable2, obj_array17);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException19 = new org.apache.commons.math.exception.MathArithmeticException(localizable1, obj_array17);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException20 = new org.apache.commons.math.exception.NotFiniteNumberException((java.lang.Number) 1074790400, obj_array17);
        java.lang.Throwable[] throwable_array21 = notFiniteNumberException20.getSuppressed();
        org.junit.Assert.assertNotNull(f_array8);
        org.junit.Assert.assertNotNull(f_array10);
        org.junit.Assert.assertTrue(b11 == false);
        org.junit.Assert.assertNotNull(obj_array17);
        org.junit.Assert.assertNotNull(throwable_array21);
    }

    @Test
    public void test128() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test128");
        byte byte1 = org.apache.commons.math.util.MathUtils.indicator((byte) -1);
        org.junit.Assert.assertTrue(byte1 == (byte) -1);
    }

    @Test
    public void test129() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test129");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(1.0E10d, (double) 4, 0.0d);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test130() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test130");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.exp();
        boolean b3 = complex1.isInfinite();
        try {
            java.util.List<org.apache.commons.math.complex.Complex> list_complex5 = complex1.nthRoot(0);
            org.junit.Assert.fail("Expected anonymous exception");
        } catch (java.lang.IllegalArgumentException e) {
            if (!e.getClass().isAnonymousClass()) {
                org.junit.Assert.fail("Expected anonymous exception, got " + e.getClass().getCanonicalName());
            }
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test131() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test131");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.tanh();
        org.apache.commons.math.complex.Complex complex5 = complex2.multiply(complex4);
        org.apache.commons.math.complex.Complex complex6 = complex2.asin();
        org.apache.commons.math.complex.Complex complex7 = complex2.tan();
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
    public void test132() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test132");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = complex2.acos();
        org.apache.commons.math.complex.Complex complex6 = complex5.sqrt1z();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
    }

    @Test
    public void test133() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test133");
        double[] d_array0 = null;
        java.math.BigInteger bigInteger2 = null;
        java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, (long) 0);
        java.math.BigInteger bigInteger6 = org.apache.commons.math.util.MathUtils.pow(bigInteger4, 42);
        java.math.BigInteger bigInteger8 = org.apache.commons.math.util.MathUtils.pow(bigInteger4, 1);
        double[] d_array11 = new double[] { (byte) -1 };
        double[] d_array13 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array11, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection14 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b17 = org.apache.commons.math.util.MathUtils.checkOrder(d_array13, orderDirection14, true, false);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException19 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) (-2112331376), (java.lang.Number) 1, 1072693248, orderDirection14, false);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection14, true);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger4);
        org.junit.Assert.assertNotNull(bigInteger6);
        org.junit.Assert.assertNotNull(bigInteger8);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertTrue("'" + orderDirection14 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection14.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b17 == true);
    }

    @Test
    public void test134() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test134");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 0, (java.lang.Number) 26.0d, 100);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test135() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test135");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        org.apache.commons.math.complex.Complex complex7 = complex6.tanh();
        java.util.List<org.apache.commons.math.complex.Complex> list_complex9 = complex6.nthRoot(1074790400);
        double d10 = complex6.getImaginary();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(list_complex9);
        org.junit.Assert.assertEquals((double) d10, Double.NaN, 0);
    }

    @Test
    public void test136() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test136");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.copyOf(d_array3);
        double d7 = org.apache.commons.math.util.MathUtils.safeNorm(d_array6);
        double[] d_array9 = new double[] { (-1.0f) };
        double[] d_array11 = org.apache.commons.math.util.MathUtils.copyOf(d_array9, 1);
        double d12 = org.apache.commons.math.util.MathUtils.safeNorm(d_array9);
        double[] d_array14 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array9, 1.1752011936438014d);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array14);
        double[] d_array19 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d20 = org.apache.commons.math.util.MathUtils.safeNorm(d_array19);
        double[] d_array21 = new double[] {};
        double[] d_array25 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array29 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array33 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array37 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array38 = new double[][] { d_array25, d_array29, d_array33, d_array37 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array21, d_array_array38);
        int i40 = org.apache.commons.math.util.MathUtils.hash(d_array21);
        boolean b41 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array19, d_array21);
        double[] d_array45 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d46 = org.apache.commons.math.util.MathUtils.safeNorm(d_array45);
        double[] d_array47 = new double[] {};
        double[] d_array51 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array55 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array59 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array63 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array64 = new double[][] { d_array51, d_array55, d_array59, d_array63 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array47, d_array_array64);
        int i66 = org.apache.commons.math.util.MathUtils.hash(d_array47);
        boolean b67 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array45, d_array47);
        boolean b68 = org.apache.commons.math.util.MathUtils.equals(d_array21, d_array45);
        double[] d_array70 = org.apache.commons.math.util.MathUtils.copyOf(d_array21, 0);
        boolean b71 = org.apache.commons.math.util.MathUtils.equals(d_array14, d_array21);
        double d72 = org.apache.commons.math.util.MathUtils.distance1(d_array6, d_array14);
        double[] d_array74 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array6, 9.619275968248924E151d);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertTrue(d7 == 1.0d);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertTrue(d12 == 1.0d);
        org.junit.Assert.assertNotNull(d_array14);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertTrue(d20 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array33);
        org.junit.Assert.assertNotNull(d_array37);
        org.junit.Assert.assertNotNull(d_array_array38);
        org.junit.Assert.assertTrue(i40 == 1);
        org.junit.Assert.assertTrue(b41 == false);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertTrue(d46 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array51);
        org.junit.Assert.assertNotNull(d_array55);
        org.junit.Assert.assertNotNull(d_array59);
        org.junit.Assert.assertNotNull(d_array63);
        org.junit.Assert.assertNotNull(d_array_array64);
        org.junit.Assert.assertTrue(i66 == 1);
        org.junit.Assert.assertTrue(b67 == false);
        org.junit.Assert.assertTrue(b68 == false);
        org.junit.Assert.assertNotNull(d_array70);
        org.junit.Assert.assertTrue(b71 == false);
        org.junit.Assert.assertTrue(d72 == 2.1752011936438014d);
        org.junit.Assert.assertNotNull(d_array74);
    }

    @Test
    public void test137() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test137");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) (-1636086454L), 2);
        org.junit.Assert.assertTrue(d2 == (-1.636086454E9d));
    }

    @Test
    public void test138() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test138");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.lcm(10000000000L, 7766279631452241920L);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test139() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test139");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (-1.0f), (double) 20);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test140() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test140");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (-2064878497L), 0.0f, (float) 52);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test141() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test141");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 42);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException7 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) bigInteger4, (java.lang.Number) 10.0f, (int) '#');
        int i8 = nonMonotonousSequenceException7.getIndex();
        org.junit.Assert.assertNotNull(bigInteger2);
        org.junit.Assert.assertNotNull(bigInteger4);
        org.junit.Assert.assertTrue(i8 == 35);
    }

    @Test
    public void test142() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test142");
        float[] f_array5 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array7 = new float[] { (-1074790400) };
        boolean b8 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array5, f_array7);
        float[] f_array14 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array16 = new float[] { (-1074790400) };
        boolean b17 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array14, f_array16);
        float[] f_array23 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array25 = new float[] { (-1074790400) };
        boolean b26 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array23, f_array25);
        boolean b27 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array16, f_array23);
        float[] f_array33 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array35 = new float[] { (-1074790400) };
        boolean b36 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array33, f_array35);
        float[] f_array42 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array44 = new float[] { (-1074790400) };
        boolean b45 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array42, f_array44);
        boolean b46 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array35, f_array42);
        boolean b47 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array16, f_array42);
        boolean b48 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array7, f_array42);
        float[] f_array54 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array56 = new float[] { (-1074790400) };
        boolean b57 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array54, f_array56);
        boolean b58 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array42, f_array54);
        float[] f_array59 = null;
        boolean b60 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array42, f_array59);
        org.junit.Assert.assertNotNull(f_array5);
        org.junit.Assert.assertNotNull(f_array7);
        org.junit.Assert.assertTrue(b8 == false);
        org.junit.Assert.assertNotNull(f_array14);
        org.junit.Assert.assertNotNull(f_array16);
        org.junit.Assert.assertTrue(b17 == false);
        org.junit.Assert.assertNotNull(f_array23);
        org.junit.Assert.assertNotNull(f_array25);
        org.junit.Assert.assertTrue(b26 == false);
        org.junit.Assert.assertTrue(b27 == false);
        org.junit.Assert.assertNotNull(f_array33);
        org.junit.Assert.assertNotNull(f_array35);
        org.junit.Assert.assertTrue(b36 == false);
        org.junit.Assert.assertNotNull(f_array42);
        org.junit.Assert.assertNotNull(f_array44);
        org.junit.Assert.assertTrue(b45 == false);
        org.junit.Assert.assertTrue(b46 == false);
        org.junit.Assert.assertTrue(b47 == false);
        org.junit.Assert.assertTrue(b48 == false);
        org.junit.Assert.assertNotNull(f_array54);
        org.junit.Assert.assertNotNull(f_array56);
        org.junit.Assert.assertTrue(b57 == false);
        org.junit.Assert.assertTrue(b58 == true);
        org.junit.Assert.assertTrue(b60 == false);
    }

    @Test
    public void test143() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test143");
        org.apache.commons.math.util.MathUtils.checkFinite((double) 52);
    }

    @Test
    public void test144() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test144");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) ' ', (float) 97, (float) 15235153920000L);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test145() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test145");
        long long1 = org.apache.commons.math.util.MathUtils.indicator(1636086543L);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test146() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test146");
        double d1 = org.apache.commons.math.util.MathUtils.indicator(99.35576372122958d);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test147() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test147");
        java.math.BigInteger bigInteger0 = null;
        try {
            java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, 8170176069297290577L);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test148() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test148");
        long long1 = org.apache.commons.math.util.MathUtils.indicator((long) 52);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test149() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test149");
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
        org.apache.commons.math.complex.Complex complex17 = complex0.tan();
        org.apache.commons.math.complex.Complex complex18 = complex17.asin();
        org.apache.commons.math.complex.Complex complex19 = complex18.sinh();
        boolean b21 = complex18.equals((java.lang.Object) 35L);
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
        org.junit.Assert.assertTrue(b21 == false);
    }

    @Test
    public void test150() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test150");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        double d5 = org.apache.commons.math.util.MathUtils.distance(i_array2, i_array4);
        int[] i_array7 = org.apache.commons.math.util.MathUtils.copyOf(i_array2, 0);
        int[] i_array9 = org.apache.commons.math.util.MathUtils.copyOf(i_array2, 3);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertTrue(d5 == 0.0d);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertNotNull(i_array9);
    }

    @Test
    public void test151() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test151");
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
        org.apache.commons.math.util.MathUtils.checkFinite(d_array53);
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
    public void test152() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test152");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.lcm((-2064878497), 2);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test153() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test153");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        java.lang.Object[] obj_array3 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException4 = new org.apache.commons.math.exception.NotFiniteNumberException((java.lang.Number) 100.0f, obj_array3);
        java.lang.Throwable[] throwable_array5 = notFiniteNumberException4.getSuppressed();
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException6 = new org.apache.commons.math.exception.MathArithmeticException(localizable1, (java.lang.Object[]) throwable_array5);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException7 = new org.apache.commons.math.exception.NullArgumentException(localizable0, (java.lang.Object[]) throwable_array5);
        org.junit.Assert.assertNotNull(throwable_array5);
    }

    @Test
    public void test154() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test154");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck((-738962753), 1100);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test155() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test155");
        byte byte1 = org.apache.commons.math.util.MathUtils.sign((byte) -1);
        org.junit.Assert.assertTrue(byte1 == (byte) -1);
    }

    @Test
    public void test156() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test156");
        double d1 = org.apache.commons.math.util.MathUtils.sign(0.0d);
        org.junit.Assert.assertTrue(d1 == 0.0d);
    }

    @Test
    public void test157() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test157");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) 3L, (double) (-447362041));
    }

    @Test
    public void test158() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test158");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (-1074790400), 19683);
    }

    @Test
    public void test159() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test159");
        long long2 = org.apache.commons.math.util.MathUtils.gcd((long) 42, 0L);
        org.junit.Assert.assertTrue(long2 == 42L);
    }

    @Test
    public void test160() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test160");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 2.1752011936438014d, (java.lang.Number) 1636086465, false);
        java.lang.Throwable[] throwable_array4 = numberIsTooSmallException3.getSuppressed();
        org.junit.Assert.assertNotNull(throwable_array4);
    }

    @Test
    public void test161() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test161");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        boolean b5 = nonMonotonousSequenceException3.getStrict();
        java.lang.String str6 = nonMonotonousSequenceException3.toString();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b5 == true);
        org.junit.Assert.assertTrue("'" + str6 + "' != '" + "org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)" + "'", str6.equals("org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)"));
    }

    @Test
    public void test162() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test162");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        double d5 = org.apache.commons.math.util.MathUtils.distance(i_array2, i_array4);
        int[] i_array7 = org.apache.commons.math.util.MathUtils.copyOf(i_array2, 0);
        int[] i_array8 = new int[] {};
        int[] i_array9 = org.apache.commons.math.util.MathUtils.copyOf(i_array8);
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array8);
        int[] i_array11 = new int[] {};
        int[] i_array12 = org.apache.commons.math.util.MathUtils.copyOf(i_array11);
        int[] i_array18 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i19 = org.apache.commons.math.util.MathUtils.distanceInf(i_array11, i_array18);
        int[] i_array21 = org.apache.commons.math.util.MathUtils.copyOf(i_array11, 1);
        int i22 = org.apache.commons.math.util.MathUtils.distance1(i_array8, i_array11);
        int[] i_array24 = org.apache.commons.math.util.MathUtils.copyOf(i_array11, 98);
        int i25 = org.apache.commons.math.util.MathUtils.distance1(i_array7, i_array24);
        try {
            int[] i_array27 = org.apache.commons.math.util.MathUtils.copyOf(i_array7, (-1074790302));
            org.junit.Assert.fail("Expected exception of type java.lang.NegativeArraySizeException");
        } catch (java.lang.NegativeArraySizeException e) {
        }
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertTrue(d5 == 0.0d);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertNotNull(i_array8);
        org.junit.Assert.assertNotNull(i_array9);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertNotNull(i_array11);
        org.junit.Assert.assertNotNull(i_array12);
        org.junit.Assert.assertNotNull(i_array18);
        org.junit.Assert.assertTrue(i19 == 0);
        org.junit.Assert.assertNotNull(i_array21);
        org.junit.Assert.assertTrue(i22 == 0);
        org.junit.Assert.assertNotNull(i_array24);
        org.junit.Assert.assertTrue(i25 == 0);
    }

    @Test
    public void test163() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test163");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck((-2064878497), (int) (byte) 10);
        org.junit.Assert.assertTrue(i2 == (-2064878487));
    }

    @Test
    public void test164() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test164");
        long long2 = org.apache.commons.math.util.MathUtils.pow(10000000000L, 98);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test165() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test165");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array10 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i11 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array10);
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 1);
        int i14 = org.apache.commons.math.util.MathUtils.distance1(i_array0, i_array3);
        int[] i_array15 = new int[] {};
        int[] i_array16 = org.apache.commons.math.util.MathUtils.copyOf(i_array15);
        int[] i_array22 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i23 = org.apache.commons.math.util.MathUtils.distanceInf(i_array15, i_array22);
        int[] i_array25 = org.apache.commons.math.util.MathUtils.copyOf(i_array15, 1);
        int i26 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array25);
        int[] i_array27 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array29 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 1155);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertTrue(i11 == 0);
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertTrue(i14 == 0);
        org.junit.Assert.assertNotNull(i_array15);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertNotNull(i_array22);
        org.junit.Assert.assertTrue(i23 == 0);
        org.junit.Assert.assertNotNull(i_array25);
        org.junit.Assert.assertTrue(i26 == 0);
        org.junit.Assert.assertNotNull(i_array27);
        org.junit.Assert.assertNotNull(i_array29);
    }

    @Test
    public void test166() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test166");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array1);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, 1.1752011936438014d);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array6);
        double[] d_array11 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d12 = org.apache.commons.math.util.MathUtils.safeNorm(d_array11);
        double[] d_array13 = new double[] {};
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array25 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array29 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array30 = new double[][] { d_array17, d_array21, d_array25, d_array29 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array13, d_array_array30);
        int i32 = org.apache.commons.math.util.MathUtils.hash(d_array13);
        boolean b33 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array11, d_array13);
        double[] d_array37 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d38 = org.apache.commons.math.util.MathUtils.safeNorm(d_array37);
        double[] d_array39 = new double[] {};
        double[] d_array43 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array47 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array51 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array55 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array56 = new double[][] { d_array43, d_array47, d_array51, d_array55 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array39, d_array_array56);
        int i58 = org.apache.commons.math.util.MathUtils.hash(d_array39);
        boolean b59 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array37, d_array39);
        boolean b60 = org.apache.commons.math.util.MathUtils.equals(d_array13, d_array37);
        double[] d_array62 = org.apache.commons.math.util.MathUtils.copyOf(d_array13, 0);
        boolean b63 = org.apache.commons.math.util.MathUtils.equals(d_array6, d_array13);
        double[] d_array65 = new double[] { (byte) -1 };
        double[] d_array67 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array65, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException71 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i72 = nonMonotonousSequenceException71.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection73 = nonMonotonousSequenceException71.getDirection();
        boolean b76 = org.apache.commons.math.util.MathUtils.checkOrder(d_array65, orderDirection73, true, false);
        double d77 = org.apache.commons.math.util.MathUtils.distance(d_array6, d_array65);
        double[] d_array79 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array6, 0.0d);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection80 = org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING;
        boolean b83 = org.apache.commons.math.util.MathUtils.checkOrder(d_array79, orderDirection80, false, false);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 1.0d);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertTrue(d12 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array_array30);
        org.junit.Assert.assertTrue(i32 == 1);
        org.junit.Assert.assertTrue(b33 == false);
        org.junit.Assert.assertNotNull(d_array37);
        org.junit.Assert.assertTrue(d38 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array39);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array51);
        org.junit.Assert.assertNotNull(d_array55);
        org.junit.Assert.assertNotNull(d_array_array56);
        org.junit.Assert.assertTrue(i58 == 1);
        org.junit.Assert.assertTrue(b59 == false);
        org.junit.Assert.assertTrue(b60 == false);
        org.junit.Assert.assertNotNull(d_array62);
        org.junit.Assert.assertTrue(b63 == false);
        org.junit.Assert.assertNotNull(d_array65);
        org.junit.Assert.assertNotNull(d_array67);
        org.junit.Assert.assertTrue(i72 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection73 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection73.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b76 == true);
        org.junit.Assert.assertTrue(d77 == 2.1752011936438014d);
        org.junit.Assert.assertNotNull(d_array79);
        org.junit.Assert.assertTrue("'" + orderDirection80 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING + "'", orderDirection80.equals(org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING));
        org.junit.Assert.assertTrue(b83 == true);
    }

    @Test
    public void test167() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test167");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) 4, 0.0d);
    }

    @Test
    public void test168() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test168");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((-1636086455L), (long) (-2112331276));
        org.junit.Assert.assertTrue(long2 == 3455956589136466580L);
    }

    @Test
    public void test169() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test169");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck(0, (-2112331476));
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test170() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test170");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck((int) (byte) 10, (-1));
        org.junit.Assert.assertTrue(i2 == 11);
    }

    @Test
    public void test171() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test171");
        double[] d_array0 = null;
        double[] d_array1 = new double[] {};
        double[] d_array5 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array9 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array18 = new double[][] { d_array5, d_array9, d_array13, d_array17 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array1, d_array_array18);
        double[] d_array21 = new double[] { (byte) -1 };
        double[] d_array23 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array21, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException27 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection28 = nonMonotonousSequenceException27.getDirection();
        int i29 = nonMonotonousSequenceException27.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable30 = null;
        java.lang.Object[] obj_array32 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException33 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable30, (java.lang.Number) (short) 100, obj_array32);
        java.lang.Number number34 = notFiniteNumberException33.getArgument();
        nonMonotonousSequenceException27.addSuppressed((java.lang.Throwable) notFiniteNumberException33);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection36 = nonMonotonousSequenceException27.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array23, orderDirection36, false);
        double d39 = org.apache.commons.math.util.MathUtils.distance1(d_array1, d_array23);
        double[] d_array41 = new double[] { (byte) -1 };
        double[] d_array43 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array41, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException47 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i48 = nonMonotonousSequenceException47.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection49 = nonMonotonousSequenceException47.getDirection();
        boolean b52 = org.apache.commons.math.util.MathUtils.checkOrder(d_array41, orderDirection49, true, false);
        double d53 = org.apache.commons.math.util.MathUtils.distance(d_array23, d_array41);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection57 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException59 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) Double.POSITIVE_INFINITY, (java.lang.Number) 1.0f, 0, orderDirection57, true);
        org.apache.commons.math.util.MathUtils.checkOrder(d_array41, orderDirection57, true);
        boolean b62 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array0, d_array41);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array_array18);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertTrue("'" + orderDirection28 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection28.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i29 == 97);
        org.junit.Assert.assertTrue("'" + number34 + "' != '" + (short) 100 + "'", number34.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection36 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection36.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d39 == 0.0d);
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertTrue(i48 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection49 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection49.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b52 == true);
        org.junit.Assert.assertTrue(d53 == 2.0d);
        org.junit.Assert.assertTrue("'" + orderDirection57 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection57.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b62 == false);
    }

    @Test
    public void test172() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test172");
        float[] f_array5 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array7 = new float[] { (-1074790400) };
        boolean b8 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array5, f_array7);
        float[] f_array14 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array16 = new float[] { (-1074790400) };
        boolean b17 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array14, f_array16);
        boolean b18 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array7, f_array14);
        float[] f_array24 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array26 = new float[] { (-1074790400) };
        boolean b27 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array24, f_array26);
        float[] f_array33 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array35 = new float[] { (-1074790400) };
        boolean b36 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array33, f_array35);
        boolean b37 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array26, f_array33);
        boolean b38 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array7, f_array33);
        float[] f_array44 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array46 = new float[] { (-1074790400) };
        boolean b47 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array44, f_array46);
        float[] f_array53 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array55 = new float[] { (-1074790400) };
        boolean b56 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array53, f_array55);
        float[] f_array62 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array64 = new float[] { (-1074790400) };
        boolean b65 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array62, f_array64);
        boolean b66 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array55, f_array62);
        float[] f_array72 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array74 = new float[] { (-1074790400) };
        boolean b75 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array72, f_array74);
        float[] f_array81 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array83 = new float[] { (-1074790400) };
        boolean b84 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array81, f_array83);
        boolean b85 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array74, f_array81);
        boolean b86 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array55, f_array81);
        boolean b87 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array46, f_array81);
        boolean b88 = org.apache.commons.math.util.MathUtils.equals(f_array7, f_array81);
        org.junit.Assert.assertNotNull(f_array5);
        org.junit.Assert.assertNotNull(f_array7);
        org.junit.Assert.assertTrue(b8 == false);
        org.junit.Assert.assertNotNull(f_array14);
        org.junit.Assert.assertNotNull(f_array16);
        org.junit.Assert.assertTrue(b17 == false);
        org.junit.Assert.assertTrue(b18 == false);
        org.junit.Assert.assertNotNull(f_array24);
        org.junit.Assert.assertNotNull(f_array26);
        org.junit.Assert.assertTrue(b27 == false);
        org.junit.Assert.assertNotNull(f_array33);
        org.junit.Assert.assertNotNull(f_array35);
        org.junit.Assert.assertTrue(b36 == false);
        org.junit.Assert.assertTrue(b37 == false);
        org.junit.Assert.assertTrue(b38 == false);
        org.junit.Assert.assertNotNull(f_array44);
        org.junit.Assert.assertNotNull(f_array46);
        org.junit.Assert.assertTrue(b47 == false);
        org.junit.Assert.assertNotNull(f_array53);
        org.junit.Assert.assertNotNull(f_array55);
        org.junit.Assert.assertTrue(b56 == false);
        org.junit.Assert.assertNotNull(f_array62);
        org.junit.Assert.assertNotNull(f_array64);
        org.junit.Assert.assertTrue(b65 == false);
        org.junit.Assert.assertTrue(b66 == false);
        org.junit.Assert.assertNotNull(f_array72);
        org.junit.Assert.assertNotNull(f_array74);
        org.junit.Assert.assertTrue(b75 == false);
        org.junit.Assert.assertNotNull(f_array81);
        org.junit.Assert.assertNotNull(f_array83);
        org.junit.Assert.assertTrue(b84 == false);
        org.junit.Assert.assertTrue(b85 == false);
        org.junit.Assert.assertTrue(b86 == false);
        org.junit.Assert.assertTrue(b87 == false);
        org.junit.Assert.assertTrue(b88 == false);
    }

    @Test
    public void test173() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test173");
        double d3 = org.apache.commons.math.util.MathUtils.round((double) (-1636086454L), 42, (int) (byte) 0);
        org.junit.Assert.assertTrue(d3 == (-1.636086454E9d));
    }

    @Test
    public void test174() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test174");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) 1.0E-9f, (double) 23183L, 3);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test175() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test175");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.tanh();
        org.apache.commons.math.complex.Complex complex4 = new org.apache.commons.math.complex.Complex((double) 0.0f, 0.0d);
        org.apache.commons.math.complex.Complex complex5 = complex0.subtract(complex4);
        org.apache.commons.math.complex.Complex complex7 = complex4.multiply((double) (-447362047));
        org.apache.commons.math.complex.Complex complex8 = complex4.asin();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
    }

    @Test
    public void test176() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test176");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) (short) 100, (java.lang.Number) 10.04987562112089d, true);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext5 = numberIsTooSmallException4.getContext();
        org.junit.Assert.assertNotNull(exceptionContext5);
    }

    @Test
    public void test177() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test177");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 362880L, (-1.0d), (double) 97L);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test178() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test178");
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        java.lang.Object[] obj_array3 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException4 = new org.apache.commons.math.exception.NotFiniteNumberException((java.lang.Number) 100.0f, obj_array3);
        java.lang.Throwable[] throwable_array5 = notFiniteNumberException4.getSuppressed();
        org.apache.commons.math.exception.MathIllegalArgumentException mathIllegalArgumentException6 = new org.apache.commons.math.exception.MathIllegalArgumentException(localizable1, (java.lang.Object[]) throwable_array5);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException7 = new org.apache.commons.math.exception.NotFiniteNumberException((java.lang.Number) 1074790400, (java.lang.Object[]) throwable_array5);
        org.junit.Assert.assertNotNull(throwable_array5);
    }

    @Test
    public void test179() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test179");
        long long2 = org.apache.commons.math.util.MathUtils.gcd((long) (-9), (long) (-738962753));
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test180() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test180");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.acos();
        org.apache.commons.math.complex.Complex complex3 = complex0.multiply((double) (-447362044));
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex3);
    }

    @Test
    public void test181() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test181");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 20, 0);
    }

    @Test
    public void test182() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test182");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = complex4.log();
        double d6 = complex4.abs();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex8 = complex7.cosh();
        double d9 = complex8.getReal();
        org.apache.commons.math.complex.Complex complex10 = complex8.cos();
        org.apache.commons.math.complex.Complex complex11 = complex10.sinh();
        org.apache.commons.math.complex.Complex complex12 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex13 = complex12.cos();
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex15 = complex14.cos();
        org.apache.commons.math.complex.Complex complex16 = complex12.subtract(complex14);
        org.apache.commons.math.complex.Complex complex17 = complex10.subtract(complex16);
        org.apache.commons.math.complex.Complex complex18 = complex4.divide(complex10);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertEquals((double) d6, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertTrue(d9 == 1.0d);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
    }

    @Test
    public void test183() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test183");
        int i2 = org.apache.commons.math.util.MathUtils.lcm(1100, 33);
        org.junit.Assert.assertTrue(i2 == 3300);
    }

    @Test
    public void test184() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test184");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(11013.232874703393d, 99.70309990644455d, 1.5707963267948966d);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test185() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test185");
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
        double[] d_array54 = org.apache.commons.math.util.MathUtils.copyOf(d_array5, 0);
        int i55 = org.apache.commons.math.util.MathUtils.hash(d_array54);
        double d56 = org.apache.commons.math.util.MathUtils.safeNorm(d_array54);
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
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertTrue(i55 == 1);
        org.junit.Assert.assertTrue(d56 == 0.0d);
    }

    @Test
    public void test186() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test186");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(0.0d, 1.0066581708938198d);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test187() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test187");
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
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex24);
        org.apache.commons.math.complex.Complex complex30 = complex24.multiply((double) 1.0f);
        org.apache.commons.math.complex.Complex complex31 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex32 = complex31.cos();
        org.apache.commons.math.complex.Complex complex33 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex34 = complex33.cos();
        org.apache.commons.math.complex.Complex complex35 = complex31.subtract(complex33);
        org.apache.commons.math.complex.Complex complex36 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex37 = complex36.cos();
        org.apache.commons.math.complex.Complex complex38 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex39 = complex38.cos();
        org.apache.commons.math.complex.Complex complex40 = complex36.subtract(complex38);
        org.apache.commons.math.complex.Complex complex41 = complex31.add(complex40);
        double d42 = complex31.getArgument();
        org.apache.commons.math.complex.Complex complex43 = complex31.exp();
        org.apache.commons.math.complex.Complex complex44 = complex31.negate();
        org.apache.commons.math.complex.Complex complex45 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex46 = complex45.cos();
        org.apache.commons.math.complex.Complex complex47 = complex31.subtract(complex45);
        org.apache.commons.math.complex.Complex complex48 = complex47.cosh();
        double d49 = complex47.getReal();
        org.apache.commons.math.complex.Complex complex50 = complex47.tanh();
        boolean b51 = complex24.equals((java.lang.Object) complex50);
        double d52 = complex24.getReal();
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
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(complex34);
        org.junit.Assert.assertNotNull(complex35);
        org.junit.Assert.assertNotNull(complex36);
        org.junit.Assert.assertNotNull(complex37);
        org.junit.Assert.assertNotNull(complex38);
        org.junit.Assert.assertNotNull(complex39);
        org.junit.Assert.assertNotNull(complex40);
        org.junit.Assert.assertNotNull(complex41);
        org.junit.Assert.assertEquals((double) d42, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex43);
        org.junit.Assert.assertNotNull(complex44);
        org.junit.Assert.assertNotNull(complex45);
        org.junit.Assert.assertNotNull(complex46);
        org.junit.Assert.assertNotNull(complex47);
        org.junit.Assert.assertNotNull(complex48);
        org.junit.Assert.assertEquals((double) d49, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex50);
        org.junit.Assert.assertTrue(b51 == false);
        org.junit.Assert.assertTrue(d52 == 0.0d);
    }

    @Test
    public void test188() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test188");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex6 = complex2.nthRoot((int) (short) 10);
        double d7 = complex2.abs();
        org.apache.commons.math.complex.ComplexField complexField8 = complex2.getField();
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex2);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(list_complex6);
        org.junit.Assert.assertEquals((double) d7, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complexField8);
    }

    @Test
    public void test189() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test189");
        long long2 = org.apache.commons.math.util.MathUtils.lcm(9L, (long) '#');
        org.junit.Assert.assertTrue(long2 == 315L);
    }

    @Test
    public void test190() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test190");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) (short) 0, (java.lang.Number) 1636086544L, 19683);
    }

    @Test
    public void test191() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest2.test191");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        double[] d_array5 = new double[] { (-1.0f) };
        double[] d_array7 = org.apache.commons.math.util.MathUtils.copyOf(d_array5, 1);
        double[] d_array9 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array7, (double) 0L);
        double[] d_array10 = org.apache.commons.math.util.MathUtils.copyOf(d_array7);
        double[] d_array12 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array7, 349.9541180407703d);
        double[] d_array14 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array12, 0.0d);
        double[] d_array16 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array12, (double) 6336994428L);
        double d17 = org.apache.commons.math.util.MathUtils.distanceInf(d_array1, d_array16);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array1);
        double[] d_array20 = new double[] { (-1.0f) };
        double[] d_array22 = org.apache.commons.math.util.MathUtils.copyOf(d_array20, 1);
        double d23 = org.apache.commons.math.util.MathUtils.safeNorm(d_array20);
        double[] d_array25 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array20, 1.1752011936438014d);
        double[] d_array26 = org.apache.commons.math.util.MathUtils.copyOf(d_array20);
        double[] d_array27 = org.apache.commons.math.util.MathUtils.copyOf(d_array26);
        double d28 = org.apache.commons.math.util.MathUtils.distanceInf(d_array1, d_array27);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array14);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertTrue(d17 == 6.336994429E9d);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertTrue(d23 == 1.0d);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array26);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertTrue(d28 == 0.0d);
    }
}

