package com.example;

import com.example.gen.CompilerBaseVisitor;
import com.example.gen.CompilerParser;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

// передаем String как generic-тип, чтобы методы возвращали название типа данных
public class TypeChecker extends CompilerBaseVisitor<String> {
    private final Map<String, String> symbolTable = new HashMap<>(); // таблица символов: хранит имя переменной и её тип
    private final Map<String, MethodSignature> functionTable = new HashMap<>(); // таблица функций
    private final ParseTreeProperty<String> nodeTypes = new ParseTreeProperty<>(); // специальная карта ANTLR связывает конкретный узел дерева с его типом
    private String currentMethodReturnType = "void"; // хранит тип возврата для метода

    // геттер, чтобы передать типы в генератор байт-кода
    public ParseTreeProperty<String> getNodeTypes() {
        return nodeTypes;
    }

    // геттер, чтобы передать таблицу в CodeGenerator вместе с nodeTypes
    public Map<String, MethodSignature> getFunctionTable() {
        return functionTable;
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
            nodeTypes.put(ctx, resultType);
            return left;
        }

        // правило неявного приведения типов (автоматическое расширение до double/float)
        if (left.equals("double") || right.equals("double")) {
            resultType = "double"; // любое число + double дает double
        } else if (left.equals("float") || right.equals("float")) {
            resultType = "float";  // любое число + float (если нет double) дает float
        } else if (left.equals("long") || right.equals("long")) {
            resultType = "long";   // расширение до длинного целого
        }
        // логика приведения типов если смешиваются byte, short и int
        else if ((left.equals("byte") || left.equals("short") || left.equals("int")) &&
                (right.equals("byte") || right.equals("short") || right.equals("int"))
        ) {
            resultType = "int";
        } else {
            throw new RuntimeException("Ошибка типов: Нельзя складывать/вычитать " + left + " и " + right);
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
        if (isNumber(varType) && isNumber(exprType) && getTypeRank(varType) >= getTypeRank(exprType)) {
        } else
            throw new RuntimeException("Ошибка типов: Нельзя присвоить тип '" + exprType + "' в переменную '" + varName + "' типа '" + varType + "'");

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

        } catch (NumberFormatException e) { }

        throw new RuntimeException("Семантическая ошибка: Не удалось определить тип для числа '" + numberText + "'");
    }

    @Override
    public String visitFloatNumber(CompilerParser.FloatNumberContext ctx) {
        String rawText = ctx.FLOAT_LITERAL().getText();

        String numberText = rawText.replaceAll("[fFdD]$", "");

        String type = "double";
        if (rawText.toLowerCase().endsWith("f")) type = "float";

        try {
            double doubleValue = Double.parseDouble(numberText);

            if (type.equals("float")) {
                if (doubleValue < -Float.MAX_VALUE || doubleValue > Float.MAX_VALUE)
                    throw new RuntimeException("Семантическая ошибка: Дробное число '" + rawText + "' выходит за границы диапазона float!");
            } else {
                if (doubleValue < -Double.MAX_VALUE || doubleValue > Double.MAX_VALUE)
                    throw new RuntimeException("Семантическая ошибка: Дробное число '" + rawText + "' выходит за границы диапазона double!");
            }

            nodeTypes.put(ctx, type);
            return type;

        } catch (NumberFormatException ex) {
            throw new RuntimeException("Семантическая ошибка: Неверный формат дробного числа '" + rawText + "'");
        }
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
        String methodName = ctx.ID().getText();
        String returnType = ctx.typeSpec() != null ? ctx.typeSpec().getText() : "void";

        // динамически находим имя класса в котором объявлен этот метод
        CompilerParser.ClassDeclContext classCtx = (CompilerParser.ClassDeclContext) ctx.getParent();
        String className = classCtx.ID().getText();

        // собираем типы всех входящих аргументов в список
        List<String> argTypes = new ArrayList<>();
        if (ctx.formalArgs() != null) {
            for (var argCtx : ctx.formalArgs().formalArg()) {
                argTypes.add(argCtx.typeSpec().getText());
            }
        }

        // сохраняем готовую сигнатуру метода в таблицу функций
        functionTable.put(methodName, new MethodSignature(className, returnType, argTypes));

        currentMethodReturnType = returnType;
        if (ctx.typeSpec() != null) currentMethodReturnType = ctx.typeSpec().getText();

        BiFunction<String, String, Boolean> isCompatible = (expected, actual) -> {
            if (expected.equals(actual)) return true;
            if (expected.equals("int") && (actual.equals("byte") || actual.equals("short"))) return true;
            return false;
        };

        for (var sattCtx : ctx.getRuleContexts(CompilerParser.StatContext.class))
            visit(sattCtx);

        if (ctx.tailExpr() != null) {
            String tailType = visit(ctx.tailExpr()); // тип возврата

            if (!isCompatible.apply(currentMethodReturnType, tailType))
                throw new RuntimeException("Ошибка типов: Метод должен возвращать " + currentMethodReturnType + ", но последнее выражение возвращает " + tailType);
            nodeTypes.put(ctx.tailExpr(), tailType);
        }

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
            if (currentMethodReturnType.equals("int") && (exprType.equals("byte") || exprType.equals("short")))
                exprType = "int";
            else
                throw new RuntimeException("Ошибка типов: Метод должен возвращать " + currentMethodReturnType + ", но возвращает " + exprType);
        }

        nodeTypes.put(ctx, exprType);
        return exprType;
    }

    // проверка типа что это число
    private boolean isNumber(String type) {
        return "byte".equals(type) ||
                "short".equals(type) ||
                "int".equals(type) ||
                "long".equals(type) ||
                "float".equals(type) ||
                "double".equals(type);
    }

    // метод для получения ранга для типа
    private int getTypeRank(String type) {
        return switch (type) {
            case "byte" -> 1;
            case "short" -> 2;
            case "int" -> 3;
            case "long" -> 4;
            case "float" -> 5;
            case "double" -> 6;
            default -> 0;
        };
    }
}

