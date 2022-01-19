package org.catalpacourt.openapi.commandlinerunner;

import java.io.File;
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

    public ProcessNode(File location) {
        this();
        processBuilder.directory(location);
    }

    public ProcessNode next(ProcessNode... nodes) {
        List<ProcessNode> nodesList = Arrays.asList(nodes);
        next(nodesList);
        return nodesList.get(nodesList.size() - 1);
    }

    public ProcessNode next(List<ProcessNode> nodes) {
        ProcessNode previous = this;
        for (ProcessNode next : nodes) {
            previous.next.add(next);
            previous = next;
        }
        return previous;
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

    public ProcessNode afterLast(ProcessNode node) {
        this.last().addNext(node);
        return node;
    }

    public ProcessNode last() {
        ProcessNode lastNode = this;
        while (!lastNode.getNext().isEmpty()) {
            lastNode = lastNode.getNext().iterator().next();
        }
        return lastNode;
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
