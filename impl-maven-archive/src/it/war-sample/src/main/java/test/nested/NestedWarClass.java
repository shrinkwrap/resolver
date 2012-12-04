package test.nested;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import test.WarClass;

// this file greets in Base64
public class NestedWarClass extends WarClass {

    public static final String GREETINGS = "Hello from MavenImporter imported nested class";

    @Override
    public String greet() {
        return Base64.encodeBase64String(GREETINGS.getBytes(Charset.defaultCharset()));
    }

}
