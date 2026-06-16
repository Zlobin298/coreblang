package com.example;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTreeProperty;

import com.example.gen.CompilerBaseVisitor;
import com.example.gen.CompilerParser;

// передаем String как generic-тип, чтобы методы возвращали название типа данных
public class TypeChecker extends CompilerBaseVisitor<String> {
    // таблица символов: хранит имя переменной и её тип (например, "x" -> "byte")
    private final Map<String, String> symbolTable = new HashMap<>();
    // специальная карта ANTLR связывает конкретный узел дерева с его типом
    private final ParseTreeProperty<String> nodeTypes = new ParseTreeProperty<>();
    // хранит тип возврата для метода 
    private String currentMethodReturnType = "void";

    // геттер, чтобы передать типы в генератор байт-кода
    public ParseTreeProperty<String> getNodeTypes() {
        return nodeTypes;
    }

    // обработка унарного минуса
    @Override
    public String visitUnaryMinus(CompilerParser.UnaryMinusContext ctx) {
        String innerType = visit(ctx.expr()); 
        
        // проверяем, что тип числовой (int или double)
        if (isNumber(innerType)) {
            return innerType; 
        }
        
        // получаем координаты ошибки в файле пользователя
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        String wrongExpression = ctx.expr().getText(); // Получит текст "hello"
        
        // выводим информативное сообщение
        System.err.printf("Ошибка типов (%d:%d): Унарный минус нельзя применить к типу '%s' в выражении -%s%n", 
                line, column, innerType, wrongExpression);
        
        return "error"; 
    }

    // обработка бинарного сложения и вычитания
    @Override
    public String visitAddSub(CompilerParser.AddSubContext ctx) {
        String left = visit(ctx.expr(0));  // левый операнд
        String right = visit(ctx.expr(1)); // правый операнд
        
        // проверяем, что оба операнда являются числами
        if (!isNumber(left) || !isNumber(right)) {
            System.err.printf("Ошибка типов: Нельзя применить операцию к типам '%s' и '%s'%n", left, right);
            return "error"; 
        }

        String resultType = left;

        // если типы одинаковые (например, int + int), то и результат будет такого же типа
        if (left.equals(right)) {
            return left; 
        }

        // логика приведения типов если смешиваются byte, short и int
        if (!left.equals(right)) {
            if ((left.equals("byte") || left.equals("short") || left.equals("int")) &&
                (right.equals("byte") || right.equals("short") || right.equals("int"))
            ) {
                resultType = "int"; 
            } else {
                throw new RuntimeException("Ошибка типов: Нельзя складывать/вычитать " + left + " и " + right);
            }
        }

        // правило неявного приведения типов (автоматическое расширение до double/float)
        if ("double".equals(left) || "double".equals(right)) {
            resultType = "double"; // любое число + double дает double
        } else if ("float".equals(left) || "float".equals(right)) {
            resultType = "float";  // любое число + float (если нет double) дает float
        } else if ("long".equals(left) || "long".equals(right)) {
            resultType = "long";   // расширение до длинного целого
        }

        nodeTypes.put(ctx, resultType);
        return resultType;
    }

    // обработка бинарного умножения и деления
    @Override
    public String visitMulDiv(CompilerParser.MulDivContext ctx) {
        String left = visit(ctx.expr(0));  // левый операнд
        String right = visit(ctx.expr(1)); // правый операнд
        
        if (!isNumber(left) || !isNumber(right)) {
            throw new RuntimeException(String.format("Семантическая ошибка: Нельзя применить операцию умножения/деления к нечисловым типам '%s' и '%s'", left, right)); 
        }

        String resultType;

        if (left.equals("double") || right.equals("double")) {
            resultType = "double";
        } else if (left.equals("float") || right.equals("float")) {
            resultType = "float";
        } else if (left.equals("long") || right.equals("long")) {
            resultType = "long";
        } else {
            resultType = "int";
        }

        nodeTypes.put(ctx, resultType);        
        return resultType;
    }

    @Override
    public String visitVarDecl(CompilerParser.VarDeclContext ctx) {
        String varType = ctx.typeSpec().getText();
        String varName = ctx.ID().getText();

        // проверяем повторное объявление
        if (symbolTable.containsKey(varName)) {
            throw new RuntimeException("Семантическая ошибка: Переменная '" + varName + "' уже объявлена!");
        }

        // вычисляем тип выражения с правой стороны
        String exprType = visit(ctx.expr()); 

        // если справа находится именно числовое литеральное выражение проверяем его границы
        if (ctx.expr() instanceof CompilerParser.NumberContext) {
            String numberText = ctx.expr().getText().replaceAll("[lLfFdD]$", "");
            
            try {
                // для целых типов парсим в long
                if (varType.equals("byte") || varType.equals("short") || varType.equals("int") || varType.equals("long")) {
                    long value = Long.parseLong(numberText);
                    
                    if (varType.equals("byte") && (value < -128 || value > 127)) {
                        throw new RuntimeException("Ошибка: Число " + value + " не соответствует кастомному типу byte!");
                    } else if (varType.equals("short") && (value < -32_768 || value > 32_767)) {
                        throw new RuntimeException("Ошибка: Число " + value + " не соответствует типу short!");
                    } else if (varType.equals("int") && (value < -2_147_483_648 || value > 2_147_483_647)) {
                        throw new RuntimeException("Ошибка: Число " + value + " не соответствует типу int!");
                    }
                } 
                // для дробных типов парсим в double и проверяем правильные границы
                else if (varType.equals("float") || varType.equals("double")) {
                    double value = Double.parseDouble(numberText);
                    
                    if (varType.equals("float") && (value < -Float.MAX_VALUE || value > Float.MAX_VALUE)) {
                        throw new RuntimeException("Ошибка: Число " + value + " не соответствует типу float!");
                    }
                }
                
                // если число прошло проверку диапазона, мы разрешаем присваивание
                exprType = varType; 

            } catch (NumberFormatException e) {
                throw new RuntimeException("Ошибка: Литерал '" + numberText + "' слишком велик для типа " + varType);
            }
        }

        // проверка совместимости типов
        if (isNumber(varType) && isNumber(exprType) && getTypeRank(varType) >= getTypeRank(exprType)) { }
        else throw new RuntimeException("Ошибка типов: Нельзя присвоить тип '" + exprType + "' в переменную '" + varName + "' типа '" + varType + "'");

        // сохраняем данные
        symbolTable.put(varName, varType);
        nodeTypes.put(ctx, varType); // вешаем наклейку типа на всё объявление
        nodeTypes.put(ctx.expr(), exprType);
        return varType;
    }

    @Override
    public String visitArrayCreation(CompilerParser.ArrayCreationContext ctx) {
        String baseType = ctx.getChild(1).getText(); 

        int dimensions = 0;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("[")) dimensions++;
        }

        String arrayType = baseType + "[]".repeat(dimensions);

        for (var exprCtx : ctx.expr()) {
            String sizeType = visit(exprCtx); // вычисляем тип выражения размера (например, числа 10)
            if (!sizeType.equals("int") && !sizeType.equals("byte") && !sizeType.equals("short")) 
                throw new RuntimeException("Семантическая ошибка: Размер массива должен быть целым числом, но передан " + sizeType);
        }

        nodeTypes.put(ctx, arrayType);
        return arrayType;
    }

    @Override
    public String visitNumber(CompilerParser.NumberContext ctx) {
        String numberText = ctx.INT_LITERAL().getText();
        
        try {
            long value = Long.parseLong(numberText);
            
            if (value >= -128 && value <= 127) {
                nodeTypes.put(ctx, "byte");
                return "byte";
            } else if (value >= -32_768 && value <= 32_767) {
                nodeTypes.put(ctx, "short");
                return "short";
            } else if (value >= -2_147_483_648 && value <= 2_147_483_647) {
                nodeTypes.put(ctx, "int");
                return "int";
            } else {
                nodeTypes.put(ctx, "long");
                return "long";
            }

        } catch (NumberFormatException e) {
            try {
                double doubleValue = Double.parseDouble(numberText);
                
                if (doubleValue >= -Float.MAX_VALUE && doubleValue <= Float.MAX_VALUE) {
                    nodeTypes.put(ctx, "float");
                    return "float";
                } else if (doubleValue >= -Double.MAX_VALUE && doubleValue <= Double.MAX_VALUE) {
                    nodeTypes.put(ctx, "double");
                    return "double";
                }
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Семантическая ошибка: Число '" + numberText + "' слишком велико для всех известных числовых типов!");
            }
        }
        
        throw new RuntimeException("Семантическая ошибка: Не удалось определить тип для числа '" + numberText + "'");
    }


    @Override
    public String visitVariable(CompilerParser.VariableContext ctx) {
        String varName = ctx.ID().getText();

        // если переменная не была объявлена ранее, это ошибка
        if (!symbolTable.containsKey(varName)) {
            throw new RuntimeException("Семантическая ошибка: Переменная '" + varName + "' не объявлена!");
        }

        String type = symbolTable.get(varName);

        // еозвращаем тип переменной из таблицы
        nodeTypes.put(ctx, type);
        return type;
    }

    @Override
    public String visitPrintln(CompilerParser.PrintlnContext ctx) {
        // для println нам просто нужно убедиться, что выражение внутри скобок вообще имеет валидный тип
        visit(ctx.expr());
        return "void";
    }

    // обработка выражений в скобках
    @Override
    public String visitParens(CompilerParser.ParensContext ctx) {
        String type = visit(ctx.expr());
        nodeTypes.put(ctx, type);
        return type;
    }

    // читаем что метод должен вернуть 
    @Override
    public String visitMethodDecl(CompilerParser.MethodDeclContext ctx) {
        currentMethodReturnType = "void";
        if (ctx.typeSpec() != null) currentMethodReturnType = ctx.typeSpec().getText();

        for (var sattCtx : ctx.getRuleContexts(CompilerParser.StatContext.class))
            visit(sattCtx);

        nodeTypes.put(ctx, currentMethodReturnType);
        return currentMethodReturnType;
    }

    @Override
    public String visitMethodCall(CompilerParser.MethodCallContext ctx) {
        String methodName = ctx.ID().getText();

        if (methodName.equals("getCalculatedValue")) {
            nodeTypes.put(ctx, "int");
            return "int";
        }

        throw new RuntimeException("Семантическая ошибка: Метод '" + methodName + "' не найден или его тип не определен!");
    }

    // метод обработки возвращаемого типа
    @Override
    public String visitReturnStat(CompilerParser.ReturnStatContext ctx) {
        String exprType = "void";
        if (ctx.expr() != null) exprType = visit(ctx.expr());
        
        if (!currentMethodReturnType.equals(exprType)) {
            if (currentMethodReturnType.equals("int") && (exprType.equals("byte") || exprType.equals("short"))) {
            exprType = "int"; 
            } else {
                throw new RuntimeException("Ошибка типов: Метод должен возвращать " + currentMethodReturnType + ", но возвращает " + exprType);
            }
        }

        nodeTypes.put(ctx, exprType);
        return exprType;
    }

    // проверка типа что это число
    private boolean isNumber(String type) {
        return "byte".equals(type) ||
            "short".equals(type)   || 
            "int".equals(type)     ||
            "long".equals(type)    ||
            "float".equals(type)   || 
            "double".equals(type);
    }

    // метод для получения ранга для типа
    private int getTypeRank(String type) {
        switch (type) {
            case "byte":   return 1;
            case "short":  return 2;
            case "int":    return 3;
            case "long":   return 4;
            case "float":  return 5;
            case "double": return 6;
            default:       return 0;
        }
    }
}

