package kenni;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Consumer;
import kenni.BaseAutomaton.State;
import kenni.IRegularFactory.BeforeInsertedEvent;

public class Sffeco {
	/* Static fields */
	private static IRegularFactory defFactory = SimRegularFactory.get();
	
	/* Private fields */
	// Maps final states to their corresponding branches
	private String[] patterns;
	private BaseAutomaton aut;
	private BasicSimulator simulator;
	private HashMap<State, Integer> finalStateToBranchID;
	
	
	public Sffeco(String... patterns) {
		this.patterns = patterns;
		this.finalStateToBranchID = new HashMap<>();
		this.aut = createSearchAutomaton(patterns);
		simulator = new BasicSimulator(this.aut);
	}

	/* Private methods */
	
	// Creates the search automaton and remembers which final state belongs to which of the
	// automaton branches.
	private BaseAutomaton createSearchAutomaton(String[] patterns) {
		if (patterns == null || patterns.length == 0)
			EmptyAutomaton.get();
		
		// Create sfoeco for each pattern
		final BaseAutomaton[] sourceAuts = new BaseAutomaton[patterns.length];
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < patterns.length; ++i) {
			BaseAutomaton sfoeco = Sfoeco.createSearchAutomaton(patterns[i], sb.toString());
			sourceAuts[i] = sfoeco;
			sb.append("'");
		}
		
		// Create consumer to remember branch id's for final states when creating the union
		Consumer<BeforeInsertedEvent> action = new Consumer<IRegularFactory.BeforeInsertedEvent>() {			
			@Override
			public void accept(BeforeInsertedEvent args) {
				BaseAutomaton branch = args.branch;
				State branchState = args.branchState;
				int branchID = args.branchID;
				State local = args.state;
				assert(branch.isFinalState(branchState));
				assert(sourceAuts[branchID] == branch);
				finalStateToBranchID.put(local, branchID);
			}
		};
		
		return defFactory.union("", action, true, sourceAuts);
	}
	
	/* Expects a final state of automaton aut. Returns the branch id for this state. */
	private int getBranchIDForFinal(State state) {
		assert(state != null);
		assert(state.parent.isFinalState(state));
		assert(finalStateToBranchID.containsKey(state));
		return finalStateToBranchID.get(state);	
		
	}
	
	/* Public methods */

	/**
	 * Searches for the underlying set of patterns in the given text.
	 * @param text The text to be searched in.
	 * @return An ArrayList of pairs of integers, where the first number indicates the index
	 * of the matched pattern in the underlying set and the second number is the position in
	 * the given text, where this match ends.
	 */
	public ArrayList<Pair<Integer>> search(String text) {
		ArrayList<Pair<Integer>> result = new ArrayList<>();
		simulator.reset(text);
		while (simulator.hasNext()) {
			simulator.next();

			if (simulator.isFinal()) {
				ArrayList<State> finalStates = simulator.getFinalStates();
				// Get the longest match
				if (finalStates.size() > 1) {
					finalStates.sort(new Comparator<State>() {
						@Override
						public int compare(State s1, State s2) {
							int id1 = getBranchIDForFinal(s1);
							int id2 = getBranchIDForFinal(s2);
							return -Integer.valueOf(patterns[id1].length())
								.compareTo(Integer.valueOf(patterns[id2].length()));
						}
					});
				}
				
				int id = getBranchIDForFinal(finalStates.get(0));
				result.add(new Pair<Integer>(id, simulator.getCurrentPosition()));
			}
		}
		return result;
	}
	
	/* Methods for testing */
	public String dumpAutomaton() {
		return simulator.dumpAutomaton();
	}
	

}
