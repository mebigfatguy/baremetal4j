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

import java.io.Closeable;
import java.io.PrintWriter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Textifier;

public class BareMetalClassVisitor extends ClassVisitor implements Closeable {

    private ClassWriter cw;
    private Options options;
    private Textifier textifier;
    private PrintWriter sourceWriter;
    private String clsName;

    public BareMetalClassVisitor(ClassWriter cw, Options options) {
        super(Opcodes.ASM5);
        this.cw = cw;
        this.options = options;
        textifier = new Textifier();
    }

    @Override
    public void close() {
        if (sourceWriter != null) {
            sourceWriter.close();
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        textifier.visit(version, access, name, signature, superName, interfaces);
        clsName = name;

    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        textifier.visitMethod(access, name, desc, signature, exceptions);
        return new BareMetalMethodVisitor(cw, options, textifier);
    }

    @Override
    public void visitEnd() {
        textifier.visitClassEnd();
        try (PrintWriter sourceWriter = SourceWriterFactory.get(clsName, options)) {
            textifier.print(sourceWriter);
        }
    }

}
