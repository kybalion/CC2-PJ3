import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


public class ClassPathModificator {
   private static final Class[] urlParams =
       new Class[]{URL.class};
   public static void appendPath(URL newURL) throws
       IOException {
       URLClassLoader classLoader = (URLClassLoader)
           ClassLoader.getSystemClassLoader();
       try {
           Method meth = URLClassLoader.class.
               getDeclaredMethod("addURL", urlParams);
           meth.setAccessible(true);
           meth.invoke(classLoader, new Object []
           {newURL});
       } catch (Throwable err) {
           throw new IOException();
       }
   }

   public static void appendPath(String newPath)
       throws IOException {
       appendPath(new File(newPath));
   }

   public static void appendPath(File fileObj)
       throws IOException {
       appendPath(fileObj.toURI().toURL());
   }
}