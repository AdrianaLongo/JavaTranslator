/* Esercizio 1.11
 * Modificare l’automa dell’esercizio precedente in modo che riconosca il linguaggio di stringhe
 * (sull’alfabeto { /, * , a } ) che contengono “commenti” delimitati da / * e * /,
 * ma con la possibilita' di avere stringhe prima e dopo come specificato qui di seguito.
 *
 * L’idea e' che sia possibile avere eventualmente commenti (anche multipli) immersi in una
 * sequenza di simboli dell’alfabeto.
 *
 * Quindi l’unico vincolo e' che l’automa deve accettare le stringhe in cui un’occorrenza della sequenza / *
 * deve essere seguita (anche non immediatamente) da un’occorrenza della sequenza * /.
 *
 * Le stringhe del linguaggio possono non avere nessuna occorrenza della sequenza / *
 * (caso della sequenza di simboli senza commenti).
 *
 * Implementare l’automa seguendo la costruzione vista in Listing 1.
 *
 * Esempi di stringhe accettate:
 * “aaa/ **** /aa”
 * “aa/ * a * a * /”
 * “aaaa”
 * “/ **** /”
 * “/ * aa * /”
 * “ * /a” 				no
 * “a/ ** / *** a” 		no
 * “a/ ** / *** /a” 	no
 * “a/ ** /aa/ *** /a”  no
 *
 * Esempi di stringhe non accettate:
 * “aaa/ * /aa”
 * “a/ ** // *** a”
 * “aa/ * aa”
 * */
public class CommentiInMezzo {
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
					else if (ch == 'a' || ch == '*')
						state = 0;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 1: // e' stato letto /, eventualmente preceduto da altri simboli
					if (ch == '*')
						state = 2;
					else if (ch == 'a' || ch == '/')
						state = 1;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 2: // finora sono stati letti /*, eventualmente preceduto da altri simboli
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
					if (ch == '*')
						state = 0;
					if(ch == '/')
						state = 1;
					else if (ch == 'a')
						state = 4;
					//else
					//	state = -1;
					break;
			}
		}

		return state == 0 || state == 1 || state == 4; // stato accettante
	}
	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}

}
