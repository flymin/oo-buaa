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
 * @since 2019/3/18 23:52
 */
public abstract class Factor {
    private FacType type;

    Factor() {
        this.type = FacType.NOT_KNOWN;
    }

    public abstract Factor combine(Factor factor);

    public abstract ArrayList<Factor> calDeriv();

    public abstract BigInteger getValue();

    protected Factor inspectZeroExp(Factor result) {
        if (result.getValue().equals(BigInteger.ZERO)) {
            Const con = new Const("1");
            return con;
        } else {
            return result;
        }
    }

    protected Factor readfactor(String string, Pattern startP, Pattern endP) {
        int start = 0;
        int end = string.length();
        Matcher matcher = startP.matcher(string);
        if (matcher.find()) {
            start = matcher.end();
        } else {
            exit(string + " in start");
        }
        matcher = endP.matcher(string);
        if (matcher.find()) {
            end = matcher.start();
        } else {
            exit(string + " in end");
        }
        String factorString = string.substring(start, end);
        Factor factor = Factor.typeDet(factorString);
        return factor;
    }

    public static void exit(String msg) {
        System.out.println("WRONG FORMAT!");
        // System.out.println(msg);
        System.exit(0);
    }

    public static Factor typeDet(String factorString) {
        Factor factor;
        switch (factorString.charAt(0)) {
            case 'x':
                factor = new XTerm(factorString);
                if (factor.getValue().equals(BigInteger.ZERO)) {
                    factor = new Const("1");
                }
                break;
            case 's':
                factor = new Sin(factorString);
                if (factor.getValue().equals(BigInteger.ZERO)) {
                    factor = new Const("1");
                }
                break;
            case 'c':
                factor = new Cos(factorString);
                if (factor.getValue().equals(BigInteger.ZERO)) {
                    factor = new Const("1");
                }
                break;
            case '(':
                factor = new Expr(factorString);
                break;
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
            case '+':
            case '-':
                factor = new Const(factorString);
                break;
            default:
                factor = new Const("1");
                exit("NO TYPE MEET for " + factorString);
        }
        return factor;
    }

    public FacType getType() {
        return type;
    }

    public void setType(FacType type) {
        this.type = type;
    }

    protected abstract String hashString();

    @Override public int hashCode() {
        return this.hashString().hashCode();
    }

    @Override public abstract String toString();

    @Override
    public boolean equals(Object obj) {
        Factor factor = (Factor) obj;
        return this.hashString().equals(factor.hashString());
    }
}
