package automata;

import java.util.Set;

import static automata.Automaton.*;


public class Test4 {
	public static void main(String[] args) {


		State i0 = new State("0", true, false);
		State i1 = new State("1", false, true);


		i0.setTransition('0', i0);
		i0.setTransition('1', i0);
		i0.setTransition('1', i1);


		Set<State> nfa = Set.of(i0, i1);
		System.out.println("NFA:");
		printAutomaton(nfa);

		System.out.println("Determinization:");
		Set<State> sc = subsetConstruction(nfa);
		printAutomaton(sc);

		System.out.println("Minimization:");
		Set<State> min=brzozowski(nfa);
		printAutomaton(min);

	}
}
