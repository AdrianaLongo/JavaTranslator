/* Esercizio 1.5.
 * Progettare e implementare un DFA (minimo) che, riconosca stringhe del linguaggio
 * L = {stringhe formate da cognome seguita da un numero di matricola | A-K con matricola pari oppure L-Z con
 * matricola dispari, almeno una cifra e almeno una lettera}
 * Esempio di stringhe accettate:
 * “Bianchi123456”
 * “Rossi654321”
 * “Bianchi2”
 * “B122”
 *
 * Esempio di stringhe non accettate:
 * “654322”
 * “Rossi”
 * “123456Bianchi”
 * “654321Rossi”
 * “2Bianchi”
 * “122B”
 */

public class CognomeMatricola {
	// L'automa viene implementato in un metodo scan che prende come parametro una stringa s e restituisce un
	// booleano a seconda che la stringa di input appartenga o meno al linguaggio riconosciuto dal DFA (o -1 se legge
	// un simbolo non contenuto nell'alfabeto del DFA)
	public static boolean scan(String s){
		int state = 0;
		int i = 0; // indice del prossimo carattere della stringa s da analizzare

		// Il corpo del metodo e' un ciclo che, analizzando il contenuto della stringa un carattere alla volta,
		// effettua un cambiamento dello stato dell'automa (tramite un costrutto switch con tanti "case" quati sono gli stati dell'automa) secondo la sua funzione di transizione; lo stato accettante e' rappresentato dal valore che si verifica nel return
		while (state >= 0 && i < s.length()){
			final char ch = s.charAt(i++);

			switch(state){

				case 0: // stato iniziale
					if (65 <= ch && ch <= 75 || 97 <= ch && ch <= 107) // A-K
						state = 1;
					else if (76 <= ch && ch <= 90 || 108 <= ch && ch <= 122) // L-Z
						state = 2;
					else if (48 <= ch && ch <= 57) // e' un numero
						state = 6;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 1: // e' stata letta A-K
					if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122) // e' una lettera
						state = 1;
					else if (48 <= ch && ch <= 57){ // e' un numero
						if(Character.getNumericValue(ch) % 2 == 0) // pari
							state = 3;
						else if (Character.getNumericValue(ch) % 2 != 0) // dispari
							state = 4;
					}
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 2: // e' stata letta L-Z
					if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122) // e' una lettera
						state = 2;
					else if (48 <= ch && ch <= 57){ // e' un numero
						if (Character.getNumericValue(ch) % 2 != 0) // dispari
							state = 5;
						else if(Character.getNumericValue(ch) % 2 == 0) // pari
							state = 4;
					}
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 3:
					if (48 <= ch && ch <= 57){ // e' un numero
						if (Character.getNumericValue(ch) % 2 != 0) // dispari
							state = 4;
						else if(Character.getNumericValue(ch) % 2 == 0) // pari
							state = 3;
					}
					else if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122) // e' una lettera
						state = 6;
					else
						state = -1;
					break;

				case 4:
					if (48 <= ch && ch <= 57){ // e' un numero
						if (Character.getNumericValue(ch) % 2 != 0) // dispari
							state = 5;
						else if(Character.getNumericValue(ch) % 2 == 0) // pari
							state = 3;
					}
					else if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122) // e' una lettera
						state = 6;
					else
						state = -1;
					break;

				case 5:
					if (48 <= ch && ch <= 57){ // e' un numero
						if (Character.getNumericValue(ch) % 2 != 0) // dispari
							state = 5;
						else if(Character.getNumericValue(ch) % 2 == 0) // pari
							state = 4;
					}
					else if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122) // e' una lettera
						state = 6;
					else
						state = -1;
					break;

				case 6:
					if (48 <= ch && ch <= 57 || 65 <= ch && ch <= 90 || 97 <= ch && ch <= 122) // e' un numero o una lettera
						state = 6;
					else
						state = -1;
					break;
			}
		}

		return state == 3 || state == 5; // stato accettante
	}
	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}

}

