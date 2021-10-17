package cloud.runningpig.bearnote.logic.network

import cloud.runningpig.bearnote.logic.utils.LogUtil
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.nio.charset.StandardCharsets

class LoggingInterceptor : Interceptor {

    companion object {
        private const val TAG = "LoggingInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()
//        val scheme = url.scheme()
//        val host = url.host()
//        val port = url.port()
//        val path = url.encodedPath()
        val query = url.query()
        val requestMethod = request.method()
        val requestHeaders = request.headers()
        val requestBody = request.body()
        var requestBodyString: String? = requestBodyToString(requestBody)
        LogUtil.dl(
            TAG, String.format(
                "发送请求:\nmethod: %s\nheaders: %s\nurl: %s\nquery: %s\nbody: %s\n",
                requestMethod,
                requestHeaders,
                url,
                query,
                requestBodyString
            )
        )
        // 打印返回报文，先执行请求，才能获取报文
        val response = chain.proceed(request)
        val responseBody = response.body()
        val responseCode = response.code()
        val responseMessage = response.message()
        val responseHeaders = response.headers()
        val requestUrl = response.request().url()
        requestBodyString = requestBodyToString(response.request().body())
        val responseBodyString: String? = responseBodyToString(responseBody)
        val contentLength = responseBody?.contentLength()
        LogUtil.dl(
            TAG, String.format(
                "收到响应:\ncode: %s\nmessage: %s\nheaders: %s请求url: %s\n请求body: %s\n响应body: %s\n响应bodySize: %s\n",
                responseCode,
                responseMessage,
                responseHeaders,
                requestUrl,
                requestBodyString,
                responseBodyString,
                contentLength
            )
        )
        return response
    }

    // 请求body转String
    private fun requestBodyToString(requestBody: RequestBody?): String? {
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            return buffer.readUtf8()
        }
        return null
    }

    // 响应body转String
    private fun responseBodyToString(responseBody: ResponseBody?): String? {
        if (responseBody != null) {
            // 不能使用responseBody.string()，否则response会close，在onResponse()回调获取不到response
            // bodyString = responseBody.string();
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            var charset = StandardCharsets.UTF_8
            val contentType = responseBody.contentType()
            charset = contentType?.charset(charset)
            return buffer.clone().readString(charset)
        }
        return null
    }

}