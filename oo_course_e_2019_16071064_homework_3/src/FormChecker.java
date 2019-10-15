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
        /* 数字之间 */
        "(\\d[\\s]+\\d)|"
            /* 一个^*或者两个+-之后符号不可分割 */
            + "(([\\^*]|([+\\-]\\s*[+\\-]))\\s*[+\\-]\\s+\\d)|"
            /* sin cos之间不可分 */
            + "(s!(i|\\s*\\())|(i[^n])|(c[^o])|(o[^s])");
    /**
     * 这次作业没有引入更多许可字符
     */
    private static final Pattern ERROR_CHAR =
        Pattern.compile("(.*([^0-9x^*+\\-\\r\\n\\t ()sincos]).*)");
    /**
     * 考虑连续+-号出现错误的可能性，没有变化
     */
    private static final Pattern ERROR_OP =
        Pattern.compile("(^([+\\-]{3,})[^\\d].*)|(.*([+\\-]{4,}).*)|"
            + "(.*[*^]([+-]{2,}).*)");
    /**
     * ERROR_PAR保证括号之间的内容正确，空格已消除
     * private static final Pattern ERROR_PAR =
     *  Pattern.compile("(\\([^x]+)|(\\(x[^)]+)");
     * 括号之间可以是任何内容
     */
    /**
     * ERROR_RES前提是空格已经消除，保留字连续，保证左括号前内容正确
     */
    private static final Pattern ERROR_LPAR =
        Pattern.compile("((sin)|(cos))[^(]+");
    /**
     * ERROR_RES前提是空格已经消除，保留字连续，保证右括号后内容正确
     */
    private static final Pattern ERROR_RPAR =
        Pattern.compile("\\)[^\\^+\\-*)]");
    private static final Pattern[] CHECK =
        {ERROR_OP, ERROR_LPAR, ERROR_RPAR};
    private static final Pattern SPLIT =
        Pattern.compile("[x\\)\\d]([+\\-]|$)");
    private static final String ADD = "+";
    private static final String SUB = "-";
    private String poly;

    FormChecker(final String input) {
        this.poly = input;
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
        // 重新格式化，每项之间只保留一个+-
        this.reformat();
        return true;
    }

    public String getPoly() {
        if (!this.checkBan()) {
            return null;
        }
        return this.poly;
    }
}
