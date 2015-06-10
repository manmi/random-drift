# random-drift
Random-drift is a semantic classification library based on random projection and wavelets. The primary purpose of this library is to provide for incremental indexing in context of semantic search/classification that other approaches like Latent Semantic Analysis implementations do not provide.

Only classification is supported, search as of now although feasible, does not scale as each query will require a full index scan.

This is supposed to be a simplistic implementation geared towards scalability. For a more comprehensive realization please look at semanticvectors (https://code.google.com/p/semanticvectors/).

The current implementation is a random vector index over a lucene (using version 3.6.2) index.

Vector classification has following shortcomings:

- Number of positives required for training a category or class goes up with increasing number of categories/classes.
- Training of categories goes stale with changing content, for example, as with news content. The documents used for training a category today might go out of sync a few weeks down the line with respect to actual occurrence of terms in the same context

Value of semantic classification systems primarily depends on providing a solution to above limitations of vector classification systems
