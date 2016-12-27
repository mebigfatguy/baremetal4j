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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Printer;

public class Sourcifier {

    private static final Map<Character, String> sigToType = new HashMap<>();
    static {
        sigToType.put('V', "void");
        sigToType.put('Z', "boolean");
        sigToType.put('B', "byte");
        sigToType.put('S', "short");
        sigToType.put('I', "int");
        sigToType.put('J', "long");
        sigToType.put('F', "float");
        sigToType.put('D', "double");
    }

    private List<String> lines = new ArrayList<>();
    private int byteOffset = 0;
    private String className;

    public int currentLine() {
        return lines.size() + 1;
    }

    public void print(PrintWriter pw) {
        for (String line : lines) {
            pw.println(line);
        }
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        String packageName;
        int lastSlash = name.lastIndexOf('/');
        if (lastSlash >= 0) {
            packageName = name.substring(0, lastSlash).replace('/', '.');
            className = name.substring(lastSlash + 1);
        } else {
            packageName = "";
            className = name;
        }

        if (!packageName.isEmpty()) {
            lines.add("package " + packageName + ";");
            lines.add("");
        }
        lines.add(accessString(access) + " class " + className + " {");
    }

    public void visitSource(String source, String debug) {
    }

    public void visitOuterClass(String owner, String name, String desc) {
    }

    public Sourcifier visitClassAnnotation(String desc, boolean visible) {
        return null;
    }

    public void visitClassAttribute(Attribute attr) {
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }

    public Sourcifier visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    public Sourcifier visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<clinit>".equals(name)) {
            lines.add("\tstatic {");
        } else {
            if ("<init>".equals(name)) {
                name = className;
            }
            lines.add("\t" + accessString(access) + " " + methodReturnType(desc) + " " + name + argumentSignature(desc) + " {");
            lines.add("");
            lines.add("\t\tint BCO; // Byte Code Offset");
            byteOffset = 0;
        }
        return this;
    }

    public void visitClassEnd() {
        lines.add("}");
    }

    public void visit(String name, Object value) {
    }

    public void visitEnum(String name, String desc, String value) {
    }

    public Sourcifier visitAnnotation(String name, String desc) {
        return null;
    }

    public Sourcifier visitArray(String name) {
        return null;
    }

    public void visitAnnotationEnd() {
    }

    public Sourcifier visitFieldAnnotation(String desc, boolean visible) {
        return null;
    }

    public void visitFieldAttribute(Attribute attr) {
    }

    public void visitFieldEnd() {
    }

    public Sourcifier visitAnnotationDefault() {
        return null;
    }

    public Sourcifier visitMethodAnnotation(String desc, boolean visible) {
        return null;
    }

    public Sourcifier visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return null;
    }

    public void visitMethodAttribute(Attribute attr) {
    }

    public void visitCode() {
        lines.add("");
    }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
    }

    public void visitInsn(int opcode) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[opcode]);
        byteOffset++;
    }

    public void visitIntInsn(int opcode, int operand) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[opcode] + " " + operand);
        byteOffset += (opcode == Opcodes.SIPUSH) ? 3 : 2;
    }

    public void visitVarInsn(int opcode, int var) {
        boolean isShortForm = var <= 3;

        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[opcode] + (isShortForm ? "_" : " ") + var);
        byteOffset += isShortForm ? 1 : 2;
    }

    public void visitTypeInsn(int opcode, String type) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[opcode] + " " + type.replace('/', '.'));
        byteOffset += 3;

    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[opcode] + " " + owner.replace('/', '.') + "." + name);
        byteOffset += 3;
    }

    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[opcode] + " " + owner.replace('/', '.') + "." + name
                + argumentSignature(desc) + " ===> " + methodReturnType(desc));
        byteOffset += 3;
    }

    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[Opcodes.INVOKEDYNAMIC] + " " + name + argumentSignature(desc)
                + " ===> " + methodReturnType(desc));
        byteOffset += 1;
    }

    public void visitJumpInsn(int opcode, Label label) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[opcode]);
        byteOffset += 3;
    }

    public void visitLabel(Label label) {
    }

    public void visitLdcInsn(Object cst) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[Opcodes.LDC] + " " + cst);
        byteOffset += 2;
    }

    public void visitIincInsn(int var, int increment) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[Opcodes.IINC] + " " + var + ", increment");
        byteOffset += 3;
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[Opcodes.TABLESWITCH]);
        byteOffset += 1;
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[Opcodes.LOOKUPSWITCH]);
        byteOffset += 1;
    }

    public void visitMultiANewArrayInsn(String desc, int dims) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + Printer.OPCODES[Opcodes.MULTIANEWARRAY] + " " + desc + ", " + dims);
        byteOffset += 4;
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        lines.add("\t\tBCO = " + String.format("%05d", byteOffset) + "; // " + "??");
        byteOffset += 1;
    }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
    }

    public void visitLineNumber(int line, Label start) {
    }

    public void visitMaxs(int maxStack, int maxLocals) {
    }

    public void visitMethodEnd() {
        lines.add("\t}");
        lines.add("");
    }

    public String accessString(int access) {
        String separator = "";
        StringBuilder sb = new StringBuilder(32);
        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            sb.append("public");
            separator = " ";
        } else if ((access & Opcodes.ACC_PROTECTED) != 0) {
            sb.append("protected");
            separator = " ";
        } else if ((access & Opcodes.ACC_PRIVATE) != 0) {
            sb.append("private");
            separator = " ";
        }

        if ((access & Opcodes.ACC_STATIC) != 0) {
            sb.append(separator).append("static");
            separator = " ";
        }

        if ((access & Opcodes.ACC_FINAL) != 0) {
            sb.append(separator).append("final");
            separator = " ";
        }

        return sb.toString();
    }

    private String methodReturnType(String signature) {
        int rParenPos = signature.indexOf(')');

        StringBuilder returnSig = new StringBuilder(signature.substring(rParenPos + 1));

        return convertInternalType(returnSig);
    }

    private String argumentSignature(String signature) {

        int lParen = signature.indexOf('(');
        int rParen = signature.lastIndexOf(')');

        StringBuilder returnSig = new StringBuilder(signature.substring(lParen + 1, rParen));

        StringBuilder returnType = new StringBuilder("(");
        String separator = "";
        while (returnSig.length() > 0) {
            returnType.append(separator).append(convertInternalType(returnSig));
            separator = ", ";
        }

        returnType.append(")");
        return returnType.toString();
    }

    private String convertInternalType(StringBuilder internalType) {

        if (internalType.charAt(0) == 'L') {
            int semiPos = internalType.indexOf(";");
            String type = internalType.substring(1, semiPos).replace('/', '.');
            internalType.delete(0, semiPos + 1);
            return type;
        }

        String returnType = sigToType.get(internalType.charAt(0));
        if (returnType != null) {
            internalType.delete(0, 1);
            return returnType;
        }

        if (internalType.charAt(0) == '[') {
            internalType.delete(0, 1);
            returnType = convertInternalType(internalType);
            return returnType + "[]";
        }

        return "";
    }
}
