package kenni;

import java.util.function.Consumer;

import kenni.BaseAutomaton.State;
import kenni.BaseAutomaton.StateEventObject;

/**
 * Represents a factory for creating automatons that are results of regular operations.
 * @author Libor
 *
 */
public interface IRegularFactory {
	/**
	 * Creates a new Automaton as a result of an associative operation applied to passed automatons.
	 * @param resultName The name of the resulting automaton.
	 * @param auts Automatons to be taken as arguments of operation. State of these automatons is not changed
	 * by calling this method.
	 * @return
	 */
	public BaseAutomaton union(String resultName, BaseAutomaton... auts);
	
	/**
	 * Creates a new Automaton as a result of an associative operation applied to passed automatons.
	 * For each new state that is about to be added to the result, transFinalState will be called
	 * allowing the caller to transform that state before insertion and possibly remembering some
	 * information.
	 * @param resultName The name of the resulting automaton.
	 * @param transState Function that gets called for each new state of the result automaton right
	 * before it gets inserted allowing to transform it before insertion. Passing null is equivalent
	 * to call create(String, IAutomaton...).
	 * @param finalOnly If true, transFinalState will be called only on the final states.
	 * @param auts  Automatons to be taken as arguments of operation. State of these automatons is not changed
	 * by calling this method.
	 * @return
	 */
	public BaseAutomaton union(String resultName, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly,
			BaseAutomaton... auts);
	
	public BaseAutomaton concatenation(String resultName, BaseAutomaton... auts);
	public BaseAutomaton concatenation(String resultName, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly,
			BaseAutomaton... auts);
	
	public BaseAutomaton iteration(String resultName, BaseAutomaton aut);
	public BaseAutomaton iteration(String resultName, Consumer<BeforeInsertedEvent> transState,
			boolean finalOnly,
			BaseAutomaton aut);
	
	public static class BeforeInsertedEvent extends StateEventObject {
		private static final long serialVersionUID = 1L;
		public final int branchID;
		public final BaseAutomaton branch;
		public final State branchState;
		
		public BeforeInsertedEvent(BaseAutomaton eventSource, State state, BaseAutomaton branch,
				State branchState, int branchID)
		{
			super(eventSource, state);
			this.branchID = branchID;
			this.branch = branch;
			this.branchState = branchState;
		}		
	}
}
