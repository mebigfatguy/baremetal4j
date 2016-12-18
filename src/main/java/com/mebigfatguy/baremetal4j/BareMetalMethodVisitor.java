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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Textifier;

public class BareMetalMethodVisitor extends MethodVisitor {

    private ClassWriter cw;
    private Options options;
    private Textifier textifier;

    public BareMetalMethodVisitor(ClassWriter cw, Options options, Textifier textifier) {
        super(Opcodes.ASM5);
        this.cw = cw;
        this.options = options;
        this.textifier = textifier;
    }

    @Override
    public void visitLineNumber(int arg0, Label arg1) {
        // don't copy these thru if they exist
    }

    @Override
    public void visitCode() {
        textifier.visitCode();
        super.visitEnd();
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        textifier.visitFieldInsn(opcode, owner, name, desc);
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        textifier.visitIincInsn(var, increment);
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitInsn(int opcode) {
        textifier.visitInsn(opcode);
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        textifier.visitIntInsn(opcode, operand);
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        textifier.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        textifier.visitJumpInsn(opcode, label);
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        textifier.visitLdcInsn(cst);
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        textifier.visitLookupSwitchInsn(dflt, keys, labels);
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        textifier.visitMethodInsn(opcode, owner, name, desc, itf);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    @Deprecated
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        textifier.visitMethodInsn(opcode, owner, name, desc);
        super.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        textifier.visitMultiANewArrayInsn(desc, dims);
        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        textifier.visitTableSwitchInsn(min, max, dflt, labels);
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        textifier.visitTypeInsn(opcode, type);
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        textifier.visitVarInsn(opcode, var);
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitEnd() {
        textifier.visitMethodEnd();
        super.visitEnd();
    }

}
