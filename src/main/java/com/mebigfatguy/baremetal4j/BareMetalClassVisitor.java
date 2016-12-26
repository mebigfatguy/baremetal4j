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

import java.io.IOException;
import java.io.PrintWriter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BareMetalClassVisitor extends ClassVisitor {

    private Options options;
    private Sourcifier sourcifier;
    private String clsName;

    public BareMetalClassVisitor(ClassWriter cw, Options options) {
        super(Opcodes.ASM5, cw);
        this.options = options;
        sourcifier = new Sourcifier();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        sourcifier.visit(version, access, name, signature, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
        clsName = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        sourcifier.visitMethod(access, name, desc, signature, exceptions);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new BareMetalMethodVisitor(mv, sourcifier);
    }

    @Override
    public void visitEnd() {
        sourcifier.visitClassEnd();
        super.visitSource(SourceWriterFactory.name(clsName, options), null);
        super.visitEnd();
        try (PrintWriter sourceWriter = SourceWriterFactory.get(clsName, options)) {
            sourcifier.print(sourceWriter);
        } catch (IOException e) {
            // log somehow
        }
    }
}
