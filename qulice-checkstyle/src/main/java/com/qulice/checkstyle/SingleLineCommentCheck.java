/*
 * Copyright (c) 2011-2025 Yegor Bugayenko
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * C++ style inline comment is not allowed.
 * Use //-style comment instead.
 * @since 0.18
 */
public final class SingleLineCommentCheck extends AbstractCheck {

    /**
     * Pattern for check.
     * It is not final as it is initialized from the configuration.
     */
    private Pattern format = Pattern.compile("^$");

    /**
     * The message to report for a match.
     * It is not final as it is initialized from the configuration.
     */
    private String message = "";

    /**
     * Comment line.
     * It is not final because the visitToken method is called many times
     * during the class under test and the field is reinitialized with a new object.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private StringBuilder line;

    /**
     * When inside a block comment, holds begin line number.
     */
    private int begin;

    @Override
    public boolean isCommentNodesRequired() {
        return true;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
            TokenTypes.BLOCK_COMMENT_BEGIN,
            TokenTypes.COMMENT_CONTENT,
            TokenTypes.BLOCK_COMMENT_END,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.BLOCK_COMMENT_BEGIN) {
            this.line = new StringBuilder(ast.getText());
            this.begin = ast.getLineNo();
        } else if (ast.getType() == TokenTypes.COMMENT_CONTENT) {
            this.line.append(ast.getText());
        } else {
            this.line.append(ast.getText());
            final Matcher matcher = this.format.matcher(this.line.toString());
            if (matcher.matches() && this.singleLineCStyleComment(ast)) {
                this.log(ast, this.message);
            }
        }
    }

    /**
     * The method is called from checkstyle to configure this class.
     * The parameter is set from the checks.xml file
     * <module name="com.qulice.checkstyle.SingleLineCommentCheck"/> and
     * <property name="format" value=" this regexp "/> property
     *
     * @param fmt Validatig regexp.
     */
    public void setFormat(final String fmt) {
        this.format = Pattern.compile(fmt);
    }

    /**
     * The method is called from checkstyle to configure this class.
     * The parameter is set from the checks.xml file
     * <module name="com.qulice.checkstyle.SingleLineCommentCheck"/> and
     * <property name="message" value="This kind of comment is not allowed."/>
     * property
     *
     * @param msg Error message.
     */
    public void setMessage(final String msg) {
        this.message = msg;
    }

    /**
     * Checks for the end of a comment line.
     * @param ast Checkstyle's AST nodes.
     * @return True if this is the end of the comment
     *  and the starting line number is equal to the ending line number.
     */
    private boolean singleLineCStyleComment(final DetailAST ast) {
        return ast.getType() == TokenTypes.BLOCK_COMMENT_END && this.begin == ast.getLineNo();
    }
}
