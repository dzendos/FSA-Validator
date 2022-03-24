import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implements Fsa object.
 * 
 * @author  Evgeny Gerasimov
 * @version 1.0; 19.02.2022
 * @param   <S> the type of State elements
 * @param   <T> the type of Transition elements
 */
public class Fsa<S, T> {
    private int     size = 0;
    private int     numberOfTransitions = 0;
    
    private List<S> states   = new ArrayList<>();
    private List<T> alphabet = new ArrayList<>();

    private S       initialState = null;

    private List<S> finalStates  = new ArrayList<>();

    private HashMap<S, Integer> indexOfStates   = new HashMap<>();
    private T[][]               transitionTable = null;

    /**
     * Adds new transition into fsa.
     * 
     * @param transition to add in fsa
     * @throws ArrayIndexOutOfBoundsException for E5 error
     * @throws InterruptedException when warning appears
     * @throws RuntimeException when some error E1-E5 appears
     */
    @SuppressWarnings("unchecked")
    public void addTransition(Transition<S, T> transition) throws ArrayIndexOutOfBoundsException, InterruptedException, RuntimeException {
        if (!indexOfStates.containsKey(transition.from))
            throw new RuntimeException("Error:\nE1: A state '" + transition.from.toString() + "' is not in the set of states");
        if (!indexOfStates.containsKey(transition.to))
            throw new RuntimeException("Error:\nE1: A state '" + transition.to.toString()   + "' is not in the set of states");
        if (!alphabet.contains(transition.transition))
            throw new RuntimeException("Error:\nE3: A transition '" + transition.transition.toString() + "' is not represented in the alphabet");

        // Initializing transition table if we have not done it before (now we know an amount of all transitions and can initialize table)
        if (transitionTable == null)
            transitionTable = (T[][])(new String[size][size]);

        int from = indexOfStates.get(transition.from);
        int to   = indexOfStates.get(transition.to);

        if (transitionTable[from][to] == transition.transition)
            throw new RuntimeException("Such transition already exists");

        for (int i = 0; i < size; i++)
            if (transitionTable[from][i] == transition.transition)
                throw new InterruptedException("W3: FSA is nondeterministic");

        transitionTable[from][to] = transition.transition;

        numberOfTransitions++;
    }

    /**
     * Adds new state in fsa.
     * 
     * @param state to add
     * @throws RuntimeException if such state already exists
     */
    public void addState(S state) throws RuntimeException {
        if (indexOfStates.containsKey(state))
            throw new RuntimeException("State " + state.toString() + " already exists");

        indexOfStates.put(state, size++);
        states.add(state);
    }

    /**
     * Adds new final state.
     * 
     * @param state to add
     */
    public void addFinalState(S state) {
        if (!indexOfStates.containsKey(state))
            throw new RuntimeException("Error:\nE1: A state '" + state.toString() + "' is not in the set of states");
        
        finalStates.add(state);
    }

    /**
     * Sets initial state.
     * 
     * @param state to set as initial
     * @throws RuntimeException when there are no such state or initial state is already defined
     */
    public void setInitialState(S state) throws RuntimeException{
        if (!indexOfStates.containsKey(state))
            throw new RuntimeException("Error:\nE1: A state '" + state.toString() + "' is not in the set of states");

        if (initialState != null)
            throw new RuntimeException("Initial state is already defined");

        initialState = state;
    }

    /**
     * Adds new item in alphabet.
     * 
     * @param item to add in alphabet
     * @throws RuntimeException when such transition already exists
     */
    public void addItemInAlphabet(T item) throws RuntimeException {
        if (alphabet.contains(item))
            throw new RuntimeException("Alpha " + item.toString() + " already exists");

        alphabet.add(item);
    }

    /**
     * Returns index of state by state.
     * 
     * @param state - the element which index function tries to find
     * @return index of state by state
     */
    public int getIndexOfStates(S state) {
        return indexOfStates.get(state);
    }

    /**
     * Returns number of States in fsa.
     * 
     * @return number of States in fsa
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns number of Transitions in fsa.
     * 
     * @return number of Transitions in fsa
     */
    public int getNumberOfTransitions() {
        return numberOfTransitions;
    }

    /**
     * Returns initial State of an fsa.
     * 
     * @return initial State of an fsa
     */
    public S getInitialState() {
        return initialState; 
    }

    /**
     * Returns transition table of an fsa.
     * 
     * @return transition table of an fsa
     */
    public T[][] getTransitionTable() {
        return transitionTable;
    }

    /**
     * Returns how many transitions should be in complete fsa.
     * 
     * @return how many transitions should be in complete fsa
     */
    public int getCompleteAmountOfStates() {
        return states.size() * alphabet.size();
    }
}