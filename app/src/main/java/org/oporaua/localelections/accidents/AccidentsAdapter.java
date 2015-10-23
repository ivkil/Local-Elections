package org.oporaua.localelections.accidents;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.oporaua.localelections.R;
import org.oporaua.localelections.data.OporaContract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AccidentsAdapter extends CursorAdapter {

    private static final String OPORAUA_ORG = "https://dts2015.oporaua.org/";

    public AccidentsAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_accident, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        long id = cursor.getLong(AccidentsListFragment.COL_ACCIDENT_ID);
        view.setTag(R.string.accident_id_tag, id);

        Date date = OporaContract.getDateFromDb(cursor.getString(AccidentsListFragment.COL_ACCIDENT_DATE));

        if (date != null) {
//            viewHolder.tvDate.setText(DateFormat.getDateInstance().format(date));
            SimpleDateFormat friendlyDateFormat = new SimpleDateFormat("dd MMM yyyy 'р.'", new Locale("uk"));
            viewHolder.tvDate.setText(friendlyDateFormat.format(date));
        }

        String title = cursor.getString(AccidentsListFragment.COL_ACCIDENT_TITLE);
        viewHolder.tvTitle.setText(title);

        String source = cursor.getString(AccidentsListFragment.COL_ACCIDENT_SOURCE);
        viewHolder.tvSource.setText(Html.fromHtml(source));

        String url = OPORAUA_ORG + cursor.getString(AccidentsListFragment.COL_ACCIDENT_EVIDENCE_URL);

        boolean imageAvailable = url.contains(".jpg") || url.contains(".png");

        viewHolder.imageView.setVisibility(imageAvailable ? View.VISIBLE : View.GONE);

        if (imageAvailable) {
            Glide.with(mContext).load(url).placeholder(R.color.white)
                    .into(viewHolder.imageView);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = (long) v.getTag(R.string.accident_id_tag);
                Intent intent = new Intent(mContext, AccidentDetailsActivity.class);
                intent.putExtra(AccidentDetailsActivity.ARG_ACCIDENT_ID, id);
                mContext.startActivity(intent);
            }
        });

    }

    static class ViewHolder {

        @Bind(R.id.list_item_title_textview)
        TextView tvTitle;

        @Bind(R.id.list_item_date_textview)
        TextView tvDate;

        @Bind(R.id.list_item_source_textview)
        TextView tvSource;

        @Bind(R.id.list_item_image)
        ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}