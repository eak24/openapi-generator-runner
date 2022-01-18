package org.catalpacourt.openapi.commandlinerunner;

import java.util.*;
import java.util.function.Consumer;

public class ProcessNode {
    private final ProcessBuilder processBuilder;
    private final Collection<ProcessNode> next;

    public ProcessNode(List<String> commands, Set<ProcessNode> next) {
        this.processBuilder = new ProcessBuilder().command(commands);
        this.next = next;
    }

    public ProcessNode(List<String> commands) {
        this(commands, new HashSet<>());
    }

    public ProcessNode(String command) {
        this(Arrays.asList(command.split(" ")));
    }

    public ProcessNode() {
        this(new ArrayList<>());
    }

    public Collection<ProcessNode> getNext() {
        return next;
    }

    public ProcessBuilder getProcessBuilder() {
        return processBuilder;
    }

    public void addNext(ProcessNode node) {
        this.next.add(node);
    }

    public void afterLast(ProcessNode node) {
        while (!node.getNext().isEmpty()) {
            node = node.getNext().iterator().next();
        }
        node.addNext(node);
    }

    /**
     * Allows chaining like node.next(first).next(second).next(third).
     *
     * @param node
     * @return
     */
    public ProcessNode next(ProcessNode node) {
        this.next.add(node);
        return node;
    }

    public Set<ProcessNode> dependents() {
        Set<ProcessNode> result = new HashSet<>();
        next.forEach(n -> {
            result.add(n);
            result.addAll(n.dependents());
        });
        return result;
    }

    public void forEach(Consumer<ProcessNode> consumer) {
        consumer.accept(this);
        next.forEach((n) -> n.forEach(consumer));
    }

    @Override
    public String toString() {
        return String.join(" ", processBuilder.command());
    }
}
