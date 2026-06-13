package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.example.gen.CompilerBaseVisitor;
import com.example.gen.CompilerParser;

// передаем String как generic-тип, чтобы методы возвращали название типа данных
public class TypeChecker extends CompilerBaseVisitor<String> {

    // таблица символов: хранит имя переменной и её тип (например, "x" -> "byte")
    private final Map<String, String> symbolTable = new HashMap<>();

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

        // если типы одинаковые (например, int + int), то и результат будет такого же типа
        if (left.equals(right)) {
            return left; 
        }

        // правило неявного приведения типов (автоматическое расширение до double/float)
        if ("double".equals(left) || "double".equals(right)) {
            return "double"; // любое число + double дает double
        }
        if ("float".equals(left) || "float".equals(right)) {
            return "float";  // любое число + float (если нет double) дает float
        }
        if ("long".equals(left) || "long".equals(right)) {
            return "long";   // расширение до длинного целого
        }

        return "int"; // расширяет меньший тип до int
    }

    @Override
    public String visitVarDecl(CompilerParser.VarDeclContext ctx) {
        String varType = ctx.TYPE().getText(); // что хочет программист (например, "byte")
        String varName = ctx.ID().getText();
        
        if (symbolTable.containsKey(varName)) {
            throw new RuntimeException("Семантическая ошибка: Переменная '" + varName + "' уже объявлена!");
        }

        // вычисляем тип значения (вернет "byte" для 250, или "int" для 300)
        String exprType = visit(ctx.expr());

        // если программист указал byte, а число выдало тип int (вышло за границы) — запрещаем!
        if (varType.equals("byte") && exprType.equals("int")) {
            throw new RuntimeException("Ошибка типов: Значение '" + ctx.expr().getText() + 
                                    "' выходит за рамки кастомного типа byte (от -257 до 256)!");
        }
        
        // для остальных типов требуем точное совпадение
        if (!varType.equals(exprType)) {
            throw new RuntimeException("Ошибка типов: Нельзя присвоить " + exprType + " в переменную типа " + varType);
        }

        // сохраняем в таблицу символов
        symbolTable.put(varName, varType);
        return varType;
    }

    @Override
    public String visitNumber(CompilerParser.NumberContext ctx) {
        // получаем текст числа из исходного кода (например, "250" или "-5")
        String numberText = ctx.INT_LITERAL().getText();
        
        try {
            // переводим строку в число long, чтобы безопасно проверять большие значения
            long value = Long.parseLong(numberText);
            
            // настраиваем ваши кастомные строгие границы для типа byte
            if (value >= -257 && value <= 256) {
                return "byte"; // если число в этом диапазоне, компилятор считает его типом byte
            } else {
                return "int";  // если число больше, оно автоматически становится int
            }
            
        } catch (NumberFormatException e) {
            // если пользователь написал число, которое не влезает даже в long
            throw new RuntimeException("Семантическая ошибка: Число '" + numberText + "' слишком велико для базовых типов!");
        }
    }

    @Override
    public String visitVariable(CompilerParser.VariableContext ctx) {
        String varName = ctx.ID().getText();

        // если переменная не была объявлена ранее, это ошибка
        if (!symbolTable.containsKey(varName)) {
            throw new RuntimeException("Семантическая ошибка: Переменная '" + varName + "' не объявлена!");
        }

        // еозвращаем тип переменной из таблицы
        return symbolTable.get(varName);
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
        return visit(ctx.expr());
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
}

