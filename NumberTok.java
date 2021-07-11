/* Estende token = <nome, valore> dichiarando il campo intero value
 * per rappresentare i token che corrispondono ai numeri
		    class Token
			int tag
 		  /        \
  class Num       class Word
  int value       string lexeme
*/

public class NumberTok extends Token {
	public int value = 0;

	// A differenza che in Word (che deve distinguere tra identificatori e
	// keywords), il costruttore può avere come
	// tag solo NUM

	public NumberTok(int tag, int v) {
		super(Tag.NUM); // inizializza il campo tag della superclasse Token al valore Tag.NUM
		value = v; // fondamentale! Bisogna dire al compilatore quale numero sta effettivamente
					// leggendo!
	}

	// Metodo per costruire una rappresentazione del token adatta ad essere stampata
	public String toString() {
		return "<" + Tag.NUM + ", " + value + ">";
	}
}
