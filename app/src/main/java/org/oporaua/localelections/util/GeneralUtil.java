package org.oporaua.localelections.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.oporaua.localelections.R;
import org.oporaua.localelections.accidents.AccidentPost;
import org.oporaua.localelections.violations.model.ViolationChild;
import org.oporaua.localelections.violations.model.ViolationParent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MIME;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.ContentBody;
import cz.msebera.android.httpclient.entity.mime.content.InputStreamBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.util.EntityUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public final class GeneralUtil {

    @NonNull
    public static List<ParentListItem> getItemList(Context context) {

        Resources res = context.getResources();
        TypedArray childViolationsNames = res
                .obtainTypedArray(R.array.violation_child_names_arrays);
        TypedArray childViolationsSources = res
                .obtainTypedArray(R.array.violation_child_source_arrays);

        String[] parentViolationNames = res.getStringArray(R.array.violation_parent_names);

        List<ParentListItem> violationParents = new ArrayList<>(parentViolationNames.length);

        for (int i = 0; i < parentViolationNames.length; i++) {
            int childViolationsNameId = childViolationsNames.getResourceId(i, -1);
            int childViolationsSourceId = childViolationsSources.getResourceId(i, -1);

            List<ViolationChild> childList = new ArrayList<>();

            String[] names = res.getStringArray(childViolationsNameId);
            String[] sources = res.getStringArray(childViolationsSourceId);

            for (int j = 0; j < names.length; j++) {
                childList.add(new ViolationChild(names[j], sources[j]));
            }

            violationParents.add(new ViolationParent(parentViolationNames[i], childList));
        }

        childViolationsNames.recycle();
        childViolationsSources.recycle();

        return violationParents;
    }

    public static void hideKeyBoard(Context context, View view) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyBoard(Context context, View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void unsubscribeSubscription(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public static boolean isPlayServicesAvailable(Activity context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(context, resultCode, Constants.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("PlayServices", "This device is not supported.");
                context.finish();
            }
            return false;
        }
        return true;
    }

    public static String getFriendlyDayString(Date date) {
        SimpleDateFormat friendlyDateFormat = new SimpleDateFormat("dd MMM yyyy 'р.'", new Locale("uk"));
        return friendlyDateFormat.format(date);
    }


    public static Observable<String> submitAccident(final AccidentPost accidentPost, final Bitmap bitmap) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(Constants.ACCIDENTS_BASE_URL + "/violations/add.json");

                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", new Locale("ua"));

                    StringBody date = new StringBody(format.format(accidentPost.getDate()), ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String subtypeStr = Long.toString(accidentPost.getAccidentSubtypeId());
                    StringBody subtype = new StringBody(subtypeStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String sourseStr = accidentPost.getSource();
                    StringBody source = new StringBody(sourseStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String ipStr = accidentPost.getLastIp();
                    StringBody ip = new StringBody(ipStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String localityStr = Long.toString(accidentPost.getLocalityId());
                    StringBody locality = new StringBody(localityStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String regionStr = Long.toString(accidentPost.getRegionId());
                    StringBody region = new StringBody(regionStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String electionsStr = Long.toString(accidentPost.getElectionsId());
                    StringBody elections = new StringBody(electionsStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String titleStr = accidentPost.getTitle();
                    StringBody title = new StringBody(titleStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String latStr = Double.toString(accidentPost.getLatitude());
                    StringBody lat = new StringBody(latStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String lngStr = Double.toString(accidentPost.getLongitude());
                    StringBody lng = new StringBody(lngStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String emailStr = accidentPost.getUserEmail() == null ? "" : accidentPost.getUserEmail();
                    StringBody email = new StringBody(emailStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String offenderStr = accidentPost.getOffender() == null ? "" : accidentPost.getOffender();
                    StringBody offender = new StringBody(offenderStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String offenderIdStr = Long.toString(accidentPost.getOffenderPartyId());
                    StringBody offenderId = new StringBody(offenderIdStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String beneficiaryStr = accidentPost.getBeneficiary() == null ? "" : accidentPost.getBeneficiary();
                    StringBody beneficiary = new StringBody(beneficiaryStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String beneficiaryIdStr = Long.toString(accidentPost.getBeneficiaryPartyId());
                    StringBody beneficiaryId = new StringBody(beneficiaryIdStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String victimStr = accidentPost.getVictim() == null ? "" : accidentPost.getVictim();
                    StringBody victim = new StringBody(victimStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    String victimIdStr = Long.toString(accidentPost.getVictimPartyId());
                    StringBody victimId = new StringBody(victimIdStr, ContentType.create("text/plain", MIME.UTF8_CHARSET));

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                    }
                    InputStream in = new ByteArrayInputStream(bos.toByteArray());

                    ContentBody mimePart = new InputStreamBody(in, "evidence");

                    HttpEntity reqEntity = MultipartEntityBuilder.create()
                            .addPart("email", email)
                            .addPart("date", date)
                            .addPart("accident_subtype_id", subtype)
                            .addPart("offender", offender)
                            .addPart("offender_party_id", offenderId)
                            .addPart("victim", victim)
                            .addPart("victim_party_id", victimId)
                            .addPart("beneficiary", beneficiary)
                            .addPart("beneficiary_party_id", beneficiaryId)
                            .addPart("source", source)
                            .addPart("evidence", mimePart)
                            .addPart("last_ip", ip)
                            .addPart("locality_id", locality)
                            .addPart("region_id", region)
                            .addPart("election_id", elections)
                            .addPart("title", title)
                            .addPart("lat", lat)
                            .addPart("lang", lng)
                            .build();

                    httpPost.setEntity(reqEntity);
                    HttpResponse response = httpClient.execute(httpPost, localContext);
                    String result = EntityUtils.toString(response.getEntity());
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }


}
