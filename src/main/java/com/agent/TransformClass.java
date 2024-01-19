package com.agent;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
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
import java.util.Optional;

/**
 * @author lishuo51
 * @date 2023/7/27
 */
public class TransformClass {

    public static byte[] transform(String fullClassName, String src) throws NotFoundException, CannotCompileException, IOException, URISyntaxException {
        ClassPool classPool = ClassPool.getDefault();
//        classPool.insertClassPath(new ClassClassPath(Main1.class));

//        CtClass ctClass = classPool.get("com.javaassist.TestBean");

        JavaParser javaParser = new JavaParser();

        ParseResult<CompilationUnit> parseResult = javaParser.parse(src);

        if (parseResult.isSuccessful()) {

            //class source
            Optional<CompilationUnit> optional = parseResult.getResult();

            CompilationUnit compilationUnit = optional.get();

            return compile(fullClassName, src);
        }

        ParseResult<MethodDeclaration> methodDeclarationParseResult = javaParser.parseMethodDeclaration(src);

        if (methodDeclarationParseResult.isSuccessful()) {
            CtClass ctClass = classPool.get(fullClassName);

            //method source
            MethodDeclaration methodDeclaration = methodDeclarationParseResult.getResult().get();

            CtMethod ctMethod = searchMethod(ctClass, methodDeclaration);

            String body = methodDeclaration.getBody().get().toString();

            System.out.println("body :" + body);

            ctMethod.setBody(body);

            byte[] bytes = ctClass.toBytecode();

            ctClass.detach();

            return bytes;
        }

        return null;
    }

    private static CtMethod searchMethod(CtClass ctClass, MethodDeclaration methodDeclaration) throws NotFoundException {
        String methodName = methodDeclaration.getNameAsString();

        CtMethod[] declaredMethods = ctClass.getDeclaredMethods();

        CtMethod returnCtMethod = null;

        for (CtMethod ctMethod : declaredMethods) {
            if (ctMethod.getName().equals(methodName)) {
                if (ctMethod.getParameterTypes().length == methodDeclaration.getParameters().size()) {
                    returnCtMethod = ctMethod;
                    break;
                }
            }
        }

        return returnCtMethod;
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
