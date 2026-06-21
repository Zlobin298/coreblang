// Generated from d:/javaProject/return_develop/coreblang/src/main/java/com/example/Compiler.g4 by ANTLR 4.13.1
package com.example.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CompilerParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CompilerVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CompilerParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(CompilerParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#classDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDecl(CompilerParser.ClassDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#methodDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDecl(CompilerParser.MethodDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#formalArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalArgs(CompilerParser.FormalArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#formalArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalArg(CompilerParser.FormalArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#typeSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeSpec(CompilerParser.TypeSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(CompilerParser.StatContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(CompilerParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(CompilerParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#println}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintln(CompilerParser.PrintlnContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#returnStat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStat(CompilerParser.ReturnStatContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#tailExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTailExpr(CompilerParser.TailExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(CompilerParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDiv(CompilerParser.MulDivContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(CompilerParser.AddSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(CompilerParser.ParensContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BooleanLiteral}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(CompilerParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational(CompilerParser.RelationalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryMinus}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryMinus(CompilerParser.UnaryMinusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(CompilerParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalOr}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOr(CompilerParser.LogicalOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalNot}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalNot(CompilerParser.LogicalNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayAccess}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAccess(CompilerParser.ArrayAccessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Number}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(CompilerParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FloatNumber}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatNumber(CompilerParser.FloatNumberContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalAnd}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalAnd(CompilerParser.LogicalAndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ArrayCreation}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayCreation(CompilerParser.ArrayCreationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Equality}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquality(CompilerParser.EqualityContext ctx);
	/**
	 * Visit a parse tree produced by {@link CompilerParser#exprArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprArgs(CompilerParser.ExprArgsContext ctx);
}