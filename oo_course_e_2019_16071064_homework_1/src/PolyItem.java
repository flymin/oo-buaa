import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gaoruiyuan
 */
public class PolyItem {

    private BigInteger coe = new BigInteger("1");
    private BigInteger exp = new BigInteger("0");
    private static final Pattern COE_NUM =
        Pattern.compile("([+-]\\d*)|([+-]?\\d+)");
    private static final Pattern X_ITEM = Pattern.compile("\\*?x\\^");
    private static final String ADD = "+";
    private static final String SUB = "-";

    PolyItem(final String itemString) {
        // Input: string of item
        Matcher matcher = COE_NUM.matcher(itemString);
        String tailing;
        if (matcher.lookingAt()) {
            if (matcher.group().equals(SUB)) {
                this.coe = new BigInteger("-1");
            } else if (matcher.group().equals(ADD)) {
                this.coe = new BigInteger("1");
            } else {
                this.coe = new BigInteger(matcher.group());
            }
            tailing = itemString.substring(matcher.end());
        } else {
            tailing = itemString;
        }
        if (tailing.length() == 0) {
            return;
        }
        matcher = X_ITEM.matcher(tailing);
        if (matcher.lookingAt()) {
            this.exp = new BigInteger(tailing.substring(matcher.end()));
        } else {
            this.exp = new BigInteger("1");
        }
    }

    void calDeriv() {
        if (this.exp.equals(BigInteger.ZERO)) {
            this.coe = new BigInteger("0");
        } else {
            this.coe = this.coe.multiply(this.exp);
            this.exp = this.exp.subtract(BigInteger.ONE);
        }
    }

    void addCoe(final BigInteger another) {
        this.coe = this.coe.add(another);
    }

    BigInteger getExp() {
        return this.exp;
    }

    BigInteger getCoe() {
        return this.coe;
    }

    @Override
    public String toString() {
        String item = "";
        if (this.coe.equals(BigInteger.ZERO)) {
            return "0";
        } else if (BigInteger.valueOf(-1).equals(this.coe)) {
            if (this.exp.equals(BigInteger.ZERO)) {
                return "-1";
            } else {
                // 后面一定有x
                item += "-";
            }
        } else if (this.coe.equals(BigInteger.ONE)) {
            if (BigInteger.ZERO.equals(this.exp)) {
                return "1";
            }
        } else {
            if (BigInteger.ZERO.equals(this.exp)) {
                item += this.coe.toString();
                return item;
            } else {
                item += this.coe.toString();
                // 后面一定有x
                item += "*";
            }
        }

        if (this.exp.equals(BigInteger.ONE)) {
            item += "x";
        } else if (!this.exp.equals(BigInteger.ZERO)) {
            item += "x^" + this.exp.toString();
        }
        return item;
    }
}
