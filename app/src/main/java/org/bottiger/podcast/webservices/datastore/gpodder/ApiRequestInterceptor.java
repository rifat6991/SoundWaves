package org.bottiger.podcast.webservices.datastore.gpodder;

/**
 * Created by apl on 23-05-2015.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Interceptor used to authorize requests.
 */
public class ApiRequestInterceptor implements Interceptor {

    @Nullable private String mUsername;
    @Nullable private String mPassword;

    public static String cookie;

    public ApiRequestInterceptor(@Nullable  String argUsername, @Nullable String argPassword) {
        mUsername = argUsername;
        mPassword = argPassword;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        boolean authenticating = false;
        if (shouldAuthenticate()) {
            if (!TextUtils.isEmpty(cookie)) {
                requestBuilder.addHeader("Cookie", cookie);
            } else {
                final String authorizationValue = encodeCredentialsForBasicAuthorization();
                //requestFacade.addHeader("Authorization", authorizationValue);
                //request.headers().newBuilder().add("Authorization", authorizationValue).build();
                requestBuilder.addHeader("Authorization", authorizationValue);
                authenticating = true;
            }
        }

        request = requestBuilder.build();
/*
    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
 */
        //String body = bodyToString(request);
        final Buffer buffer = new Buffer();
        Response response = chain.proceed(request);

        //body = body + "1";
        if (authenticating) {
            Headers headers = response.headers();
            int numHeaders = headers.size();

            String name, value;
            for (int i = 0; i < numHeaders; i++) {
                name = headers.name(i);
                value = headers.value(i);
                if (name.equals("Set-Cookie") && value.startsWith("sessionid")) {
                    ApiRequestInterceptor.cookie = value.split(";")[0];
                }
            }
        }

        /**
         * FIXME: Remove when this have been fixed: https://github.com/square/retrofit/issues/1071
         */
        //if (response.body().contentLength() < 0) {
        String r = response.body().string();

        if (TextUtils.isEmpty(r)) {
            r = "{}";
        }

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), r))
                    .build();
        //}


        //return response;
    }

    private String encodeCredentialsForBasicAuthorization() {
        final String userAndPassword = mUsername + ":" + mPassword;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes(), Base64.NO_WRAP);
    }

    private boolean shouldAuthenticate() {
        return mUsername != null;
    }
}
