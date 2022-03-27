package com.mobile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.core.annotations.DataProviderParams;
import com.mobile.core.annotations.TestInfo;
import com.mobile.core.basecomponents.BaseAppTest;
import com.mobile.core.enums.TestPriorityEnum;
import com.mobile.core.utils.TestDataProvider;
import com.mobile.dto.SampleTestData;
import com.mobile.pages.HomePage;
import com.mobile.pages.SearchPage;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


@Slf4j
public class SampleTestCase extends BaseAppTest {

    @DataProviderParams(
            fileName = "demo.json",
            jsonKey = "testAndroid"
    )
    @TestInfo(
            testId = "SEARCH_TC_001",
            testOwner = "bramesh.srit@gmail.com",
            priority = TestPriorityEnum.P1
    )
    @Test(dataProviderClass = TestDataProvider.class,
            dataProvider = "JsonDataProvider",
            groups = {"search"}
    )
    public void validateSearch(JSONObject jsonObject) {
        log.info("Staring the validation for Search functionality");
        final SoftAssert s_assert = new SoftAssert();
        HomePage homePage = new HomePage(getAppDriver());
        SearchPage searchPage = new SearchPage(getAppDriver());

        SampleTestData sampleTestData = new ObjectMapper().convertValue(jsonObject, SampleTestData.class);

        s_assert.assertTrue(homePage.checkIfExists(homePage.getFlipKartLogo()));

        log.info("Performing search using keyword as {}",sampleTestData.getSearchKey());
        s_assert.assertTrue(homePage.enterTextAndClickEnter(homePage.getSearchText(),sampleTestData.getSearchKey()));


        log.info("Validating the search response");
        s_assert.assertTrue(searchPage.validateSearchResult(Integer.valueOf(sampleTestData.getNumberOfOutput()),
                "DESC",sampleTestData.getSearchKey()));

        s_assert.assertAll();

    }
}
