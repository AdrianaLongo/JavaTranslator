/* Parser a discesa ricorsiva che parsifica espressioni aritmetiche in notazione infissa, composte soltanto da numeri
 * non negativi (ovvero sequenze di cifre decimali), operatori di somma e sottrazione + e -, operatori di
 * moltiplicazione e divisione * e /, simboli di parentesi ( e ).
 *
 * La grammatica che riconosce questo tipo di espressioni è:
 * <start> ::= <expr>EOF
 * <expr> ::= <term><exprp>
 * <exprpr> ::= +<term><exprp>
 *            |  -<term><exprp>
 *            | eps
 * <term> ::= <fact><termp>
 * <termp> ::= *<fact><termp>
 * 			  | /<fact><termp>
 *            | eps
 *
 * <fact> ::= (<expr>) | NUM
 *
 * Il parser fa uso del LexerWithComments:
 * - se l'input corrisponde alla grammatica, costruisce un albero di parsificazione/produce una derivazione (quindi
 * l'output deve consistere dell'elenco di token dell'input)
 * - se l'input non corrisponde alla grammatica del linguaggio, l'output del programma
 * deve consistere di un messaggio di errore
* */

import java.io.*;

public class ParserInfixNotation {
	private LexerWithComments lex;
	private BufferedReader pbr;
	private Token look;

	public ParserInfixNotation(LexerWithComments l, BufferedReader br) {
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

	/*
	 * E' un parser a discesa ricorsiva: avrò una procedura assocciata ad ogni
	 * variabile e, dato un input, l'albero di chiamate delle procedure ricorsive
	 * corrisponde all'albero sintattico; in ogni variabile, lo switch sarà guidato
	 * dai simboli dell'insiem guida di quella determinata produzione in analisi.
	 */
	public void start() { // S -> E
		switch (look.tag) {
			case '(': // GUIDA(S -> E) = {(, num}
			case Tag.NUM: // GUIDA(S -> E) = {(, num}
				expr();
				match(Tag.EOF); // controllo dell'EOF
				break;

			default:
				error("Error in start");

		}
	}

	private void expr() { // E -> TE'
		switch (look.tag) {
			case '(': // GUIDA(E -> TE') = {(, num}

			case Tag.NUM: // GUIDA(E -> TE') = {(, num}
				term(); // T -> FT'
				exprp(); // E' -> +TE' | -TE' | eps
				break;

			default:
				error("Error in expr");
		}
	}

	private void exprp() { // E' -> +TE' | -TE' | eps
		switch (look.tag) {
			case '+': // GUIDA(E' -> +TE') = {+}
				match(Token.plus.tag);
				term(); // T -> FT'
				exprp(); // E' -> +TE' | -TE' | eps
				break;

			case '-': // GUIDA(E' -> -TE') = {-}
				match(Token.minus.tag);
				term();
				exprp();
				break;

			case ')': // GUIDA(E' -> eps) = {$, )}
			case Tag.EOF: // GUIDA(E' -> eps) = {$, )}
				break;

			default:
				error("Error in exprp");
		}
	}

	private void term() { // T -> FT'
		switch (look.tag) {
			case '(': // GUIDA(T -> FT') = {(, num)}
			case Tag.NUM: // GUIDA(T -> FT') = {(, num)}
				fact();
				termp();
				break;

			default:
				error("Error in term");

		}
	}

	private void termp() { // T' -> *FT' | /FT' | eps
		switch (look.tag) {
			case '*': // GUIDA(T' -> *FT') = {*}
				match(Token.mult.tag);
				fact();
				termp();
				break;

			case '/': // GUIDA(T' -> /FT') = {/}
				match(Token.div.tag);
				fact();
				termp();
				break;

			case '+': // GUIDA(T' -> eps) ) {$, +, -, )}
			case '-': // GUIDA(T' -> eps) ) {$, +, -, )}
			case ')': // GUIDA(T' -> eps) ) {$, +, -, )}
			case Tag.EOF: // GUIDA(T' -> eps) ) {$, +, -, )}
				break;

			default:
				error("Error in termp");

		}
	}

	private void fact() { // F -> (E) | num
		switch (look.tag) {
			case '(': // GUIDA(F -> (E)) = {(}
				match(Token.lpt.tag);
				expr();
				match(Token.rpt.tag);
				break;

			case Tag.NUM: // GUIDA(F -> num) = {num}
				match(Tag.NUM);
				break;

			default:
				error("Error in fact");
		}
	}

	public static void main(String[] args) {
		LexerWithComments lex = new LexerWithComments();
		String path = "Test/ParserInfixNotation_Test.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			ParserInfixNotation parser = new ParserInfixNotation(lex, br);
			parser.start();
			System.out.println("Input OK");
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
