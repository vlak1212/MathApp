package com.example.ocr2;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathSolver {

    public String mathSolving(String coBang) {
        try {
            if (coBang.contains("=")) {
                return giaiPhuongTrinh(coBang);
            } else {
                return giaiToan(coBang);
            }
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }

    private String giaiPhuongTrinh(String expression) {
        String variable = getVariable(expression);
        if (variable != null) {
            if (isQuadraticEquation(expression, variable)) {
                return solveQuadraticEquation(expression, variable);
            } else if (isLinearEquation(expression, variable)) {
                return solveLinearEquation(expression, variable);
            }
        }
        return "Không thể giải phương trình này.";
    }

    private Expression buildExpression(String expression) {
        return new ExpressionBuilder(expression)
                .operator(new Operator("√", 1, true, Operator.PRECEDENCE_POWER + 1) {
                    @Override
                    public double apply(double... args) {
                        return Math.sqrt(args[0]);
                    }
                })
                .operator(new Operator("^", 2, true, Operator.PRECEDENCE_POWER) {
                    @Override
                    public double apply(double... args) {
                        return Math.pow(args[0], args[1]);
                    }
                })
                .build();
    }

    private String giaiToan(String equation) {
        try {
            Expression exp = buildExpression(equation);
            double result = exp.evaluate();
            return String.format("Kết quả: %s", result);
        } catch (Exception e) {
            return "Lỗi khi tính toán: " + e.getMessage();
        }
    }

    private boolean isLinearEquation(String equation, String variable) {
        Pattern pattern = Pattern.compile(variable + "(?!\\^2)");
        Matcher matcher = pattern.matcher(equation);
        return matcher.find();
    }

    private boolean isQuadraticEquation(String equation, String variable) {
        Pattern pattern = Pattern.compile(variable + "\\^2");
        Matcher matcher = pattern.matcher(equation);
        return matcher.find();
    }

    private String solveLinearEquation(String equation, String variable) {
        String[] parts = equation.split("=");
        if (parts.length != 2) {
            return "Lỗi: Phương trình không hợp lệ.";
        }
        try {
            String leftSide = parts[0].replace(" ", "");
            String rightSide = parts[1].replace(" ", "");
            double leftCoefficient = getCoefficient(leftSide, variable);
            double rightCoefficient = getCoefficient(rightSide, variable);
            double leftConstant = getConstant(leftSide, variable);
            double rightConstant = getConstant(rightSide, variable);

            double coefficient = leftCoefficient - rightCoefficient;
            double constant = rightConstant - leftConstant;

            if (coefficient == 0) {
                return constant == 0 ? "Phương trình có vô số nghiệm." : "Phương trình vô nghiệm.";
            }
            double solution = constant / coefficient;
            return "Kết quả: " + variable + " = " + solution;
        } catch (Exception e) {
            return "Lỗi khi giải phương trình: " + e.getMessage();
        }
    }

    private String solveQuadraticEquation(String equation, String variable) {
        String[] parts = equation.split("=");
        if (parts.length != 2) {
            return "Lỗi: Phương trình không hợp lệ.";
        }
        try {
            String leftSide = parts[0].replace(" ", "");
            String rightSide = parts[1].replace(" ", "");

            double a = getQuadraticCoefficient(leftSide, variable, 2);
            double b = getQuadraticCoefficient(leftSide, variable, 1);
            double c = getConstant(leftSide, variable) - getConstant(rightSide, variable);

            double discriminant = b * b - 4 * a * c;
            if (discriminant < 0) {
                return "Phương trình vô nghiệm.";
            } else if (discriminant == 0) {
                double root = -b / (2 * a);
                return "Phương trình có nghiệm kép: " + variable + " = " + root;
            } else {
                double root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
                double root2 = (-b - Math.sqrt(discriminant)) / (2 * a);
                return "Phương trình có hai nghiệm phân biệt: " + variable + " = " + root1 + " và " + variable + " = " + root2;
            }
        } catch (Exception e) {
            return "Lỗi khi giải phương trình: " + e.getMessage();
        }
    }

    private String getVariable(String equation) {
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(equation);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private double getCoefficient(String side, String variable) {
        Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)" + variable + "(?!\\^)");
        Matcher matcher = pattern.matcher(side);
        if (matcher.find()) {
            String coeff = matcher.group(1);
            if (coeff.isEmpty() || coeff.equals("+")) {
                return 1;
            } else if (coeff.equals("-")) {
                return -1;
            }
            return Double.parseDouble(coeff);
        }
        return 0;
    }

    private double getQuadraticCoefficient(String side, String variable, int power) {
        Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)" + variable + "\\^" + power);
        Matcher matcher = pattern.matcher(side);
        if (matcher.find()) {
            String coeff = matcher.group(1);
            if (coeff.isEmpty() || coeff.equals("+")) {
                return 1;
            } else if (coeff.equals("-")) {
                return -1;
            }
            return Double.parseDouble(coeff);
        }
        return 0;
    }

    private double getConstant(String side, String variable) {
        String modifiedSide = side.replaceAll("([+-]?\\d*\\.?\\d*)" + variable + "(\\^\\d*)?", "");
        if (modifiedSide.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(modifiedSide);
    }
}
