package com.handle;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.List;


public class AttachTest {

    static String OnlineFixBugAgentPath = "G:\\workspace_idea\\common_test\\OnlineFixBugAgent\\target\\OnlineFixBugAgent.jar";
    static String className = "com.handle.Print";

    static String attachPID = "com.handle.Main";

    public static void main(String... args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {


        String src = "    public void print() {\n" +
                "int c = 0; " +
                "        System.out.println(\"重新装载方法的类111\" + (i++) + \"c:\"+c);\n" +
                "    }";

        StringBuilder sb = new StringBuilder();
        sb.append(className).append("$$$").append(src);

        byte[] bytes = sb.toString().getBytes("utf-8");

        BASE64Encoder base64Encoder = new BASE64Encoder();
        String encode = base64Encoder.encode(bytes);

        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor virtualMachineDescriptor : list) {
            System.out.println(virtualMachineDescriptor.displayName());
            if (virtualMachineDescriptor.displayName().equals(attachPID)) {
                VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
                virtualMachine.loadAgent(OnlineFixBugAgentPath, encode);
                virtualMachine.detach();
            }
        }

    }
}