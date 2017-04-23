
<img align="left" src="images/byteseek_logo_64x63.png"> 

# byteseek

byteseek is a Java library for efficiently matching patterns of bytes and searching for those patterns.  Source code can be found at Github in the [byteseek repository](https://github.com/nishihatapalmer/byteseek). Published releases of byteseek are also available on [maven central](https://search.maven.org/#search|ga|1|byteseek).  The main well-tested packages are:

#### Matcher

A package which contains various types of matcher for individual bytes or sequences of them.
* bytes - matchers (and inverted matchers) for bytes, ranges of bytes, sets, any byte, and bitmasks.
* sequence - matchers for sequences of bytes, byte matchers, fixed gaps and sequences of sequences.  

#### Searcher

A package which contains implementations of various search algorithms.  Most of them are sub-linear, which means they don't have to examine every position in an input source to find all possible matches.  All the search algorithms have been extended to work with sequences which can match more than one byte at a given position.  Any sequence search algorithm can work with any sequence matcher, no matter how it is composed.  All the search implementations are stream-friendly - the length of an input source is not required unless you explicitly want to work at the end of an input source.  

* bytes - a naive searcher for byte matchers.
* matcher - a naive searcher for any matcher.
* sequence - various implementations of the naive search, Boyer-Moore-Horspool, Signed Horspool and Sunday QuickSearch algorithms.

#### IO

Matchers and searchers can all work over byte arrays directly.  In order to read efficiently from any other input source,
readers provide a consistent random-access interface over files, input streams, strings and byte arrays.  Pluggable caching strategies allow tailoring the memory and performance for different use cases.

This package may be generally useful, independent of byteseek matching and searching.

* reader - readers for files, input streams, strings and byte arrays, and an adapter from any reader back to an InputStream.  Readers cache the byte arrays read from the input sources using flexible caching strategies.
* reader/cache - pluggable caching strategies for readers, including least recently added, least recently used, temporary file caches, two level caches, double caches and others.

#### Parser
A byte-oriented regular expression language is given to allow the easy construction of byte matchers, sequence matchers, and (eventually) finite state automata.  An abstract syntax tree is defined, so other regular expression syntaxes could be used if required.
* regex - a parser for a byte-oriented regular expression language, which produces a byteseek abstract syntax tree.

#### Compiler
A package which contains compilers for all of the matchers from an abstract syntax tree.
* matchers - compilers from the byteseek abstract syntax tree to byte matchers and sequence matchers.

## Untested
Various other packages exist which are not currently tested, but will become so eventually.  These include:

#### Matcher
* multisequence - algorithms for multi-sequence matching, including lists and trie structures.
* automata - matchers for non deterministic and deterministic automata.

#### Searcher
* multisequence - implementations of Set Horspool, Signed Set Horspool, Wu-Manber and Signed Wu-Manber algorithms.

#### Compiler
* regex - produces full regular expressions as finite state automata from the byteseek abstract syntax tree.

Regular expressions are constructed as Glushkov finite state automata, rather than the more common [Thompson construction](https://en.wikipedia.org/wiki/Thompson's_construction).  Glushkov automata are generally more compact and have no empty transitions, which can improve performance and makes them simpler to work with.

The [normal construction for Glushkov automata](https://en.wikipedia.org/wiki/Glushkov's_construction_algorithm) involves a somewhat complex and recursive analysis stage.  In byteseek, we construct a Glushkov automata directly from the abstract syntax tree, similarly to the Thompson construction but avoiding the need for any empty transitions. It has been adapted and extended from the method given in the paper below:

> "[A reexamination of the Glushkov and Thompson Constructions](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.50.5883&rank=1)", by Dora Giammarresi, Jean-Luc Ponty, Derick Wood, 1998.

#### Automata
* Finite state automata with flexible transitions can be constructed. 
* Non deterministic automata can be converted into deterministic automata.
* Trie structures are provided from multi sequences. 
* Utilities allow for easily walking the automata and producing DOT files (graphviz) from them.

