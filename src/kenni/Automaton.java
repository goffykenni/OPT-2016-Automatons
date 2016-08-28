package kenni;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.function.Consumer;



/**
 * Represents an finite non-deterministic Automaton with possible epsilon
 * transitions.
 * @author Libor
 *
 */
public class Automaton extends BaseAutomaton {
	/* Static fields and constants */
	private HashSet<State> EMPTY_SET = new HashSet<>();
	/* Private fields */
	private final String name;
	//private HashSet<State> states = new HashSet<>();
	private State startState = null;
	private HashSet<State> finalStates = new HashSet<>();
	private HashMap<State, HashMap<Symbol, HashSet<State> > > transitions = new HashMap<>();
	
	/* Constructors */
	public Automaton(String name) {
		if (name == null)
			this.name = "";
		else
			this.name = name;
			
	}
	
	public Automaton() {
		this("");
	}
	
	/* Private methods for accessing transitions. These are the lowest level methods
	 * and they are used to access and manipulate set of states and transitions
	 */
	
	/* Does this automaton contain given state? */
	private boolean containsState(State state) {
		// All states in this automaton have this automaton set as parent
		assert(!transitions.containsKey(state) || state.parent == this);
		return transitions.containsKey(state);
	}
	
	/* Does this automaton contain given transition? */
	private boolean containsTransition(State source, Symbol symbol, State target) {
		assert(!transitions.containsKey(source) || source.parent == this);
		assert(!transitions.containsKey(target) || target.parent == this);
		return (transitions.containsKey(source) && transitions.get(source).containsKey(symbol)
				&& transitions.get(source).get(symbol).contains(target));
	}
	
	/* Inserts the state into underlying transition collection, that is not there yet. */
	private void insertNewIntoCollection(State state) {
		assert(state.parent == this);
		assert(!transitions.containsKey(state));
		transitions.put(state, new HashMap<>());
	}
	
	/* Removes an empty target set for the given state and transition symbol */
	private void removeEmptyFromCollection(State state, Symbol symbol) {
		assert(transitions.containsKey(state));
		HashMap<Symbol, HashSet<State>> image = transitions.get(state);
		assert(!image.containsKey(symbol) || image.get(symbol).isEmpty());
		image.remove(symbol);
	}
	
	/* Returns a collection of all states in this automaton */
	private Collection<State> getStates() {
		return transitions.keySet();
	}
	
	/* Returns target set for given state and symbol */
	private Collection<State> getTarget(State state, Symbol symbol) {
		assert(containsState(state));
		assert(transitions.containsKey(state));
		return transitions.get(state).get(symbol);
	}
	
	/* Connects an existing source to an existing target over a symbol */
	private void insertTarget(State source, Symbol symbol, State target) {
		assert(containsState(source));
		assert(transitions.containsKey(source));
		assert(containsState(target));
		assert(transitions.containsKey(target));
		
		HashMap<Symbol, HashSet<State>> image = transitions.get(source);
		HashSet<State> targetSet = image.get(symbol);
		if (targetSet == null) {
			image.put(symbol, new HashSet<State>());
			targetSet = image.get(symbol);
		}
		targetSet.add(target);
	}
	
	/* Removes a transition (if exists) from existing source over a symbol over and existing target */
	private void removeTarget(State source, Symbol symbol, State target) {
		assert(containsState(source));
		assert(transitions.containsKey(source));
		assert(containsState(target));
		assert(transitions.containsKey(target));
		
		HashMap<Symbol, HashSet<State>> image = transitions.get(source);
		HashSet<State> targetSet = image.get(symbol);
		if (targetSet != null) {
			targetSet.remove(target);
		}
	}
	
	/* Return a collection of all outgoing symbols from the given state */
	private Collection<Symbol> getActiveSymbols(State source) {
		assert(containsState(source));
		assert(transitions.containsKey(source));
		return transitions.get(source).keySet();
	}

	/* Private and public methods for building the automaton */
	
	//======== Transition and state insertions
	/**
	 * Inserts the given state into the automaton, if it does not exists already.
	 * If inserting, this method will not make a copy of the state. It directly inserts the
	 * passed state.
	 * @param state
	 * @return true if the state was newly inserted, false otherwise.
	 */
	private State localize(State state) {
		if (state.parent != this)
			return touch(state.id);
		else
			return state;
	}
	
	private boolean insertStateIfNew_p(State state) {
		return insertStateIfNew_p(state, "");
	}
	
	private boolean insertStateIfNew_p(State state, String postfix) {
		assert(state.parent == this);
		if (containsState(state))
			return false;
		else {			
			insertNewIntoCollection(state);
			return true;
		}
	}
	
	private void insertTransition_p(State source, Symbol symbol, State target) {
		assert(source.parent == this);
		assert(target.parent == this);
		insertStateIfNew_p(source);
		insertStateIfNew_p(target);
		insertTarget(source, symbol, target);
		/*
		TransitionPair tpair = new TransitionPair(source, symbol);
		if (!transitions.containsKey(tpair))
			transitions.put(tpair, new HashSet<State>());
		transitions.get(tpair).add(target);*/
	}
	
	/**
	 * Creates or updates a transition from the source to the target over the
	 * given symbol. If either source or target states does not exists in this automaton,
	 * they are created automatically.
	 * @param source The source state.
	 * @param symbol The transition symbol.
	 * @param target The target state.
	 */
	public void insertTransition(State source, Symbol symbol, State target) {
		if (source != null && symbol != null && target != null)
			insertTransition_p(localize(source), symbol, localize(target));
	}
	
	/**
	 * Inserts the given state into this automaton.
	 * @param state The state to be inserted
	 * @param force If false, new state will not be inserted if it exists already. Otherwise,
	 * not yet used state name will be generated and a state with this name will be created.
	 * @return The State actually inserted. If force is true, this may be state with a different name
	 * than requested.
	 */
	public State insertState(State state, boolean force) {
		if (state == null)
			return null;
		state = localize(state);
		if (!insertStateIfNew_p(state)) {
			// State exists already, generate non-conflicting name			
			StringBuilder sb = new StringBuilder();
			sb.append(state.id);
			do { 
				sb.append("'");
				state = touch(sb.toString());
			} while (!insertStateIfNew_p(state));
		}
		return state;
	}
	
	/**
	 * If passed state is not yet contained in this automaton, this method simply return
	 * the passed state. Otherwise, a state <i>other</i> is returned, such that <i>!state.equals(other)</i>,
	 * but every property in <i>other</i> that does not determine State equality will be the same
	 * as in <i>state</i>.
	 * @param state
	 * @return State <i>other</i>, such that <i>!state.equals(other)</i>.
	 */
	public State getAvailableState(State state) {
		if (state == null)
			return null;
		state = localize(state);
		if (containsState(state)) {
			// State exists already, generate non-conflicting name			
			StringBuilder sb = new StringBuilder();
			sb.append(state.id);
			do { 
				sb.append("'");
				state = touch(sb.toString());
			} while (containsState(state));
		}
		return state;
	}
	

	
	//========== Setting final and initial states
	
	private void markAsFinal_p(State state) {
		assert(state.parent == this);
		assert(containsState(state));
		finalStates.add(state);
	}
	
	private void unmarkAsFinal_p(State state) {
		assert(state.parent == this);
		assert(containsState(state));
		finalStates.remove(state);
	}
	
	/**
	 * Marks the existing automaton state as final. If the state does not exists, nothing
	 * will happen.
	 * @param stateName
	 */
	public void markAsFinal(State state) {
		if (containsState(state))
			markAsFinal_p(state);
	}
	
	/**
	 * Marks the existing automaton state as non-final (default when creating new states).
	 * If the state does not exists, nothing will happen.
	 * @param stateName
	 */
	public void unmarkAsFinal(State state) {
		if (containsState(state))
			unmarkAsFinal_p(state);
	}
	
	private void setStartState_p(State state) {
		assert(state.parent == this);
		assert(containsState(state));
		startState = state;
	}
	
	/**
	 * Sets the start state of this automaton.
	 * @param stateName
	 */
	public void setStartState(State state) {
		if (containsState(state))
			setStartState_p(state);
	}	
	
	/* IAutomaton implementation */
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public State getStartState() {
		return startState;
	}
	
	private boolean isStartState_p(State state) {
		if (state != null)
			return state.equals(startState);
		else
			return false;
	}
	
	@Override
	public boolean isStartState(State state) {
		return isStartState_p(state);
	}
	
	@Override
	public boolean isFinalState(State state) {
		return finalStates.contains(state);
	}
	
	@Override
	public void actionOverStates(Consumer<State> action) {
		Collection<State> stateSet = getStates();
		for (State st : stateSet) {
			action.accept(st);
		}		
	}
	
	@Override
	public void actionOverFinalStates(Consumer<State> action) {
		for (State st : finalStates) {
			action.accept(st);
		}			
	}
	
	@Override
	public void actionOverTransitions(TriConsumer<State, Symbol, State> action) {
		Collection<State> sourceSet = getStates();
		for (State source : sourceSet) {
			Collection<Symbol> activeSymbol = getActiveSymbols(source);
			for (Symbol symbol : activeSymbol) {
				Collection<State> targetSet = getTarget(source, symbol);
				for (State target : targetSet) {
					action.accept(source, symbol, target);
				}
			}
		}
	}
	
	/**
	 * Performs a depth first search starting with an initialized stack.
	 * @param st
	 * @return
	 */
	private HashSet<State> dfs(Stack<State> st) {
		HashSet<State> result = new HashSet<>();
		while (!st.empty()) {
			State top = st.pop();
			result.add(top);
			
			Collection<State> targetSet = getTarget(top, Symbol.EPSILON);
			if (targetSet != null) {
				for (State follow : targetSet) {
					if (!result.contains(follow))
						st.push(follow);
				}
			}
			/*
			TransitionPair ePair = new TransitionPair(top, Symbol.EPSILON);
			if (transitions.containsKey(ePair)) {
				for (State follow : transitions.get(ePair)) {
					if (!result.contains(follow))
						st.push(follow);
				}
			}*/
		}
		return result;
	}
	
	@Override
	public Collection<State> getEpsilonClosure(Collection<State> stateSet) {
		if (stateSet == null)
			return EMPTY_SET;
		// Classic depth first search
		Stack<State> st = new Stack<>();
		for (State state : stateSet) {
			st.push(state);
		}
		
		return dfs(st);
	}
	
	@Override
	public Collection<State> getEpsilonClosure(State state) {
		if (state == null)
			return EMPTY_SET;
		// Depth first search
		Stack<State> st = new Stack<>();
		st.push(state);
		return dfs(st);
	}
	
	@Override
	public Collection<State> getTransition(State state, Symbol symbol) {
		if (state == null || symbol == null)
			return EMPTY_SET;
		Collection<State> targetSet = getTarget(state, symbol);
		if (targetSet != null)
			return targetSet;
		else
			return EMPTY_SET;
		/*
		TransitionPair tpair = new TransitionPair(state, symbol);
		if (transitions.containsKey(tpair))
			return transitions.get(tpair);
		else
			return EMPTY_SET;
			*/
	}
	
	@Override
	public Collection<State> getTransition(Collection<State> stateSet, Symbol symbol) {
		if (stateSet == null || symbol == null)
			return EMPTY_SET;
		HashSet<State> result = new HashSet<>();
		for (State state : stateSet) {
			Collection<State> followSet = getTarget(state, symbol);
			if (followSet != null)
				result.addAll(followSet);
			/*
			TransitionPair tpair = new TransitionPair(state, symbol);
			HashSet<State> followSet = transitions.get(tpair);
			if (followSet != null)
				result.addAll(followSet);
				*/
		}
		return result;
	}	
	
	/**
	 * Dumps the transition table for this automaton.
	 * @return Transition table as String.
	 */
	private void listState(State state, StringBuilder sb, String postfix) {
		sb.append(System.lineSeparator());
		if (finalStates.contains(state) && isStartState(state))
			sb.append("<>");
		else if (finalStates.contains(state))
			sb.append("<-");
		else if (isStartState(state))
			sb.append("->");
		else
			sb.append("  ");
		sb.append(state.id).append(postfix).append(":: ");
	}
	
	@Override
	public String dump() {
		// Get all entries and sort them lexicographically
		ArrayList<State> entries = new ArrayList<>(getStates());
		entries.sort((State s1, State s2) -> s1.id.compareTo(s2.id));
		
		StringBuilder sb = new StringBuilder();
		for (State source : entries) {
			listState(source, sb, name);
			// Iterate over transitions for this state
			Collection<Symbol> activeSymbols = getActiveSymbols(source);
			for (Symbol symbol : activeSymbols) {
				sb.append("(").append(symbol).append(": ");
				Collection<State> targetSet = getTarget(source, symbol);
				for (State target : targetSet) {
					sb.append(target.id).append(name).append(" ");
				}
				sb.append(") ");
			}
		}
		return sb.toString();
	}
	
	/* ===== Determinization methods =====
	 * 
	 */

	/**
	 * Scans through all epsilon transitions in this automaton and replaces them with
	 * appropriate symbol transitions, so that the changed automaton accepts the same language as
	 * before.
	 */
	public void removeEpsilonTransitions() {
		actionOverStates(new Consumer<BaseAutomaton.State>() {
			@Override
			public void accept(State source) {
				Collection<State> closure = getEpsilonClosure(source);
				closure.remove(source); // Remove myself from my closure
				for (State closureState : closure) {
					Collection<Symbol> activeSymbols = getActiveSymbols(closureState);
					// Over all outgoing transitions
					for (Symbol symbol : activeSymbols) {
						if (symbol == Symbol.EPSILON)
							continue; // Skip epsilon transition, this is IMPORTANT.
						Collection<State> targetSet = getTarget(closureState, symbol);
						// Connect source to target over appropriate symbol
						for (State target : targetSet) {
							assert(symbol != Symbol.EPSILON);
							insertTransition_p(source, symbol, target);
						}
					}
					// Remove the original epsilon transition
					assert(containsTransition(source, Symbol.EPSILON, closureState));
					removeTarget(source, Symbol.EPSILON, closureState);
				}
				removeEmptyFromCollection(source, Symbol.EPSILON);
			}
		});
	}

}
