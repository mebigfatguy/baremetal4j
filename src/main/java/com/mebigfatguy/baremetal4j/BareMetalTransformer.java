/*
 * baremetal4j - A java aspect for allowing debugging at the byte code level from source debuggers (as in IDEs)
 * Copyright 2016 MeBigFatGuy.com
 * Copyright 2016 Dave Brosius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.mebigfatguy.baremetal4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class BareMetalTransformer implements ClassFileTransformer {

    private Options options;

    public BareMetalTransformer(Options options) {
        this.options = options;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        if (className.startsWith("java/") || className.startsWith("javax/") || className.startsWith("sun/")
                || className.startsWith("com/mebigfatguy/baremetal4j/") || !options.instrumentClass(className)) {
            return classfileBuffer;
        }

        try {

            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            BareMetalClassVisitor stackTraceVisitor = new BareMetalClassVisitor(cw, options);
            cr.accept(stackTraceVisitor, ClassReader.EXPAND_FRAMES);

            byte[] transformedClass = cw.toByteArray();

            // dumpGeneratedClassFile(transformedClass, "/tmp/" + className + ".class");

            return transformedClass;
        } catch (Exception e) {
            e.printStackTrace();
            IllegalClassFormatException icfe = new IllegalClassFormatException("Failed instrumenting class " + className);
            icfe.initCause(e);
            throw icfe;
        }
    }

    private void dumpGeneratedClassFile(byte[] transformedClass, String name) {
        try (FileOutputStream fos = new FileOutputStream(name)) {
            fos.write(transformedClass);
        } catch (IOException e) {
        }
    }

    @Override
    public String toString() {
        return ToString.build(this);
    }
}
