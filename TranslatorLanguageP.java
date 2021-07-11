
/* Translator per i programmi scritti in P, i cui file hanno estensione .lft: l'output sara' bytecode eseguibile
dalla JVM. In particolare, il bytecode verra' generato usando un linguaggio mnemonico che fa riferimento alle
istruzioni della JVM e che successivamente verra' tradotto in formato .class dall'assembler Jasmin, che fa una
traduzione  1-1 delle istruzioni mnemoniche nel corrispondente opcode della JVM.

1) Il compilatore scritto in questa classe traduce il sorgente in linguaggio assembler per la JVMM
2) L'outuput.j prodotto viene trasformato in .class dall'assembler jasmin
*
* */
import java.io.*;

// La classe deve implementare anche il parsing a discesa ricorsiva della grammatica in questione
public class TranslatorLanguageP {
	private LexerWithComments lex;
	private BufferedReader pbr;
	private Token look;
	SymbolTable st = new SymbolTable();
	CodeGenerator code = new CodeGenerator();
	int count = 0;

	public TranslatorLanguageP(LexerWithComments l, BufferedReader br) {
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

	public void prog() { // <prog> ::= <stat>EOF
		switch (look.tag) {
			case '(': // GUIDA(PROG -> STAT)) = {(}
				int lnext_prog = code.newLabel();
				stat(lnext_prog);
				code.emitLabel(lnext_prog);
				match(Tag.EOF);
				try {
					code.toJasmin();
				} catch (java.io.IOException e) {
					System.out.println("IO error\n");
				}
				break;

			case Tag.EOF:
				break;

			default:
				error("Error in prog");
		}
	}

	private void statlist(int lnext) { // <statlist> ::= <stat><statlistp>
		switch (look.tag) {
			case '(': // GUIDA(STATLIST -> STAT STATLISTP) = {(}
				stat(lnext);
				statlistp(lnext);
				break;

			default:
				error("Error in statlist");

		}
	}

	private void statlistp(int lnext) { // <statlistp> ::= <stat><statlistp> | eps
		switch (look.tag) {
			case '(': // GUIDA(STATLISTP -> STAT STATLISTP) = {(}
				stat(lnext);
				statlistp(lnext);
				break;

			case ')': // GUIDA(STATLISTP -> eps) = {)}
				break;

			default:
				error("Error in statlistp");
		}
	}

	public void stat(int lnext) { // <stat> ::= (<statp>)
		switch (look.tag) {
			case '(': // GUIDA(STAT -> ( STATP )) = {(}
				match(Token.lpt.tag);
				statp(lnext);
				match(Token.rpt.tag);
				break;

			default:
				error("Error in stat");
		}
	}

	private void statp(int lnext) { // <statp> ::= =ID<expr> | cond<bexpr><stat><elseopt> | while<bexpr><stat>
									// |do<statlist> |
		switch (look.tag) {
			case '=': // GUIDA(STATP -> =id EXPR) = {=}
				match(Token.assign.tag);
				int addr = st.lookupAddress(((Word) look).lexeme);
				if (addr == -1) { // il lessema non è già in tabella -> inseriamolo
					addr = count;
					st.insert((((Word) look).lexeme), count++);
				}
				match(Tag.ID);
				expr();
				code.emit(OpCode.istore, addr);
				break;

			case Tag.COND: // GUIDA(STATP -> cond BEXPR STAT ELSEOPT) = {cond}
				match(Tag.COND);
				int lend_statp_cond = code.newLabel();
				int ltrue_statp_cond = code.newLabel();
				int lfalse_statp_cond = code.newLabel();

				bexpr(ltrue_statp_cond, lfalse_statp_cond);

				code.emitLabel(ltrue_statp_cond);
				stat(ltrue_statp_cond);

				code.emit(OpCode.GOto, lend_statp_cond);

				code.emitLabel(lfalse_statp_cond);
				elseopt(lfalse_statp_cond);

				code.emitLabel(lend_statp_cond);

				break;

			case Tag.WHILE: // GUIDA(STATP -> while BEXPR STAT) = {while}
				match(Tag.WHILE);

				int lbegin_statp_while = code.newLabel();
				int ltrue_statp_while = code.newLabel();
				int lfalse_statp_while = code.newLabel();

				code.emitLabel(lbegin_statp_while);

				bexpr(ltrue_statp_while, lfalse_statp_while);

				code.emitLabel(ltrue_statp_while);

				stat(lfalse_statp_while);

				code.emit(OpCode.GOto, lbegin_statp_while);

				code.emitLabel(lfalse_statp_while);
				break;

			case Tag.DO: // GUIDA(STATP -> do STATLIST) = {do}
				match(Tag.DO);
				statlist(lnext);
				break;

			case Tag.PRINT: // GUIDA(STATP -> print EXPRLIST) = {print}
				match(Tag.PRINT);
				exprlist(OpCode.invokestatic);
				break;

			case Tag.READ: // GUIDA(STATP -> read id) = {read}
				match(Tag.READ);
				if (look.tag == Tag.ID) {
					int read_id_addr = st.lookupAddress(((Word) look).lexeme);
					if (read_id_addr == -1) {
						read_id_addr = count;
						st.insert(((Word) look).lexeme, count++);
					}

					match(Tag.ID);
					code.emit(OpCode.invokestatic, 0);
					code.emit(OpCode.istore, read_id_addr);
				}

				else
					error("Error in grammar (stat) after read with " + look);

				break;

			default:
				error("Error in statp");
		}

	}

	private void elseopt(int lnext) { // <elseopt> ::= (else<stat>) | eps
		switch (look.tag) {
			case '(': // GUIDA(ELSEOPT -> ( else STAT )) = {(}
				match(Token.lpt.tag);
				match(Tag.ELSE);
				stat(lnext);
				match(Token.rpt.tag);
				break;

			case ')': // GUIDA(ELSEOPT -> eps) = {)}
				break;

			default:
				error("Error in elseopt");
		}

	}

	private void bexpr(int lnextTrue, int lnextFalse) { // <bexpr> ::= (<bexprp>)
		switch (look.tag) {
			case '(': // GUIDA(BEXPR -> ( BEXPRP )) = {(}
				match(Token.lpt.tag);
				bexprp(lnextTrue, lnextFalse);
				match(Token.rpt.tag);
				break;

			default:
				error("Error in bexpr");
		}

	}

	private void bexprp(int lnextTrue, int lnextFalse) { // <bexprp> ::= RELOP<expr><expr>
		switch (look.tag) {
			case Tag.RELOP: // GUIDA(BEXPRP -> relop EXPR EXPR) = {relop}
				switch (((Word) look).lexeme) {
					case "=":
						match(Tag.RELOP);
						expr();
						expr();
						code.emit(OpCode.if_icmpeq, lnextTrue);
						code.emit(OpCode.GOto, lnextFalse);
						break;
					case ">=":
						match(Tag.RELOP);
						expr();
						expr();
						code.emit(OpCode.if_icmpge, lnextTrue);
						code.emit(OpCode.GOto, lnextFalse);
						break;

					case ">":
						match(Tag.RELOP);
						expr();
						expr();
						code.emit(OpCode.if_icmpgt, lnextTrue);
						code.emit(OpCode.GOto, lnextFalse);
						break;

					case "<=":
						match(Tag.RELOP);
						expr();
						expr();
						code.emit(OpCode.if_icmple, lnextTrue);
						code.emit(OpCode.GOto, lnextFalse);
						break;

					case "<":
						match(Tag.RELOP);
						expr();
						expr();
						code.emit(OpCode.if_icmplt, lnextTrue);
						code.emit(OpCode.GOto, lnextFalse);
						break;

					case "<>":
						match(Tag.RELOP);
						expr();
						expr();
						code.emit(OpCode.if_icmpne, lnextTrue);
						code.emit(OpCode.GOto, lnextFalse);
						break;

					default:
						error("defualt");
				}
				break;

			default:
				error("Error in bexprp");
		}

	}

	private void expr() { // <expr> ::= NUM | ID | (<exprp>)
		switch (look.tag) {
			case Tag.NUM: // GUIDA(EXPR -> num) = {num}
				int value = (((NumberTok) look).value);
				match(Tag.NUM);
				code.emit(OpCode.ldc, value);
				break;

			case Tag.ID: // GUIDA(EXPR -> id) = {id}
				int addr = (st.lookupAddress(((Word) look).lexeme));
				if (addr == -1)// bisogna controllare che esista davvero in
					// tabella
					error("Var not found");
				match(Tag.ID);
				code.emit(OpCode.iload, addr);
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
			case '+': // GUIDA(EXPRP -> - EXPR EXPR) = {-}
				match(Token.plus.tag);
				exprlist(OpCode.iadd);
				break;

			case '-': // GUIDA(EXPRP -> - EXPR EXPR) = {-}
				match(Token.minus.tag);
				expr();
				expr();
				code.emit(OpCode.isub);
				break;

			case '*': // GUIDA(EXPRP -> * EXPRLIST) = {*}
				match(Token.mult.tag);
				exprlist(OpCode.imul);
				break;

			case '/': // GUIDA(EXPRP -> / EXPR EXPR) = {/}
				match(Token.div.tag);
				expr();
				expr();
				code.emit(OpCode.idiv);
				break;

			default:
				error("Error in exprp");
		}

	}

	private void exprlist(OpCode opcode) { // <exprlist> ::= <expr> <exprlistp>
		switch (look.tag) {
			case '(': // GUIDA(EXPRLIST -> EXPR EXPRLISTP) = {num, id, (}
			case Tag.NUM: // GUIDA(EXPRLIST -> EXPR EXPRLISTP) = {num, id, (}
			case Tag.ID: // GUIDA(EXPRLIST -> EXPR EXPRLISTP) = {num, id, (}
				expr();

				if (opcode == OpCode.invokestatic)
					code.emit(opcode, 1);

				exprlistp(opcode);
				break;

			default:
				error("Error in exprlist");
		}

	}

	private void exprlistp(OpCode opcode) { // <exprlistp> ::= <expr><exprlistp> | eps
		switch (look.tag) {
			case Tag.ID: // GUIDA(EXPRLISTP -> EXPR EXPRLISTP) = {num, id, (}
			case '(': // GUIDA(EXPRLISTP -> EXPR EXPRLISTP) = {num, id, (}
			case Tag.NUM:// GUIDA(EXPRLISTP -> EXPR EXPRLISTP) = {num, id, (}
				expr();
				if (opcode == OpCode.invokestatic)
					code.emit(opcode, 1);
				else
					code.emit(opcode);
				exprlistp(opcode);
				break;
			case ')': // GUIDA(EXPRLISTP -> eps) = {)}
				break;
			default:
				error("Error in exprlistp");

		}
	}

	public static void main(String[] args) {
		LexerWithComments lex = new LexerWithComments();
		String path = "Test/TranslatorLanguageP_Test.lft";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			TranslatorLanguageP translator = new TranslatorLanguageP(lex, br);
			translator.prog();
			System.out.println("Input OK");
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}