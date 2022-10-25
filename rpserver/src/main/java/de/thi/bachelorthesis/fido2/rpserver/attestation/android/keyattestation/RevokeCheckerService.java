package de.thi.bachelorthesis.fido2.rpserver.attestation.android.keyattestation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RevokeCheckerService {
    @GET
    Call<ResponseBody> fetchRevokeList(@Url String url);
}
