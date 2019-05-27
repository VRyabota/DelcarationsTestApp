package com.vrb.apps.test.data.api

import com.google.gson.reflect.TypeToken
import com.vrb.apps.test.data.models.Declaration
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class EnvelopeConverter : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<out Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, Any>? {

        val envelopedType: Type = TypeToken.getParameterized(Envelope::class.java, type).type

        val delegate: Converter<ResponseBody, Envelope> = retrofit!!.nextResponseBodyConverter(
            this,
            envelopedType,
            annotations
        )

        return Converter { value ->
            val envelope: Envelope = delegate.convert(value)
            if (envelope.error != null){
                emptyList()
            } else {
                envelope.items
            }
        }
    }


}

private data class Envelope(val page: Any, val items: List<Declaration>, val error: Any?, val message: Any?)