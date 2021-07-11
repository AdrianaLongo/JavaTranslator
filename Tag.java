// Classe di supporto per rappresentare i nomi dei token.

public class Tag {
	/*
	 * I campi della classe Tag sono sostanzialmente da trattare come delle costanti
	 * (intere) => devono essere... - public per poter essere usati al di fuori
	 * della classe - static => ne esiste una copia unica per tutti gli oggetti
	 * della classe - final => non possono piu' essere modificato dopo
	 * l'assegnamento iniziale
	 *
	 * Per rappresentare i terminali vengono usati interi > 255 perchè i caratteri
	 * ASCII vengono convertiti in interi tra 0 e 255; fanno eccezione < e >, che
	 * corrispondono ad un unico Relop
	 */

	public final static int EOF = -1, NUM = 256, ID = 257, RELOP = 258, COND = 259, WHEN = 260, THEN = 261, ELSE = 262,
			WHILE = 263, DO = 264, SEQ = 265, PRINT = 266, READ = 267, OR = 268, AND = 269;
}
