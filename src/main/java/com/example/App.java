package com.example;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import com.example.gen.CompilerLexer;
import com.example.gen.CompilerParser;

import java.io.File;
import java.io.FileOutputStream;

public class App {
    public static void main(String[] args) {
        String sourceCode = "class GeneratedProgram {\n" +
                "    static int getCalculatedValue() {\n" +
                "        int a = 10;\n" +
                "        a + 5\n" +
                "    }\n" +
                "    \n" +
                "    static void main(String[] args) {\n" +
                "        int result = getCalculatedValue();\n" +
                "        println(result);\n" +
                "    }\n" +
                "}\n";

//        sourceCode = "class Main  {\n" +
//        "static void main(String[] args) {\n"      +
//        "        int result = 1 + 1;\n" +
//        "        println(result);\n"                   +
//        "    }\n"            +
//        "}";

        System.out.println("=== 1. Исходный код ===");
        System.out.println(sourceCode);

        // запуск Лексера и Парсера ANTLR
        CharStream input = CharStreams.fromString(sourceCode);
        CompilerLexer lexer = new CompilerLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CompilerParser parser = new CompilerParser(tokens);
        ParseTree tree = parser.program();

        // если ошибок синтаксиса нет, переходим к компиляции
        if (parser.getNumberOfSyntaxErrors() == 0) {
            System.out.println("=== 2. Синтаксический анализ успешен! ===");

            try {
                // проверка и разметка типов данных
                TypeChecker typeChecker = new TypeChecker();
                typeChecker.visit(tree);
                System.out.println("=== 3. Проверка типов (Семантика) успешна! ===");

                // генерация бинарного байт-кода JVM
                // передаем карту nodeTypes, которую заполнил TypeChecker
                CodeGenerator bytecodeGen = new CodeGenerator(typeChecker.getNodeTypes());
                bytecodeGen.visit(tree);
                byte[] binaryClassBytes = bytecodeGen.getBytecode();

                // запись готового .class файла на жесткий диск
                File outputFile = new File("Main.class");
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(binaryClassBytes);
                    System.out.println("=== 4. Компиляция завершена! Создан файл: " + outputFile.getAbsolutePath() + " ===");
                }

            } catch (RuntimeException e) {
                System.err.println("\n[Ошибка компиляции]: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("\n[Системная ошибка]: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("=== Обнаружены синтаксические ошибки! ===");
        }
    }
}
