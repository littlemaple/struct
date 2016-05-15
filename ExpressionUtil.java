package com.desc;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * Created by 44260 on 2016/3/28.
 */
public class ExpressionUtil {

    /**
     * @param expression like {@code (2+2)*2-2}
     * @return the result of the expression
     */
    public static double calcExpression(String expression) {
        return calc(changeToSuffix(expression));
    }

    private static double calc(@NotNull List<String> suffix) {
        Stack<String> stack = new Stack<>();
        for (String element : suffix) {
            if (Objects.equals(element, " "))
                continue;
            if (isOperateSign(element)) {
                if (stack.size() < 2)
                    throw new IllegalArgumentException("please be sure the expression is illegal");
                else {
                    String params2 = stack.pop();
                    String params1 = stack.pop();
                    double result = calc(params1, params2, element);
                    if (result == -1)
                        throw new IllegalArgumentException("the suffix expression is illegal");
                    stack.push(String.valueOf(result));
                }
            } else {
                stack.push(element);
            }
        }
        if (stack.size() == 1)
            return Double.valueOf(stack.pop());
        throw new IllegalArgumentException("be sure the expression is illegal");
    }


    /**
     * @return the result of the param and param by special operate
     */

    private static double calc(@NotNull String params1, @NotNull String params2, char operate) {
        switch (operate) {
            case '+':
                return Double.valueOf(params1) + Double.valueOf(params2);
            case '-':
                return Double.valueOf(params1) - Double.valueOf(params2);
            case '*':
                return Double.valueOf(params1) * Double.valueOf(params2);
            case '/':
                return Double.valueOf(params1) / Double.valueOf(params2);
            case '^':
                return Math.pow(Double.valueOf(params1), Double.valueOf(params2));
            case '%':
                return Double.valueOf(params1) % Double.valueOf(params2);
        }
        return -1;
    }

    private static double calc(@NotNull String params1, @NotNull String params2, String operate) {
        if (operate == null || operate.length() != 1)
            return -1;
        return calc(params1, params2, operate.charAt(0));
    }

    private static boolean isOperateSign(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '^';
    }

    private static boolean isOperateSign(String operate) {
        if (operate == null || operate.length() != 1)
            return false;
        char ch = operate.charAt(0);
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '^';
    }

    private static boolean isNum(char ch) {
        return ch >= '0' && ch <= '9' || ch == '.';
    }

    private static void addBuffer(@NotNull List<String> list, @NotNull StringBuilder builder) {
        if (builder.length() == 0)
            return;
        list.add(builder.toString());
        builder.setLength(0);

    }

    public static List<String> changeToSuffix(String infix) {
        Stack<Character> signStack = new Stack<>();
        char[] chars = infix.toCharArray();
        List<String> destList = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (char ch : chars) {
            if (ch == ' ')
                continue;
            if (!isNum(ch)) {
                addBuffer(destList, buffer);
            }
            if (ch == '(') {
                signStack.push(ch);
            } else if (ch == ')') {
                while (!signStack.empty() && signStack.peek() != '(') {
                    destList.add(String.valueOf(signStack.pop()));
                }
                signStack.pop();

            } else if (isOperateSign(ch)) {
                Character w = !signStack.empty() ? signStack.peek() : null;
                while (w != null && precedence(w) >= precedence(ch)) {
                    destList.add(String.valueOf(w));
                    signStack.pop();
                    w = !signStack.empty() ? signStack.peek() : null;
                }
                signStack.push(ch);
            } else {
                if (isNum(ch)) {
                    buffer.append(ch);
                }
            }
        }
        addBuffer(destList, buffer);
        while (!signStack.empty()) {
            destList.add(String.valueOf(signStack.pop()));
        }
        return destList;
    }


    public static int precedence(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
            case '%':
                return 3;
            case '(':
            case ')':
            default:
                return 0;
        }
    }


}
