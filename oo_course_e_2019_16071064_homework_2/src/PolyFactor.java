import java.math.BigInteger;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * 1. 输入项中的因子，判断因子格式是否符合标准
 * 2. 判断因子类型{常数、x幂、三角函数}
 * </p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/3/12 11:46
 */
public class PolyFactor {
    private static Pattern TRIG =
        Pattern.compile("((sin)|(cos))(\\^[+\\-]?\\d+)?");
    private static Pattern COS = Pattern.compile("cos.*");
    private static Pattern XTERM = Pattern.compile("x(\\^[+\\-]?\\d+)?");
    private static Pattern NUM = Pattern.compile("[+\\-]?\\d+$");
    private FacType type = FacType.NOT_KNOWN;
    private BigInteger value = BigInteger.ONE;

    /**
     * 要求输入中没有前后的乘号，单纯因子
     * 对于第一个因子的符号也在item中处理
     */
    PolyFactor(final String factor) {
        if (TRIG.matcher(factor).lookingAt()) {
            if (COS.matcher(factor).lookingAt()) {
                this.type = FacType.TRIG_COS;
            } else {
                this.type = FacType.TRIG_SIN;
            }
        } else if (XTERM.matcher(factor).lookingAt()) {
            this.type = FacType.X_TERM;
        } else {
            exit("EXIT FROM FACTOR_NO");
        }
        String[] strs = factor.split("\\^");
        if (strs.length == 1) {
            this.value = BigInteger.ONE;
        } else if (strs.length == 2 && NUM.matcher(strs[1]).lookingAt()) {
            this.value = new BigInteger(strs[1]);
        } else {
            exit("EXIT FROM FACTOR_TRIG");
        }
        if (this.value.equals(BigInteger.ZERO)) {
            this.value = BigInteger.ONE;
            this.type = FacType.CONST;
        }
    }

    PolyFactor(final FacType type) {
        this.type = type;
    }

    PolyFactor(final BigInteger num) {
        this.value = num;
        this.type = FacType.CONST;
    }

    private void exit(String msg) {
        System.out.println("WRONG FORMAT!");
        System.out.println(msg);
        System.exit(0);
    }

    public PolyFactor genSinAsCos() {
        PolyFactor result = new PolyFactor(FacType.TRIG_SIN);
        result.value = this.getValue();
        return result;
    }

    public ArrayList<PolyFactor> calDeriv() {
        ArrayList<PolyFactor> result = new ArrayList<PolyFactor>();
        PolyFactor copy;
        PolyFactor con;
        PolyFactor trig;
        copy = (PolyFactor)this.clone();
        switch (this.type) {
            case CONST:
                copy.value = BigInteger.ZERO;
                break;
            case X_TERM:
                if (!copy.value.equals(BigInteger.ONE)) {
                    con = new PolyFactor(copy.value);
                    result.add(con);
                    copy.value = copy.value.subtract(BigInteger.ONE);
                } else {
                    copy = new PolyFactor(BigInteger.ONE);
                }
                break;
            case TRIG_SIN:
                if (!copy.value.equals(BigInteger.ONE)) {
                    con = new PolyFactor(copy.value);
                    result.add(con);
                    trig = new PolyFactor(FacType.TRIG_COS);
                    result.add(trig);
                    copy.value = copy.value.subtract(BigInteger.ONE);
                } else {
                    copy = new PolyFactor(FacType.TRIG_COS);
                }
                break;
            case TRIG_COS:
                if (!copy.value.equals(BigInteger.ONE)) {
                    con = new PolyFactor(copy.value.negate());
                    result.add(con);
                    trig = new PolyFactor(FacType.TRIG_SIN);
                    result.add(trig);
                    copy.value = copy.value.subtract(BigInteger.ONE);
                } else {
                    con = new PolyFactor(BigInteger.valueOf(-1));
                    result.add(con);
                    copy = new PolyFactor(FacType.TRIG_SIN);
                }
                break;
            default:
        }
        result.add(copy);
        return result;
    }

    public FacType getType() {
        return type;
    }

    public BigInteger getValue() {
        return value;
    }

    /**
     * 用来完成一个item中的所有factor的合并
     *
     * @param another
     * @return
     */
    public PolyFactor combine(PolyFactor another) {
        assert this.type.equals(another.type);
        PolyFactor result = new PolyFactor(this.type);
        if (this.type.equals(FacType.CONST)) {
            result.value = this.value.multiply(another.value);
            if (result.value.equals(BigInteger.ONE)) {
                return null;
            }
        } else {
            result.value = this.value.add(another.value);
            if (result.value.equals(BigInteger.ZERO)) {
                return null;
            }
        }
        return result;
    }

    @Override public Object clone() {
        PolyFactor copy = new PolyFactor(this.type);
        copy.value = new BigInteger(this.value.toString());
        return copy;
    }

    @Override public String toString() {
        String string = "";
        switch (this.type) {
            case CONST:
                string = this.value.toString();
                return string;
            case X_TERM:
                string = "x";
                break;
            case TRIG_COS:
                string = "cos(x)";
                break;
            case TRIG_SIN:
                string = "sin(x)";
                break;
            default:
        }
        if (!this.value.equals(BigInteger.ONE)) {
            string += "^" + this.value.toString();
        }
        return string;
    }
}