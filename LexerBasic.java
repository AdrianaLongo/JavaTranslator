
/**
 * Un lexer legge i caratteri della stringa di ingresso e li raggruppa in
 * oggetti detti token, costituiti da un terminale (usato per le decisioni
 * in fase di parsing) e delle info aggiuntive (sotto forma di attributi).
 *
 * "lessema" = sequenza di caratteri che consiste di un singolo token
 *     QUINDI
 * un lexer disaccoppia un parser dalla rappresentazione dei token in termini di specifici lessemi
 */

import java.io.*;

public class LexerBasic {
	public static int line = 1; // conteggio delle linee d'ingresso
	private char peek = ' '; // sentinella per stabilire la fine dell'identificatore

	/*
	 * La lettura dei caratteri deve prevedere l'eventualità di token composti: ad
	 * esempio, dopo aver letto '>' possiamo o meno trovare un '='; se non dovesse
	 * esserci il '=', l'eventuale spazio viene ignorato e si passa alla lettura del
	 * simbolo successivo
	 */
	private void readch(BufferedReader br) {
		try {
			peek = (char) br.read(); // legge carattere successivo
		} catch (IOException exc) {
			peek = (char) -1; // error
		}
	}

	// Metodo che legge i cartteri di ingresso e restituisce oggetti di tipo token
	public Token lexical_scan(BufferedReader br) {
		/*
		 * Alcuni linguaggi permettono di avere un numero arbitrario di spazi tra i vari
		 * token; per trattare gli spazi si hanno due alternative: 1) farli eliminare al
		 * lexer, così il parser può ignorarli (come avviene per i commenti) 2)
		 * modificare la grammatica in modo da gestirli esplicitamente (più complicato)
		 */
		while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
			if (peek == '\n')
				line++;
			readch(br);
		} // all'uscita, la variabile peek contiene un carattere che non dev'essere
			// ignorato

		switch (peek) {
			// Gestione dei token costituiti da un unico carattere
			case '!':
				peek = ' ';
				return Token.not;

			case '(':
				peek = ' ';
				return Token.lpt;

			case ')':
				peek = ' ';
				return Token.rpt;

			case '{':
				peek = ' ';
				return Token.lpg;

			case '}':
				peek = ' ';
				return Token.rpg;

			case '+':
				peek = ' ';
				return Token.plus;

			case '-':
				peek = ' ';
				return Token.minus;

			case '*':
				peek = ' ';
				return Token.mult;

			case '/':
				peek = ' ';
				return Token.div;

			case ';':
				peek = ' ';
				return Token.semicolon;

			// Gestione dei token costituiti da due caratteri
			case '&':
				readch(br);
				if (peek == '&') {
					peek = ' ';
					return Word.and;
				} else { // & dev'essere seguito da &
					System.err.println("Erroneous character after &: " + peek);
					return null;
				}

			case '|':
				readch(br);
				if (peek == '|') {
					peek = ' ';
					return Word.or;
				} else { // | dev'essere seguito da |
					System.err.println("Erroneous character after |: " + peek);
					return null;
				}

			case '<': // < può essere da solo oppure seguito da > o da =
				readch(br);
				if (peek == '=') {
					peek = ' ';
					return Word.le;
				} else if (peek == '>') {
					peek = ' ';
					return Word.ne;
				} else {
					return Word.lt;
				}

			case '>': // > può essere da solo oppure seguito da =
				readch(br);
				if (peek == '=') {
					peek = ' ';
					return Word.ge;
				} else {
					return Word.gt;
				}

			case '=':
				readch(br);
				if (peek == '=') {
					peek = ' ';
					return Word.eq;
				} else {
					return Token.assign;
				}

			case (char) -1:
				return new Token(Tag.EOF);

			default:
				// Gestione dentificatori e parole chiave (da distinguere!)
				if (Character.isLetter(peek)) {
					/*
					 * Bisogna leggere la parola per intero: ho due opzioni per farlo: 1) carico
					 * tutto man mano su uno StringBuffer 2) append man mano su una stringa
					 * 
					 * Opzione 1) StringBuffer buffer = new StringBuffer(); do {
					 * buffer.append(peek); readch(br); } while (Character.isLetterOrDigit(peek));
					 * 
					 * String s = buffer.toString();
					 */

					// Opzione 1)
					StringBuffer buffer = new StringBuffer();
					do {
						buffer.append(peek);
						readch(br);
					} while (Character.isLetterOrDigit(peek));
					String s = buffer.toString();

					/*
					 * Opzione 2) String s = ""; do { s = s + Character.toString(peek); readch(br);
					 * } while (Character.isLetterOrDigit(peek));
					 */

					// Gestione parole chiave
					switch (s) {
						case "cond":
							peek = ' ';
							return Word.cond;
						case "when":
							peek = ' ';
							return Word.when;
						case "then":
							peek = ' ';
							return Word.then;
						case "else":
							peek = ' ';
							return Word.elsetok;
						case "while":
							peek = ' ';
							return Word.whiletok;
						case "do":
							peek = ' ';
							return Word.dotok;
						case "seq":
							peek = ' ';
							return Word.seq;
						case "print":
							peek = ' ';
							return Word.print;
						case "read":
							peek = ' ';
							return Word.read;
						default:
							return new Word(Tag.ID, s); // raggiunto sse la stringa s non è una parola chiave ma il
						// lessema per un identificatore

					}
				}
				// Gestione numeri
				else if (Character.isDigit(peek)) {
					int num = 0;
					do {
						num = num * 10 + Character.digit(peek, 10); // accumula il valore numerico della sequenza di
																	// cifre
						readch(br);
					} while (Character.isDigit(peek));

					return new NumberTok(Tag.NUM, num);

				} else {
					System.err.println("Erroneous character: " + peek);
					return null;
				}
		}
	}

	public static void main(String[] args) {
		LexerBasic lex = new LexerBasic();
		String path = "Test/LexerBasic_Test.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			Token tok;
			do {
				tok = lex.lexical_scan(br);
				System.out.println("Scan: " + tok);
			} while (tok.tag != Tag.EOF);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
