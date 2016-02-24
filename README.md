# byteseek
A Java library for byte pattern matching and searching

byteseek is a Java library for efficiently matching patterns of bytes and searching for those patterns, using a random access interface over any input source.  A regular-expression like syntax can be parsed and compiled into various types of optimised matcher. 

All the provided match and search implementations are stream-friendly - the length of an input source is not required unless you explicitly want to work at the end of an input source.
