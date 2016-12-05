package com.zeyad.usecases.data.network;

import android.support.annotation.NonNull;

import com.zeyad.usecases.BuildConfig;
import com.zeyad.usecases.Config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;

@RunWith(JUnit4.class)
//@Config(constants = BuildConfig.class)
public class IApiConnectionTest {

    private final String mValidUrl = ApiConnectionRobot.getValidUrl();
    private final RequestBody mMockedRequestBody = ApiConnectionRobot.getMockedRequestBody();
    private final Map<String, RequestBody> mPartMap = ApiConnectionRobot.getPartMap();
    private final MultipartBody.Part mMultipartBodyPart = ApiConnectionRobot.getValidMultipartBodyPart();
    private IApiConnection mApiConnection;
    private RestApi mRestApiWithCache;
    private RestApi mRestApiWithoutCache;

    @Before
    public void setUp() throws Exception {
//        Config.init(InstrumentationRegistry.getTargetContext());
        Config.getInstance().setUseApiWithCache(false);
        mRestApiWithCache = ApiConnectionRobot.createMockedRestApi();
        mRestApiWithoutCache = ApiConnectionRobot.createMockedRestApi();
        mApiConnection = getApiImplementation(mRestApiWithoutCache, mRestApiWithCache);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSetupDoneSuccessfully() {
        assertThat(getCurrentSetRestApiWithCache(mApiConnection), is(notNullValue()));
        assertThat(getCurrentSetRestApiWithoutCache(mApiConnection), is(notNullValue()));
        assertThat(mRestApiWithCache, is(notNullValue()));
        assertThat(mRestApiWithoutCache, is(notNullValue()));
        assertThat(getCurrentSetRestApiWithCache(mApiConnection), is(equalTo(mRestApiWithCache)));
        assertThat(getCurrentSetRestApiWithoutCache(mApiConnection), is(equalTo(mRestApiWithoutCache)));
    }

    @Test
    public void testProvideHttpLoggingInterceptor() throws Exception {
        HttpLoggingInterceptor httpLoggingInterceptor = mApiConnection.provideHttpLoggingInterceptor();
        assertThat(httpLoggingInterceptor.getLevel(), is(equalTo(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE)));
    }

    @Test
    public void testDynamicDownload() throws Exception {
        mApiConnection.dynamicDownload(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicDownload(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObject_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsTrue() throws Exception {
        mApiConnection.dynamicGetObject(mValidUrl, true);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObject_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsFalse() throws Exception {
        mApiConnection.dynamicGetObject(mValidUrl, false);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetObjectWithoutCacheSupport() throws Exception {
        mApiConnection.dynamicGetObject(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicGetObject(mValidUrl);
    }

    @Test
    public void testDynamicGetListWithoutCacheSupport() throws Exception {
        mApiConnection.dynamicGetList(mValidUrl);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetListCache_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsTrue() throws Exception {
        mApiConnection.dynamicGetList(mValidUrl, true);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicGetListCache_ifCorrectMethodOfCacheRestApiIsCalled_whenToCacheIsFalse() throws Exception {
        mApiConnection.dynamicGetList(mValidUrl, false);
        Mockito.verify(mRestApiWithoutCache).dynamicGetList(eq(mValidUrl));
    }

    @Test
    public void testDynamicPostObject() throws Exception {
        mApiConnection.dynamicPostObject(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPostObject(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPostList() throws Exception {
        mApiConnection.dynamicPostList(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPostList(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPutObject() throws Exception {
        mApiConnection.dynamicPutObject(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPutObject(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicPutList() throws Exception {
        mApiConnection.dynamicPutList(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicPutList(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testUploadPartAndRequestBody() throws Exception {
        mApiConnection.upload(mValidUrl, mPartMap, mMultipartBodyPart);
        Mockito.verify(mRestApiWithoutCache).upload(eq(mValidUrl), eq(mPartMap), eq(mMultipartBodyPart));
    }

    @Test
    public void testDynamicDeleteList() throws Exception {
        mApiConnection.dynamicDeleteList(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicDeleteList(eq(mValidUrl), eq(mMockedRequestBody));
    }

    @Test
    public void testDynamicDeleteObject() throws Exception {
        mApiConnection.dynamicDeleteObject(mValidUrl, mMockedRequestBody);
        Mockito.verify(mRestApiWithoutCache).dynamicDeleteObject(eq(mValidUrl), eq(mMockedRequestBody));
    }

    private RestApi getCurrentSetRestApiWithoutCache(@NonNull IApiConnection apiConnection) {
        return ((ApiConnection) apiConnection).getRestApiWithoutCache();
    }

    private RestApi getCurrentSetRestApiWithCache(@NonNull IApiConnection apiConnection) {
        return ((ApiConnection) apiConnection).getRestApiWithCache();
    }

    private IApiConnection getApiImplementation(RestApi restApiWithoutCache, RestApi restApiWithCache) {
        ApiConnection.init(restApiWithoutCache, restApiWithCache);
        return ApiConnection.getInstance();
    }
}