/* Classe di supporto per rappresentare i token = <nome, valore>
		    class Token
			int tag
 		  /        \
  class Num       class Word
  int value       string lexeme
*/

public class Token {
	public final int tag; // una volta assegnato, non può essere più modificato

	public Token(int t) {
		tag = t;
	}

	// Metodo per costruire una rappresentazione del token adatta ad essere stampata
	public String toString() {
		return "<" + tag + ">";
	}

	public static final Token not = new Token('!'), lpt = new Token('('), rpt = new Token(')'), lpg = new Token('{'),
			rpg = new Token('}'), plus = new Token('+'), minus = new Token('-'), mult = new Token('*'),
			div = new Token('/'), assign = new Token('='), semicolon = new Token(';');
}
