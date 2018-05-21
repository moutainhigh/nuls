package io.nuls.contract.vm.code;

import io.nuls.contract.vm.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassCodeLoader {

    public static final Map<String, ClassCode> RESOURCE_CLASS_CODES = loadFromResource();

    public static ClassCode load(String className) {
        try {
            ClassReader classReader = new ClassReader(className);
            return load(classReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassCode loadFromResource(String className) {
        ClassCode classCode = RESOURCE_CLASS_CODES.get(className);
        if (classCode == null) {
            throw new RuntimeException("can't load class " + className);
        } else {
            return classCode;
        }
    }

    public static Map<String, ClassCode> loadFromResource() {
        try {
            Map<String, ClassCode> map = new LinkedHashMap<>();
            URL url = ClassCodeLoader.class.getResource("/classes");
            if (url == null) {
                return map;
            }
            byte[] bytes = FileUtils.readFileToByteArray(new File(url.getFile()));
            List<ClassCode> list = loadJar(bytes);
            for (ClassCode classCode : list) {
                map.put(classCode.getName(), classCode);
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassCode loadFromResourceOrTmp(String className) {
        ClassCode classCode = RESOURCE_CLASS_CODES.get(className);
        if (classCode == null) {
            try {
                File file = new File("/tmp/classes/" + className + ".class");
                if (file.exists()) {
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    return load(bytes);
                } else {
                    throw new RuntimeException("can't load class " + className);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return classCode;
        }
    }

    public static ClassCode load(byte[] bytes) {
        return load(new ClassReader(bytes));
    }

    public static ClassCode load(ClassReader classReader) {
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        ClassCode classCode = ClassCodeConver.toClassCode(classNode);
        return classCode;
    }

    public static void load(Map<String, ClassCode> classCodes, String className, Function<String, ClassCode> loader) {
        className = Utils.classNameReplace(className);
        if (!classCodes.containsKey(className)) {
            ClassCode classCode = loader.apply(className);
            if (StringUtils.isNotEmpty(classCode.getSuperName())) {
                load(classCodes, classCode.getSuperName(), loader);
            }
            for (String interfaceName : classCode.getInterfaces()) {
                load(classCodes, interfaceName, loader);
            }
            classCodes.put(className, classCode);
        }
    }

    public static List<ClassCode> loadJar(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        return loadJar(inputStream);
    }

    public static List<ClassCode> loadJar(InputStream inputStream) {
        try {
            JarInputStream jarInputStream = new JarInputStream(inputStream);
            return loadJar(jarInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ClassCode> loadJar(JarInputStream jarInputStream) {
        List<ClassCode> list = new ArrayList<>();
        try {
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class")) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    IOUtils.copy(jarInputStream, outputStream);
                    byte[] bytes = outputStream.toByteArray();
                    ClassCode classCode = load(bytes);
                    list.add(classCode);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
