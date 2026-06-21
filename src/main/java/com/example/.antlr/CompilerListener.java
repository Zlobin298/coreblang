// Generated from d:/javaProject/return_develop/coreblang/src/main/java/com/example/Compiler.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CompilerParser}.
 */
public interface CompilerListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CompilerParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(CompilerParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(CompilerParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void enterClassDecl(CompilerParser.ClassDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#classDecl}.
	 * @param ctx the parse tree
	 */
	void exitClassDecl(CompilerParser.ClassDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#methodDecl}.
	 * @param ctx the parse tree
	 */
	void enterMethodDecl(CompilerParser.MethodDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#methodDecl}.
	 * @param ctx the parse tree
	 */
	void exitMethodDecl(CompilerParser.MethodDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#formalArgs}.
	 * @param ctx the parse tree
	 */
	void enterFormalArgs(CompilerParser.FormalArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#formalArgs}.
	 * @param ctx the parse tree
	 */
	void exitFormalArgs(CompilerParser.FormalArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#formalArg}.
	 * @param ctx the parse tree
	 */
	void enterFormalArg(CompilerParser.FormalArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#formalArg}.
	 * @param ctx the parse tree
	 */
	void exitFormalArg(CompilerParser.FormalArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#typeSpec}.
	 * @param ctx the parse tree
	 */
	void enterTypeSpec(CompilerParser.TypeSpecContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#typeSpec}.
	 * @param ctx the parse tree
	 */
	void exitTypeSpec(CompilerParser.TypeSpecContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(CompilerParser.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(CompilerParser.StatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void enterVarDecl(CompilerParser.VarDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#varDecl}.
	 * @param ctx the parse tree
	 */
	void exitVarDecl(CompilerParser.VarDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(CompilerParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(CompilerParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#println}.
	 * @param ctx the parse tree
	 */
	void enterPrintln(CompilerParser.PrintlnContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#println}.
	 * @param ctx the parse tree
	 */
	void exitPrintln(CompilerParser.PrintlnContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#returnStat}.
	 * @param ctx the parse tree
	 */
	void enterReturnStat(CompilerParser.ReturnStatContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#returnStat}.
	 * @param ctx the parse tree
	 */
	void exitReturnStat(CompilerParser.ReturnStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#tailExpr}.
	 * @param ctx the parse tree
	 */
	void enterTailExpr(CompilerParser.TailExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#tailExpr}.
	 * @param ctx the parse tree
	 */
	void exitTailExpr(CompilerParser.TailExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVariable(CompilerParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVariable(CompilerParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDiv(CompilerParser.MulDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDiv(CompilerParser.MulDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(CompilerParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(CompilerParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParens(CompilerParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParens(CompilerParser.ParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BooleanLiteral}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(CompilerParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BooleanLiteral}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(CompilerParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRelational(CompilerParser.RelationalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRelational(CompilerParser.RelationalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryMinus}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryMinus(CompilerParser.UnaryMinusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryMinus}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryMinus(CompilerParser.UnaryMinusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMethodCall(CompilerParser.MethodCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMethodCall(CompilerParser.MethodCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalOr}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOr(CompilerParser.LogicalOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalOr}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOr(CompilerParser.LogicalOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalNot}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalNot(CompilerParser.LogicalNotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalNot}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalNot(CompilerParser.LogicalNotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayAccess}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess(CompilerParser.ArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayAccess}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess(CompilerParser.ArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Number}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNumber(CompilerParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Number}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNumber(CompilerParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FloatNumber}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFloatNumber(CompilerParser.FloatNumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FloatNumber}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFloatNumber(CompilerParser.FloatNumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalAnd}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAnd(CompilerParser.LogicalAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalAnd}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAnd(CompilerParser.LogicalAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayCreation}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreation(CompilerParser.ArrayCreationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayCreation}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreation(CompilerParser.ArrayCreationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Equality}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterEquality(CompilerParser.EqualityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Equality}
	 * labeled alternative in {@link CompilerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitEquality(CompilerParser.EqualityContext ctx);
	/**
	 * Enter a parse tree produced by {@link CompilerParser#exprArgs}.
	 * @param ctx the parse tree
	 */
	void enterExprArgs(CompilerParser.ExprArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CompilerParser#exprArgs}.
	 * @param ctx the parse tree
	 */
	void exitExprArgs(CompilerParser.ExprArgsContext ctx);
}