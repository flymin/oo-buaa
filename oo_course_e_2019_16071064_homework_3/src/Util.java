import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 XXX, Inc. All rights reserved. <p>
 * Company: XXX科技有限公司<p>
 *
 * @author gaoruiyuan
 * @since 2019/3/19 13:00
 */
public abstract class Util {
    private int index;
    private String input;
    private static Pattern NUM = Pattern.compile("[\\-\\+]?\\d+");

    String nextItem(boolean poly) {
        String item = null;
        //当前要处理的poly字符串
        String subPoly = this.input.substring(this.index);
        int endIndex;
        if (subPoly.startsWith("+") || subPoly.startsWith("-") ||
            subPoly.startsWith("*")) {
            endIndex = 1;
        } else {
            endIndex = 0;
        }
        LinkedList<Character> stack = new LinkedList<Character>();
        while (true) {
            if (endIndex >= subPoly.length()) {
                if (stack.isEmpty()) {
                    break;
                } else {
                    return null;
                }
            }
            // 处理括号
            if (subPoly.charAt(endIndex) == '(') {
                stack.addLast('(');
            } else if (subPoly.charAt(endIndex) == ')') {
                if (stack.isEmpty()) {
                    return null;
                } else {
                    stack.pop();
                }
            } else {
                if (poly) {
                    int temp = this.polyProcess(subPoly, endIndex, stack);
                    if (temp == -1) {
                        break;
                    }
                    endIndex = temp;
                } else {
                    if (subPoly.charAt(endIndex) == '*') {
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }
            }
            endIndex++;
        }
        this.index += endIndex;
        return subPoly.substring(0, endIndex);
    }

    private int polyProcess(String subPoly,
        int endIndex, LinkedList<Character> stack) {
        // 涉及到可能出现不应该分割的+-符号
        int result = endIndex;
        if (subPoly.charAt(endIndex) == '*' ||
            subPoly.charAt(endIndex) == '^') {
            Matcher matcher = NUM.matcher(subPoly.substring(endIndex + 1));
            if (matcher.lookingAt()) {
                result += matcher.end();
            }
        } else {
            if (subPoly.charAt(endIndex) == '+'
                || subPoly.charAt(endIndex) == '-') {
                if (stack.isEmpty()) {
                    return -1;
                }
            }
        }
        return result;
    }

    public int getIndex() {
        return index;
    }

    public String getInput() {
        return input;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setInput(String input) {
        this.input = input;
    }

    boolean hitEnd() {
        return this.index == this.input.length();
    }

    public abstract String hashString();

    @Override
    public boolean equals(Object obj) {
        Util item = (Util)obj;
        return this.hashString().equals(item.hashString());
    }

    @Override
    public int hashCode() {
        return this.hashString().hashCode();
    }

    protected void sortedInsert(String str, LinkedList<String> strs) {
        index = 0;
        for (String item : strs) {
            if (str.compareTo(item) > 0) {
                index++;
            } else {
                break;
            }
        }
        strs.add(index, str);
    }
}
