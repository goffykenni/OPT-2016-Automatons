package kenni;

import java.util.ArrayList;

public class Sfoeco extends BasicSimulator {
	
	/* Private fields */
	private String mPattern;

	/* Constructors and helper methods */
	
	public Sfoeco(String pattern) {		
		super(createSearchAutomaton(pattern, ""));
		mPattern = pattern;
	}
	
	static public Automaton createSearchAutomaton(String pattern, String name) {
		Automaton aut = new Automaton(name);
		if (pattern == null || pattern.length() == 0)
			return aut;
		
		AutomatonBuilder builder = new AutomatonBuilder(aut);
		
		// Insert search loop
		builder.insertTransition("0", Symbol.WILD_CARD, "0");
		
		// Create the backbone
		for (int i = 0; i < pattern.length(); ++i) {
			builder.insertTransition(String.valueOf(i), Symbol.getSymbol(pattern.charAt(i)),
					String.valueOf(i + 1));
		}
		
		// Set start state and final states
		builder.setStartState("0");
		builder.markAsFinal(String.valueOf(pattern.length()));
		
		return aut;
	}
	
	/* Public methods */
	
	/**
	 * Searches the given text for the occurrences of the underlying pattern.
	 * @param text The text to be searched in.
	 * @return An ArrayList of integers representing the end positions of each successful match.
	 * The array is sorted in ascending order.
	 */
	public ArrayList<Integer> search(String text) {
		ArrayList<Integer> result = new ArrayList<>();
		reset(text);
		while (hasNext()) {
			next();
			if (isFinal())
				result.add(getCurrentPosition());
		}
		return result;
	}
	
	public String getPattern() {
		return mPattern;
	}
}
