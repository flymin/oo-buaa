import java.math.BigInteger;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/3/19 0:10
 */
public class Cos extends Factor {
    private Factor variable;
    private BigInteger exp;
    private static final Pattern COS_FAC =
        Pattern.compile("cos\\(.*\\)(\\^[\\+\\-]?\\d+)?$");
    private static final Pattern COS_E =
        Pattern.compile("cos\\(.*\\)\\^[\\+\\-]?\\d+$");
    private static final Pattern COS_N = Pattern.compile("[\\+\\-]?\\d+$");
    private static final Pattern START = Pattern.compile("^(cos\\()+?");
    private static final Pattern END =
        Pattern.compile("\\)((\\^[\\+\\-]?\\d+)?)$");

    Cos(String string) {
        this.setType(FacType.TRIG_COS);
        Matcher matcher = COS_FAC.matcher(string);
        if (!matcher.lookingAt()) {
            exit("WRONG Cos for " + string);
        }
        matcher = COS_E.matcher(string);
        if (matcher.lookingAt()) {
            matcher = COS_N.matcher(string);
            if (matcher.find()) {
                this.exp = new BigInteger(matcher.group());
                if (this.exp.compareTo(BigInteger.valueOf(10000)) > 0) {
                    exit("TOO BIG");
                }
            } else {
                exit("WRONG Sin NUM for" + string);
            }
        } else {
            this.exp = BigInteger.ONE;
        }
        this.variable = this.readfactor(string, START, END);
    }

    Cos() {
        this.setType(FacType.TRIG_COS);
        this.variable = null;
        this.exp = BigInteger.ZERO;
    }

    Cos(BigInteger exp, Factor var) {
        this.setType(FacType.TRIG_COS);
        this.variable = var;
        this.exp = exp;
    }

    /**
     * cos(_)^n
     * ->   n * cos(_)^(n-1) * -1 * sin(_) * (_)'
     * ->  -n * cos(_)^(n-1) *      sin(_) * (_)'
     *
     * @return
     */
    @Override public ArrayList<Factor> calDeriv() {
        // (_)'
        ArrayList<Factor> fromVar = this.variable.calDeriv();
        //sin(_)
        Sin sinResult = new Sin(BigInteger.ONE, this.variable);
        //-n
        Const ngN = new Const(this.exp.negate().toString());
        //cos(_)^(n-1)
        Cos cosResult =
            new Cos(this.exp.subtract(BigInteger.ONE), this.variable);
        if (cosResult.exp.equals(BigInteger.ZERO)) {
            fromVar.add(sinResult);
            fromVar.add(ngN);
        } else {
            fromVar.add(cosResult);
            fromVar.add(ngN);
            fromVar.add(sinResult);
        }
        return fromVar;
    }

    @Override public BigInteger getValue() {
        return this.exp;
    }

    @Override public Factor combine(Factor factor) {
        Cos result = new Cos();
        result.exp = this.exp.add(factor.getValue());
        result.variable = this.variable;
        return this.inspectZeroExp(result);
    }

    @Override protected String hashString() {
        String result = "";
        result += "[" + this.variable.hashString() + "]";
        result += "cos";
        return result;
    }

    @Override public String toString() {
        if (this.exp.equals(BigInteger.ONE)) {
            return "cos(" + this.variable.toString() + ")";
        } else {
            return "cos(" + this.variable.toString() + ")^" + this.exp
                .toString();
        }
    }
}
