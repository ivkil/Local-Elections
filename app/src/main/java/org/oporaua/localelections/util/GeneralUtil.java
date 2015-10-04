package org.oporaua.localelections.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import org.oporaua.localelections.R;
import org.oporaua.localelections.violations.model.ViolationChild;
import org.oporaua.localelections.violations.model.ViolationParent;

import java.util.ArrayList;
import java.util.List;

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
        if (view == null)
            return;
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

}
