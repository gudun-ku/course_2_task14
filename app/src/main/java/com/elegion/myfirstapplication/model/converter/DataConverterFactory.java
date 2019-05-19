package com.elegion.myfirstapplication.model.converter;

import com.elegion.myfirstapplication.APIDataEnvelope;
import com.elegion.myfirstapplication.model.Data;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.annotations.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class DataConverterFactory extends Converter.Factory {
    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        if(!canHandle(annotations))
            return null;

        Type envelopedType = TypeToken.getParameterized(Data.class, type).getType();

        final Converter<ResponseBody, Data> delegate = retrofit.nextResponseBodyConverter(this,envelopedType,annotations);

        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody body) throws IOException {
                Data<?> data = delegate.convert(body);
                return data.response;
            }
        };
    }

    private boolean canHandle(Annotation[] annotations) {
        for (Annotation annotation : annotations) {

            if (APIDataEnvelope.class == annotation.annotationType()) {
                return ((APIDataEnvelope) annotation).hasEnvelope();
            }
        }
        return true;
    }


}
