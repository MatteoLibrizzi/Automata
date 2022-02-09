package automata;

import java.util.*;
import java.util.stream.Collectors;

interface Automaton {

    /** metodo che stampa tutti gli stati di un automa dopo averli ordinati per etichetta.
     * @param automaton l'automa sa stampare. */
    static void printAutomaton(Set<State> automaton){
        automaton.stream()
                .sorted()
                .forEach(System.out::println);
    }

    /** Metodo che produce l'automa reverse di un automa dato.
     * @param automaton l'automa di cui fare il reverse.
     * @return l'automa reverse.*/
    static Set<State> reverseAutomaton(Set<State> automaton){
        Map<State,State> vecchioNuovo=new HashMap<>();
        for(State s:automaton){
            State t=new State(s.label,s.terminal,s.initial);
            vecchioNuovo.put(s,t);
        }
        for(State q:vecchioNuovo.keySet()){
            State qn=vecchioNuovo.get(q);
            Map<Character,Set<State>> successori=q.successors;
            for(Character c:successori.keySet()){
                Set<State> arrivi=successori.get(c);
                for(State p:arrivi){
                    State pn=vecchioNuovo.get(p);
                    pn.setTransition(c,qn);
                }
            }
        }
        return new HashSet<>(vecchioNuovo.values());
    }

    /** Metodo che produce uno stato dell'automa ottenuto mediante subset construction a
     * partire da un TreeSet di stati che rappresenta un sottoinsieme dell'insieme degli stati
     * dell'automa di input. L'etichetta dello stato prodotto sar&agrave; la lista ordinata delle etichette degli stati
     * nel TreeSet passato a parametro. Ad esempio, se il parametro &egrave; costituito dall'insieme di stati
     * aventi etichette, rispettivamente, 1, 4 e 5, lo stato restituito avr&agrave; etichetta [1,4,5].
     * @param subset il sottoinsieme di stati dell'automa da determinizzare.
     * @return lo stato dell'automa prodotto dalla subset construction.
     * */
    static State convertFromSubset(TreeSet<State> subset,boolean iniziale){
        boolean finale=subset.stream()
                .anyMatch(s->s.terminal);
        String label=subset.stream()
                .map(s->s.label)
                .collect(Collectors.joining(",","[","]"));
        return new State(label,iniziale,finale);
    }

    /** Metodo che crea il DFA ottenuto dall'automa passato a parametro mediante
     * applicazione della subset construction.
     * Al suo interno, il metodo costruisce una mappa Map&lt;TreeSet&lt;State&gt;,State&gt; conversionMap che
     * associa a ogni sottoinsieme di stati dell'automa in input lo stato dell'automa
     * da restituire, mediante applicazione del metodo convertFromSubset.
     * Per costruire l'automa da restituire, si utilizza una pila LinkedList&lt;TreeSet&lt;State&gt;&gt; stack,
     * inizialmente contenente l'insieme di stati iniziali dell'automa di input. Una volta determinate le transizioni,
     * ogni sottoinsieme di stati raggiunto dallo stato corrente verr&egrave; aggiunto alla mappa Map&lt;TreeSet&lt;State&gt;,State&gt; conversionMap
     * e, se il suo attributo visited risulta false, verr&agrave; aggiunto alla pila LinkedList&lt;TreeSet&lt;State&gt;&gt; stack.
     * @param automaton l'automa in input.
     * @return l'automa deterministico ottenuto mediante subset construction.*/
    static Set<State> subsetConstructionOld(Set<State> automaton) {
        Map<TreeSet<State>,State> conversionMap=new HashMap<>();//usato per convertire il subset nello stato corrispondente
        LinkedList<TreeSet<State>> stack=new LinkedList<>();
        TreeSet<State> iniziali= automaton.stream()
                .filter(f->f.initial)
                .collect(Collectors.toCollection(TreeSet::new));
        stack.addLast(iniziali);
        State nuovoStato=convertFromSubset(iniziali,true);//deve esserci un unico stato iniziale, quello che contiene tutti gli stati iniziali
        conversionMap.put(iniziali,nuovoStato);
        while(!stack.isEmpty()) {
            TreeSet<State> vecchiStati = stack.removeLast();//stati attualmente esaminati, il corrispondente è quello a cui aggiungere le transizioni
            nuovoStato=conversionMap.get(vecchiStati);//cioè questo
            Map<Character,Set<State>> transizioniSingoliStati=new HashMap<>();//usata per memorizzare gli stati in cui si arriva con ogni simbolo
            for (State vecchioStato : vecchiStati) {//controllo i singoli stati
                Map<Character, Set<State>> transizioniVecchio = vecchioStato.successors;
                for (Character c : transizioniVecchio.keySet()) {//ed i singoli caratteri
                    Set<State> statiRaggiunti = transizioniVecchio.get(c);//stati raggiunti col simbolo dal particolare stato (parte del subset)
                    transizioniSingoliStati.putIfAbsent(c,new HashSet<>());
                    transizioniSingoliStati.get(c).addAll(statiRaggiunti);//aggiungo poi gli stati raggiunti da questo particolare stato, agli stati complessivamente raggiunti dalla subset
                }
            }
            //proseguo aggiungendo le transizioni del subset allo stato corrispondente (del nuovo automa)
            for(Character c: transizioniSingoliStati.keySet()){//itero sui caratteri
                TreeSet<State> states=new TreeSet<>(transizioniSingoliStati.get(c));//gli stati raggiunti con quel carattere vengono messi in un treeset
                //distinguo vari casi
                if(conversionMap.get(states)==null){//se lo stato corrispondente non è stato ancora creato
                    stack.addLast(states);//ovviamente devo creare le transizioni dal nuovo stato,quindi lo metto nello stack
                    State nuovo=convertFromSubset(states,false);//non deve essere iniziale
                    nuovoStato.setTransition(c,nuovo);//aggiungo la transizione al nuovo stato. So che andrò a modificare questo
                    conversionMap.put(states,nuovo);//perché lo metto in corrispondenza nella mappa, alle iterazioni successive accedere a lui
                }else{
                    if(!conversionMap.get(states).visited){//se esiste, e non è stato ancora visitato
                        stack.addLast(states);//devo creare le transizioni da lui uscenti, quindi lo metto nello stack
                        State nuovo=conversionMap.get(states);
                        nuovoStato.setTransition(c,nuovo);//aggiungo le transizioni, ottengo lo stato usato alle iterazioni precedenti con la mappa
                    }else{
                        nuovoStato.setTransition(c,conversionMap.get(states));//se lo stato è stato già visitato mi limito ad aggiungere la transizione
                    }
                }
            }
            nuovoStato.visited=true;//marco lo stato come visitato, le transizioni dovute sono state aggiunte, non dovrò più metterlo nello stack
        }
        return conversionMap.values().stream()
                .collect(Collectors.toSet());
    }

    static Set<State> subsetConstruction(Set<State> automaton) {
        Map<TreeSet<State>,State> conversionMap=new HashMap<>();//usato per convertire il subset nello stato corrispondente
        LinkedList<TreeSet<State>> stack=new LinkedList<>();
        TreeSet<State> iniziali= automaton.stream()
                .filter(f->f.initial)
                .collect(Collectors.toCollection(TreeSet::new));
        stack.addLast(iniziali);
        State nuovoStato=convertFromSubset(iniziali,true);//deve esserci un unico stato iniziale, quello che contiene tutti gli stati iniziali
        conversionMap.put(iniziali,nuovoStato);
        while(!stack.isEmpty()) {
            TreeSet<State> vecchiStati = stack.removeLast();//stati attualmente esaminati, il corrispondente è quello a cui aggiungere le transizioni
            nuovoStato=conversionMap.get(vecchiStati);//cioè questo
            Map<Character,Set<State>> transizioniSingoliStati=new HashMap<>();//usata per memorizzare gli stati in cui si arriva con ogni simbolo
            for(Character c:vecchiStati.stream()
                    .flatMap(state->state.successors.keySet().stream())
                    .collect(Collectors.toSet())){
                for(State vecchioStato:vecchiStati){
                    transizioniSingoliStati.putIfAbsent(c,new HashSet<>());
                    if(vecchioStato.successors.get(c)!=null)
                        transizioniSingoliStati.get(c).addAll(vecchioStato.successors.get(c));
                }
                TreeSet<State> vecchiStatiRaggiunti=new TreeSet<>(transizioniSingoliStati.get(c));
                if(conversionMap.get(vecchiStatiRaggiunti)==null){
                    State statoRaggiunto=convertFromSubset(vecchiStatiRaggiunti,false);
                    conversionMap.put(vecchiStatiRaggiunti,statoRaggiunto);
                }
                if(!conversionMap.get(vecchiStatiRaggiunti).visited){
                    stack.addLast(vecchiStatiRaggiunti);
                    conversionMap.get(vecchiStatiRaggiunti).visited=true;
                }
                State statoRaggiunto=conversionMap.get(vecchiStatiRaggiunti);
                nuovoStato.setTransition(c,statoRaggiunto);
            }
        }
        return conversionMap.values().stream()
                .collect(Collectors.toSet());
    }

    /** Metodo che crea il DFA minimale ottenuto dall'automa passato a parametro mediante
     * applicazione dell'algoritmo di Brzozowski.
     * @param automaton l'automa, deterministico o non deterministico, in input.
     * @return l'automa minimale ottenuto mediante l'algoritmo di Brzozowski.*/
    static Set<State> brzozowski (Set<State> automaton){
        return subsetConstruction(reverseAutomaton(subsetConstruction(reverseAutomaton(automaton))));    }
}