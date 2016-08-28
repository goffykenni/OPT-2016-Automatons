package kenni;

public class Main {
	public static void main(String[] args) {
		/*
		Automaton aut = new Automaton();
		AutomatonBuilder builder = new AutomatonBuilder(aut);*/
		/*
		
		//aut.insertTransition(new State("Softboiled"), Symbol.getSymbol('a'), new State("Done"));
		builder.insertTransition("Start", Symbol.getSymbol('n'), "Start");
		builder.insertTransition("Start", Symbol.getSymbol('c'), "Heating");
		builder.insertTransition("Heating", Symbol.getSymbol('n'), "Start");
		builder.insertTransition("Heating", Symbol.getSymbol('c'), "Pyro");
		builder.insertTransition("Pyro", Symbol.getSymbol('n'), "Start");
		builder.insertTransition("Pyro", Symbol.getSymbol('c'), "Heating");
		builder.insertTransition("Pyro", Symbol.getSymbol('c'), "Def");
		builder.setStartState("Start");
		builder.markAsFinal("Pyro");
		System.out.println(aut.dump());
		
		System.out.println(aut.getEpsilonClosure(new State("Start")));		
		System.out.println(builder.getTransition("Pyro", Symbol.getSymbol('c')));
		
		ArrayList<String> lst = new ArrayList<>();
		lst.add("Pyro"); lst.add("Def"); lst.add("Heating");
		System.out.println(builder.getTransition(lst, Symbol.getSymbol('c')));
		
		System.out.println("SIMULATION: ");
		BasicSimulator simulator = new BasicSimulator(aut);
		System.out.println(simulator.accepts("cccc"));
		*/
		
		/*
		builder.insertTransition("1", Symbol.getSymbol('a'), "2");
		builder.insertTransition("2", Symbol.getSymbol('b'), "3");
		builder.insertTransition("2", Symbol.EPSILON, "4");
		builder.insertTransition("2", Symbol.EPSILON, "6");
		builder.insertTransition("4", Symbol.getSymbol('c'), "5");
		builder.insertTransition("6", Symbol.getSymbol('c'), "7");
		builder.insertTransition("7", Symbol.getSymbol('c'), "6");
		
		builder.setStartState("1");
		builder.markAsFinal("3");
		builder.markAsFinal("5");
		
		System.out.println(aut.dump());
		System.out.println(aut.getEpsilonClosure(new State("1", aut)));
		System.out.println(aut.getEpsilonClosure(new State("2", aut)));
		System.out.println("SIMULATION: ");
		BasicSimulator simulator = new BasicSimulator(aut);
		
		System.out.println(simulator.acceptsAsDump("cccc"));
		System.out.println(simulator.acceptsAsDump("ac"));
		*/
		
		/*
		Sfoeco sfo = new Sfoeco("fire");
		System.out.println(sfo.dumpAutomaton());
		System.out.println(sfo.search("Creates a huge pillar of fire at target foe's location"
				+ " blasting all foes in area for 243 fire damage and another 698 fire damage over the"
				+ "next 10 seconds. If this spell hits at least 3 targets, you become heated and"
				+ "your fire damage increases by 20% for the next 10 seconds."));
				*/
		
		/*
		Sffeco sff = new Sffeco("fireball", "base", "fire");
		System.out.println(sff.dumpAutomaton());
		System.out.println(sff.search("Improved fireball: Your fireball will strike foes that"
				+ " are set on fire by another 50% of base damage." ));
			*/	
				
		/*
		SuffixAutomaton sAut = SuffixAutomaton.create("", "brko", true);
		System.out.println(sAut.dump());
		BasicSimulator simula = new BasicSimulator(sAut);
		System.out.println(simula.acceptsAsDump("rko"));
		*/
		
		/*
		builder.insertTransition("Omega", Symbol.EPSILON, "Beta");
		builder.insertTransition("Beta", Symbol.EPSILON, "Omega");
		builder.markAsFinal("Omega");
		builder.setStartState("Omega");
		System.out.println(builder.getAutomaton().dump());
		BasicSimulator simulator = new BasicSimulator(builder.getAutomaton());
		String testStr = "a";
		System.out.println(simulator.acceptsAsDump(testStr));
		builder.getAutomaton().removeEpsilonTransitions();
		System.out.println(builder.getAutomaton().dump());
		System.out.println(simulator.acceptsAsDump(testStr));*/
		
		
		AutomatonBuilder ba = new AutomatonBuilder(new Automaton());
		ba.insertTransition("0", Symbol.getSymbol('a'), "1");
		ba.insertTransition("0", Symbol.getSymbol('a'), "2");
		ba.insertTransition("1", Symbol.getSymbol('b'), "3");
		ba.insertTransition("2", Symbol.getSymbol('c'), "4");
		ba.setStartState("0");
		ba.markAsFinal("3");
		ba.markAsFinal("4");
		
		AutomatonBuilder bb = new AutomatonBuilder(new Automaton());
		bb.insertTransition("0", Symbol.getSymbol('c'), "0");
		bb.insertTransition("0", Symbol.getSymbol('d'), "1");
		bb.setStartState("0");
		bb.markAsFinal("1");
		//System.out.println(ba.getAutomaton().dump());

		//System.out.println(bb.getAutomaton().dump());
		
		DirectRegularFactory fact = DirectRegularFactory.get();
		BaseAutomaton conc = fact.concatenation("", ba.getAutomaton(), bb.getAutomaton());
		System.out.println(conc.dump());
		
		BaseAutomaton it = fact.iteration("", ba.getAutomaton());
		System.out.println(it.dump());
	}
}
