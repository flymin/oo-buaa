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
 * @since 2019/3/19 0:01
 */
public class Sin extends Factor {
    private Factor variable;
    private BigInteger exp;
    private static final Pattern SIN_FAC =
        Pattern.compile("sin\\(.*\\)(\\^[\\+\\-]?\\d+)?$");
    private static final Pattern SIN_E =
        Pattern.compile("sin\\(.*\\)\\^[\\+\\-]?\\d+$");
    private static final Pattern SIN_N = Pattern.compile("[\\+\\-]?\\d+$");
    private static final Pattern START = Pattern.compile("^(sin\\()+?");
    private static final Pattern END =
        Pattern.compile("\\)((\\^[\\+\\-]?\\d+)?)$");

    Sin(String string) {
        this.setType(FacType.TRIG_SIN);
        Matcher matcher = SIN_FAC.matcher(string);
        if (!matcher.lookingAt()) {
            exit("WRONG Sin for " + string);
        }
        matcher = SIN_E.matcher(string);
        if (matcher.lookingAt()) {
            matcher = SIN_N.matcher(string);
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

    Sin() {
        this.setType(FacType.TRIG_SIN);
        this.variable = null;
        this.exp = BigInteger.ZERO;
    }

    Sin(BigInteger exp, Factor var) {
        this.setType(FacType.TRIG_SIN);
        this.variable = var;
        this.exp = exp;
    }

    /**
     * sin(_)^n
     * ->   n * sin(_)^(n-1) * cos(_) * (_)'
     * @return
     */
    @Override public ArrayList<Factor> calDeriv() {
        // (_)'
        ArrayList<Factor> fromVar = this.variable.calDeriv();
        //sin(_)
        Cos cosResult = new Cos(BigInteger.ONE, this.variable);
        //n
        Const poN = new Const(this.exp.toString());
        //cos(_)^(n-1)
        Sin sinResult =
            new Sin(this.exp.subtract(BigInteger.ONE), this.variable);
        if (sinResult.exp.equals(BigInteger.ZERO)) {
            fromVar.add(cosResult);
        } else {
            fromVar.add(cosResult);
            fromVar.add(poN);
            fromVar.add(sinResult);
        }
        return fromVar;
    }

    @Override public BigInteger getValue() {
        return this.exp;
    }

    @Override public Factor combine(Factor factor) {
        Sin result = new Sin();
        result.exp = this.exp.add(factor.getValue());
        result.variable = this.variable;
        return this.inspectZeroExp(result);
    }

    @Override protected String hashString() {
        String result = "";
        result += "[" + this.variable.hashString() + "]";
        result += "sin";
        return result;
    }

    @Override public String toString() {
        if (this.exp.equals(BigInteger.ONE)) {
            return "sin(" + this.variable.toString() + ")";
        } else {
            return "sin(" + this.variable.toString() + ")^" + this.exp
                .toString();
        }
    }
}
