package kenni;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import kenni.BaseAutomaton.State;

public class BasicSimulator {
	private final BaseAutomaton aut;
	private String sentence;
	private int position;
	// Boolean indicating whether the simulator has just been reseted
	private boolean reseted;
	private Collection<State> currentState;
	// A pooled helper object. It is cleared and rebuilt in next() and then
	// set as a currentState.
	private HashSet<State> nextState;
	
	public BasicSimulator(BaseAutomaton aut) {
		this.aut = aut;
		currentState = new HashSet<>();
		nextState = new HashSet<>();
	}
	
	public void reset(String sentence) {
		this.sentence = sentence;
		position = 0;
		currentState.clear();
		currentState.addAll(aut.getEpsilonClosure(aut.getStartState()));
		reseted = true;
	}
	
	public boolean hasNext() {
		return reseted || (sentence != null && position < sentence.length());
	}
	
	//TODO: Make it more efficient by caching final states
	public void next() {
		// Takes care of the first step, so that you can use next like iterator's next, i.e
		// while (simulator.hasNext) { ... simulator.next ... }
		
		if (reseted) {
			//System.out.println(currentState);
			reseted = false;
			return;
		}
		
		Symbol transitionSymbol = Symbol.getSymbol(sentence.charAt(position));
		nextState.clear();
		// Next input symbol
		nextState.addAll(aut.getTransition(currentState, transitionSymbol));
		// Wild card
		nextState.addAll(aut.getTransition(currentState, Symbol.WILD_CARD));
		// TODO: Complement
		
		currentState = aut.getEpsilonClosure(nextState);
		++position;
		//System.out.println(currentState);
	}
	
	/**
	 * Determines whether the underlying automaton accepts the given string.
	 * @return
	 */
	public boolean accepts(String sentence) {
		reset(sentence);
		while (hasNext()) {
			if (currentState.isEmpty())
				return false;
			else
				next();
		}
		return isFinal();
	}
	
	/**
	 * Returns the current position in the sentence.
	 */
	public int getCurrentPosition() {
		return position;
	}
	
	/**
	 * Determines whether the current simulation state contains at least one
	 * automaton state, which is final.
	 * @return
	 */
	public boolean isFinal() {
		for (State state : currentState) {
			if (aut.isFinalState(state))
				return true;
		}
		return false;
	}
	
	/**
	 * Retrieves all final states of the underlying automaton, that are currently active.
	 * @return All active final states as an ArrayList. If no final state is active, an empty
	 * array list is returned. If isFinal() returns true, then this method will always return
	 * a non-empty ArrayList.
	 */
	public ArrayList<State> getFinalStates() {
		ArrayList<State> result = new ArrayList<>();
		for (State state : currentState) {
			if (aut.isFinalState(state))
				result.add(state);
		}
		return result;
	}
	
	/* Methods for testing */
	public String acceptsAsDump(String sentence) {
		accepts(sentence);
		StringBuilder sb = new StringBuilder();
		sb.append("Sentence: ").append(sentence).append(" - ");
		sb.append(isFinal());
		return sb.toString();
	}
	
	public String dumpAutomaton() {
		return aut.dump();
	}
}
