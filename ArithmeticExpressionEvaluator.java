/* Valutatore di espressioni semplici con SDT:
 * <stat>  ::= <expr> EOF { print(expr.val) }
 * <expr>  ::= <term> { exprp.i = term.val } <exprp> {expr.val = exprp.val}
 * <exprp> ::= + <term> { exprp1.i = exprp.i + term.val } <exprp1> {exprp.val = exprp1.val}
 * 			|  - <term> { exprp1.i = exprp.i - term.val } <exprp1> {exprp.val = exprp1.val}
 * 			| eps
 * <term>  ::= <fact> { temrmp.i = fact.val } <termp> { term.val = termp.val }
 * <termp> ::= * <fact> { termp1.i = termp.i * fact.val } <termp1> { termp.val = termp1.val}
 *			|  / <fact> { termp1.i = termp.i / fact.val } <termp1> { termp.val = termp1.val}
 * 			| eps
 * <fact> ::= (<expr>) { fact.val = expr.val}
 * 			| NUM { fact.val = NUM.value}
 * */

import java.io.*;

public class ArithmeticExpressionEvaluator {
	private LexerWithComments lex;
	private BufferedReader pbr;
	private Token look;

	public ArithmeticExpressionEvaluator(LexerWithComments l, BufferedReader br) {
		lex = l;
		pbr = br;
		move();
	}

	void move() {
		look = lex.lexical_scan(pbr);
		System.out.println("token = " + look);
	}

	void error(String s) {
		throw new Error("near line " + lex.line + ": " + s);
	}

	// Prende come parametri un token/simbolo, lo confronta con quello in lettura
	// attualmente...
	void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF)
				move(); // ... se e' valido va avanti
		} else
			error("syntax error"); // ... se non e' valido da' errore
	}

	public void start() { // <stat> ::= <expr> EOF { print(expr.val) }
		int expr_val;

		switch (look.tag) {
			case '(': // GUIDA(S -> E) = {(, num}
			case Tag.NUM: // GUIDA(S -> E) = {(, num}
				expr_val = expr();
				match(Tag.EOF);
				System.out.println(expr_val);
				break;

			default:
				error("Error in start");
		}
	}

	private int expr() { // <expr> ::= <term> { exprp.i = term.val } <exprp> {expr.val = exprp.val}
		int term_val = 0, exprp_val = 0;

		switch (look.tag) {
			case '(': // GUIDA(E -> TE') = {(, num}
			case Tag.NUM: // GUIDA(E -> TE') = {(, num}
				term_val = term();
				exprp_val = exprp(term_val);
				break;

			default:
				error("Error in expr");
		}
		return exprp_val;
	}

	private int exprp(int exprp_i) { // <exprp> ::= + <term> { exprp1.i = exprp.i + term.val } <exprp1> {exprp.val =
		// exprp1.val} | - <term> { exprp1.i = exprp.i - term.val } <exprp1> {exprp.val
		// = exprp1.val} | eps
		int term_val = 0, exprp_val = 0;

		switch (look.tag) {
			case '+': // GUIDA(E' -> +TE') = {+}
				match('+');
				term_val = term();
				exprp_val = exprp(exprp_i + term_val);
				break;

			case '-': // GUIDA(E' -> -TE') = {-}
				match('-');
				term_val = term();
				exprp_val = exprp(exprp_i - term_val);
				break;

			case ')': // GUIDA(E' -> eps) = {$, )}

			case Tag.EOF: // GUIDA(E' -> eps) = {$, )}
				exprp_val = exprp_i;
				break;

			default:
				error("Error in exprp");
		}

		return exprp_val;
	}

	private int term() { // <term> ::= <fact> { temrmp.i = fact.val } <termp> { term.val = termp.val }
		int fact_val = 0, termp_val = 0;

		switch (look.tag) {
			case '(': // GUIDA(T -> FT') = {(, num)}

			case Tag.NUM: // GUIDA(T -> FT') = {(, num)}
				fact_val = fact();
				termp_val = termp(fact_val);
				break;

			default:
				error("Error in term");
		}
		return termp_val;
	}

	private int termp(int termp_i) { // <termp> ::= * <fact> { termp1.i = termp.i * fact.val } <termp1> { termp.val =
		// termp1.val}| / <fact> { termp1.i = termp.i / fact.val } <termp1> { termp.val
		// = termp1.val} | eps
		int fact_val = 0, termp_val = 0;

		switch (look.tag) {
			case '*': // GUIDA(T' -> *FT') = {*}
				match('*');
				fact_val = fact();
				termp_val = termp(termp_i * fact_val);
				break;

			case '/': // GUIDA(T' -> /FT') = {/}
				match('/');
				fact_val = fact();
				termp_val = termp(termp_i / fact_val);
				break;

			case '+': // GUIDA(T' -> eps) ) {$, +, -, )}
			case '-': // GUIDA(T' -> eps) ) {$, +, -, )}
			case ')': // GUIDA(T' -> eps) ) {$, +, -, )}
			case Tag.EOF: // GUIDA(T' -> eps) ) {$, +, -, )}
				termp_val = termp_i;
				break;

			default:
				error("Error in exprp");
		}

		return termp_val;
	}

	private int fact() { // <fact> ::= (<expr>) { fact.val = expr.val} | NUM { fact.val = NUM.value}
		int fact_val = 0;

		switch (look.tag) {
			case '(': // GUIDA(F -> (E)) = {(}
				match(Token.lpt.tag);
				fact_val = expr();
				match(Token.rpt.tag);
				break;

			case Tag.NUM: // GUIDA(F -> num) = {num}
				fact_val = (((NumberTok) look).value);
				match(Tag.NUM);
				break;

			default:
				error("Error in fact");
		}

		return fact_val;

	}

	public static void main(String[] args) {
		LexerWithComments lex = new LexerWithComments();
		String path = "Test/ArithmeticExpressionEvaluator_Test.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			ArithmeticExpressionEvaluator valutatore = new ArithmeticExpressionEvaluator(lex, br);
			valutatore.start();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
