import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest1 {

    public static boolean debug = false;

    @Test
    public void test001() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test001");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex0.asin();
        org.apache.commons.math.complex.Complex complex3 = complex2.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex2.log();
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex5.cos();
        java.lang.String str7 = complex6.toString();
        org.apache.commons.math.complex.Complex complex8 = complex6.sqrt1z();
        org.apache.commons.math.complex.Complex complex9 = complex6.sqrt();
        org.apache.commons.math.complex.Complex complex10 = complex2.pow(complex9);
        org.apache.commons.math.complex.Complex complex11 = complex2.log();
        org.apache.commons.math.complex.Complex complex12 = complex2.sinh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertTrue("'" + str7 + "' != '" + "(NaN, NaN)" + "'", str7.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
    }

    @Test
    public void test002() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test002");
        float f1 = org.apache.commons.math.util.MathUtils.indicator(Float.NaN);
        org.junit.Assert.assertEquals((float) f1, Float.NaN, 0);
    }

    @Test
    public void test003() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test003");
        try {
            double d1 = org.apache.commons.math.util.MathUtils.factorialDouble((-1));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test004() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test004");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i4 = nonMonotonousSequenceException3.getIndex();
        java.lang.Number number5 = nonMonotonousSequenceException3.getPrevious();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException9 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection10 = nonMonotonousSequenceException9.getDirection();
        int i11 = nonMonotonousSequenceException9.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable12 = null;
        java.lang.Object[] obj_array14 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException15 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable12, (java.lang.Number) (short) 100, obj_array14);
        java.lang.Number number16 = notFiniteNumberException15.getArgument();
        nonMonotonousSequenceException9.addSuppressed((java.lang.Throwable) notFiniteNumberException15);
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException15);
        org.junit.Assert.assertTrue(i4 == 97);
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + 2.2250738585072014E-308d + "'", number5.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + orderDirection10 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection10.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i11 == 97);
        org.junit.Assert.assertTrue("'" + number16 + "' != '" + (short) 100 + "'", number16.equals((short) 100));
    }

    @Test
    public void test005() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test005");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 42, 99.70309990644455d, 1074790400);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test006() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test006");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck(1, (int) (byte) 10);
        org.junit.Assert.assertTrue(i2 == (-9));
    }

    @Test
    public void test007() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test007");
        double d1 = org.apache.commons.math.util.MathUtils.cosh((double) 0L);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test008() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test008");
        int i1 = org.apache.commons.math.util.MathUtils.indicator((int) (short) 100);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test009() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test009");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) -1);
    }

    @Test
    public void test010() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test010");
        float f1 = org.apache.commons.math.util.MathUtils.sign(10.0f);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test011() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test011");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        java.lang.Throwable[] throwable_array5 = nonMonotonousSequenceException3.getSuppressed();
        boolean b6 = nonMonotonousSequenceException3.getStrict();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertNotNull(throwable_array5);
        org.junit.Assert.assertTrue(b6 == true);
    }

    @Test
    public void test012() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test012");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(98, (int) (byte) 1);
        org.junit.Assert.assertTrue(d2 == 98.0d);
    }

    @Test
    public void test013() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test013");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array7 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i8 = org.apache.commons.math.util.MathUtils.distanceInf(i_array0, i_array7);
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array0, 1);
        int[] i_array11 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertTrue(i8 == 0);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertNotNull(i_array11);
    }

    @Test
    public void test014() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test014");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.exp();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
    }

    @Test
    public void test015() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test015");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 10.04987562112089d, (java.lang.Number) (-1), true);
        java.lang.Number number4 = numberIsTooLargeException3.getMax();
        java.lang.Number number5 = numberIsTooLargeException3.getMax();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + (-1) + "'", number4.equals((-1)));
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + (-1) + "'", number5.equals((-1)));
    }

    @Test
    public void test016() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test016");
        int i2 = org.apache.commons.math.util.MathUtils.gcd((int) (byte) 10, 0);
        org.junit.Assert.assertTrue(i2 == 10);
    }

    @Test
    public void test017() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test017");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException((int) (short) 0, 1);
        int i3 = dimensionMismatchException2.getDimension();
        java.lang.Throwable[] throwable_array4 = dimensionMismatchException2.getSuppressed();
        org.junit.Assert.assertTrue(i3 == 1);
        org.junit.Assert.assertNotNull(throwable_array4);
    }

    @Test
    public void test018() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test018");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.exp();
        org.apache.commons.math.complex.Complex complex3 = complex1.tanh();
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex5 = complex4.cosh();
        double d6 = complex4.abs();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex8 = complex7.acos();
        org.apache.commons.math.complex.Complex complex9 = complex4.divide(complex7);
        org.apache.commons.math.complex.Complex complex10 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex11 = complex10.cos();
        org.apache.commons.math.complex.Complex complex12 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex13 = complex12.cos();
        org.apache.commons.math.complex.Complex complex14 = complex10.subtract(complex12);
        org.apache.commons.math.complex.Complex complex15 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex16 = complex15.cos();
        org.apache.commons.math.complex.Complex complex17 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex18 = complex17.cos();
        org.apache.commons.math.complex.Complex complex19 = complex15.subtract(complex17);
        org.apache.commons.math.complex.Complex complex20 = complex10.add(complex19);
        double d21 = complex10.getArgument();
        org.apache.commons.math.complex.Complex complex22 = complex10.exp();
        org.apache.commons.math.complex.Complex complex23 = complex10.negate();
        org.apache.commons.math.complex.Complex complex24 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex25 = complex24.cos();
        org.apache.commons.math.complex.Complex complex26 = complex10.subtract(complex24);
        org.apache.commons.math.complex.Complex complex27 = complex26.cosh();
        org.apache.commons.math.complex.Complex complex28 = complex9.multiply(complex26);
        org.apache.commons.math.complex.Complex complex29 = complex1.divide(complex28);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertTrue(d6 == 0.0d);
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
        org.junit.Assert.assertEquals((double) d21, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complex24);
        org.junit.Assert.assertNotNull(complex25);
        org.junit.Assert.assertNotNull(complex26);
        org.junit.Assert.assertNotNull(complex27);
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertNotNull(complex29);
    }

    @Test
    public void test019() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test019");
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
        org.apache.commons.math.complex.Complex complex15 = complex13.multiply(7.930067261567154E14d);
        java.lang.String str16 = complex15.toString();
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
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertTrue("'" + str16 + "' != '" + "(NaN, NaN)" + "'", str16.equals("(NaN, NaN)"));
    }

    @Test
    public void test020() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test020");
        double d1 = org.apache.commons.math.util.MathUtils.factorialLog(1);
        org.junit.Assert.assertTrue(d1 == 0.0d);
    }

    @Test
    public void test021() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test021");
        double d2 = org.apache.commons.math.util.MathUtils.round(0.035056111946177054d, (int) (byte) -1);
        org.junit.Assert.assertTrue(d2 == 0.0d);
    }

    @Test
    public void test022() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test022");
        int i2 = org.apache.commons.math.util.MathUtils.lcm(22, (int) (short) 100);
        org.junit.Assert.assertTrue(i2 == 1100);
    }

    @Test
    public void test023() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test023");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException(9, 1072693248);
        int i3 = dimensionMismatchException2.getDimension();
        java.lang.String str4 = dimensionMismatchException2.toString();
        org.junit.Assert.assertTrue(i3 == 1072693248);
        org.junit.Assert.assertTrue("'" + str4 + "' != '" + "org.apache.commons.math.exception.DimensionMismatchException: 9 != 1,072,693,248" + "'", str4.equals("org.apache.commons.math.exception.DimensionMismatchException: 9 != 1,072,693,248"));
    }

    @Test
    public void test024() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test024");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array9 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array11 = org.apache.commons.math.util.MathUtils.copyOf(i_array9, (int) '4');
        double d12 = org.apache.commons.math.util.MathUtils.distance(i_array0, i_array9);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array9);
        org.junit.Assert.assertNotNull(i_array11);
        org.junit.Assert.assertTrue(d12 == 0.0d);
    }

    @Test
    public void test025() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test025");
        int[] i_array6 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array8 = org.apache.commons.math.util.MathUtils.copyOf(i_array6, (int) '4');
        int[] i_array9 = new int[] {};
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array9);
        int[] i_array16 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i17 = org.apache.commons.math.util.MathUtils.distanceInf(i_array9, i_array16);
        int[] i_array19 = org.apache.commons.math.util.MathUtils.copyOf(i_array9, 1);
        try {
            int i20 = org.apache.commons.math.util.MathUtils.distance1(i_array6, i_array9);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array6);
        org.junit.Assert.assertNotNull(i_array8);
        org.junit.Assert.assertNotNull(i_array9);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertTrue(i17 == 0);
        org.junit.Assert.assertNotNull(i_array19);
    }

    @Test
    public void test026() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test026");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble(9, 2);
        org.junit.Assert.assertTrue(d2 == 36.0d);
    }

    @Test
    public void test027() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test027");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 9, (-1074790369));
        int i4 = dimensionMismatchException3.getDimension();
        org.junit.Assert.assertTrue(i4 == (-1074790369));
    }

    @Test
    public void test028() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test028");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        java.lang.Number number4 = nonMonotonousSequenceException3.getPrevious();
        java.lang.Number number5 = nonMonotonousSequenceException3.getPrevious();
        java.lang.Number number6 = nonMonotonousSequenceException3.getPrevious();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 2.2250738585072014E-308d + "'", number4.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + 2.2250738585072014E-308d + "'", number5.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + number6 + "' != '" + 2.2250738585072014E-308d + "'", number6.equals(2.2250738585072014E-308d));
    }

    @Test
    public void test029() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test029");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.exp();
        org.apache.commons.math.complex.Complex complex3 = complex2.sqrt();
        org.apache.commons.math.complex.Complex complex4 = complex2.cos();
        double d5 = complex4.getReal();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertTrue(d5 == (-0.9117339147869651d));
    }

    @Test
    public void test030() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test030");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b7 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection4, true, false);
        double[] d_array11 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d12 = org.apache.commons.math.util.MathUtils.safeNorm(d_array11);
        int i13 = org.apache.commons.math.util.MathUtils.hash(d_array11);
        boolean b14 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array11);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException18 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i19 = nonMonotonousSequenceException18.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection20 = nonMonotonousSequenceException18.getDirection();
        try {
            boolean b23 = org.apache.commons.math.util.MathUtils.checkOrder(d_array11, orderDirection20, false, true);
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
        org.junit.Assert.assertTrue(i19 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection20 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection20.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test031() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test031");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.tan();
        double d7 = complex6.getReal();
        double d8 = complex6.getImaginary();
        double d9 = complex6.getReal();
        try {
            java.util.List<org.apache.commons.math.complex.Complex> list_complex11 = complex6.nthRoot(0);
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
        org.junit.Assert.assertEquals((double) d7, Double.NaN, 0);
        org.junit.Assert.assertEquals((double) d8, Double.NaN, 0);
        org.junit.Assert.assertEquals((double) d9, Double.NaN, 0);
    }

    @Test
    public void test032() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test032");
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
        org.apache.commons.math.complex.Complex complex15 = complex0.sqrt();
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
    public void test033() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test033");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) 42);
    }

    @Test
    public void test034() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test034");
        double d1 = org.apache.commons.math.util.MathUtils.sinh(4.809637984124462E151d);
        org.junit.Assert.assertTrue(d1 == Double.POSITIVE_INFINITY);
    }

    @Test
    public void test035() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test035");
        int i1 = org.apache.commons.math.util.MathUtils.indicator((int) (byte) 1);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test036() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test036");
        int i2 = org.apache.commons.math.util.MathUtils.gcd(100, (-9));
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test037() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test037");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 98, (double) (-1636086455L), (double) (short) 1);
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test038() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test038");
        org.apache.commons.math.util.MathUtils.checkFinite(10.0d);
    }

    @Test
    public void test039() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test039");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = complex4.log();
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex7 = complex6.cosh();
        org.apache.commons.math.complex.Complex complex8 = complex6.asin();
        org.apache.commons.math.complex.Complex complex9 = complex4.multiply(complex6);
        org.apache.commons.math.complex.Complex complex10 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex11 = complex10.cos();
        org.apache.commons.math.complex.Complex complex12 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex13 = complex12.cos();
        org.apache.commons.math.complex.Complex complex14 = complex10.subtract(complex12);
        org.apache.commons.math.complex.Complex complex15 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex16 = complex15.cos();
        org.apache.commons.math.complex.Complex complex17 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex18 = complex17.cos();
        org.apache.commons.math.complex.Complex complex19 = complex15.subtract(complex17);
        org.apache.commons.math.complex.Complex complex20 = complex10.add(complex19);
        double d21 = complex10.getImaginary();
        org.apache.commons.math.complex.Complex complex22 = complex6.pow(complex10);
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
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertEquals((double) d21, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex22);
    }

    @Test
    public void test040() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test040");
        double[] d_array0 = null;
        try {
            double[] d_array2 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array0, (double) 1.0f);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test041() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test041");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck(6336994428L, 0L);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test042() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test042");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        java.math.BigInteger bigInteger3 = null;
        try {
            java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, bigInteger3);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger2);
    }

    @Test
    public void test043() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test043");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = new int[] {};
        int[] i_array3 = org.apache.commons.math.util.MathUtils.copyOf(i_array2);
        int[] i_array9 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i10 = org.apache.commons.math.util.MathUtils.distanceInf(i_array2, i_array9);
        int[] i_array12 = org.apache.commons.math.util.MathUtils.copyOf(i_array2, 1);
        int i13 = org.apache.commons.math.util.MathUtils.distance1(i_array0, i_array2);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array9);
        org.junit.Assert.assertTrue(i10 == 0);
        org.junit.Assert.assertNotNull(i_array12);
        org.junit.Assert.assertTrue(i13 == 0);
    }

    @Test
    public void test044() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test044");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        try {
            java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (-9));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger2);
    }

    @Test
    public void test045() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test045");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) (byte) 10, (java.lang.Number) Double.NaN, false);
    }

    @Test
    public void test046() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test046");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.asin();
        double d2 = complex0.abs();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertEquals((double) d2, Double.NaN, 0);
    }

    @Test
    public void test047() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test047");
        double d2 = org.apache.commons.math.util.MathUtils.log(409.0515645945562d, 0.035056111946177054d);
        org.junit.Assert.assertTrue(d2 == (-0.5571822032850108d));
    }

    @Test
    public void test048() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test048");
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
        org.apache.commons.math.complex.Complex complex12 = complex0.conjugate();
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
    }

    @Test
    public void test049() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test049");
        long long2 = org.apache.commons.math.util.MathUtils.pow(6336994428L, (long) 3);
        org.junit.Assert.assertTrue(long2 == (-9148717644917614656L));
    }

    @Test
    public void test050() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test050");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) (-1074790400), (java.lang.Number) 35.0d, false);
        java.lang.Number number4 = numberIsTooSmallException3.getMin();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 35.0d + "'", number4.equals(35.0d));
    }

    @Test
    public void test051() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test051");
        try {
            double d3 = org.apache.commons.math.util.MathUtils.round(0.0d, (int) (short) 0, 1075970048);
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @Test
    public void test052() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test052");
        int i2 = org.apache.commons.math.util.MathUtils.gcd(3, 0);
        org.junit.Assert.assertTrue(i2 == 3);
    }

    @Test
    public void test053() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test053");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck(2, (int) (short) -1);
        org.junit.Assert.assertTrue(i2 == 3);
    }

    @Test
    public void test054() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test054");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException((-9), 11);
        int i3 = dimensionMismatchException2.getDimension();
        org.junit.Assert.assertTrue(i3 == 11);
    }

    @Test
    public void test055() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test055");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 3.6288E8f, 349.9541180407703d);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test056() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test056");
        double d1 = org.apache.commons.math.util.MathUtils.factorialDouble((int) (byte) 1);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test057() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test057");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck(9, 0);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test058() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test058");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck(0L, (-8L));
        org.junit.Assert.assertTrue(long2 == (-8L));
    }

    @Test
    public void test059() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test059");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Object[] obj_array1 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NullArgumentException nullArgumentException2 = new org.apache.commons.math.exception.NullArgumentException(localizable0, obj_array1);
        try {
            org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) localizable0);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(obj_array1);
    }

    @Test
    public void test060() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test060");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.INF;
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
        org.apache.commons.math.complex.Complex complex14 = complex0.multiply(complex13);
        org.apache.commons.math.complex.ComplexField complexField15 = complex0.getField();
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
        org.junit.Assert.assertNotNull(complexField15);
    }

    @Test
    public void test061() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test061");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 3500L, (float) 9, (-2112331476));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test062() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test062");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        java.lang.String str7 = complex1.toString();
        double d8 = complex1.abs();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertTrue("'" + str7 + "' != '" + "(NaN, NaN)" + "'", str7.equals("(NaN, NaN)"));
        org.junit.Assert.assertEquals((double) d8, Double.NaN, 0);
    }

    @Test
    public void test063() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test063");
        double d2 = org.apache.commons.math.util.MathUtils.log((double) 42, (double) 1);
        org.junit.Assert.assertTrue(d2 == 0.0d);
    }

    @Test
    public void test064() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test064");
        short s1 = org.apache.commons.math.util.MathUtils.sign((short) -1);
        org.junit.Assert.assertTrue(s1 == (short) -1);
    }

    @Test
    public void test065() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test065");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (byte) 10);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext2 = notPositiveException1.getContext();
        org.junit.Assert.assertNotNull(exceptionContext2);
    }

    @Test
    public void test066() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test066");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) 1.63608653E9f);
    }

    @Test
    public void test067() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test067");
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
        org.apache.commons.math.complex.Complex complex28 = complex24.asin();
        org.apache.commons.math.complex.Complex complex29 = complex28.acos();
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
    }

    @Test
    public void test068() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test068");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((float) 10L, (float) (short) -1);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test069() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test069");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.exp();
        org.apache.commons.math.complex.Complex complex3 = complex2.sqrt();
        org.apache.commons.math.complex.Complex complex4 = complex3.exp();
        double d5 = complex3.abs();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertTrue(d5 == 1.6487212707001282d);
    }

    @Test
    public void test070() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test070");
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
        org.apache.commons.math.complex.Complex complex20 = complex16.atan();
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
    public void test071() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test071");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = complex1.acos();
        org.apache.commons.math.complex.Complex complex4 = complex3.sin();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
    }

    @Test
    public void test072() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test072");
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
        double[] d_array41 = org.apache.commons.math.util.MathUtils.copyOf(d_array0, 0);
        double[] d_array43 = new double[] { (byte) -1 };
        double[] d_array45 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array43, (double) (byte) 1);
        boolean b46 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array41, d_array45);
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
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertTrue(b46 == false);
    }

    @Test
    public void test073() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test073");
        int i2 = org.apache.commons.math.util.MathUtils.pow(9, (long) (byte) 1);
        org.junit.Assert.assertTrue(i2 == 9);
    }

    @Test
    public void test074() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test074");
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
        int i61 = org.apache.commons.math.util.MathUtils.hash(d_array0);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException68 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection69 = nonMonotonousSequenceException68.getDirection();
        int i70 = nonMonotonousSequenceException68.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable71 = null;
        java.lang.Object[] obj_array73 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException74 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable71, (java.lang.Number) (short) 100, obj_array73);
        java.lang.Number number75 = notFiniteNumberException74.getArgument();
        nonMonotonousSequenceException68.addSuppressed((java.lang.Throwable) notFiniteNumberException74);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection77 = nonMonotonousSequenceException68.getDirection();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException79 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) (short) 10, (java.lang.Number) 10.0d, (int) '#', orderDirection77, false);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection77, true);
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
        org.junit.Assert.assertTrue(i60 == 1);
        org.junit.Assert.assertTrue(i61 == 1);
        org.junit.Assert.assertTrue("'" + orderDirection69 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection69.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i70 == 97);
        org.junit.Assert.assertTrue("'" + number75 + "' != '" + (short) 100 + "'", number75.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection77 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection77.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test075() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test075");
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
        org.apache.commons.math.complex.Complex complex20 = complex16.sqrt1z();
        org.apache.commons.math.complex.Complex complex21 = complex16.negate();
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
    }

    @Test
    public void test076() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test076");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals(0.0f, (float) 3);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test077() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test077");
        double d1 = org.apache.commons.math.util.MathUtils.factorialDouble(22);
        org.junit.Assert.assertTrue(d1 == 1.1240007277776115E21d);
    }

    @Test
    public void test078() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test078");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.pow(10L, (long) (-1074790369));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test079() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test079");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient(42, (-1074790400));
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test080() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test080");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient(33, (int) (byte) 10);
        org.junit.Assert.assertTrue(long2 == 92561040L);
    }

    @Test
    public void test081() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test081");
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
        org.apache.commons.math.util.MathUtils.checkFinite(d_array22);
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
    public void test082() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test082");
        byte byte1 = org.apache.commons.math.util.MathUtils.indicator((byte) 100);
        org.junit.Assert.assertTrue(byte1 == (byte) 1);
    }

    @Test
    public void test083() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test083");
        double d1 = org.apache.commons.math.util.MathUtils.sign((double) 3L);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test084() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test084");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException4 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection5 = nonMonotonousSequenceException4.getDirection();
        java.lang.Throwable[] throwable_array6 = nonMonotonousSequenceException4.getSuppressed();
        org.apache.commons.math.exception.NullArgumentException nullArgumentException7 = new org.apache.commons.math.exception.NullArgumentException(localizable0, (java.lang.Object[]) throwable_array6);
        org.junit.Assert.assertTrue("'" + orderDirection5 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection5.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertNotNull(throwable_array6);
    }

    @Test
    public void test085() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test085");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b7 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection4, true, false);
        double[] d_array11 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d12 = org.apache.commons.math.util.MathUtils.safeNorm(d_array11);
        int i13 = org.apache.commons.math.util.MathUtils.hash(d_array11);
        boolean b14 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array11);
        try {
            double[] d_array16 = org.apache.commons.math.util.MathUtils.copyOf(d_array11, (-2064878497));
            org.junit.Assert.fail("Expected exception of type java.lang.NegativeArraySizeException");
        } catch (java.lang.NegativeArraySizeException e) {
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
    public void test086() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test086");
        try {
            double d3 = org.apache.commons.math.util.MathUtils.round((double) 100, 1075970048, (int) (short) 10);
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @Test
    public void test087() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test087");
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
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection59 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException61 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) Double.POSITIVE_INFINITY, (java.lang.Number) 1.0f, 0, orderDirection59, true);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException63 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 10.0f, (java.lang.Number) 0, 1075970048, orderDirection59, true);
        org.apache.commons.math.exception.util.Localizable localizable64 = null;
        double[] d_array65 = new double[] {};
        double[] d_array69 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array73 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array77 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array81 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array82 = new double[][] { d_array69, d_array73, d_array77, d_array81 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array65, d_array_array82);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException84 = new org.apache.commons.math.exception.NullArgumentException(localizable64, (java.lang.Object[]) d_array_array82);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, orderDirection59, d_array_array82);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection86 = null;
        try {
            boolean b89 = org.apache.commons.math.util.MathUtils.checkOrder(d_array5, orderDirection86, true, true);
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
        org.junit.Assert.assertTrue("'" + orderDirection59 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection59.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertNotNull(d_array65);
        org.junit.Assert.assertNotNull(d_array69);
        org.junit.Assert.assertNotNull(d_array73);
        org.junit.Assert.assertNotNull(d_array77);
        org.junit.Assert.assertNotNull(d_array81);
        org.junit.Assert.assertNotNull(d_array_array82);
    }

    @Test
    public void test088() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test088");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 97, (double) 33, (int) ' ');
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test089() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test089");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) 3.6288E8f, (java.lang.Number) 36.0d, true);
    }

    @Test
    public void test090() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test090");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck(1636086465, (-2064878497));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test091() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test091");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) 7766279631452241920L);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test092() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test092");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException((int) (byte) 1, (int) ' ');
    }

    @Test
    public void test093() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test093");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.tan();
        boolean b7 = complex6.isInfinite();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertTrue(b7 == false);
    }

    @Test
    public void test094() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test094");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) '#', (long) (byte) 100);
        org.junit.Assert.assertTrue(long2 == 8170176069297290577L);
    }

    @Test
    public void test095() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test095");
        long long1 = org.apache.commons.math.util.MathUtils.factorial(9);
        org.junit.Assert.assertTrue(long1 == 362880L);
    }

    @Test
    public void test096() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test096");
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
        org.apache.commons.math.complex.Complex complex29 = complex24.multiply(2.2250738585072014E-308d);
        org.apache.commons.math.complex.Complex complex30 = complex24.conjugate();
        double d31 = complex30.getImaginary();
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
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertTrue(d31 == (-0.0d));
    }

    @Test
    public void test097() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test097");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow(98, (-9148717644917614656L));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test098() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test098");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = null;
        try {
            org.apache.commons.math.complex.Complex complex5 = complex1.add(complex4);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
    }

    @Test
    public void test099() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test099");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 1, (double) 1.63608653E9f, 4950.0d);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test100() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test100");
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
        org.apache.commons.math.complex.Complex complex29 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex30 = complex29.cos();
        org.apache.commons.math.complex.Complex complex31 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex32 = complex31.cos();
        org.apache.commons.math.complex.Complex complex33 = complex29.subtract(complex31);
        org.apache.commons.math.complex.Complex complex34 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex35 = complex34.cos();
        org.apache.commons.math.complex.Complex complex36 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex37 = complex36.cos();
        org.apache.commons.math.complex.Complex complex38 = complex34.subtract(complex36);
        org.apache.commons.math.complex.Complex complex39 = complex29.add(complex38);
        double d40 = complex29.getArgument();
        org.apache.commons.math.complex.Complex complex41 = complex29.exp();
        org.apache.commons.math.complex.Complex complex42 = complex29.negate();
        org.apache.commons.math.complex.Complex complex43 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex44 = complex43.cos();
        org.apache.commons.math.complex.Complex complex45 = complex29.subtract(complex43);
        org.apache.commons.math.complex.Complex complex46 = complex45.cosh();
        org.apache.commons.math.complex.Complex complex47 = complex45.tanh();
        org.apache.commons.math.complex.Complex complex48 = complex45.atan();
        org.apache.commons.math.complex.Complex complex49 = complex45.sqrt1z();
        org.apache.commons.math.complex.Complex complex50 = complex0.multiply(complex45);
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
        org.junit.Assert.assertEquals((double) d40, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex41);
        org.junit.Assert.assertNotNull(complex42);
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
    public void test101() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test101");
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
        org.apache.commons.math.complex.Complex complex15 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex16 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex17 = complex15.divide(complex16);
        org.apache.commons.math.complex.Complex complex18 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex19 = complex18.tanh();
        org.apache.commons.math.complex.Complex complex20 = complex17.multiply(complex19);
        org.apache.commons.math.complex.Complex complex21 = complex14.add(complex19);
        org.apache.commons.math.complex.ComplexField complexField22 = complex19.getField();
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
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complexField22);
    }

    @Test
    public void test102() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test102");
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
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex13 = complex11.divide(complex12);
        org.apache.commons.math.complex.Complex complex14 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex15 = complex14.tanh();
        org.apache.commons.math.complex.Complex complex16 = complex13.multiply(complex15);
        org.apache.commons.math.complex.Complex complex18 = complex13.multiply(1.0747903976294017E9d);
        org.apache.commons.math.complex.Complex complex19 = complex10.add(complex13);
        org.apache.commons.math.complex.Complex complex20 = complex10.sqrt();
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
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
    }

    @Test
    public void test103() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test103");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Number number2 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) 3.6288E8f, number2, true);
        java.lang.Number number5 = numberIsTooLargeException4.getMax();
        org.junit.Assert.assertNull(number5);
    }

    @Test
    public void test104() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test104");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex4.divide(complex5);
        org.apache.commons.math.complex.Complex complex7 = complex3.multiply(complex4);
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = complex8.cos();
        java.lang.String str10 = complex9.toString();
        org.apache.commons.math.complex.Complex complex11 = complex9.sqrt1z();
        org.apache.commons.math.complex.Complex complex12 = complex9.sin();
        org.apache.commons.math.complex.Complex complex13 = complex3.add(complex12);
        org.apache.commons.math.complex.Complex complex14 = null;
        try {
            org.apache.commons.math.complex.Complex complex15 = complex12.pow(complex14);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
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
        org.junit.Assert.assertTrue("'" + str10 + "' != '" + "(NaN, NaN)" + "'", str10.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
    }

    @Test
    public void test105() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test105");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle((double) 78L, Double.POSITIVE_INFINITY);
        org.junit.Assert.assertTrue(d2 == Double.POSITIVE_INFINITY);
    }

    @Test
    public void test106() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test106");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow(1072693248, (-10L));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test107() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test107");
        try {
            long long1 = org.apache.commons.math.util.MathUtils.factorial((int) (short) 100);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test108() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test108");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient(100, (-2112331476));
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test109() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test109");
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
        org.apache.commons.math.complex.ComplexField complexField15 = complex0.getField();
        org.apache.commons.math.complex.Complex complex16 = complex0.sqrt();
        org.apache.commons.math.complex.Complex complex17 = complex0.negate();
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
        org.junit.Assert.assertNotNull(complexField15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
    }

    @Test
    public void test110() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test110");
        float f3 = org.apache.commons.math.util.MathUtils.round((float) 362880000L, (int) (byte) 1, (int) (byte) 1);
        org.junit.Assert.assertTrue(f3 == 3.6288E8f);
    }

    @Test
    public void test111() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test111");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 0, (int) (byte) 0);
    }

    @Test
    public void test112() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test112");
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
        try {
            double d42 = org.apache.commons.math.util.MathUtils.distance1(d_array22, d_array41);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
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
    public void test113() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test113");
        double d1 = org.apache.commons.math.util.MathUtils.cosh(0.0d);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test114() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test114");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 78L, (double) (-9148717644917614656L), 9);
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test115() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test115");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(6.283185307179586d, (double) 3);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test116() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test116");
        float f2 = org.apache.commons.math.util.MathUtils.round((float) 3, 1075970048);
        org.junit.Assert.assertEquals((float) f2, Float.NaN, 0);
    }

    @Test
    public void test117() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test117");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.sqrt();
        double d3 = complex2.getReal();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertTrue(d3 == 1.0d);
    }

    @Test
    public void test118() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test118");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = complex4.log();
        double d6 = complex4.abs();
        org.apache.commons.math.complex.Complex complex7 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex8 = complex7.cos();
        java.lang.String str9 = complex8.toString();
        org.apache.commons.math.complex.Complex complex10 = complex8.sqrt1z();
        boolean b11 = complex10.isNaN();
        org.apache.commons.math.complex.Complex complex12 = complex10.tan();
        org.apache.commons.math.complex.Complex complex13 = complex4.multiply(complex12);
        org.apache.commons.math.complex.Complex complex14 = complex4.atan();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertEquals((double) d6, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertTrue("'" + str9 + "' != '" + "(NaN, NaN)" + "'", str9.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertTrue(b11 == true);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
    }

    @Test
    public void test119() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test119");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 3.6288E8f, (java.lang.Number) 36.0d, false);
    }

    @Test
    public void test120() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test120");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 10L, (float) 3L, (-1074790400));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test121() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test121");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals(1.07597005E9f, (float) (short) 0, 1074790400);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test122() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test122");
        long long2 = org.apache.commons.math.util.MathUtils.gcd(8170176069297290577L, (long) 33);
        org.junit.Assert.assertTrue(long2 == 3L);
    }

    @Test
    public void test123() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test123");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        java.lang.String str7 = complex1.toString();
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex10 = complex8.divide(complex9);
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex12 = complex11.tanh();
        org.apache.commons.math.complex.Complex complex13 = complex10.multiply(complex12);
        org.apache.commons.math.complex.Complex complex14 = complex1.divide(complex10);
        org.apache.commons.math.complex.Complex complex16 = complex14.multiply(4.809637984124462E151d);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertTrue("'" + str7 + "' != '" + "(NaN, NaN)" + "'", str7.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertNotNull(complex10);
        org.junit.Assert.assertNotNull(complex11);
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex16);
    }

    @Test
    public void test124() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test124");
        try {
            double d3 = org.apache.commons.math.util.MathUtils.round((double) 3.6288E8f, 9, 1100);
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @Test
    public void test125() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test125");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = new double[] { (-1.0f) };
        double[] d_array7 = org.apache.commons.math.util.MathUtils.copyOf(d_array5, 1);
        double d8 = org.apache.commons.math.util.MathUtils.safeNorm(d_array5);
        double[] d_array10 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array5, 1.1752011936438014d);
        double d11 = org.apache.commons.math.util.MathUtils.distance1(d_array3, d_array5);
        double[] d_array12 = org.apache.commons.math.util.MathUtils.copyOf(d_array5);
        double[] d_array14 = org.apache.commons.math.util.MathUtils.copyOf(d_array12, 2);
        double[] d_array16 = new double[] { (-1.0f) };
        double[] d_array18 = org.apache.commons.math.util.MathUtils.copyOf(d_array16, 1);
        double[] d_array20 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array18, (double) 0L);
        double[] d_array21 = org.apache.commons.math.util.MathUtils.copyOf(d_array18);
        double d22 = org.apache.commons.math.util.MathUtils.safeNorm(d_array21);
        double[] d_array24 = new double[] { (-1.0f) };
        double[] d_array26 = org.apache.commons.math.util.MathUtils.copyOf(d_array24, 1);
        double d27 = org.apache.commons.math.util.MathUtils.safeNorm(d_array24);
        double[] d_array29 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array24, 1.1752011936438014d);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array29);
        double[] d_array34 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d35 = org.apache.commons.math.util.MathUtils.safeNorm(d_array34);
        double[] d_array36 = new double[] {};
        double[] d_array40 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array44 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array48 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array52 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array53 = new double[][] { d_array40, d_array44, d_array48, d_array52 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array36, d_array_array53);
        int i55 = org.apache.commons.math.util.MathUtils.hash(d_array36);
        boolean b56 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array34, d_array36);
        double[] d_array60 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d61 = org.apache.commons.math.util.MathUtils.safeNorm(d_array60);
        double[] d_array62 = new double[] {};
        double[] d_array66 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array70 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array74 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array78 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array79 = new double[][] { d_array66, d_array70, d_array74, d_array78 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array62, d_array_array79);
        int i81 = org.apache.commons.math.util.MathUtils.hash(d_array62);
        boolean b82 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array60, d_array62);
        boolean b83 = org.apache.commons.math.util.MathUtils.equals(d_array36, d_array60);
        double[] d_array85 = org.apache.commons.math.util.MathUtils.copyOf(d_array36, 0);
        boolean b86 = org.apache.commons.math.util.MathUtils.equals(d_array29, d_array36);
        double d87 = org.apache.commons.math.util.MathUtils.distance1(d_array21, d_array29);
        double[] d_array88 = org.apache.commons.math.util.MathUtils.copyOf(d_array21);
        try {
            double d89 = org.apache.commons.math.util.MathUtils.distance1(d_array14, d_array21);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertTrue(d8 == 1.0d);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertTrue(d11 == 0.0d);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array14);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertTrue(d22 == 1.0d);
        org.junit.Assert.assertNotNull(d_array24);
        org.junit.Assert.assertNotNull(d_array26);
        org.junit.Assert.assertTrue(d27 == 1.0d);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array34);
        org.junit.Assert.assertTrue(d35 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertNotNull(d_array44);
        org.junit.Assert.assertNotNull(d_array48);
        org.junit.Assert.assertNotNull(d_array52);
        org.junit.Assert.assertNotNull(d_array_array53);
        org.junit.Assert.assertTrue(i55 == 1);
        org.junit.Assert.assertTrue(b56 == false);
        org.junit.Assert.assertNotNull(d_array60);
        org.junit.Assert.assertTrue(d61 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array62);
        org.junit.Assert.assertNotNull(d_array66);
        org.junit.Assert.assertNotNull(d_array70);
        org.junit.Assert.assertNotNull(d_array74);
        org.junit.Assert.assertNotNull(d_array78);
        org.junit.Assert.assertNotNull(d_array_array79);
        org.junit.Assert.assertTrue(i81 == 1);
        org.junit.Assert.assertTrue(b82 == false);
        org.junit.Assert.assertTrue(b83 == false);
        org.junit.Assert.assertNotNull(d_array85);
        org.junit.Assert.assertTrue(b86 == false);
        org.junit.Assert.assertTrue(d87 == 2.1752011936438014d);
        org.junit.Assert.assertNotNull(d_array88);
    }

    @Test
    public void test126() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test126");
        int i2 = org.apache.commons.math.util.MathUtils.pow(3, (long) 9);
        org.junit.Assert.assertTrue(i2 == 19683);
    }

    @Test
    public void test127() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test127");
        double d3 = org.apache.commons.math.util.MathUtils.reduce(6.283185307179586d, (double) (byte) -1, (double) 1);
        org.junit.Assert.assertTrue(d3 == 0.28318530717958623d);
    }

    @Test
    public void test128() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test128");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array1);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, 1.1752011936438014d);
        double[] d_array7 = org.apache.commons.math.util.MathUtils.copyOf(d_array1);
        double[] d_array8 = org.apache.commons.math.util.MathUtils.copyOf(d_array7);
        int i9 = org.apache.commons.math.util.MathUtils.hash(d_array8);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 1.0d);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertTrue(i9 == (-1074790369));
    }

    @Test
    public void test129() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test129");
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
        float[] f_array25 = new float[] { 78L };
        float[] f_array26 = null;
        boolean b27 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array25, f_array26);
        boolean b28 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array18, f_array26);
        float[] f_array33 = new float[] { 33, (byte) 10, (-73931601660L), 3.6288E8f };
        boolean b34 = org.apache.commons.math.util.MathUtils.equals(f_array18, f_array33);
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
        org.junit.Assert.assertNotNull(f_array25);
        org.junit.Assert.assertTrue(b27 == false);
        org.junit.Assert.assertTrue(b28 == false);
        org.junit.Assert.assertNotNull(f_array33);
        org.junit.Assert.assertTrue(b34 == false);
    }

    @Test
    public void test130() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test130");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (byte) 0, (float) (short) 10, (float) 6544345860L);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test131() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test131");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = null;
        boolean b6 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        try {
            double[] d_array8 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array5, (double) 2);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertTrue(b6 == false);
    }

    @Test
    public void test132() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test132");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 98, (double) 10000000000L, (-2112331476));
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test133() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test133");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        org.apache.commons.math.complex.Complex complex7 = complex6.tanh();
        double d8 = complex6.getImaginary();
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
    public void test134() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test134");
        try {
            double d3 = org.apache.commons.math.util.MathUtils.round(0.0d, 22, 100);
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException e) {
        }
    }

    @Test
    public void test135() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test135");
        long long1 = org.apache.commons.math.util.MathUtils.indicator(3500L);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test136() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test136");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((long) '4', 100L);
        org.junit.Assert.assertTrue(long2 == 1300L);
    }

    @Test
    public void test137() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test137");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.I;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex2 = complex1.cosh();
        org.apache.commons.math.complex.Complex complex3 = complex1.asin();
        org.apache.commons.math.complex.Complex complex4 = complex0.divide(complex3);
        org.apache.commons.math.complex.Complex complex5 = complex3.conjugate();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
    }

    @Test
    public void test138() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test138");
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
        boolean b27 = complex24.isNaN();
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
        org.junit.Assert.assertTrue(b27 == false);
    }

    @Test
    public void test139() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test139");
        try {
            double d1 = org.apache.commons.math.util.MathUtils.factorialLog((-1));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test140() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test140");
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
        org.apache.commons.math.complex.Complex complex13 = complex11.tanh();
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
    }

    @Test
    public void test141() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test141");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck(1074790400, 0);
        org.junit.Assert.assertTrue(i2 == 1074790400);
    }

    @Test
    public void test142() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test142");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 3, (java.lang.Number) 10L, 33);
    }

    @Test
    public void test143() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test143");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient(1075970048, (int) (byte) 0);
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test144() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test144");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round((float) 3628800L, 9, 22);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test145() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test145");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) 6336994428L);
    }

    @Test
    public void test146() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test146");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        java.lang.Object[] obj_array8 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException9 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable6, (java.lang.Number) (short) 100, obj_array8);
        java.lang.Number number10 = notFiniteNumberException9.getArgument();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException9);
        java.lang.Throwable[] throwable_array12 = notFiniteNumberException9.getSuppressed();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number10 + "' != '" + (short) 100 + "'", number10.equals((short) 100));
        org.junit.Assert.assertNotNull(throwable_array12);
    }

    @Test
    public void test147() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test147");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 10L, (java.lang.Number) 7766279631452241920L, false);
        java.lang.Number number4 = numberIsTooSmallException3.getMin();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 7766279631452241920L + "'", number4.equals(7766279631452241920L));
    }

    @Test
    public void test148() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test148");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo(1.0066581708938198d, 100.53096491487338d, 0);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test149() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test149");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) 1, 10000000000L);
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test150() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test150");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 10000000000L, (int) (byte) -1);
        org.junit.Assert.assertTrue(d2 == 1.0E10d);
    }

    @Test
    public void test151() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test151");
        double d1 = org.apache.commons.math.util.MathUtils.sign((double) 1636086543L);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test152() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test152");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow(1074790400, (-1L));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test153() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test153");
        double d3 = org.apache.commons.math.util.MathUtils.reduce((double) 3, 0.0d, 0.0d);
        org.junit.Assert.assertEquals((double) d3, Double.NaN, 0);
    }

    @Test
    public void test154() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test154");
        int[] i_array0 = null;
        int[] i_array1 = new int[] {};
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array1);
        int[] i_array3 = org.apache.commons.math.util.MathUtils.copyOf(i_array1);
        try {
            double d4 = org.apache.commons.math.util.MathUtils.distance(i_array0, i_array3);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
    }

    @Test
    public void test155() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test155");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 3628800.0d, (java.lang.Number) (-0.5571822032850108d), (int) (short) 0);
        java.lang.Number number4 = nonMonotonousSequenceException3.getPrevious();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + (-0.5571822032850108d) + "'", number4.equals((-0.5571822032850108d)));
    }

    @Test
    public void test156() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test156");
        long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient((int) '#', (-1074790369));
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test157() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test157");
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
        float[] f_array43 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array45 = new float[] { (-1074790400) };
        boolean b46 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array43, f_array45);
        float[] f_array52 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array54 = new float[] { (-1074790400) };
        boolean b55 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array52, f_array54);
        boolean b56 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array45, f_array52);
        boolean b57 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array26, f_array52);
        boolean b58 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array7, f_array26);
        float[] f_array60 = new float[] { 78L };
        float[] f_array61 = null;
        boolean b62 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array60, f_array61);
        float[] f_array68 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array70 = new float[] { (-1074790400) };
        boolean b71 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array68, f_array70);
        float[] f_array77 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array79 = new float[] { (-1074790400) };
        boolean b80 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array77, f_array79);
        boolean b81 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array70, f_array77);
        boolean b82 = org.apache.commons.math.util.MathUtils.equals(f_array60, f_array77);
        boolean b83 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array7, f_array60);
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
        org.junit.Assert.assertNotNull(f_array43);
        org.junit.Assert.assertNotNull(f_array45);
        org.junit.Assert.assertTrue(b46 == false);
        org.junit.Assert.assertNotNull(f_array52);
        org.junit.Assert.assertNotNull(f_array54);
        org.junit.Assert.assertTrue(b55 == false);
        org.junit.Assert.assertTrue(b56 == false);
        org.junit.Assert.assertTrue(b57 == false);
        org.junit.Assert.assertTrue(b58 == true);
        org.junit.Assert.assertNotNull(f_array60);
        org.junit.Assert.assertTrue(b62 == false);
        org.junit.Assert.assertNotNull(f_array68);
        org.junit.Assert.assertNotNull(f_array70);
        org.junit.Assert.assertTrue(b71 == false);
        org.junit.Assert.assertNotNull(f_array77);
        org.junit.Assert.assertNotNull(f_array79);
        org.junit.Assert.assertTrue(b80 == false);
        org.junit.Assert.assertTrue(b81 == false);
        org.junit.Assert.assertTrue(b82 == false);
        org.junit.Assert.assertTrue(b83 == false);
    }

    @Test
    public void test158() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test158");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 1072693248, (-1));
        int i4 = dimensionMismatchException3.getDimension();
        int i5 = dimensionMismatchException3.getDimension();
        org.junit.Assert.assertTrue(i4 == (-1));
        org.junit.Assert.assertTrue(i5 == (-1));
    }

    @Test
    public void test159() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test159");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog((-9), 0);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test160() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test160");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck(362880L, 100L);
        org.junit.Assert.assertTrue(long2 == 36288000L);
    }

    @Test
    public void test161() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test161");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 10.0d, (java.lang.Number) 0.0f, false);
        boolean b5 = numberIsTooSmallException4.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b5 == false);
    }

    @Test
    public void test162() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test162");
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
        org.apache.commons.math.exception.util.Localizable localizable29 = null;
        org.apache.commons.math.complex.Complex complex30 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex31 = complex30.cos();
        org.apache.commons.math.complex.Complex complex32 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex33 = complex32.cos();
        org.apache.commons.math.complex.Complex complex34 = complex30.subtract(complex32);
        org.apache.commons.math.complex.Complex complex35 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex36 = complex35.cos();
        org.apache.commons.math.complex.Complex complex37 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex38 = complex37.cos();
        org.apache.commons.math.complex.Complex complex39 = complex35.subtract(complex37);
        org.apache.commons.math.complex.Complex complex40 = complex30.add(complex39);
        double d41 = complex30.getArgument();
        org.apache.commons.math.complex.Complex complex42 = complex30.exp();
        org.apache.commons.math.complex.Complex complex43 = complex30.negate();
        org.apache.commons.math.complex.Complex complex44 = complex30.asin();
        org.apache.commons.math.complex.Complex complex45 = complex30.acos();
        org.apache.commons.math.exception.util.Localizable localizable46 = null;
        double[] d_array47 = new double[] {};
        double[] d_array51 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array55 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array59 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array63 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array64 = new double[][] { d_array51, d_array55, d_array59, d_array63 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array47, d_array_array64);
        double[] d_array66 = new double[] {};
        double[] d_array70 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array74 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array78 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array82 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array83 = new double[][] { d_array70, d_array74, d_array78, d_array82 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array66, d_array_array83);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array47, d_array_array83);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex30, localizable46, (java.lang.Object[]) d_array_array83);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex24, localizable29, (java.lang.Object[]) d_array_array83);
        java.lang.String str88 = complex24.toString();
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
        org.junit.Assert.assertEquals((double) d41, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex42);
        org.junit.Assert.assertNotNull(complex43);
        org.junit.Assert.assertNotNull(complex44);
        org.junit.Assert.assertNotNull(complex45);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array51);
        org.junit.Assert.assertNotNull(d_array55);
        org.junit.Assert.assertNotNull(d_array59);
        org.junit.Assert.assertNotNull(d_array63);
        org.junit.Assert.assertNotNull(d_array_array64);
        org.junit.Assert.assertNotNull(d_array66);
        org.junit.Assert.assertNotNull(d_array70);
        org.junit.Assert.assertNotNull(d_array74);
        org.junit.Assert.assertNotNull(d_array78);
        org.junit.Assert.assertNotNull(d_array82);
        org.junit.Assert.assertNotNull(d_array_array83);
        org.junit.Assert.assertTrue("'" + str88 + "' != '" + "(0.0, 0.0)" + "'", str88.equals("(0.0, 0.0)"));
    }

    @Test
    public void test163() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test163");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo(11013.232874703393d, (double) 10L, (-1));
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test164() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test164");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array10 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i11 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array10);
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 1);
        int i14 = org.apache.commons.math.util.MathUtils.distance1(i_array0, i_array3);
        int[] i_array16 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 98);
        int[] i_array17 = new int[] {};
        int[] i_array18 = org.apache.commons.math.util.MathUtils.copyOf(i_array17);
        int[] i_array19 = org.apache.commons.math.util.MathUtils.copyOf(i_array17);
        int[] i_array20 = new int[] {};
        int[] i_array21 = org.apache.commons.math.util.MathUtils.copyOf(i_array20);
        int[] i_array27 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i28 = org.apache.commons.math.util.MathUtils.distanceInf(i_array20, i_array27);
        int[] i_array30 = org.apache.commons.math.util.MathUtils.copyOf(i_array20, 1);
        int i31 = org.apache.commons.math.util.MathUtils.distance1(i_array17, i_array20);
        int[] i_array32 = new int[] {};
        int[] i_array33 = org.apache.commons.math.util.MathUtils.copyOf(i_array32);
        int[] i_array39 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i40 = org.apache.commons.math.util.MathUtils.distanceInf(i_array32, i_array39);
        int[] i_array42 = org.apache.commons.math.util.MathUtils.copyOf(i_array32, 1);
        int[] i_array43 = new int[] {};
        int[] i_array44 = org.apache.commons.math.util.MathUtils.copyOf(i_array43);
        int[] i_array45 = org.apache.commons.math.util.MathUtils.copyOf(i_array43);
        int[] i_array46 = new int[] {};
        int[] i_array47 = org.apache.commons.math.util.MathUtils.copyOf(i_array46);
        int[] i_array53 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i54 = org.apache.commons.math.util.MathUtils.distanceInf(i_array46, i_array53);
        int[] i_array56 = org.apache.commons.math.util.MathUtils.copyOf(i_array46, 1);
        int i57 = org.apache.commons.math.util.MathUtils.distance1(i_array43, i_array46);
        int i58 = org.apache.commons.math.util.MathUtils.distance1(i_array32, i_array46);
        double d59 = org.apache.commons.math.util.MathUtils.distance(i_array20, i_array46);
        try {
            int i60 = org.apache.commons.math.util.MathUtils.distanceInf(i_array16, i_array20);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertTrue(i11 == 0);
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertTrue(i14 == 0);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertNotNull(i_array17);
        org.junit.Assert.assertNotNull(i_array18);
        org.junit.Assert.assertNotNull(i_array19);
        org.junit.Assert.assertNotNull(i_array20);
        org.junit.Assert.assertNotNull(i_array21);
        org.junit.Assert.assertNotNull(i_array27);
        org.junit.Assert.assertTrue(i28 == 0);
        org.junit.Assert.assertNotNull(i_array30);
        org.junit.Assert.assertTrue(i31 == 0);
        org.junit.Assert.assertNotNull(i_array32);
        org.junit.Assert.assertNotNull(i_array33);
        org.junit.Assert.assertNotNull(i_array39);
        org.junit.Assert.assertTrue(i40 == 0);
        org.junit.Assert.assertNotNull(i_array42);
        org.junit.Assert.assertNotNull(i_array43);
        org.junit.Assert.assertNotNull(i_array44);
        org.junit.Assert.assertNotNull(i_array45);
        org.junit.Assert.assertNotNull(i_array46);
        org.junit.Assert.assertNotNull(i_array47);
        org.junit.Assert.assertNotNull(i_array53);
        org.junit.Assert.assertTrue(i54 == 0);
        org.junit.Assert.assertNotNull(i_array56);
        org.junit.Assert.assertTrue(i57 == 0);
        org.junit.Assert.assertTrue(i58 == 0);
        org.junit.Assert.assertTrue(d59 == 0.0d);
    }

    @Test
    public void test165() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test165");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex6 = complex2.nthRoot((int) (short) 10);
        org.apache.commons.math.complex.Complex complex7 = complex2.log();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(list_complex6);
        org.junit.Assert.assertNotNull(complex7);
    }

    @Test
    public void test166() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test166");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) (-2064878497), (long) 1100);
        org.junit.Assert.assertTrue(long2 == (-2064879597L));
    }

    @Test
    public void test167() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test167");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck(3, (-447362047));
        org.junit.Assert.assertTrue(i2 == (-447362044));
    }

    @Test
    public void test168() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test168");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck(100, (-2112331476));
        org.junit.Assert.assertTrue(i2 == (-2112331376));
    }

    @Test
    public void test169() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test169");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 9L, (float) 7766279631452241920L, (int) (byte) 10);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test170() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test170");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle(1.1752011936438014d, 2.1752011936438014d);
        org.junit.Assert.assertTrue(d2 == 1.1752011936438014d);
    }

    @Test
    public void test171() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test171");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array3);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
    }

    @Test
    public void test172() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test172");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((float) 97, Float.NaN);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test173() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test173");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round(10.0f, (-1074790369), (-2064878497));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test174() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test174");
        short s1 = org.apache.commons.math.util.MathUtils.sign((short) (byte) 0);
        org.junit.Assert.assertTrue(s1 == (short) 0);
    }

    @Test
    public void test175() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test175");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException7 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i8 = nonMonotonousSequenceException7.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection9 = nonMonotonousSequenceException7.getDirection();
        boolean b12 = org.apache.commons.math.util.MathUtils.checkOrder(d_array1, orderDirection9, true, false);
        try {
            double[] d_array14 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, (-447362044));
            org.junit.Assert.fail("Expected exception of type java.lang.NegativeArraySizeException");
        } catch (java.lang.NegativeArraySizeException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(i8 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection9 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection9.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b12 == true);
    }

    @Test
    public void test176() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test176");
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
        double[] d_array41 = new double[] { (-1.0f) };
        double[] d_array43 = org.apache.commons.math.util.MathUtils.copyOf(d_array41, 1);
        double d44 = org.apache.commons.math.util.MathUtils.safeNorm(d_array41);
        java.lang.Number number45 = null;
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection48 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException50 = new org.apache.commons.math.exception.NonMonotonousSequenceException(number45, (java.lang.Number) (byte) 0, (-1074790400), orderDirection48, true);
        boolean b53 = org.apache.commons.math.util.MathUtils.checkOrder(d_array41, orderDirection48, true, true);
        boolean b54 = org.apache.commons.math.util.MathUtils.equals(d_array0, d_array41);
        double[] d_array55 = null;
        try {
            double d56 = org.apache.commons.math.util.MathUtils.distance1(d_array41, d_array55);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
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
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertTrue(d44 == 1.0d);
        org.junit.Assert.assertTrue("'" + orderDirection48 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection48.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b53 == true);
        org.junit.Assert.assertTrue(b54 == false);
    }

    @Test
    public void test177() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test177");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow(0, (-1L));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test178() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test178");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (-1074790400), (float) (-7500783708504147263L), (int) '4');
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test179() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test179");
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
        double d12 = complex0.abs();
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
    public void test180() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test180");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) 1.0066581708938198d, (java.lang.Number) (-1.0d), false);
        java.lang.Throwable throwable5 = null;
        try {
            numberIsTooLargeException4.addSuppressed(throwable5);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
    }

    @Test
    public void test181() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test181");
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
        double[] d_array43 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d44 = org.apache.commons.math.util.MathUtils.safeNorm(d_array43);
        double[] d_array45 = new double[] {};
        double[] d_array49 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array53 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array57 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array61 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array62 = new double[][] { d_array49, d_array53, d_array57, d_array61 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array45, d_array_array62);
        int i64 = org.apache.commons.math.util.MathUtils.hash(d_array45);
        boolean b65 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array43, d_array45);
        double d66 = org.apache.commons.math.util.MathUtils.distanceInf(d_array22, d_array43);
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
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertTrue(d44 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array57);
        org.junit.Assert.assertNotNull(d_array61);
        org.junit.Assert.assertNotNull(d_array_array62);
        org.junit.Assert.assertTrue(i64 == 1);
        org.junit.Assert.assertTrue(b65 == false);
        org.junit.Assert.assertTrue(d66 == 2.0d);
    }

    @Test
    public void test182() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test182");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog((-2112331376), (-1));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test183() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test183");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) 1.0066581708938198d);
        java.lang.Number number2 = notPositiveException1.getArgument();
        org.junit.Assert.assertTrue("'" + number2 + "' != '" + 1.0066581708938198d + "'", number2.equals(1.0066581708938198d));
    }

    @Test
    public void test184() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test184");
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
        org.apache.commons.math.complex.Complex complex11 = complex10.sqrt();
        org.apache.commons.math.complex.Complex complex12 = complex11.negate();
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
    public void test185() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test185");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        org.apache.commons.math.complex.Complex complex5 = complex3.tanh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
    }

    @Test
    public void test186() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test186");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.lcm((-9148717644917614656L), (long) (-447362047));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test187() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test187");
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
        org.apache.commons.math.complex.Complex complex13 = complex11.log();
        org.apache.commons.math.complex.Complex complex14 = complex11.atan();
        org.apache.commons.math.complex.Complex complex15 = complex11.asin();
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
    }

    @Test
    public void test188() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test188");
        double d3 = org.apache.commons.math.util.MathUtils.reduce(409.0515645945562d, (-0.9117339147869651d), (double) (short) -1);
        org.junit.Assert.assertTrue(d3 == 0.6830368552089112d);
    }

    @Test
    public void test189() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test189");
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
        org.apache.commons.math.complex.ComplexField complexField18 = complex0.getField();
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
        org.junit.Assert.assertNotNull(complexField18);
    }

    @Test
    public void test190() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test190");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck(3628800L, (long) 0);
        org.junit.Assert.assertTrue(long2 == 3628800L);
    }

    @Test
    public void test191() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test191");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 9, (-1074790369));
        org.apache.commons.math.exception.NullArgumentException nullArgumentException4 = new org.apache.commons.math.exception.NullArgumentException();
        dimensionMismatchException3.addSuppressed((java.lang.Throwable) nullArgumentException4);
    }

    @Test
    public void test192() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test192");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array7 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i8 = org.apache.commons.math.util.MathUtils.distanceInf(i_array0, i_array7);
        int[] i_array9 = new int[] {};
        int[] i_array10 = org.apache.commons.math.util.MathUtils.copyOf(i_array9);
        int[] i_array11 = org.apache.commons.math.util.MathUtils.copyOf(i_array9);
        int[] i_array12 = new int[] {};
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array12);
        int[] i_array19 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i20 = org.apache.commons.math.util.MathUtils.distanceInf(i_array12, i_array19);
        int[] i_array22 = org.apache.commons.math.util.MathUtils.copyOf(i_array12, 1);
        int i23 = org.apache.commons.math.util.MathUtils.distance1(i_array9, i_array12);
        int[] i_array27 = new int[] { (-2112331476), (short) 100, (short) 100 };
        int i28 = org.apache.commons.math.util.MathUtils.distance1(i_array12, i_array27);
        try {
            int i29 = org.apache.commons.math.util.MathUtils.distance1(i_array7, i_array12);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertTrue(i8 == 0);
        org.junit.Assert.assertNotNull(i_array9);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertNotNull(i_array11);
        org.junit.Assert.assertNotNull(i_array12);
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertNotNull(i_array19);
        org.junit.Assert.assertTrue(i20 == 0);
        org.junit.Assert.assertNotNull(i_array22);
        org.junit.Assert.assertTrue(i23 == 0);
        org.junit.Assert.assertNotNull(i_array27);
        org.junit.Assert.assertTrue(i28 == 0);
    }

    @Test
    public void test193() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test193");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.acos();
        org.apache.commons.math.complex.Complex complex2 = null;
        try {
            org.apache.commons.math.complex.Complex complex3 = complex1.add(complex2);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
    }

    @Test
    public void test194() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test194");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException4 = new org.apache.commons.math.exception.NumberIsTooLargeException(localizable0, (java.lang.Number) (-1.1752011936438014d), (java.lang.Number) 97, false);
    }

    @Test
    public void test195() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test195");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.pow(1075970048, (-447362044));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test196() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test196");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (-1074790400), (float) (byte) 0, (float) (byte) 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test197() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test197");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 10000000000L, 19683);
        org.junit.Assert.assertTrue(d2 == 1.0E10d);
    }

    @Test
    public void test198() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test198");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        int i19 = org.apache.commons.math.util.MathUtils.hash(d_array0);
        double[] d_array20 = new double[] {};
        double[] d_array24 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array28 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array32 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array36 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array37 = new double[][] { d_array24, d_array28, d_array32, d_array36 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array20, d_array_array37);
        double[] d_array39 = new double[] {};
        double[] d_array43 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array47 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array51 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array55 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array56 = new double[][] { d_array43, d_array47, d_array51, d_array55 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array39, d_array_array56);
        double[] d_array58 = new double[] {};
        double[] d_array62 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array66 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array70 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array74 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array75 = new double[][] { d_array62, d_array66, d_array70, d_array74 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array58, d_array_array75);
        int i77 = org.apache.commons.math.util.MathUtils.hash(d_array58);
        double d78 = org.apache.commons.math.util.MathUtils.distance1(d_array39, d_array58);
        double d79 = org.apache.commons.math.util.MathUtils.distance1(d_array20, d_array39);
        double d80 = org.apache.commons.math.util.MathUtils.distance(d_array0, d_array20);
        try {
            double[] d_array82 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array0, (double) 98);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertTrue(i19 == 1);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array24);
        org.junit.Assert.assertNotNull(d_array28);
        org.junit.Assert.assertNotNull(d_array32);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertNotNull(d_array_array37);
        org.junit.Assert.assertNotNull(d_array39);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array51);
        org.junit.Assert.assertNotNull(d_array55);
        org.junit.Assert.assertNotNull(d_array_array56);
        org.junit.Assert.assertNotNull(d_array58);
        org.junit.Assert.assertNotNull(d_array62);
        org.junit.Assert.assertNotNull(d_array66);
        org.junit.Assert.assertNotNull(d_array70);
        org.junit.Assert.assertNotNull(d_array74);
        org.junit.Assert.assertNotNull(d_array_array75);
        org.junit.Assert.assertTrue(i77 == 1);
        org.junit.Assert.assertTrue(d78 == 0.0d);
        org.junit.Assert.assertTrue(d79 == 0.0d);
        org.junit.Assert.assertTrue(d80 == 0.0d);
    }

    @Test
    public void test199() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test199");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (int) (byte) 10, (int) (byte) 1);
    }

    @Test
    public void test200() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test200");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (-1.0f), (double) (-1));
        org.junit.Assert.assertTrue(b2 == true);
    }

    @Test
    public void test201() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test201");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array5 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array6 = new int[] {};
        int[] i_array7 = org.apache.commons.math.util.MathUtils.copyOf(i_array6);
        double d8 = org.apache.commons.math.util.MathUtils.distance(i_array5, i_array7);
        int[] i_array15 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array17 = org.apache.commons.math.util.MathUtils.copyOf(i_array15, (int) '4');
        int i18 = org.apache.commons.math.util.MathUtils.distanceInf(i_array7, i_array15);
        int i19 = org.apache.commons.math.util.MathUtils.distanceInf(i_array0, i_array15);
        int[] i_array20 = new int[] {};
        int[] i_array21 = org.apache.commons.math.util.MathUtils.copyOf(i_array20);
        int[] i_array22 = org.apache.commons.math.util.MathUtils.copyOf(i_array20);
        int[] i_array23 = new int[] {};
        int[] i_array24 = org.apache.commons.math.util.MathUtils.copyOf(i_array23);
        int[] i_array30 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i31 = org.apache.commons.math.util.MathUtils.distanceInf(i_array23, i_array30);
        int[] i_array33 = org.apache.commons.math.util.MathUtils.copyOf(i_array23, 1);
        int i34 = org.apache.commons.math.util.MathUtils.distance1(i_array20, i_array23);
        int[] i_array35 = new int[] {};
        int[] i_array36 = org.apache.commons.math.util.MathUtils.copyOf(i_array35);
        int[] i_array42 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i43 = org.apache.commons.math.util.MathUtils.distanceInf(i_array35, i_array42);
        int[] i_array45 = org.apache.commons.math.util.MathUtils.copyOf(i_array35, 1);
        int[] i_array46 = new int[] {};
        int[] i_array47 = org.apache.commons.math.util.MathUtils.copyOf(i_array46);
        int[] i_array48 = org.apache.commons.math.util.MathUtils.copyOf(i_array46);
        int[] i_array49 = new int[] {};
        int[] i_array50 = org.apache.commons.math.util.MathUtils.copyOf(i_array49);
        int[] i_array56 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i57 = org.apache.commons.math.util.MathUtils.distanceInf(i_array49, i_array56);
        int[] i_array59 = org.apache.commons.math.util.MathUtils.copyOf(i_array49, 1);
        int i60 = org.apache.commons.math.util.MathUtils.distance1(i_array46, i_array49);
        int i61 = org.apache.commons.math.util.MathUtils.distance1(i_array35, i_array49);
        double d62 = org.apache.commons.math.util.MathUtils.distance(i_array23, i_array49);
        int i63 = org.apache.commons.math.util.MathUtils.distance1(i_array0, i_array23);
        int[] i_array64 = new int[] {};
        int[] i_array65 = org.apache.commons.math.util.MathUtils.copyOf(i_array64);
        int[] i_array66 = org.apache.commons.math.util.MathUtils.copyOf(i_array65);
        int[] i_array67 = null;
        double d68 = org.apache.commons.math.util.MathUtils.distance(i_array66, i_array67);
        double d69 = org.apache.commons.math.util.MathUtils.distance(i_array23, i_array67);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array5);
        org.junit.Assert.assertNotNull(i_array6);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertTrue(d8 == 0.0d);
        org.junit.Assert.assertNotNull(i_array15);
        org.junit.Assert.assertNotNull(i_array17);
        org.junit.Assert.assertTrue(i18 == 0);
        org.junit.Assert.assertTrue(i19 == 0);
        org.junit.Assert.assertNotNull(i_array20);
        org.junit.Assert.assertNotNull(i_array21);
        org.junit.Assert.assertNotNull(i_array22);
        org.junit.Assert.assertNotNull(i_array23);
        org.junit.Assert.assertNotNull(i_array24);
        org.junit.Assert.assertNotNull(i_array30);
        org.junit.Assert.assertTrue(i31 == 0);
        org.junit.Assert.assertNotNull(i_array33);
        org.junit.Assert.assertTrue(i34 == 0);
        org.junit.Assert.assertNotNull(i_array35);
        org.junit.Assert.assertNotNull(i_array36);
        org.junit.Assert.assertNotNull(i_array42);
        org.junit.Assert.assertTrue(i43 == 0);
        org.junit.Assert.assertNotNull(i_array45);
        org.junit.Assert.assertNotNull(i_array46);
        org.junit.Assert.assertNotNull(i_array47);
        org.junit.Assert.assertNotNull(i_array48);
        org.junit.Assert.assertNotNull(i_array49);
        org.junit.Assert.assertNotNull(i_array50);
        org.junit.Assert.assertNotNull(i_array56);
        org.junit.Assert.assertTrue(i57 == 0);
        org.junit.Assert.assertNotNull(i_array59);
        org.junit.Assert.assertTrue(i60 == 0);
        org.junit.Assert.assertTrue(i61 == 0);
        org.junit.Assert.assertTrue(d62 == 0.0d);
        org.junit.Assert.assertTrue(i63 == 0);
        org.junit.Assert.assertNotNull(i_array64);
        org.junit.Assert.assertNotNull(i_array65);
        org.junit.Assert.assertNotNull(i_array66);
        org.junit.Assert.assertTrue(d68 == 0.0d);
        org.junit.Assert.assertTrue(d69 == 0.0d);
    }

    @Test
    public void test202() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test202");
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
        org.apache.commons.math.complex.Complex complex27 = complex0.tanh();
        boolean b28 = complex27.isNaN();
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
        org.junit.Assert.assertTrue(b28 == true);
    }

    @Test
    public void test203() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test203");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array5 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array6 = new int[] {};
        int[] i_array7 = org.apache.commons.math.util.MathUtils.copyOf(i_array6);
        double d8 = org.apache.commons.math.util.MathUtils.distance(i_array5, i_array7);
        int[] i_array15 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array17 = org.apache.commons.math.util.MathUtils.copyOf(i_array15, (int) '4');
        int i18 = org.apache.commons.math.util.MathUtils.distanceInf(i_array7, i_array15);
        int i19 = org.apache.commons.math.util.MathUtils.distanceInf(i_array0, i_array15);
        int[] i_array24 = new int[] { 100, 1100, 1074790400, (-2064878497) };
        int i25 = org.apache.commons.math.util.MathUtils.distanceInf(i_array0, i_array24);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array5);
        org.junit.Assert.assertNotNull(i_array6);
        org.junit.Assert.assertNotNull(i_array7);
        org.junit.Assert.assertTrue(d8 == 0.0d);
        org.junit.Assert.assertNotNull(i_array15);
        org.junit.Assert.assertNotNull(i_array17);
        org.junit.Assert.assertTrue(i18 == 0);
        org.junit.Assert.assertTrue(i19 == 0);
        org.junit.Assert.assertNotNull(i_array24);
        org.junit.Assert.assertTrue(i25 == 0);
    }

    @Test
    public void test204() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test204");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((long) (-1074790400), 362880000L);
        org.junit.Assert.assertTrue(long2 == 15235153920000L);
    }

    @Test
    public void test205() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test205");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        try {
            java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) (-2064878497));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger2);
    }

    @Test
    public void test206() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test206");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 10.0d, (java.lang.Number) (-1), true);
    }

    @Test
    public void test207() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test207");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) 100.0d);
        java.lang.Number number3 = notPositiveException2.getMin();
        java.lang.Number number4 = notPositiveException2.getMin();
        org.junit.Assert.assertTrue("'" + number3 + "' != '" + 0 + "'", number3.equals(0));
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 0 + "'", number4.equals(0));
    }

    @Test
    public void test208() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test208");
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
        double[] d_array41 = new double[] { (-1.0f) };
        double[] d_array43 = org.apache.commons.math.util.MathUtils.copyOf(d_array41, 1);
        double d44 = org.apache.commons.math.util.MathUtils.safeNorm(d_array41);
        java.lang.Number number45 = null;
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection48 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException50 = new org.apache.commons.math.exception.NonMonotonousSequenceException(number45, (java.lang.Number) (byte) 0, (-1074790400), orderDirection48, true);
        boolean b53 = org.apache.commons.math.util.MathUtils.checkOrder(d_array41, orderDirection48, true, true);
        boolean b54 = org.apache.commons.math.util.MathUtils.equals(d_array0, d_array41);
        double[] d_array55 = new double[] {};
        double[] d_array59 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array63 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array67 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array71 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array72 = new double[][] { d_array59, d_array63, d_array67, d_array71 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array55, d_array_array72);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array72);
        double[] d_array76 = new double[] { (-1.0f) };
        double[] d_array78 = org.apache.commons.math.util.MathUtils.copyOf(d_array76, 1);
        double[] d_array80 = new double[] { (-1.0f) };
        double[] d_array82 = org.apache.commons.math.util.MathUtils.copyOf(d_array80, 1);
        double d83 = org.apache.commons.math.util.MathUtils.safeNorm(d_array80);
        double[] d_array85 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array80, 1.1752011936438014d);
        double d86 = org.apache.commons.math.util.MathUtils.distance1(d_array78, d_array80);
        org.apache.commons.math.util.MathUtils.checkOrder(d_array78);
        double d88 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array78);
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
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertTrue(d44 == 1.0d);
        org.junit.Assert.assertTrue("'" + orderDirection48 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection48.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b53 == true);
        org.junit.Assert.assertTrue(b54 == false);
        org.junit.Assert.assertNotNull(d_array55);
        org.junit.Assert.assertNotNull(d_array59);
        org.junit.Assert.assertNotNull(d_array63);
        org.junit.Assert.assertNotNull(d_array67);
        org.junit.Assert.assertNotNull(d_array71);
        org.junit.Assert.assertNotNull(d_array_array72);
        org.junit.Assert.assertNotNull(d_array76);
        org.junit.Assert.assertNotNull(d_array78);
        org.junit.Assert.assertNotNull(d_array80);
        org.junit.Assert.assertNotNull(d_array82);
        org.junit.Assert.assertTrue(d83 == 1.0d);
        org.junit.Assert.assertNotNull(d_array85);
        org.junit.Assert.assertTrue(d86 == 0.0d);
        org.junit.Assert.assertTrue(d88 == 0.0d);
    }

    @Test
    public void test209() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test209");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog(97, 0);
        org.junit.Assert.assertTrue(d2 == 0.0d);
    }

    @Test
    public void test210() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test210");
        java.lang.Object obj0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        java.lang.Object[] obj_array2 = null;
        try {
            org.apache.commons.math.util.MathUtils.checkNotNull(obj0, localizable1, obj_array2);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
    }

    @Test
    public void test211() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test211");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = null;
        boolean b6 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        double[] d_array8 = new double[] { (-1.0f) };
        double[] d_array10 = org.apache.commons.math.util.MathUtils.copyOf(d_array8, 1);
        double d11 = org.apache.commons.math.util.MathUtils.safeNorm(d_array8);
        double[] d_array13 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array8, 1.1752011936438014d);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException17 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i18 = nonMonotonousSequenceException17.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection19 = nonMonotonousSequenceException17.getDirection();
        boolean b22 = org.apache.commons.math.util.MathUtils.checkOrder(d_array13, orderDirection19, false, false);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection19, false);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NonMonotonousSequenceException");
        } catch (org.apache.commons.math.exception.NonMonotonousSequenceException e) {
        }
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertTrue(b6 == false);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertTrue(d11 == 1.0d);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertTrue(i18 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection19 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection19.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b22 == true);
    }

    @Test
    public void test212() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test212");
        double d1 = org.apache.commons.math.util.MathUtils.sinh(11013.232874703393d);
        org.junit.Assert.assertTrue(d1 == Double.POSITIVE_INFINITY);
    }

    @Test
    public void test213() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test213");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 92561040L, 1.0f, (float) 33);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test214() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test214");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) (short) 0);
    }

    @Test
    public void test215() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test215");
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
        double[] d_array31 = new double[] { (-1.0f) };
        double[] d_array33 = org.apache.commons.math.util.MathUtils.copyOf(d_array31, 1);
        double d34 = org.apache.commons.math.util.MathUtils.safeNorm(d_array31);
        double[] d_array36 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array31, 1.1752011936438014d);
        double d37 = org.apache.commons.math.util.MathUtils.distance1(d_array29, d_array31);
        double[] d_array38 = org.apache.commons.math.util.MathUtils.copyOf(d_array31);
        double d39 = org.apache.commons.math.util.MathUtils.distance1(d_array5, d_array31);
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
        org.junit.Assert.assertTrue(d34 == 1.0d);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertTrue(d37 == 0.0d);
        org.junit.Assert.assertNotNull(d_array38);
        org.junit.Assert.assertTrue(d39 == 0.0d);
    }

    @Test
    public void test216() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test216");
        float[] f_array4 = new float[] { 1.0f, 100L, (-73931601660L), (short) -1 };
        float[] f_array10 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array12 = new float[] { (-1074790400) };
        boolean b13 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array10, f_array12);
        float[] f_array19 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array21 = new float[] { (-1074790400) };
        boolean b22 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array19, f_array21);
        float[] f_array28 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array30 = new float[] { (-1074790400) };
        boolean b31 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array28, f_array30);
        boolean b32 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array21, f_array28);
        float[] f_array38 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array40 = new float[] { (-1074790400) };
        boolean b41 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array38, f_array40);
        float[] f_array47 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array49 = new float[] { (-1074790400) };
        boolean b50 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array47, f_array49);
        boolean b51 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array40, f_array47);
        boolean b52 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array21, f_array47);
        boolean b53 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array12, f_array47);
        boolean b54 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array4, f_array47);
        float[] f_array60 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array62 = new float[] { (-1074790400) };
        boolean b63 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array60, f_array62);
        float[] f_array69 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array71 = new float[] { (-1074790400) };
        boolean b72 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array69, f_array71);
        boolean b73 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array62, f_array69);
        float[] f_array79 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array81 = new float[] { (-1074790400) };
        boolean b82 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array79, f_array81);
        float[] f_array88 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array90 = new float[] { (-1074790400) };
        boolean b91 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array88, f_array90);
        boolean b92 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array81, f_array88);
        boolean b93 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array62, f_array88);
        boolean b94 = org.apache.commons.math.util.MathUtils.equals(f_array47, f_array62);
        org.junit.Assert.assertNotNull(f_array4);
        org.junit.Assert.assertNotNull(f_array10);
        org.junit.Assert.assertNotNull(f_array12);
        org.junit.Assert.assertTrue(b13 == false);
        org.junit.Assert.assertNotNull(f_array19);
        org.junit.Assert.assertNotNull(f_array21);
        org.junit.Assert.assertTrue(b22 == false);
        org.junit.Assert.assertNotNull(f_array28);
        org.junit.Assert.assertNotNull(f_array30);
        org.junit.Assert.assertTrue(b31 == false);
        org.junit.Assert.assertTrue(b32 == false);
        org.junit.Assert.assertNotNull(f_array38);
        org.junit.Assert.assertNotNull(f_array40);
        org.junit.Assert.assertTrue(b41 == false);
        org.junit.Assert.assertNotNull(f_array47);
        org.junit.Assert.assertNotNull(f_array49);
        org.junit.Assert.assertTrue(b50 == false);
        org.junit.Assert.assertTrue(b51 == false);
        org.junit.Assert.assertTrue(b52 == false);
        org.junit.Assert.assertTrue(b53 == false);
        org.junit.Assert.assertTrue(b54 == false);
        org.junit.Assert.assertNotNull(f_array60);
        org.junit.Assert.assertNotNull(f_array62);
        org.junit.Assert.assertTrue(b63 == false);
        org.junit.Assert.assertNotNull(f_array69);
        org.junit.Assert.assertNotNull(f_array71);
        org.junit.Assert.assertTrue(b72 == false);
        org.junit.Assert.assertTrue(b73 == false);
        org.junit.Assert.assertNotNull(f_array79);
        org.junit.Assert.assertNotNull(f_array81);
        org.junit.Assert.assertTrue(b82 == false);
        org.junit.Assert.assertNotNull(f_array88);
        org.junit.Assert.assertNotNull(f_array90);
        org.junit.Assert.assertTrue(b91 == false);
        org.junit.Assert.assertTrue(b92 == false);
        org.junit.Assert.assertTrue(b93 == false);
        org.junit.Assert.assertTrue(b94 == false);
    }

    @Test
    public void test217() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test217");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.acos();
        double d2 = complex1.getReal();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.5707963267948966d);
    }

    @Test
    public void test218() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test218");
        float f1 = org.apache.commons.math.util.MathUtils.sign(0.0f);
        org.junit.Assert.assertTrue(f1 == 0.0f);
    }

    @Test
    public void test219() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test219");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.pow((long) 1100, (long) (-2112331376));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test220() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test220");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, (int) (short) 1);
        org.junit.Assert.assertNotNull(bigInteger2);
        org.junit.Assert.assertNotNull(bigInteger4);
    }

    @Test
    public void test221() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test221");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle((-1.1752011936438014d), (double) 98);
        org.junit.Assert.assertTrue(d2 == 99.35576372122958d);
    }

    @Test
    public void test222() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test222");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.lang.Object[] obj_array1 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NullArgumentException nullArgumentException2 = new org.apache.commons.math.exception.NullArgumentException(localizable0, obj_array1);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) nullArgumentException2);
        org.junit.Assert.assertNotNull(obj_array1);
    }

    @Test
    public void test223() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test223");
        int i2 = org.apache.commons.math.util.MathUtils.pow(19683, (int) (short) 1);
        org.junit.Assert.assertTrue(i2 == 19683);
    }

    @Test
    public void test224() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test224");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) (-2112331476), 1.6487212707001282d);
    }

    @Test
    public void test225() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test225");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals((float) 22, (float) (-2112331476));
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test226() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test226");
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
        org.junit.Assert.assertNotNull(d_array38);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertNotNull(d_array50);
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertNotNull(d_array_array55);
        org.junit.Assert.assertTrue(i57 == 1);
        org.junit.Assert.assertTrue(d58 == 0.0d);
        org.junit.Assert.assertTrue(d59 == 0.0d);
    }

    @Test
    public void test227() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test227");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException(98, (int) (short) -1);
        int i3 = dimensionMismatchException2.getDimension();
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test228() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test228");
        int i2 = org.apache.commons.math.util.MathUtils.gcd(1072693248, (int) (short) 100);
        org.junit.Assert.assertTrue(i2 == 4);
    }

    @Test
    public void test229() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test229");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (int) '#', 42);
    }

    @Test
    public void test230() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test230");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 10.0d, (java.lang.Number) (short) 10, true);
        java.lang.Number number4 = numberIsTooLargeException3.getMax();
        java.lang.String str5 = numberIsTooLargeException3.toString();
        boolean b6 = numberIsTooLargeException3.getBoundIsAllowed();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + (short) 10 + "'", number4.equals((short) 10));
        org.junit.Assert.assertTrue("'" + str5 + "' != '" + "org.apache.commons.math.exception.NumberIsTooLargeException: 10 is larger than the maximum (10)" + "'", str5.equals("org.apache.commons.math.exception.NumberIsTooLargeException: 10 is larger than the maximum (10)"));
        org.junit.Assert.assertTrue(b6 == true);
    }

    @Test
    public void test231() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test231");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck(1636086543L, (long) (short) -1);
        org.junit.Assert.assertTrue(long2 == 1636086544L);
    }

    @Test
    public void test232() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test232");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (-1), 1.0d, (int) (byte) 100);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test233() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test233");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (-9), 7.930067261567154E14d, (-0.0d));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test234() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test234");
        float f1 = org.apache.commons.math.util.MathUtils.sign((float) (-447362044));
        org.junit.Assert.assertTrue(f1 == (-1.0f));
    }

    @Test
    public void test235() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test235");
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
        boolean b19 = complex18.isInfinite();
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
        org.junit.Assert.assertTrue(b19 == false);
    }

    @Test
    public void test236() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test236");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round((float) 33, 0, 11);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test237() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test237");
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
        org.apache.commons.math.complex.Complex complex19 = complex17.acos();
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
    }

    @Test
    public void test238() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test238");
        short s1 = org.apache.commons.math.util.MathUtils.indicator((short) 10);
        org.junit.Assert.assertTrue(s1 == (short) 1);
    }

    @Test
    public void test239() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test239");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.exp();
        org.apache.commons.math.complex.Complex complex3 = complex1.tanh();
        org.apache.commons.math.complex.Complex complex4 = null;
        try {
            org.apache.commons.math.complex.Complex complex5 = complex3.pow(complex4);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
    }

    @Test
    public void test240() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test240");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 1075970048, (java.lang.Number) 57L, false);
    }

    @Test
    public void test241() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test241");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (-9), (double) ' ', (double) 11);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test242() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test242");
        long long2 = org.apache.commons.math.util.MathUtils.pow(0L, 1636086543L);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test243() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test243");
        int i2 = org.apache.commons.math.util.MathUtils.lcm(33, (int) '#');
        org.junit.Assert.assertTrue(i2 == 1155);
    }

    @Test
    public void test244() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test244");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        double d5 = org.apache.commons.math.util.MathUtils.distance(i_array2, i_array4);
        int[] i_array12 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array14 = org.apache.commons.math.util.MathUtils.copyOf(i_array12, (int) '4');
        int i15 = org.apache.commons.math.util.MathUtils.distanceInf(i_array4, i_array12);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) i_array12);
        int[] i_array18 = org.apache.commons.math.util.MathUtils.copyOf(i_array12, (int) (short) 10);
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertTrue(d5 == 0.0d);
        org.junit.Assert.assertNotNull(i_array12);
        org.junit.Assert.assertNotNull(i_array14);
        org.junit.Assert.assertTrue(i15 == 0);
        org.junit.Assert.assertNotNull(i_array18);
    }

    @Test
    public void test245() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test245");
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
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array36);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array0);
        double[] d_array43 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d44 = org.apache.commons.math.util.MathUtils.safeNorm(d_array43);
        double[] d_array45 = new double[] {};
        double[] d_array49 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array53 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array57 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array61 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array62 = new double[][] { d_array49, d_array53, d_array57, d_array61 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array45, d_array_array62);
        int i64 = org.apache.commons.math.util.MathUtils.hash(d_array45);
        boolean b65 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array43, d_array45);
        double[] d_array69 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d70 = org.apache.commons.math.util.MathUtils.safeNorm(d_array69);
        double[] d_array71 = new double[] {};
        double[] d_array75 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array79 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array83 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array87 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array88 = new double[][] { d_array75, d_array79, d_array83, d_array87 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array71, d_array_array88);
        int i90 = org.apache.commons.math.util.MathUtils.hash(d_array71);
        boolean b91 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array69, d_array71);
        boolean b92 = org.apache.commons.math.util.MathUtils.equals(d_array45, d_array69);
        double[] d_array94 = org.apache.commons.math.util.MathUtils.copyOf(d_array45, 0);
        double[] d_array96 = org.apache.commons.math.util.MathUtils.copyOf(d_array94, (int) (short) 100);
        double d97 = org.apache.commons.math.util.MathUtils.distance(d_array0, d_array96);
        double[] d_array98 = org.apache.commons.math.util.MathUtils.copyOf(d_array0);
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
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertTrue(d44 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array45);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array57);
        org.junit.Assert.assertNotNull(d_array61);
        org.junit.Assert.assertNotNull(d_array_array62);
        org.junit.Assert.assertTrue(i64 == 1);
        org.junit.Assert.assertTrue(b65 == false);
        org.junit.Assert.assertNotNull(d_array69);
        org.junit.Assert.assertTrue(d70 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array71);
        org.junit.Assert.assertNotNull(d_array75);
        org.junit.Assert.assertNotNull(d_array79);
        org.junit.Assert.assertNotNull(d_array83);
        org.junit.Assert.assertNotNull(d_array87);
        org.junit.Assert.assertNotNull(d_array_array88);
        org.junit.Assert.assertTrue(i90 == 1);
        org.junit.Assert.assertTrue(b91 == false);
        org.junit.Assert.assertTrue(b92 == false);
        org.junit.Assert.assertNotNull(d_array94);
        org.junit.Assert.assertNotNull(d_array96);
        org.junit.Assert.assertTrue(d97 == 0.0d);
        org.junit.Assert.assertNotNull(d_array98);
    }

    @Test
    public void test246() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test246");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) (-9148717644917614656L), 100.53096491487338d);
    }

    @Test
    public void test247() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test247");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) (-1), (java.lang.Number) (byte) 10, true);
        boolean b4 = numberIsTooLargeException3.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b4 == true);
    }

    @Test
    public void test248() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test248");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.lcm((long) (short) 100, (-7500783708504147263L));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test249() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test249");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals(7.930067261567154E14d, 0.0d);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test250() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test250");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog((int) ' ', 1100);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test251() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test251");
        double[] d_array3 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d4 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array5 = null;
        boolean b6 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array5);
        double[] d_array7 = new double[] {};
        double[] d_array11 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array15 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array19 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array23 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array24 = new double[][] { d_array11, d_array15, d_array19, d_array23 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array7, d_array_array24);
        double[] d_array27 = new double[] { (byte) -1 };
        double[] d_array29 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array27, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException33 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection34 = nonMonotonousSequenceException33.getDirection();
        int i35 = nonMonotonousSequenceException33.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable36 = null;
        java.lang.Object[] obj_array38 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException39 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable36, (java.lang.Number) (short) 100, obj_array38);
        java.lang.Number number40 = notFiniteNumberException39.getArgument();
        nonMonotonousSequenceException33.addSuppressed((java.lang.Throwable) notFiniteNumberException39);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection42 = nonMonotonousSequenceException33.getDirection();
        org.apache.commons.math.util.MathUtils.checkOrder(d_array29, orderDirection42, false);
        double d45 = org.apache.commons.math.util.MathUtils.distance1(d_array7, d_array29);
        double[] d_array47 = new double[] { (byte) -1 };
        double[] d_array49 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array47, (double) (byte) 1);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException53 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i54 = nonMonotonousSequenceException53.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection55 = nonMonotonousSequenceException53.getDirection();
        boolean b58 = org.apache.commons.math.util.MathUtils.checkOrder(d_array47, orderDirection55, true, false);
        double d59 = org.apache.commons.math.util.MathUtils.distance(d_array29, d_array47);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection63 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException65 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) Double.POSITIVE_INFINITY, (java.lang.Number) 1.0f, 0, orderDirection63, true);
        org.apache.commons.math.util.MathUtils.checkOrder(d_array47, orderDirection63, true);
        try {
            boolean b70 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection63, false, true);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NonMonotonousSequenceException");
        } catch (org.apache.commons.math.exception.NonMonotonousSequenceException e) {
        }
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(d4 == 10.04987562112089d);
        org.junit.Assert.assertTrue(b6 == false);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array_array24);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertTrue("'" + orderDirection34 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection34.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i35 == 97);
        org.junit.Assert.assertTrue("'" + number40 + "' != '" + (short) 100 + "'", number40.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection42 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection42.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(d45 == 0.0d);
        org.junit.Assert.assertNotNull(d_array47);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertTrue(i54 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection55 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection55.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b58 == true);
        org.junit.Assert.assertTrue(d59 == 2.0d);
        org.junit.Assert.assertTrue("'" + orderDirection63 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection63.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test252() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test252");
        float[] f_array0 = null;
        float[] f_array6 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array8 = new float[] { (-1074790400) };
        boolean b9 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array6, f_array8);
        float[] f_array15 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array17 = new float[] { (-1074790400) };
        boolean b18 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array15, f_array17);
        boolean b19 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array8, f_array15);
        float[] f_array25 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array27 = new float[] { (-1074790400) };
        boolean b28 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array25, f_array27);
        float[] f_array34 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array36 = new float[] { (-1074790400) };
        boolean b37 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array34, f_array36);
        boolean b38 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array27, f_array34);
        boolean b39 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array8, f_array34);
        boolean b40 = org.apache.commons.math.util.MathUtils.equals(f_array0, f_array8);
        org.junit.Assert.assertNotNull(f_array6);
        org.junit.Assert.assertNotNull(f_array8);
        org.junit.Assert.assertTrue(b9 == false);
        org.junit.Assert.assertNotNull(f_array15);
        org.junit.Assert.assertNotNull(f_array17);
        org.junit.Assert.assertTrue(b18 == false);
        org.junit.Assert.assertTrue(b19 == false);
        org.junit.Assert.assertNotNull(f_array25);
        org.junit.Assert.assertNotNull(f_array27);
        org.junit.Assert.assertTrue(b28 == false);
        org.junit.Assert.assertNotNull(f_array34);
        org.junit.Assert.assertNotNull(f_array36);
        org.junit.Assert.assertTrue(b37 == false);
        org.junit.Assert.assertTrue(b38 == false);
        org.junit.Assert.assertTrue(b39 == false);
        org.junit.Assert.assertTrue(b40 == false);
    }

    @Test
    public void test253() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test253");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = complex4.tanh();
        org.apache.commons.math.complex.Complex complex6 = complex4.sqrt();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
    }

    @Test
    public void test254() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test254");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 35.0d, (java.lang.Number) 0, true);
        boolean b5 = numberIsTooSmallException4.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b5 == true);
    }

    @Test
    public void test255() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test255");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) (short) 1, (double) (byte) 100);
    }

    @Test
    public void test256() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test256");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals(100.53096491487338d, 100.53096491487338d, (int) (short) -1);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test257() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test257");
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
        org.apache.commons.math.complex.Complex complex15 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex16 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex17 = complex15.divide(complex16);
        org.apache.commons.math.complex.Complex complex18 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex19 = complex18.tanh();
        org.apache.commons.math.complex.Complex complex20 = complex17.multiply(complex19);
        org.apache.commons.math.complex.Complex complex21 = complex14.add(complex19);
        org.apache.commons.math.complex.Complex complex22 = complex19.cos();
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
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
    }

    @Test
    public void test258() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test258");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (-2064879597L), (float) 0L, (float) (-10L));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test259() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test259");
        long long1 = org.apache.commons.math.util.MathUtils.indicator((-1636086455L));
        org.junit.Assert.assertTrue(long1 == (-1L));
    }

    @Test
    public void test260() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test260");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog((-2112331376), (int) (byte) 0);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test261() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test261");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) (-1), (java.lang.Number) (byte) 10, true);
        java.lang.Number number4 = numberIsTooLargeException3.getMax();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + (byte) 10 + "'", number4.equals((byte) 10));
    }

    @Test
    public void test262() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test262");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 362880000L, (java.lang.Number) 10.0d, true);
        java.lang.Number number4 = numberIsTooLargeException3.getMax();
        java.lang.Number number5 = numberIsTooLargeException3.getMax();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 10.0d + "'", number4.equals(10.0d));
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + 10.0d + "'", number5.equals(10.0d));
    }

    @Test
    public void test263() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test263");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.copyOf(d_array3);
        double d7 = org.apache.commons.math.util.MathUtils.safeNorm(d_array6);
        double[] d_array9 = new double[] { (-1.0f) };
        double[] d_array11 = org.apache.commons.math.util.MathUtils.copyOf(d_array9, 1);
        double[] d_array13 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array11, (double) 0L);
        double[] d_array14 = org.apache.commons.math.util.MathUtils.copyOf(d_array11);
        double[] d_array16 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array11, 349.9541180407703d);
        double[] d_array18 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array16, 0.0d);
        boolean b19 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array6, d_array16);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array16);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array16);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertTrue(d7 == 1.0d);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array14);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertTrue(b19 == false);
    }

    @Test
    public void test264() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test264");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) (-2112331476), 4950.0d, 0);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test265() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test265");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (byte) 10);
        java.lang.Number number2 = notPositiveException1.getMin();
        java.lang.Throwable[] throwable_array3 = notPositiveException1.getSuppressed();
        org.junit.Assert.assertTrue("'" + number2 + "' != '" + 0 + "'", number2.equals(0));
        org.junit.Assert.assertNotNull(throwable_array3);
    }

    @Test
    public void test266() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test266");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 1636086465, (double) 15235153920000L, (int) (short) 1);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test267() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test267");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        org.apache.commons.math.exception.util.Localizable localizable3 = null;
        java.lang.Object[] obj_array5 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException6 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable3, (java.lang.Number) 100.0d, obj_array5);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException7 = new org.apache.commons.math.exception.NullArgumentException(localizable2, obj_array5);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException8 = new org.apache.commons.math.exception.MathArithmeticException(localizable1, obj_array5);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException9 = new org.apache.commons.math.exception.NullArgumentException(localizable0, obj_array5);
        org.junit.Assert.assertNotNull(obj_array5);
    }

    @Test
    public void test268() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test268");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 99.70309990644455d, (java.lang.Number) 0.6830368552089112d, true);
        java.lang.Number number4 = numberIsTooSmallException3.getMin();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 0.6830368552089112d + "'", number4.equals(0.6830368552089112d));
    }

    @Test
    public void test269() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test269");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        int i4 = org.apache.commons.math.util.MathUtils.hash(d_array3);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException8 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection9 = nonMonotonousSequenceException8.getDirection();
        int i10 = nonMonotonousSequenceException8.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable11 = null;
        java.lang.Object[] obj_array13 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException14 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable11, (java.lang.Number) (short) 100, obj_array13);
        java.lang.Number number15 = notFiniteNumberException14.getArgument();
        nonMonotonousSequenceException8.addSuppressed((java.lang.Throwable) notFiniteNumberException14);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection17 = nonMonotonousSequenceException8.getDirection();
        boolean b20 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection17, true, false);
        double d21 = org.apache.commons.math.util.MathUtils.safeNorm(d_array3);
        double[] d_array23 = new double[] { (-1.0f) };
        double[] d_array25 = org.apache.commons.math.util.MathUtils.copyOf(d_array23, 1);
        double[] d_array27 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array25, (double) 0L);
        double[] d_array28 = org.apache.commons.math.util.MathUtils.copyOf(d_array25);
        double d29 = org.apache.commons.math.util.MathUtils.safeNorm(d_array28);
        boolean b30 = org.apache.commons.math.util.MathUtils.equals(d_array3, d_array28);
        org.apache.commons.math.exception.util.Localizable localizable31 = null;
        org.apache.commons.math.complex.Complex complex32 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex33 = complex32.cos();
        org.apache.commons.math.complex.Complex complex34 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex35 = complex34.cos();
        org.apache.commons.math.complex.Complex complex36 = complex32.subtract(complex34);
        org.apache.commons.math.complex.Complex complex37 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex38 = complex37.cos();
        org.apache.commons.math.complex.Complex complex39 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex40 = complex39.cos();
        org.apache.commons.math.complex.Complex complex41 = complex37.subtract(complex39);
        org.apache.commons.math.complex.Complex complex42 = complex32.add(complex41);
        double d43 = complex32.getArgument();
        org.apache.commons.math.complex.Complex complex44 = complex32.exp();
        org.apache.commons.math.complex.Complex complex45 = complex32.negate();
        org.apache.commons.math.complex.Complex complex46 = complex32.asin();
        org.apache.commons.math.complex.Complex complex47 = complex32.acos();
        org.apache.commons.math.exception.util.Localizable localizable48 = null;
        double[] d_array49 = new double[] {};
        double[] d_array53 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array57 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array61 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array65 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array66 = new double[][] { d_array53, d_array57, d_array61, d_array65 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array49, d_array_array66);
        double[] d_array68 = new double[] {};
        double[] d_array72 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array76 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array80 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array84 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array85 = new double[][] { d_array72, d_array76, d_array80, d_array84 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array68, d_array_array85);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array49, d_array_array85);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex32, localizable48, (java.lang.Object[]) d_array_array85);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException89 = new org.apache.commons.math.exception.MathArithmeticException(localizable31, (java.lang.Object[]) d_array_array85);
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array28, d_array_array85);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.DimensionMismatchException");
        } catch (org.apache.commons.math.exception.DimensionMismatchException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue(i4 == (-1074790369));
        org.junit.Assert.assertTrue("'" + orderDirection9 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection9.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i10 == 97);
        org.junit.Assert.assertTrue("'" + number15 + "' != '" + (short) 100 + "'", number15.equals((short) 100));
        org.junit.Assert.assertTrue("'" + orderDirection17 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection17.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b20 == true);
        org.junit.Assert.assertTrue(d21 == 1.0d);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array27);
        org.junit.Assert.assertNotNull(d_array28);
        org.junit.Assert.assertTrue(d29 == 1.0d);
        org.junit.Assert.assertTrue(b30 == true);
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
        org.junit.Assert.assertNotNull(complex42);
        org.junit.Assert.assertEquals((double) d43, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex44);
        org.junit.Assert.assertNotNull(complex45);
        org.junit.Assert.assertNotNull(complex46);
        org.junit.Assert.assertNotNull(complex47);
        org.junit.Assert.assertNotNull(d_array49);
        org.junit.Assert.assertNotNull(d_array53);
        org.junit.Assert.assertNotNull(d_array57);
        org.junit.Assert.assertNotNull(d_array61);
        org.junit.Assert.assertNotNull(d_array65);
        org.junit.Assert.assertNotNull(d_array_array66);
        org.junit.Assert.assertNotNull(d_array68);
        org.junit.Assert.assertNotNull(d_array72);
        org.junit.Assert.assertNotNull(d_array76);
        org.junit.Assert.assertNotNull(d_array80);
        org.junit.Assert.assertNotNull(d_array84);
        org.junit.Assert.assertNotNull(d_array_array85);
    }

    @Test
    public void test270() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test270");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex6 = complex2.nthRoot((int) (short) 10);
        org.apache.commons.math.complex.Complex complex7 = complex2.sqrt1z();
        org.apache.commons.math.complex.Complex complex8 = complex2.exp();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(list_complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
    }

    @Test
    public void test271() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test271");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.sqrt();
        org.apache.commons.math.complex.Complex complex3 = complex2.sqrt1z();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
    }

    @Test
    public void test272() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test272");
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
        org.apache.commons.math.complex.Complex complex27 = complex24.negate();
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
    }

    @Test
    public void test273() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test273");
        int i1 = org.apache.commons.math.util.MathUtils.sign(11);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test274() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test274");
        java.lang.Number number0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        double[] d_array2 = new double[] {};
        double[] d_array6 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array10 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array14 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array18 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array19 = new double[][] { d_array6, d_array10, d_array14, d_array18 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array2, d_array_array19);
        double[] d_array21 = new double[] {};
        double[] d_array25 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array29 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array33 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array37 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array38 = new double[][] { d_array25, d_array29, d_array33, d_array37 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array21, d_array_array38);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array2, d_array_array38);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException41 = new org.apache.commons.math.exception.MathArithmeticException(localizable1, (java.lang.Object[]) d_array_array38);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException42 = new org.apache.commons.math.exception.NotFiniteNumberException(number0, (java.lang.Object[]) d_array_array38);
        org.junit.Assert.assertNotNull(d_array2);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertNotNull(d_array14);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertNotNull(d_array_array19);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array33);
        org.junit.Assert.assertNotNull(d_array37);
        org.junit.Assert.assertNotNull(d_array_array38);
    }

    @Test
    public void test275() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test275");
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
        int i36 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array35);
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
        org.junit.Assert.assertTrue(i36 == 0);
    }

    @Test
    public void test276() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test276");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((long) (-447362047), 1L);
        org.junit.Assert.assertTrue(long2 == 447362047L);
    }

    @Test
    public void test277() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test277");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        java.lang.Number number4 = nonMonotonousSequenceException3.getPrevious();
        java.lang.Number number5 = nonMonotonousSequenceException3.getPrevious();
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext6 = nonMonotonousSequenceException3.getContext();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 2.2250738585072014E-308d + "'", number4.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + number5 + "' != '" + 2.2250738585072014E-308d + "'", number5.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertNotNull(exceptionContext6);
    }

    @Test
    public void test278() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test278");
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
        double[] d_array88 = new double[] { (byte) -1 };
        double[] d_array90 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array88, (double) (byte) 1);
        boolean b91 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array65, d_array88);
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array65);
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
        org.junit.Assert.assertNotNull(d_array88);
        org.junit.Assert.assertNotNull(d_array90);
        org.junit.Assert.assertTrue(b91 == false);
    }

    @Test
    public void test279() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test279");
        java.lang.Number number1 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 100.0d, number1, 0);
        java.lang.Number number4 = nonMonotonousSequenceException3.getPrevious();
        org.junit.Assert.assertNull(number4);
    }

    @Test
    public void test280() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test280");
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection6 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException8 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) Double.POSITIVE_INFINITY, (java.lang.Number) 1.0f, 0, orderDirection6, true);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException10 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 10.0f, (java.lang.Number) 0, 1075970048, orderDirection6, true);
        java.lang.Number number11 = nonMonotonousSequenceException10.getArgument();
        org.junit.Assert.assertTrue("'" + orderDirection6 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection6.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue("'" + number11 + "' != '" + 10.0f + "'", number11.equals(10.0f));
    }

    @Test
    public void test281() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test281");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        int i19 = org.apache.commons.math.util.MathUtils.hash(d_array0);
        double[] d_array26 = new double[] { 1.3440585709080678E43d, 36.0d, 4950.0d, 99.70309990644455d, Double.POSITIVE_INFINITY, 99.70309990644455d };
        boolean b27 = org.apache.commons.math.util.MathUtils.equals(d_array0, d_array26);
        try {
            double[] d_array29 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array0, 1.0d);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertTrue(i19 == 1);
        org.junit.Assert.assertNotNull(d_array26);
        org.junit.Assert.assertTrue(b27 == false);
    }

    @Test
    public void test282() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test282");
        int i1 = org.apache.commons.math.util.MathUtils.indicator(0);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test283() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test283");
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
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection59 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException61 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) Double.POSITIVE_INFINITY, (java.lang.Number) 1.0f, 0, orderDirection59, true);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException63 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 10.0f, (java.lang.Number) 0, 1075970048, orderDirection59, true);
        org.apache.commons.math.exception.util.Localizable localizable64 = null;
        double[] d_array65 = new double[] {};
        double[] d_array69 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array73 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array77 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array81 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array82 = new double[][] { d_array69, d_array73, d_array77, d_array81 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array65, d_array_array82);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException84 = new org.apache.commons.math.exception.NullArgumentException(localizable64, (java.lang.Object[]) d_array_array82);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, orderDirection59, d_array_array82);
        double[][] d_array_array86 = null;
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array5, d_array_array86);
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
        org.junit.Assert.assertTrue("'" + orderDirection59 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection59.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertNotNull(d_array65);
        org.junit.Assert.assertNotNull(d_array69);
        org.junit.Assert.assertNotNull(d_array73);
        org.junit.Assert.assertNotNull(d_array77);
        org.junit.Assert.assertNotNull(d_array81);
        org.junit.Assert.assertNotNull(d_array_array82);
    }

    @Test
    public void test284() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test284");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) 362880L, (double) 35L, (-2112331476));
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test285() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test285");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.copyOf(d_array3);
        double[] d_array8 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, 349.9541180407703d);
        double[] d_array9 = new double[] {};
        double[] d_array13 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array17 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array25 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array26 = new double[][] { d_array13, d_array17, d_array21, d_array25 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array9, d_array_array26);
        double[] d_array28 = new double[] {};
        double[] d_array32 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array36 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array40 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array44 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array45 = new double[][] { d_array32, d_array36, d_array40, d_array44 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array28, d_array_array45);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array9, d_array_array45);
        try {
            org.apache.commons.math.util.MathUtils.sortInPlace(d_array8, d_array_array45);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.DimensionMismatchException");
        } catch (org.apache.commons.math.exception.DimensionMismatchException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array_array26);
        org.junit.Assert.assertNotNull(d_array28);
        org.junit.Assert.assertNotNull(d_array32);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertNotNull(d_array44);
        org.junit.Assert.assertNotNull(d_array_array45);
    }

    @Test
    public void test286() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test286");
        double d1 = org.apache.commons.math.util.MathUtils.indicator((double) (short) 0);
        org.junit.Assert.assertTrue(d1 == 1.0d);
    }

    @Test
    public void test287() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test287");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex5 = complex4.cos();
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex7 = complex6.cos();
        org.apache.commons.math.complex.Complex complex8 = complex4.subtract(complex6);
        org.apache.commons.math.complex.Complex complex9 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex10 = complex9.cos();
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = complex11.cos();
        org.apache.commons.math.complex.Complex complex13 = complex9.subtract(complex11);
        org.apache.commons.math.complex.Complex complex14 = complex4.add(complex13);
        double d15 = complex4.getArgument();
        org.apache.commons.math.complex.Complex complex16 = complex4.exp();
        org.apache.commons.math.complex.Complex complex17 = complex4.negate();
        org.apache.commons.math.complex.Complex complex18 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex19 = complex18.cos();
        org.apache.commons.math.complex.Complex complex20 = complex4.subtract(complex18);
        org.apache.commons.math.complex.Complex complex21 = complex20.asin();
        org.apache.commons.math.complex.Complex complex22 = complex21.log();
        org.apache.commons.math.complex.Complex complex23 = complex1.subtract(complex21);
        double d24 = complex23.abs();
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
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertEquals((double) d15, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertEquals((double) d24, Double.NaN, 0);
    }

    @Test
    public void test288() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test288");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck(3, (-447362044));
        org.junit.Assert.assertTrue(i2 == (-447362041));
    }

    @Test
    public void test289() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test289");
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
        org.apache.commons.math.complex.Complex complex37 = complex36.negate();
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
    public void test290() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test290");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(1.3440585709080678E43d, (double) (short) 0, (double) 10.0f);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test291() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test291");
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
        org.apache.commons.math.complex.Complex complex37 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex38 = complex37.cos();
        org.apache.commons.math.complex.Complex complex39 = complex30.subtract(complex38);
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
    }

    @Test
    public void test292() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test292");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(99.70309990644455d, (double) 3L, (int) (short) 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test293() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test293");
        double[] d_array0 = null;
        java.lang.Number number1 = null;
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException6 = new org.apache.commons.math.exception.NonMonotonousSequenceException(number1, (java.lang.Number) (byte) 0, (-1074790400), orderDirection4, true);
        try {
            boolean b9 = org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection4, true, true);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
    }

    @Test
    public void test294() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test294");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.tan();
        double d7 = complex6.getReal();
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex6);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertEquals((double) d7, Double.NaN, 0);
    }

    @Test
    public void test295() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test295");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck((-1636086455L), 0L);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test296() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test296");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 10.04987562112089d, (java.lang.Number) (-1), true);
        java.lang.Number number4 = numberIsTooLargeException3.getMax();
        boolean b5 = numberIsTooLargeException3.getBoundIsAllowed();
        java.lang.Throwable[] throwable_array6 = numberIsTooLargeException3.getSuppressed();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + (-1) + "'", number4.equals((-1)));
        org.junit.Assert.assertTrue(b5 == true);
        org.junit.Assert.assertNotNull(throwable_array6);
    }

    @Test
    public void test297() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test297");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) (-1.0d), (java.lang.Number) 42, false);
        boolean b4 = numberIsTooLargeException3.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b4 == false);
    }

    @Test
    public void test298() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test298");
        long long1 = org.apache.commons.math.util.MathUtils.indicator(6336994428L);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test299() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test299");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 42);
        java.math.BigInteger bigInteger6 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 1);
        java.math.BigInteger bigInteger8 = org.apache.commons.math.util.MathUtils.pow(bigInteger6, 97);
        java.math.BigInteger bigInteger9 = null;
        try {
            java.math.BigInteger bigInteger10 = org.apache.commons.math.util.MathUtils.pow(bigInteger6, bigInteger9);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger2);
        org.junit.Assert.assertNotNull(bigInteger4);
        org.junit.Assert.assertNotNull(bigInteger6);
        org.junit.Assert.assertNotNull(bigInteger8);
    }

    @Test
    public void test300() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test300");
        int i2 = org.apache.commons.math.util.MathUtils.addAndCheck((-2112331376), (int) (short) 100);
        org.junit.Assert.assertTrue(i2 == (-2112331276));
    }

    @Test
    public void test301() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test301");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 1, (int) (byte) 0);
    }

    @Test
    public void test302() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test302");
        java.math.BigInteger bigInteger1 = null;
        java.math.BigInteger bigInteger3 = org.apache.commons.math.util.MathUtils.pow(bigInteger1, (long) 0);
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException5 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 1072693248, (java.lang.Number) 0, false);
        org.junit.Assert.assertNotNull(bigInteger3);
    }

    @Test
    public void test303() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test303");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((-2064879597L), 0L);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test304() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test304");
        long long1 = org.apache.commons.math.util.MathUtils.indicator((-7500783708504147263L));
        org.junit.Assert.assertTrue(long1 == (-1L));
    }

    @Test
    public void test305() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test305");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException(0, 1074790400);
    }

    @Test
    public void test306() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test306");
        int i1 = org.apache.commons.math.util.MathUtils.hash(1.3440585709080678E43d);
        org.junit.Assert.assertTrue(i1 == (-738962753));
    }

    @Test
    public void test307() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test307");
        float f1 = org.apache.commons.math.util.MathUtils.indicator((float) 100);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test308() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test308");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck((-1L), (long) 98);
        org.junit.Assert.assertTrue(long2 == (-98L));
    }

    @Test
    public void test309() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test309");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(6.283185307179586d, 0.6830368552089112d, 9);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test310() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test310");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (-9));
        java.lang.Number number2 = notPositiveException1.getMin();
        org.junit.Assert.assertTrue("'" + number2 + "' != '" + 0 + "'", number2.equals(0));
    }

    @Test
    public void test311() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test311");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 0, (int) '4');
        int i4 = dimensionMismatchException3.getDimension();
        org.junit.Assert.assertTrue(i4 == 52);
    }

    @Test
    public void test312() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test312");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 161700L, (float) (short) -1, 0);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test313() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test313");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) (-2112331476), (java.lang.Number) 3628800L, 1636086465);
        java.lang.Number number4 = nonMonotonousSequenceException3.getPrevious();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 3628800L + "'", number4.equals(3628800L));
    }

    @Test
    public void test314() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test314");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.tanh();
        org.apache.commons.math.complex.Complex complex5 = complex2.multiply(complex4);
        org.apache.commons.math.complex.Complex complex6 = complex2.asin();
        org.apache.commons.math.complex.Complex complex7 = complex6.sqrt1z();
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
    public void test315() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test315");
        long long2 = org.apache.commons.math.util.MathUtils.lcm(0L, (long) (-1074790400));
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test316() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test316");
        double[] d_array4 = new double[] { (byte) -1 };
        double[] d_array6 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array4, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection7 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b10 = org.apache.commons.math.util.MathUtils.checkOrder(d_array6, orderDirection7, true, false);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException12 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 10, (java.lang.Number) 1.0d, (-1074790400), orderDirection7, true);
        boolean b13 = nonMonotonousSequenceException12.getStrict();
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertTrue("'" + orderDirection7 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection7.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b10 == true);
        org.junit.Assert.assertTrue(b13 == true);
    }

    @Test
    public void test317() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test317");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 100);
    }

    @Test
    public void test318() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test318");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck(52, (int) ' ');
        org.junit.Assert.assertTrue(i2 == 20);
    }

    @Test
    public void test319() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test319");
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
        org.apache.commons.math.complex.Complex complex20 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex21 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex22 = complex20.divide(complex21);
        org.apache.commons.math.complex.Complex complex23 = complex19.pow(complex20);
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
    }

    @Test
    public void test320() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test320");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b7 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection4, true, false);
        double[] d_array9 = org.apache.commons.math.util.MathUtils.copyOf(d_array3, 97);
        double[] d_array11 = new double[] { (-1.0f) };
        double[] d_array13 = org.apache.commons.math.util.MathUtils.copyOf(d_array11, 1);
        double[] d_array15 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array13, (double) 0L);
        double[] d_array17 = new double[] { (byte) -1 };
        double[] d_array19 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array17, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection20 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b23 = org.apache.commons.math.util.MathUtils.checkOrder(d_array19, orderDirection20, true, false);
        boolean b24 = org.apache.commons.math.util.MathUtils.equals(d_array13, d_array19);
        try {
            double d25 = org.apache.commons.math.util.MathUtils.distanceInf(d_array9, d_array13);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b7 == true);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertNotNull(d_array11);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array19);
        org.junit.Assert.assertTrue("'" + orderDirection20 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection20.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b23 == true);
        org.junit.Assert.assertTrue(b24 == false);
    }

    @Test
    public void test321() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test321");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (-73931601660L), (float) (-2112331376), 0.0f);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test322() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test322");
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
        org.apache.commons.math.complex.Complex complex27 = complex24.sinh();
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
    }

    @Test
    public void test323() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test323");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.exp();
        boolean b3 = complex1.isInfinite();
        org.apache.commons.math.complex.ComplexField complexField4 = complex1.getField();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertTrue(b3 == false);
        org.junit.Assert.assertNotNull(complexField4);
    }

    @Test
    public void test324() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test324");
        long long1 = org.apache.commons.math.util.MathUtils.sign((long) 1636086465);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test325() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test325");
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
        java.lang.String str19 = complex18.toString();
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
        org.junit.Assert.assertTrue("'" + str19 + "' != '" + "(NaN, NaN)" + "'", str19.equals("(NaN, NaN)"));
    }

    @Test
    public void test326() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test326");
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
        org.apache.commons.math.complex.Complex complex22 = complex19.asin();
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
        org.junit.Assert.assertNotNull(complex22);
    }

    @Test
    public void test327() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test327");
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
        org.apache.commons.math.complex.Complex complex17 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex18 = complex17.acos();
        org.apache.commons.math.complex.Complex complex19 = complex18.sqrt1z();
        org.apache.commons.math.complex.Complex complex20 = complex0.pow(complex18);
        boolean b21 = complex18.isNaN();
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
        org.junit.Assert.assertTrue(b21 == false);
    }

    @Test
    public void test328() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test328");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(1.63608653E9f, (float) 6336994428L, (-1.0f));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test329() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test329");
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
        boolean b29 = complex28.isInfinite();
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
        org.junit.Assert.assertTrue(b29 == false);
    }

    @Test
    public void test330() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test330");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 9L, (java.lang.Number) 447362047L, false);
    }

    @Test
    public void test331() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test331");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 26.0d, (java.lang.Number) (-1.1752011936438014d), (-1074790369));
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext4 = nonMonotonousSequenceException3.getContext();
        org.junit.Assert.assertNotNull(exceptionContext4);
    }

    @Test
    public void test332() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test332");
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
        org.apache.commons.math.complex.Complex complex13 = complex11.log();
        org.apache.commons.math.complex.Complex complex14 = complex11.atan();
        org.apache.commons.math.complex.Complex complex15 = complex11.negate();
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
    }

    @Test
    public void test333() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test333");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 1.3440585709080678E43d, (java.lang.Number) (byte) 1, false);
        boolean b4 = numberIsTooLargeException3.getBoundIsAllowed();
        boolean b5 = numberIsTooLargeException3.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b4 == false);
        org.junit.Assert.assertTrue(b5 == false);
    }

    @Test
    public void test334() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test334");
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
        double d19 = complex18.getArgument();
        org.apache.commons.math.complex.Complex complex20 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex21 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex22 = complex20.divide(complex21);
        org.apache.commons.math.complex.Complex complex23 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex24 = complex23.acos();
        org.apache.commons.math.complex.Complex complex25 = complex21.divide(complex23);
        org.apache.commons.math.complex.Complex complex26 = complex21.log();
        java.lang.String str27 = complex21.toString();
        org.apache.commons.math.complex.Complex complex28 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex29 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex30 = complex28.divide(complex29);
        org.apache.commons.math.complex.Complex complex31 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex32 = complex31.tanh();
        org.apache.commons.math.complex.Complex complex33 = complex30.multiply(complex32);
        org.apache.commons.math.complex.Complex complex34 = complex21.divide(complex30);
        org.apache.commons.math.complex.Complex complex35 = complex18.divide(complex34);
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
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complex24);
        org.junit.Assert.assertNotNull(complex25);
        org.junit.Assert.assertNotNull(complex26);
        org.junit.Assert.assertTrue("'" + str27 + "' != '" + "(NaN, NaN)" + "'", str27.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex28);
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertNotNull(complex30);
        org.junit.Assert.assertNotNull(complex31);
        org.junit.Assert.assertNotNull(complex32);
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(complex34);
        org.junit.Assert.assertNotNull(complex35);
    }

    @Test
    public void test335() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test335");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        boolean b7 = complex1.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertTrue(b7 == true);
    }

    @Test
    public void test336() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test336");
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
        org.apache.commons.math.complex.Complex complex29 = complex24.multiply(2.2250738585072014E-308d);
        org.apache.commons.math.complex.Complex complex30 = complex24.sin();
        try {
            java.util.List<org.apache.commons.math.complex.Complex> list_complex32 = complex30.nthRoot((-1074790369));
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
        org.junit.Assert.assertNotNull(complex29);
        org.junit.Assert.assertNotNull(complex30);
    }

    @Test
    public void test337() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test337");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.copyOf(d_array3);
        double[] d_array8 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, 349.9541180407703d);
        double[] d_array10 = new double[] { (-1.0f) };
        double[] d_array12 = org.apache.commons.math.util.MathUtils.copyOf(d_array10, 1);
        double[] d_array14 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array12, (double) 0L);
        double[] d_array15 = org.apache.commons.math.util.MathUtils.copyOf(d_array12);
        double d16 = org.apache.commons.math.util.MathUtils.safeNorm(d_array15);
        double[] d_array18 = new double[] { (-1.0f) };
        double[] d_array20 = org.apache.commons.math.util.MathUtils.copyOf(d_array18, 1);
        double d21 = org.apache.commons.math.util.MathUtils.safeNorm(d_array18);
        double[] d_array23 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array18, 1.1752011936438014d);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array23);
        double[] d_array28 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d29 = org.apache.commons.math.util.MathUtils.safeNorm(d_array28);
        double[] d_array30 = new double[] {};
        double[] d_array34 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array38 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array42 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array46 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array47 = new double[][] { d_array34, d_array38, d_array42, d_array46 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array30, d_array_array47);
        int i49 = org.apache.commons.math.util.MathUtils.hash(d_array30);
        boolean b50 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array28, d_array30);
        double[] d_array54 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d55 = org.apache.commons.math.util.MathUtils.safeNorm(d_array54);
        double[] d_array56 = new double[] {};
        double[] d_array60 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array64 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array68 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array72 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array73 = new double[][] { d_array60, d_array64, d_array68, d_array72 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array56, d_array_array73);
        int i75 = org.apache.commons.math.util.MathUtils.hash(d_array56);
        boolean b76 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array54, d_array56);
        boolean b77 = org.apache.commons.math.util.MathUtils.equals(d_array30, d_array54);
        double[] d_array79 = org.apache.commons.math.util.MathUtils.copyOf(d_array30, 0);
        boolean b80 = org.apache.commons.math.util.MathUtils.equals(d_array23, d_array30);
        double d81 = org.apache.commons.math.util.MathUtils.distance1(d_array15, d_array23);
        boolean b82 = org.apache.commons.math.util.MathUtils.equals(d_array3, d_array15);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array10);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array14);
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertTrue(d16 == 1.0d);
        org.junit.Assert.assertNotNull(d_array18);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertTrue(d21 == 1.0d);
        org.junit.Assert.assertNotNull(d_array23);
        org.junit.Assert.assertNotNull(d_array28);
        org.junit.Assert.assertTrue(d29 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array30);
        org.junit.Assert.assertNotNull(d_array34);
        org.junit.Assert.assertNotNull(d_array38);
        org.junit.Assert.assertNotNull(d_array42);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertNotNull(d_array_array47);
        org.junit.Assert.assertTrue(i49 == 1);
        org.junit.Assert.assertTrue(b50 == false);
        org.junit.Assert.assertNotNull(d_array54);
        org.junit.Assert.assertTrue(d55 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array56);
        org.junit.Assert.assertNotNull(d_array60);
        org.junit.Assert.assertNotNull(d_array64);
        org.junit.Assert.assertNotNull(d_array68);
        org.junit.Assert.assertNotNull(d_array72);
        org.junit.Assert.assertNotNull(d_array_array73);
        org.junit.Assert.assertTrue(i75 == 1);
        org.junit.Assert.assertTrue(b76 == false);
        org.junit.Assert.assertTrue(b77 == false);
        org.junit.Assert.assertNotNull(d_array79);
        org.junit.Assert.assertTrue(b80 == false);
        org.junit.Assert.assertTrue(d81 == 2.1752011936438014d);
        org.junit.Assert.assertTrue(b82 == true);
    }

    @Test
    public void test338() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test338");
        double d1 = org.apache.commons.math.util.MathUtils.sinh((-0.9117339147869651d));
        org.junit.Assert.assertTrue(d1 == (-1.0434034937813494d));
    }

    @Test
    public void test339() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test339");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        org.apache.commons.math.exception.util.Localizable localizable3 = null;
        org.apache.commons.math.exception.util.Localizable localizable4 = null;
        java.lang.Object[] obj_array6 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException7 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable4, (java.lang.Number) 100.0d, obj_array6);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException8 = new org.apache.commons.math.exception.NullArgumentException(localizable3, obj_array6);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) 6.283185307179586d, localizable2, obj_array6);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException10 = new org.apache.commons.math.exception.NullArgumentException(localizable0, obj_array6);
        org.junit.Assert.assertNotNull(obj_array6);
    }

    @Test
    public void test340() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test340");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 0, 0);
    }

    @Test
    public void test341() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test341");
        int i2 = org.apache.commons.math.util.MathUtils.gcd(1075970048, 9);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test342() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test342");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 19683, (java.lang.Number) 11, 10);
        org.apache.commons.math.exception.NotPositiveException notPositiveException5 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (byte) 10);
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notPositiveException5);
    }

    @Test
    public void test343() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test343");
        int i2 = org.apache.commons.math.util.MathUtils.pow(3, (long) (short) 1);
        org.junit.Assert.assertTrue(i2 == 3);
    }

    @Test
    public void test344() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test344");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) 3);
    }

    @Test
    public void test345() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test345");
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
        org.apache.commons.math.complex.Complex complex13 = complex11.log();
        org.apache.commons.math.complex.Complex complex14 = complex11.atan();
        org.apache.commons.math.complex.Complex complex15 = complex11.tanh();
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
    }

    @Test
    public void test346() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test346");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.tanh();
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex0);
        java.lang.String str3 = complex0.toString();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str3 + "' != '" + "(0.0, 0.0)" + "'", str3.equals("(0.0, 0.0)"));
    }

    @Test
    public void test347() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test347");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) (-1636086455L), 0.0f, (float) (short) 10);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test348() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test348");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) (-447362041), 99.35576372122958d, 0.0d);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test349() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test349");
        float f3 = org.apache.commons.math.util.MathUtils.round((float) 57L, (int) (short) 100, 0);
        org.junit.Assert.assertEquals((float) f3, Float.NaN, 0);
    }

    @Test
    public void test350() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test350");
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection3 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException5 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) (-1.1752011936438014d), (java.lang.Number) 92561040L, (int) ' ', orderDirection3, true);
    }

    @Test
    public void test351() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test351");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        org.apache.commons.math.complex.Complex complex7 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex8 = complex1.sqrt1z();
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
    public void test352() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test352");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck(3, 11);
        org.junit.Assert.assertTrue(i2 == (-8));
    }

    @Test
    public void test353() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test353");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo((double) (-2064879597L), (double) 7766279631452241920L, 98);
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test354() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test354");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException((-8), (int) (byte) 1);
    }

    @Test
    public void test355() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test355");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.pow(36288000L, (-73931601660L));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test356() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test356");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (short) 0, (double) 100.0f);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test357() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test357");
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
        int[] i_array17 = org.apache.commons.math.util.MathUtils.copyOf(i_array15);
        int[] i_array18 = new int[] {};
        int[] i_array19 = org.apache.commons.math.util.MathUtils.copyOf(i_array18);
        double d20 = org.apache.commons.math.util.MathUtils.distance(i_array17, i_array19);
        int[] i_array27 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array29 = org.apache.commons.math.util.MathUtils.copyOf(i_array27, (int) '4');
        int i30 = org.apache.commons.math.util.MathUtils.distanceInf(i_array19, i_array27);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) i_array27);
        int i32 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array27);
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
        org.junit.Assert.assertNotNull(i_array17);
        org.junit.Assert.assertNotNull(i_array18);
        org.junit.Assert.assertNotNull(i_array19);
        org.junit.Assert.assertTrue(d20 == 0.0d);
        org.junit.Assert.assertNotNull(i_array27);
        org.junit.Assert.assertNotNull(i_array29);
        org.junit.Assert.assertTrue(i30 == 0);
        org.junit.Assert.assertTrue(i32 == 0);
    }

    @Test
    public void test358() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test358");
        int i1 = org.apache.commons.math.util.MathUtils.hash((double) (-98L));
        org.junit.Assert.assertTrue(i1 == (-1067941888));
    }

    @Test
    public void test359() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test359");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) 2, (float) (-1636086455L), 0.0f);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test360() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test360");
        float f3 = org.apache.commons.math.util.MathUtils.round((float) 3, 1, 2);
        org.junit.Assert.assertTrue(f3 == 3.1f);
    }

    @Test
    public void test361() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test361");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(Float.NaN, (float) 7766279631452241920L, (-9));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test362() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test362");
        double d2 = org.apache.commons.math.util.MathUtils.normalizeAngle((double) ' ', (double) (-9));
        org.junit.Assert.assertTrue(d2 == (-11.982297150257104d));
    }

    @Test
    public void test363() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test363");
        int i2 = org.apache.commons.math.util.MathUtils.subAndCheck((int) (short) 100, 0);
        org.junit.Assert.assertTrue(i2 == 100);
    }

    @Test
    public void test364() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test364");
        int i1 = org.apache.commons.math.util.MathUtils.indicator(3);
        org.junit.Assert.assertTrue(i1 == 1);
    }

    @Test
    public void test365() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test365");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        java.lang.Number number4 = nonMonotonousSequenceException3.getPrevious();
        java.lang.String str5 = nonMonotonousSequenceException3.toString();
        boolean b6 = nonMonotonousSequenceException3.getStrict();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection7 = nonMonotonousSequenceException3.getDirection();
        java.lang.Number number8 = nonMonotonousSequenceException3.getPrevious();
        java.lang.Number number9 = nonMonotonousSequenceException3.getArgument();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 2.2250738585072014E-308d + "'", number4.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + str5 + "' != '" + "org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)" + "'", str5.equals("org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)"));
        org.junit.Assert.assertTrue(b6 == true);
        org.junit.Assert.assertTrue("'" + orderDirection7 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection7.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue("'" + number8 + "' != '" + 2.2250738585072014E-308d + "'", number8.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + number9 + "' != '" + 2.2250738585072014E-308d + "'", number9.equals(2.2250738585072014E-308d));
    }

    @Test
    public void test366() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test366");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        float[] f_array6 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array8 = new float[] { (-1074790400) };
        boolean b9 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array6, f_array8);
        java.lang.Object[] obj_array15 = new java.lang.Object[] { b9, 0, 0, (-1.0f), 100.0f, (short) 1 };
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException16 = new org.apache.commons.math.exception.MathArithmeticException(localizable0, obj_array15);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext17 = mathArithmeticException16.getContext();
        org.junit.Assert.assertNotNull(f_array6);
        org.junit.Assert.assertNotNull(f_array8);
        org.junit.Assert.assertTrue(b9 == false);
        org.junit.Assert.assertNotNull(obj_array15);
        org.junit.Assert.assertNotNull(exceptionContext17);
    }

    @Test
    public void test367() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test367");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        org.apache.commons.math.complex.Complex complex5 = complex4.log();
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex7 = complex6.cosh();
        org.apache.commons.math.complex.Complex complex8 = complex6.asin();
        org.apache.commons.math.complex.Complex complex9 = complex4.multiply(complex6);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex6);
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
    }

    @Test
    public void test368() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test368");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) (-1074790369), (double) 100);
        org.apache.commons.math.complex.Complex complex3 = complex2.acos();
        org.apache.commons.math.complex.Complex complex4 = complex2.conjugate();
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
    }

    @Test
    public void test369() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test369");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient(0, 1074790400);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test370() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test370");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((-0.0d), (double) 1636086544L);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test371() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test371");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array7 = new double[] { (byte) -1 };
        double[] d_array9 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array7, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection10 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b13 = org.apache.commons.math.util.MathUtils.checkOrder(d_array9, orderDirection10, true, false);
        boolean b14 = org.apache.commons.math.util.MathUtils.equals(d_array3, d_array9);
        int i15 = org.apache.commons.math.util.MathUtils.hash(d_array3);
        org.apache.commons.math.util.MathUtils.checkOrder(d_array3);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array7);
        org.junit.Assert.assertNotNull(d_array9);
        org.junit.Assert.assertTrue("'" + orderDirection10 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection10.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b13 == true);
        org.junit.Assert.assertTrue(b14 == false);
        org.junit.Assert.assertTrue(i15 == (-1074790369));
    }

    @Test
    public void test372() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test372");
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException2 = new org.apache.commons.math.exception.DimensionMismatchException((-1074790400), (int) (byte) -1);
        int i3 = dimensionMismatchException2.getDimension();
        org.junit.Assert.assertTrue(i3 == (-1));
    }

    @Test
    public void test373() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test373");
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
        org.apache.commons.math.complex.Complex complex19 = complex18.tan();
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
    }

    @Test
    public void test374() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test374");
        long long2 = org.apache.commons.math.util.MathUtils.pow((long) 52, 1072693248);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test375() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test375");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble((-447362041), (int) (byte) 10);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test376() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test376");
        long long2 = org.apache.commons.math.util.MathUtils.gcd(7766279631452241920L, (long) (-447362047));
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test377() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test377");
        try {
            int i2 = org.apache.commons.math.util.MathUtils.lcm((int) (byte) 10, (-2064878497));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test378() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test378");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex0.abs();
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex0.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex7 = complex6.cos();
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = complex8.cos();
        org.apache.commons.math.complex.Complex complex10 = complex6.subtract(complex8);
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = complex11.cos();
        org.apache.commons.math.complex.Complex complex13 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex14 = complex13.cos();
        org.apache.commons.math.complex.Complex complex15 = complex11.subtract(complex13);
        org.apache.commons.math.complex.Complex complex16 = complex6.add(complex15);
        double d17 = complex6.getArgument();
        org.apache.commons.math.complex.Complex complex18 = complex6.exp();
        org.apache.commons.math.complex.Complex complex19 = complex6.negate();
        org.apache.commons.math.complex.Complex complex20 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex21 = complex20.cos();
        org.apache.commons.math.complex.Complex complex22 = complex6.subtract(complex20);
        org.apache.commons.math.complex.Complex complex23 = complex22.cosh();
        org.apache.commons.math.complex.Complex complex24 = complex5.multiply(complex22);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex26 = complex5.nthRoot(100);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 0.0d);
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
        org.junit.Assert.assertEquals((double) d17, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complex24);
        org.junit.Assert.assertNotNull(list_complex26);
    }

    @Test
    public void test379() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test379");
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) 3628800L);
    }

    @Test
    public void test380() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test380");
        int i2 = org.apache.commons.math.util.MathUtils.gcd((-2112331376), 1);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test381() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test381");
        long long2 = org.apache.commons.math.util.MathUtils.lcm((long) (short) 1, (long) 97);
        org.junit.Assert.assertTrue(long2 == 97L);
    }

    @Test
    public void test382() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test382");
        int i3 = org.apache.commons.math.util.MathUtils.compareTo(0.6830368552089112d, (double) (-1074790369), (-447362044));
        org.junit.Assert.assertTrue(i3 == 1);
    }

    @Test
    public void test383() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test383");
        long long2 = org.apache.commons.math.util.MathUtils.pow((-1636086455L), 33);
        org.junit.Assert.assertTrue(long2 == (-7613835992134657463L));
    }

    @Test
    public void test384() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test384");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) 4, (double) 1L);
    }

    @Test
    public void test385() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test385");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex5 = complex4.cos();
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex7 = complex6.cos();
        org.apache.commons.math.complex.Complex complex8 = complex4.subtract(complex6);
        org.apache.commons.math.complex.Complex complex9 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex10 = complex9.cos();
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = complex11.cos();
        org.apache.commons.math.complex.Complex complex13 = complex9.subtract(complex11);
        org.apache.commons.math.complex.Complex complex14 = complex4.add(complex13);
        double d15 = complex4.getArgument();
        org.apache.commons.math.complex.Complex complex16 = complex4.exp();
        org.apache.commons.math.complex.Complex complex17 = complex4.negate();
        org.apache.commons.math.complex.Complex complex18 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex19 = complex18.cos();
        org.apache.commons.math.complex.Complex complex20 = complex4.subtract(complex18);
        org.apache.commons.math.complex.Complex complex21 = complex20.asin();
        org.apache.commons.math.complex.Complex complex22 = complex21.log();
        org.apache.commons.math.complex.Complex complex23 = complex1.subtract(complex21);
        org.apache.commons.math.complex.ComplexField complexField24 = complex21.getField();
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
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertEquals((double) d15, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
        org.junit.Assert.assertNotNull(complex18);
        org.junit.Assert.assertNotNull(complex19);
        org.junit.Assert.assertNotNull(complex20);
        org.junit.Assert.assertNotNull(complex21);
        org.junit.Assert.assertNotNull(complex22);
        org.junit.Assert.assertNotNull(complex23);
        org.junit.Assert.assertNotNull(complexField24);
    }

    @Test
    public void test386() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test386");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = complex1.exp();
        org.apache.commons.math.complex.Complex complex3 = complex2.sqrt();
        org.apache.commons.math.complex.Complex complex4 = complex2.cos();
        org.apache.commons.math.complex.Complex complex5 = complex2.sinh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
    }

    @Test
    public void test387() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test387");
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
        org.apache.commons.math.exception.util.Localizable localizable16 = null;
        double[] d_array17 = new double[] {};
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array25 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array29 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array33 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array34 = new double[][] { d_array21, d_array25, d_array29, d_array33 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array17, d_array_array34);
        double[] d_array36 = new double[] {};
        double[] d_array40 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array44 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array48 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array52 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array53 = new double[][] { d_array40, d_array44, d_array48, d_array52 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array36, d_array_array53);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array17, d_array_array53);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex0, localizable16, (java.lang.Object[]) d_array_array53);
        org.apache.commons.math.complex.Complex complex59 = new org.apache.commons.math.complex.Complex(197.05131665139353d, (double) 3628800L);
        boolean b60 = complex59.isInfinite();
        org.apache.commons.math.complex.Complex complex61 = complex0.subtract(complex59);
        double d62 = complex0.getImaginary();
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
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array33);
        org.junit.Assert.assertNotNull(d_array_array34);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertNotNull(d_array44);
        org.junit.Assert.assertNotNull(d_array48);
        org.junit.Assert.assertNotNull(d_array52);
        org.junit.Assert.assertNotNull(d_array_array53);
        org.junit.Assert.assertTrue(b60 == false);
        org.junit.Assert.assertNotNull(complex61);
        org.junit.Assert.assertEquals((double) d62, Double.NaN, 0);
    }

    @Test
    public void test388() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test388");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) (-447362041), 100);
        org.junit.Assert.assertTrue(d2 == (-4.47362041E8d));
    }

    @Test
    public void test389() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test389");
        double d2 = org.apache.commons.math.util.MathUtils.log(0.0d, (-1.1752011936438014d));
        org.junit.Assert.assertEquals((double) d2, Double.NaN, 0);
    }

    @Test
    public void test390() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test390");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = complex2.cosh();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
    }

    @Test
    public void test391() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test391");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (-2112331276), (float) 8170176069297290577L, 1.07597005E9f);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test392() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test392");
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
        org.apache.commons.math.complex.ComplexField complexField15 = complex0.getField();
        org.apache.commons.math.complex.Complex complex16 = complex0.sqrt();
        org.apache.commons.math.complex.Complex complex17 = complex16.tanh();
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
        org.junit.Assert.assertNotNull(complexField15);
        org.junit.Assert.assertNotNull(complex16);
        org.junit.Assert.assertNotNull(complex17);
    }

    @Test
    public void test393() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test393");
        java.math.BigInteger bigInteger0 = null;
        java.math.BigInteger bigInteger2 = org.apache.commons.math.util.MathUtils.pow(bigInteger0, (long) 0);
        java.math.BigInteger bigInteger4 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 42);
        java.math.BigInteger bigInteger6 = org.apache.commons.math.util.MathUtils.pow(bigInteger2, 1);
        java.math.BigInteger bigInteger8 = org.apache.commons.math.util.MathUtils.pow(bigInteger6, 97);
        try {
            java.math.BigInteger bigInteger10 = org.apache.commons.math.util.MathUtils.pow(bigInteger8, (long) (-447362044));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
        org.junit.Assert.assertNotNull(bigInteger2);
        org.junit.Assert.assertNotNull(bigInteger4);
        org.junit.Assert.assertNotNull(bigInteger6);
        org.junit.Assert.assertNotNull(bigInteger8);
    }

    @Test
    public void test394() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test394");
        boolean b2 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((float) (-2112331376), (float) (-2112331276));
        org.junit.Assert.assertTrue(b2 == true);
    }

    @Test
    public void test395() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test395");
        float f1 = org.apache.commons.math.util.MathUtils.indicator((float) (short) 0);
        org.junit.Assert.assertTrue(f1 == 1.0f);
    }

    @Test
    public void test396() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test396");
        long long2 = org.apache.commons.math.util.MathUtils.gcd(0L, (long) 11);
        org.junit.Assert.assertTrue(long2 == 11L);
    }

    @Test
    public void test397() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test397");
        long long1 = org.apache.commons.math.util.MathUtils.sign((long) 98);
        org.junit.Assert.assertTrue(long1 == 1L);
    }

    @Test
    public void test398() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test398");
        double d3 = org.apache.commons.math.util.MathUtils.reduce((double) 1.63608653E9f, 2.2250738585072014E-308d, 0.0d);
        org.junit.Assert.assertTrue(d3 == Double.NEGATIVE_INFINITY);
    }

    @Test
    public void test399() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test399");
        int i2 = org.apache.commons.math.util.MathUtils.pow((int) (short) 0, 3);
        org.junit.Assert.assertTrue(i2 == 0);
    }

    @Test
    public void test400() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test400");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 42, (-11.982297150257104d), (double) 10.0f);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test401() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test401");
        boolean b2 = org.apache.commons.math.util.MathUtils.equals(3.6288E8f, (float) 9L);
        org.junit.Assert.assertTrue(b2 == false);
    }

    @Test
    public void test402() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test402");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        org.apache.commons.math.complex.Complex complex2 = null;
        try {
            org.apache.commons.math.complex.Complex complex3 = complex1.pow(complex2);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
    }

    @Test
    public void test403() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test403");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array6 = org.apache.commons.math.util.MathUtils.copyOf(d_array3);
        double[] d_array8 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, 349.9541180407703d);
        double d9 = org.apache.commons.math.util.MathUtils.safeNorm(d_array8);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array6);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertTrue(d9 == 349.9541180407703d);
    }

    @Test
    public void test404() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test404");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        java.lang.Number number4 = nonMonotonousSequenceException3.getPrevious();
        java.lang.String str5 = nonMonotonousSequenceException3.toString();
        boolean b6 = nonMonotonousSequenceException3.getStrict();
        java.lang.Number number7 = nonMonotonousSequenceException3.getPrevious();
        int i8 = nonMonotonousSequenceException3.getIndex();
        java.lang.Number number9 = nonMonotonousSequenceException3.getPrevious();
        org.junit.Assert.assertTrue("'" + number4 + "' != '" + 2.2250738585072014E-308d + "'", number4.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue("'" + str5 + "' != '" + "org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)" + "'", str5.equals("org.apache.commons.math.exception.NonMonotonousSequenceException: points 96 and 97 are not strictly increasing (0 >= 0)"));
        org.junit.Assert.assertTrue(b6 == true);
        org.junit.Assert.assertTrue("'" + number7 + "' != '" + 2.2250738585072014E-308d + "'", number7.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue(i8 == 97);
        org.junit.Assert.assertTrue("'" + number9 + "' != '" + 2.2250738585072014E-308d + "'", number9.equals(2.2250738585072014E-308d));
    }

    @Test
    public void test405() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test405");
        int[] i_array0 = new int[] {};
        int[] i_array1 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array0);
        int[] i_array3 = new int[] {};
        int[] i_array4 = org.apache.commons.math.util.MathUtils.copyOf(i_array3);
        int[] i_array10 = new int[] { (byte) 1, (short) -1, (byte) 0, 0, (byte) 10 };
        int i11 = org.apache.commons.math.util.MathUtils.distanceInf(i_array3, i_array10);
        int[] i_array13 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 1);
        int i14 = org.apache.commons.math.util.MathUtils.distance1(i_array0, i_array3);
        int[] i_array16 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 98);
        int[] i_array17 = new int[] {};
        int[] i_array18 = org.apache.commons.math.util.MathUtils.copyOf(i_array17);
        try {
            double d19 = org.apache.commons.math.util.MathUtils.distance(i_array16, i_array18);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(i_array0);
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array10);
        org.junit.Assert.assertTrue(i11 == 0);
        org.junit.Assert.assertNotNull(i_array13);
        org.junit.Assert.assertTrue(i14 == 0);
        org.junit.Assert.assertNotNull(i_array16);
        org.junit.Assert.assertNotNull(i_array17);
        org.junit.Assert.assertNotNull(i_array18);
    }

    @Test
    public void test406() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test406");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) (-2112331476));
    }

    @Test
    public void test407() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test407");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 11L, (double) (-1074790400), (double) 3L);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test408() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test408");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable3 = null;
        org.apache.commons.math.exception.util.Localizable localizable4 = null;
        org.apache.commons.math.exception.util.Localizable localizable5 = null;
        java.lang.Object[] obj_array7 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException8 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable5, (java.lang.Number) 100.0d, obj_array7);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException9 = new org.apache.commons.math.exception.NullArgumentException(localizable4, obj_array7);
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException10 = new org.apache.commons.math.exception.MathArithmeticException(localizable3, obj_array7);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException11 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable1, (java.lang.Number) 4950.0d, obj_array7);
        org.apache.commons.math.exception.NullArgumentException nullArgumentException12 = new org.apache.commons.math.exception.NullArgumentException(localizable0, obj_array7);
        org.junit.Assert.assertNotNull(obj_array7);
    }

    @Test
    public void test409() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test409");
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
        org.apache.commons.math.exception.util.Localizable localizable16 = null;
        double[] d_array17 = new double[] {};
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array25 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array29 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array33 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array34 = new double[][] { d_array21, d_array25, d_array29, d_array33 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array17, d_array_array34);
        double[] d_array36 = new double[] {};
        double[] d_array40 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array44 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array48 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array52 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array53 = new double[][] { d_array40, d_array44, d_array48, d_array52 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array36, d_array_array53);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array17, d_array_array53);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex0, localizable16, (java.lang.Object[]) d_array_array53);
        org.apache.commons.math.complex.Complex complex59 = new org.apache.commons.math.complex.Complex(197.05131665139353d, (double) 3628800L);
        boolean b60 = complex59.isInfinite();
        org.apache.commons.math.complex.Complex complex61 = complex0.subtract(complex59);
        org.apache.commons.math.complex.Complex complex62 = complex59.acos();
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
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array33);
        org.junit.Assert.assertNotNull(d_array_array34);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertNotNull(d_array44);
        org.junit.Assert.assertNotNull(d_array48);
        org.junit.Assert.assertNotNull(d_array52);
        org.junit.Assert.assertNotNull(d_array_array53);
        org.junit.Assert.assertTrue(b60 == false);
        org.junit.Assert.assertNotNull(complex61);
        org.junit.Assert.assertNotNull(complex62);
    }

    @Test
    public void test410() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test410");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 10.04987562112089d, (java.lang.Number) (-1.0f), false);
        boolean b5 = numberIsTooSmallException4.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b5 == false);
    }

    @Test
    public void test411() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test411");
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
        org.apache.commons.math.complex.Complex complex28 = complex24.asin();
        boolean b29 = complex28.isInfinite();
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
        org.junit.Assert.assertTrue(b29 == false);
    }

    @Test
    public void test412() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test412");
        double[] d_array1 = new double[] { (byte) -1 };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array1, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b7 = org.apache.commons.math.util.MathUtils.checkOrder(d_array3, orderDirection4, true, false);
        double[] d_array11 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d12 = org.apache.commons.math.util.MathUtils.safeNorm(d_array11);
        int i13 = org.apache.commons.math.util.MathUtils.hash(d_array11);
        boolean b14 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array3, d_array11);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection15 = null;
        try {
            boolean b18 = org.apache.commons.math.util.MathUtils.checkOrder(d_array11, orderDirection15, true, true);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
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
    public void test413() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test413");
        int[] i_array0 = null;
        int[] i_array1 = new int[] {};
        int[] i_array2 = org.apache.commons.math.util.MathUtils.copyOf(i_array1);
        int[] i_array3 = org.apache.commons.math.util.MathUtils.copyOf(i_array1);
        int[] i_array4 = new int[] {};
        int[] i_array5 = org.apache.commons.math.util.MathUtils.copyOf(i_array4);
        double d6 = org.apache.commons.math.util.MathUtils.distance(i_array3, i_array5);
        int[] i_array8 = org.apache.commons.math.util.MathUtils.copyOf(i_array3, 0);
        try {
            double d9 = org.apache.commons.math.util.MathUtils.distance(i_array0, i_array8);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertNotNull(i_array1);
        org.junit.Assert.assertNotNull(i_array2);
        org.junit.Assert.assertNotNull(i_array3);
        org.junit.Assert.assertNotNull(i_array4);
        org.junit.Assert.assertNotNull(i_array5);
        org.junit.Assert.assertTrue(d6 == 0.0d);
        org.junit.Assert.assertNotNull(i_array8);
    }

    @Test
    public void test414() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test414");
        org.apache.commons.math.exception.NotPositiveException notPositiveException1 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        org.apache.commons.math.exception.NotPositiveException notPositiveException3 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        notPositiveException1.addSuppressed((java.lang.Throwable) notPositiveException3);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext5 = notPositiveException1.getContext();
        java.lang.Number number6 = notPositiveException1.getArgument();
        org.junit.Assert.assertNotNull(exceptionContext5);
        org.junit.Assert.assertTrue("'" + number6 + "' != '" + (short) 1 + "'", number6.equals((short) 1));
    }

    @Test
    public void test415() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test415");
        long long2 = org.apache.commons.math.util.MathUtils.lcm(97L, (long) 100);
        org.junit.Assert.assertTrue(long2 == 9700L);
    }

    @Test
    public void test416() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test416");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((float) 10, (float) (short) 0, (float) 10000000000L);
        org.junit.Assert.assertTrue(b3 == true);
    }

    @Test
    public void test417() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test417");
        org.apache.commons.math.util.MathUtils.checkFinite((double) 33);
    }

    @Test
    public void test418() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test418");
        long long2 = org.apache.commons.math.util.MathUtils.pow(1636086543L, 0);
        org.junit.Assert.assertTrue(long2 == 1L);
    }

    @Test
    public void test419() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test419");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck(3500L, (long) 19683);
        org.junit.Assert.assertTrue(long2 == 23183L);
    }

    @Test
    public void test420() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test420");
        double d3 = org.apache.commons.math.util.MathUtils.reduce(0.6830368552089112d, (double) 1636086465, (double) (byte) 100);
        org.junit.Assert.assertTrue(d3 == 1.6360863656830368E9d);
    }

    @Test
    public void test421() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test421");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        boolean b5 = nonMonotonousSequenceException3.getStrict();
        boolean b6 = nonMonotonousSequenceException3.getStrict();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b5 == true);
        org.junit.Assert.assertTrue(b6 == true);
    }

    @Test
    public void test422() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test422");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) 1074790400, (long) (-2112331276));
        org.junit.Assert.assertTrue(long2 == 3187121676L);
    }

    @Test
    public void test423() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test423");
        org.apache.commons.math.util.MathUtils.checkFinite((double) (-7613835992134657463L));
    }

    @Test
    public void test424() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test424");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex4.divide(complex5);
        org.apache.commons.math.complex.Complex complex7 = complex3.multiply(complex4);
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex10 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex11 = complex9.divide(complex10);
        org.apache.commons.math.complex.Complex complex12 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex13 = complex12.acos();
        org.apache.commons.math.complex.Complex complex14 = complex10.divide(complex12);
        org.apache.commons.math.complex.Complex complex15 = complex10.tan();
        boolean b16 = complex8.equals((java.lang.Object) complex15);
        org.apache.commons.math.complex.Complex complex17 = complex4.pow(complex15);
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
        org.junit.Assert.assertNotNull(complex12);
        org.junit.Assert.assertNotNull(complex13);
        org.junit.Assert.assertNotNull(complex14);
        org.junit.Assert.assertNotNull(complex15);
        org.junit.Assert.assertTrue(b16 == true);
        org.junit.Assert.assertNotNull(complex17);
    }

    @Test
    public void test425() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test425");
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
        int[] i_array53 = org.apache.commons.math.util.MathUtils.copyOf(i_array49);
        int[] i_array60 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array62 = org.apache.commons.math.util.MathUtils.copyOf(i_array60, (int) '4');
        int i63 = org.apache.commons.math.util.MathUtils.distance1(i_array49, i_array60);
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
        org.junit.Assert.assertNotNull(i_array53);
        org.junit.Assert.assertNotNull(i_array60);
        org.junit.Assert.assertNotNull(i_array62);
        org.junit.Assert.assertTrue(i63 == 0);
    }

    @Test
    public void test426() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test426");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.sqrt();
        double d2 = complex1.getImaginary();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertEquals((double) d2, Double.NaN, 0);
    }

    @Test
    public void test427() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test427");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        boolean b5 = complex4.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertTrue(b5 == true);
    }

    @Test
    public void test428() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test428");
        long long2 = org.apache.commons.math.util.MathUtils.subAndCheck((long) 11, (long) 1636086465);
        org.junit.Assert.assertTrue(long2 == (-1636086454L));
    }

    @Test
    public void test429() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test429");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex(0.28318530717958623d, 197.05131665139353d);
    }

    @Test
    public void test430() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test430");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException4 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) 10L, (java.lang.Number) (byte) 1, true);
    }

    @Test
    public void test431() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test431");
        org.apache.commons.math.complex.Complex complex2 = new org.apache.commons.math.complex.Complex((double) 0.0f, 0.0d);
        boolean b3 = complex2.isNaN();
        org.apache.commons.math.complex.Complex complex4 = complex2.sin();
        org.junit.Assert.assertTrue(b3 == false);
        org.junit.Assert.assertNotNull(complex4);
    }

    @Test
    public void test432() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test432");
        int i2 = org.apache.commons.math.util.MathUtils.mulAndCheck(42, 19683);
        org.junit.Assert.assertTrue(i2 == 826686);
    }

    @Test
    public void test433() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test433");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals(10.0f, (float) (-2112331476), (int) (short) -1);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test434() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test434");
        double[] d_array1 = new double[] { (-1.0f) };
        double[] d_array3 = org.apache.commons.math.util.MathUtils.copyOf(d_array1, 1);
        double[] d_array5 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array3, (double) 0L);
        double[] d_array13 = new double[] { (byte) -1 };
        double[] d_array15 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array13, (double) (byte) 1);
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection16 = org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING;
        boolean b19 = org.apache.commons.math.util.MathUtils.checkOrder(d_array15, orderDirection16, true, false);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException21 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 10, (java.lang.Number) 1.0d, (-1074790400), orderDirection16, true);
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException23 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) (-1.0f), (java.lang.Number) 0.0d, (int) ' ', orderDirection16, true);
        org.apache.commons.math.util.MathUtils.checkOrder(d_array5, orderDirection16, true);
        org.junit.Assert.assertNotNull(d_array1);
        org.junit.Assert.assertNotNull(d_array3);
        org.junit.Assert.assertNotNull(d_array5);
        org.junit.Assert.assertNotNull(d_array13);
        org.junit.Assert.assertNotNull(d_array15);
        org.junit.Assert.assertTrue("'" + orderDirection16 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection16.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b19 == true);
    }

    @Test
    public void test435() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test435");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals((double) 4, 0.0d, 0.0d);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test436() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test436");
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
        int[] i_array53 = org.apache.commons.math.util.MathUtils.copyOf(i_array49);
        int[] i_array54 = null;
        try {
            int i55 = org.apache.commons.math.util.MathUtils.distanceInf(i_array53, i_array54);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
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
        org.junit.Assert.assertNotNull(i_array53);
    }

    @Test
    public void test437() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test437");
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
        org.apache.commons.math.complex.Complex complex37 = complex36.sinh();
        org.apache.commons.math.complex.Complex complex38 = complex36.log();
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
    }

    @Test
    public void test438() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test438");
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
        int[] i_array37 = org.apache.commons.math.util.MathUtils.copyOf(i_array33);
        int[] i_array44 = new int[] { (-1), 1072693248, '#', 0, (byte) 0, (short) 10 };
        int[] i_array46 = org.apache.commons.math.util.MathUtils.copyOf(i_array44, (int) '4');
        int i47 = org.apache.commons.math.util.MathUtils.distanceInf(i_array33, i_array46);
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
        org.junit.Assert.assertNotNull(i_array44);
        org.junit.Assert.assertNotNull(i_array46);
        org.junit.Assert.assertTrue(i47 == 0);
    }

    @Test
    public void test439() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test439");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.math.BigInteger bigInteger1 = null;
        java.math.BigInteger bigInteger3 = org.apache.commons.math.util.MathUtils.pow(bigInteger1, (long) 0);
        java.math.BigInteger bigInteger5 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 42);
        java.math.BigInteger bigInteger7 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 1);
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException10 = new org.apache.commons.math.exception.NumberIsTooSmallException(localizable0, (java.lang.Number) bigInteger3, (java.lang.Number) 9.332621544395286E157d, true);
        org.apache.commons.math.exception.util.Localizable localizable11 = null;
        java.lang.Object[] obj_array13 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException14 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable11, (java.lang.Number) (short) 100, obj_array13);
        numberIsTooSmallException10.addSuppressed((java.lang.Throwable) notFiniteNumberException14);
        org.junit.Assert.assertNotNull(bigInteger3);
        org.junit.Assert.assertNotNull(bigInteger5);
        org.junit.Assert.assertNotNull(bigInteger7);
    }

    @Test
    public void test440() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test440");
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        java.lang.Object[] obj_array4 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException5 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable2, (java.lang.Number) 100.0d, obj_array4);
        java.lang.Throwable[] throwable_array6 = notFiniteNumberException5.getSuppressed();
        org.apache.commons.math.exception.NullArgumentException nullArgumentException7 = new org.apache.commons.math.exception.NullArgumentException(localizable1, (java.lang.Object[]) throwable_array6);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException8 = new org.apache.commons.math.exception.NotFiniteNumberException((java.lang.Number) (-1067941888), (java.lang.Object[]) throwable_array6);
        org.junit.Assert.assertNotNull(obj_array4);
        org.junit.Assert.assertNotNull(throwable_array6);
    }

    @Test
    public void test441() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test441");
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
        org.apache.commons.math.complex.Complex complex37 = complex30.log();
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
    public void test442() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test442");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        java.math.BigInteger bigInteger1 = null;
        java.math.BigInteger bigInteger3 = org.apache.commons.math.util.MathUtils.pow(bigInteger1, (long) 0);
        java.math.BigInteger bigInteger5 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 42);
        java.math.BigInteger bigInteger7 = org.apache.commons.math.util.MathUtils.pow(bigInteger3, 1);
        java.math.BigInteger bigInteger9 = org.apache.commons.math.util.MathUtils.pow(bigInteger7, (long) (byte) 10);
        org.apache.commons.math.exception.NotPositiveException notPositiveException10 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) bigInteger7);
        org.junit.Assert.assertNotNull(bigInteger3);
        org.junit.Assert.assertNotNull(bigInteger5);
        org.junit.Assert.assertNotNull(bigInteger7);
        org.junit.Assert.assertNotNull(bigInteger9);
    }

    @Test
    public void test443() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test443");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round(10.0f, 11, (-1));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test444() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test444");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.tanh();
        org.apache.commons.math.complex.Complex complex5 = null;
        try {
            org.apache.commons.math.complex.Complex complex6 = complex4.divide(complex5);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NullArgumentException");
        } catch (org.apache.commons.math.exception.NullArgumentException e) {
        }
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
    }

    @Test
    public void test445() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test445");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.tan();
        double d7 = complex6.getReal();
        double d8 = complex6.getImaginary();
        double d9 = complex6.getReal();
        org.apache.commons.math.complex.Complex complex10 = complex6.tan();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertEquals((double) d7, Double.NaN, 0);
        org.junit.Assert.assertEquals((double) d8, Double.NaN, 0);
        org.junit.Assert.assertEquals((double) d9, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex10);
    }

    @Test
    public void test446() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test446");
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException0 = new org.apache.commons.math.exception.MathArithmeticException();
        java.lang.String str1 = mathArithmeticException0.toString();
        org.apache.commons.math.exception.NotPositiveException notPositiveException3 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        org.apache.commons.math.exception.NotPositiveException notPositiveException5 = new org.apache.commons.math.exception.NotPositiveException((java.lang.Number) (short) 1);
        notPositiveException3.addSuppressed((java.lang.Throwable) notPositiveException5);
        mathArithmeticException0.addSuppressed((java.lang.Throwable) notPositiveException5);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext8 = mathArithmeticException0.getContext();
        org.junit.Assert.assertTrue("'" + str1 + "' != '" + "org.apache.commons.math.exception.MathArithmeticException: arithmetic exception" + "'", str1.equals("org.apache.commons.math.exception.MathArithmeticException: arithmetic exception"));
        org.junit.Assert.assertNotNull(exceptionContext8);
    }

    @Test
    public void test447() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test447");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        java.lang.Object[] obj_array3 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NullArgumentException nullArgumentException4 = new org.apache.commons.math.exception.NullArgumentException(localizable2, obj_array3);
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException5 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable0, (java.lang.Number) 1636086465, obj_array3);
        org.junit.Assert.assertNotNull(obj_array3);
    }

    @Test
    public void test448() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test448");
        double d2 = org.apache.commons.math.util.MathUtils.log(1.0066581708938198d, (double) 92561040L);
        org.junit.Assert.assertTrue(d2 == 2764.17927763715d);
    }

    @Test
    public void test449() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test449");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck((long) 42, 7766279631452241920L);
        org.junit.Assert.assertTrue(long2 == 7766279631452241962L);
    }

    @Test
    public void test450() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test450");
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
        org.apache.commons.math.complex.Complex complex17 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex18 = complex17.acos();
        org.apache.commons.math.complex.Complex complex19 = complex18.sqrt1z();
        org.apache.commons.math.complex.Complex complex20 = complex0.pow(complex18);
        org.apache.commons.math.complex.ComplexField complexField21 = complex18.getField();
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
        org.junit.Assert.assertNotNull(complexField21);
    }

    @Test
    public void test451() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test451");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, (-1), (int) ' ');
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext4 = dimensionMismatchException3.getContext();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException8 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 26.0d, (java.lang.Number) (-1.1752011936438014d), (-1074790369));
        dimensionMismatchException3.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException8);
        int i10 = nonMonotonousSequenceException8.getIndex();
        java.lang.Number number11 = nonMonotonousSequenceException8.getPrevious();
        org.junit.Assert.assertNotNull(exceptionContext4);
        org.junit.Assert.assertTrue(i10 == (-1074790369));
        org.junit.Assert.assertTrue("'" + number11 + "' != '" + (-1.1752011936438014d) + "'", number11.equals((-1.1752011936438014d)));
    }

    @Test
    public void test452() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test452");
        double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientLog((int) '4', 0);
        org.junit.Assert.assertTrue(d2 == 0.0d);
    }

    @Test
    public void test453() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test453");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.binomialCoefficient((-1067941888), 1);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test454() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test454");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        java.lang.Object[] obj_array8 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException9 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable6, (java.lang.Number) (short) 100, obj_array8);
        java.lang.Number number10 = notFiniteNumberException9.getArgument();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException9);
        int i12 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection13 = nonMonotonousSequenceException3.getDirection();
        java.lang.Number number14 = nonMonotonousSequenceException3.getPrevious();
        boolean b15 = nonMonotonousSequenceException3.getStrict();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number10 + "' != '" + (short) 100 + "'", number10.equals((short) 100));
        org.junit.Assert.assertTrue(i12 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection13 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection13.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue("'" + number14 + "' != '" + 2.2250738585072014E-308d + "'", number14.equals(2.2250738585072014E-308d));
        org.junit.Assert.assertTrue(b15 == true);
    }

    @Test
    public void test455() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test455");
        short s1 = org.apache.commons.math.util.MathUtils.sign((short) (byte) -1);
        org.junit.Assert.assertTrue(s1 == (short) -1);
    }

    @Test
    public void test456() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test456");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.tanh();
        org.apache.commons.math.complex.Complex complex5 = complex2.multiply(complex4);
        org.apache.commons.math.complex.Complex complex6 = complex2.asin();
        org.apache.commons.math.complex.Complex complex7 = complex2.asin();
        boolean b8 = complex7.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertTrue(b8 == true);
    }

    @Test
    public void test457() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test457");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.exception.util.Localizable localizable2 = null;
        java.lang.Object[] obj_array4 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException5 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable2, (java.lang.Number) 100.0d, obj_array4);
        java.lang.Throwable[] throwable_array6 = notFiniteNumberException5.getSuppressed();
        org.apache.commons.math.exception.NullArgumentException nullArgumentException7 = new org.apache.commons.math.exception.NullArgumentException(localizable1, (java.lang.Object[]) throwable_array6);
        org.apache.commons.math.exception.MathIllegalArgumentException mathIllegalArgumentException8 = new org.apache.commons.math.exception.MathIllegalArgumentException(localizable0, (java.lang.Object[]) throwable_array6);
        org.junit.Assert.assertNotNull(obj_array4);
        org.junit.Assert.assertNotNull(throwable_array6);
    }

    @Test
    public void test458() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test458");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) (short) 10, (double) 20, (double) 3.1f);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test459() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test459");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round(0.0f, (int) '#', 98);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test460() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test460");
        double[] d_array0 = new double[] {};
        double[] d_array4 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array8 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array12 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array16 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array17 = new double[][] { d_array4, d_array8, d_array12, d_array16 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array17);
        double[] d_array20 = new double[] { (-1.0f) };
        double[] d_array22 = org.apache.commons.math.util.MathUtils.copyOf(d_array20, 1);
        double[] d_array24 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array22, (double) 0L);
        double[] d_array25 = org.apache.commons.math.util.MathUtils.copyOf(d_array22);
        double d26 = org.apache.commons.math.util.MathUtils.safeNorm(d_array25);
        double[] d_array28 = new double[] { (-1.0f) };
        double[] d_array30 = org.apache.commons.math.util.MathUtils.copyOf(d_array28, 1);
        double[] d_array32 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array30, (double) 0L);
        double[] d_array33 = org.apache.commons.math.util.MathUtils.copyOf(d_array30);
        double[] d_array35 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array30, 349.9541180407703d);
        double[] d_array37 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array35, 0.0d);
        boolean b38 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(d_array25, d_array35);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array35);
        double[] d_array41 = org.apache.commons.math.util.MathUtils.normalizeArray(d_array35, (double) 10.0f);
        double d42 = org.apache.commons.math.util.MathUtils.distanceInf(d_array0, d_array41);
        org.junit.Assert.assertNotNull(d_array0);
        org.junit.Assert.assertNotNull(d_array4);
        org.junit.Assert.assertNotNull(d_array8);
        org.junit.Assert.assertNotNull(d_array12);
        org.junit.Assert.assertNotNull(d_array16);
        org.junit.Assert.assertNotNull(d_array_array17);
        org.junit.Assert.assertNotNull(d_array20);
        org.junit.Assert.assertNotNull(d_array22);
        org.junit.Assert.assertNotNull(d_array24);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertTrue(d26 == 1.0d);
        org.junit.Assert.assertNotNull(d_array28);
        org.junit.Assert.assertNotNull(d_array30);
        org.junit.Assert.assertNotNull(d_array32);
        org.junit.Assert.assertNotNull(d_array33);
        org.junit.Assert.assertNotNull(d_array35);
        org.junit.Assert.assertNotNull(d_array37);
        org.junit.Assert.assertTrue(b38 == false);
        org.junit.Assert.assertNotNull(d_array41);
        org.junit.Assert.assertTrue(d42 == 0.0d);
    }

    @Test
    public void test461() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test461");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NotPositiveException notPositiveException2 = new org.apache.commons.math.exception.NotPositiveException(localizable0, (java.lang.Number) 100L);
        java.lang.Number number3 = notPositiveException2.getMin();
        java.lang.Throwable throwable4 = null;
        try {
            notPositiveException2.addSuppressed(throwable4);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertTrue("'" + number3 + "' != '" + 0 + "'", number3.equals(0));
    }

    @Test
    public void test462() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test462");
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
        org.apache.commons.math.complex.Complex complex20 = complex16.sqrt1z();
        org.apache.commons.math.complex.Complex complex21 = complex16.sqrt();
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
    }

    @Test
    public void test463() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test463");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 1L, (java.lang.Number) 1636086543L, 1074790400);
        boolean b4 = nonMonotonousSequenceException3.getStrict();
        org.junit.Assert.assertTrue(b4 == true);
    }

    @Test
    public void test464() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test464");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex4.divide(complex5);
        org.apache.commons.math.complex.Complex complex7 = complex3.multiply(complex4);
        org.apache.commons.math.complex.Complex complex8 = complex4.sqrt();
        boolean b9 = complex4.isNaN();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertTrue(b9 == true);
    }

    @Test
    public void test465() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test465");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 26.0d, (java.lang.Number) (-1.1752011936438014d), (-1074790369));
        int i4 = nonMonotonousSequenceException3.getIndex();
        org.junit.Assert.assertTrue(i4 == (-1074790369));
    }

    @Test
    public void test466() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test466");
        short s1 = org.apache.commons.math.util.MathUtils.indicator((short) (byte) 0);
        org.junit.Assert.assertTrue(s1 == (short) 1);
    }

    @Test
    public void test467() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test467");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        org.apache.commons.math.complex.Complex complex4 = complex0.subtract(complex2);
        java.util.List<org.apache.commons.math.complex.Complex> list_complex6 = complex2.nthRoot((int) (short) 10);
        double d7 = complex2.abs();
        org.apache.commons.math.complex.Complex complex8 = complex2.atan();
        org.apache.commons.math.complex.Complex complex9 = complex2.exp();
        double d10 = complex9.abs();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(list_complex6);
        org.junit.Assert.assertEquals((double) d7, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex8);
        org.junit.Assert.assertNotNull(complex9);
        org.junit.Assert.assertEquals((double) d10, Double.NaN, 0);
    }

    @Test
    public void test468() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test468");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex0.abs();
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex0.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex7 = complex6.cos();
        org.apache.commons.math.complex.Complex complex8 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex9 = complex8.cos();
        org.apache.commons.math.complex.Complex complex10 = complex6.subtract(complex8);
        org.apache.commons.math.complex.Complex complex11 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex12 = complex11.cos();
        org.apache.commons.math.complex.Complex complex13 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex14 = complex13.cos();
        org.apache.commons.math.complex.Complex complex15 = complex11.subtract(complex13);
        org.apache.commons.math.complex.Complex complex16 = complex6.add(complex15);
        org.apache.commons.math.complex.Complex complex17 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex18 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex19 = complex17.divide(complex18);
        org.apache.commons.math.complex.Complex complex20 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex21 = complex20.tanh();
        org.apache.commons.math.complex.Complex complex22 = complex19.multiply(complex21);
        org.apache.commons.math.complex.Complex complex24 = complex19.multiply(1.0747903976294017E9d);
        org.apache.commons.math.complex.Complex complex25 = complex16.add(complex19);
        org.apache.commons.math.complex.Complex complex26 = complex19.conjugate();
        org.apache.commons.math.complex.Complex complex27 = complex0.add(complex19);
        org.apache.commons.math.complex.Complex complex28 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex29 = complex28.cos();
        org.apache.commons.math.complex.Complex complex30 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex31 = complex30.cos();
        org.apache.commons.math.complex.Complex complex32 = complex28.subtract(complex30);
        org.apache.commons.math.complex.Complex complex33 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex34 = complex33.cos();
        org.apache.commons.math.complex.Complex complex35 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex36 = complex35.cos();
        org.apache.commons.math.complex.Complex complex37 = complex33.subtract(complex35);
        org.apache.commons.math.complex.Complex complex38 = complex28.add(complex37);
        double d39 = complex28.getArgument();
        org.apache.commons.math.complex.Complex complex40 = complex28.exp();
        org.apache.commons.math.complex.Complex complex41 = complex28.negate();
        org.apache.commons.math.complex.Complex complex42 = complex28.asin();
        org.apache.commons.math.complex.Complex complex43 = complex0.subtract(complex28);
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 0.0d);
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
        org.junit.Assert.assertEquals((double) d39, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex40);
        org.junit.Assert.assertNotNull(complex41);
        org.junit.Assert.assertNotNull(complex42);
        org.junit.Assert.assertNotNull(complex43);
    }

    @Test
    public void test469() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test469");
        org.apache.commons.math.exception.util.Localizable localizable1 = null;
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex2.cos();
        java.lang.String str4 = complex3.toString();
        org.apache.commons.math.complex.Complex complex5 = complex3.sqrt1z();
        org.apache.commons.math.complex.Complex complex6 = complex5.sinh();
        org.apache.commons.math.complex.Complex complex7 = complex5.cosh();
        org.apache.commons.math.exception.util.Localizable localizable8 = null;
        org.apache.commons.math.exception.util.Localizable localizable9 = null;
        org.apache.commons.math.exception.util.Localizable localizable10 = null;
        java.lang.Object[] obj_array12 = new java.lang.Object[] {};
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException13 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable10, (java.lang.Number) 100.0d, obj_array12);
        java.lang.Throwable[] throwable_array14 = notFiniteNumberException13.getSuppressed();
        org.apache.commons.math.exception.NullArgumentException nullArgumentException15 = new org.apache.commons.math.exception.NullArgumentException(localizable9, (java.lang.Object[]) throwable_array14);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex5, localizable8, (java.lang.Object[]) throwable_array14);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) (-0.5571822032850108d), localizable1, (java.lang.Object[]) throwable_array14);
        org.junit.Assert.assertNotNull(complex2);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertTrue("'" + str4 + "' != '" + "(NaN, NaN)" + "'", str4.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(obj_array12);
        org.junit.Assert.assertNotNull(throwable_array14);
    }

    @Test
    public void test470() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test470");
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
        int i55 = org.apache.commons.math.util.MathUtils.hash(d_array5);
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
    }

    @Test
    public void test471() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test471");
        long long2 = org.apache.commons.math.util.MathUtils.mulAndCheck(0L, 6544345860L);
        org.junit.Assert.assertTrue(long2 == 0L);
    }

    @Test
    public void test472() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test472");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.tanh();
        org.apache.commons.math.complex.Complex complex4 = new org.apache.commons.math.complex.Complex((double) 0.0f, 0.0d);
        org.apache.commons.math.complex.Complex complex5 = complex0.subtract(complex4);
        boolean b6 = complex5.isInfinite();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertTrue(b6 == false);
    }

    @Test
    public void test473() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test473");
        try {
            long long1 = org.apache.commons.math.util.MathUtils.factorial(22);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathArithmeticException");
        } catch (org.apache.commons.math.exception.MathArithmeticException e) {
        }
    }

    @Test
    public void test474() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test474");
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection4 = nonMonotonousSequenceException3.getDirection();
        int i5 = nonMonotonousSequenceException3.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable6 = null;
        java.lang.Object[] obj_array8 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException9 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable6, (java.lang.Number) (short) 100, obj_array8);
        java.lang.Number number10 = notFiniteNumberException9.getArgument();
        nonMonotonousSequenceException3.addSuppressed((java.lang.Throwable) notFiniteNumberException9);
        boolean b12 = nonMonotonousSequenceException3.getStrict();
        boolean b13 = nonMonotonousSequenceException3.getStrict();
        org.junit.Assert.assertTrue("'" + orderDirection4 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection4.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i5 == 97);
        org.junit.Assert.assertTrue("'" + number10 + "' != '" + (short) 100 + "'", number10.equals((short) 100));
        org.junit.Assert.assertTrue(b12 == true);
        org.junit.Assert.assertTrue(b13 == true);
    }

    @Test
    public void test475() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test475");
        try {
            long long2 = org.apache.commons.math.util.MathUtils.pow(97L, (-447362044));
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NotPositiveException");
        } catch (org.apache.commons.math.exception.NotPositiveException e) {
        }
    }

    @Test
    public void test476() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test476");
        double d3 = org.apache.commons.math.util.MathUtils.reduce((double) 100, 98.0d, 0.0d);
        org.junit.Assert.assertTrue(d3 == 2.0d);
    }

    @Test
    public void test477() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test477");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(1.6360863656830368E9d, (double) (-2112331476), 4);
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test478() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test478");
        try {
            double d2 = org.apache.commons.math.util.MathUtils.binomialCoefficientDouble((-2112331376), (int) (short) 0);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.NumberIsTooLargeException");
        } catch (org.apache.commons.math.exception.NumberIsTooLargeException e) {
        }
    }

    @Test
    public void test479() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test479");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex1 = complex0.cosh();
        double d2 = complex1.getReal();
        org.apache.commons.math.complex.Complex complex3 = complex1.cos();
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex5 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex6 = complex4.divide(complex5);
        org.apache.commons.math.complex.Complex complex7 = complex3.multiply(complex4);
        org.apache.commons.math.complex.Complex complex8 = complex3.sqrt1z();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue(d2 == 1.0d);
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertNotNull(complex6);
        org.junit.Assert.assertNotNull(complex7);
        org.junit.Assert.assertNotNull(complex8);
    }

    @Test
    public void test480() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test480");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex3.sinh();
        org.apache.commons.math.complex.Complex complex5 = complex3.cosh();
        java.lang.String str6 = complex5.toString();
        org.junit.Assert.assertNotNull(complex0);
        org.junit.Assert.assertNotNull(complex1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "(NaN, NaN)" + "'", str2.equals("(NaN, NaN)"));
        org.junit.Assert.assertNotNull(complex3);
        org.junit.Assert.assertNotNull(complex4);
        org.junit.Assert.assertNotNull(complex5);
        org.junit.Assert.assertTrue("'" + str6 + "' != '" + "(NaN, NaN)" + "'", str6.equals("(NaN, NaN)"));
    }

    @Test
    public void test481() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test481");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex3 = complex1.divide(complex2);
        org.apache.commons.math.complex.Complex complex4 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex5 = complex4.acos();
        org.apache.commons.math.complex.Complex complex6 = complex2.divide(complex4);
        org.apache.commons.math.complex.Complex complex7 = complex2.tan();
        boolean b8 = complex0.equals((java.lang.Object) complex7);
        org.apache.commons.math.complex.Complex complex9 = complex7.tanh();
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
    }

    @Test
    public void test482() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test482");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 0, (java.lang.Number) (-7500783708504147263L), true);
    }

    @Test
    public void test483() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test483");
        long long2 = org.apache.commons.math.util.MathUtils.addAndCheck(0L, 10L);
        org.junit.Assert.assertTrue(long2 == 10L);
    }

    @Test
    public void test484() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test484");
        int i2 = org.apache.commons.math.util.MathUtils.pow(10, 0);
        org.junit.Assert.assertTrue(i2 == 1);
    }

    @Test
    public void test485() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test485");
        float[] f_array5 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array7 = new float[] { (-1074790400) };
        boolean b8 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array5, f_array7);
        float[] f_array14 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array16 = new float[] { (-1074790400) };
        boolean b17 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array14, f_array16);
        boolean b18 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array7, f_array14);
        float[] f_array23 = new float[] { 1.0f, 100L, (-73931601660L), (short) -1 };
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
        boolean b73 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array23, f_array66);
        float[] f_array74 = null;
        boolean b75 = org.apache.commons.math.util.MathUtils.equals(f_array23, f_array74);
        boolean b76 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array14, f_array74);
        org.junit.Assert.assertNotNull(f_array5);
        org.junit.Assert.assertNotNull(f_array7);
        org.junit.Assert.assertTrue(b8 == false);
        org.junit.Assert.assertNotNull(f_array14);
        org.junit.Assert.assertNotNull(f_array16);
        org.junit.Assert.assertTrue(b17 == false);
        org.junit.Assert.assertTrue(b18 == false);
        org.junit.Assert.assertNotNull(f_array23);
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
        org.junit.Assert.assertTrue(b75 == false);
        org.junit.Assert.assertTrue(b76 == false);
    }

    @Test
    public void test486() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test486");
        double[] d_array0 = null;
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection1 = org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING;
        try {
            org.apache.commons.math.util.MathUtils.checkOrder(d_array0, orderDirection1, false);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException");
        } catch (java.lang.NullPointerException e) {
        }
        org.junit.Assert.assertTrue("'" + orderDirection1 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING + "'", orderDirection1.equals(org.apache.commons.math.util.MathUtils.OrderDirection.DECREASING));
    }

    @Test
    public void test487() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test487");
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
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array0, d_array_array36);
        org.apache.commons.math.util.MathUtils.checkFinite(d_array0);
        double[] d_array43 = new double[] { (-1L), (byte) 10, 1.1102230246251565E-16d };
        double d44 = org.apache.commons.math.util.MathUtils.safeNorm(d_array43);
        double[] d_array46 = org.apache.commons.math.util.MathUtils.copyOf(d_array43, 0);
        double d47 = org.apache.commons.math.util.MathUtils.distance1(d_array0, d_array43);
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
        org.junit.Assert.assertNotNull(d_array43);
        org.junit.Assert.assertTrue(d44 == 10.04987562112089d);
        org.junit.Assert.assertNotNull(d_array46);
        org.junit.Assert.assertTrue(d47 == 0.0d);
    }

    @Test
    public void test488() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test488");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException4 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection5 = nonMonotonousSequenceException4.getDirection();
        int i6 = nonMonotonousSequenceException4.getIndex();
        org.apache.commons.math.exception.util.Localizable localizable7 = null;
        java.lang.Object[] obj_array9 = null;
        org.apache.commons.math.exception.NotFiniteNumberException notFiniteNumberException10 = new org.apache.commons.math.exception.NotFiniteNumberException(localizable7, (java.lang.Number) (short) 100, obj_array9);
        java.lang.Number number11 = notFiniteNumberException10.getArgument();
        nonMonotonousSequenceException4.addSuppressed((java.lang.Throwable) notFiniteNumberException10);
        int i13 = nonMonotonousSequenceException4.getIndex();
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException17 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 2.2250738585072014E-308d, (java.lang.Number) 2.2250738585072014E-308d, (int) 'a');
        int i18 = nonMonotonousSequenceException17.getIndex();
        org.apache.commons.math.util.MathUtils.OrderDirection orderDirection19 = nonMonotonousSequenceException17.getDirection();
        nonMonotonousSequenceException4.addSuppressed((java.lang.Throwable) nonMonotonousSequenceException17);
        boolean b21 = nonMonotonousSequenceException17.getStrict();
        boolean b22 = nonMonotonousSequenceException17.getStrict();
        java.lang.Throwable[] throwable_array23 = nonMonotonousSequenceException17.getSuppressed();
        org.apache.commons.math.exception.MathArithmeticException mathArithmeticException24 = new org.apache.commons.math.exception.MathArithmeticException(localizable0, (java.lang.Object[]) throwable_array23);
        org.apache.commons.math.exception.util.ExceptionContext exceptionContext25 = mathArithmeticException24.getContext();
        org.junit.Assert.assertTrue("'" + orderDirection5 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection5.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(i6 == 97);
        org.junit.Assert.assertTrue("'" + number11 + "' != '" + (short) 100 + "'", number11.equals((short) 100));
        org.junit.Assert.assertTrue(i13 == 97);
        org.junit.Assert.assertTrue(i18 == 97);
        org.junit.Assert.assertTrue("'" + orderDirection19 + "' != '" + org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING + "'", orderDirection19.equals(org.apache.commons.math.util.MathUtils.OrderDirection.INCREASING));
        org.junit.Assert.assertTrue(b21 == true);
        org.junit.Assert.assertTrue(b22 == true);
        org.junit.Assert.assertNotNull(throwable_array23);
        org.junit.Assert.assertNotNull(exceptionContext25);
    }

    @Test
    public void test489() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test489");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = complex0.cos();
        java.lang.String str2 = complex1.toString();
        org.apache.commons.math.complex.Complex complex3 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex4 = complex1.sqrt();
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
        double d16 = complex5.getArgument();
        org.apache.commons.math.complex.Complex complex17 = complex5.exp();
        org.apache.commons.math.complex.Complex complex18 = complex5.negate();
        org.apache.commons.math.complex.Complex complex19 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex20 = complex19.cos();
        org.apache.commons.math.complex.Complex complex21 = complex5.subtract(complex19);
        org.apache.commons.math.complex.Complex complex22 = complex21.cosh();
        org.apache.commons.math.complex.Complex complex23 = complex21.tanh();
        org.apache.commons.math.complex.Complex complex24 = complex21.atan();
        org.apache.commons.math.complex.Complex complex25 = complex21.sqrt1z();
        org.apache.commons.math.complex.Complex complex26 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex27 = complex26.cos();
        org.apache.commons.math.complex.Complex complex28 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex29 = complex28.cos();
        org.apache.commons.math.complex.Complex complex30 = complex26.subtract(complex28);
        org.apache.commons.math.complex.Complex complex31 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex32 = complex31.cos();
        org.apache.commons.math.complex.Complex complex33 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex34 = complex33.cos();
        org.apache.commons.math.complex.Complex complex35 = complex31.subtract(complex33);
        org.apache.commons.math.complex.Complex complex36 = complex26.add(complex35);
        double d37 = complex26.getImaginary();
        org.apache.commons.math.complex.Complex complex38 = complex26.negate();
        org.apache.commons.math.complex.Complex complex39 = complex21.subtract(complex26);
        org.apache.commons.math.complex.Complex complex40 = complex1.subtract(complex26);
        org.apache.commons.math.complex.Complex complex41 = complex26.conjugate();
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
        org.junit.Assert.assertEquals((double) d16, Double.NaN, 0);
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
        org.junit.Assert.assertNotNull(complex33);
        org.junit.Assert.assertNotNull(complex34);
        org.junit.Assert.assertNotNull(complex35);
        org.junit.Assert.assertNotNull(complex36);
        org.junit.Assert.assertEquals((double) d37, Double.NaN, 0);
        org.junit.Assert.assertNotNull(complex38);
        org.junit.Assert.assertNotNull(complex39);
        org.junit.Assert.assertNotNull(complex40);
        org.junit.Assert.assertNotNull(complex41);
    }

    @Test
    public void test490() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test490");
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
        org.apache.commons.math.exception.util.Localizable localizable16 = null;
        double[] d_array17 = new double[] {};
        double[] d_array21 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array25 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array29 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array33 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array34 = new double[][] { d_array21, d_array25, d_array29, d_array33 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array17, d_array_array34);
        double[] d_array36 = new double[] {};
        double[] d_array40 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array44 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array48 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[] d_array52 = new double[] { 2.2250738585072014E-308d, 10.0d, (short) 10 };
        double[][] d_array_array53 = new double[][] { d_array40, d_array44, d_array48, d_array52 };
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array36, d_array_array53);
        org.apache.commons.math.util.MathUtils.sortInPlace(d_array17, d_array_array53);
        org.apache.commons.math.util.MathUtils.checkNotNull((java.lang.Object) complex0, localizable16, (java.lang.Object[]) d_array_array53);
        org.apache.commons.math.complex.Complex complex57 = complex0.cosh();
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
        org.junit.Assert.assertNotNull(d_array17);
        org.junit.Assert.assertNotNull(d_array21);
        org.junit.Assert.assertNotNull(d_array25);
        org.junit.Assert.assertNotNull(d_array29);
        org.junit.Assert.assertNotNull(d_array33);
        org.junit.Assert.assertNotNull(d_array_array34);
        org.junit.Assert.assertNotNull(d_array36);
        org.junit.Assert.assertNotNull(d_array40);
        org.junit.Assert.assertNotNull(d_array44);
        org.junit.Assert.assertNotNull(d_array48);
        org.junit.Assert.assertNotNull(d_array52);
        org.junit.Assert.assertNotNull(d_array_array53);
        org.junit.Assert.assertNotNull(complex57);
    }

    @Test
    public void test491() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test491");
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
        float[] f_array25 = new float[] { 78L };
        float[] f_array26 = null;
        boolean b27 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array25, f_array26);
        boolean b28 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array18, f_array26);
        float[] f_array34 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array36 = new float[] { (-1074790400) };
        boolean b37 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array34, f_array36);
        float[] f_array43 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array45 = new float[] { (-1074790400) };
        boolean b46 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array43, f_array45);
        float[] f_array52 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array54 = new float[] { (-1074790400) };
        boolean b55 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array52, f_array54);
        boolean b56 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array45, f_array52);
        float[] f_array62 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array64 = new float[] { (-1074790400) };
        boolean b65 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array62, f_array64);
        float[] f_array71 = new float[] { 1L, 0L, 100, 100.0f, (-1) };
        float[] f_array73 = new float[] { (-1074790400) };
        boolean b74 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array71, f_array73);
        boolean b75 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array64, f_array71);
        boolean b76 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array45, f_array71);
        boolean b77 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN(f_array36, f_array71);
        boolean b78 = org.apache.commons.math.util.MathUtils.equals(f_array18, f_array71);
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
        org.junit.Assert.assertNotNull(f_array25);
        org.junit.Assert.assertTrue(b27 == false);
        org.junit.Assert.assertTrue(b28 == false);
        org.junit.Assert.assertNotNull(f_array34);
        org.junit.Assert.assertNotNull(f_array36);
        org.junit.Assert.assertTrue(b37 == false);
        org.junit.Assert.assertNotNull(f_array43);
        org.junit.Assert.assertNotNull(f_array45);
        org.junit.Assert.assertTrue(b46 == false);
        org.junit.Assert.assertNotNull(f_array52);
        org.junit.Assert.assertNotNull(f_array54);
        org.junit.Assert.assertTrue(b55 == false);
        org.junit.Assert.assertTrue(b56 == false);
        org.junit.Assert.assertNotNull(f_array62);
        org.junit.Assert.assertNotNull(f_array64);
        org.junit.Assert.assertTrue(b65 == false);
        org.junit.Assert.assertNotNull(f_array71);
        org.junit.Assert.assertNotNull(f_array73);
        org.junit.Assert.assertTrue(b74 == false);
        org.junit.Assert.assertTrue(b75 == false);
        org.junit.Assert.assertTrue(b76 == false);
        org.junit.Assert.assertTrue(b77 == false);
        org.junit.Assert.assertTrue(b78 == true);
    }

    @Test
    public void test492() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test492");
        org.apache.commons.math.exception.NumberIsTooLargeException numberIsTooLargeException3 = new org.apache.commons.math.exception.NumberIsTooLargeException((java.lang.Number) 33, (java.lang.Number) 100, true);
    }

    @Test
    public void test493() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test493");
        boolean b3 = org.apache.commons.math.util.MathUtils.equalsIncludingNaN((double) 2, 0.0d, (int) 'a');
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test494() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test494");
        double d2 = org.apache.commons.math.util.MathUtils.round((double) 3628800L, (int) '#');
        org.junit.Assert.assertTrue(d2 == 3628800.0d);
    }

    @Test
    public void test495() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test495");
        java.lang.Number number1 = null;
        org.apache.commons.math.exception.NonMonotonousSequenceException nonMonotonousSequenceException3 = new org.apache.commons.math.exception.NonMonotonousSequenceException((java.lang.Number) 362880000L, number1, 0);
    }

    @Test
    public void test496() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test496");
        try {
            float f3 = org.apache.commons.math.util.MathUtils.round(0.0f, 100, 1155);
            org.junit.Assert.fail("Expected exception of type org.apache.commons.math.exception.MathIllegalArgumentException");
        } catch (org.apache.commons.math.exception.MathIllegalArgumentException e) {
        }
    }

    @Test
    public void test497() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test497");
        org.apache.commons.math.complex.Complex complex0 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex1 = org.apache.commons.math.complex.Complex.NaN;
        org.apache.commons.math.complex.Complex complex2 = complex0.divide(complex1);
        org.apache.commons.math.complex.Complex complex3 = org.apache.commons.math.complex.Complex.ZERO;
        org.apache.commons.math.complex.Complex complex4 = complex3.acos();
        org.apache.commons.math.complex.Complex complex5 = complex1.divide(complex3);
        org.apache.commons.math.complex.Complex complex6 = complex1.log();
        org.apache.commons.math.complex.Complex complex7 = complex1.sqrt1z();
        org.apache.commons.math.complex.Complex complex8 = complex1.exp();
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
    public void test498() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test498");
        org.apache.commons.math.exception.NumberIsTooSmallException numberIsTooSmallException3 = new org.apache.commons.math.exception.NumberIsTooSmallException((java.lang.Number) 22, (java.lang.Number) (byte) 0, true);
        boolean b4 = numberIsTooSmallException3.getBoundIsAllowed();
        org.junit.Assert.assertTrue(b4 == true);
    }

    @Test
    public void test499() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test499");
        boolean b3 = org.apache.commons.math.util.MathUtils.equals(0.0d, (double) (byte) 1, (-11.982297150257104d));
        org.junit.Assert.assertTrue(b3 == false);
    }

    @Test
    public void test500() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test500");
        org.apache.commons.math.exception.util.Localizable localizable0 = null;
        org.apache.commons.math.exception.DimensionMismatchException dimensionMismatchException3 = new org.apache.commons.math.exception.DimensionMismatchException(localizable0, 1075970048, 1155);
    }
}

