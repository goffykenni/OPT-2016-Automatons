package kenni;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represent an immutable suffix automaton constructed for the given string pattern.
 * @author Libor
 */
public class SuffixAutomaton extends BaseAutomaton {
	/* Private fields */
	private Automaton aut;
	private String pattern;
	
	/* Constructors and factory methods */
	
	private SuffixAutomaton(Automaton aut, String pattern) {
		this.aut = aut;
		this.pattern = pattern;
	}
	
	public static SuffixAutomaton create(String name, String pattern, boolean removeEpsilonTrans) {
		return new SuffixAutomaton(createSuffixAutomaton(name, pattern, removeEpsilonTrans), pattern);
	}
	
	/* Private and helper methods */
	private static Automaton createSuffixAutomaton(String name, String pattern, boolean removeEpsilonTrans) {
		AutomatonBuilder builder = new AutomatonBuilder(new Automaton());
		// Create the backbone
		for (int i = 0; i < pattern.length(); ++i) {
			builder.insertTransition(String.valueOf(i), Symbol.getSymbol(pattern.charAt(i)),
					String.valueOf(i + 1));
			// Add epsilon transitions from the initial state
			builder.insertTransition("0", Symbol.EPSILON, String.valueOf(i + 1));
		}
		
		// Set start state and final states
		builder.setStartState("0");
		builder.markAsFinal(String.valueOf(pattern.length()));		
		
		if (removeEpsilonTrans)
			builder.getAutomaton().removeEpsilonTransitions();
		
		return builder.getAutomaton();
	}

	/* IAutomaton implementation */
	
	@Override
	public String getName() {
		return aut.getName();
	}

	@Override
	public State getStartState() {
		return aut.getStartState();
	}

	@Override
	public boolean isStartState(State state) {
		return aut.isStartState(state);
	}

	@Override
	public boolean isFinalState(State state) {
		return aut.isFinalState(state);
	}

	@Override
	public Collection<State> getEpsilonClosure(Collection<State> stateSet) {
		return aut.getEpsilonClosure(stateSet);
	}

	@Override
	public Collection<State> getEpsilonClosure(State state) {
		return aut.getEpsilonClosure(state);
	}

	@Override
	public Collection<State> getTransition(State state, Symbol symbol) {
		return aut.getTransition(state, symbol);
	}

	@Override
	public Collection<State> getTransition(Collection<State> stateSet, Symbol symbol) {
		return aut.getTransition(stateSet, symbol);
	}

	@Override
	public void actionOverStates(Consumer<State> action) {
		aut.actionOverStates(action);
	}

	@Override
	public void actionOverFinalStates(Consumer<State> action) {
		aut.actionOverFinalStates(action);
	}

	@Override
	public void actionOverTransitions(TriConsumer<State, Symbol, State> action) {
		aut.actionOverTransitions(action);
	}

	@Override
	public String dump() {
		return aut.dump();
	}

}
