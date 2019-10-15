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
 * @since 2019/3/19 0:24
 */
public class Const extends Factor {
    private BigInteger value;
    private static final Pattern CON_FAC = Pattern.compile("[\\+-]?\\d+$");

    Const(String string) {
        Matcher matcher = CON_FAC.matcher(string);
        if (!matcher.lookingAt()) {
            exit("WRONG CONST for " + string);
        }
        this.value = new BigInteger(string);
        this.setType(FacType.CONST);
    }

    Const() {
        this.setType(FacType.CONST);
        this.value = BigInteger.ZERO;
    }

    @Override public Factor combine(Factor factor) {
        Const result = new Const();
        result.value = this.value.multiply(factor.getValue());
        return result;
    }

    @Override public ArrayList<Factor> calDeriv() {
        Const result = new Const();
        result.value = BigInteger.ZERO;
        ArrayList<Factor> resultList = new ArrayList<Factor>();
        resultList.add(result);
        return resultList;
    }

    @Override public BigInteger getValue() {
        return this.value;
    }

    @Override protected String hashString() {
        return "";
    }

    @Override public String toString() {
        return this.value.toString();
    }
}
