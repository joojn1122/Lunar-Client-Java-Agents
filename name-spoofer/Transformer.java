package net.joojn;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class Transformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        // 1.8 && 1.7
        if (!className.contains("epsappshahepahssspshsssas") && !className.contains("aeeassphphshpsppsehshhhhh")) {
            return classfileBuffer;
        }

        ClassReader cr = new ClassReader(classfileBuffer);

        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        FieldNode field = cn.fields.get(0);

        for (MethodNode method : cn.methods) {
            // getFormattedText
            if(!method.desc.equals("()Ljava/lang/String;")) continue;

            //if (!method.name.equals("bridge$getFormattedText")) continue;

            method.instructions.clear();

            method.instructions.add(
                    new VarInsnNode(Opcodes.ALOAD, 0)
            );

            method.instructions.add(
                    new FieldInsnNode(Opcodes.GETFIELD, className, field.name, field.desc)
            );

            method.instructions.add(new LdcInsnNode(Main.config.get(0)));
            method.instructions.add(new LdcInsnNode(Main.config.get(1)));

            method.instructions.add(
                    new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "replace", "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;")
            );

            method.instructions.add(
                    new InsnNode(Opcodes.ARETURN)
            );

            // return {field.name}.replace("name1", "name2");
        }

        // ClassWriter.COMPUTE_MAXS == recalculate max stacks -> https://stackoverflow.com/questions/23481234/modify-method-body-with-asm-result-exceeded-max-stack-size-during
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);

        return cw.toByteArray();
    }
}
