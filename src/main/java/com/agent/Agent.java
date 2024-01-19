package com.agent;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Agent {

    public static void agentmain(String arg, Instrumentation instrumentation) {

//        Class[] classes = instrumentation.getAllLoadedClasses();
//        for(Class cls :classes){
////            System.out.println("agentMain: "+cls.getName());
//        }

        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] bytes = base64Decoder.decodeBuffer(arg);

            arg = new String(bytes);

            System.out.println("agentmain , args is " + arg);

            int index = arg.indexOf("$$$");
            String fullClassName = arg.substring(0, index);
            String src = arg.substring(index + 3).trim();

            System.out.println("className: " + fullClassName);
            System.out.println("src: " + src);

            byte[] transform = TransformClass.transform(fullClassName, src);

            ClassDefinition classDefinition = new ClassDefinition(Class.forName(fullClassName), transform);
            instrumentation.redefineClasses(classDefinition);

            System.out.println("agentmain , redefineClasses done className:" + fullClassName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     *
     * @param classPath 文件路径
     * @return 文件字节数据
     * @throws IOException 文件读取异常
     */
    private static byte[] readClassBytes(String classPath) throws IOException {
        return Files.readAllBytes(Paths.get(classPath));
    }
}