package automata;

import java.util.Set;

import static automata.Automaton.*;

public class Test2 {

    public static void main(String[] args) {

        State i0 = new State("0", true, false);
        State i1 = new State("1", true, false);
        State i2 = new State("2", true, false);
        State i3 = new State("3", true, true);

        i0.setTransition('a',i0);
        i0.setTransition('a',i1);
        i0.setTransition('b',i1);
        i2.setTransition('a',i3);
        i3.setTransition('b',i1);

        Set<State> nfa = Set.of(i0,i1,i2,i3);
        System.out.println("NFA:");
        printAutomaton(nfa);

        System.out.println("Determinization:");
        Set<State> sc=subsetConstruction(nfa);
        printAutomaton(sc);

        for(State s:sc){
            System.out.println(s);
        }
        System.out.println("Minimization:");
        Set<State> min=brzozowski(nfa);
        printAutomaton(min);
        for(State s:min){
            System.out.println(s);
        }
    }
}
