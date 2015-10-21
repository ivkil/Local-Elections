package org.oporaua.localelections.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.oporaua.localelections.R;
import org.oporaua.localelections.data.OporaContract;
import org.oporaua.localelections.ui.fragment.AccidentsListFragment;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AccidentsAdapter extends CursorAdapter {


    public static class ViewHolder {

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

    public AccidentsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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
        viewHolder.tvDate.setText(date.toString());

        String title = cursor.getString(AccidentsListFragment.COL_ACCIDENT_TITLE);
        viewHolder.tvTitle.setText(title);

        String source = cursor.getString(AccidentsListFragment.COL_ACCIDENT_SOURCE);
        viewHolder.tvSource.setText(Html.fromHtml(source));

        String url = "https://dts2015.oporaua.org/" + cursor.getString(AccidentsListFragment.COL_ACCIDENT_EVIDENCE_URL);

        Picasso.with(mContext).load(url).into(viewHolder.imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = (long) v.getTag(R.string.accident_id_tag);
                Log.d("id", Long.toString(id));
            }
        });
    }

}