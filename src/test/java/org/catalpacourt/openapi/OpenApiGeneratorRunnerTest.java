package org.catalpacourt.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.catalpacourt.openapi.commandlinerunner.ProcessNode;
import org.catalpacourt.openapi.schema.Extension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.catalpacourt.openapi.schema.Extension.X_OPENAPI_GENERATOR_RUNNER;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Assure correct defaults are added.
 */
public class OpenApiGeneratorRunnerTest {

    private static final File TEST_RESOURCES = new File("src/test/resources/org/catalpacourt/openapi");
    private static final File SAMPLES_DIRECTORY = new File(TEST_RESOURCES, "samples");
    private static final File SAMPLE_PROCESS_GRAPHS = new File(TEST_RESOURCES, "processgraphs");
    private static final File SAMPLE_COMMANDS = new File(TEST_RESOURCES, "sequentialcommands");
    private static final Collection<String> SAMPLES;

    static {
        try {
            SAMPLES = Files.walk(SAMPLES_DIRECTORY.toPath()).filter(Files::isRegularFile).map(p -> p.getFileName().toString()).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to find samples", e);
        }
    }

    private static Collection<String> getSamples() {
        return SAMPLES;
    }

    @ParameterizedTest
    @MethodSource("getSamples")
    public void shouldSerializeAndDeserializeRoundTrip(String sample) throws IOException {
        File file = new File(SAMPLES_DIRECTORY, sample);
        ObjectMapper mapper = MapperWrapper.getMapper(file.getName());
        Extension extension = readExtension(file);
        String roundTripResult = mapper.writeValueAsString(extension);
        assertThat(mapper.readValue(roundTripResult, Extension.class)).usingRecursiveComparison().isEqualTo(extension);
    }

    private Extension readExtension(File file) throws IOException {
        ObjectMapper mapper = MapperWrapper.getMapper(file.getName());
        JsonNode node = mapper.readValue(file, JsonNode.class);
        return mapper.treeToValue(node.findValue(X_OPENAPI_GENERATOR_RUNNER), Extension.class);
    }

    private ProcessNode createProcessGraph(File spec) throws IOException {
        OpenApiGeneratorRunner openApiGeneratorRunner = new OpenApiGeneratorRunner();
        openApiGeneratorRunner.setSpec(spec);
        return openApiGeneratorRunner.createProcessGraph();
    }

    private List<ProcessBuilder> getSequentialCommands(File spec) throws IOException {
        SequentialProcessor sequentialProcessor = new SequentialProcessor();
        return sequentialProcessor.linearizeAndConvertToProcessBuilders(createProcessGraph(spec));
    }

    private void writeValue(File file, Object object) throws IOException {
        MapperWrapper.getMapper(file.getAbsolutePath()).writeValue(file, object);
    }

    private void runSpec(File spec) throws Exception {
        OpenApiGeneratorRunner openApiGeneratorRunner = new OpenApiGeneratorRunner(false);
        openApiGeneratorRunner.setSpec(spec);
        assertEquals(0, openApiGeneratorRunner.call());
    }

    @ParameterizedTest
    @MethodSource("getSamples")
    public void shouldCheckProcessGraphSamples(String file) throws IOException {
        ProcessNode node = createProcessGraph(new File(SAMPLES_DIRECTORY, file));
        writeValue(new File(SAMPLE_PROCESS_GRAPHS, file), node);
    }

    @ParameterizedTest
    @MethodSource("getSamples")
    public void shouldCheckSequentialCommands(String file) throws IOException {
        writeValue(new File(SAMPLE_COMMANDS, file), getSequentialCommands(new File(SAMPLES_DIRECTORY, file)));
    }

    @ParameterizedTest
    @MethodSource("getSamples")
    public void shouldRunTheSpec(String file) throws Exception {
        runSpec(new File(SAMPLES_DIRECTORY, file));
    }
}
