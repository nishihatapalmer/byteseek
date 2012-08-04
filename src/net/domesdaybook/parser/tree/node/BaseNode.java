package net.domesdaybook.parser.tree.node;

import java.util.Collections;
import java.util.List;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;
import net.domesdaybook.parser.tree.ParseTreeType;


public class BaseNode implements ParseTree {

  private final ParseTreeType type;
  private final boolean inverted; 
  
  
  public BaseNode(final ParseTreeType type) {
    this(type, false);
  }
  
  public BaseNode(final ParseTreeType type, 
                      final boolean inverted) {
    this.type = type;
    this.inverted = inverted;
  }
  
  @Override
  public ParseTreeType getParseTreeType() {
    return type;
  }

  @Override
  public byte getByteValue() throws ParseException {
    throw new ParseException("No byte value is available.");
  }

  @Override
  public int getIntValue() throws ParseException {
    throw new ParseException("No int value is available.");
  }

  @Override
  public String getTextValue() throws ParseException {
    throw new ParseException("No text value is available.");
  }

  @Override
  public boolean isValueInverted() throws ParseException {
    return inverted;
  }
  
  @Override
  public List<ParseTree> getChildren() {
    return Collections.emptyList();
  }

}
