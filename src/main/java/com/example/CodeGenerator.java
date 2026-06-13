package com.example;

import com.example.gen.CompilerBaseVisitor;
import com.example.gen.CompilerParser;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

// наследуемся от Visitor возвращаем Void так как мы пишем напрямую в бинарный поток ASM
public class CodeGenerator extends CompilerBaseVisitor<Void> implements Opcodes {

    private final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    private MethodVisitor mv;
    
    // локальные переменные в байт коде JVM адресуются по числовым индексам (0, 1, 2...)
    // индекс 0 в static методе это первый аргумент поэтому наши переменные начнем с 1
    private final Map<String, Integer> variableIndexes = new HashMap<>();
    private int nextVariableIndex = 1;

    private final ParseTreeProperty<String> nodeTypes;

    // Конструктор принимает готовые типы из TypeChecker
    public CodeGenerator(ParseTreeProperty<String> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    // метод возвращает готовый бинарный массив байт класса
    public byte[] getBytecode() {
        return cw.toByteArray();
    }

    @Override
    public Void visitProgram(CompilerParser.ProgramContext ctx) {
        // 1. создаем структуру класса (версия JDK11, публичный, имя: GeneratedProgram)
        cw.visit(V11, ACC_PUBLIC, "GeneratedProgram", null, "java/lang/Object", null);

        // 2. создаем стандартный конструктор по умолчанию
        MethodVisitor init = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        init.visitCode();
        init.visitVarInsn(ALOAD, 0);
        init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        init.visitInsn(RETURN);
        init.visitMaxs(1, 1);
        init.visitEnd();

        // 3. создаем точку входа: public static void main(String[] args)
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        // обходим все команды нашей программы
        for (CompilerParser.StatContext statCtx : ctx.stat()) {
            visit(statCtx);
        }

        // завершаем метод main
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // завершаем генерацию класса
        cw.visitEnd();
        return null;
    }

    @Override
    public Void visitVarDecl(CompilerParser.VarDeclContext ctx) {
        String varName = ctx.ID().getText();

        // вычисляем выражение (оно положит результат на вершину стека JVM)
        visit(ctx.expr());

        // выделяем индекс для новой переменной
        int index = nextVariableIndex++;
        variableIndexes.put(varName, index);

        // ISTORE сохраняет int/byte из вершины стека в локальную переменную под указанным индексом
        mv.visitVarInsn(ISTORE, index);
        return null;
    }

    @Override
    public Void visitPrintln(CompilerParser.PrintlnContext ctx) {
        // загружаем System.out
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // генерируем байт-код для вычисления выражения (оно ложится на стек JVM)
        visit(ctx.expr());

        String exprType = nodeTypes.get(ctx.expr()); 
        
        // переводим его в дескриптор JVM ("I", "J", "Z" и т.д.)
        String jvmType = getJvmDescriptor(exprType);

        // Вызываем правильный метод println
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(" + jvmType + ")V", false);
        return null;
    }

    @Override
    public Void visitNumber(CompilerParser.NumberContext ctx) {
        String numberText = ctx.INT_LITERAL().getText();
        String type = nodeTypes.get(ctx);

        if (type.equals("byte") || type.equals("short") || type.equals("int")) {
            int value = Integer.parseInt(numberText);
            
            // быстрые низкоуровневые оптимизации JVM для экономии байт-кода
            if (value >= -1 && value <= 5) {
                mv.visitInsn(ICONST_0 + value); // инструкции ICONST_M1, ICONST_0 ... ICONST_5 (1 байт)
            } else if (value >= -128 && value <= 127) {
                mv.visitIntInsn(BIPUSH, value); // положить однобайтовое число (2 байта из-за инструкции .class)
            } else if (value >= -32768 && value <= 32767) {
                mv.visitIntInsn(SIPUSH, value); // положить двухбайтовое число (3 байта)
            } else {
                mv.visitLdcInsn(value);         // Загрузка 4-байтового int константы из Constant Pool
            }
        } else if (type.equals("long")) {
            long value = Long.parseLong(numberText);
            
            if (value == 0) {
                mv.visitInsn(LCONST_0); // быстрая загрузка нуля для long
            } else if (value == 1) {
                mv.visitInsn(LCONST_1); // быстрая загрузка единицы для long
            } else {
                mv.visitLdcInsn(value); // загрузка 8-байтового long константы из Constant Pool
            }
        } else if (type.equals("float")) {
            float value = Float.parseFloat(numberText);
            if (value == 0.0f) mv.visitInsn(FCONST_0);
            else if (value == 1.0f) mv.visitInsn(FCONST_1);
            else if (value == 2.0f) mv.visitInsn(FCONST_2);
            else mv.visitLdcInsn(value);
        } else if (type.equals("double")) {
            double value = Double.parseDouble(numberText);
            if (value == 0.0) mv.visitInsn(DCONST_0);
            else if (value == 1.0) mv.visitInsn(DCONST_1);
            else mv.visitLdcInsn(value);
        }

        return null;
    }

    @Override
    public Void visitAddSub(CompilerParser.AddSubContext ctx) {
        // генерируем код для левой части
        visit(ctx.expr(0));
        // генерируем код для правой части
        visit(ctx.expr(1));

        // узнаем точный тип этого математического узла из разметки TypeChecker
        String type = nodeTypes.get(ctx);
        // узнаем, какая именно операция происходит: "+" или "-"
        String op = ctx.op.getText();

        // выбираем правильную команду процессора JVM в зависимости от типа данных
        if (op.equals("+")) {
            switch (type) {
                case "long":   mv.visitInsn(LADD); break; // сложение 64-битных long
                case "float":  mv.visitInsn(FADD); break; // сложение float
                case "double": mv.visitInsn(DADD); break; // сложение double
                default:       mv.visitInsn(IADD); break; // byte, short, int складываются через IADD
            }
        } else if (op.equals("-")) {
            switch (type) {
                case "long":   mv.visitInsn(LSUB); break;
                case "float":  mv.visitInsn(FSUB); break;
                case "double": mv.visitInsn(DSUB); break;
                default:       mv.visitInsn(ISUB); break;
            }
        }

        return null;
    }

    @Override
    public Void visitVariable(CompilerParser.VariableContext ctx) {
        String varName = ctx.ID().getText();
        
        // получаем индекс локальной переменной в JVM
        int index = variableIndexes.get(varName);

        // мгновенно узнаем точный тип переменной который определил TypeChecker
        String type = nodeTypes.get(ctx);

        // выбираем правильную JVM-инструкцию загрузки (LOAD) в зависимости от типа данных
        switch (type) {
            case "long":   mv.visitVarInsn(LLOAD, index); break;
            case "float":  mv.visitVarInsn(FLOAD, index); break;
            case "double": mv.visitVarInsn(DLOAD, index); break;
            case "byte":
            case "short":
            case "int":
            case "char":
            case "boolean":
            default:       mv.visitVarInsn(ILOAD, index); break;
        }

        return null;
    }

    @Override
    public Void visitParens(CompilerParser.ParensContext ctx) {
        return visit(ctx.expr());
    }

    private String getJvmDescriptor(String myType) {
        switch (myType) {
            case "long":    return "J";
            case "float":   return "F";
            case "double":  return "D";
            case "boolean": return "Z";
            case "char":    return "C";
            default:        return "I"; // byte и short обрабатываются как int
        }
    }
}
