/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.nuls.kernel.constant;

import io.nuls.kernel.i18n.I18nUtils;

/**
 * @author: Niels Wang
 * @date: 2018/5/5
 */
public class ErrorCode {
    private final int msg;
    private final String code;

    protected ErrorCode(String code, int msg) {
        this.code = code;
        this.msg = msg;
        if (null == code) {
            throw new RuntimeException("the errorcode code cann't be null!");
        }
    }

    public String getMsg() {
        return I18nUtils.get(msg);
    }

    public String getCode() {
        return code;
    }

    public static final ErrorCode init(String code, int msg) {
        return new ErrorCode(code, msg);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ErrorCode)) {
            return false;
        }
        return code.equals(((ErrorCode) obj).getCode());
    }
}
