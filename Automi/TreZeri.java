// Implementazione di un DFA che riconosce stringhe con tre zeri consecutivi

public class TreZeri {
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
					if (ch == '0')
						state = 1;
					else if (ch == '1')
						state = 0;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 1: // la stringa contiene 0
					if (ch == '0')
						state = 2;
					else if (ch == '1')
						state = 0;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 2: // la stringa contiene 00
					if (ch == '0')
						state = 3;
					else if (ch == '1')
						state = 0;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 3: // la stringa contiene 000
					if (ch == '0' || ch == '1')
						state = 3;
					else
						state = -1;
					break;
			}
		}

		return state == 3; // infatti case 3 rappresenta lo stato accettante
	}
	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}

}
