package automata;

import java.util.*;
import java.util.stream.Collectors;

public class State implements Comparable<State> {
    String label;
    boolean initial, terminal, visited;
    Map<Character,Set<State>> successors = new HashMap<>();

    State(String label, boolean initial, boolean terminal) {
        this.label = label;
        this.initial = initial;
        this.terminal = terminal;
        this.visited=false;
    }

    public String getLabel() {
        return label;
    }

    void setTransition(Character a, State target){
        this.successors.putIfAbsent(a,new HashSet<>());
        this.successors.get(a).add(target);
    }

    String printSuccessors(){
        String s = "[";
        for (Character a : this.successors.keySet())
            s+= " " + a + " -> "
                    + this.successors.get(a).stream()
                    .map(q -> q.label)
                    .collect(Collectors.joining(", "))
                    + ";";
        return s+"]";
    }

    @Override
    public String toString() {
        return "State{" +
                "label=" + label +
                ", initial=" + initial +
                ", terminal=" + terminal +
                ", successors=" + printSuccessors() +
                "}";
    }

    @Override
    public int compareTo(State s){
        if(s.equals(this)) return 0;
        return this.label.compareTo(s.label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return this.label.equals(state.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }
}