package com.maeng0830.core.web;

import com.maeng0830.core.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogDemoService {

    private final ObjectProvider<MyLogger> myLoggersProvider;

    public void logic(String id) {
        MyLogger myLogger = myLoggersProvider.getObject();
        myLogger.log("service id = " + id);
    }
}
