package net.domesdaybook.parser.tree.node;

import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTreeType;


public final class ByteNode extends BaseNode {

  private byte value;

  public ByteNode() {
    this((byte)0, false);
  }
  
  public ByteNode(final byte value) {
    this(value, false);
  }

  public ByteNode(final byte value,
                  final boolean inverted) {
    super(ParseTreeType.BYTE, inverted);
    this.value = value;
  }

  @Override
  public byte getByteValue() throws ParseException {
    return value;
  }
  
  public void setByteValue(final byte value) {
    this.value = value;
  }

}
