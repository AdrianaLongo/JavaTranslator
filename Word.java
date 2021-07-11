/* Classe per gestire i lessemi per le parole riservate, gli identificatori e i token composti come ||; e' utile
anche per gestire la forma scritta estesa degli operatori nel codice intermedio.

Estende token = <nome, valore> dichiarando il campo stringa lexeme
		    class Token
			int tag
 		  /        \
  class Num       class Word
  int value       string lexeme

Per "lessema" si intende una sequenza di caratteri nel programma sorgente che corrisponde al pattern di un token ed è
 identificata dal lexer come una specifica istanza di quel token.
*/

public class Word extends Token {
	public String lexeme = "";

	// A differenza di NumberTok, che gestisce solo numeri (quindi può avere solo un
	// Tag.NUM), Word deve gestire sia le
	// parole chiave riservate (ognuna con un proprio Tag) che gli identificatori
	// (Tag.ID), quindi dobbiamo dare al
	// costruttore due parametri: un lessema e un corrispondente valore intero per
	// il campo tag
	public Word(int tag, String s) {
		super(tag);
		lexeme = s;
	}

	// Metodo per costruire una rappresentazione del token adatta ad essere stampata
	public String toString() {
		return "<" + tag + ", " + lexeme + ">";
	}

	/*
	 * I campi della classe Tag sono sostanzialmente da trattare come delle costanti
	 * (intere) => devono essere... - public per poter essere usati al di fuori
	 * della classe - static => ne esiste una copia unica per tutti gli oggetti
	 * della classe - final => non possono piu' essere modificato dopo
	 * l'assegnamento iniziale
	 */
	public static final Word cond = new Word(Tag.COND, "cond"), when = new Word(Tag.WHEN, "when"),
			then = new Word(Tag.THEN, "then"), elsetok = new Word(Tag.ELSE, "else"),
			whiletok = new Word(Tag.WHILE, "while"), dotok = new Word(Tag.DO, "do"), seq = new Word(Tag.SEQ, "seq"),
			print = new Word(Tag.PRINT, "print"), read = new Word(Tag.READ, "read"), or = new Word(Tag.OR, "||"),
			and = new Word(Tag.AND, "&&"), lt = new Word(Tag.RELOP, "<"), gt = new Word(Tag.RELOP, ">"),
			eq = new Word(Tag.RELOP, "=="), le = new Word(Tag.RELOP, "<="), ge = new Word(Tag.RELOP, ">="),
			ne = new Word(Tag.RELOP, "<>");

}
