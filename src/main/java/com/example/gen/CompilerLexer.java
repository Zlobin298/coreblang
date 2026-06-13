// Generated from d:/javaProject/return_develop/coreblang/src/main/java/com/example/Compiler.g4 by ANTLR 4.13.1
package com.example.gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CompilerLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, TYPE=6, BOOL=7, CHAR=8, INT_LITERAL=9, 
		FLOAT_LITERAL=10, PLUS=11, MINUS=12, MUL=13, DIV=14, ID=15, WS=16;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "TYPE", "BOOL", "CHAR", "INT_LITERAL", 
			"FLOAT_LITERAL", "PLUS", "MINUS", "MUL", "DIV", "ID", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "';'", "'println'", "'('", "')'", null, null, null, null, 
			null, "'+'", "'-'", "'*'", "'/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, "TYPE", "BOOL", "CHAR", "INT_LITERAL", 
			"FLOAT_LITERAL", "PLUS", "MINUS", "MUL", "DIV", "ID", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CompilerLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Compiler.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0010\u0092\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0003\u0005X\b\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0003\u0006c\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0004\bj\b\b\u000b\b\f\bk\u0001\b\u0003\bo\b\b\u0001"+
		"\t\u0004\tr\b\t\u000b\t\f\ts\u0001\t\u0001\t\u0004\tx\b\t\u000b\t\f\t"+
		"y\u0001\t\u0003\t}\b\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f"+
		"\u0001\f\u0001\r\u0001\r\u0001\u000e\u0004\u000e\u0088\b\u000e\u000b\u000e"+
		"\f\u000e\u0089\u0001\u000f\u0004\u000f\u008d\b\u000f\u000b\u000f\f\u000f"+
		"\u008e\u0001\u000f\u0001\u000f\u0000\u0000\u0010\u0001\u0001\u0003\u0002"+
		"\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013"+
		"\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010\u0001"+
		"\u0000\u0005\u0001\u000009\u0002\u0000LLll\u0004\u0000DDFFddff\u0002\u0000"+
		"AZaz\u0003\u0000\t\n\r\r  \u00a0\u0000\u0001\u0001\u0000\u0000\u0000\u0000"+
		"\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000"+
		"\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b"+
		"\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001"+
		"\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001"+
		"\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001"+
		"\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001"+
		"\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001"+
		"\u0000\u0000\u0000\u0001!\u0001\u0000\u0000\u0000\u0003#\u0001\u0000\u0000"+
		"\u0000\u0005%\u0001\u0000\u0000\u0000\u0007-\u0001\u0000\u0000\u0000\t"+
		"/\u0001\u0000\u0000\u0000\u000bW\u0001\u0000\u0000\u0000\rb\u0001\u0000"+
		"\u0000\u0000\u000fd\u0001\u0000\u0000\u0000\u0011i\u0001\u0000\u0000\u0000"+
		"\u0013q\u0001\u0000\u0000\u0000\u0015~\u0001\u0000\u0000\u0000\u0017\u0080"+
		"\u0001\u0000\u0000\u0000\u0019\u0082\u0001\u0000\u0000\u0000\u001b\u0084"+
		"\u0001\u0000\u0000\u0000\u001d\u0087\u0001\u0000\u0000\u0000\u001f\u008c"+
		"\u0001\u0000\u0000\u0000!\"\u0005=\u0000\u0000\"\u0002\u0001\u0000\u0000"+
		"\u0000#$\u0005;\u0000\u0000$\u0004\u0001\u0000\u0000\u0000%&\u0005p\u0000"+
		"\u0000&\'\u0005r\u0000\u0000\'(\u0005i\u0000\u0000()\u0005n\u0000\u0000"+
		")*\u0005t\u0000\u0000*+\u0005l\u0000\u0000+,\u0005n\u0000\u0000,\u0006"+
		"\u0001\u0000\u0000\u0000-.\u0005(\u0000\u0000.\b\u0001\u0000\u0000\u0000"+
		"/0\u0005)\u0000\u00000\n\u0001\u0000\u0000\u000012\u0005b\u0000\u0000"+
		"23\u0005y\u0000\u000034\u0005t\u0000\u00004X\u0005e\u0000\u000056\u0005"+
		"s\u0000\u000067\u0005h\u0000\u000078\u0005o\u0000\u000089\u0005r\u0000"+
		"\u00009X\u0005t\u0000\u0000:;\u0005i\u0000\u0000;<\u0005n\u0000\u0000"+
		"<X\u0005t\u0000\u0000=>\u0005l\u0000\u0000>?\u0005o\u0000\u0000?@\u0005"+
		"n\u0000\u0000@X\u0005g\u0000\u0000AB\u0005f\u0000\u0000BC\u0005l\u0000"+
		"\u0000CD\u0005o\u0000\u0000DE\u0005a\u0000\u0000EX\u0005t\u0000\u0000"+
		"FG\u0005d\u0000\u0000GH\u0005o\u0000\u0000HI\u0005u\u0000\u0000IJ\u0005"+
		"b\u0000\u0000JK\u0005l\u0000\u0000KX\u0005e\u0000\u0000LM\u0005c\u0000"+
		"\u0000MN\u0005h\u0000\u0000NO\u0005a\u0000\u0000OX\u0005r\u0000\u0000"+
		"PQ\u0005b\u0000\u0000QR\u0005o\u0000\u0000RS\u0005o\u0000\u0000ST\u0005"+
		"l\u0000\u0000TU\u0005e\u0000\u0000UV\u0005a\u0000\u0000VX\u0005n\u0000"+
		"\u0000W1\u0001\u0000\u0000\u0000W5\u0001\u0000\u0000\u0000W:\u0001\u0000"+
		"\u0000\u0000W=\u0001\u0000\u0000\u0000WA\u0001\u0000\u0000\u0000WF\u0001"+
		"\u0000\u0000\u0000WL\u0001\u0000\u0000\u0000WP\u0001\u0000\u0000\u0000"+
		"X\f\u0001\u0000\u0000\u0000YZ\u0005t\u0000\u0000Z[\u0005r\u0000\u0000"+
		"[\\\u0005u\u0000\u0000\\c\u0005e\u0000\u0000]^\u0005f\u0000\u0000^_\u0005"+
		"a\u0000\u0000_`\u0005l\u0000\u0000`a\u0005s\u0000\u0000ac\u0005e\u0000"+
		"\u0000bY\u0001\u0000\u0000\u0000b]\u0001\u0000\u0000\u0000c\u000e\u0001"+
		"\u0000\u0000\u0000de\u0005\'\u0000\u0000ef\t\u0000\u0000\u0000fg\u0005"+
		"\'\u0000\u0000g\u0010\u0001\u0000\u0000\u0000hj\u0007\u0000\u0000\u0000"+
		"ih\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000"+
		"\u0000kl\u0001\u0000\u0000\u0000ln\u0001\u0000\u0000\u0000mo\u0007\u0001"+
		"\u0000\u0000nm\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000o\u0012"+
		"\u0001\u0000\u0000\u0000pr\u0007\u0000\u0000\u0000qp\u0001\u0000\u0000"+
		"\u0000rs\u0001\u0000\u0000\u0000sq\u0001\u0000\u0000\u0000st\u0001\u0000"+
		"\u0000\u0000tu\u0001\u0000\u0000\u0000uw\u0005.\u0000\u0000vx\u0007\u0000"+
		"\u0000\u0000wv\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000yw\u0001"+
		"\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z|\u0001\u0000\u0000\u0000"+
		"{}\u0007\u0002\u0000\u0000|{\u0001\u0000\u0000\u0000|}\u0001\u0000\u0000"+
		"\u0000}\u0014\u0001\u0000\u0000\u0000~\u007f\u0005+\u0000\u0000\u007f"+
		"\u0016\u0001\u0000\u0000\u0000\u0080\u0081\u0005-\u0000\u0000\u0081\u0018"+
		"\u0001\u0000\u0000\u0000\u0082\u0083\u0005*\u0000\u0000\u0083\u001a\u0001"+
		"\u0000\u0000\u0000\u0084\u0085\u0005/\u0000\u0000\u0085\u001c\u0001\u0000"+
		"\u0000\u0000\u0086\u0088\u0007\u0003\u0000\u0000\u0087\u0086\u0001\u0000"+
		"\u0000\u0000\u0088\u0089\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000"+
		"\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u001e\u0001\u0000"+
		"\u0000\u0000\u008b\u008d\u0007\u0004\u0000\u0000\u008c\u008b\u0001\u0000"+
		"\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000"+
		"\u0000\u0000\u008e\u008f\u0001\u0000\u0000\u0000\u008f\u0090\u0001\u0000"+
		"\u0000\u0000\u0090\u0091\u0006\u000f\u0000\u0000\u0091 \u0001\u0000\u0000"+
		"\u0000\n\u0000Wbknsy|\u0089\u008e\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}