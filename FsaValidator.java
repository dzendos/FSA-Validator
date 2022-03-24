import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 * Implements validator for fsa objects.
 * 
 * @author  Evgeny Gerasimov
 * @version 1.0; 19.02.2022
 */
public class FsaValidator {
    private static final String inputFile  = "fsa.txt";    // Input and Output files for CodeTest
    private static final String outputFile = "result.txt";

    private Fsa<String, String> fsa;

    private List<String> warnings = new ArrayList<>();

    private boolean[] isInSet = null;

    public static void main(String[] args) throws Exception {
        Fsa<String, String> fsa = new Fsa<>();

        new FsaValidator(fsa).run();
    }

    /**
     * Initialize new FsaValidator for given fsa.
     * 
     * @param fsa to validate 
     */
    public FsaValidator(Fsa<String, String> fsa) {
        this.fsa = fsa;
    }

    /**
     * Inserts values in fsa.
     * Validate current fsa.
     * Output the result of validating.
     * 
     * @throws IOException
     */
    private void run() throws IOException {
        Scanner    scanner = new Scanner(new File(inputFile));
        FileWriter writer  = new FileWriter(new File(outputFile));

        try {
            for (int i = 0; i < 5; i++)
                input(scanner);
            check();
            output(writer);
        } catch (ArrayIndexOutOfBoundsException ex) {
            writer.write("Error:\nE5: Input file is malformed");
        } catch (RuntimeException ex) {
            writer.write(ex.getMessage());
        } catch (InterruptedException ex) {
            saveWarning(ex.getMessage());
        } finally {
            scanner.close();
            writer.close();
        }
    }

    /**
     * Reads command and pasts value in fsa.
     * 
     * @param scanner which reads values from some input stream
     * @throws RuntimeException for exceptions E1-E5
     * @throws ArrayIndexOutOfBoundsException for exception E5
     * @throws InterruptedException for warnings
     */
    private void input(Scanner scanner) throws RuntimeException, ArrayIndexOutOfBoundsException, InterruptedException {
        String   commandLine;
        String[] params;

        commandLine = scanner.nextLine();

        if (!commandLine.contains("=") || commandLine.length() == 0 || commandLine.charAt(0) == '[')
            throw new RuntimeException("Error:\nE5: Input file is malformed");

        String command = commandLine.substring(0, commandLine.indexOf("="));

        if (!commandLine.startsWith(command + "=[") || ! commandLine.endsWith("]"))
            throw new RuntimeException("Error:\nE5: Input file is malformed");

        params = getParams(commandLine);

        switch (command) 
        {
        case "states":
            if (params[0].equals(""))
                throw new RuntimeException("States array is empty");

            for (String param : params)
                fsa.addState(param);
            break;

        case "alpha":
            if (params[0].equals(""))
                throw new RuntimeException("Alphabet array is empty");

            for (String param : params)
                fsa.addItemInAlphabet(param);
            break;

        case "init.st":
            if (params[0].equals(""))
                throw new RuntimeException("Error:\nE4: Initial state is not defined");

            fsa.setInitialState(params[0]);
            break;

        case "fin.st":
            if (params[0].equals("")) {
                saveWarning("W1: Accepting state is not defined");
                break;
            }

            for (String param : params)
                fsa.addFinalState(param);

            break;

        case "trans":
            if (isInSet == null) {
                isInSet = new boolean[fsa.getSize()];
            }

            for (String param : params) {
                String[] transition = param.split(">");
                if (transition.length != 3)
                    throw new RuntimeException("Error:\nE5: Input file is malformed");

                fsa.addTransition(new Transition<String, String>(transition[0], transition[2], transition[1]));

                if (fsa.getNumberOfTransitions() == 1) {
                    isInSet[fsa.getIndexOfStates(transition[0])] = isInSet[fsa.getIndexOfStates(transition[2])] = true;
                }

                if (isInSet[fsa.getIndexOfStates(transition[0])] == false && isInSet[fsa.getIndexOfStates(transition[2])] == false) {
                    throw new RuntimeException("Error:\nE2: Some states are disjoint");
                } else {
                    isInSet[fsa.getIndexOfStates(transition[0])] = isInSet[fsa.getIndexOfStates(transition[2])] = true;
                }
            }

            break;
        }
    }

    /**
     * Checks whether all states are reachable from the initial one.
     */
    private void check() {
        String initState = fsa.getInitialState();

        Queue<Integer> queue = new LinkedList<>();

        final String[][] transitionTable = (fsa.getTransitionTable());

        boolean[] visited = new boolean[fsa.getSize()];

        queue.add(fsa.getIndexOfStates(initState));
        visited[fsa.getIndexOfStates(initState)] = true;

        while (!queue.isEmpty()) {
            Integer curState = queue.poll();

            for (int i = 0; i < fsa.getSize(); i++) {
                if (transitionTable[curState][i] == null)
                    continue;
                if (!visited[i]) {
                    queue.add(i);
                    visited[i] = true;
                }
            }
        }

        for (int i = 0; i < visited.length; i++) {
            if (visited[i] == false) {
                saveWarning("W2: Some states are not reachable from the initial state");
                break;
            }
        }
    }

    /**
     * Outputs the result of validating.
     * 
     * @param writer with output stream
     * @throws IOException
     */
    private void output(FileWriter writer) throws IOException {
        boolean isComplete = (fsa.getCompleteAmountOfStates() == fsa.getNumberOfTransitions());

        String message = (isComplete ? "FSA is complete" : "FSA is incomplete");
        writer.write(message + "\n");

        Collections.sort(warnings);
        if (warnings.size() != 0)
            writer.write("Warning:\n");

        for (int i = 0; i < warnings.size(); i++) {
            writer.write(warnings.get(i) + "\n");
        }
    }

    /**
     * Returns an array of params of the command.
     * 
     * @param params of the command
     * @return an array of params of the command
     * @throws ArrayIndexOutOfBoundsException
     */
    private String[] getParams(String params) throws ArrayIndexOutOfBoundsException{
        return params.substring(params.indexOf("[") + 1, params.length() - 1).split(",");
    }

    /**
     * Saves warnings that appears during runtime.
     * 
     * @param warning to save
     */
    private void saveWarning(String warning) {
        warnings.add(warning);
    }
}
