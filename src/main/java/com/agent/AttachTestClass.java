package com.agent;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


public class AttachTestClass {
    public static void main(String... args) throws IOException, NotFoundException, CannotCompileException, URISyntaxException {

        String className = "com.javaagent.Print";
        String src = "package com.javaagent;\n" +
                "\n" +
                "public class Print {\n" +
                "\n" +
                "    static int i = 0;\n" +
                "//    static int j = 0;\n" +
                "\n" +
                "\n" +
                "\n" +
                "    public void print() {\n" +
                "        System.out.println(\"重新装载的类\" + (i++));\n" +
                "    }\n" +
                "\n" +
                "}\n";

        byte[] compile = compile(className, src);
        System.out.println(compile.length);


    }

    /**
     * 装载字符串成为java可执行文件
     * @param className className
     * @param javaCodes javaCodes
     * @return Class
     */
    private static byte[] compile(String className, String javaCodes) throws URISyntaxException, IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,null, null);
        JavaSourceFromString srcObject = new JavaSourceFromString(className, javaCodes);
        Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(srcObject);
        String flag = "-d";
        String outDir = "";
        try {
            File classPath = new File(Thread.currentThread().getContextClassLoader().getResource("").toURI());
            outDir = classPath.getAbsolutePath() + File.separator + "tmp" + File.separator;

            System.out.println("class输出路径：" + outDir);

            File file = new File(outDir);

            if (!file.exists()) {
                boolean mkdir = file.mkdir();
                System.out.println("创建目录：" + mkdir);
            }

        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        Iterable<String> options = Arrays.asList(flag, outDir);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, fileObjects);
        boolean result = task.call();
        if (result == true) {
            String classPath = outDir + className.replace(".", File.separator) + ".class";
            return Files.readAllBytes(Paths.get(classPath));
        }
        return null;
    }
}