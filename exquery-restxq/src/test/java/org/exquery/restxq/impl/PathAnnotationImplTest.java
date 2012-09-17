/*
Copyright (c) 2012, Adam Retter
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Adam Retter Consulting nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.exquery.restxq.impl;

import javax.xml.namespace.QName;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.restxq.impl.annotation.PathAnnotationImpl;
import org.exquery.xquery.Cardinality;
import org.exquery.xquery.FunctionArgument;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;
import org.exquery.xquery3.Annotation;
import org.exquery.xquery3.FunctionSignature;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class PathAnnotationImplTest {
    
    @Test
    public void pathSpecificity_three_concrete_missing_preceding_slash() throws RestAnnotationException {
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new NoArgsFunctionSignature());
        pa.setLiterals(new Literal[]{
            new StringLiteral("person/elisabeth/nose")
        });
        pa.initialise();
        
        assertEquals(15, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_three_concrete() throws RestAnnotationException {
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new NoArgsFunctionSignature());
        pa.setLiterals(new Literal[]{
            new StringLiteral("/person/elisabeth/nose")
        });
        pa.initialise();
        
        assertEquals(15, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_two_concrete_one_template() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("part")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/person/elisabeth/{$part}")
        });
        pa.initialise();
        
        assertEquals(14, pa.getPathSpecificityMetric());
    }
    
        
    @Test
    public void pathSpecificity_one_concrete_one_template_one_concrete() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("name")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/person/{$name}/nose")
        });
        pa.initialise();
        
        assertEquals(13, pa.getPathSpecificityMetric());
    }
    
    
    @Test
    public void pathSpecificity_one_concrete_two_template_last() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("name"),
            new StrFnArg("part")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/person/{$name}/{$part}")
        });
        pa.initialise();
        
        assertEquals(12, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_one_template_two_concrete() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("type"),
            new StrFnArg("part")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$type}/elisabeth/nose")
        });
        pa.initialise();
        
        assertEquals(11, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_one_template_one_concrete_one_template() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("type"),
            new StrFnArg("part")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$type}/elisabeth/{$part}")
        });
        pa.initialise();
        
        assertEquals(10, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_one_concrete_two_template_first() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("type"),
            new StrFnArg("name")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$type}/{$name}/nose")
        });
        pa.initialise();
        
        assertEquals(9, pa.getPathSpecificityMetric());
    }
    
    
    @Test
    public void pathSpecificity_three_template() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("type"),
            new StrFnArg("name"),
            new StrFnArg("part")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$type}/{$name}/{$part}")
        });
        pa.initialise();
        
        assertEquals(8, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_two_concrete() throws RestAnnotationException {
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new NoArgsFunctionSignature());
        pa.setLiterals(new Literal[]{
            new StringLiteral("/person/elisabeth")
        });
        pa.initialise();
        
        assertEquals(7, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_one_concrete_one_template() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("name"),
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/person/{$name}")
        });
        pa.initialise();
        
        assertEquals(6, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_one_template_one_concrete() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("type"),
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$type}/elisabeth")
        });
        pa.initialise();
        
        assertEquals(5, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_two_template() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new StrFnArg("type"),
            new StrFnArg("name")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$type}/{$name}")
        });
        pa.initialise();
        
        assertEquals(4, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_one_concrete() throws RestAnnotationException {
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new NoArgsFunctionSignature());
        pa.setLiterals(new Literal[]{
            new StringLiteral("/person")
        });
        pa.initialise();
        
        assertEquals(3, pa.getPathSpecificityMetric());
    }
    
    @Test
    public void pathSpecificity_one_template() throws RestAnnotationException {
        
        final FunctionArgument[] args = {
            new StrFnArg("type")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$type}")
        });
        pa.initialise();
        
        assertEquals(2, pa.getPathSpecificityMetric());
    }
    
    public class StrFnArg implements FunctionArgument {
        private final String name;

        public StrFnArg(final String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }

        @Override
        public Type getType() {
            return Type.STRING;
        }

        @Override
        public Cardinality getCardinality() {
            return Cardinality.ZERO_OR_ONE;
        }
    }
    
    public class NoArgsFunctionSignature implements FunctionSignature {

        @Override
        public QName getName() {
            return new QName("http://somewhere", "something");
        }

        @Override
        public int getArgumentCount() {
            return 0;
        }

        @Override
        public FunctionArgument[] getArguments() {
            return new FunctionArgument[]{};
        }

        @Override
        public Annotation[] getAnnotations() {
            return new Annotation[]{};
        }
    }
    
    public class ArgsFunctionSignature implements FunctionSignature {
        private final FunctionArgument[] args;

        public ArgsFunctionSignature(final FunctionArgument[] args) {
            this.args = args;
        }
        
        @Override
        public QName getName() {
            return new QName("http://somewhere", "something");
        }

        @Override
        public int getArgumentCount() {
            return args.length;
        }

        @Override
        public FunctionArgument[] getArguments() {
            return args;
        }

        @Override
        public Annotation[] getAnnotations() {
            return new Annotation[]{};
        }
    }
    
    public class StringLiteral implements Literal {
        private final String str;

        public StringLiteral(final String str) {
            this.str = str;
        }
        
        @Override
        public Type getType() {
            return Type.STRING;
        }

        @Override
        public String getValue() {
            return str;
        }
    }
}
