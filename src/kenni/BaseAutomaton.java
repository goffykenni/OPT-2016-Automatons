package kenni;

import java.util.Collection;
import java.util.EventObject;
import java.util.function.Consumer;

/**
 * Interface for an non-deterministic finite automaton
 * @author Libor
 *
 */
public abstract class BaseAutomaton {
	/* Public methods */
	
	/**
	 * Returns the name of this automaton.
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Returns the start state of this automaton.
	 * @return
	 */
	public abstract State getStartState();
	/**
	 * Determines whether the state with the given name is initial.
	 * @param stateName
	 * @return
	 */
	public abstract boolean isStartState(State state);
	/**
	 * Determines whether the given state is a final state.
	 * @param state
	 * @return
	 */
	public abstract boolean isFinalState(State state);
	/**
	 * Returns the epsilon closure for the given set of states.
	 * @param stateSet
	 * @return Epsilon closure as a set of states.
	 */
	public abstract Collection<State> getEpsilonClosure(Collection<State> stateSet);
	/**
	 * Returns the epsilon closure for the given state.
	 * @param state
	 * @return
	 */
	public abstract Collection<State> getEpsilonClosure(State state);
	/**
	 * Returns the transition result for the given state and symbol.
	 * If such transition is empty, an empty set is returned.
	 * Note that for better performance, no copy is made.
	 * @param source
	 * @param symbol
	 * @return Transition result as a set of states.
	 */
	public abstract Collection<State> getTransition(State state, Symbol symbol);
	
	public abstract Collection<State> getTransition(Collection<State> stateSet, Symbol symbol);
	
	/**
	 * Iterates over all states within this Automaton and carries out the
	 * specified action for each state.
	 * @param action
	 */
	public abstract void actionOverStates(Consumer<State> action);
	
	public abstract void actionOverFinalStates(Consumer<State> action);
	
	public abstract void actionOverTransitions(TriConsumer<State, Symbol, State> action);
	
	/**
	 * Dumps the transition table of this automaton. Mostly used for testing.
	 * @return Transiton table as formatted string.
	 */
	public abstract String dump();
	/* Protected methods */
	/**
	 * Creates a new state associated with this automaton.
	 * @param id The name of the state
	 * @return The created state.
	 */
	protected State touch(String id) {
		State result = new State(id);
		assert(result.parent == this);
		return result;
	}
	/* Nested classes */
	
	/**
	 * Represents an automaton state. The main idea here is to allow
	 * states to be named, so this little class is used instead of just plain integers.
	 * @author Libor
	 */
	public class State {
		public final String id;
		public final BaseAutomaton parent;
		private State(String id) {
			this.id = id;
			this.parent = BaseAutomaton.this;
		}
		
		private boolean cmpParents(BaseAutomaton other) {
			if (parent == null) {
				if (other == null)
					return true;
				else 
					return false;
			} else
				return parent.equals(other);
		}
		
		private boolean cmpIDs(String other) {
			if (id == null) {
				if (other == null)
					return true;
				else
					return false;
			} else
				return id.equals(other);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof State) {
				State other = (State) obj;
				return cmpParents(other.parent) && cmpIDs(other.id);
			} else
				return false;			
		}
		
		@Override
		public int hashCode() {
			int result = 19;
			result = 37 * result + (id == null ? 0 : id.hashCode());
			result = 37 * result + (parent == null ? 0 : parent.hashCode());
			return result;
		}

		@Override
		public String toString() {
			return "[State: " + id + "]";
		}
	}
	
	public static class StateEventObject extends EventObject {
		private static final long serialVersionUID = 1L;
		public final State state;
		
		public StateEventObject(BaseAutomaton source, State state) {
			super(source);
			this.source = source;
			this.state = state;
		}
	}
	
}
