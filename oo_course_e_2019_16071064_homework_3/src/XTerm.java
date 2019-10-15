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
 * @since 2019/3/19 0:23
 */
public class XTerm extends Factor {
    private BigInteger exp;
    private static final Pattern X_FAC = Pattern.compile("x(\\^[+-]?\\d+)?$");
    private static final Pattern CON_FAC = Pattern.compile("[+-]?\\d+$");

    XTerm(String string) {
        this.setType(FacType.X_TERM);
        Matcher matcher = X_FAC.matcher(string);
        if (!matcher.lookingAt()) {
            exit("WRONG XTerm for " + string);
        }
        matcher = CON_FAC.matcher(string);
        if (matcher.find()) {
            this.exp = new BigInteger(matcher.group());
            if (this.exp.compareTo(BigInteger.valueOf(10000)) > 0) {
                exit("TOO BIG");
            }
        } else {
            this.exp = BigInteger.ONE;
        }
    }

    XTerm() {
        this.setType(FacType.X_TERM);
        this.exp = BigInteger.ONE;
    }

    @Override public Factor combine(Factor factor) {
        XTerm term = new XTerm();
        term.exp = this.exp.add(factor.getValue());
        return this.inspectZeroExp(term);
    }

    /**
     * 如果求导后是常数只返回一项，如果包含x返回两项
     * 即不会返回指数为零的情况
     *
     * @return
     */
    @Override public ArrayList<Factor> calDeriv() {
        XTerm result = new XTerm();
        result.exp = this.exp.subtract(BigInteger.ONE);
        ArrayList<Factor> resultList = new ArrayList<Factor>();
        Const constResult;
        constResult = new Const(this.exp.toString());
        if (result.exp.equals(BigInteger.ZERO) || constResult.getValue()
            .equals(BigInteger.ZERO)) {
            resultList.add(constResult);
            return resultList;
        } else {
            resultList.add(constResult);
            resultList.add(result);
            return resultList;
        }
    }

    @Override public BigInteger getValue() {
        return this.exp;
    }

    @Override protected String hashString() {
        return "x";
    }

    @Override public String toString() {
        if (this.exp.equals(BigInteger.ONE)) {
            return "x";
        } else {
            return "x^" + this.exp.toString();
        }
    }
}
