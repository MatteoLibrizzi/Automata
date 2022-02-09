package automata;

import java.util.Set;

import static automata.Automaton.*;

public class Test1 {//suffix automaton

    public static void main(String[] args) {

        //Suffix automaton
        State i0 = new State("0", true, false);
        State i2 = new State("2", true, false);

        State i1 = new State("1", true, false);
        State i3 = new State("3", true, false);
        State i4 = new State("4", true, false);
        State i5 = new State("5", true, false);
        State i6 = new State("6", true, false);
        State i7 = new State("7", true, true);

        i0.setTransition('a',i1);
        i1.setTransition('a',i2);
        i2.setTransition('b',i3);
        i3.setTransition('b',i4);
        i4.setTransition('a',i5);
        i5.setTransition('b',i6);
        i6.setTransition('b',i7);

        Set<State> nfa = Set.of(i0,i1,i2,i3,i4,i5,i6,i7);
        System.out.println("NFA:");
        printAutomaton(nfa);


        System.out.println("Determinization:");
        printAutomaton(subsetConstruction(nfa));
        System.out.println("Minimization:");
        printAutomaton(brzozowski(nfa));
    }
}
