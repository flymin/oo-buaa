import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gaoruiyuan
 */
class FormChecker {

    /**
     * spaceCheck保证了相邻保留字之间的连续性
     */
    private static final Pattern SPACE_CHECK = Pattern.compile(
        "(\\d[\\s]+\\d)|(([\\^*]|([+\\-]\\s*[+\\-]))[\\s]*[+-][\\s]+\\d)|"
            + "(s!(i|\\s*\\())|(i[^n])|(c[^o])|(o[^s])");
    private static final Pattern ERROR_CHAR =
        Pattern.compile("(.*([^0-9x^*+\\-\\r\\n\\t ()sincos]).*)");
    private static final Pattern ERROR_OP =
        Pattern.compile("(^([+\\-]{3,})[^\\d].*)|(.*([+\\-]{4,}).*)|"
            + "(.*[*^]([+-]{2,}).*)");
    /**
     * ERROR_PAR保证括号之间的内容正确，空格已消除
     */
    private static final Pattern ERROR_PAR =
        Pattern.compile("(\\([^x]+)|(\\(x[^)]+)");
    /**
     * ERROR_RES前提是空格已经消除，保留字连续，保证左括号前内容正确
     */
    private static final Pattern ERROR_LPAR =
        Pattern.compile("((sin)|(cos))[^(]+");
    /**
     * ERROR_RES前提是空格已经消除，保留字连续，保证右括号后内容正确
     */
    private static final Pattern ERROR_RPAR = Pattern.compile("\\)[^\\^+\\-*]");
    private static final Pattern[] CHECK =
        {ERROR_OP, ERROR_PAR, ERROR_LPAR, ERROR_RPAR};
    private static final Pattern SPLIT = Pattern.compile("[x\\)\\d]([+\\-]|$)");
    /*
    private static final Pattern FIRST_ITRM =
        Pattern.compile("[+-]?((\\d+(\\*x(\\^[+-]?\\d+)?)?)|"
            + "(x(\\^([+-]?)\\d+)?))([+-]|$)");
    private static final Pattern SEC_ITEM =
        Pattern.compile("[+-]((\\d+(\\*x(\\^[+-]?\\d+)?)?)|"
            + "(x(\\^([+-]?)\\d+)?))([+-]|$)");
    */
    private static final String ADD = "+";
    private static final String SUB = "-";
    private String poly;
    private int index;

    FormChecker(final String input) {
        this.poly = input;
        this.index = 0;
    }

    private void reformat() {
        // 保证每个项前面只有一个+-运算符
        this.poly = this.poly.replaceAll("\\+\\+\\+?", "+");
        this.poly = this.poly.replaceAll("\\+?\\-\\+?\\-\\+?", "+");
        this.poly = this.poly.replaceAll("\\-\\-\\-", "-");
        this.poly = this.poly.replaceAll("\\+\\+\\-", "-");
        this.poly = this.poly.replaceAll("\\+\\-\\+?", "-");
        this.poly = this.poly.replaceAll("\\-\\+\\+?", "-");
    }

    private boolean checkBan() {
        // 检查数字之间的空格，制表符
        // out: 不包含空格制表符，且每个项前面只有一个+-运算符
        // 检查字符是否合法
        Matcher matcher = ERROR_CHAR.matcher(this.poly);
        if (matcher.find()) {
            return false;
        }
        matcher = SPACE_CHECK.matcher(this.poly);
        if (matcher.find()) {
            return false;
        }
        // 去掉所有空白字符
        this.poly = this.poly.replaceAll("\\s*", "");
        // 循环检查合法性
        for (Pattern checkI : CHECK) {
            matcher = checkI.matcher(this.poly);
            if (matcher.find()) {
                return false;
            }
        }
        // 重新格式化
        this.reformat();
        return true;
    }

    String nextItem() {
        String item = null;
        if (this.index == 0) {
            if (!this.checkBan()) {
                return null;
            }
        }
        String subPoly = this.poly.substring(this.index);
        Matcher matcher = SPLIT.matcher(subPoly);
        if (matcher.find()) {
            item = subPoly.substring(0, matcher.start() + 1);
            if (matcher.group().endsWith("+") || matcher.group()
                .endsWith("-")) {
                this.index += matcher.end() - 1;
            } else {
                this.index += matcher.end();
            }
        }
        return item;
    }

    boolean hitEnd() {
        return this.index == this.poly.length();
    }
}
