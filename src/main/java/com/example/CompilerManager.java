package com.example;

import com.example.gen.CompilerLexer;
import com.example.gen.CompilerParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileOutputStream;

public class CompilerManager {
    public static String start(String sourceCode) {
        // запуск Лексера и Парсера ANTLR
        CharStream input = CharStreams.fromString(sourceCode);
        CompilerLexer lexer = new CompilerLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CompilerParser parser = new CompilerParser(tokens);
        ParseTree tree = parser.program();

        // если ошибок синтаксиса нет, переходим к компиляции
        if (parser.getNumberOfSyntaxErrors() == 0) {
            try {
                // проверка и разметка типов данных
                TypeChecker typeChecker = new TypeChecker();
                typeChecker.visit(tree);

                // генерация бинарного байт-кода JVM
                // передаем карту nodeTypes, которую заполнил TypeChecker
                CodeGenerator bytecodeGen = new CodeGenerator(typeChecker.getNodeTypes(), typeChecker.getFunctionTable());
                bytecodeGen.visit(tree);
                byte[] binaryClassBytes = bytecodeGen.getBytecode();

                // запись готового .class файла на жесткий диск
                File outputFile = new File("Main.class");
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(binaryClassBytes);
                }

            } catch (RuntimeException e) {
                return "\n[Ошибка компиляции]: " + e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return "\n[Системная ошибка]: " + e.getMessage();
            }
        } else {
            return "\n=== Обнаружены синтаксические ошибки! ===";
        }

        return "0";
    }
}
