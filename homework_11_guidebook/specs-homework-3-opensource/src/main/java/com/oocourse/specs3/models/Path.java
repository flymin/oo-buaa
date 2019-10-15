package com.oocourse.specs3.models;

public interface Path extends Iterable<Integer>, Comparable<Path> {
    // Iterable<Integer>和Comparable<Path>接口的规格请参阅JDK
    //@ public instance model non_null int[] nodes;

    //@ ensures \result == nodes.length;
    public /*@pure@*/int size();

    /*@ requires index >= 0 && index < size();
      @ assignable \nothing;
      @ ensures \result == nodes[index];
      @*/
    public /*@pure@*/ int getNode(int index);

    //@ ensures \result == (\exists int i; 0 <= i && i < nodes.length; nodes[i] == node);
    public /*@pure@*/ boolean containsNode(int node);

    /*@ ensures \result == (\exists int i; 0 <= i && i < nodes.length - 1;
      @             (nodes[i] == fromNodeId && nodes[i + 1] == toNodeId) ||
      @             (nodes[i + 1] == fromNodeId && nodes[i] == toNodeId));
      @*/
    //It is not required to implement this interface, just for constructing the JML spec.
    public /*@pure@*/ boolean containsEdge(int fromNodeId, int toNodeId);

    /*@ normal_behavior
      @ requires containsNode(fromNodeId) && containsNode(toNodeId) && (\exists int i, j; 0 <= i && i < j && j < nodes.length; nodes[i] == fromNodeId && nodes[j] == toNodeId); 
      @ ensures  (fromNodeId != toNodeId) ==>
      @ (\exists int[] snodes; snodes[0] == fromNodeId && snodes[snodes.length - 1] == toNodeId && (\forall int i; 0 <= i && i < snodes.length - 1; containsEdge(snodes[i], snodes[i + 1]));
      @ (\forall int[] cnodes; cnodes[0] == fromNodeId && cnodes[cnodes.length - 1] == toNodeId && (\forall int j; 0 <= j && j < cnodes.length - 1; containsEdge(cnodes[j], cnodes[j + 1]));
      @                   snodes.length <= cnodes.length) && \result == snodes.length);
      @ ensures (fromNodeId == toNodeId) ==> \result == 0;
      @ also
      @ exceptional_behavior
      @ signals (NodeIdNotFoundException e) !containsNode(fromNodeId) || !containsNode(toNodeId);
      @ signals (NodeNotConnectedException e) !(\exists int i, j; 0 <= i && i < j && j < nodes.length; nodes[i] == fromNodeId && nodes[j] == toNodeId);
      @*/
    //It is not required to implement this interface, just for constructing the JML spec.
    public /*@pure@*/ int getShortestPathLength(int fromNodeId, int toNodeId) throws NodeIdNotFoundException, NodeNotConnectedException;

    /*@ ensures (\exists int[] arr; (\forall int i, j; 0 <= i && i < j && j < arr.length; arr[i] != arr[j]);
      @             (\forall int i; 0 <= i && i < arr.length;this.containsNode(arr[i]))
      @           && (\forall int node; this.containsNode(node); (\exists int j; 0 <= j && j < arr.length; arr[j] == node))
      @           && (\result == arr.length));
      @*/
    public /*pure*/ int getDistinctNodeCount();

    /*@ also
      @ public normal_behavior
      @ requires obj != null && obj instanceof Path;
      @ assignable \nothing;
      @ ensures \result == (((Path) obj).nodes.length == nodes.length) &&
      @                      (\forall int i; 0 <= i && i < nodes.length; nodes[i] == ((Path) obj).nodes[i]);
      @ also
      @ public normal_behavior
      @ requires obj == null || !(obj instanceof Path);
      @ assignable \nothing;
      @ ensures \result == false;
      @*/
    public boolean equals(Object obj);

    //@ ensures \result == (nodes.length >= 2);
    public /*@pure@*/ boolean isValid();

    //@ ensures containsNode(nodeId) ==> \result == Math.pow(4, (nodeId % 5 + 5) % 5);
    //@ ensures !containsNode(nodeId) ==> \result == 0;
    public /*@pure@*/ int getUnpleasantValue(int nodeId);
}