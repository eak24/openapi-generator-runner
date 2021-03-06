package org.catalpacourt.openapi;

import org.catalpacourt.openapi.commandlinerunner.ProcessNode;

import java.io.File;
import java.util.*;

/**
 * The SequentialProcessor orders nodes such that all dependents are before any given node.
 */
public class SequentialProcessor {
    public List<ProcessBuilder> linearizeAndConvertToProcessBuilders(ProcessNode graph) {
        Map<ProcessNode, Set<ProcessNode>> nodeToDependencies = new HashMap<>();
        graph.forEach(p -> {
            p.dependents().forEach(n -> {
                if (n == null) {
                    return;
                }
                if (!nodeToDependencies.containsKey(n)) {
                    nodeToDependencies.put(n, new HashSet<>());
                }
                nodeToDependencies.get(n).add(p);
            });
        });

        List<ProcessNode> nodes = new ArrayList<>();
        nodes.add(graph);
        Set<ProcessNode> nodesToProcess = graph.dependents();

        while (!nodesToProcess.isEmpty()) {
            Set<ProcessNode> nodesToAdd = new HashSet<>();
            for (ProcessNode node : nodesToProcess) {
                if (nodes.containsAll(nodeToDependencies.get(node))) {
                    nodesToAdd.add(node);
                }
            }
            if (nodesToAdd.isEmpty()) {
                throw new IllegalStateException("Cycle detected in process graph.");
            } else {
                nodes.addAll(nodesToAdd);
                nodesToProcess.removeAll(nodesToAdd);
            }
        }
        List<ProcessBuilder> processBuilders = new ArrayList<>();
        File currentDirectory = new File(".");
        for (ProcessNode node : nodes) {
            ProcessBuilder processBuilder = node.getProcessBuilder();
            if (processBuilder.directory() != null) {
                currentDirectory = processBuilder.directory();
            } else {
                processBuilder.directory(currentDirectory);
            }
            if (processBuilder.command().isEmpty()) {
                continue;
            }
            processBuilders.add(processBuilder);
        }
        return processBuilders;
    }
}
