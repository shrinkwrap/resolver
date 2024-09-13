package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SystemOutExtension implements BeforeEachCallback, AfterEachCallback {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        System.setOut(originalOut);
    }

    public String getLog() {
        return outputStream.toString();
    }

    public void clearLog() {
        outputStream.reset();
    }
}
