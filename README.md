# Description
A solution for https://priceonomics.com/jobs/puzzle/. 

> Current version of algorithm does not find all arbitrage opportunities - it only checks if there are any, and list some of them.

# Algorithm

A modified version of [Bellman-Ford algorithm](https://en.wikipedia.org/wiki/Bellman%E2%80%93Ford_algorithm) is used to find arbitrage opportunities. 
In general Bellman-Ford's algorithm can be used for two things: 
* find the best bath between two nodes in a weighted graph. 
* detect if there is a negative cycle in a graph

For us a negative loop is a sign, that there is an arbitrage opportunity. 
The modified version of algorithm not only detects if there is a negative cycle, but also finds it (by retracing the predecessors of vertices modified in the last step of algorithm).

> NOTE: I'm fully aware, that this algorithm does not find all arbitrage opportunities, but I think this is the right place to start discussion:).  
> If finding all arbitrage opportunities is a must, it is possible to 
> * implement dfs algorithm to find all simple cycles in a graph and check if there is arbitrage opportunity for them - [Only for small number of exchange pairs]
> * use [Donald B. Johnson algorithm](https://www.cs.tufts.edu/comp/150GA/homeworks/hw1/Johnson%2075.PDF) to find all elementary cycles in graph and check arbitrage opportunities for them [For larger number exchange pairs] 

### Complexity

- Time complexity `O(V*E)` - for very dense graphs (number of currency exchanges (`E`) is close to number of currencies (`V`), it could be close to `O(V^3)`)
- Time complexity (best case) `O(E)` - if there is no negative cycle, and Bellman Ford Algorithm stops after first iteration (no additional changes)
- Space complexity `O(V)`
  
where `V` is number of vertices (number of currencies in our case) and `E` is a number of edges (number of exchanges between currencies)

### Usage
Run app:
```
amm app.sc
```

Run tests:
```
amm app.test.sc
```