/* Esercizio 1.10
 * Progettare e implementare un DFA con alfabeto { /, * , a } che riconosca il linguaggio di
 * “commenti” delimitati da / * (all’inizio) e * / (alla fine):
 * l’automa deve accettare le stringhe che contengono almeno 4 caratteri
 * 		che iniziano con / * ,
 * 		che finiscono con * /,
 * 		che contengono una sola occorrenza della sequenza * /, quella finale
 * 			(dove l’asterisco della sequenza * / non deve essere in comune con quello della sequenza / * all’inizio).
 *
 * Esempi di stringhe accettate:
 * “/ **** /”
 * “/ * a * a * /”
 * “/ * a/ ** /”
 * “/ ** a///a/a ** /” -no
 * “/ ** /”
 * “/ * / * /”
 *
 * Esempi di stringhe non accettate:
 * “/ * /”
 * “/ ** / *** /” -- ok
 * */

public class Commenti {
	// L'automa viene implementato in un metodo scan che prende come parametro una stringa s e restituisce un
	// booleano a seconda che la stringa di input appartenga o meno al linguaggio riconosciuto dal DFA (o -1 se legge
	// un simbolo non contenuto nell'alfabeto del DFA)
	public static boolean scan(String s){
		int state = 0;
		int i = 0; // indice del prossimo carattere della stringa s da analizzare

		// Il corpo del metodo è un ciclo che, analizzando il contenuto della stringa un carattere alla volta,
		// effettua un cambiamento dello stato dell'automa (tramite un costrutto switch con tanti "case" quati sono
		// gli stati dell'automa) secondo la sua funzione di transizione; lo stato accettante e' rappresentato dal
		// valore che si verifica nel return
		while (state >= 0 && i < s.length()){
			final char ch = s.charAt(i++);

			switch(state){

				case 0: // stato iniziale
					if (ch == '/')
						state = 1;
					else if (ch == '*' || ch == 'a')
						state = 5;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 1: // finora e' stato letto /
					if (ch == '*')
						state = 2;
					else if (ch == 'a' || ch == '/')
						state = 5;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 2: // finora sono stati letti /*
					if (ch == 'a' || ch == '/')
						state = 2;
					else if (ch == '*')
						state = 3;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 3: // e' stato letto un altro * oltre a quello iniziale
					if (ch == '/')
						state = 4;
					else if (ch == 'a')
						state = 2;
					else if (ch == '*')
						state = 3;
					else
						state = -1;
					break;

				case 4: // gli ultimi due simboli letti sono stati */
					if (ch == '*' || ch == '/' || ch == 'a')
						state = 5;
					else
						state = -1;
					break;

				case 5:
					if (ch == '*' || ch == '/' || ch == 'a')
						state = 5;
					else
						state = -1;
					break;
			}
		}

		return state == 4; // stato accettante
	}
	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}

}
