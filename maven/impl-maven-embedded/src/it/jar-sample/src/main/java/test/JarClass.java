package test;

import javax.ejb.Stateless;

@Stateless
public class JarClass {

    public static final String GREETINGS = "Hello from dummy class for EmbeddedMaven";

    public String greet() {
        return GREETINGS;
    }
}
