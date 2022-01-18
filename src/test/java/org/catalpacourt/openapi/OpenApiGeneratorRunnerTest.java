package org.catalpacourt.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.catalpacourt.openapi.commandlinerunner.ProcessNode;
import org.catalpacourt.openapi.schema.Extension;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assure correct defaults are added.
 */
public class OpenApiGeneratorRunnerTest {

    private static final File TEST_RESOURCES = new File("/Users/ethankeller/Repos/openapi-generator-runner/src/test/resources/org/catalpacourt/openapi");
    private static final File SAMPLES = new File(TEST_RESOURCES, "samples");
    private static final File SAMPLE_PROCESS_GRAPHS = new File(TEST_RESOURCES, "processgraphs");
    private static final File SAMPLE_COMMANDS = new File(TEST_RESOURCES, "sequentialcommands");


    @Test
    public void shouldSerializeAndDeserializeRoundTrip() throws IOException {
        String path = new File(TEST_RESOURCES, "extension.yaml").getAbsolutePath();
        ObjectMapper mapper = MapperWrapper.getMapper(path);
        Extension extension = mapper.readValue(new File("/Users/ethankeller/Repos/openapi-generator-runner/src/test/resources/org/catalpacourt/openapi/extension.yaml"), Extension.class);
        String roundTripResult = mapper.writeValueAsString(extension);
        assertThat(mapper.readValue(roundTripResult, Extension.class)).usingRecursiveComparison().isEqualTo(extension);
    }

    private ProcessNode createProcessGraph(File spec) throws IOException {
        OpenApiGeneratorRunner openApiGeneratorRunner = new OpenApiGeneratorRunner();
        openApiGeneratorRunner.setSpec(spec);
        return openApiGeneratorRunner.createProcessGraph();
    }

    private List<ProcessBuilder> getSequentialCommands(File spec) throws IOException {
        SequentialProcessor sequentialProcessor = new SequentialProcessor();
        return sequentialProcessor.process(createProcessGraph(spec));
    }

    private void writeValue(File file, Object object) throws IOException {
        MapperWrapper.getMapper(file.getAbsolutePath()).writeValue(file, object);
    }

    @Test
    public void shouldCheckProcessGraphSamples() throws IOException {
        ProcessNode node = createProcessGraph(new File(SAMPLES, "simple.json"));
        writeValue(new File(SAMPLE_PROCESS_GRAPHS, "simple.json"), node);
    }

    @Test
    public void shouldCheckSequentialCommands() throws IOException {
        writeValue(new File(SAMPLE_COMMANDS, "simple.json"), getSequentialCommands(new File(SAMPLES, "simple.json")));
    }
}
