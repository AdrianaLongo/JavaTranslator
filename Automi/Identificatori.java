/* Esercizio 1.2
	Progettare e implementare un DFA che riconosca il linguaggio degli identificatori in stile Java, le cui
	caratteristiche sono:
	- sono sequenze non vuote di lettere, numeri e _
	- non possono cominciare con un numero
	- non possono essere composti solo da _

	Esempi di stringhe accettate: 
		“x”
		“flag1”
		“x2y2”
		“x 1”
		“lft lab”
		“ temp”
		“x 1 y 2”
		“x ”
		“ 5” 
	Esempi di stringhe non accettate: 
		“5”
		“221B”
		“123”
		“9 to 5”
		“ ”
*/

public class Identificatori{
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
					if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122) // e' una lettera
						state = 1;
					else if (ch == ' ' || ch == '_')
						state = 2;
					else if (48 <= ch && ch <= 57) // e' un numero
						state = 3;
					else
						state = -1;
					break;

				case 1: // e' stata letta una lettera
					if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122 || 48 <= ch && ch <= 57 || ch == '_' || ch == ' ') // qualsiasi simbolo dell'alfabeto
						state = 1;
					else // il simbolo letto non appartiene all'alfabeto del DFA
						state = -1;
					break;

				case 2: // e' stato letto _ oppure spazio
					if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122 || 48 <= ch && ch <= 57) // e' una lettera o un numero
						state = 1;
					else if (ch == ' ' || ch == '_')
						state = 2; // rimane in attesa di leggere un simbolo valido (lettera o numero)
					else
						state = -1;
					break;

				case 3: // il primo simbolo letto e' un numero
					if (65 <= ch && ch <= 90 || 97 <= ch && ch <= 122 || 48 <= ch && ch <= 57 || ch == '_' || ch == ' ') // qualsiasi simbolo dell'alfabeto
						state = 3;
					else
						state = -1;
					break;
			}
		}
		return state == 1; // stato accettante
	}
	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
	}
}