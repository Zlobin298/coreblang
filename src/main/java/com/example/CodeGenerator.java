package com.example;

import com.example.gen.CompilerBaseVisitor;
import com.example.gen.CompilerParser;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// TODO настроить метод visitAssignment для инициализации переменных
// наследуемся от Visitor возвращаем Void так как мы пишем напрямую в бинарный поток ASM
public class CodeGenerator extends CompilerBaseVisitor<Void> implements Opcodes {
    private final ParseTreeProperty<String> nodeTypes;
    private final Map<String, MethodSignature> functionTable;

    private ClassWriter cw;
    private MethodVisitor mv;
    private Map<String, Integer> variableIndexes;

    private int nextVariableIndex;

    // Конструктор принимает готовые типы из TypeChecker
    public CodeGenerator(ParseTreeProperty<String> nodeTypes, Map<String, MethodSignature> functionTable) {
        this.nodeTypes = nodeTypes;
        this.functionTable = functionTable;
    }

    // метод возвращает готовый бинарный массив байт класса
    public byte[] getBytecode() {
        return cw.toByteArray();
    }

    @Override
    public Void visitProgram(CompilerParser.ProgramContext ctx) {
        for (var classCtx : ctx.classDecl())
            visit(classCtx);

        return null;
    }

    @Override
    public Void visitVarDecl(CompilerParser.VarDeclContext ctx) {
        String varName = ctx.ID().getText();
        String varType = ctx.typeSpec().getText();

        visit(ctx.expr());

        if (!variableIndexes.containsKey(varName)) {
            variableIndexes.put(varName, nextVariableIndex);
            nextVariableIndex += getAsmTypeSize(varType);
        }

        int index = variableIndexes.get(varName);

        mv.visitVarInsn(getStoreOpcode(varType), index);
        return null;
    }

    @Override
    public Void visitMethodCall(CompilerParser.MethodCallContext ctx) {
        String methodName = ctx.ID().getText();

        // достаем готовую сигнатуру метода из таблицы функций компилятора
        MethodSignature sig = functionTable.get(methodName);
        if (sig == null) throw new RuntimeException("Ошибка генерации байт-кода: Неизвестный метод '" + methodName + "'");

        // генерируем код для вычисления аргументов
        if (ctx.exprArgs() != null) {
            for (var exprCtx : ctx.exprArgs().expr())
                visit(exprCtx);
        }

        // поднимаемся по дереву вверх до объявления класса, чтобы узнать имя текущего класса
        org.antlr.v4.runtime.RuleContext parent = ctx.getParent();
        while (parent != null && !(parent instanceof CompilerParser.ClassDeclContext))
            parent = parent.getParent();
        if (parent == null) throw new RuntimeException("Критическая ошибка: Вызов метода '" + methodName + "' находится вне класса!");
        String currentClassName = ((CompilerParser.ClassDeclContext) parent).ID().getText();

        // строим jvm-дескриптор метода динамически на основе данных из MethodSignature
        StringBuilder descriptorBuilder = new StringBuilder("()");

        // добавляем букву возвращаемого типа метода
        String returnType = sig.returnType();
        descriptorBuilder.append(returnType.equals("void") ? "V" : getJvmDescriptor(returnType));

        // генерируем инструкцию вызова с динамическим классом, методом и дескриптором
        mv.visitMethodInsn(INVOKESTATIC, currentClassName, methodName, descriptorBuilder.toString(), false);

        return null;
    }

    @Override
    public Void visitReturnStat(CompilerParser.ReturnStatContext ctx) {
        if (ctx.expr() != null) visit(ctx.expr());

        String type = nodeTypes.get(ctx);
        if (type == null) type = "void";

        switch (type) {
            case "long":
                mv.visitInsn(LRETURN);
                break;
            case "float":
                mv.visitInsn(FRETURN);
                break;
            case "double":
                mv.visitInsn(DRETURN);
                break;
            case "byte":
            case "short":
            case "int":
            case "char":
            case "boolean":
                mv.visitInsn(IRETURN);
                break;
            case "void":
            default:
                mv.visitInsn(RETURN);
                break;
        }

        return null;
    }

    @Override
    public Void visitMethodDecl(CompilerParser.MethodDeclContext ctx) {
        String methodName = ctx.ID().getText();
        String returnType = ctx.typeSpec() != null ? ctx.typeSpec().getText() : "void";

        boolean isStatic = ctx.getChild(0).getText().equals("static");
        int accessFlags = ACC_PUBLIC + (isStatic ? ACC_STATIC : 0);

        this.variableIndexes = new HashMap<>();
        this.nextVariableIndex = isStatic ? 0 : 1;

        Function<String, Integer> getReturnOpcode = type -> switch (type) {
            case "long" -> LRETURN;
            case "float" -> FRETURN;
            case "double" -> DRETURN;
            case "byte", "short", "int", "char", "boolean" -> IRETURN;
            default -> RETURN;
        };

        // реализовываем сдвиг аргументов
        if (ctx.formalArgs() != null) {
            for (var argsCtx : ctx.formalArgs().formalArg()) {
                String argName = argsCtx.ID().getText();
                // регистрация аргумента (например, args получит индекс 0)
                variableIndexes.put(argName, nextVariableIndex);

                String argType = argsCtx.typeSpec().getText();
                // если аргумент - массив (String[]) или объект, он занимает 1 слот JVM
                if (argType.contains("[")) nextVariableIndex += 1;
                else if (argType.equals("long") || argType.equals("double")) nextVariableIndex += 2;
                else nextVariableIndex += 1;
            }
        }

        String descriptor = buildMethodDescriptor(ctx, returnType);
        mv = cw.visitMethod(accessFlags, methodName, descriptor, null, null);
        mv.visitCode();

        for (var statCtx : ctx.stat())
            visit(statCtx);

        if (ctx.tailExpr() != null) visit(ctx.tailExpr());
        if (returnType.equals("void")) mv.visitInsn(RETURN);
        else mv.visitInsn(getReturnOpcode.apply(returnType));

        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return null;
    }

    @Override
    public Void visitClassDecl(CompilerParser.ClassDeclContext ctx) {
        String className = ctx.ID().getText();

        this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        this.cw.visit(V21, ACC_PUBLIC, className, null, "java/lang/Object", null);

        MethodVisitor init = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        init.visitCode();
        init.visitVarInsn(ALOAD, 0);
        init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        init.visitInsn(RETURN);
        init.visitMaxs(1, 1);
        init.visitEnd();

        for (var methodCtx : ctx.methodDecl()) {
            visit(methodCtx);
        }

        this.cw.visitEnd();
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
            if (value >= -1 && value <= 5)
                mv.visitInsn(ICONST_0 + value); // инструкции ICONST_M1, ICONST_0 ... ICONST_5 (1 байт)
            else if (value >= -128 && value <= 127)
                mv.visitIntInsn(BIPUSH, value); // положить однобайтовое число (2 байта из-за инструкции .class)
            else if (value >= -32768 && value <= 32767)
                mv.visitIntInsn(SIPUSH, value); // положить двухбайтовое число (3 байта)
            else mv.visitLdcInsn(value);         // Загрузка 4-байтового int константы из Constant Pool
        } else if (type.equals("long")) {
            long value = Long.parseLong(numberText);

            if (value == 0) mv.visitInsn(LCONST_0); // быстрая загрузка нуля для long
            else if (value == 1) mv.visitInsn(LCONST_1); // быстрая загрузка единицы для long
            else mv.visitLdcInsn(value); // загрузка 8-байтового long константы из Constant Pool
        }

        return null;
    }

    @Override
    public Void visitFloatNumber(CompilerParser.FloatNumberContext ctx) {
        String numberText = ctx.FLOAT_LITERAL().getText();
        String cleanNumber = numberText.replaceAll("[fFdD]$", "");
        String type = nodeTypes.get(ctx);
        if (type == null) type = "float";

        if (type.equals("float")) {
            float value = Float.parseFloat(cleanNumber);
            if (value == 0.0f) mv.visitInsn(FCONST_0);
            else if (value == 1.0f) mv.visitInsn(FCONST_1);
            else if (value == 2.0f) mv.visitInsn(FCONST_2);
            else mv.visitLdcInsn(value);
        } else if (type.equals("double")) {
            double value = Double.parseDouble(cleanNumber);
            if (value == 0.0) mv.visitInsn(DCONST_0);
            else if (value == 1.0) mv.visitInsn(DCONST_1);
            else mv.visitLdcInsn(value);
        }

        return null;
    }

    @Override
    public Void visitAddSub(CompilerParser.AddSubContext ctx) {
        // узнаем точный тип этого математического узла из разметки TypeChecker
        String type = nodeTypes.get(ctx);
        if (type == null) type = "int";

        // генерируем код для левой части
        visit(ctx.expr(0));
        String leftType = nodeTypes.get(ctx.expr(0));

        if (leftType.equals("int") || leftType.equals("byte") || leftType.equals("short")) {
            if (type.equals("float")) mv.visitInsn(I2F);
            else if (type.equals("double")) mv.visitInsn(I2D);
        }

        // генерируем код для правой части
        visit(ctx.expr(1));
        String rightType = nodeTypes.get(ctx.expr(1));

        if (rightType.equals("int") || rightType.equals("byte") || rightType.equals("short")) {
            if (type.equals("float")) mv.visitInsn(I2F);
            else if (type.equals("double")) mv.visitInsn(I2D);
        }

        // узнаем, какая именно операция происходит: "+" или "-"
        String op = ctx.op.getText();

        // выбираем правильную команду процессора JVM в зависимости от типа данных
        if (op.equals("+")) {
            switch (type) {
                case "long":
                    mv.visitInsn(LADD);
                    break; // сложение 64-битных long
                case "float":
                    mv.visitInsn(FADD);
                    break; // сложение float
                case "double":
                    mv.visitInsn(DADD);
                    break; // сложение double
                default:
                    mv.visitInsn(IADD);
                    break; // byte, short, int складываются через IADD
            }
        } else if (op.equals("-")) {
            switch (type) {
                case "long":
                    mv.visitInsn(LSUB);
                    break;
                case "float":
                    mv.visitInsn(FSUB);
                    break;
                case "double":
                    mv.visitInsn(DSUB);
                    break;
                default:
                    mv.visitInsn(ISUB);
                    break;
            }
        }

        return null;
    }

    @Override
    public Void visitMulDiv(CompilerParser.MulDivContext ctx) {
        String type = nodeTypes.get(ctx);
        if (type == null) type = "int";

        visit(ctx.expr(0));
        String leftType = nodeTypes.get(ctx.expr(0));

        if (leftType.equals("int") || leftType.equals("byte") || leftType.equals("short")) {
            if (type.equals("float")) mv.visitInsn(I2F);
            else if (type.equals("double")) mv.visitInsn(I2D);
        }

        visit(ctx.expr(1));
        String rightType = nodeTypes.get(ctx.expr(1));

        if (rightType.equals("int") || rightType.equals("byte") || rightType.equals("short")) {
            if (type.equals("float")) mv.visitInsn(I2F);
            else if (type.equals("double")) mv.visitInsn(I2D);
        }

        String op = ctx.op.getText();

        if (op.equals("*")) {
            switch (type) {
                case "long":
                    mv.visitInsn(LMUL);
                    break;
                case "float":
                    mv.visitInsn(FMUL);
                    break;
                case "double":
                    mv.visitInsn(DMUL);
                    break;
                case "byte":
                case "short":
                case "int":
                default:
                    mv.visitInsn(IMUL);
                    break;
            }
        } else if (op.equals("/")) {
            switch (type) {
                case "long":
                    mv.visitInsn(LDIV);
                    break;
                case "float":
                    mv.visitInsn(FDIV);
                    break;
                case "double":
                    mv.visitInsn(DDIV);
                    break;
                case "byte":
                case "short":
                case "int":
                default:
                    mv.visitInsn(IDIV);
                    break;
            }
        }

        return null;
    }

    @Override
    public Void visitVariable(CompilerParser.VariableContext ctx) {
        String varName = ctx.ID().getText();

        if (!variableIndexes.containsKey(varName)) {
            throw new RuntimeException("Ошибка генерации кода: Попытка прочитать неинициализированную переменную '" + varName + "'");
        }

        int index = variableIndexes.get(varName);
        String type = nodeTypes.get(ctx);
        if (type == null) type = "int";

        mv.visitVarInsn(getLoadOpcode(type), index);
        return null;
    }

    @Override
    public Void visitParens(CompilerParser.ParensContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Void visitArrayCreation(CompilerParser.ArrayCreationContext ctx) {
        for (var exprCtx : ctx.expr()) visit(exprCtx);
        String baseType = ctx.getChild(1).getText();

        int dimensions = 0;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("[")) dimensions++;
        }

        if (dimensions > 1) {
            String arrayDescriptor = "[".repeat(dimensions) + getJvmTypeLetter(baseType);
            mv.visitMultiANewArrayInsn(arrayDescriptor, ctx.expr().size());
        } else {
            if (isPrimitive(baseType)) {
                mv.visitIntInsn(NEWARRAY, getAsmPrimitiveTypeFlag(baseType));
            } else {
                String internalName = baseType.equals("String") ? "java/lang/String" : baseType;
                mv.visitTypeInsn(ANEWARRAY, internalName);
            }
        }

        return null;
    }

    private String buildMethodDescriptor(CompilerParser.MethodDeclContext ctx, String returnType) {
        if (ctx.ID().getText().equals("main") && ctx.getChild(0).getText().equals("static")) {
            return "([Ljava/lang/String;)V";
        }

        var descriptor = new StringBuilder("(");
        if (ctx.formalArgs() != null) {
            for (var argsCtx : ctx.formalArgs().formalArg())
                descriptor.append(getJvmMethodDescriptor(argsCtx.typeSpec().getText()));
        }

        descriptor.append(")");
        descriptor.append(getJvmMethodDescriptor(returnType));

        return descriptor.toString();
    }

    private String getJvmDescriptor(String myType) {
        return switch (myType) {
            case "long" -> "J";
            case "float" -> "F";
            case "double" -> "D";
            case "boolean" -> "Z";
            case "char" -> "C";
            default -> "I"; // byte и short обрабатываются как int
        };
    }

    private String getJvmMethodDescriptor(String myType) {
        return switch (myType) {
            case "void" -> "V";
            case "boolean" -> "Z";
            case "char" -> "C";
            case "byte" -> "B";
            case "short" -> "S";
            case "int" -> "I";
            case "long" -> "J";
            case "float" -> "F";
            case "double" -> "D";
            default -> "Ljava/lang/Object;";
        };
    }

    // вспомогательный метод для получения буквы типа в дескриптор многомерного массива
    private String getJvmTypeLetter(String type) {
        return switch (type) {
            case "boolean" -> "Z";
            case "char" -> "C";
            case "byte" -> "B";
            case "short" -> "S";
            case "int" -> "I";
            case "long" -> "J";
            case "float" -> "F";
            case "double" -> "D";
            case "String" -> "Ljava/lang/String;";
            default -> "L" + type + ";";
        };
    }

    // вспомогательный метод для определения флага примитивного типа для команды NEWARRAY
    private int getAsmPrimitiveTypeFlag(String type) {
        return switch (type) {
            case "boolean" -> T_BOOLEAN;
            case "char" -> T_CHAR;
            case "float" -> T_FLOAT;
            case "double" -> T_DOUBLE;
            case "byte" -> T_BYTE;
            case "short" -> T_SHORT;
            case "long" -> T_LONG;
            default -> T_INT;
        };
    }

    private int getStoreOpcode(String type) {
        return switch (type) {
            case "long" -> LSTORE;
            case "float" -> FSTORE;
            case "double" -> DSTORE;
            case "byte", "short", "int", "char", "boolean" -> ISTORE;
            default -> ASTORE;
        };
    }

    private int getLoadOpcode(String type) {
        return switch (type) {
            case "long" -> LLOAD;
            case "float" -> FLOAD;
            case "double" -> DLOAD;
            case "byte", "short", "int", "char", "boolean" -> ILOAD;
            default -> ALOAD;
        };
    }

    private int getAsmTypeSize(String type) {
        if ("long".equals(type) || "double".equals(type)) return 2;
        return 1;
    }

    private boolean isPrimitive(String type) {
        return !type.equals("String") && getAsmPrimitiveTypeFlag(type) != T_INT || type.equals("int");
    }
}
