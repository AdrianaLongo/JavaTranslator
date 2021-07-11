/* Classe di supporto alla classe Translator.java per costruire una tabella dei simboli che tenga traccia degli
identificatori .
* */

import java.util.*;

public class SymbolTable {
	Map <String, Integer> OffsetMap = new HashMap <String,Integer>();

	public void insert( String s, int address ) {

		if( !OffsetMap.containsValue(address) )

			OffsetMap.put(s,address);

		else

			throw new IllegalArgumentException("Riferimento ad una  locazione di memoria gia‘ occupata da un’altra " +
					"variabile");

	}

	public int lookupAddress ( String s ) {

		if( OffsetMap.containsKey(s) )

			return OffsetMap.get(s);

		else

			return -1;

	}

}