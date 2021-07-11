/*Esercizio 1.6.
 * Progettare e implementare un DFA che riconosca il linguaggio dei numeri binari il cui valore e` multiplo di 3.
 *
 * NB: trattare anche eventuali sequenze di zeri iniziali
 *
 * Esempi di stringhe valide:
 * “110” = “0110” = 6
 * “1001” = 9
 * “0” = 0
 * “0001111”
 *
 * Esempi di stringhe non valide:
 * “10” = 2
 * “111” = 7

 * Suggerimento: usare tre stati per rappresentare il resto della divisione per 3 del numero.

 */
public class MultipliDiTre {
	// L'automa viene implementato in un metodo scan che prende come parametro una stringa s e restituisce un
	// booleano a seconda che la stringa di input appartenga o meno al linguaggio riconosciuto dal DFA (o -1 se legge
	// un simbolo non contenuto nell'alfabeto del DFA)
	public static boolean scan(String s){
		int state = 0;
		int i = 0; // indice del prossimo carattere della stringa s da analizzare

		// Il corpo del metodo è un ciclo che, analizzando il contenuto della stringa un carattere alla volta,
		// effettua un cambiamento dello stato dell'automa (tramite un costrutto switch con tanti "case" quati sono gli stati dell'automa) secondo la sua funzione di transizione; lo stato accettante e' rappresentato dal valore che si verifica nel return
		while (state >= 0 && i < s.length()){
			final char ch = s.charAt(i++);

			/* Se un numero e' divisibile per 3, il suo resto puo' essere 0, 1 o 2:
			 * gli stati dell'automa rappresentano proprio i tre resti
			 */
			switch(state){

				case 0: // resto 0
					if (ch == '0')
						state = 0;
					else if (ch == '1')
						state = 1;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 1: // resto 1
					if (ch == '0')
						state = 2;
					else if (ch == '1')
						state = 0;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 2: // resto 2
					if (ch == '0')
						state = 1;
					else if (ch == '1')
						state = 2;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;
			}
		}

		return state == 0; // infatti case 0, 1 e 2 rappresentano gli stati accettanti
	}
	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}
