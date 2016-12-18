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
    public void visitEnd() {
        textifier.visitMethodEnd();
    }

}
