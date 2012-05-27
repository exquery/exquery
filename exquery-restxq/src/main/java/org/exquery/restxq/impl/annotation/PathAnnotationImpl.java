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
import org.exquery.restxq.RESTXQAnnotationException;
import org.exquery.restxq.RESTXQErrorCodes;
import org.exquery.restxq.annotation.PathAnnotation;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;

/**
 * Implementation of RESTXQ Path Annotation
 * e.g. %rest:path
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class PathAnnotationImpl extends AbstractRESTAnnotation implements PathAnnotation {
    
    private final static char URI_PATH_SEGMENT_DELIMITER = '/';
    
    /**
     * URI path segment valid characters from RFC 3986
     * 
     * http://labs.apache.org/webarch/uri/rfc/rfc3986.html#collected-abnf
     * 
     * pchar         = unreserved / pct-encoded / sub-delims / ":" / "@"
     * unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * pct-encoded   = "%" HEXDIG HEXDIG
     * sub-delims    = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     */
    private final static String unreservedRegExp = "[A-Za-z0-9\\-\\._~]";
    private final static String pctEncodedRegExp = "%[A-F0-9]{2}";
    private final static String subDelimsRegExp = "[!\\$&'\\(\\)\\*\\+,;=]";
    
    //capturing
    private final static String cPcharRegExp = "(" + unreservedRegExp + "|" + pctEncodedRegExp + "|" + subDelimsRegExp + "|[\\:@]" + ")+";
    
    //non-capturing
    private final static String ncPcharRegExp = "(?:" + unreservedRegExp + "|" + pctEncodedRegExp + "|" + subDelimsRegExp + "|[\\:@]" + ")+";
    
    //combines official RFC URI path segment regexp  with our encoded function argument regexp
    private final static String pathSegmentRegExp = "(?:"  + ncPcharRegExp + "|" + functionArgumentRegExp + ")";
    
    //path segment extractor
    private final static Pattern ptnPathSegment = Pattern.compile(pathSegmentRegExp);
    
    //regexp to validate entire path
    private final static String pathRegExp = "^(?:" + URI_PATH_SEGMENT_DELIMITER + "?" + pathSegmentRegExp + ")+$";
    
    //validator for Path
    private final static Pattern ptnPath = Pattern.compile(pathRegExp);
    
    private PathMatcherAndGroupParamNames pathMatcherAndGroupParamNames;
    
    private int pathSegmentCount = -1;
    
    
    /**
     * Ensures that the Path Annotation
     * is compatible with the Function Signature
     * and extracts templates from the path
     * for later use
     * 
     * @throws  RESTXQAnnotationException if the Path Annotation is not compatible
     * with the function signature or if the path is malformed
     */
    @Override
    public void initialise() throws RESTXQAnnotationException {
        super.initialise();
        this.pathMatcherAndGroupParamNames = parsePath(); 
    }
    
    /**
     * @see org.exquery.restxq.annotation.PathAnnotation#matchesPath(java.lang.String)
     */
    @Override
    public boolean matchesPath(final String path) {
        final Matcher m = getPathMatcherAndParamIndicies().getPathMatcher(path);
        return m.matches();
    }
    
    /**
     * @see org.exquery.restxq.annotation.PathAnnotation#extractPathParameters(java.lang.String) 
     */
    @Override
    public Map<String, String> extractPathParameters(final String uriPath) {
        
        final Map<String, String> pathParamNameAndValues = new HashMap<String, String>();        
        final Matcher m = getPathMatcherAndParamIndicies().getPathMatcher(uriPath);        

        if(m.matches()) {
            for(int i = 1 ; i <= m.groupCount(); i++) {

                final String paramName = getPathMatcherAndParamIndicies().getFnParamNameForGroup(i);
                final String paramValue = m.group(i);

                pathParamNameAndValues.put(paramName, paramValue);
            }
        }
        return pathParamNameAndValues;
    }
    
    /**
     * @see org.exquery.restxq.annotation.PathAnnotation#getPathSegmentCount() 
     */
    @Override
    public int getPathSegmentCount() {
        return pathSegmentCount;
    }
    
    private PathMatcherAndGroupParamNames getPathMatcherAndParamIndicies(){
        return pathMatcherAndGroupParamNames;
    }
    
    private void setPathSegmentCount(int pathSegmentCount) {
        this.pathSegmentCount = pathSegmentCount;
    }
    
    private PathMatcherAndGroupParamNames parsePath() throws RESTXQAnnotationException {
        
        final Literal[] annotationValue = getLiterals();
        
        if(annotationValue.length != 1) {
            throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0001);
        }
        
        final Literal pathValue = annotationValue[0];
        if(pathValue.getType() != Type.STRING) {
            throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0002);
        }
        
        final String pathStr = pathValue.getValue();
        if(pathStr.isEmpty()) {
            throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0003);
        }

        //validate the Path
        final Matcher mchPath = ptnPath.matcher(pathStr);
        if(!mchPath.matches()) {
            throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0004);
        }

        //extract the Path segments
        final StringBuilder thisPathExprRegExp = new StringBuilder();
        final List<String> pathFnParams = new ArrayList<String>();

        final Matcher mchPathSegment = ptnPathSegment.matcher(pathStr);

        int segmentCount = 0;

        final Map<Integer, String> groupParamNames = new HashMap<Integer, String>();
        int groupCount = 0;

        while(mchPathSegment.find()) {
            final String pathSegment = pathStr.substring(mchPathSegment.start(), mchPathSegment.end());

            final Matcher mtcFnParameter = functionArgumentPattern.matcher(pathSegment);

            thisPathExprRegExp.append(URI_PATH_SEGMENT_DELIMITER);

            if(mtcFnParameter.matches()) {
                //is a path function parameter
                final String fnParamName = mtcFnParameter.replaceFirst("$1");
                pathFnParams.add(fnParamName);

                //thisPathExprRegExp.append(cPcharRegExp);
                thisPathExprRegExp.append("(");
                thisPathExprRegExp.append(ncPcharRegExp);
                thisPathExprRegExp.append(")");

                //record the position of the param in the path
                groupParamNames.put(++groupCount, fnParamName);
            } else {
                //is just a string path segment
                thisPathExprRegExp.append("(?:");
                thisPathExprRegExp.append(Pattern.quote(pathSegment));
                thisPathExprRegExp.append(")");
            }
            segmentCount++;
        }
        setPathSegmentCount(segmentCount);

        //check the function that has this annotation has parameters as declared by the annotation
        checkFnDeclaresParameters(getFunctionSignature(), pathFnParams);

        //we now have a pattern for matching the URI path!
        final Pattern ptnThisPath = Pattern.compile(thisPathExprRegExp.toString());

        return new PathMatcherAndGroupParamNames(ptnThisPath, groupParamNames);
    }
    
    private class PathMatcherAndGroupParamNames {
        final Pattern ptnPath;
        final Map<Integer, String> groupParamNames;
        
        public PathMatcherAndGroupParamNames(final Pattern ptnPath, final Map<Integer, String> groupParamNames) {
            this.ptnPath = ptnPath;
            this.groupParamNames = groupParamNames;
        }

        public Matcher getPathMatcher(final String path) {
            return ptnPath.matcher(path);
        }

        public String getFnParamNameForGroup(final int groupId) {
            return groupParamNames.get(groupId);
        }
    }
}