package test.nested;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import test.JarClass;

// this file greets in Base64
public class NestedJarClass extends JarClass {

    public static final String GREETINGS = "Hello from MavenBuilder imported nested class";

    @Override
    public String greet() {
        return Base64.encodeBase64String(GREETINGS.getBytes(Charset.defaultCharset()));
    }

}
