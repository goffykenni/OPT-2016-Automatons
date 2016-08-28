package kenni;

import java.util.function.Consumer;

import kenni.BaseAutomaton.State;

/**
 * Implements a factory for automatons that are results of regular operations.
 * This factory returns immutable IAutomatons, that simulates the desired result.
 * @author Libor
 */
public class SimRegularFactory implements IRegularFactory {

	/* Singleton pattern implementation */
	private static class Holder {
		private static final SimRegularFactory singleton = new SimRegularFactory();
	}
	
	private SimRegularFactory() { }
	
	public static SimRegularFactory get() {
		return Holder.singleton;
	}
	
	@Override
	public BaseAutomaton union(String resultName, BaseAutomaton... auts) {
		return new UnionAutomatonSim(resultName, auts);
	}

	@Override
	public UnionAutomatonSim union(String resultName, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly, BaseAutomaton... auts)
	{
		UnionAutomatonSim result = new UnionAutomatonSim(resultName, auts);		
		for (int i = 0; i < auts.length; i++) {
			final int branchID = i;
			// Define action. Note that sourceState is passed twice here,
			// because the result automaton is only simulation and does not create any states
			// on its own.
			Consumer<State> action = (State sourceState) ->
			transState.accept(new BeforeInsertedEvent(result, sourceState,
					auts[branchID], sourceState, branchID));
			if (finalOnly)
				auts[i].actionOverFinalStates(action);
			else
				auts[i].actionOverStates(action);
		}
		return result;
	}

	@Override
	public UnionAutomatonSim concatenation(String resultName, BaseAutomaton... auts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseAutomaton concatenation(String resultName, Consumer<BeforeInsertedEvent> transState, boolean finalOnly,
			BaseAutomaton... auts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseAutomaton iteration(String resultName, BaseAutomaton aut) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseAutomaton iteration(String resultName, Consumer<BeforeInsertedEvent> transState, boolean finalOnly,
			BaseAutomaton aut) {
		// TODO Auto-generated method stub
		return null;
	}

}
