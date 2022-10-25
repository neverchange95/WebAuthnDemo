package de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation;

import okhttp3.ResponseBody;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

@Service
public class RevokeCheckerClient {
    @Cacheable("androidKeyAttestationRevokeList")
    public Response<ResponseBody> fetchAndroidKeyAttestationRevokeList(String url) throws IOException {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl("https://android.googleapis.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        return retrofit.create(RevokeCheckerService.class).fetchRevokeList(url).execute();
    }
}
