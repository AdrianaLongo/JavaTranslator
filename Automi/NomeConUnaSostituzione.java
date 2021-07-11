/*Esercizio 1.9
 * Progettare e implementare un DFA che riconosca il linguaggio di stringhe che contengono
 * il tuo nome e tutte le stringhe ottenute dopo la sostituzione di un carattere del nome con un altro qualsiasi.
 *
 * Se nome = "Paolo":
 * esempi di stringhe accettate:
 * “Paolo”
 * “Pjolo”
 * “caolo”
 * “Pa%lo”
 * “Paola”
 * “Parlo”
 *
 * esempi di stringhe non accettate:
 * “Eva”
 * “Perro”
 * “Pietro”
 * “P * o * o”
 * */

// 	COME IMPLEMENTO IL -1???????

public class NomeConUnaSostituzione {
	// L'automa viene implementato in un metodo scan che prende come parametro una stringa s e restituisce un booleano a seconda che la stringa di input appartenga o meno al linguaggio riconosciuto dal DFA (o -1 se legge un simbolo non contenuto nell'alfabeto del DFA)
	public static boolean scan(String s){
		int state = 0;
		int i = 0; // indice del prossimo carattere della stringa s da analizzare

		// Il corpo del metodo è un ciclo che, analizzando il contenuto della stringa un carattere alla volta, effettua un cambiamento dello stato dell'automa (tramite un costrutto switch con tanti "case" quati sono gli stati dell'automa) secondo la sua funzione di transizione; lo stato accettante e' rappresentato dal valore che si verifica nel return
		while (state >= 0 && i < s.length()){
			final char ch = s.charAt(i++);

			switch(state){

				case 0: // stato iniziale
					if (ch == 'A' || ch == 'a')
						state = 1;
					else
						state = 7;
					break;

				case 1: // e' stata letta A
					if (ch == 'D' || ch == 'd')
						state = 2;
					else
						state = 8;
					break;

				case 2: // finora sono stati letti i caratteri AD
					if (ch == 'R' || ch == 'r')
						state = 3;
					else
						state = 9;
					break;

				case 3: // finora sono stati letti i caratteri ADR
					if (ch == 'I' || ch == 'i')
						state = 4;
					else
						state = 10;
					break;

				case 4: // finora sono stati letti i caratteri ADRI
					if (ch == 'A' || ch == 'a')
						state = 5;
					else
						state = 11;
					break;

				case 5: // finora sono stati letti i caratteri ADRIA
					if (ch == 'N' || ch == 'n')
						state = 6;
					else
						state = 12;
					break;

				case 6: // finora sono stati letti i caratteri ADRIAN
					/* La lunghezza della stringa che abbiamo in input e' nota, quindi non dobbiamo ipotizzare
					 * che ci siano ancora altri simboli dopo l'ultimo letto che ha portato in questo stato: qualsiasi
					 * sia l'ultimo simbolo che verra' letto, la stringa a questo punto dev'essere accettata
					 * */
					state = 6;
					break;

				case 7: // finora e' stato letto un simbolo
					if (ch == 'D' || ch == 'd')
						state = 8;
					else // se viene letto un altro simbolo, la stringa non e' riconosciuta
						state = 13;
					break;

				case 8: // finora e' stato un letto un simbolo e D
					if (ch == 'R' || ch == 'r')
						state = 9;
					else
						state = 13;
					break;

				case 9: // finora e' stato letto un simbolo e DR
					if (ch == 'I' || ch == 'i')
						state = 10;
					else
						state = 13;
					break;

				case 10: // finora e' stato letto un simbolo e DRI
					if (ch == 'A' || ch == 'a')
						state = 11;
					else
						state = 13;
					break;

				case 11: // finora e' stato letto un simbolo e DRIA
					if (ch == 'N' || ch == 'n')
						state = 12;
					else
						state = 13;
					break;

				case 12: // finora e' stato letto un simbolo e DRIAN
					if (ch == 'A' || ch == 'a')
						state = 12;
					else
						state = 13;
					break;

				case 13: // la stringa non e' accettabile (nonostante sia formata da simboli dell'alfabeto)
					state = 13;
					break;
			}
		}

		return state == 6 || state == 12; // stati accettanti
	}
	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}

}


