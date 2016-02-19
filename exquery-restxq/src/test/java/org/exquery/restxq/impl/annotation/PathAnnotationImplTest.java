/**
 * Copyright © 2012, Adam Retter / EXQuery
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.exquery.restxq.impl.annotation;

import javax.xml.namespace.QName;
import org.exquery.ErrorCodes.ErrorCode;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.xquery.Cardinality;
import org.exquery.xquery.FunctionArgument;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;
import org.exquery.xquery3.Annotation;
import org.exquery.xquery3.FunctionSignature;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Map;

/**
 *
 * @author Adam Retter
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
    
    @Test
    public void path_fnArgItemType_one_template_isOk() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new ItemFnArg("arg1")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$arg1}")
        });
        pa.initialise();
    }
    
    @Test
    public void path_fnArgNodeType_one_template_isNotOk() throws RestAnnotationException {
        final FunctionArgument[] args = {
            new NodeFnArg("arg1")
        };
        
        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
            new StringLiteral("/{$arg1}")
        });
        
        ErrorCode code = null;
        try {
            pa.initialise();
        } catch(final RestAnnotationException rae) {
            code = rae.getErrorCode();            
        }
        
        assertEquals(RestXqErrorCodes.RQST0006, code);
    }

    @Test
    public void parse_path_oneParam_partFilename() throws RestAnnotationException {
        final FunctionArgument[] args = {
                new StrFnArg("arg1")
        };

        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
                new StringLiteral("/{$arg1}.json")
        });

        pa.initialise();

        final String requestPath = "/something.json";

        assertTrue(pa.matchesPath(requestPath));
        final Map<String, String> requestPathParams = pa.extractPathParameters(requestPath);
        assertEquals(1, requestPathParams.size());
        assertEquals("something", requestPathParams.get("arg1"));
    }

    @Test
    public void parse_path_oneParam_containingTemplate() throws RestAnnotationException {
        final FunctionArgument[] args = {
                new StrFnArg("arg1")
        };

        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
                new StringLiteral("/pre{$arg1}post")
        });

        pa.initialise();

        final String requestPath = "/pre-main-post";

        assertTrue(pa.matchesPath(requestPath));
        final Map<String, String> requestPathParams = pa.extractPathParameters(requestPath);
        assertEquals(1, requestPathParams.size());
        assertEquals("-main-", requestPathParams.get("arg1"));
    }

    @Test
    public void parse_path_oneParam_mixed2() throws RestAnnotationException {
        final FunctionArgument[] args = {
                new StrFnArg("arg1")
        };

        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
                new StringLiteral("/seg1/{$arg1}")
        });

        pa.initialise();

        final String requestPath = "/seg1/seg2";

        assertTrue(pa.matchesPath(requestPath));
        final Map<String, String> requestPathParams = pa.extractPathParameters(requestPath);
        assertEquals(1, requestPathParams.size());
        assertEquals("seg2", requestPathParams.get("arg1"));
    }

    @Test
    public void parse_path_oneParam_mixed1() throws RestAnnotationException {
        final FunctionArgument[] args = {
                new StrFnArg("arg1")
        };

        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
                new StringLiteral("/{$arg1}/seg2")
        });

        pa.initialise();

        final String requestPath = "/seg1/seg2";

        assertTrue(pa.matchesPath(requestPath));
        final Map<String, String> requestPathParams = pa.extractPathParameters(requestPath);
        assertEquals(1, requestPathParams.size());
        assertEquals("seg1", requestPathParams.get("arg1"));
    }

    @Test
    public void parse_path_twoParams() throws RestAnnotationException {
        final FunctionArgument[] args = {
                new StrFnArg("arg1"),
                new StrFnArg("arg2")
        };

        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
                new StringLiteral("/{$arg1}/{$arg2}")
        });

        pa.initialise();

        final String requestPath = "/seg1/seg2";

        assertTrue(pa.matchesPath(requestPath));
        final Map<String, String> requestPathParams = pa.extractPathParameters(requestPath);
        assertEquals(2, requestPathParams.size());
        assertEquals("seg1", requestPathParams.get("arg1"));
        assertEquals("seg2", requestPathParams.get("arg2"));
    }

    @Test
    public void parse_path_oneParam() throws RestAnnotationException {
        final FunctionArgument[] args = {
                new StrFnArg("arg1")
        };

        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new ArgsFunctionSignature(args));
        pa.setLiterals(new Literal[]{
                new StringLiteral("/{$arg1}")
        });

        pa.initialise();

        final String requestPath = "/seg1";

        assertTrue(pa.matchesPath(requestPath));
        final Map<String, String> requestPathParams = pa.extractPathParameters(requestPath);
        assertEquals(1, requestPathParams.size());
        assertEquals("seg1", requestPathParams.get("arg1"));
    }

    @Test
    public void parse_path_zeroParams() throws RestAnnotationException {

        final PathAnnotationImpl pa = new PathAnnotationImpl();
        pa.setFunctionSignature(new NoArgsFunctionSignature());
        pa.setLiterals(new Literal[]{
            new StringLiteral("/path1")
        });

        pa.initialise();

        final String requestPath = "/path1";

        assertTrue(pa.matchesPath(requestPath));
        final Map<String, String> requestPathParams = pa.extractPathParameters(requestPath);
        assertEquals(0, requestPathParams.size());
    }
    
    public class NodeFnArg implements FunctionArgument {
        private final String name;

        public NodeFnArg(final String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }

        @Override
        public Type getType() {
            return Type.NODE;
        }

        @Override
        public Cardinality getCardinality() {
            return Cardinality.ZERO_OR_ONE;
        }
    }
    
    public class ItemFnArg implements FunctionArgument {
        private final String name;

        public ItemFnArg(final String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }

        @Override
        public Type getType() {
            return Type.ITEM;
        }

        @Override
        public Cardinality getCardinality() {
            return Cardinality.ZERO_OR_ONE;
        }
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
}
