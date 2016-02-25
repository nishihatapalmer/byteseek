# byteseek
byteseek is a Java library for efficiently matching patterns of bytes and searching for those patterns.  The main well-tested packages are:

####Matcher
* bytes - matchers (and inverted matchers) for bytes, ranges of bytes, sets, any byte, and bitmasks.
* sequence - matchers for sequences of bytes, byte matchers, fixed gaps and sequences of sequences.  

####Searcher
All the search algorithms have been extended to work with sequences which can match more than one byte at a given position.  Any sequence search algorithm can work with any sequence matcher, no matter how it is composed.  All the search implementations are stream-friendly - the length of an input source is not required unless you explicitly want to work at the end of an input source.  
* bytes - a naive searcher for byte matchers.
* sequence - various implementations of the naive search, Boyer-Moore-Horspool, Signed Horspool and Sunday QuickSearch algorithms.

####IO
* reader - readers for files, input streams, strings and byte arrays, and an adaptor from any reader back to an inputstream.  Readers cache the byte arrays read from the input sources using flexible caching strategies.
* reader/cache - pluggable caching strategies for readers, including least recently added, least recently used, temporary file caches, two level caches, double caches and others.

####Parser
* regex - a parser for a byte-oriented regular expression language, which produces a byteseek abstract syntax tree.

####Compiler
* matchers - compilers from the byteseek abstract syntax tree to byte matchers and sequence matchers.

##Untested
Various other packages exist which are not currently tested, but will become so eventually.  These include:

####Matcher
* multisequence - algorithms for multi-sequence matching, including lists and trie structures.

####Searcher
* multisequence - implementations of Set Horspool, Signed Set Horspool, Wu-Manber and Signed Wu-Manber algorithms.

####Compiler
* regex - produces full regular expressions as finite state automata from the byteseek abstract syntax tree.

Regular expressions are constructed as Glushkov finite state automata, rather than the more common Thompson construction.  However, the construction method has been adapted from the paper given below, which allows construction directly from the abstract syntax tree.

> "A reexamination of the Glushkov and Thompson Constructions", by Dora Giammarresi, Jean-Luc Ponty, Derick Wood, 1998.


####Automata
* Finite state automata with flexible transitions can be constructed. 
* Non deterministic automata can be converted into deterministic automata.
* Trie structures are provided from multi sequences. 
* Utilities allow for easily walking the automata and producing DOT files (graphviz) from them.

