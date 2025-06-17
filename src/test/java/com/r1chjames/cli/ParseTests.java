package com.r1chjames.cli;

import com.r1chjames.CommandRunner;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseTests {

    @Test
    void testParseWithInvalidSubcommand() {
        CliParameterException exception = assertThrows(CliParameterException.class, () ->
                new CommandRunner().execute(List.of("consumption").toArray(new String[0])));
        assertThat(exception.getMessage()).contains("Invalid command line arguments");
    }

    @Test
    void testParseWithMissingArgs() {
        CliParameterException exception = assertThrows(CliParameterException.class, () ->
                new CommandRunner().execute(List.of("consume").toArray(new String[0])));
        assertThat(exception.getMessage()).contains("Missing required options");
    }

    @Test
    void testParseWithValidConsumeArgs() {
        CommandRunner runner = new CommandRunner();
        String[] args = {"consume", "-topics", "test-topic", "-groupId", "test-group", "-bootstrapServers", "localhost:9092", "-schemaRegistry", "http://localhost:8081"};
        assertThatCode(() -> runner.execute(args)).doesNotThrowAnyException();
    }

    @Test
    void testParseWithValidProduceArgs() {
        CommandRunner runner = new CommandRunner();
        String[] args = {"produce", "-topic", "test-topic", "-bootstrapServers", "localhost:9092"};
        assertThatCode(() -> runner.execute(args)).doesNotThrowAnyException();
    }
}