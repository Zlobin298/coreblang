package com.example;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.example.gen.CompilerLexer;
import com.example.gen.CompilerParser;

public class App {

    public static void main(String[] args) {
        // 1. входной тестовый код вашего языка в виде строки
        String sourceCode = "byte x = 5;\n" +
                            "println(x);\n";

        System.out.println("=== Исходный код ===");
        System.out.println(sourceCode);

        // 2. передаем код в Лексер (он разбивает текст на токены)
        CharStream input = CharStreams.fromString(sourceCode);
        CompilerLexer lexer = new CompilerLexer(input);

        // 3. передаем токены в Парсер (он строит дерево)
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CompilerParser parser = new CompilerParser(tokens);

        // 4. вызываем стартовое правило грамматики (в .g4 файле это program)
        ParseTree tree = parser.program();

        if (parser.getNumberOfSyntaxErrors() == 0) {
            System.out.println("=== Синтаксический анализ успешен! ===");
            
            try {
                // запускаем определение и проверку типов
                TypeChecker typeChecker = new TypeChecker();
                typeChecker.visit(tree);
                
                System.out.println("=== Семантический анализ (проверка типов) успешен! ===");
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
