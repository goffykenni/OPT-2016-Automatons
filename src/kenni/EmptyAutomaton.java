package kenni;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
/**
 * Represents a completely empty automaton, that only has one state, which is initial,
 * and no other states and no transitions.
 * @author Libor
 *
 */
public class EmptyAutomaton extends BaseAutomaton {
	/* Private fields */
	private final String name = "Empty";
	private final State initState = touch("Init");
	
	/* Singleton pattern implementation */
	private static class Holder {
		private static final EmptyAutomaton singleton = new EmptyAutomaton();
	}
	
	private EmptyAutomaton() { }
	
	public static EmptyAutomaton get() {
		return Holder.singleton;
	}
	
	/* IAutomaton implementation */

	@Override
	public String getName() {
		return name;
	}

	@Override
	public State getStartState() {
		return initState;
	}

	@Override
	public boolean isStartState(State state) {
		assert(initState != null);
		return initState.equals(state);
	}

	@Override
	public boolean isFinalState(State state) {
		return false;
	}

	@Override
	public Collection<State> getEpsilonClosure(Collection<State> stateSet) {
		return new HashSet<>();
	}

	@Override
	public Collection<State> getEpsilonClosure(State state) {
		return new HashSet<>();
	}

	@Override
	public Collection<State> getTransition(State state, Symbol symbol) {
		return new HashSet<>();
	}

	@Override
	public Collection<State> getTransition(Collection<State> stateSet, Symbol symbol) {
		return new HashSet<>();
	}

	@Override
	public void actionOverStates(Consumer<State> action) {
		assert(initState != null);
		action.accept(initState);
	}

	@Override
	public void actionOverFinalStates(Consumer<State> action) {	}

	@Override
	public void actionOverTransitions(TriConsumer<State, Symbol, State> action) { }

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("Empty Automaton: ")
			.append(System.lineSeparator())
			.append(initState.id)
			.append(System.lineSeparator());
		return sb.toString();			
	}

}
