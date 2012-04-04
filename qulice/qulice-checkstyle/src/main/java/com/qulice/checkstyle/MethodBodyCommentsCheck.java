/**
 * Copyright (c) 2011-2012, Qulice.com
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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks method bodies for comments. All comments in method bodies are
 * prohibited.
 *
 * <p>We believe that in-code comments and empty lines are evil. If you
 * need to use
 * a comment inside a method - your code needs refactoring. Either move that
 * comment to a method javadoc block or add a logging mechanism with the same
 * text.
 *
 * @author Dmitry Bashkin (dmitry.bashkin@qulice.com)
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class MethodBodyCommentsCheck extends Check {

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitToken(final DetailAST ast) {
        final DetailAST start = ast.findFirstToken(TokenTypes.SLIST);
        if (start != null) {
            this.checkMethod(
                this.getLines(),
                start.getLineNo(),
                start.findFirstToken(TokenTypes.RCURLY).getLineNo() - 1
            );
        }
    }

    /**
     * Checks method body for comments.
     * @param lines Array of lines, containing code to check.
     * @param start Start line of the method body.
     * @param end End line of the method body.
     */
    private void checkMethod(final String[] lines, final int start,
        final int end) {
        final boolean oneliner = start == end - 1;
        for (int pos = start; pos < end; ++pos) {
            final String line = lines[pos].trim();
            if (line.startsWith("//")) {
                final String comment = line.substring(2).trim();
                if (!comment.startsWith("@checkstyle") && !oneliner) {
                    this.log(pos + 1, "Comments in method body are prohibited");
                }
            }
        }
    }
}
