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
        try {
            Expression exp = new ExpressionBuilder(expression)
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
            double result = exp.evaluate();
            return String.format("Kết quả: %s", result);
        } catch (Exception e) {
            return "Lỗi khi tính toán: " + e.getMessage();
        }
    }

    private String giaiToan(String equation) {
        String variable = getVariable(equation);
        if (variable != null) {
            return solveLinearEquation(equation, variable);
        }
        return "Không thể giải phương trình này.";
    }

    private String getVariable(String equation) {
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(equation);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
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

    private double getCoefficient(String side, String variable) {
        Pattern pattern = Pattern.compile("([+-]?\\d*\\.?\\d*)" + variable);
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
        side = side.replaceAll("([+-]?\\d*\\.?\\d*)" + variable, "");
        if (side.isEmpty()) {
            return 0;
        }
        return Double.parseDouble(side);
    }
}
