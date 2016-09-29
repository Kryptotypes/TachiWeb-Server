/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts.sync.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 26/08/16
 *
 * Simple HTTP client that uses URLConnections
 */
public class DefaultHttpClient implements HttpClient {
    @Override
    public String postRequest(String url, Map<String, String> headers, String postBody)
            throws IOException {
        return request(Protocol.POST, url, headers, postBody);
    }

    @Override
    public String getRequest(String url, Map<String, String> headers) throws IOException {
        return request(Protocol.GET, url, headers, null);
    }

    private String request(
            Protocol protocol, String url, Map<String, String> headers, String postBody)
            throws IOException {
        boolean isPost = protocol == Protocol.POST;
        URL parsedUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) parsedUrl.openConnection();
        connection.setRequestMethod(protocol.name());
        if (isPost) connection.setDoOutput(true);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }
        connection.connect();
        String result;
        OutputStream stream = null;
        InputStream inputStream = null;
        try {
            if (isPost) {
                stream = connection.getOutputStream();
                stream.write(postBody.getBytes("UTF-8"));
            }
            inputStream = connection.getInputStream();
            Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
            result = s.next();
        } finally {
            if(stream != null) {
                stream.close();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return result;
    }

    private enum Protocol {
        GET,
        POST
    }
}