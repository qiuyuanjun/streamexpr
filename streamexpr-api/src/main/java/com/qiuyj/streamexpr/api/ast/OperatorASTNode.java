package com.qiuyj.streamexpr.api.ast;

import com.qiuyj.streamexpr.api.utils.ArrayUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 代表一个操作符的抽象语法树节点
 * @author qiuyj
 * @since 2023-07-23
 */
public interface OperatorASTNode extends ExpressionASTNode {

    /**
     * 得到左操作数
     * @return 左操作数对应的抽象语法树节点
     */
    ASTNode getLeftOperand();

    /**
     * 得到右操作数
     * @return 右操作数对应的抽象语法树节点
     */
    ASTNode getRightOperand();

    /**
     * 得到操作符
     * @return 操作符枚举
     */
    Operator getOperator();

    enum Operator {

        EQ("==", "eq", "EQ") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return new EqExpressionASTNode(left, right);
            }
        },
        NEQ("!=", "neq", "NEQ") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return new NeqExpressionASTNode(left, right);
            }
        },
        GT(">", "gt", "GT") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return new GtExpressionASTNode(left, right);
            }
        },
        GTEQ(">=", "gteq", "GTEQ") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return new GteqExpressionASTNode(left, right);
            }
        },
        LT("<", "lt", "LT") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return new LtExpressionASTNode(left, right);
            }
        },
        LTEQ("<=", "lteq", "LTEQ") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return new LteqExpressionASTNode(left, right);
            }
        },
        OR,
        AND,
        PLUS,
        MINUS,
        MULTI,
        DIV,
        MOD,
        INC("++") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return null;
            }
        },
        DEC("--") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return null;
            }
        },
        BANG("!") {
            @Override
            public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
                return null;
            }
        },
        ;

        private static final Map<String, Operator> OPERATORS;
        static {
            Map<String, Operator> operators = new HashMap<>();
            for (Operator operator : values()) {
                if (Objects.nonNull(operator.supportedNames)) {
                    for (String name : operator.supportedNames) {
                        operators.put(name, operator);
                    }
                }
            }
            OPERATORS = Collections.unmodifiableMap(operators);
        }

        private final String[] supportedNames;

        Operator(String firstName, String... otherNames) {
            supportedNames = ArrayUtils.makeArray(String.class, firstName, otherNames);
        }

        Operator() {
            supportedNames = null;
        }

        public OperatorASTNode createOperatorASTNode(ASTNode left, ASTNode right, Object... args) {
            return null;
        }

        public static Operator getByName(String name) {
            return OPERATORS.get(name);
        }
    }
}
