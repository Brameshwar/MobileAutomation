package com.mobile.pages;

import com.mobile.core.basecomponents.BaseAppPage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.TreeMap;

@Getter
@Slf4j
public class SearchPage extends BaseAppPage<HomePage> {

    public SearchPage(BaseAppPage page) {
        super(page);
        PageFactory.initElements(driver,this);
    }

    @AndroidFindBy(xpath="Xpath for Android")
    private MobileElement firstElement;


    @AndroidFindBy(xpath="Xpath for Android")
    private List<MobileElement> searchResult;



    public boolean validateSearchResult(int number, String order, String searchKey){

        try {

            TreeMap<Double, String> priceWithName = new TreeMap<>();

            for (MobileElement element : searchResult) {

                String itemName = element.findElementByXPath("xpath for name relative to search")
                        .getAttribute("");

                if (!itemName.contains(searchKey)) {
                    log.error("Item name: {} found for search key: {}", itemName, searchKey);
                    return false;
                }

                Double itemPrice = Double.parseDouble(element.findElementByXPath("xpath for price relative to search")
                        .getAttribute(""));

                priceWithName.put(itemPrice, itemName);
            }

            if (order.equalsIgnoreCase("desc")) {
                log.info("Printing the items in descending Order");
                while (number > 0) {
                    log.info(String.valueOf(priceWithName.pollLastEntry()));
                    number--;
                }

            } else {
                log.info("Printing the items in descending Order");
                while (number > 0) {
                    log.info(String.valueOf(priceWithName.pollFirstEntry()));
                    number--;
                }
            }
            return true;
        }
        catch (Exception e){
            log.error(e.getStackTrace().toString());
            return false;
        }


    }

}
