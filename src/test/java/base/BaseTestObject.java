package base;

import com.cloud.api.base.APIBaseService;
import com.cloud.api.base.APIBaseTest;
import com.cloud.api.dto.BaseResponseDTO;
import com.cloud.api.headers.DynamicHeaders;
import com.cloud.core.config.enums.ConfigKeys;
import com.cloud.core.testdataprovider.enums.DataProviderType;
import com.cloud.core.testdataprovider.utils.DataProviderUtil;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import io.restassured.http.Header;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.asserts.SoftAssert;

import java.util.EnumSet;
import java.util.HashMap;

public abstract class BaseTestObject extends APIBaseTest {

    public static HashMap<String, String> loginData = null;
    public static HashMap<String, String> urlData = null;
    public static HashMap<String, String> billingData = null;
    public static HashMap<String, String> approvalData = null;
    public static HashMap<String, String> invoiceData = null;
    public static HashMap<String, String> generalData = null;
    public static HashMap<String, String> admissionData = null;
    public static HashMap<String, String> promotionData = null;
    public static HashMap<String, String> regressionData = null;
    public static Header authorizationHeader = null;

    @BeforeSuite
    public void loadEnvironmentData() throws Exception {

        try {
            setSystemProperties();

            String path = config.getValue(ConfigKeys.KEY_DATA_FILE_PATH.getKey()) + config.getValue(ConfigKeys.KEY_ENVIRONMENT.getKey()).toLowerCase();
            DataProviderUtil.setDataFile(path, DataProviderType.PROPERTY);

            APIBaseService.OBJECT_MAPPER.setAnnotationIntrospector(new IgnoreInheritedIntrospector());


        } catch (Exception e) {
            throw new Exception("Failed : loadEnvironmentData()" + e.getLocalizedMessage());
        }
    }

    public static class IgnoreInheritedIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public boolean hasIgnoreMarker(final AnnotatedMember m) {
            return m.getDeclaringClass() == BaseResponseDTO.class || super.hasIgnoreMarker(m);
        }
    }


    @BeforeMethod
    public void beforeMethod() throws Exception {

        try {
            softAssert = new SoftAssert();

        } catch (Exception e) {
            throw new Exception("Failed : beforeMethod()" + e.getLocalizedMessage());
        }
    }

    private void setSystemProperties() {

        String environment = System.getProperty("environment");
        String updateTestRail = System.getProperty("updateTestRail");
        String testType = System.getProperty("testType");

        if(environment != null) config.setValue(ConfigKeys.KEY_ENVIRONMENT.getKey(), environment);
        if(updateTestRail != null) config.setValue(ConfigKeys.KEY_UPDATE_TESTRAIL.getKey(), updateTestRail);
        if (testType != null) config.setValue(ConfigKeys.KEY_TEST_TYPE.getKey(), testType);
    }

}
