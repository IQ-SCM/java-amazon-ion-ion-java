// Copyright (c) 2009 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * An extension of {@link BigDecimal} that can represent negative zeros.
 * The primary change is the addition of {@link #isNegativeZero()}.
 * <p>
 * <b>WARNING:</b> This class currently does not have any behavioral difference
 * from {@link BigDecimal} except for the following:
 * <ul>
 *   <li>{@link #toString()}, {@link #toEngineeringString()}, and
 *     {@link #toPlainString()} print a negative sign if necessary.
 *   </li>
 *   <li>{@link #floatValue()} and {@link #doubleValue()} return negative zero
 *     results.
 *   </li>
 *   <li>{@link #abs} does the right thing.
 * </ul>
 * This class does <b>NOT</b> override {@link #equals} or {@link #compareTo}
 * so those methods cannot distinguish positive and negative zeros.
 * <p>
 * It also does not currently override any of the numerical methods,
 * <em>but it may do so in the future.</em>  If you are concerned about
 * consistent treatment of negative zeros in future releases, you may wish to
 * use {@link #plainBigDecimal} before performing those operations.
 */
public class Decimal
    extends BigDecimal
{
    private static final long serialVersionUID = 1L;

    private static final class NegativeZero extends Decimal
    {
        private static final long serialVersionUID = 1L;

        private NegativeZero(int scale)
        {
            super(BigInteger.ZERO, scale);
        }

        private NegativeZero(int scale, MathContext mc)
        {
            super(BigInteger.ZERO, scale, mc);
        }


        @Override
        public float floatValue()
        {
            float v = super.floatValue();
            if (Float.compare(0f, v) <= 0) v = -1 * v;
            return v;
        }

        @Override
        public double doubleValue()
        {
            double v = super.doubleValue();
            if (Double.compare(0d, v) <= 0) v = -1 * v;
            return v;
        }


        @Override
        public BigDecimal abs()
        {
            return new BigDecimal(unscaledValue(), scale());
        }

        @Override
        public BigDecimal abs(MathContext mc)
        {
            return new BigDecimal(unscaledValue(), scale(), mc);
        }

        // TODO signum might break clients if -0 returns <0 instead of 0
        // TODO static negate(BigDecimal)
        // TODO hashcode

        // TODO some other things:
        //   * byte/int/longValueExact throws?
        //   * movePointLeft/Right
        //   * round
        //   * scaleByPowerOfTen
        //   * setScale
        //   * stripTrailingZeros
        //   * toBigIntegerExact

        @Override
        public String toString()
        {
            return '-' + super.toString();
        }

        @Override
        public String toEngineeringString()
        {
            return '-' + super.toEngineeringString();
        }

        @Override
        public String toPlainString()
        {
            return '-' + super.toPlainString();
        }
    }

    /**
     * The value 0, with a scale of 0.
     */
    public static final Decimal ZERO = new Decimal(0);

    /**
     * The value -0, with a scale of 0.
     */
    public static final Decimal NEGATIVE_ZERO = new NegativeZero(0);


    public static boolean isNegativeZero(BigDecimal val)
    {
        return (val.getClass() == NegativeZero.class);
    }

    public static BigDecimal plainBigDecimal(BigDecimal val)
    {
        if (val.getClass() == BigDecimal.class) return val;
        return new BigDecimal(val.unscaledValue(), val.scale());
    }


    //=========================================================================

    /**
     * Returns a negative-zero decimal value, with the given number of
     * significant digits (zeros).
     *
     * @param scale the number of significant digits (zeros) after the decimal
     * point.
     */
    public static Decimal negativeZero(int scale)
    {
        return new NegativeZero(scale);
    }

    /**
     * Returns a negative-zero decimal value, with the given number of
     * significant digits (zeros) and given context.
     *
     * @param scale the number of significant digits (zeros) after the decimal
     * point.
     */
    public static Decimal negativeZero(int scale, MathContext mc)
    {
        return new NegativeZero(scale, mc);
    }


    public static Decimal valueOf(BigInteger unscaledVal, int scale)
    {
        return new Decimal(unscaledVal, scale);
    }

    public static Decimal valueOf(BigInteger unscaledVal, int scale,
                                        MathContext mc)
    {
        return new Decimal(unscaledVal, scale, mc);
    }


    public static Decimal valueOf(BigInteger val)
    {
        return new Decimal(val);
    }

    public static Decimal valueOf(BigInteger val, MathContext mc)
    {
        return new Decimal(val, mc);
    }


    public static Decimal valueOf(int val)
    {
        return new Decimal(val);
    }

    public static Decimal valueOf(int val, MathContext mc)
    {
        return new Decimal(val, mc);
    }


    public static Decimal valueOf(long val)
    {
        return new Decimal(val);
    }

    public static Decimal valueOf(long val, MathContext mc)
    {
        return new Decimal(val, mc);
    }

    public static Decimal valueOf(double val)
    {
        if (0d == val && Double.compare(val, 0d) < 0)
        {
            // Simulate BigDecimal.valueOf(0d) which has scale of 1
            return new NegativeZero(1);
        }
        return new Decimal(Double.toString(val));
    }

    public static Decimal valueOf(double val, MathContext mc)
    {
        if (0d == val && Double.compare(val, 0d) < 0)
        {
            return new NegativeZero(1, mc);
        }
        return new Decimal(Double.toString(val), mc);
    }

    public static Decimal valueOf(BigDecimal val)
    {
        if (val instanceof Decimal) return (Decimal) val;
        return new Decimal(val.unscaledValue(), val.scale());
    }

    public static Decimal valueOf(BigDecimal val, MathContext mc)
    {
        return new Decimal(val.unscaledValue(), val.scale(), mc);
    }

    public static Decimal valueOf(String val)
    {
        boolean negative = val.startsWith("-");
        Decimal ibd = new Decimal(val);
        if (negative && ibd.signum() == 0)
        {
            ibd = new NegativeZero(ibd.scale());
        }
        return ibd;
    }

    public static Decimal valueOf(String val, MathContext mc)
    {
        boolean negative = val.startsWith("-");
        Decimal ibd = new Decimal(val, mc);
        if (negative && ibd.signum() == 0)
        {
            ibd = new NegativeZero(ibd.scale(), mc);
        }
        return ibd;
    }


    //=========================================================================
    // Constructors are private so we have flexibility in changing
    // implementation of how we create negative zero.
    // We force the user to call static valueOf() methods instead.

    private Decimal(BigInteger unscaledVal, int scale)
    {
        super(unscaledVal, scale);
    }

    private Decimal(BigInteger unscaledVal, int scale, MathContext mc)
    {
        super(unscaledVal, scale, mc);
    }


    private Decimal(BigInteger val)
    {
        super(val);
    }

    private Decimal(BigInteger val, MathContext mc)
    {
        super(val, mc);
    }


    private Decimal(int val)
    {
        super(val);
    }

    private Decimal(int val, MathContext mc)
    {
        super(val, mc);
    }

    private Decimal(long val)
    {
        super(val);
    }

    private Decimal(long val, MathContext mc)
    {
        super(val, mc);
    }


    private Decimal(double val)
    {
        super(val);
    }

    private Decimal(double val, MathContext mc)
    {
        super(val, mc);
    }


    // TODO create static valueOf to check for -0
    private Decimal(char[] in, int offset, int len)
    {
        super(in, offset, len);
    }

    // TODO create static valueOf to check for -0
    private Decimal(char[] in, int offset, int len, MathContext mc)
    {
       super(in, offset, len, mc);
    }


    // TODO create static valueOf to check for -0
    private Decimal(char[] in)
    {
        super(in);
    }

    // TODO create static valueOf to check for -0
    private Decimal(char[] in, MathContext mc)
    {
        super(in, mc);
    }


    private Decimal(String val)
    {
        super(val);
    }

    private Decimal(String val, MathContext mc)
    {
        super(val, mc);
    }


    //========================================================================

    public final boolean isNegativeZero()
    {
        return (getClass() == NegativeZero.class);
    }
}
