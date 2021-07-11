/* Esercizio 1.8
 * Progettare e implementare un DFA con alfabeto {a, b} che riconosca il linguaggio
 * delle stringhe tali che a occorre almeno una volta in una delle ultime tre posizioni della stringa.
 *
 * Il DFA deve accettare anche stringhe che contengono meno di tre simboli (ma almeno uno dei simboli deve essere a).
 *
 * Esempi di stringhe accettate:
 * “abb”
 * “bbaba”
 * “baaaaaaa”
 * “aaaaaaa”
 * “a”
 * “ba”
 * “bba”
 * “aa”
 * “bbbababab”
 *
 * Esempi di stringhe non accettate:
 * “abbbbbb”
 * “bbabbbbbbbb”
 * “b”
 */

public class AInUltimeTre {
	// L'automa viene implementato in un metodo scan che prende come parametro una stringa s e restituisce un
	// booleano a seconda che la stringa di input appartenga o meno al linguaggio riconosciuto dal DFA (o -1 se legge
	// un simbolo non contenuto nell'alfabeto del DFA)
	public static boolean scan(String s) {
		int state = 0;
		int i = 0; // indice del prossimo carattere della stringa s da analizzare

		// Il corpo del metodo è un ciclo che, analizzando il contenuto della stringa un carattere alla volta,
		// effettua un cambiamento dello stato dell'automa (tramite un costrutto switch con tanti "case" quati sono
		// gli stati dell'automa) secondo la sua funzione di transizione; lo stato accettante e' rappresentato dal
		// valore che si verifica nel return
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);

			switch (state) {

				case 0: // stato iniziale
					if (ch == 'a')
						state = 1;
					else if (ch == 'b')
						state = 0;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 1: // e' stata letta una a
					if (ch == 'a')
						state = 1;
					else if (ch == 'b')
						state = 2;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 2: // e' stata letta una b ma prima e' stata letta almeno una a
					if (ch == 'a')
						state = 1;
					else if (ch == 'b')
						state = 3;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 3: // gli ultimi due simboli erano bb ma prima e' stata letta almeno una a
					if (ch == 'a')
						state = 1;
					else if (ch == 'b')
						state = 4;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				// case 4 = case 3 ma 4 non e' accettante (la stringa continua) mentre 3 si' (la stringa e' finita)
				case 4: // gli ultimi tre simboli erano tre b ma la stringa potrebbe non essere finita
					if (ch == 'a')
						state = 1;
					else if (ch == 'b')
						state = 4;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;
			}
		}
		return state == 1 || state == 2 || state == 3; // stato accettante
	}
	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}
