import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gaoruiyuan
 */
class FormChecker {

    private String poly;
    private int index;
    private static final Pattern SPACE_CHECK =
        Pattern.compile("(\\d[\\s]+\\d)|"
            + "([+\\-^][\\s]*[+-][\\s]+\\d)");
    private static final Pattern ERROR_FORM =
        Pattern.compile("(.*([^0-9x^*+\\-\\r\\n]).*)|"
            + "(.*([+-]{3,}).*)|(.*\\^([+-]{2,}).*)");
    private static final Pattern FIRST_ITRM =
        Pattern.compile("[+-]?((\\d+(\\*x(\\^[+-]?\\d+)?)?)|"
            + "(x(\\^([+-]?)\\d+)?))([+-]|$)");
    private static final Pattern SEC_ITEM =
        Pattern.compile("[+-]((\\d+(\\*x(\\^[+-]?\\d+)?)?)|"
            + "(x(\\^([+-]?)\\d+)?))([+-]|$)");
    private static final String ADD = "+";
    private static final String SUB = "-";

    FormChecker(final String input) {
        this.poly = input;
        this.index = 0;
    }

    private void reformat() {
        // 保证每个项前面只有一个+-运算符
        this.poly = this.poly.replaceAll("\\+\\+", "+");
        this.poly = this.poly.replaceAll("\\+-", "-");
        this.poly = this.poly.replaceAll("-\\+", "-");
        this.poly = this.poly.replaceAll("--", "+");
    }

    private boolean checkBan() {
        // 检查数字之间的空格，制表符
        // out: 不包含空格制表符，且每个项前面只有一个+-运算符
        Matcher matcher = SPACE_CHECK.matcher(this.poly);
        if (matcher.find()) {
            return false;
        }
        // 去掉所有空白字符
        this.poly = this.poly.replaceAll("\\s*", "");
        // 检查是否有非法字符
        // 检查运算符合法性
        // 因为reformat会处理+-符号，所以需要保证其合法性
        matcher = ERROR_FORM.matcher(this.poly);
        if (matcher.find()) {
            return false;
        }
        // 重新格式化
        this.reformat();
        return true;
    }

    String nextItem() {
        Pattern itemPattern;
        String item = null;
        if (this.index == 0) {
            if (!this.checkBan()) {
                return null;
            }
            itemPattern = FIRST_ITRM;
        } else {
            itemPattern = SEC_ITEM;
        }
        String subPoly = this.poly.substring(this.index);
        Matcher matcher = itemPattern.matcher(subPoly);
        if (matcher.lookingAt()) {
            item = matcher.group();
            if (item.endsWith(ADD) || item.endsWith(SUB)) {
                this.index += matcher.end() - 1;
                item = item.substring(0, item.length() - 1);
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
