/**
 * 
 */
package org.jboss.shrinkwrap.resolver.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * An utility to generate artifact wars
 * 
 * @author <a href="kpiwko@redhat.com>Karel Piwko</a>
 * 
 */
@RunWith(Parameterized.class)
public class WarGenerator
{
   private String name;
   private Class<?>[] classes;
   private String[] directories;

   @Parameters
   public static Collection<Object[]> jars()
   {
      Object[][] data = new Object[][] { 
            { "test-war", new Class<?>[] { Object.class, List.class }, new String[] {"html", "jsp"} }, 
            { "test-war-classifier", new Class<?>[] { Arrays.class }, new String[] {"xhtml", "rf" }}
      };

      return Arrays.asList(data);
   }

   public WarGenerator(String name, Class<?>[] classes, String[] directories)
   {
      this.name = name;
      this.classes = classes;
      this.directories = directories;
   }

   @Test
   public void createJars()
   {
      WebArchive archive = ShrinkWrap.create(WebArchive.class, name).addClasses(classes)
         .addAsDirectories(directories);

      archive.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

}
