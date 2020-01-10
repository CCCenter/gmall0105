package com.bbu.gmall.passport.sourceType;

public enum PlatformPType {

    WEIBO(1),
    ;
    private Integer code;
    PlatformPType(Integer code){
        this.code = code;
    }
    public Integer getCode(){
        return code;
    }
}
