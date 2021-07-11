/* Parser a discesa ricorsiva per un linguaggio di programmazione ispirato a Scheme e descritto da una grammatica i cui
 * terminali corrispondono ai token:
 * 			TOKEN			|				PATTERN				 |	NOME
 * ------------------------------------------------------------------------
 * Numeri					| Costante numerica 				 | 256
 * Identificatore			| Lettera seguita da lettere e cifre | 257
 * Relop					| <, >, <=, >=, ==, <>  			 | 258
 * Condizionale				| cond								 | 259
 * When						| when								 | 260
 * Then						| then								 | 261
 * Else						| else								 | 262
 * While					| while								 | 263
 * Do						| do								 | 264
 * Sequence					| sequence							 | 265
 * Print					| print								 | 266
 * Read						| read 								 | 267
 * Disgiunzione				| || 								 | 268
 * Congiunzione				| &&								 | 269
 * Negazione				| !									 | 33
 * Parentesi tonda sx		| (									 | 40
 * Parentesi tonda dx		| )									 | 41
 * Parentesi graffa sx		| {									 | 123
 * Parentesi graffa dx		| }									 | 125
 * Somma					| + 								 | 43
 * Sottrazione				| -									 | 45
 * Moltiplicazione			| *									 | 42
 * Divisione				| /									 | 47
 * Assegnamento				| =									 | 61
 * Punto e virgola			| ;									 | 59
 * EOF						| Fine dell'input					 | -1
 *
 * La grammatica è:
 * <prog> 		::= <stat>EOF
 * <statlist> 	::= <stat><statlistp>
 * <statlistp> 	::= <stat><statlistp> | eps
 * <stat> 		::= (<statp>)
 * <statp> 		::= =ID<expr>
 * 					 | cond<bexpr><stat><elseopt>
 * 					 | while<bexpr><stat>
 * 					 | do<statlist>
 * 					 | print<exprlist>
 * 					 | read ID
 * <elseopt> 	::= (else<stat>) | eps
 * <bexpr> 		::= (<bexprp>)
 * <bexprp> 	::= RELOP<expr><expr>
 * <expr>		::= NUM | ID | (<exprp>)
 * <exprp> 		::= +<exprlist> | -<expr><expr> | * <exprlist> | /<expr><expr>
 * <exprlist> 	::= <expr> | <exprlistp>
 * <exprlistp> 	::= <expr><exprlistp> | eps

 * Le espressioni aritmetiche e booleane in questo linguaggio sono scritte in notazione prefissa o polacca.
 */

import java.io.*;

public class ParserPrefixNotation {
	private LexerWithComments lex;
	private BufferedReader pbr;
	private Token look;

	public ParserPrefixNotation(LexerWithComments l, BufferedReader br) {
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
	public void prog() { // <prog> ::= <stat>EOF
		switch (look.tag) {
			case '(': // GUIDA(PROG -> STAT)) = {(}
				stat();
				match(Tag.EOF); // controllo dell'EOF
				break;

			case Tag.EOF: // Fine del file
				break;

			default:
				error("Error in prog");
		}

	}

	private void statlist() { // <statlist> ::= <stat><statlistp>
		switch (look.tag) {
			case '(': // GUIDA(STATLIST -> STAT STATLISTP) = {(}
				stat();
				statlistp();
				break;

			default:
				error("Error in statlist");
		}
	}

	private void statlistp() { // <statlistp> ::= <stat><statlistp> | eps
		switch (look.tag) {
			case '(': // GUIDA(STATLISTP -> STAT STATLISTP) = {(}
				stat();
				statlistp();
				break;

			case ')': // GUIDA(STATLISTP -> eps) = {)}
			case Tag.EOF:
				break;

			default:
				error("Error in statlistp");
		}
	}

	private void stat() { // <stat> ::= (<statp>)
		switch (look.tag) {
			case '(': // GUIDA(STAT -> ( STATP )) = {(}
				match(Token.lpt.tag);
				statp();
				match(Token.rpt.tag);
				break;

			default:
				error("Error in stat");
		}
	}

	public void statp() { // <statp> ::= =ID<expr> | cond<bexpr><stat><elseopt> | while<bexpr><stat> |
							// do<statlist> |
		switch (look.tag) {
			case '=': // GUIDA(STATP -> =id EXPR) = {=}
				match(Token.assign.tag);
				match(Tag.ID);
				expr();
				break;

			case Tag.COND: // GUIDA(STATP -> cond BEXPR STAT ELSEOPT) = {cond}
				match(Tag.COND);
				bexpr();
				stat();
				elseopt();
				break;

			case Tag.WHILE: // GUIDA(STATP -> while BEXPR STAT) = {while}
				match(Tag.WHILE);
				bexpr();
				stat();
				break;

			case Tag.DO: // GUIDA(STATP -> do STATLIST) = {do}
				match(Tag.DO);
				statlist();
				break;

			case Tag.PRINT: // GUIDA(STATP -> print EXPRLIST) = {print}
				match(Tag.PRINT);
				exprlist();
				break;

			case Tag.READ: // GUIDA(STATP -> read id) = {read}
				match(Tag.READ);
				match(Tag.ID);
				break;

			default:
				error("Error in statp");
		}
	}

	private void elseopt() { // <elseopt> ::= (else<stat>) | eps
		switch (look.tag) {
			case '(': // GUIDA(ELSEOPT -> ( else STAT )) = {(}
				match(Token.lpt.tag);
				match(Tag.ELSE);
				stat();
				match(Token.rpt.tag);
				break;

			case ')': // GUIDA(ELSEOPT -> eps) = {)}
				break;

			default:
				error("Error in elseopt");
		}
	}

	public void bexpr() { // <bexpr> ::= (<bexprp>)
		switch (look.tag) {
			case '(': // GUIDA(BEXPR -> ( BEXPRP )) = {(}
				match(Token.lpt.tag);
				bexprp();
				match(Token.rpt.tag);
				break;

			default:
				error("Error in bexpr");
		}
	}

	public void bexprp() { // <bexprp> ::= RELOP<expr><expr>
		switch (look.tag) {
			case Tag.RELOP: // GUIDA(BEXPRP -> relop EXPR EXPR) = {relop}
				match(Tag.RELOP);
				expr();
				expr();
				break;

			default:
				error("Error in bexprp");
		}
	}

	private void expr() { // <expr> ::= NUM | ID | (<exprp>)
		switch (look.tag) {
			case Tag.NUM: // GUIDA(EXPR -> num) = {num}
				match(Tag.NUM);
				break;

			case Tag.ID: // GUIDA(EXPR -> id) = {id}
				match(Tag.ID);
				break;

			case '(': // GUIDA(EXPR -> ( EXPRP )) = {(}
				match(Token.lpt.tag);
				exprp();
				match(Token.rpt.tag);
				break;

			default:
				error("Error in expr");
		}
	}

	private void exprp() { // <exprp> ::= +<exprlist> | -<expr><expr> | * <exprlist> | /<expr><expr>
		switch (look.tag) {
			case '+': // GUIDA(EXPRP -> + EXPRLIST) = {+}
				match(Token.plus.tag);
				exprlist();
				break;

			case '-': // GUIDA(EXPRP -> - EXPR EXPR) = {-}
				match(Token.minus.tag);
				expr();
				expr();
				break;

			case '*': // GUIDA(EXPRP -> * EXPRLIST) = {*}
				match(Token.mult.tag);
				exprlist();
				break;

			case '/': // GUIDA(EXPRP -> / EXPR EXPR) = {/}
				match(Token.div.tag);
				expr();
				expr();
				break;

			default:
				error("Error in exprp");
		}
	}

	private void exprlist() { // <exprlist> ::= <expr> <exprlistp>
		switch (look.tag) {
			case '(': // GUIDA(EXPRLIST -> EXPR EXPRLISTP) = {num, id, (}
			case Tag.NUM: // GUIDA(EXPRLIST -> EXPR EXPRLISTP) = {num, id, (}
			case Tag.ID: // GUIDA(EXPRLIST -> EXPR EXPRLISTP) = {num, id, (}
				expr();
				exprlistp();
				break;
			default:
				error("Error in exprlist");
		}
	}

	private void exprlistp() { // <exprlistp> ::= <expr><exprlistp> | eps
		switch (look.tag) {
			case Tag.ID: // GUIDA(EXPRLISTP -> EXPR EXPRLISTP) = {num, id, (}
			case '(': // GUIDA(EXPRLISTP -> EXPR EXPRLISTP) = {num, id, (}
			case Tag.NUM:// GUIDA(EXPRLISTP -> EXPR EXPRLISTP) = {num, id, (}
				expr();
				exprlistp();
				break;
			case ')': // GUIDA(EXPRLISTP -> eps) = {)}
				break;
			default:
				error("Error in exprlistp");
		}
	}

	public static void main(String[] args) {
		LexerWithComments lex = new LexerWithComments();
		String path = "Test/ParserPrefixNotation_Test.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			ParserPrefixNotation parser = new ParserPrefixNotation(lex, br);
			parser.prog();
			System.out.println("Input OK");
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
