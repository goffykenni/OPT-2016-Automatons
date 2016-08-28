package kenni;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * This class represents an immutable automaton that is a result of a
 * union operation applied to some set of finite automatons.
 * @author Libor
 *
 */
public class UnionAutomatonSim extends BaseAutomaton {

	/* Private fields */
	private final String name;
	private final State startState;
	private final BaseAutomaton[] auts;
	private static final HashSet<State> EMPTY_SET = new HashSet<>();
	
	// Cache
	private final HashMap<BaseAutomaton, Integer> autToBranchID = new HashMap<>();
	private final HashSet<State> originalStartStates = new HashSet<>();
			
	/* Private methods */
	private void performCaching(BaseAutomaton[] auts) {		
		for (int i = 0; i < auts.length; ++i){
			originalStartStates.add(auts[i].getStartState());
			autToBranchID.put(auts[i], i);
		}
	}
	
	public UnionAutomatonSim(String name, BaseAutomaton... auts) {
		if (name == null)
			this.name = "";
		else
			this.name = name;
		
		startState = touch("Init");
		this.auts = auts.clone();
		performCaching(auts);
	}
	
	public UnionAutomatonSim(BaseAutomaton... auts) {
		this("", auts);
	}
	
	/* Public methods */
	/**
	 * Returns the index (starting from zero)
	 * of the original automaton that this state appears in. If the given
	 * state does not appear in any of the original automatons, -1 is returned.
	 * @param state
	 * @return
	 */
	public int getBranchID(State state) {
		BaseAutomaton aut = state.parent;
		if (autToBranchID.containsKey(aut))
			return autToBranchID.get(aut);
		else
			return -1;
	}
	
	/* IAutomaton implementation */
	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public State getStartState() {
		return startState;
	}

	@Override
	public boolean isStartState(State state) {
		return state == startState;
	}

	@Override
	public boolean isFinalState(State state) {
		for (BaseAutomaton aut : auts) {
			if (aut.isFinalState(state))
				return true;
		}
		return false;
	}

	private Collection<State> getEpsilonClosure_p(State state) {
		if (state.equals(startState)) {
			HashSet<State> result = new HashSet<>();
			result.add(startState);
			for (State follow : originalStartStates) {
				BaseAutomaton parent = follow.parent;
				assert(autToBranchID.containsKey(parent));
				result.addAll(parent.getEpsilonClosure(follow));
			}
			return result;
		} else {
			BaseAutomaton parent = state.parent;
			Integer id = autToBranchID.get(parent);
			if (id != null)
				return parent.getEpsilonClosure(state);
			else 
				return EMPTY_SET;
		}
	}
	
	private Collection<State> getTransition_p(State state, Symbol symbol) {
		if (state.equals(startState)) {
			if (symbol == Symbol.EPSILON)
				return originalStartStates;
			else
				return EMPTY_SET;
		} else {
			BaseAutomaton parent = state.parent;
			Integer id = autToBranchID.get(parent);
			if (id != null)
				return parent.getTransition(state, symbol);
			else
				return EMPTY_SET;
		}
	}
	
	@Override
	public Collection<State> getEpsilonClosure(Collection<State> stateSet) {
		HashSet<State> result = new HashSet<>();
		for (State state : stateSet) {
			result.addAll(getEpsilonClosure_p(state));
		}
		return result;
	}

	@Override
	public Collection<State> getEpsilonClosure(State state) {
		return new HashSet<>(getEpsilonClosure_p(state));
	}

	@Override
	public Collection<State> getTransition(State state, Symbol symbol) {
		return new HashSet<>(getTransition_p(state, symbol));
	}

	@Override
	public Collection<State> getTransition(Collection<State> stateSet, Symbol symbol) {
		HashSet<State> result = new HashSet<>();
		for (State state : stateSet) {
			result.addAll(getTransition_p(state, symbol));
		}
		return result;
	}

	@Override
	public void actionOverStates(Consumer<State> action) {
		action.accept(startState);
		for (BaseAutomaton aut : auts) {
			aut.actionOverStates(action);
		}
	}
	
	@Override
	public void actionOverFinalStates(Consumer<State> action) {
		for (BaseAutomaton aut : auts) {
			aut.actionOverFinalStates(action);
		}
	}
	
	@Override
	public void actionOverTransitions(TriConsumer<State, Symbol, State> action) {
		// Transitions from the start state will be handled separately
		for (State target : originalStartStates) {
			action.accept(startState, Symbol.EPSILON, target);
		}
		// The rest of the transitions is delegated to their corresponding automatons.
		for (BaseAutomaton aut : auts) {
			aut.actionOverTransitions(action);
		}
	}

	@Override
	public String dump() {
		StringBuilder sb = new StringBuilder();
		for (BaseAutomaton aut : auts) {
			sb.append(aut.dump());
		}
		return sb.toString();
	}
	

	
}
