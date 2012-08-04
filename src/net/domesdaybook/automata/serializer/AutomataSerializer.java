package net.domesdaybook.automata.serializer;

import net.domesdaybook.automata.Automata;

public interface AutomataSerializer<T, S> {
  
  public String serialize(Automata<T> automata, S additionalInfo);
  
}
