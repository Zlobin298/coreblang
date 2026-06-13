grammar Compiler;

program : stat+ EOF ;

stat: varDecl
    | assignment
    | println
    ;

varDecl : TYPE ID '=' expr ';' ;
assignment : ID '=' expr ';' ;
println  : 'println' '(' expr ')' ';' ;

expr
    : '(' expr ')'             # Parens       // Скобки всегда имеют наивысший приоритет
    | MINUS expr               # unaryMinus   // Унарный минус СТРОГО выше бинарных операций

    | expr op=('*'|'/') expr   # MulDiv       // Умножение/деление ниже унарных, но выше сложения
    | expr op=('+'|'-') expr   # AddSub       // Сложение/вычитание имеют самый низкий приоритет
    | INT_LITERAL              # Number       // Базовые литералы
    | ID                       # Variable
    ;

TYPE : 'byte' 
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

ID    : [a-zA-Z]+ ;
WS    : [ \t\r\n]+ -> skip ;
