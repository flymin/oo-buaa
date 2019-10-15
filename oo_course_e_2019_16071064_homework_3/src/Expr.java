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
 * @since 2019/3/19 0:26
 */
public class Expr extends Factor {
    private Poly expression;
    private static Integer polyNum = 0;
    private Integer myIndex;
    private static final Pattern PAR =
        Pattern.compile("\\(.*\\)$");

    Expr(String string) {
        this.setType(FacType.EXPR);
        this.myIndex = polyNum++;
        Matcher matcher = PAR.matcher(string);
        if (!matcher.lookingAt()) {
            exit("WRONG EXPR for " + string);
        }
        String polyString = string.substring(1, string.length() - 1);
        this.expression = new Poly(polyString);
    }

    Expr(Poly poly) {
        this.setType(FacType.EXPR);
        this.myIndex = polyNum++;
        this.expression = (Poly) poly.clone();
    }

    private Factor shrink(Expr expr) {
        Factor result = expr.expression.onlyFactor();
        if (result != null) {
            return result;
        } else {
            return expr;
        }
    }

    @Override public Factor combine(Factor factor) {
        exit("CANNOT COMBINE EXPR");
        return null;
    }

    @Override public ArrayList<Factor> calDeriv() {
        Expr factor = new Expr(this.expression);
        factor.expression.calDeriv();
        ArrayList<Factor> result = new ArrayList<>();
        result.add(this.shrink(factor));
        return result;
    }

    @Override public BigInteger getValue() {
        return BigInteger.valueOf(this.expression.getItemNum());
    }

    public Poly getExpression() {
        return expression;
    }

    @Override protected String hashString() {
        String result = this.expression.hashString();
        result += "_" + this.myIndex.toString();
        return result;

    }

    @Override public String toString() {
        return "(" + this.expression.toString() + ")";
    }
}
