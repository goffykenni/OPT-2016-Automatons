package kenni;

import java.util.HashMap;
import java.util.function.Consumer;
import kenni.BaseAutomaton.State;

/**
 * Implements a factory for automatons that are results of regular operations.
 * This factory directly creates its objects as mutable Automatons, which it returns.
 * @author Libor
 *
 */
public class DirectRegularFactory implements IRegularFactory {
	
	/* Singleton pattern implementation */
	private static class Holder {
		private static final DirectRegularFactory singleton = new DirectRegularFactory();
	}
	
	private DirectRegularFactory() { }
	
	public static DirectRegularFactory get() {
		return Holder.singleton;
	}
	
	/* Private methods */
	/* Transform the source state, call consumer, insert the state */
	private State insertStateIntoResult(Consumer<BeforeInsertedEvent> transState, boolean finalOnly,
			BaseAutomaton branch, State branchState, int branchID, Automaton result)
	{
		State local = result.getAvailableState(branchState);
		if (transState != null && (!finalOnly || branch.isFinalState(branchState))) {
			transState.accept(new BeforeInsertedEvent(result, local, branch, branchState, branchID));
		}
		result.insertState(local, false);
		
		return local;
	}
	
	/* Regular operations implementation */
	/* The idea is simple, just go over all transitions within auts and recreate
	 * them inside result. The practice, however, is slighty more difficult, as
	 * I do not allow two states with the same name exists in one automat. Therefore, it is
	 * necessary to create copies of all states inside result and then create transitions.
	 */
	private Automaton constructUnion(String name, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly, BaseAutomaton[] auts)
	{
		Automaton result = new Automaton(name);
		// Insert states and transitions from all passed automatons
		// These are maps from original states to newly constructed ones
		final HashMap<State, State> oldToNew = new HashMap<>();
		final HashMap<State, State> oldStartToNew = new HashMap<>();
		// Remember current branch for IBranch implementation

		for (int i = 0; i < auts.length; i++) {
			oldToNew.clear();
			// Create new states for actual branch
			final int branchID = i;
			auts[i].actionOverStates(new Consumer<BaseAutomaton.State>() {				
				@Override
				public void accept(State state) {
					State insState = insertStateIntoResult(transState, finalOnly, auts[branchID],
							state, branchID, result);
					oldToNew.put(state, insState);
					if (auts[branchID].isFinalState(state)) {
						result.markAsFinal(insState);
					}
				}
			});
			// Remember the start state
			State start = auts[i].getStartState();
			oldStartToNew.put(start, oldToNew.get(start));
			
			// Create transitions for actual branch
			auts[i].actionOverTransitions(new TriConsumer<BaseAutomaton.State, Symbol, BaseAutomaton.State>() {
				@Override
				public void accept(State source, Symbol symbol, State target) {
					assert(oldToNew.containsKey(source));
					assert(oldToNew.containsKey(target));
					result.insertTransition(oldToNew.get(source), symbol, oldToNew.get(target));
				}
			});
		}
		// Create new state and connect it with epsilon transitions to start states
		// of all passed automata.
		
		State init = result.touch("Init");
		init = result.insertState(init, true);
		result.setStartState(init);
		for (BaseAutomaton aut : auts) {
			result.insertTransition(init, Symbol.EPSILON, oldStartToNew.get(aut.getStartState()));
		}
		return result;
	}
	
	private Automaton constructConcat(String name, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly, BaseAutomaton[] auts)
	{
		Automaton result = new Automaton(name);		
		
		// Insert states and transitions from all passed automatons
		// This is a map from original states to newly constructed ones
		final HashMap<State, State> oldToNew = new HashMap<>();
		State lastStartState = null;
		
		for (int i = auts.length - 1; i >= 0; i--) {
			oldToNew.clear();
			
			// Create new states for actual branch
			final int branchID = i;
			auts[i].actionOverStates(new Consumer<BaseAutomaton.State>() {				
				@Override
				public void accept(State state) {
					State insState = insertStateIntoResult(transState, finalOnly, auts[branchID],
							state, branchID, result);
					oldToNew.put(state, insState);
				}
			});
			
			
			// Create transitions for actual branch
			auts[i].actionOverTransitions(new TriConsumer<BaseAutomaton.State, Symbol, BaseAutomaton.State>() {
				@Override
				public void accept(State source, Symbol symbol, State target) {
					assert(oldToNew.containsKey(source));
					assert(oldToNew.containsKey(target));
					result.insertTransition(oldToNew.get(source), symbol, oldToNew.get(target));
				}
			});
			
			// Perform action on final states. If this is the last automaton
			// Mark its states as final in result, otherwise connect them to the next
			// automaton's start state
			if (i == auts.length - 1) {
				auts[i].actionOverFinalStates(new Consumer<BaseAutomaton.State>() {					
					@Override
					public void accept(State state) {
						assert(oldToNew.containsKey(state));
						result.markAsFinal(oldToNew.get(state));
					}
				});
			} else {
				final State start = lastStartState;
				auts[i].actionOverFinalStates(new Consumer<BaseAutomaton.State>() {
					@Override
					public void accept(State state) {
						assert(oldToNew.containsKey(state));
						assert(branchID < auts.length - 1);
						result.insertTransition(oldToNew.get(state), Symbol.EPSILON,
								start);
					}
				});
			}
			
			// Remember the last start state
			lastStartState = oldToNew.get(auts[i].getStartState());
		}
		return result;
	}
	
	private Automaton constructIteration(String name, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly, BaseAutomaton aut)
	{
		Automaton result = new Automaton(name);
	
		aut.actionOverStates(new Consumer<BaseAutomaton.State>() {
			@Override
			public void accept(State state) {
				final State local = insertStateIntoResult(transState, finalOnly, aut, state, 0, result);
				// No clash in names since there is only one automaton
				assert(local.id == state.id);
				if (aut.isFinalState(state)) {
					result.markAsFinal(local);
				}
			}
		});
		
		// Copy the automaton
		aut.actionOverTransitions(new TriConsumer<BaseAutomaton.State, Symbol, BaseAutomaton.State>() {
			@Override
			public void accept(State source, Symbol symbol, State target) {
				result.insertTransition(source, symbol, target);				
			}
		});
		
		// New start state has to be created and connected with e-trans to
		// the previous start state
		final State start = result.getAvailableState(result.touch(aut.getStartState().id + "#"));
		result.insertState(start, false);
		result.insertTransition(start, Symbol.EPSILON, result.touch(aut.getStartState().id));
		result.setStartState(start);
		result.markAsFinal(start);
		
		// Now insert e-loops starting in final states
		result.actionOverFinalStates(new Consumer<BaseAutomaton.State>() {			
			@Override
			public void accept(State state) {
				result.insertTransition(state, Symbol.EPSILON, start);				
			}
		});
		
		return result;
	}
	
	
	/* IRegularFactory implementation */

	@Override
	public Automaton union(String resultName, BaseAutomaton... auts) {
		return constructUnion(resultName, null, false, auts);
	}

	@Override
	public Automaton union(String resultName, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly,
			BaseAutomaton... auts)
	{
		return constructUnion(resultName, transState, finalOnly, auts);
	}

	@Override
	public BaseAutomaton concatenation(String resultName, BaseAutomaton... auts) {
		return concatenation(resultName, null, false, auts);
	}

	@Override
	public BaseAutomaton concatenation(String resultName, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly,
			BaseAutomaton... auts) {
		return constructConcat(resultName, transState, finalOnly, auts);
	}

	@Override
	public BaseAutomaton iteration(String resultName, BaseAutomaton aut) {
		return iteration(resultName, null, false, aut);
	}

	@Override
	public BaseAutomaton iteration(String resultName, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly,
			BaseAutomaton aut) {
		return constructIteration(resultName, transState, finalOnly, aut);
	}

}
