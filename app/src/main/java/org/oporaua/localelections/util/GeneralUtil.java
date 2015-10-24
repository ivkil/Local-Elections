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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("https://dts2015.oporaua.org/violations/add.json");
                MultipartEntityBuilder entity = MultipartEntityBuilder.create();

                entity.addTextBody("date","2015-10-18");
                entity.addTextBody("accident_subtype_id","1");
                entity.addTextBody("source","65465");
                entity.addTextBody("last_ip","176.38.35.4");
                entity.addTextBody("locality_id","12172");
                entity.addTextBody("region_id","13");
                entity.addTextBody("election_id","1");
                entity.addTextBody("title","TITLE");
                entity.addTextBody("lat","49.32512199104001");
                entity.addTextBody("lang","32.607421875");

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                InputStream in = new ByteArrayInputStream(bos.toByteArray());
                ContentBody photo = new InputStreamBody(in, "compressedFile");
                entity.addPart("evidence", photo);
                httpPost.setEntity(entity.build());
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity httpEntity = response.getEntity();
                    String output = EntityUtils.toString(httpEntity);
//                    JSONObject jObj = new JSONObject(output);
                    subscriber.onNext(output);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

}
