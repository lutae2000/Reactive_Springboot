package com.greglturnquist.hackingspringbootch2reactive.entity;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;

/* actuator 에서 어떤 인터페이스에서 HTTP 요청을 했는지 확인 */
public class HttpTraceWrapper {
    private @Id String id;
    private HttpTrace httpTrace;

    public HttpTraceWrapper(HttpTrace httpTrace) {
        this.httpTrace = httpTrace;
    }

    public HttpTrace getHttpTrace() {
        return httpTrace;
    }
}
