package automata;

import java.util.Set;

import static automata.Automaton.*;

public class Test3 {

    public static void main(String[] args) {//3 lettera da destra è 1

        //Caso limite della subset construction
        State q0 = new State("0", true, false);
        State q1 = new State("1", false, false);
        State q2 = new State("2", false, false);
        State q3 = new State("3", false, false);
        State q4 = new State("4", false, true);
        q0.setTransition('0',q0);
        q0.setTransition('1',q0);
        q0.setTransition('1',q1);
        q1.setTransition('0',q2);
        q1.setTransition('1',q2);
        q2.setTransition('0',q3);
        q2.setTransition('1',q3);
        q3.setTransition('0',q4);
        q3.setTransition('1',q4);

        Set<State> nfa = Set.of(q0,q1,q2,q3,q4);
        System.out.println("NFA:");
        printAutomaton(nfa);
        System.out.println("Determinization:");
        printAutomaton(subsetConstruction(nfa));
        System.out.println("Minimization:");
        printAutomaton(brzozowski(nfa));
    }
}
