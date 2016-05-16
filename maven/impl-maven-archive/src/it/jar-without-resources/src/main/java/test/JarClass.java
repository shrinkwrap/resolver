package test;

import javax.ejb.Stateless;

@Stateless
public class JarClass {

    public static final String GREETINGS = "Hello from MavenImporter imported class";

    public String greet() {
        return GREETINGS;
    }
}
