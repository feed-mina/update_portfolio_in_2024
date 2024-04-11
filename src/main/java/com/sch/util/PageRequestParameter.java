package com.sch.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 페이지 요청정보 파라미터 정보 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageRequestParameter<T> {

    private MySQLPageRequest pageRequest;
    private T parameter;
}
