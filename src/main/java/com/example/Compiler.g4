grammar Compiler;

program : classDecl+ EOF ;

classDecl : 'class' ID '{' methodDecl* '}' ;
methodDecl : 'static'? typeSpec ID '(' formalArgs? ')' '{' stat* '}' ;

formalArgs : formalArg (',' formalArg)* ;
formalArg : typeSpec ID ;
typeSpec : (TYPE | 'void' | ID) ('[' ']')* ;

stat: varDecl
    | assignment
    | println
    | returnStat 
    ;

varDecl : typeSpec ID '=' expr ';' ;
assignment : (ID | expr ('[' expr ']')+) '=' expr ';' ;
println  : 'println' '(' expr ')' ';' ;
returnStat : 'return' expr? ';' ;

expr
    : '(' expr ')'                                          # Parens       
    | MINUS expr                                            # unaryMinus   
    
    | ID '(' exprArgs? ')'                                  # MethodCall   

    | 'new' (TYPE | ID) ('[' expr ']')+ ('[' ']')*          # ArrayCreation
    | expr ('[' expr ']')+                                  # ArrayAccess

    | expr op=(MUL|DIV) expr                                # MulDiv       
    | expr op=(PLUS|MINUS) expr                             # AddSub       
    | INT_LITERAL                                           # Number       
    | ID                                                    # Variable
    ;

exprArgs : expr (',' expr)* ;

TYPE: 'byte' 
     | 'short' 
     | 'int' 
     | 'long' 
     | 'float' 
     | 'double' 
     | 'char' 
     | 'boolean' 
     ;

BOOL  : 'true' | 'false' ;
CHAR  : '\'' . '\'' ;
INT_LITERAL : [0-9]+ [lL]? ;
FLOAT_LITERAL : [0-9]+ '.' [0-9]+ [fFdD]? ;

PLUS  : '+' ;
MINUS : '-' ;
MUL   : '*' ;
DIV   : '/' ;

ID    : [a-zA-Z_][a-zA-Z0-9_]* ;
WS    : [ \t\r\n]+ -> skip ;
