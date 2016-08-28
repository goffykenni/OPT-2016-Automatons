package kenni;

import java.util.HashMap;

/**
 * Represents a transition symbol for finite automatons.
 * A transition symbol is either epsilon, single character, wild card or a complement.
 * @author Libor
 *
 */
public class Symbol implements Comparable<Symbol> {
	/* Static constants */
	public static final Symbol EPSILON = new Symbol(' ');
	public static final Symbol WILD_CARD = new Symbol(' ');
	public static final Symbol COMPLEMENT = new Symbol(' ');
	
	// Used for comparing
	private static final HashMap<Symbol, Integer> specialPriorities;
	static {
		specialPriorities = new HashMap<>();
		specialPriorities.put(EPSILON, 3);
		specialPriorities.put(WILD_CARD, 2);
		specialPriorities.put(COMPLEMENT, 1);
	}
	
	/* Static fields and constants */
	private static HashMap<Character, Symbol> datamap = new HashMap<>();
	
	/* Public fields */
	public final char value;
	
	private Symbol(char value) {
		this.value = value;
	}
	
	/* Static methods */
	/**
	 * Returns a Symbol representing the given value. If such Symbol has not yet been accessed
	 * (and therefore it does not exist yet), it will be created and then returned.
	 * @param value Character for which its Symbol object will be returned.
	 * @return The Symbol representing the given value.
	 */
	public static Symbol getSymbol(char value) {
		if (datamap.containsKey(value))
			return datamap.get(value);
		else {
			Symbol s = new Symbol(value);
			datamap.put(value, s);
			return s;
		}			
	}
	
	/* Public methods */

	@Override
	public int compareTo(Symbol other) {
		int r = Character.compare(this.value, other.value);
		if (r == 0 && specialPriorities.containsKey(this) && specialPriorities.containsKey(other)) {
			return specialPriorities.get(this).compareTo(specialPriorities.get(other));
		} else
			return r;
	}
	
	@Override
	public String toString() {
		if (this == EPSILON)
			return "EP";
		else if (this == WILD_CARD)
			return "*";
		else if (this == COMPLEMENT)
			return "*_C";
		else
			return String.valueOf(value);
	}
}
