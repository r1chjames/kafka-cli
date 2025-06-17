package com.r1chjames.cli;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.r1chjames.cli.CommandLineConstants.*;

public class CommandLineConstantsTests {

    @ParameterizedTest
    @MethodSource("constantList")
    void testGetShortOpt(final String shortOpt, final String longOpt) {
        assert shortOpt.equals(CommandLineConstants.getShortOpt(longOpt));
    }

    private static Stream<Arguments> constantList() {
        return Stream.of(
                Arguments.of("spfb", SHOULD_PROCESS_FROM_BEGINNING),
                Arguments.of("t", TOPICS),
                Arguments.of("gi", GROUP_ID),
                Arguments.of("bs", BOOTSTRAP_SERVERS),
                Arguments.of("sr", SCHEMA_REGISTRY),
                Arguments.of("dkd", DEFAULT_KEY_DESERIALIZER),
                Arguments.of("dvd", DEFAULT_VALUE_DESERIALIZER),
                Arguments.of("pc", PRODUCE_COUNT),
                Arguments.of("ps", PRODUCE_STRING)
        );
    }
}
