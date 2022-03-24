/**
 * Implements Transition class for Fsa.
 * 
 * @author  Evgeny Gerasimov
 * @version 1.0; 19.02.2022
 * @param   <S> the type of State elements
 * @param   <T> the type of Transition elements
 */
public class Transition <S, T> {
    public S from;
    public S to;

    public T transition;

    /**
     * 
     * @param from - the beginning of movement
     * @param to   - destination point
     * @param transition - which command do this transition
     */
    public Transition (S from, S to, T transition) {
        this.from       = from;
        this.to         = to;
        this.transition = transition;
    }
}
