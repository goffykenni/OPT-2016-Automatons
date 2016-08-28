package kenni;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import kenni.BaseAutomaton.State;

/**
 * <p>Convinience class for constructing automata, that allows creating and
 * accessing Automata states just by their name.
 * It contains adaptor methods for most building and accessing methods in the Automata class.</p>
 * <p>
 * If you directly use any of the underlying automaton's method that changes its state, the state
 * of this object will be undefined.</p>
 * @author Libor
 *
 */
public class AutomatonBuilder {
	/* Private fields and constants */
	private Automaton aut;
	private HashMap<String, State> cache = new HashMap<>();
	private HashSet<State> EMPTY_SET = new HashSet<>();
	
	public AutomatonBuilder(Automaton aut) {
		this.aut = aut;
	}
	
	/* Private methods */
	private State getState(String key) {
		State result = cache.get(key);
		if (result == null) {
			result = aut.touch(key);
			cache.put(key, result);
		}
		return result;			
	}	
	
	/* Public building methods */
	public void insertTransition(String source, Symbol symbol, String target) {
		if (source != null && target != null) {
			State sourceState = getState(source);
			State targetState = getState(target);
			aut.insertTransition(sourceState, symbol, targetState);
		}
	}
	
	public void setStartState(String stateName) {
		if (stateName != null) {
			State state = getState(stateName);
			aut.setStartState(state);
		}			
	}
	
	public void markAsFinal(String stateName) {
		if (stateName != null) {
			State state = getState(stateName);
			aut.markAsFinal(state);
		}
	}
	
	public void unmarkAsFinal(String stateName) {
		if (stateName != null) {
			State state = getState(stateName);
			aut.unmarkAsFinal(state);
		}
	}
	
	/* Public methods accesing the automaton instance state */
	
	/**
	 * Returns the underlying automaton.
	 * @return
	 */
	public Automaton getAutomaton() {
		return aut;
	}
	
	public Collection<State> getTransition(String stateName, Symbol symbol) {
		if (stateName != null) {
			State state = getState(stateName);
			return aut.getTransition(state, symbol);
		} else
			return EMPTY_SET;
	}
	
	public Collection<State> getTransition(ArrayList<String> stateNames, Symbol symbol) {
		HashSet<State> sourceSet = new HashSet<>();
		for (String stateName : stateNames) {
			if (stateName != null) {
				sourceSet.add(getState(stateName));				
			}			
		}
		return aut.getTransition(sourceSet, symbol);
	}
}
