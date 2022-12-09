package io.github.masachi.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CheckResult implements Serializable {

    int code = 0;

    Boolean haveToken = true;

    String message;

    public static CheckResult setError(String errorMsg) {
        CheckResult checkResult = new CheckResult();
        checkResult.setCode(1);
        checkResult.setMessage(errorMsg);
        return checkResult;
    }

    public static CheckResult setError(int code, String errorMsg) {
        CheckResult checkResult = new CheckResult();
        checkResult.setMessage(errorMsg);
        checkResult.setCode(code);
        return checkResult;
    }

    public static CheckResult success() {
        return new CheckResult();
    }

    public CheckResult setHaveToken(Boolean have) {
        this.haveToken = have;
        return this;
    }

    public Boolean isSuccess() {
        return this.getCode() == 0;
    }

    public Boolean isFail() {
        return !isSuccess();
    }

    public Boolean checkCode(int code) {
        return this.getCode() == code;
    }

}
