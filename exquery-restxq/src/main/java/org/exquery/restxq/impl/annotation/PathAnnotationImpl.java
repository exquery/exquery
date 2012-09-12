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
package org.exquery.restxq.impl.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exquery.http.URI;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.annotation.PathAnnotation;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;

/**
 * Implementation of RESTXQ Path Annotation
 * i.e. %rest:path
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class PathAnnotationImpl extends AbstractRestAnnotation implements PathAnnotation {
    
    protected final static int PATH_SEGMENT_PARAM_SPECIFICITY = 1;
    protected final static int PATH_SEGMENT_SOLID_SPECIFICITY = 2;
    
    //combines official RFC URI path segment regexp  with our encoded function argument regexp
    public final static String pathSegmentRegExp = "(?:"  + URI.pchar_regExp + "|" + functionArgumentRegExp + ")";
    
    //path segment extractor
    public final static Pattern ptnPathSegment = Pattern.compile(pathSegmentRegExp);
    
    //regexp to validate entire path
    public final static String pathRegExp = "^(?:" + URI.PATH_SEGMENT_DELIMITER + "?" + pathSegmentRegExp + ")+$";
    
    //validator for Path
    public final static Pattern ptnPath = Pattern.compile(pathRegExp);
    
    private PathInformation pathRegularExpression;
    
    
    /**
     * Ensures that the Path Annotation
     * is compatible with the Function Signature
     * and extracts templates from the path
     * for later use
     * 
     * @throws  RestAnnotationException if the Path Annotation is not compatible
     * with the function signature or if the path is malformed
     */
    @Override
    public void initialise() throws RestAnnotationException {
        super.initialise();
        this.pathRegularExpression = parsePath();
    }
    
    /**
     * @see org.exquery.restxq.annotation.PathAnnotation#matchesPath(java.lang.String)
     */
    @Override
    public boolean matchesPath(final String path) {
        final Matcher m = getPathInformation().getPathMatcher(path);
        return m.matches();
    }
    
    /**
     * @see org.exquery.restxq.annotation.PathAnnotation#extractPathParameters(java.lang.String) 
     */
    @Override
    public Map<String, String> extractPathParameters(final String uriPath) {
        
        final Map<String, String> pathParamNameAndValues = new HashMap<String, String>();        
        final Matcher m = getPathInformation().getPathMatcher(uriPath);        

        if(m.matches()) {
            for(int i = 1 ; i <= m.groupCount(); i++) {

                final String paramName = getPathInformation().getFnParamNameForGroup(i);
                final String paramValue = m.group(i);

                pathParamNameAndValues.put(paramName, paramValue);
            }
        }
        return pathParamNameAndValues;
    }

     /**
     * @see org.exquery.restxq.annotation.PathAnnotation#getPathSpecificityMetric() 
     */
    @Override
    public int getPathSpecificityMetric() {
        return getPathInformation().getPathSpecificityMetric();
    }
    
    /**
     * Get the Path Information
     * 
     * @return The Path Information
     */
    protected PathInformation getPathInformation(){
        return pathRegularExpression;
    }
    
    /**
     * Parses the Path literal of a Path Annotation
     * 
     * @return The Path Pattern and Group Parameter Names in the Pattern
     * 
     * @throws RestAnnotationException if the Path literal is invalid
     */
    protected PathInformation parsePath() throws RestAnnotationException {
        
        final Literal[] annotationValue = getLiterals();
        
        if(annotationValue.length != 1) {
            throw new RestAnnotationException(RestXqErrorCodes.RQST0001);
        }
        
        final Literal pathValue = annotationValue[0];
        if(pathValue.getType() != Type.STRING) {
            throw new RestAnnotationException(RestXqErrorCodes.RQST0002);
        }
        
        final String pathStr = pathValue.getValue();
        if(pathStr.isEmpty()) {
            throw new RestAnnotationException(RestXqErrorCodes.RQST0003);
        }

        //validate the Path
        final Matcher mchPath = ptnPath.matcher(pathStr);
        if(!mchPath.matches()) {
            throw new RestAnnotationException(RestXqErrorCodes.RQST0004);
        }

        //extract the Path segments
        final StringBuilder thisPathExprRegExp = new StringBuilder();
        final List<String> pathFnParams = new ArrayList<String>();

        final Matcher mchPathSegment = ptnPathSegment.matcher(pathStr);

        final Map<Integer, String> groupParamNames = new HashMap<Integer, String>();
        int groupCount = 0;

        int pathSpecificityMetric = 0;
        
        while(mchPathSegment.find()) {
            final String pathSegment = pathStr.substring(mchPathSegment.start(), mchPathSegment.end());

            final Matcher mtcFnParameter = functionArgumentPattern.matcher(pathSegment);

            thisPathExprRegExp.append(URI.PATH_SEGMENT_DELIMITER);

            /* 
             * if not the first segment,
             * left shift the last specifity segment of the path
             */
            if(pathSpecificityMetric > 0) {
                pathSpecificityMetric = pathSpecificityMetric << 2;
            }
            
            if(mtcFnParameter.matches()) {
                //is a path function parameter
                final String fnParamName = mtcFnParameter.replaceFirst("$1");
                pathFnParams.add(fnParamName);

                thisPathExprRegExp.append("(");
                thisPathExprRegExp.append(URI.pchar_regExp);
                thisPathExprRegExp.append(")");

                //record the position of the param in the path
                groupParamNames.put(++groupCount, fnParamName);
                
                //record the specifity of this path segment
                pathSpecificityMetric ^= PATH_SEGMENT_PARAM_SPECIFICITY;
            } else {
                //is just a string path segment
                thisPathExprRegExp.append("(?:");
                thisPathExprRegExp.append(Pattern.quote(pathSegment));
                thisPathExprRegExp.append(")");
                
                //record the specifity of this path segment
                pathSpecificityMetric ^= PATH_SEGMENT_SOLID_SPECIFICITY;
            }
        }

        //check the function that has this annotation has parameters as declared by the annotation
        checkFnDeclaresParameters(getFunctionSignature(), pathFnParams);

        //we now have a pattern for matching the URI path!
        final Pattern ptnThisPath = Pattern.compile(thisPathExprRegExp.toString());

        return new PathInformation(pathStr, ptnThisPath, groupParamNames, pathSpecificityMetric);
    }
    
    /**
     * Represents the extracted information from the parameter to the Path Annotation
     */
    protected class PathInformation {
        
        private final String pathLiteral;
        
        /**
         * Regular Expression to match a corresponding path with the Parameters of the Path setup as Groups in the Expression
         */
        private final Pattern ptnPath;
        
        /**
         * Map of Group indices in the Regular Expression (ptnPath) to Parameter Names
         */
        private final Map<Integer, String> groupParamNames;
        
        /**
         * Metric describing the path Specificity
         */
        private final int pathSpecificityMetric;
        
        /**
         *
         * @param pathLiteral The original path literal provided as the parameter to the Path Annotation
         * @param ptnPath The Regular Expression that matches a path against the pathLiteral
         * @param groupParamNames A mapping of group indexes in the regular expression to parameter names
         */
        public PathInformation(final String pathLiteral, final Pattern ptnPath, final Map<Integer, String> groupParamNames, final int pathSpecificityMetric) {
            this.pathLiteral = pathLiteral;
            this.ptnPath = ptnPath;
            this.groupParamNames = groupParamNames;
            this.pathSpecificityMetric = pathSpecificityMetric;
        }

        /**
         * Gets the original Path Literal
         * which was provided as the parameter
         * to the Path Annotation
         * 
         * @return The Path Literal
         */
        public String getPathLiteral() {
            return pathLiteral;
        }
        
        /**
         * Gets a Matcher for the Path Regular Expression
         * The Matcher enables you to process a Path
         * 
         * Note: Matchers are not thread safe, but can be
         * sequentially reused by calling .reset(str)
         * 
         * @param path A Path to process with the Path Regular Expression
         * 
         * @return The Mather for the Path Regular Expression
         */
        public Matcher getPathMatcher(final String path) {
            return ptnPath.matcher(path);
        }

        /**
         * Gets the Parameter Name for a Group in the Path Regular Expression
         * 
         * @param groupIndex The index of the Group in the Regular Expression
         *
         * @return The name of the Parameter for which a value
         * extracted by the Group at the index is provided
         */
        public String getFnParamNameForGroup(final int groupIndex) {
            return groupParamNames.get(groupIndex);
        }

        
        /**
         * Gets the specificity metric of this path
         * 
         * @return the Specificity metric of this path
         */
        public int getPathSpecificityMetric() {
            return pathSpecificityMetric;
        }
    }
}