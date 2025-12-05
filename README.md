The following serves as a documentation primer for the GE (Graph Embeddings) library. It is intended to allow for use of a computer-aided approach for finding and characterizing graph embeddings, a subtopic in topological graph theory. 

The curious reader can refer to the technical report included in the repository (it will be uploaded shortly) for more of a theoretical background, or to Gross and Tucker's monograph on topological graph theory, which serves as an 
excellent starting point. 

This codebase is primarily written in Java and has extensive comments (some might say an excessive amount, but to that end, the author pleads his case that the comments were helping him with clarity for defining operations, since it turns out that
computationalizing well-defined mathematical operations is still very finicky and detail oriented when making the procedures concrete with data structures and algorithm implementing) which hopefully will help the newcomer.

The main driving file is "Tester.java", which calls other files as needed. The Graph and Embedding classes form the core of the library insofar as the object-oriented paradigm is concerned.  The Embedding class should be initialized with the 
structures that define an embedding - the 2d Array of integers which defines a graph's nodes and neighbor lists, and an array (or roughly equivalent structure) of its 'type 1' edges (edges which contain a half twist). This class also contains such
helpful functions as flipNode, cleanNode, doubleCover (for face tracing on nonorientable embeddings) and so on.

The Graph class contains utilities for graphs, such as manipulating the order of a node's cyclic permutation of neighbors. It also houses one of the central functions, the 'raw' diamond sum for constructing embeddings from two input embeddings. 

The Facetracer class primarily serves as a housing for the various methods around the implementation of Heffter-Edmonds face tracing, such as the getFaces method (which traces the actual faces), a checker for face-simplicity, and a few other quality of life
functions.

FormatConverter serves as a useful way of turning text-formatted embeddings into hard-coded Java embeddings which are immediately useful in running code.

BaseCases.java holds hard coded base cases for the face-simple minimal quadrangulations and nonorientable genera results found in the course of the research project for which this codebase was originally written.

Constructors.java holds useful automated scripts for iteratively building up larger and larger graph embeddings (i.e. graph embeddings with with arbitrary number of nodes) that proved useful in the inductive proof approach which was taken
for the theoretical results proven in the research process. 

All other files mentioned are either evident in their support structure (they serve as "helpers" for other parts to work properly, or are deprecated preceding a possible refactor). 

Please feel free to reach out with any further questions or clarifications.


Best Regards,
-Warren
December 2023

