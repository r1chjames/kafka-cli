package com.r1chjames.cli;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandLineConstants {
    public static final String SHOULD_PROCESS_FROM_BEGINNING = "-shouldProcessFromBeginning";
    public static final String SPFB = "-spfb";
    public static final String TOPICS = "-topics";
    public static final String T = "-t";
    public static final String GROUP_ID = "-groupId";
    public static final String GI = "-gi";
    public static final String BOOTSTRAP_SERVERS = "-bootstrapServers";
    public static final String BS = "-bs";
    public static final String SCHEMA_REGISTRY = "-schemaRegistry";
    public static final String SR = "-sr";
    public static final String DEFAULT_KEY_DESERIALIZER = "-defaultKeyDeserializer";
    public static final String DKD = "-dkd";
    public static final String DEFAULT_VALUE_DESERIALIZER = "-defaultValueDeserializer";
    public static final String DVD = "-dvd";
    public static final String PRODUCE_COUNT = "-produceCount";
    public static final String PC = "-pc";
    public static final String PRODUCE_STRING = "-produceString";
    public static final String PS = "-ps";

    public static String getShortOpt(String longOpt) {
        return !longOpt.isEmpty()
                ? Arrays.stream(longOpt.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
                .sequential()
                .map(w -> w.substring(0,1))
                .collect(Collectors.joining())
                .toLowerCase()
                : "-";
    }
}
