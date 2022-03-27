package com.mobile.core.basecomponents;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestExecutionContext {

    private static final TestExecutionContext testExecutionContext_instance = new TestExecutionContext();
    private static final ThreadLocal<TestExecutionCtx> ctx = new InheritableThreadLocal<>();


    public static TestExecutionContext getInstance(){
        return testExecutionContext_instance;
    }

    void initContext(String pathToTestLogs, String relativeLogPathInSuite){
        clearThreadLocals();
        log.info("Initializing test execution with pathToTestLogs : {} &,relativeLogPathInSuite: {}", pathToTestLogs, relativeLogPathInSuite);
        ctx.set(new TestExecutionCtx(pathToTestLogs,relativeLogPathInSuite));
    }

    private void clearThreadLocals(){
        try{
            ctx.remove();
        }
        catch (Exception e){}
    }

    @Slf4j
    @Getter
    private class TestExecutionCtx{

        private final String pathToTestLogs;
        private final String relativeLogPathInSuite;

        private TestExecutionCtx(String pathToTestLogs, String relativeLogPathInSuite) {
            this.pathToTestLogs = pathToTestLogs;
            this.relativeLogPathInSuite = relativeLogPathInSuite;
        }

    }

}
