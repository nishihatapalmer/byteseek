/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.expression.compiler.multisequence;
/*
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.expression.compiler.Compiler;
import net.domesdaybook.expression.parser.ParseException;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
*/

/**
 *
 * @author matt
 */
/*
public class SequenceMatcherTrieCompiler implements Compiler<DfaAssociatedState<SequenceMatcher>, List<SequenceMatcher>> {

    
    @Override
    public DfaAssociatedState<SequenceMatcher> compile(List<SequenceMatcher> sequences) throws ParseException {
        DfaAssociatedState<SequenceMatcher> initialState = new DfaAssociatedState<SequenceMatcher>();
        for (SequenceMatcher sequence : sequences) {
            addSequence(sequence, initialState);
        }
        return initialState;
    }

    
    private void addSequence(SequenceMatcher sequence, DfaAssociatedState initialState) {
        DfaAssociatedState currentState = initialState;
        for (int position = 0; position < sequence.length(); position++) {
            final SequenceMatcher associatedSequence = position == sequence.length() -1? sequence : null;
            SingleByteMatcher byteMatcher = sequence.getByteMatcherForPosition(position);
            byte[] transitionBytes = byteMatcher.getMatchingBytes();
            Map<Byte, State> overlapWithExistingTransitions = getOverlappingTransitions(currentState, transitionBytes);
            if (overlapWithExistingTransitions.size() > 0) {
                
            } else {
                currentState = createTransitionToNewState(currentState, transitionBytes, associatedSequence);
            }
        }
    }


    private DfaAssociatedState<SequenceMatcher> createTransitionToNewState(DfaAssociatedState<SequenceMatcher> currentState, byte[] transitionBytes, SequenceMatcher associatedSequence) {
        DfaAssociatedState<SequenceMatcher> newState = new DfaAssociatedState<SequenceMatcher>();

        

        return newState;
    }

    
    private Map<Byte, State> getOverlappingTransitions(DfaAssociatedState<SequenceMatcher> currentState, byte[] transitionBytes) {
        final Map<Byte, State> overlaps = new HashMap<Byte, State>();
        for (int byteNumber = 0; byteNumber < transitionBytes.length; byteNumber++) {
            final byte theByte = transitionBytes[byteNumber];
            for (Transition transition : currentState.getTransitions()) {
                State toState = transition.getStateForByte(theByte);
                if (toState != null) {
                    overlaps.put(theByte, toState);
                }
            }
        }
        return overlaps;
    }

}
*/
