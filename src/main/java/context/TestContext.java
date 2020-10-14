package context;

import managers.APIObjectManager;

public class TestContext {
    private APIObjectManager apiObjectManager;

    public TestContext(){
        apiObjectManager = new APIObjectManager();
    }

    public APIObjectManager getApiObjectManager() {
        return apiObjectManager;
    }

}
