package com.pre;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;

/**
 * @title: PreMain
 * @description:
 * @author: zhangfan
 * @data: 2021年09月26日 19:41
 */
public class PreMainAgent {

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        premain(agentArgs, instrumentation);
    }

    public static void premain(String agentArgs, Instrumentation inst) {

        try {
            String[] split = agentArgs.split(",");
            LogEnhance.filePath = split[0];
            if (split.length == 3) {
                LogEnhance.greaterMS = Long.valueOf(split[2]);
            }
            inst.addTransformer(new DefineTransformer().setKeyword(split[1]), true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class DefineTransformer implements ClassFileTransformer {

        String keyword;

        public DefineTransformer setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

            if (classfileBuffer == null) {
                return null;
            }

            if (className.indexOf(keyword) > -1) {
                System.out.println("命中的：" + className);
                try {
                    ClassPool pool = ClassPool.getDefault();
                    CtClass ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
                    Arrays.stream(ctClass.getDeclaredMethods())
                            .forEach(ctMethod -> {
                                if (ctMethod.isEmpty()) {
                                    return;
                                }
                                try {
                                    ctMethod.insertBefore("com.pre.LogEnhance.get().point();\n");
                                    ctMethod.insertAfter("com.pre.LogEnhance.get().printEnd();\n");
                                } catch (CannotCompileException e) {
                                    e.printStackTrace();
                                }
                            });
                    classfileBuffer = ctClass.toBytecode();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            return classfileBuffer;
        }
    }
}
