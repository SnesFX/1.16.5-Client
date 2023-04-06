/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.pathfinder;

import net.minecraft.world.level.pathfinder.Node;

public class BinaryHeap {
    private Node[] heap = new Node[128];
    private int size;

    public Node insert(Node node) {
        if (node.heapIdx >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.size == this.heap.length) {
            Node[] arrnode = new Node[this.size << 1];
            System.arraycopy(this.heap, 0, arrnode, 0, this.size);
            this.heap = arrnode;
        }
        this.heap[this.size] = node;
        node.heapIdx = this.size;
        this.upHeap(this.size++);
        return node;
    }

    public void clear() {
        this.size = 0;
    }

    public Node pop() {
        Node node = this.heap[0];
        this.heap[0] = this.heap[--this.size];
        this.heap[this.size] = null;
        if (this.size > 0) {
            this.downHeap(0);
        }
        node.heapIdx = -1;
        return node;
    }

    public void changeCost(Node node, float f) {
        float f2 = node.f;
        node.f = f;
        if (f < f2) {
            this.upHeap(node.heapIdx);
        } else {
            this.downHeap(node.heapIdx);
        }
    }

    private void upHeap(int n) {
        Node node = this.heap[n];
        float f = node.f;
        while (n > 0) {
            int n2 = n - 1 >> 1;
            Node node2 = this.heap[n2];
            if (!(f < node2.f)) break;
            this.heap[n] = node2;
            node2.heapIdx = n;
            n = n2;
        }
        this.heap[n] = node;
        node.heapIdx = n;
    }

    private void downHeap(int n) {
        Node node = this.heap[n];
        float f = node.f;
        do {
            float f2;
            Node node2;
            int n2 = 1 + (n << 1);
            int n3 = n2 + 1;
            if (n2 >= this.size) break;
            Node node3 = this.heap[n2];
            float f3 = node3.f;
            if (n3 >= this.size) {
                node2 = null;
                f2 = Float.POSITIVE_INFINITY;
            } else {
                node2 = this.heap[n3];
                f2 = node2.f;
            }
            if (f3 < f2) {
                if (!(f3 < f)) break;
                this.heap[n] = node3;
                node3.heapIdx = n;
                n = n2;
                continue;
            }
            if (!(f2 < f)) break;
            this.heap[n] = node2;
            node2.heapIdx = n;
            n = n3;
        } while (true);
        this.heap[n] = node;
        node.heapIdx = n;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}

